package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.coverage.jaxb.CoverageDailyProjectData;
import org.hackystat.dailyprojectdata.resource.coverage.jaxb.ConstructData;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducer;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducerException;
import org.hackystat.telemetry.analyzer.reducer.util.IntervalUtility;
import org.hackystat.telemetry.service.server.ServerProperties;
import org.hackystat.utilities.time.interval.Interval;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;


/**
 * Returns a single stream providing Coverage data.
 * <p>
 * Options:
 * <ol>
 * <li> mode: One of 'Percentage', 'NumCovered', or 'NumUncovered'. Default is 'Percentage'.
 * <li> granularity: A string indicating the type of coverage, such as 'line', 'method', 'class'.
 * Default is 'method'.
 * </ol>
 * 
 * @author Philip Johnson, Cedric Zhang
 */
public class CoverageReducer implements TelemetryReducer { 
 
  /** Possible mode values. */
  public enum Mode { PERCENTAGE, NUMCOVERED, NUMUNCOVERED }
  
  /**
   * Computes and returns the required telemetry streams object.
   * 
   * @param project The project.
   * @param dpdClient The DPD Client.
   * @param interval The interval.
   * @param options The optional parameters.
   * 
   * @return Telemetry stream collection.
   * @throws TelemetryReducerException If there is any error.
   */
  public TelemetryStreamCollection compute(Project project, DailyProjectDataClient dpdClient, 
      Interval interval, String[] options) throws TelemetryReducerException {
    Mode mode = Mode.PERCENTAGE;
    String granularity = null;
    // process options
    if (options.length > 2) {
      throw new TelemetryReducerException("Coverage reducer takes 2 optional parameters.");
    }
    if (options.length >= 1) {
      try {
        mode = Mode.valueOf(options[0].toUpperCase());
      }
      catch (Exception e) {
        throw new TelemetryReducerException("Illegal mode value.", e);
      }
    }

    if (options.length >= 2) {
      granularity = options[1];
    }
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in DevTimeReducer");
    }

    // now get the telemetry stream.
    try {
      TelemetryStream telemetryStream = this.getStream(dpdClient, project, interval,  
          mode, granularity, null);
      TelemetryStreamCollection streams = new TelemetryStreamCollection(null, project, interval);
      streams.add(telemetryStream);
      return streams;
    } 
    catch (Exception e) {
      throw new TelemetryReducerException(e);
    }
  }

  /**
   * Gets the telemetry stream.
   * 
   * @param dpdClient The DailyProjectData client we will contact for the data.
   * @param project The project.
   * @param interval The interval.
   * @param mode The mode (PERCENTAGE, NUMCOVERED, NUMUNCOVERED).
   * @param granularity The type of coverage.
   * @param streamTagValue The tag for the generated telemetry stream.
   * 
   * @return The telemetry stream as required.
   * 
   * @throws Exception If there is any error.
   */
  TelemetryStream getStream(DailyProjectDataClient dpdClient, 
      Project project, Interval interval, Mode mode, 
      String granularity, Object streamTagValue) 
        throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream(streamTagValue);
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);

    for (IntervalUtility.Period period : periods) {
      Long value = this.getData(dpdClient, project, period.getStartDay(), period.getEndDay(),
          mode, granularity);
      telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), value));
    }

    return telemetryStream;
  }
  
  /**
   * Returns a Coverage value for the specified time interval, or null if no SensorData. Note that
   * we return a Long, so percentage is a Long ranging from 0 to 100. (There is no fractional
   * coverage percentage.)
   * 
   * We work backward through the time interval, and return the Coverage value for the first day in
   * the interval for which Coverage data exists.
   * 
   * @param dpdClient The DailyProjectData client we will use to get this data.
   * @param project The project.
   * @param startDay The start day (inclusive).
   * @param endDay The end day (inclusive).
   * @param mode The mode: PERCENTAGE, NUMCOVERED, or NUMUNCOVERED.
   * @param granularity The type of coverage, such as 'line' or 'method'.
   * @throws TelemetryReducerException If anything goes wrong.
   * 
   * @return The Coverage value, or null if there is no Coverage SensorData for that time period.
   */
  Long getData(DailyProjectDataClient dpdClient, Project project, Day startDay, Day endDay, 
      Mode mode, String granularity) throws TelemetryReducerException {
    try {
      // Work backward through the interval, and return as soon as we get Coverage info.
      for (Day day = endDay; day.compareTo(startDay) >= 0; day = day.inc(-1) ) {
        // Get the DPD...
        CoverageDailyProjectData dpdData = 
          dpdClient.getCoverage(project.getOwner(), project.getName(), Tstamp.makeTimestamp(day),
              granularity);
        // Go to the next day in the interval if we don't have anything for this day.
        if ((dpdData.getConstructData() == null) || dpdData.getConstructData().isEmpty()) {
          continue;
        }
        
        // Otherwise we have coverage data, so get the total covered and uncovered values.
        int totalCovered = 0;
        int totalUncovered = 0;
        for (ConstructData data : dpdData.getConstructData()) {
          totalCovered += data.getNumCovered();
          totalUncovered += data.getNumUncovered();
        }
        
        // Now return the value based upon mode.
        switch (mode) {
        case NUMCOVERED:
          return Long.valueOf(totalCovered);
        case NUMUNCOVERED:
          return Long.valueOf(totalUncovered);
        case PERCENTAGE:
          double total = totalCovered + totalUncovered;
          int percent = (int)((totalCovered / total) * 100.0);
          return Long.valueOf(percent);
        default: 
          throw new TelemetryReducerException("Unknown mode: " + mode);
        }
      }
    }
    catch (Exception ex) {
      throw new TelemetryReducerException(ex);
    }
    // Never found appropriate coverage data in this interval, so return null.
    return null;
  }

}

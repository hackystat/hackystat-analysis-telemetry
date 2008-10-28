package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.coupling.jaxb.CouplingDailyProjectData;
import org.hackystat.dailyprojectdata.resource.coupling.jaxb.CouplingData;
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
 * Returns a single stream providing a Coupling value.
 * <p>
 * Options:
 * <ol>
 * <li> coupling: One of 'Afferent', 'Efferent', or 'All'.  Defaults to 'All'.
 * <li> mode: One of 'Total', 'Average', or 'TotalInstancesAboveThreshold'. Defaults to 'Average'.
 * Note that the value returned depends upon the value selected for coupling.  
 * <li> type: A string indicating the type of coupling. Defaults to 'class'. Could also be 
 * 'package'.
 * <li> threshold: A string indicating the threshold value.  Defaults to '10'.
 * This parameter is ignored unless the mode is 
 * 'TotalAboveThreshold', in which case it must be parsed to an integer.
 * <li> tool: The tool whose sensor data is to be used to calculate the coupling information.
 * Defaults to 'DependencyFinder'. 
 * </ol>
 * 
 * @author Philip Johnson
 */
public class CouplingReducer implements TelemetryReducer { 
  
  /** Possible mode values. */
  public enum Coupling {
    /** Afferent coupling (measure of responsibility). */
    AFFERENT,
    /** Efferent coupling (measure of independence). */
    EFFERENT, 
    /** Both Afferent and Efferent coupling.*/
    ALL  }
 
  /** Possible mode values. */
  public enum Mode {
    /** Aggregate total of all couplings. */
    TOTAL,
    /** Average number of couplings.. */
    AVERAGE,
    /** Total number of instances with a coupling value above the threshold. */
    TOTALINSTANCESABOVETHRESHOLD }
  
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
    Coupling coupling = Coupling.ALL;
    Mode mode = Mode.AVERAGE;
    String type = "class";
    String thresholdString = null;
    String tool = "DependencyFinder";
    // process options
    if (options.length > 5) {
      throw new TelemetryReducerException("Coupling reducer needs only 5 parameters.");
    }
    if (options.length >= 1) {
      try {
        coupling = Coupling.valueOf(options[0].toUpperCase());
      }
      catch (Exception e) {
        throw new TelemetryReducerException("Illegal coupling value: " + options[0], e);
      }
    }
    
    if (options.length >= 2) {
      try {
        mode = Mode.valueOf(options[1].toUpperCase());
      }
      catch (Exception e) {
        throw new TelemetryReducerException("Illegal mode value: " + options[1], e);
      }
    }

    if (options.length >= 3) {
      type = options[2];
    }

    if (options.length >= 4) {
      thresholdString = options[3];
    }
    
    if (options.length >= 5) {
      tool = options[4];
    }

    // Make sure threshold is a number if mode is TotalAboveThreshold.
    int threshold = 10;
    if (mode == Mode.TOTALINSTANCESABOVETHRESHOLD) {
      try {
        threshold = Integer.valueOf(thresholdString);
      }
      catch (Exception e) {
        throw new TelemetryReducerException("Illegal threshold value: " + options[3], e);
      }
    }
    
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in CouplingReducer");
    }

    // Now get the telemetry stream.
    try {
      TelemetryStream telemetryStream = this.getStream(dpdClient, project, interval,  
          coupling, mode, type, threshold, tool, null);
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
   * @param coupling The coupling.
   * @param mode The mode.
   * @param type The type.
   * @param threshold The threshold (if mode is TOTALABOVETHRESHOLD).
   * @param tool The tool whose sensor data will be used. 
   * @param streamTagValue The tag for the generated telemetry stream.
   * 
   * @return The telemetry stream as required.
   * 
   * @throws Exception If there is any error.
   */
  TelemetryStream getStream(DailyProjectDataClient dpdClient, 
      Project project, Interval interval, Coupling coupling, Mode mode, String type,
      int threshold, String tool, Object streamTagValue) 
        throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream(streamTagValue);
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);

    for (IntervalUtility.Period period : periods) {
      Double value = this.getData(dpdClient, project, period.getStartDay(), period.getEndDay(),
          coupling, mode, type, threshold, tool);
      telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), value));
    }
    return telemetryStream;
  }
  
  /**
   * Returns a Coupling value for the specified time interval, or null if no SensorData. 
   * 
   * We work backward through the time interval, and return a Coupling value for the first day in
   * the interval for which Coupling data exists.
   * 
   * @param dpdClient The DailyProjectData client we will use to get this data.
   * @param project The project.
   * @param startDay The start day (inclusive).
   * @param endDay The end day (inclusive).
   * @param coupling The coupling.
   * @param mode The mode.
   * @param type The type.
   * @param threshold The threshold, if mode is TOTALINSTANCESABOVETHRESHOLD.
   * @param tool The tool whose sensor data will be used. 
   * @throws TelemetryReducerException If anything goes wrong.
   * 
   * @return The coupling value, or null if there is no coupling SensorData in that period.
   */
  Double getData(DailyProjectDataClient dpdClient, Project project, Day startDay, Day endDay, 
      Coupling coupling, Mode mode, String type, int threshold, String tool) 
  throws TelemetryReducerException {
    try {
      // Work backward through the interval, and return as soon as we get Complexity info.
      for (Day day = endDay; day.compareTo(startDay) >= 0; day = day.inc(-1) ) {
        // Get the DPD...
        CouplingDailyProjectData dpdData = 
          dpdClient.getCoupling(project.getOwner(), project.getName(), Tstamp.makeTimestamp(day),
              type, tool);
        // Go to the next day in the interval if we don't have anything for this day.
        if ((dpdData.getCouplingData() == null) || dpdData.getCouplingData().isEmpty()) {
          continue;
        }
        
        // Otherwise we have complexity data, so calculate the desired values.
        double totalAfferent = 0;
        double totalEfferent = 0;
        double totalUnits = 0;
        double totalAboveThreshold = 0;
        for (CouplingData data : dpdData.getCouplingData()) {
          totalUnits++;
          totalAfferent += data.getAfferent().intValue();
          totalEfferent += data.getEfferent().intValue();
          if ((mode.equals(Mode.TOTALINSTANCESABOVETHRESHOLD)) &&
              (coupling.equals(Coupling.AFFERENT)) &&
              (data.getAfferent().intValue() > threshold)) {
            totalAboveThreshold++;
          }
          if ((mode.equals(Mode.TOTALINSTANCESABOVETHRESHOLD)) &&
              (coupling.equals(Coupling.EFFERENT)) &&
              (data.getEfferent().intValue() > threshold)) {
            totalAboveThreshold++;
          }
          if ((mode.equals(Mode.TOTALINSTANCESABOVETHRESHOLD)) &&
              (coupling.equals(Coupling.ALL)) &&
              (data.getAfferent().intValue() + data.getEfferent().intValue() > threshold)) {
            totalAboveThreshold++;
          }
        }

        // Now return the value based upon mode and coupling.
        switch (mode) { //NOPMD
        case TOTAL:
          switch (coupling) {
          case AFFERENT: 
            return Double.valueOf(totalAfferent);
          case EFFERENT:
            return Double.valueOf(totalEfferent);
          case ALL:
            return Double.valueOf((totalEfferent + totalAfferent));
          default: 
            throw new TelemetryReducerException("Unknown coupling: " + coupling);
          }
        case AVERAGE:
          switch (coupling) {
          case AFFERENT: 
            return Double.valueOf(totalAfferent / totalUnits);
          case EFFERENT:
            return Double.valueOf(totalEfferent / totalUnits);
          case ALL:
            return Double.valueOf((totalEfferent + totalAfferent) / totalUnits);
          default: 
            throw new TelemetryReducerException("Unknown coupling: " + coupling);
          }
        case TOTALINSTANCESABOVETHRESHOLD:
          return Double.valueOf(totalAboveThreshold);
        default: 
          throw new TelemetryReducerException("Unknown mode: " + mode);
        }
      }
    }
    catch (Exception ex) {
      throw new TelemetryReducerException(ex);
    }
    // Never found appropriate coupling data in this interval, so return null.
    return null;
  }
}

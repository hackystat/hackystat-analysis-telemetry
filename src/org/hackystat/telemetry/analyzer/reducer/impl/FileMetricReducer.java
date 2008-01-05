package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.filemetric.jaxb.FileMetricDailyProjectData;
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
 * Returns a single stream providing FileMetric data.
 * <p>
 * Options:
 * <ol>
 * <li> sizemetric: A string indicating the size metric to return, such as "TotalLines".
 * Default is 'TotalLines'.
 * </ol>
 * 
 * @author Philip Johnson, Cedric Zhang
 */
public class FileMetricReducer implements TelemetryReducer { 
 
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
    String sizeMetric = null;
    // process options
    if (options.length > 1) {
      throw new TelemetryReducerException("Coverage reducer takes 1 optional parameter.");
    }
    if (options.length >= 1) {
      sizeMetric = options[0];
    }

    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in FileMetricReducer.");
    }

    // now get the telemetry stream.
    try {
      TelemetryStream telemetryStream = this.getStream(dpdClient, project, interval,   
          sizeMetric, null);
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
   * @param sizeMetric The SizeMetric.
   * @param streamTagValue The tag for the generated telemetry stream.
   * 
   * @return The telemetry stream as required.
   * 
   * @throws Exception If there is any error.
   */
  TelemetryStream getStream(DailyProjectDataClient dpdClient, 
      Project project, Interval interval, String sizeMetric,
      Object streamTagValue) 
        throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream(streamTagValue);
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);

    for (IntervalUtility.Period period : periods) {
      Double value = this.getData(dpdClient, project, period.getStartDay(), period.getEndDay(),
          sizeMetric);
      telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), value));
    }
    return telemetryStream;
  }
  
  /**
   * Returns a FileMetric value for the specified time interval and sizeMetric, or null 
   * if no SensorData. 
   * 
   * We work backward through the time interval, and return the FileMetric value for the first day 
   * in the interval for which FileMetric data exists.
   * 
   * @param dpdClient The DailyProjectData client we will use to get this data.
   * @param project The project.
   * @param startDay The start day (inclusive).
   * @param endDay The end day (inclusive).
   * @param sizeMetric The size metric.
   * @throws TelemetryReducerException If anything goes wrong.
   * 
   * @return The FileMetric value, or null if there is no SensorData for that time period.
   */
  Double getData(DailyProjectDataClient dpdClient, Project project, Day startDay, Day endDay, 
      String sizeMetric) throws TelemetryReducerException {
    try {
      // Work backward through the interval, and return as soon as we get matching FileMetric info.
      // We might want to make this smarter, and keep searching if we find FileMetric data but
      // not containing the given sizeMetric.
      for (Day day = endDay; day.compareTo(startDay) >= 0; day = day.inc(-1) ) {
        // Get the DPD...
        FileMetricDailyProjectData dpdData = 
          dpdClient.getFileMetric(project.getOwner(), project.getName(), Tstamp.makeTimestamp(day),
              sizeMetric);
        // Return null right away if DPD is empty.
        if ((dpdData.getFileData() == null) || dpdData.getFileData().isEmpty()) {
          continue;
        }
        // Otherwise we have FileMetric data, so return the total field.
        // Note that it can be null.
        return dpdData.getTotal();
      }
    }
    catch (Exception ex) {
      throw new TelemetryReducerException(ex);
    }
    // Never found appropriate FileMetric data in this interval, so return null.
    return null;
  }

}

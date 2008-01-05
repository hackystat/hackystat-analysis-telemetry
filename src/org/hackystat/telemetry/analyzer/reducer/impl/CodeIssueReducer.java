package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.CodeIssueDailyProjectData;
import org.hackystat.dailyprojectdata.resource.codeissue.jaxb.CodeIssueData;
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
 * Returns a single stream providing CodeIssue counts.
 * <p>
 * Accepts the following options in the following order.
 * <ol>
 *  <li> Tool: A string indicating the tool whose CodeIssue data is to be counted, or "*" for all
 *       tools found.
 *       Default is "*".
 *  <li> Type: A string to restrict the counts to the CodeIssue's with the specified type, or 
 *       "*" to match all types.  Default is "*".
 * </ol>
 * 
 * @author Philip Johnson
 */
public class CodeIssueReducer implements TelemetryReducer {
 
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
    String tool = null;
    String type = null;
    //process options
    if (options.length > 2) {
      throw new TelemetryReducerException("CodeIssue reducer takes 2 optional parameters.");
    }

    if (options.length >= 1 && !"*".equals(options[0])) {
      tool = options[0];
    }

    if (options.length >= 2 && !"*".equals(options[1])) {
      type = options[1];
    }
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in BuildReducer");
    }

    // now compute the single telemetry stream. 
    try {
      TelemetryStream telemetryStream = this.getStream(dpdClient, project, interval,  
          tool, type, null);
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
   * @param tool The tool whose CodeIssue data is to be returned.
   * @param type The type of CodeIssue data to return.
   * @param streamTagValue The tag for the generated telemetry stream.
   * 
   * @return The telemetry stream as required.
   * @throws Exception If there is any error.
   */
  TelemetryStream getStream(DailyProjectDataClient dpdClient, 
      Project project, Interval interval, String tool, String type, 
      Object streamTagValue) throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream(streamTagValue);
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);

    for (IntervalUtility.Period period : periods) {
      Long value = this.getData(dpdClient, project, period.getStartDay(), period.getEndDay(),
          tool, type);
      telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), value));
    }
    return telemetryStream;
  }
  
  /**
   * Returns a CodeIssue count for the specified time interval, or null if no SensorData. 
   *
   * @param dpdClient The DailyProjectData client we will use to get this data. 
   * @param project The project.
   * @param startDay The start day (inclusive).
   * @param endDay The end day (inclusive).
   * @param tool The tool whose CodeIssue data is to be counted, or null for all tools.
   * @param type The type of CodeIssue data to count, or null for all CodeIssue types.
   * @throws TelemetryReducerException If anything goes wrong.
   *
   * @return The CodeIssue count, or null if there is no CodeIssue SensorData for that time period. 
   */
  Long getData(DailyProjectDataClient dpdClient, Project project, Day startDay, Day endDay, 
      String tool, String type) throws TelemetryReducerException {
    try {
      // Work backward through the interval, and return as soon as we get matching CodeIssue info.
      for (Day day = endDay; day.compareTo(startDay) >= 0; day = day.inc(-1) ) {
        // Get the DPD...
        CodeIssueDailyProjectData dpdData = 
          dpdClient.getCodeIssue(project.getOwner(), project.getName(), Tstamp.makeTimestamp(day),
              tool, type);
        // Keep going if there's no data.
        if ((dpdData.getCodeIssueData() == null) || dpdData.getCodeIssueData().isEmpty()) {
          continue;
        }
        // Otherwise we have CodeIssue data, so return the total field.
        return getTotalIssues(dpdData);
      }
    }
    catch (Exception ex) {
      throw new TelemetryReducerException(ex);
    }
    // Never found appropriate CodeIssue data in this interval, so return null.
    return null;
  }
  
  /**
   * Returns the total number of code issues in this CodeIssueDailyProjectData object.
   * @param dpdData The CodeIssueDailyProjectData object.
   * @return The total number of issues. 
   */
  private long getTotalIssues (CodeIssueDailyProjectData dpdData) {
    long count = 0;
    for (CodeIssueData data : dpdData.getCodeIssueData()) {
      count += data.getNumIssues();
    }
    return count;
  }

}

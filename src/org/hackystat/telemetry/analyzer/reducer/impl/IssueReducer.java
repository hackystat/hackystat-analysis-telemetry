package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.issue.jaxb.IssueDailyProjectData;
import org.hackystat.dailyprojectdata.resource.issue.jaxb.IssueData;
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
 * Returns a single stream providing Issue counts.
 * <p>
 * Options:
 * <ol>
 * <li> member: The project member whose commit counts are to be returned, or "*" for all members.
 * </ol>
 * 
 * @author Shaoxuan Zhang, Philip Johnson
 */
public class IssueReducer implements TelemetryReducer { 
 
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
    String member = null;
    String issueStatus = null;
    //process options
    if (options.length > 2) {
      throw new TelemetryReducerException("Commit reducer takes 1 optional parameters.");
    }
    if (options.length >= 1) {
      member = options[0];
    }

    if (options.length >= 2) {
      issueStatus = options[1];
    }
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in CommitReducer");
    }

    // now get the telemetry stream. 
    try {
      TelemetryStream telemetryStream = this.getIssueStream(dpdClient, project, interval,  
          member, issueStatus, null);
      TelemetryStreamCollection streams = new TelemetryStreamCollection(null, project, interval);
      streams.add(telemetryStream);
      return streams;
    } 
    catch (Exception e) {
      throw new TelemetryReducerException(e);
    }
  }

  /**
   * Gets the issue telemetry stream.
   * 
   * @param dpdClient The DailyProjectData client we will contact for the data. 
   * @param project The project.
   * @param interval The interval.
   * @param member The member, or "*" for all members.
   * @param streamTagValue The tag for the generated telemetry stream.
   * @param issueStatus the status of the issues to be counted.
   * 
   * @return The telemetry stream as required.
   * 
   * @throws Exception If there is any error.
   */
  TelemetryStream getIssueStream(DailyProjectDataClient dpdClient, 
      Project project, Interval interval, String member, String issueStatus, Object streamTagValue) 
        throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream(streamTagValue);
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);
    
    for (IntervalUtility.Period period : periods) {
      Long value = this.getIssuesData(dpdClient, project, period.getStartDay(), period.getEndDay(),
          member, issueStatus);
      telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), value));
    }
    return telemetryStream;
  }
  
  /**
   * Returns a Issue value for the specified time interval, or null if no SensorData. 
   * 
   * @param dpdClient The DailyProjectData client we will use to get this data. 
   * @param project The project.
   * @param startDay The start day (inclusive).
   * @param endDay The end day (inclusive).
   * @param member The member email, or "*" for all members.
   * @param issueStatus The status of the issue, open or closed.
   * @throws TelemetryReducerException If anything goes wrong.
   *
   * @return The UnitTest count, or null if there is no UnitTest SensorData for that time period. 
   */
  Long getIssuesData(DailyProjectDataClient dpdClient, Project project, Day startDay, Day endDay, 
      String member, String issueStatus) throws TelemetryReducerException {
    try {
      IssueDailyProjectData data = 
        dpdClient.getIssue(project.getOwner(), project.getName(), 
            Tstamp.makeTimestamp(endDay), issueStatus);
      if (member == null || "*".equals(member)) {
        return Long.valueOf(data.getIssueData().size());
      }
      else {
        Long count = 0L;
        for (IssueData issueData : data.getIssueData()) {
          if (member.equals(issueData.getOwner())) {
            count++;
          }
        }
        return count;
      }
    }
    catch (Exception ex) {
      throw new TelemetryReducerException(ex);
    }

  }

}

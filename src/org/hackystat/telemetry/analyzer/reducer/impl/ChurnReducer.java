package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.commit.jaxb.CommitDailyProjectData;
import org.hackystat.dailyprojectdata.resource.commit.jaxb.MemberData;
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
 * Returns a single stream providing Churn data.
 * Churn is linesAdded + linesDeleted. 
 * <p>
 * Options:
 * <ol>
 * <li> member: The project member whose churn data is to be returned, or "*" for all members.
 * <li> isCumulative: True or false. Default is false.
 * </ol>
 * 
 * @author Philip Johnson
 */
public class ChurnReducer implements TelemetryReducer { 
 
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
    boolean isCumulative = false;
    //process options
    if (options.length > 2) {
      throw new TelemetryReducerException("Churn reducer takes 2 optional parameters.");
    }
    if (options.length >= 1) {
      member = options[0];
    }
    
    if (options.length >= 2) {
      try {
        isCumulative = Boolean.valueOf(options[1]);
      }
      catch (Exception e) {
        throw new TelemetryReducerException("Illegal cumulative value.", e);
      }
    }
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in ChurnReducer");
    }

    // now get the telemetry stream. 
    try {
      TelemetryStream telemetryStream = this.getStream(dpdClient, project, interval,  
          member, isCumulative, null);
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
   * @param member The member, or "*" for all members.
   * @param isCumulative True for cumulative measure.
   * @param streamTagValue The tag for the generated telemetry stream.
   * 
   * @return The telemetry stream as required.
   * 
   * @throws Exception If there is any error.
   */
  TelemetryStream getStream(DailyProjectDataClient dpdClient, 
      Project project, Interval interval,
      String member, boolean isCumulative, Object streamTagValue) 
        throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream(streamTagValue);
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);
    long cumulativeCount = 0;
    
    for (IntervalUtility.Period period : periods) {
      Long value = this.getData(dpdClient, project, period.getStartDay(), period.getEndDay(),
          member);
      
      if (value != null) {
        cumulativeCount += value;
      }
      
      if (isCumulative) {
        telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), 
            cumulativeCount));        
      }
      else {
        telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), value));
      }
    }
    return telemetryStream;
  }
  
  /**
   * Returns a Churn value for the specified time interval, or null if no Commit SensorData. 
   * 
   * @param dpdClient The DailyProjectData client we will use to get this data. 
   * @param project The project.
   * @param startDay The start day (inclusive).
   * @param endDay The end day (inclusive).
   * @param member The member email, or "*" for all members.
   * @throws TelemetryReducerException If anything goes wrong.
   *
   * @return The Churn count, or null if there is no Commit SensorData for that time period. 
   */
  Long getData(DailyProjectDataClient dpdClient, Project project, Day startDay, Day endDay, 
      String member) throws TelemetryReducerException {
    long count = 0;
    boolean hasData = false;
    try {
      // For each day in the interval... 
      for (Day day = startDay; day.compareTo(endDay) <= 0; day = day.inc(1) ) {
        // Get the DPD...
        CommitDailyProjectData data = 
          dpdClient.getCommit(project.getOwner(), project.getName(), Tstamp.makeTimestamp(day));
        hasData = !data.getMemberData().isEmpty();
        // Go through the DPD per-member data...
        for (MemberData memberData : data.getMemberData()) {
          if ((member == null) || "*".equals(member) || 
              (memberData.getMemberUri().endsWith(member))) {
            count += memberData.getLinesAdded() + memberData.getLinesDeleted();
          }
        }
      }
    }
    catch (Exception ex) {
      throw new TelemetryReducerException(ex);
    }

    //Return null if no data, the Commit counts otherwise. 
    return (hasData) ? Long.valueOf(count) : null; 
  }

}

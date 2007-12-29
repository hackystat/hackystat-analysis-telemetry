package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.build.jaxb.BuildDailyProjectData;
import org.hackystat.dailyprojectdata.resource.build.jaxb.MemberData;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducer;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducerException;
import org.hackystat.telemetry.analyzer.reducer.util.IntervalUtility;
import org.hackystat.telemetry.analyzer.reducer.util.ReducerOptionUtility;
import org.hackystat.telemetry.service.server.ServerProperties;
import org.hackystat.utilities.time.interval.Interval;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;


/**
 * Returns a single stream providing Build data.
 * <p>
 * Accepts the following options in the following order.
 * <ol>
 *  <li> User: Supply a user email to get Build counts for just that user. 
 *       Default is "*" indicating the aggregate Build Data for all project members.
 *  <li> Result: One of Success, Failure, or *, indicating whether the count is
 *       just of successful builds, failed builds, or all builds. Default is "*". 
 *  <li> Type: A string to restrict the counts to those builds with a "Type" property
 *       matching this string, or "*" to indicate all builds regardless of Type. 
 *       Default is "*".
 *  <li> isCumulative: True or false. Default is false.
 * </ol>
 * 
 * @author Philip Johnson
 */
public class BuildReducer implements TelemetryReducer {
 
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
    // weird. for some reason we want 'null' as default rather than '*' etc.
    String member = null;
    String result = null;
    String type = null;
    boolean isCumulative = false;
    //process options
    if (options.length > 4) {
      throw new TelemetryReducerException("Build reducer takes 4 optional parameters.");
    }

    if (options.length >= 1 && !"*".equals(options[0])) {
      member = options[0];
    }

    if (options.length >= 2 && !"*".equals(options[1])) {
      result = options[1];
    }
    
    if (options.length >= 3 && !"*".equals(options[2])) {
      type = options[2];
    }
    
    if (options.length >= 4) {
      isCumulative = ReducerOptionUtility.parseBooleanOption(4, options[3]);
    }
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in BuildReducer");
    }

    // now compute the single telemetry stream. 
    try {
      TelemetryStream telemetryStream = this.getStream(dpdClient, project, interval,  
          member, result, type, isCumulative, null);
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
   * @param member Project member, or null to match all members. 
   * @param result The build result, either FAILURE, SUCCESS, or null to match both.
   * @param type The value of the build 'Type' property, a string or null to match anything.
   * @param isCumulative True for cumulative measure.
   * @param streamTagValue The tag for the generated telemetry stream.
   * 
   * @return The telemetry stream as required.
   * @throws Exception If there is any error.
   */
  TelemetryStream getStream(DailyProjectDataClient dpdClient, 
      Project project, Interval interval, String member, String result, 
      String type, boolean isCumulative, Object streamTagValue) 
        throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream(streamTagValue);
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);
    long cumulativeBuilds = 0;

    for (IntervalUtility.Period period : periods) {
      Long value = this.getData(dpdClient, project, period.getStartDay(), period.getEndDay(),
          member, result, type);
      
      if (value != null) {
        cumulativeBuilds += value;
      }
      
      if (isCumulative) {
        telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), 
            cumulativeBuilds));        
      }
      else {
        telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), value));
      }
    }
    return telemetryStream;
  }
  
  /**
   * Returns a Build count for the specified time interval, or null if no SensorData. 
   *
   * @param dpdClient The DailyProjectData client we will use to get this data. 
   * @param project The project.
   * @param startDay The start day (inclusive).
   * @param endDay The end day (inclusive).
   * @param member The project member email, or null to match all members. 
   * @param result The result, either SUCCESS, FAILURE, or null for both.
   * @param type The 'Type' property, or null to match anything.
   * @throws TelemetryReducerException If anything goes wrong.
   *
   * @return The Build count, or null if there is no Build SensorData for that time period. 
   */
  Long getData(DailyProjectDataClient dpdClient, Project project, Day startDay, Day endDay, 
      String member, String result, String type) throws TelemetryReducerException {
    long buildCount = 0;
    String typeString = (type == null) ? "*" : type;
    try {
      // For each day in the interval... 
      for (Day day = startDay; day.compareTo(endDay) <= 0; day = day.inc(1) ) {
        // Get the DPD for the required 'Type' property.
        BuildDailyProjectData data = 
          dpdClient.getBuild(project.getOwner(), project.getName(), Tstamp.makeTimestamp(day), 
              typeString);
        // Go through the DPD per-member data...
        for (MemberData memberData : data.getMemberData()) {
          // Check to see if this data is for the given member and of the appropriate type.
          if ((member == null) || (memberData.getMemberUri().endsWith(member))) {
            if ((result == null) || "*".equals(result)) {
              buildCount += memberData.getFailure() + memberData.getSuccess();
            }
            else if ("Success".equals(result)) {
              buildCount += memberData.getSuccess();
            }
            else if ("Failure".equals(result)) {
              buildCount += memberData.getFailure();
            }
          }
        }
      }
    }
    catch (Exception ex) {
      throw new TelemetryReducerException(ex);
    }

    //Return null if no data, the Build count data otherwise. 
    return (buildCount > 0) ? new Long(buildCount) : null; 
  }

}

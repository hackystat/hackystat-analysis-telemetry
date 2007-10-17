package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.devtime.jaxb.DevTimeDailyProjectData;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.telemetry.analyzer.util.selector.interval.Interval;
import org.hackystat.sensorbase.uripattern.UriPattern;
import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.telemetry.analyzer.util.Day;
import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducer;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducerException;
import org.hackystat.telemetry.analyzer.reducer.util.IntervalUtility;
import org.hackystat.telemetry.analyzer.reducer.util.ReducerOptionUtility;
import org.hackystat.telemetry.service.server.ServerProperties;
import org.hackystat.utilities.tstamp.Tstamp;


/**
 * Returns a single stream providing DevTime data. 
 * <p>
 * Options:
 * <ol>
 * <li> EventType: Supply an Event Type to restrict the DevTime to just the time 
 *      associated with that Event Type. 
 *       Default is "*" which indicates all file types are used in computing the 
 *       DevTime.  
 *  <li> User: Supply a user email to get DevTime for just that user. 
 *       Default is "*" indicating the aggregate DevTime of all project members.
 *  <li> ResourceFilterPattern: Restricts the files over which the DevTime 
 *       is computed. Default is "**".
 *  <li> isCumulative: True or false. Default is false.
 * </ol>
 * 
 * @author Hongbing Kou
 */
public class DevTimeReducer implements TelemetryReducer {
  
  /** Maps user emails to their DPD clients. */ 
  private Map<String, DailyProjectDataClient> dpdClients = 
    new HashMap<String, DailyProjectDataClient>();

  /**
   * Computes and returns the required telemetry streams object.
   *
   * @param project The project.
   * @param user The user email.
   * @param password The user password. 
   * @param interval The interval.
   * @param options The optional parameters.
   *
   * @return Telemetry stream collection.
   * @throws TelemetryReducerException If there is any error.
   */
  public TelemetryStreamCollection compute(Project project, String user, String password, 
      Interval interval, String[] options) throws TelemetryReducerException {
    // weird. for some reason we want 'null' as default rather than '*' etc.
    String eventType = null;
    String member = null;
    UriPattern resourcePattern = null;
    boolean isCumulative = false;
    //process options
    if (options.length > 4) {
      throw new TelemetryReducerException("DevTime reducer takes 4 optional parameters.");
    }

    if (options.length >= 1 && ! "*".equals(options[0])) {
      eventType = options[0];
      eventType = eventType.toLowerCase();
    }

    if (options.length >= 2 && ! "*".equals(options[1])) {
      member = options[1];
    }
    
    if (options.length >= 3) {
      resourcePattern = new UriPattern(options[2]);
    }
    
    if (options.length >= 4) {
      isCumulative = ReducerOptionUtility.parseBooleanOption(4, options[3]);
    }
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_HOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in DevTimeReducer");
    }

    // now get the telemetry stream. 
    try {
      if (!dpdClients.containsKey(user)) {
        dpdClients.put(user, new DailyProjectDataClient(user, password, dpdHost));
      }
      DailyProjectDataClient dpdClient = dpdClients.get(user);
      TelemetryStream telemetryStream = this.getStream(dpdClient, project, interval,  
          eventType, member, resourcePattern, isCumulative, null);
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
   * @param eventType The event type, or null to match all event types.
   * @param member Project member, or null to match all members. 
   * @param filePattern File filter pattern, or null to match all files.
   * @param isCumulative True for cumulative measure.
   * @param streamTagValue The tag for the generated telemetry stream.
   * 
   * @return The telemetry stream as required.
   * 
   * @throws Exception If there is any error.
   */
  TelemetryStream getStream(DailyProjectDataClient dpdClient, 
      Project project, Interval interval, String eventType, String member, 
      UriPattern filePattern, boolean isCumulative, Object streamTagValue) 
        throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream(streamTagValue);
    List periods = IntervalUtility.getPeriods(interval);
    double cumulativeDevTime = 0;
    for (Iterator i = periods.iterator(); i.hasNext(); ) {
      IntervalUtility.Period period = (IntervalUtility.Period) i.next();
      Double value = this.getData(dpdClient, project, period.getStartDay(), period.getEndDay(),
          eventType, member, filePattern);
      
      if (value != null) {
        cumulativeDevTime += value;
      }
      
      if (isCumulative) {
        telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), 
            cumulativeDevTime));        
      }
      else {
        telemetryStream.addDataPoint(new TelemetryDataPoint(period.getTimePeriod(), value));
      }
    }
    return telemetryStream;
  }
  
  /**
   * Returns a DevTime value for the specified time interval, or null if no SensorData. 
   *
   * @param dpdClient The DailyProjectData client we will use to get this data. 
   * @param project The project.
   * @param startDay The start day (inclusive).
   * @param endDay The end day (inclusive).
   * @param eventType The event type, or null to match all file types.
   * @param member The project member, or null to match all members. 
   * @param filePattern File filter pattern, or null to match all files.
   * @throws TelemetryReducerException If anything goes wrong.
   *
   * @return The DevTime, or null if there is no SensorData for that time period. 
   */
  Double getData(DailyProjectDataClient dpdClient, Project project, Day startDay, Day endDay, 
      String eventType, String member, UriPattern filePattern) throws TelemetryReducerException {
    double devTime = 0;
    try {
      // For each day in the interval... 
      for (Day day = startDay; day.compareTo(endDay) <= 0; day = day.inc(1) ) {
        // Go through each project member...
        for (String projectMember : project.getMembers().getMember()) {
          // If we want to include the project member's DevTime data... 
          if (projectMember.equals(member) || (member == null)) {
            DevTimeDailyProjectData data = 
              dpdClient.getDevTime(projectMember, project.getName(), Tstamp.makeTimestamp(day));
          }
        }
        DailyProjectDevTime dailyProjectDevTime = null; //DailyProjectDevTime.getInstance(project, day);
        if (dailyProjectDevTime.hasSensorData()) {
          if (member == null) {
            devTime += dailyProjectDevTime.getDevTime(filePattern, eventType);
          }
          else {
            devTime += dailyProjectDevTime.getDevTime(filePattern, member, eventType);
          }
        }
      }
    }
    catch (Exception ex) {
      throw new TelemetryReducerException(ex);
    }
    
    Double result = null;
    //Zero means no de time data for the pattern specified, we return null instead of zero.
    if (devTime > 0) {
      result = new Double(devTime);
    }
    return result;
  }

}

package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.Iterator;
import java.util.List;

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


/**
 * A class that returns dev time related telemtry stream.
 * <p>
 * <pre>
 *   Multiplicity: single-stream.
 *
 *   Options: 
 *   (1) EventType, such as "*", "edit", "build", etc. 
 *       Optional. Default is "*" for all file types. 
 *   (2) User, Optional. Default is the aggregation of all project members.
 *   (3) FileFilterPattern, optional, default="**".
 *   (4) isCumulative, optional, default=false.
 * </pre>
 * 
 * @author Hongbing Kou
 */
public class DevTimeReducer implements TelemetryReducer {
  /**
   * Computes the required telemetry streams object.
   *
   * @param project The project.
   * @param interval The interval.
   * @param options The optional parameters.
   *
   * @return Telemetry stream collection.
   * @throws TelemetryReducerException If there is any error.
   */
  public TelemetryStreamCollection compute(Project project, Interval interval, String[] options)
      throws TelemetryReducerException {
    String eventType = null;
    User member = null;
    UriPattern fileFilterPattern = null;
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
      String userEmail = options[1];
      member = null; //UserManager.getInstance().getUser(userEmail);
    }
    
    if (options.length >= 3) {
      fileFilterPattern = new UriPattern(options[2]);
    }
    
    if (options.length >= 4) {
      isCumulative = ReducerOptionUtility.parseBooleanOption(4, options[3]);
    }


    //compute
    try {
      TelemetryStream telemetryStream = this.getStream(project, interval, 
          eventType, member, fileFilterPattern, isCumulative, null);
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
   * @param project The project.
   * @param interval The interval.
   * 
   * @param eventType The event type, or null to match all event types.
   * @param member Project member.
   * @param filePattern File filter pattern, or null to match all files.
   * @param isCumulative True for cumulative measure.
   * @param streamTagValue The tag for the generated telemetry stream.
   * 
   * @return The telemetry stream as required.
   * 
   * @throws Exception If there is any error.
   */
  TelemetryStream getStream(Project project, Interval interval, String eventType, User member, 
      UriPattern filePattern, boolean isCumulative, Object streamTagValue) 
        throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream(streamTagValue);
    List periods = IntervalUtility.getPeriods(interval);
    double cumulativeDevTime = 0;
    for (Iterator i = periods.iterator(); i.hasNext(); ) {
      IntervalUtility.Period period = (IntervalUtility.Period) i.next();
      Double value = this.getData(project, period.getStartDay(), period.getEndDay(),
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
   * Computes dev event information.
   *
   * @param project The project.
   * @param startDay The start day (inclusive).
   * @param endDay The end day (inclusive).
   * @param eventType The event type, or null to match all file types.
   * @param member The project member.
   * @param filePattern File filter pattern, or null to match all files.
   * 
   * @throws TelemetryReducerException If anything is wrong.
   *
   * @return The dev time as required. Note that null is returned when there is no sensor data 
   *         during the period specified.
   */
  Double getData(Project project, Day startDay, Day endDay, String eventType, 
      User member, UriPattern filePattern) throws TelemetryReducerException {
    double devTime = 0;
    try {
      for (Day day = startDay; day.compareTo(endDay) <= 0; day = day.inc(1) ) {
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

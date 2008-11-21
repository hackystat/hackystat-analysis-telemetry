package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.Property;
import org.hackystat.sensorbase.uripattern.UriPattern;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducer;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducerException;
import org.hackystat.telemetry.analyzer.reducer.util.ReducerOptionUtility;
import org.hackystat.telemetry.service.server.ServerProperties;
import org.hackystat.utilities.time.interval.Interval;

/**
 * Returns a set of streams providing DevTime data in hours for each member of the project. 
 * <p>
 * Accepts the following options in the following order, although only isCumulative
 * is supported at the current time.
 * <ol>
 * <li> EventType: Supply an Event Type to restrict the DevTime to just the time 
 *      associated with that Event Type. 
 *      Default is "*" which indicates all file types are used in computing the 
 *      DevTime.  
 *  <li> ResourceFilterPattern: Restricts the files over which the DevTime 
 *       is computed. Default is "**".
 *  <li> isCumulative: True or false. Default is false.
 * </ol>
 * 
 * @author Hongbing Kou, Philip Johnson
 */
public class MemberDevTimeReducer implements TelemetryReducer {
  
  private static DevTimeReducer genericDevTimeReducer = new DevTimeReducer();
 

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
    String eventType = null;
    UriPattern resourcePattern = null;
    boolean isCumulative = false;
    //process options
    if (options.length > 3) {
      throw new TelemetryReducerException("MemberDevTime reducer takes 3 optional parameters.");
    }

    if (options.length >= 1 && ! "*".equals(options[0])) {
      eventType = options[0];
      eventType = eventType.toLowerCase();
    }

    if (options.length >= 2 && ! "*".equals(options[1])) {
      resourcePattern = new UriPattern(options[1]);
    }
    
    if (options.length >= 3) {
      isCumulative = ReducerOptionUtility.parseBooleanOption(4, options[2]);
    }
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in MemberDevTimeReducer");
    }

    // now compute the set of telemetry streams. Remember, we only process the Cumulative
    // optional parameter.
    try {
      TelemetryStreamCollection streams = new TelemetryStreamCollection(null, project, interval);
      // Make a list of emails containing all members plus the owner. 
      List<String> emails = new ArrayList<String>(); 
      emails.addAll(project.getMembers().getMember());
      emails.add(project.getOwner());
      // Now remove any email that has been specified as an agent.
      for (Property property : project.getProperties().getProperty()) {
        if (property.getKey().equals("agent")) {
          emails.remove(property.getValue());
        }
      }
      for (String email : emails) {
        streams.add(genericDevTimeReducer.getStream(dpdClient, project, interval, eventType, 
            email, resourcePattern, isCumulative, email));
      }
      return streams;
    } 
    catch (Exception e) {
      throw new TelemetryReducerException(e);
    }
  }
}

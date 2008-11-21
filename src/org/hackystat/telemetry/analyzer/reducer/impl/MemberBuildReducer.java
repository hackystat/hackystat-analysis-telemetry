package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.Property;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducer;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducerException;
import org.hackystat.telemetry.analyzer.reducer.util.ReducerOptionUtility;
import org.hackystat.telemetry.service.server.ServerProperties;
import org.hackystat.utilities.time.interval.Interval;

/**
 * Returns a set of streams providing Build data for each user. 
 * <p>
 * Accepts the following options in the following order.
 * <ol>
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
public class MemberBuildReducer implements TelemetryReducer {
  private static BuildReducer genericBuildReducer = new BuildReducer();

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
    String result = null;
    String type = null;
    boolean isCumulative = false;
    //process options
    if (options.length > 3) {
      throw new TelemetryReducerException("Member Build reducer takes 3 optional parameters.");
    }

    if (options.length >= 1 && !"*".equals(options[0])) {
      result = options[0];
    }

    if (options.length >= 2 && !"*".equals(options[1])) {
      type = options[1];
    }

    if (options.length >= 3) {
      isCumulative = ReducerOptionUtility.parseBooleanOption(4, options[2]);
    }
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in MemberBuildReducer");
    }

    // now compute the single telemetry stream. 
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
        streams.add(genericBuildReducer.getStream(dpdClient, project, interval, email, result, 
            type, isCumulative, email));
      }
      return streams;
    } 
    catch (Exception e) {
      throw new TelemetryReducerException(e);
    }
  }

}

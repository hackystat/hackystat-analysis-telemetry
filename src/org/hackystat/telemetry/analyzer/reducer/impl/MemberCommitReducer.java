package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.Property;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducer;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducerException;
import org.hackystat.telemetry.service.server.ServerProperties;
import org.hackystat.utilities.time.interval.Interval;

/**
 * Returns a set of streams providing Commit counts for each member of the project. 
 * <p>
 * Options:
 * <ol>
 * <li> isCumulative: True or false. Default is false.
 * </ol>
 * 
 * @author Philip Johnson
 */
public class MemberCommitReducer implements TelemetryReducer { 
  private static CommitReducer genericCommitReducer = new CommitReducer();
  
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
    boolean isCumulative = false;
    //process options
    if (options.length > 1) {
      throw new TelemetryReducerException("Commit reducer takes 1 optional parameter.");
    }
    if (options.length == 1) {
      try {
        isCumulative = Boolean.valueOf(options[0]);
      }
      catch (Exception e) {
        throw new TelemetryReducerException("Illegal cumulative value.", e);
      }
    }
    
    // Find out the DailyProjectData host, throw error if not found.
    String dpdHost = System.getProperty(ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY);
    if (dpdHost == null) {
      throw new TelemetryReducerException("Null DPD host in MemberCommitReducer");
    }

    // now get the telemetry stream. 
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
        streams.add(genericCommitReducer.getStream(dpdClient, project, interval, email, 
            isCumulative, email));
      }
      return streams;
    } 
    catch (Exception e) {
      throw new TelemetryReducerException(e);
    }
  }
}

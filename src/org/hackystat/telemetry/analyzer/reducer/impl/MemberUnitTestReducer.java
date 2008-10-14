package org.hackystat.telemetry.analyzer.reducer.impl;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducer;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducerException;
import org.hackystat.telemetry.analyzer.reducer.impl.UnitTestReducer.Mode;
import org.hackystat.telemetry.service.server.ServerProperties;
import org.hackystat.utilities.time.interval.Interval;

/**
 * Returns a set of streams providing UnitTest invocation data for each member of the project. 
 * <p>
 * Options:
 * <ol>
 * <li> mode: One of 'TotalCount', 'SuccessCount', or 'FailureCount'. Default is 'TotalCount'. 
 * <li> isCumulative: True or false. Default is false.
 * </ol>
 * 
 * @author Hongbing Kou, Philip Johnson
 */
public class MemberUnitTestReducer implements TelemetryReducer { 
  
  private static UnitTestReducer genericUnitTestReducer = new UnitTestReducer();
  
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
    Mode mode = Mode.TOTALCOUNT;
    boolean isCumulative = false;
    //process options
    if (options.length > 2) {
      throw new TelemetryReducerException("MemberUnitTest reducer takes 2 optional parameters.");
    }
    if (options.length >= 1) {
      try {
        mode = Mode.valueOf(options[0].toUpperCase());
      }
      catch (Exception e) {
        throw new TelemetryReducerException("Illegal mode value.", e);
      }
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
      throw new TelemetryReducerException("Null DPD host in MemberUnitTestReducer");
    }

    // now get the telemetry stream. 
    try {
      TelemetryStreamCollection streams = new TelemetryStreamCollection(null, project, interval);
      // Make a list of emails containing all members plus the owner. 
      List<String> emails = new ArrayList<String>(); 
      emails.addAll(project.getMembers().getMember());
      emails.add(project.getOwner());
      for (String email : emails) {
        streams.add(genericUnitTestReducer.getStream(dpdClient, project, interval, mode, email, 
            isCumulative, email));
      }
      return streams;
    } 
    catch (Exception e) {
      throw new TelemetryReducerException(e);
    }
  }
}

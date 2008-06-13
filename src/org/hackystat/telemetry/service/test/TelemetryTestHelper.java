package org.hackystat.telemetry.service.test;

import org.hackystat.telemetry.service.server.Server;
import org.junit.BeforeClass;

import static org.hackystat.telemetry.service.server.ServerProperties.SENSORBASE_FULLHOST_KEY;
import static org.hackystat.telemetry.service.server.ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY;

/**
 * Provides a helper class to facilitate JUnit testing. 
 * @author Philip Johnson
 */
public class TelemetryTestHelper {

  /** The Sensorbase server used in these tests. */
  @SuppressWarnings("unused")
  private static org.hackystat.sensorbase.server.Server sensorbaseServer;
  /** The DailyProjectData server used in these tests. */
  @SuppressWarnings("unused")
  private static org.hackystat.dailyprojectdata.server.Server dpdServer;  
  /** The Telemetry server used in these tests. */
  private static org.hackystat.telemetry.service.server.Server server;  
  

  /**
   * Constructor.
   */
  public TelemetryTestHelper () {
    // Does nothing.
  }
  
  /** 
   * Starts the server going for these tests. 
   * @throws Exception If problems occur setting up the server. 
   */
  @BeforeClass public static void setupServer() throws Exception {
    // Create testing versions of the Sensorbase, DPD, and Telemetry servers.
    TelemetryTestHelper.sensorbaseServer = org.hackystat.sensorbase.server.Server.newTestInstance();
    TelemetryTestHelper.dpdServer = org.hackystat.dailyprojectdata.server.Server.newTestInstance(); 
    TelemetryTestHelper.server = Server.newTestInstance();
  }
  

  /**
   * Returns the hostname associated with this Telemetry test server. 
   * @return The host name, including the context root. 
   */
  protected String getTelemetryHostName() {
    return TelemetryTestHelper.server.getHostName();
  }
  
  /**
   * Returns the sensorbase hostname that this Telemetry server communicates with.
   * @return The host name, including the context root. 
   */
  protected String getSensorBaseHostName() {
    return TelemetryTestHelper.server.getServerProperties().get(SENSORBASE_FULLHOST_KEY);
  }
  
  /**
   * Returns the DPD hostname that this Telemetry server communicates with.
   * @return The host name, including the context root. 
   */
  protected String getDailyProjectDataHostName() {
    return TelemetryTestHelper.server.getServerProperties().get(DAILYPROJECTDATA_FULLHOST_KEY);
  }
}


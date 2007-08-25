package org.hackystat.telemetry.test;

import org.hackystat.telemetry.server.Server;
import org.junit.BeforeClass;

import static org.hackystat.telemetry.server.ServerProperties.SENSORBASE_HOST_KEY;

/**
 * Provides a helper class to facilitate JUnit testing. 
 * @author Philip Johnson
 */
public class TelemetryTestHelper {

  /** The DailyProjectData server used in these tests. */
  private static Server server;

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
    TelemetryTestHelper.server = Server.newInstance();
  }

  /**
   * Returns the hostname associated with this DPD test server. 
   * @return The host name, including the context root. 
   */
  protected String getTelemetryHostName() {
    return TelemetryTestHelper.server.getHostName();
  }
  
  /**
   * Returns the sensorbase hostname that this DPD server communicates with.
   * @return The host name, including the context root. 
   */
  protected String getSensorBaseHostName() {
    return TelemetryTestHelper.server.getServerProperties().get(SENSORBASE_HOST_KEY);
  }
}


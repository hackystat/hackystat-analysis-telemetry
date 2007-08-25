package org.hackystat.telemetry.resource.ping;

import static org.junit.Assert.assertTrue;

import org.hackystat.telemetry.client.TelemetryClient;
import org.hackystat.telemetry.test.TelemetryTestHelper;
import org.junit.Test;

/**
 * Tests the Ping REST API.
 * 
 * @author Philip Johnson
 */
public class TestPingRestApi extends TelemetryTestHelper {

  /**
   * Test that GET {host}/ping returns the service name.
   * 
   * @throws Exception If problems occur.
   */
  @Test
  public void testPing() throws Exception {
    assertTrue("Checking ping", TelemetryClient.isHost(getTelemetryHostName()));
  }
}

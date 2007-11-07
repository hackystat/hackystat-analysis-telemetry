package org.hackystat.telemetry.service.resource.chart;

import static org.junit.Assert.assertTrue;

import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartIndex;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartDefinition;
import org.hackystat.telemetry.service.test.TelemetryTestHelper;
import org.junit.Test;
import org.junit.Before;

/**
 * Tests the definition and index parts of the Chart REST API.
 * @author Philip Johnson
 */
public class TestChartRestApi extends TelemetryTestHelper {
  
  /** The user for this test case. */
  private String user = "TestChart@hackystat.org";
  
  /** The telemetry client */
  private TelemetryClient telemetryClient;
  
  /**
   * Creates DevEvents on server for use in Telemetry processing. 
   * @throws Exception If problems occur. 
   */
  @Before
  public void setupClient() throws Exception { 
  // Now connect to the Telemetry server. 
  this.telemetryClient = new TelemetryClient(getTelemetryHostName(), user, user);
  telemetryClient.authenticate();
  }
  
  /**
   * Tests the ChartIndex interface. 
   * @throws Exception If problems occur. 
   */
  @Test public void testChartIndex() throws Exception {
    TelemetryChartIndex index = telemetryClient.getChartIndex();
    assertTrue("Got at least one defined chart", index.getTelemetryChartRef().size() > 1);
  }
  
  /**
   * Tests the Chart Definition interface. 
   * @throws Exception If problems occur. 
   */
  @Test public void testChartDefinition() throws Exception {
    String chartName = "DevTime";
    TelemetryChartDefinition chartDef = telemetryClient.getChartDefinition(chartName);
    assertTrue("Got the DevTime chart", chartDef.getName().equals(chartName));
  }
}

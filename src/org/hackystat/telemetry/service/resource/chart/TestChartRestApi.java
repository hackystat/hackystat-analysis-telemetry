package org.hackystat.telemetry.service.resource.chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.telemetry.service.resource.chart.jaxb.ParameterDefinition;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartIndex;
import org.hackystat.telemetry.service.resource.chart.jaxb.Type;
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
  
  /** The telemetry client. */
  private TelemetryClient telemetryClient;
  
  /**
   * Creates DevEvents on server for use in Telemetry processing. 
   * @throws Exception If problems occur. 
   */
  @Before
  public void setupClient() throws Exception {
    // Ensure that user is registered.
    SensorBaseClient.registerUser(this.getSensorBaseHostName(), user);
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
    List<ParameterDefinition> parameters = chartDef.getParameterDefinition();
    assertEquals("Got two parameter definitions", 2, parameters.size());
    ParameterDefinition param = parameters.get(0);
    assertEquals("Getting the member param", "member", param.getName());
    Type type = param.getType();
    assertEquals("Checking the type", "Text", type.getName());
    assertEquals("Checking the default", "*", type.getDefault());
    assertEquals("Checking the value list", 0, type.getValue().size());
  }
}

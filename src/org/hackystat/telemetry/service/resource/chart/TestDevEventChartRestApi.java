package org.hackystat.telemetry.service.resource.chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.telemetry.service.resource.chart.jaxb.Parameter;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartData;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryPoint;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream;
import org.hackystat.telemetry.service.test.TelemetryTestHelper;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;
import org.junit.Before;

/**
 * Tests the DevTime part of the Telemetry REST API. 
 * @author Philip Johnson
 */
public class TestDevEventChartRestApi extends TelemetryTestHelper {
  
  /** The user for this test case. */
  private String user = "TestChart@hackystat.org";
  
  /** The telemetry client */
  private TelemetryClient telemetryClient;
  
  /**
   * Creates DevEvents on server for use in Telemetry processing. 
   * @throws Exception If problems occur. 
   */
  @Before
  public void generateData() throws Exception { 
  // [1] First, create a batch of sensor data.
  SensorDatas batchData = new SensorDatas();
  batchData.getSensorData().add(makeData("2007-08-01T02:00:00", user));
  batchData.getSensorData().add(makeData("2007-08-01T02:10:00", user));
  batchData.getSensorData().add(makeData("2007-08-02T23:55:00", user));
  batchData.getSensorData().add(makeData("2007-08-03T00:01:00", user));
  
  // Connect to the sensorbase and register the user. 
  SensorBaseClient.registerUser(getSensorBaseHostName(), user);
  SensorBaseClient client = new SensorBaseClient(getSensorBaseHostName(), user, user);
  client.authenticate();
  // Send the sensor data to the SensorBase. 
  client.putSensorDataBatch(batchData);
  
  // Now connect to the Telemetry server. 
  this.telemetryClient = new TelemetryClient(getTelemetryHostName(), user, user);
  telemetryClient.authenticate();
  }
  
  
  
  /**
   * Tests the DevTime chart.
   * @throws Exception If problems occur. 
   */
  @Test public void testChartParams() throws Exception {
    String chartName = "DevTime";
    String params = user + ",false"; // make sure no embedded spaces, or else escape them.
    TelemetryChartData chart = telemetryClient.getChart(chartName, user, "Default", "Day", 
          Tstamp.makeTimestamp("2007-08-01"), Tstamp.makeTimestamp("2007-08-03"), params);
    // See if this chart contains 1 stream with 3 data points of 10, 15, and 20.
    List<TelemetryStream> streams = chart.getTelemetryStream();
    assertEquals("Checking only 1 stream returned", 1, streams.size());
    // Get the data points in the single returned stream.
    List<TelemetryPoint> points = streams.get(0).getTelemetryPoint();
    assertEquals("Checking for 3 points", 3, points.size());
    // Check that these three points are 10, 15, and 20.
    assertTrue("Checking point 1 is 0.16", points.get(0).getValue().startsWith("0.16"));
    assertTrue("Checking point 2 is 0.08", points.get(1).getValue().startsWith("0.08"));
    assertTrue("Checking point 3 is 0.08", points.get(2).getValue().startsWith("0.08"));
    List<Parameter> parameters = chart.getParameter();
    assertEquals("Checking first param id", "member", parameters.get(0).getName());
    assertEquals("Checking first param val", user, parameters.get(0).getValue());
    assertEquals("Checking second param id", "cumulative", parameters.get(1).getName());
    assertEquals("Checking second param val", "false", parameters.get(1).getValue());
  }
  
  
  /**
   * Creates a sample SensorData instance given a timestamp and a user. 
   * @param tstampString The timestamp as a string
   * @param user The user.
   * @return The new SensorData DevEvent instance.
   * @throws Exception If problems occur. 
   */
  private static SensorData makeData(String tstampString, String user) throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    String sdt = "DevEvent";
    SensorData data = new SensorData();
    String tool = "Emacs";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(tstamp);
    data.setResource("file://foo/bar/baz.txt");
    data.setRuntime(tstamp);
    Property property = new Property();
    property.setKey("Type");
    property.setValue("StateChange");
    Properties properties = new Properties();
    properties.getProperty().add(property);
    data.setProperties(properties);
    return data;
  }

}

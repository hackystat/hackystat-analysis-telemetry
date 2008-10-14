package org.hackystat.telemetry.service.resource.chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartData;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryPoint;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream;
import org.hackystat.telemetry.service.resource.chart.jaxb.YAxis;
import org.hackystat.telemetry.service.test.TelemetryTestHelper;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;
import org.junit.Before;

/**
 * Tests the Coverage Chart processing. 
 * @author Philip Johnson
 */
public class TestCoverageChartRestApi extends TelemetryTestHelper {
  
  /** The user for this test case. */
  private String user = "TestChart@hackystat.org";
  
  /** The telemetry client. */
  private TelemetryClient telemetryClient;
  
  /** A counter so that each resource (file name) is unique in the sample data. */
  int counter = 0;
  
  /**
   * Creates UnitTests on server for use in Telemetry processing. 
   * @throws Exception If problems occur. 
   */
  @Before
  public void generateData() throws Exception { 
  // [1] First, create a batch of sensor data for Day 1.
  SensorDatas batchData = new SensorDatas();
  String tstampString = "2007-08-01T02:00:00";
  XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
  // Create four instances with unique tstamps and the same runtime. 
  batchData.getSensorData().add(makeData(tstamp, user, tstamp));
  batchData.getSensorData().add(makeData(tstamp, user, tstamp));
  batchData.getSensorData().add(makeData(tstamp, user, tstamp));
  batchData.getSensorData().add(makeData(tstamp, user, tstamp));
  
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
   * Tests the Coverage chart.
   * @throws Exception If problems occur. 
   */
  @Test public void testCoverageChart() throws Exception {
    String chartName = "Coverage";
    String params = "Percentage,line"; 
    TelemetryChartData chart = telemetryClient.getChart(chartName, user, "Default", "Day", 
          Tstamp.makeTimestamp("2007-08-01"), Tstamp.makeTimestamp("2007-08-04"), params);
    // See if this chart contains 1 stream.
    List<TelemetryStream> streams = chart.getTelemetryStream();
    assertEquals("Checking only 1 stream returned", 1, streams.size());
    // Get the data points in the single returned stream.
    List<TelemetryPoint> points = streams.get(0).getTelemetryPoint();
    assertEquals("Checking for 4 points", 4, points.size());
    // Check that these four points are 0 and null (only first day has value).
    assertEquals("Checking point 1 is 66", "66", points.get(0).getValue());
    assertNull("Checking point 2 is null", points.get(1).getValue());
    
    // See if the chart Y-Axis provides the type, lower, and upper bound information. 
    YAxis yAxis = streams.get(0).getYAxis();
    assertEquals("Checking number type", "integer", yAxis.getNumberType());
    assertEquals("Checking lower bound", 0, yAxis.getLowerBound().intValue());
    assertEquals("Checking upper bound", 100, yAxis.getUpperBound().intValue());
  }
  
 
 
  /**
   * Creates a sample SensorData Coverage instance given a timestamp and a user.
   * Only "line" coverage metrics are provided here. 
   *
   * @param tstamp The timestamp, incremented each time to guarantee uniqueness.
   * @param user The user.
   * @param runtime The runtime, which should be the same for all grouped Coverage instances. 
   * @return The new SensorData instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeData(XMLGregorianCalendar tstamp, String user, 
      XMLGregorianCalendar runtime) throws Exception {
    
    String sdt = "Coverage";
    SensorData data = new SensorData();
    String tool = "Emma";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(Tstamp.incrementMinutes(tstamp, counter++));
    data.setResource("file://foo/bar/baz-" + counter + ".java");
    data.setRuntime(runtime);
    Properties prop = new Properties();
    prop.getProperty().add(makeProperty("line_Uncovered", "1"));
    prop.getProperty().add(makeProperty("line_Covered", "2"));
    data.setProperties(prop);
    return data;
  }

  /**
   * Creates and returns a Property initialized with key and value. 
   * @param key The key. 
   * @param value The value.
   * @return The Property instance. 
   */
  private Property makeProperty(String key, String value) {
    Property property = new Property();
    property.setKey(key);
    property.setValue(value);
    return property;
  }
}

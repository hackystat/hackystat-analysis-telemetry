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
import org.hackystat.telemetry.service.test.TelemetryTestHelper;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;
import org.junit.Before;

/**
 * Tests the FileMetric Chart processing. 
 * @author Philip Johnson
 */
public class TestFileMetricChartRestApi extends TelemetryTestHelper {
  
  /** The user for this test case. */
  private String user = "TestChart@hackystat.org";
  
  /** The telemetry client */
  private TelemetryClient telemetryClient;
  
  /** A counter so that each resource (file name) is unique in the sample data. */
  int counter = 0;
  
  /**
   * Creates FileMetrics on server for use in Telemetry processing. 
   * @throws Exception If problems occur. 
   */
  @Before
  public void generateData() throws Exception { 
  // [1] First, create a batch of sensor data for Day 1.
  SensorDatas batchData = new SensorDatas();
  String tstampString = "2007-08-01T02:00:00";
  XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
  // Create four instances with unique tstamps and the same runtime. 
  batchData.getSensorData().add(makeData(tstamp, 100));
  batchData.getSensorData().add(makeData(tstamp, 200));
  
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
   * Tests the FileMetric chart.
   * @throws Exception If problems occur. 
   */
  @Test public void testFileMetricChart() throws Exception {
    String chartName = "FileMetric";
    String params = "TotalLines"; 
    TelemetryChartData chart = telemetryClient.getChart(chartName, user, "Default", "Day", 
          Tstamp.makeTimestamp("2007-08-01"), Tstamp.makeTimestamp("2007-08-04"), params);
    // See if this chart contains 1 stream.
    List<TelemetryStream> streams = chart.getTelemetryStream();
    assertEquals("Checking only 1 stream returned", 1, streams.size());
    // Get the data points in the single returned stream.
    List<TelemetryPoint> points = streams.get(0).getTelemetryPoint();
    assertEquals("Checking for 4 points", 4, points.size());
    // Check that these four points are 0 and null (only first day has value).
    assertEquals("Checking point 1 is 0.3", "0.3", points.get(0).getValue());
    assertNull("Checking point 2 is null", points.get(1).getValue());
  }
  
 
 
  /**
   * Creates a sample SensorData FileMetric instance given a timestamp and a size.
   * Only the "TotalLines" size metric is saved. 
   * @param tstamp The timestamp, incremented each time to guarantee uniqueness.
   * @param size The size to create.
   * @return The new SensorData instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeData(XMLGregorianCalendar tstamp, int size) throws Exception {
    String sdt = "FileMetric";
    SensorData data = new SensorData();
    String tool = "SCLC";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(Tstamp.incrementMinutes(tstamp, counter++));
    data.setRuntime(tstamp);
    data.setResource("/users/johnson/Foo-" + counter + ".java");
    Properties properties = new Properties();
    properties.getProperty().add(makeProperty("TotalLines", String.valueOf(size)));
    data.setProperties(properties);
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

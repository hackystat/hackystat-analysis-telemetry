package org.hackystat.telemetry.service.resource.chart;

import static org.junit.Assert.assertEquals;

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
 * Tests the Churn Chart processing. 
 * @author Philip Johnson
 */
public class TestCyclomaticComplexityChartRestApi extends TelemetryTestHelper {
  
  /** The user for this test case. */
  private String user = "TestChart@hackystat.org";
  
  /** The telemetry client */
  private TelemetryClient telemetryClient;
  
  /**
   * Creates FileMetrics on server for use in Telemetry processing. 
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
   * Tests the chart.
   * @throws Exception If problems occur. 
   */
  @Test public void testChart() throws Exception {
    String chartName = "CyclomaticComplexity";
    String params = "AverageComplexityPerMethod,10,JavaNCSS"; 
    String defaultProj = "Default";
    String day = "Day";
    XMLGregorianCalendar start = Tstamp.makeTimestamp("2007-08-01");
    XMLGregorianCalendar end = Tstamp.makeTimestamp("2007-08-04");
    
    TelemetryChartData chart = 
      telemetryClient.getChart(chartName, user, defaultProj, day, start, end, params);
    // See if this chart contains 1 stream.
    List<TelemetryStream> streams = chart.getTelemetryStream();
    assertEquals("Checking only 1 stream returned", 1, streams.size());
    // Get the data points in the single returned stream.
    List<TelemetryPoint> points = streams.get(0).getTelemetryPoint();
    assertEquals("Checking for 4 points", 4, points.size());
    // Check that these four points are all 20.
    assertEquals("Checking point 1 is 20", "20.0", points.get(0).getValue());
    assertEquals("Checking point 2 is 20", "20.0", points.get(1).getValue());
    assertEquals("Checking point 3 is 20", "20.0", points.get(2).getValue());
    
    // Now check the other modes
    params = "TotalMethods,10,JavaNCSS";
    chart = telemetryClient.getChart(chartName, user, defaultProj, day, start, end, params);
    streams = chart.getTelemetryStream();
    points = streams.get(0).getTelemetryPoint();
    assertEquals("Checking point 1 is 2", "2.0", points.get(0).getValue());
    
    params = "TotalLines,10,JavaNCSS";
    chart = telemetryClient.getChart(chartName, user, defaultProj, day, start, end, params);
    streams = chart.getTelemetryStream();
    points = streams.get(0).getTelemetryPoint();
    assertEquals("Checking point 1 is 10", "10.0", points.get(0).getValue());
    
    params = "TotalMethodsAboveComplexityThreshold,10,JavaNCSS";
    chart = telemetryClient.getChart(chartName, user, defaultProj, day, start, end, params);
    streams = chart.getTelemetryStream();
    points = streams.get(0).getTelemetryPoint();
    assertEquals("Checking point 1 is 2 ", "2.0", points.get(0).getValue());
    
  }
  

  /**
   * Creates a sample SensorData FileMetric instance given a timestamp and a user.
   *
   * @param tstampString The timestamp as a string
   * @param user The user.
   * @return The new SensorData DevEvent instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeData(String tstampString, String user) throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    String sdt = "FileMetric";
    SensorData data = new SensorData();
    String tool = "JavaNCSS";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(tstamp);
    data.setResource("file://foo/bar/baz.txt");
    data.setRuntime(tstamp);

    Properties prop = new Properties();
    prop.getProperty().add(makeProperty("TotalLines", "10"));
    prop.getProperty().add(makeProperty("CyclomaticComplexityList", "20,20"));
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

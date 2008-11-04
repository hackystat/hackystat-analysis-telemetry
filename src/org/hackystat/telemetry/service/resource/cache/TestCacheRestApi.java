package org.hackystat.telemetry.service.resource.cache;

import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Properties;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDatas;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.telemetry.service.test.TelemetryTestHelper;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;
import org.junit.Before;

/**
 * Tests the Telemetry Cache API.
 * @author Philip Johnson
 */
public class TestCacheRestApi extends TelemetryTestHelper {
  
  /** The user for this test case. */
  private String user = "TestCache@hackystat.org";
  
  /** The telemetry client. */
  private TelemetryClient telemetryClient;
  
  /**
   * Creates Build sensor data on server for use in Telemetry processing. 
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
   * Tests the cache api.
   * @throws Exception If problems occur. 
   */
  @Test public void testBuildChartDefault() throws Exception {  //NOPMD no assertions here.
    // Make a chart.
    String chartName = "Build";
    String project = "Default";
    //String params = "FailureCount,*,false"; // make sure no embedded spaces, or else escape them.
    String params = "*,*,Integration,false";
    telemetryClient.getChart(chartName, user, project, "Day", 
          Tstamp.makeTimestamp("2007-08-01"), Tstamp.makeTimestamp("2007-08-04"), params);
    
    // Note that caching is not enabled, so these tests are simply making sure the protocol is OK.
    telemetryClient.clearCache();
    telemetryClient.clearCache(project);
  }
  
  
  /**
   * Creates a sample SensorData UnitTest instance given a timestamp and a user.
   *
   * @param tstampString The timestamp as a string
   * @param user The user.
   * @return The new SensorData DevEvent instance.
   * @throws Exception If problems occur.
   */
  private SensorData makeData(String tstampString, String user) throws Exception {
    XMLGregorianCalendar tstamp = Tstamp.makeTimestamp(tstampString);
    String sdt = "Build";
    SensorData data = new SensorData();
    String tool = "Ant";
    data.setTool(tool);
    data.setOwner(user);
    data.setSensorDataType(sdt);
    data.setTimestamp(tstamp);
    data.setResource("file://foo/bar/baz.txt");
    data.setRuntime(tstamp);

    Properties prop = new Properties();
    prop.getProperty().add(makeProperty("Result", "Success"));
    prop.getProperty().add(makeProperty("Type", "Integration"));
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

package org.hackystat.telemetry.service.resource.chart;

import static org.junit.Assert.assertEquals;

import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.dailyprojectdata.resource.issue.TestIssueDataParser;
import org.hackystat.sensorbase.client.SensorBaseClient;
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
 * Tests the Issue Chart processing. 
 * @author Shaoxuan Zhang, Philip Johnson
 */
public class TestIssueChartRestApi extends TelemetryTestHelper {
  
  /** The user for this test case. */
  private static final String NEW = "New";
  private static final String STARTED = "Started";
  private static final String ACCEPTED = "Accepted";
  private static final String FIXED = "Fixed";
  private static final String DEFECT = "Defect";
  private static final String TASK = "Task";
  private static final String ENHANCEMENT = "Enhancement";
  private static final String MEDIUM = "Medium";
  private static final String HIGH = "High";
  private static final String CRITICAL = "Critical";

  private static final String dataOwner = "testDataOwner@hackystat.org";
  private static final String testUser1 = "tester1@hackystat.org";
  private static final String testUser2 = "tester2@hackystat.org";
  
  private static final String defaultProject = "Default";
  private static final String day = "Day";
  
  private static final String[] testData1T1 = {"21", DEFECT, ACCEPTED, MEDIUM, "", testUser1, 
      "2008-09-07T11:00:00"};
  private static final String[] testData1T2 = {"21", ENHANCEMENT, FIXED, MEDIUM, "8.4", testUser1, 
      "2008-09-07T11:00:00"};
  
  private static final String[] testData2T1 = {"23", ENHANCEMENT, FIXED, MEDIUM, "", testUser1, 
      "2009-07-20T00:24:06"};
  private static final String[] testData2T2 = {"23", ENHANCEMENT, ACCEPTED, HIGH, "", testUser2, 
      "2009-07-20T00:24:06"};
  
  private static final String[] testData3T1 = {"14", ENHANCEMENT, NEW, HIGH, "", testUser2, 
      "2009-06-20T14:35:32"};
  
  private static final String[] testData4T1 = {"25", DEFECT, STARTED, CRITICAL, "8.4", testUser1, 
      "2009-07-21T20:00:00"};
  
  private static final String[] testData5T1 = {"18", TASK, FIXED, HIGH, "", testUser2, 
      "2009-07-21T18:00:00"};
  
  private static final String testTimeString1 = "2009-07-20T00:00:00";
  private static final String testTimeString2 = "2009-07-22T10:00:00";
  /** The telemetry client. */
  private TelemetryClient telemetryClient;
  
  /**
   * Creates Commits on server for use in Telemetry processing. 
   * @throws Exception If problems occur. 
   */
  @Before
  public void generateData() throws Exception { 
    // [1] First, create a batch of sensor data.
    SensorDatas batchData = new SensorDatas();
    XMLGregorianCalendar testTime1 = Tstamp.makeTimestamp(testTimeString1);
    XMLGregorianCalendar testTime2 = Tstamp.makeTimestamp(testTimeString2);
    // First, create a batch of Issue sensor data.
    batchData.getSensorData().add(TestIssueDataParser.makeIssueSensorData(dataOwner,
        new String[][]{testData1T1, testData1T2}, 
        new XMLGregorianCalendar[]{testTime1, testTime2}));
    batchData.getSensorData().add(TestIssueDataParser.makeIssueSensorData(dataOwner,
        new String[][]{testData2T1, testData2T2}, 
        new XMLGregorianCalendar[]{testTime1, testTime2}));
    batchData.getSensorData().add(TestIssueDataParser.makeIssueSensorData(dataOwner,
        new String[][]{testData3T1}, new XMLGregorianCalendar[]{testTime1}));
    batchData.getSensorData().add(TestIssueDataParser.makeIssueSensorData(dataOwner,
        new String[][]{testData4T1}, new XMLGregorianCalendar[]{testTime2}));
    batchData.getSensorData().add(TestIssueDataParser.makeIssueSensorData(dataOwner,
        new String[][]{testData5T1}, new XMLGregorianCalendar[]{testTime2}));
    
    // Connect to the sensorbase and register the user. 
    SensorBaseClient.registerUser(getSensorBaseHostName(), dataOwner);
    SensorBaseClient client = new SensorBaseClient(getSensorBaseHostName(), dataOwner, dataOwner);
    client.authenticate();
    // Send the sensor data to the SensorBase. 
    client.putSensorDataBatch(batchData);
    
    // Now connect to the Telemetry server. 
    this.telemetryClient = new TelemetryClient(getTelemetryHostName(), dataOwner, dataOwner);
    telemetryClient.authenticate();
  }
  
  
  /**
   * Tests the chart.
   * @throws Exception If problems occur. 
   */
  @Test public void testIssueChartAllCount() throws Exception {
    String params = "*,all"; 
    TelemetryChartData chart = telemetryClient.getChart("Issue", dataOwner, defaultProject, day, 
        Tstamp.makeTimestamp(testTimeString1), Tstamp.makeTimestamp(testTimeString2), params);
    // See if this chart contains 1 stream.
    List<TelemetryStream> streams = chart.getTelemetryStream();
    assertEquals("Checking only 1 stream returned", 1, streams.size());
    // Get the data points in the single returned stream.
    List<TelemetryPoint> points = streams.get(0).getTelemetryPoint();
    assertEquals("Checking for 3 points", 3, points.size());
    assertEquals("Checking point 1 is 3", "3", points.get(0).getValue());
    assertEquals("Checking point 2 is 5", "5", points.get(1).getValue());
    assertEquals("Checking point 3 is 5", "5", points.get(2).getValue());
  }
  
  /**
   * Tests the chart.
   * @throws Exception If problems occur. 
   */
  @Test public void testIssueChartOpenCount() throws Exception {
    String params = "*,open"; 
    TelemetryChartData chart = telemetryClient.getChart("Issue", dataOwner, defaultProject, day, 
        Tstamp.makeTimestamp(testTimeString1), Tstamp.makeTimestamp(testTimeString2), params);
    // See if this chart contains 1 stream.
    List<TelemetryStream> streams = chart.getTelemetryStream();
    assertEquals("Checking only 1 stream returned", 1, streams.size());
    // Get the data points in the single returned stream.
    List<TelemetryPoint> points = streams.get(0).getTelemetryPoint();
    assertEquals("Checking for 3 points", 3, points.size());
    assertEquals("Checking point 1 is 2", "2", points.get(0).getValue());
    assertEquals("Checking point 2 is 2", "2", points.get(1).getValue());
    assertEquals("Checking point 3 is 3", "3", points.get(2).getValue());
  }

}

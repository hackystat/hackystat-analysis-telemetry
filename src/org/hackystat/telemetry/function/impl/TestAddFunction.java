package org.hackystat.telemetry.function.impl;

import junit.framework.TestCase;

import org.hackystat.telemetry.function.TelemetryFunctionManager;
import org.hackystat.telemetry.model.TelemetryDataPoint;
import org.hackystat.telemetry.model.TelemetryStream;
import org.hackystat.telemetry.model.TelemetryStreamCollection;
import org.hackystat.telemetry.model.TestTelemetryStreamCollection;
import org.hackystat.telemetry.util.Day;
import org.hackystat.telemetry.util.project.Project;
import org.hackystat.telemetry.util.project.ProjectManager;
import org.hackystat.telemetry.util.selector.interval.DayInterval;

/**
 * Test suite for <code>AddFunction</code<>
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TestAddFunction extends TestCase {

  private TelemetryFunctionManager manager = TelemetryFunctionManager.getInstance();
  private String projectName = "TestAddFunction";
  private Project project;
  private Day startDay;
  private DayInterval interval;

  /**
   * Sets up this test case.
   * @throws Exception If test case cannot be set up.
   */
  protected void setUp() throws Exception {

    this.project = ProjectManager.getInstance().createTestProjectClientSide(projectName);
    this.startDay = Day.getInstance("01-Jan-2004"); 
    this.interval = new DayInterval(this.startDay, this.startDay.inc(1));
  }
  
  /**
   * Tears down this test case.
   * @throws Exception If tear down failed.
   */
  protected void tearDown() throws Exception {
    ProjectManager.getInstance().deleteProject(this.projectName);
  }
  
  /**
   * Tests with 2 numbers.
   * @throws Exception If test fails.
   */
  public void testWith2Numbers() throws Exception {
    Number num1 = new Integer(1);
    Number num2 = new Integer(3);
    Object result = this.manager.compute("Add", new Number[]{num1, num2});
    assertEquals(4, ((Integer) result).intValue());
    
    num1 = new Integer(1);
    num2 = new Double(3.5);
    result = this.manager.compute("Add", new Number[]{num1, num2});
    assertEquals(4.50, ((Double) result).doubleValue(), 0.00001);
    
    num1 = new Double(1.1);
    num2 = new Double(3.5);
    result = this.manager.compute("Add", new Number[]{num1, num2});
    assertEquals(4.60, ((Double) result).doubleValue(), 0.00001);
  }
  
  /**
   * Tests with 1 telemetry stream collection and 1 numbers.
   * @throws Exception If test fails.
   */
  public void testWithTelemetryStreamCollectionAndNumber() throws Exception {    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream = new TelemetryStream("test");
    inputStream.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(0)));
    inputStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(1)));
    input.add(inputStream);

    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.manager.compute("Add", new Object[]{input, new Double(0.1)});
    
    TelemetryStreamCollection expected 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream expectedStream = new TelemetryStream("test");
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay, new Double(0.1)));
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Double(1.1)));
    expected.add(expectedStream);
    
    TestTelemetryStreamCollection.assertEqualsIgnoreName(expected, output);
  }
  
  /**
   * Tests with 1 numbers and  1 telemetry stream collection.
   * @throws Exception If test fails.
   */
  public void testWithNumberAndTelemetryStreamCollection() throws Exception {
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream = new TelemetryStream("test");
    inputStream.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(0)));
    inputStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(1)));
    input.add(inputStream);

    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.manager.compute("Add", new Object[]{new Double(0.1), input});

    TelemetryStreamCollection expected 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream expectedStream = new TelemetryStream("test");
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay, new Double(0.1)));
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Double(1.1)));
    expected.add(expectedStream);

    TestTelemetryStreamCollection.assertEqualsIgnoreName(expected, output);
  }
  
  /**
   * Tests with 2 telemetry stream collections.
   * @throws Exception If test fails.
   */
  public void testWith2TelemetryStreamCollections() throws Exception {
    TelemetryStreamCollection input1
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream1 = new TelemetryStream("test");
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(0)));
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(1)));
    input1.add(inputStream1);

    TelemetryStreamCollection input2 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream2 = new TelemetryStream("test");
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(10)));
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(11)));
    input2.add(inputStream2);

    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.manager.compute("Add", new Object[]{input1, input2});

    TelemetryStreamCollection expected 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream expectedStream = new TelemetryStream("test");
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(10)));
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(12)));
    expected.add(expectedStream);

    TestTelemetryStreamCollection.assertEqualsIgnoreName(expected, output);    
  }
}

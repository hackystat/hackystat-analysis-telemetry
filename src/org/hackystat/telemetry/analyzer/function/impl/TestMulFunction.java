package org.hackystat.telemetry.analyzer.function.impl;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.function.TelemetryFunctionManager;
import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.model.TestTelemetryStreamCollection;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.utilities.time.interval.DayInterval;
import org.hackystat.utilities.time.period.Day;

/**
 * Test suite for <code>MulFunction</code<>
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TestMulFunction extends TestCase {

  private TelemetryFunctionManager manager = TelemetryFunctionManager.getInstance();
  private String projectName = "TestMulFunction";
  private Project project;
  private Day startDay;
  private DayInterval interval;
  private String mul = "mul";
  private String test = "test";
  
  

  /**
   * Sets up this test case.
   * @throws Exception If test case cannot be set up.
   */
  @Override
  protected void setUp() throws Exception {

    //this.project = ProjectManager.getInstance().createTestProjectClientSide(projectName);
    this.project = new Project();
    this.project.setName(projectName);
    this.startDay = Day.getInstance("01-Jan-2004"); 
    this.interval = new DayInterval(this.startDay, this.startDay.inc(1));
  }
  
  /**
   * Tears down this test case.
   * @throws Exception If tear down failed.
   */
  @Override
  protected void tearDown() throws Exception {
    //ProjectManager.getInstance().deleteProject(this.projectName);
  }
  
  /**
   * Tests with 2 numbers.
   * @throws Exception If test fails.
   */
  public void testWith2Numbers() throws Exception {
    Number num1 = Integer.valueOf(1);
    Number num2 = Integer.valueOf(3);
    Object result = this.manager.compute(mul, new Number[]{num1, num2});
    assertEquals(3, ((Integer) result).intValue());
    
    num1 = Integer.valueOf(1);
    num2 = new Double(3.5);
    result = this.manager.compute(mul, new Number[]{num1, num2});
    assertEquals(3.50, ((Double) result).doubleValue(), 0.00001);
    
    num1 = new Double(1.1);
    num2 = new Double(3.5);
    result = this.manager.compute(mul, new Number[]{num1, num2});
    assertEquals(3.85, ((Double) result).doubleValue(), 0.00001);
    
    num1 = Integer.valueOf(0);
    num2 = Integer.valueOf(0);
    result = this.manager.compute(mul, new Number[]{num1, num2});
    assertEquals(0, ((Integer) result).intValue());
  }
  
  /**
   * Tests with 1 telemetry stream collection and 1 numbers.
   * @throws Exception If test fails.
   */
  public void testWithTelemetryStreamCollectionAndNumber() throws Exception { //NOPMD assert OK    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection(test, this.project, this.interval);
    TelemetryStream inputStream = new TelemetryStream(test);
    inputStream.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(2)));
    inputStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(3)));
    input.add(inputStream);

    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.manager.compute(mul, new Object[]{input, new Double(0.1)});
    
    TelemetryStreamCollection expected 
        = new TelemetryStreamCollection(test, this.project, this.interval);
    TelemetryStream expectedStream = new TelemetryStream(test);
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay, new Double(0.2)));
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Double(0.3)));
    expected.add(expectedStream);
    
    TestTelemetryStreamCollection.assertEqualsIgnoreName(expected, output);
  }
  
  /**
   * Tests with 1 numbers and  1 telemetry stream collection.
   * @throws Exception If test fails.
   */
  public void testWithNumberAndTelemetryStreamCollection() throws Exception { //NOPMD assert OK
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection(test, this.project, this.interval);
    TelemetryStream inputStream = new TelemetryStream(test);
    inputStream.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(2)));
    inputStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(3)));
    input.add(inputStream);

    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.manager.compute(mul, new Object[]{new Double(0.1), input});

    TelemetryStreamCollection expected 
        = new TelemetryStreamCollection(test, this.project, this.interval);
    TelemetryStream expectedStream = new TelemetryStream(test);
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay, new Double(0.2)));
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Double(0.3)));
    expected.add(expectedStream);

    TestTelemetryStreamCollection.assertEqualsIgnoreName(expected, output);
  }
  
  /**
   * Tests with 2 telemetry stream collections.
   * @throws Exception If test fails.
   */
  public void testWith2TelemetryStreamCollections() throws Exception { //NOPMD assert OK
    TelemetryStreamCollection input1
        = new TelemetryStreamCollection(test, this.project, this.interval);
    TelemetryStream inputStream1 = new TelemetryStream(test);
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(0)));
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(1)));
    input1.add(inputStream1);

    TelemetryStreamCollection input2 
        = new TelemetryStreamCollection(test, this.project, this.interval);
    TelemetryStream inputStream2 = new TelemetryStream(test);
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(10)));
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(11)));
    input2.add(inputStream2);

    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.manager.compute(mul, new Object[]{input1, input2});

    TelemetryStreamCollection expected 
        = new TelemetryStreamCollection(test, this.project, this.interval);
    TelemetryStream expectedStream = new TelemetryStream(test);
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(0)));
    expectedStream.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(11)));
    expected.add(expectedStream);

    TestTelemetryStreamCollection.assertEqualsIgnoreName(expected, output);    
  }
}
package org.hackystat.telemetry.analyzer.function.impl;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.function.TelemetryFunctionManager;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.utilities.time.interval.DayInterval;
import org.hackystat.utilities.time.period.Day;

/**
 * Test suite for <code>IdempotentFunction</code>.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TestIdempotentFunction extends TestCase {

  private TelemetryFunctionManager manager = TelemetryFunctionManager.getInstance();
  private String projectName = "TestIdempotentFunction";
  private Project project;
  private DayInterval interval;

  /**
   * Sets up this test case.
   * @throws Exception If test case cannot be set up.
   */
  @Override
  protected void setUp() throws Exception {

    //this.project = ProjectManager.getInstance().createTestProjectClientSide(projectName);
    this.project = new Project();
    this.project.setName(projectName);   
    Day startDay = Day.getInstance("01-Jan-2004"); 
    this.interval = new DayInterval(startDay, startDay.inc(1));
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
   * Tests with numbers.
   * @throws Exception If test fails.
   */
  public void testNumbers() throws Exception {
    Number num = Integer.valueOf(1);
    Object result = this.manager.compute("idempotent", new Number[]{num});
    assertSame(num, result);
  }
  
  /**
   * Tests with 1 telemetry stream collection and 1 numbers.
   * @throws Exception If test fails.
   */
  public void testWithTelemetryStreamCollectionAndNumber() throws Exception {    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.manager.compute("idempotent", new Object[]{input});
    assertSame(input, output);
  }
}
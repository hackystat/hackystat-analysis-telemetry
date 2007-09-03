package org.hackystat.telemetry.function.impl;

import junit.framework.TestCase;

import org.hackystat.telemetry.function.TelemetryFunctionManager;
import org.hackystat.telemetry.model.TelemetryStreamCollection;
import org.hackystat.telemetry.util.Day;
import org.hackystat.telemetry.util.project.Project;
import org.hackystat.telemetry.util.project.ProjectManager;
import org.hackystat.telemetry.util.selector.interval.DayInterval;

/**
 * Test suite for <code>IdempotentFunction</code<>
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TestIdempotentFunction extends TestCase {

  private TelemetryFunctionManager manager = TelemetryFunctionManager.getInstance();
  private String projectName = "TestIdempotentFunction";
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
   * Tests with numbers.
   * @throws Exception If test fails.
   */
  public void testNumbers() throws Exception {
    Number num = new Integer(1);
    Object result = this.manager.compute("Idempotent", new Number[]{num});
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
        this.manager.compute("Idempotent", new Object[]{input});
    assertSame(input, output);
  }
}
package org.hackystat.telemetry.function.impl;

import junit.framework.TestCase;

import org.hackystat.telemetry.function.TelemetryFunctionManager;
import org.hackystat.telemetry.model.TelemetryDataPoint;
import org.hackystat.telemetry.model.TelemetryStream;
import org.hackystat.telemetry.model.TelemetryStreamCollection;
import org.hackystat.telemetry.util.Day;
import org.hackystat.telemetry.util.project.Project;
import org.hackystat.telemetry.util.project.ProjectManager;
import org.hackystat.telemetry.util.selector.interval.DayInterval;

/**
 * Test suite for <code>FilterZeroFunction</code<>
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TestFilterZeroFunction extends TestCase {
  
  private TelemetryFunctionManager manager = TelemetryFunctionManager.getInstance();
  private String projectName = "TestFilterZeroFunction";
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
   * Tests in GE mode.
   * @throws Exception If test fails.
   */
  public void testModeGE() throws Exception {    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream1 = new TelemetryStream("test1"); //remain
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(1)));
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(2)));
    input.add(inputStream1);
    TelemetryStream inputStream2 = new TelemetryStream("test2"); 
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(0)));
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(0)));
    input.add(inputStream2);
    TelemetryStream inputStream3 = new TelemetryStream("test3");
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay, null));
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), null));
    input.add(inputStream3);
    TelemetryStream inputStream4 = new TelemetryStream("test4");
    inputStream4.addDataPoint(new TelemetryDataPoint(this.startDay, new Double(0)));
    inputStream4.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Double(0)));
    input.add(inputStream4);
    TelemetryStream inputStream5 = new TelemetryStream("test5");
    inputStream5.addDataPoint(new TelemetryDataPoint(this.startDay, new Float(0)));
    inputStream5.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Float(0)));
    input.add(inputStream5);
    TelemetryStream inputStream6 = new TelemetryStream("test6"); //remain
    inputStream6.addDataPoint(new TelemetryDataPoint(this.startDay, new Double(Double.NaN)));
    inputStream6.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Double(Double.NaN)));
    input.add(inputStream6);
    TelemetryStream inputStream7 = new TelemetryStream("test7"); //remain
    inputStream7.addDataPoint(new TelemetryDataPoint(this.startDay, new Double(2.3)));
    inputStream7.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Double(0)));
    input.add(inputStream7);
    TelemetryStream inputStream8 = new TelemetryStream("test8"); //remain
    inputStream8.addDataPoint(new TelemetryDataPoint(this.startDay, new Double(2.3)));
    inputStream8.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), null));
    input.add(inputStream8);
    TelemetryStream inputStream9 = new TelemetryStream("test9"); //remain
    inputStream9.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(2)));
    inputStream9.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), null));
    input.add(inputStream9);
    
    TelemetryStreamCollection output 
       = (TelemetryStreamCollection) this.manager.compute("FilterZero", new Object[]{input});
    
    assertEquals("test", output.getName());
    assertSame(this.project, output.getProject());
    assertSame(this.interval, output.getInterval());
    assertEquals(5, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get("test1"));
    assertSame(inputStream6, output.get("test6"));
    assertSame(inputStream7, output.get("test7"));
    assertSame(inputStream8, output.get("test8"));
    assertSame(inputStream9, output.get("test9"));
  }
}
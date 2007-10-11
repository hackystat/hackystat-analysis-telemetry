package org.hackystat.telemetry.analyzer.function.impl;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.util.Day;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.telemetry.analyzer.util.selector.interval.DayInterval;

/**
 * Test suite for <code>FilterFunction</code<>
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TestFilterFunction extends TestCase {

  private FilterFunction filterFunction = new FilterFunction("Filter");
  private String projectName = "TestFilterFunction";
  private Project project;
  private Day startDay;
  private DayInterval interval;

  /**
   * Sets up this test case.
   * @throws Exception If test case cannot be set up.
   */
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
  protected void tearDown() throws Exception {
    //ProjectManager.getInstance().deleteProject(this.projectName);
  }
  
  /**
   * Tests the filter in absolute mode.
   * @throws Exception If test fails.
   */
  public void testFilterAbsoluteMode() throws Exception {    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream1 = new TelemetryStream("test1");
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(1)));
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(2)));
    input.add(inputStream1);
    TelemetryStream inputStream2 = new TelemetryStream("test2");
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(3)));
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(4)));
    input.add(inputStream2);
    TelemetryStream inputStream3 = new TelemetryStream("test3");
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(0)));
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(5)));
    input.add(inputStream3);
    
    //Mode AllAbove
    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Min", "Above", new Integer(2)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get("test2"));

    //Mode AllBlow
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Max", "Below", new Integer(3)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get("test1"));

    //Mode AnyAbove
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Max", "Above", new Integer(3)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get("test2"));
    assertSame(inputStream3, output.get("test3"));
    
    //Mode AnyBelow
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Min", "Below", new Integer(3)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get("test1"));
    assertSame(inputStream3, output.get("test3"));    
  }
  
  /**
   * Tests the filter in relative mode.
   * @throws Exception If test fails.
   */
  public void testFilterRelativeMode() throws Exception {    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream1 = new TelemetryStream("test1");
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(1)));
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(1)));
    input.add(inputStream1);
    TelemetryStream inputStream2 = new TelemetryStream("test2");
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(2)));
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(2)));
    input.add(inputStream2);
    TelemetryStream inputStream3 = new TelemetryStream("test3");
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(3)));
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(3)));
    input.add(inputStream3);

    //Mode TopPercent
    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "TopPercent", new Integer(30)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream3, output.get("test3"));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "TopPercent", new Integer(60)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get("test2"));
    assertSame(inputStream3, output.get("test3"));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "TopPercent", new Integer(0)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "TopPercent", new Integer(100)});
    assertEquals(3, output.getTelemetryStreams().size());
    
    //Mode BottomPercent
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "BottomPercent", new Integer(30)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get("test1"));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "BottomPercent", new Integer(60)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get("test1"));
    assertSame(inputStream2, output.get("test2"));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "BottomPercent", new Integer(0)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "BottomPercent", new Integer(100)});
    assertEquals(3, output.getTelemetryStreams().size());
    
    //Mode Top
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Top", new Integer(2)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get("test2"));
    assertSame(inputStream3, output.get("test3"));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Top", new Integer(-1)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Top", new Integer(0)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Top", new Integer(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Top", new Integer(2)});
    assertEquals(2, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Top", new Integer(3)});
    assertEquals(3, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Top", new Integer(4)});
    assertEquals(3, output.getTelemetryStreams().size());

    //Mode Bottom
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Bottom", new Integer(2)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get("test1"));
    assertSame(inputStream2, output.get("test2"));  
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Bottom", new Integer(-1)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Bottom", new Integer(0)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Bottom", new Integer(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Bottom", new Integer(2)});
    assertEquals(2, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Bottom", "3"});
    assertEquals(3, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Bottom", "4"});
    assertEquals(3, output.getTelemetryStreams().size());
  }
  
  /**
   * Tests the rank functions.
   * @throws Exception If test fails.
   */
  public void testRankFunctions() throws Exception {    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream1 = new TelemetryStream("test1");
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(100)));
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(50)));
    input.add(inputStream1);
    TelemetryStream inputStream2 = new TelemetryStream("test2");
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(3)));
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(4)));
    input.add(inputStream2);
    TelemetryStream inputStream3 = new TelemetryStream("test3");
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay, new Integer(120)));
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), new Integer(-100)));
    input.add(inputStream3);
    
    FilterFunction.RankFunction rank = null;
    
    //Average: (150/2, 7/2, 20/2)
    rank = new FilterFunction.AverageRankFunction();
    assertEquals(150.0 / 2, rank.getRank(inputStream1), 0);
    assertEquals(7.0 / 2, rank.getRank(inputStream2), 0);
    assertEquals(20.0 / 2, rank.getRank(inputStream3), 0);
    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Avg", "Top", new Integer(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get("test1"));

    //Max: (100, 4, 120)
    rank = new FilterFunction.MaxRankFunction();
    assertEquals(100.0, rank.getRank(inputStream1), 0);
    assertEquals(4.0, rank.getRank(inputStream2), 0);
    assertEquals(120.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Max", "Top", new Integer(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream3, output.get("test3"));

    //Min (50, 3, -100)
    rank = new FilterFunction.MinRankFunction();
    assertEquals(50.0, rank.getRank(inputStream1), 0);
    assertEquals(3.0, rank.getRank(inputStream2), 0);
    assertEquals(-100.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Min", "Top", new Integer(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get("test1"));

    //Min (50, 4, -100)
    rank = new FilterFunction.LastRankFunction();
    assertEquals(50.0, rank.getRank(inputStream1), 0);
    assertEquals(4.0, rank.getRank(inputStream2), 0);
    assertEquals(-100.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Last", "Top", new Integer(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get("test1"));
    
    //Delta (50, 1, 220)
    rank = new FilterFunction.DeltaRankFunction();
    assertEquals(50.0, rank.getRank(inputStream1), 0);
    assertEquals(1.0, rank.getRank(inputStream2), 0);
    assertEquals(220.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Delta", "Top", new Integer(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream3, output.get("test3"));
    
    //SimpleDelta (-50, 1, -220)
    rank = new FilterFunction.SimpleDeltaRankFunction();
    assertEquals(-50.0, rank.getRank(inputStream1), 0);
    assertEquals(1.0, rank.getRank(inputStream2), 0);
    assertEquals(-220.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "SimpleDelta", "Top", new Integer(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get("test2"));
  }
}
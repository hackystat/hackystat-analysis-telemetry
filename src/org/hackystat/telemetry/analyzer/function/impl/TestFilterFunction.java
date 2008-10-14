package org.hackystat.telemetry.analyzer.function.impl;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.utilities.time.interval.DayInterval;
import org.hackystat.utilities.time.period.Day;

/**
 * Test suite for <code>FilterFunction</code>.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TestFilterFunction extends TestCase {

  private FilterFunction filterFunction = new FilterFunction();
  private String projectName = "TestFilterFunction";
  private Project project;
  private Day startDay;
  private DayInterval interval;
  
  private String test1 = "test1";
  private String test2 = "test2";
  private String test3 = "test3";
  private String avg = "Avg"; 
  private String topPercent = "TopPercent";
  private String bottomPercent = "BottomPercent";
  private String top = "Top";
  private String bottom = "Bottom";
  
  
  
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
   * Tests the filter in absolute mode.
   * @throws Exception If test fails.
   */
  public void testFilterAbsoluteMode() throws Exception {    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream1 = new TelemetryStream(test1);
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(1)));
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(2)));
    input.add(inputStream1);
    TelemetryStream inputStream2 = new TelemetryStream(test2);
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(3)));
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(4)));
    input.add(inputStream2);
    TelemetryStream inputStream3 = new TelemetryStream(test3);
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(0)));
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(5)));
    input.add(inputStream3);
    
    //Mode AllAbove
    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Min", "Above", Integer.valueOf(2)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get(test2));

    //Mode AllBlow
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Max", "Below", Integer.valueOf(3)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get(test1));

    //Mode AnyAbove
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Max", "Above", Integer.valueOf(3)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get(test2));
    assertSame(inputStream3, output.get(test3));
    
    //Mode AnyBelow
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Min", "Below", Integer.valueOf(3)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get(test1));
    assertSame(inputStream3, output.get(test3));    
  }
  
  /**
   * Tests the filter in relative mode.
   * @throws Exception If test fails.
   */
  public void testFilterRelativeMode() throws Exception {    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream1 = new TelemetryStream(test1);
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(1)));
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(1)));
    input.add(inputStream1);
    TelemetryStream inputStream2 = new TelemetryStream(test2);
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(2)));
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(2)));
    input.add(inputStream2);
    TelemetryStream inputStream3 = new TelemetryStream(test3);
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(3)));
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(3)));
    input.add(inputStream3);

    //Mode TopPercent
    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, topPercent, Integer.valueOf(30)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream3, output.get(test3));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, topPercent, Integer.valueOf(60)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get(test2));
    assertSame(inputStream3, output.get(test3));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, topPercent, Integer.valueOf(0)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, topPercent, Integer.valueOf(100)});
    assertEquals(3, output.getTelemetryStreams().size());
    
    //Mode BottomPercent
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottomPercent, Integer.valueOf(30)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get(test1));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottomPercent, Integer.valueOf(60)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get(test1));
    assertSame(inputStream2, output.get(test2));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottomPercent, Integer.valueOf(0)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottomPercent, Integer.valueOf(100)});
    assertEquals(3, output.getTelemetryStreams().size());
    
    //Mode Top
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, top, Integer.valueOf(2)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get(test2));
    assertSame(inputStream3, output.get(test3));
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, top, Integer.valueOf(-1)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, top, Integer.valueOf(0)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, top, Integer.valueOf(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, top, Integer.valueOf(2)});
    assertEquals(2, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, top, Integer.valueOf(3)});
    assertEquals(3, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, top, Integer.valueOf(4)});
    assertEquals(3, output.getTelemetryStreams().size());

    //Mode Bottom
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottom, Integer.valueOf(2)});
    assertEquals(2, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get(test1));
    assertSame(inputStream2, output.get(test2));  
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottom, Integer.valueOf(-1)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottom, Integer.valueOf(0)});
    assertEquals(0, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottom, Integer.valueOf(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottom, Integer.valueOf(2)});
    assertEquals(2, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottom, "3"});
    assertEquals(3, output.getTelemetryStreams().size());
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, bottom, "4"});
    assertEquals(3, output.getTelemetryStreams().size());
  }
  
  /**
   * Tests the rank functions.
   * @throws Exception If test fails.
   */
  public void testRankFunctions() throws Exception {    
    TelemetryStreamCollection input 
        = new TelemetryStreamCollection("test", this.project, this.interval);
    TelemetryStream inputStream1 = new TelemetryStream(test1);
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(100)));
    inputStream1.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(50)));
    input.add(inputStream1);
    TelemetryStream inputStream2 = new TelemetryStream(test2);
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(3)));
    inputStream2.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(4)));
    input.add(inputStream2);
    TelemetryStream inputStream3 = new TelemetryStream(test3);
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay, Integer.valueOf(120)));
    inputStream3.addDataPoint(new TelemetryDataPoint(this.startDay.inc(1), Integer.valueOf(-100)));
    input.add(inputStream3);
    
    FilterFunction.RankFunction rank = null;
    
    //Average: (150/2, 7/2, 20/2)
    rank = new FilterFunction.AverageRankFunction();
    assertEquals(150.0 / 2, rank.getRank(inputStream1), 0);
    assertEquals(7.0 / 2, rank.getRank(inputStream2), 0);
    assertEquals(20.0 / 2, rank.getRank(inputStream3), 0);
    TelemetryStreamCollection output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, avg, top, Integer.valueOf(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get(test1));

    //Max: (100, 4, 120)
    rank = new FilterFunction.MaxRankFunction();
    assertEquals(100.0, rank.getRank(inputStream1), 0);
    assertEquals(4.0, rank.getRank(inputStream2), 0);
    assertEquals(120.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Max", top, Integer.valueOf(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream3, output.get(test3));

    //Min (50, 3, -100)
    rank = new FilterFunction.MinRankFunction();
    assertEquals(50.0, rank.getRank(inputStream1), 0);
    assertEquals(3.0, rank.getRank(inputStream2), 0);
    assertEquals(-100.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Min", top, Integer.valueOf(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get(test1));

    //Min (50, 4, -100)
    rank = new FilterFunction.LastRankFunction();
    assertEquals(50.0, rank.getRank(inputStream1), 0);
    assertEquals(4.0, rank.getRank(inputStream2), 0);
    assertEquals(-100.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Last", top, Integer.valueOf(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream1, output.get(test1));
    
    //Delta (50, 1, 220)
    rank = new FilterFunction.DeltaRankFunction();
    assertEquals(50.0, rank.getRank(inputStream1), 0);
    assertEquals(1.0, rank.getRank(inputStream2), 0);
    assertEquals(220.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "Delta", top, Integer.valueOf(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream3, output.get(test3));
    
    //SimpleDelta (-50, 1, -220)
    rank = new FilterFunction.SimpleDeltaRankFunction();
    assertEquals(-50.0, rank.getRank(inputStream1), 0);
    assertEquals(1.0, rank.getRank(inputStream2), 0);
    assertEquals(-220.0, rank.getRank(inputStream3), 0);
    output = (TelemetryStreamCollection) 
        this.filterFunction.compute(new Object[]{input, "SimpleDelta", top, Integer.valueOf(1)});
    assertEquals(1, output.getTelemetryStreams().size());
    assertSame(inputStream2, output.get(test2));
  }
}
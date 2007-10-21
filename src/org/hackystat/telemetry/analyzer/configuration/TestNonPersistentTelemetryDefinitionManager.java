package org.hackystat.telemetry.analyzer.configuration;

import junit.framework.TestCase;

import org.hackystat.sensorbase.resource.users.jaxb.User;


/**
 * Test suite for <code>NonPersistentTelemetryDefinitionManager</code>.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TestNonPersistentTelemetryDefinitionManager extends TestCase {

  private User user; //UserManager.getInstance().getTestUser();  
  private ShareScope privateShareScope = ShareScope.getPrivateShareScope();
  
  private String chartDefName = "nonpersistent-CreatedbyUnitTest-ChartDefName";
  private String reportDefName = "nonpersistent-CreatedbyUnitTest-ReportDefName";
  private String sameNameDefName = "nonpersistent-CreatedbyUnitTest-SameNameDefName";

  /**
   * Sets up the testcase.
   * 
   */
  @Override
  protected void setUp() {
    TelemetryDefinitionManager globalManager 
        = TelemetryDefinitionManagerFactory.getGlobalPersistentInstance();
    globalManager.remove(this.user, this.chartDefName, TelemetryDefinitionType.CHART);
    globalManager.remove(this.user, this.reportDefName, TelemetryDefinitionType.REPORT);
    
    globalManager.remove(this.user, this.sameNameDefName, TelemetryDefinitionType.CHART);
    globalManager.remove(this.user, this.sameNameDefName, TelemetryDefinitionType.REPORT);
  }

  /**
   * Tears down the testcase.
   */
  @Override
  protected void tearDown() {
    this.setUp(); // just do the clean up one more time.
  }

  /**
   * Tests the non-persistent implementation.
   * 
   * @throws Exception If test fails.
   */
  public void testNonPersistentInstanceUnlinked() throws Exception {
    TelemetryDefinitionManager manager 
        = TelemetryDefinitionManagerFactory.createNonPersistentInstance(false);  
    
    // chart defintion
    TelemetryChartDefinitionInfo chartDefInfo = new TelemetryChartDefinitionInfo(
        "chart " + this.chartDefName + "() = {\"title\", (StreamRef(), yAxis())};", 
        this.user, this.privateShareScope);
    assertSame(null, manager.get(this.user, chartDefInfo.getName(), false, 
        TelemetryDefinitionType.CHART));
    int chartDefSize = 0; //may not be 0 if the manager is liked to global singleton.
    manager.add(chartDefInfo);
    assertSame(chartDefInfo, manager.get(this.user, chartDefInfo.getName(), false, 
        TelemetryDefinitionType.CHART));
    assertEquals(chartDefSize + 1, manager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());
    manager.remove(this.user, chartDefInfo.getName(), 
        TelemetryDefinitionType.CHART);
    assertEquals(chartDefSize, manager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());

    // report defintion
    TelemetryReportDefinitionInfo reportDefInfo = new TelemetryReportDefinitionInfo(
        "report " + this.reportDefName + "() = {\"title\", ChartRef()};",
        this.user, this.privateShareScope);
    assertSame(null, manager.get(this.user, reportDefInfo.getName(), false, 
        TelemetryDefinitionType.REPORT));
    int reportDefSize = 0; //may not be 0 if the manager is liked to global singleton.
    manager.add(reportDefInfo);
    assertSame(reportDefInfo, manager.get(this.user, reportDefInfo.getName(), false, 
        TelemetryDefinitionType.REPORT));
    assertEquals(reportDefSize + 1, manager.getAll(this.user, false, 
        TelemetryDefinitionType.REPORT).size());
    manager.remove(this.user, reportDefInfo.getName(), 
        TelemetryDefinitionType.REPORT);
    assertEquals(reportDefSize, manager.getAll(this.user, false, 
        TelemetryDefinitionType.REPORT).size());

    // test single global namespace constraint
    chartDefInfo = new TelemetryChartDefinitionInfo(
        "chart " + this.sameNameDefName + "() = {\"title\", (StreamRef(), yAxis())};",
        this.user, this.privateShareScope);
    manager.add(chartDefInfo);
    assertEquals(chartDefSize + 1, manager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());
    assertEquals(reportDefSize, manager.getAll(this.user, false, 
        TelemetryDefinitionType.REPORT).size());

    reportDefInfo = new TelemetryReportDefinitionInfo(
        "report " + this.sameNameDefName + "() = {\"title\", ChartRef()};",
        this.user, this.privateShareScope);
    try {
      manager.add(reportDefInfo);
      fail("Global namespace constraint violation.");
    }
    catch (TelemetryConfigurationException ex) {
      // expected
    }
    assertEquals(chartDefSize + 1, manager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());
    assertEquals(reportDefSize, manager.getAll(this.user, false, 
        TelemetryDefinitionType.REPORT).size());
  }

  /**
   * Tests the non-persistent implementation.
   * 
   * @throws Exception If test fails.
   */
  public void testNonPersistentInstanceLinked() throws Exception {
    TelemetryDefinitionManager globalManager 
        = TelemetryDefinitionManagerFactory.getGlobalPersistentInstance(); 
    TelemetryDefinitionManager localManager 
        = TelemetryDefinitionManagerFactory.createNonPersistentInstance(true);
    
    // global instance
    TelemetryChartDefinitionInfo chartDefInfo = new TelemetryChartDefinitionInfo(
        "chart " + this.chartDefName + "() = {\"title\", (StreamRef(), yAxis())};", 
        this.user, this.privateShareScope);
    assertSame(null, globalManager.get(this.user, chartDefInfo.getName(), false, 
        TelemetryDefinitionType.CHART));
    int globalStreamsDefSize = globalManager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size();
    globalManager.add(chartDefInfo);
    assertSame(chartDefInfo, globalManager.get(this.user, chartDefInfo.getName(), 
        false, TelemetryDefinitionType.CHART));
    assertEquals(globalStreamsDefSize + 1, globalManager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());

    // local instance
    assertSame(chartDefInfo, localManager.get(this.user, chartDefInfo.getName(), 
        false, TelemetryDefinitionType.CHART));
    assertEquals(globalStreamsDefSize + 1, localManager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());    
    try {
      localManager.add(chartDefInfo);
      fail("Name already used in global instance, should not be used again in local instance.");
    }
    catch (TelemetryConfigurationException ex) {
      // expected.
    }
    assertSame(chartDefInfo, localManager.get(this.user, chartDefInfo.getName(), 
        false, TelemetryDefinitionType.CHART));
    assertEquals(globalStreamsDefSize + 1, localManager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());   
  }
}

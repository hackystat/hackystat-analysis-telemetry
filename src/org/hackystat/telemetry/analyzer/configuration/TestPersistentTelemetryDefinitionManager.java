package org.hackystat.telemetry.analyzer.configuration;

import junit.framework.TestCase;

import org.hackystat.sensorbase.resource.users.jaxb.User;

/**
 * Test suite for <code>PersistentTelemetryDefinitionManager</code>.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TestPersistentTelemetryDefinitionManager extends TestCase {

  private User user = new User(); 
  private ShareScope privateShareScope = ShareScope.getPrivateShareScope();
  
  private String chartDefName = "persistent-CreatedbyUnitTest-ChartDefName";
  private String reportDefName = "persistent-CreatedbyUnitTest-ReportDefName";
  private String sameNameDefName = "persistent-CreatedbyUnitTest-SameNameDefName";

  /**
   * Sets up the testcase.
   * 
   */
  @Override
  protected void setUp() {
    this.user.setEmail("TelemetryDefinitions@hackystat.org");
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
   * Test the global persistent instance.
   * 
   * @throws Exception If test fails.
   */
  public void testGlobalInstance() throws Exception {
    TelemetryDefinitionManager manager 
        = TelemetryDefinitionManagerFactory.getGlobalPersistentInstance();

    // chart definition
    TelemetryChartDefinitionInfo chartDefInfo = new TelemetryChartDefinitionInfo(
        "chart " + this.chartDefName + "() = {\"title\", (StreamRef(), yAxis())};", 
        this.user, this.privateShareScope);
    assertSame(null, manager.get(this.user, chartDefInfo.getName(), false, 
        TelemetryDefinitionType.CHART));
    int chartDefSize = manager.getAll(this.user, false, TelemetryDefinitionType.CHART).size();
    manager.add(chartDefInfo);
    assertSame(chartDefInfo, manager.get(this.user, chartDefInfo.getName(), false, 
        TelemetryDefinitionType.CHART));
    assertEquals(chartDefSize + 1, manager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());
    manager.remove(this.user, chartDefInfo.getName(), TelemetryDefinitionType.CHART);
    assertEquals(chartDefSize, manager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());

    // report defintion
    TelemetryReportDefinitionInfo reportDefInfo = new TelemetryReportDefinitionInfo(
        "report " + this.reportDefName + "() = {\"title\", ChartRef()};",
        this.user, this.privateShareScope);
    assertSame(null, manager.get(this.user, reportDefInfo.getName(), false, 
        TelemetryDefinitionType.REPORT));
    int reportDefSize = manager.getAll(this.user, false, TelemetryDefinitionType.REPORT).size();
    manager.add(reportDefInfo);
    assertSame(reportDefInfo, manager.get(this.user, reportDefInfo.getName(), false,
        TelemetryDefinitionType.REPORT));
    assertEquals(reportDefSize + 1, manager.getAll(this.user, false, 
        TelemetryDefinitionType.REPORT).size());
    manager.remove(this.user, reportDefInfo.getName(), TelemetryDefinitionType.REPORT);
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
    catch (TelemetryConfigurationException ex) { //NOPMD
      // expected
    }
    assertEquals(chartDefSize + 1, manager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());
    assertEquals(reportDefSize, manager.getAll(this.user, false, 
        TelemetryDefinitionType.REPORT).size());
  }
}

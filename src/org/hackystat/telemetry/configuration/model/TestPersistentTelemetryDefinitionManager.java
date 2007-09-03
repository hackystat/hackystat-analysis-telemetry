package org.hackystat.telemetry.configuration;

import junit.framework.TestCase;

import org.hackystat.telemetry.util.user.User;
import org.hackystat.telemetry.util.user.UserManager;

/**
 * Test suite for <code>PersistentTelemetryDefinitionManager</code>.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TestPersistentTelemetryDefinitionManager extends TestCase {

  private User user = UserManager.getInstance().getTestUser();  
  private ShareScope privateShareScope = ShareScope.getPrivateShareScope();
  
  private String chartDefName = "persistent-CreatedbyUnitTest-ChartDefName";
  private String reportDefName = "persistent-CreatedbyUnitTest-ReportDefName";
  private String sameNameDefName = "persistent-CreatedbyUnitTest-SameNameDefName";

  /**
   * Sets up the testcase.
   * 
   */
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

    // chart defintion
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
    catch (TelemetryConfigurationException ex) {
      // expected
    }
    assertEquals(chartDefSize + 1, manager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());
    assertEquals(reportDefSize, manager.getAll(this.user, false, 
        TelemetryDefinitionType.REPORT).size());
  }
  
  /**
   * Tests persistence mechanism of telemetry definitions.
   * 
   * @throws Exception If test fails.
   */
  public void testDefinitionPersistence() throws Exception {
    TelemetryDefinitionManager manager 
        = TelemetryDefinitionManagerFactory.getGlobalPersistentInstance();

    // chart defintion
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
    
    //create a new instance of Persistent manager, it will read state from hard disk.
    //Caution: don't add or remove in the second instance.
    TelemetryDefinitionManager manager2 = new PersistentTelemetryDefinitionManager();
    assertEquals(this.chartDefName, manager2.get(this.user, chartDefInfo.getName(), false, 
        TelemetryDefinitionType.CHART).getName());
    assertEquals(chartDefSize + 1, manager2.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());   
    
    //delete the chart defintion using the first manager
    manager.remove(this.user, chartDefInfo.getName(), TelemetryDefinitionType.CHART);
    assertEquals(chartDefSize, manager.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());
    
    //re-read using the second manager
    manager2 = new PersistentTelemetryDefinitionManager();
    assertEquals(null, manager2.get(this.user, chartDefInfo.getName(), false, 
        TelemetryDefinitionType.CHART));
    assertEquals(chartDefSize, manager2.getAll(this.user, false, 
        TelemetryDefinitionType.CHART).size());   
  }
}

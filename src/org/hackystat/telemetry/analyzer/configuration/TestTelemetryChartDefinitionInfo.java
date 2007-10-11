package org.hackystat.telemetry.analyzer.configuration;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartDefinition;
import org.hackystat.telemetry.analyzer.language.parser.TelemetryLanguageParser;
import org.hackystat.sensorbase.resource.users.jaxb.User;

/**
 * Test suite for <code>TelemetryChartDefinitionInfo</code>
 * 
 * @author (Cedric) Qin Zhang
 */
public class TestTelemetryChartDefinitionInfo extends TestCase {

  /**
   * Test case 1.
   * 
   * @throws Exception If test fails.
   */
  public void testCase1() throws Exception {
    User user = null; // = UserManager.getInstance().getTestUser();
    ShareScope share = ShareScope.getPrivateShareScope();
    String defString = "chart name(t) = {\"title\", (MyStreams(t), yAxis()), " +
        "(MyStreams2(),yAxis())};";
    
    TelemetryChartDefinitionInfo defInfo 
        = new TelemetryChartDefinitionInfo(defString, user, share);
    assertEquals(user, defInfo.getOwner());
    assertEquals(share, defInfo.getShareScope());
    assertEquals("name", defInfo.getName());
    assertEquals(defString, defInfo.getDefinitionString());

    TelemetryChartDefinition def = defInfo.getChartDefinitionObject();
    assertEquals("name", def.getName());
    assertEquals("title", def.getTitle());
  }
  

  /**
   * Test case 2.
   * 
   * @throws Exception If test fails.
   */
  public void testCase2() throws Exception {
    User user = null; //UserManager.getInstance().getTestUser();
    ShareScope share = ShareScope.getPrivateShareScope();
    String defString = "chart name(t) = {\"title\", (MyStreams(t), yAxis()), " +
        "(MyStreams2(),yAxis())};";
    TelemetryChartDefinition def = TelemetryLanguageParser.parseChartDef(defString);
    
    TelemetryChartDefinitionInfo defInfo 
        = new TelemetryChartDefinitionInfo(def, user, share);
    assertEquals(user, defInfo.getOwner());
    assertEquals(share, defInfo.getShareScope());
    assertEquals("name", defInfo.getName());
    assertEquals(defString, defInfo.getDefinitionString());

    assertSame(def, defInfo.getChartDefinitionObject());
    assertEquals("name", def.getName());
    assertEquals("title", def.getTitle());
  }
}

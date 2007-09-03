package org.hackystat.telemetry.configuration;

import junit.framework.TestCase;

import org.hackystat.telemetry.util.user.User;
import org.hackystat.telemetry.util.user.UserManager;
import org.hackystat.telemetry.language.ast.TelemetryReportDefinition;
import org.hackystat.telemetry.language.parser.TelemetryLanguageParser;

/**
 * Test suite for <code>TelemetryReportDefInfo</code>
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TestTelemetryReportDefinitionInfo extends TestCase {

  /**
   * Test case 1.
   * 
   * @throws Exception If test fails.
   */
  public void testCase1() throws Exception {
    User user = UserManager.getInstance().getTestUser();
    ShareScope share = ShareScope.getPrivateShareScope();
    String defString = "report name(t) = {\"title\", MyChart(t), MyChart2()};";

    TelemetryReportDefinitionInfo defInfo 
        = new TelemetryReportDefinitionInfo(defString, user, share);
    assertEquals(user, defInfo.getOwner());
    assertEquals(share, defInfo.getShareScope());
    assertEquals("name", defInfo.getName());
    assertEquals(defString, defInfo.getDefinitionString());

    TelemetryReportDefinition def = defInfo.getReportDefinitionObject();
    assertEquals("name", def.getName());
    assertEquals("title", def.getTitle());
  }

  /**
   * Test case 2.
   * 
   * @throws Exception If test fails.
   */
  public void testCase2() throws Exception {
    User user = UserManager.getInstance().getTestUser();
    ShareScope share = ShareScope.getPrivateShareScope();
    String defString = "report name(t) = {\"title\", MyChart(t), MyChart2()};";
    TelemetryReportDefinition def = TelemetryLanguageParser.parseReportDef(defString);
    
    TelemetryReportDefinitionInfo defInfo 
        = new TelemetryReportDefinitionInfo(def, user, share);    
    assertEquals(user, defInfo.getOwner());
    assertEquals(share, defInfo.getShareScope());
    assertEquals("name", defInfo.getName());
    assertEquals(defString, defInfo.getDefinitionString());


    
    assertSame(def, defInfo.getReportDefinitionObject());
    assertEquals("name", def.getName());
    assertEquals("title", def.getTitle());
  }
}
package org.hackystat.telemetry.analyzer.configuration;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.language.ast.TelemetryReportDefinition;
import org.hackystat.telemetry.analyzer.language.parser.TelemetryLanguageParser;
import org.hackystat.sensorbase.resource.users.jaxb.User;

/**
 * Test suite for <code>TelemetryReportDefInfo</code>.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TestTelemetryReportDefinitionInfo extends TestCase {
  
  private String name = "name";
  

  /**
   * Test case 1.
   * 
   * @throws Exception If test fails.
   */
  public void testCase1() throws Exception {
    User user = new User();
    user.setEmail("TelemetryDefinitions@hackystat.org");
    ShareScope share = ShareScope.getPrivateShareScope();
    String defString = "report name(t) = {\"title\", MyChart(t), MyChart2()};";

    TelemetryReportDefinitionInfo defInfo 
        = new TelemetryReportDefinitionInfo(defString, user, share);
    assertEquals(user, defInfo.getOwner());
    assertEquals(share, defInfo.getShareScope());
    assertEquals(name, defInfo.getName());
    assertEquals(defString, defInfo.getDefinitionString());

    TelemetryReportDefinition def = defInfo.getReportDefinitionObject();
    assertEquals(name, def.getName());
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
    String defString = "report name(t) = {\"title\", MyChart(t), MyChart2()};";
    TelemetryReportDefinition def = TelemetryLanguageParser.parseReportDef(defString);
    
    TelemetryReportDefinitionInfo defInfo 
        = new TelemetryReportDefinitionInfo(def, user, share);    
    assertEquals(user, defInfo.getOwner());
    assertEquals(share, defInfo.getShareScope());
    assertEquals(name, defInfo.getName());
    assertEquals(defString, defInfo.getDefinitionString());


    
    assertSame(def, defInfo.getReportDefinitionObject());
    assertEquals(name, def.getName());
    assertEquals("title", def.getTitle());
  }
}
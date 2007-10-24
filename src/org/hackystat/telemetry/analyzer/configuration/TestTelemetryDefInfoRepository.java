package org.hackystat.telemetry.analyzer.configuration;

import junit.framework.TestCase;

import org.hackystat.sensorbase.resource.users.jaxb.User;

/**
 * Test suite for <code>TelemetryDefInfoRepository</codee>.
 * 
 * TODO: find and findAll method with "true" not tested!!!!
 * 
 * @author (Cedric) Qin Zhang
 */
public class TestTelemetryDefInfoRepository extends TestCase {

  private User user = new User();
  private ShareScope privateShareScope = ShareScope.getPrivateShareScope();

  /**
   * Tests all.
   * 
   * @throws Exception If test fails.
   */
  public void testAll() throws Exception {
    this.user.setEmail("TelemetryDefinitions@hackystat.org");
    TelemetryChartDefinitionInfo a1 = new TelemetryChartDefinitionInfo(
        "chart A() = {\"title\", (StreamRef1(), yAxis())};", this.user, this.privateShareScope);
    TelemetryChartDefinitionInfo a2 = new TelemetryChartDefinitionInfo(
        "chart A() = {\"title\", (StreamRef2(), yAxis())};", this.user, this.privateShareScope);
    TelemetryChartDefinitionInfo b = new TelemetryChartDefinitionInfo(
        "chart B() = {\"title\", (StreamRef3(), yAxis())};", this.user, this.privateShareScope);

    TelemetryDefinitionInfoRepository repository = new TelemetryDefinitionInfoRepository();
    assertEquals(0, repository.findAll(this.user, false).size());
    assertNull(repository.find(this.user, "Ghost", false));
    assertEquals(false, repository.exists("A"));
    assertEquals(false, repository.exists("B"));

    repository.add(a1);
    assertSame(a1, repository.find(this.user, "A", false));
    assertEquals(1, repository.findAll(this.user, false).size());
    assertEquals(true, repository.exists("A"));
    assertEquals(false, repository.exists("B"));

    try {
      repository.add(a2);
      fail("Should not allow adding defintion with the same name under same user.");
    }
    catch (TelemetryConfigurationException ex) { //NOPMD
      // expected
    }
    assertSame(a1, repository.find(this.user, "A", false));
    assertEquals(1, repository.findAll(this.user, false).size());

    repository.add(b);
    assertSame(b, repository.find(this.user, "B", false));
    assertEquals(2, repository.findAll(this.user, false).size());
    assertEquals(true, repository.exists("A"));
    assertEquals(true, repository.exists("B"));

    // do update by remove first and then add
    repository.remove(this.user, "A");
    repository.add(a2);
    assertSame(a2, repository.find(this.user, "A", false));
    assertEquals(2, repository.findAll(this.user, false).size());
    assertEquals(true, repository.exists("A"));
    assertEquals(true, repository.exists("B"));

    try {
      repository.add(a1);
      fail("Should not allow adding defintion with the same name under same user.");
    }
    catch (TelemetryConfigurationException ex) { //NOPMD
      // expected
    }
    assertSame(a2, repository.find(this.user, "A", false));
    assertEquals(2, repository.findAll(this.user, false).size());

    repository.remove(this.user, "B");
    assertEquals(1, repository.findAll(this.user, false).size());
    assertNull(repository.find(this.user, "B", false));
    assertEquals(true, repository.exists("A"));
    assertEquals(false, repository.exists("B"));
  }
}

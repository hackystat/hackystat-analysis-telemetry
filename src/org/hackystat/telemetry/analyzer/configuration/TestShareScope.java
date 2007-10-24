package org.hackystat.telemetry.analyzer.configuration;

import junit.framework.TestCase;

import org.hackystat.sensorbase.resource.projects.jaxb.Project;


/**
 * Test suite for <code>ShareScope</code>.
 * 
 * @author (Cedric) Qin Zhang, Philip Johnson
 */
public class TestShareScope extends TestCase {

  private Project project;

  /**
   * Test case set up.
   * 
   * @throws Exception If test case cannot be set up.
   */
  @Override
  protected void setUp() throws Exception {
    this.project = new Project();
  }

  /**
   * Tests everything.
   * 
   * @throws Exception If test fails.
   */
  public void testAll() throws Exception {
    ShareScope scope = ShareScope.getGlobalShareScope();
    assertEquals(true, scope.isGlobal());
    assertEquals(false, scope.isProject());
    assertEquals(false, scope.isPrivate());
    try {
      scope.getProject();
      fail("Should raise exception.");
    }
    catch (TelemetryConfigurationException ex) { //NOPMD
      // expected
    }

    scope = ShareScope.getPrivateShareScope();
    assertEquals(false, scope.isGlobal());
    assertEquals(false, scope.isProject());
    assertEquals(true, scope.isPrivate());
    try {
      scope.getProject();
      fail("Should raise exception.");
    }
    catch (TelemetryConfigurationException ex) { //NOPMD
      // expected
    }

    scope = ShareScope.getProjectShareScope(this.project);
    assertEquals(false, scope.isGlobal());
    assertEquals(true, scope.isProject());
    assertEquals(false, scope.isPrivate());
    assertSame(this.project, scope.getProject());

    try {
      scope = ShareScope.getProjectShareScope(null);
      fail("Should raise exception.");
    }
    catch (Exception ex) { //NOPMD
      // expected
    }
  }

  /**
   * Tests equals and hashCode methods.
   * 
   * @throws Exception If test fails.
   */
  public void testEquals() throws Exception {
    ShareScope scope1a = ShareScope.getGlobalShareScope();
    ShareScope scope1b = ShareScope.getGlobalShareScope();
    ShareScope scope2a = ShareScope.getPrivateShareScope();
    ShareScope scope2b = ShareScope.getPrivateShareScope();
    ShareScope scope3a = ShareScope.getProjectShareScope(this.project);
    ShareScope scope3b = ShareScope.getProjectShareScope(this.project);

    assertEquals(scope1a, scope1b);
    assertEquals(scope1a.hashCode(), scope1b.hashCode());
    assertEquals(scope2a, scope2b);
    assertEquals(scope2a.hashCode(), scope2b.hashCode());
    assertEquals(scope3a, scope3b);
    assertEquals(scope3a.hashCode(), scope3b.hashCode());

    assertFalse(scope1a.equals(scope2a));
    assertFalse(scope1a.equals(scope3a));
    assertFalse(scope2a.equals(scope3a));
  }
}

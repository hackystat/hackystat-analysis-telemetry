package org.hackystat.telemetry.analyzer.configuration;

import java.util.TreeSet;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.util.Day;
import org.hackystat.telemetry.analyzer.util.project.Project;
import org.hackystat.telemetry.analyzer.util.project.ProjectManager;
import org.hackystat.telemetry.analyzer.util.user.User;
import org.hackystat.telemetry.analyzer.util.user.UserManager;


/**
 * Test suite for <code>ShareScope</code>.
 * <p>
 * V8 Notes:  This test class must be reimplemented since it requires the creation of a
 * Project instance. 
 * 
 * @author (Cedric) Qin Zhang
 */
public class TestShareScope extends TestCase {

  private String testProjectName = "Test-Project-Telemetry-ShareScope-vfdf3d";
  //private User user = UserManager.getInstance().getTestUser();
  //private Project project;

  /**
   * Test case set up.
   * 
   * @throws Exception If test case cannot be set up.
   */
  protected void setUp() throws Exception {
//    ProjectManager projectManager = ProjectManager.getInstance();
//
//    //remove the test project, just in case previous test fails and did not remove the project.
//    try {
//      projectManager.deleteProject(this.testProjectName);
//    }
//    catch (Exception ex) {
//      // don't care.
//    }
//
//    // register a project to do test
//    Day startDay = Day.getInstance("01-Feb-2003");
//    Workspace workspaceJunk = WorkspaceCache.getInstance().getWorkspace(this.user, "C:\\junk\\");
//    TreeSet<Workspace> workspaceSet = new TreeSet<Workspace>();
//    workspaceSet.add(workspaceJunk);
//    this.project = projectManager.createTestProjectClientSide(this.testProjectName, user, startDay,
//        startDay.inc(3), workspaceSet);
  }

  /**
   * Test case tear down.
   * 
   * @throws Exception If tear down failed.
   */
  protected void tearDown() throws Exception {
    ProjectManager.getInstance().deleteProject(this.testProjectName);
  }

  /**
   * Tests everything.
   * 
   * @throws Exception If test fails.
   */
  public void testAll() throws Exception {
//    ShareScope scope = ShareScope.getGlobalShareScope();
//    assertEquals(true, scope.isGlobal());
//    assertEquals(false, scope.isProject());
//    assertEquals(false, scope.isPrivate());
//    assertEquals(scope, ShareScope.deserializeFromString(scope.serializeToString()));
//    try {
//      scope.getProject();
//      fail("Should raise exception.");
//    }
//    catch (TelemetryConfigurationException ex) {
//      // expected
//    }
//
//    scope = ShareScope.getPrivateShareScope();
//    assertEquals(false, scope.isGlobal());
//    assertEquals(false, scope.isProject());
//    assertEquals(true, scope.isPrivate());
//    assertEquals(scope, ShareScope.deserializeFromString(scope.serializeToString()));
//    try {
//      scope.getProject();
//      fail("Should raise exception.");
//    }
//    catch (TelemetryConfigurationException ex) {
//      // expected
//    }
//
//    scope = ShareScope.getProjectShareScope(this.project);
//    assertEquals(false, scope.isGlobal());
//    assertEquals(true, scope.isProject());
//    assertEquals(false, scope.isPrivate());
//    assertSame(this.project, scope.getProject());
//    assertEquals(scope, ShareScope.deserializeFromString(scope.serializeToString()));
//
//    try {
//      scope = ShareScope.getProjectShareScope(null);
//      fail("Should raise exception.");
//    }
//    catch (Exception ex) {
//      // expected
//    }
  }

  /**
   * Tests equals and hashCode methods.
   * 
   * @throws Exception If test fails.
   */
  public void testEquals() throws Exception {
//    ShareScope scope1a = ShareScope.getGlobalShareScope();
//    ShareScope scope1b = ShareScope.getGlobalShareScope();
//    ShareScope scope2a = ShareScope.getPrivateShareScope();
//    ShareScope scope2b = ShareScope.getPrivateShareScope();
//    ShareScope scope3a = ShareScope.getProjectShareScope(this.project);
//    ShareScope scope3b = ShareScope.getProjectShareScope(this.project);
//
//    assertEquals(scope1a, scope1b);
//    assertEquals(scope1a.hashCode(), scope1b.hashCode());
//    assertEquals(scope2a, scope2b);
//    assertEquals(scope2a.hashCode(), scope2b.hashCode());
//    assertEquals(scope3a, scope3b);
//    assertEquals(scope3a.hashCode(), scope3b.hashCode());
//
//    assertFalse(scope1a.equals(scope2a));
//    assertFalse(scope1a.equals(scope3a));
//    assertFalse(scope2a.equals(scope3a));
  }
}

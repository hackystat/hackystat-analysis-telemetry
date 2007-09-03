package org.hackystat.telemetry.util.project;

import java.util.HashSet;
import java.util.Set;

import org.hackystat.telemetry.util.user.User;
import org.hackystat.telemetry.util.user.UserManager;
import org.hackystat.telemetry.util.Day;
import org.hackystat.telemetry.util.selector.interval.DayInterval;

import junit.framework.TestCase;

/**
 * Tests simple instantiation and methods on the Project class. 
 * The semantic validity constraints are tested in the ProjectManager class.
 * This is a client-side unit test.
 *
 * @author Philip Johnson
 * @version $Id: TestProject.java,v 1.1.1.1 2005/10/20 23:56:36 johnson Exp $
 */
public class TestProject extends TestCase {

  /**
   * Tests that Projects can be created and manipulated.
   * @throws Exception If problems occur instantiating or accessing the test Project instance.
   */
  public void testProject() throws Exception {
//    // Set up the attributes of the test project.
//    User testUser = UserManager.getInstance().getTestUser();
//    Workspace testWorkspace =  WorkspaceCache.getInstance().getWorkspace(testUser, "C:\\junk\\");
//    Set<Workspace> workspaceSet = new HashSet<Workspace>();
//    workspaceSet.add(testWorkspace);
//    Day startDay = Day.getInstance("01-Jan-2004");
//    Day endDay = Day.getInstance("10-Jan-2004");
//    String name = "Test Project";
//    String description = "Test Description";
//    Set<User> members = new HashSet<User>();
//    // Now create the project
//    Project testProject = 
//      new Project(name, testUser, description, startDay, endDay, workspaceSet, members, members);
//    // Fiddle around with the project a bit.
//    testProject.addMember(testUser);
//    testProject.addWorkspace(WorkspaceCache.getInstance().getWorkspace(testUser, "/usr/local/"));
//    testProject.confirmMembership(testUser);
//    // Do some checking on it.
//    assertEquals("Checking name", name, testProject.getName());
//    assertEquals("Checking description", description, testProject.getDescription());
//    assertEquals("Checking owner", testUser, testProject.getOwner());
//    assertEquals("Checking startDay", startDay, testProject.getStartDay());
//    assertEquals("Checking endDay", endDay, testProject.getEndDay());
//    assertTrue("Checking workspaceSet", testProject.isProjectWorkspace(testWorkspace));
//    assertTrue("Checking members", testProject.getMembers().contains(testUser));
//    assertTrue("Checking isConfirmedMember", testProject.isConfirmedMember(testUser));
//    assertTrue("Checking confirmed members", testProject.getConfirmedMembers().contains(testUser));
//    assertTrue("Checking inProjectInterval 1", testProject.inProjectInterval(startDay));
//    assertTrue("Checking inProjectInterval 2", testProject.inProjectInterval(endDay));
//    assertTrue("Checking inProjectInterval 3", testProject.inProjectInterval(startDay.inc(1)));
//    assertTrue("Checking inProjectInterval 4", !testProject.inProjectInterval(endDay.inc(1)));
//    assertTrue("Checking inProjectInterval 5", !testProject.inProjectInterval(startDay.inc(-1)));
//    // test Intervals.
//    DayInterval interval1 = new DayInterval("2004", "00", "01", "2004", "00", "10");
//    assertTrue("Checking inProjectInterval 6", testProject.inProjectInterval(interval1));
//    DayInterval interval2 = new DayInterval("2004", "01", "01", "2006", "01", "10");
//    assertTrue("Checking inProjectInterval 7", !testProject.inProjectInterval(interval2));

  }
}

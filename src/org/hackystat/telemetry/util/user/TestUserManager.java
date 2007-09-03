package org.hackystat.telemetry.util.user;

import java.util.Iterator;

import org.hackystat.telemetry.util.ServerProperties;

import junit.framework.TestCase;

/**
 * Tests the UserManager implementation, ensuring it can correctly initialize
 * its state from disk and that new Users can be created and manipulated correctly.
 * <p>
 * Note that this Test class creates and instantiates User
 * data on the client side only.  There is no interaction with 
 * the server; in fact, it does not even need to be running for
 * this test case to execute.  
 *
 * @author    Philip Johnson
 * @version   $Id: TestUserManager.java,v 1.1.1.1 2005/10/20 23:56:44 johnson Exp $
 */
public class TestUserManager extends TestCase {
  /**
   * Test the UserManager API, ensuring it can put and get User instances 
   * correctly.
   */
  public void testUserManager() {
    UserManager manager = UserManager.getInstance();
    // Create a test user email.
    String testUserEmail = "testuserxml" + ServerProperties.getInstance().getTestDomain();
    // Now create (or retrieve) the corresponding User instance.
    User testUser = UserManager.getInstance().getUser(testUserEmail);
    String userKey = testUser.getUserKey();
    // Now retrieve it.
    assertEquals("Checking getUser", testUser, manager.getUser(userKey));
    // Check the isUserKey
    assertTrue("Checking valid user key", manager.isUserKey(userKey));
    assertFalse("Checking invalid user key", manager.isUserKey("@@"));
    assertNotNull("Checking email list", manager.userEmailList());
    assertNotNull("Checking userKey list", manager.userKeyList());
    // Test the iterator
    for (Iterator i = manager.iterator(); i.hasNext();) {
      User user = (User) i.next();
      assertTrue("Checking iterator list", manager.isUserKey(user.getUserKey()));
    }
    assertNotNull("Checking testUser", manager.getTestUser());
  }
}

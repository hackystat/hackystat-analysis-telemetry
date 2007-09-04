package org.hackystat.telemetry.analyzer.util.user;

import java.util.Arrays;
import java.util.List;

import org.hackystat.telemetry.analyzer.util.ServerProperties;
import org.jdom.Document;
import org.jdom.Element;

import junit.framework.TestCase;

/**
 * Tests the User implementation, checking that its methods return 
 * correct values and that stored properties are persisted and 
 * retrieved correctly.
 * This is a client-side test. 
 *
 * @author    Philip Johnson
 * @version   $Id: TestUser.java,v 1.1.1.1 2005/10/20 23:56:44 johnson Exp $
 */
public class TestUser extends TestCase {
  /**
   * Tests that User properties can be stored and retrieved in the 
   * various ways allowed by the API, and report how many reads and writes 
   * were performed at the end of the test. 
   */
  public void testUser() {
    String testUserEmail = "testuserxml" + ServerProperties.getInstance().getTestDomain();
    // Now create the User instance.
    User testUser = UserManager.getInstance().getUser(testUserEmail);
    
    // Test identity methods on this instance.
    assertEquals("Checking userEmail", testUserEmail, testUser.getUserEmail());
    assertFalse("Checking admin status", testUser.isAdmin());
    assertTrue("Checking test user status", testUser.isTestUser());
    
    // Test string put and get.
    testUser.put("foo", "bar");
    assertEquals("Checking put", "bar", testUser.get("foo"));
    
    // Test list put and get
    String [] stringArray = {"foo", "bar", "baz" };
    List stringList = Arrays.asList(stringArray);
    testUser.putList("fooList", stringList);
    assertEquals("Checking fooList", stringList, testUser.getList("fooList"));
    
    // Test nondefault partition
    testUser.put("foo", "bar", "PartitionBaz");
    assertEquals("Checking put", "bar", testUser.get("foo", "PartitionBaz"));
    testUser.putList("fooList", stringList, "PartitionBaz");
    assertEquals("Checking par. fooList", stringList, testUser.getList("fooList", "PartitionBaz"));
    
    // Test to see if we can read the persistent data from disk.
    testUser.clear();
    assertEquals("Checking fooList from disk", stringList, testUser.getList("fooList"));
    assertEquals("Checking par. fooList from disk", stringList, 
      testUser.getList("fooList", "PartitionBaz"));
    // Test to see if remove works.
    String removeKey = "KeyToRemove";
    testUser.put(removeKey, "value");
    testUser.remove(removeKey);
    assertNull("Checking removed property", testUser.get(removeKey));
    
    // Report what happened.
    ServerProperties.getInstance().getLogger().fine(
        "In TestUser XML Reads: " 
        + UserManager.getNumReads() 
        + " Writes: " 
        + UserManager.getNumWrites());
    ServerProperties.getInstance().getLogger().fine("In Test User: " +
        UserManager.getInstance().toString());
  }

  /**
   * Tests storage and retrieval of JDOM-based documents.
   */
  public void testJDOM() {
    // Get the testUserXML instance. 
    String testUserEmail = "testuserxml" + ServerProperties.getInstance().getTestDomain();
    User testUser = UserManager.getInstance().getUser(testUserEmail);
    // Create a JDOM-based Document for storage.
    Element rootElement = new Element("TestElement");
    rootElement.addContent("TestContent");
    Document document = new Document(rootElement);
    // Store it.
    testUser.putJDomDocument(document, "testpartition");
    // Now retrieve it.
    Document document2 = testUser.getJDomDocument("testpartition");
    // Now see if what we got is what we stored.
    // Since elements are compared using '==', we must content ourselves with toString() equality.
    assertEquals("Checking JDOM document storage and retrieval", document.toString(), 
                 document2.toString());
  }
  
}

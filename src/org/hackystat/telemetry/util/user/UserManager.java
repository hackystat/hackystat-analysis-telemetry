package org.hackystat.telemetry.util.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.io.File;

import org.hackystat.telemetry.util.ServerProperties;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Provides a manager supporting thread-safe access to the set of User instances.
 * To provide thread-safety and efficiency, the iterator may not reflect
 * the latest new User instances if they were added after this iterator
 * was returned. 
 * 
 * @author    Philip M. Johnson
 * @version   $Id: UserManager.java,v 1.1.1.1 2005/10/20 23:56:44 johnson Exp $
 */
public class UserManager implements Iterable<User> {
  
  /** The singleton instance of the UserManager. */  
  private static UserManager theInstance = new UserManager();
  
  /** The set of User instances, used to support iterating over User instances.*/
  private Set<User> userSet = new CopyOnWriteArraySet<User>();
  
  /** Mappings from userKeys and userEmails to user instances. */
  private ConcurrentHashMap<String, User> userKeyMap = new ConcurrentHashMap<String, User>(); 
  private ConcurrentHashMap<String, User> userEmailMap = new ConcurrentHashMap<String, User>(); 
  
  /** 
   * Get the singleton UserManager instance. 
   * @return The singleton UserManager instance. 
   */
  public static UserManager getInstance() {
    return UserManager.theInstance;
  }

  /**
   * Returns the User associated with the passed userKey or email address. No userKey can 
   * contain the "@" character, so we can distinguish between keys and emails this way. 
   * If the string is an emailAddress, then either the pre-existing User instance 
   * associated with this emailAddress is returned or else a new User instance for
   * this emailAddress is instantiated and returned.  If the string is not an 
   * emailAddress, then either the pre-existing User instance is returned or an
   * IllegalArgumentException is thrown. 
   * @param userKeyOrEmailAddress The string user key or the email address.
   * Throws IllegalArgumentException if it's null or if it's a userKey that is not valid.
   * @return The User instance for this key or email address, creating it if necessary.
   */
  public User getUser(String userKeyOrEmailAddress) {
    if (userKeyOrEmailAddress == null) {
      throw new IllegalArgumentException("null passed as userKey");
    }
    // First, deal with the case of userKeys
    if (userKeyOrEmailAddress.indexOf("@") == -1) {
      // Throw exception or return the User.
      if (!this.userKeyMap.containsKey(userKeyOrEmailAddress)) {
        throw new IllegalArgumentException("Unknown user key: " + userKeyOrEmailAddress);
      }
      return this.userKeyMap.get(userKeyOrEmailAddress);
    }
    // Second, deal with the case of email addresses.
    else { 
      // Return the pre-existing User or create a new one and return it.
      return this.makeUser(userKeyOrEmailAddress);      
    }
  }

  /**
   * Returns the predefined User instance (testdataset) for use in testing. 
   * @return The User instance predefined for testing. 
   */
  public User getTestUser() {
    String testUserKey = ServerProperties.getInstance().getTestDataSetKey();
    String testDomain = ServerProperties.getInstance().getTestDomain();
    return this.makeUser(testUserKey + testDomain);
  }
  
  /**
   * Returns the admin User instance, creating it if necessary.
   * @return The admin User instance.
   */
  public User getAdminUser() {
    String adminEmail = ServerProperties.getInstance().getAdminEmail();
    return this.makeUser(adminEmail); 
  }

  /**
   * Returns the admin User instance, creating it from adminEmail and adminUserKey
   * if necessary, and checking to make sure that the passed email-userKey pair is 
   * consistent with any pre-existing admin email-userKey pair.
   * If an admin User already has been instantiated from disk, but its userKey does
   * not equal the passed adminUserKey, then a logging message is generated and an
   * IllegalArgumentException is thrown. 
   * This method is called from ServerStartup to ensure that the declared admin
   * userEmail and userKey in the hackystat.properties file is consistent with the 
   * newly created or restored from disk version of the admin user instance. 
   * If adminUserKey is null, then no info about the userKey is present in hackystat.properties,
   * and we are free to create any userKey we want for this admin.
   * @param adminEmail The admin email, generally taken from hackystat.properties.
   * @param adminUserKey The admin userKey, generally taken from hackystat.properties.
   * @return The admin User instance.
   */
  public User getAdminUser(String adminEmail, String adminUserKey) {
    //(1) If adminUserKey is null, then just get a new or pre-existing User with adminEmail.
    if (adminUserKey == null) {
      return this.getUser(adminEmail); 
    }
    
    // (2) If we have a pre-existing adminUser, then if userKey matches, we're cool, else error.
    if (this.userEmailMap.containsKey(adminEmail)) {
      User existingAdmin = (User) this.userEmailMap.get(adminEmail);
      String existingAdminUserKey = existingAdmin.getUserKey();
      if (adminUserKey.equals(existingAdminUserKey)) {
        return existingAdmin;
      }
      else {
        String message = "ERROR:  Invalid hackystat.admin.userkey (" + adminUserKey + 
                         ") in hackystat.properties.";
        ServerProperties.getInstance().getLogger().info(message);
        throw new IllegalArgumentException(message);
      }
    }   

    // (3) We've got an email and a userKey, but nothing pre-existing. So, define it.
    User newAdminUser = new User(adminUserKey, adminEmail);
    this.userSet.add(newAdminUser);
    this.userKeyMap.put(adminUserKey, newAdminUser);
    this.userEmailMap.put(newAdminUser.getUserEmail(), newAdminUser);
    return newAdminUser;
  }

  /**
   * Returns the user key associated with this user email, or null if not found.
   * UserEmail is lowercased before comparison.
   * 
   * @param userEmail The string user email
   * @return The corresponding userKey or null if not found.
   */
  public String getUserKey(String userEmail) {
    if (userEmail == null) {
      return null;
    }
    User user = this.userEmailMap.get(userEmail);
    return (user == null) ? null : user.getUserKey();  
  }  

  /**
   * Returns true if the passed userKey has already been defined as a userKey.
   * @param userKey The potential userKey.
   * @return True if it is an existing user key, false otherwise.
   */
  public boolean isUserKey(String userKey) {
    return (userKey == null) ? false : (this.userKeyMap.containsKey(userKey));
  }

  /**
   * Returns true if the passed userEmail is associated with a user.
   * @param userEmail The potential userEmail.
   * @return True if it is associated with a User, false otherwise.
   */
  public boolean isUserEmail(String userEmail) {
    return (userEmail == null) ? false : (this.userEmailMap.get(userEmail) != null);
  }


  /**
   * Creates a new user or returns the existing user whose email is userEmail.
   * If the userEmail is from the test domain, then the key will be the account name.
   * @param userEmail The email address for the user. Throws unchecked IllegalArgumentException 
   * if userEmail is null.
   * @return The User instance for this userEmail.
   */
  private User makeUser(String userEmail) {
    if (userEmail == null) {
      throw new IllegalArgumentException("userEmail is null");
    }
    User user = this.userEmailMap.get (userEmail);
    if (user == null) {
      // Generate a new user key, using the account name if a test user.
      String userKey;
      if (userEmail.endsWith(ServerProperties.getInstance().getTestDomain())) {
        userKey = userEmail.substring(0, userEmail.indexOf('@')).toLowerCase();
      }
      // Otherwise, use a normal userKey string.
      else {
        userKey = UserKeyGenerator.make();
      }
      user = new User(userKey, userEmail);
      this.userSet.add(user);
      this.userKeyMap.put(userKey, user);
      this.userEmailMap.put(user.getUserEmail(), user);
    }
    return user;
  }

  /** 
   * Traverses the hackystat data directory to initialize the set of User instances.
   */
  private UserManager () {
    Logger logger = ServerProperties.getInstance().getLogger();
    try {
      File usersDir = ServerProperties.getInstance().getUsersDir();
      File[] userDirs = usersDir.listFiles();
      for (int i = 0; i < userDirs.length; i++) {
        if (userDirs[i].isDirectory()) {
          File userFileXml = new File(userDirs[i], "user." + User.defaultPartition + ".xml");
          File userFileProp = new File(userDirs[i], "user.txt");
          if (userFileXml.exists() || userFileProp.exists()) {
            String userKey = userDirs[i].getName();
            User user = new User(userKey);
            String userEmail = user.getUserEmail();
            // Make sure we both have a user and userEmail before adding.
            if (userEmail == null) {
              logger.warning("Server Startup Error: No email found for user " + user);
            }
            // Make sure we haven't already seen a user with this email. 
            else if (userEmailMap.containsKey(userEmail)) {
              logger.warning("Server Startup Error: Duplicate user key '" + userKey + 
                  "' found for user email '" + userEmail + 
                  "'. Shutdown server, fix user directory, and restart!");
            } 
            // Everything looks good, so add this user to our user map.
            else {
              userSet.add(user);
              userKeyMap.put(userKey, user);
              userEmailMap.put(user.getUserEmail(), user);
            }
          }
          else {
            ServerProperties.getInstance().getLogger().warning("No user file in " + userDirs[i]);
          }
        }
      }
    }
    catch (Exception e) {
      ServerProperties.getInstance().getLogger().warning("Error in UserManager instantiation: " 
      + e);
      e.printStackTrace();
    }
  }
  
  /**
   * Returns a thread-safe iterator over the set of User instances.
   * Includes both test and real users. Use User.isTestUser() to check.
   * May not include very recently added Users. 
   * @return An iterator over the set of Users.
   */
  public Iterator<User> iterator () {
    return this.userSet.iterator();
  }
  
  /**
   * Returns the set of Users.
   * @return The users. 
   */
  public Set<User> getUsers() {
    return this.userSet;
  }
  
  /**
   * Returns the number of currently defined users.
   * @return The number of users.
   */
  public int getNumUsers() {
    return this.userSet.size();
  }
  
  /** 
   * Returns a string containing a list of the current User instance strings. 
   * @return The string representation of this User. 
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("[UserManager");
    for (User user : this.getUsers()) {
      buffer.append(" ");
      buffer.append(user);
    }
    buffer.append("]");
    return buffer.toString();
  }
  
  /**
   * Returns a newly generated list of the current (non-test) user email strings.
   * @return   A list of user email strings.
   */
  public List<String> userEmailList() {
    ArrayList<String> emailList = new ArrayList<String>();
    for (User user : this.getUsers()) {
      String userEmail = user.getUserEmail();
      if ((!userEmail.endsWith(ServerProperties.getInstance().getTestDomain()))) {
        emailList.add(userEmail);
      }
    }
    return emailList;
  }
  
  /**
   * Returns a newly generated list of the current (non-test) user key strings.
   *
   * @return   A list of user key strings.
   */
  public List userKeyList() {
    ArrayList<String> userKeyList = new ArrayList<String>();
    for (User user : this.getUsers()) {
      String userKey = user.getUserKey();
      String userEmail = user.getUserEmail();
      if (!userEmail.endsWith(ServerProperties.getInstance().getTestDomain())) {
        userKeyList.add(userKey);
      }
    }
    return userKeyList;
  }
  
  /**
   * Returns the total number of times any User XML file has been written since server startup.
   * Provided as a simple way to detect performance problems due to excessive XML file writing. 
   * @return The number of XML file writes. 
   */
  public static long getNumWrites() {
    return User.numWrites;
  }

  /**
   * Returns the total number of times any User XML file has been read since server startup.
   * Provided as a simple way to detect performance problems due to excessive XML file reading.
   * @return The number of XML file reads. 
   */
  public static long getNumReads() {
    return User.numReads;
  }
}

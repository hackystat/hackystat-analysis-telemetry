package org.hackystat.telemetry.analyzer.util.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.hackystat.telemetry.analyzer.util.ServerProperties;
//import org.hackystat.core.kernel.mvc.Page;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides a thread-safe repository of key-value pairs for a given user. Key-value pairs can be
 * persistent or non-persistent (volatile). Persistent key-value pairs are either [String, String]
 * or [String, List(String)]. Volatile key-value pairs are [Object, Object]. Persistent values are
 * written out immediately.  The client can control the overhead of writing by specifying an 
 * optional partition string, which enables key-value pairs to be divided among multiple XML files.
 * <p>
 * The original User class saved values into a property file called user.txt.  To support the 
 * transition, the constructor checks for a user.txt file and if it is present, reads the data 
 * in, saves it to the new XML format, and deletes the file. 
 * <p>
 * In general, the XML files are formatted following this example. The root element is UserData. 
 * Simple key-value pairs are in KeyValuePair tags. The KeyValueList tag holds a single key and 
 * a list of values. 
 * <pre>
 * &lt;UserData&gt;
 *   &lt;KeyValuePair key=&quot;foo&quot; value=&quot;bar&quot;/&gt;
 *   &lt;KeyValueList key=&quot;baz&quot;&gt;
 *     &lt;ValueList value=&quot;qux1&quot;/&gt;
 *     &lt;ValueList value=&quot;qux2&quot;/&gt;
 *   &lt;/KeyValueList&gt;
 * &lt;/UserData&gt;
 *</pre>
 * <p>
 * We don't write out the XML file if an update does not change the value.
 * <p>
 * In cases where complex data must be stored and retrieved, such as Project data, use the getDoc
 * and putDoc methods which allow storage and retrieval of JDOM Document objects. 
 * <p>
 * This class will create the user's directory if it does not already exist.
 * <p>
 * This class is designed to be Thread Safe.  We use ConcurrentHashMaps to provide 
 * thread-safety when accessing user properties, and we synchronize on the User instance to 
 * when reading/writing XML files to sequentialize access to the file system.  
 * @author    Philip M. Johnson
 * @version   $Id: User.java,v 1.1.1.1 2005/10/20 23:56:44 johnson Exp $
 */
public class User implements Comparable {
  /** The 12 character user key associated with this user. */
  private String userKey;
  /** Keys are partition name strings, values are HashMaps of persistent key-value pairs. */
  private ConcurrentHashMap<String, Map<Object, Object>> partitionMap = 
    new ConcurrentHashMap<String, Map<Object, Object>>();
  /** Holds the key-value pairs for volatile, non-persistent associations. No partitions. */
  private ConcurrentHashMap<Object, Object> volatileMap = new ConcurrentHashMap<Object, Object>();
  /** Holds the directory containing the user files. */
  private File userDir;
  /** Default user.*.xml file component name. Package-private for DirectoryKeyTable. */
  static String defaultPartition = "default";
  /** The key used to get and retrieve the email address for this user. */
  private static String emailKey = "UserEmail";
  /** The key used to get and retrieve the last page displayed for this user. */
  private static String lastPage = "LastPage";
  /** The number of times a user XML file has been written since startup. To check performance. */
  static long numWrites = 0;
  /** The number of times a user XML file has been read since startup. To check performance. */
  static long numReads = 0;

  /**
   * Generates a new User instance given the key and the email address, which 
   * occurs when UserManager is defining a new User.
   * Only UserManager invokes User constructors, thus package-private visibility.  
   * To obtain a user instance, you normally use code such as:
   * <pre>UserManager.getInstance().getUser(userKey);</pre>
   * The user's directory is created and the default user XML file is written with the email.
   * The userEmail is always lowercased before storage. 
   * @param userKey The user key for this user.
   * @param userEmail The email for this user.
   */
  User(String userKey, String userEmail) {
    this.userKey = userKey;
    this.userDir = new File(ServerProperties.getInstance().getUsersDir(), userKey);
    this.userDir.mkdirs();
    this.put(User.emailKey, userEmail.toLowerCase());
  }

  /**
   * Generates a new User instance given only its key, which occurs when UserManager is
   * traversing the Hackystat data directory.  
   * Only UserManager invokes User constructors, thus package-private visibility.  
   * XML-based values are loaded lazily.
   * If the user.default.xml file exists, then we use it, otherwise we look for user.txt 
   * and load those properties. 
   * We try to delete the user.txt file after loading, but this may not succeed. 
   * The user's directory is created if it does not already exist.
   *
   * @param userKey The user key for this user.
   */
  User(String userKey) {
    this.userKey = userKey;
    this.userDir = new File(ServerProperties.getInstance().getUsersDir(), userKey);
    this.userDir.mkdirs();
    // If we can find the user.default.xml file, then we won't try to load user.txt.
    File xmlFile = new File(this.userDir, "user." + User.defaultPartition + ".xml");
    if (!xmlFile.exists()) {
      processPropertyFile(userKey);
    }
  }
  
  /**
   * Reads in the user property file if present, defines any properties, and deletes the file.
   * @param userKey The user key.
   * @return Returns true if a property file was found.
   */
  private boolean processPropertyFile(String userKey) {
    File propFile = new File(this.userDir, "user.txt");
    boolean fileExists = propFile.exists();
    if (fileExists) {
      ServerProperties.getInstance().getLogger().info("Found " + propFile.toString());
      try {
        Properties props = new Properties();
        FileInputStream propStream = new FileInputStream(propFile);
        props.load(propStream);
        propStream.close();
        for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
          String key = (String) e.nextElement();
          String value = props.getProperty(key);
          // Writes the file each time through the loop, but this happens only once per user.
          this.put(key, value);
        }
      }
      catch (Exception e) {
        ServerProperties.getInstance().getLogger().warning(userKey + " bad prop file: " + e);
      }
      if (!propFile.delete()) {
        ServerProperties.getInstance().getLogger().warning("Delete failed " + propFile.toString());
      }
    }
    return fileExists;
  }
  
  /**
   * Puts the key-value pair and stores them in the default user xml file.
   * The file is written only if the data has changed.
   * @param key A string key, cannot be null.
   * @param value A string value, cannot be null.
   */
  public void put(String key, String value) {
    this.put(key, value, User.defaultPartition);
  }

  /**
   * Puts the key-StringList pair and stores them in the default user xml file.
   * The file is written only if the data has changed.
   * @param key A string key, cannot be null. If null, reset to "".
   * @param valueList A list of Strings, cannot be null. If null, reset to an empty ArrayList.
   */
  public void putList(String key, List valueList) {
    this.putList(key, valueList, User.defaultPartition);
  }

  /**
   * Removes this key-value pair from the default partition.
   * Removes both key-value or key-listValue pairings.
   * @param key The string key whose key-value pair is to be removed.
   */  
  public void remove(String key) {
    this.remove(key, User.defaultPartition);
  }
  
  /**
   * Removes this key-value pair from the specified partition.
   * Removes both key-value or key-listValue pairings.
   * @param key The string key whose key-value pair is to be removed.
   * @param partition The string partition name.
   */  
  public void remove(String key, String partition) {
    if ((partition != null) && (key != null)) {
      ConcurrentHashMap keyValueMap = (ConcurrentHashMap) this.partitionMap.get(partition);
      if (keyValueMap != null) {
        keyValueMap.remove(key);
        try {
          this.storeKeyValueMap(partition, keyValueMap);
        }
        catch (Exception e) {
          ServerProperties.getInstance().getLogger().warning("Error: User.remove" + this + " " + e);
        }
      }
    }
  }
  
  /**
   * Puts the key-valueList pair and stores them in the user XML file specified by partition.
   * The file is written only if the data has changed.
   * Does nothing if key, value, or partition are null.
   * @param key A string key.
   * @param valueList A list of strings.
   * @param partition A partition, such as "workspace". Avoid blanks and punctuation.
   */
  public void putList(String key, List valueList, String partition) {
    if ((key != null) && (valueList != null) && (partition != null)) {
      this.putHelper(key, valueList, partition);
    }
  }

  /**
   * Puts the key-value pair and stores them in the user xml file specified by partition.
   * The file is written only if the data has changed.
   * Does nothing if key, value, or partition are null.
   * @param key A string key.
   * @param value A string value.
   * @param partition A partition, such as "workspace". Avoid blanks and punctuation.
   */
  public void put(String key, String value, String partition) {
    if ((key != null) && (value != null) && (partition != null)) {
      this.putHelper(key, value, partition);
    }
  }

  /**
   * Writes out the provided JDOM Document instance to an XML file.
   * If the docPartition is "foo", then the file will be named
   * "user.foo.doc.xml".  This avoids clashes with files that are
   * read and written using the standard put and get mechanisms.
   * Put another way, to read a file with getDoc, you must have first
   * written it with putDoc. 
   * @param document The JDOM Document to be written.
   * @param docPartition The string partition to name this file. 
   */
  public void putJDomDocument(Document document, String docPartition) {
    try {
      writeJDOMDocument(document, docPartition + ".doc"); // .xml added later.
    }
    catch (Exception e) {
      ServerProperties.getInstance().getLogger().warning("Error: User.putDoc " + this + " " + e);
    }
  }
  
  /**
   * Helper function for put and putList. Does the actual storage and writing.
   * @param key The string key.
   * @param value The value, either a String or a list of Strings.
   * @param partition The partition.
   */
  private void putHelper(String key, Object value, String partition) {
    try {
      if (this.partitionMap.get(partition) == null) {
        // Initialize the partition key-value mapping from the disk.
        partitionMap.put(partition, this.loadKeyValueMap(partition));
      }
      Map<Object, Object> keyValueMap = this.partitionMap.get(partition);
      Object oldValue = keyValueMap.get(key);
      // Only update and write out the data if it has changed.
      if (!value.equals(oldValue)) {
        keyValueMap.put(key, value);
        this.storeKeyValueMap(partition, keyValueMap);
      }
    } 
    catch (Exception e) {
      ServerProperties.getInstance().getLogger().warning("Error: User.put " + this + " " + e);
    }
  }
  
  /**
   * Returns a JDOM document obtained by reading in the XML file for this
   * user specified by docPartition. 
   * If no such file exists, or if there is an error reading, then an
   * empty Document instance is returned.
   * @param docPartition The string docPartition used to identify this XML file.
   * @return A Document instance corresponding to this docPartition. 
   */
  public Document getJDomDocument(String docPartition) {
    synchronized (this) {
      File xmlFile = new File(this.userDir, "user." + docPartition + ".doc.xml");
      if (xmlFile.exists()) {
        try {
          SAXBuilder builder = new SAXBuilder();
          return builder.build(xmlFile);
        }
        catch (Exception e) {
          ServerProperties.getInstance().getLogger().info("Error reading " + xmlFile + e);
          return new Document();
        }
      }
      else {
        return new Document();
      }
    }
  }
  
  /**
   * Reads key-value pairs from the user's xml file for the specified partition, and 
   * builds and returns a HashMap data structure containing them. 
   * File system reads and writes are synchronized on this instance for thread-safety. 
   * @param partition The partition used to identify the xml file.
   * @return A Map containing the key-value pairs (both simple and lists) from that partition.
   */
  private Map<Object, Object> loadKeyValueMap(String partition) {
    synchronized (this) {
      Map<Object, Object> keyValueMap = new ConcurrentHashMap<Object, Object>();
      File xmlFile = new File(this.userDir, "user." + partition + ".xml");
      if (xmlFile.exists()) {
        try {
          SAXBuilder builder = new SAXBuilder();
          Document doc = builder.build(xmlFile);
          Element root = doc.getRootElement();
          // First, read the simple key-value string pairs.
          List keyValuePairs = root.getChildren("KeyValuePair");
          for (Iterator i = keyValuePairs.iterator(); i.hasNext(); ) {
            Element keyValuePair = (Element) i.next();
            String key = keyValuePair.getAttributeValue("key");
            String value = keyValuePair.getAttributeValue("value");
            keyValueMap.put(key, value);
          }
          // Second, read the key-value lists. 
          List keyValueLists = root.getChildren("KeyValueList");
          for (Iterator i = keyValueLists.iterator(); i.hasNext(); ) {
            Element keyValueList = (Element) i.next();
            String key = keyValueList.getAttributeValue("key");
            List<String> valueList = new ArrayList<String>();
            for (Iterator j = keyValueList.getChildren("ValueList").iterator(); j.hasNext();) {
              Element valueElement = (Element) j.next();
              valueList.add(valueElement.getAttributeValue("value"));
            }
            keyValueMap.put(key, valueList);
            User.numReads++;
          }
        }
        catch (Exception e) {
          ServerProperties.getInstance().getLogger().info("Error reading " + xmlFile + e);
        }
      }
      return keyValueMap;
    }
  }
  
  /**
   * Writes the provided keyValueMap to the XML file specified by partition.
   * Updates User.numWrites if successful.
   * File system reads and writes are synchronized on this instance for thread-safety. 
   * @param partition The partition name.
   * @param keyValueMap The keyValueMap to be stored.
   * @throws Exception If problems during the write.
   */
  private void storeKeyValueMap (String partition, Map keyValueMap) throws Exception {
    // Build the JDOM Document from keyValueMap
    Element userDataElement = new Element("UserData");
    Document document = new Document(userDataElement);
    for (Iterator i = keyValueMap.entrySet().iterator(); i.hasNext();) {
      Entry entry = (Entry) i.next();
      Element keyValueElement = new Element("Unknown");
      userDataElement.addContent(keyValueElement);
      keyValueElement.setAttribute("key", (String) entry.getKey());
      if (entry.getValue() instanceof String) {
        keyValueElement.setName("KeyValuePair");
        keyValueElement.setAttribute("value", (String) entry.getValue());
      }
      else if (entry.getValue() instanceof List) {
        keyValueElement.setName("KeyValueList");
        for (Iterator j = ((List) entry.getValue()).iterator(); j.hasNext();) {
          String value = (String) j.next();
          Element valueElement = new Element("ValueList");
          valueElement.setAttribute("value", value);
          keyValueElement.addContent(valueElement);
        }
      }
    }
    writeJDOMDocument(document, partition);
  }
  
  /**
   * Writes the passed JDOM Document object out to a file.
   * Synchronized on this User instance to serialize file system access.
   * @param document  The JDOM document to be written.
   * @param partition The partition it should be written into.
   * @throws Exception If any errors occur during the write. 
   */
  private void writeJDOMDocument(Document document, String partition) throws Exception {
    synchronized (this) {
      File xmlFile = new File(this.userDir, "user." + partition + ".xml");    
      XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
      //This encoding supports unicode (including foreign characters). 
      OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8");
      outputter.output(document, writer);
      writer.close();
      // Update the count of XML file writes. 
      User.numWrites++;
    } 
  }
  
  /**
   * Returns the String value associated with key, or null if not found.
   * Looks in the default partition. 
   * @param key The string key.
   * @return The value associated with key, or null.
   */
  public String get(String key) {
    return this.get(key, User.defaultPartition);
  }
  
  /**
   * Returns the list of Strings associated with key, or null if not found.
   * Looks in the default partition. 
   * @param key The string key.
   * @return A list containing strings, or an empty List if not found.
   */
  public List getList(String key) {
    return this.getList(key, User.defaultPartition);
  }
  
  /**
   * Returns the String value associated with key in partition, or null if not found.
   * @param key The string key.
   * @param partition The partition to look in.
   * @return The string value, or null if not found.
   */
  public String get(String key, String partition) {
    Map<Object, Object> keyValueMap = this.partitionMap.get(partition);
    if (keyValueMap == null) {
      keyValueMap = this.loadKeyValueMap(partition);
      this.partitionMap.put(partition, keyValueMap);
    }
    return (String) keyValueMap.get(key);
  }

  /**
   * Returns the list of Strings associated with key in partition, or an empty list
   * if not found.
   * @param key The string key.
   * @param partition The string partition.
   * @return A list containing strings, or an empty List if not found.
   */
  public List getList(String key, String partition) {
    Map<Object, Object> keyValueMap = this.partitionMap.get(partition);
    if (keyValueMap == null) {
      keyValueMap = this.loadKeyValueMap(partition);
      this.partitionMap.put(partition, keyValueMap);
    }
    return (keyValueMap.get(key) == null) ? new ArrayList<String>() : (List) keyValueMap.get(key);
  }
  
  /**
   * Returns true if this user is the server administrator.
   * @return   True if this user is the server administrator.
   */
  public boolean isAdmin() {
    return this.getUserEmail().equals(ServerProperties.getInstance().getAdminEmail());
  }

  /**
   * Gets the user key associated with this user.
   * @return   The user key.
   */
  public String getUserKey() {
    return this.userKey;
  }
  
  /**
   * Gets the last Page instance that was displayed to this user, or null if there 
   * was no last page. For use in displayDoc processing.
   * @return   The last displayed Page or null.
   */
//  public Page getLastPage() {
//    return (Page) this.volatileMap.get(User.lastPage);
//  }
  
   /**
    * Returns the object associated with key in the volatile map associated with this user,
    * or null if not found.
    * The volatile map is not persisted.
    * @param key The key.
    * @return The object associated with key, or null if not found.
    */
   public Object getVolatile(Object key) {
     return this.volatileMap.get(key);
   }  

  /**
   * Saves the key value pair in the volatile map associated with this user.
   * @param key The key.
   * @param value The value.
   */
  public void putVolatile(Object key, Object value) {
    this.volatileMap.put(key, value);
  }

  /**
   * Sets the last page that was displayed to this user. For use in displayDoc processing.
   * @param lastPage  The last page instance displayed to this user.
   */
//  public void setLastPage(Page lastPage) {
//    this.volatileMap.put(User.lastPage, lastPage);
//  }

  /**
   * Gets the userEmail associated with this user.
   * @return   The userEmail associated with this user.
   */
  public String getUserEmail() {
    return this.get(User.emailKey);
  }

  /**
   * Gets the String property value associated with the passed key, or returns the passed default
   * string if no such key-value pair exists. User property keys should use a type.key notation,
   * such as "selector.StartMonthDate", where types are selector, analyses, preference, alert, for
   * readability.
   *
   * @param key           The string key.
   * @param defaultValue  The value returned if no such key exists.
   * @return              The property associated with key.
   */
  public String getOrDefault(String key, String defaultValue) {
    return (this.get(key) == null) ? defaultValue : this.get(key);
  }

  /**
   * Returns true if this User is a test case generated user.
   * @return True if a test user. 
   */  
  public boolean isTestUser() {
    String userEmail = this.getUserEmail();
    return userEmail.endsWith(ServerProperties.getInstance().getTestDomain());
  }

  /**
   * Returns a string containing this User userKey and Email.
   * @return A string identifying this user.
   */
  public String toString() {
    return "<User " + this.userKey + " " + this.getUserEmail() + ">"; 
  }
  
  /**
   * Clears the contents of the internal hashmaps storing key-value pairs, which
   * means that the next access will force a re-read from the persistent store. 
   * This package-private method is provided for unit testing purposes only.
   */
  void clear () {
    this.partitionMap = new ConcurrentHashMap<String, Map<Object, Object>>();
    this.volatileMap = new ConcurrentHashMap<Object, Object>();
  }
  
  /**
   * Compares two users to each other and returns their ranking.
   * Provided to allow Users to be stored in sorted data structures.
   * @param arg The object to compare this User to.
   * @return -1 if less, 0 if equal, +1 if greater. 
   */
  public int compareTo(Object arg) {
    return this.getUserKey().compareTo(((User) arg).getUserKey());
  }
  
  /**
   * Two Users are equal iff their userKey values are equal.
   * @param obj The user to compare.
   * @return True if obj is a User and their userKey values are equal.
   */
  public boolean equals (Object obj) {
    if (!(obj instanceof User)) {
      return false;
    } 
    return this.getUserKey().equals(((User)obj).getUserKey());
  }
  
  /**
   * Gets the hash code of this user, computed from its Name.
   * @return Hashcode of the user.
   */
  public int hashCode() {
    return this.getUserKey().hashCode();
  }
  
//  /**
//   * Gets this user's command visibility preference.
//   * @return This user's command visibility preference
//   */
//  public UserCommandVisibility getCommandVisibilityPreference() {
//    return UserCommandVisibilityManager.getInstance().getUserCommandVisibility(this);
//  }
}


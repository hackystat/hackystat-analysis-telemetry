package org.hackystat.telemetry.service.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Provides access to the values stored in the telemetry.properties file, and provides default
 * values if the file is not found.
 * 
 * @author Philip Johnson
 */
public class ServerProperties {

  /** The sensorbase host. */
  public static final String SENSORBASE_FULLHOST_KEY = "telemetry.sensorbase.host";
  /** The DPD host. */
  public static final String DAILYPROJECTDATA_FULLHOST_KEY = "telemetry.dailyprojectdata.host";
  /** The dailyprojectdata hostname key. */
  public static final String HOSTNAME_KEY = "telemetry.hostname";
  /** The dailyprojectdata context root. */
  public static final String CONTEXT_ROOT_KEY = "telemetry.context.root";
  /** The logging level key. */
  public static final String LOGGING_LEVEL_KEY = "telemetry.logging.level";
  /** The dailyprojectdata port key. */
  public static final String PORT_KEY = "telemetry.port";
  /** The definitions directory key. */
  public static final String DEF_DIR_KEY = "telemetry.def.dir";
  /** The prefetch directory key. */
  public static final String PREFETCH_DIR_KEY = "telemetry.prefetch.dir";
  /** The dpd port key during testing. */
  public static final String TEST_PORT_KEY = "telemetry.test.port";
  /** The test installation key. */
  public static final String TEST_INSTALL_KEY = "telemetry.test.install";
  /** The test installation key. */
  public static final String TEST_HOSTNAME_KEY = "telemetry.test.hostname";
  /** The test sensorbase host key. */
  public static final String TEST_SENSORBASE_HOST_KEY = "telemetry.test.sensorbase.host";
  /** The test dpd host key. */
  public static final String TEST_DAILYPROJECTDATA_FULLHOST_KEY = "telemetry.test.dpd.host";

  /** Where we store the properties. */
  private Properties properties;

  /** Indicates whether DPDClient caching is enabled. */
  public static final String CACHE_ENABLED = "telemetry.cache.enabled";
  /** The maxLife in seconds for each instance in each DPDClient cache. */
  public static final String CACHE_MAX_LIFE = "telemetry.cache.max.life";
  /** The in-memory capacity of each DPDClient cache. */
  public static final String CACHE_CAPACITY = "telemetry.cache.capacity";
  
  /**
   * Creates a new ServerProperties instance. Prints an error to the console if problems occur on
   * loading.
   */
  public ServerProperties() {
    try {
      initializeProperties();
    }
    catch (Exception e) {
      System.out.println("Error initializing Telemetry server properties.");
    }
  }

  /**
   * Reads in the properties in ~/.hackystat/dailyprojectdata/dailyprojectdata.properties if this
   * file exists, and provides default values for all properties. .
   * 
   * @throws Exception if errors occur.
   */
  private void initializeProperties() throws Exception {
    String userHome = System.getProperty("user.home");
    String userDir = System.getProperty("user.dir");
    String propFile = userHome + "/.hackystat/telemetry/telemetry.properties";
    this.properties = new Properties();
    // Set defaults
    properties.setProperty(SENSORBASE_FULLHOST_KEY, "http://localhost:9876/sensorbase/");
    properties
        .setProperty(DAILYPROJECTDATA_FULLHOST_KEY, "http://localhost:9877/dailyprojectdata/");
    properties.setProperty(HOSTNAME_KEY, "localhost");
    properties.setProperty(PORT_KEY, "9878");
    properties.setProperty(CONTEXT_ROOT_KEY, "telemetry");
    properties.setProperty(LOGGING_LEVEL_KEY, "INFO");
    properties.setProperty(DEF_DIR_KEY, userDir + "/definitions");
    properties.setProperty(PREFETCH_DIR_KEY, userDir + "/prefetch");
    properties.setProperty(TEST_PORT_KEY, "9978");
    properties.setProperty(TEST_HOSTNAME_KEY, "localhost");
    properties.setProperty(TEST_SENSORBASE_HOST_KEY, "http://localhost:9976/sensorbase");
    properties.setProperty(TEST_DAILYPROJECTDATA_FULLHOST_KEY,
        "http://localhost:9977/dailyprojectdata");
    properties.setProperty(TEST_INSTALL_KEY, "false");
    properties.setProperty(CACHE_ENABLED, "true");
    properties.setProperty(CACHE_MAX_LIFE, "86400");
    properties.setProperty(CACHE_CAPACITY, "50000");
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(propFile);
      properties.load(stream);
      System.out.println("Loading Telemetry properties from: " + propFile);
    }
    catch (IOException e) {
      System.out.println(propFile + " not found. Using default telemetry properties.");
    }
    finally {
      if (stream != null) {
        stream.close();
      }
    }
    // make sure that SENSORBASE_HOST always has a final slash.
    String sensorBaseHost = (String) properties.get(SENSORBASE_FULLHOST_KEY);
    if (!sensorBaseHost.endsWith("/")) {
      properties.put(SENSORBASE_FULLHOST_KEY, sensorBaseHost + "/");
    }
    // make sure that DAILYPROJECTDATA_HOST always has a final slash.
    String dailyProjectDataHost = (String) properties.get(DAILYPROJECTDATA_FULLHOST_KEY);
    if (!dailyProjectDataHost.endsWith("/")) {
      properties.put(DAILYPROJECTDATA_FULLHOST_KEY, dailyProjectDataHost + "/");
    }
    
    trimProperties(properties);
    // Now add to System properties.
    Properties systemProperties = System.getProperties();
    systemProperties.putAll(properties);
    System.setProperties(systemProperties);
  }

  /**
   * Sets the following properties' values to their "test" equivalent.
   * <ul>
   * <li> HOSTNAME_KEY
   * <li> PORT_KEY
   * <li> SENSORBASE_HOST_KEY
   * <li> DAILYPROJECTDATA_FULLHOST_KEY
   * </ul>
   * Also sets TEST_INSTALL_KEY's value to "true".
   */
  public void setTestProperties() {
    properties.setProperty(HOSTNAME_KEY, properties.getProperty(TEST_HOSTNAME_KEY));
    properties.setProperty(PORT_KEY, properties.getProperty(TEST_PORT_KEY));
    properties.setProperty(SENSORBASE_FULLHOST_KEY, properties
        .getProperty(TEST_SENSORBASE_HOST_KEY));
    properties.setProperty(DAILYPROJECTDATA_FULLHOST_KEY, properties
        .getProperty(TEST_DAILYPROJECTDATA_FULLHOST_KEY));
    properties.setProperty(TEST_INSTALL_KEY, "true");
    properties.setProperty(CACHE_ENABLED, "false");
    trimProperties(properties);
  }

  /**
   * Returns all of the Telemetry settings to the logger.
   * 
   * @return The string indicating the properties.
   */
  public String echoProperties() {
    String cr = System.getProperty("line.separator");
    String eq = " = ";
    String pad = "                ";
    return "Telemetry Properties:" + cr + 
    pad + SENSORBASE_FULLHOST_KEY + eq + get(SENSORBASE_FULLHOST_KEY) + cr + 
    pad + DAILYPROJECTDATA_FULLHOST_KEY + eq + get(DAILYPROJECTDATA_FULLHOST_KEY) + cr +
    pad + HOSTNAME_KEY + eq + get(HOSTNAME_KEY) + cr + 
    pad + CONTEXT_ROOT_KEY + eq + get(CONTEXT_ROOT_KEY) + cr + 
    pad + LOGGING_LEVEL_KEY + eq + get(LOGGING_LEVEL_KEY) + cr + 
    pad + DEF_DIR_KEY + eq + get(DEF_DIR_KEY) + cr + 
    pad + PORT_KEY + eq + get(PORT_KEY) + cr + 
    pad + TEST_INSTALL_KEY + eq + get(TEST_INSTALL_KEY) + cr + 
    pad + CACHE_ENABLED + eq + get(CACHE_ENABLED) + cr +
    pad + CACHE_MAX_LIFE + eq + get(CACHE_MAX_LIFE) + cr +
    pad + CACHE_CAPACITY + eq + get(CACHE_CAPACITY);
  }

  /**
   * Returns the value of the Server Property specified by the key.
   * 
   * @param key Should be one of the public static final strings in this class.
   * @return The value of the key, or null if not found.
   */
  public String get(String key) {
    return this.properties.getProperty(key);
  }

  /**
   * Ensures that the there is no leading or trailing whitespace in the property values. The fact
   * that we need to do this indicates a bug in Java's Properties implementation to me.
   * 
   * @param properties The properties.
   */
  private void trimProperties(Properties properties) {
    // Have to do this iteration in a Java 5 compatible manner. no stringPropertyNames().
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      String propName = (String)entry.getKey();
      properties.setProperty(propName, properties.getProperty(propName).trim());
    }
  }

  /**
   * Returns the fully qualified host name, such as "http://localhost:9878/telemetry/".
   * 
   * @return The fully qualified host name.
   */
  public String getFullHost() {
    return "http://" + get(HOSTNAME_KEY) + ":" + get(PORT_KEY) + "/" + get(CONTEXT_ROOT_KEY) + "/";
  }
  
  /**
   * Returns true if caching is enabled in this service. 
   * @return True if caching enabled.
   */
  public boolean isCacheEnabled() {
    return Boolean.valueOf(this.properties.getProperty(CACHE_ENABLED));
  }
  
  /**
   * Returns the caching max life as a long.
   * If the property has an illegal value, then return the default. 
   * @return The max life of each instance in the cache.
   */
  public long getCacheMaxLife() {
    String maxLifeString = this.properties.getProperty(CACHE_MAX_LIFE);
    long maxLife = 0;
    try {
      maxLife = Long.valueOf(maxLifeString);
    }
    catch (Exception e) {
      System.out.println("Illegal cache max life: " + maxLifeString + ". Using default.");
      maxLife = 86400L;
    }
    return maxLife;
  }
  
  /**
   * Returns the in-memory capacity for each cache.
   * If the property has an illegal value, then return the default. 
   * @return The in-memory capacity.
   */
  public long getCacheCapacity() {
    String capacityString = this.properties.getProperty(CACHE_CAPACITY);
    long capacity = 0;
    try {
      capacity = Long.valueOf(capacityString);
    }
    catch (Exception e) {
      System.out.println("Illegal cache capacity: " + capacityString + ". Using default.");
      capacity = 50000L;
    }
    return capacity;
  }
}

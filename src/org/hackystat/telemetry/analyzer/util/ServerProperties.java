package org.hackystat.telemetry.analyzer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//import javax.servlet.ServletContext;

//import org.hackystat.core.kernel.sdt.SensorDataType;
import org.hackystat.telemetry.analyzer.util.user.User;

//import javax.servlet.ServletContext;
//
//import org.hackystat.core.kernel.sdt.SensorDataType;
//import org.hackystat.core.kernel.util.DateInfo;
//import org.hackystat.core.kernel.util.ElapsedTimeFormat;
//import org.hackystat.core.kernel.util.ElapsedTimeFormatException;
//import org.hackystat.core.kernel.util.OneLineFormatter;
//import org.hackystat.core.kernel.util.StackTrace;
//import org.hackystat.telemetry.util.user.User;
//import org.hackystat.telemetry.util.user.UserManager;

/**
 * Provides server and site configuration information to (1) server-side code running inside Tomcat,
 * (2) client-side test code running in a separate process, and (3) code running inside SensorShell.
 * <p>
 * When running inside Tomcat, ServerProperties reads information from the hackystat.site.properties
 * to determine how to find the WEB-INF directory and thus the data stored therein.
 * In this situation, the hackystat.site.properties file must be located
 * in the tomcat conf/ directory (i.e. catalina.home/conf/hackystat.site.properties).
 * <p>
 * When running inside client-side test classes, ServerProperties must provide the URL
 * of the running test server.  In this situation, the hackystat.site.properties file must be 
 * located in the directory specified by the hackystat.properties.dir environment
 * variable. (This is typically specified within the hackyCore_Build/junit.build.xml file and
 * passed into the JUnit task.)
 * <p>
 * The third situation is when a specific method, such as getWebInfDir(), is called
 * from within SensorShell (specifically, it's called by the SdtManager while it's
 * instantiating SDTs). In this case, ServerProperties should fail gracefully, and 
 * all of the methods should return default values. 
 * <p>
 * The constructor checks first to see if there is a system property called 
 * catalina.home, and if so, requires the hackystat.site.properties file to be found in its
 * conf/ directory.
 * If there is no catalina.home, it next checks to see if there is a system property
 * called hackystat.properties.dir, and if so, requires the hackystat.site.properties file
 * to be found there. 
 * <p>
 * All of the ServerProperties properties are added to the System property instance after being
 * read from the file.  This enables other tools that interoperate with Hackystat to obtain 
 * settings from the System instance and/or environments to configure System property settings
 * (such as for SSL) simply by adding entries to the properties file. 
 * 
 * @author    Philip M. Johnson
 * @version   $Id: ServerProperties.java,v 1.1.1.1 2005/10/20 23:56:42 johnson Exp $
 */
public class ServerProperties {
  
  private String loggingLevel = "INFO";
  private String tomcatHome = "";
  private String mailServer = "";
  private String siteName = "";
  private String adminEmail = "";
  private String adminUserKey = null;
  private String hackystatHost = "";
  private File webAppDir = new File("");
  private String contextRoot = "hackystat";
  private String hackystatHome = "";
  private boolean enableAnalysisLogging = false;
    
  /** The ServletContext instance associated with this web applicaton instance. */
//  private ServletContext servletContext = null;
  
  /** The Properties instance corresponding to the hackystat.site.properties file values. */
  private Properties properties = null;

  /** The time that this web application was last loaded. */
  private Date startupTime = null;

  /** The root directory where server data will be stored. */
  private File dataRootDir = null;
//
//  /** The logging instance for this server. */
//  private Logger logger = null;
//
  /** The logging formatter that prefixes the message string with a date stamp and adds a cr. */
  private Formatter oneLineFormatter = null;

  /** The OS specific directory separator. */
  private String slash = File.separator;
//  
//  /** The singleton instance of this class. */
  private static ServerProperties theInstance = null;
//  
  /** The version string. */
  private String version = "";
  /** The buildtime string. */
  private String buildtime = "";
  
  /** The hour at which daily alert emails will be sent. */
  private int dailyAlertHour = 2;
//  
//  
  /**
   * Returns the singleton ServerProperties instance. Note that when running inside
   * the web server, ServerProperties will always be initialized inside ServerStartup, and thus
   * will use getInstance(ServletContext). If first called by code running tests, or in code
   * running as part of the SensorShell, then the server context will be null.
   * @return The ServerProperties instance.
   */
  public static ServerProperties getInstance() {
//    if (theInstance == null) {
//      // Happens when initialized by testing or SensorShell code.
//      theInstance = new ServerProperties(null);
//    }
    return theInstance;
  }
  
  /**
   * Returns the initialized singleton. Called by ServerStartup when the web app starts up.
   * @param servletContext The servlet context instance.
   * @return The singleton ServletContext.
   */
//  public static ServerProperties getInstance(ServletContext servletContext) {
////    if (theInstance == null) { 
////      theInstance = new ServerProperties(servletContext);
////    }
//    return theInstance;
//  }
    
  /**
   * Creates the singleton ServerProperties instance and initializes its internal state by
   * loading the hackystat server properties file if available.
   * Under the new build system, the hackystat.server.properties file should be found in the 
   * catalina.home/conf directory if running on the server side or through reference to the 
   * hackystat.properties.dir system property if running in test code.  If running in 
   * SensorShell, this file will not be present at all. 
   * When testing, the hackystat.war.dir system property should also be provided
   * in order to indicate where the WEB-INF directory is actually located.
   * @param servletContext The ServletContext instance for this webapp, or null if
   * running outside of a server.
   */
//  private ServerProperties(ServletContext servletContext) {
////    this.servletContext = servletContext;
////    this.startupTime = new Date();
////    this.tomcatHome = System.getProperty("catalina.home", "");
////    this.properties = getHackystatProperties();
////    if (this.properties == null) {
////      initializeProperties();
////    }
////    else {
////      initializeProperties(this.properties);
////    }
////    //printProperties();
////    // Add the current set of hackystat properties to the System property object.
////    ServerProperties.addToSystemProperties(this.properties);
////    // Initialize the logger, now that everything should be OK.
////    this.logger = this.getLogger();
////    // Initialize hackystatHome, now that we know the host and contextRoot.
////    this.hackystatHome = this.getHackystatHost() + this.contextRoot + "/";
//  }
  
  /**
   * Attempts to load the hackystat.server.properties file from either catalina.home/conf or from 
   * hackystat.properties.dir. 
   * @return Either the properties instance or null if it could not be found or a RuntimeException
   * if it was found but was malformed.
   */
  private Properties getHackystatProperties() {
    return null;
//    File propertiesFile = new File("");
//    // First, try to get the Properties file using catalina.home.
//    File catalinaHome = new File(System.getProperty("catalina.home", ""));
//    if (catalinaHome.exists()) {
//      propertiesFile = new File(catalinaHome, "conf/hackystat.server.properties");
//    }
//    else {
//      // Catalina.home does not exist, so try hackystat.war.dir.
//      String hackystatWarDirString = System.getProperty("hackystat.properties.dir", "");
//      File warDir = new File(hackystatWarDirString);
//      if (warDir.exists()) {
//        propertiesFile = new File(warDir, "hackystat.server.properties");
//      }
//    }
//   
//    // If we haven't found a hackystat.server.properties file, then return now with null.
//    if (!propertiesFile.exists()) {
//      return null;
//    }
//    
//    // Otherwise, try to load the properties file.
//    Properties properties = new Properties();
//    try {
//      properties.load(new FileInputStream(propertiesFile));
//    }
//    catch (Exception e) {
//      throw new RuntimeException("ServerProperties (hackystat.server.properties malformed).");
//    }
//    // Victory!
//    return properties;
  }          
  
  /**
   * Uses hackystat.server.properties values to complete initialization of ServerProperties.
   * @param properties The Properties instance containing hackystat.server.properties values. 
   */
  private void initializeProperties(Properties properties) {
//    this.tomcatHome = properties.getProperty("tomcat.home", "");
//    this.adminEmail = properties.getProperty("hackystat.admin.email", null);
//    this.adminUserKey = properties.getProperty("hackystat.admin.userkey", null);
//    this.hackystatHost = properties.getProperty("hackystat.host", "");
//    this.mailServer = properties.getProperty("hackystat.mail.server", "");
//    this.siteName = properties.getProperty("hackystat.site.name", "");
//    this.loggingLevel = properties.getProperty("hackystat.logging.level", "info").toUpperCase();
//    this.version = properties.getProperty("hackystat.version", "(Unknown Version)");
//    this.buildtime = properties.getProperty("hackystat.buildtime", "(Unknown Build Time)");
//    this.dataRootDir = new File (properties.getProperty("hackystat.data.dir"));
//    this.contextRoot = properties.getProperty("hackystat.context.root", "hackystat"); 
//    this.enableAnalysisLogging = "true".equals(properties.getProperty("hackystat.analysis.logging",
//      "false"));
//    if (!this.dataRootDir.exists()) {
//      this.dataRootDir.mkdirs();
//    }
//    this.webAppDir = initializeWebAppDir(); 
//    try {
//      this.dailyAlertHour 
//          = Integer.parseInt(properties.getProperty("hackystat.dailyalert.hour", "2"));
//    }
//    catch (Exception e) {
//      this.getLogger().warning("Invalid value for hackystat.dailyalert.hour detected.");
 //   }
  }
  
  /**
   * Determine the location of the hackystat web application directory. When this
   * code runs in the server, it can be found using the servletContext variable. 
   * When it runs during testing, then JUnit should be passed hackystat.war.dir as
   * a System property variable that specifies the location. Otherwise, we try
   * tomcat.home/webapps/hackystat, although that is unlikely to work. If we can't
   * find the directory anywhere, then we print out a warning and return null.
   * @return The hackystat webapp directory, or null if not found.
   */
  private File initializeWebAppDir () {
//    File webAppDir = null;
//    // First, try to get it using hackystat.war.dir property. 
//    String hackystatWarDirString = System.getProperty("hackystat.war.dir", null);
//    
//    if (hackystatWarDirString != null) {
//      webAppDir = new File(hackystatWarDirString);
//      if (webAppDir.exists()) {
//        return webAppDir;
//      }
//    }
//    // Second, try to get it using servlet context instance.
//    if (this.servletContext != null) {
//      webAppDir = new File(this.servletContext.getRealPath("/"));
//      if (webAppDir.exists()) {
//        return webAppDir;
//      }
//    }
//    // Third, try to get it using tomcat.home
//    webAppDir = new File(this.tomcatHome + slash + "webapps" + slash + "hackystat");
//    if (webAppDir.exists()) {
//      return webAppDir;        
//    }
//    // Otherwise print out a warning that we don't know where it is.
//    System.out.println("Warning: hackystat webapp dir could not be located!");
    return null;
    }

  /**
   * This is the worst case scenario.  We can't find a hackystat.server.properties file anywhere, 
   * which can occur during client-side testing or within the sensorshell on the client side. 
   * The most important value set is the dataRootDir, which (in the absence of other information)
   * will be located inside this user's .hackystat directory. 
   */
  private void initializeProperties() {
//    //System.out.println("hackystat.server.properties not found. Using bogus defaults.");
//    File testingDir = new File(System.getProperty("user.home") + slash + ".hackystat" 
//        + slash + "testing");
//    this.dataRootDir = new File(testingDir, "hackystatdata");
//    // Let's experiment and see if we can avoid having to create this directory.
//    // this.dataRootDir.mkdirs();
//    this.tomcatHome = new File(testingDir, "tomcatHome").toString();
//    this.mailServer = "unknown.mail.server.com";
//    this.siteName = "Hackystat";
//    this.adminEmail = "UnknownAdmin" + this.getTestDomain();
//    this.hackystatHost = "http://localhost:8080/";
//    this.properties = new Properties();
  }
  
  /**
   * Prints the instance settings for debugging purposes.
   */
  @SuppressWarnings("unused")
  private void printProperties() {
//    System.out.println("=================================");
//    System.out.println("hackystatHost:" + this.hackystatHost);
//    System.out.println("hackystatHome:" + this.hackystatHome);
//    System.out.println("adminEmail:" + this.adminEmail);
//    System.out.println("siteName:" + this.siteName);
//    System.out.println("loggingLevel:" + this.loggingLevel);
//    System.out.println("tomcatHome:" + this.tomcatHome);
//    System.out.println("mailServer:" + this.mailServer);
//    System.out.println("servletContext:" + this.servletContext);
//    System.out.println("contextRoot:"    + this.contextRoot);
//    System.out.println("startupTime:" + this.startupTime);
//    System.out.println("dataRootDir:" + this.dataRootDir);
//    System.out.println("webAppDir:" + this.webAppDir);
//    System.out.println("properties:" + this.properties);
//    System.out.println("=================================");
  }

  /**
   * Initializes and returns a package-specific logger that writes to the command shell and to a
   * tomcat/logs/ file. Use this method to create custom debugging loggers for development. Use the
   * no-arg getLogger() method to get a system-wide logger for standard logging.
   *
   * @param logName  The name of the logger, such as "org.hackystat.core.kernel.sdt".
   * @return         The logger to be used.
   */
  public Logger getLogger(String logName) {
//    try {
//      // Now create a logger and disable default handlers.
//      Logger logger = Logger.getLogger(logName);
//      logger.setUseParentHandlers(false);
//
//      // Define a file handler that writes to the tomcat log file, making sure the dir exists.
//      // We only do this if the tomcat home directory actually exists.
//      if (new File(this.getTomcatHome()).exists()) {
//        File logDir = new File(this.getTomcatHome() + "/logs");
//        logDir.mkdirs();
//        String fileName = logDir + "/" + logName + "_log.%u.txt";
//        FileHandler fileHandler = new FileHandler(fileName, 500000, 1, true);
//        this.oneLineFormatter = new OneLineFormatter(true);
//        fileHandler.setFormatter(this.oneLineFormatter);
//        logger.addHandler(fileHandler);
//      }
//
//      // Define a console handler to also write the message to the console if in webserver.
//      ConsoleHandler consoleHandler = new ConsoleHandler();
//      consoleHandler.setFormatter(this.oneLineFormatter);
//      logger.addHandler(consoleHandler);
//      // Disable INFO-level output from the console logger if not in webserver.
//      if (!this.inWebServer()) {  
//        String clientsideLevel = this.getProperty("hackystat.clientside.logging.level", "WARNING");
//        consoleHandler.setLevel(Level.parse(clientsideLevel));
//      }
//      else {
//        this.setLoggingLevel(logger);
//      }
//      return logger;
//    }
//    catch (Exception e) {
//      System.out.println("Error initializing logger:\n" + StackTrace.toString(e));
//      return null;
//    }
    return null;
  }

  /**
   * Returns the general purpose hackystat logger, creating it if it couldn't be found.
   *
   * @return   The hackystat logger.
   */
  public Logger getLogger() {
//    if (this.logger == null) {
//      // Try to get it from the Log Manager.
//      // When reloading into a running Tomcat, the previous logger survives, so use it.
//      LogManager logManager = LogManager.getLogManager();
//      this.logger = logManager.getLogger("hackystat");
//      if (this.logger == null) {
//        // If we couldn't get it from the LogManager, then make one.
//        this.logger = this.getLogger("hackystat");
//      }
//      this.setLoggingLevel(this.logger);
//    }
//    return this.logger;
    return null;
  }
  
  /**
   * Converts the Throwable.getStackTrace to a String representation for logging.
   * @param throwable The Throwable exception.
   * @return A String containing the StackTrace. 
   * @deprecated Use org.hackystat.core.kernel.util.StackTrace.toString() instead.
   */
  public String convertStackTraceToString(Throwable throwable) {
    return null;
//    StringWriter stringWriter = new StringWriter();
//    throwable.printStackTrace(new PrintWriter(stringWriter));
//    return stringWriter.toString();    
  }

  /**
   * Gets the logging level to be used for the system-wide Hackystat logger. 
   * Defaults to INFO, but can be reset using the hackystat.logging.level property
   * inside the hackystat.server.properties file.
   * @param logger The logger whose logging level is to be set.
   */
  private void setLoggingLevel(Logger logger) {
//    if ((this.loggingLevel == null) || (this.loggingLevel.equals(""))) {
//      this.loggingLevel = "INFO";
//    }
//    Level newLevel = Level.parse(this.loggingLevel);
//    logger.setLevel(newLevel);
//    logger.getHandlers()[0].setLevel(newLevel);
//    // Only change the console logger if in web server.
//    if (this.inWebServer()) {  
//      logger.getHandlers()[1].setLevel(newLevel);
//    }
  }

  /**
   * Gets a Date instance indicating the date and time at which this server started up. Can be used
   * to do things like calculate uptime and is reported in the Admin Summary daily email.
   *
   * @return   The startupTime value.
   */
  public Date getStartupTime() {
    return null;
//    return this.startupTime;
  }

  /**
   * Gets a String indicating the date and time at which this server started up. Can be used to do
   * things like calculate uptime and is reported in the Admin Summary daily email.
   *
   * @return   The startupTime value.
   */
  public String getStartupTimeString() {
    return null;
//    return DateInfo.shortFormat(getStartupTime());
  }


  /**
   * Returns a string indicating the elapsed time since this server started up.
   *
   * @return   The uptime for this server.
   */
  public String getUptime() {
    return null;
//    Date startTime = this.getStartupTime();
//    Date currTime = new Date();
//    long elapsedTimeSeconds = (currTime.getTime() - startTime.getTime()) / 1000;
//    String uptime;
//    try {
//      uptime = ElapsedTimeFormat.toDayString(elapsedTimeSeconds);
//    }
//    catch (ElapsedTimeFormatException e) {
//      uptime = "Error computing uptime";
//    }
//    return uptime;
  }


  /**
   * Returns the File instance associated with the data directory for this SensorLog Type and user,
   * creating it if it does not yet exist.
   *
   * @param sdt     The sensor log type for this data directory.
   * @param dirKey  The directory key associated with this user.
   * @return        A file instance corresponding to the data directory for SensorLogs of this type.
   */
//  public File getDataDir(SensorDataType sdt, String dirKey) {
//    return null;
////    File dataDir = new File(this.getDataRootDir() + slash +
////        "users" + slash +
////        dirKey + slash +
////        "data" + slash +
////        sdt.getName());
////    if (!dataDir.exists()) {
////      dataDir.mkdirs();
////    }
////    return dataDir;
//  }

  /**
   * Returns the File instance associated with the tmp directory for this user, creating it if it
   * does not yet exist.
   *
   * @param user  The user whose tmp directory is to be created.
   * @return      A file instance corresponding to the tmp data directory for this user.
   */
  public File getTmpDir(User user) {
    return null;
//    File tmpDir = new File(this.getWebAppDir(true) + slash +
//        "tmp" + slash + user.getUserKey());
//    if (!tmpDir.exists()) {
//      tmpDir.mkdirs();
//    }
//    return tmpDir;
  }

  /**
   * Returns the directory root used to store data for this hackystat host specified via the system
   * property hackystat.data.dir. If that can't be found, creates (if necessary) and returns
   * user.home\hackystatdata as the root dir.
   *
   * @return   The data root directory.
   */
  public File getDataRootDir() {
    return this.dataRootDir;
  }
  
  /**
   * Gets the hackystat analysislogs/ directory, creating it if necessary. The analysislogs/ 
   * directory contains daily log files of command invocations.
   * 
   * @return The hackystat analysislogs/ directory.
   */
  public File getAnalysisLogsDataDir() {
    return null;
//    File analysisLogsDataDir = new File(this.getDataRootDir() + slash + "analysislogs");
//    if (!analysisLogsDataDir.exists()) {
//      analysisLogsDataDir.mkdirs();
//    }
//    return analysisLogsDataDir;
  }


  /**
   * Gets the hackystat users/ directory, creating it if necessary.
   *
   * @return   The hackystat users/ directory.
   */
  public File getUsersDir() {
    return null;
//    File usersDir = new File(this.getDataRootDir() + slash + "users");
//    if (!usersDir.exists()) {
//      usersDir.mkdirs();
//    }
//    return usersDir;
  }
  
  /**
   * Returns the directory associated with this User. 
   * @param user The User of interest.
   * @return The User's directory.
   */
  public File getUserDir(User user) {
    return new File(this.getUsersDir(), user.getUserKey());
  }

  /**
   * Gets the hackystat WEB-INF/ directory, creating it if necessary.
   *
   * @param create  True if the WEB-INF directory should be created.
   * @return        The hackystat WEB-INF/ directory.
   */
  public File getWebInfDir(boolean create) {
    return null;
//    File webInfDir = new File(this.getWebAppDir(create) + slash + "WEB-INF");
//    if (!webInfDir.exists()) {
//      webInfDir.mkdirs();
//    }
//    return webInfDir;
  }

  /**
   * Gets the hackystat webapp directory, creating it if create is true.
   *
   * @param create  True if the webapp directory should be created.
   * @return        The hackystat webapp directory.
   */
  public File getWebAppDir(boolean create) {
    return null;
//    if (this.webAppDir != null) {
//      return this.webAppDir;
//    }
//    // Otherwise we try these other things, for backward compatibility.
//    if (this.servletContext == null) {
//      webAppDir = new File(this.getTomcatHome() + slash + "webapps" + slash + this.contextRoot);
//    }
//    else {
//      webAppDir = new File(this.servletContext.getRealPath("/"));
//    }
//    if (create && !webAppDir.exists()) {
//      webAppDir.mkdirs();
//    }
//    return webAppDir;
  }


  /**
   * Pointer to the Hackystat research page at CSDL.
   *
   * @return   The url to CSDL.
   */
  public String getHackystatResearchUrl() {
    return "http://csdl.ics.hawaii.edu/Research/Hackystat/";
  }


  /**
   * Returns the directory to be used as the "root" for the Hackystat file system. This is normally
   * TomcatHome, but we will automatically set it to user.home if we can't find the Tomcat
   * directory. This enables us to do some testing with IDEs where we haven't passed the tomcat.home
   * environment variable into the IDE. User.home is always in system-properties.
   *
   * @return   The tomcatHome value
   */
  public String getTomcatHome() {
    return this.tomcatHome;
  }


  /**
   * Returns the SMTP mail server to be used for outgoing emails from the Hackystat server.
   *
   * @return   The mailServer value
   */
  public String getMailServer() {
    return this.mailServer;
  }

  /**
   * Returns the Hackystat site name value.
   *
   * @return   The siteName value
   */
  public String getSiteName() {
    return this.siteName;
  }


  /**
   * Returns the email address of the hackystat administrator to be used as the FROM, BCC, and
   * REPLY-TO fields of outgoing emails from Hackystat.
   *
   * @return   The adminEmail value
   */
  public String getAdminEmail() {
    return this.adminEmail;
  }
  
  /**
   * Returns the user key of the administrator as specified in hackystat.server.properties.
   * If not present in hackystat.server.properties, then returns null.  Note that this 
   * property value is only present for use by certain client-side test code.  Server-side
   * code should use UserManager.getInstance().getAdminUser().getUserEmail(). 
   *
   * @return   The adminUserKey property, or null if not present in hackystat.server.properties.
   */
  public String getAdminUserKeyProperty() {
    return this.adminUserKey;
  }  

  /**
   * Returns a hackystat host URL. An example is "http://hackystat.ics.hawaii.edu/". 
   * Analysis clients will want to use getHackystatHome() instead of this method, since
   * that includes the contextRoot. 
   *
   * @return   The hackystatHost URL.
   */
  public String getHackystatHost() {
    // Guarantee that hackystathost ends with a slash.
    if (!this.hackystatHost.endsWith("/")) {
      this.hackystatHost = this.hackystatHost + "/";
    }
    return this.hackystatHost;
  }
  
  /**
   * Returns the hackystat home URL. An example is "http://hackystat.ics.hawaii.edu/hackystat-uh/". 
   * @return The hackystatHome URL.
   */
  public String getHackystatHome() {
    return this.hackystatHome;
  }
  


  /**
   * Gets the userKey of the default test user ("testdataset").
   *
   * @return   The default test user ("testdataset").
   */
  public String getTestDataSetKey() {
    return "testdataset";
  }

  /**
   * Gets the userHomePage associated with the passed dirKey or userEmail.
   *
   * @param keyOrEmail  A string, either the directory key for a user or their email  address.
   * @return An URL corresponding to the user home page for that user on this server.
   */
  public String getUserHomePage(String keyOrEmail) {
    return null;
//    UserManager manager = UserManager.getInstance();
//    // Find the dirKey
//    String dirKey = (keyOrEmail.indexOf('@') != -1) ? manager.getUserKey(keyOrEmail) : keyOrEmail;
//    // Return the home page.
//    return getHackystatHome() + "controller?Key=" + dirKey;
  }


  /**
   * Return the test domain (currently "@hackystat.test").
   *
   * @return   The testDomain value
   */
  public String getTestDomain() {
    return "@hackystat.test";
  }


  /**
   * Return the current version number if found.
   *
   * @return   The version number, or "Unknown" if could not be determined.
   */
  public String getVersion() {
    if (this.version == null) {
      try {
        Package thisPackage = Class.forName("org.hackystat.core.kernel.admin.ServerProperties")
          .getPackage();
        this.version = thisPackage.getSpecificationVersion();
      } 
      catch (Exception e) {
        // Do nothing.
      }
    }
    return (this.version == null) ? "unknown" : this.version;
  }


  /**
   * Return the current build time.
   *
   * @return   The buildtime value, or "Unknown" if could not be determined.
   */
  public String getBuildtime() {
    if (this.buildtime == null) {
      try {
        Package thisPackage = Class.forName("org.hackystat.core.kernel.admin.ServerProperties")
                              .getPackage();
        this.buildtime = thisPackage.getImplementationVersion();
      }
      catch (Exception e) {
        // Do nothing.
      }
    }
    return (this.buildtime == null) ? "unknown" : this.buildtime;
  }

  /**
   * Return the hour at which daily alert emails should be sent.
   * 
   * @return The hour.
   */
  public int getDailyAlertHour() {
    return this.dailyAlertHour;
  }
  
  /**
   * Returns true if the passed subdirectory and file exist in the Tomcat webapps/hackystat dir.
   *
   * @param subdirName  A string webapps/hackystat subdirectory name.
   * @param fileName    The file name.
   * @return            True if the file exists.
   */
  public boolean fileExists(String subdirName, String fileName) {
    return getFile(subdirName, fileName).exists();
  }

  /**
   * Returns true if the passed file exists in the Tomcat webapps/hackystat directory.
   *
   * @param fileName  The file name.
   * @return          True if the file exists.
   */
  public boolean fileExists(String fileName) {
    return getFile(fileName).exists();
  }

  /**
   * Gets the webapps/hackystat File instance in subdirName with name fileName.
   *
   * @param subdirName  The subdirectory name within webapps/hackystat.
   * @param fileName    The file name.
   * @return            A File instance (that may or may not exist).
   */
  public File getFile(String subdirName, String fileName) {
    return new File(this.getWebAppDir(true) + slash + subdirName + slash + fileName);
  }

  /**
   * Gets the webapps/hackystat File instance with name fileName.
   *
   * @param fileName  The file name.
   * @return          A File instance (that may or may not exist).
   */
  public File getFile(String fileName) {
    return new File(this.getWebAppDir(true) + slash + fileName);
  }


  /**
   * Returns the contents of the passed File as a String. Adapted with thanks from Java Cookbook,
   * Ian Darwin, OReilly, 2001.
   *
   * @param file             a <code>File</code> value
   * @return                 a <code>String</code> value
   * @exception IOException  If the file cannot be read.
   */
  public String getFileContents(File file) throws IOException {
    FileReader reader = null;
    try {
      reader = new FileReader(file);
      StringBuffer buff = new StringBuffer();
      char[] b = new char[8192];
      int n;
      while ((n = reader.read(b)) > 0) {
        buff.append(b, 0, n);
      }
      return buff.toString();
    }
    finally {
      reader.close();
    }
  }


//  /**
//   * Indicates whether or not this ServerProperties instance was created inside a web server or not.
//   * This boolean can be used to determine if the code is running in a web server or in a
//   * client-side application like SensorShell, which is useful to SdtManager, for example.
//   * @return True if running inside a web server.
//   */
//  public boolean inWebServer() {
//    return (this.servletContext != null);
//  }
  
  /**
   * Returns the servlet context for this web application.
   * @return The servlet context.
   */
//  public ServletContext getServletContext() {
//    return this.servletContext;
//  }
//  
  /**
   * Returns true if command logging is enabled.
   * To disable it, you have to set hackystat.analysis.logging=false in hackystat.server.properties.
   * 
   * @return true If command logging is enabled.
   */
  public boolean isAnalysisLoggingEnabled() {
    return this.enableAnalysisLogging;
  }
  
  /**
   * Determines whether the Hackystat server is a test installation or production installation.
   * @return True if it is a test installation; false if it is a production installation.
   */
  public boolean isTestInstallation() {
    return this.getProperty("hackystat.test.installation", false);
  }

  /**
   * Gets the desired Hackystat system property or default value if there is none.
   * 
   * @param name Property name to be retrieved from hackystat.server.properties.
   * @param defaultValue Default property value if the property is not defined. 
   * @return Property value or default if it was not set in the hackystat.server.properties file.
   */
  public String getProperty(String name, String defaultValue) {
    // Return the default value if no property is set.
    if (this.properties == null) {
      return defaultValue;
    }
    return this.properties.getProperty(name, defaultValue);
  }  

  /**
   * Returns a boolean representing the property's value, which should be set to
   * either true or false.  If the property does not exist or the property value is not true
   * or false, then the default value will be returned.
   * 
   * @param name Property name to be retrieved from hackystat.server.properties.
   * @param defaultBoolean Default boolean to be returned if the property does not exist or 
   * is not set to true or false.
   * @return A boolean representing the property value.
   */
  public boolean getProperty(String name, boolean defaultBoolean) {
    // Return default if hackystat.server.properties is not found.
    if (this.properties == null) {
      return defaultBoolean;
    }
    String property = this.properties.getProperty(name);
    // Return default if property cannot be found in hackystat.server.properties.
    if (property == null) {
      return defaultBoolean;
    }
    property = property.toLowerCase().trim();
    // Otherwise return property value as boolean or default if it's not true or false.
    if ("true".equals(property)) {
      return true;
    }
    else if ("false".equals(property)) {
      return false;
    }
    else {
      return defaultBoolean;
    }
  }  
  
  /**
   * Updates the System properties object with the contents of the passed Hackystat
   * properties instance.  This method is declared static so that it can be invoked
   * from both SensorProperties and ServerProperties.  The hackystat properties added from
   * SensorProperties were found in sensor.properties. The hackystat properties added from
   * ServerProperties were found in hackystat.server.properties.  
   * This method is package-private for access by SensorProperties.
   * @param hackyProperties A Properties instance containing hackystat properties.
   */
  static void addToSystemProperties(Properties hackyProperties) {
    Properties systemProperties = System.getProperties();
    systemProperties.putAll(hackyProperties);
    System.setProperties(systemProperties);
  }
}


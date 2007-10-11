package org.hackystat.telemetry.analyzer.util.project.cache;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

//import org.hackystat.core.kernel.admin.ServerProperties;
//import org.hackystat.core.kernel.util.ExtensionFileFilter;

/**
 * Provides access to the known names and classes associated with DailyAnalyses. Accomplished by
 * reading in the DailyAnalyses configuration file (DailyAnalyses.xml). Used by the
 * DailyAnalysisSelector and the DailyDiary.
 *
 * @author Philip Johnson
 */
public class DailyProjectDataManager {
  /** The singleton DailyProjectDataManager instance */
  private static DailyProjectDataManager theInstance;
  /** Maps the column name to column class */
  private Map<String, Class> dailyProjectDataMap;
  /** List of analysis name */
  private Set<String> nameList;
  /** Logger object of the server */
  //private Logger logger = ServerProperties.getInstance().getLogger();

  /**
   * Gets the singleton DailyProjectDataManager instance.
   *
   * @return   The cached or new DailyProjectDataManager instance
   */
  public static DailyProjectDataManager getInstance() {
    if (theInstance == null) {
      theInstance = new DailyProjectDataManager();
    }
    return theInstance;
  }

  /** Create an instance of DailyProjectDataManager. */
  private DailyProjectDataManager() {
//    this.dailyProjectDataMap = new HashMap<String, Class>();
//    this.nameList = new TreeSet<String>();
//    String slash = File.separator;
//
//    String dailyProjectDataDirName = ServerProperties.getInstance().getWebInfDir(false) + slash + 
//      "dailyprojectdata";
//    File dailyProjectDataDir = new File(dailyProjectDataDirName);
//    if (!dailyProjectDataDir.isDirectory()) {
//      logger.severe(dailyProjectDataDirName + " is not found. Cannot load the analysis.");
//      return;
//    }
//
//    FileFilter dailyProjectDataFileFilter = new ExtensionFileFilter(".xml");
//    File[] dailyProjectDataFiles = dailyProjectDataDir.listFiles(dailyProjectDataFileFilter);
//    for (int i = 0; i < dailyProjectDataFiles.length; i++) {
//      try {
//        loadDailyProjectData(new FileInputStream(dailyProjectDataFiles[i]));
//      }
//      catch (Exception e) {
//        logger.severe("Error processing " + dailyProjectDataFiles[i].getName() + "\n" + e);
//      }
//    }
  }

  /**
   * Loads the daily project data XML definition files.
   *
   * @param dailyProjectDataStream  The input stream
   * @exception JDOMException    If the XML file is wrongly formatted.
   * @exception IOException    If the XML file is wrongly formatted.
   */
  private void loadDailyProjectData(InputStream dailyProjectDataStream) 
    throws IOException, JDOMException {
//    // Read in the XML configuration file
//    SAXBuilder builder = new SAXBuilder();
//    Document doc = builder.build(dailyProjectDataStream);
//    Element rootElement = doc.getRootElement();
//
//    List analysisList = rootElement.getChildren("analysis");
//
//    for (Iterator i = analysisList.iterator(); i.hasNext();) {
//      Element analysis = (Element) i.next();
//      String analysisName = analysis.getAttributeValue("name");
//      String analysisClass = analysis.getAttributeValue("class");
//      String enabled = analysis.getAttributeValue("enable");
//      Class theClass = null;
//      if (!"true".equals(enabled)) {
//         logger.fine("Defining daily project data: " + analysisName + " (disabled)");
//         continue;
//      }
//      if (this.dailyProjectDataMap.containsKey(analysisName)) {
//        logger.warning("Defining daily project data: " + analysisName + " (Error: duplicate.)");
//        continue;
//      }
//
//      try {
//        theClass = Class.forName(analysisClass);
//      }
//      catch (ClassNotFoundException e) {
//        logger.warning("Defining daily project data: " + analysisName
//                       + " (Error: class " + analysisClass + " not found.)");
//        continue;
//      }
//
//      this.dailyProjectDataMap.put(analysisName, theClass);
//      this.nameList.add(analysisName);
//      logger.fine("Defining daily project data: " + analysisName);
//    }
  }

  /**
   * Returns the list of the daily project data analysis names.
   *
   * @return   Analysis name list
   */
  public Set<String> getDailyProjectDataNameList() {
    return this.nameList;
  }
  
  /**
   * Returns true if the passed name is a DailyProjectData name.
   * @param name The name to be verified as an analysis name.
   * @return True if it names a Daily Analysis.
   */
  public boolean isDailyProjectDataName(String name) {
    return this.nameList.contains(name);
  }

  /**
   * Returns the class instance given the name. 
   *
   * @param analysisName  The name of the daily analysis
   * @return The class instance associated with this name.
   */
  public Class<?> getDailyProjectDataClass(String analysisName) {
    
    return (Class<?>) this.dailyProjectDataMap.get(analysisName);
  }

  /**
   * Returns a collection of Class instances consisting of all available DailyProjectData classes.
   * @return A collection of currently defined DailyProjectData subclasses. 
   */
  public Collection<Class> getClasses() {
    return (Collection<Class>) this.dailyProjectDataMap.values();
  }  
}

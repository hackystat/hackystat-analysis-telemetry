package org.hackystat.telemetry.analyzer.reducer;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.util.ServerProperties;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.telemetry.analyzer.util.selector.interval.Interval;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides a singleton manager for telemetry reducers. 
 * <p>
 * V8 Notes: Reducers must be read in using JAXB.
 * 
 * @author Qin ZHANG
 * @version $Id$
 */
public class TelemetryReducerManager {

  private static TelemetryReducerManager theInstance = new TelemetryReducerManager();
  private Map<String, TelemetryReducerInfo> reducerMap = 
    new TreeMap<String, TelemetryReducerInfo>();
  private Logger logger;

  /**
   * Gets the singleton instance of this class.
   * 
   * @return An instance of this class.
   */
  public static TelemetryReducerManager getInstance() {
    return theInstance;
  }

  /**
   * Initializes telemetry reducers. If a reducer definition file cannot be parsed or a 
   * telemetry reducer cannot be instantiated, no exception will be raised. 
   * Instead errors will be logged through Hackystat standard logging mechanism.
   */
  private TelemetryReducerManager() {
    this.logger = ServerProperties.getInstance().getLogger();
    
    // find all "**telemetry.def.xml" files, and call processSingleFile() on each file.
    File webInfDir = ServerProperties.getInstance().getWebInfDir(false);
    File telemetryDir = new File(webInfDir, "telemetry");
    if (!telemetryDir.isDirectory()) {
      this.logger.fine("[TelemetryReducerManager] Reducer definition directory " 
          + telemetryDir.getAbsolutePath() + " does not exists. No telemetry reducer defined.");
    }
    else {
      File[] files = telemetryDir.listFiles();
      for (int i = 0; i < files.length; i++) {
        File file = files[i];
        String fileName = file.getName();
        if (fileName.endsWith("telemetry.def.xml")) {
          this.processSingleFile(file);
        }
      }
    }
  }

  /**
   * Processes a single telemetry reducers definition file.
   * 
   * @param xmlFile The xml definition file.
   */
  private void processSingleFile(File xmlFile) {
    this.logger.fine("[TelemetryReducerManager] Processing " + xmlFile.getAbsolutePath());
    try {
      Element root = new SAXBuilder().build(xmlFile).getRootElement();
      List reducers = root.getChildren("Reducer");

      for (Iterator i = reducers.iterator(); i.hasNext();) {
        Element reducerElement = (Element) i.next();
        String name = reducerElement.getAttributeValue("name");
        String className = reducerElement.getAttributeValue("class");
        String reducerDescription = reducerElement.getAttributeValue("reducerDescription");
        String optionDescription = reducerElement.getAttributeValue("optionDescription");

        if (name == null) {
          this.logger.severe("[TelemetryReducerManager] One reducer definition is ignored, " +
              "because attribute 'name' is not defined.");
        }
        else if (this.reducerMap.containsKey(name)) {
          this.logger.severe("[TelemetryReducerManager] Duplicated definition '" 
              + name + "' ignored.");
        }
        else {
          // instantiate the class
          if (className == null) {
            this.logger.severe("[TelemetryReducerManager] Reducer class not defined for " 
                + name + ". Entry ignored.");
          }
          else {
            try {
              Class<?> clazz = Class.forName(className);
              TelemetryReducer reducer = (TelemetryReducer) clazz.newInstance();
              TelemetryReducerInfo reducerInfo = new TelemetryReducerInfo(name, reducer,
                  reducerDescription, optionDescription);
              this.reducerMap.put(name, reducerInfo);
            }
            catch (Exception classEx) {
              this.logger.severe("[TelemetryReducerManager] Unable to instantiate class " 
                  + className + ". Entry ignored. " + classEx.getMessage());
              continue;
            }
          }
        } // end else
      } // end for loop
    }
    catch (Exception ex) {
      this.logger.severe("[TelemetryReducerManager] Severe error: " + ex.getMessage());
    }
  }

  /**
   * Invokes the telemetry reducer to perform computations and generate a TelemetryStreamCollection.
   * 
   * @param reducerName The name of the reducer to be invoked.
   * @param project The project which defines the scope of metrics to be used in the computation.
   * @param interval The time interval.
   * @param parameters Parameters passed to reducer implementation. In case a reducer does not
   *        need any parameter, either null or an empty array may be passed.
   * @return The resulting instance of <code>TelemetryStreamCollection</code>. 
   * @throws TelemetryReducerException If anything is wrong.
   */
  public TelemetryStreamCollection compute(String reducerName, Project project,
      Interval interval, String[] parameters) throws TelemetryReducerException {
    TelemetryReducerInfo reducerInfo = (TelemetryReducerInfo) this.reducerMap.get(reducerName);
    if (reducerInfo == null) {
      throw new TelemetryReducerException("Telemetry reducer " + reducerName + " not defined.");
    }
    return reducerInfo.getReducer().compute(project, interval, parameters);
  }

  /**
   * Determines whether a particular telemetry reducer is available.
   * 
   * @param reducerName Telemetry reducer name.
   * @return True if the specified telemetry reducer is available.
   */
  public boolean isReducerExist(String reducerName) {
    return this.reducerMap.containsKey(reducerName);
  }

  /**
   * Gets telemetry reducer information by name.
   * 
   * @param reducerName The name of the reducer.
   * @return The telemetry reducer information, or null if the reducer is not defined.
   */
  public TelemetryReducerInfo getReducerInfo(String reducerName) {
    return (TelemetryReducerInfo) this.reducerMap.get(reducerName);
  }

  /**
   * Gets information about all defined telemetry reducers.
   * 
   * @return A collection of <code>TelemetryReducerInfo</code> instances.
   */
  public Collection<TelemetryReducerInfo> getAllReducerInfo() {
    return this.reducerMap.values();
  }
}
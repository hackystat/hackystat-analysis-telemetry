package org.hackystat.telemetry.analyzer.reducer;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.reducer.jaxb.ReducerDefinition;
import org.hackystat.telemetry.analyzer.reducer.jaxb.ReducerDefinitions;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.utilities.logger.HackystatLogger;
import org.hackystat.utilities.stacktrace.StackTrace;
import org.hackystat.utilities.time.interval.Interval;

/**
 * Provides a singleton manager for telemetry reducers. 
 * 
 * @author Qin ZHANG, Philip Johnson
 */
public class TelemetryReducerManager {

  /** The singleton */
  private static TelemetryReducerManager theInstance = new TelemetryReducerManager();
  /** Maps reducer names to a data structure with information about them. */ 
  private Map<String, TelemetryReducerInfo> reducerMap = 
    new TreeMap<String, TelemetryReducerInfo>();
  private Logger logger;
  
  private ReducerDefinitions definitions;

  /**
   * Gets the singleton instance of this class.
   * 
   * @return An instance of this class.
   */
  public static TelemetryReducerManager getInstance() {
    return theInstance;
  }

  /**
   * Finds and defines built-in telemetry reducer functions. 
   * If the reducer.definitions.xml file cannot be found, or the file specifies
   * a class that cannot be instantiated, then logging messages will the displayed but no
   * errors will be thrown.
   */
  private TelemetryReducerManager() {
    this.logger = HackystatLogger.getLogger("org.hackystat.telemetry");
    
    try {
      this.logger.info("Loading built-in telemetry reduction function definitions.");
      //InputStream defStream = getClass().getResourceAsStream("impl/reducer.definitions.xml");
      InputStream defStream = 
        TelemetryReducerManager.class.getResourceAsStream("impl/reducer.definitions.xml");
      JAXBContext jaxbContext = JAXBContext
      .newInstance(org.hackystat.telemetry.analyzer.reducer.jaxb.ObjectFactory.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      this.definitions = (ReducerDefinitions) unmarshaller.unmarshal(defStream);
    } 
    catch (Exception e) {
      this.logger.severe("Could not find built-in telemetry reduction function definitions! " 
          + StackTrace.toString(e));
    }

    for (ReducerDefinition definition : this.definitions.getReducerDefinition()) {
      String name = definition.getName();
      try {
        this.logger.info("Defining built-in telemetry reduction function " + name);
        Class<?> clazz = Class.forName(definition.getClassName());
        TelemetryReducer reducer = (TelemetryReducer) clazz.newInstance();
        TelemetryReducerInfo reducerInfo =  new TelemetryReducerInfo(reducer, definition);
        this.reducerMap.put(name, reducerInfo);
      }
      catch (Exception classEx) {
        this.logger.severe("Unable to define " 
            + definition.getClassName() + ". Entry ignored. " + classEx.getMessage());
        continue;
      }
    }
  }

 
  /**
   * Invokes the telemetry reducer to perform computations and generate a TelemetryStreamCollection.
   * 
   * @param reducerName The name of the reducer to be invoked.
   * @param project The project which defines the scope of metrics to be used in the computation.
   * @param user The user.
   * @param password The password.
   * @param interval The time interval.
   * @param parameters Parameters passed to reducer implementation. In case a reducer does not
   *        need any parameter, either null or an empty array may be passed.
   * @return The resulting instance of <code>TelemetryStreamCollection</code>. 
   * @throws TelemetryReducerException If anything is wrong.
   */
  public TelemetryStreamCollection compute(String reducerName, String user, String password,
      Project project, Interval interval, String[] parameters) throws TelemetryReducerException {
    TelemetryReducerInfo reducerInfo = this.reducerMap.get(reducerName);
    if (reducerInfo == null) {
      throw new TelemetryReducerException("Telemetry reducer " + reducerName + " not defined.");
    }
    return reducerInfo.getReducer().compute(project, user, password, interval, parameters);
  }

  /**
   * Determines whether a particular telemetry reducer is defined.
   * 
   * @param reducerName Telemetry reducer name.
   * @return True if the specified telemetry reducer is defined.
   */
  public boolean isReducer(String reducerName) {
    return this.reducerMap.containsKey(reducerName);
  }

  /**
   * Gets telemetry reducer information by name.
   * 
   * @param reducerName The name of the reducer.
   * @return The telemetry reducer information, or null if the reducer is not defined.
   */
  public TelemetryReducerInfo getReducerInfo(String reducerName) {
    return this.reducerMap.get(reducerName);
  }

  /**
   * Returns information about all defined TelemetryReducers. 
   * 
   * @return A collection of <code>TelemetryReducerInfo</code> instances.
   */
  public Collection<TelemetryReducerInfo> getAllReducerInfo() {
    return this.reducerMap.values();
  }
}
package org.hackystat.telemetry.analyzer.function;

import java.io.InputStream;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.hackystat.telemetry.analyzer.function.jaxb.FunctionDefinition;
import org.hackystat.telemetry.analyzer.function.jaxb.FunctionDefinitions;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.utilities.logger.HackystatLogger;
import org.hackystat.utilities.stacktrace.StackTrace;

/**
 * Implements a global singleton for managing telemetry function instances. It serves two purposes:
 * <ul>
 *   <li>Provides a central repository for all available telemetry functions.
 *   <li>Provides a interface for invoking those functions.
 * </ul>
 * 
 * All function names are case-insensitive.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TelemetryFunctionManager {

  /**
   * Built-in functions for telemetry language and evaluator.
   * Key is function name (lower case), value is TelemetryFunctionInfo instance. 
   */
  private TreeMap<String, TelemetryFunctionInfo> functionMap = 
    new TreeMap<String, TelemetryFunctionInfo>();
  
  /** The global instance of this manager. */
  private static TelemetryFunctionManager theInstance = new TelemetryFunctionManager();
  
  /** The logger. */
  private Logger logger;
  
  /** The predefined function definitions. */
  private FunctionDefinitions definitions;
  
  /**
   * Gets the global instance.
   * 
   * @return The global instance.
   */
  public static TelemetryFunctionManager getInstance() {
    return theInstance;
  }

  /**
   * Defines "built-in" telemetry functions. 
   */
  private TelemetryFunctionManager() {
    this.logger = HackystatLogger.getLogger("org.hackystat.telemetry");
    
    try {
      this.logger.info("Loading built-in telemetry function definitions.");
      InputStream defStream = getClass().getResourceAsStream("impl/function.definitions.xml");
      JAXBContext jaxbContext = JAXBContext
      .newInstance(org.hackystat.telemetry.analyzer.function.jaxb.ObjectFactory.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      this.definitions = (FunctionDefinitions) unmarshaller.unmarshal(defStream);
    } 
    catch (Exception e) {
      this.logger.severe("Could not find built-in telemetry function definitions! " 
          + StackTrace.toString(e));
    }

    for (FunctionDefinition definition : this.definitions.getFunctionDefinition()) {
      String name = definition.getName();
      try {
        this.logger.info("Defining built-in telemetry function " + name);
        Class<?> clazz = Class.forName(definition.getClassName());
        TelemetryFunction telemetryFunc = (TelemetryFunction) clazz.newInstance();
        TelemetryFunctionInfo funcInfo =  new TelemetryFunctionInfo(telemetryFunc, definition);
        this.functionMap.put(name.toLowerCase(), funcInfo);
      }
      catch (Exception classEx) {
        this.logger.severe("Unable to define " 
            + definition.getClassName() + ". Entry ignored. " + classEx.getMessage());
        continue;
      }
    }
  }
  
  /**
   * Determines whether a particular telemetry function is available.
   * 
   * @param functionName Telemetry function name.
   * @return True if the specified telemetry function is available.
   */
  public boolean isFunction(String functionName) {
    return this.functionMap.containsKey(functionName.toLowerCase());
  }

  /**
   * Gets telemetry function information by name.
   * 
   * @param functionName The name of the function.
   * @return The telemetry function information, or null if the function is not defined.
   */
  public TelemetryFunctionInfo getFunctionInfo(String functionName) {
    return this.functionMap.get(functionName.toLowerCase());
  }
  
  /**
   * Invokes telemetry function to perform computation.
   * 
   * @param functionName The name of the telemetry function.
   * @param parameters An array of objects of type either <code>String</code>,
   *        <code>Number</code>, and/or <code>TelemetryStreamCollection</code>. 
   * 
   * @return Either an instance of <code>Number</code> or <code>TelemetryStreamCollection</code>. 
   * 
   * @throws TelemetryFunctionException If anything is wrong.
   */
  public Object compute(String functionName, Object[] parameters) 
      throws TelemetryFunctionException {

    TelemetryFunctionInfo functionInfo = this.getFunctionInfo(functionName.toLowerCase());
    if (functionInfo == null) {
      throw new TelemetryFunctionException(
          "Telemetry function " + functionName + " does not exist.");
    }
    
    //check input types
    for (int i = 0; i < parameters.length; i++) {
      Object object = parameters[i];
      if (! (object instanceof String || object instanceof Number
           || object instanceof TelemetryStreamCollection)) {
        throw new TelemetryFunctionException("Telemetry function parameter should be of type "
            + "either 'String', 'Number', and/or 'TelemetryStreamCollection'.");
      }
    }
    //delegate computation
    Object result = functionInfo.getFunction().compute(parameters);
    //check output types
    if (! (result instanceof Number || result instanceof TelemetryStreamCollection)) {
      throw new TelemetryFunctionException("Telemetry function " + functionName 
          + " implementation error. It returns a result of unexpected type.");
    }
    return result;
  }
}

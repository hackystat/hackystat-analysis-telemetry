package org.hackystat.telemetry.analyzer.function;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.hackystat.telemetry.analyzer.function.impl.AddFunction;
import org.hackystat.telemetry.analyzer.function.impl.DivFunction;
import org.hackystat.telemetry.analyzer.function.impl.IdempotentFunction;
import org.hackystat.telemetry.analyzer.function.impl.MulFunction;
import org.hackystat.telemetry.analyzer.function.impl.SubFunction;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.util.ServerProperties;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Implements a global singleton for managing telemetry function instances. It serves two purposes:
 * <ul>
 *   <li>Provides a central repository for all available telemetry functions.
 *   <li>Provides a interface for invoking those functions.
 * </ul>
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TelemetryFunctionManager {

  /**
   * Built-in functions essential to the functioning of telemetry language and evaluator.
   * Key is function name (lower case), value is TelemetryFunctionInfo instance. 
   */
  private TreeMap<String, TelemetryFunctionInfo> essentialFunctionInfoMap = 
    new TreeMap<String, TelemetryFunctionInfo>();
  
  /**
   * Additional custom-supplied functions through telemetry function extension point.
   * Key is function name (lower case), value is TelemetryFunctionInfo instance. 
   */
  private TreeMap<String, TelemetryFunctionInfo> additionalFunctionInfoMap = 
    new TreeMap<String, TelemetryFunctionInfo>();
  
  /** The global instance of this manager. */
  private static TelemetryFunctionManager theInstance = new TelemetryFunctionManager();
  
  /** The logger. */
  private Logger logger;
  
  /**
   * Gets the global instance.
   * 
   * @return The global instance.
   */
  public static TelemetryFunctionManager getInstance() {
    return theInstance;
  }

  /**
   * Private no-arg constructor to enforce singleton pattern.
   */
  private TelemetryFunctionManager() {
    this.logger = ServerProperties.getInstance().getLogger();
    
    //Register built-in functions.
    //Note that the names of these essential functions are hardwired in telemetry language parser.
    String description = "Telemetry infrastructure. Internal use only.";
    this.essentialFunctionInfoMap.put("Add", 
        new TelemetryFunctionInfo(new AddFunction("Add"), description, ""));
    this.essentialFunctionInfoMap.put("Sub", 
        new TelemetryFunctionInfo(new SubFunction("Sub"), description, ""));
    this.essentialFunctionInfoMap.put("Mul", 
        new TelemetryFunctionInfo(new MulFunction("Mul"), description, ""));
    this.essentialFunctionInfoMap.put("Div", 
        new TelemetryFunctionInfo(new DivFunction("Div"), description, ""));
    this.essentialFunctionInfoMap.put("Idempotent", 
        new TelemetryFunctionInfo(new IdempotentFunction("Idempotent"), description, ""));
    
    //Register custom functions.
    //Find all "**telemetry.def.xml" files, and call processSingleFile() on each file.
    File webInfDir = ServerProperties.getInstance().getWebInfDir(false);
    File telemetryDir = new File(webInfDir, "telemetry");
    if (telemetryDir.isDirectory()) {
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
   * @param telemetryDefFile The telemetry xml definition file.
   */
  private void processSingleFile(File telemetryDefFile) {
    this.logger.fine("[TelemetryFunctionManager] Processing " + telemetryDefFile.getAbsolutePath());
    try {
      Element root = new SAXBuilder().build(telemetryDefFile).getRootElement();
      List functions = root.getChildren("function");

      for (Iterator i = functions.iterator(); i.hasNext();) {
        Element functionElement = (Element) i.next();
        String name = functionElement.getAttributeValue("name");
        String className = functionElement.getAttributeValue("class");
        String functionDescription = functionElement.getAttributeValue("description");
        String parameterDescription = functionElement.getAttributeValue("parameters");

        if (name == null) {
          this.logger.severe("[TelemetryFunctionManager] One function definition is ignored, " +
              "because attribute 'name' is not defined.");
        }
        else if (this.essentialFunctionInfoMap.containsKey(name) 
              || this.additionalFunctionInfoMap.containsKey(name)) {
          this.logger.severe("[TelemetryFunctionManager] Duplicated function definition '" 
              + name + "' ignored.");
        }
        else {
          // instantiate the class
          if (className == null) {
            this.logger.severe("[TelemetryFunctionManager] Function class not defined for " 
                + name + ". Entry ignored.");
          }
          else {
            try {
              Class<?> clazz = Class.forName(className);
              Constructor ctor = clazz.getConstructor(new Class[]{String.class});
              TelemetryFunction function = (TelemetryFunction) ctor.newInstance(new Object[]{name});
              TelemetryFunctionInfo functionInfo = new TelemetryFunctionInfo(function,
                  functionDescription, parameterDescription);
              this.additionalFunctionInfoMap.put(name, functionInfo);
            }
            catch (Exception classEx) {
              this.logger.severe("[TelemetryFunctionManager] Unable to instantiate class " 
                  + className + ". Entry ignored. ");
              classEx.printStackTrace();
            }
          }
        } // end else
      } // end for loop
    }
    catch (Exception ex) {
      this.logger.severe("[TelemetryFunctionManager] Severe error: " + ex.getMessage());
    }
  }
    
  /**
   * Determines whether a particular telemetry function is available.
   * 
   * @param functionName Telemetry function name.
   * @return True if the specified telemetry function is available.
   */
  public boolean isFunctionExist(String functionName) {
    return this.essentialFunctionInfoMap.containsKey(functionName)
        || this.additionalFunctionInfoMap.containsKey(functionName);
  }

  /**
   * Gets telemetry function information by name.
   * 
   * @param functionName The name of the function.
   * @return The telemetry function information, or null if the function is not defined.
   */
  public TelemetryFunctionInfo getFunctionInfo(String functionName) {
    TelemetryFunctionInfo functionInfo
        = (TelemetryFunctionInfo) this.essentialFunctionInfoMap.get(functionName);
    if (functionInfo == null) {
      functionInfo = (TelemetryFunctionInfo) this.additionalFunctionInfoMap.get(functionName);
    }
    return functionInfo;
  }

  /**
   * Gets information about all telemetry function defined through telemetry function extension
   * point. Note that built-in, "essential" internal telemetry functions are not returned.
   * 
   * @return A collection of <code>TelemetryFunctionInfo</code> instances.
   */
  public Collection<TelemetryFunctionInfo> getAllCustomFunctionInfo() {
    return this.additionalFunctionInfoMap.values();
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

    TelemetryFunctionInfo functionInfo = this.getFunctionInfo(functionName);
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
          + " implementation error. It returns result of unexpected type.");
    }
    return result;
  }
}

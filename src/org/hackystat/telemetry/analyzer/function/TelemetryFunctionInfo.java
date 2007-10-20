package org.hackystat.telemetry.analyzer.function;

import org.hackystat.telemetry.analyzer.function.jaxb.FunctionDefinition;
import org.hackystat.telemetry.analyzer.function.jaxb.Parameters;

/**
 * Provides information about a specific implementation of a TelemetryFunction. 
 * 
 * @author (Cedric) Qin Zhang, Philip Johnson
 */
public class TelemetryFunctionInfo {

  private TelemetryFunction function;
  private FunctionDefinition definition;

  /**
   * Constructs this instance.
   * 
   * @param function The concrete instance of telemetry function.
   * @param definition The definition of this function, from the JAXB XML class.
   */
  TelemetryFunctionInfo(TelemetryFunction function, FunctionDefinition definition) {
    this.function = function;
    this.definition = definition;
  }

  /**
   * Gets the name of the telemetry function.
   * 
   * @return The name.
   */
  public String getName() {
    return this.function.getName();
  }

  /**
   * Gets the instance of the telemetry function.
   * 
   * @return The instance.
   */
  public TelemetryFunction getFunction() {
    return this.function;
  }

  /**
   * Gets the description of this function.
   * 
   * @return The description of the function.
   */
  public String getFunctionDescription() {
    return this.definition.getDescription();
  }

  /**
   * Gets the description of the parameters this function takes if any.
   * 
   * @return The description of the parameters.
   */
  public Parameters getParameterDescription() {
    return this.definition.getParameters();
  }
}
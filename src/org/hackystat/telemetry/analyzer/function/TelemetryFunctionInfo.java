package org.hackystat.telemetry.analyzer.function;

/**
 * Provides information about a specific implementation of <code>TelemetryFunction</code>, 
 * including its name, the function instance, and its description.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TelemetryFunctionInfo {

  private TelemetryFunction function;
  private String functionDescription;
  private String parameterDescription;

  /**
   * Constructs this instance.
   * 
   * @param function The concrete instance of telemetry function.
   * @param functionDescription The description of the function.
   * @param parameterDescription The description of the parameters the function takes.
   */
  TelemetryFunctionInfo(TelemetryFunction function, String functionDescription,
      String parameterDescription) {
    this.function = function;
    this.functionDescription = functionDescription;
    this.parameterDescription = parameterDescription;
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
   * Gets the instance of the telemtry function.
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
    return this.functionDescription;
  }

  /**
   * Gets the description of the parameters this function takes if any.
   * 
   * @return The description of the parameters.
   */
  public String getParameterDescription() {
    return this.parameterDescription;
  }
}
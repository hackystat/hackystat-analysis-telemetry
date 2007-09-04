package org.hackystat.telemetry.analyzer.reducer;

/**
 * Provides information about a particular implementation of 
 * <code>TelemetryReducer</code>.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TelemetryReducerInfo {

  private String name;
  private TelemetryReducer reducer;
  private String reducerDescription;
  private String optionDescription;

  /**
   * Constructs this instance.
   * 
   * @param name The name of the telemtry reducer.
   * @param reducer The concrete instance of telemetry reducer.
   * @param reducerDescription The description of the reducer.
   * @param optionDescription The description of the parameters the reducer takes.
   */
  TelemetryReducerInfo(String name, TelemetryReducer reducer, String reducerDescription,
      String optionDescription) {
    this.name = name;
    this.reducer = reducer;
    this.reducerDescription = reducerDescription;
    this.optionDescription = optionDescription;
  }

  /**
   * Gets the name of the telemetry reducer.
   * 
   * @return The name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the instance of the telemetry reducer.
   * 
   * @return The instance.
   */
  public TelemetryReducer getReducer() {
    return this.reducer;
  }

  /**
   * Gets the description of this reducer.
   * 
   * @return The description of the reducer.
   */
  public String getReducerDescription() {
    return this.reducerDescription;
  }

  /**
   * Gets the description of the parameters this reducer takes.
   * 
   * @return The description of the options.
   */
  public String getParameterDescription() {
    return this.optionDescription;
  }
}

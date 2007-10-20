package org.hackystat.telemetry.analyzer.reducer;

import org.hackystat.telemetry.analyzer.reducer.jaxb.Parameters;
import org.hackystat.telemetry.analyzer.reducer.jaxb.ReducerDefinition;

/**
 * Provides information about a single Telemetry Reduction Function. 
 * 
 * @author (Cedric) Qin Zhang, Philip Johnson
 */
public class TelemetryReducerInfo {

  /** The instance. */
  private TelemetryReducer reducer;
  /** The ReducerDefinition instance obtained from the XML. */
  private ReducerDefinition definition;

  /**
   * Constructs this instance.
   * 
   * @param name The name of this telemetry reducer.
   * @param reducer The concrete instance of telemetry reducer.
   * @param definition The ReducerDefinition. 
   */
  TelemetryReducerInfo(TelemetryReducer reducer, ReducerDefinition definition) {
    this.reducer = reducer;
    this.definition = definition;
  }

  /**
   * Gets the name of the telemetry reducer.
   * 
   * @return The name.
   */
  public String getName() {
    return this.definition.getName();
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
    return this.definition.getDescription();
  }

  /**
   * Gets the description of the parameters this reducer takes.
   * 
   * @return The description of the options.
   */
  public Parameters getParameterDescription() {
    //Should return the Parameters object.   
    return definition.getParameters();
  }
}

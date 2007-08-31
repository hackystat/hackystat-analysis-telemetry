package org.hackystat.telemetry.language.ast;

/**
 * Telemetry expression representing a reducer call. A reducer is responsible for processing
 * low level software metrics. Reducer call always evaluates to an instance of 
 * <code>TelemetryStreamCollection</code> which contains at least one telemetry stream.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class ReducerCall implements Expression {

  /** The name of the reducer. */
  private String reducerName;
  
  /** The parameters. */
  private Expression[] parameters;
  
  /**
   * Constructs this instance.
   * 
   * @param reducerName The name of the reducer.
   * @param parameters The reducer parameters. Null is valid if the reducer does not
   *        require any parameter.
   */
  public ReducerCall(String reducerName, Expression[] parameters) {
    this.reducerName = reducerName;
    this.parameters = parameters == null ? new Expression[]{} : parameters;
  }

  /**
   * Gets the name of the reducer to be invoked.
   * 
   * @return The reducer name.
   */
  public String getReducerName() {
    return this.reducerName;
  }
 
  /**
   * Gets reducer parameters.
   * 
   * @return The reducer parameters. It will never return null. If there is no parameter,
   *         an empty array will be returned.
   */
  public Expression[] getParameters() {
    return this.parameters;
  }
}

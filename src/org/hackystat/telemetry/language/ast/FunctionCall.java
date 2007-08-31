package org.hackystat.telemetry.language.ast;

/**
 * Telemetry expression representing a function call. A function can be as simple as "Add"
 * or "Subtract" funciton built into telemetry language, or it can be a custom supplied function
 * doing anything imaginable.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class FunctionCall implements Expression {

  /** The name of the function. */
  private String functionName;
  
  /** The function parameters. */
  private Expression[] parameters;
  
  /**
   * Constructs this instance.
   * 
   * @param functionName The name of the function.
   * @param parameters The function parameters. Null is valid if the function does not
   *        require any parameter.
   */
  public FunctionCall(String functionName, Expression[] parameters) {
    this.functionName = functionName;
    this.parameters = parameters == null ? new Expression[]{} : parameters;
  }
  
  /**
   * Gets the name of the function to be invoked.
   * 
   * @return The function name.
   */
  public String getFunctionName() {
    return this.functionName;
  }
  
  /**
   * Gets function parameters.
   * 
   * @return The function parameters. It will never return null. If there is no parameter,
   *         an empty array will be returned.
   */
  public Expression[] getParameters() {
    return this.parameters;
  }
}

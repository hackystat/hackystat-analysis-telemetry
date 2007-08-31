package org.hackystat.telemetry.language.ast;

/**
 * A reference to telemetry chart definition by its name. This is used by 
 * <code>TelemetryReportDefinition</code>.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class ChartReference {
  
  /** The name of telemetry chart being referenced. */
  private String name;
  
  /** 
   * The parameters that need to be passed. Note that not all <code>Expression</code>
   * objects are valid. Only <code>Variable</code> and <code>Constant</code> objects
   * are legal.
   */
  private Expression[] parameters;

  /**
   * Constructs this instance.
   * 
   * @param name The name of telemetry chart being referenced.
   * @param parameters The parameters that need to be passed. 
   *        Note that not all <code>Expression</cod> objects are valid. 
   *        Only <code>Variable</code> and <code>Constant</code> objects are legal.
   *        Null is valid if no parameter needs to be passed.
   */
  public ChartReference(String name, Expression[] parameters) {
    this.name = name;
    this.parameters = parameters == null ? new Expression[0] : parameters;
    
    //validity check
    for (int i = 0; i < this.parameters.length; i++) {
      Expression exp = this.parameters[i];
      if (! (exp instanceof Variable || exp instanceof Constant)) {
        throw new RuntimeException("Only objects of Variable and Constant types are legal"
            + " parameter types.");
      }
    }
  }

  /**
   * Gets the name of the referenced telemetry chart object.
   * 
   * @return The name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the parameters.
   * 
   * @return An array of <code>Variable</code> or <code>Constant</code> or both objects.
   *         Null will never be returned. If there is no parameter, an empty array will be returned.
   */
  public Expression[] getParameters() {
    return this.parameters;
  }
}
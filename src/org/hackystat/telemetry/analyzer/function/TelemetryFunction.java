package org.hackystat.telemetry.analyzer.function;

/**
 * Abstract base class for telemetry functions. A telemetry function operates on an array of
 * <code>Number</code> and/or <code>TelemetryStreamCollection</code> objects, and 
 * returns either a <code>Number</code> or a <code>TelemetryStreamCollection</code> object.
 * <p/>
 * There will be exactly one instance of each implementation. Therefore, all implementation
 * should be thread-safe.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public abstract class TelemetryFunction {

  /** The name of this function. */
  private String name;
  
  /**
   * Constructs this instance.
   * 
   * @param name The assigned name of this function.
   */
  protected TelemetryFunction(String name) {
    this.name = name;    
  }
  
  /**
   * The name of this function.
   * 
   * @return The name.
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * Performs operation. Note that multiple thread might call this method at the same time.
   * The implementation must be thread-safe.
   * 
   * @param parameters An array of objects of type either <code>String</code>,
   *        <code>Number</code>, and/or <code>TelemetryStreamCollection</code>. 
   *        <b>Note: Since telemetry chart analysis might pass in parameter values as string,
   *        custom implementation should be prepared to handle string even if it is expecting
   *        a number instance.</b>
   * 
   * @return Either an instance of <code>Number</code> or <code>TelemetryStreamCollection</code>.
   *          
   * @throws TelemetryFunctionException If anything is wrong.
   */
  public abstract Object compute(Object[] parameters) throws TelemetryFunctionException;
}

package org.hackystat.telemetry.analyzer.function.impl;

import org.hackystat.telemetry.analyzer.function.TelemetryFunction;
import org.hackystat.telemetry.analyzer.function.TelemetryFunctionException;

/**
 * Stock telememtry function: "Idempotent". "Stock" means that this function is essential
 * to telemetry functionality and is always available.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class IdempotentFunction extends TelemetryFunction {

  /**
   * Constructs this instance.
   * 
   * @param name The assigned name of this function.
   */
  public IdempotentFunction(String name) {
    super(name);
  }
  
 /**
  * @param parameters An array of 1 object of type either <code>Number</code> 
  *        or <code>TelemetryStreamCollection</code>. 
  * 
  * @return Either an instance of <code>Number</code> or <code>TelemetryStreamCollection</code>. 
  * 
  * @throws TelemetryFunctionException If anything is wrong.
  */
  public Object compute(Object[] parameters) throws TelemetryFunctionException {
    if (parameters.length == 1) {
      return parameters[0];
    }
    else {
      throw new TelemetryFunctionException("Telemetry function " + this.getName()
          + " takes 1 parameter.");
    }
  }
}

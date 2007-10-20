package org.hackystat.telemetry.analyzer.function.impl;

import org.hackystat.telemetry.analyzer.function.TelemetryFunction;
import org.hackystat.telemetry.analyzer.function.TelemetryFunctionException;

/**
 * Accepts either a number or a telemetry stream and returns it. 
 * 
 * @author (Cedric) Qin ZHANG, Philip Johnson
 */
public class IdempotentFunction extends TelemetryFunction {

  /**
   * Constructs this instance.
   */
  public IdempotentFunction() {
    super("idempotent");
  }
  
 /**
  * @param parameters An array of 1 object of type either <code>Number</code> 
  *        or <code>TelemetryStreamCollection</code>. 
  * 
  * @return Either an instance of <code>Number</code> or <code>TelemetryStreamCollection</code>. 
  * 
  * @throws TelemetryFunctionException If anything is wrong.
  */
  @Override
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

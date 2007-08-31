package org.hackystat.telemetry.function.impl;

import org.hackystat.telemetry.function.TelemetryFunction;
import org.hackystat.telemetry.function.TelemetryFunctionException;
import org.hackystat.telemetry.model.TelemetryStreamCollection;

/**
 * Stock telememtry function: "Div". "Stock" means that this function is essential to telemetry
 * functionality and is always available.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class DivFunction extends TelemetryFunction {

  /** Thread-safe div operator. */
  private DivOperator divOperator = new DivOperator();
  
  /**
   * Constructs this instance.
   * 
   * @param name The assigned name of this function.
   */
  public DivFunction(String name) {
    super(name);
  }
  
  /**
   * @param parameters An array of 2 objects of type either <code>Number</code> 
   *        or <code>TelemetryStreamCollection</code>. 
   * 
   * @return Either an instance of <code>Number</code> or <code>TelemetryStreamCollection</code>. 
   * 
   * @throws TelemetryFunctionException If anything is wrong.
   */
  public Object compute(Object[] parameters) throws TelemetryFunctionException {
    //parameter validity check
    if (parameters.length != 2 
       || ! (parameters[0] instanceof Number || parameters[0] instanceof TelemetryStreamCollection)
       || ! (parameters[1] instanceof Number || parameters[1] instanceof TelemetryStreamCollection)
       ) {
      throw new TelemetryFunctionException("Telemetry function " + this.getName()
          + " takes 2 parameters of type 'Number' and/or 'TelemetryStreamCollection'.");
    }
    
    if (parameters[0] instanceof Number && parameters[1] instanceof Number) {
      return this.divOperator.computes((Number) parameters[0], (Number) parameters[1]);
    }
    else {
      TelemetryStreamCollection tsc0 = (parameters[0] instanceof Number)
        ? Utility.expandNumber((Number) parameters[0], (TelemetryStreamCollection) parameters[1])
        : (TelemetryStreamCollection) parameters[0];
      TelemetryStreamCollection tsc1 = (parameters[1] instanceof Number)
        ? Utility.expandNumber((Number) parameters[1], (TelemetryStreamCollection) parameters[0])
        : (TelemetryStreamCollection) parameters[1];
      return BinaryOperationUtility.computes(this.divOperator, tsc0, tsc1);
    }
  }
  
  /**
   * Binary operator used by the enclosing class.
   * 
   * @author (Cedric) Qin ZHANG
   * @version $Id$
   */
  private static class DivOperator implements BinaryOperationUtility.BinaryOperator {
    /**
     * Performs binary operation. 
     * @param a Number 1.
     * @param b Number 2.
     * @return The result.
     */
    public Number computes(Number a, Number b) {
      return new Double(a.doubleValue() / b.doubleValue());     
    }
  }
}

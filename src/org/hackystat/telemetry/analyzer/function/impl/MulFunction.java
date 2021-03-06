package org.hackystat.telemetry.analyzer.function.impl;

import org.hackystat.telemetry.analyzer.function.TelemetryFunction;
import org.hackystat.telemetry.analyzer.function.TelemetryFunctionException;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;

/**
 * If passed two numbers, returns their product, and if passed two telemetry streams, returns a 
 * new TelemetryStreamCollection containing the pairwise multiplication of the individual elements. 
 * 
 * @author (Cedric) Qin ZHANG, Philip Johnson
 */
public class MulFunction extends TelemetryFunction {

  /** Thread-safe mul operator. */
  private MulOperator mulOperator = new MulOperator();
  
  /**
   * Constructs this instance.
   */
  public MulFunction() {
    super("mul");
  }
  
  /**
   * @param parameters An array of 2 objects of type either <code>Number</code> 
   *        or <code>TelemetryStreamCollection</code>. 
   * 
   * @return Either an instance of <code>Number</code> or <code>TelemetryStreamCollection</code>. 
   * @throws TelemetryFunctionException If anything is wrong.
   */
  @Override
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
      return this.mulOperator.computes((Number) parameters[0], (Number) parameters[1]);
    }
    else {
      TelemetryStreamCollection tsc0 = (parameters[0] instanceof Number)
        ? Utility.expandNumber((Number) parameters[0], (TelemetryStreamCollection) parameters[1])
        : (TelemetryStreamCollection) parameters[0];
      TelemetryStreamCollection tsc1 = (parameters[1] instanceof Number)
        ? Utility.expandNumber((Number) parameters[1], (TelemetryStreamCollection) parameters[0])
        : (TelemetryStreamCollection) parameters[1];
      return BinaryOperationUtility.computes(this.mulOperator, tsc0, tsc1);
    }
  }

  /**
   * Binary operator used by the enclosing class.
   * 
   * @author (Cedric) Qin ZHANG
   */
  private static class MulOperator implements BinaryOperationUtility.BinaryOperator {
    /**
     * Performs binary operation. 
     * @param a Number 1.
     * @param b Number 2.
     * @return The result.
     */
    public Number computes(Number a, Number b) {
      if (a instanceof Integer && b instanceof Integer) {
        return Integer.valueOf(a.intValue() * b.intValue());
      }
      else {
        return Double.valueOf(a.doubleValue() * b.doubleValue());
      }      
    }
  }
}

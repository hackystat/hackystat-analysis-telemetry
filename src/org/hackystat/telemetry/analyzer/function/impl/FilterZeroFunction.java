package org.hackystat.telemetry.analyzer.function.impl;

import java.util.Iterator;

import org.hackystat.telemetry.analyzer.function.TelemetryFunction;
import org.hackystat.telemetry.analyzer.function.TelemetryFunctionException;
import org.hackystat.telemetry.analyzer.model.TelemetryDataModelException;
import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;

/**
 * Filters out telemetry streams with zero values or no value in a 
 * <code>TelemetryStreamCollection</code> object. 
 * 
 * @author (Cedric) Qin ZHANG
 */
public class FilterZeroFunction extends TelemetryFunction {

  /**
   * Constructs this instance.
   */
  public FilterZeroFunction() {
    super("FilterZero");
  }
  
 /**
  * Performs filter operation.
  * @param parameters An array of 1 objects of <code>TelemetryStreamCollection</code> type.
  * @return A new <code>TelemetryStreamCollection</code> instance after filtering.
  * @throws TelemetryFunctionException If anything is wrong.
  */
  @Override
  public Object compute(Object[] parameters) throws TelemetryFunctionException {
    if (parameters.length != 1 || ! (parameters[0] instanceof TelemetryStreamCollection)) {
      throw new TelemetryFunctionException("Telemetry function " + this.getName()
          + " takes 1 TelemetryStreamCollection object.");
    }
    else {
      try {
        TelemetryStreamCollection source = (TelemetryStreamCollection) parameters[0];
        TelemetryStreamCollection target = new TelemetryStreamCollection(
            source.getName(), source.getProject(), source.getInterval());
        for (Iterator i = source.getTelemetryStreams().iterator(); i.hasNext(); ) {
          TelemetryStream stream = (TelemetryStream) i.next();
          if (! this.isAllZero(stream)) {
            target.add(stream);
          }
        }
        return target;
      }
      catch (TelemetryDataModelException ex) {
        throw new TelemetryFunctionException(ex);
      }
    }
  }
  
  /**
   * Checks whether a telemetry stream contains all zeros or no value at all.
   * 
   * @param stream The telemetry stream to be checked.
   * @return True if it contains all zero or no value.
   */
  private boolean isAllZero(TelemetryStream stream) {
    for (Iterator i = stream.getDataPoints().iterator(); i.hasNext(); ) {
      TelemetryDataPoint dp = (TelemetryDataPoint) i.next();
      Number value = dp.getValue();
      if (value != null) {
        if ((value instanceof Double || value instanceof Float)
            && value.doubleValue() != 0.0) {
          return false;
        }
        else if (value.longValue() != 0L) {
          return false;
        }
      }
    }
    return true;
  }
  
}
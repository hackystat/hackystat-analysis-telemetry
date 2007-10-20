package org.hackystat.telemetry.analyzer.function.impl;

import org.hackystat.telemetry.analyzer.function.TelemetryFunctionException;
import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;

/**
 * Utility class handling miscellaneous tasks.
 * 
 * @author (Cedric) Qin ZHANG
 */
class Utility {
  
  /**
   * Creates a new instance of <code>TelemetryStreamCollection</code> based on supplied source,
   * and fills it with supplied value.
   * 
   * @param value The value to fill the newly created instance of 
   *        <code>TelemetryStreamCollection</code>.
   * @param source The template <code>TelemetryStreamCollection</code> object.
   * @return A newly created instance of <code>TelemetryStreamCollection</code>.
   * @throws TelemetryFunctionException If anything is wrong.
   */
  static TelemetryStreamCollection expandNumber(Number value, TelemetryStreamCollection source) 
      throws TelemetryFunctionException {
    TelemetryStreamCollection target = new TelemetryStreamCollection(source.getName(),
        source.getProject(), source.getInterval());
    try {
      for (TelemetryStream srcStream : source) {
        target.add(expandNumber(value, srcStream));
      }
    }
    catch (Exception ex) {
      throw new TelemetryFunctionException(ex);
    }   
    return target;
  }

  /**
   * Creates a new instance of <code>TelemetryStream</code> based on supplied source,
   * and fills it with supplied value.
   * 
   * @param value The value to fill the newly created instance of <code>TelemetryStream</code>.
   * @param source The template <code>TelemetryStream</code> object.
   * @return A newly created instance of <code>TelemetryStream</code>.
   * @throws TelemetryFunctionException If anything is wrong.
   */
  private static TelemetryStream expandNumber(Number value, TelemetryStream source) 
      throws TelemetryFunctionException {
    TelemetryStream target = new TelemetryStream(source.getTag());
    try {
      for (TelemetryDataPoint srcDataPoint : source.getDataPoints()) {
        target.addDataPoint(new TelemetryDataPoint(srcDataPoint.getPeriod(), value));
      }
    }
    catch (Exception ex) {
      throw new TelemetryFunctionException(ex);
    }
    return target;
  }
}

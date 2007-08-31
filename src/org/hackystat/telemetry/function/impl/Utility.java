package org.hackystat.telemetry.function.impl;

import java.util.Iterator;

import org.hackystat.telemetry.function.TelemetryFunctionException;
import org.hackystat.telemetry.model.TelemetryDataPoint;
import org.hackystat.telemetry.model.TelemetryStream;
import org.hackystat.telemetry.model.TelemetryStreamCollection;

/**
 * Utility class handling miscellaneous tasks.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
class Utility {
  
  /**
   * Creates a new instance of <code>TelemetryStreamCollection</code> based on supplied source,
   * and fill it with supplied value.
   * 
   * @param value The value to fill the newly created instance of 
   *        <code>TelemetryStreamCollection</code>.
   * @param source The template <code>TelemetryStreamCollection</code> object.
   * 
   * @return A newly created instance of <code>TelemetryStreamCollection</code>.
   * 
   * @throws TelemetryFunctionException If anything is wrong.
   */
  static TelemetryStreamCollection expandNumber(Number value, TelemetryStreamCollection source) 
      throws TelemetryFunctionException {
    TelemetryStreamCollection target = new TelemetryStreamCollection(source.getName(),
        source.getProject(), source.getInterval());
    try {
      for (Iterator i = source.getTelemetryStreams().iterator(); i.hasNext(); ) {
        TelemetryStream srcStream = (TelemetryStream) i.next();
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
   * and fill it with supplied value.
   * 
   * @param value The value to fill the newly created instance of <code>TelemetryStream</code>.
   * @param source The template <code>TelemetryStream</code> object.
   * 
   * @return A newly created instance of <code>TelemetryStream</code>.
   * 
   * @throws TelemetryFunctionException If anything is wrong.
   */
  private static TelemetryStream expandNumber(Number value, TelemetryStream source) 
      throws TelemetryFunctionException {
    TelemetryStream target = new TelemetryStream(source.getTag());
    try {
      for (Iterator i = source.getDataPoints().iterator(); i.hasNext(); ) {
        TelemetryDataPoint srcDataPoint = (TelemetryDataPoint) i.next();
        target.addDataPoint(new TelemetryDataPoint(srcDataPoint.getPeriod(), value));
      }
    }
    catch (Exception ex) {
      throw new TelemetryFunctionException(ex);
    }
    return target;
  }
}

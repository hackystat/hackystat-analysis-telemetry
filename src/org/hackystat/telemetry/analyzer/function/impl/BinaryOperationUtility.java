package org.hackystat.telemetry.analyzer.function.impl;

import java.util.Iterator;
import java.util.List;

import org.hackystat.telemetry.analyzer.function.TelemetryFunctionException;
import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.util.TimePeriod;

/**
 * Helper class to perform binary operations on <code>TelemetryStreamCollection</code> objects.
 * 
 * @author (Cedric) Qin ZHANG
 */
class BinaryOperationUtility {
  
  /**
   * Binary operator interface.
   * 
   * @author (Cedric) Qin ZHANG
   */
  interface BinaryOperator {
    /**
     * Performs binary operation.
     * 
     * @param a Number 1.
     * @param b Number 2.
     * @return The result.
     */
    Number computes(Number a, Number b);
  }

  /**
   * Applies binary operation to two <code>TelemetryStreamCollection</code> objects, 
   * and generates a new <code>TelemetryStreamCollection</code> object.
   * 
   * @param operator The binary operator.
   * @param streamCollection1 The first <code>TelemetryStreamCollection</code> object.
   * @param streamCollection2 The second <code>TelemetryStreamCollection</code> object.
   * @return A new <code>TelemetryStreamCollection</code> object after applying the operator.
   * @throws TelemetryFunctionException If there is anything wrong.
   */
  static TelemetryStreamCollection computes(BinaryOperator operator,
      TelemetryStreamCollection streamCollection1, TelemetryStreamCollection streamCollection2) 
      throws TelemetryFunctionException {

    // check whether the streams in the two collections can be matched.
    if (!streamCollection1.getProject().equals(streamCollection2.getProject())) {
      throw new TelemetryFunctionException("Two stream collections are for different projects.");
    }
    if (!streamCollection1.getInterval().equals(streamCollection2.getInterval())) {
      throw new TelemetryFunctionException("Two stream collections are for different intervals.");
    }
    if (streamCollection1.getTelemetryStreams().size() 
        != streamCollection2.getTelemetryStreams().size()) {
      throw new TelemetryFunctionException(
          "Two stream collections have different number of streams.");
    }
    else {
      for (Iterator i = streamCollection1.getTelemetryStreams().iterator(); i.hasNext();) {
        TelemetryStream stream1 = (TelemetryStream) i.next();
        if (streamCollection2.get(stream1.getTag()) == null) {
          throw new TelemetryFunctionException("Two stream collections do not match.");
        }
      }
    }

    //do compuatation
    try {
      TelemetryStreamCollection resultStreams = new TelemetryStreamCollection(null,
          streamCollection1.getProject(), streamCollection2.getInterval());
      for (Iterator i = streamCollection1.getTelemetryStreams().iterator(); i.hasNext();) {
        TelemetryStream stream1 = (TelemetryStream) i.next();
        TelemetryStream stream2 = streamCollection2.get(stream1.getTag());
        resultStreams.add(computes(operator, stream1, stream2));
      }
      return resultStreams;
    }
    catch (Exception ex) {
      throw new TelemetryFunctionException(ex.getMessage());
    }
  }
  
  /**
   * Applies binary operation to two telemetry streams and generates a new stream.
   * The two source streams must:
   * <ul>
   * <li>Have the same tag.</li>
   * <li>All time periods in the data points must match.</li>
   * </ul>
   * If one of the values in a data point is null, then a null will be put in the
   * generated data point.
   * 
   * @param operator The binary operator.
   * @param stream1 Source telemetry stream 1. Order may be important in certain
   *        operations.
   * @param stream2 Source telemetry stream 2. Order may be important in certain
   *        operations.
   * @return The new generated telemetry stream after applying the operator.
   * @throws TelemetryFunctionException If there is any error.
   */
  private static TelemetryStream computes(BinaryOperator operator,
      TelemetryStream stream1, TelemetryStream stream2) throws TelemetryFunctionException {
    // check tag match
    Object tag1 = stream1.getTag();
    Object tag2 = stream2.getTag();
    if (tag1 == null) {
      if (tag2 != null) {
        throw new TelemetryFunctionException(
            "The tags of the two telemetry streams does not match.");
      }
    }
    else {
      if (!tag1.equals(tag2)) {
        throw new TelemetryFunctionException(
            "The tags of the two telemetry streams does not match.");
      }
    }
    // check number of data points in the two streams
    List dataPointSeries1 = stream1.getDataPoints();
    List dataPointSeries2 = stream2.getDataPoints();
    if (dataPointSeries1.size() != dataPointSeries2.size()) {
      throw new TelemetryFunctionException("Two telemetry streams are of different size.");
    }

    //do computation
    TelemetryStream resultStream = new TelemetryStream(tag1);
    try {
      int size = dataPointSeries1.size();
      for (int i = 0; i < size; i++) {
        TelemetryDataPoint dataPoint1 = (TelemetryDataPoint) dataPointSeries1.get(i);
        TelemetryDataPoint dataPoint2 = (TelemetryDataPoint) dataPointSeries2.get(i);
        TimePeriod timePeriod = dataPoint1.getPeriod();
        if (!timePeriod.equals(dataPoint2.getPeriod())) {
          throw new TelemetryFunctionException(
              "Different time periods detected in the two streams.");
        }
        Number value1 = dataPoint1.getValue();
        Number value2 = dataPoint2.getValue();
        Number resultValue = null;
        if (value1 != null && value2 != null) {
          resultValue = operator.computes(value1, value2);
        }
        resultStream.addDataPoint(new TelemetryDataPoint(timePeriod, resultValue));
      }
    }
    catch (Exception ex) {
      throw new TelemetryFunctionException(ex.getMessage());
    }
    return resultStream;
  }
}

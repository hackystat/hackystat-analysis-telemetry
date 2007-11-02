package org.hackystat.telemetry.analyzer.function.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hackystat.telemetry.analyzer.function.TelemetryFunction;
import org.hackystat.telemetry.analyzer.function.TelemetryFunctionException;
import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;

/**
 * Filters telemetry streams in a <code>TelemetryStreamCollection</code> object by applying a 
 * ranking function.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class FilterFunction extends TelemetryFunction {

  private static Map<String, RankFunction> RANK_FUNCTIONS = new TreeMap<String, RankFunction>();
  
  /**
   * Constructs this instance.
   * 
   */
  public FilterFunction() {
    super("Filter");
    RANK_FUNCTIONS.put("avg", new AverageRankFunction());
    RANK_FUNCTIONS.put("max", new MaxRankFunction());
    RANK_FUNCTIONS.put("min", new MinRankFunction());
    RANK_FUNCTIONS.put("last", new LastRankFunction());
    RANK_FUNCTIONS.put("delta", new DeltaRankFunction());
    RANK_FUNCTIONS.put("simpledelta", new SimpleDeltaRankFunction());
  }
  
  /**
   * Performs filter operation. The returned <code>TelemetryStreamCollection</code> only contains
   * telemetry streams that satisfies the specified criteria.
   * 
   * @param parameters An array of 3 objects.
   *        <ul>
   *          <li>1st element: A <code>TelemetryStreamCollection</code> instance.
   *          <li>2nd element: A <code>String</code> representing ranking function name.
   *              It must be one of "Sum|Max|Min|Last|Delta".
   *          <li>3rd element: A <code>String</code> specifying how to apply cutoff value.
   *              It must be one of "Above|Below|Top|Bottom|TopPercent|BottomPercent".
   *          <li>4th element: A <code>Number</code> instance representing cutoff value.
   *        </ul> 
   * 
   * @return A new <code>TelemetryStreamCollection</code> instance after filtering. Note
   *         that it may contain no telemetry stream at all if no stream satisfies the criteria.
   * 
   * @throws TelemetryFunctionException If anything is wrong.
   */
  @Override
  public Object compute(Object[] parameters) throws TelemetryFunctionException {
    if (parameters.length != 4 || ! (parameters[0] instanceof TelemetryStreamCollection)
         || ! (parameters[1] instanceof String) || ! (parameters[2] instanceof String)
         || ! (parameters[3] instanceof Number || parameters[3] instanceof String)) {
       throw new TelemetryFunctionException("Telemetry function " + this.getName()
         + " accept 4 parameters: TelemetryStreamCollection, Avg|Max|Min|Last|Delta|SimpleDelta,"
         + " Above|Below|Top|Bottom|TopPercent|BottomPercent, Number.");
    }
    
    if (parameters[3] instanceof String) {
      try {
        parameters[3] = new Double((String) parameters[3]);
      }
      catch (NumberFormatException ex) {
        throw new TelemetryFunctionException("The 4th parameter of telemetry function " 
            + this.getName() + " is not a valid number.", ex);     
      }
    }
      
    TelemetryStreamCollection streams = (TelemetryStreamCollection) parameters[0];
     
    String rankFunctionName = (String) parameters[1];
    RankFunction rankFunction = RANK_FUNCTIONS.get(rankFunctionName.toLowerCase());
    if (rankFunction == null) {
      throw new TelemetryFunctionException("Rank function '" + rankFunctionName 
          + "' does not exist.");
    }
     
    String opMode = (String) parameters[2];
    Number cutoff = (Number) parameters[3];
   
    try {
      if ("Above".equalsIgnoreCase(opMode)) {
        return this.applyAbsoluteCutoff(streams, rankFunction, true, cutoff.doubleValue());
      }
      else if ("Below".equals(opMode)) {
        return this.applyAbsoluteCutoff(streams, rankFunction, false, cutoff.doubleValue());
      }
      else if ("TopPercent".equalsIgnoreCase(opMode) || "BottomPercent".equalsIgnoreCase(opMode)
          || "Top".equalsIgnoreCase(opMode) || "Bottom".equalsIgnoreCase(opMode)) {
        return this.applyRelativeCutoff(streams, rankFunction, opMode, cutoff.intValue());
      }
      else {
        throw new TelemetryFunctionException("The 3rd parameter of telemetry function " 
            + this.getName() + " must be one of Above|Below|TopPercent|BottomPercent|Top|Bottom.");
        }
    }
    catch (Exception ex) {
      throw new TelemetryFunctionException(ex);
    }
  }
  
  /**
   * Applies filter and returns only the telemetry streams meeting the criteria.
   * 
   * @param streams Telemetry stream collection.
   * @param rankFunction The rank function.
   * @param isAbove True if the filter is to return the streams above the cutoff value.
   *                False if the filter is to return the streams below the cutoff value.
   * @param cutoff The cutoff value.
   * 
   * @return A new <code>TelemetryStreamCollection</code> instance after filtering. 
   * @throws Exception If there is anything wrong.
   */
  private TelemetryStreamCollection applyAbsoluteCutoff(TelemetryStreamCollection streams,
      RankFunction rankFunction, boolean isAbove, double cutoff) throws Exception {
    
    TelemetryStreamCollection target = new TelemetryStreamCollection(
        streams.getName(), streams.getProject(), streams.getInterval());   
    for (TelemetryStream stream : streams) {
      double rank = rankFunction.getRank(stream);
      if ((isAbove && rank > cutoff) || (! isAbove && rank < cutoff)) {
        target.add(stream);
      }
    }
    return target;
  }
  
  /**
   * Applies filter and returns the telemetry streams at the top or bottom.
   * 
   * @param streams Telemetry stream collection.
   * @param rankFunction The rank function.
   * @param opMode Operation mode.
   * @param cutoff The cutoff value.
   * 
   * @return A new <code>TelemetryStreamCollection</code> instance after filtering. 
   * @throws Exception If there is anything wrong.
   */
  @SuppressWarnings("cast")
  private TelemetryStreamCollection applyRelativeCutoff(TelemetryStreamCollection streams, 
      RankFunction rankFunction, String opMode, int cutoff) throws Exception {
    
    TelemetryStreamCollection target = new TelemetryStreamCollection(
        streams.getName(), streams.getProject(), streams.getInterval());
    ArrayList<TelemetryStream> orderedList = this.sort(rankFunction, streams);
    
    if ("TopPercent".equalsIgnoreCase(opMode)) {
      if (cutoff < 0 || cutoff > 100) {
        throw new TelemetryFunctionException("You must supply a cutoff value from 0 to 100 for " +
            "'TopPercent' operation mode.");
      }
      int start = (int) Math.floor(
          (double) orderedList.size() - (double) orderedList.size() * cutoff / 100);
      if (start < 0) {
        start = 0;
      }
      for (int i = start; i < orderedList.size(); i++) {
        target.add(orderedList.get(i));
      }
    }
    else if ("BottomPercent".equalsIgnoreCase(opMode)) {
      if (cutoff < 0 || cutoff > 100) {
        throw new TelemetryFunctionException("You must supply a cutoff value from 0 to 100 for " +
            "'BottomPercent' operation mode.");
      }
      int count = (int) Math.ceil((double) orderedList.size() * cutoff / 100);
      for (int i = 0; i < count && i < orderedList.size(); i++) {
        target.add(orderedList.get(i));
      }
    }
    else if ("Top".equalsIgnoreCase(opMode)) {
      int start = orderedList.size() - cutoff;
      if (start < 0) {
        start = 0;
      }
      for (int i = start; i < orderedList.size(); i++) {
        target.add(orderedList.get(i));
      }
    }
    else if ("Bottom".equalsIgnoreCase(opMode)) {
      for (int i = 0; i < cutoff && i < orderedList.size(); i++) {
        target.add(orderedList.get(i));
      }
    }
    else {
      throw new TelemetryFunctionException("Unsupported op mode '" + opMode + "'.");
    }
    
    return target;
  }
  
  /**
   * Sorts the telemetry streams according to a rank function.
   * 
   * @param rankFunction The rank function.
   * @param streams Telemetry stream collection.
   * 
   * @return A sorted list of telemetry streams, from the smallest rank value to the largest.
   * 
   * @throws TelemetryFunctionException If the rank function does not exist.
   */
  private ArrayList<TelemetryStream> sort(RankFunction rankFunction, 
      TelemetryStreamCollection streams) 
      throws TelemetryFunctionException {

    TreeMap<Double, List<TelemetryStream>> map = new TreeMap<Double, List<TelemetryStream>>();
    for (TelemetryStream stream : streams.getTelemetryStreams()) {
      Double rank = new Double(rankFunction.getRank(stream));
      List<TelemetryStream> list = map.get(rank);
      if (list == null) {
        list = new ArrayList<TelemetryStream>(1);
        map.put(rank, list);
      }
      list.add(stream);
    }
    
    ArrayList<TelemetryStream> sortedList = 
      new ArrayList<TelemetryStream>(streams.getTelemetryStreams().size());
    for (List<TelemetryStream> list : map.values()) {
      sortedList.addAll(list);
    }
    return sortedList;
  }
  
  //===================== Rank Function ==============================
  
  /**
   * Rank function.
   */
  interface RankFunction {
    
    /**
     * Gets a rank value for a telemetry stream. Note that any implmentation MUST observe the
     * rules: The function should never return Double.POSITIVE_INFINITY, 
     *       Double.NEGATIVE_INFINITY, or Double.NaN.
     *
     * @param stream A telemetry stream.
     * @return A rank value.
     */
    double getRank(TelemetryStream stream);
  }
  
  //NOTE: Sum function is bad, it ignores the data points with no value, which makes the sum
  //      of different telemetry streams not comparable if there is a missing point.
  //      There is no easy way around this. Use Average instead.
  
//  /**
//   * A rank function using sum as ranking criteria.
//   * 
//   * @author (Cedric) Qin ZHANG
//   * @version $Id$
//   */
//  static class SumRankFunction implements RankFunction {
//    
//    /**
//     * Computes the sum of a telemetry stream.
//     * @param stream A telemetry stream.
//     * @return The sum.
//     */
//    public double getRank(TelemetryStream stream) {
//      double sum = 0;
//      for (Iterator i = stream.getDataPoints().iterator(); i.hasNext(); ) {
//        TelemetryDataPoint dp = (TelemetryDataPoint) i.next();
//        Number number = dp.getValue();
//        if (number != null) {
//          double numberValue = number.doubleValue();
//          if (! Double.isInfinite(numberValue) && ! Double.isNaN(numberValue)) {
//            //Cannot use infinity, because Positive infinity + negative infinity = NaN
//            sum += numberValue;
//          }
//        }
//      }
//      return sum;
//    }
//  }
  
  /**
   * A rank function using average as ranking criteria.
   * 
   * @author (Cedric) Qin ZHANG
   */
  static class AverageRankFunction implements RankFunction {
    
    /**
     * Computes the average of a telemetry stream.
     * @param stream A telemetry stream.
     * @return The average.
     */
    public double getRank(TelemetryStream stream) {
      int count = 0;
      double sum = 0;
      for (TelemetryDataPoint dp : stream.getDataPoints()) {
        Number number = dp.getValue();
        if (number != null) {
          double numberValue = number.doubleValue();
          if (! Double.isInfinite(numberValue) && ! Double.isNaN(numberValue)) {
            //Cannot use infinity, because Positive infinity + negative infinity = NaN
            count ++;
            sum += numberValue;
          }
        }
      }
      return count == 0 ? 0 : sum / count;
    }
  }
  
  /**
   * A rank function using max as ranking criteria.
   * 
   * @author (Cedric) Qin ZHANG
   */
  static class MaxRankFunction implements RankFunction {
    
    /**
     * Computes the max value of a telemetry stream.
     * @param stream A telemetry stream.
     * @return The max value.
     */
    public double getRank(TelemetryStream stream) {
      double max = Double.MIN_VALUE;
      for (TelemetryDataPoint dp : stream.getDataPoints()) {
        Number number = dp.getValue();
        if (number != null) {
          double numberValue = number.doubleValue();
          if (! Double.isInfinite(numberValue) && ! Double.isNaN(numberValue)) {
            if (max < numberValue) { //NOPMD
              max = numberValue;
            }
          }
        }
      }
      return max;
    }
  }
  
  /**
   * A rank function using min as ranking criteria.
   * 
   * @author (Cedric) Qin ZHANG
   */
  static class MinRankFunction implements RankFunction {
    
    /**
     * Computes the min value of a telemetry stream.
     * @param stream A telemetry stream.
     * @return The min value.
     */
    public double getRank(TelemetryStream stream) {
      double min = Double.MAX_VALUE;
      for (TelemetryDataPoint dp : stream.getDataPoints()) {
        Number number = dp.getValue();
        if (number != null) {
          double numberValue = number.doubleValue();
          if (! Double.isInfinite(numberValue) && ! Double.isNaN(numberValue)) {
            if (min > numberValue) { //NOPMD
              min = numberValue;
            }
          }
        }
      }
      return min;
    }
  }
  
  /**
   * A rank function using the lastest data point value as ranking criteria.
   * 
   * @author (Cedric) Qin ZHANG
   */
  static class LastRankFunction implements RankFunction {
    
    /**
     * Computes the last value of a telemetry stream.
     * @param stream A telemetry stream.
     * @return The last value.
     */
    public double getRank(TelemetryStream stream) {
      List<TelemetryDataPoint> list = stream.getDataPoints();
      for (int i = list.size() - 1; i >= 0; i--) {
        TelemetryDataPoint dp = list.get(i);
        Number number = dp.getValue();
        if (number != null) {
          double numberValue = number.doubleValue();
          if (! Double.isInfinite(numberValue) && ! Double.isNaN(numberValue)) {
            return numberValue;
          }
        }
      }
      return 0;
    }
  }
  
  /**
   * A rank function using delta as ranking criteria.
   * 
   * @author (Cedric) Qin ZHANG
   */
  static class DeltaRankFunction implements RankFunction {
    
    /**
     * Computes the sum of delta of a telemetry stream.
     * @param stream A telemetry stream.
     * @return The delta sum.
     */
    public double getRank(TelemetryStream stream) {
      double sum = 0;
      double lastValue = Double.NaN;
      for (TelemetryDataPoint dp : stream.getDataPoints()) {
        Number number = dp.getValue();
        if (number != null) {
          double numberValue = number.doubleValue();
          if (! Double.isInfinite(numberValue) && ! Double.isNaN(numberValue)) {
            if (! Double.isNaN(lastValue)) {
              sum += Math.abs(numberValue - lastValue);
            }
            lastValue = numberValue;
          }
        }
      }
      return sum;
    }
  }
  
  /**
   * A rank function using the different between the last data point and the first data point
   * as criteria. Note that this rank value may be negative if the last 
   * 
   * @author (Cedric) Qin ZHANG
   */
  static class SimpleDeltaRankFunction implements RankFunction {
    
    /**
     * Computes the sum of delta of a telemetry stream.
     * @param stream A telemetry stream.
     * @return The delta sum.
     */
    public double getRank(TelemetryStream stream) {
      double firstValue = Double.NaN;
      double lastValue = Double.NaN;
      for (TelemetryDataPoint dp : stream.getDataPoints()) {
        Number number = dp.getValue();
        if (number != null) {
          double numberValue = number.doubleValue();
          if (! Double.isInfinite(numberValue) && ! Double.isNaN(numberValue)) {
            if (Double.isNaN(firstValue)) {
              firstValue = numberValue;
            }
            lastValue = numberValue;
          }
        }
      }
      return (Double.isNaN(firstValue) || Double.isNaN(lastValue)) ? 0 : lastValue - firstValue;
    }
  }
}
package org.hackystat.telemetry.analyzer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import org.hackystat.telemetry.analyzer.util.TimePeriod;

/**
 * Represents a single telemetry stream, which contains an ordered series of TelemetryDataPoint
 * instances.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TelemetryStream {

  private static final TelemetryDataPointTimePeriodComparator DATAPOINT_TIMEPERIOD_COMPARATOR 
      = new TelemetryDataPointTimePeriodComparator();

  private Object tag;

  private TreeMap<TimePeriod, TelemetryDataPoint> dataPoints = 
    new TreeMap<TimePeriod, TelemetryDataPoint>();

  /**
   * Constructs this instance.
   * 
   * @param tag An object that helps to recognize this telemetry stream. Null is
   *        a valid value.
   */
  public TelemetryStream(Object tag) {
    this.tag = tag;
  }

  /**
   * Gets the tag associated with this telemetry stream.
   * 
   * @return The tag object.
   */
  public Object getTag() {
    return this.tag;
  }

  /**
   * Sets the tag associated with this telemetry stream.
   * 
   * @param tag The new tag value
   */
  public void setTag(final Object tag) {
    this.tag = tag;
  }

  /**
   * Adds a data point to this telemetry stream. Note that the time period in
   * all data points must be of the same type (either day, week, or month).
   * Otherwise, an exception will be raised.
   * 
   * @param dataPoint The data point to be added.
   * 
   * @throws TelemetryDataModelException If the data for the time period already exists.
   */
  public void addDataPoint(TelemetryDataPoint dataPoint) throws TelemetryDataModelException {
    try {
      TimePeriod period = dataPoint.getPeriod();
      if (this.dataPoints.containsKey(period)) {
        throw new TelemetryDataModelException("Duplicated period: " + period.toString());
      }
      this.dataPoints.put(period, dataPoint);
    }
    catch (ClassCastException ex) {
      // this exception is raised when the types of time period in data points are different.
      throw new TelemetryDataModelException("All data points must have the same time period type.");
    }
  }

  /**
   * Gets the value associated with the specified time period. Note that if this
   * telemetry stream does not contain the data point associated the time
   * period, an exception is raised. If a null is return, it means that this
   * streams contains the data point, but the value in that data point is null.
   * 
   * @param timePeriod The time period.
   * @return The value.
   * 
   * @throws TelemetryDataModelException If there is no value associated with the
   *         time period.
   */
  public Number getDataPointValue(TimePeriod timePeriod) throws TelemetryDataModelException {
    TelemetryDataPoint dataPoint = this.dataPoints.get(timePeriod);
    if (dataPoint == null) {
      throw new TelemetryDataModelException("No data for the period " + timePeriod.toString());
    }
    return dataPoint.getValue();
  }

  /**
   * Gets a list of data points in this telemetry stream, ordered by time
   * period.
   * 
   * @return A collection of <code>TelemetryDataPoint</code> objects.
   */
  public List<TelemetryDataPoint> getDataPoints() {
    ArrayList<TelemetryDataPoint> list = 
      new ArrayList<TelemetryDataPoint>(this.dataPoints.values());
    Collections.sort(list, DATAPOINT_TIMEPERIOD_COMPARATOR);
    return list;
  }

  /**
   * Comparator for <code>TelemetryDataPoint</code> instances based on their
   * time period property.
   * 
   * @author (Cedric) Qin Zhang
   * @version $Id: TelemetryStream.java,v 1.6 2005/03/15 06:08:36 christoph Exp $
   */
  private static class TelemetryDataPointTimePeriodComparator 
  implements Comparator<TelemetryDataPoint> {

    /**
     * Compares two instances.
     * 
     * @param o1 Data point 1.
     * @param o2 Data point 2.
     * 
     * @return 0 indicates they are equal, positive value indicates data point 1
     *         has later time stamp than data point 2, negative value indicates
     *         data point 1 has earlier time stamp than data point 2.
     */
    @SuppressWarnings("unchecked")
    public int compare(TelemetryDataPoint o1, TelemetryDataPoint o2) {
      TimePeriod t1 = o1.getPeriod();
      TimePeriod t2 = o2.getPeriod();
      return t1.compareTo(t2);
    }
  }
}

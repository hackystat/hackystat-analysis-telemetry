package org.hackystat.telemetry.analyzer.model;

import org.hackystat.telemetry.analyzer.util.TimePeriod;

/**
 * Represents one data point a telemetry stream.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TelemetryDataPoint {

  private TimePeriod period;

  private Number value;

  /**
   * Constructs this instance.
   * 
   * @param timePeriod A time period. It's either day, week, or month.
   * @param value The value associated with the time period.
   */
  public TelemetryDataPoint(TimePeriod timePeriod, Number value) {
    this.period = timePeriod;
    this.value = value;
  }

  /**
   * Gets the time period.
   * 
   * @return The time period.
   */
  public TimePeriod getPeriod() {
    return this.period;
  }

  /**
   * Gets the value.
   * 
   * @return The value.
   */
  public Number getValue() {
    return this.value;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * 
   * @param obj the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument;
   *         <code>false</code> otherwise.
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof TelemetryDataPoint)) {
      return false;
    }
    TelemetryDataPoint another = (TelemetryDataPoint) obj;
    if (!this.period.equals(another.period)) {
      return false;
    }
    else {
      if (this.value == null && another.value == null) {
        return true;
      }
      else if (this.value != null) {
        return this.value.equals(another.value);
      }
      else {
        return another.value.equals(this.value);
      }
    }
  }

  /**
   * Gets the hash code.
   * 
   * @return The hash code.
   */
  public int hashCode() {
    int result = this.period.hashCode();
    if (this.value != null) {
      result = result / 13 + this.value.hashCode() / 2 + 7;
    }
    return result;
  }
}

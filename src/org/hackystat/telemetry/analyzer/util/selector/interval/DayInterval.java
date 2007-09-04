package org.hackystat.telemetry.analyzer.util.selector.interval;

import java.util.Iterator;

import org.hackystat.telemetry.analyzer.util.Day;

/**
 * Provides day interval.
 * 
 * @author Hongbing Kou
 * @version $Id: DayInterval.java,v 1.1.1.1 2005/10/20 23:56:40 johnson Exp $
 */
public class DayInterval extends Interval {
  /** Start day of the interval. */
  private Day startDay;

  /** End day of the interval. */
  private Day endDay;

  /**
   * Instantiates DayInterval with start day and end day.
   * 
   * @param startYear Interval's start year.
   * @param startMonth Interval's start month.
   * @param startDay Interval's start day.
   * @param endYear Interval's end year.
   * @param endMonth Interval's end month.
   * @param endDay Interval's end day.
   * @throws IllegalIntervalException If start day is later than end day.
   */
  public DayInterval(String startYear, String startMonth, String startDay, String endYear,
      String endMonth, String endDay) throws IllegalIntervalException {
    super("Day");
    IntervalUtility utility = IntervalUtility.getInstance();
    this.startDay = utility.getDay(startYear, startMonth, startDay);
    this.endDay = utility.getDay(endYear, endMonth, endDay);
    if (this.startDay.compareTo(this.endDay) > 0) {
      throw new IllegalIntervalException("Start day " + this.startDay + " is later than end day "
          + this.endDay);
    }
  }

  /**
   * Instantiates DayInterval with start day and end day.
   * 
   * @param startDay startday
   * @param endDay endday
   * @throws IllegalIntervalException If start day is later than end day.
   */
  public DayInterval(Day startDay, Day endDay) throws IllegalIntervalException {
    super("Day");
    this.startDay = startDay;
    this.endDay = endDay;
    if (this.startDay.compareTo(this.endDay) > 0) {
      throw new IllegalIntervalException("Start day " + this.startDay + " is later than end day "
          + this.endDay);
    }
  }

  /**
   * Getss start day of the day interval.
   * 
   * @return Start day of the interval.
   */
  public Day getStartDay() {
    return this.startDay;
  }

  /**
   * Gets end day of the day interval.
   * 
   * @return End day of the day interval.
   */
  public Day getEndDay() {
    return this.endDay;
  }

  /**
   * Gets the day interator the period.
   * 
   * @return Iterator over a period.
   */
  public Iterator iterator() {
    return new DayIterator(this);
  }

  /**
   * String representaiton of the DayInterval object.
   * 
   * @return Day interval string
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer(55);
    buffer.append("Day Interval : ");
    buffer.append(this.startDay);
    buffer.append(" ~ ");
    buffer.append(this.endDay);

    return buffer.toString();
  }

  /**
   * Gets the hash code.
   * 
   * @return The hash code.
   */
  public int hashCode() {
    return this.startDay.hashCode() / 2 + this.endDay.hashCode() / 2;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * 
   * @param obj Another instance of <code>DayInterval</code>.
   * 
   * @return True if they are equal, false otherwise.
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof DayInterval)) {
      return false;
    }
    DayInterval another = (DayInterval) obj;
    return this.startDay.equals(another.startDay) && this.endDay.equals(another.endDay);
  }
}

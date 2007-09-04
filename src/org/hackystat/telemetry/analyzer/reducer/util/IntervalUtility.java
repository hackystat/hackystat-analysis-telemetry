package org.hackystat.telemetry.analyzer.reducer.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hackystat.telemetry.analyzer.util.Day;
import org.hackystat.telemetry.analyzer.util.Month;
import org.hackystat.telemetry.analyzer.util.TimePeriod;
import org.hackystat.telemetry.analyzer.util.Week;
import org.hackystat.telemetry.analyzer.util.selector.interval.DayInterval;
import org.hackystat.telemetry.analyzer.util.selector.interval.IllegalIntervalException;
import org.hackystat.telemetry.analyzer.util.selector.interval.Interval;
import org.hackystat.telemetry.analyzer.util.selector.interval.MonthInterval;
import org.hackystat.telemetry.analyzer.util.selector.interval.WeekInterval;

/**
 * Interval utility.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class IntervalUtility {
 
  /**
   * Gets time periods in the interval.
   * 
   * @param interval The interval.
   * @return A ordered list of <code>IntervalUtility.Period</code> objects.
   * 
   * @throws IllegalIntervalException If the interval is not recognized.
   */
  public static List<IntervalUtility.Period> getPeriods(Interval interval) 
                                                      throws IllegalIntervalException {
    ArrayList<IntervalUtility.Period> list = new ArrayList<IntervalUtility.Period>();
    
    if (interval instanceof DayInterval) {
      for (Iterator i = interval.iterator(); i.hasNext(); ) {
        Day day = (Day) i.next();
        list.add(new Period(day));
      }
    }
    else if (interval instanceof WeekInterval) {
      for (Iterator i = interval.iterator(); i.hasNext(); ) {
        Week week = (Week) i.next();
        list.add(new Period(week));
      }      
    }
    else if (interval instanceof MonthInterval) {
      for (Iterator i = interval.iterator(); i.hasNext(); ) {
        Month month = (Month) i.next();
        list.add(new Period(month));
      }  
    }
    else {
      throw new IllegalIntervalException("Unknown interval.");
    }
    
    return list;
  }
  
  /**
   * Time period, which is either day, week or month.
   * 
   * @author (Cedric) Qin Zhang.
   * @version $Id: IntervalUtility.java,v 1.1.1.1 2005/10/20 23:56:49 johnson Exp $
   */
  public static class Period {
    
    private TimePeriod rawPeriod;
    private Day startDay;
    private Day endDay;
    
    /**
     * Constructs this instance from day.
     * 
     * @param day The day.
     */
    private Period(Day day) {
      this.rawPeriod = day;
      this.startDay = day;
      this.endDay = day;
    }
    
    /**
     * Constructs this instance from week.
     * 
     * @param week The week.
     */
    private Period(Week week) {
      this.rawPeriod = week;
      this.startDay = week.getFirstDay();
      this.endDay = week.getLastDay();
    }
    
    /**
     * Constructs this instance from month.
     * 
     * @param month The month.
     */
    private Period(Month month) {
      this.rawPeriod = month;
      this.startDay = month.getFirstDay();
      this.endDay = month.getLastDay();
    }
    
    /**
     * Gets the time period wrapped by this instance.
     * 
     * @return The wrapped <code>TimePeriod</code> object.
     */
    public TimePeriod getTimePeriod() {
      return this.rawPeriod;
    }
    
    /**
     * Gets the start day of the period, inclusive.
     *
     * @return The start day.
     */
    public Day getStartDay() {
      return this.startDay;
    }
    
    /**
     * Gets the end day of the period, inclusive.
     *
     * @return The start day.
     */
    public Day getEndDay() {
      return this.endDay;
    }
    
    /**
     * Gets the number of day in this period, including both start day and end day.
     * 
     * @return The number of days.
     */
    public int getNumOfDays() {
      return Day.daysBetween(this.startDay, this.endDay) + 1; 
    }
  }
}

package org.hackystat.telemetry.analyzer.reducer.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hackystat.utilities.time.interval.DayInterval;
import org.hackystat.utilities.time.interval.MonthInterval;
import org.hackystat.utilities.time.interval.WeekInterval;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.time.period.Month;
import org.hackystat.utilities.time.period.Week;
import org.junit.Test;

/**
 * Test suite for <code>IntervalUtility</code>.
 * 
 * @author (Cedric) Qin Zhang, Philip Johnson
 */
public class TestIntervalUtility {
  
  private String year2002 = "2002";
  

  /**
   * Tests with day interval.
   * 
   * @throws Exception If test fails.
   */
  @Test
  public void testWithDayInterval() throws Exception {
    DayInterval interval = new DayInterval(year2002, "0", "1", year2002, "0", "3");
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);
    assertEquals(3, periods.size());
    
    Day firstDay = Day.getInstance(2002, 0, 1);
    for (int i = 0; i < periods.size(); i++) {
      Day expected = firstDay.inc(i);
      IntervalUtility.Period period = periods.get(i);
      assertEquals(expected, period.getTimePeriod());
      assertEquals(expected, period.getStartDay());
      assertEquals(expected, period.getEndDay());
      assertEquals(1, period.getNumOfDays());
    }
  }
  
  /**
   * Tests with week interval.
   * 
   * @throws Exception If test fails.
   */
  public void testWithWeekInterval() throws Exception {
    WeekInterval interval = new WeekInterval("06-Jan-2002 to 12-Jan-2002",
                                             "20-Jan-2002 to 26-Jan-2002");
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);
    assertEquals(3, periods.size());
    
    Week currentWeek = new Week(Day.getInstance(2002, 0, 6));
    for (int i = 0; i < periods.size(); i++) {
      IntervalUtility.Period period = periods.get(i);
      assertEquals(currentWeek, period.getTimePeriod());
      assertEquals(currentWeek.getFirstDay(), period.getStartDay());
      assertEquals(currentWeek.getLastDay(), period.getEndDay());
      assertEquals(7, period.getNumOfDays());
      currentWeek = currentWeek.inc();
    }       
  }
  
  /**
   * Tests with month interval.
   * 
   * @throws Exception If test fails.
   */
  public void testWithMonthInterval() throws Exception {
    MonthInterval interval = new MonthInterval(year2002, "0", year2002, "3");//jan to april
    List<IntervalUtility.Period> periods = IntervalUtility.getPeriods(interval);
    assertEquals(4, periods.size());
    
    Month currentMonth = new Month(2002, 0);
    for (int i = 0; i < periods.size(); i++) {
      IntervalUtility.Period period = periods.get(i);
      assertEquals(currentMonth, period.getTimePeriod());
      assertEquals(currentMonth.getFirstDay(), period.getStartDay());
      assertEquals(currentMonth.getLastDay(), period.getEndDay());
      
      //TODO: fail in PST when day light saving is on!!!  //APR error
      //               return 30                   return 29
      assertEquals(currentMonth.getNumOfDays(), period.getNumOfDays()); 
      currentMonth = currentMonth.inc();
    }    
  }
}

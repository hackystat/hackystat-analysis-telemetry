package org.hackystat.telemetry.analyzer.model;

import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.time.period.Month;
import org.hackystat.utilities.time.period.TimePeriod;
import org.hackystat.utilities.time.period.Week;

import junit.framework.TestCase;

/**
 * Test suite for <code>TelemetryDataPoint</code>.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TestTelemetryDataPoint extends TestCase {

  /**
   * Tests properties.
   */
  public void testProperties() {
    TimePeriod period = Day.getInstance(2002, 1, 1);
    Number value = new Integer(12);
    TelemetryDataPoint dataPoint = new TelemetryDataPoint(period, value);
    assertEquals(period, dataPoint.getPeriod());
    assertEquals(value, dataPoint.getValue());
  }

  /**
   * Tests <code>equals</code> method.
   */
  public void testEquals() {
    Day day1 = Day.getInstance(2002, 1, 1);
    Day day2 = Day.getInstance(2002, 1, 1);
    Day day3 = Day.getInstance(2002, 2, 2);
    Number value = new Integer(12);
    assertEquals(new TelemetryDataPoint(day1, value), new TelemetryDataPoint(day2, value));
    assertEquals(new TelemetryDataPoint(day1, value).hashCode(),
        new TelemetryDataPoint(day2, value).hashCode());
    assertEquals(new TelemetryDataPoint(day1, null), new TelemetryDataPoint(day2, null));
    assertEquals(new TelemetryDataPoint(day1, null).hashCode(), new TelemetryDataPoint(day2, null)
        .hashCode());

    assertNotEquals(new TelemetryDataPoint(day1, value), new TelemetryDataPoint(day2, null));
    assertNotEquals(new TelemetryDataPoint(day1, null), new TelemetryDataPoint(day2, value));
    assertNotEquals(new TelemetryDataPoint(day1, value), new TelemetryDataPoint(day3, value));
    assertNotEquals(new TelemetryDataPoint(day3, value), new TelemetryDataPoint(day2, value));

    Week week1 = new Week(day1);
    Week week2 = new Week(day2);
    assertEquals(new TelemetryDataPoint(week1, value), new TelemetryDataPoint(week2, value));
    assertEquals(new TelemetryDataPoint(week1, value).hashCode(), new TelemetryDataPoint(week2,
        value).hashCode());

    Month month1 = new Month(2002, 1);
    Month month2 = new Month(2002, 1);
    assertEquals(new TelemetryDataPoint(month1, value), new TelemetryDataPoint(month2, value));
    assertEquals(new TelemetryDataPoint(month1, value).hashCode(), new TelemetryDataPoint(month2,
        value).hashCode());
  }

  /**
   * Asserts that two objects are not equals.
   * 
   * @param o1 Object 1.
   * @param o2 Object 2.
   */
  private void assertNotEquals(Object o1, Object o2) {
    assertFalse(o1.equals(o2));
  }
}

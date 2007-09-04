package org.hackystat.telemetry.analyzer.model;

import java.util.List;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.util.Day;

/**
 * Test suite for <code>TelemetryStream</code>.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TestTelemetryStream extends TestCase {

  /**
   * Test case.
   * 
   * @throws Exception If test fails.
   */
  public void testAll() throws Exception {
    TelemetryStream telemetryStream = new TelemetryStream("TestingStream");
    assertEquals("TestingStream", telemetryStream.getTag());

    Day day = Day.getInstance(2003, 1, 1);
    telemetryStream.addDataPoint(new TelemetryDataPoint(day.inc(1), new Integer(1)));
    telemetryStream.addDataPoint(new TelemetryDataPoint(day, new Integer(0)));
    telemetryStream.addDataPoint(new TelemetryDataPoint(day.inc(2), new Integer(2)));

    assertEquals(new Integer(0), telemetryStream.getDataPointValue(day));
    assertEquals(new Integer(1), telemetryStream.getDataPointValue(day.inc(1)));
    assertEquals(new Integer(2), telemetryStream.getDataPointValue(day.inc(2)));

    List list = telemetryStream.getDataPoints();
    assertEquals(day, ((TelemetryDataPoint) list.get(0)).getPeriod());
    assertEquals(new Integer(0), ((TelemetryDataPoint) list.get(0)).getValue());
    assertEquals(day.inc(1), ((TelemetryDataPoint) list.get(1)).getPeriod());
    assertEquals(new Integer(1), ((TelemetryDataPoint) list.get(1)).getValue());
    assertEquals(day.inc(2), ((TelemetryDataPoint) list.get(2)).getPeriod());
    assertEquals(new Integer(2), ((TelemetryDataPoint) list.get(2)).getValue());
  }
  
  /**
   * Asserts equality for two telemetry stream objects.
   * 
   * @param stream1 Telemetry stream1.
   * @param stream2 Telemetry stream2.
   * 
   * @throws Exception If the two streams are not the same.
   */
  static void assertEquals(TelemetryStream stream1, TelemetryStream stream2) throws Exception {
    assertEquals(stream1.getTag(), stream2.getTag());
    List dataPoints1 = stream1.getDataPoints();
    List dataPoints2 = stream2.getDataPoints();
    assertEquals(dataPoints1.size(), dataPoints2.size());
    
    for (int i = 0; i < dataPoints1.size(); i++) {
      TelemetryDataPoint dp1 = (TelemetryDataPoint) dataPoints1.get(i);
      TelemetryDataPoint dp2 = (TelemetryDataPoint) dataPoints2.get(i);
      assertEquals(dp1.getPeriod(), dp2.getPeriod());
      
      //don't use dp1.equals(dp2), we allow some error margin due to compute rounding.
      Number value1 = dp1.getValue();
      Number value2 = dp2.getValue();
      if (value1 == null && value2 == null) {
        //ok, do nothing
      }
      else if (value1 != null && value2 != null) {
        assertEquals(value1.doubleValue(), value2.doubleValue(), 0.0001);
      }
      else {
        throw new Exception("One data point has value null, while the other has non-null value.");
      }
    }
  }
}

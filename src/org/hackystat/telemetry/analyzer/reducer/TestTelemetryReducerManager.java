package org.hackystat.telemetry.analyzer.reducer;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

/**
 * Test suite for TelemetryReducerManager.
 * 
 * @author (Cedric) Qin Zhang, Philip Johnson
 */
public class TestTelemetryReducerManager {

  /** How many stock reducer implementations do you have? */
  private static final int numberOfStockReducer = 0;

  /**
   * Tests that built-in reducers can be found and defined.
   * 
   * @throws Exception If test fails.
   */
  @Test
  public void testFramework() throws Exception {
    TelemetryReducerManager manager = TelemetryReducerManager.getInstance();
    Collection<TelemetryReducerInfo> reducerInfo = manager.getAllReducerInfo();
    assertTrue(reducerInfo.size() >= TestTelemetryReducerManager.numberOfStockReducer);

    for (TelemetryReducerInfo theReducer : reducerInfo) {
      assertSame("Checking num reducers", theReducer, manager.getReducerInfo(theReducer.getName()));
      assertTrue("Checking that manager exists", manager.isReducer(theReducer.getName()));
    }
  }
}

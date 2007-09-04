package org.hackystat.telemetry.analyzer.reducer;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Test suite for <code>TelemetryReducerManager</code>.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TestTelemetryReducerManager extends TestCase {

  /** How many stock reducer implementation do you have? */
  private static final int numberOfStockReducer = 0;

  /**
   * Tests all methods except reducer-dispatch code.
   * 
   * @throws Exception If test fails.
   */
  public void testFramework() throws Exception {
    TelemetryReducerManager manager = TelemetryReducerManager.getInstance();
    Collection allReducerInfos = manager.getAllReducerInfo();
    assertTrue(allReducerInfos.size() >= TestTelemetryReducerManager.numberOfStockReducer);

    for (Iterator i = allReducerInfos.iterator(); i.hasNext();) {
      TelemetryReducerInfo theReducer = (TelemetryReducerInfo) i.next();
      assertSame("Checking num reducers", theReducer, manager.getReducerInfo(theReducer.getName()));
      assertTrue("Checking that manager exists", manager.isReducerExist(theReducer.getName()));
    }
  }
}

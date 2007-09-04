package org.hackystat.telemetry.analyzer.function;

import junit.framework.TestCase;

/**
 * Test suite for <code>TelemetryFunctionManager</code>.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TestTelemetryFunctionManager extends TestCase {

  /**
   * Test case.
   */
  public void testAll() {
    TelemetryFunctionManager manager = TelemetryFunctionManager.getInstance();
    
    String[] essentialFunctionNames = {"Add", "Sub", "Mul", "Div", "Idempotent"};
    
    for (int i = 0; i < essentialFunctionNames.length; i++) {
      String name = essentialFunctionNames[i];
      TelemetryFunctionInfo info = (TelemetryFunctionInfo) manager.getFunctionInfo(name);
      assertNotNull(info);
      assertEquals(info.getName(), info.getFunction().getName());
      assertTrue(manager.isFunctionExist(name));
      assertFalse(manager.getAllCustomFunctionInfo().contains(name));
    }
  }
}

package org.hackystat.telemetry.analyzer.evaluator;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.language.ast.Expression;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryStreamsDefinition;
import org.hackystat.telemetry.analyzer.language.parser.TelemetryLanguageParser;

/**
 * Test suite for <code>TelemetryExpressionEvaluator</code>.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TestTelemetryExpressionEvaluator extends TestCase {

  /**
   * Expressions containing constants only, so it can be evaluated without
   * sensor data.
   */
  private String[] expressions = new String[] {"10", "22", "2", "0-2", "11+22", "11-22", "11*22",
      "11/22", "11 + 10 * 20", "10 / (10 + 90)", "1 - ( 10 - (1+(2-3)*4) / (1 + 2 * 3) )",
      "1-2-3", "1/2/4"
  };
  
  /** Expected result of evaluation, must be of the same length as formulae. */
  private double[] expectedResults = new double[] {10, 22, 2, -2, 33, -11, 242, 0.5, 211, 0.1,
      -9.4285714285714285714285714285714, -4, 0.125};

  /**
   * Tests <code>evaluateImpl</code> with constants only.
   * 
   * @throws Exception If test fails.
   */
  public void testEvaluatorWithConstants() throws Exception {
    for (int i = 0; i < this.expressions.length; i++) {
      // construts a query contains all the formulae listed above.
      StringBuffer buffer = new StringBuffer(512);
      buffer.append("streams StreamForUnitTest() = { \"desc\", ");
      buffer.append(expressions[i]);
      buffer.append("};");
      // parse to generate TelemetryStreamsDef
      TelemetryStreamsDefinition streamsDef = TelemetryLanguageParser.parseStreamsDef(buffer
          .toString());
      Expression expression = streamsDef.getExpression();
      // evaluation the expression
      Object result = TelemetryEvaluator.resolveExpression(expression, null, null, null, null, null);
      // check with expected result
      Number expected = new Double(this.expectedResults[i]);
      assertEquals(expected.doubleValue(), ((Number) result).doubleValue(), 0.00001);
    }
  }
}

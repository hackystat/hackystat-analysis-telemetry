package org.hackystat.telemetry.language.parser;

import java.io.StringReader;

import junit.framework.TestCase;

import org.hackystat.telemetry.language.ast.Expression;
import org.hackystat.telemetry.language.ast.FunctionCall;
import org.hackystat.telemetry.language.ast.NumberConstant;
import org.hackystat.telemetry.language.ast.ReducerCall;
import org.hackystat.telemetry.language.ast.StringConstant;
import org.hackystat.telemetry.language.ast.Variable;
import org.hackystat.telemetry.language.parser.impl.TelemetryLanguageParserImpl;

/**
 * Test suite for internal parser implementations.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TestParserInternals extends TestCase {
  
  /**
   * Tests parsing function calls.
   * 
   * @throws Exception If test fails.
   */
  public void testParsingFunctionCall1() throws Exception {
    String input = "Idempotent(var1, Reducer(), \"p\")";
    TelemetryLanguageParserImpl parser = new TelemetryLanguageParserImpl(new StringReader(input));
    FunctionCall functionCall = (FunctionCall) parser.call();
    assertEquals("Idempotent", functionCall.getFunctionName());
    assertEquals(3, functionCall.getParameters().length);
    //parameter 0
    Variable var = (Variable) functionCall.getParameters()[0];
    assertEquals("var1", var.getName());
    //parameter 1
    ReducerCall reducerCall = (ReducerCall) functionCall.getParameters()[1];
    assertEquals("Reducer", reducerCall.getReducerName());
    assertEquals(0, reducerCall.getParameters().length);
    //parameter 3
    StringConstant constant = (StringConstant) functionCall.getParameters()[2];
    assertEquals("p", constant.getValue());
  }
  
  /**
   * Tests parsing function calls.
   * 
   * @throws Exception If test fails.
   */
  public void testParsingFunctionCall2() throws Exception {
    String input = "Sub(Add(Reducer1(a), Reducer2()), 10)";
    TelemetryLanguageParserImpl parser = new TelemetryLanguageParserImpl(new StringReader(input));
    FunctionCall functionCall = (FunctionCall) parser.call();
    assertEquals("Sub", functionCall.getFunctionName());
    assertEquals(2, functionCall.getParameters().length);
    //parameter 0
    FunctionCall functionCall_0 = (FunctionCall) functionCall.getParameters()[0];
    assertEquals("Add", functionCall_0.getFunctionName());
    assertEquals(2, functionCall_0.getParameters().length);
    
    ReducerCall reducerCall = (ReducerCall) functionCall_0.getParameters()[0];
    assertEquals("Reducer1", reducerCall.getReducerName());
    assertEquals(1, reducerCall.getParameters().length);   
    
    reducerCall = (ReducerCall) functionCall_0.getParameters()[1];
    assertEquals("Reducer2", reducerCall.getReducerName());
    assertEquals(0, reducerCall.getParameters().length);
    
    //parameter 1
    NumberConstant constant = (NumberConstant) functionCall.getParameters()[1];
    assertEquals(new Integer(10), constant.getValue());
  }
  
  /**
   * Tests parsing function calls.
   * 
   * @throws Exception If test fails.
   */
  public void testParsingFunctionCall3() throws Exception {
    String input = "Filter(WorkspaceActiveTime(filePattern, cumulative, memberEmail), \"GE\" , 0)";
    TelemetryLanguageParserImpl parser = new TelemetryLanguageParserImpl(new StringReader(input));
    FunctionCall functionCall = (FunctionCall) parser.call();
    assertEquals("Filter", functionCall.getFunctionName());
    Expression[] params = functionCall.getParameters();
    assertEquals(3, params.length);
    assertEquals("WorkspaceActiveTime", ((ReducerCall) params[0]).getReducerName());
    assertEquals("GE", ((StringConstant) params[1]).getValue());
    assertEquals(new Integer(0), ((NumberConstant) params[2]).getValue());
  }
  
  /**
   * Tests parsing function calls.
   * 
   * @throws Exception If test fails.
   */
  public void testParsingFunctionCall4() throws Exception {
    String input = "Function(  (Reducer1(var) + Reducer2()) / Function2(Reducer3()) * 100   )";
    TelemetryLanguageParserImpl parser = new TelemetryLanguageParserImpl(new StringReader(input));
    FunctionCall functionCall = (FunctionCall) parser.call();
    assertEquals("Function", functionCall.getFunctionName());
    Expression[] params = functionCall.getParameters();
    assertEquals(1, params.length);
    
    FunctionCall mulFunctionCall = (FunctionCall) params[0];
    assertEquals("Mul", mulFunctionCall.getFunctionName());
    assertEquals(2, mulFunctionCall.getParameters().length);
  }
  
  /**
   * Tests parsing reducer calls.
   * 
   * @throws Exception If test fails.
   */
  public void testParsingReductionCalls() throws Exception {
    String input = "R12()";
    TelemetryLanguageParserImpl parser = new TelemetryLanguageParserImpl(new StringReader(input));
    ReducerCall reducerCall = (ReducerCall) parser.call();
    assertEquals("R12", reducerCall.getReducerName());
    assertEquals(0, reducerCall.getParameters().length);

    input = "A(p)";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    reducerCall = (ReducerCall) parser.call();
    assertEquals("A", reducerCall.getReducerName());
    assertEquals(1, reducerCall.getParameters().length);
    Variable param1 = (Variable) reducerCall.getParameters()[0];
    assertEquals("p", param1.getName());

    input = "B(\"p\")";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    reducerCall = (ReducerCall) parser.call();
    assertEquals("B", reducerCall.getReducerName());
    assertEquals(1, reducerCall.getParameters().length);
    StringConstant param2 = (StringConstant) reducerCall.getParameters()[0];
    assertEquals("p", param2.getValue());   
    
    input = "r12(t1, \"t2a,t2b, t2c\", t3)";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    reducerCall = (ReducerCall) parser.call();
    assertEquals("r12", reducerCall.getReducerName());
    assertEquals(3, reducerCall.getParameters().length);
    Variable param3a = (Variable) reducerCall.getParameters()[0];
    assertEquals("t1", param3a.getName());
    StringConstant param3b = (StringConstant) reducerCall.getParameters()[1];
    assertEquals("t2a,t2b, t2c", param3b.getValue());
    Variable param3c = (Variable) reducerCall.getParameters()[2];
    assertEquals("t3", param3c.getName());
    
    input = "r12(\"t1\", t2, \"t3\")";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    parser.call();
  }
  
  /**
   * Tests parsing string.
   * 
   * @throws Exception If test fails.
   */
  public void testParsingString() throws Exception {
    String input = "\"abc\"";
    TelemetryLanguageParserImpl parser = new TelemetryLanguageParserImpl(new StringReader(input));
    assertEquals("abc", parser.string_constant());

    input = "\"a b c\"";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    assertEquals("a b c", parser.string_constant());

    input = "\"This is,\t you know,\n just a common comments.\"";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    assertEquals("This is,\t you know,\n just a common comments.", parser.string_constant());

    input = "\"a_a-a@bbb.com\"";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    assertEquals("a_a-a@bbb.com", parser.string_constant());

    input = "\"\"";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    assertEquals("", parser.string_constant());
  }

  /**
   * Tests parsing of StreamsReference portion.
   * 
   * @throws Exception If test fails.
   */
  public void testParsingStreamsReference() throws Exception {
    String input = "A(\"b1@ggg.com\", b2)";
    TelemetryLanguageParserImpl parser = new TelemetryLanguageParserImpl(new StringReader(input));
    parser.streams_reference(); // parsing is ok if there is no exception.
    input = "A(\"b1@ggg.com\", b2)";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    parser.chart_reference(); // parsing is ok if there is no exception.
    input = "A(c1, c2)";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    parser.streams_reference();
  }
  
  /**
   * Tests parsing number constants.
   * @throws Exception If test fails.
   */
  public void testParsingNumberConstant() throws Exception {
    String input = "10";
    TelemetryLanguageParserImpl parser = new TelemetryLanguageParserImpl(new StringReader(input));
    Number number = parser.number_constant();
    assertTrue(number instanceof Integer);
    assertEquals(new Integer(10), number);
    
    input = "-10";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    number = parser.number_constant();
    assertTrue(number instanceof Integer);
    assertEquals(new Integer(-10), number);
    
    input = "0";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    number = parser.number_constant();
    assertTrue(number instanceof Integer);
    assertEquals(new Integer(0), number);
    
    input = "1.2";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    number = parser.number_constant();
    assertTrue(number instanceof Double);
    assertEquals(new Double(1.2), number);
    
    input = "-1.2";
    parser = new TelemetryLanguageParserImpl(new StringReader(input));
    number = parser.number_constant();
    assertTrue(number instanceof Double);
    assertEquals(new Double(-1.2), number);
  }
}

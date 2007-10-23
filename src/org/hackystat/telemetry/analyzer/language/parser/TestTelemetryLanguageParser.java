package org.hackystat.telemetry.analyzer.language.parser;

import java.util.List;

import junit.framework.TestCase;

import org.hackystat.telemetry.analyzer.language.ast.ChartReference;
import org.hackystat.telemetry.analyzer.language.ast.DrawCommand;
import org.hackystat.telemetry.analyzer.language.ast.FunctionCall;
import org.hackystat.telemetry.analyzer.language.ast.NumberConstant;
import org.hackystat.telemetry.analyzer.language.ast.ReducerCall;
import org.hackystat.telemetry.analyzer.language.ast.StreamsReference;
import org.hackystat.telemetry.analyzer.language.ast.StringConstant;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartYAxisDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryReportDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryStreamsDefinition;
import org.hackystat.telemetry.analyzer.language.ast.Variable;
import org.hackystat.telemetry.analyzer.language.ast.YAxisReference;

/**
 * Test suite for <code>TelemetryQueryParser</code>.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TestTelemetryLanguageParser extends TestCase {

  /**
   * Tests <code>parseStreamsDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseSteamsDef1() throws Exception {
    String strDef 
    = "streams myStreamsDef-A() =  {\"This is description, oh yeah.\", MyReducer()};";
    TelemetryStreamsDefinition def = TelemetryLanguageParser.parseStreamsDef(strDef);
    assertEquals("myStreamsDef-A", def.getName());
    assertEquals("This is description, oh yeah.", def.getDescription());
    assertEquals(0, def.getVariables().length);
    ReducerCall reducerCall = (ReducerCall) def.getExpression();
    assertEquals("MyReducer", reducerCall.getReducerName());
    assertEquals(0, reducerCall.getParameters().length);
    
    // another one
    strDef = "streams A (  )  = {\"description\", ActiveTime(  \"**\")  };";
    def = TelemetryLanguageParser.parseStreamsDef(strDef);
    assertEquals("A", def.getName());
    assertEquals("description", def.getDescription());
    assertEquals(0, def.getVariables().length);
    reducerCall = (ReducerCall) def.getExpression();
    assertEquals("ActiveTime", reducerCall.getReducerName());
    assertEquals(1, reducerCall.getParameters().length);
    StringConstant param = (StringConstant) reducerCall.getParameters()[0];
    assertEquals("**", param.getValue());
  }

  /**
   * Tests <code>parseStreamsDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseSteamsDef2() throws Exception {
    String strDef = "streams myStreamsDef-A ( t1) =  { \"description\" ,  MyReducer()};";
    TelemetryStreamsDefinition def = TelemetryLanguageParser.parseStreamsDef(strDef);
    assertEquals("myStreamsDef-A", def.getName());
    assertEquals("description", def.getDescription());

    Variable[] vars = def.getVariables();
    assertEquals(1, vars.length);
    assertEquals("t1", vars[0].getName());

    ReducerCall reducerCall = (ReducerCall) def.getExpression();
    assertEquals("MyReducer", reducerCall.getReducerName());
    assertEquals(0, reducerCall.getParameters().length);
  }

  /**
   * Tests <code>parseStreamsDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseSteamsDef3() throws Exception {
    String strDef = "streams myStreamsDef_A() = {\"description\", 1+2*((0-3-4)/5)-6};";
    TelemetryStreamsDefinition def = TelemetryLanguageParser.parseStreamsDef(strDef);
    assertEquals("myStreamsDef_A", def.getName());
    assertEquals("description", def.getDescription());
    assertEquals(0, def.getVariables().length);

    FunctionCall root = (FunctionCall) def.getExpression();
    assertEquals("Sub", root.getFunctionName());
    assertEquals(2, root.getParameters().length);

    FunctionCall left = (FunctionCall) root.getParameters()[0];
    assertEquals("Add", left.getFunctionName());
    assertEquals(2, left.getParameters().length);
    
    NumberConstant right = (NumberConstant) root.getParameters()[1];
    assertEquals(new Integer(6), right.getValue());
  }

  /**
   * Tests <code>parseStreamsDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseSteamsDef4() throws Exception {
    String strDef = "streams myStreamsDef()= {\"Desc Field\", "
        + "Reducer1(\"A_a-b@b.com\", \"b\", \"**/*.java\","
        + "\"Ccc,DDD, eee \")/(Reducer1(\"C:\\test\\*.java\", \"true\")+Reducer2())};";
    // no space allowed between C and cc, e.g. "C cc"
    TelemetryStreamsDefinition def = TelemetryLanguageParser.parseStreamsDef(strDef);
    assertEquals("myStreamsDef", def.getName());
    assertEquals("Desc Field", def.getDescription());

    //Expression = Reducer1 / (Reducer1 + Reducer2)
    FunctionCall root = (FunctionCall) def.getExpression();
    assertEquals("Div", root.getFunctionName());
    assertEquals(2, root.getParameters().length);
    
    //left = Reducer1
    ReducerCall left = (ReducerCall) root.getParameters()[0];
    assertEquals("Reducer1", left.getReducerName());
    assertEquals(4, left.getParameters().length);
    assertEquals("A_a-b@b.com", ((StringConstant) left.getParameters()[0]).getValue());
    assertEquals("b", ((StringConstant) left.getParameters()[1]).getValue());
    assertEquals("**/*.java", ((StringConstant) left.getParameters()[2]).getValue());
    assertEquals("Ccc,DDD, eee ", ((StringConstant) left.getParameters()[3]).getValue());

    //right = Reducer1 + Reducer2
    FunctionCall right = (FunctionCall) root.getParameters()[1];
    assertEquals("Add", right.getFunctionName());
    assertEquals(2, right.getParameters().length);
    
    ReducerCall right1 = (ReducerCall) right.getParameters()[0];
    assertEquals("Reducer1", right1.getReducerName());
    assertEquals(2, right1.getParameters().length);
    assertEquals("C:\\test\\*.java", ((StringConstant) right1.getParameters()[0]).getValue());
    assertEquals("true", ((StringConstant) right1.getParameters()[1]).getValue());

    ReducerCall right2 = (ReducerCall) right.getParameters()[1];
    assertEquals("Reducer2", right2.getReducerName());
    assertEquals(0, right2.getParameters().length);
  }

  /**
   * Tests <code>parseStreamsDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseSteamsDef5() throws Exception {
    String strdef = "stream CoverageStream()" // should use "streams" instead of
        // "stream"
        + "= {\"Test Coverage\", \"unit\", JavaCoverage(\"NumOfCoveredMethods\")"
        + "/ (JavaCoverage(\"NumOfCoveredMethods\")+JavaCoverage(\"NumOfUncoveredMethods\"))};";
    try {
      TelemetryLanguageParser.parseStreamsDef(strdef);
      fail("Error in telemetry language not caught.");
    }
    catch (ParsingException ex) {
      // expected.
    }
  }

  /**
   * Tests <code>parseStreamsDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseSteamsDef6() throws Exception {
    String strDef = "streams myStreamsDef-A() =  {\"description\", MyFunc(MyReducer())};";
    TelemetryStreamsDefinition def = TelemetryLanguageParser.parseStreamsDef(strDef);
    assertEquals("myStreamsDef-A", def.getName());
    assertEquals("description", def.getDescription());
    assertEquals(0, def.getVariables().length);

    FunctionCall functionCall = (FunctionCall) def.getExpression();
    assertEquals("MyFunc", functionCall.getFunctionName());
    assertEquals(1, functionCall.getParameters().length);
    
    ReducerCall reducerCall = (ReducerCall) functionCall.getParameters()[0];
    assertEquals("MyReducer", reducerCall.getReducerName());
    assertEquals(0, reducerCall.getParameters().length);

    // another one
    strDef = "streams A (  )  = {\"description\", Func(  ActiveTime(  \"**\"))  };";
    def = TelemetryLanguageParser.parseStreamsDef(strDef);// no exception is good enough.
  }
  
  /**
   * Tests <code>parseChartDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseChartDef1() throws Exception {
    String strDef 
    = "chart myChartsDef ()= {\"Title\", (Aa ( ), yAxisA()), (Bb (  ),yAxisB(var))  ," +
        " (Cc(),yAxisC(var, \"const\", 1, -1.1))};";
    TelemetryChartDefinition def = TelemetryLanguageParser.parseChartDef(strDef);
    assertEquals("myChartsDef", def.getName());
    assertEquals("Title", def.getTitle());
    assertEquals("Title", def.getDocString());
    assertEquals(0, def.getVariables().length);
    assertEquals(3, def.getSubCharts().size());
    
    TelemetryChartDefinition.SubChartDefinition subChart0 
        = (TelemetryChartDefinition.SubChartDefinition) def.getSubCharts().get(0);
    StreamsReference streamsRef0 = subChart0.getStreamsReference();
    assertEquals("Aa", streamsRef0.getName());
    assertEquals(0, streamsRef0.getParameters().length);
    YAxisReference yAxisRef0 = subChart0.getYAxisReference();
    assertEquals("yAxisA", yAxisRef0.getName());
    assertEquals(0, yAxisRef0.getParameters().length);

    TelemetryChartDefinition.SubChartDefinition subChart1 
        = (TelemetryChartDefinition.SubChartDefinition) def.getSubCharts().get(1);
    StreamsReference streamsRef1 = subChart1.getStreamsReference();
    assertEquals("Bb", streamsRef1.getName());
    assertEquals(0, streamsRef1.getParameters().length);
    YAxisReference yAxisRef1 = subChart1.getYAxisReference();
    assertEquals("yAxisB", yAxisRef1.getName());
    assertEquals(1, yAxisRef1.getParameters().length);
    assertEquals("var", ((Variable) yAxisRef1.getParameters()[0]).getName());
    
    TelemetryChartDefinition.SubChartDefinition subChart2 
        = (TelemetryChartDefinition.SubChartDefinition) def.getSubCharts().get(2);
    StreamsReference streamsRef2 = subChart2.getStreamsReference();
    assertEquals("Cc", streamsRef2.getName());
    assertEquals(0, streamsRef2.getParameters().length);
    YAxisReference yAxisRef2 = subChart2.getYAxisReference();
    assertEquals("yAxisC", yAxisRef2.getName());
    assertEquals(4, yAxisRef2.getParameters().length);
    assertEquals("var", ((Variable) yAxisRef2.getParameters()[0]).getName());
    assertEquals("const", ((StringConstant) yAxisRef2.getParameters()[1]).getValue());
    assertEquals(new Integer(1), ((NumberConstant) yAxisRef2.getParameters()[2]).getValue());
    assertEquals(new Double(-1.1), ((NumberConstant) yAxisRef2.getParameters()[3]).getValue());
  }

  /**
   * Tests <code>parseChartDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseChartDef2() throws Exception {
    String strDef = "chart myChartsDef(  t1,t2  ) = {\"Title. (1)Additional, Doc.\",  " 
        + " (Aa(t1, \"r2\"), yAxisA()), (Bb(),yAxisB()),(Cc(),yAxisC()) };";
    TelemetryChartDefinition def = TelemetryLanguageParser.parseChartDef(strDef);
    assertEquals("myChartsDef", def.getName());
    assertEquals("Title", def.getTitle());
    assertEquals("Title. (1)Additional, Doc.", def.getDocString());
    assertEquals(3, def.getSubCharts().size());
    
    // check template
    Variable[] vars = def.getVariables();
    assertEquals(2, vars.length);
    assertEquals("t1", vars[0].getName());
    assertEquals("t2", vars[1].getName());

    // check subcharts
    TelemetryChartDefinition.SubChartDefinition subChart0 
        = (TelemetryChartDefinition.SubChartDefinition) def.getSubCharts().get(0);
    StreamsReference streamsRef0 = subChart0.getStreamsReference();
    assertEquals("Aa", streamsRef0.getName());
    assertEquals(2, streamsRef0.getParameters().length);
    assertEquals("t1", ((Variable) streamsRef0.getParameters()[0]).getName());
    assertEquals("r2", ((StringConstant) streamsRef0.getParameters()[1]).getValue());    
    YAxisReference yAxisRef0 = subChart0.getYAxisReference();
    assertEquals("yAxisA", yAxisRef0.getName());

    TelemetryChartDefinition.SubChartDefinition subChart1 
        = (TelemetryChartDefinition.SubChartDefinition) def.getSubCharts().get(1);
    StreamsReference streamsRef1 = subChart1.getStreamsReference();
    assertEquals("Bb", streamsRef1.getName());
    assertEquals(0, streamsRef1.getParameters().length);
    YAxisReference yAxisRef1 = subChart1.getYAxisReference();
    assertEquals("yAxisB", yAxisRef1.getName());
    assertEquals(0, yAxisRef1.getParameters().length);

    TelemetryChartDefinition.SubChartDefinition subChart2 
        = (TelemetryChartDefinition.SubChartDefinition) def.getSubCharts().get(2);
    StreamsReference streamsRef2 = subChart2.getStreamsReference();
    assertEquals("Cc", streamsRef2.getName());
    assertEquals(0, streamsRef2.getParameters().length);
    YAxisReference yAxisRef2 = subChart2.getYAxisReference();
    assertEquals("yAxisC", yAxisRef2.getName());
    assertEquals(0, yAxisRef2.getParameters().length);
  }

  /**
   * Tests <code>parseChartDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseChartsDef3() throws Exception {
    String strDef = "chart JavaTestLoc() = {\"\", (Sloc(  \"**\"  ),yAxis())};";
    TelemetryChartDefinition def = TelemetryLanguageParser.parseChartDef(strDef);
    assertEquals("JavaTestLoc", def.getName());
    assertEquals("", def.getTitle());
    assertEquals("", def.getDocString());
    assertEquals(0, def.getVariables().length);
    assertEquals(1, def.getSubCharts().size());

    TelemetryChartDefinition.SubChartDefinition subChart0 
        = (TelemetryChartDefinition.SubChartDefinition) def.getSubCharts().get(0);
    StreamsReference streamsRef0 = subChart0.getStreamsReference();
    assertEquals("Sloc", streamsRef0.getName());
    assertEquals(1, streamsRef0.getParameters().length);
    assertEquals("**", ((StringConstant) streamsRef0.getParameters()[0]).getValue());    
    YAxisReference yAxisRef0 = subChart0.getYAxisReference();
    assertEquals("yAxis", yAxisRef0.getName());
  }

  /**
   * Tests <code>parseReportDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseReportDef1() throws Exception {
    String strDef = "report myReportDef () = {\"A Description, testing report.\", Aa() , Bb ( )};";
    TelemetryReportDefinition def = TelemetryLanguageParser.parseReportDef(strDef);
    assertEquals("myReportDef", def.getName());
    assertEquals("A Description, testing report", def.getTitle());
    assertEquals("A Description, testing report.", def.getDocString());
    
    List<ChartReference> nameRefs = def.getChartReferences();
    assertEquals(2, nameRefs.size());

    ChartReference ref0 = (ChartReference) nameRefs.get(0);
    assertEquals("Aa", ref0.getName());
    assertEquals(0, ref0.getParameters().length);

    ChartReference ref1 = (ChartReference) nameRefs.get(1);
    assertEquals("Bb", ref1.getName());
    assertEquals(0, ref1.getParameters().length);
  }

  /**
   * Tests <code>parseReportDef</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParseReportDef2() throws Exception {
    String strDef = "report myReportDef (t1,t2 )= {\".\",  " + " Aa(t1, \"r2\"), Bb(),Cc() };";
    TelemetryReportDefinition def = TelemetryLanguageParser.parseReportDef(strDef);
    assertEquals("myReportDef", def.getName());
    assertEquals("", def.getTitle());
    assertEquals(".", def.getDocString());
    
    // check template
    Variable[] vars = def.getVariables();
    assertEquals(2, vars.length);
    assertEquals("t1", vars[0].getName());
    assertEquals("t2", vars[1].getName());

    // check subcharts
    List<ChartReference> nameRefs = def.getChartReferences();
    assertEquals(3, nameRefs.size());

    ChartReference ref0 = (ChartReference) nameRefs.get(0);
    assertEquals("Aa", ref0.getName());
    assertEquals(2, ref0.getParameters().length);
    assertEquals("t1", ((Variable) ref0.getParameters()[0]).getName());
    assertEquals("r2", ((StringConstant) ref0.getParameters()[1]).getValue());

    ChartReference ref1 = (ChartReference) nameRefs.get(1);
    assertEquals("Bb", ref1.getName());
    assertEquals(0, ref1.getParameters().length);

    ChartReference ref2 = (ChartReference) nameRefs.get(2);
    assertEquals("Cc", ref2.getName());
    assertEquals(0, ref2.getParameters().length);
  }

  /**
   * Tests parsing of telemetry chart y-axis defintions.
   * 
   * @throws Exception If test fails.
   */
  public void testParsingChartYAxisDefinition() throws Exception {
    String input = "y-axis my() = {\"label\"};";
    TelemetryChartYAxisDefinition yAxis = TelemetryLanguageParser.parseChartYAxisDef(input);
    assertEquals("my", yAxis.getName());
    assertEquals(0, yAxis.getVariables().length);
    assertEquals("label", ((StringConstant) yAxis.getLabelParameter()).getValue());
    assertEquals(TelemetryChartYAxisDefinition.NUMBER_TYPE_AUTO, yAxis.getNumberType());
    assertEquals(true, yAxis.isAutoScale());
    assertEquals(null, yAxis.getLowerBound());
    assertEquals(null, yAxis.getUpperBound());

    input = "y-axis my(var) = {var, \"integer\"};";
    yAxis = TelemetryLanguageParser.parseChartYAxisDef(input);
    assertEquals("my", yAxis.getName());
    assertEquals(1, yAxis.getVariables().length);
    assertEquals("var", yAxis.getVariables()[0].getName());
    assertEquals("var", ((Variable) yAxis.getLabelParameter()).getName());
    assertEquals(TelemetryChartYAxisDefinition.NUMBER_TYPE_INTEGER, yAxis.getNumberType());
    assertEquals(true, yAxis.isAutoScale());
    assertEquals(null, yAxis.getLowerBound());
    assertEquals(null, yAxis.getUpperBound());
    
    input = "y-axis my() = {\"label\", \"double\", 0, 100};";
    yAxis = TelemetryLanguageParser.parseChartYAxisDef(input);
    assertEquals("my", yAxis.getName());
    assertEquals(0, yAxis.getVariables().length);
    assertEquals("label", ((StringConstant) yAxis.getLabelParameter()).getValue());
    assertEquals(TelemetryChartYAxisDefinition.NUMBER_TYPE_DOUBLE, yAxis.getNumberType());
    assertEquals(false, yAxis.isAutoScale());
    assertTrue(yAxis.getLowerBound() instanceof Integer);
    assertTrue(yAxis.getUpperBound() instanceof Integer);
    assertEquals(new Integer(0), yAxis.getLowerBound());
    assertEquals(new Integer(100), yAxis.getUpperBound());
    
    input = "y-axis my() = {\"label\", \"double\", -0.10, 1.10};";
    yAxis = TelemetryLanguageParser.parseChartYAxisDef(input);
    assertEquals("my", yAxis.getName());
    assertEquals(0, yAxis.getVariables().length);
    assertEquals("label", ((StringConstant) yAxis.getLabelParameter()).getValue());
    assertEquals(TelemetryChartYAxisDefinition.NUMBER_TYPE_DOUBLE, yAxis.getNumberType());
    assertEquals(false, yAxis.isAutoScale());
    assertTrue(yAxis.getLowerBound() instanceof Double);
    assertTrue(yAxis.getUpperBound() instanceof Double);
    assertEquals(new Double(-0.1), yAxis.getLowerBound());
    assertEquals(new Double(1.1), yAxis.getUpperBound());
  }  
  
  /**
   * Tests <code>parse</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParse1() throws Exception {
    String defs = "streams MyStreamsA()= {\"MyStreamsA Description\", MyReducerA()};\n"
        + "chart  MyChartA()  = {\"description \",(A(),a()), (B (),b()), (C  (),  c()) };\n"
        + "report  MyReportA ()= {\"description\", A(), B(), C()};\n"
        + "draw A ( ) ;\n"
        + "streams MyStreamsB() = {\"description\", MyreducerB()};\n"
        + "draw  B    (\"b1\", \"b2\");\n";
    List<TelemetryDefinition> list = TelemetryLanguageParser.parse(defs);
    assertEquals(6, list.size());
    assertTrue(list.get(0) instanceof TelemetryStreamsDefinition);
    assertTrue(list.get(1) instanceof TelemetryChartDefinition);
    assertTrue(list.get(2) instanceof TelemetryReportDefinition);
    assertTrue(list.get(3) instanceof DrawCommand);
    assertTrue(list.get(4) instanceof TelemetryStreamsDefinition);
    assertTrue(list.get(5) instanceof DrawCommand);

    // check DrawCommand, no need to check others since other test cases did
    // that.
    DrawCommand draw = (DrawCommand) list.get(3);
    assertEquals("A", draw.getTelemetryDefinitionName());
    assertEquals(0, draw.getParameters().length);

    draw = (DrawCommand) list.get(5);
    assertEquals("B", draw.getTelemetryDefinitionName());
    assertEquals(2, draw.getParameters().length);
    assertEquals("b1", ((StringConstant) draw.getParameters()[0]).getValue());
    assertEquals("b2", ((StringConstant) draw.getParameters()[1]).getValue());

    // another test
    defs = "streams S() = {\"active time\", ActiveTime(\"**\")};\n" 
         + "draw S();";
    list = TelemetryLanguageParser.parse(defs);
    assertEquals(2, list.size());
  }

  /**
   * Tests <code>parse</code> method.
   * 
   * @throws Exception If test fails.
   */
  public void testParse2() throws Exception {
    String defs = "streams CodeSizeStream(FilePattern)"
        + "= {\"Lines of Code\", \"unit\", JavaFileMetric(\"Sloc\", FilePattern)};"
        + "chart CodeSizeChart()"
        + "= {\"Code Size\", CodeSizeStream(\"**\"), CodeSizeStream(\"**/*.java\")};"
        + "stream CoverageStream()" // should use "streams" instead of "stream"
        + "= {\"Test Coverage\", \"unit\", JavaCoverage(\"NumOfCoveredMethods\")"
        + "/ (JavaCoverage(\"NumOfCoveredMethods\")+JavaCoverage(\"NumOfUncoveredMethods\"))};"
        + "chart CoverageChart()" + "= {\"Coverage\", CoverageStream()};"
        + "report MyReport() = {\"Test\", CodeSizeChart(), CoverageChart()};" + "draw MyReport();";

    try {
      TelemetryLanguageParser.parse(defs);
      fail("Error in telemetry language not caught!");
    }
    catch (ParsingException ex) {
      // expected
    }
  }
  
  /**
   * Tests recovering definition string operation.
   * 
   * @throws Exception If test fails.
   */
  public void testRecoveringDefinitionString() throws Exception {
    String defs = "streams MyStreamsA()= {\"description\", MyReducerA()};  "
      + "chart  MyChartA()  = \n{\"description \",(A(),a()), (B (),b()), \n(C  (),  c()) };   "
      + "report  MyReportA ()= {\"description\", A(), B(), C()};\n"
      + "draw A \n\n\n( ) ;\n"
      + "              streams MyStreamsB() = {\"description\", MyreducerB()};\n"
      + "draw  B    (\"b1\", \"b2\");\n";
    List<TelemetryDefinition> list = TelemetryLanguageParser.parse(defs);
    assertEquals(6, list.size());
    assertTrue(list.get(0) instanceof TelemetryStreamsDefinition);
    assertTrue(list.get(1) instanceof TelemetryChartDefinition);
    assertTrue(list.get(2) instanceof TelemetryReportDefinition);
    assertTrue(list.get(3) instanceof DrawCommand);
    assertTrue(list.get(4) instanceof TelemetryStreamsDefinition);
    assertTrue(list.get(5) instanceof DrawCommand);
    
    String[] expectedDefStrings = new String[] {
        "streams MyStreamsA()= {\"description\", MyReducerA()};",
        "chart  MyChartA()  = \n{\"description \",(A(),a()), (B (),b()), \n(C  (),  c()) };",
        "report  MyReportA ()= {\"description\", A(), B(), C()};",
        "draw A \n\n\n( ) ;",
        "streams MyStreamsB() = {\"description\", MyreducerB()};",
        "draw  B    (\"b1\", \"b2\");",
    };
    for (int i = 0; i < 6; i++) {
      TelemetryDefinition telemetryDefinition = (TelemetryDefinition) list.get(i);
      assertEquals(expectedDefStrings[i], telemetryDefinition.getDefinitionString());
    }
  }
}

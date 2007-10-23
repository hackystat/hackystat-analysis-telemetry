package org.hackystat.telemetry.analyzer.language.parser;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartYAxisDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryReportDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryStreamsDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TextPosition;
import org.hackystat.telemetry.analyzer.language.parser.impl.TelemetryLanguageParserImpl;

/**
 * Parser for telemetry language. All static methods in this class are thread-safe.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TelemetryLanguageParser {

  /**
   * Private construct to prevent this class from being instantiated.
   *
   */
  private TelemetryLanguageParser() {
  }
  
  /**
   * Parsing full telemetry streams and charts definitions.
   * 
   * @param string The definitions.
   * @return A list containing <code>TelemetryDefinition</code> objects in the order appeared.
   * 
   * @throws ParsingException If there is parsing error.
   */
  @SuppressWarnings("unchecked")
  public static List<TelemetryDefinition> parse(String string) throws ParsingException {
    try {
      TelemetryLanguageParserImpl parser 
          = new TelemetryLanguageParserImpl(new StringReader(string));
      List<TelemetryDefinition> defs = parser.all_input();
      TextExtractor textExtractor = new TextExtractor(string);
      for (TelemetryDefinition def : defs) {
        def.setDefinitionString(textExtractor.extract(def.getTextPosition()));       
      }
      return defs;
    }
    catch (Throwable ex) { // might get JavaCC TokenMgrError
      throw new ParsingException(ex);
    }
  }

  /**
   * Parses telemetry "streams" object definition.
   * 
   * @param string A telemetry query statement that defines a telemetry "streams" object. 
   *        Note that you should not include the final semi-colon.
   *        
   * @return Java object representation of the "streams" definition.
   * 
   * @throws ParsingException If the input is not grammatically correct.
   */
  public static TelemetryStreamsDefinition parseStreamsDef(String string) throws ParsingException {
    try {
      TelemetryLanguageParserImpl parser 
          = new TelemetryLanguageParserImpl(new StringReader(string));
      TelemetryStreamsDefinition def = parser.streams_statement_input();
      def.setDefinitionString(new TextExtractor(string).extract(def.getTextPosition()));
      return def;
    }
    catch (Throwable ex) { // might get JavaCC TokenMgrError
      throw new ParsingException(ex);
    }
  }

  /**
   * Parses telemetry "chart" object definition.
   * 
   * @param string A telemetry query statement that defines a telemetry "chart" object. 
   *        Note that you should not include the final semi-colon.
   *          
   * @return Java object representation of the "chart" definition.
   * 
   * @throws ParsingException If the input is not gramatically correct.
   */
  public static TelemetryChartDefinition parseChartDef(String string) throws ParsingException {
    try {
      TelemetryLanguageParserImpl parser 
          = new TelemetryLanguageParserImpl(new StringReader(string));
      TelemetryChartDefinition def = parser.chart_statement_input();
      def.setDefinitionString(new TextExtractor(string).extract(def.getTextPosition()));
      return def;
    }
    catch (Throwable ex) { // might get JavaCC TokenMgrError
      throw new ParsingException(ex);
    }
  }
  
  /**
   * Parses telemetry "chart y-axis" object definition.
   * 
   * @param string A telemetry query statement that defines a telemetry "chart y-axis" object. 
   *        Note that you should not include the final semi-colon.
   *          
   * @return Java object representation of the "chart y-axis" definition.
   * 
   * @throws ParsingException If the input is not gramatically correct.
   */
  public static TelemetryChartYAxisDefinition parseChartYAxisDef(String string) 
      throws ParsingException {
    try {
      TelemetryLanguageParserImpl parser 
          = new TelemetryLanguageParserImpl(new StringReader(string));
      TelemetryChartYAxisDefinition def = parser.chart_y_axis_statement_input();
      def.setDefinitionString(new TextExtractor(string).extract(def.getTextPosition()));
      return def;
    }
    catch (Throwable ex) { // might get JavaCC TokenMgrError
      throw new ParsingException(ex);
    }
  }

  /**
   * Parses telemetry "report" object definition.
   * 
   * @param string A telemetry query statement that defines a telemetry "report" object. 
   *        Note that you should not include the final semi-colon.
   *          
   * @return Java object representation of the "report" definition.
   * 
   * @throws ParsingException If the input is not gramatically correct.
   */
  public static TelemetryReportDefinition parseReportDef(String string) throws ParsingException {
    try {
      TelemetryLanguageParserImpl parser 
          = new TelemetryLanguageParserImpl(new StringReader(string));
      TelemetryReportDefinition def = parser.report_statement_input();
      def.setDefinitionString(new TextExtractor(string).extract(def.getTextPosition()));
      return def;
    }
    catch (Throwable ex) { // might get JavaCC TokenMgrError
      throw new ParsingException(ex);
    }
  }
  
  /**
   * Extracts text at given position.
   * 
   * @author (Cedric) Qin ZHANG
   * @version $Id$
   */
  private static class TextExtractor {
    
    private List<String> lines = new ArrayList<String>(4);
    
    /**
     * Constructs this instance.
     * 
     * @param input The input string.
     * @throws Exception If the input string cannot be parsed into lines.
     */
    private TextExtractor(String input) throws Exception {
      BufferedReader reader = new BufferedReader(new StringReader(input));
      String line = reader.readLine();
      while (line != null) {
        this.lines.add(line);
        line = reader.readLine();
      }
      reader.close();
    }
    
    /**
     * Extracts text at given position.
     * 
     * @param textPosition The position.
     * @return The extracted text.
     * @throws Exception If we cannot extract the text.
     */
    private String extract(TextPosition textPosition) throws Exception {
      //Note: the index in textPosition is 1-indexed.
      int startRow = textPosition.getBeginLine() - 1;
      int startCol = textPosition.getBeginColumn() - 1; 
      int endRow = textPosition.getEndLine() - 1;
      int endCol = textPosition.getEndColumn() - 1;
      
      if (startRow < endRow) {
        StringBuffer buffer = new StringBuffer(64);      
        String line = this.lines.get(startRow);
        buffer.append(line.substring(startCol)).append('\n');
        for (int i = startRow + 1; i < endRow; i++) {
          buffer.append(this.lines.get(i)).append('\n');
        }
        line = this.lines.get(endRow);
        buffer.append(line.substring(0, endCol + 1));
        return buffer.toString();
      }
      else if (startRow == endRow) {
        if (startCol <= endCol) {
          String line = this.lines.get(startRow);
          return line.substring(startCol, endCol + 1);
        }
        else {
          throw new Exception("Begin column is greater than end column.");
        }
      }
      else {
        throw new Exception("Begin line is greater than end line.");
      }
    }
  }
}

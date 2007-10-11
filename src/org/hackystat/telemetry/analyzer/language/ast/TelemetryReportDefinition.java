package org.hackystat.telemetry.analyzer.language.ast;


import java.util.ArrayList;
import java.util.List;

import org.hackystat.telemetry.analyzer.language.TelemetryLanguageException;

/**
 * Telemetry report definition, which contains one or more telemetry charts.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TelemetryReportDefinition extends TelemetryDefinition {

  private String title;
  private String docString;
  private Variable[] variables; //List<Variable>
  private List chartReferences; //List<ChartReference>

  /**
   * Constructs this instance.
   * 
   * @param name The name of the chart.
   * @param docString The doc string of the report. 
   *        The report title is extracted fromthe doc string.
   * @param variables The variables used in the expression. Variables are essentially holding
   *        places so that real value can be swapped in when the expression is evaluated.
   *        Null is valid if there is no variable used in this definition.
   * @param chartReferences A list of <code>ChartReference</code> objects referring to
   *        the telemetry charts that should be contained in this report.
   * @param textPosition The text position of the definition string in the input.
   * 
   * @throws TelemetryLanguageException If the variable list contains duplicated variable
   *         declaration or does not declare all variables needed by the referred telemetry
   *         charts.
   */
  public TelemetryReportDefinition(String name, String docString, Variable[] variables, 
      List<ChartReference> chartReferences, TextPosition textPosition) 
  throws TelemetryLanguageException {
    
    super(name, textPosition);

    this.docString = docString == null ? "" : docString;
    int commaIndex = this.docString.indexOf('.');
    this.title = commaIndex >= 0 ? this.docString.substring(0, commaIndex) : this.docString;
    
    this.variables = variables == null ? new Variable[0] : variables;
    this.chartReferences = (chartReferences == null)
    ? new ArrayList<ChartReference>() : chartReferences;

    // check whether all variable has unique name or not.
    //TreeSet varNames = new TreeSet();
//    for (Iterator i = this.variables.iterator(); i.hasNext();) {
//      String varName = ((Variable) i.next()).getName();
//      if (varNames.contains(varName)) {
//        throw new Exception("Duplicated variable name declaration.");
//      }
//      else {
//        varNames.add(varName);
//      }
//    }

    // check whether all templates used in chartDefs are declared
//    for (Iterator i = this.streamsRefs.iterator(); i.hasNext();) {
//      NameReference ref = (NameReference) i.next();
//      for (Iterator j = ref.getParameters().iterator(); j.hasNext();) {
//        Object param = j.next();
//        if (param instanceof TemplatedParameter) {
//          if (!set.contains(param)) {
//            throw new Exception("Undeclared parameter '"
//                + ((TemplatedParameter) param).getTemplateName() + "' used.");
//          }
//        }
//      }
//    }
  }

  /**
   * Gets the title of the chart.
   * 
   * @return The chart title.
   */
  public String getTitle() {
    return this.title;
  }
  
  /**
   * Gets the doc string for the chart.
   * 
   * @return The doc string.
   */
  public String getDocString() {
    return this.docString;
  }
  
  /**
   * Gets an array of variables used in the definition.
   * 
   * @return An array of <code>Varaible</code> objects. If there is no variable used,
   *         then an empty array is returned.
   */
  public Variable[] getVariables() {
    return this.variables;
  }

  /**
   * Gets all telemetry charts references in this report.
   * 
   * @return A list of <code>ChartReference</code> objects.
   */
  public List getChartReferences() {
    return this.chartReferences;
  }
}
package org.hackystat.telemetry.language.ast;

import java.util.ArrayList;
import java.util.List;

import org.hackystat.telemetry.language.TelemetryLanguageException;

/**
 * Telemetry chart definition. A chart contains one or more telemetry streams.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TelemetryChartDefinition extends TelemetryDefinition {

  private String title;
  private String docString;
  private Variable[] variables;
  private List subChartDefinitions; //List<TelemetryChartDefinition.SubChartDefinition>

  /**
   * Constructs this instance.
   * 
   * @param name The name of the chart.
   * @param docString The doc string of the chart. The chart title is extracted fromthe doc string.
   * @param variables The variables used in the expression. Variables are essentially holding
   *        places so that real value can be swapped in when the expression is evaluated.
   *        Null is valid if there is no variable used in this definition.
   * @param subChartDefinitions A list of <code>TelemetryChartDefinition.SubChartDefinition</code> 
   *        objects, referring to the sub charts that should be contained in this chart.
   * @param textPosition The text position of the definition string in the input.
   * 
   * @throws TelemetryLanguageException If the varaible array contains duplicated variable 
   *         declaration or does not declare all variables needed by the referred telemetry
   *         streams.
   */
  public TelemetryChartDefinition(String name, String docString, Variable[] variables, 
      List subChartDefinitions, TextPosition textPosition) throws TelemetryLanguageException {
    
    super(name, textPosition);
    
    this.docString = docString == null ? "" : docString;
    int commaIndex = this.docString.indexOf('.');
    this.title = commaIndex >= 0 ? this.docString.substring(0, commaIndex) : this.docString;
    
    this.variables = variables == null ? new Variable[0] : variables;
    this.subChartDefinitions = subChartDefinitions == null ? new ArrayList() : subChartDefinitions;

    // check whether all variable has unique name or not.
//    TreeSet varNames = new TreeSet();
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
   * Gets all sub-charts in this chart.
   * 
   * @return A list of <code>TelemetryChartDefinition.SubChartDefinition</code> objects.
   */
  public List getSubCharts() {
    return this.subChartDefinitions;
  }
  
  /**
   * Sub-chart definition. A telemtry chart can contain one or more sub-charts, with each sub-chart
   * having its own axis.
   *  
   * @author (Cedric) Qin ZHANG
   * @version $Id$
   */
  public static class SubChartDefinition {
    private StreamsReference streamsReference;
    private YAxisReference yAxsisReference;
    
    /**
     * Constructs this instance.
     * @param streamsReference A reference to streams definition.
     * @param yAxisReference A refrence to y-axis definition.
     */
    public SubChartDefinition(StreamsReference streamsReference, YAxisReference yAxisReference) {
      this.streamsReference = streamsReference;
      this.yAxsisReference = yAxisReference;
    }
    
    /**
     * Gets the reference to streams definition.
     * @return The reference to streams definition.
     */
    public StreamsReference getStreamsReference() {
      return this.streamsReference;
    }
    
    /**
     * Gets the reference to y-axis definition.
     * @return The reference to y-axis definition.
     */
    public YAxisReference getYAxisReference() {
      return this.yAxsisReference;
    }
  }
}

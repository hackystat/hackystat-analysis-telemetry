package org.hackystat.telemetry.analyzer.configuration;

import org.hackystat.telemetry.analyzer.language.TelemetryLanguageException;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartDefinition;
import org.hackystat.telemetry.analyzer.language.parser.TelemetryLanguageParser;
import org.hackystat.telemetry.analyzer.util.user.User;

/**
 * Information holder for a telemetry chart definition.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TelemetryChartDefinitionInfo extends TelemetryDefinitionInfo {

  private TelemetryChartDefinition chartDefinition;

  /**
   * Constructs this instance.
   * 
   * @param fullDefinitionString The defintion string.
   * @param owner The owner of this definition.
   * @param shareScope The share scope of this definition.
   * 
   * @throws TelemetryConfigurationException If the definition string cannot be parsed.
   */
  public TelemetryChartDefinitionInfo(String fullDefinitionString, User owner, 
      ShareScope shareScope) throws TelemetryConfigurationException {
    super(fullDefinitionString, owner, shareScope);
    try {
      this.chartDefinition = TelemetryLanguageParser.parseChartDef(fullDefinitionString);
    }
    catch (TelemetryLanguageException ex) {
      throw new TelemetryConfigurationException(ex);
    }
    //TODO: though the definition is syntatically correct, need to perform semantic validation!
  }
  
  /**
   * Constructs this instance.
   * 
   * @param chartDefinition The telemetry chart definition object.
   * @param owner The owner of this definition.
   * @param shareScope The share scope of this definition.
   * 
   * @throws TelemetryConfigurationException If the definition string cannot be parsed.
   */
  public TelemetryChartDefinitionInfo(TelemetryChartDefinition chartDefinition, User owner, 
      ShareScope shareScope) throws TelemetryConfigurationException {
    super(chartDefinition.getDefinitionString(), owner, shareScope);
    this.chartDefinition = chartDefinition;
  }
  
  /**
   * Gets the name of this telemetry definition.
   * 
   * @return The name.
   */
  public String getName() {
    return this.getChartDefinitionObject().getName();
  }
  
  /**
   * Gets telemetry definition type.
   * 
   * @return Telemetry definition type.
   */
  public TelemetryDefinitionType getType() {
    return TelemetryDefinitionType.CHART;
  }
  
  /**
   * Gets the abstract syntax tree representation of this telemetry chart.
   * 
   * @return The abstract syntax tree representation.
   */
  public TelemetryChartDefinition getChartDefinitionObject() {
    return this.chartDefinition;
  }
}
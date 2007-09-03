package org.hackystat.telemetry.configuration.model;

import org.hackystat.telemetry.util.user.User;
import org.hackystat.telemetry.configuration.TelemetryConfigurationException;
import org.hackystat.telemetry.language.TelemetryLanguageException;
import org.hackystat.telemetry.language.ast.TelemetryChartYAxisDefinition;
import org.hackystat.telemetry.language.parser.TelemetryLanguageParser;

/**
 * Information holder for a telemetry chart y-axis definition.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TelemetryChartYAxisDefinitionInfo extends TelemetryDefinitionInfo {

  private TelemetryChartYAxisDefinition chartYAxisDefinition;

  /**
   * Constructs this instance.
   * 
   * @param fullDefinitionString The defintion string.
   * @param owner The owner of this definition.
   * @param shareScope The share scope of this definition.
   * 
   * @throws TelemetryConfigurationException If the definition string cannot be parsed.
   */
  public TelemetryChartYAxisDefinitionInfo(String fullDefinitionString, User owner, 
      ShareScope shareScope) throws TelemetryConfigurationException {
    super(fullDefinitionString, owner, shareScope);
    try {
      this.chartYAxisDefinition = TelemetryLanguageParser.parseChartYAxisDef(fullDefinitionString);
    }
    catch (TelemetryLanguageException ex) {
      throw new TelemetryConfigurationException(ex);
    }
    //TODO: though the definition is syntatically correct, need to perform semantic validation!
  }

  /**
   * Constructs this instance.
   * 
   * @param chartYAxisDefinition The telemetry chart y-axis definition.
   * @param owner The owner of this definition.
   * @param shareScope The share scope of this definition.
   * 
   * @throws TelemetryConfigurationException If the definition string cannot be parsed.
   */
  public TelemetryChartYAxisDefinitionInfo(TelemetryChartYAxisDefinition chartYAxisDefinition, 
      User owner, ShareScope shareScope) throws TelemetryConfigurationException {
    super(chartYAxisDefinition.getDefinitionString(), owner, shareScope);
    this.chartYAxisDefinition = chartYAxisDefinition;
  }
  
  
  /**
   * Gets the name of this telemetry definition.
   * 
   * @return The name.
   */
  public String getName() {
    return this.chartYAxisDefinition.getName();
  }
  
  /**
   * Gets telemetry definition type.
   * 
   * @return Telemetry definition type.
   */
  public TelemetryDefinitionType getType() {
    return TelemetryDefinitionType.YAXIS;
  }
  
  /**
   * Gets the abstract syntax tree representation of this telemetry chart y-axis.
   * 
   * @return The abstract syntax tree representation.
   */
  public TelemetryChartYAxisDefinition getChartYAxisDefinitionObject() {
    return this.chartYAxisDefinition;
  }
}
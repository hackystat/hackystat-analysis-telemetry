package org.hackystat.telemetry.configuration;

import org.hackystat.telemetry.util.user.User;
import org.hackystat.telemetry.language.TelemetryLanguageException;
import org.hackystat.telemetry.language.ast.TelemetryReportDefinition;
import org.hackystat.telemetry.language.parser.TelemetryLanguageParser;

/**
 * Information holder for a telemetry report definition.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TelemetryReportDefinitionInfo extends TelemetryDefinitionInfo {

  private TelemetryReportDefinition reportDefinition;

  /**
   * Constructs this instance.
   * 
   * @param fullDefinitionString The defintion string.
   * @param owner The owner of this definition.
   * @param shareScope The share scope of this definition.
   * 
   * @throws TelemetryConfigurationException If the definition string cannot be parsed.
   */
  public TelemetryReportDefinitionInfo(String fullDefinitionString, User owner, 
      ShareScope shareScope) throws TelemetryConfigurationException {
    super(fullDefinitionString, owner, shareScope);
    try {
      this.reportDefinition = TelemetryLanguageParser.parseReportDef(fullDefinitionString);
    }
    catch (TelemetryLanguageException ex) {
      throw new TelemetryConfigurationException(ex);
    }
    //TODO: though the definition is syntatically correct, need to perform semantic validation!
  }

  /**
   * Constructs this instance.
   * 
   * @param reportDefinition The telemetry report definition object.
   * @param owner The owner of this definition.
   * @param shareScope The share scope of this definition.
   * 
   * @throws TelemetryConfigurationException If the definition string cannot be parsed.
   */
  public TelemetryReportDefinitionInfo(TelemetryReportDefinition reportDefinition, User owner, 
      ShareScope shareScope) throws TelemetryConfigurationException {
    super(reportDefinition.getDefinitionString(), owner, shareScope);
    this.reportDefinition = reportDefinition;
  }
  
  /**
   * Gets the name of this telemetry definition.
   * 
   * @return The name.
   */
  public String getName() {
    return this.getReportDefinitionObject().getName();
  }
  
  /**
   * Gets telemetry definition type.
   * 
   * @return Telemetry definition type.
   */
  public TelemetryDefinitionType getType() {
    return TelemetryDefinitionType.REPORT;
  }
  
  /**
   * Gets the abstract syntax tree representation of this telemetry report.
   * 
   * @return The abstract syntax tree representation.
   */
  public TelemetryReportDefinition getReportDefinitionObject() {
    return this.reportDefinition;
  }
}
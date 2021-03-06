package org.hackystat.telemetry.analyzer.configuration;

import org.hackystat.telemetry.analyzer.language.TelemetryLanguageException;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryStreamsDefinition;
import org.hackystat.telemetry.analyzer.language.parser.TelemetryLanguageParser;
import org.hackystat.sensorbase.resource.users.jaxb.User;

/**
* Provides information about a Telemetry Stream, including its name, its type (Stream), and
 * the definition.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TelemetryStreamsDefinitionInfo extends TelemetryDefinitionInfo {

  private TelemetryStreamsDefinition streamsDefinition;

  /**
   * Constructs this instance.
   * 
   * @param fullDefinitionString The definition string.
   * @param owner The owner of this definition.
   * @param shareScope The share scope of this definition.
   * 
   * @throws TelemetryConfigurationException If the definition string cannot be parsed.
   */
  public TelemetryStreamsDefinitionInfo(String fullDefinitionString, User owner, 
      ShareScope shareScope) throws TelemetryConfigurationException {
    super(fullDefinitionString, owner, shareScope);
    try {
      this.streamsDefinition = TelemetryLanguageParser.parseStreamsDef(fullDefinitionString);
    }
    catch (TelemetryLanguageException ex) {
      throw new TelemetryConfigurationException(ex);
    }
  }
  
  /**
   * Constructs this instance.
   * 
   * @param streamsDef Telemetry streams definition object.
   * @param owner The owner of this definition.
   * @param shareScope The share scope of this definition.
   * 
   * @throws TelemetryConfigurationException If the definition string cannot be parsed.
   */
  public TelemetryStreamsDefinitionInfo(TelemetryStreamsDefinition streamsDef, User owner, 
      ShareScope shareScope) throws TelemetryConfigurationException {
    super(streamsDef.getDefinitionString(), owner, shareScope);
    this.streamsDefinition = streamsDef;
  }
  
  /**
   * Gets the name of this telemetry definition.
   * 
   * @return The name.
   */
  @Override
  public String getName() {
    return this.streamsDefinition.getName();
  }
  
  /**
   * Gets telemetry definition type.
   * 
   * @return Telemetry definition type.
   */
  @Override
  public TelemetryDefinitionType getType() {
    return TelemetryDefinitionType.STREAMS;
  }
  
  /**
   * Gets the abstract syntax tree representation of this telemetry report.
   * 
   * @return The abstract syntax tree representation.
   */
  public TelemetryStreamsDefinition getStreamsDefinitionObject() {
    return this.streamsDefinition;
  }
}
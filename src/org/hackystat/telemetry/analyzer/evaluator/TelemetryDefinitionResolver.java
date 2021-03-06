package org.hackystat.telemetry.analyzer.evaluator;

import org.hackystat.telemetry.analyzer.configuration.TelemetryChartDefinitionInfo;
import org.hackystat.telemetry.analyzer.configuration.TelemetryChartYAxisDefinitionInfo;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionInfo;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionManager;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionType;
import org.hackystat.telemetry.analyzer.configuration.TelemetryStreamsDefinitionInfo;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartYAxisDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryStreamsDefinition;
import org.hackystat.sensorbase.resource.users.jaxb.User;

/**
 * Telemetry definition resolver. It finds telemetry definition objects by name.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TelemetryDefinitionResolver {

  private User user;
  private TelemetryDefinitionManager manager;
  
  /**
   * Constructs this instance.
   * 
   * @param manager A telemetry definition manager.
   * @param currentUser The user who makes request.
   */
  public TelemetryDefinitionResolver(TelemetryDefinitionManager manager, User currentUser) {
    this.manager = manager;
    this.user = currentUser;
  }
  
  /**
   * Resolves telemetry streams definition by name.
   * 
   * @param name Telemetry streams definition name.
   * @return The telemetry streams object.
   * @throws TelemetryEvaluationException If the telemetry streams object does not exist.
   */
  public TelemetryStreamsDefinition resolveStreamsDefinition(String name)
      throws TelemetryEvaluationException {
    TelemetryDefinitionInfo defInfo = this.manager.get(this.user, name, true,
        TelemetryDefinitionType.STREAMS);
    if (defInfo == null) {
      throw new TelemetryEvaluationException("Unable to find definition for telemetry streams '"
          + name + "'.");
    }
    else {
      return ((TelemetryStreamsDefinitionInfo) defInfo).getStreamsDefinitionObject();
    }
  }
  
  /**
   * Resolves telemetry chart definition by name.
   * 
   * @param name Telemetry chart definition name.
   * @return The telemetry chart object.
   * @throws TelemetryEvaluationException If the telemetry chart object does not exist.
   */
  public TelemetryChartDefinition resolveChartDefinition(String name)
      throws TelemetryEvaluationException {
    TelemetryDefinitionInfo defInfo = this.manager.get(this.user, name, true,
        TelemetryDefinitionType.CHART);
    if (defInfo == null) {
      throw new TelemetryEvaluationException("Unable to find definition for telemetry chart '"
          + name + "'.");
    }
    else {
      return ((TelemetryChartDefinitionInfo) defInfo).getChartDefinitionObject();
    }
  }  

  /**
   * Resolves telemetry chart y-axis definition by name.
   * 
   * @param name Telemetry chart y-axis definition name.
   * @return The telemetry chart y-axis object.
   * @throws TelemetryEvaluationException If the telemetry chart y-axis object does not exist.
   */
  public TelemetryChartYAxisDefinition resolveYAxisDefinition(String name)
      throws TelemetryEvaluationException {
    TelemetryDefinitionInfo defInfo = this.manager.get(this.user, name, true,
        TelemetryDefinitionType.YAXIS);
    if (defInfo == null) {
      throw new TelemetryEvaluationException(
          "Unable to find definition for telemetry chart y-axis '" + name + "'.");
    }
    else {
      return ((TelemetryChartYAxisDefinitionInfo) defInfo).getChartYAxisDefinitionObject();
    }
  } 
}

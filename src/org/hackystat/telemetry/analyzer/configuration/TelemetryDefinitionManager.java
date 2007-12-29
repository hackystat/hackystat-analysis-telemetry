package org.hackystat.telemetry.analyzer.configuration;

import java.util.Collection;
import java.util.List;

import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.telemetry.analyzer.configuration.jaxb.TelemetryDefinition;

/**
 * Provides the base class associated with the persistent and non-persistent
 * Telemetry definition managers.
 * 
 * @author (Cedric) Qin Zhang
 */
public abstract class TelemetryDefinitionManager {
  
  /**
   * Gets the telemetry definition information by name.
   * 
   * @param owner The owner under which to find the telemetry definition object.
   * @param name The name of the telemetry definition.
   * @param includeShared If true, then those telemetry definitions owned by other users, 
   *        but is shared will also be returned.
   * @param type The definition type.
   * 
   * @return The object if found, or null.
   */
  public abstract TelemetryDefinitionInfo get(
      User owner, String name, boolean includeShared, TelemetryDefinitionType type);
  
  /**
   * Gets all telemetry definitions that this user has access to.
   * 
   * @param owner The owner of the telemetry definitions returned.
   * @param includeShared If true, then those telemetry definitions owned by
   *        other users, but is shared will also be returned.
   * @param type The definition type.
   *       
   * @return A collection of found objects.
   */
  public abstract Collection<TelemetryDefinitionInfo> getAll(User owner, boolean includeShared, 
      TelemetryDefinitionType  type);
  
  /**
   * Adds information about a definition.
   * 
   * @param defInfo Information about the definition to be added.
   * 
   * @throws TelemetryConfigurationException If there is duplicated definition.
   */
  public abstract void add(TelemetryDefinitionInfo defInfo) 
      throws TelemetryConfigurationException;
  
  /**
   * Returns the list of TelemetryDefinitions associated with this manager.
   * @return The list of telemetry definitions.
   */
  public abstract List<TelemetryDefinition> getDefinitions();

  /**
   * Deletes a telemetry object definition. Does nothing if the definition does not exist.
   * 
   * @param owner The owner of the definition.
   * @param name The name of the definition.
   * @param type The definition type.
   */
  public abstract void remove(User owner, String name, TelemetryDefinitionType  type);
}

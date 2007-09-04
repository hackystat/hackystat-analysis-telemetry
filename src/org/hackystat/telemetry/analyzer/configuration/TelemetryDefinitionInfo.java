package org.hackystat.telemetry.analyzer.configuration;

import org.hackystat.telemetry.analyzer.util.user.User;

/**
 * Base class for information holders of telemetry objects such as Charts,  
 * Y-Axes, Reports, and Streams.
 * 
 * @author (Cedric) Qin Zhang
 */
public abstract class TelemetryDefinitionInfo {

  private User owner;
  private ShareScope shareScope;
  private String fullDefinitionString;

  /**
   * Constructs this instance.
   * 
   * @param fullDefinitionString The definition string.
   * @param owner The owner of this definition.
   * @param shareScope The share scope of this definition.
   */
  protected TelemetryDefinitionInfo(String fullDefinitionString, User owner, 
      ShareScope shareScope) {
    this.owner = owner;
    this.shareScope = shareScope;
    this.fullDefinitionString = fullDefinitionString;
  }
  
  /**
   * Gets the name of this telemetry definition.
   * 
   * @return The name.
   */
  public abstract String getName();
  
  /**
   * Gets telemetry definition type.
   * 
   * @return Telemetry definition type.
   */
  public abstract TelemetryDefinitionType getType();
  
  /**
   * Gets the owner of this definition.
   * 
   * @return The owner.
   */
  public User getOwner() {
    return this.owner;
  }

  /**
   * Gets the share scope of this definition.
   * 
   * @return The share scope.
   */
  public ShareScope getShareScope() {
    return this.shareScope;
  }

  /**
   * Gets complete definition string.
   * 
   * @return The definition string.
   */
  public String getDefinitionString() {
    return this.fullDefinitionString;
  }
}
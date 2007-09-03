package org.hackystat.telemetry.configuration.model;

import org.hackystat.telemetry.util.user.User;

/**
 * Base class for information holder about various telemetry object definition.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id: TelemetryDefInfo.java,v 1.1.1.1 2005/10/20 23:56:48 johnson Exp $
 */
public abstract class TelemetryDefinitionInfo {

  private User owner;
  private ShareScope shareScope;
  private String fullDefinitionString;

  /**
   * Constructs this instance.
   * 
   * @param fullDefinitionString The defintion string.
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
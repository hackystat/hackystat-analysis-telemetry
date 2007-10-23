package org.hackystat.telemetry.analyzer.configuration;

import org.hackystat.sensorbase.resource.projects.jaxb.Project;

/**
 * The level of sharing associated with a telemetry chart or report definition.
 * The share scope can be global, project or private (not shared).
 * <p>
 * V8 Notes:  Initially, all scope will be global.  This is an enumerated type and
 * really should be implemented that way. 
 * 
 * @author (Cedric) Qin Zhang  
 */
public class ShareScope {

  private static final int GLOBAL = 100;
  private static final int PROJECT = 200;
  private static final int PRIVATE = 300;

  private static final String GLOBAL_STRING = "Global";
  private static final String PRIVATE_STRING = "Not Shared";
  //private static final String PROJECT_PREFIX_STRING = "Project@@@";

  private static final ShareScope GLOBAL_SHARE_SCOPE = new ShareScope(GLOBAL, null);
  private static final ShareScope PRIVATE_SHARE_SCOPE = new ShareScope(PRIVATE, null);

  private int scope;
  private Project project;

  /**
   * Private constructor. User cannot call this method.
   * 
   * @param shareScope The share scope.
   * @param project The project. It must be null if share scope is global or
   *          private. It must not be null if share scope is project.
   */
  private ShareScope(int shareScope, Project project) {
    this.scope = shareScope;
    this.project = project;
  }

  /**
   * Gets the global share scope.
   * 
   * @return The global share scope.
   */
  public static ShareScope getGlobalShareScope() {
    return GLOBAL_SHARE_SCOPE;
  }

  /**
   * Gets the project share scope.
   * 
   * @param project The project.
   * @return The project share scope. Always Global in V8.
   */
  public static ShareScope getProjectShareScope(Project project) {
    if (project == null) {
      throw new IllegalArgumentException("Project cannot be null");
    }
    return new ShareScope(PROJECT, project);
  }

  /**
   * Gets the private share scope.
   * 
   * @return The private share scope.
   */
  public static ShareScope getPrivateShareScope() {
    return PRIVATE_SHARE_SCOPE;
  }

  /**
   * Gets the project if the the share scope is project.
   * 
   * @return The project.
   * 
   * @throws TelemetryConfigurationException If the share scope is not project.
   */
  public Project getProject() throws TelemetryConfigurationException {
    if (!this.isProject()) {
      throw new TelemetryConfigurationException("The share scope is not project.");
    }
    return this.project;
  }

  /**
   * Checks whether is share scope is global.
   * 
   * @return True if share scope is global.
   */
  public boolean isGlobal() {
    return GLOBAL == this.scope;
  }

  /**
   * Checks whether is share scope is project.
   * 
   * @return True if share scope is project.
   */
  public boolean isProject() {
    return PROJECT == this.scope;
  }

  /**
   * Checks whether is share scope is private.
   * 
   * @return True if share scope is private.
   */
  public boolean isPrivate() {
    return PRIVATE == this.scope;
  }

  /**
   * Gets the string representation of this instance.
   * 
   * @return The string representation.
   */
  @Override
  public String toString() {
    if (this.isGlobal()) {
      return GLOBAL_STRING;
    }
    else if (this.isProject()) {
      return "Project: " + this.project.getName();
    }
    else {
      // must be private
      return PRIVATE_STRING;
    }
  }

  /**
   * Serialized this instance to a string. Note that this string may not be user
   * readable.
   * 
   * @return The serialized form.
   */
  public String serializeToString() {
    if (this.isGlobal()) {
      return GLOBAL_STRING;
    }
    else if (this.isProject()) {
      return "Project@@@" + this.project.getName();
    }
    else {
      // must be private
      return PRIVATE_STRING;
    }
  }

  /**
   * Tests whether this instance equals another instance.
   * 
   * @param obj Another instance.
   * 
   * @return True if they are equal.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ShareScope)) {
      return false;
    }
    ShareScope another = (ShareScope) obj;
    if (this.scope == another.scope) {
      if (this.project == null) {
        return another.project == null;
      }
      else {
        return this.project.equals(another.project);
      }
    }
    else {
      return false;
    }
  }

  /**
   * Gets the hash code.
   * 
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    if (this.project != null) {
      return this.project.hashCode();
    }
    else {
      return this.scope;
    }
  }
}

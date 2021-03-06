package org.hackystat.telemetry.analyzer.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.hackystat.sensorbase.resource.users.jaxb.User;

/**
 * The manager for <code>TelemetryDefinitionInfo</code> objects. 
 * <p> 
 * Warning: This class is NOT thread-safe.
 * <p>
 * V8 Notes: If/when we decide to provide project-level sharing, we need to enforce the
 * condition that only one instance of a name can exist for a given project. Right now it
 * appears that multiple users can create telemetry definitions with the same name with 
 * project scope.
 * <p>
 * I wonder why this class is not thread safe.  Perhaps synchronization occurs at a higher
 * level. 
 * 
 * @author (Cedric) Qin Zhang
 */
class TelemetryDefinitionInfoRepository {

  /**
   * Two level map storing <code>TelemetryDefInfo</code> objects. The first
   * level is keyed by userEmail (the owner), and the second level is keyed by the
   * name of the telemetry definition object.
   */
  private Map<String, Map<String, TelemetryDefinitionInfo>> ownerKeyDefMap = 
    new TreeMap<String, Map<String, TelemetryDefinitionInfo>>();

  /**
   * Find the telemetry definition object by name. This method returns null if
   * the telemetry definition cannot be found.
   * <p>
   * <b>Important</b> Note that it's possible that there are multiple
   * definitions with the same name (all have project level sharing, but owned
   * by different users). In this case, any one of the definitions might be
   * returned. The only guarantee is that if there are definitions in the
   * project share scope and global share scope with the same name, the one in
   * project share scope will be returned.
   * 
   * @param owner The owner under which to find the telemetry definition object.
   * @param name The name of the telemetry definition.
   * @param includeShared If true, then those telemetry definitions owned by
   *        other users, but is shared will also be returned.
   * 
   * @return An instance of <code>TelemetryDefInfo</code> object if found, or null.
   */
  TelemetryDefinitionInfo find(User owner, String name, boolean includeShared) {
    TelemetryDefinitionInfo result = null;

    // find whether there is any definition owned by this user
    Map<String, TelemetryDefinitionInfo> secondLevelMap = 
      this.ownerKeyDefMap.get(owner.getEmail());
    if (secondLevelMap != null) {
      result = secondLevelMap.get(name);
    }

    if (result == null && includeShared) {
      // cannot find definition owned by this user, now search all definitions
      // accessible to this user.
      TelemetryDefinitionInfo globalDefInfo = null;
      for (TelemetryDefinitionInfo theDefInfo : this.findAll(owner, true)) {
        if (theDefInfo.getName().equals(name)) {
          if (theDefInfo.getShareScope().isGlobal()) {
            globalDefInfo = theDefInfo;
          }
          else {
            result = theDefInfo;
            break;
          }
        }
      }
      if (result == null) {
        result = globalDefInfo;
      }
    }

    return result;
  }

  /**
   * Gets a list of telemetry definitions that this user has access to.
   * 
   * @param owner The owner of the telemetry definitions returned.
   * @param includesShared If true, then those telemetry definitions owned by
   *          other users, but is shared will also be returned.
   * 
   * @return A collection of <code>TelemetryDefInfo</code> objects.
   */
  Collection<TelemetryDefinitionInfo> findAll(User owner, boolean includesShared) {
    if (!includesShared) { //NOPMD
      Map<String, TelemetryDefinitionInfo> secondLevelMap =  
        this.ownerKeyDefMap.get(owner.getEmail());
      return secondLevelMap == null ? 
          new ArrayList<TelemetryDefinitionInfo>(0) : secondLevelMap.values();
    }
    else {
      // since shared defs need to be included, we need to scan everything.
      ArrayList<TelemetryDefinitionInfo> list = new ArrayList<TelemetryDefinitionInfo>();
      for (Map<String, TelemetryDefinitionInfo> secondLevelMap : this.ownerKeyDefMap.values()) {
        for (TelemetryDefinitionInfo defInfo : secondLevelMap.values()) {
          if (owner.equals(defInfo.getOwner())) {
            list.add(defInfo);
          }
          else {
            ShareScope shareScope = defInfo.getShareScope();
            if (shareScope.isGlobal()) {
              list.add(defInfo);
            }
            else if (shareScope.isProject()) {
              try {
                if (shareScope.getProject().getMembers().getMember().contains(owner)) {
                  list.add(defInfo);
                }
              }
              catch (TelemetryConfigurationException ex) {
                // should not happen, since we have already checked isProject()
                throw new RuntimeException(ex);
              }
            }
          }
        }
      }
      return list;
    }
  }

  /**
   * Adds a telemetry definition to this in-memory repository.
   * 
   * @param telemetryDefInfo Information about the definition to be added.
   * @throws TelemetryConfigurationException If there is duplicated definition.
   */
  void add(TelemetryDefinitionInfo telemetryDefInfo) throws TelemetryConfigurationException {
    User owner = telemetryDefInfo.getOwner();
    Map<String, TelemetryDefinitionInfo> secondLevelMap = 
      this.ownerKeyDefMap.get(owner.getEmail());
    if (secondLevelMap == null) {
      secondLevelMap = new TreeMap<String, TelemetryDefinitionInfo>();
      this.ownerKeyDefMap.put(owner.getEmail(), secondLevelMap);
    }

    String name = telemetryDefInfo.getName();
    if (secondLevelMap.containsKey(name)) {
      throw new TelemetryConfigurationException("User " + owner.toString()
          + " already has telemetry definition " + name + " defined.");
    }
    secondLevelMap.put(name, telemetryDefInfo);
  }

  /**
   * Goes through all users, and checks whether there is a definition by name.
   * 
   * @param telemetryDefinitionName The definition name.
   * @return True if it exists.
   */
  boolean exists(String telemetryDefinitionName) {
    boolean found = false;
    for (Map<String, TelemetryDefinitionInfo> secondLevelMap : this.ownerKeyDefMap.values()) {
      if (secondLevelMap.containsKey(telemetryDefinitionName)) {
        found = true;
        break;
      }
    }
    return found;
  }

  /**
   * Deletes a telemetry object definition. Only the owner can make the
   * deletion. This method does nothing if the definition does not exist.
   * 
   * @param owner The owner of the definition.
   * @param telemetryDefinitionName The name of the definition.
   */
  void remove(User owner, String telemetryDefinitionName) {
    Map<String, TelemetryDefinitionInfo> secondLevelMap = this.ownerKeyDefMap.get(owner.getEmail());
    if (secondLevelMap != null) {
      secondLevelMap.remove(telemetryDefinitionName);
    }
  }
}

package org.hackystat.telemetry.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hackystat.telemetry.util.user.User;

/**
 * Non-persistent implementation of <code>TelemetryDefinitionManager</code>.
 * This class can be made to merge its repository with the global singleton instance of
 * <code>PersistentTelemetryDefinitionManager</code>. The effect is that (1)
 * All telemetry definitions in this instance and in the global instance share
 * one single name space. (2) When searching, both the repository managed by
 * this instance and the repository managed by the global instance will be
 * searched. (3) Adding and deleting are only performed on the repository
 * managed by this instance. (4) The repository managed by this instance only
 * exists in memory, it will not be persisted.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
class NonPersistentTelemetryDefinitionManager extends TelemetryDefinitionManager {
  
  private boolean linkToGlobalSingleton;
  private PersistentTelemetryDefinitionManager globalSingleton;
  private Map<TelemetryDefinitionType, TelemetryDefinitionInfoRepository> defInfoRepositoryMap = 
    new HashMap<TelemetryDefinitionType, TelemetryDefinitionInfoRepository>(11);
  
  /**
   * Constructs this instance.
   * 
   * @param linkToGlobalSingleton True if the definitions in global singleton telemetry manager
   *        should be merged to this instance.
   */
  NonPersistentTelemetryDefinitionManager(boolean linkToGlobalSingleton) {
    this.linkToGlobalSingleton = linkToGlobalSingleton;
    if (this.linkToGlobalSingleton) {
      this.globalSingleton = (PersistentTelemetryDefinitionManager) 
          TelemetryDefinitionManagerFactory.getGlobalPersistentInstance();
    }
    
    this.defInfoRepositoryMap.put(TelemetryDefinitionType.STREAMS, 
        new TelemetryDefinitionInfoRepository());
    this.defInfoRepositoryMap.put(TelemetryDefinitionType.CHART, 
        new TelemetryDefinitionInfoRepository());
    this.defInfoRepositoryMap.put(TelemetryDefinitionType.YAXIS, 
        new TelemetryDefinitionInfoRepository());
    this.defInfoRepositoryMap.put(TelemetryDefinitionType.REPORT, 
        new TelemetryDefinitionInfoRepository());
  }
  
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
  public synchronized TelemetryDefinitionInfo get(
      User owner, String name, boolean includeShared, TelemetryDefinitionType type) {
    TelemetryDefinitionInfo defInfo = null;
    if (this.linkToGlobalSingleton) {
      defInfo = this.globalSingleton.get(owner, name, includeShared, type);
    }
    if (defInfo == null) {
      TelemetryDefinitionInfoRepository repository 
          = (TelemetryDefinitionInfoRepository) this.defInfoRepositoryMap.get(type);
      if (repository == null) {
        throw new RuntimeException("Unknow telemetry definition type " + type);
      }
      else {
        defInfo = repository.find(owner, name, includeShared);   
      }
    }
    return defInfo;
  }

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
  public synchronized Collection<TelemetryDefinitionInfo> getAll(User owner, boolean includeShared, 
      TelemetryDefinitionType type) {
    ArrayList<TelemetryDefinitionInfo> result = new ArrayList<TelemetryDefinitionInfo>();
    if (this.linkToGlobalSingleton) {
      result.addAll(this.globalSingleton.getAll(owner, includeShared, type));
    }
    TelemetryDefinitionInfoRepository repository 
        = (TelemetryDefinitionInfoRepository) this.defInfoRepositoryMap.get(type);
    if (repository == null) {
        throw new RuntimeException("Unknown telemetry definition type " + type);
    }
    else {
      result.addAll(repository.findAll(owner, includeShared));
    }
    return result;
  }

  /**
   * Adds information about a definition.
   * 
   * @param defInfo Information about the definition to be added.
   * 
   * @throws TelemetryConfigurationException If there is duplicated definition.
   */
  public synchronized void add(TelemetryDefinitionInfo defInfo) 
      throws TelemetryConfigurationException {
    if (this.linkToGlobalSingleton && this.globalSingleton.isNameInUse(defInfo.getName())) {
      throw new TelemetryConfigurationException(
          "All telemetry definitions (chart, report) share a global namespace. The name '"
          + defInfo.getName() + "' is already used by either you or other user.");        
    }
    
    TelemetryDefinitionInfoRepository repository 
        = (TelemetryDefinitionInfoRepository) this.defInfoRepositoryMap.get(defInfo.getType());
    if (repository == null) {
        throw new RuntimeException("Unknow telemetry definition: " + defInfo.getClass().getName());
    }
    else {
      //check namespace constraint.
      for (TelemetryDefinitionInfoRepository rep : this.defInfoRepositoryMap.values()) {
        if (rep.exists(defInfo.getName())) {
          throw new TelemetryConfigurationException(
              "All telemetry definitions (chart, report) share a global namespace. The name '"
              + defInfo.getName() + "' is already used by either you or other user.");    
        }
      }
      //add
      repository.add(defInfo);
    }  
  }

  /**
   * Deletes a telemetry object definition. Does nothing if the definition does not exist.
   * 
   * @param owner The owner of the definition.
   * @param name The name of the definition.
   * @param type The definition type.
   */
  public synchronized void remove(User owner, String name, TelemetryDefinitionType type) {
    TelemetryDefinitionInfoRepository repository 
        = (TelemetryDefinitionInfoRepository) this.defInfoRepositoryMap.get(type);
    if (repository == null) {
        throw new RuntimeException("Unknow telemetry definition type " + type);
    }
    else {
      repository.remove(owner, name);  
    } 
  }
}

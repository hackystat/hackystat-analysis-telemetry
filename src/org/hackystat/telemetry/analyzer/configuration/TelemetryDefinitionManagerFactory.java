package org.hackystat.telemetry.analyzer.configuration;

/**
 * Provides a Factory class for generating the single PersistentTelemetryDefinitionManager
 * and as many non-persistent ones as required.  
 * 
 * @author (Cedric) Qin Zhang
 */
public class TelemetryDefinitionManagerFactory {

  private static TelemetryDefinitionManager theGlobalInstance; 

  /**
   * Gets the singleton global instance of <code>TelemetryDefinitionManger</code>. 
   * 
   * @return The global instance of <code>TelemetryDefinitionManager</code>.
   */
  public static TelemetryDefinitionManager getGlobalPersistentInstance() {
    // The null situation should only occur during testing. 
    if (theGlobalInstance == null) { //NOPMD because this only happens during testing. 
      theGlobalInstance = new PersistentTelemetryDefinitionManager(null);
    }
    return theGlobalInstance;
  }
  
  /**
   * Creates the singleton persistent instance, using the passed defDir to find telemetry 
   * definitions.  Note that this must be called when the Restlet server is being created and
   * thus in advance of any calls to getGlobalPersistentInstance.
   * @param defDir A string indicating the directory where telemetry definitions (in addition to 
   * the "builtin" definitions) will be found.
   */
  public static void buildGlobalPersistentInstance(String defDir) {
    theGlobalInstance = new PersistentTelemetryDefinitionManager(defDir);
   }

  /**
   * Creates a new non-persistent version of
   * <code>TelemetryDefinitionManager</code> instance. Note that the returned
   * instance merges its name space with the name space in the global instance.
   * This means that if a definition name is used in the global instance, it
   * cannot be used in the returned instance.
   * 
   * @param linkToGlobalSingleton True if the definitions in global singleton telemetry manager
   *        should be merged to this instance.
   * 
   * @return A non-persistent version of <code>TelemetryDefinitionManager</code>
   *         instance.
   */
  public static TelemetryDefinitionManager createNonPersistentInstance(
      boolean linkToGlobalSingleton) {
    return new NonPersistentTelemetryDefinitionManager(linkToGlobalSingleton);
  }
}

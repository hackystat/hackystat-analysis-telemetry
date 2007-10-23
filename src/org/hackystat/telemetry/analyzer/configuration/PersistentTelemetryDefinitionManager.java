package org.hackystat.telemetry.analyzer.configuration;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.telemetry.analyzer.configuration.jaxb.TelemetryDefinition;
import org.hackystat.telemetry.analyzer.configuration.jaxb.TelemetryDefinitions;
import org.hackystat.utilities.logger.HackystatLogger;
import org.hackystat.utilities.stacktrace.StackTrace;

/**
 * Implements a persistent Telemetry definition manager. 
 * Reads in Telemetry definitions from the telemetrydefinitions.xml file. 
 * <p>
 * All public methods in the class are thread-safe.
 * <p>
 * In Version 7, this class also was responsible for persisting dynamically defined definitions.
 * It does not currently support dynamic persistent definition in Version 8.
 * 
 * @author (Cedric) Qin Zhang
 */
class PersistentTelemetryDefinitionManager extends TelemetryDefinitionManager {

  private Map<TelemetryDefinitionType, TelemetryDefinitionInfoRepository> defInfoRepositoryMap = 
    new HashMap<TelemetryDefinitionType, TelemetryDefinitionInfoRepository>();
  
  private Logger logger;
  
  TelemetryDefinitions definitions;

  /**
   * Creates a new Persistent Telemetry Definition Manager, reading in the pre-defined definitions
   * from the telemetrydefinitions.xml file. 
   */
  PersistentTelemetryDefinitionManager() {
    this.defInfoRepositoryMap.put(TelemetryDefinitionType.STREAMS, 
        new TelemetryDefinitionInfoRepository());
    this.defInfoRepositoryMap.put(TelemetryDefinitionType.CHART, 
        new TelemetryDefinitionInfoRepository());
    this.defInfoRepositoryMap.put(TelemetryDefinitionType.YAXIS, 
        new TelemetryDefinitionInfoRepository());
    this.defInfoRepositoryMap.put(TelemetryDefinitionType.REPORT, 
        new TelemetryDefinitionInfoRepository());

    this.logger = HackystatLogger.getLogger("org.hackystat.telemetry");
    // Read in the definitions file.
    try {
      this.logger.info("Loading built-in telemetry chart/report/stream/y-axis definitions.");
      InputStream defStream = getClass().getResourceAsStream("telemetry.definitions.xml");
      JAXBContext jaxbContext = JAXBContext
      .newInstance(org.hackystat.telemetry.analyzer.configuration.jaxb.ObjectFactory.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      this.definitions = (TelemetryDefinitions) unmarshaller.unmarshal(defStream);
    } 
    catch (Exception e) {
      this.logger.severe("Could not find telemetry.definitions.xml! " + StackTrace.toString(e));
    }
    
    // Now update the repository maps.
    for (TelemetryDefinition definition : definitions.getTelemetryDefinition()) {
      String defType = definition.getDefinitionType(); 
      String definitionString = definition.getValue();
      ShareScope globalScope = ShareScope.getGlobalShareScope();
      User user = new User();
      user.setEmail("TelemetryDefinitions@hackystat.org");
      // A bit of stuff to make the logging message pretty.
      String oneLineDef = definition.getValue().replace("\n", "");
      int equalsPos = oneLineDef.indexOf("=");
      this.logger.info("  " + oneLineDef.substring(0, equalsPos));

      try {
        if (TelemetryDefinitionType.STREAMS.toString().equalsIgnoreCase(defType)) {
          this.add(new TelemetryStreamsDefinitionInfo(definitionString, user, globalScope));
        }
        else if (TelemetryDefinitionType.CHART.toString().equalsIgnoreCase(defType)) {
          this.add(new TelemetryChartDefinitionInfo(definitionString, user, globalScope));
        }
        else if (TelemetryDefinitionType.YAXIS.toString().equalsIgnoreCase(defType)) {
          this.add(new TelemetryChartYAxisDefinitionInfo(definitionString, user, globalScope));
        }
        else if (TelemetryDefinitionType.REPORT.toString().equalsIgnoreCase(defType)) {
          this.add(new TelemetryReportDefinitionInfo(definitionString, user, globalScope));
        }
        else {
          this.logger.warning("Unknown definition type in telemetry.definitions.xml" + defType); 
        }
      }
      catch (Exception e) {
        this.logger.warning("Error defining a telemetry construct: " + StackTrace.toString(e));
      } 
    }
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
  @Override
  public synchronized TelemetryDefinitionInfo get(
      User owner, String name, boolean includeShared, TelemetryDefinitionType type) {
    TelemetryDefinitionInfoRepository repository 
        = this.defInfoRepositoryMap.get(type);
    if (repository == null) {
      throw new RuntimeException("Unknow telemetry definition type " + type);
    }
    else {
      return repository.find(owner, name, includeShared);   
    }
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
  @Override
  public synchronized Collection<TelemetryDefinitionInfo> getAll(User owner, boolean includeShared, 
      TelemetryDefinitionType type) {
    TelemetryDefinitionInfoRepository repository 
        = this.defInfoRepositoryMap.get(type);
    if (repository == null) {
        throw new RuntimeException("Unknow telemetry definition type " + type);
    }
    else {
      return repository.findAll(owner, includeShared);
    }
  }
  
  /**
   * Adds information about a definition.
   * 
   * @param defInfo Information about the definition to be added.
   * 
   * @throws TelemetryConfigurationException If there is duplicated definition.
   */
  @Override
  public synchronized void add(TelemetryDefinitionInfo defInfo) 
      throws TelemetryConfigurationException {
    TelemetryDefinitionInfoRepository repository 
        = this.defInfoRepositoryMap.get(defInfo.getType());
    if (repository == null) {
        throw new RuntimeException("Unknow telemetry definition: " + defInfo.getClass().getName());
    }
    else {
      //check global namespace constraint.
      if (this.isDefinition(defInfo.getName())) {
        throw new TelemetryConfigurationException(
          "All telemetry definitions (chart, report) share a global namespace. The name '"
          + defInfo.getName() + "' is already used by either you or other user.");
      }
      //add
      repository.add(defInfo);
      //this.write(defInfo.getOwner());
    }  
  }
  
  /**
   * Checks whether the name has already been used by any type of telemetry definition.
   * 
   * @param name The name.
   * @return True if the name has already been used.
   */
  synchronized boolean isDefinition(String name) {
    for (TelemetryDefinitionInfoRepository rep : this.defInfoRepositoryMap.values()) {
      if (rep.exists(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Deletes a telemetry object definition. Does nothing if the definition does not exist.
   * 
   * @param owner The owner of the definition.
   * @param name The name of the definition.
   * @param type The definition type.
   */
  @Override
  public synchronized void remove(User owner, String name, TelemetryDefinitionType type) {
    TelemetryDefinitionInfoRepository repository 
        = this.defInfoRepositoryMap.get(type);
    if (repository == null) {
        throw new RuntimeException("Unknow telemetry definition type " + type);
    }
    else {
      repository.remove(owner, name);  
      //this.write(owner);
    } 
  }
}
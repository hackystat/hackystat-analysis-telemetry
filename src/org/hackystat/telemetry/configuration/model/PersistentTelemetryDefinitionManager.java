package org.hackystat.telemetry.configuration.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hackystat.telemetry.util.user.User;
import org.hackystat.telemetry.util.user.UserManager;
import org.hackystat.telemetry.util.ServerProperties;
import org.hackystat.telemetry.configuration.TelemetryConfigurationException;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Telemetry definition manager. This class is responsible for on-disk
 * persistence of telemetry defintions for all Hackystat users.
 * <p>
 * All public methods in the class are thread-safe.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id:$
 */
class PersistentTelemetryDefinitionManager extends TelemetryDefinitionManager {

  private Map<TelemetryDefinitionType, TelemetryDefinitionInfoRepository> defInfoRepositoryMap = 
    new HashMap<TelemetryDefinitionType, TelemetryDefinitionInfoRepository>(11);

  /** Used to temporarily disable write operation during initialization. */
  private boolean disableWrite = false;
  
  /**
   * Constructs this instance.
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
    
    this.disableWrite = true;
    try {
      for (Iterator i = UserManager.getInstance().iterator(); i.hasNext();) {
        this.read((User) i.next());
      }
    }
    catch (Exception ex) {
      System.err.println("[PersistenTelemetryDefinitionManager] " +
          "Unexpected excetion during initialization." + ex.getMessage());
    }
    finally {
      this.disableWrite = false;
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
  public synchronized TelemetryDefinitionInfo get(
      User owner, String name, boolean includeShared, TelemetryDefinitionType type) {
    TelemetryDefinitionInfoRepository repository 
        = (TelemetryDefinitionInfoRepository) this.defInfoRepositoryMap.get(type);
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
  public synchronized Collection<TelemetryDefinitionInfo> getAll(User owner, boolean includeShared, 
      TelemetryDefinitionType type) {
    TelemetryDefinitionInfoRepository repository 
        = (TelemetryDefinitionInfoRepository) this.defInfoRepositoryMap.get(type);
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
  public synchronized void add(TelemetryDefinitionInfo defInfo) 
      throws TelemetryConfigurationException {
    TelemetryDefinitionInfoRepository repository 
        = (TelemetryDefinitionInfoRepository) this.defInfoRepositoryMap.get(defInfo.getType());
    if (repository == null) {
        throw new RuntimeException("Unknow telemetry definition: " + defInfo.getClass().getName());
    }
    else {
      //check global namespace constraint.
      if (this.isNameInUse(defInfo.getName())) {
        throw new TelemetryConfigurationException(
          "All telemetry definitions (chart, report) share a global namespace. The name '"
          + defInfo.getName() + "' is already used by either you or other user.");
      }
      //add
      repository.add(defInfo);
      this.write(defInfo.getOwner());
    }  
  }
  
  /**
   * Checks whether the name has already been used by any type of telemetry definition.
   * 
   * @param name The name.
   * @return True if the name has already been used.
   */
  synchronized boolean isNameInUse(String name) {
    for (Iterator i = this.defInfoRepositoryMap.values().iterator(); i.hasNext(); ) {
      TelemetryDefinitionInfoRepository rep = (TelemetryDefinitionInfoRepository) i.next();
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
  public synchronized void remove(User owner, String name, TelemetryDefinitionType type) {
    TelemetryDefinitionInfoRepository repository 
        = (TelemetryDefinitionInfoRepository) this.defInfoRepositoryMap.get(type);
    if (repository == null) {
        throw new RuntimeException("Unknow telemetry definition type " + type);
    }
    else {
      repository.remove(owner, name);  
      this.write(owner);
    } 
  }
 
  /**
   * Reads the telemetry configuration file. The configuration file mainly
   * stores the definition of telemetry "streams", "chart" and "report" objects.
   * The definitions are stored in owner's directory.
   * 
   * @param user The owern, which is at the same time a Hackystat user.
   */
  private synchronized void read(User user) {
    Document doc = user.getJDomDocument("telemetry2");
    if (doc.hasRootElement()) {
      Element root = doc.getRootElement();
      
      //handles telemetry chart definitions
      for (Iterator i = root.getChildren().iterator(); i.hasNext(); ) {
        Element element = (Element) i.next();
        try {
          ShareScope shareScope 
              = ShareScope.deserializeFromString(element.getAttributeValue("share"));
          String definitionString = null;
          for (Iterator j = element.getContent().iterator(); j.hasNext(); ) {
            Object o = j.next();
            if (o instanceof CDATA) {
              definitionString = ((CDATA) o).getText();
              break;
            }
          }
          
          TelemetryDefinitionType type = TelemetryDefinitionType.valueOf(element.getName());
          if (TelemetryDefinitionType.STREAMS == type) {
            this.add(new TelemetryStreamsDefinitionInfo(definitionString, user, shareScope));
          }
          else if (TelemetryDefinitionType.CHART == type) {
            this.add(new TelemetryChartDefinitionInfo(definitionString, user, shareScope));
          }
          else if (TelemetryDefinitionType.YAXIS == type) {
            this.add(new TelemetryChartYAxisDefinitionInfo(definitionString, user, shareScope));
          }
          else if (TelemetryDefinitionType.REPORT == type) {
            this.add(new TelemetryReportDefinitionInfo(definitionString, user, shareScope));
          }
          else {
            this.warning("Unknow element " + element.getName()
                + " in telemetry configuration file for user " + user.toString());
          }
        }
        catch (Exception ex) {
          XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
          String elementString = xmlOutputter.outputString(element);
          this.warning("Incorrect TelemetryStreamsDef element detected in telemetry "
              + "configuration file for user " + user.toString()
              + ".\nIncorrect element is:\n" + elementString);
        }
      }
    }
  }
  

  /**
   * Writes out the telemetry configuration file. The configuration file mainly
   * stores the definition of telemetry "streams", "chart" and "report" objects.
   * The definitions are stored in owner's directory.
   * 
   * @param owner The owner, which is at the same time a Hackystat user.
   */
  private synchronized void write(User owner) {
    if (this.disableWrite) {
      return;
    }
    
    Element root = new Element("telemetry");
    
    for (Iterator i = this.defInfoRepositoryMap.entrySet().iterator(); i.hasNext(); ) {
      Map.Entry entry = (Map.Entry) i.next();
      TelemetryDefinitionType type = (TelemetryDefinitionType) entry.getKey();
      TelemetryDefinitionInfoRepository repository 
          = (TelemetryDefinitionInfoRepository) entry.getValue();
      
      for (Iterator j = repository.findAll(owner, false).iterator(); j.hasNext(); ) {
        TelemetryDefinitionInfo defInfo = (TelemetryDefinitionInfo) j.next();
        Element chartElement = new Element(type.toString());
        chartElement.setAttribute("share", defInfo.getShareScope().serializeToString());
        CDATA cdata = new CDATA("definition");
        cdata.setText(defInfo.getDefinitionString());
        chartElement.addContent(cdata);
        root.addContent(chartElement);
      }      
    }
   
    Document doc = new Document(root);
    owner.putJDomDocument(doc, "telemetry2");
  }

  /**
   * Logs message. All logging in this class goes through this method.
   * 
   * @param message The message to be logged.
   */
  private void warning(String message) {
    ServerProperties.getInstance().getLogger().severe("[TelemetryDefinitionManager] " + message);
  }
}
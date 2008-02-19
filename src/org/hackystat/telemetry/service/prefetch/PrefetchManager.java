package org.hackystat.telemetry.service.prefetch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.hackystat.telemetry.analyzer.configuration.ExtensionFileFilter;
import org.hackystat.telemetry.service.prefetch.jaxb.TelemetryPrefetch;
import org.hackystat.telemetry.service.server.Server;
import org.hackystat.telemetry.service.server.ServerProperties;
import org.hackystat.utilities.stacktrace.StackTrace;

/**
 * Reads in the XML files (if any) containing prefetch information, and sets up a timer task to 
 * prefetch (i.e. run) these chart analyses once a day.  If RunOnStartup is true, then the prefetch
 * tasks are run immediately.  
 * @author Philip Johnson 
 */
public class PrefetchManager {
  /** This telemetry server. */
  Server server;
  /** The logger for this telemetry server. */
  Logger logger;
  /** The list of TelemetryPrefetch JAXB instances. */
  List<TelemetryPrefetch> telemetryPrefetchList = new ArrayList<TelemetryPrefetch>();
  /** The list of DailyTimer instances, one per TelemetryPrefetch file. */
  List<DailyTimer> timers = new ArrayList<DailyTimer>();
  
  /**
   * Constructs a PrefetchManager, which reads in the Telemetry prefetch XML files and sets up 
   * a DailyTimer task for each XML file found.
   * @param server This Telemetry analysis server.
   */
  public PrefetchManager(Server server) {
    this.server = server;
    this.logger = server.getLogger();
    this.telemetryPrefetchList = getTelemetryPrefetchList();
    setupTimers();
  }
  
  /**
   * Find any Prefetch XML files and constructs the Java instances for them, returning them
   * as a List of TelemetryPrefetch instances. 
   * @return The list of TelemetryPrefetch instances, or an empty list if none found.
   */
  private List<TelemetryPrefetch> getTelemetryPrefetchList() {
    List<TelemetryPrefetch> telemetryPrefetchList = new ArrayList<TelemetryPrefetch>();
    String defDirString = server.getServerProperties().get(ServerProperties.PREFETCH_DIR_KEY);
    if (defDirString == null || !((new File(defDirString)).exists())) {
      server.getLogger().info("Telemetry prefetch directory not available: " + defDirString);
    }
    else {
      // Read in the any prefetch definitions from the prefetch directory.
      File defDir = new File(defDirString);
      File[] xmlFiles = defDir.listFiles(new ExtensionFileFilter(".xml"));
      FileInputStream fileStream = null;
      for (int i = 0; i < xmlFiles.length; i++) {
        try {
          // Get the TelemetryPrefetch instance from the file.
          this.logger.info("Reading telemetry prefetch data from: " + xmlFiles[i].getName());
          fileStream = new FileInputStream(xmlFiles[i]);
          telemetryPrefetchList.add(getDefinitions(fileStream));
          fileStream.close();
        }
        catch (Exception e) {
          this.logger.info("Error reading definitions from: " + xmlFiles[i] + " " + e);
          try {
            fileStream.close();
          }
          catch (Exception f) { 
            this.logger.info("Failed to close: " + fileStream.toString() + " " + e);
          }
        }
      }
    }
    return telemetryPrefetchList;
  }

  /**
   * Initializes the list of DailyTimer instances. 
   */
  private void setupTimers() {
    for (TelemetryPrefetch prefetch : telemetryPrefetchList) {
      PrefetchTask task = new PrefetchTask(prefetch, this.logger, this.server.getHostName());
      DailyTimer timer = new DailyTimer(prefetch.getMinutesAfterMidnight(), task); 
      this.logger.info("Prefetch '" + prefetch.getName() + "' scheduled for: " + 
          timer.getTriggerTime());
      this.timers.add(timer);
      if ("True".equalsIgnoreCase(prefetch.getRunOnStartup())) {
        this.logger.info("Run on startup is true, so invoking: " + prefetch.getName());
        task.run();
      }
    }
  }
  
  /**
   * Returns a TelemetryPrefetch object constructed from defStream, or null if unsuccessful.
   * @param defStream The input stream containing a TelemetryPrefetch object in XML format.
   * @return The TelemetryPrefetch object.
   */
  private TelemetryPrefetch getDefinitions (InputStream defStream) {
    // Read in the definitions file.
    try {
      JAXBContext jaxbContext = JAXBContext
      .newInstance(org.hackystat.telemetry.service.prefetch.jaxb.ObjectFactory.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      return (TelemetryPrefetch) unmarshaller.unmarshal(defStream);
    } 
    catch (Exception e) {
      this.logger.severe("Failed to get TelemetryPrefetch from stream" + StackTrace.toString(e));
    }
    return null;
  }

}

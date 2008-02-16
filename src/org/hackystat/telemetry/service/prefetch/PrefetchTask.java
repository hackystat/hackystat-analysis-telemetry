package org.hackystat.telemetry.service.prefetch;

import java.util.TimerTask;
import java.util.logging.Logger;

import org.hackystat.telemetry.service.prefetch.jaxb.TelemetryPrefetch;

/**
 * A TimerTask that encapsulates a TelemetryPrefetch instance. 
 * When this timer's run() method is called, it will construct a set of calls to the Telemetry 
 * analysis service from the information in the TelemetryPrefetch instance.  
 * @author Philip Johnson
 *
 */
public class PrefetchTask extends TimerTask {
  
  private TelemetryPrefetch telemetryPrefetch;
  private Logger logger; 
  
  /**
   * Create a PrefetchTask, which will run all of the charts in this TelemetryPrefetch instance.
   * @param telemetryPrefetch The Telemetry Prefetch instance. 
   * @param logger The logger. 
   */
  public PrefetchTask(TelemetryPrefetch telemetryPrefetch, Logger logger) {
    this.telemetryPrefetch = telemetryPrefetch;
    this.logger = logger;
  }

  /**
   * Invoked automatically by the associated DailyTimer task.
   */
  @Override
  public void run() {
    this.logger.info("Running prefetch: " + this.telemetryPrefetch.getTelemetryHost());
  }

}

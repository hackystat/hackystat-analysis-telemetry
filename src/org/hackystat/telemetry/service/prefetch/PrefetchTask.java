package org.hackystat.telemetry.service.prefetch;

import java.util.TimerTask;
import java.util.logging.Logger;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.telemetry.service.prefetch.jaxb.PrefetchChart;
import org.hackystat.telemetry.service.prefetch.jaxb.TelemetryPrefetch;
import org.hackystat.utilities.tstamp.Tstamp;

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
  private String host; 
  
  /**
   * Create a PrefetchTask, which will run all of the charts in this TelemetryPrefetch instance.
   * @param telemetryPrefetch The Telemetry Prefetch instance. 
   * @param logger The logger.
   * @param host The telemetry service host to be contacted.  
   */
  public PrefetchTask(TelemetryPrefetch telemetryPrefetch, Logger logger, String host) {
    this.telemetryPrefetch = telemetryPrefetch;
    this.logger = logger;
    this.host = host;
  }

  /**
   * Invoked automatically by the associated DailyTimer task or by the PrefetchManager on 
   * startup if RunOnStartup is true.
   */
  @Override
  public void run() {
    String prefetchName = this.telemetryPrefetch.getName();
    this.logger.info("Running prefetch: " + prefetchName);
    // We process each of the Prefetch Charts in this TelemetryPrefetch instance.
    for (PrefetchChart chart : this.telemetryPrefetch.getPrefetchChart()) {
      // [1] Try to make an authenticated TelemetryClient using the PrefetchChart data.
      String user = chart.getAuthorizedUserName();
      String password = chart.getAuthorizedUserPassword();
      TelemetryClient client = new TelemetryClient(host, user, password);
      try {
        client.authenticate();
      }
      catch (Exception e) {
        String msg = "Prefetch failed authentication, skipping: " + prefetchName + " " + host + " " 
        + user + " " + password + " " + e.getMessage();
        this.logger.info(msg);
        return;
      }
      // [2] Ensure that the specified Start Time is a valid XMLGregorianCalendar. 
      XMLGregorianCalendar startTime;
      try {
        startTime = Tstamp.makeTimestamp(chart.getStartTime());
      }
      catch (Exception e) {
        this.logger.info("Prefetch had illegal time, skipping: " + prefetchName + " " 
            + chart.getStartTime());
        return;
      }
      XMLGregorianCalendar yesterday = Tstamp.incrementDays(Tstamp.makeTimestamp(), -1);
      // [3] Request the chart from the Telemetry Client.
      String chartName = chart.getChartName();
      try {
        client.getChart(chartName, chart.getProjectOwner(), chart.getProjectName(), 
            "Day", startTime, yesterday, chart.getChartParameters());
        this.logger.info("Prefetched chart: " + chartName);
      }
      catch (Exception e) {
        this.logger.info("Error prefetching chart: " + chartName + " " + e.getMessage());
      }
    }
  }
}

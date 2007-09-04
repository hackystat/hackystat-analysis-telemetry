package org.hackystat.telemetry.analyzer.configuration;

/**
 * Telemetry configuration exception.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TelemetryConfigurationException extends Exception {
 
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs this instance with an exception message.
   * 
   * @param message The exception message.
   */
  public TelemetryConfigurationException(String message) {
    super(message);
  }
  
  /**
   * Constructs this instance with a wrapped exception.
   * 
   * @param throwable The wrapped exception.
   */
  public TelemetryConfigurationException(Throwable throwable) {
    super(throwable);
  }
}
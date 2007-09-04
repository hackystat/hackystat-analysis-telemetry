package org.hackystat.telemetry.analyzer.model;

/**
 * Telemetry data model exception.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TelemetryDataModelException extends Exception {
 
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs this instance with an exception message.
   * 
   * @param message The exception message.
   */
  public TelemetryDataModelException(String message) {
    super(message);
  }
  
  /**
   * Constructs this instance with a wrapped exception.
   * 
   * @param throwable The wrapped exception.
   */
  public TelemetryDataModelException(Throwable throwable) {
    super(throwable);
  }
}
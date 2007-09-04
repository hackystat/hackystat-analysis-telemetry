package org.hackystat.telemetry.analyzer.language;

/**
 * Telemetry language exception.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class TelemetryLanguageException extends Exception {
 
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs this instance with an exception message.
   * 
   * @param message The exception message.
   */
  public TelemetryLanguageException(String message) {
    super(message);
  }
  
  /**
   * Constructs this instance with a wrapped exception.
   * 
   * @param throwable The wrapped exception.
   */
  public TelemetryLanguageException(Throwable throwable) {
    super(throwable);
  }
}

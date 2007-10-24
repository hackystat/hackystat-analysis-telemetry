package org.hackystat.telemetry.analyzer.reducer;

/**
 * Telemetry reducer exception.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TelemetryReducerException extends Exception {
 
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs this instance with an exception message.
   * 
   * @param message The exception message.
   */
  public TelemetryReducerException(String message) {
    super(message);
  }
  
  /**
   * Constructs this instance with a wrapped exception.
   * 
   * @param throwable The wrapped exception.
   */
  public TelemetryReducerException(Throwable throwable) {
    super(throwable);
  }
  
  /**
   * Constructs this instance with a wrapped exception.
   * 
   * @param message The message.
   * @param throwable The wrapped exception.
   */
  public TelemetryReducerException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
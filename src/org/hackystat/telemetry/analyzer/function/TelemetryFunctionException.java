package org.hackystat.telemetry.analyzer.function;

/**
 * Telemetry function exception.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TelemetryFunctionException extends Exception {
 
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs this instance with an exception message.
   * 
   * @param message The exception message.
   */
  public TelemetryFunctionException(String message) {
    super(message);
  }
  
  /**
   * Constructs this instance with a wrapped exception.
   * 
   * @param throwable The wrapped exception.
   */
  public TelemetryFunctionException(Throwable throwable) {
    super(throwable);
  }
  
  /**
   * Constructs this instance with a wrapped exception.
   * 
   * @param message The exception message.
   * @param throwable The wrapped exception.
   */
  public TelemetryFunctionException(String message, Throwable throwable) {
    super(message, throwable);
  }
}

package org.hackystat.telemetry.analyzer.evaluator;

/**
 * Telemetry evaluation exception.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TelemetryEvaluationException extends Exception {
 
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs this instance with an exception message.
   * 
   * @param message The exception message.
   */
  public TelemetryEvaluationException(String message) {
    super(message);
  }
  
  /**
   * Constructs this instance with a wrapped exception.
   * 
   * @param throwable The wrapped exception.
   */
  public TelemetryEvaluationException(Throwable throwable) {
    super(throwable);
  }
}

package org.hackystat.telemetry.service.client;

import org.restlet.data.Status;

/**
 * An exception that is thrown when the DailyProjectData server does not return a success code. 
 * @author Philip Johnson
 */
public class TelemetryClientException extends Exception {

  /** The default serial version UID. */
  private static final long serialVersionUID = 1L;
  
  /**
   * Thrown when an unsuccessful status code is returned from the Server.
   * @param status The Status instance indicating the problem.
   */
  public TelemetryClientException(Status status) {
    super(status.getCode() + ": " + status.getDescription());
  }

  /**
   * Thrown when an unsuccessful status code is returned from the Server.
   * @param status The status instance indicating the problem. 
   * @param error The previous error.
   */
  public TelemetryClientException(Status status, Throwable error) {
    super(status.getCode() + ": " + status.getDescription(), error);
  }
  
  /**
   * Thrown when some problem occurs with Client not involving the server. 
   * @param description The problem description.
   * @param error The previous error.
   */
  public TelemetryClientException(String description, Throwable error) {
    super(description, error);
  }
  
  /**
   * Thrown when some problem occurs with Client not involving the server. 
   * @param description The problem description.
   */
  public TelemetryClientException(String description) {
    super(description);
  }  

}

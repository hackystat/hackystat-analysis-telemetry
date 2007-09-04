package org.hackystat.telemetry.analyzer.util.project;


/**
 * Thrown when exceptions occur in project associated operation. This class implements exception
 * chaining. Use printStackTrace to see all exceptions.
 *
 * @author Hongbing Kou
 * @version $Id: ProjectException.java,v 1.1.1.1 2005/10/20 23:56:36 johnson Exp $
 */
@SuppressWarnings("serial")
public class ProjectException extends Exception {
  /**
   * Thrown when exceptions occur during elapsed time processing.
   *
   * @param detailMessage A message describing the problem.
   * @param previousException A possibly null reference to a prior exception.
   */
  public ProjectException(String detailMessage, Throwable previousException) {
    super(detailMessage, previousException);
  }


  /**
   * Thrown when exceptions occur during elapsed time processing.
   *
   * @param detailMessage A message describing the problem.
   */
  public ProjectException(String detailMessage) {
    super(detailMessage, null);
  }
}

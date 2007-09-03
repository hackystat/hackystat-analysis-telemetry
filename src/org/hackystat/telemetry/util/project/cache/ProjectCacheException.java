package org.hackystat.telemetry.util.project.cache;

/**
 * Generic exception for this package.
 *
 * @author Mike Paulding
 * @version $Id: ProjectCacheException.java,v 1.1.1.1 2005/10/20 23:56:36 johnson Exp $
 */
@SuppressWarnings("serial")
public class ProjectCacheException extends Exception {

  /**
   * Constructs an exception.
   * 
   * @param message Exception message.
   */
  public ProjectCacheException(String message) {
    super(message);
  }

  /**
   * Constructs a chained exception.
   *  
   * @param message Exception message.
   * @param e Original exception.
   */
  public ProjectCacheException(String message, Exception e) {
    super(message, e);
  }
}
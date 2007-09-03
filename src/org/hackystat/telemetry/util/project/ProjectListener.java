package org.hackystat.telemetry.util.project;

/**
 * Provides an interface to be implemented by classes that need to know about manipulations of
 * the Projects in this server, either their modification or deletion. 
 * Classes implementing this interface must also ensure that they add themselves as a listener via:
 * ProjectManager.getInstance().addProjectChangeListener(ProjectListener listener).
 * <p>
 * Important Note: all subclasses implementing this interface must _not_ change the Project
 * instance (either directly or indirectly) in their implementations of these methods.  
 * If they change the Project instance, that can re-fire the ProjectChanged event, 
 * causing the potential for an infinite loop. 
 * <p>
 * 
 * @author Philip Johnson, Hongbing Kou, Takuya Yamashita
 * @version $Id: ProjectListener.java,v 1.1.1.1 2005/10/20 23:56:36 johnson Exp $
 */
public interface ProjectListener {
  
  /**
   * Provides code that reacts to the Project definition, typically by invalidating
   * DailyProject instances that have cached project statistics based upon the previous definition
   * of the Project.
   * 
   * @param project The Project instance that has been defined. 
   */
  void projectDefined(Project project);
  
  /**
   * Provides code that reacts to the changing of a Project definition, typically by invalidating
   * DailyProject instances that have cached project statistics based upon the previous definition
   * of the Project.
   * 
   * @param project The Project instance that has been changed. 
   */
  void projectChanged(Project project);
  
  /**
   * Provides code that reacts to the deletion of a Project by its owner. Although
   * the Project instance is not physically deleted, it is removed from the ProjectManager and
   * is thus not available as a valid Project in the system. 
   * @param project The Project instance that has been specified as deleted. 
   */
  void projectDeleted(Project project);
}

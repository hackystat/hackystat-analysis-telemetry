package org.hackystat.telemetry.util.project.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.hackystat.telemetry.util.ServerProperties;
import org.hackystat.telemetry.util.user.User;
import org.hackystat.telemetry.util.Day;
import org.hackystat.telemetry.util.project.Project;

/**
 * Provides a general data abstraction mechanism for raw sensor data based upon the locality of a
 * single Project and a single Day. This class allows developers to create cached summaries of
 * Project statistics computed from any number of SensorData types for a single Day. The benefits of
 * this mechanism are that higher level analysis facilities (such as Reduction functions) are
 * provided with a high-level API for gathering this information rather than dealing with low-level
 * sensor data, as well as the performance improvements possible through caching of these summaries
 * once created. The performance improvements should derive from both the elimination of redundant
 * computation, as well as the ability for the raw sensor data instances to be garbage collected.
 * <P>
 * Basic usage of DailyProjectData involves the following:
 * <ul>
 * <li> Create a subclass of this class, each instance of which will provide summary statistics for
 * a given Project and Day.
 * <li> The constructor must call super(Project, Day, String) in their constructor to initialize the
 * Project, Day, and Name fields.
 * <li> Each subclass should implement a getInstance method that returns the cached instance of the
 * class for a given Project and Day, or else creates it from scratch, cache it, and return it.
 * Clients should always call this getInstance method.
 * <li> Each subclass should also implement the getOneLineSummary and getSummaryTable methods, which
 * provide data useful for the DailyProjectSummary alert and analysis commands.
 * <li> While subclass instances will generally need to process raw sensor data in order to create
 * their summaries, they should not hold references to raw sensor data. The goal is to allow the raw
 * sensor data to be garbage-collected by the system.
 * <li> DailyProjectData subclasses are likely to be concurrently accessed. Be sure to use either a
 * ThreeKeyCache or util.concurrent classes to manage any collections of data created and maintained
 * inside these subclass instances.
 * </ul>
 * See any of the subclasses of DailyProjectData for examples of how to use this facility.
 * 
 * @author Philip Johnson, Hongbing Kou, Takuya Yamashita
 * @version $Id: DailyProjectData.java,v 1.1.1.1 2005/10/20 23:56:36 johnson Exp $
 */
public abstract class DailyProjectData {
  /** The project associated with this DailyProjectData. */
  protected Project project;
  /** The day associated with this DailyProjectData. */
  protected Day day;
  /** The name of this DailyProjectData analysis. */
  protected String name;
  /** A logger for debugging purposes. */
  //protected Logger logger;

  /**
   * Initializes the base ProjectDailyAnalysis class instance's project and day fields.
   * 
   * @param project The project associated with this DailyProjectData.
   * @param day The Day associated with this DailyProjectData.
   * @param name The name of this DailyProjectData analysis.
   */
  public DailyProjectData(Project project, Day day, String name) {
    this.project = project;
    this.day = day;
    this.name = name;
    //this.logger = ServerProperties.getInstance().getLogger();
  }

  /** Ensure that subclasses have to call super(project, day, string). */
  private DailyProjectData() {
    // Empty body. We just want to make sure no one can instantiate this.
  }

  /**
   * Returns the Project associated with this DailyProjectData.
   * 
   * @return The Project associated with this DailyProjectData.
   */
  public Project getProject() {
    return this.project;
  }

  /**
   * Returns the Day associated with this DailyProjectData.
   * 
   * @return The Day associated with this DailyProjectData.
   */
  public Day getDay() {
    return this.day;
  }

  /**
   * Returns the name associated with this DailyProjectData analysis.
   * 
   * @return The name associated with this DailyProjectData.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns true if this DailyProjectData instance is within its project start end day.
   * 
   * @return True if this day is within the project's time interval.
   */
  public boolean withinProjectStartEndDay() {
    return this.project.inProjectInterval(this.day);
  }

  /**
   * The getSummaryStrings() method must return a list of two element String arrays representing a
   * concise summary of the measurements provided by this DailyProjectData instance. Element 0 of
   * the String array is a label, and Element 1 is the summary associated with that label. This
   * default implementation provides a list containing a single String array containing the name of
   * this DailyProjectData analysis and "No summary method available". Subclasses should override
   * this method to provide a true summary of their measures. This often takes the form of a "total"
   * value as well as a "personal" value for this data for the passed user (if a "personal" value
   * makes sense.)
   * 
   * @param user The User requesting this summary, or null.
   * @return The default List
   */
  public List<String[]> getSummaryStrings(User user) {
    List<String[]> summaryList = new ArrayList<String[]>();
    String[] summaryString = { this.getName(), "No summary method available." };
    summaryList.add(summaryString);
    return summaryList;
  }

  /**
   * Returns true if this DailyProjectData instance was based upon existing sensor data that was
   * able to be successfully processed.
   * 
   * @return True if sensor data exists for this DailyProjectData instance.
   */
  public abstract boolean hasSensorData();

  /**
   * The getDrillDowns() method must return a list of lists. Each interior list represents a single
   * "drill down" generated from the DailyProjectData class. While most DailyProjectData classes
   * will only have one "drill down", some could have several. For example DailyProjectCommit has a
   * drill down for Churn and another for Commits.
   * <p>
   * A DrillDown consists of a List of one dimensional String arrays. The first String array should
   * have only one element, which is interpreted as the title of that Drill Down. Element 0 of each
   * array is always assumed as a label. The remaining entries in the array are the values
   * associated with that label. If a String array contains only one element, then that is
   * interpreted as a title for a new set of details.
   * <p>
   * This default implementation simply returns a list containing a single List representing the
   * default DrillDown, which indicates that no drill down is defined for this subclass.
   * 
   * @param user The User requesting this DrillDowns list, or null.
   * @return The default DrillDowns list.
   */
  public List<List<String[]>> getDrillDowns(User user) {
    List<List<String[]>> drillDowns = new ArrayList<List<String[]>>();
    List<String[]> drillDown = new ArrayList<String[]>();
    String[] title = { this.getName() };
    String[] entry = { "Data", "No drill down method available." };
    drillDown.add(title);
    drillDown.add(entry);
    drillDowns.add(drillDown);
    return drillDowns;
  }
}
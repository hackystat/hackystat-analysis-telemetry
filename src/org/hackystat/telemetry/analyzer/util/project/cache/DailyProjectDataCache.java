package org.hackystat.telemetry.analyzer.util.project.cache;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Set;

//import org.hackystat.core.kernel.cache.ThreeKeyCache;
//import org.hackystat.core.kernel.sdt.SensorDataType;
//import org.hackystat.core.kernel.sensordata.SensorData;
//import org.hackystat.core.kernel.sensordata.SensorDataEntry;
//import org.hackystat.core.kernel.sensordata.SensorDataListener;
import org.hackystat.telemetry.analyzer.util.Day;
import org.hackystat.telemetry.analyzer.util.project.Project;
import org.hackystat.telemetry.analyzer.util.project.ProjectListener;
import org.hackystat.telemetry.analyzer.util.project.ProjectManager;
import org.hackystat.telemetry.analyzer.util.user.User;
import org.hackystat.telemetry.analyzer.util.user.UserManager;

/**
 * Provides cache-based access to DailyProjectData instances. 
 * DailyProjectDataCache has the following design properties:
 * <ul>
 * <li> It uses ThreeKeyCache for memory management and thread-safety.  
 * <li>It listens to ProjectChange events and clears all cache entries related to the given 
 * project when the corresponding Project definition changes.  This includes changes to 
 * the set of Workspace Roots associated with a project. 
 * <li> It listens to SensorData events and clears all cache entries related to a Project for 
 * which the user owning the SensorData participates in.
 * <li> It maintains counters of cache hits and cache misses for performance evaluation.
 * </ul> 
 * <p>  
 * Note that the cache listens to Project and SensorData events and then 
 * clears the cache of related instances.  This has an unintuitive side-effect: the 
 * top-level key for the DailyProjectData ThreeKeyCache can contain Project instances
 * for which no DailyProjectData instances have ever been defined.  This has no bad
 * consequences on operations, but if you're debugging (as I was) and see Project 
 * instances appear as if out of nowhere in the cache, it can be very disconcerting. 
 *
 * @author Philip Johnson, Hongbing Kou
 * @version $Id: DailyProjectDataCache.java,v 1.1.1.1 2005/10/20 23:56:36 johnson Exp $
 */
//public class DailyProjectDataCache implements SensorDataListener, ProjectListener {
  public class DailyProjectDataCache  {
  /** Singleton cache instance. */
  private static DailyProjectDataCache theInstance = new DailyProjectDataCache();
  /** Number of times the cache is consulted and doesn't contain the data (so it's added). */
  private int numMisses = 0;
  /** Number of times the cache is consulted and does contain the data. */
  private int numHits = 0;
  /** Number of times a cache entry has been cleared due to SensorData or Project updates. */
  private int numClears = 0;
  /** The daily project data cache: Project -> Day -> Analysis.class -> Analysis.instance  */
  //private ThreeKeyCache dailyDataCache = new ThreeKeyCache("Daily Project Data", false, 100);
  /** Project manager. */
  private ProjectManager projectManager = ProjectManager.getInstance();

  /** Initializes the DailyProjectDataCache by setting up listeners. */
  private DailyProjectDataCache() {
//    // Watch for changes to the set of projects.
//    this.projectManager.addProjectListener(this);
//    // Watch for changes to the sensor data. 
//    SensorData.addListener(this);
  }

  /**
   * Gets the singleton DailyProjectDataCache instance.
   * @return The singleton DailyProjectDataCache instance.
   */
  public static DailyProjectDataCache getInstance() {
    return DailyProjectDataCache.theInstance;
  }
  
  /**
   * Returns true if the ProjectDailyData instance corresponding to the specified Project,
   * Day, and DailyProjectData class is present in the cache, false otherwise.
   * @param project The project associated with this data.
   * @param day The Day associated with this data.
   * @param analysisClass The ProjectDailyData class associated with this data.
   * @return True if the DailyProject data instance exists in the cache.
   */
  public boolean isCached(Project project, Day day, Class analysisClass) {
    return true;
    //return (!(this.dailyDataCache.get(project, day, analysisClass) == null)); 
  }


  /**
   * Returns the DailyProjectData instance from the cache if present, or else creates and 
   * returns a new DailyProjectData instance. If newly created, it is automatically
   * placed into the cache unless caching is disabled.
   *
   * @param project The Project key.
   * @param day The day key.
   * @param analysisClass The analysis class type to be retrieved or instantiated.
   * @return The requested DailyProjectData instance.
   */
  public DailyProjectData get(Project project, Day day, Class analysisClass) {
    return null;
//    DailyProjectData data = (DailyProjectData) this.dailyDataCache.get(project, day, analysisClass);
//    if (data == null) {
//      // Create the DailyProjectData instance since not present, and then cache it.
//      this.numMisses ++;
//      data = this.createDailyProjectData(project, day, analysisClass);
//      this.put(project, day, analysisClass, data);
//      //ServerProperties.getInstance().getLogger().warning("Cache updated: " + this.toString());
//      //ServerProperties.getInstance().getLogger().warning("Cache addition.");
//      
//      // Retrieve it one more time, to make sure we're returning the object that actually made
//      // it into the cache (under multi-threaded conditions).
//      return (DailyProjectData) this.dailyDataCache.get(project, day, analysisClass); 
//    }
//    else {
//      this.numHits ++;
//      return data;
//    }
  }
  
  /**
   * Instantiates a new DailyProjectData instance of the type analysisClass for the given
   * Project and Day.  This method is used to automatically create new instances 
   * when the cache does not already hold the desired instance. 
   * @param project  The project associated with this DailyProjectData.
   * @param day The day associated with this DailyProjectData.
   * @param analysisClass  The DailyProjectData subclass to be instantiated. 
   * @return A new DailyProjectData instance of type analysisClass.
   * @throws IllegalArgumentException If problems during instantiation, indicating design error. 
   */
//  private DailyProjectData createDailyProjectData(Project project, Day day, Class analysisClass) 
//    throws IllegalArgumentException {
//    DailyProjectData analysis = null;
//    try {
//      Constructor[] constructors = analysisClass.getConstructors();
//      if (constructors == null) {
//        throw new IllegalArgumentException("Error: no constructor available for " + analysisClass);
//      }
//      
//      // Loop through all the constructors for the one with argument types Project and Day
//      Constructor constructor = null;
//      for (int i = 0; i < constructors.length; i++) {
//        Class parameterTypes[] = constructors[i].getParameterTypes();
//        if (parameterTypes != null && parameterTypes.length == 2) {
//          if (parameterTypes[0].equals(Project.class) && parameterTypes[1].equals(Day.class)) {
//            constructor = constructors[i];
//            break;
//          }
//        }
//      }
//      if (constructor == null) {
//        throw new Exception("Error: " + analysisClass + " has no Constructor(Project, Day).");
//      }
//      // Found the right constructor, so instantiate the object.      
//      Object[] parameters = new Object[2];
//      parameters[0] = project;
//      parameters[1] = day;
//      analysis = (DailyProjectData)constructor.newInstance(parameters);
//    }
//    catch (Exception e) {
//      System.out.println("Yikes!  Failed to instantiate: " + analysisClass);
//      System.out.println(StackTrace.toString(e));
//      throw new IllegalArgumentException("Error: " + analysisClass + " failed to instantiate.");
//    }
//    return analysis;
//  }


  /**
   * Put the analysis object into cache. Note that we put a hard reference in, so that the
   * entries in this cache cannot be GC'd.  This appears to improve performance significantly
   * (at least during trials on my laptop), but means that this cache will grow without bound.
   * We probably need to design some kind of custom cache size manager that runs once a day
   * and reduces the size when it gets too large. 
   *
   * @param project Project.
   * @param day Day.
   * @param analysisClass Hackystat analysis.
   * @param theInstance Project daily analysis instance.
   */
//  private void put(Project project, Day day, Class analysisClass, DailyProjectData theInstance) {
////    this.dailyDataCache.put(project, day, analysisClass, theInstance);
//    //this.dailyDataCache.putNoGC(project, day, analysisClass, theInstance);
//  }
  
  /**
   * Provides code that reacts to the Project definition. Required by ProjectListener.
   * 
   * @param project The Project instance that has been defined. 
   */
  public void projectDefined(Project project) {
    // do nothing.
  }

  /**
   * Clears all data associated with the given project if caching is enabled.
   * @param project The Project whose definition has been updated.
   */
  public void projectChanged(Project project) {
//    this.numClears += this.dailyDataCache.size(project);
//    this.dailyDataCache.clear(project);
  }
  
  /**
   * Clears all data associated with the given project if caching is enabled.
   * @param project The Project whose definition has been deleted.
   */
  public void projectDeleted(Project project) {
//    this.numClears += this.dailyDataCache.size(project);
//    this.dailyDataCache.clear(project);
  }

  /**
   * When new sensor data is received for a given user on a given Day, we are conservative and 
   * clear the cache of all DailyProjectData instances for all Projects that this user participates
   * in for that Day. This is regardless of whether or not the received data actually affects the
   * cached computations for those DailyProjectData instances or not.  
   *
   * @param sdt The type of new sensor data received.
   * @param userKey The user who received this data.
   * @param day The day associated with the new data.
   * @param entry The actual SensorDataEntry received.
   */
//  public void newEntry(SensorDataType sdt, String userKey, Day day, SensorDataEntry entry) {
////    User user = UserManager.getInstance().getUser(userKey);
////    Set projects = this.projectManager.getProjectsWithOwner(user);
////    for (Iterator i = projects.iterator(); i.hasNext();) {
////      Project project = (Project) i.next();
////      this.numClears += this.dailyDataCache.size(project, day);
////      this.dailyDataCache.clear(project, day);
////    }
////    projects = this.projectManager.getProjectsWithMember(user);
////    for (Iterator i = projects.iterator(); i.hasNext();) {
////      Project project = (Project) i.next();
////      this.numClears += this.dailyDataCache.size(project, day);
////      this.dailyDataCache.clear(project, day);
////    }
//  }
  
  
  /**
   * Clears cached daily project objects. 
   * 
   * @param user Hackystat user. 
   */
  public void clearCache(User user) {
//    Set projects = this.projectManager.getProjectsWithOwner(user);
//    for (Iterator i = projects.iterator(); i.hasNext();) {
//      Project project = (Project) i.next();
//      this.numClears += this.dailyDataCache.size(project);
//      this.dailyDataCache.clear(project);
//    }
//    projects = this.projectManager.getProjectsWithMember(user);
//    for (Iterator i = projects.iterator(); i.hasNext();) {
//      Project project = (Project) i.next();
//      this.numClears += this.dailyDataCache.size(project);
//      this.dailyDataCache.clear(project);
//    }
  }

  /**
   * Returns the number of times the cache did not contain the data, so it was created and added.
   * @return Number of misses.
   */
  public int getNumMisses() {
    return this.numMisses;
  }


  /**
   * Returns the number of times the cache was queried but did contain the data.
   * @return Number of hits
   */
  public int getNumHits() {
    return this.numHits;
  }
  
  /**
   * Returns the number of cache entries removed due to updates to Project or SensorData.
   * @return Number of cache entries cleared.
   */
  public int getNumClears() {
    return this.numClears;
  }
  
  /**
   * Returns the size of the internal ThreeKeyCache associated with this DailyProjectData cache.
   * @return The size of this cache.
   */
  public long getSize() {
    return 1;
  }

  /**
   * Returns a textual representation of the contents of the DailyProjectDataCache.
   * Each line provides shows the Project, Day, and analysis class instance. 
   * @return A string showing the contents of the DailyProjectDataCache.
   */
  public String toString() {
    StringBuffer buff = new StringBuffer();
//    buff.append("[DailyProjectDataCache");
//    for (Iterator i = this.dailyDataCache.getKey1Set().iterator(); i.hasNext(); ) {
//      Project project = (Project)i.next();
//      for (Iterator j = this.dailyDataCache.getKey2Set(project).iterator(); j.hasNext(); ) {
//        Day day = (Day)j.next();
//        for (Iterator k = this.dailyDataCache.getKey3Set(project, day).iterator(); k.hasNext(); ) {
//          Class analysisClass = (Class)k.next();
//          Object analysis = this.dailyDataCache.get(project, day, analysisClass);
//          buff.append("\n" + project.getName() + " " + day + " " + analysis);
//        }
//      }
//    }
//    buff.append("]");
    return buff.toString();
  }
}

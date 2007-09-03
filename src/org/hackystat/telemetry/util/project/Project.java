package org.hackystat.telemetry.util.project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.hackystat.telemetry.util.user.User;
import org.hackystat.telemetry.util.Day;
import org.hackystat.telemetry.util.selector.interval.DayInterval;
import org.hackystat.telemetry.util.selector.interval.Interval;
import org.hackystat.telemetry.util.selector.interval.MonthInterval;
import org.hackystat.telemetry.util.selector.interval.WeekInterval;
//import org.hackystat.telemetry.util.workspace.Workspace;


/**
 * Provides a thread-safe representation of a Project, consisting of a name, a description,
 * a set of Workspaces, an owner User, and a set of member Users. Members may
 * be either Pending (they have been invited by the Project owner but have not yet
 * confirmed) or Confirmed.  
 * <p>
 * Projects can only be created by the ProjectManager.  Once a Project instance has been created,
 * its name can not be changed. (You must delete and then create a new Project to get a new name.)
 * The ProjectManager is also responsible for enforcing semantic validity on Project fields, such
 * as ensuring that start day precedes end day, and that there is at least one workspace. 
 * However, certain aspects of the Project instance do change over time, such as the set of 
 * confirmed members, the set of Workspaces, and so forth.  
 * <p>
 * Some classes (for example, DailyProjectData subclasses) need to know when Project definitions
 * change, typically because they are caching results of data analyses that can be rendered 
 * invalid if the associated Project definition changes. These classes can implement the 
 * ProjectChangeListener interface and add themselves as a listener to ProjectChanged events in 
 * the ProjectManager.   
 *   
 *
 * @author Philip Johnson, Hongbing Kou
 * @version $Id: Project.java,v 1.1.1.1 2005/10/20 23:56:36 johnson Exp $
 */
public class Project implements Comparable {
  /** Indicates that the User is added to this Project but has not confirmed themselves. */
  static final String MEMBERSHIP_PENDING = "Pending";
  /** Indicates that the User has confirmed themselves for this Project. */
  static final String MEMBERSHIP_CONFIRMED = "Confirmed";
  /** Name of this Project. */
  private String name;
  /** Description of this Project. */
  private String description;
  /** Start Day of this Project. */
  //private Day startDay;
  /** End Day of this Project, or null if the end Day is unspecified. */
  //private Day endDay;
  /** The thread-safe set of Workspaces associated with this project, represented as Strings. */
  //private CopyOnWriteArraySet<Workspace> workspaceSet;
  /** The User that owns this Project. */
  //private User owner;
  /** Thread-safe mapping of all User members (including the owner) to their membership status. */
  //private ConcurrentHashMap<User, String> memberMap;


  /**
   * Creates a new Project instance containing a name, the Project owner, the description,
   * the start Day, the end Day (or null if unspecified), 
   * the initial set of Workspaces, the initial set of pending members, and the initial
   * set of confirmed members.  For convenience, the Owner can be present or absent in either the 
   * confirmed or pending list, but will be explicitly added if not present and explicitly
   * set to confirmed. 
   * This constructor is package private because only ProjectManager should create Projects,
   * and the ProjectManager is required to ensure the validity of the arguments. The rationale
   * is that the ProjectManager is responsible for the defining the "policy" regarding 
   * appropriate Project definition. 
   * The underlying Collection objects are all thread-safe. 
   * 
   * @param name The Project name, which should also be a globally unique ID on this server.
   * @param owner The User that is defining (and can thus delete) this Project.
   * @param description The description String associated with this Project.
   * @param startDay The Day this Project started. 
   * @param endDay The Day this Project ends, or null if unspecified.
   * @param workspaceSet A Set containing at least one Workspace.
   * @param pendingMembers A (potentially empty) Set containing pending members.
   * @param confirmedMembers A (potentially empty) Set containing confirmed members.
   */
   Project(String name, User owner, String description, Day startDay, Day endDay, 
      Set workspaceSet, Set<User> pendingMembers, Set<User> confirmedMembers) {
//     synchronized (Project.class) {
//      this.name = name;
//      this.owner = owner;
//      this.description = description;
//      this.startDay = startDay;
//      this.endDay = endDay;
//      // Initialize the thread safe workspace set.
//      this.workspaceSet = new CopyOnWriteArraySet<Workspace>(workspaceSet);
//      // Now set up the thead-safe memberMap built using pendingMembers and confirmedMembers.
//      this.memberMap = new ConcurrentHashMap<User, String>();
//      for (Iterator i = pendingMembers.iterator(); i.hasNext();) {
//        User member = (User) i.next();
//        this.memberMap.put(member, MEMBERSHIP_PENDING);
//      }
//      for (Iterator i = confirmedMembers.iterator(); i.hasNext();) {
//        User member = (User) i.next();
//        this.memberMap.put(member, MEMBERSHIP_CONFIRMED);
//      }
//      // Make sure the owner is present as a confirmed member.
//      this.memberMap.put(owner, MEMBERSHIP_CONFIRMED);
//    }
  }

  /**
   * Returns this Project's name.
   * @return The name of this Project.
   */
  public synchronized String getName() {
    return this.name;
  }

  /**
   * Returns this Project's owner.
   * @return The User who owns this Project.
   */
  public synchronized User getOwner() {
    return null;
//    return this.owner;
  }

  /**
   * Sets the description associated with this Project.
   * 
   * @param description The (non-null) description String for this Project.
   */
  public synchronized void setDescription(String description) {
    if (description == null) {
      this.description = "";
    }
    
    this.description = description;
  }

  /**
   * Returns the description associated with this Project.
   * @return This Project's description.
   */
  public synchronized String getDescription() {
    return this.description;
  }

  /**
   * Sets the start Day associated with this Project, which must always be non-null and 
   * which must always be earlier than the end Day.
   * 
   * @param startDay The start Day for this Project.
   * @throws ProjectException If startDay is null or after end day.
   */
  public synchronized void setStartDay(Day startDay) throws ProjectException {
//    if (startDay == null) {
//      throw new ProjectException("Start day is null in setStartDay.");
//    } 
//    
//    if (!(this.endDay == null) && !(startDay.isBefore(endDay))) {
//      throw new ProjectException("Start day " + startDay +  " is after end day " + endDay);
//    }
//    
//    this.startDay = startDay;
  }

  /**
   * Returns the startDay associated with this Project.
   * @return The start Day of this Project.
   */
  public synchronized Day getStartDay() {
    return null;
    //return this.startDay;
  }
  
  /**
   * Returns true if the passed day is within the interval of time defined by this Project.
   * @param day A day instance. Also could be null.
   * @return True if the day is within this Project's time interval.
   */
  public  synchronized boolean inProjectInterval(Day day) {
//    if (day == null) {
//      return false;
//    }
//    if (day.isBefore(this.startDay)) {
//      return false;
//    }
//    if (this.endDay == null) {
//      return true;
//    }
//    if (this.endDay.isBefore(day)) {
//      return false;
//    }
    return true;
  }
  
  /**
   * Returns true if the passed interval is within the project start and end day.
   * 
   * @param interval The interval.
   * @return True if the interval is within the project start and end day.
   */
  public synchronized boolean inProjectInterval(Interval interval) {
    return true;
//    Day startDay, endDay;
//    if (interval instanceof DayInterval) {
//      DayInterval dayInterval = (DayInterval) interval;
//      startDay = dayInterval.getStartDay();
//      endDay = dayInterval.getEndDay();
//    }
//    else if (interval instanceof WeekInterval) {
//      WeekInterval weekInterval = (WeekInterval) interval;
//      startDay = weekInterval.getStartWeek().getLastDay();
//      endDay = weekInterval.getEndWeek().getFirstDay();
//    }
//    else if (interval instanceof MonthInterval) {
//      MonthInterval monthInterval = (MonthInterval) interval;
//      startDay = monthInterval.getStartMonth().getLastDay();
//      endDay = monthInterval.getEndMonth().getFirstDay();
//    }
//    else {
//      throw new RuntimeException("Unknown interval type: " + interval);
//    }
//    
//    Day projectStartDay = this.getStartDay();
//    Day projectEndDay = this.getEndDay();
//    return ! ((projectStartDay != null && startDay.isBefore(projectStartDay))
//           || (projectEndDay != null && projectEndDay.isBefore(endDay)));
  }
  
  /**
   * Returns a string documenting the Project interval. 
   * @return A string documenting the Project interval. 
   */
  public synchronized String getInterval() {
    return null;
//    return "Start day: " + this.getStartDay().toString() + ", End day: " + 
//    ((this.getEndDay() == null) ? "undetermined" : this.getEndDay().toString());
  }

  /**
   * Sets the end Day associated with this Project. If the endDay is 
   * not null (indicating an unspecified end Day), then it must be
   * before the start Day for this Project.
   * 
   * @param endDay The end Day for this Project.
   * @throws ProjectException If passed endDay is not valid. 
   */
  public synchronized void setEndDay(Day endDay) throws ProjectException {
//    if (!(endDay == null) && (endDay.isBefore(startDay))) {
//      throw new ProjectException("End Day " + endDay + "is before start Day " + startDay + ".");
//    }
//    this.endDay = endDay;
  }

  /**
   * Gets the end day of the project
   * @return The end day of the project
   */
  public synchronized Day getEndDay() {
    return null;
    //return this.endDay;
  }

//  /**
//   * Adds the workspace to this Project.
//   * @param workspace The Workspace to be added to this Project.
//   */
//  public synchronized void addWorkspace(Workspace workspace) {
////    this.workspaceSet.add(workspace);
//  }
  
  /**
   * Returns true if the passed workspace is a member of this Project's set of workspaces. 
   * Also returns true if the passed workspace is a subworkspace of any of this Project's
   * workspaces.
   * @param workspace The workspace that might be a member of this Project's workspaces.
   * @return True if the Workspace is one of this Project's workspaces (or subworkspaces).
   */
//  public synchronized boolean isProjectWorkspace(Workspace workspace) {
////    if (workspace == null) {
////      return false;
////    }
////    
////    for (Iterator i = this.workspaceSet.iterator(); i.hasNext(); ) {
////      Workspace projectWorkspace = (Workspace) i.next();
////      if (workspace.isSubWorkspace(projectWorkspace)) {
////        return true;
////      }
////    }
//    
//    return false;
//  }
  
  /**
   * Returns project's workspace that is the parent workspace of the passed workspace. 
   * This method is added to have consistent top-level workspace of the project. It will
   * return null if the passed workspace is not a membed workspace.
   * 
   * 
   * @param workspace The workspace that might be a member of this Project's workspaces.
   * @return Parent workspace belongs to the project.
   */
//  public synchronized Workspace getProjectWorkspace(Workspace workspace) {
////    if (workspace == null) {
////      return null;
////    }
////    
////    for (Iterator i = this.workspaceSet.iterator(); i.hasNext(); ) {
////      Workspace projectWorkspace = (Workspace) i.next();
////      if (workspace.isSubWorkspace(projectWorkspace)) {
////        return projectWorkspace;
////      }
////    }
//    
//    return null;
//  }
  
  /**
   * Clears the set of workspaces associated with this project.
   */
  public synchronized void clearWorkspaces() {
//    this.workspaceSet.clear();
  }

//  /**
//   * Removes the specified Workspace from this Project, if it exists.
//   * 
//   * @param workspace The Workspace to be removed if it exists.
//   * @return True if it is removed sucessfully.
//   */
//  public synchronized  boolean removeWorkspace(Workspace workspace) {
//    return true;
////    return this.workspaceSet.remove(workspace);
//  }

  /**
   * Sets the Workspace set associated with this Project to the passed set of Workspaces.
   * @param workspaceSet The new Workspace set for this Project.
   */
  public synchronized void setWorkspaces(Set workspaceSet) {
//    this.workspaceSet = new CopyOnWriteArraySet<Workspace>(workspaceSet);
  }

  /**
   * Returns a (thread safe) iterator over the set of Workspaces 
   * associated with this project.
   * 
   * @return An iterator over the Workspaces in this project.
   */
  public synchronized Iterator getWorkspaceSetIterator() {
    return null;
//    return this.workspaceSet.iterator();    
  }
  
  /**
   * Returns a newly constructed set containing the current Workspaces in 
   * this Project. It's a shallow copy of reference to all workspaces 
   * included by this project.  A convenience method for JSP and other 
   * low-performance situations; use getWorkspaceSetIterator to avoid 
   * the cost of creating a new Set.
   * 
   * @return A new Set containing Workspaces.
   */
  public synchronized Set getWorkspaceSetCopy() {
    return null;
//    Set<Workspace> copySet = new TreeSet<Workspace>();
//    copySet.addAll(this.workspaceSet);
//    return copySet;
  }


  /**
   * Adds a new member to the project, setting them as pending.
   * Does nothing if the User is already associated with the project. 
   * @param member The (potentially) new member for the project.
   */
  public synchronized void addMember(User member) {
//    if (!this.memberMap.containsKey(member)) {
//      this.memberMap.put(member, MEMBERSHIP_PENDING);
//    }
  }

  /**
   * Removes a (non-owner) member from the project if they are present.
   * Does nothing if the member is the owner or if the member is not in the Project.
   * @param member The User to be removed from the Project.
   */
  public synchronized void removeMember(User member) {
//    if (!this.owner.equals(member)) {
//      this.memberMap.remove(member);
//    }
  }

  /**
   * Sets the User's membership confirmed if they are a member of this Project.
   * @param member The project member whose membership is to be set to confirmed.
   */
  public synchronized void confirmMembership(User member) {
//    if (this.memberMap.containsKey(member)) {
//      this.memberMap.put(member, MEMBERSHIP_CONFIRMED);
//    }
  }

  /**
   * Returns true if the User is a member of this Project and if their membership is confirmed.
   * @param member The User whose status is to be checked.
   * @return True if the User is a member of this Project and they are confirmed.
   */
  public synchronized boolean isConfirmedMember(User member) {
    return true;
//    return ((this.memberMap.containsKey(member)) &&
//            ((String) this.memberMap.get(member) == MEMBERSHIP_CONFIRMED));
  }
  
  /**
   * Returns true if the passed User is the owner of this Project.
   * @param user The user whose ownership status is being checked.
   * @return True if the User is this Project's owner.
   */
  public synchronized boolean isOwner(User user) {
    return true;
//    return this.owner.equals(user);
  }
  
  /**
   * Returns a string indicating the status of this member in this project.  
   * Can also return null if the user is not a member of this project at all.
   * @param member A user who must be a member of this project.
   * @return A string indicating the membership status of this User.
   */
  public synchronized String getMemberStatus(User member) {
    return null;
//    return (this.memberMap.containsKey(member)) ? (String) this.memberMap.get(member) : "Nonmember";
  }

  /**
   * Returns a newly constructed map of User->Status string, useful in JSP pages for specifying
   * the membership status of users in this Project.
   * @return A map of members to their membership status (confirmed or pending).
   */
  public synchronized Map<User, String> getMemberStatusMap() {
    return null;
//    HashMap<User, String> map = new HashMap<User, String>();
//    for (Map.Entry<User, String> entry : this.memberMap.entrySet()) {
//      map.put(entry.getKey(), entry.getValue());
//      
//    }
//    return map;
  }

  /**
   * Returns a newly created set of the confirmed members in this project.
   * @return A set of Users who are members of this Project with confirmed membership.
   */
  public synchronized Set<User> getConfirmedMembers() {
    return null;
//    Set<User> confirmedMembers = new HashSet<User>();
//    for (Iterator i = this.memberMap.keySet().iterator(); i.hasNext();) {
//      User member = (User) i.next();
//      String membership = (String) this.memberMap.get(member);
//      if (membership == MEMBERSHIP_CONFIRMED) {
//        confirmedMembers.add(member);
//      }
//    }
//    return confirmedMembers;
  }
  
  /**
   * Clears the set of members associated with this Project, except for the owner, who is always
   * a confirmed member of their project.
   */
  public synchronized void clearMembers() {
//    this.memberMap.clear();
//    this.memberMap.put(this.owner, MEMBERSHIP_CONFIRMED);
  }


  /**
   * Returns a newly created set of all members (confirmed or pending) of this Project, including
   * the owner.
   * @return A set of Users who are all members of this Project, including the owner.
   */
  public synchronized Set<User> getMembers() {
    return null;
//    HashSet<User> returnSet = new HashSet<User>();
//    returnSet.addAll(this.memberMap.keySet());
//    return returnSet; 
  }
  
  /**
   * Returns a string representation of this Project.
   * @return The string representation of this Project.
   */
  public synchronized String toString() {
    StringBuffer body = new StringBuffer();
//    body.append("[Project ")
//    .append(this.name)
//    .append(", Owner: ")
//    .append(this.owner.getUserEmail())
//    .append(", Start: ")
//    .append(this.startDay.toString())
//    .append(", End: ")
//    .append(((this.endDay != null) ? this.endDay.toString() : "Undetermined"))
//    .append(", Workspaces: ");
//    for (Iterator i = this.workspaceSet.iterator(); i.hasNext();) {
//      Workspace workspace = (Workspace) i.next();
//      body.append(workspace + ", ");
//    }
//    int toStringLength = super.toString().length();
//    String address = super.toString().substring(toStringLength - 6);
//    body.append(address + "]");
    return body.toString();
  }

  /**
   * Compares two projects based upon their name.
   * Recall that ProjectManager must guarantee that project names are unique.
   * @param obj The project to be compared.
   * @return -1 if less, 0 if equal, and 1 if more.
   */
  public synchronized int compareTo(Object obj) {
    return this.name.compareTo(((Project) obj).name);
  }
  
  /**
   * Returns the hashcode for this Project, which is based upon its name.
   * Recall that ProjectManager must guarantee that project names are unique.
   * @return This Project's hashcode.
   */
  public synchronized int hashCode() {
    return this.name.hashCode();
  }
  
  /**
   * Returns true if the passed Object is the equal to this Project.
   * @param obj The object to be checked for equality with this Project.
   * @return True if obj is this Project, false otherwise.
   */
  public synchronized boolean equals(Object obj) {
    return (this == obj);
  }
  
  /**
   * Find project member by email.
   * 
   * @param memberEmail The email address, which is case-insensitive.
   * 
   * @return The project member found, or null.
   */
  public synchronized User findMemberByEmail(String memberEmail) {
//    for (Iterator i = this.getMembers().iterator(); i.hasNext();) {
//      User member = (User) i.next();
//      if (memberEmail.equalsIgnoreCase(member.getUserEmail())) { 
//        return member; 
//      }
//    }
    return null;
  }
}
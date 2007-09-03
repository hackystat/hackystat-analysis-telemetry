package org.hackystat.telemetry.util.project;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

//import org.hackystat.core.common.selector.day.EndDayTestSelector;
//import org.hackystat.core.common.selector.day.StartDayTestSelector;
import org.hackystat.telemetry.util.ServerProperties;
import org.hackystat.telemetry.util.user.User;
import org.hackystat.telemetry.util.user.UserManager;
//import org.hackystat.core.kernel.test.HackystatTestConversation;
//import org.hackystat.core.kernel.test.HackystatTestException;
//import org.hackystat.core.kernel.test.HackystatTestParameters;
//import org.hackystat.core.kernel.test.SimpleTestSelector;
import org.hackystat.telemetry.util.Day;
//import org.hackystat.telemetry.util.workspace.Workspace;
//import org.hackystat.telemetry.util.workspace.WorkspaceCache;
//import org.hackystat.telemetry.util.workspace.WorkspaceException;
//import org.hackystat.telemetry.util.workspace.WorkspaceRootListener;
//import org.hackystat.telemetry.util.workspace.WorkspaceRootManager;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides a thread-safe singleton Manager for Project instances that manages Project 
 * instantiation, name uniqueness, storage and retrieval from disk, and in-memory access.
 * If the ProjectManager cannot be instantiated correctly (for example, the hackystat data directory
 * is corrupted in some way) then a warning message will be printed to the console, but
 * no error will be thrown.  We do it this way so that the singleton ProjectManager instance
 * can be instantiated at class-loading time (as part of the initialization of the static
 * instance variable) which guarantees that only one instance will be created without resorting
 * to synchronization.
 * <p>
 * Only the ProjectManager can create new Project instances, which allows it to guarantee the 
 * following semantic constraints on Project instances:
 * <ul>
 * <li> The name must be unique.
 * <li> Start day must be a Day instance.
 * <li> End day must be null or a Day instance prior to or equal to Start day.
 * <li> All members must be valid User instances.
 * <li> There must be at least one valid workspace in every Project.
 * </ol>
 * <p>
 * The saveProjects() method in this class must be invoked manually by clients in order to persist
 * new and changed Projects.  I'm not happy with this design decision, but the obvious alternative
 * (embedding calls to saveProject within each state-changing public operation of the Project class)
 * will lead to a half dozen redundent file writes for most Project management operations. So, for
 * now, I'll leave it in the (hopefully capable) hands of the clients of this class to ensure that
 * saveProject() is invoked whenever Projects are changed. You Have Been Warned.
 * <p>
 * The ProjectManager class also includes three methods for supporting testing.  The 
 * createProjectClientSide methods can be used for creating Project instances within the test
 * process memory space (i.e. without accessing a Hackystat server). In most cases, however, 
 * testers will want to use createProject and deleteProject, which use the Hackystat test 
 * framework to access the server and create/delete a test Project through the web-based interface.
 *
 * @author Philip Johnson, Hongbing Kou
 * @version $Id: ProjectManager.java,v 1.1.1.1 2005/10/20 23:56:36 johnson Exp $
 */
public class ProjectManager {
  /** The singleton ProjectManager instance */
  private static ProjectManager theInstance = new ProjectManager();
    
  /** The logger object */
  //private Logger logger = ServerProperties.getInstance().getLogger();
  /** Maps project name to project instance. The project name is globally unique in Hackystat */
  private Map<String, Project> projectMap = new ConcurrentHashMap<String, Project>();
  /** Maps the user to a List of the Projects owned by that User. */
  private Map<User, HashSet<Project>> ownerProjectMap = 
    new ConcurrentHashMap<User, HashSet<Project>>();
  /** Instances of classes implementing the ProjectListener interface. */
  private List<ProjectListener> projectListeners = new CopyOnWriteArrayList<ProjectListener>();

  /**
   * Creates the singleton ProjectManager instance, initializing its Project instances by 
   * reading them from disk. Logs an error message to the console and the hackystat logging
   * file in tomcat/logs if an error occurs during instantiation.
   */
  private ProjectManager() {
//    User user = null;
//    try {
//      ServerProperties serverProperties = ServerProperties.getInstance();
//      // Load the user.projects.doc.xml file or the (old) projects.xml file. 
//      for (Iterator i = UserManager.getInstance().iterator(); i.hasNext();) {
//        user = (User) i.next();
//        // First, try for the user.projects.doc.xml file.
//        Document doc = user.getJDomDocument("projects");
//        if (doc.hasRootElement()) {
//          loadProjectsFromJDom(user, doc);
//        }
//        // Otherwise, try to read in the old project.xml file, and if we find one, then 
//        // save out in the new format and then delete the old file. 
//        else {
//          File projectFile = new File(serverProperties.getUserDir(user), "projects.xml");
//          if (projectFile.exists()) {
//            loadProjectsFromFile(user, projectFile);
//            this.saveProjects(user);
//            projectFile.delete();
//          } 
//        }
//      }
//    }
//    catch (Exception e) {
//      String errorString = "ERROR: Failed to load " + user.getUserEmail() + "'s projects correctly!"
//                           + e;
//      ServerProperties.getInstance().getLogger().severe(errorString);    
//    }
//
//    ServerProperties.getInstance().getLogger().fine("Project Manager finished instantiation.");
//    
//    // Adds project manager to workspace root manager.
//    for (Iterator i = UserManager.getInstance().iterator(); i.hasNext();) {
//      user = (User) i.next();
//      try {
//        WorkspaceRootManager workspacerootManager = WorkspaceRootManager.getInstance(user);
//        workspacerootManager.addWorkspaceRootListener(this);
//      }
//      catch (WorkspaceException e) {
//        ServerProperties.getInstance().getLogger().info(e.getMessage());        
//      }      
//    }
  }

  
  /**
   * Returns the singleton ProjectManager instance.
   * 
   * @return The singleton ProjectManager instance. 
   */
  public static ProjectManager getInstance() {
    return theInstance;
  }

  
  /**
   * Reads the projects.xml file associated with this User to define their projects.
   * This method will be removed in future, since all Project info will use the JDOM version.
   *
   * @param owner The hackystat User whose projects are to be read.
   * @param projectsFile The projects.xml file
   * @exception ProjectException If error in reading projects.xml
   */
  private void loadProjectsFromFile(User owner, File projectsFile) throws ProjectException {
//    SAXBuilder builder = new SAXBuilder();
//    Document doc;
//    try {
//      doc = builder.build(new FileInputStream(projectsFile));
//      loadProjectsFromJDom(owner, doc);
//    }
//    catch (Exception e) {
//      throw new ProjectException("Error building Document object: " + projectsFile.getPath(), e);
//    }
  }

  /**
   * Creates and registers a set of Project instances from this JDom XML document object.
   * 
   * @param owner The User that owns these projects.
   * @param doc The JDOM XML Document object.
   * @throws Exception If an error occurs while processing these XML definitions of the Projects,
   * such as violation of the semantic validity constraints.
   */
  private void loadProjectsFromJDom(User owner, Document doc) throws Exception {
//    // <projects> element is the root element
//    Element projectsElement = doc.getRootElement();
//    List projectElementList = projectsElement.getChildren("project");
//    // Loops all the project elements
//    for (Iterator i = projectElementList.iterator(); i.hasNext();) {
//      Element projectElement = (Element) i.next();
//      try {
//      createProject(owner, projectElement);
//      }
//      catch (Exception e) {
//        String project = projectElement.getAttributeValue("name");
//        String email = owner.getUserEmail();
//        logger.info("Error loading project: " + project + " for user: " + email +  ": " + e);
//      }
//    }
  }
 
  /**
   * Creates and registers a Project given its XML definition and its owner, enforcing 
   * semantic validity constraints on Project instances. This method extracts the data from
   * the XML projectElement, then calls the public createProject method to actually check
   * the semantic validity constraints and create and register the Project.
   * 
   * @param owner The User who owns this project.
   * @param projectElement The XML definition of this project.
   * @return The Project instance corresponding to projectElement
   * @throws Exception If an error occurs while reading in this Project or if one of the 
   * validity conditions are violated.
   */
  private Project createProject(User owner, Element projectElement) throws Exception {
    return null;
//    // Get name
//    String name = projectElement.getAttributeValue("name");
//
//    // Get start and end days.
//    Day startDay;
//    Day endDay;
//    try {
//      String startUTCDay = projectElement.getAttributeValue("startday");
//      startDay = Day.getInstance(Long.parseLong(startUTCDay));
//      String endUTCDay = projectElement.getAttributeValue("endday");
//      endDay = null;
//      if (endUTCDay != null) {
//        endDay = Day.getInstance(Long.parseLong(endUTCDay));
//      }
//    }
//    catch (Exception e) {
//      throw new ProjectException ("Error getting start or end day in Project " + name, e);
//    }
//    
//    // Get description
//    String description = projectElement.getAttributeValue("description");
//    
//    // Get pending and confirmed member sets.  We will check to ensure these members exist.
//    Set<User> pendingMembers = new HashSet<User>();
//    Set<User> confirmedMembers = new HashSet<User>();
//    confirmedMembers.add(owner);
//    Element membersElement = projectElement.getChild("members");
//    if (!(membersElement == null)) {
//      // List of members
//      List memberList = membersElement.getChildren("member");
//      // Iterate through member list, building confirmedMembers and pendingMembers.
//      for (Iterator j = memberList.iterator(); j.hasNext();) {
//        Element memberElement = (Element) j.next();
//        String email = memberElement.getAttributeValue("email");
//        String status = memberElement.getAttributeValue("status");
//        User member; 
//        if (!UserManager.getInstance().isUserEmail(email)) {
//          throw new ProjectException("Unknown user email: " + email);
//        }
//        member = UserManager.getInstance().getUser(email);
//        if (status.equals(Project.MEMBERSHIP_CONFIRMED)) {
//          confirmedMembers.add(member);
//        }
//        else {
//          pendingMembers.add(member);
//        }
//      }
//    }
//
//    // Get workspace set.
//    Element workspacesElement = projectElement.getChild("workspaces");
//    if (workspacesElement == null) {
//      throw new ProjectException("Project " + name + " has no <workspaces> XML element.");
//    }
//    List workspaceList = workspacesElement.getChildren("workspace");
//
//    // Now build the  set of workspaces.
//    Set<Workspace> workspaceSet = new HashSet<Workspace>();
//    for (Iterator j = workspaceList.iterator(); j.hasNext();) {
//      Element workspaceElement = (Element) j.next();
//      String workspacePath = workspaceElement.getAttributeValue("value");
//      workspaceSet.add(WorkspaceCache.getInstance().getWorkspace(owner, workspacePath));
//    }
//    
//    // Now return the newly created and registered Project instance, 
//    // or throw a ProjectException if the semantic validity constraints are violated.
//    return createProject(name, owner, description, startDay, endDay, workspaceSet, 
//        pendingMembers, confirmedMembers);
  }

  /**
   * Saves the projects belonging to this owner to their user.projects.doc.xml file.
   * This is thread-safe since putJDomDocument is thread-safe.  
   * 
   * @param owner The Hackystat user whose project data is to be saved.
   * @exception ProjectException If an error occurs while saving the projects.
   */
  public void saveProjects(User owner) throws ProjectException {
//    Set<Project> projectSet = this.ownerProjectMap.get(owner);
//    // Only write out project data if this user actually has some project data.
//    if (!(projectSet == null)) {
//      this.logger.fine("Writing out project data for user : " + owner.getUserEmail());
//      // Create the root element of the XML document.
//      Element projectsElement = new Element("projects");
//      // Build the project element list
//      List<Element> projectElementList = new ArrayList<Element>();
//      for (Iterator i = projectSet.iterator(); i.hasNext();) {
//        Element projectElement = createProjectElement((Project) i.next());
//        projectElementList.add(projectElement);
//      }
//      projectsElement = projectsElement.setContent(projectElementList);
//      // Construct the Document object with the root tag
//      Document projectDoc = new Document(projectsElement);
//      // Save projects to user.projects.doc.xml file 
//      owner.putJDomDocument(projectDoc, "projects");
//    }
  }
  
  /**
   * Adds a ProjectListener instance to the list of class instances whose methods are invoked
   * when Projects are created, changed, or deleted.
   * @param listener The instance of a ProjectListener.
   */
  public void addProjectListener(ProjectListener listener) {
    this.projectListeners.add(listener);
  }
  
  /**
   * Invokes the projectDefined method of all ProjectListener instances, passing each
   * the Project instance that has just been defined. 
   * 
   * @param project The Project that has been defined. 
   */
  public void notifyProjectDefined(Project project) {
    for (ProjectListener listener : this.projectListeners) {
      listener.projectDefined(project); 
    }
  }
  
  /**
   * Invokes the projectChanged method of all ProjectListener instances, passing each
   * the Project instance that has just been modified.
   * This method is package private because it must be invoked by all of the Project set* and add* 
   * methods that result in changes to a Project definition.  
   * 
   * @param project The Project that has been changed. 
   */
  public void notifyProjectChange(Project project) {
    for (ProjectListener listener : this.projectListeners) {
      listener.projectChanged(project); 
    }
  }
  
  /**
   * Invokes the projectDeleted method of all ProjectListener instances, passing each the Project
   * instance that has just been deleted as a result of a call to ProjectManager.deleteProject.
   * @param project The Project that has just been deleted.
   */
  private void notifyProjectDeleted (Project project) {
    for (ProjectListener listener : this.projectListeners) {
      listener.projectDeleted(project); 
    }
  }
  
  /**
   * Creates a project JDOM element from its corresponding Project instance.
   *
   * @param project The project instance
   * @return The JDOM project element associated with this instance. 
   */
  private Element createProjectElement(Project project) {
    return null;
//    Element projectElement = new Element("project");
//    // Start with the simple attributes.
//    projectElement.setAttribute("name", project.getName());
//    projectElement.setAttribute("owner", project.getOwner().getUserEmail());
//    projectElement.setAttribute("startday",
//      String.valueOf(project.getStartDay().getDate().getTime()));
//    if (project.getEndDay() != null) {
//      projectElement.setAttribute("endday",
//        String.valueOf(project.getEndDay().getDate().getTime()));
//    }
//    projectElement.setAttribute("description", project.getDescription());
//    
//    // Now build the internal list of members with their email and membership status.
//    List<Element> memberElementList = new ArrayList<Element>();
//    for (Iterator i = project.getMembers().iterator(); i.hasNext();) {
//      User member = (User) i.next();
//      Element memberElement = new Element("member");
//      memberElement = memberElement.setAttribute("email", member.getUserEmail());
//      String status = project.isConfirmedMember(member) ? Project.MEMBERSHIP_CONFIRMED : 
//                                                          Project.MEMBERSHIP_PENDING;
//      memberElement = memberElement.setAttribute("status", status);
//      memberElementList.add(memberElement);
//    }
//    Element membersElement = new Element("members");
//    membersElement.setContent(memberElementList);
//    projectElement.addContent(membersElement);
//    
//    // Now build the list of workspaces.
//    List<Element> workspaceElementList = new ArrayList<Element>();
//    for (Iterator i = project.getWorkspaceSetIterator(); i.hasNext();) {
//      Workspace workspace = (Workspace) i.next();
//      Element workspaceElement = new Element("workspace");
//      workspaceElement = workspaceElement.setAttribute("value", workspace.getCanonicalPath());
//      workspaceElementList.add(workspaceElement);
//    }
//    Element workspacesElement = new Element("workspaces");
//    workspacesElement.setContent(workspaceElementList);
//    projectElement.addContent(workspacesElement);
//    
//    // Now return the JDOM element containing the representation of the Project instance.
//    return projectElement;
  }


  /**
   * Creates and returns a new Project instance given its name, owner, description, start day, 
   * end day, workspace(s), pending members, and confirmed members, as long as all semantic 
   * validity constraints are met. See the ProjectManager class-level JavaDoc for the current
   * semantic validity constraints on projects.  
   * 
   * Regiesters this Project, but does not save it out to disk (since this method is called
   * by the other createProject that is reading Projects in from disk).
   *
   * @param name The name of the new Project.
   * @param owner The owner of the new Project.
   * @param description A descriptive string for the new Project.
   * @param startDay The Day that this Project started.
   * @param endDay The Day that this Project ends, or null if there is no end Day.
   * @param workspaces The non-empty set of Workspaces associated with this Project.
   * @param pendingMembers The set of Users who are pending membership.
   * @param confirmedMembers The set of Users who have confirmed membership.
   * @return The newly created Project instance. 
   * @exception ProjectException If the semantic validity constraints are violated.
   */
  public Project createProject(String name, User owner, String description, Day startDay, 
    Day endDay, Set workspaces, Set<User> pendingMembers, Set<User> confirmedMembers) 
      throws ProjectException {
    return null;
//    
//    // Validate name.
//    if ((name == null)) {
//      throw new ProjectException("The Project name must not be null.");
//    }
//    if ("".equals(name)) {
//      throw new ProjectException("The Project name must not be the empty string.");
//    }
//    if (this.projectMap.containsKey(name)) {
//      throw new ProjectException("The Project name " + name + " already exists.");
//    }
//    // Validate owner.
//    if ((owner == null)) {
//      throw new ProjectException("The Project owner must not be null in Project " + name);
//    }
//    // Ensure that description is not null.
//    description = (description == null) ? "" : description;
//    // Validate start day.
//    if ((startDay == null)) {
//      throw new ProjectException("The start day must not be null in Project " + name);
//    }
//    // Validate end day.
//    if (!(endDay == null) && endDay.isBefore(startDay)) {
//      throw new ProjectException("The end day must come after the start day in Project " + name);
//    }
//    // Validate workspaces
//    if (workspaces.isEmpty()) {
//      throw new ProjectException("At least one Workspace must be specified in Project " + name);
//    }
//    // Validate confirmed and pending members.
//    if (pendingMembers == null) {
//      throw new ProjectException("The pending member set must not be null in Project " + name);
//    }
//    if (confirmedMembers == null) {
//      throw new ProjectException("The confirmed member set must not be null in Project " + name);
//    }
//    
//    // We got here, so all of the arguments to the Project constructor are valid.
//    Project project = new Project(name, owner, description, startDay, endDay, workspaces,
//        pendingMembers, confirmedMembers);
//    
//    // Now register this project with the Manager.
//    this.projectMap.put(name, project);
//    HashSet<Project> projectSet = this.ownerProjectMap.get(owner);
//    // Create the data structure holding the list of Projects for this User if missing.
//    if (projectSet == null) {
//      projectSet = new HashSet<Project>();
//      this.ownerProjectMap.put(owner, projectSet);
//    }
//    // Add this Project to the set of Projects owned by this User.
//    projectSet.add(project);
//    // Return the newly created Project.
//    this.logger.fine("Project defined: " + project);
//    return project;
  }


  /**
   * Returns true if the passed name is a Project name.
   * 
   * @param name The possible Project name.
   * @return True if name is a Project name, false otherwise.
   */
  public boolean isProject(String name) {
    return this.projectMap.containsKey(name);
  }


  /**
   * Returns a newly created Set containing the Projects owned by the User.
   * 
   * @param owner A Hackystat user instance.
   * @return The (possibly empty) set of Projects owned by this user.
   */
  public Set<Project> getProjectsWithOwner(User owner) {
    HashSet<Project> ownedProjectSet = new HashSet<Project>();
    if (this.ownerProjectMap.containsKey(owner)) {
      HashSet<Project> projectSet = this.ownerProjectMap.get(owner); 
      ownedProjectSet.addAll(projectSet);
    }
    
    return ownedProjectSet;
  }


  /**
   * Returns a newly created Set containing the Projects for which the User is 
   * a (confirmed or pending) member but not the owner.
   * 
   * @param user The Hackystat User.
   * @return The Set of Projects this User is associated with either as a member but not owner.
   */
  public Set<Project> getProjectsWithMember(User user) {
    Set<Project> projectSet = new HashSet<Project>();
//    for (Iterator i = this.projectMap.values().iterator(); i.hasNext();) {
//      Project project = (Project) i.next();
//      // If the user is the project owner then skip it
//      if (!project.getOwner().equals(user)) {
//        Set users = project.getMembers();
//        if (users.contains(user)) {
//          projectSet.add(project);
//        }
//      }
//    }
    return projectSet;
  }
  
  /**
   * Returns a Set of all Projects with which this User is associated either as an Owner or Member.
   * 
   * @param user The User whose Projects are to be returned.
   * @return The set of all Projects associated with this User.
   */
  public Set<Project> getAllProjects(User user) {
    Set<Project> projectSet = new HashSet<Project>();
    projectSet.addAll(getProjectsWithMember(user));
    projectSet.addAll(getProjectsWithOwner(user));
    return projectSet;
  }
  
  
  /**
   * Returns a thread-safe iterator over all of the Project instances registered with this Manager.
   * @return An iterator over Project instances. 
   */
  public Iterator iterator() {
    return this.projectMap.values().iterator();
  }


  /**
   * Gets the project with the project name. Returns null if the name doesn't identify a
   * currently registered Project.
   * @param name The name of the Project to be retrieved.
   * @return The Project associated with this name, or null if there is no currently registered
   * Project with this name.
   */
  public Project getProject(String name) {
    return (Project) this.projectMap.get(name);
  }


  /**
   * Deletes the Project with the passed name from this Manager's set of Projects.
   * Does nothing if the Project is not a currently registered Project.
   * 
   * @param name The name of the Project to be deleted.
   * @throws ProjectException If problems occur while saving out the revised Project XML file for
   * the User who owns this Project that is being deleted.
   */
  public void deleteProject(String name) throws ProjectException {
//    Project project = (Project) this.projectMap.get(name);
//    if (!(project == null)) {
//      User owner = project.getOwner();
//      Set projectSet = (Set) this.ownerProjectMap.get(owner);
//      this.projectMap.remove(name);
//      projectSet.remove(project);
//      saveProjects(owner);
//      notifyProjectDeleted(project);
//    }
  }


  /**
   * Notifies ProjectListener instances when any user modifies their workspace roots, which
   * can potentially modify the set of Workspaces associated with any of their projects.
   * This implementation is not "smart", in that it simply invokes the ProjectChanged method
   * of all the Projects this User is involved with, rather than attempting to figure out 
   * what Projects are actually affected by a change in Workspace roots (which is pretty 
   * difficult, actually). 
   * 
   * @param user The Hackystat User who has changed their Workspace Root settings.
   * @param workspaceRootSet The new Workspace Root set (ignored).
   * @throws WorkspaceException If some problem occurs during processing (unlikely).
   */
  public void newWorkspaceRoots(User user, Set workspaceRootSet) {
//    // First, call the notify method on all the projects that this User owns.
//    Set ownedProjects = getProjectsWithOwner(user);
//    for (Iterator i = ownedProjects.iterator(); i.hasNext();) {
//      Project project = (Project) i.next();
//      notifyProjectChange(project);
//    }
//    
//    // Now call it on all the Projects for which this User is a non-Owner member.
//    Set involvedProjects = getProjectsWithMember(user);    
//    for (Iterator i = involvedProjects.iterator(); i.hasNext();) {
//      Project project = (Project) i.next();
//      notifyProjectChange(project);
//    }
  }
  
  /**
   * Creates and returns a test Project with the specified name on the client-side, which means 
   * the Project is created in the testing framework itself rather than through interactions
   * with a Hackystat server. Most test code will want to use createTestProject instead of this
   * method.
   * The test Project is created with the following attributes:
   * <ul>
   * <li> The owner is the testdataset user, and there are no other members.
   * <li> There are three workspaces: c:\\cvs\\, c:\\svn\\, and  C:\\work\\hackyStdExt\\.
   * <li> The project duration is from 01-Jan-2002 to has no end date.
   * </ul>
   * Clients specify the name so that separate test cases can manipulate their own Projects.
   * These properties ensure that there is data associated with this Project for retrieval.
   * If the Project with this name already exists, then nothing happens.
   * This Project instance is not saved to disk, so it is not generally necessary to explicitly
   * delete it, but the client can call ProjectManager.deleteProject(name) to do so if they wish.
   * 
   * @param name The name of the Project to be defined or retrieved.
   * @return The new test Project instance. 
   * @throws Exception If there are errors during Project instantiation.
   */
  public Project createTestProjectClientSide(String name) throws Exception {
//    User testUser = UserManager.getInstance().getTestUser();
//    
//    // If the test project already exists, then return it instead.
//    if (this.isProject(name)) {
//      return this.getProject(name);
//    }
//    
//    // Otherwise the project does not exist, so set up the project properties.
//    Set<Workspace> workspaces = new HashSet<Workspace>();
//    workspaces.add(WorkspaceCache.getInstance().getWorkspace(testUser, "C:\\svn\\"));
//    workspaces.add(WorkspaceCache.getInstance().getWorkspace(testUser, "C:\\cvs\\"));
//    workspaces.add(WorkspaceCache.getInstance().getWorkspace(testUser, "C:\\work\\hackyStdExt\\"));
//    Day startDay = Day.getInstance("01-Jan-2002");
//    Day endDay = null;
//    String description = "Project Description";
//    Set<User> members = new HashSet<User>();
//    Project testProject = this.createProject(name, testUser, description, startDay, endDay, 
//        workspaces, members, members);
//    return testProject;
    return null;
  }
  
  /**
   * Creates and returns a test Project with the specified attributes on the client-side, which
   * means the Project is created in the testing framework itself rather than through interactions
   * with the Hackystat server. This version of createProjectClientSide enables test developers
   * who need control over the Project attributes to define their own version. If the Project
   * with the passed name is already defined, then that Project is returned. 
   * 
   * @param name The name of the test Project to be defined or retrieved. 
   * @param owner The User who should be the owner of this test project. This is the only 
   * User involved with this Project. 
   * @param startDay The start Day for this Project.
   * @param endDay The end Day for this Project.
   * @param workspaceSet  The set of Workspace instances to be associated with this Project. 
   * Must be non-empty.
   * @return The Project corresponding to this definition.
   * @throws Exception If errors occur during definition of this Project.
   */
  public Project createTestProjectClientSide(String name, User owner, Day startDay, Day endDay,
      Set workspaceSet) throws Exception {
//    
//    // If the test project already exists, then return it instead.
//    if (this.isProject(name)) {
//      return this.getProject(name);
//    }    
//    Set<User> members = new HashSet<User>();
//    // Return the newly created project according to the callers requirements.
//    return this.createProject(name, owner, "Description", startDay, endDay, workspaceSet, members,
//        members);
      return null;
  }
  
  /**
   * Defines a new test project by completing the Register Project page at the Hackystat server.
   * If the Project is found to already exist, then nothing is done. 
   * The test Project is created with the following attributes:
   * <ul>
   * <li> The owner is the testdataset user, and there are no other members.
   * <li> There are no workspaces.
   * <li> The project duration is from 01-Jan-2002 to 31-Dec-2003. 
   * </ul>
   * If this method returns, the testproject was successfully created, otherwise a
   * HackystatTestException is thrown.
   * @param test The test conversation to be used to perform this registration.
   * @param name The name of the new test project.
   * @throws HackystatTestException If errors occur during registration of this test project.
   */
//  public static void createTestProject(HackystatTestConversation test, String name) 
//    throws HackystatTestException {
////    // If we're logged in as the Admin, we will relogin as the test user.
////    if (test.isAdminLogin()) {
////      test.login();
////    }
////
////    String[] workspaces = {}; 
////    createTestProject(test, name, "01-Jan-2002", "31-Dec-2003", workspaces);
//
//  }
  
  /**
   * Defines a new test project by completing the Register Project page at the Hackystat server.
   * If a Project with the same name is found to already exist, then nothing is done. 
   * The test Project is created with the passed attributes.
   * If this method returns, the Project was successfully created, otherwise a
   * HackystatTestException is thrown.
   * The Project is defined for whatever user is currently logged in.
   * @param test The test conversation to be used to perform this registration.
   * @param name The name of the new test project.
   * @param startDay The start day in dd-MMM-yyyy format. For example, 01-Dec-2002.
   * @param endDay The end day in dd-MMM-yyyy format. For example, 01-Dec-2002.
   * @param workspaces Workspaces of the project.
   * @throws HackystatTestException If errors occur during registration of this test project.
   */
//  public static void createTestProject(HackystatTestConversation test, String name, 
//      String startDay, String endDay, String[] workspaces) throws HackystatTestException {
////    test.log("Attempting to define the project " + name);
////    // Begin by going to the Project Management page.
////    test.invokeCommand("Manage Project");
////    
////    
////    // If testdataset already owns this Project, return successfully right now.
////    try {
////      if (test.setResultTableID("ProjectOwnedTable").inColumn(0, name)) {
////       test.log("Project " + name + " was previously defined; no redefinition necessary.");
////        return;
////      }
////    }
////    catch (HackystatTestException e) {
////      // No project defined yet, so create it below.
////    }
////
////    //Project is not already defined, so go back and invoke Register Project on Preferences page.
////    HackystatTestConversation testResult = test.invokeCommand("Register Project");
////    
////    // Default C:\ or c:\ as workspace of the test project if there is no project workspace. 
////    if (workspaces == null || workspaces.length == 0) {
////      workspaces = new String[1];
////      if (testResult.getResultPage().indexOf("C:\\") > 0) {
////        workspaces[0] = "C:\\";
////      }
////      else {
////        workspaces[0] = "c:\\";
////      }
////    }
////    
////    StringBuffer workspacesString = new StringBuffer();
////    for (int i = 0; i < workspaces.length; i++) {
////      workspacesString.append(workspaces[i]);
////      if (i < workspaces.length - 1) {
////        workspacesString.append("\n");
////      }
////    }
////        
////    // Now set up the params for actual registration.
////    HackystatTestParameters[] params = 
////      { new SimpleTestSelector("Name", name),
////        new StartDayTestSelector(startDay), 
////        new EndDayTestSelector(endDay),
////        new SimpleTestSelector("EndDayType", "Determined"),
////        new SimpleTestSelector("Workspaces", workspacesString.toString()),
////        new SimpleTestSelector("Description", "Project description")};
////    // Now go back to the RegisterProject form on the Preferences page with test params.
////    test.invokeCommand("Register Project", params);
////    String feedback = test.setResultTableID("ProjectFeedbackTable").getResultCell(0,0);
////    if (feedback.endsWith("successful.")) {
////      test.log("Project " + name + " was defined successfully.");
////    }
////    else {
////      throw new HackystatTestException("Project not created successfully: " + feedback + test);
////    }
//  }
//  
  /**
   * Deletes the Project named 'name' from the Hackystat server.
   * If the testdataset user does not currently own this Project, or if any other problems
   * occur during deletion, then a HackystatTestException is thrown. 
   * @param test The HackystatTestConversation associated with this deletion request.
   * @param name The name of the Project to be deleted.
   * @throws HackystatTestException If any problems occur.
   */
//  public static void deleteTestProject(HackystatTestConversation test, String name) 
//    throws HackystatTestException {
////    // If we're logged in as the Admin, we will relogin as the test user.
////    test.log("Attempting to delete the project " + name);
////    if (test.isAdminLogin()) {
////      test.login();
////    }
////    // Begin by going to the Project Management page.
////    test.invokeCommand("Manage Project");
////    // If testdataset does not own this Project, throw error.
////    if (!test.setResultTableID("ProjectOwnedTable").inColumn(0, name)) {
////      test.log("Project " + name + " not owned by testdataset, cannot delete.");
////      throw new HackystatTestException(test.toString());
////    }
////    // Otherwise invoke the delete form on this page.
////    test.invokeCommand("DeleteProject" + name);
////    String feedback = test.setResultTableID("DeleteProjectFeedbackTable").getResultCell(0,0);
////    if (feedback.endsWith("deleted.")) {
////      test.log("Project " + name + " was deleted successfully.");
////    }
////    else {
////      test.log("Project " + name + " was not deleted successfully.");
////      throw new HackystatTestException(test.toString());
////    }
//  }
}

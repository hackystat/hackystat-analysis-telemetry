package org.hackystat.telemetry.service.resource.telemetry;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionManager;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionManagerFactory;
import org.hackystat.telemetry.analyzer.configuration.jaxb.TelemetryDefinition;
import org.hackystat.telemetry.service.server.Server;
import org.hackystat.telemetry.service.server.ServerProperties;
import org.restlet.Context;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import static org.hackystat.telemetry.service.server.Authenticator.AUTHENTICATOR_DPD_CLIENTS_KEY;
import static 
org.hackystat.telemetry.service.server.Authenticator.AUTHENTICATOR_SENSORBASE_CLIENTS_KEY;
import static org.hackystat.telemetry.service.server.ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY;
import static org.hackystat.telemetry.service.server.ServerProperties.SENSORBASE_FULLHOST_KEY;

/**
 * An abstract superclass for all Telemetry resources that supplies common 
 * initialization processing. 
 * This includes:
 * <ul>
 * <li> Extracting the authenticated user identifier (when authentication available)
 * <li> Extracting the URI elements and parameters. 
 * <li> Declares that the TEXT/XML representational variant is supported.
 * </ul>
 * 
 * @author Philip Johnson
 *
 */
public abstract class TelemetryResource extends Resource {
  
  /** To be retrieved from the URL as the 'email' template parameter, or null. */
  protected String uriUser = null; 

  /** To be retrieved from the URL as the 'project' template parameter, or null. */
  protected String projectName = null; 

  /** To be retrieved from the URL as the 'chart' template parameter, or null. */
  protected String chart = null; 

  /** To be retrieved from the URL as the 'granularity' template parameter, or null. */
  protected String granularity = null; 

  /** To be retrieved from the URL as the 'start' template parameter, or null. */
  protected String start = null; 

  /** To be retrieved from the URL as the 'end' template parameter, or null. */
  protected String end = null; 

  /** To be retrieved from the URL as the 'params' template parameter, or null. */
  protected String params = null; 
  
  /** The authenticated user, retrieved from the ChallengeResponse, or null. */
  protected String authUser = null;
  
  /** This server (telemetry). */
  protected Server telemetryServer;
  
  /** The sensorbase host (for authentication). */
  protected String sensorBaseHost;
  
  /** The dailyprojectdata host (for analysis). */
  protected String dpdHost;
  
  /** The standard error message returned from invalid authentication. */
  protected String badAuth = "User is not admin and authenticated user does not not match URI user";
  
  /** Records the time at which each HTTP request was initiated. */
  protected long requestStartTime = new Date().getTime();
 
  /**
   * Provides the following representational variants: TEXT_XML.
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public TelemetryResource(Context context, Request request, Response response) {
    super(context, request, response);
    if (request.getChallengeResponse() != null) {
      this.authUser = request.getChallengeResponse().getIdentifier();
    }
    this.telemetryServer = (Server)getContext().getAttributes().get("TelemetryServer");
    ServerProperties properties = this.telemetryServer.getServerProperties();
    this.dpdHost = properties.get(DAILYPROJECTDATA_FULLHOST_KEY);
    this.sensorBaseHost = properties.get(SENSORBASE_FULLHOST_KEY);
    this.chart = (String) request.getAttributes().get("chart");
    this.uriUser = (String) request.getAttributes().get("email");
    this.projectName = (String) request.getAttributes().get("project");
    this.granularity = (String) request.getAttributes().get("granularity");
    this.start = (String) request.getAttributes().get("start");
    this.end = (String) request.getAttributes().get("end");
    this.params = (String) request.getAttributes().get("params");
    getVariants().clear(); // copied from BookmarksResource.java, not sure why needed.
    getVariants().add(new Variant(MediaType.TEXT_XML));
  }

  /**
   * The Restlet getRepresentation method which must be overridden by all concrete Resources.
   * @param variant The variant requested.
   * @return The Representation. 
   */
  @Override
  public abstract Representation getRepresentation(Variant variant);
  
  /**
   * Creates and returns a new Restlet StringRepresentation built from xmlData.
   * The xmlData will be prefixed with a processing instruction indicating UTF-8 and version 1.0.
   * @param xmlData The xml data as a string. 
   * @return A StringRepresentation of that xmldata. 
   */
  public StringRepresentation getStringRepresentation(String xmlData) {
    return new StringRepresentation(xmlData, MediaType.TEXT_XML, Language.ALL, CharacterSet.UTF_8);
  }
  
  /**
   * Returns a DailyProjectDataClient instance associated with the User in this request. 
   * @return The DailyProjectDataClient instance. 
   */
  @SuppressWarnings("unchecked")
  public DailyProjectDataClient getDailyProjectDataClient() {
    Map<String, DailyProjectDataClient> userClientMap = 
      (Map<String, DailyProjectDataClient>)this.telemetryServer.getContext().getAttributes()
      .get(AUTHENTICATOR_DPD_CLIENTS_KEY);
    return userClientMap.get(this.authUser);
  }
  
  /**
   * Returns a SensorBaseClient instance associated with the User in this request. 
   * @return The SensorBaseClient instance. 
   */
  @SuppressWarnings("unchecked")
  public SensorBaseClient getSensorBaseClient() {
    Map<String, SensorBaseClient> userClientMap = 
      (Map<String, SensorBaseClient>)this.telemetryServer.getContext().getAttributes()
      .get(AUTHENTICATOR_SENSORBASE_CLIENTS_KEY);
    return userClientMap.get(this.authUser);
  }
  
  /**
   * Returns a list of TelemetryDefinition instances corresponding to all definitions in 
   * telemetry.definitions.xml. 
   * @return A List of TelemetryDefinition instances. 
   */
  public List<TelemetryDefinition> getTelemetryDefinitions() {
    TelemetryDefinitionManager manager =  
      TelemetryDefinitionManagerFactory.getGlobalPersistentInstance();
    return manager.getDefinitions();
  }
  
  /**
   * Generates a log message indicating the type of request, the elapsed time required, 
   * the user who requested the data, and the day.
   */
  protected void logRequest() {
    long elapsed = new Date().getTime() - requestStartTime;
    String msg = elapsed + " ms: " + this.chart + " " + uriUser + " " + this.projectName;
    telemetryServer.getLogger().info(msg);
  }
  
  /**
   * Generates a log message indicating the command. 
   * @param command The command (typically cache).
   */
  protected void logRequest(String command) {
    long elapsed = new Date().getTime() - requestStartTime;
    String msg = elapsed + " ms: " + command;
    telemetryServer.getLogger().info(msg);
  }
  
  /**
   * Called when an error resulting from an exception is caught during processing. 
   * @param msg A description of the error.
   * @param e A chained exception.
   */
  protected void setStatusError (String msg, Exception e) {
    String responseMsg = String.format("%s:%n  Request: %s %s%n  Caused by: %s", 
        msg,  
        this.getRequest().getMethod().getName(),
        this.getRequest().getResourceRef().toString(),
        e.getMessage());
    this.getLogger().info(responseMsg);
    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, responseMsg);
  }
  
  /**
   * Called when an error occurs during processing. 
   * @param msg A description of the error.
   */
  protected void setStatusError (String msg) {
    String responseMsg = String.format("%s:%n  Request: %s %s", 
        msg,  
        this.getRequest().getMethod().getName(),
        this.getRequest().getResourceRef().toString());
    this.getLogger().info(responseMsg);
    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, responseMsg);
  }
  
  /**
   * Called when an internal error occurs during processing. 
   * @param msg A description of the error.
   */
  protected void setStatusInternalError (String msg) {
    String responseMsg = String.format("%s:%n  Request: %s %s", 
        msg,  
        this.getRequest().getMethod().getName(),
        this.getRequest().getResourceRef().toString());
    this.getLogger().info(responseMsg);
    getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, responseMsg);
  }
  
  

}
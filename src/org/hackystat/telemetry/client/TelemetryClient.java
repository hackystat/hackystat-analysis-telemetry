package org.hackystat.telemetry.client;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.telemetry.resource.chart.jaxb.TelemetryChart;
import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;

/**
 * Provides a client to support access to the DailyProjectData service. 
 * @author Philip Johnson
 */
public class TelemetryClient {
  
  /** Holds the userEmail to be associated with this client. */
  private String userEmail;
  /** Holds the password to be associated with this client. */
  private String password;
  /** The Telemetry host, such as "http://localhost:9878/telemetry". */
  private String telemetryHost;
  /** The Restlet Client instance used to communicate with the server. */
  private Client client;
  /** Chart JAXBContext */
  private JAXBContext chartJAXB;
  /** The http authentication approach. */
  private ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
  /** The preferred representation type. */
  private Preference<MediaType> xmlMedia = new Preference<MediaType>(MediaType.TEXT_XML);
  /** To facilitate debugging of problems using this system. */
  private boolean isTraceEnabled = false;
  
  /**
   * Initializes a new TelemetryClient, given the host, userEmail, and password. 
   * Note that the userEmail and password refer to the underlying SensorBase client
   * associated with this Telemetry service.  This service does not keep its own
   * independent set of userEmails and passwords.  Authentication is not actually performed
   * in this constructor. Use the authenticate() method to explicitly check the authentication
   * credentials. 
   * @param host The host, such as 'http://localhost:9878/telemetry'.
   * @param email The user's email used for authentication. 
   * @param password The password used for authentication.
   */
  public TelemetryClient(String host, String email, String password) {
    validateArg(host);
    validateArg(email);
    validateArg(password);
    this.userEmail = email;
    this.password = password;
    this.telemetryHost = host;
    if (!this.telemetryHost.endsWith("/")) {
      this.telemetryHost = this.telemetryHost + "/";
    }
    if (this.isTraceEnabled) {
      System.out.println("TelemetryClient Tracing: INITIALIZE " + 
          "host='" + host + "', email='" + email + "', password='" + password + "'");
    }
    this.client = new Client(Protocol.HTTP);
    try {
      this.chartJAXB = 
        JAXBContext.newInstance(
            org.hackystat.telemetry.resource.chart.jaxb.ObjectFactory.class);
    }
    catch (Exception e) {
      throw new RuntimeException("Couldn't create JAXB context instances.", e);
    }
  }
  
  /**
   * Throws an unchecked illegal argument exception if the arg is null or empty. 
   * @param arg The String that must be non-null and non-empty. 
   */
  private void validateArg(String arg) {
    if ((arg == null) || ("".equals(arg))) {
      throw new IllegalArgumentException(arg + " cannot be null or the empty string.");
    }
  }
  
  /**
   * Does the housekeeping for making HTTP requests to the SensorBase by a test or admin user. 
   * @param method The type of Method.
   * @param requestString A string, such as "users". No preceding slash. 
   * @param entity The representation to be sent with the request, or null if not needed.  
   * @return The Response instance returned from the server.
   */
  private Response makeRequest(Method method, String requestString, Representation entity) {
    Reference reference = new Reference(this.telemetryHost + requestString);
    Request request = (entity == null) ? 
        new Request(method, reference) :
          new Request(method, reference, entity);
    request.getClientInfo().getAcceptedMediaTypes().add(xmlMedia); 
    ChallengeResponse authentication = new ChallengeResponse(scheme, this.userEmail, this.password);
    request.setChallengeResponse(authentication);
    if (this.isTraceEnabled) {
      System.out.println("TelemetryClient Tracing: " + method + " " + reference);
      if (entity != null) {
        try {
          System.out.println(entity.getText());
        }
        catch (Exception e) {
          System.out.println("  Problems with getText() on entity.");
        }
      }
    }
    Response response = this.client.handle(request);
    if (this.isTraceEnabled) {
      Status status = response.getStatus();
      System.out.println("  => " + status.getCode() + " " + status.getDescription());
    }
    return response;
  }
  
  /**
   * Takes a String encoding of a TelemetryChart in XML format and converts it. 
   * @param xmlString The XML string representing a TelemetryChart.
   * @return The corresponding TelemetryChart instance. 
   * @throws Exception If problems occur during unmarshalling.
   */
  private TelemetryChart makeChart(String xmlString) throws Exception {
    Unmarshaller unmarshaller = this.chartJAXB.createUnmarshaller();
    return (TelemetryChart)unmarshaller.unmarshal(new StringReader(xmlString));
  }
  
  /**
   * Authenticates this user and password with this Telemetry service, throwing a
   * TelemetryClientException if the user and password associated with this instance
   * are not valid credentials. 
   * Note that authentication is performed by checking these credentials with the underlying
   * SensorBase; this service does not keep its own independent set of usernames and passwords.
   * @return This TelemetryClient instance. 
   * @throws TelemetryClientException If authentication is not successful. 
   */
  public synchronized TelemetryClient authenticate() throws TelemetryClientException {
    // Performs authentication by invoking ping with user and password as form params.
    String uri = "ping?user=" + this.userEmail + "&password=" + this.password;
    Response response = makeRequest(Method.GET, uri, null); 
    if (!response.getStatus().isSuccess()) {
      throw new TelemetryClientException(response.getStatus());
    }
    String responseString;
    try {
      responseString = response.getEntity().getText();
    }
    catch (Exception e) {
      throw new TelemetryClientException("Bad response", e);
    }
    if (!"Telemetry authenticated".equals(responseString)) {
      throw new TelemetryClientException("Authentication failed");
    }
    return this;
  }
  
  /**
   * Returns a TelemetryChart instance from this server, or throws a
   * TelemetryClientException if problems occur.  
   * @param name The chart name.
   * @param user The user email.
   * @param project The project.
   * @param granularity Either Day, Week, or Month.
   * @param start The start day.
   * @param end The end day.
   * @return The TelemetryChart instance. 
   * @throws TelemetryClientException If the credentials associated with this instance
   * are not valid, or if the underlying SensorBase service cannot be reached, or if one or more
   * of the supplied user, password, or timestamp is not valid.
   */
  public synchronized TelemetryChart getChart(String name, String user, String project, 
      String granularity, XMLGregorianCalendar start, XMLGregorianCalendar end) 
  throws TelemetryClientException {
    String uri = 
      "chart/" + name + "/" + user + "/" + project + "/" + granularity + "/" + start + "/" + end;
    Response response = makeRequest(Method.GET,  uri, null);
    TelemetryChart chart;
    if (!response.getStatus().isSuccess()) {
      throw new TelemetryClientException(response.getStatus());
    }
    try {
      String xmlData = response.getEntity().getText();
      chart = makeChart(xmlData);
    }
    catch (Exception e) {
      throw new TelemetryClientException(response.getStatus(), e);
    }
    return chart;
  } 
  
  /**
   * Returns true if the passed host is a Telemetry host. 
   * @param host The URL of a Telemetry host, "http://localhost:9875/telemetry".
   * @return True if this URL responds as a Telemetry host. 
   */
  public static boolean isHost(String host) {
    // All sensorbase hosts use the HTTP protocol.
    if (!host.startsWith("http://")) {
      return false;
    }
    // Create the host/register URL.
    try {
      String registerUri = host.endsWith("/") ? host + "ping" : host + "/ping"; 
      Request request = new Request();
      request.setResourceRef(registerUri);
      request.setMethod(Method.GET);
      Client client = new Client(Protocol.HTTP);
      Response response = client.handle(request);
      String pingText = response.getEntity().getText();
      return (response.getStatus().isSuccess() && "Telemetry".equals(pingText));
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the host associated with this Telemetry client. 
   * @return The host.
   */
  public String getHostName() {
    return this.telemetryHost;
  }

}

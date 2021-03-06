package org.hackystat.telemetry.service.resource.ping;

import static org.hackystat.telemetry.service.server.ServerProperties.SENSORBASE_FULLHOST_KEY;

import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.telemetry.service.resource.telemetry.TelemetryResource;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * The PingResource responds to a GET {host}/ping with the string "Telemetry".
 * It responds to GET  {host}/ping?user={user}&password={password} with
 * "Telemetry authenticated" if the user and password are valid, and 
 * "Telemetry" if not valid. 
 * @author Philip Johnson
 */
public class PingResource extends TelemetryResource {
  /** From the URI, if authentication is desired. */
  private String user; 
  /** From the URI, if authentication is desired. */
  private String password;
  
  /**
   * The standard constructor.
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public PingResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.user = (String) request.getAttributes().get("user");
    this.password = (String) request.getAttributes().get("password");
  }
  
  /**
   * Returns the string "DailyProjectData" or "DailyProjectData authenticated", 
   * depending upon whether credentials are passed as form parameters and whether
   * they are valid. 
   * @param variant The representational variant requested.
   * @return The representation as a string.  
   */
  @Override
  public Representation represent(Variant variant) {
    try {
      String unauthenticated = "Telemetry";
      String authenticated = "Telemetry authenticated";
      // Don't try to authenticate unless the user has passed both a user and password. 
      if ((user == null) || (password == null)) {
        return new StringRepresentation(unauthenticated);
      }
      // There is a user and password. So, check the SensorBase to see if they're OK. 
      String sensorBaseHost = telemetryServer.getServerProperties().get(SENSORBASE_FULLHOST_KEY);
      boolean OK = SensorBaseClient.isRegistered(sensorBaseHost, user, password);
      return new StringRepresentation((OK ? authenticated : unauthenticated));
    }
    catch (Exception e) {
      setStatusError("Error during ping", e);
      return null;
    }
  }
}

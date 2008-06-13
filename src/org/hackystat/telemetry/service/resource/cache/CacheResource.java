package org.hackystat.telemetry.service.resource.cache;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.Date;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.telemetry.service.resource.telemetry.TelemetryResource;
import org.hackystat.utilities.stacktrace.StackTrace;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import static org.hackystat.telemetry.service.server.Authenticator.AUTHENTICATOR_DPD_CLIENTS_KEY;


/**
 * This resource responds to requests of form:
 * DELETE {host}/cache/{user}
 * DELETE {host}/cache/{user}/{dpdtype}
 * DELETE {host}/cache/{user}/{dpdtype}/{tstamp}
 * GET {host}/cache/{user}/{dpdtype}/keys
 * 
 * The DELETE requests clear the caches for the user, dpdtype, or individual entry.
 * The GET request returns the set of keys for that dpdtype in the cache, one per line.
 * The UriUser must always be the same as the authenticated user. 
 * 
 * @author Philip Johnson
 */
public class CacheResource extends TelemetryResource {
 
  /** Holds the (optional) DPD Type in the URI string. */
  private String dpdType = null;
  /** Holds the (optional) timestamp in the URI string. */
  private String tstamp = null;

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public CacheResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.dpdType = (String) request.getAttributes().get("dpdType");
    this.tstamp = (String) request.getAttributes().get("tstamp");
  }

  /**
   * Responds to GET {host}/cache/{user}/{dpdtype}/keys, returning a string indicating the 
   * keys in this cache.
   * Useful mainly for debugging purposes. 
   * Note that at present, the Server template will match anything, not just "keys", because it
   * is binding the last component of the URI to the variable "tstamp".  Since we only have a 
   * single GET URI pattern, we will let this bit of looseness slide by for now.  But 
   * eventually we will probably want to tighten this up.  
   * 
   * The uriUser must be the authuser. 
   * 
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    Logger logger = this.telemetryServer.getLogger();
    logger.fine("GET cache keys starting, auth/uri user is: " + authUser + "/" + uriUser);
    try {
      // [1] Make sure the authorized user is the same as the uriUser
      if (!this.authUser.equals(this.uriUser)) {
        String msg = "Authenticated user (" + this.authUser + ") must be the URI user (" +
        this.uriUser + ")";
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg);
        return null;
      }
      // [2] Get the keys.
      DailyProjectDataClient dpdClient = this.getDailyProjectDataClient();
      Set<String> keys = dpdClient.getCacheKeys(this.dpdType);
      return new StringRepresentation(keys.toString());
    }
    catch (Exception e) {
      this.telemetryServer.getLogger().warning("Chart process error: " + StackTrace.toString(e));
      return null;
    }
  }
  
  /**
   * Indicate the DELETE method is supported.
   * 
   * @return True.
   */
  @Override
  public boolean allowDelete() {
    return true;
  }
  
  /**
   * Responds to DELETE requests for clearing the cache. Includes:
   * <ul>
   * <li> DELETE {host}/cache/{user}
   * <li> DELETE {host}/cache/{user}/{dpdType}
   * <li> DELETE {host}/cache/{user}/{dpdType}/{tstamp}
   * </ul>
   * Returns 200 if cache delete command succeeded. 
   * The authorized user must be the same as the user specified in the URI.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void delete() {
    Date startTime = new Date();
    try {
      // [1] Make sure the authorized user is the same as the uriUser
      if (!this.authUser.equals(this.uriUser)) {
        String msg = "Authenticated user (" + this.authUser + ") must be the URI user (" +
        this.uriUser + ")";
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg);
        return;
      }
      // [2] Now get the associated dpdClient and invoke its clear operation.
      Map<String, DailyProjectDataClient> dpdMap =
        (Map<String, DailyProjectDataClient>)this.telemetryServer.getContext().getAttributes()
        .get(AUTHENTICATOR_DPD_CLIENTS_KEY);
      DailyProjectDataClient client = dpdMap.get(this.uriUser);
      if (client == null) {
        String msg = "Unexpected Null DPD client."; 
        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, msg);
      }
      // Otherwise we have the DPDClient for this user, so clear the cache.
      if ((this.tstamp == null) && (this.dpdType == null)) {
        client.clearCache();
      }
      else if ((this.tstamp == null) && (this.dpdType != null)) {
        client.clearCache(this.dpdType);
      }
      else if ((this.tstamp != null) && (this.dpdType != null)) {
        client.clearCache(this.dpdType, this.tstamp);
      }
      // Finish by writing a log message indicating the result of the cache deletion.
      StringBuffer msg = new StringBuffer(25);
      long elapsedTime = ((new Date()).getTime()) - startTime.getTime();
      msg.append(elapsedTime).append(" ms: Cache delete: ").append(this.uriUser);
      if (this.dpdType != null) {
        msg.append(' ').append(this.dpdType);
      }
      if (this.tstamp != null) {
        msg.append(' ').append(this.tstamp);
      }
      this.telemetryServer.getLogger().info(msg.toString());
    }
    catch (Exception e) {
      this.telemetryServer.getLogger().warning("Cache delete error: " + StackTrace.toString(e));
    }
  }
}

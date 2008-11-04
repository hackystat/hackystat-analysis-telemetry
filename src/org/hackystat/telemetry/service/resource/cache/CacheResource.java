package org.hackystat.telemetry.service.resource.cache;

import java.util.Map;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.telemetry.service.resource.telemetry.TelemetryResource;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import static org.hackystat.telemetry.service.server.Authenticator.AUTHENTICATOR_DPD_CLIENTS_KEY;


/**
 * This resource responds to requests of form:
 * DELETE {host}/cache/{user}
 * DELETE {host}/cache/{user}/{project}
 * 
 * The DELETE requests clear the caches for the user, dpdtype, or individual entry.
 * The UriUser must always be the same as the authenticated user. 
 * 
 * @author Philip Johnson
 */
public class CacheResource extends TelemetryResource {
 
  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public CacheResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Indicate that GET is not supported.
   * 
   * @return False.
   */
  @Override
  public boolean allowGet() {
    return false;
  }

  /**
   * Get is not supported, but the method must be implemented.
   * 
   * @param variant Ignored.
   * @return Null.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    getResponse().setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    return null;
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
   * <li> DELETE {host}/cache/{user}/{project}
   * </ul>
   * Returns 200 if cache delete command succeeded. 
   * The authorized user must be the same as the user specified in the URI.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void delete() {
    try {
      // [1] Make sure the authorized user is the same as the uriUser
      if (!this.authUser.equals(this.uriUser)) {
        String msg = String.format("Authenticated user (%s) isn't UriUser (%s)", authUser, uriUser);
        setStatusError(msg);
        return;
      }
      // [2] Now get the associated dpdClient. Return immediately if we can't find one.
      Map<String, DailyProjectDataClient> dpdMap =
        (Map<String, DailyProjectDataClient>)this.telemetryServer.getContext().getAttributes()
        .get(AUTHENTICATOR_DPD_CLIENTS_KEY);
      DailyProjectDataClient client = dpdMap.get(this.uriUser);
      if (client == null) {
        setStatusError("No instantiated DPD client for " + this.uriUser);
        return;
      }
      
      // Otherwise we have the DPDClient for this user, so clear the cache.
      if (projectName == null) {
        client.clearCache();
      }
      else {
        client.clearCache(this.projectName);
      }
      logRequest(String.format("Cache delete of user %s and project %s", uriUser, projectName));
    }
    catch (Exception e) {
      setStatusError("Error during cache delete", e);
    }
  }
}

package org.hackystat.telemetry.service.server;

import java.util.HashMap;

import java.util.Map;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.restlet.Context;
import org.restlet.Guard;
import org.restlet.data.ChallengeScheme;

/**
 * Performs authentication of each HTTP request using HTTP Basic authentication. Checks
 * user/password credentials by pinging SensorBase, then caching authentic user/password
 * combinations. If a cached user/password combo does not match the current user/password combo,
 * then the SensorBase is pinged again (because maybe the user has changed their password recently).
 * <p>
 * Because this resource will always want to communicate with the underlying DailyProjectData
 * service, this Authenticator also creates a map of user-to-DailyProjectDataClient instances which
 * can be retrieved from the server context. This keeps the user password info in this class, while
 * making the DailyProjectDataClient instance available to the service.
 * 
 * @author Philip Johnson
 */
public class Authenticator extends Guard {
  
  /** A map containing previously verified credentials. */
  private Map<String, String> credentials = new HashMap<String, String>();
  
  /** A map containing DailyProjectDataClient instances, one per credentialled user. */
  private Map<String, DailyProjectDataClient> dpdMap = 
    new HashMap<String, DailyProjectDataClient>();
  
  /** A map containing SensorBaseClient instances, one per credentialled user. */
  private Map<String, SensorBaseClient> sensorbaseMap = 
    new HashMap<String, SensorBaseClient>();
   
  /** The sensorbase host, such as "http://localhost:9876/sensorbase/" */
  private String sensorBaseHost;

  /** The DailyProjectData host, such as "http://localhost:9877/dailyprojectdata/" */
  private String dpdHost;

  /** The key to be used to retrieve the DailyProjectDataClient map from the server context. */
  public static final String AUTHENTICATOR_DPD_CLIENTS_KEY = "authenticator.dpd.clients";

  /** The key to be used to retrieve the SensorbaseClient map from the server context. */
  public static final String AUTHENTICATOR_SENSORBASE_CLIENTS_KEY = 
    "authenticator.sensorbase.clients";

  /**
   * Initializes this Guard to do HTTP Basic authentication.
   * Puts the credentials map in the server context so that Resources can get the 
   * password associated with the uriUser for their own invocations to the SensorBase. 
   * @param context The server context.
   * @param sensorBaseHost The sensorbase service, such as 'http://localhost:9876/sensorbase/'.
   * @param dpdHost The DPD service, such as 'http://localhost:9877/dailyprojectdata/'.
   */
  public Authenticator (Context context, String sensorBaseHost, String dpdHost) {
    super(context, ChallengeScheme.HTTP_BASIC,  "DailyProjectData");
    this.sensorBaseHost = sensorBaseHost;
    this.dpdHost = dpdHost;
    context.getAttributes().put(AUTHENTICATOR_DPD_CLIENTS_KEY, dpdMap);
    context.getAttributes().put(AUTHENTICATOR_SENSORBASE_CLIENTS_KEY, sensorbaseMap);
  }
  
  /**
   * Returns true if the passed credentials are OK.
   * @param identifier The account name.
   * @param secretCharArray The password. 
   * @return If the credentials are valid.
   */
  @Override protected boolean checkSecret(String identifier, char[] secretCharArray) {
    String secret = new String(secretCharArray);
    // Return true if the user/password credentials are in the cache. 
    if (credentials.containsKey(identifier) &&
        secret.equals(credentials.get(identifier))) {
      return true;
    }
    // Otherwise we check the credentials with the SensorBase.
    boolean isRegistered = SensorBaseClient.isRegistered(sensorBaseHost, identifier, secret);
    if (isRegistered) {
      // Credentials are good, so save them and create clients for this user. 
      credentials.put(identifier, secret);
      // Only create a new client if there is no old one. This is intended to facilitate caching,
      // but has the problem that there's no way to get rid of bogus entries besides restarting
      // this service.  Something to worry about. 
      if (!dpdMap.containsKey(identifier)) {
        Server server = (Server)getContext().getAttributes().get("TelemetryServer");
        ServerProperties props = server.getServerProperties();
        DailyProjectDataClient client = new DailyProjectDataClient(dpdHost, identifier, secret); 
        if (props.isCacheEnabled()) { //NOPMD
          client.enableCaching(identifier, "telemetry", 
              props.getCacheMaxLife(), props.getCacheCapacity());
        }
        dpdMap.put(identifier, client);
      }
      if (!sensorbaseMap.containsKey(identifier)) {
        sensorbaseMap.put(identifier, new SensorBaseClient(sensorBaseHost, identifier, secret));
      }
    }
    return isRegistered;
  }
}

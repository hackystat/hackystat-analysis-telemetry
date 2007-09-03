package org.hackystat.telemetry.reducer;

import org.hackystat.telemetry.model.TelemetryStreamCollection;
import org.hackystat.telemetry.util.project.Project;
import org.hackystat.telemetry.util.selector.interval.Interval;

/**
 * Telemetry reducer interface. A reducer is reponsible for processing raw software
 * metrics to generate an instance of <code>TelemetryStreamCollection</code>. 
 * <p>
 * Note to implementator: 
 * <ul>
 *   <li> The "time periods" for the data points in the generated telemtry stream(s) 
 *        must be derived from the 'interval' parameter.  
 *   <li> Reducer implemetation class must have a public constructor which takes no parameter.
 *   <li> Reducer implemetation must be thread-safe.
 * </ul>
 * 
 * @author Qin ZHANG
 * @version $Id$
 */
public interface TelemetryReducer {

  /**
   * Computes telemetry streams. Note that if there is no data for any paticular time period, 
   * null should be used as the value for that time period.
   * 
   * @param project The project which defines the scope of metrics to be used in the computation.
   * @param interval The time interval.
   * @param parameters Parameters passed to reducer implementation. In case a reducer does not
   *        need any parameter, either null or an empty array may be passed.
   * 
   * @throws TelemetryReducerException If there is any error during metrics computation.
   * 
   * @return The resulting telemetry stream collection.
   */
  TelemetryStreamCollection compute(Project project, Interval interval, String[] parameters)
      throws TelemetryReducerException;
}

package org.hackystat.telemetry.reducer.impl;

import java.util.Iterator;
import java.util.List;

import org.hackystat.telemetry.model.TelemetryDataPoint;
import org.hackystat.telemetry.model.TelemetryStream;
import org.hackystat.telemetry.model.TelemetryStreamCollection;
import org.hackystat.telemetry.reducer.TelemetryReducer;
import org.hackystat.telemetry.reducer.TelemetryReducerException;
import org.hackystat.telemetry.reducer.util.IntervalUtility;
import org.hackystat.telemetry.util.project.Project;
import org.hackystat.telemetry.util.selector.interval.Interval;

/**
 * Draws straight line over the chart.
 * 
 * 
 * @author Pavel Senin.
 * 
 */
public class StraightLine implements TelemetryReducer {

  /**
   * Performs the computation.
   * 
   * @param project The project.
   * @param interval The interval over which the computation should be performed.
   * @param parameters parameters, first one is initial line value, second one is delta.
   * 
   * @return Telemetry stream collection.
   * @throws TelemetryReducerException If there is any error.
   */
  public TelemetryStreamCollection compute(Project project, Interval interval, String[] parameters)
      throws TelemetryReducerException {
    try {

      Double initialValue = 1D;
      Double deltaValue = 0D;
      if (parameters.length > 0) {
        initialValue = Double.valueOf(parameters[0]);
      }
      if (parameters.length > 1) {
        deltaValue = Double.valueOf(parameters[1]);
      }
      Double currentValue = initialValue;

      TelemetryStream telemetryStream = new TelemetryStream(null);
      // Use a utility class 'IntervalUtility' to break interval into periods.
      // One period corresponds to one data point on the telemetry stream.
      List periods = IntervalUtility.getPeriods(interval);
      for (Iterator i = periods.iterator(); i.hasNext();) {
        IntervalUtility.Period period = (IntervalUtility.Period) i.next();

        // Compute total elapsed time for the current period.
        // We make use of the DailyProjectSimpleSdt abstraction layer, instead of the raw data.
        // We simply add elapsed time for each data to get the total elapsed time for this period.

        currentValue += deltaValue;

        TelemetryDataPoint dp = new TelemetryDataPoint(period.getTimePeriod(), new Double(
            currentValue));

        // Add the data point to the telemetry stream.
        telemetryStream.addDataPoint(dp);
      }

      // Wrap the telemetry stream in a telemetry stream collection, and return it.
      TelemetryStreamCollection streams = new TelemetryStreamCollection(null, project, interval);
      streams.add(telemetryStream);
      return streams;

    }
    catch (Exception e) {
      throw new TelemetryReducerException(e.getMessage());
    }
  }

}

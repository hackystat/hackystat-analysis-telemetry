package org.hackystat.telemetry.analyzer.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hackystat.telemetry.analyzer.language.ast.TelemetryReportDefinition;

/**
 * The evaluation result after resolving a telemetry report definition. 
 *
 * @author (Cedric) Qin Zhang
 */
public class TelemetryReportObject {
  
  private TelemetryReportDefinition definition;
  private List<TelemetryChartObject> chartObjects = 
    new ArrayList<TelemetryChartObject>(); 
  
  /**
   * Constructs this instance.
   * 
   * @param definition The telemetry report definition.
   */
  TelemetryReportObject(TelemetryReportDefinition definition) {
    this.definition = definition;
  }
  
  /**
   * Gets the telemetry report definition.
   * 
   * @return The telemetry report definiton. 
   */
  public TelemetryReportDefinition getTelemetryReportDefinition() {
    return this.definition;
  }
  
  /**
   * Adds a telemetry chart object.
   * 
   * @param chartObject The telemetry chart object to be added.
   */
  void addChartObject(TelemetryChartObject chartObject) {
    this.chartObjects.add(chartObject);
  }
  
  /**
   * Gets a read-only list of all charts.
   * 
   * @return A read-only list containing <code>TelemetryChartObject</code> objects.
   */
  public List<TelemetryChartObject> getChartObjects() {
    return Collections.unmodifiableList(this.chartObjects);
  }
}

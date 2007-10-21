package org.hackystat.telemetry.analyzer.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartDefinition;

/**
 * The evaluation result after resolving a telemetry chart definition. 
 *
 * @author (Cedric) Qin Zhang
 */
public class TelemetryChartObject {

  private TelemetryChartDefinition definition;
  private List<SubChart> subCharts = new ArrayList<SubChart>();
  
  /**
   * Constructs this instance.
   * 
   * @param definition The telemetry chart definition.
   */
  TelemetryChartObject(TelemetryChartDefinition definition) {
    this.definition = definition;
  }
  
  /**
   * Gets the telemetry chart definition.
   * 
   * @return The telemetry chart definiton. 
   */
  public TelemetryChartDefinition getTelemetryChartDefinition() {
    return this.definition;
  }
  
  /**
   * Adds a sub-chart.
   * 
   * @param subChart The sub-chart to be added.
   */
  void addSubChart(SubChart subChart) {
    this.subCharts.add(subChart);
  }
  
  /**
   * Gets a read-only list of all sub-charts.
   * 
   * @return A read-only list containing <code>SubChart</code> objects.
   */
  public List<SubChart> getSubCharts() {
    return Collections.unmodifiableList(this.subCharts);
  }
  
  /**
   * A telemetry sub chart, consisting of a Y-Axis and a Telemetry Stream.
   * 
   * @author (Cedric) Qin ZHANG
   */
  public static class SubChart {

    private YAxis yAxis;
    private TelemetryStreamsObject streamsObject;
    
    /**
     * Constructs this instance.
     * @param streamsObject The telemetry streams object.
     * @param yAxis Y-axis.

     */
    SubChart(TelemetryStreamsObject streamsObject, YAxis yAxis) {
      this.streamsObject = streamsObject;
      this.yAxis = yAxis;
    }
    
    /**
     * Gets y-axis object.
     * 
     * @return Y-axis object.
     */
    public YAxis getYAxis() {
      return this.yAxis;
    }
    
    /**
     * Gets telemetry stream object.
     * 
     * @return The telemetry stream object.
     */
    public TelemetryStreamsObject getTelemetryStreamObject() {
      return this.streamsObject;
    }
  }
  
  /**
   * A Y-axis, consisting of a label, a boolean indicating if the Axis is an integer, and 
   * an optional lower bound and upper bound value. 
   * 
   * @author (Cedric) Qin ZHANG
   */
  public static class YAxis {
    private String label; 
    private boolean integerAxis;
    private Number lowerBound, upperBound;
    
    /**
     * Constructs this instance.
     * 
     * @param label Y-axis label.
     * @param integerAxis True if y-axis is integer axis.
     * @param lowerBound Y-axis lower bound, use null to enable y-axis autoscale.
     * @param upperBound Y-axis upper bound, use null to enable y-axis autoscale.
     */
    public YAxis(String label, boolean integerAxis, Number lowerBound, Number upperBound) {
      this.label = label;
      this.integerAxis = integerAxis;
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
    }
    
    /**
     * Gets y-axis label.
     * @return Y-axis label.
     */
    public String getLabel() {
      return this.label;
    }
    
    /**
     * Determines if y-axis is integer axis.
     * @return True if y-axis is integer axis.
     */
    public boolean isIntegerAxis() {
      return this.integerAxis;
    }
    
    /**
     * Determine if y-axis is auto-scaled.
     * @return True if it is auto-scaled.
     */
    public boolean isAutoScaledAxis() {
      return this.lowerBound == null || this.upperBound == null; 
    }
    
    /**
     * Gets lower bound in case y-axis is not auto-scaled.
     * @return The lower bound.
     */
    public Number getLowerBound() {
      return this.lowerBound; 
    }
    
    /**
     * Gets upper bound in case y-axis is not auto-scaled.
     * @return The upper bound.
     */
    public Number getUpperBound() {
      return this.upperBound;  
    }

    /**
     * Tests equality of two objects.
     * @param o another object to be compared with this object.
     * @return True if they are equal.
     */
    @Override
    public boolean equals(Object o) {
      if (o instanceof YAxis) {
        YAxis y = (YAxis) o;
        return this.label.equals(y.label) && this.integerAxis == y.integerAxis
            && this.lowerBound == y.lowerBound && this.upperBound == y.upperBound;
      }
      return false;
    }

    /**
     * Computes hash code.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
      return this.label.hashCode();
    }

  }
}

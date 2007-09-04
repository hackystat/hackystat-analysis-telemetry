package org.hackystat.telemetry.analyzer.configuration;


/**
 * Type safe enumeration of telemetry definition types. We are not using Java 5 enum because 
 * we still have to support Java 1.4.
 * <p>
 * V8 Notes: Convert to Java 5 enum.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TelemetryDefinitionType {

  /** Type streams. */
  public static TelemetryDefinitionType STREAMS = new TelemetryDefinitionType("Streams");
  
  /** Type chart. */
  public static TelemetryDefinitionType CHART = new TelemetryDefinitionType("Chart");
  
  /** Type report. */
  public static TelemetryDefinitionType REPORT = new TelemetryDefinitionType("Report");
  
  /** Type chart y-axis. */
  public static TelemetryDefinitionType YAXIS = new TelemetryDefinitionType("YAxis");
  
  /** Type name. The value is used by PersistentTelemetryDefinitionManager for xml persistence. */
  private String typeName;
  
  /**
   * Private constructor to enforce type safe enumeration pattern.
   * 
   * @param typeName The name of the type.
   */
  private TelemetryDefinitionType(String typeName) {
    this.typeName = typeName;  
  }
  
  /**
   * Gets the string representation.
   * 
   * @return The name of the type.
   */
  public String toString() {
    return this.typeName;
  }
  
  /**
   * Gets telemetry definition type.
   * 
   * @param typeName Type name.
   * @return The telemetry definition type.
   * @throws TelemetryConfigurationException If type name is unknown.
   */
  public static TelemetryDefinitionType valueOf(String typeName) 
      throws TelemetryConfigurationException {
    if (STREAMS.toString().equals(typeName)) {
      return STREAMS;
    }
    else if (CHART.toString().equals(typeName)) {
      return CHART;
    }
    else if (REPORT.toString().equals(typeName)) {
      return REPORT;
    }
    else if (YAXIS.toString().equals(typeName)) {
      return YAXIS;
    }
    else {
      throw new TelemetryConfigurationException("Unknown telemetry definition type " + typeName);
    }
  }
}

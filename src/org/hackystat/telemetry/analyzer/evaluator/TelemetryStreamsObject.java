package org.hackystat.telemetry.analyzer.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hackystat.telemetry.analyzer.language.ast.TelemetryStreamsDefinition;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;

/**
 * The evaluation result after resolving a telemetry streams definition. 
 *
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TelemetryStreamsObject {
  
  private TelemetryStreamsDefinition definition;
  private List<Stream> streams = new ArrayList<Stream>();
  
  /**
   * Constructs this instance.
   * 
   * @param definition The telemetry streams definition.
   */
  TelemetryStreamsObject(TelemetryStreamsDefinition definition) {
    this.definition = definition;
  }
  
  /**
   * Gets the telemetry streams definition.
   * 
   * @return The telemetry steams definiton. 
   */
  public TelemetryStreamsDefinition getTelemetryStreamsDefinition() {
    return this.definition;
  }
  
  /**
   * Adds a telemetry stream.
   * 
   * @param stream The telemetry stream to be added.
   */
  void addStream(Stream stream) {
    this.streams.add(stream);
  }
  
  /**
   * Gets a read-only list of all telemetry streams..
   * 
   * @return A read-only list containing <code>Stream</code> objects.
   */
  public List<Stream> getStreams() {
    return Collections.unmodifiableList(this.streams);
  }
  
  /**
   * A telemetry stream.
   * 
   * @author (Cedric) Qin ZHANG
   * @version $Id$
   */
  public static class Stream {
    
    private String name;    
    private TelemetryStream stream;

    /**
     * Constructs this instance.
     * 
     * @param name The name of the telemetry stream.
     * @param stream A <code>TelemetryStream</code> object.
     */
    Stream(String name, TelemetryStream stream) {
      this.name = name;
      this.stream = stream;
    }
    
    /**
     * Gets the name of the telemetry stream.
     * 
     * @return The name.
     */
    public String getName() {
      return this.name;
    }
    
    /**
     * Gets the <code>TelemetryStream</code> object.
     * 
     * @return The <code>TelemetryStream</code> object.
     */
    public TelemetryStream getTelemetryStream() {
      return this.stream;
    }
  }
}

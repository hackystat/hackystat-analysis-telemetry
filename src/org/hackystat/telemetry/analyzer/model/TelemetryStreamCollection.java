package org.hackystat.telemetry.analyzer.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.telemetry.analyzer.util.selector.interval.Interval;

/**
 * Provides a collection of telemetry streams. Note that this class does not constrain
 * what kind of telemetry streams are present in a collection. Typically, you want to add only
 * related streams, and each stream should contain points over the same interval.
 * <p>
 * Thread Safety: methods in this class are not synchronized.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TelemetryStreamCollection {

  private String name;

  private Project project;

  private Interval interval;

  // key is stream tag, value is telemetry stream
  // Must use HashMap, since key could be null. TreeMap throws null pointer exception.
  private Map<Object, TelemetryStream> streamMap = new HashMap<Object, TelemetryStream>();

  /**
   * Constructs this instance. The project and the interval are just tags, they
   * are not used to constrain the kind of streams that can be added. (Maybe I
   * should do this :=)).
   * 
   * @param name The name of this telemetry streams collection.
   * @param project The project.
   * @param interval The interval.
   */
  public TelemetryStreamCollection(String name, Project project, Interval interval) {
    this.name = name;
    this.project = project;
    this.interval = interval;
  }

  /**
   * Sets the name tag.
   * 
   * @param name The name tage.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the name tag.
   * 
   * @return The name tag.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the project tag.
   * 
   * @return The project tag.
   */
  public Project getProject() {
    return this.project;
  }

  /**
   * Gets the interval tag.
   * 
   * @return The interval tag.
   */
  public Interval getInterval() {
    return this.interval;
  }

  /**
   * Adds a telemetry stream to this collection.
   * 
   * @param stream The telemetry stream to be added.
   * @throws TelemetryDataModelException If there is already a stream with the same tag.
   */
  public void add(TelemetryStream stream) throws TelemetryDataModelException {
    if (this.streamMap.containsKey(stream.getTag())) {
      throw new TelemetryDataModelException("Duplicated stream tag detected.: " + stream.getTag());
    }
    this.streamMap.put(stream.getTag(), stream);
  }

  /**
   * Gets telemetry stream by tag value.
   * 
   * @param tag Telemetry stream tag. Null is a valid value.
   * @return TelemetryStream The telemetry stream, or null if it does not exist.
   */
  public TelemetryStream get(Object tag) {
    return (TelemetryStream) this.streamMap.get(tag);
  }

  /**
   * Gets all the telemetry streams contained in this collection.
   * 
   * @return A collection of <code>TelemetryStream</code> object.
   */
  public Collection<TelemetryStream> getTelemetryStreams() {
    return this.streamMap.values();
  }
}

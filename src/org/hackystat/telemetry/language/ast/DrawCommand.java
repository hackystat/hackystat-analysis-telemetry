package org.hackystat.telemetry.language.ast;

/**
 * Draw command to render telemetry charts or reports.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class DrawCommand extends TelemetryDefinition {

  private String telemetryDefinitionName;
  private Constant[] parameters;
  
  /**
   * Constructs this instance.
   * 
   * @param telemetryDefinitionName The name of the telemetry object definition.
   * @param parameters An array of <code>Constant</code> objects that need to pass to the 
   *        telemetry definition to render it. Null is a valid if there is no parameter needed.
   * @param textPosition The text position of the definition string in the input.
   */
  public DrawCommand(String telemetryDefinitionName, Constant[] parameters, 
      TextPosition textPosition) {
    super(null, textPosition);
    this.telemetryDefinitionName = telemetryDefinitionName;
    this.parameters = parameters == null ? new Constant[0] : parameters;
  }
  
  /**
   * Gets the name of the telemetry object definition.
   * 
   * @return The name.
   */
  public String getTelemetryDefinitionName() {
    return this.telemetryDefinitionName;
  }
  
  /**
   * Gets the parameters.
   * 
   * @return The parameters that need to pass to the referenced telemetry object. 
   */
  public Constant[] getParameters() {
    return this.parameters;
  }
}

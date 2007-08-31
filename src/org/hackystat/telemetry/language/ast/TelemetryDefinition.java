package org.hackystat.telemetry.language.ast;

/**
 * Telemetry definition base class.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public abstract class TelemetryDefinition {

  private String name;
  private TextPosition textPosition;
  private String definitionString;
  
  /**
   * Constucts this instance.
   * 
   * @param name The name of this definition.
   * @param textPosition The text position of the definition string in the input.
   */
  protected TelemetryDefinition(String name, TextPosition textPosition) { 
    this.name = name;
    this.textPosition = textPosition;
  }

  /**
   * Gets the name of this telemetry "streams" object.
   * 
   * @return The name.
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * Gets the text position of the definition string in the input.
   * 
   * @return The text position of the definition string in the input.
   */
  public TextPosition getTextPosition() {
    return this.textPosition;
  }

  /**
   * Gets the definition string.
   * 
   * @return The definition string, or null if the definition string is not available.
   */
  public String getDefinitionString() {
    return this.definitionString;
  }

  /**
   * Sets the definition string.
   * 
   * @param definitionString The description.
   */
  public void setDefinitionString(String definitionString) {
    this.definitionString = definitionString;
  }
}
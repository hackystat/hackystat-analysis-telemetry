package org.hackystat.telemetry.language.ast;

/**
 * A constant in telemetry language.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public abstract class Constant implements Expression {
  
  /**
   * Gets the value of the constant as a string.
   * 
   * @return The string representation of the constant value.
   */
  public abstract String getValueString();
}

package org.hackystat.telemetry.analyzer.language.ast;

/**
 * Number constant in telemetry language.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class NumberConstant extends Constant {
  
  /** The number value. */
  private Number value;
  
  /**
   * Constructs this instance with supplied number value.
   * 
   * @param value The number value.
   */
  public NumberConstant(Number value) {
    if (value == null) {
      throw new NullPointerException("Value cannot be null in NumberConstant.");
    }
    this.value = value;
  }
  
  /**
   * Gets the value of this number.
   * 
   * @return The value.
   */
  public Number getValue() {
    return this.value;
  }
  
  /**
   * Gets the string representation of this number.
   * 
   * @return The string representation of this number.
   */
  @Override
  public String getValueString() {
    return this.value.toString();
  }

  /**
   * Tests for equality of two instances.
   * 
   * @param another The other object to be tested with this object.
   * 
   * @return True if two instances are equal.
   */
  @Override
  public boolean equals(Object another) {
    return (another instanceof NumberConstant)
        && (this.value.equals(((NumberConstant) another).value));
  }

  /**
   * Gets the hash code for this instance.
   * 
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    return this.value.hashCode();
  }
}

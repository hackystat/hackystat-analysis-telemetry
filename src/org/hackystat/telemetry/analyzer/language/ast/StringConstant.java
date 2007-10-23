package org.hackystat.telemetry.analyzer.language.ast;

/**
 * String constant in telemetry language.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class StringConstant extends Constant {

  /** The string value. */
  private String value;
  
  /**
   * Constructs this instance.
   * 
   * @param value The string value of this constant.
   */
  public StringConstant(String value) {
    if (value == null) {
      throw new NullPointerException("Value cannot be null in StringConstant.");
    }
    this.value = value;  
  }
  
  /**
   * Gets the string this constant represents.
   * 
   * @return The string value.
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Gets the string this constant represents. The return value is the same as getValue() method.
   * 
   * @return The string value.
   */
  @Override
  public String getValueString() {
    return this.value;
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
    return (another instanceof StringConstant)
        && (this.value.equals(((StringConstant) another).value));
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

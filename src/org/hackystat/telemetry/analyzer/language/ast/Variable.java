package org.hackystat.telemetry.analyzer.language.ast;

/**
 * A constant in telemetry language. Since telemetry language is dynamically typed language 
 * (type information can only be determined at run time), there is no type information 
 * associated with variable.
 * 
 * @author (Cedric) Qin ZHANG
 * @version $Id$
 */
public class Variable implements Expression {

  /** Variable name. */
  private String name;
  
  /**
   * Constructs this instance.
   * 
   * @param name The name of this variable.
   */
  public Variable(String name) {
    if (name == null) {
      throw new NullPointerException("Name cannot be null in Variable.");
    }
    this.name = name;
  }
  
  /**
   * Gets the name of this variable.
   * 
   * @return The variable name.
   */
  public String getName() {
    return this.name;
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
    return (another instanceof Variable)
        && (this.name.equals(((Variable) another).name));
  }

  /**
   * Gets the hash code for this instance.
   * 
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}

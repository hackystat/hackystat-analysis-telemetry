package org.hackystat.telemetry.analyzer.language.ast;

import org.hackystat.telemetry.analyzer.language.TelemetryLanguageException;

/**
 * Y-axis information in telemetry chart definition.
 * 
 * @author (Cedric) Qin Zhang
 */
public class TelemetryChartYAxisDefinition extends TelemetryDefinition {

  /** Chart y-axis should have integer numbers. */
  public static final String NUMBER_TYPE_INTEGER = "integer";
  /** Chart y-axis should have double number. */
  public static final String NUMBER_TYPE_DOUBLE = "double";
  /** Chart y-axis have auto-determined number type, either integer or double. */
  public static final String NUMBER_TYPE_AUTO = "auto";
  
  private Variable[] variables; //List<Variable>
  
  private Expression labelParameter; //Variable or StringConstant
  private String numberType;
  private boolean autoScaled;
  private Number lowerBound, upperBound;
    
  /**
   * Constructs the y-axis definition. Y-axis can be either auto-scaled or fixed-scaled,
   * depending on whether lower bound and upper bounds are supplied or not.
   * 
   * @param name The name of this y-axis definition.
   * @param variables The variables used in the definition. Variables are essentially holding
   *        places so that real value can be swapped in later.
   *        Null is valid if there is no variable used in this definition.
   * @param labelParameter Either a <code>Variable</code> or a <code>StringConstant</code> for
   *        y-axis label.
   * @param numberType The axis number type. Use one of the constants in this class.
   * @param lowerBound Y-axis lower bound. Null is valid i
   * @param upperBound Y-axis upper bound.
   * @param textPosition The text position of the definition string in the input.
   * 
   * @throws TelemetryLanguageException If y-axis number type is unsupported, 
   *         or supplied lower and upper bounds are invalid.
   */
  public TelemetryChartYAxisDefinition(String name, Variable[] variables, Expression labelParameter,
      String numberType, Number lowerBound, Number upperBound, TextPosition textPosition) 
      throws TelemetryLanguageException {
    super(name, textPosition);
    this.variables = variables == null ? new Variable[0] : variables;
    
    //check label parameter
    if (labelParameter instanceof Variable || labelParameter instanceof StringConstant) {
      this.labelParameter = labelParameter;
    }
    else {
      throw new TelemetryLanguageException("labelParameter must be of type either Variable " +
          "or StringConstant.");
    }
    
    //check number type
    if (NUMBER_TYPE_INTEGER.equals(numberType) || NUMBER_TYPE_DOUBLE.equals(numberType)
        || NUMBER_TYPE_AUTO.equals(numberType)) {
      this.numberType = numberType;
    }
    else {
      throw new TelemetryLanguageException("Y-Axis number type must be one of integer|double|auto");
    }

    //check bounds
    if (lowerBound == null && upperBound == null) {
      this.autoScaled = true;
      this.lowerBound = null;
      this.upperBound = null;
    }
    else if (lowerBound != null && upperBound != null) {
      if (NUMBER_TYPE_INTEGER.equals(numberType) && 
          ! (lowerBound instanceof Integer && upperBound instanceof Integer)) {
        throw new TelemetryLanguageException("Upper and lower bound for integer y-axis must be" +
            "integers.");
      }
      if (lowerBound.floatValue() >= upperBound.floatValue()) {
        throw new TelemetryLanguageException(
            "Y-Axis lower bound must be smaller than its upper bound.");
      }
      this.autoScaled = false;
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
    }
    else {
      throw new TelemetryLanguageException("You must specify both lower and upper bounds, " +
          "or none of them.");
    }
  }
  
  /**
   * Gets an array of variables used in the definition.
   * 
   * @return An array of <code>Varaible</code> objects. If there is no variable used,
   *         then an empty array is returned.
   */
  public Variable[] getVariables() {
    return this.variables;
  }
  
  /**
   * Gets the y-axis label.
   * 
   * @return Either a <code>Variable</code> or a <code>StringConstant</code> for y-axis label.
   */
  public Expression getLabelParameter() {
    return this.labelParameter;
  }
  
  /**
   * Gets the y-axis number type. The return value is one of the number type constant defined
   * in this class.
   * 
   * @return Y-axis number type.
   */
  public String getNumberType() {
    return this.numberType;
  }
  
  /**
   * Determines whether the y-axis is auto-scales.
   * 
   * @return True if y-axis is auto-scaled.
   */
  public boolean isAutoScale() {
    return this.autoScaled;
  }
  
  /**
   * Gets the lower bound of the axis.
   * 
   * @return The lower bound of the axis, or null if the axis is auto-scaled.
   */
  public Number getLowerBound() {
    return this.lowerBound;
  }
  
  /**
   * Gets the upper bound of the axis.
   * 
   * @return The upper bound of the axis, or null if the axis is auto-scaled.
   */
  public Number getUpperBound() {
    return this.upperBound;
  }
}

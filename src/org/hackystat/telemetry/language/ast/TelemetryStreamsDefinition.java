package org.hackystat.telemetry.language.ast;

import org.hackystat.telemetry.language.TelemetryLanguageException;


/**
 * Definition for telemetry "streams" object. A streams object consists of one or more telemetry
 * streams.
 * 
 * @author (Cedric) Qin Zhang
 * @version $Id$
 */
public class TelemetryStreamsDefinition extends TelemetryDefinition {

  private String description;
  private Expression expression;
  private Variable[] variables;
  
  /**
   * Constucts this instance.
   * 
   * @param name The name of this definition.
   * @param expression The expression that defines this telemetry "streams" object.
   * @param variables The variables used in the expression. Variables are essentially holding
   *        places so that real value can be swapped in when the expression is evaluated.
   *        Null is valid if there is no variable used in this definition.
   * @param textPosition The text position of the definition string in the input.
   * 
   * @throws TelemetryLanguageException If the variable array contains duplicated variable
   *         declaration or does not declare all variables needed by the expression.
   */
  public TelemetryStreamsDefinition(String name, Expression expression,
      Variable[] variables, TextPosition textPosition) throws TelemetryLanguageException {
    
    super(name, textPosition);
    this.expression = expression;
    this.variables = variables == null ? new Variable[0] : variables;

    // check whether template list contains duplicated declaration
    //HashSet set = new HashSet(); // don't use tree set, see TemplatedParameter
    // impl.
//    for (Iterator iter = this.templatedParamters.iterator(); iter.hasNext();) {
//      TemplatedParameter param = (TemplatedParameter) iter.next();
//      if (set.contains(param)) {
//        throw new Exception("Duplicated parameter declaration.");
//      }
//      else {
//        set.add(param);
//      }
//    }

    // check whether template list contains all templates required by expression
    //TODO: reimplement
//    int size = expression.size();
//    for (int i = 0; i < size; i++) {
//      ExpressionElement element = expression.getElement(i);
//      if (element instanceof VariableOperand) {
//        VariableOperand variable = (VariableOperand) element;
//        for (Iterator iter = variable.getParameters().iterator(); iter.hasNext();) {
//          Parameter param = (Parameter) iter.next();
//          if (param instanceof TemplatedParameter) {
//            if (!set.contains(param)) {
//              throw new Exception("Undeclared parameter '"
//                  + ((TemplatedParameter) param).getTemplateName() + "' used.");
//            }
//          }
//        }
//      }
//    }
  }
  
  /**
   * Gets the expression that defines this telemetry "streams" object.
   * 
   * @return The expression.
   */
  public Expression getExpression() {
    return this.expression;
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
   * Gets the description of this telemetry "streams" object.
   * 
   * @return The description.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Sets the description.
   * 
   * @param description The description.
   */
  public void setDescription(String description) {
    this.description = description;
  }
}
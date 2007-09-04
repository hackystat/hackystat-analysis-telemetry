package org.hackystat.telemetry.analyzer.evaluator;

import java.util.TreeMap;

import org.hackystat.telemetry.analyzer.language.ast.Constant;
import org.hackystat.telemetry.analyzer.language.ast.Variable;

/**
 * Variable resolver. It maps <code>Variable</code>s to <code>Constant</code>s.
 * 
 * @author (Cedric) Qin ZHANG
 */
public class VariableResolver {

  /** Key is variable name, value is instance of Constant. */
  private TreeMap<String, Constant> map = new TreeMap<String, Constant>();
  
  /**
   * Adds a variable-constant pair so that later the variable can be resolved.
   * 
   * @param variable The variable.
   * @param constant The constant.
   * @throws TelemetryEvaluationException If a variable with the same name has already been added.
   */
  public void add(Variable variable, Constant constant) throws TelemetryEvaluationException {
    String varName = variable.getName();
    if (this.map.containsKey(varName)) {
      throw new TelemetryEvaluationException("Variable " + varName + " already added.");
    }
    this.map.put(varName, constant);
  }
  
  /**
   * Resolves a variable to a constant.
   * 
   * @param variable The variable to be resolved.
   * @return The constant the variable resolves to.
   * @throws TelemetryEvaluationException If the variable cannot be resolved to a constant.
   */
  public Constant resolve(Variable variable) throws TelemetryEvaluationException {
    String varName = variable.getName();
    if (! this.map.containsKey(varName)) {
      throw new TelemetryEvaluationException("Variable " + varName + " not found.");
    }
    return (Constant) this.map.get(varName);
  }
}

package org.hackystat.telemetry.analyzer.evaluator;

import java.util.Iterator;

import org.hackystat.telemetry.analyzer.function.TelemetryFunctionManager;
import org.hackystat.telemetry.analyzer.language.ast.ChartReference;
import org.hackystat.telemetry.analyzer.language.ast.Constant;
import org.hackystat.telemetry.analyzer.language.ast.Expression;
import org.hackystat.telemetry.analyzer.language.ast.FunctionCall;
import org.hackystat.telemetry.analyzer.language.ast.NumberConstant;
import org.hackystat.telemetry.analyzer.language.ast.ReducerCall;
import org.hackystat.telemetry.analyzer.language.ast.StreamsReference;
import org.hackystat.telemetry.analyzer.language.ast.StringConstant;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartYAxisDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryReportDefinition;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryStreamsDefinition;
import org.hackystat.telemetry.analyzer.language.ast.Variable;
import org.hackystat.telemetry.analyzer.language.ast.YAxisReference;
import org.hackystat.telemetry.analyzer.model.TelemetryStream;
import org.hackystat.telemetry.analyzer.model.TelemetryStreamCollection;
import org.hackystat.telemetry.analyzer.reducer.TelemetryReducerManager;
import org.hackystat.telemetry.analyzer.util.project.Project;
import org.hackystat.telemetry.analyzer.util.selector.interval.Interval;

/**
 * Provides an evaluation function for Telemetry, in which a definition is evaluated with 
 * respect to a set of variables and their values, a Project, and an Interval. 
 * 
 * @author (Cedric) Qin ZHANG
 */
public class TelemetryEvaluator {

  /**
   * Private constructor to prevent this class from being instantiated.
   */
  private TelemetryEvaluator() {
  }
  
  /**
   * Evaluates a telemetry streams definition to produce a 
   * <code>TelemetryStreamsObject</code> object.
   * 
   * @param streamsDefinition The telemetry streams definition.
   * @param variableResolver The variable resolver.
   * @param project The project.
   * @param interval The interval.
   * 
   * @return An instance of a <code>TelemetryStreamsObject</code> object.  
   * 
   * @throws TelemetryEvaluationException If there is any error during the evalutation process.
   */
  public static TelemetryStreamsObject evaluate(TelemetryStreamsDefinition streamsDefinition, 
      VariableResolver variableResolver, Project project, Interval interval)
      throws TelemetryEvaluationException {
    
    Object result = resolveExpression(streamsDefinition.getExpression(), 
                                      variableResolver, project, interval);
    if (! (result instanceof TelemetryStreamCollection)) {
      throw new TelemetryEvaluationException("Telemetry streams " + streamsDefinition.getName()
          + " does not evaluation to TelemetryStreamCollection. "
          + "Is there any reducer in the expression?");
    }
    
    // telemetry_streams_definition_name[<parameter, ..., parameter>][:individual_stream_tag].
    StringBuffer categorySeriesNamePrefix = new StringBuffer(streamsDefinition.getName());
    Variable[] variables = streamsDefinition.getVariables();
    if (variables.length > 0) {
      categorySeriesNamePrefix.append('<');
      categorySeriesNamePrefix.append(variableResolver.resolve(variables[0]).getValueString());
      for (int i = 1; i < variables.length; i++) {
        categorySeriesNamePrefix.append(", ");
        categorySeriesNamePrefix.append(variableResolver.resolve(variables[i]).getValueString());
      }
      categorySeriesNamePrefix.append('>');
    }
    
    TelemetryStreamsObject telemetryStreamsObject = new TelemetryStreamsObject(streamsDefinition);
    for (Iterator i = ((TelemetryStreamCollection) result).getTelemetryStreams().iterator(); 
         i.hasNext();) {
      TelemetryStream stream = (TelemetryStream) i.next();
      Object streamTag = stream.getTag();
      String streamName = streamTag == null ? categorySeriesNamePrefix.toString()
          : categorySeriesNamePrefix.toString() + ':' + streamTag.toString();
      telemetryStreamsObject.addStream(new TelemetryStreamsObject.Stream(streamName, stream));
    }
    return telemetryStreamsObject;
  }

  /**
   * Evaluates a telemetry chart definition to <code>TeemetryChartObject</code> object.
   * 
   * @param chartDefinition The telemetry chart definition.
   * @param telemetryDefinitionResolver The telemetry definition resolver.
   * @param variableResolver The variable resolver.
   * @param project The project.
   * @param interval The interval.
   * 
   * @return An instance of <code>TelemetryChartObject</code> object.  
   * 
   * @throws TelemetryEvaluationException If there is any error during the evalutation process.
   */
  public static TelemetryChartObject evaluate(TelemetryChartDefinition chartDefinition, 
      TelemetryDefinitionResolver telemetryDefinitionResolver, VariableResolver variableResolver, 
      Project project, Interval interval) throws TelemetryEvaluationException {
    
    TelemetryChartObject telemetryChartObject = new TelemetryChartObject(chartDefinition);
    
    for (Iterator i = chartDefinition.getSubCharts().iterator(); i.hasNext(); ) {
      TelemetryChartDefinition.SubChartDefinition subChart 
          = (TelemetryChartDefinition.SubChartDefinition) i.next();
      
      //find the TelmetryStreamsDefinition object
      StreamsReference streamsRef = subChart.getStreamsReference();
      TelemetryStreamsDefinition streamsDef 
          = telemetryDefinitionResolver.resolveStreamsDefinition(streamsRef.getName());     
      //prepares a variable resolver for the TelemetryStreamsDefinition object
      Expression[] streamsRefParameters = streamsRef.getParameters();
      Variable[] streamsVariables = streamsDef.getVariables();
      if (streamsVariables.length != streamsRefParameters.length) {
        throw new TelemetryEvaluationException("Error in chart definition detected. The streams '"
          + streamsDef.getName() + "' the chart relies on has " + streamsVariables.length
          + " parameter(s), but the chart has only supplied " + streamsRefParameters.length 
          + " parameter value(s).");
      }
      VariableResolver streamsVariableResolver = new VariableResolver();
      for (int j = 0; j < streamsVariables.length; j++) {
        Expression streamsRefParameter = streamsRefParameters[j];
        if (streamsRefParameter instanceof Constant) {
          streamsVariableResolver.add(streamsVariables[j], (Constant) streamsRefParameter);
        }
        else if (streamsRefParameter instanceof Variable) {
          Constant constant = variableResolver.resolve((Variable) streamsRefParameter);
          streamsVariableResolver.add(streamsVariables[j], constant);          
        }
        else {
          throw new RuntimeException("Unknow parameter type.");          
        }
      }
      //get TelemetryStreamsObject
      TelemetryStreamsObject streamsObject 
          = TelemetryEvaluator.evaluate(streamsDef, streamsVariableResolver, project, interval);
    
      
      //find the YAxisDefinition object
      YAxisReference yAxisRef = subChart.getYAxisReference();
      TelemetryChartYAxisDefinition yAxisDef
          = telemetryDefinitionResolver.resolveYAxisDefinition(yAxisRef.getName());
      //prepares a variable resolver for the YAxisDefinition object
      Expression[] yAxisRefParameters = yAxisRef.getParameters();
      Variable[] yAxisVariables = yAxisDef.getVariables();
      if (yAxisVariables.length != yAxisRefParameters.length) {
        throw new TelemetryEvaluationException("Error in chart definition detected. The y-axis '"
          + yAxisDef.getName() + "' the chart relies on has " + yAxisVariables.length
          + " parameter(s), but the chart has only supplied " + yAxisRefParameters.length 
          + " parameter value(s).");
      }
      VariableResolver yAxisVariableResolver = new VariableResolver();
      for (int j = 0; j < yAxisVariables.length; j++) {
        Expression yAxisRefParameter = yAxisRefParameters[j];
        if (yAxisRefParameter instanceof Constant) {
          yAxisVariableResolver.add(yAxisVariables[j], (Constant) yAxisRefParameter);
        }
        else if (yAxisRefParameter instanceof Variable) {
          Constant constant = variableResolver.resolve((Variable) yAxisRefParameter);
          yAxisVariableResolver.add(yAxisVariables[j], constant);          
        }
        else {
          throw new RuntimeException("Unsupported parameter type in y-axis " + yAxisDef.getName());
        }
      }
      //get y-axis label value
      String yAxisLabelValue = null;
      Expression yAxisDefLabelParam = yAxisDef.getLabelParameter();
      if (yAxisDefLabelParam instanceof StringConstant) {
        yAxisLabelValue = ((StringConstant) yAxisDefLabelParam).getValue();
      }
      else if (yAxisDefLabelParam instanceof Variable) {
        Constant constant = yAxisVariableResolver.resolve((Variable) yAxisDefLabelParam);
        if (constant instanceof StringConstant) {
          yAxisLabelValue = ((StringConstant) constant).getValue();
        }
        else {
          throw new TelemetryEvaluationException("Y-axis '" + yAxisDef.getName()
              + "' variable '" + ((Variable) yAxisDefLabelParam).getName() 
              + "' does not resolve to a string.");
        }
      }
      else {
        throw new RuntimeException("Unsupported parameter type in y-axis " + yAxisDef.getName());
      }

      //add the sub-chart
      boolean isYAxisInteger = TelemetryChartYAxisDefinition.NUMBER_TYPE_INTEGER.equals(
          yAxisDef.getNumberType());
      Number lowerBound = null;
      Number upperBound = null;
      if (! yAxisDef.isAutoScale()) {
        lowerBound = yAxisDef.getLowerBound();
        upperBound = yAxisDef.getUpperBound();
      }
      TelemetryChartObject.YAxis yAxisObject = new TelemetryChartObject.YAxis(yAxisLabelValue,
          isYAxisInteger, lowerBound, upperBound);
      telemetryChartObject.addSubChart(new TelemetryChartObject.SubChart(
          streamsObject, yAxisObject));
    }
    
    return telemetryChartObject;
  }
  
  /**
   * Evaluates a telemetry report definition to <code>TelemetryReportObject</code> object.
   * 
   * @param reportDefinition The telemetry report definition.
   * @param telemetryDefinitionResolver The telemetry definition resolver.
   * @param variableResolver The variable resolver.
   * @param project The project.
   * @param interval The interval.
   * 
   * @return An instance of <code>TelemetryReportObject</code> object.  
   * 
   * @throws TelemetryEvaluationException If there is any error during the evalutation process.
   */
  public static TelemetryReportObject evaluate(TelemetryReportDefinition reportDefinition, 
      TelemetryDefinitionResolver telemetryDefinitionResolver, VariableResolver variableResolver, 
      Project project, Interval interval) throws TelemetryEvaluationException {
    
    TelemetryReportObject telemetryReportObject = new TelemetryReportObject(reportDefinition);
    for (Iterator i = reportDefinition.getChartReferences().iterator(); i.hasNext(); ) {
      //find the TelmetryChartDefinition object
      ChartReference chartRef = (ChartReference) i.next();
      TelemetryChartDefinition chartDef 
          = telemetryDefinitionResolver.resolveChartDefinition(chartRef.getName());
      
      //prepares a variable resolver for the TelemetryChartDefinition object
      Expression[] chartRefParameters = chartRef.getParameters();
      Variable[] chartVariables = chartDef.getVariables();
      if (chartVariables.length != chartRefParameters.length) {
        throw new TelemetryEvaluationException("Error in report definition detected. The chart '"
          + chartDef.getName() + "' the chart relies on has " + chartVariables.length
          + " parameter(s), but the chart has only supplied " + chartRefParameters.length 
          + " parameter value(s).");
      }
      VariableResolver chartVariableResolver = new VariableResolver();
      for (int j = 0; j < chartVariables.length; j++) {
        Expression chartRefParameter = chartRefParameters[j];
        if (chartRefParameter instanceof Constant) {
          chartVariableResolver.add(chartVariables[j], (Constant) chartRefParameter);
        }
        else if (chartRefParameter instanceof Variable) {
          Constant constant = variableResolver.resolve((Variable) chartRefParameter);
          chartVariableResolver.add(chartVariables[j], constant);          
        }
        else {
          throw new RuntimeException("Unknow parameter type.");          
        }
      }
    
      //generate telemetry chart object
      TelemetryChartObject chartObject = TelemetryEvaluator.evaluate(chartDef, 
          telemetryDefinitionResolver, chartVariableResolver, project, interval);  
      telemetryReportObject.addChartObject(chartObject);
    }
    
    return telemetryReportObject;
  }
  
  /**
   * Resolves an expression to an instance of <code>TelemetryStreamCollection</code>
   * or <code>Number</code>.
   * 
   * @param expression The telemetry expression.
   * @param variableResolver The variable resolver.
   * @param project The project.
   * @param interval The interval.
   * 
   * @return The resulting instance of type either <code>TelemetryStreamCollection</code>
   *         or <code>Number</code>.
   * 
   * @throws TelemetryEvaluationException If the expression call cannot be resolved.
   */
  static Object resolveExpression(Expression expression, VariableResolver variableResolver,
      Project project, Interval interval) throws TelemetryEvaluationException {
    try {
      FunctionCall idempotent = new FunctionCall("Idempotent", new Expression[]{expression});
      return resolveFunctionCall(idempotent, variableResolver, project, interval);
    }
    catch (Exception ex) {
      throw new TelemetryEvaluationException(ex);
    }
  }
 
  /**
   * Resolves a function call to an instance of <code>TelemetryStreamCollection</code>
   * or <code>Number</code>.
   * 
   * @param functionCall The <code>FunctionCall</code> instance.
   * @param variableResolver The variable resolver.
   * @param project The project.
   * @param interval The interval.
   * 
   * @return The result. It's an instance of type either <code>TelemetryStreamCollection</code>
   *         or <code>Number</code>.
   * 
   * @throws Exception If the function call cannot be resolved.
   */
  private static Object resolveFunctionCall(FunctionCall functionCall, 
      VariableResolver variableResolver, Project project, Interval interval) throws Exception {
    
    Expression[] parameters = functionCall.getParameters();
    
    //Only 3 types of objects are valid: String, Number, TelemetryStreamCollection.
    Object[] parameterValues = new Object[parameters.length];
    
    for (int i = 0; i < parameters.length; i++) {
      Expression param = parameters[i];
      //resolve variable first
      if (param instanceof Variable) {
        param = variableResolver.resolve((Variable) param);
      }
      //fill parameterValues next
      if (param instanceof ReducerCall) {
        parameterValues[i] 
            = resolveReducerCall((ReducerCall) param, variableResolver, project, interval);
      }
      else if (param instanceof FunctionCall) {
        parameterValues[i] 
            = resolveFunctionCall((FunctionCall) param, variableResolver, project, interval);
      }
      else if (param instanceof NumberConstant) {
        parameterValues[i] = ((NumberConstant) param).getValue();
      }
      else if (param instanceof StringConstant) {
        parameterValues[i] = ((StringConstant) param).getValue();
      }
      else {
        throw new TelemetryEvaluationException("Function " + functionCall.getFunctionName() 
            + " does not accept parameter of type " + param.getClass().getName());
      }  
    }  
    return TelemetryFunctionManager.getInstance().compute(functionCall.getFunctionName(),
        parameterValues);
  }
  
  
  /**
   * Resolves a reducer call to an instance of <code>TelemetryStreamCollection</code>.
   * 
   * @param reducerCall The <code>ReducerCall</code> instance.
   * @param variableResolver The variable resolver.
   * @param project The project.
   * @param interval The interval.
   * 
   * @return The resulting instance of <code>TelemetryStreamCollection</code>.
   * 
   * @throws Exception If the reducer call cannot be resolved.
   */
  private static TelemetryStreamCollection resolveReducerCall(ReducerCall reducerCall, 
      VariableResolver variableResolver, Project project, Interval interval) throws Exception {
    
    Expression[] parameters = reducerCall.getParameters();
    
    //Only objects of type String is valid.
    String[] parameterValues = new String[parameters.length];
    
    for (int i = 0; i < parameters.length; i++) {
      Expression param = parameters[i];
      //resolve variable first
      if (param instanceof Variable) {
        param = variableResolver.resolve((Variable) param); 
      }
      //fill parameterValues next
      if (param instanceof Constant) {
        parameterValues[i] = ((Constant) param).getValueString();
      }
      else {
        throw new TelemetryEvaluationException("Reducer " + reducerCall.getReducerName() 
            + " does not accept parameter of type " + param.getClass().getName());
      }
    }
    return TelemetryReducerManager.getInstance().compute(reducerCall.getReducerName(),
        project, interval, parameterValues);
  }
}

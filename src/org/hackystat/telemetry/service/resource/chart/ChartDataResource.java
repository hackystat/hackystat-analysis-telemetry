package org.hackystat.telemetry.service.resource.chart;

import java.io.StringWriter;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.projects.ProjectUtils;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.telemetry.analyzer.configuration.TelemetryChartDefinitionInfo;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionManager;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionManagerFactory;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionType;
import org.hackystat.telemetry.analyzer.evaluator.TelemetryChartObject;
import org.hackystat.telemetry.analyzer.evaluator.TelemetryDefinitionResolver;
import org.hackystat.telemetry.analyzer.evaluator.TelemetryEvaluator;
import org.hackystat.telemetry.analyzer.evaluator.TelemetryStreamsObject;
import org.hackystat.telemetry.analyzer.evaluator.VariableResolver;
import org.hackystat.telemetry.analyzer.evaluator.TelemetryChartObject.SubChart;
import org.hackystat.telemetry.analyzer.evaluator.TelemetryStreamsObject.Stream;
import org.hackystat.telemetry.analyzer.language.ast.StringConstant;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartDefinition;
import org.hackystat.telemetry.analyzer.language.ast.Variable;
import org.hackystat.telemetry.analyzer.model.TelemetryDataPoint;
import org.hackystat.telemetry.service.resource.chart.jaxb.Parameter;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartData;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryPoint;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream;
import org.hackystat.telemetry.service.resource.chart.jaxb.YAxis;
import org.hackystat.telemetry.service.resource.telemetry.TelemetryResource;
import org.hackystat.utilities.time.interval.DayInterval;
import org.hackystat.utilities.time.interval.Interval;
import org.hackystat.utilities.time.interval.MonthInterval;
import org.hackystat.utilities.time.interval.WeekInterval;
import org.hackystat.utilities.tstamp.Tstamp;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;

/**
 * Processes GET {host}/chart/{chart}/{email}/{project}/{granularity}/{start}/{end} and
 * GET {host}/chart/{chart}/{email}/{project}/{granularity}/{start}/{end}?params={params} requests.
 * Requires the authenticated user to be {email} or else the Admin user for the sensorbase connected
 * to this service.
 * 
 * @author Philip Johnson
 */
public class ChartDataResource extends TelemetryResource {
  
  private XMLGregorianCalendar startDay; 
  private XMLGregorianCalendar endDay;

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public ChartDataResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Returns an TelemetryChart instance representing the trend information associated with the
   * Project data, or null if not authorized.
   * 
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation represent(Variant variant) {
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        
        // [1] Authenticate to DPD and SensorBase services; return with error if failure.
        DailyProjectDataClient dpdClient = this.getDailyProjectDataClient();
        SensorBaseClient sensorBaseClient = this.getSensorBaseClient();
        try {
          dpdClient.authenticate();
          sensorBaseClient.authenticate();
        }
        catch (Exception e) {
          setStatusError("Cannot authenticate this user", e);
          return null;
        }
        
        // [2] Get the User representation, return with error if not defined. 
        User user;
        try {
          user = sensorBaseClient.getUser(this.uriUser);
        }
        catch (Exception e) {
          setStatusError("Undefined user: " + this.uriUser, e);
          return null;
        }
        
        // [3] get the Project representation; return with error if not defined.
        Project project;
        try {
          project = sensorBaseClient.getProject(this.uriUser, this.projectName);
        }
        catch (Exception e) {
          setStatusError(String.format("Undefined project %s for owner %s", 
              this.projectName, uriUser), e);
          return null;
        }
        
        // [4] Get the chart representation, return with error if chart not defined.
        TelemetryChartDefinition chartDef;
        try {
          TelemetryDefinitionManager manager = 
            TelemetryDefinitionManagerFactory.getGlobalPersistentInstance();
          TelemetryChartDefinitionInfo chartDefInfo = (TelemetryChartDefinitionInfo) manager.get(
              user, this.chart, true, TelemetryDefinitionType.CHART);
          chartDef = chartDefInfo.getChartDefinitionObject();
        }
        catch (Exception e) {
          setStatusError("Undefined chart " + this.chart, e);
          return null;
        }
        
        // [5] Get the start and end days, return with error if cannot be parsed.
        try {
          this.startDay = Tstamp.makeTimestamp(this.start);
        }
        catch (Exception e) {
          setStatusError("Bad start day: " + this.start, e);
          return null;
        }
        try {
          this.endDay = Tstamp.makeTimestamp(this.end);
        }
        catch (Exception e) {
          setStatusError("Bad end day: " + this.end, e);
          return null;
        }
        
        // [5.5] Make sure start and end days are OK w.r.t. project times.
        if (!ProjectUtils.isValidStartTime(project, this.startDay)) {
          String msg = this.startDay + " is before Project start day: " + project.getStartTime();
          setStatusError(msg);
          return null;
        }
        if (!ProjectUtils.isValidEndTime(project, this.endDay)) {
          String msg = this.endDay + " is after Project end day: " + project.getEndTime();
          setStatusError(msg);
          return null;
        }
        if (Tstamp.lessThan(this.endDay, this.startDay)) {
          String msg = this.startDay + " must be greater than: " + this.endDay;
          setStatusError(msg);
          return null;
        }
        
        // [5.6] Telemetry end date cannot be after tomorrow. 
        XMLGregorianCalendar tomorrow = Tstamp.incrementDays(Tstamp.makeTimestamp(), 1);
        if (Tstamp.greaterThan(this.endDay, tomorrow)) {
          String msg = this.endDay + " cannot be in the future. Change to today at the latest.";
          setStatusError(msg);
          return null;
        }
        
        // [6] Create the appropriate interval based upon granularity, or return error.
        Interval interval;
        try {
          if ("day".equalsIgnoreCase(this.granularity)) {
            interval = new DayInterval(startDay, endDay);
          }
          else if ("week".equalsIgnoreCase(this.granularity)) {
            interval = new WeekInterval(startDay, endDay);
          }
          else if ("month".equalsIgnoreCase(this.granularity)) {
            interval = new MonthInterval(startDay, endDay);
          }
          else {
            String msg = this.granularity + " must be either 'day', 'week', or 'month'";
            setStatusError(msg);
            return null;
          }
        }
        catch (Exception e) {
          String msg = this.start + " and " + this.end + " are illegal. Maybe out of order?";
          setStatusError(msg, e);
          return null;
        }
        
        // [7] Get the parameters.
        StringConstant[] varValues = parseParams(this.params);

        // [8] Check that supplied parameters match required parameters.
        Variable[] variables = chartDef.getVariables();
        if (varValues.length != variables.length) {
          String msg = "Chart needs " + variables.length + " variables; got: " + varValues.length;
          setStatusError(msg);
          return null;
        }
        // Bind variables to values. 
        VariableResolver variableResolver = new VariableResolver();
        for (int i = 0; i < variables.length; i++) {
          variableResolver.add(variables[i], varValues[i]);
        }

        // [9] Make a telemetry definition resolver and generate the TelemetryChartObject.
        TelemetryDefinitionResolver telemetryDefinitionResolver = new TelemetryDefinitionResolver(
            TelemetryDefinitionManagerFactory.getGlobalPersistentInstance(), user);
        TelemetryChartObject chartObject = TelemetryEvaluator.evaluate(chartDef,
            telemetryDefinitionResolver, variableResolver, project, dpdClient, interval);
        
        // [10] Convert the TelemetryChartObject into it's "resource" representation.
        TelemetryChartData chart = convertChartObjectToResource(chartObject);
        
        // [11] Add information about the variables and parameters to the resource.
        for (int i = 0; i < variables.length; i++) {
          Parameter parameter = new Parameter();
          parameter.setName(variables[i].getName());
          parameter.setValue(varValues[i].getValue());
          chart.getParameter().add(parameter);
        }

        // [12] package the resulting data up in XML and return it.
        logRequest();
        return this.getStringRepresentation(makeChartXml(chart));
      }
      catch (Exception e) {
        setStatusError("Error processing chart", e);
        return null;
      }
    }
    // Shouldn't ever get here. 
    return null;
  }

  /**
   * Takes the TelemetryChartObject returned from the telemetry analyzer code and converts it
   * into the resource representation. 
   * @param chartObject The TelemetryChartObject
   * @return A TelemetryChart resource representation. 
   */
  private TelemetryChartData convertChartObjectToResource(TelemetryChartObject chartObject) {
    // All of the JAXB instances will have variable names ending with "Resource" in order to
    // distinguish them from their Telemetry Analyzer counterparts.  
    TelemetryChartData chartResource = new TelemetryChartData();
    chartResource.setURI(this.telemetryServer.getHostName() + "chart/" + this.chart +
        "/" + this.uriUser + "/" + this.projectName + "/" + this.granularity + "/" +
        this.start + "/" + this.end + "/");
    //Not dealing with <Parameter> yet.
    // for each Analyzer subchart...
    for (SubChart subChart : chartObject.getSubCharts()) {
      // Get the Analyzer YAxis instance. 
      org.hackystat.telemetry.analyzer.evaluator.TelemetryChartObject.YAxis yaxis = 
        subChart.getYAxis();
      YAxis yAxisResource = new YAxis();
      yAxisResource.setName("Unknown");
      yAxisResource.setUnits(yaxis.getLabel());
      if (yaxis.getLowerBound() != null) {
        yAxisResource.setLowerBound(BigInteger.valueOf(yaxis.getLowerBound().longValue()));
      }
      if (yaxis.getUpperBound() != null) {
        yAxisResource.setUpperBound(BigInteger.valueOf(yaxis.getUpperBound().longValue()));
      }
      
      yAxisResource.setNumberType((yaxis.isIntegerAxis() ? "integer" : "double"));

      // Get the StreamsObject inside the SubChart.
      TelemetryStreamsObject streams = subChart.getTelemetryStreamsObject();
      // Get each Stream inside the StreamsObject...
      for (Stream streamObject : streams.getStreams()) {
        // Create a TelemetryStream Resource to associate with the Analyzer streamObject
        TelemetryStream telemetryStreamResource = new TelemetryStream();
        telemetryStreamResource.setYAxis(yAxisResource);
        chartResource.getTelemetryStream().add(telemetryStreamResource);
        telemetryStreamResource.setName(streamObject.getName());
        // Now get the Analyzer TelemetryStream 'model' instance associated with the streamObject.
        org.hackystat.telemetry.analyzer.model.TelemetryStream stream 
        = streamObject.getTelemetryStream();
        // Now iterate through the Analyzer TelemetryDataPoint instances. 
        for (TelemetryDataPoint dataPoint : stream.getDataPoints()) {
          // Create a resource DataPoint.
          TelemetryPoint pointResource = new TelemetryPoint();
          pointResource.setTime(Tstamp.makeTimestamp(dataPoint.getPeriod().getFirstDay()));
          String val = (dataPoint.getValue() == null) ? null : dataPoint.getValue().toString();
          pointResource.setValue(val);
          telemetryStreamResource.getTelemetryPoint().add(pointResource);
        }
      }
    }      
    return chartResource;
  }

  /**
   * Creates a fake TelemetryChart instance with reasonable looking internal data.
   * 
   * @return A TelemetryChart instance.
   * @throws Exception If problems occur in Tstamp.
   */
  @SuppressWarnings("unused")
  private TelemetryChartData makeSampleChart() throws Exception {
    TelemetryChartData chart = new TelemetryChartData();
    // Set the attributes
    chart.setURI(this.telemetryServer.getHostName() + "chart/" + this.chart);
    // Set the parameter
    Parameter parameter = new Parameter();
    parameter.setName("FilePattern");
    parameter.setValue("**");
    chart.getParameter().add(parameter);
    // Create telemetry points.
    TelemetryPoint point1 = new TelemetryPoint();
    point1.setTime(Tstamp.makeTimestamp("2007-08-01"));
    TelemetryPoint point2 = new TelemetryPoint();
    point2.setTime(Tstamp.makeTimestamp("2007-08-02"));
    TelemetryPoint point3 = new TelemetryPoint();
    point1.setTime(Tstamp.makeTimestamp("2007-08-03"));
    // Create telemetry stream
    TelemetryStream stream = new TelemetryStream();
    stream.getTelemetryPoint().add(point1);
    stream.getTelemetryPoint().add(point2);
    stream.getTelemetryPoint().add(point3);
    // Set the stream.
    chart.getTelemetryStream().add(stream);
    return chart;
  }

  /**
   * Returns the passed TelemetryChart instance as a String encoding of its XML representation.
   * 
   * @param data The SensorData instance.
   * @return The XML String representation.
   * @throws Exception If problems occur during translation.
   */
  private String makeChartXml(TelemetryChartData data) throws Exception {
    JAXBContext chartJAXB = (JAXBContext) this.telemetryServer.getContext().getAttributes().get(
        "ChartJAXB");
    Marshaller marshaller = chartJAXB.createMarshaller();
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
    Document doc = documentBuilder.newDocument();
    marshaller.marshal(data, doc);
    DOMSource domSource = new DOMSource(doc);
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.transform(domSource, result);
    return writer.toString();
  }
  
  /**
   * Parses the params parameter and returns the comma-separated values as an array of 
   * StringConstant. 
   * White spaces at the beginning and end of those substrings will be trimmed. 
   * If a substring has single quotes or double quotes surrounding it, they are removed 
   * before returning.
   * <p>
   * If you really want to preserve white spaces at the beginning or end of the substring, 
   * use ' sub ' or " sub ".
   * 
   * @param input A comma-separated strings.
   * @return An array of StringConstant.
   */
  private StringConstant[] parseParams(String input) {
    String singleQuote = "'";
    String doubleQuote = "\"";
    if (input == null || input.length() == 0) {
      return new StringConstant[0];
    }
    else {
      String[] subs = input.split(",");
      StringConstant[] constants = new StringConstant[subs.length];
      for (int i = 0; i < subs.length; i++) {
        String str = subs[i].trim();
        if ((str.startsWith(singleQuote) && str.endsWith(singleQuote))
            || (str.startsWith(doubleQuote) && str.endsWith(doubleQuote))) {
          constants[i] = new StringConstant(str.substring(1, str.length() - 1));
        }
        else {
          constants[i] = new StringConstant(str);
        }
      }
      return constants;
    }
  }

}

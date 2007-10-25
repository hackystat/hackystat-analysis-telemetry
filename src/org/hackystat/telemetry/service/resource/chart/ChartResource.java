package org.hackystat.telemetry.service.resource.chart;

import java.io.StringWriter;

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
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.telemetry.analyzer.configuration.TelemetryChartDefinitionInfo;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionManager;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionManagerFactory;
import org.hackystat.telemetry.analyzer.configuration.TelemetryDefinitionType;
import org.hackystat.telemetry.analyzer.evaluator.TelemetryChartObject;
import org.hackystat.telemetry.analyzer.evaluator.TelemetryDefinitionResolver;
import org.hackystat.telemetry.analyzer.evaluator.TelemetryEvaluator;
import org.hackystat.telemetry.analyzer.evaluator.VariableResolver;
import org.hackystat.telemetry.analyzer.language.ast.StringConstant;
import org.hackystat.telemetry.analyzer.language.ast.TelemetryChartDefinition;
import org.hackystat.telemetry.analyzer.language.ast.Variable;
import org.hackystat.telemetry.service.resource.chart.jaxb.Parameter;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChart;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryPoint;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream;
import org.hackystat.telemetry.service.resource.telemetry.TelemetryResource;
import org.hackystat.utilities.stacktrace.StackTrace;
import org.hackystat.utilities.time.interval.DayInterval;
import org.hackystat.utilities.time.interval.Interval;
import org.hackystat.utilities.time.interval.MonthInterval;
import org.hackystat.utilities.time.interval.WeekInterval;
import org.hackystat.utilities.tstamp.Tstamp;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;

/**
 * Processes GET {host}/chart/{chart}/{email}/{project}/{granularity}/{start}/{end} requests.
 * Requires the authenticated user to be {email} or else the Admin user for the sensorbase connected
 * to this service.
 * 
 * @author Philip Johnson
 */
public class ChartResource extends TelemetryResource {

  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public ChartResource(Context context, Request request, Response response) {
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
  public Representation getRepresentation(Variant variant) {
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
          String msg = "Cannot authenticate this user with the DPD or SensorBase server: ";
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg + e.getMessage());
          return null;
        }
        
        // [2] Get the User representation, return with error if not defined. 
        User user;
        try {
          user = sensorBaseClient.getUser(this.uriUser);
        }
        catch (Exception e) {
          String msg = "Cannot retrieve user: " + this.uriUser + ": ";
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg + e.getMessage());
          return null;
        }
        
        // [3] get the Project representation; return with error if not defined.
        Project project;
        try {
          project = sensorBaseClient.getProject(this.uriUser, this.project);
        }
        catch (Exception e) {
          String msg = "Cannot retrieve user/project: " + this.uriUser + "/" + this.project + ": ";
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg + e.getMessage());
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
          String msg = "Chart " + this.chart + " is not defined: ";
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg + e.getMessage());
          return null;
        }
        
        // [5] Get the start and end days, return with error if cannot be parsed.
        XMLGregorianCalendar startDay;
        try {
          startDay = Tstamp.makeTimestamp(this.start);
        }
        catch (Exception e) {
          String msg = "Start day " + this.start + " cannot be parsed.";
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg);
          return null;
        }
        XMLGregorianCalendar endDay;
        try {
          endDay = Tstamp.makeTimestamp(this.end);
        }
        catch (Exception e) {
          String msg = "End day " + this.end + " cannot be parsed.";
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg);
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
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg);
            return null;
          }
        }
        catch (Exception e) {
          String msg = this.start + " and " + this.end + " are illegal. Maybe out of order? : ";
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg + StackTrace.toString(e));
          return null;
        }
        
        // [7] Get the parameters. Initially, parameters are not supported.
        // StringConstant[] constants = TemplateParameterSelector.getTemplateValues(request);
        StringConstant[] constants = new StringConstant[0];
        

        // [8] Check that supplied parameters match required parameters.
        Variable[] variables = chartDef.getVariables();
        if (constants.length != variables.length) {
          String msg = "Chart needs " + variables.length + " variables; got: " + constants.length;
          getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, msg);
          return null;
        }
        
        VariableResolver variableResolver = new VariableResolver();
        for (int i = 0; i < variables.length; i++) {
          variableResolver.add(variables[i], constants[i]);
        }

        // [9] Make a telemetry definition resolver and generate the TelemetryChartObject.
        TelemetryDefinitionResolver telemetryDefinitionResolver = new TelemetryDefinitionResolver(
            TelemetryDefinitionManagerFactory.getGlobalPersistentInstance(), user);
        TelemetryChartObject chartObject = TelemetryEvaluator.evaluate(chartDef,
            telemetryDefinitionResolver, variableResolver, project, dpdClient, interval);

        // [3] invoke the Telemetry language processor, which returns a TelemetryChart.
        TelemetryChart chart = makeSampleChart();
        // [4] package the resulting data up in XML and return it.
        return this.getStringRepresentation(makeChart(chart));
      }
      catch (Exception e) {
        this.telemetryServer.getLogger().warning("Error in chart: " + StackTrace.toString(e));
        return null;
      }
    }
    return null;
  }


  /**
   * Creates a fake TelemetryChart instance with reasonable looking internal data.
   * 
   * @return A TelemetryChart instance.
   * @throws Exception If problems occur in Tstamp.
   */
  private TelemetryChart makeSampleChart() throws Exception {
    TelemetryChart chart = new TelemetryChart();
    // Set the attributes
    chart.setURI(this.telemetryServer.getHostName() + "chart/" + this.chart);
    chart.setGranularity(this.granularity);
    chart.setStart(Tstamp.makeTimestamp("2007-08-01"));
    chart.setEnd(Tstamp.makeTimestamp("2007-08-03"));
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
  private String makeChart(TelemetryChart data) throws Exception {
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

}

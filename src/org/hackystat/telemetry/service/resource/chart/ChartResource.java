package org.hackystat.telemetry.service.resource.chart;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hackystat.telemetry.service.resource.chart.jaxb.Parameter;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChart;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryPoint;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream;
import org.hackystat.telemetry.service.resource.telemetry.TelemetryResource;
import org.hackystat.utilities.stacktrace.StackTrace;
import org.hackystat.utilities.tstamp.Tstamp;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;

/**
 * Processes GET {host}/chart/{chart}/{email}/{project}/{granularity}/{start}/{end} requests.
 * Requires the authenticated user to be {email} or else the Admin user for the sensorbase 
 * connected to this service. 
 * @author Philip Johnson
 */
public class ChartResource extends TelemetryResource {
  
  /**
   * The standard constructor.
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
   * @param variant The representational variant requested.
   * @return The representation. 
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        // [1] perform authorization.
        // [2] get the Chart parameters from the call.
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
   * @param data The SensorData instance. 
   * @return The XML String representation.
   * @throws Exception If problems occur during translation. 
   */
  private String makeChart(TelemetryChart data) throws Exception {
    JAXBContext chartJAXB = 
      (JAXBContext)this.telemetryServer.getContext().getAttributes().get("ChartJAXB");
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


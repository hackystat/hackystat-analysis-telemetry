package org.hackystat.telemetry.resource.chart;

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

import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.telemetry.resource.chart.jaxb.TelemetryChart;
import org.hackystat.telemetry.resource.telemetry.TelemetryResource;
import org.hackystat.utilities.stacktrace.StackTrace;
import org.hackystat.utilities.tstamp.Tstamp;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;

//import static org.hackystat.telemetry.server.ServerProperties.SENSORBASE_HOST_KEY;

/**
 * Implements the Resource for processing GET {host}/devtime/{user}/{project}/{starttime} requests.
 * Requires the authenticated user to be {user} or else the Admin user for the sensorbase 
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
   * Returns an DevTimeDailyProjectData instance representing the DevTime associated with the 
   * Project data, or null if not authorized. 
   * @param variant The representational variant requested.
   * @return The representation. 
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();
        // [2] get a SensorDataIndex of all sensor data for this Project on the requested day.
        XMLGregorianCalendar startTime = Tstamp.makeTimestamp(this.timestamp);
        XMLGregorianCalendar endTime = Tstamp.incrementDays(startTime, 1);
        SensorDataIndex index = client.getProjectSensorData(authUser, project, startTime, endTime);
 
      }
      catch (Exception e) {
        server.getLogger().warning("Error processing devTime: " + StackTrace.toString(e));
        return null;
      }
    }
    return null;
  }
  
  /**
   * Returns the passed SensorData instance as a String encoding of its XML representation.
   * @param data The SensorData instance. 
   * @return The XML String representation.
   * @throws Exception If problems occur during translation. 
   */
  private String makeChart (TelemetryChart data) throws Exception {
    JAXBContext chartJAXB = 
      (JAXBContext)this.server.getContext().getAttributes().get("ChartJAXB");
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


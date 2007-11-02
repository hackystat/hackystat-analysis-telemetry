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

import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartIndex;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartRef;
import org.hackystat.telemetry.analyzer.configuration.jaxb.TelemetryDefinition;
import org.hackystat.telemetry.service.resource.telemetry.TelemetryResource;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;

/**
 * Processes GET {host}/charts and returns a TelemetryChartIndex representation. 
 * Requires the authenticated user to be {email} or else the Admin user for the sensorbase connected
 * to this service.
 * 
 * @author Philip Johnson
 */
public class ChartsResource extends TelemetryResource {
  
  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public ChartsResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Returns a TelemetryChartIndex instance providing pointers to all defined Charts.
   * 
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
      TelemetryChartIndex index = new TelemetryChartIndex();
      for (TelemetryDefinition definition : this.getTelemetryDefinitions()) {
        if (definition.getDefinitionType().equalsIgnoreCase("Chart")) {
          TelemetryChartRef ref = new TelemetryChartRef();
          ref.setName(definition.getName());
          ref.setHref(this.telemetryServer.getHostName() + "chart/" + definition.getName());
          index.getTelemetryChartRef().add(ref);
        }
      }
      return super.getStringRepresentation(makeChartIndexXml(index));
    }
    // Shouldn't ever get here. 
    return null;
  }

  /**
   * Returns the passed TelemetryChart instance as a String encoding of its XML representation.
   * 
   * @param index The SensorData instance.
   * @return The XML String representation.
   */
  private String makeChartIndexXml(TelemetryChartIndex index) {
    StringWriter writer = new StringWriter();
    try {
      JAXBContext jaxbContext = JAXBContext
      .newInstance(org.hackystat.telemetry.service.resource.chart.jaxb.ObjectFactory.class);
      Marshaller marshaller = jaxbContext.createMarshaller();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
      Document doc = documentBuilder.newDocument();
      marshaller.marshal(index, doc);
      DOMSource domSource = new DOMSource(doc);
      StreamResult result = new StreamResult(writer);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.transform(domSource, result);
    }
    catch (Exception e) {
      System.out.println(e);
    }
    return writer.toString();
  }
  
  
}

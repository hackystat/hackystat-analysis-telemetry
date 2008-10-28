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

import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartDefinition;
import org.hackystat.telemetry.service.resource.chart.jaxb.ParameterDefinition;
import org.hackystat.telemetry.service.resource.chart.jaxb.Type;
import org.hackystat.telemetry.analyzer.configuration.jaxb.TelemetryDefinition;
import org.hackystat.telemetry.analyzer.configuration.jaxb.Parameter;
import org.hackystat.telemetry.service.resource.telemetry.TelemetryResource;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;

/**
 * Processes GET {host}/chart/{chart} and returns a TelemetryChartDefinition representation. 
 * Requires an authenticated user for the SensorBase associated with this service.
 * 
 * @author Philip Johnson
 */
public class ChartDefinitionResource extends TelemetryResource {
  
  /**
   * The standard constructor.
   * 
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public ChartDefinitionResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Returns a TelemetryChartDefinition for the requested Chart, or returns an error code
   * if the definition of the chart could not be found. 
   * 
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation getRepresentation(Variant variant) {
    try {
      if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
        TelemetryChartDefinition chartDef = new TelemetryChartDefinition();
        for (TelemetryDefinition definition : this.getTelemetryDefinitions()) {
          if ((definition.getDefinitionType().equalsIgnoreCase("Chart"))
              && (definition.getName().equals(this.chart))) {
            chartDef.setDescription(definition.getDescription());
            chartDef.setName(definition.getName());
            chartDef.setSourceCode(definition.getSourceCode());
            for (Parameter param : definition.getParameter()) {
              ParameterDefinition chartParam = new ParameterDefinition();
              chartParam.setName(param.getName());
              chartParam.setDescription(param.getDescription());
              Type type = new Type();
              type.setDefault(param.getType().getDefault());
              type.setName(param.getType().getName());
              if (param.getType().getMinValue() != null) { // NOPMD
                type.setMinValue(param.getType().getMinValue());
              }
              if (param.getType().getMaxValue() != null) { // NOPMD
                type.setMaxValue(param.getType().getMaxValue());
              }
              for (String value : param.getType().getValue()) {
                type.getValue().add(value);
              }
              chartParam.setType(type);
              chartDef.getParameterDefinition().add(chartParam);
            }
            // Made the definition, so return now.
            return super.getStringRepresentation(makeChartDefinitionXml(chartDef));
          }
        }
        // got past the for loop without finding the chart and returning
        setStatusError("Could not find chart definition " + this.chart);
        return null;
      }
    }
    catch (Exception e) {
      setStatusError("Error getting chart definition.", e);
      return null;
    }
    return null;
  }

  /**
   * Returns the passed TelemetryChartDefinition instance as a String encoding of its XML.
   * 
   * @param chartDef The SensorData instance.
   * @return The XML String representation.
   */
  private String makeChartDefinitionXml(TelemetryChartDefinition chartDef) {
    StringWriter writer = new StringWriter();
    try {
      JAXBContext jaxbContext = JAXBContext
      .newInstance(org.hackystat.telemetry.service.resource.chart.jaxb.ObjectFactory.class);
      Marshaller marshaller = jaxbContext.createMarshaller();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
      Document doc = documentBuilder.newDocument();
      marshaller.marshal(chartDef, doc);
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

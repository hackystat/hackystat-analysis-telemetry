//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.11.01 at 02:59:31 PM GMT-10:00 
//


package org.hackystat.telemetry.service.resource.chart.jaxb;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.hackystat.telemetry.service.resource.chart.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.hackystat.telemetry.service.resource.chart.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link YAxis }
     * 
     */
    public YAxis createYAxis() {
        return new YAxis();
    }

    /**
     * Create an instance of {@link TelemetryChartRef }
     * 
     */
    public TelemetryChartRef createTelemetryChartRef() {
        return new TelemetryChartRef();
    }

    /**
     * Create an instance of {@link Parameter }
     * 
     */
    public Parameter createParameter() {
        return new Parameter();
    }

    /**
     * Create an instance of {@link TelemetryPoint }
     * 
     */
    public TelemetryPoint createTelemetryPoint() {
        return new TelemetryPoint();
    }

    /**
     * Create an instance of {@link TelemetryChart }
     * 
     */
    public TelemetryChart createTelemetryChart() {
        return new TelemetryChart();
    }

    /**
     * Create an instance of {@link TelemetryChartIndex }
     * 
     */
    public TelemetryChartIndex createTelemetryChartIndex() {
        return new TelemetryChartIndex();
    }

    /**
     * Create an instance of {@link TelemetryStream }
     * 
     */
    public TelemetryStream createTelemetryStream() {
        return new TelemetryStream();
    }

}

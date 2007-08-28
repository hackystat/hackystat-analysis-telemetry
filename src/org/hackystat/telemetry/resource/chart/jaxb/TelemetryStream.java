//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.08.27 at 04:46:34 PM GMT-10:00 
//


package org.hackystat.telemetry.resource.chart.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}Y-Axis"/>
 *         &lt;element ref="{}TelemetryPoint" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{}Name use="required""/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "yAxis",
    "telemetryPoint"
})
@XmlRootElement(name = "TelemetryStream")
public class TelemetryStream {

    @XmlElement(name = "Y-Axis", required = true)
    protected YAxis yAxis;
    @XmlElement(name = "TelemetryPoint")
    protected List<TelemetryPoint> telemetryPoint;
    @XmlAttribute(name = "Name", required = true)
    protected String name;

    /**
     * Gets the value of the yAxis property.
     * 
     * @return
     *     possible object is
     *     {@link YAxis }
     *     
     */
    public YAxis getYAxis() {
        return yAxis;
    }

    /**
     * Sets the value of the yAxis property.
     * 
     * @param value
     *     allowed object is
     *     {@link YAxis }
     *     
     */
    public void setYAxis(YAxis value) {
        this.yAxis = value;
    }

    /**
     * Gets the value of the telemetryPoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the telemetryPoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTelemetryPoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TelemetryPoint }
     * 
     * 
     */
    public List<TelemetryPoint> getTelemetryPoint() {
        if (telemetryPoint == null) {
            telemetryPoint = new ArrayList<TelemetryPoint>();
        }
        return this.telemetryPoint;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}

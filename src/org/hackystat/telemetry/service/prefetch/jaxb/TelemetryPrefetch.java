//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.07.15 at 10:02:35 AM GMT-10:00 
//


package org.hackystat.telemetry.service.prefetch.jaxb;

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
 *         &lt;element ref="{}MinutesAfterMidnight"/>
 *         &lt;element ref="{}RunOnStartup"/>
 *         &lt;element ref="{}PrefetchChart" maxOccurs="unbounded" minOccurs="0"/>
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
    "minutesAfterMidnight",
    "runOnStartup",
    "prefetchChart"
})
@XmlRootElement(name = "TelemetryPrefetch")
public class TelemetryPrefetch {

    @XmlElement(name = "MinutesAfterMidnight")
    protected int minutesAfterMidnight;
    @XmlElement(name = "RunOnStartup", required = true)
    protected String runOnStartup;
    @XmlElement(name = "PrefetchChart")
    protected List<PrefetchChart> prefetchChart;
    @XmlAttribute(name = "Name", required = true)
    protected String name;

    /**
     * Gets the value of the minutesAfterMidnight property.
     * 
     */
    public int getMinutesAfterMidnight() {
        return minutesAfterMidnight;
    }

    /**
     * Sets the value of the minutesAfterMidnight property.
     * 
     */
    public void setMinutesAfterMidnight(int value) {
        this.minutesAfterMidnight = value;
    }

    /**
     * Gets the value of the runOnStartup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRunOnStartup() {
        return runOnStartup;
    }

    /**
     * Sets the value of the runOnStartup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRunOnStartup(String value) {
        this.runOnStartup = value;
    }

    /**
     * Gets the value of the prefetchChart property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prefetchChart property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrefetchChart().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrefetchChart }
     * 
     * 
     */
    public List<PrefetchChart> getPrefetchChart() {
        if (prefetchChart == null) {
            prefetchChart = new ArrayList<PrefetchChart>();
        }
        return this.prefetchChart;
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

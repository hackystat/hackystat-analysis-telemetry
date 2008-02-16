//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.02.15 at 05:19:54 PM GMT-10:00 
//


package org.hackystat.telemetry.service.prefetch.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.hackystat.telemetry.service.prefetch.jaxb package. 
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

    private final static QName _Name_QNAME = new QName("", "Name");
    private final static QName _Value_QNAME = new QName("", "Value");
    private final static QName _ChartName_QNAME = new QName("", "ChartName");
    private final static QName _ProjectName_QNAME = new QName("", "ProjectName");
    private final static QName _AuthorizedUserName_QNAME = new QName("", "AuthorizedUserName");
    private final static QName _TelemetryHost_QNAME = new QName("", "TelemetryHost");
    private final static QName _ProjectOwner_QNAME = new QName("", "ProjectOwner");
    private final static QName _StartTime_QNAME = new QName("", "StartTime");
    private final static QName _AuthorizedUserPassword_QNAME = new QName("", "AuthorizedUserPassword");
    private final static QName _MinutesAfterMidnight_QNAME = new QName("", "MinutesAfterMidnight");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.hackystat.telemetry.service.prefetch.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Chart }
     * 
     */
    public Chart createChart() {
        return new Chart();
    }

    /**
     * Create an instance of {@link ChartParameter }
     * 
     */
    public ChartParameter createChartParameter() {
        return new ChartParameter();
    }

    /**
     * Create an instance of {@link TelemetryPrefetch }
     * 
     */
    public TelemetryPrefetch createTelemetryPrefetch() {
        return new TelemetryPrefetch();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Value")
    public JAXBElement<String> createValue(String value) {
        return new JAXBElement<String>(_Value_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ChartName")
    public JAXBElement<String> createChartName(String value) {
        return new JAXBElement<String>(_ChartName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ProjectName")
    public JAXBElement<String> createProjectName(String value) {
        return new JAXBElement<String>(_ProjectName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "AuthorizedUserName")
    public JAXBElement<String> createAuthorizedUserName(String value) {
        return new JAXBElement<String>(_AuthorizedUserName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "TelemetryHost")
    public JAXBElement<String> createTelemetryHost(String value) {
        return new JAXBElement<String>(_TelemetryHost_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ProjectOwner")
    public JAXBElement<String> createProjectOwner(String value) {
        return new JAXBElement<String>(_ProjectOwner_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "StartTime")
    public JAXBElement<String> createStartTime(String value) {
        return new JAXBElement<String>(_StartTime_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "AuthorizedUserPassword")
    public JAXBElement<String> createAuthorizedUserPassword(String value) {
        return new JAXBElement<String>(_AuthorizedUserPassword_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "MinutesAfterMidnight")
    public JAXBElement<Integer> createMinutesAfterMidnight(Integer value) {
        return new JAXBElement<Integer>(_MinutesAfterMidnight_QNAME, Integer.class, null, value);
    }

}
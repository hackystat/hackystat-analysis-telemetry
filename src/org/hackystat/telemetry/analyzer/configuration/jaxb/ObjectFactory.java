//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.02.16 at 08:42:12 AM HST 
//


package org.hackystat.telemetry.analyzer.configuration.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.hackystat.telemetry.analyzer.configuration.jaxb package. 
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

    private final static QName _Value_QNAME = new QName("", "Value");
    private final static QName _Description_QNAME = new QName("", "Description");
    private final static QName _SourceCode_QNAME = new QName("", "SourceCode");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.hackystat.telemetry.analyzer.configuration.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Parameter }
     * 
     */
    public Parameter createParameter() {
        return new Parameter();
    }

    /**
     * Create an instance of {@link TelemetryDefinitions }
     * 
     */
    public TelemetryDefinitions createTelemetryDefinitions() {
        return new TelemetryDefinitions();
    }

    /**
     * Create an instance of {@link Type }
     * 
     */
    public Type createType() {
        return new Type();
    }

    /**
     * Create an instance of {@link TelemetryDefinition }
     * 
     */
    public TelemetryDefinition createTelemetryDefinition() {
        return new TelemetryDefinition();
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
    @XmlElementDecl(namespace = "", name = "Description")
    public JAXBElement<String> createDescription(String value) {
        return new JAXBElement<String>(_Description_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "SourceCode")
    public JAXBElement<String> createSourceCode(String value) {
        return new JAXBElement<String>(_SourceCode_QNAME, String.class, null, value);
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.09.20 at 11:48:06 PM CEST 
//


package org.medee.playground.upnp.didllite.jaxb;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * 
 * 				Contains the actual foreign metadata.
 * 			
 * 
 * <p>Java class for fm.elements.body.type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fm.elements.body.type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="fmEmbeddedXML" type="{urn:schemas-upnp-org:metadata-1-0/upnp/}fm.elements.body.embeddedXML.type"/>
 *         &lt;element name="fmEmbeddedString" type="{urn:schemas-upnp-org:metadata-1-0/upnp/}fm.elements.body.embeddedString.type"/>
 *         &lt;element name="fmURI" type="{urn:schemas-upnp-org:metadata-1-0/upnp/}fm.elements.body.uri.type"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{urn:schemas-upnp-org:metadata-1-0/upnp/}fm.attrs.bodyAttr.group"/>
 *       &lt;attGroup ref="{urn:schemas-upnp-org:metadata-1-0/upnp/}fm.extensions.attributes.any"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fm.elements.body.type", propOrder = {
    "fmEmbeddedXML",
    "fmEmbeddedString",
    "fmURI"
})
public class FmElementsBodyType {

    protected FmElementsBodyEmbeddedXMLType fmEmbeddedXML;
    protected FmElementsBodyEmbeddedStringType fmEmbeddedString;
    protected FmElementsBodyUriType fmURI;
    @XmlAttribute(name = "xmlFlag", required = true)
    protected boolean xmlFlag;
    @XmlAttribute(name = "mimeType")
    protected String mimeType;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the fmEmbeddedXML property.
     * 
     * @return
     *     possible object is
     *     {@link FmElementsBodyEmbeddedXMLType }
     *     
     */
    public FmElementsBodyEmbeddedXMLType getFmEmbeddedXML() {
        return fmEmbeddedXML;
    }

    /**
     * Sets the value of the fmEmbeddedXML property.
     * 
     * @param value
     *     allowed object is
     *     {@link FmElementsBodyEmbeddedXMLType }
     *     
     */
    public void setFmEmbeddedXML(FmElementsBodyEmbeddedXMLType value) {
        this.fmEmbeddedXML = value;
    }

    /**
     * Gets the value of the fmEmbeddedString property.
     * 
     * @return
     *     possible object is
     *     {@link FmElementsBodyEmbeddedStringType }
     *     
     */
    public FmElementsBodyEmbeddedStringType getFmEmbeddedString() {
        return fmEmbeddedString;
    }

    /**
     * Sets the value of the fmEmbeddedString property.
     * 
     * @param value
     *     allowed object is
     *     {@link FmElementsBodyEmbeddedStringType }
     *     
     */
    public void setFmEmbeddedString(FmElementsBodyEmbeddedStringType value) {
        this.fmEmbeddedString = value;
    }

    /**
     * Gets the value of the fmURI property.
     * 
     * @return
     *     possible object is
     *     {@link FmElementsBodyUriType }
     *     
     */
    public FmElementsBodyUriType getFmURI() {
        return fmURI;
    }

    /**
     * Sets the value of the fmURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link FmElementsBodyUriType }
     *     
     */
    public void setFmURI(FmElementsBodyUriType value) {
        this.fmURI = value;
    }

    /**
     * Gets the value of the xmlFlag property.
     * 
     */
    public boolean isXmlFlag() {
        return xmlFlag;
    }

    /**
     * Sets the value of the xmlFlag property.
     * 
     */
    public void setXmlFlag(boolean value) {
        this.xmlFlag = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}

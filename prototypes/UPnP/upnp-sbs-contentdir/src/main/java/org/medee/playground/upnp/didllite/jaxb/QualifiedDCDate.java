//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.09.20 at 11:48:06 PM CEST 
//


package org.medee.playground.upnp.didllite.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for qualifiedDCDate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="qualifiedDCDate">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://purl.org/dc/elements/1.1/>elementType">
 *       &lt;attGroup ref="{urn:schemas-upnp-org:metadata-1-0/upnp/}dateTime.attr.group"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qualifiedDCDate", namespace = "urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/")
public class QualifiedDCDate
    extends ElementType
{

    @XmlAttribute(name = "daylightSaving", required = true)
    protected DaylightSavingType daylightSaving;

    /**
     * Gets the value of the daylightSaving property.
     * 
     * @return
     *     possible object is
     *     {@link DaylightSavingType }
     *     
     */
    public DaylightSavingType getDaylightSaving() {
        return daylightSaving;
    }

    /**
     * Sets the value of the daylightSaving property.
     * 
     * @param value
     *     allowed object is
     *     {@link DaylightSavingType }
     *     
     */
    public void setDaylightSaving(DaylightSavingType value) {
        this.daylightSaving = value;
    }

}

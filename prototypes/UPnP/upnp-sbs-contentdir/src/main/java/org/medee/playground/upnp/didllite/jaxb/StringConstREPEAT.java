//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.09.20 at 11:48:06 PM CEST 
//


package org.medee.playground.upnp.didllite.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for string.const.REPEAT.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="string.const.REPEAT">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="REPEAT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "string.const.REPEAT", namespace = "urn:schemas-upnp-org:av:av")
@XmlEnum
public enum StringConstREPEAT {

    REPEAT;

    public String value() {
        return name();
    }

    public static StringConstREPEAT fromValue(String v) {
        return valueOf(v);
    }

}

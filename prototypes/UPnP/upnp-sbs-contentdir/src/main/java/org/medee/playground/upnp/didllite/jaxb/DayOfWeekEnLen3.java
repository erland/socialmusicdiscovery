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
 * <p>Java class for dayOfWeek.en.len.3.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="dayOfWeek.en.len.3">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="SUN"/>
 *     &lt;enumeration value="MON"/>
 *     &lt;enumeration value="TUE"/>
 *     &lt;enumeration value="WED"/>
 *     &lt;enumeration value="THU"/>
 *     &lt;enumeration value="FRI"/>
 *     &lt;enumeration value="SAT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "dayOfWeek.en.len.3", namespace = "urn:schemas-upnp-org:av:av")
@XmlEnum
public enum DayOfWeekEnLen3 {

    SUN,
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT;

    public String value() {
        return name();
    }

    public static DayOfWeekEnLen3 fromValue(String v) {
        return valueOf(v);
    }

}

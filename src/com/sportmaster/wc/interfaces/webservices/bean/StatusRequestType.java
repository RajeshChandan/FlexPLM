
package com.sportmaster.wc.interfaces.webservices.bean;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusRequestType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StatusRequestType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INTEGRATED"/>
 *     &lt;enumeration value="NO_INTEGRATED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StatusRequestType")
@XmlEnum
public enum StatusRequestType {

    INTEGRATED,
    NO_INTEGRATED;

    public String value() {
        return name();
    }

    public static StatusRequestType fromValue(String v) {
        return valueOf(v);
    }

}

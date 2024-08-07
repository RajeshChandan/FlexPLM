
package com.sportmaster.wc.interfaces.webservices.bean;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BOEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BOEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GoM"/>
 *     &lt;enumeration value="Category"/>
 *     &lt;enumeration value="SubCategory"/>
 *     &lt;enumeration value="Class"/>
 *     &lt;enumeration value="SubClass"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BOEnumeration")
@XmlEnum
public enum BOEnumeration {

    @XmlEnumValue("GoM")
    GO_M("GoM"),
    @XmlEnumValue("Category")
    CATEGORY("Category"),
    @XmlEnumValue("SubCategory")
    SUB_CATEGORY("SubCategory"),
    @XmlEnumValue("Class")
    CLASS("Class"),
    @XmlEnumValue("SubClass")
    SUB_CLASS("SubClass");
    private final String value;

    BOEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BOEnumeration fromValue(String v) {
        for (BOEnumeration c: BOEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

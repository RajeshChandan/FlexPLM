
package com.sportmaster.wc.interfaces.webservices.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="divisionStatus" type="{http://www.sportmaster.ru/plmservice}DivisionStatusInformation"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "divisionStatus"
})
@XmlRootElement(name = "GetStatusDivisionRequest")
public class GetStatusDivisionRequest {

    @XmlElement(required = true)
    protected DivisionStatusInformation divisionStatus;

    /**
     * Gets the value of the divisionStatus property.
     * 
     * @return
     *     possible object is
     *     {@link DivisionStatusInformation }
     *     
     */
    public DivisionStatusInformation getDivisionStatus() {
        return divisionStatus;
    }

    /**
     * Sets the value of the divisionStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link DivisionStatusInformation }
     *     
     */
    public void setDivisionStatus(DivisionStatusInformation value) {
        this.divisionStatus = value;
    }

}

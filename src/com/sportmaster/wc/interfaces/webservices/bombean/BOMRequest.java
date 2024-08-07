
package com.sportmaster.wc.interfaces.webservices.bombean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BOMRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BOMRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BOMPart" type="{http://www.sportmaster.ru/plmbom}BOMPart"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BOMRequest", propOrder = {
    "requestId",
    "bomPart"
})
@XmlRootElement(name = "BOMRequest")
public class BOMRequest {

    protected int requestId;
    @XmlElement(name = "BOMPart", required = true)
    protected BOMPart bomPart;

    /**
     * Gets the value of the requestId property.
     * 
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     */
    public void setRequestId(int value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the bomPart property.
     * 
     * @return
     *     possible object is
     *     {@link BOMPart }
     *     
     */
    public BOMPart getBOMPart() {
        return bomPart;
    }

    /**
     * Sets the value of the bomPart property.
     * 
     * @param value
     *     allowed object is
     *     {@link BOMPart }
     *     
     */
    public void setBOMPart(BOMPart value) {
        this.bomPart = value;
    }

}


package com.sportmaster.wc.interfaces.webservices.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="mdmBO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestType" type="{http://www.sportmaster.ru/plmservice}StatusRequestType"/>
 *         &lt;element name="ErrMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateInformation", propOrder = {

})
public class UpdateInformation {

    @XmlElement(required = true)
    protected String mdmBO;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected StatusRequestType requestType;
    @XmlElement(name = "ErrMessage")
    protected String errMessage;

    /**
     * Gets the value of the mdmBO property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdmBO() {
        return mdmBO;
    }

    /**
     * Sets the value of the mdmBO property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdmBO(String value) {
        this.mdmBO = value;
    }

    /**
     * Gets the value of the requestType property.
     * 
     * @return
     *     possible object is
     *     {@link StatusRequestType }
     *     
     */
    public StatusRequestType getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusRequestType }
     *     
     */
    public void setRequestType(StatusRequestType value) {
        this.requestType = value;
    }

    /**
     * Gets the value of the errMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrMessage() {
        return errMessage;
    }

    /**
     * Sets the value of the errMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrMessage(String value) {
        this.errMessage = value;
    }

}

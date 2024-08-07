
package com.sportmaster.wc.interfaces.webservices.bombean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BOMRequestResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BOMRequestResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BomPartBranchID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SpecPLMID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SpecSeasonMDMID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IntegrationStatus" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BOMRequestResponse", propOrder = {
    "requestId",
    "bomPartBranchID",
    "specPLMID",
    "specSeasonMDMID",
    "integrationStatus",
    "errorMessage"
})
public class BOMRequestResponse {

    protected int requestId;
    @XmlElement(name = "BomPartBranchID", required = true)
    protected String bomPartBranchID;
    @XmlElement(name = "SpecPLMID", required = true)
    protected String specPLMID;
    @XmlElement(name = "SpecSeasonMDMID", required = true)
    protected String specSeasonMDMID;
    @XmlElement(name = "IntegrationStatus")
    protected boolean integrationStatus;
    @XmlElement(name = "ErrorMessage")
    protected String errorMessage;

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
     * Gets the value of the bomPartBranchID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBomPartBranchID() {
        return bomPartBranchID;
    }

    /**
     * Sets the value of the bomPartBranchID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBomPartBranchID(String value) {
        this.bomPartBranchID = value;
    }

    /**
     * Gets the value of the specPLMID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecPLMID() {
        return specPLMID;
    }

    /**
     * Sets the value of the specPLMID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecPLMID(String value) {
        this.specPLMID = value;
    }

    /**
     * Gets the value of the specSeasonMDMID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecSeasonMDMID() {
        return specSeasonMDMID;
    }

    /**
     * Sets the value of the specSeasonMDMID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecSeasonMDMID(String value) {
        this.specSeasonMDMID = value;
    }

    /**
     * Gets the value of the integrationStatus property.
     * 
     */
    public boolean isIntegrationStatus() {
        return integrationStatus;
    }

    /**
     * Sets the value of the integrationStatus property.
     * 
     */
    public void setIntegrationStatus(boolean value) {
        this.integrationStatus = value;
    }

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

}

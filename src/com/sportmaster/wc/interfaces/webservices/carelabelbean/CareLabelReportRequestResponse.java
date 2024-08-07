
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CareLabelReportRequestResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CareLabelReportRequestResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="productSeasonPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="productSeasonMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="integrationStatus" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CareLabelReportRequestResponse", propOrder = {
    "requestId",
    "productSeasonPLMId",
    "productSeasonMDMId",
    "integrationStatus",
    "errorMessage"
})
public class CareLabelReportRequestResponse {

    protected int requestId;
    @XmlElement(required = true)
    protected String productSeasonPLMId;
    @XmlElement(required = true)
    protected String productSeasonMDMId;
    protected boolean integrationStatus;
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
     * Gets the value of the productSeasonPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductSeasonPLMId() {
        return productSeasonPLMId;
    }

    /**
     * Sets the value of the productSeasonPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductSeasonPLMId(String value) {
        this.productSeasonPLMId = value;
    }

    /**
     * Gets the value of the productSeasonMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductSeasonMDMId() {
        return productSeasonMDMId;
    }

    /**
     * Sets the value of the productSeasonMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductSeasonMDMId(String value) {
        this.productSeasonMDMId = value;
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

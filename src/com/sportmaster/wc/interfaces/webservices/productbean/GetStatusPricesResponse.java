
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetStatusPricesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetStatusPricesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="productSeasonPricesInformationStatusResponse" type="{http://www.sportmaster.ru/plmproduct}ProductSeasonPricesInformationStatusResponse" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "GetStatusPricesResponse", propOrder = {
    "requestId",
    "productSeasonPricesInformationStatusResponse",
    "errorMessage"
})
@XmlRootElement(name="GetStatusPricesResponse")
public class GetStatusPricesResponse {

    @XmlElement(required = true)
    protected String requestId;
    protected List<ProductSeasonPricesInformationStatusResponse> productSeasonPricesInformationStatusResponse;
    protected String errorMessage;

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the productSeasonPricesInformationStatusResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productSeasonPricesInformationStatusResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductSeasonPricesInformationStatusResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductSeasonPricesInformationStatusResponse }
     * 
     * 
     */
    public List<ProductSeasonPricesInformationStatusResponse> getProductSeasonPricesInformationStatusResponse() {
        if (productSeasonPricesInformationStatusResponse == null) {
            productSeasonPricesInformationStatusResponse = new ArrayList<ProductSeasonPricesInformationStatusResponse>();
        }
        return this.productSeasonPricesInformationStatusResponse;
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

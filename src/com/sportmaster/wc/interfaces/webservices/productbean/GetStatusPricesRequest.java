
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetStatusPricesRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetStatusPricesRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="productSeasonPricesInformationStatus" type="{http://www.sportmaster.ru/plmproduct}ProductSeasonPricesInformationStatus" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetStatusPricesRequest", propOrder = {
    "requestId",
    "productSeasonPricesInformationStatus"
})
@XmlRootElement(name="GetStatusPricesRequest")
public class GetStatusPricesRequest {

    @XmlElement(required = true)
    protected String requestId;
    protected List<ProductSeasonPricesInformationStatus> productSeasonPricesInformationStatus;

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
     * Gets the value of the productSeasonPricesInformationStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productSeasonPricesInformationStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductSeasonPricesInformationStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductSeasonPricesInformationStatus }
     * 
     * 
     */
    public List<ProductSeasonPricesInformationStatus> getProductSeasonPricesInformationStatus() {
        if (productSeasonPricesInformationStatus == null) {
            productSeasonPricesInformationStatus = new ArrayList<ProductSeasonPricesInformationStatus>();
        }
        return this.productSeasonPricesInformationStatus;
    }

}

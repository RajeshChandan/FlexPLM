
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductSeasonLinkInformationUpdatesRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductSeasonLinkInformationUpdatesRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="productSeasonLink" type="{http://www.sportmaster.ru/plmproduct}ProductSeasonLink"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductSeasonLinkInformationUpdatesRequest", propOrder = {
    "requestId",
    "productSeasonLink"
})
@XmlRootElement(name = "ProductSeasonLinkInformationUpdatesRequest")
public class ProductSeasonLinkInformationUpdatesRequest implements Serializable {

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	protected int requestId;
    @XmlElement(required = true)
    protected ProductSeasonLink productSeasonLink;

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
     * Gets the value of the productSeasonLink property.
     * 
     * @return
     *     possible object is
     *     {@link ProductSeasonLink }
     *     
     */
    public ProductSeasonLink getProductSeasonLink() {
        return productSeasonLink;
    }

    /**
     * Sets the value of the productSeasonLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductSeasonLink }
     *     
     */
    public void setProductSeasonLink(ProductSeasonLink value) {
        this.productSeasonLink = value;
    }

}

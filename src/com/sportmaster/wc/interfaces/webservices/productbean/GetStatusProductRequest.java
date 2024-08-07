
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetStatusProductRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetStatusProductRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="productSeasonLinkInformationStatus" type="{http://www.sportmaster.ru/plmproduct}ProductSeasonLinkInformationStatus" minOccurs="0"/>
 *         &lt;element name="colorwaySeasonLinkInformationStatus" type="{http://www.sportmaster.ru/plmproduct}ColorwaySeasonLinkInformationStatus" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetStatusProductRequest", propOrder = {
    "requestId",
    "productSeasonLinkInformationStatus",
    "colorwaySeasonLinkInformationStatus"
})
public class GetStatusProductRequest implements Serializable{

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2144225874889237270L;
	protected int requestId;
    protected ProductSeasonLinkInformationStatus productSeasonLinkInformationStatus;
    protected ColorwaySeasonLinkInformationStatus colorwaySeasonLinkInformationStatus;

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
     * Gets the value of the productSeasonLinkInformationStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ProductSeasonLinkInformationStatus }
     *     
     */
    public ProductSeasonLinkInformationStatus getProductSeasonLinkInformationStatus() {
        return productSeasonLinkInformationStatus;
    }

    /**
     * Sets the value of the productSeasonLinkInformationStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductSeasonLinkInformationStatus }
     *     
     */
    public void setProductSeasonLinkInformationStatus(ProductSeasonLinkInformationStatus value) {
        this.productSeasonLinkInformationStatus = value;
    }

    /**
     * Gets the value of the colorwaySeasonLinkInformationStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ColorwaySeasonLinkInformationStatus }
     *     
     */
    public ColorwaySeasonLinkInformationStatus getColorwaySeasonLinkInformationStatus() {
        return colorwaySeasonLinkInformationStatus;
    }

    /**
     * Sets the value of the colorwaySeasonLinkInformationStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorwaySeasonLinkInformationStatus }
     *     
     */
    public void setColorwaySeasonLinkInformationStatus(ColorwaySeasonLinkInformationStatus value) {
        this.colorwaySeasonLinkInformationStatus = value;
    }

}

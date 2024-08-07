
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetProductUpdatesRequestResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetProductUpdatesRequestResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="productSeasonLinkInformation" type="{http://www.sportmaster.ru/plmproduct}ProductSeasonLinkInformation" minOccurs="0"/>
 *         &lt;element name="colorwaySeasonLinkInformation" type="{http://www.sportmaster.ru/plmproduct}ColorwaySeasonLinkInformation" minOccurs="0"/>
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
@XmlType(name = "GetProductUpdatesRequestResponse", propOrder = {
    "requestId",
    "productSeasonLinkInformation",
    "colorwaySeasonLinkInformation",
    "errorMessage"
})
public class GetProductUpdatesRequestResponse implements Serializable  {

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 5617122489475834544L;
	protected int requestId;
    protected ProductSeasonLinkInformation productSeasonLinkInformation;
    protected ColorwaySeasonLinkInformation colorwaySeasonLinkInformation;
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
     * Gets the value of the productSeasonLinkInformation property.
     * 
     * @return
     *     possible object is
     *     {@link ProductSeasonLinkInformation }
     *     
     */
    public ProductSeasonLinkInformation getProductSeasonLinkInformation() {
        return productSeasonLinkInformation;
    }

    /**
     * Sets the value of the productSeasonLinkInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductSeasonLinkInformation }
     *     
     */
    public void setProductSeasonLinkInformation(ProductSeasonLinkInformation value) {
        this.productSeasonLinkInformation = value;
    }

    /**
     * Gets the value of the colorwaySeasonLinkInformation property.
     * 
     * @return
     *     possible object is
     *     {@link ColorwaySeasonLinkInformation }
     *     
     */
    public ColorwaySeasonLinkInformation getColorwaySeasonLinkInformation() {
        return colorwaySeasonLinkInformation;
    }

    /**
     * Sets the value of the colorwaySeasonLinkInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorwaySeasonLinkInformation }
     *     
     */
    public void setColorwaySeasonLinkInformation(ColorwaySeasonLinkInformation value) {
        this.colorwaySeasonLinkInformation = value;
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

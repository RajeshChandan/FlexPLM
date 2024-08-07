
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetStatusProductRequestResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetStatusProductRequestResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="productSeasonLinkInformationStatusResponse" type="{http://www.sportmaster.ru/plmproduct}ProductSeasonLinkInformationStatusResponse" minOccurs="0"/>
 *         &lt;element name="colorwaySeasonLinkInformationStatusResponse" type="{http://www.sportmaster.ru/plmproduct}ColorwaySeasonLinkInformationStatusResponse" minOccurs="0"/>
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
@XmlType(name = "GetStatusProductRequestResponse", propOrder = {
    "requestId",
    "productSeasonLinkInformationStatusResponse",
    "colorwaySeasonLinkInformationStatusResponse",
    "errorMessage"
})
public class GetStatusProductRequestResponse implements Serializable{

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	protected int requestId;
    protected ProductSeasonLinkInformationStatusResponse productSeasonLinkInformationStatusResponse;
    protected ColorwaySeasonLinkInformationStatusResponse colorwaySeasonLinkInformationStatusResponse;
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
     * Gets the value of the productSeasonLinkInformationStatusResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ProductSeasonLinkInformationStatusResponse }
     *     
     */
    public ProductSeasonLinkInformationStatusResponse getProductSeasonLinkInformationStatusResponse() {
        return productSeasonLinkInformationStatusResponse;
    }

    /**
     * Sets the value of the productSeasonLinkInformationStatusResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductSeasonLinkInformationStatusResponse }
     *     
     */
    public void setProductSeasonLinkInformationStatusResponse(ProductSeasonLinkInformationStatusResponse value) {
        this.productSeasonLinkInformationStatusResponse = value;
    }

    /**
     * Gets the value of the colorwaySeasonLinkInformationStatusResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ColorwaySeasonLinkInformationStatusResponse }
     *     
     */
    public ColorwaySeasonLinkInformationStatusResponse getColorwaySeasonLinkInformationStatusResponse() {
        return colorwaySeasonLinkInformationStatusResponse;
    }

    /**
     * Sets the value of the colorwaySeasonLinkInformationStatusResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorwaySeasonLinkInformationStatusResponse }
     *     
     */
    public void setColorwaySeasonLinkInformationStatusResponse(ColorwaySeasonLinkInformationStatusResponse value) {
        this.colorwaySeasonLinkInformationStatusResponse = value;
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

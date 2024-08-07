
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ColorwaySeasonLinkInformationUpdatesRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorwaySeasonLinkInformationUpdatesRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="colorwaySeasonLink" type="{http://www.sportmaster.ru/plmproduct}ColorwaySeasonLink"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorwaySeasonLinkInformationUpdatesRequest", propOrder = {
    "requestId",
    "colorwaySeasonLink"
})
@XmlRootElement(name = "ColorwaySeasonLinkInformationUpdatesRequest")
public class ColorwaySeasonLinkInformationUpdatesRequest implements Serializable {

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 6301571730239334004L;
	protected int requestId;
    @XmlElement(required = true)
    protected ColorwaySeasonLink colorwaySeasonLink;

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
     * Gets the value of the colorwaySeasonLink property.
     * 
     * @return
     *     possible object is
     *     {@link ColorwaySeasonLink }
     *     
     */
    public ColorwaySeasonLink getColorwaySeasonLink() {
        return colorwaySeasonLink;
    }

    /**
     * Sets the value of the colorwaySeasonLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColorwaySeasonLink }
     *     
     */
    public void setColorwaySeasonLink(ColorwaySeasonLink value) {
        this.colorwaySeasonLink = value;
    }

}

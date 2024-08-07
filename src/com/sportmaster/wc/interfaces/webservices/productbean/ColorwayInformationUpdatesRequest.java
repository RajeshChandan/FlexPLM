
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ColorwayInformationUpdatesRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorwayInformationUpdatesRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="colorway" type="{http://www.sportmaster.ru/plmproduct}Colorway"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorwayInformationUpdatesRequest", propOrder = {
    "requestId",
    "colorway"
})
@XmlRootElement(name ="ColorwayInformationUpdatesRequest")
public class ColorwayInformationUpdatesRequest implements Serializable {

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2392467987479284770L;
	protected int requestId;
    @XmlElement(required = true)
    protected Colorway colorway;

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
     * Gets the value of the colorway property.
     * 
     * @return
     *     possible object is
     *     {@link Colorway }
     *     
     */
    public Colorway getColorway() {
        return colorway;
    }

    /**
     * Sets the value of the colorway property.
     * 
     * @param value
     *     allowed object is
     *     {@link Colorway }
     *     
     */
    public void setColorway(Colorway value) {
        this.colorway = value;
    }

}

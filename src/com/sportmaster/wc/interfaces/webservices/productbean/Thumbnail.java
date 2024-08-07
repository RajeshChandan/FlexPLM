
package com.sportmaster.wc.interfaces.webservices.productbean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Thumbnail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Thumbnail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="thumbnailURL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="thumbnailHash" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Thumbnail", propOrder = {
    "thumbnailURL",
    "thumbnailHash"
})
public class Thumbnail {

    @XmlElement(required = true)
    protected String thumbnailURL;
    @XmlElement(required = true)
    protected String thumbnailHash;

    /**
     * Gets the value of the thumbnailURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    /**
     * Sets the value of the thumbnailURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThumbnailURL(String value) {
        this.thumbnailURL = value;
    }

    /**
     * Gets the value of the thumbnailHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThumbnailHash() {
        return thumbnailHash;
    }

    /**
     * Sets the value of the thumbnailHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThumbnailHash(String value) {
        this.thumbnailHash = value;
    }

}

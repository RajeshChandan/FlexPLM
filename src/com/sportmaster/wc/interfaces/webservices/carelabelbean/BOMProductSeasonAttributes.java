
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BOMProductSeasonAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BOMProductSeasonAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="productSeasonMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="productSeasonPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="seasonMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="seasonName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smProductTechnologist" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smProductionGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BOMProductSeasonAttributes", propOrder = {
    "productSeasonMDMId",
    "productSeasonPLMId",
    "seasonMDMId",
    "seasonName",
    "smProductTechnologist",
    "smProductionGroup"
})
public class BOMProductSeasonAttributes {

    @XmlElement(required = true)
    protected String productSeasonMDMId;
    @XmlElement(required = true)
    protected String productSeasonPLMId;
    @XmlElement(required = true)
    protected String seasonMDMId;
    @XmlElement(required = true)
    protected String seasonName;
    protected String smProductTechnologist;
    protected String smProductionGroup;

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
     * Gets the value of the seasonMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeasonMDMId() {
        return seasonMDMId;
    }

    /**
     * Sets the value of the seasonMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeasonMDMId(String value) {
        this.seasonMDMId = value;
    }

    /**
     * Gets the value of the seasonName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeasonName() {
        return seasonName;
    }

    /**
     * Sets the value of the seasonName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeasonName(String value) {
        this.seasonName = value;
    }

    /**
     * Gets the value of the smProductTechnologist property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmProductTechnologist() {
        return smProductTechnologist;
    }

    /**
     * Sets the value of the smProductTechnologist property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmProductTechnologist(String value) {
        this.smProductTechnologist = value;
    }

    /**
     * Gets the value of the smProductionGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmProductionGroup() {
        return smProductionGroup;
    }

    /**
     * Sets the value of the smProductionGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmProductionGroup(String value) {
        this.smProductionGroup = value;
    }

}


package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BOMProductAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BOMProductAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="productMDMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="productPLMId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="productName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vrdStyleNum" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vrdBrand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smAge" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smProject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BOMProductAttributes", propOrder = {
    "productMDMId",
    "productPLMId",
    "productName",
    "vrdStyleNum",
    "vrdBrand",
    "smAge",
    "smProject"
})
public class BOMProductAttributes {

    @XmlElement(required = true)
    protected String productMDMId;
    @XmlElement(required = true)
    protected String productPLMId;
    @XmlElement(required = true)
    protected String productName;
    @XmlElement(required = true)
    protected String vrdStyleNum;
    @XmlElement(required = true)
    protected String vrdBrand;
    @XmlElement(required = true)
    protected String smAge;
    protected String smProject;

    /**
     * Gets the value of the productMDMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductMDMId() {
        return productMDMId;
    }

    /**
     * Sets the value of the productMDMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductMDMId(String value) {
        this.productMDMId = value;
    }

    /**
     * Gets the value of the productPLMId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductPLMId() {
        return productPLMId;
    }

    /**
     * Sets the value of the productPLMId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductPLMId(String value) {
        this.productPLMId = value;
    }

    /**
     * Gets the value of the productName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the value of the productName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductName(String value) {
        this.productName = value;
    }

    /**
     * Gets the value of the vrdStyleNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdStyleNum() {
        return vrdStyleNum;
    }

    /**
     * Sets the value of the vrdStyleNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdStyleNum(String value) {
        this.vrdStyleNum = value;
    }

    /**
     * Gets the value of the vrdBrand property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdBrand() {
        return vrdBrand;
    }

    /**
     * Sets the value of the vrdBrand property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdBrand(String value) {
        this.vrdBrand = value;
    }

    /**
     * Gets the value of the smAge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmAge() {
        return smAge;
    }

    /**
     * Sets the value of the smAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmAge(String value) {
        this.smAge = value;
    }

    /**
     * Gets the value of the smProject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmProject() {
        return smProject;
    }

    /**
     * Sets the value of the smProject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmProject(String value) {
        this.smProject = value;
    }

}

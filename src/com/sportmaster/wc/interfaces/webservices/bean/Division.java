
package com.sportmaster.wc.interfaces.webservices.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Division complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Division">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="mdmBO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mdmGom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mdmCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mdmSubCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mdmClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mdmSubClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestType" type="{http://www.sportmaster.ru/plmservice}RequestType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Division", propOrder = {

})
public class Division {

    @XmlElement(required = true)
    protected String mdmBO;
    protected String mdmGom;
    protected String mdmCategory;
    protected String mdmSubCategory;
    protected String mdmClass;
    protected String mdmSubClass;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected RequestType requestType;

    /**
     * Gets the value of the mdmBO property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdmBO() {
        return mdmBO;
    }

    /**
     * Sets the value of the mdmBO property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdmBO(String value) {
        this.mdmBO = value;
    }

    /**
     * Gets the value of the mdmGom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdmGom() {
        return mdmGom;
    }

    /**
     * Sets the value of the mdmGom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdmGom(String value) {
        this.mdmGom = value;
    }

    /**
     * Gets the value of the mdmCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdmCategory() {
        return mdmCategory;
    }

    /**
     * Sets the value of the mdmCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdmCategory(String value) {
        this.mdmCategory = value;
    }

    /**
     * Gets the value of the mdmSubCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdmSubCategory() {
        return mdmSubCategory;
    }

    /**
     * Sets the value of the mdmSubCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdmSubCategory(String value) {
        this.mdmSubCategory = value;
    }

    /**
     * Gets the value of the mdmClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdmClass() {
        return mdmClass;
    }

    /**
     * Sets the value of the mdmClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdmClass(String value) {
        this.mdmClass = value;
    }

    /**
     * Gets the value of the mdmSubClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdmSubClass() {
        return mdmSubClass;
    }

    /**
     * Sets the value of the mdmSubClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdmSubClass(String value) {
        this.mdmSubClass = value;
    }

    /**
     * Gets the value of the requestType property.
     * 
     * @return
     *     possible object is
     *     {@link RequestType }
     *     
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestType }
     *     
     */
    public void setRequestType(RequestType value) {
        this.requestType = value;
    }

}

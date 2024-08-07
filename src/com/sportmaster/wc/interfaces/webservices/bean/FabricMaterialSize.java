
package com.sportmaster.wc.interfaces.webservices.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FabricMaterialSize complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FabricMaterialSize">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="finishing" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="treatment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FabricMaterialSize", propOrder = {
    "size",
    "finishing",
    "treatment"
})
public class FabricMaterialSize {

    protected String size;
    protected String finishing;
    protected String treatment;

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSize(String value) {
        this.size = value;
    }

    /**
     * Gets the value of the finishing property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinishing() {
        return finishing;
    }

    /**
     * Sets the value of the finishing property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinishing(String value) {
        this.finishing = value;
    }

    /**
     * Gets the value of the treatment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTreatment() {
        return treatment;
    }

    /**
     * Sets the value of the treatment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTreatment(String value) {
        this.treatment = value;
    }

}

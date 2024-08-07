
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductBOMComponentDestination complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductBOMComponentDestination">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="destination" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="primary" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="altPrimary" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="consumption" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="contrastColorCombination" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="colorBOMComponent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="colorwayColorBOMComponent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BOMMaterialAttributes" type="{http://www.sportmaster.ru/plm-services-careLabel}BOMMaterialAttributes"/>
 *         &lt;element name="BOMLinkBranchID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductBOMComponentDestination", propOrder = {
    "destination",
    "primary",
    "altPrimary",
    "consumption",
    "contrastColorCombination",
    "colorBOMComponent",
    "colorwayColorBOMComponent",
    "bomMaterialAttributes",
    "bomLinkBranchID"
})
public class ProductBOMComponentDestination {

    @XmlElement(required = true, nillable = true)
    protected String destination;
    protected boolean primary;
    protected boolean altPrimary;
    protected BigDecimal consumption;
    protected boolean contrastColorCombination;
    protected String colorBOMComponent;
    protected String colorwayColorBOMComponent;
    @XmlElement(name = "BOMMaterialAttributes", required = true)
    protected BOMMaterialAttributes bomMaterialAttributes;
    @XmlElement(name = "BOMLinkBranchID", required = true)
    protected String bomLinkBranchID;

    /**
     * Gets the value of the destination property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the value of the destination property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestination(String value) {
        this.destination = value;
    }

    /**
     * Gets the value of the primary property.
     * 
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * Sets the value of the primary property.
     * 
     */
    public void setPrimary(boolean value) {
        this.primary = value;
    }

    /**
     * Gets the value of the altPrimary property.
     * 
     */
    public boolean isAltPrimary() {
        return altPrimary;
    }

    /**
     * Sets the value of the altPrimary property.
     * 
     */
    public void setAltPrimary(boolean value) {
        this.altPrimary = value;
    }

    /**
     * Gets the value of the consumption property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getConsumption() {
        return consumption;
    }

    /**
     * Sets the value of the consumption property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setConsumption(BigDecimal value) {
        this.consumption = value;
    }

    /**
     * Gets the value of the contrastColorCombination property.
     * 
     */
    public boolean isContrastColorCombination() {
        return contrastColorCombination;
    }

    /**
     * Sets the value of the contrastColorCombination property.
     * 
     */
    public void setContrastColorCombination(boolean value) {
        this.contrastColorCombination = value;
    }

    /**
     * Gets the value of the colorBOMComponent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorBOMComponent() {
        return colorBOMComponent;
    }

    /**
     * Sets the value of the colorBOMComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorBOMComponent(String value) {
        this.colorBOMComponent = value;
    }

    /**
     * Gets the value of the colorwayColorBOMComponent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorwayColorBOMComponent() {
        return colorwayColorBOMComponent;
    }

    /**
     * Sets the value of the colorwayColorBOMComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorwayColorBOMComponent(String value) {
        this.colorwayColorBOMComponent = value;
    }

    /**
     * Gets the value of the bomMaterialAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link BOMMaterialAttributes }
     *     
     */
    public BOMMaterialAttributes getBOMMaterialAttributes() {
        return bomMaterialAttributes;
    }

    /**
     * Sets the value of the bomMaterialAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BOMMaterialAttributes }
     *     
     */
	public void setBOMMaterialAttributes(BOMMaterialAttributes value) {
        this.bomMaterialAttributes = value;
    }

    /**
     * Gets the value of the bomLinkBranchID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBOMLinkBranchID() {
        return bomLinkBranchID;
    }

    /**
     * Sets the value of the bomLinkBranchID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBOMLinkBranchID(String value) {
        this.bomLinkBranchID = value;
    }

}

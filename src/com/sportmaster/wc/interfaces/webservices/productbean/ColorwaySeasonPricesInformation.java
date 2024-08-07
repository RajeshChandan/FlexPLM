
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ColorwaySeasonPricesInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorwaySeasonPricesInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plannedMUP" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="RRPGrossRUB" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorwaySeasonPricesInformation", propOrder = {
    "mdmId",
    "plmId",
    "plannedMUP",
    "rrpGrossRUB"
})
public class ColorwaySeasonPricesInformation {

    @XmlElement(required = true)
    protected String mdmId;
    @XmlElement(required = true)
    protected String plmId;
    protected BigDecimal plannedMUP;
    @XmlElement(name = "RRPGrossRUB")
    protected BigDecimal rrpGrossRUB;

    /**
     * Gets the value of the mdmId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdmId() {
        return mdmId;
    }

    /**
     * Sets the value of the mdmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdmId(String value) {
        this.mdmId = value;
    }

    /**
     * Gets the value of the plmId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlmId() {
        return plmId;
    }

    /**
     * Sets the value of the plmId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlmId(String value) {
        this.plmId = value;
    }

    /**
     * Gets the value of the plannedMUP property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPlannedMUP() {
        return plannedMUP;
    }

    /**
     * Sets the value of the plannedMUP property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPlannedMUP(BigDecimal value) {
        this.plannedMUP = value;
    }

    /**
     * Gets the value of the rrpGrossRUB property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRRPGrossRUB() {
        return rrpGrossRUB;
    }

    /**
     * Sets the value of the rrpGrossRUB property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRRPGrossRUB(BigDecimal value) {
        this.rrpGrossRUB = value;
    }

}

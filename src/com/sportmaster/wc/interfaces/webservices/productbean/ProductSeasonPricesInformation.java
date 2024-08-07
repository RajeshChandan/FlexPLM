
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductSeasonPricesInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductSeasonPricesInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="iterationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="targetMUp" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="targetPP" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="RRPru" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="TargetPurchasePrice" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="TargetPPCurrency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProductionRegion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="APRRPGrossRUB" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="colorwaySeasonPricesInformation" type="{http://www.sportmaster.ru/plmproduct}ColorwaySeasonPricesInformation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductSeasonPricesInformation", propOrder = {
    "mdmId",
    "plmId",
    "iterationId",
    "targetMUp",
    "targetPP",
    "rrPru",
    "targetPurchasePrice",
    "targetPPCurrency",
    "productionRegion",
    "aprrpGrossRUB",
    "colorwaySeasonPricesInformation"
})
public class ProductSeasonPricesInformation {

    @XmlElement(required = true)
    protected String mdmId;
    @XmlElement(required = true)
    protected String plmId;
    @XmlElement(required = true)
    protected String iterationId;
    protected BigDecimal targetMUp;
    protected BigDecimal targetPP;
    @XmlElement(name = "RRPru")
    protected BigDecimal rrPru;
    @XmlElement(name = "TargetPurchasePrice")
    protected BigDecimal targetPurchasePrice;
    @XmlElement(name = "TargetPPCurrency")
    protected String targetPPCurrency;
    @XmlElement(name = "ProductionRegion")
    protected String productionRegion;
    @XmlElement(name = "APRRPGrossRUB")
    protected BigDecimal aprrpGrossRUB;
    protected List<ColorwaySeasonPricesInformation> colorwaySeasonPricesInformation;

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
     * Gets the value of the iterationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIterationId() {
        return iterationId;
    }

    /**
     * Sets the value of the iterationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIterationId(String value) {
        this.iterationId = value;
    }

    /**
     * Gets the value of the targetMUp property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTargetMUp() {
        return targetMUp;
    }

    /**
     * Sets the value of the targetMUp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTargetMUp(BigDecimal value) {
        this.targetMUp = value;
    }

    /**
     * Gets the value of the targetPP property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTargetPP() {
        return targetPP;
    }

    /**
     * Sets the value of the targetPP property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTargetPP(BigDecimal value) {
        this.targetPP = value;
    }

    /**
     * Gets the value of the rrPru property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRRPru() {
        return rrPru;
    }

    /**
     * Sets the value of the rrPru property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRRPru(BigDecimal value) {
        this.rrPru = value;
    }

    /**
     * Gets the value of the targetPurchasePrice property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTargetPurchasePrice() {
        return targetPurchasePrice;
    }

    /**
     * Sets the value of the targetPurchasePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTargetPurchasePrice(BigDecimal value) {
        this.targetPurchasePrice = value;
    }

    /**
     * Gets the value of the targetPPCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetPPCurrency() {
        return targetPPCurrency;
    }

    /**
     * Sets the value of the targetPPCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetPPCurrency(String value) {
        this.targetPPCurrency = value;
    }

    /**
     * Gets the value of the productionRegion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductionRegion() {
        return productionRegion;
    }

    /**
     * Sets the value of the productionRegion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductionRegion(String value) {
        this.productionRegion = value;
    }

    /**
     * Gets the value of the aprrpGrossRUB property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAPRRPGrossRUB() {
        return aprrpGrossRUB;
    }

    /**
     * Sets the value of the aprrpGrossRUB property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAPRRPGrossRUB(BigDecimal value) {
        this.aprrpGrossRUB = value;
    }

    /**
     * Gets the value of the colorwaySeasonPricesInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorwaySeasonPricesInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorwaySeasonPricesInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColorwaySeasonPricesInformation }
     * 
     * 
     */
    public List<ColorwaySeasonPricesInformation> getColorwaySeasonPricesInformation() {
        if (colorwaySeasonPricesInformation == null) {
            colorwaySeasonPricesInformation = new ArrayList<ColorwaySeasonPricesInformation>();
        }
        return this.colorwaySeasonPricesInformation;
    }

}

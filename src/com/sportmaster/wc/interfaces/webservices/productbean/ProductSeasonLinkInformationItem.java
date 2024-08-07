
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ProductSeasonLinkInformationItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductSeasonLinkInformationItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="iterationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smIntakeDateStyleRussia" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="smIntakeDateStyleChina" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsStyle1stRUonHold" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsStyle1stUAonHold" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsStyle1stCHonHold" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsStyle1stRU" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsStyle1stUA" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsStyle1stCH" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsStyle2ndRU" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsStyle2ndUA" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsStyle2ndCH" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smBulkOrderUnitsStyleRU" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smBulkOrderUnitsStyleUA" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smBulkOrderUnitsStyleCH" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductSeasonLinkInformationItem", propOrder = {
    "mdmId",
    "plmId",
    "iterationId",
    "smIntakeDateStyleRussia",
    "smIntakeDateStyleChina",
    "smForecastUnitsStyle1StRUonHold",
    "smForecastUnitsStyle1StUAonHold",
    "smForecastUnitsStyle1StCHonHold",
    "smForecastUnitsStyle1StRU",
    "smForecastUnitsStyle1StUA",
    "smForecastUnitsStyle1StCH",
    "smForecastUnitsStyle2NdRU",
    "smForecastUnitsStyle2NdUA",
    "smForecastUnitsStyle2NdCH",
    "smBulkOrderUnitsStyleRU",
    "smBulkOrderUnitsStyleUA",
    "smBulkOrderUnitsStyleCH"
})
@XmlRootElement(name = "ProductSeasonLinkInformationItem")
public class ProductSeasonLinkInformationItem {

    @XmlElement(required = true)
    protected String mdmId;
    @XmlElement(required = true)
    protected String plmId;
    @XmlElement(required = true)
    protected String iterationId;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar smIntakeDateStyleRussia;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar smIntakeDateStyleChina;
    @XmlElement(name = "smForecastUnitsStyle1stRUonHold")
    protected BigInteger smForecastUnitsStyle1StRUonHold;
    @XmlElement(name = "smForecastUnitsStyle1stUAonHold")
    protected BigInteger smForecastUnitsStyle1StUAonHold;
    @XmlElement(name = "smForecastUnitsStyle1stCHonHold")
    protected BigInteger smForecastUnitsStyle1StCHonHold;
    @XmlElement(name = "smForecastUnitsStyle1stRU")
    protected BigInteger smForecastUnitsStyle1StRU;
    @XmlElement(name = "smForecastUnitsStyle1stUA")
    protected BigInteger smForecastUnitsStyle1StUA;
    @XmlElement(name = "smForecastUnitsStyle1stCH")
    protected BigInteger smForecastUnitsStyle1StCH;
    @XmlElement(name = "smForecastUnitsStyle2ndRU")
    protected BigInteger smForecastUnitsStyle2NdRU;
    @XmlElement(name = "smForecastUnitsStyle2ndUA")
    protected BigInteger smForecastUnitsStyle2NdUA;
    @XmlElement(name = "smForecastUnitsStyle2ndCH")
    protected BigInteger smForecastUnitsStyle2NdCH;
    protected BigInteger smBulkOrderUnitsStyleRU;
    protected BigInteger smBulkOrderUnitsStyleUA;
    protected BigInteger smBulkOrderUnitsStyleCH;

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
     * Gets the value of the smIntakeDateStyleRussia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSmIntakeDateStyleRussia() {
        return smIntakeDateStyleRussia;
    }

    /**
     * Sets the value of the smIntakeDateStyleRussia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSmIntakeDateStyleRussia(XMLGregorianCalendar value) {
        this.smIntakeDateStyleRussia = value;
    }

    /**
     * Gets the value of the smIntakeDateStyleChina property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSmIntakeDateStyleChina() {
        return smIntakeDateStyleChina;
    }

    /**
     * Sets the value of the smIntakeDateStyleChina property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSmIntakeDateStyleChina(XMLGregorianCalendar value) {
        this.smIntakeDateStyleChina = value;
    }

    /**
     * Gets the value of the smForecastUnitsStyle1StRUonHold property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsStyle1StRUonHold() {
        return smForecastUnitsStyle1StRUonHold;
    }

    /**
     * Sets the value of the smForecastUnitsStyle1StRUonHold property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsStyle1StRUonHold(BigInteger value) {
        this.smForecastUnitsStyle1StRUonHold = value;
    }

    /**
     * Gets the value of the smForecastUnitsStyle1StUAonHold property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsStyle1StUAonHold() {
        return smForecastUnitsStyle1StUAonHold;
    }

    /**
     * Sets the value of the smForecastUnitsStyle1StUAonHold property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsStyle1StUAonHold(BigInteger value) {
        this.smForecastUnitsStyle1StUAonHold = value;
    }

    /**
     * Gets the value of the smForecastUnitsStyle1StCHonHold property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsStyle1StCHonHold() {
        return smForecastUnitsStyle1StCHonHold;
    }

    /**
     * Sets the value of the smForecastUnitsStyle1StCHonHold property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsStyle1StCHonHold(BigInteger value) {
        this.smForecastUnitsStyle1StCHonHold = value;
    }

    /**
     * Gets the value of the smForecastUnitsStyle1StRU property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsStyle1StRU() {
        return smForecastUnitsStyle1StRU;
    }

    /**
     * Sets the value of the smForecastUnitsStyle1StRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsStyle1StRU(BigInteger value) {
        this.smForecastUnitsStyle1StRU = value;
    }

    /**
     * Gets the value of the smForecastUnitsStyle1StUA property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsStyle1StUA() {
        return smForecastUnitsStyle1StUA;
    }

    /**
     * Sets the value of the smForecastUnitsStyle1StUA property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsStyle1StUA(BigInteger value) {
        this.smForecastUnitsStyle1StUA = value;
    }

    /**
     * Gets the value of the smForecastUnitsStyle1StCH property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsStyle1StCH() {
        return smForecastUnitsStyle1StCH;
    }

    /**
     * Sets the value of the smForecastUnitsStyle1StCH property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsStyle1StCH(BigInteger value) {
        this.smForecastUnitsStyle1StCH = value;
    }

    /**
     * Gets the value of the smForecastUnitsStyle2NdRU property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsStyle2NdRU() {
        return smForecastUnitsStyle2NdRU;
    }

    /**
     * Sets the value of the smForecastUnitsStyle2NdRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsStyle2NdRU(BigInteger value) {
        this.smForecastUnitsStyle2NdRU = value;
    }

    /**
     * Gets the value of the smForecastUnitsStyle2NdUA property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsStyle2NdUA() {
        return smForecastUnitsStyle2NdUA;
    }

    /**
     * Sets the value of the smForecastUnitsStyle2NdUA property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsStyle2NdUA(BigInteger value) {
        this.smForecastUnitsStyle2NdUA = value;
    }

    /**
     * Gets the value of the smForecastUnitsStyle2NdCH property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsStyle2NdCH() {
        return smForecastUnitsStyle2NdCH;
    }

    /**
     * Sets the value of the smForecastUnitsStyle2NdCH property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsStyle2NdCH(BigInteger value) {
        this.smForecastUnitsStyle2NdCH = value;
    }

    /**
     * Gets the value of the smBulkOrderUnitsStyleRU property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmBulkOrderUnitsStyleRU() {
        return smBulkOrderUnitsStyleRU;
    }

    /**
     * Sets the value of the smBulkOrderUnitsStyleRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmBulkOrderUnitsStyleRU(BigInteger value) {
        this.smBulkOrderUnitsStyleRU = value;
    }

    /**
     * Gets the value of the smBulkOrderUnitsStyleUA property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmBulkOrderUnitsStyleUA() {
        return smBulkOrderUnitsStyleUA;
    }

    /**
     * Sets the value of the smBulkOrderUnitsStyleUA property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmBulkOrderUnitsStyleUA(BigInteger value) {
        this.smBulkOrderUnitsStyleUA = value;
    }

    /**
     * Gets the value of the smBulkOrderUnitsStyleCH property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmBulkOrderUnitsStyleCH() {
        return smBulkOrderUnitsStyleCH;
    }

    /**
     * Sets the value of the smBulkOrderUnitsStyleCH property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmBulkOrderUnitsStyleCH(BigInteger value) {
        this.smBulkOrderUnitsStyleCH = value;
    }

}

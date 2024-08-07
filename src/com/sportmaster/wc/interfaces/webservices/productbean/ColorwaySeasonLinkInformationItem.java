
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
 * <p>Java class for ColorwaySeasonLinkInformationItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorwaySeasonLinkInformationItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mdmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="iterationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smIntakeDateColorwayRussia" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="smIntakeDateColorwayChina" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsColorway1stRUonHold" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsColorway1stUAonHold" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsColorway1stCHonHold" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsColorway1stRU" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsColorway1stUA" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsColorway1stCH" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsColorway2ndRU" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsColorway2ndUA" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smForecastUnitsColorway2ndCH" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smBulkOrderUnitsColorwayRU" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smBulkOrderUnitsColorwayUA" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="smBulkOrderUnitsColorwayCH" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorwaySeasonLinkInformationItem", propOrder = {
    "mdmId",
    "plmId",
    "iterationId",
    "smIntakeDateColorwayRussia",
    "smIntakeDateColorwayChina",
    "smForecastUnitsColorway1StRUonHold",
    "smForecastUnitsColorway1StUAonHold",
    "smForecastUnitsColorway1StCHonHold",
    "smForecastUnitsColorway1StRU",
    "smForecastUnitsColorway1StUA",
    "smForecastUnitsColorway1StCH",
    "smForecastUnitsColorway2NdRU",
    "smForecastUnitsColorway2NdUA",
    "smForecastUnitsColorway2NdCH",
    "smBulkOrderUnitsColorwayRU",
    "smBulkOrderUnitsColorwayUA",
    "smBulkOrderUnitsColorwayCH"
})
@XmlRootElement(name = "ColorwaySeasonLinkInformationItem")
public class ColorwaySeasonLinkInformationItem {

    @XmlElement(required = true)
    protected String mdmId;
    @XmlElement(required = true)
    protected String plmId;
    @XmlElement(required = true)
    protected String iterationId;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar smIntakeDateColorwayRussia;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar smIntakeDateColorwayChina;
    @XmlElement(name = "smForecastUnitsColorway1stRUonHold")
    protected BigInteger smForecastUnitsColorway1StRUonHold;
    @XmlElement(name = "smForecastUnitsColorway1stUAonHold")
    protected BigInteger smForecastUnitsColorway1StUAonHold;
    @XmlElement(name = "smForecastUnitsColorway1stCHonHold")
    protected BigInteger smForecastUnitsColorway1StCHonHold;
    @XmlElement(name = "smForecastUnitsColorway1stRU")
    protected BigInteger smForecastUnitsColorway1StRU;
    @XmlElement(name = "smForecastUnitsColorway1stUA")
    protected BigInteger smForecastUnitsColorway1StUA;
    @XmlElement(name = "smForecastUnitsColorway1stCH")
    protected BigInteger smForecastUnitsColorway1StCH;
    @XmlElement(name = "smForecastUnitsColorway2ndRU")
    protected BigInteger smForecastUnitsColorway2NdRU;
    @XmlElement(name = "smForecastUnitsColorway2ndUA")
    protected BigInteger smForecastUnitsColorway2NdUA;
    @XmlElement(name = "smForecastUnitsColorway2ndCH")
    protected BigInteger smForecastUnitsColorway2NdCH;
    protected BigInteger smBulkOrderUnitsColorwayRU;
    protected BigInteger smBulkOrderUnitsColorwayUA;
    protected BigInteger smBulkOrderUnitsColorwayCH;

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
     * Gets the value of the smIntakeDateColorwayRussia property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSmIntakeDateColorwayRussia() {
        return smIntakeDateColorwayRussia;
    }

    /**
     * Sets the value of the smIntakeDateColorwayRussia property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSmIntakeDateColorwayRussia(XMLGregorianCalendar value) {
        this.smIntakeDateColorwayRussia = value;
    }

    /**
     * Gets the value of the smIntakeDateColorwayChina property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSmIntakeDateColorwayChina() {
        return smIntakeDateColorwayChina;
    }

    /**
     * Sets the value of the smIntakeDateColorwayChina property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSmIntakeDateColorwayChina(XMLGregorianCalendar value) {
        this.smIntakeDateColorwayChina = value;
    }

    /**
     * Gets the value of the smForecastUnitsColorway1StRUonHold property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsColorway1StRUonHold() {
        return smForecastUnitsColorway1StRUonHold;
    }

    /**
     * Sets the value of the smForecastUnitsColorway1StRUonHold property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsColorway1StRUonHold(BigInteger value) {
        this.smForecastUnitsColorway1StRUonHold = value;
    }

    /**
     * Gets the value of the smForecastUnitsColorway1StUAonHold property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsColorway1StUAonHold() {
        return smForecastUnitsColorway1StUAonHold;
    }

    /**
     * Sets the value of the smForecastUnitsColorway1StUAonHold property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsColorway1StUAonHold(BigInteger value) {
        this.smForecastUnitsColorway1StUAonHold = value;
    }

    /**
     * Gets the value of the smForecastUnitsColorway1StCHonHold property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsColorway1StCHonHold() {
        return smForecastUnitsColorway1StCHonHold;
    }

    /**
     * Sets the value of the smForecastUnitsColorway1StCHonHold property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsColorway1StCHonHold(BigInteger value) {
        this.smForecastUnitsColorway1StCHonHold = value;
    }

    /**
     * Gets the value of the smForecastUnitsColorway1StRU property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsColorway1StRU() {
        return smForecastUnitsColorway1StRU;
    }

    /**
     * Sets the value of the smForecastUnitsColorway1StRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsColorway1StRU(BigInteger value) {
        this.smForecastUnitsColorway1StRU = value;
    }

    /**
     * Gets the value of the smForecastUnitsColorway1StUA property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsColorway1StUA() {
        return smForecastUnitsColorway1StUA;
    }

    /**
     * Sets the value of the smForecastUnitsColorway1StUA property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsColorway1StUA(BigInteger value) {
        this.smForecastUnitsColorway1StUA = value;
    }

    /**
     * Gets the value of the smForecastUnitsColorway1StCH property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsColorway1StCH() {
        return smForecastUnitsColorway1StCH;
    }

    /**
     * Sets the value of the smForecastUnitsColorway1StCH property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsColorway1StCH(BigInteger value) {
        this.smForecastUnitsColorway1StCH = value;
    }

    /**
     * Gets the value of the smForecastUnitsColorway2NdRU property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsColorway2NdRU() {
        return smForecastUnitsColorway2NdRU;
    }

    /**
     * Sets the value of the smForecastUnitsColorway2NdRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsColorway2NdRU(BigInteger value) {
        this.smForecastUnitsColorway2NdRU = value;
    }

    /**
     * Gets the value of the smForecastUnitsColorway2NdUA property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsColorway2NdUA() {
        return smForecastUnitsColorway2NdUA;
    }

    /**
     * Sets the value of the smForecastUnitsColorway2NdUA property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsColorway2NdUA(BigInteger value) {
        this.smForecastUnitsColorway2NdUA = value;
    }

    /**
     * Gets the value of the smForecastUnitsColorway2NdCH property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmForecastUnitsColorway2NdCH() {
        return smForecastUnitsColorway2NdCH;
    }

    /**
     * Sets the value of the smForecastUnitsColorway2NdCH property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmForecastUnitsColorway2NdCH(BigInteger value) {
        this.smForecastUnitsColorway2NdCH = value;
    }

    /**
     * Gets the value of the smBulkOrderUnitsColorwayRU property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmBulkOrderUnitsColorwayRU() {
        return smBulkOrderUnitsColorwayRU;
    }

    /**
     * Sets the value of the smBulkOrderUnitsColorwayRU property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmBulkOrderUnitsColorwayRU(BigInteger value) {
        this.smBulkOrderUnitsColorwayRU = value;
    }

    /**
     * Gets the value of the smBulkOrderUnitsColorwayUA property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmBulkOrderUnitsColorwayUA() {
        return smBulkOrderUnitsColorwayUA;
    }

    /**
     * Sets the value of the smBulkOrderUnitsColorwayUA property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmBulkOrderUnitsColorwayUA(BigInteger value) {
        this.smBulkOrderUnitsColorwayUA = value;
    }

    /**
     * Gets the value of the smBulkOrderUnitsColorwayCH property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSmBulkOrderUnitsColorwayCH() {
        return smBulkOrderUnitsColorwayCH;
    }

    /**
     * Sets the value of the smBulkOrderUnitsColorwayCH property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSmBulkOrderUnitsColorwayCH(BigInteger value) {
        this.smBulkOrderUnitsColorwayCH = value;
    }

}

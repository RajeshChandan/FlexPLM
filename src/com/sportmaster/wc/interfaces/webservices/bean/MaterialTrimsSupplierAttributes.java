
package com.sportmaster.wc.interfaces.webservices.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for MaterialTrimsSupplierAttributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MaterialTrimsSupplierAttributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="smLeadTimeGeneral" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="nominated" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smMDMVENDOR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lastUpdated" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastUpdatedBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vrdOrderMinimum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smSupplierName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="trimMaterialSize" type="{http://www.sportmaster.ru/plmservice}TrimMaterialSize" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="referenceNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="managingDepartment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MaterialTrimsSupplierAttributes", propOrder = {
    "smLeadTimeGeneral",
    "nominated",
    "smMDMVENDOR",
    "lastUpdated",
    "lastUpdatedBy",
    "vrdOrderMinimum",
    "smSupplierName",
    "trimMaterialSize",
    "referenceNumber",
    "status",
    "managingDepartment"
})
public class MaterialTrimsSupplierAttributes {

    protected Long smLeadTimeGeneral;
    protected String nominated;
    @XmlElement(required = true)
    protected String smMDMVENDOR;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdated;
    @XmlElement(required = true)
    protected String lastUpdatedBy;
    protected String vrdOrderMinimum;
    @XmlElement(required = true)
    protected String smSupplierName;
    protected List<TrimMaterialSize> trimMaterialSize;
    protected String referenceNumber;
    @XmlElement(required = true)
    protected String status;
    protected String managingDepartment;

    /**
     * Gets the value of the smLeadTimeGeneral property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getSmLeadTimeGeneral() {
        return smLeadTimeGeneral;
    }

    /**
     * Sets the value of the smLeadTimeGeneral property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setSmLeadTimeGeneral(Long value) {
        this.smLeadTimeGeneral = value;
    }

    /**
     * Gets the value of the nominated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNominated() {
        return nominated;
    }

    /**
     * Sets the value of the nominated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNominated(String value) {
        this.nominated = value;
    }

    /**
     * Gets the value of the smMDMVENDOR property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmMDMVENDOR() {
        return smMDMVENDOR;
    }

    /**
     * Sets the value of the smMDMVENDOR property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmMDMVENDOR(String value) {
        this.smMDMVENDOR = value;
    }

    /**
     * Gets the value of the lastUpdated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets the value of the lastUpdated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastUpdated(XMLGregorianCalendar value) {
        this.lastUpdated = value;
    }

    /**
     * Gets the value of the lastUpdatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets the value of the lastUpdatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastUpdatedBy(String value) {
        this.lastUpdatedBy = value;
    }

    /**
     * Gets the value of the vrdOrderMinimum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVrdOrderMinimum() {
        return vrdOrderMinimum;
    }

    /**
     * Sets the value of the vrdOrderMinimum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVrdOrderMinimum(String value) {
        this.vrdOrderMinimum = value;
    }

    /**
     * Gets the value of the smSupplierName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmSupplierName() {
        return smSupplierName;
    }

    /**
     * Sets the value of the smSupplierName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmSupplierName(String value) {
        this.smSupplierName = value;
    }

    /**
     * Gets the value of the trimMaterialSize property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trimMaterialSize property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrimMaterialSize().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrimMaterialSize }
     * 
     * 
     */
    public List<TrimMaterialSize> getTrimMaterialSize() {
        if (trimMaterialSize == null) {
            trimMaterialSize = new ArrayList<TrimMaterialSize>();
        }
        return this.trimMaterialSize;
    }

    /**
     * Gets the value of the referenceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * Sets the value of the referenceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceNumber(String value) {
        this.referenceNumber = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the managingDepartment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManagingDepartment() {
        return managingDepartment;
    }

    /**
     * Sets the value of the managingDepartment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManagingDepartment(String value) {
        this.managingDepartment = value;
    }

}

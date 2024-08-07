
package com.sportmaster.wc.interfaces.webservices.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StatusInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="name" type="{http://www.sportmaster.ru/plmservice}BOEnumeration"/>
 *         &lt;element name="updateInformation" type="{http://www.sportmaster.ru/plmservice}UpdateInformation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusInformation", propOrder = {
    "requestId",
    "name",
    "updateInformation"
})
public class StatusInformation {

    @XmlElement(name = "RequestId")
    protected int requestId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected BOEnumeration name;
    protected List<UpdateInformation> updateInformation;

    /**
     * Gets the value of the requestId property.
     * 
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     */
    public void setRequestId(int value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link BOEnumeration }
     *     
     */
    public BOEnumeration getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link BOEnumeration }
     *     
     */
    public void setName(BOEnumeration value) {
        this.name = value;
    }

    /**
     * Gets the value of the updateInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the updateInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUpdateInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UpdateInformation }
     * 
     * 
     */
    public List<UpdateInformation> getUpdateInformation() {
        if (updateInformation == null) {
            updateInformation = new ArrayList<UpdateInformation>();
        }
        return this.updateInformation;
    }

}

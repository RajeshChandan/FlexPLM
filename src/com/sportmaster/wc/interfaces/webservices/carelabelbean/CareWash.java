
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CareWash complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CareWash">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="careWashComponent" type="{http://www.sportmaster.ru/plm-services-careLabel}CareWashComponent" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CareWash", propOrder = {
    "careWashComponent"
})
public class CareWash {

    @XmlElement(required = true)
    protected List<CareWashComponent> careWashComponent;

    /**
     * Gets the value of the careWashComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the careWashComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCareWashComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CareWashComponent }
     * 
     * 
     */
    public List<CareWashComponent> getCareWashComponent() {
        if (careWashComponent == null) {
            careWashComponent = new ArrayList<CareWashComponent>();
        }
        return this.careWashComponent;
    }

}

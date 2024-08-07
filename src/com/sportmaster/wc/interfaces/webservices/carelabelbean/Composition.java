
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Composition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Composition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="compositionComponent" type="{http://www.sportmaster.ru/plm-services-careLabel}CompositionComponent" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Composition", propOrder = {
    "compositionComponent"
})
public class Composition {

    @XmlElement(required = true)
    protected List<CompositionComponent> compositionComponent;

    /**
     * Gets the value of the compositionComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the compositionComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompositionComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompositionComponent }
     * 
     * 
     */
    public List<CompositionComponent> getCompositionComponent() {
        if (compositionComponent == null) {
            compositionComponent = new ArrayList<CompositionComponent>();
        }
        return this.compositionComponent;
    }

}

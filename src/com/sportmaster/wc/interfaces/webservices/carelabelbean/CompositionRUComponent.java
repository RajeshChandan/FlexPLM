
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompositionRUComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompositionRUComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="compositionRUPercentage" type="{http://www.sportmaster.ru/plm-services-careLabel}percent"/>
 *         &lt;element name="compositionRUItem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompositionRUComponent", propOrder = {
    "compositionRUPercentage",
    "compositionRUItem"
})
public class CompositionRUComponent {

    @XmlElement(required = true)
    protected BigDecimal compositionRUPercentage;
    @XmlElement(required = true)
    protected String compositionRUItem;

    /**
     * Gets the value of the compositionRUPercentage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCompositionRUPercentage() {
        return compositionRUPercentage;
    }

    /**
     * Sets the value of the compositionRUPercentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCompositionRUPercentage(BigDecimal value) {
        this.compositionRUPercentage = value;
    }

    /**
     * Gets the value of the compositionRUItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompositionRUItem() {
        return compositionRUItem;
    }

    /**
     * Sets the value of the compositionRUItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompositionRUItem(String value) {
        this.compositionRUItem = value;
    }

}

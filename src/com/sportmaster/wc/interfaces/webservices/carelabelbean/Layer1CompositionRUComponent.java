
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Layer1CompositionRUComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Layer1CompositionRUComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layer1CompositionRUPercentage" type="{http://www.sportmaster.ru/plm-services-careLabel}percent"/>
 *         &lt;element name="layer1CompositionRUItem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Layer1CompositionRUComponent", propOrder = {
    "layer1CompositionRUPercentage",
    "layer1CompositionRUItem"
})
public class Layer1CompositionRUComponent {

    @XmlElement(required = true)
    protected BigDecimal layer1CompositionRUPercentage;
    @XmlElement(required = true)
    protected String layer1CompositionRUItem;

    /**
     * Gets the value of the layer1CompositionRUPercentage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLayer1CompositionRUPercentage() {
        return layer1CompositionRUPercentage;
    }

    /**
     * Sets the value of the layer1CompositionRUPercentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLayer1CompositionRUPercentage(BigDecimal value) {
        this.layer1CompositionRUPercentage = value;
    }

    /**
     * Gets the value of the layer1CompositionRUItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer1CompositionRUItem() {
        return layer1CompositionRUItem;
    }

    /**
     * Sets the value of the layer1CompositionRUItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer1CompositionRUItem(String value) {
        this.layer1CompositionRUItem = value;
    }

}

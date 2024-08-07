
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Layer1CompositionComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Layer1CompositionComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layer1CompositionPercentage" type="{http://www.sportmaster.ru/plm-services-careLabel}percent"/>
 *         &lt;element name="layer1CompositionItem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Layer1CompositionComponent", propOrder = {
    "layer1CompositionPercentage",
    "layer1CompositionItem"
})
public class Layer1CompositionComponent {

    @XmlElement(required = true)
    protected BigDecimal layer1CompositionPercentage;
    @XmlElement(required = true)
    protected String layer1CompositionItem;

    /**
     * Gets the value of the layer1CompositionPercentage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLayer1CompositionPercentage() {
        return layer1CompositionPercentage;
    }

    /**
     * Sets the value of the layer1CompositionPercentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLayer1CompositionPercentage(BigDecimal value) {
        this.layer1CompositionPercentage = value;
    }

    /**
     * Gets the value of the layer1CompositionItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayer1CompositionItem() {
        return layer1CompositionItem;
    }

    /**
     * Sets the value of the layer1CompositionItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayer1CompositionItem(String value) {
        this.layer1CompositionItem = value;
    }

}

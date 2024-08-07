
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CompositionComponent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CompositionComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="compositionPercentage" type="{http://www.sportmaster.ru/plm-services-careLabel}percent"/>
 *         &lt;element name="compositionItem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompositionComponent", propOrder = {
    "compositionPercentage",
    "compositionItem"
})
public class CompositionComponent {

    @XmlElement(required = true)
    protected BigDecimal compositionPercentage;
    @XmlElement(required = true)
    protected String compositionItem;

    /**
     * Gets the value of the compositionPercentage property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCompositionPercentage() {
        return compositionPercentage;
    }

    /**
     * Sets the value of the compositionPercentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCompositionPercentage(BigDecimal value) {
        this.compositionPercentage = value;
    }

    /**
     * Gets the value of the compositionItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompositionItem() {
        return compositionItem;
    }

    /**
     * Sets the value of the compositionItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompositionItem(String value) {
        this.compositionItem = value;
    }

}

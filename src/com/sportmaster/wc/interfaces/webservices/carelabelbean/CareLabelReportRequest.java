
package com.sportmaster.wc.interfaces.webservices.carelabelbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CareLabelReportRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CareLabelReportRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BOMProductAttributes" type="{http://www.sportmaster.ru/plm-services-careLabel}BOMProductAttributes"/>
 *         &lt;element name="BOMProductSeasonAttributes" type="{http://www.sportmaster.ru/plm-services-careLabel}BOMProductSeasonAttributes"/>
 *         &lt;element name="productBOM" type="{http://www.sportmaster.ru/plm-services-careLabel}ProductBOM" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CareLabelReportRequest", propOrder = {
    "requestId",
    "bomProductAttributes",
    "bomProductSeasonAttributes",
    "productBOM"
})
@XmlRootElement(name = "CareLabelReportRequest")
public class CareLabelReportRequest {

    protected int requestId;
    @XmlElement(name = "BOMProductAttributes", required = true)
    protected BOMProductAttributes bomProductAttributes;
    @XmlElement(name = "BOMProductSeasonAttributes", required = true)
    protected BOMProductSeasonAttributes bomProductSeasonAttributes;
    @XmlElement(required = true)
    protected List<ProductBOM> productBOM;

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
     * Gets the value of the bomProductAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link BOMProductAttributes }
     *     
     */
    public BOMProductAttributes getBOMProductAttributes() {
        return bomProductAttributes;
    }

    /**
     * Sets the value of the bomProductAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BOMProductAttributes }
     *     
     */
    public void setBOMProductAttributes(BOMProductAttributes value) {
        this.bomProductAttributes = value;
    }

    /**
     * Gets the value of the bomProductSeasonAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link BOMProductSeasonAttributes }
     *     
     */
    public BOMProductSeasonAttributes getBOMProductSeasonAttributes() {
        return bomProductSeasonAttributes;
    }

    /**
     * Sets the value of the bomProductSeasonAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BOMProductSeasonAttributes }
     *     
     */
    public void setBOMProductSeasonAttributes(BOMProductSeasonAttributes value) {
        this.bomProductSeasonAttributes = value;
    }

    /**
     * Gets the value of the productBOM property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productBOM property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductBOM().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductBOM }
     * 
     * 
     */
    public List<ProductBOM> getProductBOM() {
        if (productBOM == null) {
            productBOM = new ArrayList<ProductBOM>();
        }
        return this.productBOM;
    }

}

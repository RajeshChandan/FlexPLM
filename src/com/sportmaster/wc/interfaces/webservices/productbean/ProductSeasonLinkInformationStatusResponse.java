
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductSeasonLinkInformationStatusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductSeasonLinkInformationStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="productSeasonLinkInformationStatusResponseItem" type="{http://www.sportmaster.ru/plmproduct}ProductSeasonLinkInformationStatusResponseItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductSeasonLinkInformationStatusResponse", propOrder = {
    "productSeasonLinkInformationStatusResponseItem"
})
public class ProductSeasonLinkInformationStatusResponse implements Serializable {

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	protected List<ProductSeasonLinkInformationStatusResponseItem> productSeasonLinkInformationStatusResponseItem;

    /**
     * Gets the value of the productSeasonLinkInformationStatusResponseItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productSeasonLinkInformationStatusResponseItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductSeasonLinkInformationStatusResponseItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductSeasonLinkInformationStatusResponseItem }
     * 
     * 
     */
    public List<ProductSeasonLinkInformationStatusResponseItem> getProductSeasonLinkInformationStatusResponseItem() {
        if (productSeasonLinkInformationStatusResponseItem == null) {
            productSeasonLinkInformationStatusResponseItem = new ArrayList<ProductSeasonLinkInformationStatusResponseItem>();
        }
        return this.productSeasonLinkInformationStatusResponseItem;
    }

}

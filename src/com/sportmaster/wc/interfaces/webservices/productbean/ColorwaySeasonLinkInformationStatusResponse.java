
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ColorwaySeasonLinkInformationStatusResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorwaySeasonLinkInformationStatusResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="colorwaySeasonLinkInformationStatusResponseItem" type="{http://www.sportmaster.ru/plmproduct}ColorwaySeasonLinkInformationStatusResponseItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorwaySeasonLinkInformationStatusResponse", propOrder = {
    "colorwaySeasonLinkInformationStatusResponseItem"
})
public class ColorwaySeasonLinkInformationStatusResponse implements Serializable {

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -5529901742533316647L;
	protected List<ColorwaySeasonLinkInformationStatusResponseItem> colorwaySeasonLinkInformationStatusResponseItem;

    /**
     * Gets the value of the colorwaySeasonLinkInformationStatusResponseItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorwaySeasonLinkInformationStatusResponseItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorwaySeasonLinkInformationStatusResponseItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColorwaySeasonLinkInformationStatusResponseItem }
     * 
     * 
     */
    public List<ColorwaySeasonLinkInformationStatusResponseItem> getColorwaySeasonLinkInformationStatusResponseItem() {
        if (colorwaySeasonLinkInformationStatusResponseItem == null) {
            colorwaySeasonLinkInformationStatusResponseItem = new ArrayList<ColorwaySeasonLinkInformationStatusResponseItem>();
        }
        return this.colorwaySeasonLinkInformationStatusResponseItem;
    }

}

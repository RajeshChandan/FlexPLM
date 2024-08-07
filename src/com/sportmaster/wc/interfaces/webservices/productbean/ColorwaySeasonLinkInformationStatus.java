
package com.sportmaster.wc.interfaces.webservices.productbean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ColorwaySeasonLinkInformationStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorwaySeasonLinkInformationStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="colorwaySeasonLinkInformationStatusItem" type="{http://www.sportmaster.ru/plmproduct}ColorwaySeasonLinkInformationStatusItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorwaySeasonLinkInformationStatus", propOrder = {
    "colorwaySeasonLinkInformationStatusItem"
})
public class ColorwaySeasonLinkInformationStatus {

    protected List<ColorwaySeasonLinkInformationStatusItem> colorwaySeasonLinkInformationStatusItem;

    /**
     * Gets the value of the colorwaySeasonLinkInformationStatusItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorwaySeasonLinkInformationStatusItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorwaySeasonLinkInformationStatusItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColorwaySeasonLinkInformationStatusItem }
     * 
     * 
     */
    public List<ColorwaySeasonLinkInformationStatusItem> getColorwaySeasonLinkInformationStatusItem() {
        if (colorwaySeasonLinkInformationStatusItem == null) {
            colorwaySeasonLinkInformationStatusItem = new ArrayList<ColorwaySeasonLinkInformationStatusItem>();
        }
        return this.colorwaySeasonLinkInformationStatusItem;
    }

}

package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.mc.SMMaterialPrice;
import com.sportmaster.wc.mc.SMMaterialPriceListLC;
import org.apache.log4j.Logger;
import wt.util.WTException;

import java.util.*;

public abstract class SMCostSheetBOMTableRow {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetBOMTableRow.class);

    public abstract FlexObject getRow(FlexObject bomObj, FlexType bomType, FlexType materialType, int sortingNumberRow) throws WTException;

    public SMMaterialPrice getRowPrice(FlexObject bomObject, FlexType bomType, Date dateForMaterialPrice) throws WTException
    {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBOMTableRow.getRowPrice: Start");

        String materialColorId = FormatHelper.format(bomObject.getData("LCSMATERIALCOLOR.IDA2A2"));
        String materialSupplierMasterId = FormatHelper.format(bomObject.getData("LCSMATERIALSUPPLIERMASTER.IDA2A2"));

        if (SMCostSheetReader.isTextMaterial(bomObject, bomType)) {
            return new SMMaterialPrice();
        }

        //String colorName = FormatHelper.format(bomObject.getData("LCSCOLOR.COLORNAME"));
        String colorID = FormatHelper.format(bomObject.getData("LCSCOLOR.IDA2A2"));
        String colorDescription = FormatHelper.format(bomObject.getData(bomType.getAttribute("colorDescription").getSearchResultIndex()));
        if (colorID.isEmpty()) {
            materialColorId = "";
        }
        else if (!colorDescription.isEmpty()) {
            LCSColor color = (LCSColor) new wt.fc.ReferenceFactory().getReference("OR:com.lcs.wc.color.LCSColor:" + colorID).getObject();
            if (color != null && !color.getName().equalsIgnoreCase(colorDescription)) {
                materialColorId = "";
            }
        }

        Map matSup = new HashMap();
        matSup.put("materialSupplierMasterId", materialSupplierMasterId);

        Map matSupColor = new HashMap();
        matSupColor.put("materialSupplierMasterId", materialSupplierMasterId);
        matSupColor.put("materialColorId", materialColorId);

        Collection matSups = new ArrayList();
        Collection matSupColors = new ArrayList();

        matSups.add(matSup);
        matSupColors.add(matSupColor);

        SMMaterialPriceListLC mpllc =  new SMMaterialPriceListLC(matSups, matSupColors, dateForMaterialPrice);
        SMMaterialPrice mpeSMPrice = mpllc.getPrice(materialSupplierMasterId, materialColorId);

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBOMTableRow.getRowPrice: Finish");

        return mpeSMPrice;
    }

    public FlexObject getRow(String smRowID, String moaTableCellKey, String moaTableCellValue, int sortingNumberRow) {
        FlexObject result = new FlexObject();
        result.put( moaTableCellKey, moaTableCellValue );
        result.put("smConsumption", 1);
        result.put("smIncludeInPurchasePrice", true);
        result.put("smRowID", smRowID );
        result.put("sortingNumber", Integer.toString( sortingNumberRow ) );
        return result;
    }

    public Map getAdditionalRows(int nextID, int nextSortingNumber) throws WTException {
        return new HashMap();
    }

    protected Map toRows(int nextID, int nextSortingNumber, String smSectionInternalName, List<String> rows) {

        Map result = new HashMap();
        for (String smSection : rows) {
            String id = Integer.toString(nextID);
            result.put( id,
                    getRow( smSection,
                            smSectionInternalName, smSection, nextSortingNumber) );
            nextID++;
            nextSortingNumber++;
        }

        return result;
    }
}

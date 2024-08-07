package com.sportmaster.wc.mc;

import com.lcs.wc.client.web.TableData;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialColorQuery;
import com.lcs.wc.material.MaterialPricingEntry;
import com.lcs.wc.material.MaterialPricingEntryQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.mc.sourcing.SMCostSheetConfig;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.util.WTException;
import java.util.Collection;
import java.util.Date;

public class SMMaterialPriceListLC {

    private static final Logger LOGGER = Logger.getLogger(SMMaterialPriceListLC.class);

    public SearchResults materialPricingEntries = null;
    public SearchResults materialSuppliers = null;

    public SMMaterialPriceListLC() {
    }

    public SMMaterialPriceListLC(Collection matSups, Collection matSupColors, Date reqDate) {
        if (reqDate == null) {
            reqDate = new Date();
        }
        try {
            this.materialPricingEntries = new MaterialPricingEntryQuery().getMPEFromMatSupColorIDnDate(matSupColors, reqDate);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
        try {
            this.materialSuppliers = new MaterialPricingEntryQuery().getMaterialSupplierFromIDs(matSups);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public SMMaterialPrice getPrice(String matSupMstrId, String matColorId) throws WTException
    {
        LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPrice(matSupMstrId: " + matSupMstrId + ", matColorId: " + matColorId);

        if (!FormatHelper.hasContent(matSupMstrId)) matSupMstrId = "";
        else matSupMstrId = matSupMstrId.trim();
        if (!FormatHelper.hasContent(matColorId)) matColorId = "";
        else matColorId = matColorId.trim();

        SMMaterialPrice smMaterialPrice = getPriceForSupplierAndColor(materialPricingEntries, matSupMstrId, matColorId);
        if (smMaterialPrice.getPriceLC() > 0d || smMaterialPrice.getPrice() > 0d)
            return smMaterialPrice;

        smMaterialPrice = getPriceForSupplierAndColor(materialPricingEntries, matSupMstrId, "0");
        if (smMaterialPrice.getPriceLC() > 0d || smMaterialPrice.getPrice() > 0d)
            return smMaterialPrice;

        smMaterialPrice = getPriceForMaterialColor(matColorId);
        if (smMaterialPrice.getPriceLC() > 0d || smMaterialPrice.getPrice() > 0d)
            return smMaterialPrice;

        smMaterialPrice = getPriceForMaterialSupplier(materialSuppliers, matSupMstrId);
        if (smMaterialPrice.getPriceLC() > 0d || smMaterialPrice.getPrice() > 0d)
            return smMaterialPrice;

        return smMaterialPrice;
    }

    private static SMMaterialPrice getPriceForSupplierAndColor(SearchResults materialPricingEntriesResult, String matSupMasterId, String matColorId) throws WTException {
        SMMaterialPrice result = new SMMaterialPrice();
        String msgText = matColorId.equals("0") ? "Supplier" : "Color";
        for(Object obj : materialPricingEntriesResult.getResults())
        {
            FlexObject mpeflex = (FlexObject) obj;
            //LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.MPE Flex Object: " + mpeflex);
            String materialSupplierMasterId = (String) mpeflex.get("MATERIALPRICINGENTRY.IDA3A5");
            String materialColorId = (String) mpeflex.get("MATERIALPRICINGENTRY.IDA3B5");
            if ((materialSupplierMasterId.trim().equals(matSupMasterId)) && (materialColorId.trim().equals(matColorId)))
            {
                result.setPrice(Double.parseDouble((String) mpeflex.get("MATERIALPRICINGENTRY.PRICE")));
                result.setPriceLC(getMSPrice(mpeflex));
                result.setLocalCurrency(getMSCurrency(mpeflex));
                LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPriceForSupplierAndColor: Effectivity Material " + msgText + " price: " + result.getPrice());
                LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPriceForSupplierAndColor: Effectivity Material " + msgText + " MS Price: " + result.getPriceLC());
                LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPriceForSupplierAndColor: Effectivity Material " + msgText + " MS Currency: " + result.getLocalCurrency());
                break;
            }
        }
        return result;
    }

    private static SMMaterialPrice getPriceForMaterialSupplier(SearchResults materialSuppliersResult, String matSupMasterId) throws WTException {
        SMMaterialPrice result = new SMMaterialPrice();
        FlexType materialSupplierType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.material.LCSMaterialSupplier");

        for(Object obj : materialSuppliersResult.getResults())
        {
            FlexObject flexObject = (FlexObject) obj;
            String lcsMatSupplierMasterId = (String) flexObject.get("LCSMATERIALSUPPLIER.IDA3MASTERREFERENCE");
            if (lcsMatSupplierMasterId.equals(matSupMasterId)) {
                double materialPrice = FormatHelper.parseDouble( flexObject.getData(materialSupplierType.getAttribute("materialPrice").getSearchResultIndex()) );
                double smMaterialSupplierPrice = FormatHelper.parseDouble( flexObject.getData(materialSupplierType.getAttribute("smMaterialSupplierPrice").getSearchResultIndex()) );
                String smMsCurrencyUnit = FormatHelper.format( flexObject.getData(materialSupplierType.getAttribute("smMsCurrencyUnit").getSearchResultIndex()) );
                LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPriceForMaterialSupplier: materialPrice = " + materialPrice);
                LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPriceForMaterialSupplier: smMaterialSupplierPrice = " + smMaterialSupplierPrice);
                LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPriceForMaterialSupplier: smMsCurrencyUnit = " + smMsCurrencyUnit);
                result.setPrice(materialPrice);
                result.setPriceLC(smMaterialSupplierPrice);
                result.setLocalCurrency(smMsCurrencyUnit);
                break;
            }
        }
        return result;
    }

    private static SMMaterialPrice getPriceForMaterialColor(FlexObject bomObject) throws WTException {
        SMMaterialPrice result = new SMMaterialPrice();
        TableData branch = (TableData) bomObject;
        FlexType materialColorType = FlexTypeCache.getFlexTypeFromPath("Material Color");
        double vrdColorSpecificPrice = FormatHelper.parseDouble(branch.getData(materialColorType.getAttribute(SMCostSheetConfig.VRD_COLOR_SPECIFIC_PRICE).getSearchResultIndex())); //vrdColorSpecificPrice
        double smMsColorSpecificPrice = FormatHelper.parseDouble(branch.getData(materialColorType.getAttribute(SMCostSheetConfig.SM_COLOR_SPECIFIC_PRICE_LC).getSearchResultIndex())); //smMsColorSpecificPrice
        String smMsColorCurrency = FormatHelper.format(branch.getData(materialColorType.getAttribute(SMCostSheetConfig.COLOR_SPECIFIC_CURRENCY_UNIT).getSearchResultIndex())); //smMsColorCurrency
        LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPriceForMaterialColor: vrdColorSpecificPrice = " + vrdColorSpecificPrice);
        LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPriceForMaterialColor: smMsColorSpecificPrice = " + smMsColorSpecificPrice);
        LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getPriceForMaterialColor: smMsColorCurrency = " + smMsColorCurrency);
        result.setPrice(vrdColorSpecificPrice);
        result.setPriceLC(smMsColorSpecificPrice);
        result.setLocalCurrency(smMsColorCurrency);
        return result;
    }

    private static SMMaterialPrice getPriceForMaterialColor(String matColorId) throws WTException {
        SMMaterialPrice result = new SMMaterialPrice();
        if (FormatHelper.hasContent(matColorId))
        {
            LCSMaterialColor lcsMaterialColor = LCSMaterialColorQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:" + matColorId);
            if (lcsMaterialColor != null)
            {
                double matColSpecPrice = SMFormatHelper.getDouble( lcsMaterialColor.getValue(SMCostSheetConfig.VRD_COLOR_SPECIFIC_PRICE) ); //"vrdColorSpecificPrice"
                double matColSpecPriceLC = SMFormatHelper.getDouble( lcsMaterialColor.getValue(SMCostSheetConfig.SM_COLOR_SPECIFIC_PRICE_LC) ); //"smColorSpecificPriceLC"
                String matColSpecPriceCurr = (String) lcsMaterialColor.getValue(SMCostSheetConfig.COLOR_SPECIFIC_CURRENCY_UNIT); //"smCurrencyUnit"
                result.setPrice(matColSpecPrice);
                result.setPriceLC(matColSpecPriceLC);
                result.setLocalCurrency(matColSpecPriceCurr);
                if (matColSpecPrice != 0.0D) LOGGER.debug("CUSTOM>>>>>> Material Color Specific Price found: " + matColSpecPrice);
                if (matColSpecPriceLC != 0.0D) LOGGER.debug("CUSTOM>>>>>> Material Color Specific Price LC found: " + matColSpecPriceLC);
            }
        }
        return result;
    }

    public static double getMSPrice(FlexObject mpeObject) throws WTException {
        String mpeId = (String) mpeObject.get("MATERIALPRICINGENTRY.IDA2A2");
        MaterialPricingEntry mpe = (MaterialPricingEntry) LCSQuery.findObjectById("com.lcs.wc.material.MaterialPricingEntry:" + mpeId);
        double value = SMFormatHelper.getDouble( mpe.getValue(SMCostSheetConfig.MATERIAL_PRICING_ENTRY_SM_MATERIAL_PRICE_LC) );//smPriceLC
        LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getMSPrice: (" + value + ")");
        return value;
    }

    public static String getMSCurrency(FlexObject mpeObject) throws WTException {
        String mpeId = (String) mpeObject.get("MATERIALPRICINGENTRY.IDA2A2");
        MaterialPricingEntry mpe = (MaterialPricingEntry) LCSQuery.findObjectById("com.lcs.wc.material.MaterialPricingEntry:" + mpeId);
        String value =  (String) mpe.getValue(SMCostSheetConfig.MATERIAL_PRICING_ENTRY_SM_CURRENCY_UNIT);//smMPECurrencyUnit
        LOGGER.debug("CUSTOM>>>>>> SMMaterialPriceListLC.getMSCurrency: (" + value + ")");
        return value;
    }
}


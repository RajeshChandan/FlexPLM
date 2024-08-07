package com.sportmaster.wc.mc.material;

import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.material.MaterialPricingEntry;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.mc.sourcing.SMCostSheetConfig;
import org.apache.log4j.Logger;
import wt.fc.WTObject;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.util.WTException;

public class SMMaterialPricingEntryPlugin {

    private static final Logger LOGGER = Logger.getLogger(SMMaterialPricingEntryPlugin.class);

    public static WTObject rollupMaterialSupplierMSCurrencyUnit(WTObject wtobject) throws WTException
    {
        LOGGER.info("CUSTOM>>>>>> SMMaterialPricingEntryPlugin.rollupMaterialSupplierMSCurrencyUnit: *** PLUGIN START *** (" + wtobject + ")," +
                " DisplayIdentity (" + IxbHndHelper.getDisplayIdentityForIxb(wtobject) + ")");

        MaterialPricingEntry materialPricingEntry = toMaterialPricingEntry(wtobject);
        LCSMaterialSupplierMaster lcsMaterialSupplierMaster = materialPricingEntry.getMaterialSupplier();
        if (lcsMaterialSupplierMaster != null) {
            LCSMaterialSupplier lcsMaterialSupplier = VersionHelper.latestIterationOf(lcsMaterialSupplierMaster);
            LOGGER.debug("CUSTOM>>>>>> SMMaterialPricingEntryPlugin.rollupMaterialSupplierMSCurrencyUnit: " +
                    "LCSMaterialSupplier object is (" + lcsMaterialSupplier + ")");
            String unit = (String) lcsMaterialSupplier.getValue(SMCostSheetConfig.MATERIAL_SUPPLIER_CURRENCY_ATT);
            LOGGER.debug("CUSTOM>>>>>> SMMaterialPricingEntryPlugin.rollupMaterialSupplierMSCurrencyUnit: " +
                    "MS Currency Unit is (" + unit + ")");
            materialPricingEntry.setValue(SMCostSheetConfig.MATERIAL_PRICING_ENTRY_SM_CURRENCY_UNIT, unit);
        }

        LOGGER.info("CUSTOM>>>>>> SMMaterialPricingEntryPlugin.rollupMaterialSupplierMSCurrencyUnit: *** PLUGIN FINISH *** ");
        return materialPricingEntry;
    }

    public static MaterialPricingEntry toMaterialPricingEntry(WTObject wtobject) throws WTException {
        if(wtobject instanceof MaterialPricingEntry) {
            return  (MaterialPricingEntry) wtobject;
        } else {
            throw new WTException("CUSTOM>>>>>> SMMaterialPricingEntryPlugin.toMaterialPricingEntry: Object is not instance of MaterialPricingEntry");
        }
    }
}

package com.sportmaster.wc.mc.material;

import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.mc.sourcing.SMCostSheetConfig;
import org.apache.log4j.Logger;
import wt.fc.WTObject;
import wt.util.WTException;

public class SMMaterialColorPlugin {

  //  private static final String LC_UNIT_ATTRIBUTE = LCSProperties.get("com.custom.flexdemo.plugins.CostSheetBOMPlugin.LCUnitAtt","smCurrencyUnit");
  //  private static final Logger LOGGER = LogR.getLogger(SMMaterialSupplierPlugin.class.getName());

    private static final Logger LOGGER = Logger.getLogger(SMMaterialColorPlugin.class);

    public static final WTObject rollupMatSupCurrency(WTObject wtobject)
            throws WTException
    {

        LOGGER.debug("CUSTOM>>>>>> SMMaterialColorPlugin.rollupMatSupCurrency: Start ("+wtobject+")");
        LCSMaterialColor materialColor = null;

        if(wtobject instanceof LCSMaterialColor) {
            materialColor = (LCSMaterialColor) wtobject;
        } else {
            throw new WTException("CUSTOM>>>>>> SMMaterialColorPlugin.rollupMatSupCurrency: Object is not instance of LCSMaterialColor");
        }
        LOGGER.debug("CUSTOM>>>>>> CUSTOM>>>>>> SMMaterialColorPlugin.rollupMatSupCurrency: Mat-Color object is ("+materialColor+")");
        LCSMaterialSupplier matSup =  (LCSMaterialSupplier) VersionHelper.latestIterationOf(materialColor.getMaterialSupplierMaster());
        LOGGER.debug("CUSTOM>>>>>> CUSTOM>>>>>> SMMaterialColorPlugin.rollupMatSupCurrency: MaterialSupplier object is ("+matSup+")");
        String cUnit = (String)matSup.getValue(SMCostSheetConfig.MATERIAL_SUPPLIER_CURRENCY_ATT);
        LOGGER.debug("CUSTOM>>>>>> CUSTOM>>>>>> SMMaterialColorPlugin.rollupMatSupCurrency: MS Currency Unit is ("+cUnit+")");
        materialColor.setValue(SMCostSheetConfig.MATERIAL_COLOR_CURRENCY_ATT,cUnit);
        //            materialSupplier = (LCSMaterialSupplier)PersistenceHelper.manager.save(materialSupplier);

        return materialColor;

    }
}

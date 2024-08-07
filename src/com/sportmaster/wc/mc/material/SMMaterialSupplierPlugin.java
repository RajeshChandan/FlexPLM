package com.sportmaster.wc.mc.material;

//import com.lcs.wc.db.FlexObject;
//import com.lcs.wc.db.SearchResults;
//import com.lcs.wc.flextype.FlexType;
//import com.lcs.wc.flextype.FlexTypeAttribute;
//import com.lcs.wc.flextype.FlexTypeCache;
//import com.lcs.wc.util.FormatHelper;
//import java.util.Collection;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.Vector;
import com.sportmaster.wc.mc.sourcing.SMCostSheetConfig;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.LCSLog;
//import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;
import com.lcs.wc.supplier.*;
import wt.fc.WTObject;
import wt.fc.*;
import com.lcs.wc.util.VersionHelper;

public class SMMaterialSupplierPlugin
{
   private static final String LC_UNIT_ATTRIBUTE = LCSProperties.get("com.custom.flexdemo.plugins.CostSheetBOMPlugin.LCUnitAtt","smCurrencyUnit");
   private static final Logger LOGGER = LogR.getLogger(SMMaterialSupplierPlugin.class.getName());
  
   
  
   
    public static WTObject rollupSupplierCurrency(WTObject wtobject)
        throws WTException
    {

		LOGGER.debug("CUSTOM>>>>>> SMMaterialSupplierPlugin.rollupSupplierCurrency: Start ("+wtobject+")");
        LCSMaterialSupplier materialSupplier = null;

        if(wtobject instanceof LCSMaterialSupplier) {
            materialSupplier = (LCSMaterialSupplier) wtobject;
        } else {
            throw new WTException("CUSTOM>>>>>> CostSheetBOMPlugin.calculateBOMTotal: Object is not instance of LCSMOAObject");
        }
        LOGGER.debug("CUSTOM>>>>>> SMMaterialSupplierPlugin.rollupSupplierCurrency: Mat-Supp object is ("+materialSupplier+")");
	   LCSSupplier supplier =  (LCSSupplier)VersionHelper.latestIterationOf(materialSupplier.getSupplierMaster());
        LOGGER.debug("CUSTOM>>>>>> SMMaterialSupplierPlugin.rollupSupplierCurrency: Supplier object is ("+supplier+")");
	   String lcUnit = (String)supplier.getValue(SMCostSheetConfig.SUPPLIER_CURRENCY_ATT);
        LOGGER.debug("CUSTOM>>>>>> SMMaterialSupplierPlugin.rollupSupplierCurrency: LC Unit is ("+lcUnit+")");
	   materialSupplier.setValue(SMCostSheetConfig.MATERIAL_SUPPLIER_CURRENCY_ATT,lcUnit);

	   return materialSupplier;

    }
	
}


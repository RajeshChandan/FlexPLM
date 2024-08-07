package com.sportmaster.wc.sourcing;

import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.*;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.specification.*;
import com.lcs.wc.supplier.*;
import com.lcs.wc.material.*;
import com.lcs.wc.product.*;
import com.lcs.wc.util.*;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.moa.*;
import com.lcs.wc.db.*;
import com.lcs.wc.client.web.*;
import wt.fc.*;
import wt.util.*;
import wt.part.WTPartMaster;
import wt.vc.Mastered;
import java.util.*;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.supplier.LCSSupplier;

public class SMSourcingConfigPlugin
{
	

    public static final WTObject checkUniquess(WTObject wtobject)
        throws WTException 
    {

		LCSLog.debug("CUSTOM>>>>>> SMSourcingConfigPlugin.checkUniquess: Start ("+wtobject+")");
        LCSSourcingConfig srcConfig = null;
		boolean flag = false;
        if(wtobject instanceof LCSSourcingConfig) {
            srcConfig = (LCSSourcingConfig) wtobject;
        } else {
            throw new WTException("CUSTOM>>>>>> SMSourcingConfigPlugin.checkUniquess: Object is not instance of LCSSourcingConfig");
        }
		
		
		Object srcSupp   = (Object) srcConfig.getValue("vendor");
		LCSPartMaster productmaster = 	srcConfig.getProductMaster() ;
		LCSLog.debug("CUSTOM>>>>>> SMSourcingConfigPlugin.checkUniquess: Src Config Product = " + productmaster.getName());
		LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(productmaster);
		String styleNum = ""+(Long)product.getValue("vrdStyleNum");

		Collection srcConfigs = LCSSourcingConfigQuery.getSourcingConfigForProduct(productmaster);
		Iterator srcConfigsIterator  = srcConfigs.iterator();
		 while(srcConfigsIterator.hasNext()){
           LCSSourcingConfig aConfig = (LCSSourcingConfig)srcConfigsIterator.next();
			LCSLog.debug("CUSTOM>>>>>> SMSourcingConfigPlugin.checkUniquess: Found config: " + aConfig.getIdentity());
			Object obj   = (Object) aConfig.getValue("vendor");
			LCSLog.debug("CUSTOM>>>>>> SMSourcingConfigPlugin.checkUniquess: Found Supplier: " + obj);
			if (srcSupp.equals(obj) && (!srcConfig.getMaster().equals(aConfig.getMaster()))) {
				LCSLog.debug("CUSTOM>>>>>> SMSourcingConfigPlugin.checkUniquess: Supplier is the same! " + obj); 
				flag = true;
			}
				else LCSLog.debug("CUSTOM>>>>>> SMSourcingConfigPlugin.checkUniquess: Supplier is different! " + obj);
		   
		 }
			if (flag) 
			{
			LCSLog.debug("CUSTOM>>>>>> SMSourcingConfigPlugin.checkUniquess: Creation is forbidden ");
		//	WTException wte = new LCSException("Sourcing Config for Supplier '" + ((LCSSupplier)srcSupp).getName() + "' already exists. Please choose another one.");
			WTException wte = new LCSException("Sourcing Config for Style " + styleNum + " and Supplier '" + ((LCSSupplier)srcSupp).getName() + "' already exists.");

				throw wte;
			}
			
		LCSLog.debug("CUSTOM>>>>>> SMSourcingConfigPlugin.checkUniquess: Finish.");
 	    return srcConfig;
    }

   
}

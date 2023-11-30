package com.hbi.wc.sourcing;

import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import org.apache.log4j.Logger;
import   wt.log4j.LogR;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HBIPrimaryMaterialSettingPlugin {
	private static final Logger logger = LogR.getLogger("com.hbi.wc.sourcing.HBIPrimaryMaterialSettingPlugin");
	private static String PRIMARY_MATERIAL_KEY = LCSProperties.get("com.hbi.wc.sourcing.HBIPrimarySourcingConfigPlugin.primaryMaterialkey", "primaryMaterial");
	private static String FIBER_CONTENT = LCSProperties.get("com.hbi.wc.sourcing.HBIPrimarySourcingConfigPlugin.hbiFiberContentkey", "hbiFiberContent");
	private static String GARMENT_SOURCING_TYPE = LCSProperties.get("com.hbi.wc.sourcing.HBIPrimarySourcingConfigPlugin.GarmentType", "Sourcing Configuration\\Garment");
	private static String PM_DESCRIPTION = LCSProperties.get("com.hbi.wc.sourcing.HBIPrimarySourcingConfigPlugin.pmDescription", "pmDescription");

	public static void setPrimaryMaterialOnMaterial(WTObject wtObj) throws WTException, WTPropertyVetoException {
		logger.debug(":::::::::::::HBIPrimaryMaterialSettingPlugin Started:::::::::::::::::::");

		//Validating the incoming WTObject and processing/returning based on the instance type of the WTObject (processing only if the instance is of type LCSSourceToSeasonLink Object
				if(!(wtObj instanceof LCSSourceToSeasonLink))
				{
					logger.debug("Returning without performing any action as the incoming object is not an instance of LCSSourceToSeasonLink, this plug-in is specific to LCSSourceToSeasonLink");
					return;
				}
				
				LCSSourceToSeasonLink sourceToSeasonLinkObj = (LCSSourceToSeasonLink) wtObj;
				LCSSourcingConfig sourcingConfigObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceToSeasonLinkObj.getSourcingConfigMaster());
				String sourcingConfigType = sourcingConfigObj.getFlexType().getFullName(true);
				if(GARMENT_SOURCING_TYPE.equalsIgnoreCase(sourcingConfigType) && sourcingConfigObj.isPrimarySource())
				{
					LCSProduct productObj = (LCSProduct) VersionHelper.latestIterationOf(sourcingConfigObj.getProductMaster());
					productObj=(LCSProduct)VersionHelper.getVersion(productObj, "A");
					productObj=(LCSProduct)VersionHelper.latestIterationOf(productObj);
					LCSProduct previousIteration=(LCSProduct)VersionHelper.predecessorOf(productObj);
					LCSMaterialSupplier matSupp=(LCSMaterialSupplier)sourceToSeasonLinkObj.getValue(PRIMARY_MATERIAL_KEY);
					LCSMaterial material=null;
					if(matSupp!=null){
					material=(LCSMaterial)VersionHelper.latestIterationOf(matSupp.getMaterialMaster());
					
               	    if(previousIteration!=null &&previousIteration.getValue(PRIMARY_MATERIAL_KEY)==material && previousIteration.getValue(FIBER_CONTENT)==material.getValue(FIBER_CONTENT)){
               	    	logger.debug("Returning without performing any action as the data is same");

               	    	return;	
               	    }
               	 logger.debug(":::::::::::::sourceToSeasonLinkObj:::::::::::::::::::"+sourceToSeasonLinkObj);

					if(sourceToSeasonLinkObj!=null){
					productObj.setValue(PRIMARY_MATERIAL_KEY, sourceToSeasonLinkObj.getValue(PRIMARY_MATERIAL_KEY));
					productObj.setValue(FIBER_CONTENT, material.getValue(FIBER_CONTENT));
					productObj.setValue(PM_DESCRIPTION, sourceToSeasonLinkObj.getValue(PM_DESCRIPTION));

					
					LCSProductLogic logic=new LCSProductLogic();
					logic.save(productObj);
					}
					logger.debug(":::::::::::::HBIPrimaryMaterialSettingPlugin ENDED:::::::::::::::::::");
				}
				}
	}
	
}

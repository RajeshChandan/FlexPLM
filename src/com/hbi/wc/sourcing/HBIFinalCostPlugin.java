package com.hbi.wc.sourcing;

import java.util.Collection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hbi.wc.util.HBIAttributeKeyHelper;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSCostSheetLogic;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBIFinalCostPlugin.java
 *
 * Class for Product CostSheet plug-in function and generic function to validate Cost Type, based on the Cost Type change CostSheet Primary status
 * @author Abdul.Patel@Hanes.com
 * @since Sept-11-2023
 */
public class HBIFinalCostPlugin
{
	private static Logger log = LogManager.getLogger(HBIFinalCostPlugin.class);
	
	/**
	 * This function is using as a plug-in function which is registered on LCSProductCostSheet PRE_PERSIST to populate Primary status by Cost Type
	 * @param sourcingConfigObj - LCSSourcingConfig
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void changeProdCSPrimaryStatus(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		log.info("### HBIFinalCostPlugin.changeProdCSPrimaryStatus(WTObject wtObj) START ###");
		
		//if the event object is other than LCSProductCostSheet, return
		if(!(wtObj instanceof LCSProductCostSheet)) {
			return;
		}
		LCSProductCostSheet prodCostSheet = (LCSProductCostSheet)wtObj;
		
		//if the LCSProductCostSheet owner is other than Selling Product, return
		LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(prodCostSheet.getProductMaster());
		String productType = product.getFlexType().getFullNameDisplay(true);
		if(!productType.equals(HBIAttributeKeyHelper.PRODUCT_SELLING_TYPEPATH)) {
			return;
		}
		
		//if Cost Type is other than Final Cost, return
		String costType = (String) prodCostSheet.getValue(HBIAttributeKeyHelper.COSTSHEET_COST_TYPE_ATTKEY);
		if(!(FormatHelper.hasContent(costType) && costType.equals("hbiFinalCost"))) {
			return;
		}
		
		//if CostSheet is marked as Primary, return
		if(prodCostSheet.isPrimaryCostSheet()) {
			return;
		}
		
		new HBIFinalCostPlugin().changeProdCSPrimaryStatus(prodCostSheet, product);
		prodCostSheet.setPrimaryCostSheet(true);
		
		log.info("### HBIFinalCostPlugin.changeProdCSPrimaryStatus(WTObject wtObj) END ###");
	}
	
	@SuppressWarnings("unchecked")
	public void changeProdCSPrimaryStatus(LCSProductCostSheet prodCostSheet, LCSProduct product) throws WTException, WTPropertyVetoException
	{
		log.info("### HBIFinalCostPlugin.changeProdCSPrimaryStatus(prodCostSheet, product) START ###");
		LCSProduct productARev = SeasonProductLocator.getProductARev(product);
		Collection<LCSProductCostSheet> productCSColl = null;
		
		if(prodCostSheet.getSeasonMaster() != null && prodCostSheet.getSourcingConfigMaster() != null) {
			productCSColl = LCSCostSheetQuery.getCostSheetsForSourceToSeason(prodCostSheet.getSeasonMaster(), prodCostSheet.getSourcingConfigMaster(), productARev.getPlaceholderMaster());
		}
		else if(prodCostSheet.getSourcingConfigMaster() != null) {
			LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(prodCostSheet.getSourcingConfigMaster());
			productCSColl = LCSCostSheetQuery.getAllCostSheetsForSourcingConfig(sourcingConfig);
		}
		
		if(!(productCSColl != null && productCSColl.size() > 0)) {
			return;
		}
		
		for(LCSProductCostSheet costSheet : productCSColl) {
			if(costSheet.isPrimaryCostSheet()) {
				persistCostSheet(costSheet);
			}
		}
		
		log.info("### HBIFinalCostPlugin.changeProdCSPrimaryStatus(prodCostSheet, product) END ###");
	}
	
	public void persistCostSheet(LCSProductCostSheet costSheet) throws WTException, WTPropertyVetoException
	{
		try
		{
			if(!VersionHelper.isCheckedOut(costSheet)) {
				costSheet = (LCSProductCostSheet) VersionHelper.checkout(costSheet);
			}
			else {
				costSheet = (LCSProductCostSheet) VersionHelper.getWorkingCopy(costSheet);
			}
			costSheet.setPrimaryCostSheet(false);
			costSheet = (LCSProductCostSheet) LCSCostSheetLogic.persist(costSheet, true);
		}
		finally {
			if(VersionHelper.isCheckedOut(costSheet)) {
				costSheet = (LCSProductCostSheet) VersionHelper.checkin(costSheet);
			}
		}
	}
}
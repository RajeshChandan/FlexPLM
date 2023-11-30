package com.hbi.wc.interfaces.outbound.product.util;

import java.util.Collection;
import java.util.ArrayList;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.util.VersionHelper;
//import wt.part.WTPartMaster;

/**
 * HBISPAutomationGenericMethods.java
 * 
 * This class contains generic functions which are using to lock and unlock FlexObjects (for example, Product, Colorway, Product-Season Page, Color, Colorway-
 * Size, object level locking status is required to lock integration fields, FlexObject locking and unlocking will be managed using integration trigger points
 * @author UST

 */
public class HBISPAutomationGenericMethods
{
	private static String productLockStatusKey = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.validation.HBISPAutomationAttributeEditLockValidation.productLockStatusKey", "hbiProductEditLockStatus");
	private static String colorwayLockStatusKey = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.validation.HBISPAutomationAttributeEditLockValidation.colorwayLockStatusKey", "hbiColorwayEditLockStatus");
	private static String colorObjectType = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.colorObjectType", "Color\\Colorway");
	
	/**
	 * This function is using to get colorways
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public Collection<LCSSKU> getSKUFromProductSeason(LCSProduct productObj,LCSSeason seasonObj) throws WTException, WTPropertyVetoException
	{
		LCSSKU  skuObj = null;
		LCSColor colorObj = null;
		Collection<LCSSKU> listOfSKUObjects =new ArrayList<LCSSKU>();
		
		if(productObj != null && seasonObj != null)
		{
			Collection<LCSPartMaster> skuMasterCollection = LCSSeasonQuery.getSKUMastersForSeasonAndProduct(seasonObj, productObj, false);
			if(skuMasterCollection != null && skuMasterCollection.size() > 0)
			{
				for(LCSPartMaster skuMasterObj : skuMasterCollection)
				{
					skuObj = (LCSSKU) VersionHelper.latestIterationOf(skuMasterObj);
					skuObj = SeasonProductLocator.getSKUARev(skuObj);
					colorObj = (LCSColor)skuObj.getValue("color");
					if(colorObj != null && colorObjectType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)))
					{
						listOfSKUObjects.add(skuObj);
					}	
				}
			}
		}
		return listOfSKUObjects;
	}
	
}	
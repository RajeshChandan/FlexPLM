package com.hbi.wc.sourcing;

import java.util.Date;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.FormatHelper;
import org.apache.log4j.Logger;
import   wt.log4j.LogR;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.enterprise.RevisionControlled;
import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

/**
 * HBISourceToSeasonLinkSealDatePlugin.java
 * 
 * This class contains generic functions which are using to validate the 'SourcingConfig' 'Primary Source' flag and cascade 'Green Seal', 'Red Seal' from SourceToSeasonLink to the Product
 * @author Abdul.Patel@Hanes.com
 * @since April-25-2016
 */
public class HBISourceToSeasonLinkSealDatePlugin
{
	private static String sourcingConfigPrimarySourceKey = LCSProperties.get("com.hbi.wc.sourcing.HBIPrimarySourcingConfigPlugin.sourcingConfigPrimarySourceKey", "hbiPrimarySource");
	private static String sourceToSeasonLinkGreenSealKey = LCSProperties.get("com.hbi.wc.sourcing.HBISourceToSeasonLinkSealDatePlugin.sourceToSeasonLinkGreenSealKey", "hbiGreenSeal");
	private static String sourceToSeasonLinkRedSealKey = LCSProperties.get("com.hbi.wc.sourcing.HBISourceToSeasonLinkSealDatePlugin.sourceToSeasonLinkRedSealKey", "hbiRedSeal");
	private static String productGreenSealCompleteKey = LCSProperties.get("com.hbi.wc.sourcing.HBISourceToSeasonLinkSealDatePlugin.productGreenSealCompleteKey", "hbiGreenSealComplete");
	private static String productRedSealCompleteKey = LCSProperties.get("com.hbi.wc.sourcing.HBISourceToSeasonLinkSealDatePlugin.productRedSealCompleteKey", "hbiRedSealComplete");
	private static final Logger logger = LogR.getLogger("com.hbi.wc.sourcing.HBISourceToSeasonLinkSealDatePlugin");
	/**
	 * This function is using as a plug-in function which is registered on LCSSourceToSeasonLink POST_PERSIST EVENT to validate 'Primary Source' and 'Seal Date' and update the Product data
	 * @param wtObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void validateAndUpdateProductSealDate(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
	logger.debug("### START HBISourceToSeasonLinkSealDatePlugin.validateAndUpdateProductSealDate(WTObject wtObj) ###");
		
		//Validating the incoming WTObject and processing/returning based on the instance type of the WTObject (processing only if the instance is of type LCSSourceToSeasonLink Object
		if(!(wtObj instanceof LCSSourceToSeasonLink))
		{
			logger.debug("Returning without performing any action as the incoming object is not an instance of LCSSourceToSeasonLink, this plug-in is specific to LCSSourceToSeasonLink");
			return;
		}
		
		//Get SourcingConfig object from the given SourceToSeasonLink, validate the type path (as we need to invoke this only for Garment Source) and 'Primary Source' flag from Source 
		LCSSourceToSeasonLink sourceToSeasonLinkObj = (LCSSourceToSeasonLink) wtObj;
		LCSSourcingConfig sourcingConfigObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceToSeasonLinkObj.getSourcingConfigMaster());
		String sourcingConfigType = sourcingConfigObj.getFlexType().getFullName(true);
		if("Sourcing Configuration\\Garment".equalsIgnoreCase(sourcingConfigType) && "true".equals(String.valueOf(sourcingConfigObj.getValue(sourcingConfigPrimarySourceKey))))
		{
			new HBISourceToSeasonLinkSealDatePlugin().validateAndUpdateProductSealDate(sourcingConfigObj, sourceToSeasonLinkObj);
		}
		
		logger.debug("### END HBISourceToSeasonLinkSealDatePlugin.validateAndUpdateProductSealDate(WTObject wtObj) ###");
	}
	
	/**
	 * This function is calling from HBIPrimarySourcingConfigPlugin.validateAndUpdatePrimarySourceFlag(WTObject wtObj) which will invoke/trigger on SourcingConfig create or update event
	 * using this function to cascade 'Green Seal' and 'Red Seal' Date from SourceToSeasonLink to Product based on the validation of 'Primary Source' flag defined on SourcingConfig object
	 * @param sourcingConfigObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void validateAndUpdateProductSealDate(LCSSourcingConfig sourcingConfigObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBISourceToSeasonLinkSealDatePlugin.validateAndUpdateProductSealDate(LCSSourcingConfig sourcingConfigObj) ###");
		LCSSourceToSeasonLink sourceToSeasonLinkObj = null;
		String predecessorPrimarySourceFlag = "";
		
		String sourcingConfigType = sourcingConfigObj.getFlexType().getFullName(true);
		//String primarySourceFlag = (String)sourcingConfigObj.getValue(sourcingConfigPrimarySourceKey);
		//Wipro Team Upgarde
		String primarySourceFlag = String.valueOf(sourcingConfigObj.getValue(sourcingConfigPrimarySourceKey));
		if("Sourcing Configuration\\Garment".equalsIgnoreCase(sourcingConfigType) && FormatHelper.hasContent(primarySourceFlag) && "true".equals(primarySourceFlag))
		{
			//Get Product from a SourcingConfig, then get Season object from the Product (which is derived from the given SourcingConfig), get SourceToSeasonLink for a Season, Source 
			LCSProduct productObj = (LCSProduct) VersionHelper.latestIterationOf(sourcingConfigObj.getProductMaster());
			if(productObj.getSeasonMaster() != null)
			{
				LCSSeason seasonObj = (LCSSeason) VersionHelper.latestIterationOf(productObj.getSeasonMaster());
				sourceToSeasonLinkObj = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourcingConfigObj, seasonObj);
			}
			
			//Get Predecessor from the given 'SourcingConfig' which is using to get 'Primary Source' flag needed to get the 'Primary Source' change set and cascade Seal Date to Product
			ObjectReference objRef = VersionControlHelper.getPredecessor((RevisionControlled) sourcingConfigObj);
			if(objRef != null)
			{
				LCSSourcingConfig oldSourcingConfigObj = (LCSSourcingConfig) objRef.getObject();
				if(oldSourcingConfigObj != null)
				{
					//predecessorPrimarySourceFlag = (String)oldSourcingConfigObj.getValue(sourcingConfigPrimarySourceKey);
					//Wipro Team Upgrade
					predecessorPrimarySourceFlag = String.valueOf(oldSourcingConfigObj.getValue(sourcingConfigPrimarySourceKey));
				}
			}
			
			//Get 'Primary Source' Flag from the given 'SourcingConfig' object, compare with the existing Predecessor 'Primary Source' Flag, cascade Seal Date from SourcingLink to Product
			if(!primarySourceFlag.equals(predecessorPrimarySourceFlag) && sourceToSeasonLinkObj != null)
			{
				new HBISourceToSeasonLinkSealDatePlugin().validateAndUpdateProductSealDate(sourcingConfigObj, sourceToSeasonLinkObj);
			}
		}
		
		// LCSLog.debug("### END HBISourceToSeasonLinkSealDatePlugin.validateAndUpdateProductSealDate(LCSSourcingConfig sourcingConfigObj) ###");
	}
	
	/**
	 * This function is using to get 'Green Seal' and 'Red Seal' from 'SourceToSeasonLink' and 'Product', validate the data, based on the validation update the Product Seal Date & persist
	 * @param sourcingConfigObj - LCSSourcingConfig
	 * @param sourceToSeasonLinkObj - LCSSourceToSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void validateAndUpdateProductSealDate(LCSSourcingConfig sourcingConfigObj, LCSSourceToSeasonLink sourceToSeasonLinkObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBISourceToSeasonLinkSealDatePlugin.validateAndUpdateProductSealDate(LCSSourcingConfig sourcingConfgObj, LCSSourceToSeasonLink sourceToSeasonLink) ###");
		LCSProduct productObj = (LCSProduct) VersionHelper.latestIterationOf(sourcingConfigObj.getProductMaster());
		productObj = SeasonProductLocator.getProductARev(productObj);
		boolean persistProduct = false;
		
		//This block of code is to get the 'Green Seal' from 'SourceToSeasonLink' and 'Product', validate the data, based on the validation status updating the Product object Green Seal
		Date sourceToSeasonLinkGreenSealDate = (Date) sourceToSeasonLinkObj.getValue(sourceToSeasonLinkGreenSealKey);
		Date productGreenSealComplete = (Date) productObj.getValue(productGreenSealCompleteKey);
		
			//This block of code is to get the 'Red Seal' date from 'SourceToSeasonLink' and 'Product', validate the data, based on the validation status updating the Product object Red Seal
		Date sourceToSeasonLinkRedSealDate = (Date) sourceToSeasonLinkObj.getValue(sourceToSeasonLinkRedSealKey);
		Date productRedSealComplete = (Date) productObj.getValue(productRedSealCompleteKey);
		//Wipro Team Upgrade
		if(!VersionHelper.isCheckedOut(productObj)	)
     			  productObj=(LCSProduct)VersionHelper.checkout(productObj);
			  else
				  productObj=(LCSProduct)VersionHelper.getWorkingCopy(productObj);
		
		if(sourceToSeasonLinkGreenSealDate != productGreenSealComplete)
		{
			productObj.setValue(productGreenSealCompleteKey, sourceToSeasonLinkGreenSealDate);
			persistProduct = true;
		}
		
	
		if(sourceToSeasonLinkRedSealDate != productRedSealComplete)
		{
			productObj.setValue(productRedSealCompleteKey, sourceToSeasonLinkRedSealDate);
			persistProduct = true;
		}
		
		//Validating the change flag, if the Product object is updated to change 'Green Seal' and/or 'Red Seal' then persisting the object to cascade the Seal Date from Source to Product
		if(persistProduct)
		{
         
				  

		LCSProductLogic.persist(productObj, false);
		}
		
		if(VersionHelper.isCheckedOut(productObj))
			VersionHelper.checkin(productObj);
		
		// LCSLog.debug("### END HBISourceToSeasonLinkSealDatePlugin.validateAndUpdateProductSealDate(LCSSourcingConfig sourcingConfigObj, LCSSourceToSeasonLink sourceToSeasonLink) ###");
	}
}
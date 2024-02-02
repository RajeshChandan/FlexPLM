package com.hbi.wc.sourcing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import wt.enterprise.RevisionControlled;
import wt.fc.ObjectReference;
import wt.fc.WTObject;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLinkMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.sourcing.SourcingConfigHelper;
import com.lcs.wc.util.FormatHelper;
import org.apache.log4j.Logger;
import   wt.log4j.LogR;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

/**
 * HBIPrimarySourcingConfigPlugin.java
 * Updated on 23-02-2020, new product source should have primary boolean as yes, fix for a source present in one season but not other season
 * This class contains generic functions which are using to validate the 'SourcingConfig' 'Primary Source' flag and enforce a uniqueness rule for a SourcingConfig within a Product instance
 * @author Abdul.Patel@Hanes.com
 * @since March-14-2016
 * @updated on Dec 2019- Added multiple seasons and avoid updating primary flag from yes to no, 
 * as ideally one source which should be primary and its flag to be yes should not be changed to no when that source is primary.
 * When a source is made primary by using the flag, it will become primary for all the seasons it is associated for that product.
 */
public class HBIPrimarySourcingConfigPlugin
{
	private static final Logger logger = LogR.getLogger("ccom.hbi.wc.sourcing.HBIPrimarySourcingConfigPlugin");
	private static String hbiPrimarySourceKey = LCSProperties.get("com.hbi.wc.sourcing.HBIPrimarySourcingConfigPlugin.hbiPrimarySourceKey", "hbiPrimarySource");
	
	/**
	 * This function is using as a plug-in function which is registered on LCSSourcingConfig POST_PERSIST EVENT to validate 'Primary Source' flag and enforce uniqueness within a Product
	 * @param sourcingConfigObj - LCSSourcingConfig
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void validateAndUpdatePrimarySourceFlag(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		
		
		
		//Validating the incoming WTObject and processing/returning based on the instance type of the WTObject (processing only if the instance is of type LCSSourcingConfig Object
		if(!(wtObj instanceof LCSSourcingConfig))
		{
			logger.debug("Returning without performing any action as the incoming object is not an instance of LCSSourcingConfig, this plug-in is specific to LCSSourcingConfig Object");
			return;
		}
		LCSSourcingConfig sourcingConfigObj = (LCSSourcingConfig) wtObj;
		
		//Get SourcingConfig Type, validate the SourcingConfig Type with the pre-defined Type, based on the type validation invoke/skip the 'Primary Source' uniqueness validation functions 
		String sourcingConfigType = sourcingConfigObj.getFlexType().getFullName(true);
		if(! ("Sourcing Configuration\\Garment".equalsIgnoreCase(sourcingConfigType) || "Sourcing Configuration\\Pattern".equalsIgnoreCase(sourcingConfigType)))
		{
			logger.debug("Returning without performing any action as the incoming SourcingConfig Type is other than Garment and Pattern");
			return;
		}
		LCSProduct productObj = (LCSProduct) VersionHelper.latestIterationOf(sourcingConfigObj.getProductMaster());
		//Get 'Primary Source' Flag from the given 'SourcingConfig' object, compare with the existing Predecessor 'Primary Source' Flag and invoke uniqueness enforcing validation function
		//String primarySourceFlag = (String)sourcingConfigObj.getValue(hbiPrimarySourceKey);
		//Wipro Team Upgrade
		String primarySourceFlag = String .valueOf(sourcingConfigObj.getValue(hbiPrimarySourceKey));
		
		
		String predecessorPrimarySourceFlag = "";
		//Get Predecessor from the given 'SourcingConfig' which is using to get 'Primary Source' flag needed to get the 'Primary Source' change set and invoke validation functions
		ObjectReference objRef = VersionControlHelper.getPredecessor((RevisionControlled) sourcingConfigObj);
		if(objRef != null)
		{
			LCSSourcingConfig oldSourcingConfigObj = (LCSSourcingConfig) objRef.getObject();
			
			//This block is specific to a 'SourcingConfig' which is creating on Product creation, using this block to update 'Primary Source' status from 'No to Yes' based on OOTB status
			if(!(oldSourcingConfigObj != null && FormatHelper.hasContent(primarySourceFlag))){
				logger.debug("Changing primary flag of first source on the product which is primary by default to Yes even if no seasons associated");
				new HBIPrimarySourcingConfigPlugin().changePrimaryFlagToYesForFirstSource(productObj, sourcingConfigObj);
				logger.debug("Returning without performing further action as this is on a new product first source");
				return;
			}else{
				//predecessorPrimarySourceFlag = (String)oldSourcingConfigObj.getValue(hbiPrimarySourceKey);
				//Wipro Team Upgrade
				predecessorPrimarySourceFlag = String.valueOf(oldSourcingConfigObj.getValue(hbiPrimarySourceKey));
				
			}
		}
		//Dec 2019 - Get all active seasons of a product as there can be multiple seasons associated to a product
		//Earlier assumption as per requirements was that a product should not have more than one season but now more seasons are there, code updated to handle new scenario
		Collection<LCSSeason> seasons = getAllActiveSeasonsOfAProduct(productObj);
		
		Boolean seasonSource= false;
		if(seasons.isEmpty()){
			logger.debug("Produt not having Season");
			//When a product not having any season
			new HBIPrimarySourcingConfigPlugin().updatePrimarySourceFlag( productObj, sourcingConfigObj,predecessorPrimarySourceFlag);
			//No change to primary flag
			if(FormatHelper.hasContent(primarySourceFlag) && "false".equals(primarySourceFlag) && !primarySourceFlag.equals(predecessorPrimarySourceFlag))
			{
				new HBIPrimarySourcingConfigPlugin().setPrimaryToYes( sourcingConfigObj);
			}
			return;
		}else{
			//Product having seasons
			
			for(LCSSeason seasonObj:seasons){
				if(LCSSourcingConfigQuery.sourceToSeasonExists(sourcingConfigObj, seasonObj)){
					//Calling a function which is using to validate the 'Primary Source' change set, based on the change set status invoking/skipping the functions using for uniqueness validation
					new HBIPrimarySourcingConfigPlugin().updatePrimarySourceFlagSeason(seasonObj, productObj, sourcingConfigObj,predecessorPrimarySourceFlag);
					seasonSource=true;
				}else{
					//Product having Season but Source not having season
					//No change to primary flag from already Yes
					if(FormatHelper.hasContent(primarySourceFlag) && "false".equals(primarySourceFlag) && !primarySourceFlag.equals(predecessorPrimarySourceFlag))
					{	logger.debug("Product having Season but Source not having season setPrimaryToYes");
						new HBIPrimarySourcingConfigPlugin().setPrimaryToYes( sourcingConfigObj);
					}	
				}			
			}
			//Update 23-02-2020
			//This moved outside if loop, as it was not allowing a source not present in all season to set as primary.
			if(!seasonSource){
				//When a source not associated to any season in a product and other source is already primary, set the primary flag to false.
				new HBIPrimarySourcingConfigPlugin().setPrimaryToNo( sourcingConfigObj);
			}
			
		}
		//Calling a function which is using to validate the 'Primary Source' change, based on the validation status cascade 'Green Seal' and 'Red Seal' from SourceToSeasonLink to Product
		new HBISourceToSeasonLinkSealDatePlugin().validateAndUpdateProductSealDate(sourcingConfigObj);
		
	}
	
	/**
	 * This function is using to populate the 'Primary Source' change set from the given SourcingConfig object and invoke/skip the 'Primary Source' uniqueness enforcing/validating function
	 * @param seasonObj - LCSSeason
	 * @param productObj - LCSProduct
	 * @param sourcingConfigObj - LCSSourcingConfig
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void updatePrimarySourceFlagSeason(LCSSeason seasonObj, LCSProduct productObj, LCSSourcingConfig sourcingConfigObj, String predecessorPrimarySourceFlag) throws WTException, WTPropertyVetoException
	{
		
		//Get 'Primary Source' Flag from the given 'SourcingConfig' object, compare with the existing Predecessor 'Primary Source' Flag and invoke uniqueness enforcing validation function
		//String primarySourceFlag = (String)sourcingConfigObj.getValue(hbiPrimarySourceKey);
		//Wipro Team Upgrade
		String primarySourceFlag = String.valueOf(sourcingConfigObj.getValue(hbiPrimarySourceKey));
		
		
		if(FormatHelper.hasContent(primarySourceFlag) && "true".equals(primarySourceFlag) && !primarySourceFlag.equals(predecessorPrimarySourceFlag))
		{	
			updateSourcingConfigPrimarySourceFlagSeason(seasonObj, productObj, sourcingConfigObj);
		}
		
		
		//Dec 2019- New method added
		//When a primary source flag is set to No or False or Un checked from Yes/true/checked
		//Revert back to Yes, as a product should have one primary source and primary source to season
		if(FormatHelper.hasContent(primarySourceFlag) && "false".equals(primarySourceFlag) && !primarySourceFlag.equals(predecessorPrimarySourceFlag))
		{
			setPrimaryToYes( sourcingConfigObj);
		}
	}
	
	public void updatePrimarySourceFlag( LCSProduct productObj, LCSSourcingConfig sourcingConfigObj, String predecessorPrimarySourceFlag) throws WTException, WTPropertyVetoException
	{
		
		//Get 'Primary Source' Flag from the given 'SourcingConfig' object, compare with the existing Predecessor 'Primary Source' Flag and invoke uniqueness enforcing validation function
		//String primarySourceFlag = (String)sourcingConfigObj.getValue(hbiPrimarySourceKey);
		//Wipro Team Upgrade
		String primarySourceFlag = String.valueOf(sourcingConfigObj.getValue(hbiPrimarySourceKey));
		
		
		if(FormatHelper.hasContent(primarySourceFlag) && "true".equals(primarySourceFlag) && !primarySourceFlag.equals(predecessorPrimarySourceFlag))
		{
			updateSourcingConfigPrimarySourceFlag( productObj, sourcingConfigObj);
		}
		
		
		//Dec 2019- New method added
		//When a primary source flag is set to No or False or Un checked from Yes/true/checked
		//Revert back to Yes, as a product should have one primary source and primary source to season
		if(FormatHelper.hasContent(primarySourceFlag) && "false".equals(primarySourceFlag) && !primarySourceFlag.equals(predecessorPrimarySourceFlag))
		{
			setPrimaryToYes( sourcingConfigObj);
		}
	}

	/**
	 * This function is using to get 'Product' from the given 'SourcingConfig', then get a Collection of LCSSourcingConfig based on the Product then validate and update 'Primary Source'
	 * @param seasonObj - LCSSeason
	 * @param productObj - LCSProduct
	 * @param sourcingConfigObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	public void updateSourcingConfigPrimarySourceFlagSeason(LCSSeason seasonObj, LCSProduct productObj, LCSSourcingConfig sourcingConfigObj) throws WTException, WTPropertyVetoException
	{
		// logger.debug("### START HBIPrimarySourcingConfigPlugin.updateSourcingConfigPrimarySourceFlag(seasonObj, productObj, sourcingConfigObj) ###");
		LCSSourcingConfigMaster sourcingConfigMasterObj = (LCSSourcingConfigMaster) sourcingConfigObj.getMaster();
		
		String existingPrimarySourceFlag = "";
		
		//Get a Collection of 'SourcingConfig' objects from the given Product, Iterate on each 'SourcingConfig' object to validate the 'Primary Source' flag and update to mark the status
		Collection<LCSSourcingConfig> sourcingConfigCollection = LCSSourcingConfigQuery.getSourcingConfigsForProduct(productObj);
		for(LCSSourcingConfig existingSourcingConfigObj : sourcingConfigCollection)
		{logger.debug("Setting other sources to false");
			//Get an Existing object 'Primary Source' flag, validate and update an existing 'SourcingConfig' object to change the 'Primary Source' flag in order to maintain uniqueness
			
		//existingPrimarySourceFlag = (String)existingSourcingConfigObj.getValue(hbiPrimarySourceKey);
		//Wipro Team Upgrade
		existingPrimarySourceFlag = String.valueOf(existingSourcingConfigObj.getValue(hbiPrimarySourceKey));
			
	
			LCSSourcingConfigMaster existingSourcingConfigMasterObj = (LCSSourcingConfigMaster) existingSourcingConfigObj.getMaster();
			
			//
			if(!sourcingConfigMasterObj.equals(existingSourcingConfigMasterObj) && "true".equals(existingPrimarySourceFlag))
			{	//Setting primary flag to no for other sources
				setPrimarySourceFlagToFalse(existingSourcingConfigObj, existingSourcingConfigMasterObj);
			}
		}
		
		//Updating the LCSSourcingConfigMaster (which is derived from the given SourcingConfig Object) to set 'Primary Source' for Product as 'Yes' based on the user data at Source Level
		sourcingConfigMasterObj.setPrimarySource(true);
		LCSSourcingConfigLogic.persist(sourcingConfigMasterObj, true);
		
		//Calling a function which is using to update 'SourceToSeasonLink' 'Primary Source' for 'Season' flag from Yes to No as we got a new 'Primary Source' from the user input data
		//updateSourceToSeasonLinkPrimarySourceFlagToNo(seasonObj, productObj, sourcingConfigMasterObj);
		
		//Calling a function which is using to update 'SourceToSeasonLink' 'Primary Source' for 'Season' flag from No to Yes as we got request to make this Source as Primary for a Season
		updateSourceToSeasonLinkPrimarySourceFlagToYes(seasonObj, productObj, sourcingConfigMasterObj);
		
		
	}
	
	public void updateSourcingConfigPrimarySourceFlag( LCSProduct productObj, LCSSourcingConfig sourcingConfigObj) throws WTException, WTPropertyVetoException
	{
		// logger.debug("### START HBIPrimarySourcingConfigPlugin.updateSourcingConfigPrimarySourceFlag(seasonObj, productObj, sourcingConfigObj) ###");
		LCSSourcingConfigMaster sourcingConfigMasterObj = (LCSSourcingConfigMaster) sourcingConfigObj.getMaster();
		
		String existingPrimarySourceFlag = "";
		
		//Get a Collection of 'SourcingConfig' objects from the given Product, Iterate on each 'SourcingConfig' object to validate the 'Primary Source' flag and update to mark the status
		Collection<LCSSourcingConfig> sourcingConfigCollection = LCSSourcingConfigQuery.getSourcingConfigsForProduct(productObj);
		for(LCSSourcingConfig existingSourcingConfigObj : sourcingConfigCollection)
		{
			//Get an Existing object 'Primary Source' flag, validate and update an existing 'SourcingConfig' object to change the 'Primary Source' flag in order to maintain uniqueness
			//existingPrimarySourceFlag = (String)existingSourcingConfigObj.getValue(hbiPrimarySourceKey);
			//Wipro Team Upgrade
			existingPrimarySourceFlag =String.valueOf(existingSourcingConfigObj.getValue(hbiPrimarySourceKey));
			
			
			
			LCSSourcingConfigMaster existingSourcingConfigMasterObj = (LCSSourcingConfigMaster) existingSourcingConfigObj.getMaster();
			
			//
			if(!sourcingConfigMasterObj.equals(existingSourcingConfigMasterObj) && "true".equals(existingPrimarySourceFlag))
			{	//Setting primary flag to no for other sources
				setPrimarySourceFlagToFalse(existingSourcingConfigObj, existingSourcingConfigMasterObj);
			}
		}
		
		//Updating the LCSSourcingConfigMaster (which is derived from the given SourcingConfig Object) to set 'Primary Source' for Product as 'Yes' based on the user data at Source Level
		sourcingConfigMasterObj.setPrimarySource(true);
		LCSSourcingConfigLogic.persist(sourcingConfigMasterObj, true);
		
	}
	
	/**
	 * This function is using to update the given SourcingConfig and SourcingConfigMaster to change the 'Primary Source' flag from 'Yes to No' and persisting/saving the given objects 
	 * @param sourcingConfigObj - LCSSourcingConfig
	 * @param sourcingConfigMasterObj - LCSSourcingConfigMaster
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void setPrimarySourceFlagToFalse(LCSSourcingConfig sourcingConfigObj, LCSSourcingConfigMaster sourcingConfigMasterObj) throws WTException, WTPropertyVetoException
	{
		// logger.debug("### START HBIPrimarySourcingConfigPlugin.updateSourcingConfigPrimarySourceFlag(LCSSourcingConfig sourcingConfigObj) ###");
		
		//Updating an existing 'SourcingConfig' object to change 'Primary Source' flag from 'Yes' to 'No' and Persisting/Saving the 'SourcingConfig' object to reflect the changes
		logger.debug("Source to be set as false for primary "+sourcingConfigObj.getName());
		
		//Wipro Team Upgrade
		if(!VersionHelper.isCheckedOut(sourcingConfigObj)	)
     			  sourcingConfigObj=(LCSSourcingConfig) VersionHelper.checkout(sourcingConfigObj);
			  else
				  sourcingConfigObj=(LCSSourcingConfig )VersionHelper.getWorkingCopy(sourcingConfigObj);
		
		
		sourcingConfigObj.setValue(hbiPrimarySourceKey, false);
		LCSSourcingConfigLogic.persist(sourcingConfigObj, true);
		
		
		if(VersionHelper.isCheckedOut(sourcingConfigObj))
			VersionHelper.checkin(sourcingConfigObj);
		
		//Updating an existing 'SourcingConfigMaster' object to change 'Primary Source' flag from 'Yes' to 'No' and Persisting/Saving the 'SourcingConfig' object to reflect the changes
		sourcingConfigMasterObj.setPrimarySource(false);
		LCSSourcingConfigLogic.persist(sourcingConfigMasterObj, true);
		
		// logger.debug("### END HBIPrimarySourcingConfigPlugin.updateSourcingConfigPrimarySourceFlag(LCSSourcingConfig sourcingConfigObj) ###");
	}
	
	/**
	 * @param seasonObj - LCSSeason
	 * @param productObj - LCSProduct
	 * @param sourcingConfigObj - LCSSourcingConfig
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	public void changePrimaryFlagToYesForFirstSource( LCSProduct productObj, LCSSourcingConfig sourcingConfigObj) throws WTException, WTPropertyVetoException
	{
		// logger.debug("### START HBIPrimarySourcingConfigPlugin.validateAndUpdateSourcingConfigPrimarySource(LCSSeason seasonObj , LCSProduct productObj) ###");
		Collection<LCSSourcingConfig> sourcingConfigCollection = LCSSourcingConfigQuery.getSourcingConfigsForProduct(productObj);
		
		//Get 'Primary Source for Product' from the given Product and validate the Primary SourcingConfigLink, based on the validation status updating the given SourcingConfig object
		LCSSourcingConfig primarySourcingConfigObj = LCSSourcingConfigQuery.getPrimarySourceForProduct(productObj); 
		
		logger.debug("New add sourcingConfigCollection size " +sourcingConfigCollection.size());
		if(sourcingConfigCollection != null && sourcingConfigCollection.size() == 1 && primarySourcingConfigObj != null)
		{	logger.debug("New setting first source to primary on boolean " );
			sourcingConfigObj.setValue(hbiPrimarySourceKey, true);
			LCSSourcingConfigLogic.persist(sourcingConfigObj, true);
			
		}
		
	}
	
	/**
	 * This function is using to get Primary Source for a Season, validate with the context SourcingConfig and update the SourceToSeasonLink to change the primary flag from Yes to No
	 * @param seasonObj - LCSSeason
	 * @param productObj - LCSProduct
	 * @param primarySourcingConfigUniqueIdentifier - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void updateSourceToSeasonLinkPrimarySourceFlagToNo(LCSSeason seasonObj, LCSProduct productObj, LCSSourcingConfigMaster primarySourcingConfigMasterObj) throws WTException, WTPropertyVetoException
	{
		// logger.debug("### START HBIPrimarySourcingConfigPlugin.updateSourceToSeasonLinkPrimarySourceFlagToNo(seasonObj, productObj, primarySourcingConfigMasterObj) ###");
		LCSSourceToSeasonLink primarySTSL = null;
		
		//Validating the given Season and Product object then get PrimarySourceToSeasonLink ('Primary Source' for Season), which is using to change the 'Primary Source' flag
		if(seasonObj != null && productObj != null)
		{
			primarySTSL = LCSSourcingConfigQuery.getPrimarySourceToSeasonLink(productObj.getMaster(), seasonObj.getMaster());
		}
		logger.debug("ToNo sourceToSeasonLinkObj "+primarySTSL);
		//validating LCSSourceToSeasonLink object, get SourcingConfig from the LCSSourceToSeasonLink to validate with the current SourcingConfig and update LCSSourceToSeasonLink status
		if(primarySTSL != null)
		{
			LCSSourcingConfig sourcingConfigObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(primarySTSL.getSourcingConfigMaster());
			LCSSourcingConfigMaster currentSourcingConfigMasterObj = (LCSSourcingConfigMaster)sourcingConfigObj.getMaster();
			if(!primarySourcingConfigMasterObj.equals(currentSourcingConfigMasterObj))
			{
				//Get SourceToSeasonLinkMaster from SourceToSeasonLink, which is using to update 'Primary Source' for Season Flag from Yes to No as need to mark other source as Primary
				LCSSourceToSeasonLinkMaster sourceToSeasonLinkMasterObj = (LCSSourceToSeasonLinkMaster)primarySTSL.getMaster();
				sourceToSeasonLinkMasterObj.setPrimarySTSL(false);
				LCSSourcingConfigLogic.persist(sourceToSeasonLinkMasterObj, true);
			}
		}
		
		// logger.debug("### END HBIPrimarySourcingConfigPlugin.updateSourceToSeasonLinkPrimarySourceFlagToNo(seasonObj, productObj, primarySourcingConfigMasterObj) ###");
	}
	
	/**
	 * This function is using to get SourceToSeasonLink using SeasonMaster and SourcingConfigMaster then validate and update the SourceToSeasonLink 'Primary Source' Flag from No to Yes
	 * @param seasonObj - LCSSeason
	 * @param productObj - LCSProduct
	 * @param sourcingConfigMasterObj - LCSSourcingConfigMaster
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void updateSourceToSeasonLinkPrimarySourceFlagToYes(LCSSeason seasonObj, LCSProduct productObj, LCSSourcingConfigMaster sourcingConfigMasterObj) throws WTException, WTPropertyVetoException
	{
		// logger.debug("### START HBIPrimarySourcingConfigPlugin.updateSourceToSeasonLinkPrimarySourceFlagToYes(seasonObj, productObj, sourcingConfigMasterObj) ###");
		LCSSourceToSeasonLink sourceToSeasonLinkObj = null;
		
		//Validating the given Season Master and SourcingConfigMaster which are using to get SourceToSeasonLink for changing the 'Primary Source' for 'Season' flag from Yes to No
		if(sourcingConfigMasterObj != null && seasonObj != null && seasonObj.getMaster() != null)
		{
			sourceToSeasonLinkObj = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourcingConfigMasterObj, seasonObj.getMaster());
		}
		logger.debug("ToYes sourceToSeasonLinkObj "+sourceToSeasonLinkObj);
		//Validate the SourceToSeasonLink, get LCSSourceToSeasonLinkMaster and update the Primary flag 'Primary Source' for Season and Persist the SourceToSeasonLinkMaster object
		if(sourceToSeasonLinkObj != null)
		{
			//LCSSourceToSeasonLinkMaster sourceToSeasonLinkMasterObj = (LCSSourceToSeasonLinkMaster)sourceToSeasonLinkObj.getMaster();
			//sourceToSeasonLinkMasterObj.setPrimarySTSL(true);
			//LCSSourcingConfigLogic.persist(sourceToSeasonLinkMasterObj, true);
			SourcingConfigHelper.service.setAsPrimary(sourceToSeasonLinkObj);
		}
		
		// logger.debug("### END HBIPrimarySourcingConfigPlugin.updateSourceToSeasonLinkPrimarySourceFlagToYes(seasonObj, productObj, sourcingConfigMasterObj) ###");
	}
	
	//When an existing primary source, primary flag is set to false from true. Revert back to Yes.
	
	private void setPrimaryToYes(LCSSourcingConfig sourcingConfigObj) {
		try {
			sourcingConfigObj.setValue(hbiPrimarySourceKey, true);
			LCSSourcingConfigLogic.persist(sourcingConfigObj, true);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			
			e.printStackTrace();
		}
	}
	
	private void setPrimaryToNo(LCSSourcingConfig sourcingConfigObj) {
		try {
			sourcingConfigObj.setValue(hbiPrimarySourceKey, false);
			LCSSourcingConfigLogic.persist(sourcingConfigObj, true);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			
			e.printStackTrace();
		}
	}
	
	public static Collection<LCSSeason> getAllActiveSeasonsOfAProduct(LCSProduct prod)  {
		logger.debug("#######Started getAllActiveSeasonsOfAProduct Method ######");
		String masterida2 = FormatHelper.getNumericObjectIdFromObject((WTObject) prod.getMaster());
		LCSProduct product = null;
		Collection<LCSSeason> allSeasons = new ArrayList();
		try {
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		stmt.appendFromTable("LCSProduct", "product");
		stmt.appendSelectColumn("product", "ida2a2");
		stmt.appendOpenParen();
		stmt.appendCriteria(new Criteria("product", "ida3Masterreference", masterida2, Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", "latestIterationInfo", "1", Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", "versionida2versioninfo", "A", Criteria.NOT_EQUAL_TO));
		stmt.appendAnd();
		//Wipro Team Upgrade
		//stmt.appendCriteria(new Criteria("product", "ClassNameKeyB12", "wt.part.WTPartMaster", Criteria.EQUALS));
		
		stmt.appendCriteria(new Criteria("product", "ClassNameKeyB12", "com.lcs.wc.season.LCSSeasonMaster", Criteria.EQUALS));
		
		
		
		stmt.appendClosedParen();

		Vector output = LCSQuery.runDirectQuery(stmt).getResults();
		logger.debug("Prod Seasons Size.."+output.size());
		Iterator itr = output.iterator();
		while (itr.hasNext()) {

			FlexObject obj = (FlexObject) itr.next();
			
			product = (LCSProduct) LCSQuery
						.findObjectById("OR:com.lcs.wc.product.LCSProduct:" + obj.getData("PRODUCT.IDA2A2"));
			

			// Get the active season associated to product
			logger.debug("####### getAllActiveSeasonsOfAProduct product ###### "+product.getName());
			
			LCSSeason season = com.lcs.wc.season.SeasonProductLocator.getSeasonRev(product);
			logger.debug("####### getAllActiveSeasonsOfAProduct season ###### "+season.getName());
			LCSSeasonProductLink spLink = LCSSeasonQuery.findSeasonProductLink(product, season);
			if (spLink != null && !spLink.isSeasonRemoved()) {
				allSeasons.add(season);
			}
		}
		logger.debug("####### getAllActiveSeasonsOfAProduct allSeasons size ###### "+allSeasons.size());
		
		} catch (WTException e) {
			e.printStackTrace();
		}
		return allSeasons;

	}
	
}
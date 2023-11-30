package com.hbi.wc.season;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.SeasonGroup;
import com.lcs.wc.season.SeasonGroupLogic;
import com.lcs.wc.season.SeasonGroupQuery;
import com.lcs.wc.season.SeasonGroupToProductLink;
import com.lcs.wc.util.FormatHelper;
import org.apache.log4j.Logger;
import   wt.log4j.LogR;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBISeasonGroupToProductLinkCreationPlugin.java
 * 
 * This class contains a plug-in function and generic functions which are invoking on LCSSeasonProductLink (link type as PRODUCT) create event and this function will process the request
 * of adding a Product to the existing SeasonGroup object only if the Season and Product are of Selling Type, SeasonGroup's are configured only for the Season\Selling with default object
 * @author Abdul.Patel@Hanes.com
 * @since June-09-2016
 */
public class HBISeasonGroupToProductLinkCreationPlugin
{
	private static String gpOptionsSelectedStatus = LCSProperties.get("com.hbi.wc.season.HBIGPOptionsUniquenessValidation.gpOptionsSelectedStatus", "selected");
	private static final Logger logger = LogR.getLogger("com.hbi.wc.season.HBISeasonGroupToProductLinkCreationPlugin");
	/**
	 * This function is using as a plug-in function which is registered on LCSSeasonProductLink POST_CREATE_PERSIST EVENT which is using to validate the type path of the SeasonProductLink,
	 * for Selling Product and Season, add the Product to the default SeasonGroup (GP Options) object which internally creates SeasonGroupToProductLink to populate the link level attribute
	 * @param wtObj - WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void validateAndCreateSeasonGroupToProductLink(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		logger.debug("### START HBISeasonGroupToProductLinkCreationPlugin.validateAndCreateSeasonGroupToProductLink(WTObject wtObj) ###");
		String sellingProductFlexTypePath = LCSProperties.get("com.hbi.wc.season.HBISeasonGroupToProductLinkCreationPlugin.sellingProductTypePath", "Product\\BASIC CUT & SEW - SELLING");
		String sellingSeasonFlexTypePath = LCSProperties.get("com.hbi.wc.season.HBISeasonGroupToProductLinkCreationPlugin.sellingSeasonFlexTypePath", "Season\\Selling");
		
		//Validating the incoming WTObject and processing/returning based on the instance type of the WTObject (processing only if the instance is of type LCSSeasonProductLink Object
		if(!(wtObj instanceof LCSSeasonProductLink))
		{
			logger.debug("Returning without performing any action as the incoming object is not an instance of LCSSeasonProductLink, this plug-in is specific to LCSSeasonProductLink");
			return;
		}
		
		//Initializing SeasonProductLink object, get SeasonProductLink Type Path which is needed to validate along with the Season Removed flag and Event Type Flag (EffectSequence value)
		LCSSeasonProductLink seasonProductLinkObj = (LCSSeasonProductLink) wtObj;
		String productTypePath = seasonProductLinkObj.getFlexType().getFullNameDisplay(true);
		if(!seasonProductLinkObj.isSeasonRemoved() && seasonProductLinkObj.getEffectSequence() == 0 && "PRODUCT".equalsIgnoreCase(seasonProductLinkObj.getSeasonLinkType()))
		{
			LCSProduct productObj = (LCSProduct) seasonProductLinkObj.getOwner();
			LCSSeason seasonObj = (LCSSeason) VersionHelper.latestIterationOf(seasonProductLinkObj.getSeasonMaster());
			String seasonTypePath = seasonObj.getFlexType().getFullNameDisplay(true);
			
			//Product Carryover Event:- Calling a function which is using to validate the given data and carryover SeasonGroupToProductLink data from the previous season LineSheet
			if(sellingProductFlexTypePath.equalsIgnoreCase(productTypePath) && seasonTypePath.contains(sellingSeasonFlexTypePath) && seasonProductLinkObj.getCarriedOverFrom() != null)
			{
				new HBISeasonGroupToProductLinkCreationPlugin().validateAndCarryoverSeasonGroupToProductLink(seasonObj, productObj, seasonProductLinkObj);
			}
			//Product Create Event:- Calling a function which is using to validate the given data and create new SeasonGroupToProductLink based on the given Season and Product object
			else if(sellingProductFlexTypePath.equalsIgnoreCase(productTypePath) && seasonTypePath.contains(sellingSeasonFlexTypePath))
			{
				new HBISeasonGroupToProductLinkCreationPlugin().validateAndCreateSeasonGroupToProductLink(seasonObj, productObj);
			}
		}
		
	logger.debug("### END HBISeasonGroupToProductLinkCreationPlugin.validateAndCreateSeasonGroupToProductLink(WTObject wtObj) ###");
	}
	
	/**
	 * This function is using validate the SeasonGroupToProductLink status for the given Season & SeasonGroup Object, based on the validation status create SeasonGroupToProductLink object
	 * @param seasonObj - LCSSeason
	 * @param productObj - LCSProduct
	 * @return seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public SeasonGroupToProductLink validateAndCreateSeasonGroupToProductLink(LCSSeason seasonObj, LCSProduct productObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBISeasonGroupToProductLinkCreationPlugin.validateAndCreateSeasonGroupToProductLink(LCSSeason seasonObj, LCSProduct productObj) ###");
		String seasonGroupFlexTypePath = LCSProperties.get("com.hbi.wc.season.HBISeasonGroupAutoCreatePlugin.seasonGroupFlexTypePath", "Season Group\\Garment Development Options");
		SeasonGroupToProductLink seasonGroupToProductLinkObj = null;
		Collection<String> seasonGroupVID = new ArrayList<String>();
		Collection<String> productVID = new ArrayList<String>();
		
		//Calling a function which is using to get SeasonGroup Object for the given Season object and SeasonGroup Type Path (Assuming that we are having only one SeasonGroup in a Season)
		SeasonGroup seasonGroupObj = getSeasonGroupBySeasonAndFlexType(seasonObj, seasonGroupFlexTypePath);
		if(seasonGroupObj != null)
		{
			seasonGroupToProductLinkObj = SeasonGroupQuery.findSeasonGroupToProductLink(productObj, seasonGroupObj);
			if(seasonGroupToProductLinkObj == null)
			{
				//Formating the input parameters (SeasonGroup Version ID and Product Version ID)which are needed to create SeasonGroupToProductLink (a Link object using for 'GP Options' 
				String seasonGroupVersionID = FormatHelper.getVersionId(seasonGroupObj);
				String productVersionID = FormatHelper.getVersionId(productObj);
				seasonGroupVID.add(seasonGroupVersionID);
				productVID.add(productVersionID);
			
				//Calling a function which will create a new link for the given SeasonGroup and Product object (called as SeasonGroupToProductLink) and returning the newly created object
				new SeasonGroupLogic().addProductsToSeasonGroups(productVID, seasonGroupVID);
				seasonGroupToProductLinkObj = SeasonGroupQuery.findSeasonGroupToProductLink(productObj, seasonGroupObj);
			}
		}
		
		// LCSLog.debug("### END HBISeasonGroupToProductLinkCreationPlugin.validateAndCreateSeasonGroupToProductLink(LCSSeason seasonObj, LCSProduct productObj) ###");
		return seasonGroupToProductLinkObj;
	}
	
	/**
	 * This function is using on carryover event to create new SeasonGroupToProductLink for the given Season and SeasonGroup object and cascade attribute data from carriedOver to new SGPL
	 * @param seasonObj - LCSSeason
	 * @param productObj - LCSProduct
	 * @param seasonProductLinkObj - LCSSeasonProductLink
	 * @return seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public SeasonGroupToProductLink validateAndCarryoverSeasonGroupToProductLink(LCSSeason seasonObj, LCSProduct productObj, LCSSeasonProductLink seasonProductLinkObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBISeasonGroupToProductLinkCreationPlugin.validateAndCarryoverSeasonGroupToProductLink(LCSSeasonProductLink seasonProductLinkObj) ###");
		String seasonGroupFlexTypePath = LCSProperties.get("com.hbi.wc.season.HBISeasonGroupAutoCreatePlugin.seasonGroupFlexTypePath", "Season Group\\Garment Development Options");
		
		//Calling a function which is using to create SeasonGroupToProductLink for the given Season and Product object by assuming that we are having only one 'SeasonGroup' object
		SeasonGroupToProductLink seasonGroupToProductLinkObj = validateAndCreateSeasonGroupToProductLink(seasonObj, productObj);
		
		//Get CarriedOverFrom object from the given SeasonProductLink, initialize Season and Product object from the CarrivedOver Link (using the previous season SeasonProductLink)
		LCSSeasonProductLink previousSeasonProductLinkObj = seasonProductLinkObj.getCarriedOverFrom();
		LCSProduct previousProductObj = (LCSProduct) previousSeasonProductLinkObj.getOwner();
		LCSSeason previousSeasonObj = (LCSSeason) VersionHelper.latestIterationOf(previousSeasonProductLinkObj.getSeasonMaster());
		
		//Calling a function which is using to get SeasonGroup Object for the given Season object and SeasonGroup Type Path (Assuming that we are having only one SeasonGroup in a Season)
		SeasonGroup seasonGroupObj = getSeasonGroupBySeasonAndFlexType(previousSeasonObj, seasonGroupFlexTypePath);
		
		//Calling an existing out of the box function to get SeasonGroupToProductLink object for the given Season and SeasonGroup object, which is using for 'GP Options' data carryover
		SeasonGroupToProductLink previousSeasonGroupToProductLinkObj = SeasonGroupQuery.findSeasonGroupToProductLink(previousProductObj, seasonGroupObj);
		
		//Validating the two versions of a SeasonGroupToProductLink object (current version and carriedOverFrom version) and calling a function for data cascading from carriedOver to new
		if(seasonGroupToProductLinkObj != null && previousSeasonGroupToProductLinkObj != null)
		{
			seasonGroupToProductLinkObj = validateAndCarryoverSeasonGroupToProductLink(seasonGroupToProductLinkObj, previousSeasonGroupToProductLinkObj);
		}
		
		// LCSLog.debug("### END HBISeasonGroupToProductLinkCreationPlugin.validateAndCarryoverSeasonGroupToProductLink(LCSSeasonProductLink seasonProductLinkObj) ###");
		return seasonGroupToProductLinkObj;
	}
	
	/**
	 * This function is using to cascade the 'GP Options' and 'GP Options Selection Status' attributes data from the previous season LineSheet to the current or latest season LineSheet
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @param previousSeasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @return seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private SeasonGroupToProductLink validateAndCarryoverSeasonGroupToProductLink(SeasonGroupToProductLink seasonGroupToProductLinkObj, SeasonGroupToProductLink previousSeasonGroupToProductLinkObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBISeasonGroupToProductLinkCreationPlugin.validateAndCarryoverSeasonGroupToProductLink(seasonGroupToProductLinkObj, oldSeasonGroupToProductLinkObj) ###");
		String gpOptionsSelectionStatus = "";
		String garmentProductRefAttKey = "";
		LCSProduct garmentProductObj = null;
		
		//Calling a function to get a map (contains 'GP Options' attribute-key and 'GP Options Selection Status' attribute-key) for the given 'GP Options' mapping attributes
		Map<String, String> attributeKeysMap = new HBIProductToProductLinkValidation().getAttributeKeysMapfromPropertiesEntry(HBIProductToProductLinkValidation.gpOptionsMappingAttributeKeys);
		
		//Iterating on 'GP Options' and 'GP Options Selection Status' attribute (a total of 10 'GP Options' and corresponding 'GP Options Selection Status' attribute) to get the data
		for(String attributeKey : attributeKeysMap.keySet())
		{
			//Get 'GP Options' attribute-key from the map, fetch the associated 'GP Option' from the given SeasonGroupToProductLink, validate and update to the given link object
			garmentProductRefAttKey = attributeKeysMap.get(attributeKey);
			garmentProductObj = (LCSProduct) previousSeasonGroupToProductLinkObj.getValue(garmentProductRefAttKey);
			if(garmentProductObj != null)
			{
				seasonGroupToProductLinkObj.setValue(garmentProductRefAttKey, garmentProductObj);
			}
			
			//Get 'GP Options Selection Status' from the given SeasonGroupToProductLink, validate the 'GP Options Selection Status' data and update to the given link(sgpl) object
			gpOptionsSelectionStatus = (String)previousSeasonGroupToProductLinkObj.getValue(attributeKey);
			if(FormatHelper.hasContent(gpOptionsSelectionStatus))
			{
				seasonGroupToProductLinkObj.setValue(attributeKey, gpOptionsSelectionStatus);
			}
			
			//Validating the 'GP Options Selection Status' and Garment Product based on the status calling an internal function to create ProductToProductLink (as a Garment-Selling Link)
			if(gpOptionsSelectedStatus.equals(gpOptionsSelectionStatus) && garmentProductObj != null)
			{
				LCSProduct sellingProductObj = (LCSProduct) VersionHelper.latestIterationOf(seasonGroupToProductLinkObj.getProductMaster());
				new HBIProductToProductLinkValidation().validateAndCreateProductToProductLink(garmentProductObj, sellingProductObj);
			}
		}
		
		seasonGroupToProductLinkObj = (SeasonGroupToProductLink)SeasonGroupLogic.persist(seasonGroupToProductLinkObj, false);
		
		// LCSLog.debug("### END HBISeasonGroupToProductLinkCreationPlugin.validateAndCarryoverSeasonGroupToProductLink(seasonGroupToProductLinkObj, oldSeasonGroupToProductLinkObj) ###");
		return seasonGroupToProductLinkObj;
	}
	
	/**
	 * This function is using to get SeasonGroup object for the given Season Object and SeasonGroup FlexType Path (by assuming that we are having only one SeasonGroup Object in a Season)
	 * @param seasonObj - LCSSeason
	 * @param seasonGroupFlexTypePath - String
	 * @return seasonGroupObj - SeasonGroup
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	public SeasonGroup getSeasonGroupBySeasonAndFlexType(LCSSeason seasonObj, String seasonGroupFlexTypePath) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBISeasonGroupToProductLinkCreationPlugin.getSeasonGroupBySeasonAndFlexType(LCSSeason seasonObj, String seasonGroupFlexTypePath) ###");
		FlexType seasonGroupFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(seasonGroupFlexTypePath);
		SeasonGroup seasonGroupObj = null;
		
		//Get all SeasonGroup object based on the given Season Object and FlexType Hierarchy of the SeasonGroup, then iterate on each object to get the Specific SeasonGroup to return
		SearchResults results = new SeasonGroupQuery().findSeasonGroupsForSeason(seasonObj, seasonGroupFlexTypeObj);
		if(results != null && results.getResultsFound() > 0)
		{
			Collection<FlexObject> seasonGroupFlexObjectsColl = results.getResults();
			for(FlexObject flexObj : seasonGroupFlexObjectsColl)
			{
				//Get SeasonGroup object identifier (BRANCHIDITERATIONINFO) from the FlexObject to form the SeasonGroup, get latest iteration of the SeasonGroup to return from header
				seasonGroupObj = (SeasonGroup) LCSQuery.findObjectById("VR:com.lcs.wc.season.SeasonGroup:"+flexObj.getInt("SEASONGROUP.BRANCHIDITERATIONINFO"));
				if(seasonGroupObj != null)
				{
					seasonGroupObj = (SeasonGroup)VersionHelper.latestIterationOf(seasonGroupObj);
				}
			}
		}
		
		// LCSLog.debug("### END HBISeasonGroupToProductLinkCreationPlugin.getSeasonGroupBySeasonAndFlexType(LCSSeason seasonObj, String seasonGroupFlexTypePath) ###");
		return seasonGroupObj;
	}
}
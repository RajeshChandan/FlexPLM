package com.hbi.wc.season;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.SeasonGroupToProductLink;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import org.apache.log4j.Logger;
import   wt.log4j.LogR;
import com.lcs.wc.util.LCSProperties;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBIGPOptionsUniquenessValidation.java
 * 
 * This class contains a plug-in function and generic functions which are invoking on SeasonGroupToProductLink update event to validate the 'GP Option 1' to 'GP Option 10' selection status
 * to maintain the uniqueness within the 'GP Option Selection', updating the given SeasonGroupToProductLink(which is triggered from plug-in event) to manage uniqueness in case of duplicate
 * @author Abdul.Patel@Hanes.com
 * @since June-13-2016
 */
public class HBIGPOptionsUniquenessValidation
{
	private static String gpOptionsSelectedStatus = LCSProperties.get("com.hbi.wc.season.HBIGPOptionsUniquenessValidation.gpOptionsSelectedStatus", "selected");
	private static String gpOptionsAttributeKeys = "hbiIsItSelectedGPOption1,hbiIsItSelectedGPOption2,hbiIsItSelectedGPOption3,hbiIsItSelectedGPOption4,hbiIsItSelectedGPOption5,hbiIsItSelectedGPOption6,hbiIsItSelectedGPOption7,hbiIsItSelectedGPOption8,hbiIsItSelectedGPOption9,hbiIsItSelectedGPOption10";
	public static String techErrorMessageForSelectionStatus = LCSProperties.get("com.hbi.wc.season.HBIGPOptionsUniquenessValidation.techErrorMessageForSelectionStatus", "GPOPtionsSelectionStatusErroredOutDueToUniquenessValidationFail");
	public static String userErrorMessageForSelectionStatus = LCSProperties.get("com.hbi.wc.season.HBIGPOptionsUniquenessValidation.userErrorMessageForSelectionStatus", "More than one GP Option is selected for one Selling Product, Select one GP Option per row");
	public static String techErrorMessageForCarryoverGPChanges = LCSProperties.get("com.hbi.wc.season.HBIGPOptionsUniquenessValidation.techErrorMessageForCarryoverGPChanges", "ActionErroredOutDueToChangesMadeToCarryoverProductGPOPtions");
	public static String userErrorMessageForCarryoverGPChanges = LCSProperties.get("com.hbi.wc.season.HBIGPOptionsUniquenessValidation.userErrorMessageForCarryoverGPChanges", "Can not change 'GP Option' or 'GP Option Selection Status' for a CarriedOver Selling Product");
	private static final Logger logger = LogR.getLogger("com.hbi.wc.season.HBIGPOptionsUniquenessValidation");
	/**
	 * This function is using as a plug-in function which is registered on SeasonGroupToProductLink PRE_CREATE_PERSIST EVENT for 'GP Options Selection Status' field uniqueness validation
	 * @param wtObj - WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void validateAndUpdateGPOptionsAttribute(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		logger.debug("### START HBIGPOptionsUniquenessValidation.validateAndUpdateGPOptionsAttribute(WTObject wtObj) ###");
		String seasonGroupFlexTypePath = LCSProperties.get("com.hbi.wc.season.HBISeasonGroupAutoCreatePlugin.seasonGroupFlexTypePath", "Season Group\\Garment Development Options");
		
		//Validating the incoming WTObject and processing/returning based on the instance type of the WTObject (processing only if the instance is of type SeasonGroupToProductLink Object
		if(!(wtObj instanceof SeasonGroupToProductLink))
		{
		logger.debug("Returning without performing any action as the incoming object is not an instance of SeasonGroupToProductLink, this plug-in is on SeasonGroupToProductLink");
			return;
		}
		
		//Validating the incoming WTObject and processing/returning based on the removed status the WTObject (processing only if the SeasonGroupToProductLink Object is not removed
		if(((SeasonGroupToProductLink) wtObj).isRemoved())
		{
			logger.debug("Returning without performing any action as the incoming object is removed from the SeasonGroup object within a Season and Product context, skipping validation");
			return;
		}
		
		//Initializing SeasonGroupToProductLink object, validate the Type Path (as we need to skip the uniqueness validation for Line Board data) and 'GP Options' as there own sub-type 
		SeasonGroupToProductLink seasonGroupToProductLinkObj = (SeasonGroupToProductLink) wtObj;
		if(seasonGroupFlexTypePath.equalsIgnoreCase(seasonGroupToProductLinkObj.getFlexType().getFullNameDisplay(true)))
		{
			//Calling a function which is using to get the Attributes List<String> from the given properties entry (using as property entry key) and returning attributes List<String>
			List<String> attributesList = getAttributesListFromPropertiesFile(gpOptionsAttributeKeys);
			
			//Calling a function which is using to validate the 'SeasonGroupToProductLink Type' (whether it is created for a new Product object or for a CarriedOver Product object)
			seasonGroupToProductLinkObj = new HBIGPOptionsUniquenessValidation().validateAndUpdateGPOptionsAttribute(seasonGroupToProductLinkObj, attributesList);
		}
		
		logger.debug("### END HBIGPOptionsUniquenessValidation.validateAndUpdateGPOptionsAttribute(WTObject wtObj) ###");
	}
	
	/**
	 * This function is using to validate the product type (is a new Product or Carried-Over Product) then invoke the specific functions to validate the data as per defined business logic 
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @param attributesList - List<String> 
	 * @return seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private SeasonGroupToProductLink validateAndUpdateGPOptionsAttribute(SeasonGroupToProductLink seasonGroupToProductLinkObj, List<String> attributesList) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIGPOptionsUniquenessValidation.validateAndUpdateGPOptionsAttribute(SeasonGroupToProductLink seasonGroupToProductLinkObj, attributesList) ###");
		//LCSProduct productObj = (LCSProduct) VersionHelper.latestIterationOf(seasonGroupToProductLinkObj.getProductMaster());
		//WTPartMaster seasonMasterObj = seasonGroupToProductLinkObj.getSeasonGroupMaster().getSeasonMaster();
		
		//This block of code is using for the carried over products to validate the 'GP Option' attributes, flip the attribute data and print the suitable error message to the user
		/*if(productObj != null && productObj.getCarriedOverFrom() != null && !PersistenceHelper.isEquivalent(productObj.getCarriedOverFrom(), seasonMasterObj))
		{
			seasonGroupToProductLinkObj = validateAndFlipGPOptionsForCarryoverProduct(seasonGroupToProductLinkObj);
		}
		//This block of code is using to validate (enforcing uniqueness for 'GP Selection Status') and update the link for the Products which are created as a new product in this season
		else
		{*/
			//Calling a function which is using to validate the 'GP Options Selection Status' attribute value (for all 10 'GP Options Selection Status' attributes) and return status
			String uniquenessValidationStatus = new HBIGPOptionsUniquenessValidation().getGPOptionsUniquenessValidationStatus(seasonGroupToProductLinkObj, attributesList);
			if("Fail".equalsIgnoreCase(uniquenessValidationStatus))
			{
				//Calling a function which is using to validate the 'GP Options Selection Status' attribute, based on the validation status updating the given link to flip selection flag
				seasonGroupToProductLinkObj = new HBIGPOptionsUniquenessValidation().flipGPOptionsSelectionStatusForNewProduct(seasonGroupToProductLinkObj, attributesList);
				
				//Throwing an explicit Exception to interrupt the execution flow and give an error message in the line sheet explaining the reason for red-line in the line sheet
				throw new LCSException(techErrorMessageForSelectionStatus);
			}
		
			//Calling a function which is using to validate the and create(link) or update(DeLink) ProductToProductLink between 'Garment Product' and 'Selling Product' for a 'GP Options'
			new HBIProductToProductLinkValidation().validateAndCrateOrUpdateProductToProductLink(seasonGroupToProductLinkObj);
		//}
		
		// LCSLog.debug("### END HBIGPOptionsUniquenessValidation.validateAndUpdateGPOptionsAttribute(SeasonGroupToProductLink seasonGroupToProductLinkObj, attributesList) ###");
		return seasonGroupToProductLinkObj;
	}
	
	/**
	 * This function is using to validate the change in 'GP Options' or 'GP Options Selection Status' for a carried-over Product, based on the validation status return appropriate message
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @return seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	/*public SeasonGroupToProductLink validateAndFlipGPOptionsForCarryoverProduct(SeasonGroupToProductLink seasonGroupToProductLinkObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIGPOptionsUniquenessValidation.validateAndFlipGPOptionsForCarryoverProduct(SeasonGroupToProductLink seasonGroupToProductLinkObj) ###");
		SeasonGroupToProductLink oldSeasonGroupToProductLinkObj = getOldOrLatestSeasonGroupToProductLink(seasonGroupToProductLinkObj, false, false);
		String garmentProductRefAttKey = "";
		boolean gpOptionsChanged = false;
		
		//Calling a function to get a map (contains 'GP Options' attribute-key and 'GP Options Selection Status' attribute-key) for the given 'GP Options' mapping attributes
		Map<String, String> attributeKeysMap = new HBIProductToProductLinkValidation().getAttributeKeysMapfromPropertiesEntry(HBIProductToProductLinkValidation.gpOptionsMappingAttributeKeys);
		
		//Validating the old iteration of the given SeasonGroupToProductLink as we need to do this validation only on update of SeasonGroupToProductLink to check 'GP Options' changes
		if(oldSeasonGroupToProductLinkObj != null && attributeKeysMap != null && attributeKeysMap.size() > 0)
		{
			//
			boolean allowGPOptionChanges = allowGPOptionStatusChangesForCarryoverProduct(seasonGroupToProductLinkObj, oldSeasonGroupToProductLinkObj, attributeKeysMap);
			if(!allowGPOptionChanges)
			{
				//Iterating on 'GP Options' and 'GP Options Selection Status' attribute (a total of 10 'GP Options' and corresponding 'GP Options Selection Status' attribute) to get the data
				for(String attributeKey : attributeKeysMap.keySet())
				{
					garmentProductRefAttKey = attributeKeysMap.get(attributeKey);
					
					//Validate the change in 'GP Options' or 'GP Options Selection Status' for a carried-over Product, if their is change then print convey the message to the user in UI
					gpOptionsChanged = getGPOptionsChangeSetForCarryoverProduct(seasonGroupToProductLinkObj, oldSeasonGroupToProductLinkObj, garmentProductRefAttKey, attributeKey);
					if(gpOptionsChanged)
					{
						//Throwing an explicit Exception to interrupt the execution flow and give an error message in the line sheet explaining the reason for red-line in the line sheet
						throw new LCSException(techErrorMessageForCarryoverGPChanges);
					}
				}
			}
		}
		
		// LCSLog.debug("### END HBIGPOptionsUniquenessValidation.validateAndFlipGPOptionsForCarryoverProduct(SeasonGroupToProductLink seasonGroupToProductLinkObj) ###");
		return seasonGroupToProductLinkObj;
	}*/
	
	/*public boolean allowGPOptionStatusChangesForCarryoverProduct(SeasonGroupToProductLink seasonGroupToProductLinkObj, SeasonGroupToProductLink oldSeasonGroupToProductLinkObj, Map<String, String> attributeKeysMap) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIGPOptionsUniquenessValidation.allowGPOptionStatusChangesForCarryoverProduct(seasonGroupToProductLinkObj, oldSGPLObj,attributeKeysMap) ###");
		boolean allowGPOptionChanges = true;
		int gpOptionsSelectedStatusCount = 0;
		String currentGPOptionsSelectionStatus = "";
		String oldGPOptionsSelectionStatus = "";
		
		//Iterating on 'GP Options' and 'GP Options Selection Status' attribute (a total of 10 'GP Options' and corresponding 'GP Options Selection Status' attribute) to get the data
		for(String attributeKey : attributeKeysMap.keySet())
		{
			//
			currentGPOptionsSelectionStatus = (String) seasonGroupToProductLinkObj.getValue(attributeKey);
			if(FormatHelper.hasContent(currentGPOptionsSelectionStatus) && gpOptionsSelectedStatus.equalsIgnoreCase(currentGPOptionsSelectionStatus))
			{
				gpOptionsSelectedStatusCount = gpOptionsSelectedStatusCount + 1;
			}
			
			//
			oldGPOptionsSelectionStatus = (String) oldSeasonGroupToProductLinkObj.getValue(attributeKey);
			if(FormatHelper.hasContent(oldGPOptionsSelectionStatus) && gpOptionsSelectedStatus.equalsIgnoreCase(oldGPOptionsSelectionStatus))
			{
				allowGPOptionChanges = false;
			}
		}
		
		//Throwing an explicit Exception to interrupt the execution flow and give an error message in the line sheet explaining the reason for red-line in the line sheet
		if(gpOptionsSelectedStatusCount > 1)
		{
			throw new LCSException(techErrorMessageForSelectionStatus);
		}
		
		// LCSLog.debug("### END HBIGPOptionsUniquenessValidation.allowGPOptionStatusChangesForCarryoverProduct(seasonGroupToProductLinkObj, oldSGPLObj,attributeKeysMap) ###");
		return allowGPOptionChanges;
	}*/
	
	/**
	 * This function is using to validate the changeSet for 'GP Option' and 'GP Option Selection Status' and return the status for the given SeasonGroupToProductLink(two versions of link)
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @param oldSeasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @param gpOptionsAttKey - String
	 * @param gpOptionsStatusAttKey - String
	 * @return gpOptionsChanged - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	/*private boolean getGPOptionsChangeSetForCarryoverProduct(SeasonGroupToProductLink seasonGroupToProductLinkObj, SeasonGroupToProductLink oldSeasonGroupToProductLinkObj, String gpOptionsAttKey, String gpOptionsStatusAttKey) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIGPOptionsUniquenessValidation.getGPOptionsChangeSetForCarryoverProduct(sgplObj, oldSgplObj, gpOptionsAttKey, gpOptionsStatusAttKey) ###");
		boolean gpOptionsChanged = false;
		
		//Get 'GP Option' from the given SeasonGroupToProductLink (from current and old version of the link), validate as per the business logic and re-initialize gpOptionsChanged flag
		LCSProduct currentGarmentProductObj = (LCSProduct) seasonGroupToProductLinkObj.getValue(gpOptionsAttKey);
		LCSProduct oldGarmentProductObj = (LCSProduct) oldSeasonGroupToProductLinkObj.getValue(gpOptionsAttKey);
		if(currentGarmentProductObj != null && !PersistenceHelper.isEquivalent(currentGarmentProductObj, oldGarmentProductObj))
		{
			gpOptionsChanged = true;
		}
		else if(currentGarmentProductObj == null && oldGarmentProductObj != null)
		{
			gpOptionsChanged = true;
		}
		
		//Get 'GP Options Selection Status' from the given SeasonGroupToProductLink (from current and old version of the link), validate and and re-initialize gpOptionsChanged flag
		String currentGPOptionsSelectionStatus = (String) seasonGroupToProductLinkObj.getValue(gpOptionsStatusAttKey);
		String oldGPOptionsSelectionStatus = (String) oldSeasonGroupToProductLinkObj.getValue(gpOptionsStatusAttKey);
		if(gpOptionsSelectedStatus.equalsIgnoreCase(currentGPOptionsSelectionStatus) && !gpOptionsSelectedStatus.equalsIgnoreCase(oldGPOptionsSelectionStatus))
		{
			gpOptionsChanged = true;
		}
		else if(gpOptionsSelectedStatus.equalsIgnoreCase(oldGPOptionsSelectionStatus) && !gpOptionsSelectedStatus.equalsIgnoreCase(currentGPOptionsSelectionStatus))
		{
			gpOptionsChanged = true;
		}
		
		// LCSLog.debug("### END HBIGPOptionsUniquenessValidation.getGPOptionsChangeSetForCarryoverProduct(sgplObj, oldSgplObj, gpOptionsAttKey, gpOptionsStatusAttKey) ###");
		return gpOptionsChanged;
	}*/
	
	/**
	 * This function is using to validate the 'GP Options Selection Status' attribute value (for all 10 'GP Options Selection Status' attributes), return the uniqueness validation status
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @param attributesList - List<String>
	 * @return uniquenessValidationStatus - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public String getGPOptionsUniquenessValidationStatus(SeasonGroupToProductLink seasonGroupToProductLinkObj, List<String> attributesList) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIGPOptionsUniquenessValidation.getGPOptionsUniquenessValidationStatus(seasonGroupToProductLinkObj, List<String> attributesList) ###");
		String uniquenessValidationStatus = "Pass";
		String gpOptionsSelectionStatus = "";
		int gpOptionsSelectedStatusCount = 0;
		
		//Iterating on 'GP Options Selection Status' attribute (a total of 10 'GP Options' and corresponding 'GP Options Selection Status' attribute which are created at Selling Product
		for(String attributeKey : attributesList)
		{
			gpOptionsSelectionStatus = (String)seasonGroupToProductLinkObj.getValue(attributeKey);
			if(FormatHelper.hasContent(gpOptionsSelectionStatus) && gpOptionsSelectedStatus.equalsIgnoreCase(gpOptionsSelectionStatus))
			{
				gpOptionsSelectedStatusCount = gpOptionsSelectedStatusCount + 1;
			}
		}
		
		//Validating the number of attributes (where 'GP Option Selection Status' is marked as 'Selected'), if the count is more than one return the uniquenessValidationStatus as Fail
		if(gpOptionsSelectedStatusCount > 1)
		{
			uniquenessValidationStatus = "Fail";
		}
		
		// LCSLog.debug("### END HBIGPOptionsUniquenessValidation.getGPOptionsUniquenessValidationStatus(seasonGroupToProductLinkObj, List<String> attributesList) ###");
		return uniquenessValidationStatus;
	}
	
	/**
	 * This function is using to validate the 'GP Options Selection Status' attribute, based on the validation status updating the given SeasonGroupToProductLink to flip GP selection flag
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @param attributesList - List<String>
	 * @return seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public SeasonGroupToProductLink flipGPOptionsSelectionStatusForNewProduct(SeasonGroupToProductLink seasonGroupToProductLinkObj, List<String> attributesList) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIGPOptionsUniquenessValidation.flipGPOptionsSelectionStatusForNewProduct(seasonGroupToProductLinkObj, List<String> attributesList) ###");
		SeasonGroupToProductLink oldSeasonGroupToProductLinkObj = getOldOrLatestSeasonGroupToProductLink(seasonGroupToProductLinkObj, false, false);
		String gpOptionsSelectionStatus = "";
		String oldObjGPOptionsSelectionStatus = "";
		
		//Iterating on 'GP Options Selection Status' attribute (a total of 10 'GP Options' and corresponding 'GP Options Selection Status' attribute which are created at Selling Product
		for(String attributeKey : attributesList)
		{
			gpOptionsSelectionStatus = (String)seasonGroupToProductLinkObj.getValue(attributeKey);
			
			//Validating the 'GP Options Selection Status' attribute, if the selection status is 'Selected' then validate the previous iteration status and update the given link object
			if(FormatHelper.hasContent(gpOptionsSelectionStatus) && gpOptionsSelectedStatus.equalsIgnoreCase(gpOptionsSelectionStatus) && oldSeasonGroupToProductLinkObj != null)
			{
				oldObjGPOptionsSelectionStatus = (String)oldSeasonGroupToProductLinkObj.getValue(attributeKey);
				if(!gpOptionsSelectionStatus.equalsIgnoreCase(oldObjGPOptionsSelectionStatus))
				{
					seasonGroupToProductLinkObj.setValue(attributeKey, oldObjGPOptionsSelectionStatus);
				}
			}
			else if(FormatHelper.hasContent(gpOptionsSelectionStatus) && gpOptionsSelectedStatus.equalsIgnoreCase(gpOptionsSelectionStatus))
			{
				seasonGroupToProductLinkObj.setValue(attributeKey, "");
			}
		}
		
		// LCSLog.debug("### END HBIGPOptionsUniquenessValidation.flipGPOptionsSelectionStatusForNewProduct(seasonGroupToProductLinkObj, List<String> attributesList) ###");
		return seasonGroupToProductLinkObj;
	}
	
	/**
	 * This function is using to validate the input parameters (like needCreateEventLink or needLatestLink or needOldLink or needPreviousIterationLink) and return SeasonGroupToProductLink
	 * @param seasonGroupToProdLinkObj - SeasonGroupToProductLink
	 * @param needLatest - boolean
	 * @param needCreateEventLink - boolean
	 * @return seasonGroupToProdLinkObj - SeasonGroupToProductLink
	 * @throws WTException
	 */
	public SeasonGroupToProductLink getOldOrLatestSeasonGroupToProductLink(SeasonGroupToProductLink seasonGroupToProdLinkObj, boolean needLatest, boolean needCreateEventLink) throws WTException
	{
		// LCSLog.debug("### START HBIGPOptionsUniquenessValidation.getOldOrLatestSeasonGroupToProductLink(seasonGroupToProductLinkObj, needLatest, needCreateEventLink) ###");
		String roleAObjRef = FormatHelper.getNumericFromReference(seasonGroupToProdLinkObj.getRoleAObjectRef());
		String roleBObjRef = FormatHelper.getNumericFromReference(seasonGroupToProdLinkObj.getRoleBObjectRef());
		int effSeq = 0;
		
		//Validate the given flag (like needCreateEventLink or needLatestLink or needOldLink or needPreviousIterationLink) and re-initialize the effectSequence attribute value 
		if (needCreateEventLink)
		{
			effSeq = 0;			// Effect Sequence for create event object (SeasonGroupToProductLink Create Event record/row)
		}
		else if (needLatest)
		{
			effSeq = seasonGroupToProdLinkObj.getEffectSequence() + 1;		// Effect Sequence of latest Object, Assuming that the given object is one version older object
		}
		else
		{
			effSeq = seasonGroupToProdLinkObj.getEffectSequence() - 1;		// Effect Sequence of old Object, Assuming that the given object has at-least one iteration
		}

		//Calling an internal function which will take roleAObjectRef, roleBObjectRef and effectSequence as input parameter to return SeasonGroupToProductLink Object using to return
		String effectSequence = String.valueOf(effSeq);
		seasonGroupToProdLinkObj = getOldOrLatestSeasonGroupToProductLink(roleAObjRef, roleBObjRef, effectSequence);
		
		// LCSLog.debug("### END HBIGPOptionsUniquenessValidation.getOldOrLatestSeasonGroupToProductLink(seasonGroupToProductLinkObj, needLatest, needCreateEventLink) ###");
		return seasonGroupToProdLinkObj;
	}
	
	/**
	 * This function is using to get/return SeasonGroupToProductLink Object for the given input parameters(Like roleAObjectRef, roleBObjectRef, effectSequence) using PreparedQueryStatement
	 * @param roleAObjRef - String
	 * @param roleBObjRef - String
	 * @param effectSequence - String
	 * @return seasonGroupToProdLinkObj - SeasonGroupToProductLink
	 * @throws WTException
	 */
	private SeasonGroupToProductLink getOldOrLatestSeasonGroupToProductLink(String roleAObjRef, String roleBObjRef, String effectSequence) throws WTException
	{
		// LCSLog.debug("### START HBIGPOptionsUniquenessValidation.getOldOrLatestSeasonGroupToProductLink(String roleAObjRef, String roleBObjRef, String effectSequence) ###");
		SeasonGroupToProductLink seasonGroupToProdLinkObj = null;
		
		//Initializing the PreparedQueryStatement, which is using to get SeasonGroupToProductLink object based on the given set of parameters(Like roleAObject, roleBObject, effectSeq)
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendSelectColumn(new QueryColumn(SeasonGroupToProductLink.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendFromTable(SeasonGroupToProductLink.class);
		statement.appendCriteria(new Criteria(new QueryColumn(SeasonGroupToProductLink.class, "roleAObjectRef.key.id"), "?", "="), new Long(roleAObjRef));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(SeasonGroupToProductLink.class, "roleBObjectRef.key.id"), "?", "="), new Long(roleBObjRef));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(SeasonGroupToProductLink.class, "effectSequence"), "?", "="), new Long(effectSequence));
		
		//Get FlexObject Collection from the SearchResults instance, which is using to form/prepare SeasonGroupToProductLink instance, needed to return from the function header
		logger.debug("HBIGPOptionsUniquenessValidation.getOldOrLatestSeasonGroupToProductLink() Query to get SeasonGroupToProductLink FlexObject Collection = "+ statement);
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if(results != null && results.getResultsFound() > 0)
		{
			FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
			seasonGroupToProdLinkObj = (SeasonGroupToProductLink) LCSQuery.findObjectById("OR:com.lcs.wc.season.SeasonGroupToProductLink:"+flexObj.getString("SeasonGroupToProductLink.IDA2A2"));
		}
		
		// LCSLog.debug("### END HBIGPOptionsUniquenessValidation.getOldOrLatestSeasonGroupToProductLink(String roleAObjRef, String roleBObjRef, String effectSequence) ###");
		return seasonGroupToProdLinkObj;
	}
	
	/**
	 * This function is using to get the Attributes List<String> from the given properties entry (using as property entry key) and returning a List<String> contains all the attribute keys
	 * @param propertyEntryValue - String
	 * @return attributesList - List<String>
	 * @throws WTException
	 */
	public static List<String> getAttributesListFromPropertiesFile(String propertyEntryValue) throws WTException
	{
		// LCSLog.debug("### START HBIGPOptionsUniquenessValidation.getAttributesListFromPropertiesFile(String propertyEntryValue) ###");
		List<String> attributesList = new ArrayList<String>();
		
		//Validating the given 'Property Entry key' and converting the string into StringTokenizer using comma (,) as delimiter, using StringTokenizer object to iterate and get keys
		if (FormatHelper.hasContent(propertyEntryValue))
		{
			StringTokenizer strTokenAttributeKeysObj = new StringTokenizer(propertyEntryValue, ",");
			
			//Iterating through StringTokenizer object to get each Attribute-Key and adding new attribute-key to the List<String>, which is using to return from the function header
			while (strTokenAttributeKeysObj.hasMoreTokens())
			{
				String attributeKey = strTokenAttributeKeysObj.nextToken().trim();
				attributesList.add(attributeKey);
			}
		}
		
		// LCSLog.debug("### END HBIGPOptionsUniquenessValidation.getAttributesListFromPropertiesFile(String propertyEntryValue) ###");
		return attributesList;
	}
}
package com.hbi.wc.season;

import java.util.Collection;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.placeholder.Placeholder;
import com.lcs.wc.placeholder.PlaceholderHelper;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBIPlaceholderGenerator.java
 * 
 * This class contain functions which are using to fetch all the existing Placeholder data within a Season object and Product Type, subtracting the existing records with the given count
 * to form a new count which is using to Initialize new Placeholder within the given Season object and Product Type, formating the placeholder status message to indicate the create data
 * @author Abdul.Patel@Hanes.com
 * @since February-15-2016
 */
public class HBIPlaceholderGenerator
{
	private static String successfulProcessMsg = LCSProperties.get("com.hbi.wc.season.HBIPlaceholderGenerator.successfulProcessMsg", " Placeholders successfully generated.");
	private static String ProcessingErrorMsg = LCSProperties.get("com.hbi.wc.season.HBIPlaceholderGenerator.ProcessingErrorMsg", "Placeholders not generated.");
	
	/**
	 * This function is using to validate the given data (Season OID and Placeholder count), initialize the necessary parameters to create the Placeholder within the given context
	 * @param seasonOID - String
	 * @param placeholderCount - int
	 * @return statusMessage - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static String bulkPlaceholderGenerator(String seasonOID, int placeholderCount) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIPlaceholderGenerator.bulkPlaceholderGenerator(String seasonOID, int placeholderCount) ###");
		LCSLog.debug("HBIPlaceholderGenerator.bulkPlaceholderGenerator :: Season OID = "+ seasonOID +" and Placeholder Count = "+ placeholderCount);
		FlexType placeholderFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Product");
		Placeholder placeholderObj = null;
		
		//Initializing Season object from the given Season OID, which is using to get Product Type and Create Placeholder object within the given Season Object and Product Type
		LCSSeason seasonObj = (LCSSeason) LCSQuery.findObjectById(seasonOID);
		seasonObj = (LCSSeason) VersionHelper.latestIterationOf(seasonObj);
		FlexType productFlexTypeObj = seasonObj.getProductType();
		
		//Calling a function which is using to return 'Placeholder count', this count indicates the total number of records need to initialize within a Season and Product Type context
		placeholderCount = getExistingPlaceholderCount(seasonObj, productFlexTypeObj, placeholderCount);
		String placeholderStatus = placeholderCount + successfulProcessMsg;
		if(placeholderCount == 0)
		{
			placeholderStatus = ProcessingErrorMsg;
		}
		
		//Iterating on Placeholder count (this count is using to initialize total number of placeholder object within the given Season object and Product Type context
		for(int recordCount = 1; recordCount <= placeholderCount; recordCount++)
		{
			placeholderObj = Placeholder.newPlaceholder();
			placeholderObj.setFlexType(productFlexTypeObj);
			placeholderObj.setSeasonMaster(seasonObj.getMaster());
			
			placeholderObj = populateDefaultAttributevalues(placeholderObj, placeholderFlexTypeObj);
			PlaceholderHelper.service.savePlaceholder(placeholderObj);
		}

		// LCSLog.debug("### END HBIPlaceholderGenerator.bulkPlaceholderGenerator(String seasonOID, int placeholderCount) ###");
		return placeholderStatus;
	}
	
	/**
	 * This function is using to get all the Placeholder scope attributes from the given FlexType, validate the default value and populate each attribute default value to the Placeholder
	 * @param placeholderObj - Placeholder
	 * @param placeholderFlexTypeObj - FlexType
	 * @return placeholderObj - Placeholder
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	public static Placeholder populateDefaultAttributevalues(Placeholder placeholderObj, FlexType placeholderFlexTypeObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIPlaceholderGenerator.populateDefaultAttributevalues(Placeholder placeholderObj) ###");
		
		//Get all FlexTypeAttribute from the given FlexType object, Iterate on each attributes to validate the scope of the attribute as we need to process only Placeholder attributes
		Collection<FlexTypeAttribute> flexTypeAttributeCollection = placeholderFlexTypeObj.getAllAttributes();
		if(flexTypeAttributeCollection != null && flexTypeAttributeCollection.size() > 0)
		{
			for(FlexTypeAttribute flexTypeAttributeObj : flexTypeAttributeCollection)
			{
				//Validate the scope of the attribute continue only if the scope of the attribute is 'Placeholder'  as we need to get the default value of the Placeholder attributes
				if("Placeholder".equalsIgnoreCase(flexTypeAttributeObj.getAttScope()) && FormatHelper.hasContent(flexTypeAttributeObj.getAttDefaultValue()))
				{
					placeholderObj.setValue(flexTypeAttributeObj.getAttKey(), flexTypeAttributeObj.getAttDefaultValue());
				}
			}
		}
		
		// LCSLog.debug("### END HBIPlaceholderGenerator.populateDefaultAttributevalues(Placeholder placeholderObj) ###");
		return placeholderObj;
	}
	
	/**
	 * This function is using to fetch all the existing Placeholder for the given Season object and Product Type then subtract the record count with the given Placeholder count and return
	 * @param seasonObj - LCSSeason
	 * @param productFlexTypeObj - FlexType
	 * @param placeholderCount - int
	 * @return placeholderCount - int
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static int getExistingPlaceholderCount(LCSSeason seasonObj, FlexType productFlexTypeObj, int placeholderCount) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIPlaceholderGenerator.getExistingPlaceholderCount(LCSSeason seasonObj, FlexType productFlexTypeObj) ###");
		//String typeIdPath = String.valueOf(productFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
		String typeIdPath = productFlexTypeObj.getIdPath();
		String seasonMasterId = FormatHelper.getNumericObjectIdFromObject( seasonObj.getMaster());
		//Initializing the PreparedQueryStatement, which is using to get Placeholder object based on the given set of parameters(like FlexTypePath and Season Master ID)
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendSelectColumn(new QueryColumn(Placeholder.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendFromTable(Placeholder.class);
    	//Code Upgrade by Wipro Team
    	statement.appendCriteria(new Criteria(new QueryColumn(Placeholder.class, "flexTypeIdPath"), "?", Criteria.EQUALS), typeIdPath);
    	statement.appendAndIfNeeded();
    	statement.appendCriteria(new Criteria(new QueryColumn(Placeholder.class, "seasonMasterReference.key.id"), "?", "="), new Long(seasonMasterId));
    	
    	//Executing the PreparedQueryStatement to fetch Placeholder records for the given criteria, subtract the existing record with the given Placeholder count and return data count
    	SearchResults placeholderResults = LCSQuery.runDirectQuery(statement);
    	if(placeholderResults != null && placeholderResults.getResultsFound() > 0)
    	{
    		placeholderCount = placeholderCount - placeholderResults.getResultsFound();
    		if(placeholderCount <= 0)
    			placeholderCount = 0;
    	}
    	
		// LCSLog.debug("### END HBIPlaceholderGenerator.getExistingPlaceholderCount(LCSSeason seasonObj, FlexType productFlexTypeObj) ###");
		return placeholderCount;
	}
	
	/**
	 * This function is using to fetch all the existing Placeholder for the given Season OID (which is using to identify the existing Placeholder context), calling this function from JSP
	 * @param seasonOID - String
	 * @return placeholderCount - int
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static int getExistingPlaceholderCount(String seasonOID) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIPlaceholderGenerator.getExistingPlaceholderCount(LCSSeason seasonObj, FlexType productFlexTypeObj) ###");
		int placeholderCount = 0;
		
		//Initializing Season object from the given Season OID, which is using to get Product Type and Create Placeholder object within the given Season Object and Product Type
		LCSSeason seasonObj = (LCSSeason) LCSQuery.findObjectById(seasonOID);
		seasonObj = (LCSSeason) VersionHelper.latestIterationOf(seasonObj);
		FlexType productFlexTypeObj = seasonObj.getProductType();
		//Code Upgrade by Wipro Team
		//String typeIdPath = String.valueOf(productFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
		String typeIdPath =productFlexTypeObj.getIdPath();
		String seasonMasterId = FormatHelper.getNumericObjectIdFromObject( seasonObj.getMaster());
		//Initializing the PreparedQueryStatement, which is using to get Placeholder object based on the given set of parameters(like FlexTypePath and Season Master ID)
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendSelectColumn(new QueryColumn(Placeholder.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendFromTable(Placeholder.class);
    	//Code Upgrade by Wipro Team
    	statement.appendCriteria(new Criteria(new QueryColumn(Placeholder.class, "flexTypeIdPath"), "?", "="), typeIdPath);
    	statement.appendAndIfNeeded();
    	statement.appendCriteria(new Criteria(new QueryColumn(Placeholder.class, "seasonMasterReference.key.id"), "?", "="), new Long(seasonMasterId));
    	//Executing the PreparedQueryStatement to fetch Placeholder records for the given criteria, subtract the existing record with the given Placeholder count and return data count
    	SearchResults placeholderResults = LCSQuery.runDirectQuery(statement);
    	if(placeholderResults != null && placeholderResults.getResultsFound() > 0)
    	{
    		placeholderCount = placeholderResults.getResultsFound();
    	}
    	
		// LCSLog.debug("### END HBIPlaceholderGenerator.getExistingPlaceholderCount(LCSSeason seasonObj, FlexType productFlexTypeObj) ###");
		return placeholderCount;
	}
}
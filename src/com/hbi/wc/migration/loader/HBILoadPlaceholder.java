package com.hbi.wc.migration.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.placeholder.Placeholder;
import com.lcs.wc.placeholder.PlaceholderLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

import wt.method.RemoteAccess;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBILoadPlaceholder.java
 * 
 * This class contains generic functions which are using to get Placeholder object based on criteria's (like 'Placeholder Name' as unique parameter) and populate/update all the latest data
 * @author Abdul.Patel@Hanes.com
 * @since  August-12-2015
 */
public class HBILoadPlaceholder implements RemoteAccess
{
	private static String placeholderNameKey = LCSProperties.get("com.hbi.wc.migration.loader.HBILoadPlaceholder.placeholderNameKey", "placeholderName");
	//private static String productActivewearFlexTypes = LCSProperties.get("com.hbi.wc.migration.loader.HBILoadPlaceholder.productActivewearFlexTypes", "Product\\Activewear");
	//private static String placeholderLevelSkipAttributes = LCSProperties.get("com.hbi.wc.migration.loader.HBILoadPlaceholder.placeholderLevelSkipAttributes", "placeholderName,vrdColorwayCountPH");
	private static String productActivewearFlexTypes = LCSProperties.get("com.hbi.wc.migration.loader.HBILoadPlaceholder.productActivewearFlexTypes", "Product");
	private static String placeholderLevelSkipAttributes = LCSProperties.get("com.hbi.wc.migration.loader.HBILoadPlaceholder.placeholderLevelSkipAttributes", "placeholderName,hbiColorwayCountPH");
	
	/**
	 * This function is using as a custom loader invocation port, which internally invoking to get Placeholder object based on the 'Placeholder Name' and update the Placeholder changes
	 * @param dataValues - Hashtable<String,String>
	 * @param commandLine - Hashtable<String,String>
	 * @param returnObjects - Vector<String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static boolean loadPlaceholder(Hashtable<String,String> dataValues,Hashtable<String,String> commandLine, Vector<String> returnObjects) throws WTException, WTPropertyVetoException
	{
		LCSLog.debug("### START HBILoadPlaceholder.loadPlaceholder(Hashtable<String,String> dataValues,Hashtable<String,String> commandLine, Vector<String> returnObjects) ###");
		LCSLog.debug("HBILoadPlaceholder.loadPlaceholder(Hashtable<String,String> dataValues,Hashtable<String,String> commandLine, Vector<String> returnObjects) dataValues:: "+ dataValues);
		String placeholderName = dataValues.get(placeholderNameKey);
		LCSLog.debug("placeholderName" + placeholderName);
		String placeholderAttributeValue = "";
		
		//Validating the 'Placeholder Name' and calling an internal function to get Placeholder Object for the given 'Placeholder Name' which is using to update all the latest changes
		if(FormatHelper.hasContent(placeholderName))
		{
			Placeholder placeholderObj = new HBILoadPlaceholder().getPlaceholderObjectByName(placeholderName);
			if(placeholderObj != null)
			{
				LCSLog.debug("placeholderObj is not null");
				//Calling a function to get List<String> skipFlexTypeAttributeKeysList (Collection of Attributes which need to skip from update of Placeholder) from pre-defined path
				List<String> skipFlexTypeAttributeKeysList = new HBILoadPlaceholder().getFlexTypeAttributeKeysFromPropertiesFile(placeholderLevelSkipAttributes);
					
				//Iterating on each Attributes (Attributes registered in map file), get AttributeValue from the given Hashtable, update the latest changes & persist the Placeholder object
				for(String placeholderAttributeKey : dataValues.keySet())
				{
					placeholderAttributeValue = dataValues.get(placeholderAttributeKey);
					placeholderObj = new HBILoadPlaceholder().updateAndReturnPlaceholder(placeholderObj, placeholderAttributeKey, placeholderAttributeValue, skipFlexTypeAttributeKeysList);
				}
					
				PlaceholderLogic.deriveFlexTypeValues(placeholderObj);
				new PlaceholderLogic().savePlaceholder(placeholderObj);
			}		
		}
		
		LCSLog.debug("### END HBILoadPlaceholder.loadPlaceholder(Hashtable<String,String> dataValues,Hashtable<String,String> commandLine, Vector<String> returnObjects) ###");
		return true;
	}
	
	/**
	 * This function is using to validate the given Attribute-Key, format Attribute-Value as needed based on the types, validate the Attribute-Key with skip status and update the object
	 * @param placeholderObj - Placeholder
	 * @param placeholderAttributeKey - String
	 * @param placeholderAttributeValue - String
	 * @param skipFlexTypeAttributeKeysList - List<String>
	 * @return placeholderObj - Placeholder
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public Placeholder updateAndReturnPlaceholder(Placeholder placeholderObj, String placeholderAttributeKey, String placeholderAttributeValue, List<String> skipFlexTypeAttributeKeysList) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBILoadPlaceholder.updateAndReturnPlaceholder(placeholderObj, placeholderAttributeKey, placeholderAttributeValue, skipFlexTypeAttributeKeysList) ###");
		String attributeVariableType = placeholderObj.getFlexType().getAttribute(placeholderAttributeKey).getAttVariableType();
		boolean skipPlaceholderUpdate = false;
		
		//Validating the Attribute data type (is of type single list or multi list or composite or driven), re-initializing Attribute-Value based on the Attribute data type
		if(FormatHelper.hasContent(attributeVariableType) && ("choice".equalsIgnoreCase(attributeVariableType) || "moaList".equalsIgnoreCase(attributeVariableType) || "composite".equalsIgnoreCase(attributeVariableType) || "driven".equalsIgnoreCase(attributeVariableType)))
		{
			placeholderAttributeValue = getAttributeValueListKeyFromDisplayName(placeholderObj.getFlexType(), placeholderAttributeKey, placeholderAttributeValue);
		}
		
		//Validating the given Attribute Variable Type and invoking internal functions to validate, format and return 'Attribute value' from the function header to the calling function
		if(FormatHelper.hasContent(placeholderAttributeValue) && ("boolean".equalsIgnoreCase(attributeVariableType) || "object_ref".equalsIgnoreCase(attributeVariableType)))
		{
			//Calling a function which is using to format the attribute-value as per the attribute data type, update Placeholder object attribute value and return updated Placeholder
			placeholderObj = updateAndReturnPlaceholder(placeholderObj, placeholderAttributeKey, placeholderAttributeValue, attributeVariableType, skipFlexTypeAttributeKeysList);
			skipPlaceholderUpdate = true;
		}
		
		//Validating the given Attribute-Key to check with the pre-defined Attribute-List (List of Attributes defined as a variable with comma separated value) and skip/update Placeholder
		if(!skipPlaceholderUpdate && skipFlexTypeAttributeKeysList != null && skipFlexTypeAttributeKeysList.size() > 0 && !skipFlexTypeAttributeKeysList.contains(placeholderAttributeKey))
		{
			placeholderObj.setValue(placeholderAttributeKey, placeholderAttributeValue);
		}
		
		// LCSLog.debug("### END HBILoadPlaceholder.updateAndReturnPlaceholder(placeholderObj, placeholderAttributeKey, placeholderAttributeValue, skipFlexTypeAttributeKeysList) ###");
		return placeholderObj;
	}
	
	/**
	 * This function is using to validate the attribute-key, format attribute-value based on the data type, update the given Placeholder with latest attribute-values and return Placeholder
	 * @param placeholderObj - Placeholder
	 * @param placeholderAttributeKey - String
	 * @param placeholderAttributeValue - String
	 * @param attributeVariableType - String
	 * @param skipFlexTypeAttributeKeysList - List<String>
	 * @return placeholderObj - Placeholder
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public Placeholder updateAndReturnPlaceholder(Placeholder placeholderObj, String placeholderAttributeKey, String placeholderAttributeValue, String attributeVariableType, List<String> skipFlexTypeAttributeKeysList) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBILoadPlaceholder.updateAndReturnPlaceholder(placeholderObj, placeholderAttributeKey, placeholderAttributeValue, attributeVariableType, skipFlexTypeAttributeKeysList) ###");
		
		//Validating the given Attribute Type, formating the attribute value based on the attribute validation/type status and this block of code is applicable only for boolean data types
		if(FormatHelper.hasContent(placeholderAttributeValue) && "boolean".equalsIgnoreCase(attributeVariableType) && !skipFlexTypeAttributeKeysList.contains(placeholderAttributeKey))
		{
			if("Yes".equalsIgnoreCase(placeholderAttributeValue) || "true".equalsIgnoreCase(placeholderAttributeValue) || "Y".equalsIgnoreCase(placeholderAttributeValue))
			{
				placeholderAttributeValue = "true";
			}
			else if("No".equalsIgnoreCase(placeholderAttributeValue) || "false".equalsIgnoreCase(placeholderAttributeValue) || "N".equalsIgnoreCase(placeholderAttributeValue))
			{
				placeholderAttributeValue = "false";
			}
			
			placeholderObj.setValue(placeholderAttributeKey, placeholderAttributeValue);
		}
		
		//Validating the given Attribute Type, formating the attribute value based on the attribute validation/type status and this block of code is applicable for object reference types
		if(FormatHelper.hasContent(placeholderAttributeValue) && "object_ref".equalsIgnoreCase(attributeVariableType) && !skipFlexTypeAttributeKeysList.contains(placeholderAttributeKey))
		{
			//Validating the Attribute-Key (this block of code is specific to Product Object only), get Product object based on Product-Name and Product Hierarchy, update to Placeholder
			if("hbiReplacingStylePH".equalsIgnoreCase(placeholderAttributeKey))
			{
				LCSProduct productObj = new LCSProductQuery().findProductByNameType(placeholderAttributeValue, placeholderObj.getFlexType());
				if(productObj != null)
				{
					productObj = SeasonProductLocator.getProductARev(productObj);
					placeholderObj.setValue(placeholderAttributeKey, productObj);
				}
			}
		}
		
		// LCSLog.debug("### END HBILoadPlaceholder.updateAndReturnPlaceholder(placeholderObj, placeholderAttributeKey, placeholderAttributeValue, attributeVariableType, skipFlexTypeAttributeKeysList) ###");
		return placeholderObj;
	}
	
	/**
	 * This function is using to get Placeholder Object based on the given 'PlaceHolder Name', which is using to update with the given latest data (as Hashtable<String,String> dataValues)
	 * @param placeholderName - String
	 * @return placeholderObj - Placeholder
	 * @throws WTException
	 */
	public Placeholder getPlaceholderObjectByName(String placeholderName) throws WTException
	{
		LCSLog.debug("### START HBILoadPlaceholder.getPlaceholderObjectByName(String placeholderName) ###");
		Placeholder placeholderObj = null;
		FlexType productActivewearFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(productActivewearFlexTypes);
		String placeholderNameDBColumn = productActivewearFlexTypeObj.getAttribute(placeholderNameKey).getVariableName();
		
		//Initializing the PreparedQueryStatement, which is using to get Placeholder object based on the given set of parameters( Like 'Placeholder Name' as one of the unique parameter)
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendSelectColumn(new QueryColumn(Placeholder.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendFromTable(Placeholder.class);
		statement.appendCriteria(new Criteria(new QueryColumn(Placeholder.class, placeholderNameDBColumn), placeholderName, Criteria.EQUALS));
		
		//Get FlexObject from the SearchResults instance, which is using to form/prepare Placeholder instance, needed to return the object from the function header
		LCSLog.debug("HBILoadPlaceholder.getPlaceholderObjectByName(String placeholderName) Query to get Placeholder Object = "+ statement);
        SearchResults results = LCSQuery.runDirectQuery(statement);
        if(results != null && results.getResultsFound() > 0)
        {
        	LCSLog.debug(" Result is not null : "+ results);
        	FlexObject flexObj = (FlexObject)results.getResults().get(0);
        	placeholderObj = (Placeholder)LCSQuery.findObjectById("OR:com.lcs.wc.placeholder.Placeholder:"+ flexObj.getString("Placeholder.IDA2A2"));
        	LCSLog.debug(" PlaceholderObj from statement : "+ placeholderObj);
        }
		
		LCSLog.debug("### END HBILoadPlaceholder.getPlaceholderObjectByName(String placeholderName) ###");
		return placeholderObj;
	}
	
	/**
	 * This function is using to format and return List of FlextypeAttributeKeys for the given PropertyEntryValue(which contains set of FlexTypeAttribute Keys with comma separator)
	 * @param propertyEntryValue - String
	 * @return flexTypeAttributeKeysList - List<String>
	 */
	public List<String> getFlexTypeAttributeKeysFromPropertiesFile(String propertyEntryValue)
	{
		// LCSLog.debug("### START HBILoadPlaceholder.getFlexTypeAttributeKeysFromPropertiesFile(propertyEntryValue) ###");
		List<String> flexTypeAttributeKeysList = new ArrayList<String>();

		//Validating the given Property Entry Value(which contains set of FlexTypeAttribute Keys with comma as delimiter) and forming StringTokenizer instance
		if (FormatHelper.hasContent(propertyEntryValue))
		{
			StringTokenizer strTokenFlexTypeAttributeKeys = new StringTokenizer(propertyEntryValue, ",");

			//Iterating the StringTokenizer instance, get FlexTypeAttribute-Key, adding FlexTypeAttributeKey to the contains to return from the function header
			while (strTokenFlexTypeAttributeKeys.hasMoreTokens())
			{
				String flexTypeAttributeKey = strTokenFlexTypeAttributeKeys.nextToken().trim();
				flexTypeAttributeKeysList.add(flexTypeAttributeKey);
			}
		}

		// LCSLog.debug("### END HBILoadPlaceholder.getFlexTypeAttributeKeysFromPropertiesFile(propertyEntryValue) ###");
		return flexTypeAttributeKeysList;
	}
	
	/**
	 * This function is using to get AttributeValueList Key for the given FlexType object(contains all FlexTypeAttributes), FlexTypeAttribute-Key and AttributeValueList Display Name
	 * @param flexTypeObj - FlexType
	 * @param flexTypeAttributeKey - String
	 * @param attributeValueListDisplayName - String
	 * @return attributeValueListKey - String
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public String getAttributeValueListKeyFromDisplayName(FlexType flexTypeObj, String flexTypeAttributeKey, String attributeValueListDisplayName) throws WTException
	{
		// LCSLog.debug("### START HBILoadPlaceholder.getAttributeValueListKeyFromDisplayName(flexTypeObj, flexTypeAttributeKey, attributeValueListDisplayName) ###");
		String attributeValueListKey = "";
		String attributeValueListValue = "";
		Collection<FlexObject> attributeValueListCollection = flexTypeObj.getAttribute(flexTypeAttributeKey).getAttValueList().getDataSet();
		
		//Validate AttributeValueList Collection(contains given FlexTypeAttribute Keys and Values), Iterating on each FlexObject to get AttributeValueList Key and Display Name
		if(attributeValueListCollection != null && attributeValueListCollection.size() > 0)
		{
			for(FlexObject flexObj : attributeValueListCollection)
			{
				//Get AttributeValueList Key and Display Name from the given FlexObject, compare with the given AttributeValueList Display Name and return AttributeValueList Key
				attributeValueListValue = flexObj.getString("VALUE");
				if(FormatHelper.hasContent(attributeValueListValue) && attributeValueListValue.equalsIgnoreCase(attributeValueListDisplayName))
				{
					attributeValueListKey = flexObj.getString("KEY");
					break;
				}
			}
		}
		
		// LCSLog.debug("### END HBILoadPlaceholder.getAttributeValueListKeyFromDisplayName(flexTypeObj, flexTypeAttributeKey, attributeValueListDisplayName) ###");
		return attributeValueListKey;
	}
}
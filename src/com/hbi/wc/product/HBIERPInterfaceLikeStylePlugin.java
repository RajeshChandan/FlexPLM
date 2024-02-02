package com.hbi.wc.product;

import java.lang.reflect.InvocationTargetException;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Date;

import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import org.apache.log4j.Logger;
import   wt.log4j.LogR;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.util.FormatHelper;

import wt.fc.WTObject;
import wt.fc.ObjectReference;
import wt.vc.VersionControlHelper;
import wt.fc.PersistenceHelper;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBIERPInterfaceLikeStylePlugin.java
 *
 * This class contains plug-in function which are using to trigger on Product Persist event to get the data from ERP Products to populate on  newly created ERP Product.
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since March-16-2016
 */
public class HBIERPInterfaceLikeStylePlugin
{
	private static String businessObjectType = LCSProperties.get("com.hbi.wc.product.HBIERPInterfaceValidationPlugin.businessObjectType", "Business Object\\Integration\\Like Style Attributes");
	private static String businessObjectName = LCSProperties.get("com.hbi.wc.product.HBIERPInterfaceValidationPlugin.businessObjectName", "LikeStyleAttributes");
	private static String hbiValidationAttributesKey = LCSProperties.get("com.hbi.wc.product.HBIERPInterfaceValidationPlugin.hbiLikeStyleAttributesKey", "hbiLikeStyleAttributes");
	private static String hbiAttributeDataTypeKey = LCSProperties.get("com.hbi.wc.product.HBIERPInterfaceValidationPlugin.hbiAttributeDataTypeKey", "hbiAttributeDataType");
	private static String hbiAttributeKey = LCSProperties.get("com.hbi.wc.product.HBIERPInterfaceValidationPlugin.hbiAttributeKey", "hbiAttributeKey");
	private static String hbiScopeKey = LCSProperties.get("com.hbi.wc.product.HBIERPInterfaceValidationPlugin.hbiScopeKey", "hbiScope");
	private static final Logger logger = LogR.getLogger("com.hbi.wc.season.HBIERPInterfaceLikeStylePlugin");
	/**
	 * This function will invoke from custom plug-in entry which is registered on PRE_PERSIST event of LCSProduct of type 'ERP FINISHED & PRE-PACKS' to populate Product Hierarchy(B1) data
	 * @param wtObj - WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void copyAttributesFromERPStyle(WTObject wtObj) throws WTException, WTPropertyVetoException,NoSuchMethodException,InvocationTargetException,IllegalAccessException
	{
		logger.debug("### START HBIERPInterfaceLikeStylePlugin.copyAttributesFromERPStyle()###");
		HBIERPInterfaceLikeStylePlugin erpInterfaceLikeStylePluginObj = new HBIERPInterfaceLikeStylePlugin();
		if(wtObj instanceof LCSProduct)
		{
			LCSProduct targetProdObj = (LCSProduct) wtObj;
			logger.debug("### START HBIERPInterfaceLikeStylePlugin.copyAttributesFromERPStyle()###"+targetProdObj.getName());

			boolean firstVersion=VersionHelper.isFirstIterationOfAll(targetProdObj);
			logger.debug("### firstVersion###"+firstVersion);
			

			
				
      if(!(firstVersion && targetProdObj!=null && targetProdObj.getCopiedFrom()!=null )) {
    	  logger.debug("### entered if loop as its not a first version and not copied###"+firstVersion);

				LCSProduct sourceProdObj = (LCSProduct) targetProdObj.getValue("hbiErpLikeStyle");
				

				LCSLifecycleManaged businessObject = erpInterfaceLikeStylePluginObj.findValidationBOByNameAndType(businessObjectName, businessObjectType);

				Map<String, Map<String, String>> validationAttributesMap = erpInterfaceLikeStylePluginObj.getValidationAttributesMap(businessObject);

				erpInterfaceLikeStylePluginObj.getFlexObjectAttributesValidationStatus(sourceProdObj,targetProdObj,validationAttributesMap);
      }
		}	
		logger.debug("### END HBIERPInterfaceLikeStylePlugin.copyAttributesFromERPStyle() ###");
	}
	
	
	/**
	 * This function is using to get LCSLifecycleManaged object for the given business object name and type path, this business object contains all the validation attributes of APS & SAP
	 * @param businessObjectName - String
	 * @param businessObjectTypePath - String
	 * @return businessObject - LCSLifecycleManaged
	 * @throws WTException
	 */
	public LCSLifecycleManaged findValidationBOByNameAndType(String businessObjectName, String businessObjectTypePath) throws WTException
	{
		// LCSLog.debug("### START HBIERPInterfaceValidationPlugin.findValidationBOByNameAndType(String businessObjectName, String businessObjectPath) ###");
		LCSLifecycleManaged businessObject = null;
		FlexType boFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjectTypePath); 
		
		//Wipro Team Upgrade
		//String nameDBColumn = boFlexTypeObj.getAttribute("name").getVariableName();
		String nameDBColumn = boFlexTypeObj.getAttribute("name").getColumnDescriptorName();
	
		//String typeIdPath = String.valueOf(boFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
		String typeIdPath = String.valueOf(boFlexTypeObj.getIdPath());
		
		
		//Initializing the PreparedQueryStatement, which is using to get LCSLifecycleManaged object based on the given set of parameters(like FlexTypePath of the object and unique name)
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendFromTable(LCSLifecycleManaged.class);
    	statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendAndIfNeeded();
    	statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, nameDBColumn), businessObjectName, Criteria.EQUALS));
    	statement.appendAndIfNeeded();
    	statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeIdPath"), "?", "="),typeIdPath);
		
    	//Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSLifecycleManaged object, needed to return to the calling function
        SearchResults results = LCSQuery.runDirectQuery(statement);
        if(results != null && results.getResultsFound() > 0)
        {
        	FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
        	
        	businessObject = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+flexObj.getString("LCSLifecycleManaged.IDA2A2"));
		}
    	
		// LCSLog.debug("### END HBIERPInterfaceValidationPlugin.findValidationBOByNameAndType(String businessObjectName, String businessObjectPath) ###");
		
        return businessObject;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////					Following functions are using to get All Validation Attributes from Look-up Table					//////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This function is using as validation attributes map for the given business object instance (a map which contains FlexObject, attribute and attribute-data types)
	 * @param businessObject - LCSLifecycleManaged
	 * @return validationAttributesMap - Map<String, Map<String, String>>
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, String>> getValidationAttributesMap(LCSLifecycleManaged businessObject) throws WTException
	{
		Map<String, Map<String, String>> validationAttributesMap = new HashMap<String, Map<String,String>>();
		Map<String, String> productAttributeMap = new HashMap<String, String>();
		String attributeKey = "";
		String attributeDataType = "";
		String attributeScope = "";
		
		//Get validation table from the given business object, get all rows from the validation table, fetch the data for required attributes which are a part of initial validation
		LCSMOATable validationAttributesMOA = (LCSMOATable) businessObject.getValue(hbiValidationAttributesKey);
		if(validationAttributesMOA != null && validationAttributesMOA.getRows() != null && validationAttributesMOA.getRows().size() > 0)
		{
			Collection<FlexObject> validationAttributesColl = validationAttributesMOA.getRows();
			for(FlexObject flexObj : validationAttributesColl)
			{
				attributeKey = flexObj.getString(hbiAttributeKey.toUpperCase());
				attributeDataType = flexObj.getString(hbiAttributeDataTypeKey.toUpperCase());
				attributeScope = flexObj.getString(hbiScopeKey.toUpperCase());
				
				//validating the attribute scope (actual FlexObject where attribute resides), required flag, attribute-key  and attribute-data type which are using validation criteria
				if("Product".equals(attributeScope) && FormatHelper.hasContent(attributeKey) && FormatHelper.hasContent(attributeDataType))
				{
					productAttributeMap.put(attributeKey, attributeDataType);
				}
				
			}
			
			//Adding all the map instances (map contains each attribute-key and attribute data type, which are using for data validation from actual FlexObject) to the parent map
			validationAttributesMap.put("Product", productAttributeMap);
			
		}
		return validationAttributesMap;
	}
	
	/**
	 * This function is using to iterate on the attributes map, get attribute-value from the given FlexObject, validate attribute value in comparison with default value and return status
	 * @param wtObj - WTObject
	 * @param validationAttributes - Map<String, String>
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void getFlexObjectAttributesValidation(LCSProduct sourceProdObj,LCSProduct targetProdObj, Map<String, String> validationAttributes) throws WTException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,WTPropertyVetoException
	{
		String attributeDataTypeName= "";

		//Iterating on each Attribute, get the data type of attribute which is using in data down casting, validating/comparing actual data in comparison with the default values
		for(String attributeKey : validationAttributes.keySet())
		{
			attributeDataTypeName = validationAttributes.get(attributeKey);
			ObjectReference objRef = VersionControlHelper.getPredecessor(targetProdObj);

			if(sourceProdObj != null)
			{	
				if(objRef != null)
				{

					LCSProduct oldProductObj = (LCSProduct) objRef.getObject();
					if(oldProductObj!=null){
					LCSProduct sourceOldProdObj = (LCSProduct)oldProductObj.getValue("hbiErpLikeStyle");
					
					 if(!PersistenceHelper.isEquivalent(sourceProdObj, sourceOldProdObj))
					 {	
						//Calling a function which is using to validate the given attribute data (comparing the actual data with the default values) and populating the missing attributes set
						getAllAttributeValidationStatus(attributeKey,attributeDataTypeName,sourceProdObj,targetProdObj);
					 }	
					 }
				}
				else
				{

					getAllAttributeValidationStatus(attributeKey,attributeDataTypeName,sourceProdObj,targetProdObj);
				}	
			}
			else
			{
				if(objRef != null)
				{
					LCSProduct oldProductObj = (LCSProduct) objRef.getObject();
					LCSProduct sourceOldProdObj = (LCSProduct)oldProductObj.getValue("hbiErpLikeStyle");
					if(sourceOldProdObj != null)
					{
						getAllAttributeValidationStatus(attributeKey,attributeDataTypeName,targetProdObj);
					}
				}
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////					Following functions are using to perform data validation from all the reference objects				//////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This function is using to validate each associated object data for all the corresponding attributes and format/prepare missing attributes set using to return from function header
	 * @param seasonProductLinkObj - LCSSeasonProductLink
	 * @param validationAttributesMap - Map<String, Map<String, String>>
	 * @return missingAttributes - String
	 * @throws WTException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void getFlexObjectAttributesValidationStatus(LCSProduct sourceProdObj,LCSProduct targetProdObj, Map<String, Map<String, String>> validationAttributesMap) throws WTException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,WTPropertyVetoException
	{
		//This block of code is to validate the Product Level Attributes data.
		getFlexObjectAttributesValidation(sourceProdObj,targetProdObj, validationAttributesMap.get("Product"));
	}
	
	/**
	 * This function is using to validate each required attribute value (attribute required for APS and SAP validation) with the default values and populating the missing attributes set
	 * @param attributeKey - String
	 * @param attributeDataType - String
	 * @param objAttributeValue - Object
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	public void getAllAttributeValidationStatus(String attributeKey, String attributeDataType,LCSProduct sourceProdObj,LCSProduct targetProdObj) throws WTException,WTPropertyVetoException
	{
		
		if("Single List".equals(attributeDataType) || "Multi List".equals(attributeDataType) || "Text".equals(attributeDataType) || "Text Area".equals(attributeDataType) || "Composite".equals(attributeDataType) || "Driven".equals(attributeDataType) || "Derived String".equals(attributeDataType))
		{
			String objectStringValue = (String)sourceProdObj.getValue(attributeKey);
			if(FormatHelper.hasContent(objectStringValue))
			{
				targetProdObj.setValue(attributeKey,objectStringValue);
			}
			else
			{
				targetProdObj.setValue(attributeKey,"");
			}	
		}
		else if("Float".equals(attributeDataType) || "Integer".equals(attributeDataType) || "Currency".equals(attributeDataType) || "Sequence".equals(attributeDataType))
		{
			double objectStringValue = (Double)sourceProdObj.getValue(attributeKey);
			if(objectStringValue != 0.0)
			{
				targetProdObj.setValue(attributeKey,objectStringValue);
			}
		}
		else if("Boolean".equals(attributeDataType))
		{
			//String objectStringValue = (String)sourceProdObj.getValue(attributeKey);
			String objectStringValue = String.valueOf(sourceProdObj.getValue(attributeKey));
			if(FormatHelper.hasContent(objectStringValue))
			{
				targetProdObj.setValue(attributeKey,objectStringValue);	
			}	
		}
		else if("Date".equals(attributeDataType))
		{
			Date objectStringValue = (Date)sourceProdObj.getValue(attributeKey);
			if(objectStringValue != null)
			{
				targetProdObj.setValue(attributeKey,objectStringValue);		
			}	
		}
		else if("Object Reference".equals(attributeDataType) || "Object Reference List".equals(attributeDataType))
		{
			WTObject objectStringValue = (WTObject)sourceProdObj.getValue(attributeKey);
			if(objectStringValue != null)
			{
				targetProdObj.setValue(attributeKey,objectStringValue);		
			}	
		}
		else if("User List".equals(attributeDataType))
		{
			Object objectStringValue = (Object)sourceProdObj.getValue(attributeKey);
			if(objectStringValue != null)
			{
				targetProdObj.setValue(attributeKey,objectStringValue);		
			}	
		}
	}
	
	
	/**
	 * This function is using to validate each required attribute value (attribute required for APS and SAP validation) with the default values and populating the missing attributes set
	 * @param attributeKey - String
	 * @param attributeDataType - String
	 * @param objAttributeValue - Object
	 * @param missingAttributes - String
	 * @return missingAttributes - String
	 * @throws WTException
	 */
	public void getAllAttributeValidationStatus(String attributeKey, String attributeDataType,LCSProduct targetProdObj) throws WTException,WTPropertyVetoException
	{
		
		if("Single List".equals(attributeDataType) || "Multi List".equals(attributeDataType) || "Text".equals(attributeDataType) || "Text Area".equals(attributeDataType) || "Composite".equals(attributeDataType) || "Driven".equals(attributeDataType) || "Derived String".equals(attributeDataType))
		{
			targetProdObj.setValue(attributeKey,"");
		}
		else if("Float".equals(attributeDataType) || "Integer".equals(attributeDataType) || "Currency".equals(attributeDataType) || "Sequence".equals(attributeDataType))
		{
			targetProdObj.setValue(attributeKey,0.0);
		}
		else if("Boolean".equals(attributeDataType))
		{
			targetProdObj.setValue(attributeKey,"");	
		}
		else if("Date".equals(attributeDataType))
		{
			targetProdObj.setValue(attributeKey,"");			
		}
		else if("Object Reference".equals(attributeDataType) || "Object Reference List".equals(attributeDataType))
		{
			targetProdObj.setValue(attributeKey,null);		
		}
		else if("User List".equals(attributeDataType))
		{
			targetProdObj.setValue(attributeKey,null);	
		}
	}

}	
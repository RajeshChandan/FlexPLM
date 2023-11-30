package com.hbi.wc.interfaces.inbound.global.material.server;

import java.util.Map;

import wt.util.WTException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;

/**
 * HBIMaterialQuery.java
 *
 * Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object data and unique id's).
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-14-2018
 */
public class HBIMaterialQuery
{
	/**
	 * Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object data and unique id's).
	 * @param materialCriteriaMap - Map<String, Object>
	 * @throws WTException
	 */
	public LCSMaterial findMaterialByMaterialSKUAndMaterialType(Map<String, String> materialCriteriaMap, String materialFlexTypePath) throws WTException
	{
		LCSLog.debug("### START HBIMaterialQuery.findMaterialByMaterialSKUAndMaterialType(Map<String, String> materialCriteriaMap, materialFlexTypePath) ###");
		
		//Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object data and unique id's)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
	    statement.appendFromTable(LCSMaterial.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "?", "="), "1");
	    
	    //This condtion is used to validate  the given Material Type with the system.
	    if(FormatHelper.hasContent(materialFlexTypePath))
	    {
	    	FlexType materialFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(materialFlexTypePath);
	    	String typeIdPath = String.valueOf(materialFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
	    	statement.appendAndIfNeeded();
	    	statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "flexTypeReference.key.id"), "?", "="), new Long(typeIdPath));
	    }
	    
    	//This function is used to find material Object based on Material SKU attributes (Attribute Code,Color Code and Size Code).
	    LCSMaterial materialObj = findMaterialByMaterialSKUAndMaterialType(materialCriteriaMap, statement);
		
		LCSLog.debug("### END HBIMaterialQuery.findMaterialByMaterialSKUAndMaterialType(Map<String, String> materialCriteriaMap, materialFlexTypePath) ###");
		return materialObj;
	}
	
	/**
	 * This function is used to find material Object based on Material SKU attributes (Attribute Code,Color Code and Size Code).
	 * @param materialCriteriaMap - Map<String, String>
	 * @param statement - PreparedQueryStatement
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 */
	private LCSMaterial findMaterialByMaterialSKUAndMaterialType(Map<String, String> materialCriteriaMap, PreparedQueryStatement statement) throws WTException
	{
		// LCSLog.debug("### START HBIMaterialQuery.findMaterialByMaterialSKUAndMaterialType(Map<String, String> materialCriteriaMap, statement) ###");
		FlexType materialFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Material\\Material SKU");
		LCSMaterial materialObj = null;
		String criteriaDBColumnName = "";
		String criteriaValue = "";
		
		//
		for(String criteriaKey : materialCriteriaMap.keySet())
		{
			criteriaValue = materialCriteriaMap.get(criteriaKey);
			criteriaDBColumnName = materialFlexTypeObj.getAttribute(criteriaKey).getColumnDescriptorName();
			
			statement.appendAndIfNeeded();
		    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, criteriaDBColumnName), criteriaValue, Criteria.EQUALS));
		}
		
		//Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSMaterial object and return the LCSMaterial object
	    SearchResults results = LCSQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() > 0)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().iterator().next();
	  		materialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMaterial.BRANCHIDITERATIONINFO"));
	  	}
		
		// LCSLog.debug("### END HBIMaterialQuery.findMaterialByMaterialSKUAndMaterialType(Map<String, String> materialCriteriaMap, statement) ###");
		return materialObj;
	}
}
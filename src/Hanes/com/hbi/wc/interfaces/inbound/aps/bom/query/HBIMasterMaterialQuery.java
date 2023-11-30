package com.hbi.wc.interfaces.inbound.aps.bom.query;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

/**
 * HBIMasterMaterialQuery.java
 *
 * This class contains specific and generic functions which are using to fetch 'Material object' based on the given Material Name, Attribute Code, Color Code and Size Code, using material
 * SKU attribute to query material record, HBI Material Sku attribute is same across all types of material, considering attribute default value and color default value to retrieve material
 * @author Abdul.Patel@Hanes.com
 * @since November-20-2017
 */
public class HBIMasterMaterialQuery
{
	private static String hbiMaterialSKUKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.query.HBIMasterMaterialQuery.hbiMaterialSKUKey", "hbi17DigitSKU");

	/**
	 * This function is using to get Material object for the given criteria (like Material Name, Attribute Code, Color Code, Size Code) and return LCSMaterial object from function header
	 * @param materialName - String
	 * @param colorCode - String
	 * @param attributeCode - String
	 * @param sizeCode - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial getMaterialObjectForCriteria(String materialName, String colorCode, String attributeCode, String sizeCode) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMasterMaterialQuery.getMaterialObjectForCriteria(String materialName, String colorCode, String attributeCode, String sizeCode) ###");
		String hbiMaterialSKUDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute(hbiMaterialSKUKey).getColumnDescriptorName();
		String hbiMaterialSKU = materialName.concat(" ").concat(colorCode).concat(" ").concat(attributeCode).concat(" ").concat(sizeCode);
		String hbiMaterialSKUWithDefColorCode = materialName.concat(" ").concat("").concat(" ").concat(attributeCode).concat(" ").concat(sizeCode);
		String hbiMaterialSKUWithDefAttrCode1 = materialName.concat(" ").concat(colorCode).concat(" ").concat("").concat(" ").concat(sizeCode);
		String hbiMaterialSKUWithDefAttrCode2 = materialName.concat(" ").concat(colorCode).concat(" ").concat("DYEZZZ").concat(" ").concat(sizeCode);
		LCSMaterial materialObj = null;
		
		//This block of code is explicitly written for ATT86 index, we have created index on ATT86 using UPPER char, this is done to manage material SKU uniqueness check performance issue
		String queryTableName = "UPPER(".concat("LCSMaterial");
		hbiMaterialSKUDBColumn = hbiMaterialSKUDBColumn.concat(")");
		
		//Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object and unique parameters)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
	    statement.appendFromTable(LCSMaterial.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "?", "="), new Long(1));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "c/i", Criteria.EQUALS));
	    statement.appendAndIfNeeded();
	    statement.appendOpenParen();
	    statement.appendCriteria(new Criteria(new QueryColumn(queryTableName, hbiMaterialSKUDBColumn), hbiMaterialSKU, Criteria.EQUALS));
	    statement.appendOrIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(queryTableName, hbiMaterialSKUDBColumn), hbiMaterialSKUWithDefColorCode, Criteria.EQUALS));
	    statement.appendOrIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(queryTableName, hbiMaterialSKUDBColumn), hbiMaterialSKUWithDefAttrCode1, Criteria.EQUALS));
	    statement.appendOrIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(queryTableName, hbiMaterialSKUDBColumn), hbiMaterialSKUWithDefAttrCode2, Criteria.EQUALS));
	    statement.appendClosedParen();
	    
	    //Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSMaterial instance/object and return the LCSMaterial object from the function
	  	SearchResults results = LCSQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() > 0)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().iterator().next();
	  		materialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMaterial.BRANCHIDITERATIONINFO"));
	  		materialObj = (LCSMaterial) VersionHelper.latestIterationOf(materialObj);
	  	}
	    
		// LCSLog.debug("### END HBIMasterMaterialQuery.getMaterialObjectForCriteria(String materialName, String colorCode, String attributeCode, String sizeCode) ###");
		return materialObj;
	}
}
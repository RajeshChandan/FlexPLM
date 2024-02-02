package com.hbi.wc.material;

import com.lcs.wc.db.Criteria;

import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;

import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;


import com.lcs.wc.util.LCSException;

import org.apache.log4j.Logger;
import   wt.log4j.LogR;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HBIDuplicateMaterialSKUCheck {
	private static final Logger logger = LogR.getLogger("com.hbi.wc.material.HBIDuplicateMaterialSKUCheck");

	public static void materialCheck(WTObject obj) throws WTException,WTPropertyVetoException,LCSException{
		
		if(obj instanceof LCSMaterial){
			
			LCSMaterial matObj=(LCSMaterial)obj;
			
			String materialSKU = (String) matObj.getValue("hbi17DigitSKU");
			String colorCode = (String) matObj.getValue("hbiColorCode");
			String sizeCode = (String) matObj.getValue("hbiSizeCode");
			String attributeCode = (String) matObj.getValue("hbiAttrCode");
			
			
	        if (sizeCode != null) {
	        	sizeCode = sizeCode.trim();
	        }
	        if (colorCode != null) {
	        	colorCode = colorCode.trim();
	        }
	        if (attributeCode != null) {
	        	attributeCode = attributeCode.trim();
	        }
	 
	 
	        if (colorCode == null || "".equals(colorCode)) {
	            	colorCode = "000";
	         }
	        
	        if (sizeCode == null || "".equals(sizeCode)) {
	            	sizeCode = "00";
	         }
	        
	        if (attributeCode == null || "".equals(attributeCode)) {
	            	attributeCode = "------";
	        }

			
			String name = (String) matObj.getValue("name");
			String matFlexType = matObj.getFlexType().getFullName();
			System.out.println("----Material Type------------"+matFlexType);
			duplicateMaterialSKUCheck(materialSKU,colorCode,sizeCode,attributeCode,name,matFlexType,matObj);
			
			
		}
	}

	private static void duplicateMaterialSKUCheck(String materialSKU, String colorCode, String sizeCode, String attributeCode, String name, String matFlexType, LCSMaterial matObj) throws WTException {
		// TODO Auto-generated method stub
		FlexType subRootType = FlexTypeCache.getFlexTypeFromPath("Material");

		String colorCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiColorCode").getColumnDescriptorName();
		String attributeCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiAttrCode").getColumnDescriptorName();
		String sizeCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiSizeCode").getColumnDescriptorName();
		String matNameDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("name").getColumnDescriptorName();
		String materialCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material\\Material SKU").getAttribute("hbiMaterialCode").getColumnDescriptorName();
	
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		
		if("Material SKU".equalsIgnoreCase(matFlexType)){
			stmt.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
		
		stmt.appendFromTable(LCSMaterial.class);
		
		
		stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "1", "="));
		String materialCode = null;
    	
			System.out.println("---------Inside the MaterialSKU type----");
			 materialCode = (String)matObj.getValue("hbiMaterialCode");
    		
			
    		stmt.appendAndIfNeeded();
    		
    		stmt.appendOpenParen();
    		
    		stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, materialCodeDBColumn), materialCode, Criteria.EQUALS));
    		
    		stmt.appendOrIfNeeded();
			
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, matNameDBColumn), materialCode, Criteria.EQUALS));
			
			stmt.appendClosedParen();
		
		
		if ("00".equals(sizeCode)){
			
			stmt.appendAndIfNeeded();
			stmt.appendOpenParen();
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, sizeCodeDBColumn), "", Criteria.IS_NULL));
			stmt.appendOrIfNeeded();
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, sizeCodeDBColumn), sizeCode, Criteria.EQUALS));
			stmt.appendClosedParen();
		
		
		}
		
		else if(!("00".equals(sizeCode))){
			
			stmt.appendAndIfNeeded();
			
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, sizeCodeDBColumn), sizeCode, Criteria.EQUALS));
			
			
		}
		
		if ("000".equals(colorCode)){
		
		stmt.appendAndIfNeeded();
		stmt.appendOpenParen();
		stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, colorCodeDBColumn), "", Criteria.IS_NULL));
		stmt.appendOrIfNeeded();
		stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, colorCodeDBColumn), colorCode, Criteria.EQUALS));
		stmt.appendClosedParen();
		
		}
		else if(!("000".equals(colorCode))){
			stmt.appendAndIfNeeded();
			
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, colorCodeDBColumn), colorCode, Criteria.EQUALS));
		}
		
		if ("------".equals(attributeCode)){
			
			stmt.appendAndIfNeeded();
			stmt.appendOpenParen();
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, attributeCodeDBColumn), "", Criteria.IS_NULL));
			stmt.appendOrIfNeeded();
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, attributeCodeDBColumn), attributeCode, Criteria.EQUALS));
			stmt.appendClosedParen();
		}
		
		else if(!("------".equals(attributeCode))){
			stmt.appendAndIfNeeded();
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, attributeCodeDBColumn), attributeCode, Criteria.EQUALS));
		}
		
		

		SearchResults results = LCSQuery.runDirectQuery(stmt);
		
		logger.debug("-------material query--------"+ results.getResults().size());
		
		if (results.getResults().size() != 0){
			
				throw new LCSException("The Value for HBI Material Sku must be unique. The value " +materialCode+" "+colorCode+" "+attributeCode+" "+sizeCode+ " is already in use");
			
    	}
		
		}
    	
		else if (!("Material SKU".equalsIgnoreCase(matFlexType))) {
			
			
			
			stmt.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
			
			stmt.appendFromTable(LCSMaterial.class);
			
			
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "1", "="));
			
			
			
			stmt.appendAndIfNeeded();
			
			stmt.appendOpenParen();
			
			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, matNameDBColumn), name, Criteria.EQUALS));

    		stmt.appendOrIfNeeded();
    		
    		stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, materialCodeDBColumn), name, Criteria.EQUALS));
    		
    		stmt.appendClosedParen();
    		
    		if ("00".equals(sizeCode)){
			
    			stmt.appendAndIfNeeded();
    			stmt.appendOpenParen();
    			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, sizeCodeDBColumn), "", Criteria.IS_NULL));
    			stmt.appendOrIfNeeded();
    			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, sizeCodeDBColumn), sizeCode, Criteria.EQUALS));
    			stmt.appendClosedParen();
    		
    		
    		}
    		
    		else if(!("00".equals(sizeCode))){
    			
    			stmt.appendAndIfNeeded();
    			
    			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, sizeCodeDBColumn), sizeCode, Criteria.EQUALS));
    			
    			
    		}
    		
    		if ("000".equals(colorCode)){
    		
    		stmt.appendAndIfNeeded();
    		stmt.appendOpenParen();
    		stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, colorCodeDBColumn), "", Criteria.IS_NULL));
    		stmt.appendOrIfNeeded();
    		stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, colorCodeDBColumn), colorCode, Criteria.EQUALS));
    		stmt.appendClosedParen();
    		
    		}
    		else if(!("000".equals(colorCode))){
    			stmt.appendAndIfNeeded();
    			
    			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, colorCodeDBColumn), colorCode, Criteria.EQUALS));
    		}
    		
    		if ("------".equals(attributeCode)){
    			
    			stmt.appendAndIfNeeded();
    			stmt.appendOpenParen();
    			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, attributeCodeDBColumn), "", Criteria.IS_NULL));
    			stmt.appendOrIfNeeded();
    			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, attributeCodeDBColumn), attributeCode, Criteria.EQUALS));
    			stmt.appendClosedParen();
    		}
    		else if(!("------".equals(attributeCode))){
    			stmt.appendAndIfNeeded();
    			stmt.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, attributeCodeDBColumn), attributeCode, Criteria.EQUALS));
    		}

		SearchResults results = LCSQuery.runDirectQuery(stmt);
		
		logger.debug("-------material query--------"+ results.getResults().size());
		
		if (results.getResults().size() != 0){
			
				throw new LCSException("The Value for HBI Material Sku must be unique. The value " +name+" "+colorCode+" "+attributeCode+" "+sizeCode+ " is already in use");
			
		}
    					
		}
	}

}

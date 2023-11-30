package com.hbi.wc.interfaces.outbound.product;

import java.util.Collection;
import java.util.HashMap;


import org.apache.log4j.Logger;

import com.hbi.wc.util.logger.HBIUtilLogger;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

//import wt.part.WTPartMaster;
import wt.util.WTException;


public class HBIGetRoundValue {
	private static String hbiErpMaterialType = LCSProperties.get("com.hbi.wc.product.hbiErpMaterialType", "hbiErpMaterialType");
	private static String hbiPutUpCode = LCSProperties.get("com.hbi.wc.product.hbiPutUpCode", "hbiPutUpCode");
	private static String bomLinkObjectTypePC = LCSProperties.get("com.hbi.wc.bom.bomLinkObjectTypePC", "BOM\\Materials\\HBI\\Selling\\Pack Case BOM");
	private static String hbiReferenceSpecification = LCSProperties.get("com.hbi.wc.product.hbiReferenceSpecification", "hbiReferenceSpecification");
	private static String hbiPrimarySecondary = LCSProperties.get("com.hbi.bom.product.hbiPrimarySecondary", "hbiPrimarySecondary");
	//private static String hbiPkgsOrInner = LCSProperties.get("com.hbi.wc.bom.hbiPkgsOrInner", "hbiPkgsOrInner");
	private static String hbiCV ="hbiCV";
	private static String hbiPksCases = LCSProperties.get("com.hbi.wc.bom.hbiPksCases", "hbiPksCases");
    public static final String logLevel = LCSProperties.get("com.hbi.util.logLevel","DEBUG");
    static Logger utilLogger = HBIUtilLogger.createInstance(HBIGetRoundValue.class, logLevel);		
    private static String splitFormat = "|~*~|";
    private static String splitStr = "\\|\\~\\*\\~\\|";
    
    
    //To get round val in a Map.
    public HashMap<String,String> getRoundVal(LCSProduct productObj)  {
		utilLogger.debug("<<<<<<<<<<<<<<<getRoundVal method called>>>>>>>>>>>>>>>>>>>>>>");
		
		HashMap<String,String> roundValMap = new HashMap<String,String>();
		try {
		
		
		boolean zppk = false;
		
    	String erpMaterialType = (String) productObj.getValue(hbiErpMaterialType);
	
    	if(FormatHelper.hasContent(erpMaterialType) && "hbiZPPK".equalsIgnoreCase(erpMaterialType)) {
    		zppk=true;
    		//For ZPPK, round val is constant - 22 Aug 2019
    		roundValMap.put("roundValZPPK", "1");
    	}
		
		//
		//for ZRFT, Pkgs/Cases value from CV values based on size variation
		//
    	if(!zppk) {
    		SearchResults moaPUC_SR = LCSMOAObjectQuery.findMOACollectionData(productObj.getMaster(),  
    				productObj.getFlexType().getAttribute(hbiPutUpCode), "LCSMOAObject.createStampA2", true);
    		if(moaPUC_SR != null && moaPUC_SR.getResultsFound() > 0){
    	    	Collection<FlexObject> moaPUC_Collection = moaPUC_SR.getResults();
    	    	for(FlexObject moaPUC_FO : moaPUC_Collection){
    	   			String moaPUC_IDA2A2 = moaPUC_FO.getString("LCSMOAOBJECT.IDA2A2");
    	   			LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);
    		   		if(moaPUC_Obj.getValue(hbiReferenceSpecification) !=null) {
    					FlexSpecification spec = (FlexSpecification) moaPUC_Obj.getValue(hbiReferenceSpecification);
    					Collection<FlexBOMPart> specComponentColl = FlexSpecQuery.getSpecComponents(spec, "BOM");
    					for (FlexBOMPart bomPartObj : specComponentColl) {
    						if(bomLinkObjectTypePC.equalsIgnoreCase(bomPartObj.getFlexType().getFullName(true))) {
    							roundValMap= getZFRTRoundValMap(bomPartObj );
    						}
    					}
    		   		}
    	    	}
    		}
    	}

		utilLogger.debug("getRoundVal roundValMAp: "+roundValMap);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return roundValMap;
	}
	//Get pkg/ cases value which is round value in SAP from ZFRT Pack Case BOM CVs from casing section only
	    private HashMap<String,String> getZFRTRoundValMap(FlexBOMPart bomPartObj) {
	    	HashMap<String,String> roundValMap = new HashMap();
	    	HashMap<String,String> pkgsCaseMap = new HashMap();
	    	
	    	
	    	String valueStr ="0";
	    	Long valueDouble = 0L;
	    	Integer valueInt=0;
	    	//HashMap cvMap = new HashMap();
	    	try {
	    	//Variation and parent rows
	    		//This will give all rows in random order, parent row and variation row, use branch id to distinguish between different rows
	    	Collection<FlexObject> pcBOMLink_Coll= LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, 
						  null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,"ALL_SIZE1").getResults();
	    	Boolean hasvariation=false;
	    	 utilLogger.debug("getRoundVal pcBOMLink_Coll: "+pcBOMLink_Coll.size());
	    	if(pcBOMLink_Coll != null && pcBOMLink_Coll.size() > 0){
			   for(FlexObject pcBOMLinkFO : pcBOMLink_Coll){ 
				   FlexBOMLink pcBOMlink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" 
						   		+ pcBOMLinkFO.getString("FLEXBOMLINK.IDA2A2"));
				   String size1 = pcBOMlink.getSize1();
				  // utilLogger.debug("getRoundVal valueDouble: "+pcBOMlink.getDimensionName());

				   
				   String primarySecondary = (String) pcBOMlink.getValue(hbiPrimarySecondary);
				   valueDouble =   (Long) pcBOMlink.getValue(hbiPksCases);
				   utilLogger.debug("getRoundVal valueDouble: "+valueDouble);

				   valueInt = valueDouble.intValue();
				   valueStr =String.valueOf(valueInt);

				   //If value already entered in the map from initial variation row, do not enter again.
				   //As parent row with blank value might come at last iteration
				   //The loop might have last value as 0 and it will override the map with 0.
				   /*if(  checkDataMap(roundValMap,hbiPksCases)) {
						 
					   roundValMap.put(hbiPksCases,  valueStr);
				   }*/
			
				   //Branch IDs are unique for variation rows
				   int branchId = pcBOMlink.getBranchId();
				   String componentSection = (String)pcBOMlink.getValue("section");
				   utilLogger.debug("getRoundVal size1: "+size1);
				   utilLogger.debug("getRoundVal componentSection: "+componentSection);
				  //To avoid over riding value to null when section is already set
				   if(checkDataMap(roundValMap,String.valueOf(branchId)+"componentSection")) {
					 
				   	 roundValMap.put( String.valueOf(branchId)+"componentSection",componentSection);
			   	   }
				  // boolean componentSectionAdded=false;
				  
				   //This will check for size variation data
				   if(FormatHelper.hasContent(size1) && FormatHelper.hasContent(pcBOMlink.getDimensionName())) {
					   
					   roundValMap.put(size1 +splitFormat+ branchId , valueStr);
					   
					   //BranchId for CV when parent row not having CV value, this is just to get branch id for CV
					   
					   if( FormatHelper.hasContent(primarySecondary) && primarySecondary.contains(hbiCV)) {	
						   roundValMap.put(hbiCV, String.valueOf(branchId));
						   
						  /* if(!FormatHelper.hasContent(componentSection) && !componentSectionAdded){
							   
							   roundValMap.put( String.valueOf(branchId)+"componentSection","casing");
							   componentSectionAdded=true;

						   }*/
					   }
					   
				   }else {//When no size variation found, get the top row data. Top row should have primary sec when no variation
					   
					   //BranchId for CV from parentRow
					   if( FormatHelper.hasContent(primarySecondary) && primarySecondary.contains(hbiCV)) {
						   roundValMap.put("NoSize"+ splitFormat+branchId , valueStr);
						   roundValMap.put(hbiCV, String.valueOf(branchId));
					   }
				   }
			   }
			}
			
	    	utilLogger.debug(" ZFRT preparation roundValMap : "+roundValMap);
	    	//As branch IDs should be same, cv should be present under casing section
	    	//This to get the value from casing section only and CV. This CV can be at parent row or in child variation rows.
	    	//SO it is necessary to check CV should be there, as round value is taken form CV only which is the Pkgs\Cases
	    	 if(roundValMap.containsKey(hbiCV)) { 
	    		 String cvBranchId = (String) roundValMap.get(hbiCV);
	    		 Collection<String> keySetColl = roundValMap.keySet();
	    		
		    	 for(String key :keySetColl) {
		    		 if(key.contains(splitFormat)){
		    			 String[] keySplit =key.split(splitStr);
						 String size1 = (String) keySplit[0];
						
						 String branchId = (String) keySplit[1];
						 String section = (String) roundValMap.get(branchId+"componentSection");
						 utilLogger.debug(" ZFRT preparation section : "+section);
						 if(cvBranchId.equalsIgnoreCase(branchId) && "casing".equalsIgnoreCase(section)){
							 utilLogger.debug(" ZFRT preparation size1 : "+size1);
							 utilLogger.debug(" ZFRT preparation value of pkg case : "+roundValMap.get(key));
							 //When in casing section of Pack Case BOM for ZFRT
							 //For CVs, that can be at top row in primary sec and pkg/cases will be at the variation row.
							 //Primary sec blank at variation row.
							 //No size to be removed as this bom has variation
							 if(FormatHelper.hasContent(size1) && !"NoSize".equalsIgnoreCase(size1)){
								 hasvariation=true;
							 }
							 pkgsCaseMap.put(size1,roundValMap.get(key));
						 }
		    		 }
		    		 
		    	 } 
    		 }
			 if(hasvariation){
				 pkgsCaseMap.remove("NoSize");
			}
	    	 utilLogger.debug(" ZFRT preparation pkgsCaseMap : "+pkgsCaseMap);
			} catch (WTException e) {
				e.printStackTrace();
			}
	    	return pkgsCaseMap;
		}
	    
	    // No Variation
	private HashMap<String, String> getZPPKRoundValMap(FlexBOMPart bomPartObj) {
		HashMap<String, String> roundValMap = new HashMap();
		roundValMap.put("roundValZPPK", "0");
		String valueStr ="0";
    	Long valueDouble = 0L;
    	Integer valueInt=0;
		try {
    	Collection<FlexObject>pcBOMLink_Coll = LCSFlexBOMQuery.findFlexBOMData(bomPartObj,null,null, null,null, null,LCSFlexBOMQuery.WIP_ONLY, null,false,false,null,null,null,null).getResults();
		
    	
    	if(pcBOMLink_Coll != null && pcBOMLink_Coll.size() > 0)
		{
		   for(FlexObject pcBOMLinkFO : pcBOMLink_Coll)
		   { 
			   FlexBOMLink pcBOMlink = (FlexBOMLink)LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" 
					   		+ pcBOMLinkFO.getString("FLEXBOMLINK.IDA2A2"));
			  
			   String componentSection = (String)pcBOMlink.getValue("section");
			   String primarySecondary = (String) pcBOMlink.getValue(hbiPrimarySecondary);
			   if(FormatHelper.hasContent(componentSection) && FormatHelper.hasContent(primarySecondary) 
						  &&"casing".equalsIgnoreCase(componentSection) && "hbiCV1".equalsIgnoreCase(primarySecondary)) {
				   
				   if(pcBOMlink.getValue(hbiPksCases)!=null) {
					  
					   valueDouble =   (Long) pcBOMlink.getValue(hbiPksCases);
					   
						valueInt = valueDouble.intValue();
						valueStr =String.valueOf(valueInt);
				   }
			
			   }
			   
		   }
		}
    	utilLogger.debug(" roundValZPPK : "+valueStr);
    	roundValMap.put("roundValZPPK", valueStr);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return roundValMap;
	}
	 public static Boolean checkDataMap(HashMap dataMap, String key) {
			
			Boolean putValue = false;
			Object value = dataMap.get(key);
		
			if (value != null) {
				if(value instanceof Double) {
					Double doubleValue = (Double) value;
					
					
					if(doubleValue==0.0) {
						
						putValue=true;
					}
				}else if(value instanceof String){
					
					if(!FormatHelper.hasContent((String) value)) {
					
						putValue=true;
					}
					
				}
			} else {
				putValue=true;
			}
			
			return putValue;
			
		}
}

package com.hbi.wc.interfaces.inbound.esko;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.hbi.wc.interfaces.inbound.aps.bom.logic.HBIProductBOMLogic;
import com.hbi.wc.interfaces.inbound.aps.bom.query.HBIFlexBOMLinkQuery;
import com.lcs.wc.db.Query;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartClientModel;
import com.lcs.wc.flexbom.FlexBOMPartMaster;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HBIMasterMaterialBOMUtil {
	
	 /**
     * Logger
     */
	 private static final Logger LOGGER = LogR.getLogger("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialPlugin");
    /**
     * This represents bomSection key
     */
    private static final String BOM_SECTION_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialBOMUtil.bomSectionKey", "section");
    /**
     * This represents labelling BOM section key
     */
    static final String LABELLING_BOM_SECTION_KEY = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialBOMUtil.labellingSectionKey", "labelling");
    /**
     * This represents Label BOM Type to be used in Master Material BOM
     */
    static final String LABEL_BOM_FLEXTYPE = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMasterMaterialBOMUtil.bomType","BOM\\Materials\\HBI\\Label");
    
	
	public static FlexBOMPart createMaterialBOM(String flexBOMPartTypePath,LCSMaterial material) throws WTException, WTPropertyVetoException
	{
		LOGGER.debug("### START HBIMasterMaterialBOMUtil.createMaterialBOM ###");
		String bomTypeID = FormatHelper.getObjectId(FlexTypeCache.getFlexTypeFromPath(flexBOMPartTypePath));
		//Initializing FlexBOMPart using Client Model(which internally calls OOTB functions)
		FlexBOMPartClientModel bomPartClientModelObj = new FlexBOMPartClientModel();
		bomPartClientModelObj.setTypeId(bomTypeID);
		bomPartClientModelObj.initiateBOMPart(FormatHelper.getObjectId(material), bomTypeID, "MAIN", (Collection)null, true); 
		LOGGER.debug("### END HBIMasterMaterialBOMUtil.createMaterialBOM ###");
		return bomPartClientModelObj.getBusinessObject();
	}

	public static void removePartMaterialFromMasterMaterialBOM(LCSMaterial masterMaterialReference,LCSMaterial partMaterial) throws WTPropertyVetoException, WTException {
		LOGGER.debug("### START HBIMasterMaterialBOMUtil.removePartMaterialFromMasterMaterialBOM ###");
		if (masterMaterialReference!=null && !new LCSFlexBOMQuery().findBOMPartsForOwner(masterMaterialReference).isEmpty()) {
			LOGGER.debug(">>>>>>>>>>>>>>Remove Old Master Material BOM Link>>>>>>>>>>>>>>>>");
			FlexBOMPart oldMasterMaterialBOM = (FlexBOMPart) new LCSFlexBOMQuery().findBOMPartsForOwner(masterMaterialReference).iterator().next();
			LOGGER.debug("oldMasterMaterialBOM>>>>>>>>>>>"+oldMasterMaterialBOM.getName());
			FlexBOMLink oldLink = new HBIFlexBOMLinkQuery().getFlexBOMLinkForOwner(oldMasterMaterialBOM, partMaterial);
			if (oldLink != null) {
				new LCSFlexBOMLogic().delete(oldLink);
			}
		}
		LOGGER.debug("### END HBIMasterMaterialBOMUtil.removePartMaterialFromMasterMaterialBOM ###");
	}
	
	public static void addPartMaterialToMasterMaterialBOM(LCSMaterial masterMaterial, LCSMaterial partMaterial ) throws WTPropertyVetoException, WTException {
		LOGGER.debug("### START HBIMasterMaterialBOMUtil.addPartMaterialToMasterMaterialBOM ###");
		FlexBOMPart masterMaterialBOM = null;
		Collection masterMaterialBOMsCollection = new LCSFlexBOMQuery().findBOMPartsForOwner(masterMaterial);
		if (!masterMaterialBOMsCollection.isEmpty()) {
			masterMaterialBOM = (FlexBOMPart) masterMaterialBOMsCollection.iterator().next();
			FlexBOMLink existingLink = new HBIFlexBOMLinkQuery().getFlexBOMLinkForOwner(masterMaterialBOM, partMaterial);
			LOGGER.debug("existingLink>>>>>>>>>>>>>>"+existingLink);
			if (existingLink==null) {
				int sortingNumber = getMaximumSortingNumberForFlexBOMPartAndSection(masterMaterialBOM, LABELLING_BOM_SECTION_KEY)+ 1;
				LOGGER.debug("createNewBOMLink>>>>>>>>>>>>>>");
				new HBIProductBOMLogic().createFlexBOMLinkForOwner(masterMaterialBOM, partMaterial, LABELLING_BOM_SECTION_KEY,sortingNumber);
			}
			
		}else {
			//This block of code runs if Master Material is Present but Material BOM is not yet created.
			LOGGER.debug(">>>>>>>>>>>>>>Creating Master Material BOM>>>>>>>>>>>>>>>>");
			 masterMaterialBOM = createMaterialBOM(LABEL_BOM_FLEXTYPE, masterMaterial);
			LOGGER.debug(">>>>>>>>>>>>>>Creating Master Material BOM Link>>>>>>>>>>>>>>>>");
			new HBIProductBOMLogic().createFlexBOMLinkForOwner(masterMaterialBOM, partMaterial, LABELLING_BOM_SECTION_KEY, 1);
		}
		LOGGER.debug("### END HBIMasterMaterialBOMUtil.addPartMaterialToMasterMaterialBOM ###");	
		
	}
	
	/**
	 * This function is using to get/return a next available sorting number/ID of a FlexBOMLink based on the given FlexBOMPart(owner of FlexBOMLink) and section name of the FlexBOMLink
	 * @param flexBOMPartObj - FlexBOMPart
	 * @param bomSection - String
	 * @return sortingNumber - int
	 * @throws WTException
	 */
	public static int getMaximumSortingNumberForFlexBOMPartAndSection(FlexBOMPart flexBOMPartObj, String bomSection) throws WTException
	{
		LOGGER.debug("### START HBIMasterMaterialBOMUtil.getMaximumSortingNumberForFlexBOMPartAndSection(FlexBOMPart flexBOMPartObj, String bomSection) ###");
		int sortingNumber= 0;
		
		try
		{
			FlexBOMPartMaster  ownerMasterObj = (FlexBOMPartMaster )flexBOMPartObj.getMaster();
			String bomPartMasterID = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(ownerMasterObj));
			String sectionNameDBColumn = flexBOMPartObj.getFlexType().getAttribute(BOM_SECTION_KEY).getColumnName();
			
			//Preparing SQL Query to get Maximum Sorting Number for the given FlexBOMPart(Owner of all FlexBOMLinks) and for the given BOM Section Name 
			String sortingNumberQuery = "SELECT MAX(SORTINGNUMBER) FROM FLEXBOMLINK WHERE FLEXBOMLINK.OUTDATE IS NULL AND IDA3A5 = " + bomPartMasterID + " AND "+sectionNameDBColumn+" LIKE '"+bomSection +"'"+ " GROUP BY "+sectionNameDBColumn;
			LOGGER.debug("HBIMasterMaterialBOMUtil.getMaximumSortingNumberForFlexBOMPartAndSection() : sortingNumberQuery = " + sortingNumberQuery);
			 
			//Initializing Query object(which internally take care of Database Connection) and ResultSet to execute the SQL Query to get Maximum Sorting Number
			Query query = new Query();    
			query.prepareForQuery();
			ResultSet results = query.runQuery(sortingNumberQuery);  
			if(results != null && results.next())
			{
				sortingNumber = results.getInt(1);
			}
			
			LOGGER.debug("HBIMasterMaterialBOMUtil.getMaximumSortingNumberForFlexBOMPartAndSection() : sortingNumber = " + sortingNumber);
			results.close();
			query.cleanUpQuery();
		}
		catch (SQLException sqlExp)
		{
			LOGGER.debug(" SQLException in HBIMasterMaterialBOMUtil.getMaximumSortingNumberForFlexBOMPartAndSection() is :: "+ sqlExp);
		}
		
		 LOGGER.debug("### END HBIMasterMaterialBOMUtil.getMaximumSortingNumberForFlexBOMPartAndSection(FlexBOMPart flexBOMPartObj, String bomSection) ###");
		return sortingNumber;
	}
	
	 
}

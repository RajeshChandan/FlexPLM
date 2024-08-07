package com.sportmaster.wc.reports;

import java.util.Collection;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartMaster;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.product.LCSProduct;
/**
 * 
 * @author 'true' BSC -PTC.
 * @version 'true' 1.0 version number.
 */
public final class SMMaterialForecastReportInputPageHelper {
	
	private static final org.apache.log4j.Logger LOGGER=Logger.getLogger("MFDRLOG");
 
	private static final String VERSION_ID = "versionInfo.identifier.versionId";

 	private static final String ID_A3MASTER_REFERENCE = "idA3masterReference";

	/**
	 * Constant LCS_MATERIAL.
	 */
	private static final String LCS_MATERIAL = "LCSMaterial";
	/**
	 * Constant EMPTY_STRING.
	 */
	private static final String EMPTY_STRING = "";
	/**
	 * Constant MATERIALMASTER.
	 */
	private static final String MATERIALMASTER = "MATERIALMASTER";

 
	/**
	 * Constant LATEST_ITERATION_INFO.
	 */
	private static final String LATEST_ITERATION_INFO = "iterationInfo.latest";
	/**
	 * Constant WRK_STATE.
	 */
	private static final String WRK_STATE = "wrk";
	/**
	 * Constant MASTER_REFERENCE_KEY_ID.
	 */
	private static final String MASTER_REFERENCE_KEY_ID = "masterReference.key.id";
	/**
	 * Constant CHECKOUT_INFO_STATE.
	 */
	private static final String CHECKOUT_INFO_STATE = "checkoutInfo.state";
	/**
	 * Constant PARENT_REFERENCE_KEY_ID.
	 */
	private static final String PARENT_REFERENCE_KEY_ID = "parentReference.key.id";
	/**
	 * Constant CHILD_REFERENCE_KEY_ID.
	 */
	private static final String CHILD_REFERENCE_KEY_ID = "childReference.key.id";
	/**
	 * Constant THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID.
	 */
	private static final String THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID = "thePersistInfo.theObjectIdentifier.id";
	/**
	 * Constant ATT_NAME_KEY.
	 */
	private static final String ATT_NAME_KEY = "name";
 
	/**
	 * Constant BOMPARTMASTER.
	 */
	private static final String BOMPARTMASTER = "BOMPARTMASTER";
	/**
	 * Constant FLEX_BOM_LINK.
	 */
	private static final String FLEX_BOM_LINK = "FlexBOMLink";
 
	/**
	 * SMMaterialForecastReportQuery method.
	 */
	private SMMaterialForecastReportInputPageHelper(){

	}


	/**
	 * Method getMaterialSuppFromBOMLinkData - method which queries the data from bom link.
	 * @param reportBean the reportBean.
	 * @param seasonOids the seasonOids.
	 * @param includeMaterialColor the includeMaterialColor.
	 * @return searchresults.
	 * @throws WTException the WTException.
	 */
	public static com.lcs.wc.db.SearchResults getMaterialSupUsedInBOMs(SMMaterialForecastReportBean reportBean,Collection<String> seasonOids, boolean includeMaterialColor) throws WTException{	    	
		com.lcs.wc.db.PreparedQueryStatement pqs = new com.lcs.wc.db.PreparedQueryStatement();

		pqs.setDistinct(true);
		pqs.appendFromTable(FlexBOMLink.class, FLEX_BOM_LINK);
		pqs.appendFromTable(FlexBOMPart.class);
		pqs.appendFromTable(FlexBOMPartMaster.class, BOMPARTMASTER); 
		FlexType bomType= FlexTypeCache.getFlexTypeFromPath("BOM\\Materials");
		pqs.appendFromTable(LCSMaterial.class);   

		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, PARENT_REFERENCE_KEY_ID)); // PARENT MASTER ID
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, CHILD_REFERENCE_KEY_ID)); // CHILD MASTER ID     	 
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID)); 
   		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, LCSQuery.TYPED_BRANCH_ID));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, CHECKOUT_INFO_STATE));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, "ownerMasterReference.key.id"));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class,  bomType.getAttribute(ATT_NAME_KEY).getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, "bomType"));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
 
 		appendCriteriaForBomLinkData(reportBean, seasonOids,
				includeMaterialColor, pqs);

		LOGGER.debug("PreparedQuerystatement for bom data..."+pqs.toString());
		//		SearchResults  bomData= ;
		return LCSQuery.runDirectQuery(pqs);
	}



	/**
	 * Method appendCriteriaForBomLinkData - all criteria is added in this method.
	 * @param reportBean the reportBean.
	 * @param seasonOids the seasonOids.
	 * @param includeMaterialColor the includeMaterialColor.
	 * @param pqs the pqs.
	 * @throws WTException the WTException.
	 * @throws NumberFormatException the NumberFormatException.
	 */
	private static void appendCriteriaForBomLinkData(
			SMMaterialForecastReportBean reportBean,
			Collection<String> seasonOids, boolean includeMaterialColor,
			com.lcs.wc.db.PreparedQueryStatement pqs) throws WTException,
			NumberFormatException {
		// SPECIFY ONLY NON WIP RECORDS
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, "wip"), "0", Criteria.EQUALS));

		// SPECIFY ONLY NON DROPPED RECORDS
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, "dropped"), "0", Criteria.EQUALS));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, "outDate"), EMPTY_STRING, Criteria.IS_NULL));	


		pqs.appendFromTable(LCSProduct.class);
		pqs.appendSelectColumn(new QueryColumn(LCSProduct.class, LCSQuery.TYPED_BRANCH_ID));
		pqs.appendSelectColumn(new QueryColumn(LCSProduct.class, MASTER_REFERENCE_KEY_ID));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, CHECKOUT_INFO_STATE), WRK_STATE, Criteria.NOT_EQUAL_TO));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, VERSION_ID), "A", Criteria.EQUALS));

		pqs.appendSelectColumn( new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE));

		pqs.appendJoin( new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE), new QueryColumn(FlexBOMLink.class, CHILD_REFERENCE_KEY_ID)); // MATERIAL
 
		pqs.appendJoin(new QueryColumn(LCSProduct.class, MASTER_REFERENCE_KEY_ID), new QueryColumn(FlexBOMPart.class, "ownerMasterReference.key.id"));

		pqs.appendFromTable(com.lcs.wc.supplier.LCSSupplierMaster.class);
		pqs.appendFromTable(LCSMaterialMaster.class, MATERIALMASTER);

		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.supplier.LCSSupplierMaster.class, "supplierName"));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.supplier.LCSSupplierMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendSelectColumn(new QueryColumn(MATERIALMASTER, LCSMaterialMaster.class, ATT_NAME_KEY));

		pqs.appendSelectColumn(new QueryColumn(MATERIALMASTER, LCSMaterialMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, "bomType"), "?", Criteria.EQUALS), "MAIN");

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(MATERIALMASTER, LCSMaterialMaster.class, ATT_NAME_KEY), "material_placeholder", Criteria.NOT_EQUAL_TO));

		pqs.appendJoin(new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE), new QueryColumn(MATERIALMASTER, LCSMaterialMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

  
		pqs.appendFromTable(LCSMaterialSupplier.class);
 		pqs.appendFromTable(LCSMaterialSupplierMaster.class);
		pqs.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "state.state"));
		pqs.appendSelectColumn(new QueryColumn(LCSMaterialSupplierMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.branchId"));
 
		pqs.appendJoin(new QueryColumn(LCSMaterialSupplier.class, MASTER_REFERENCE_KEY_ID), new QueryColumn(LCSMaterialSupplierMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

 
		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "materialMasterReference.key.id"), new QueryColumn(MATERIALMASTER,LCSMaterialMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "supplierMasterReference.key.id"), new QueryColumn(com.lcs.wc.supplier.LCSSupplierMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));


		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "supplierMasterReference.key.id"), new QueryColumn(FlexBOMLink.class, "supplierReference.key.id")); // SUPPLIER
		// MASTER
		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "materialMasterReference.key.id"), new QueryColumn(FlexBOMLink.class, CHILD_REFERENCE_KEY_ID)); // MATERIAL

		pqs.appendSelectColumn(new QueryColumn(BOMPARTMASTER, FlexBOMPartMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, CHECKOUT_INFO_STATE));
 
		// LINK FLEXBOMLINK TO FLEXBOMPART MASTER.
		pqs.appendJoin(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, PARENT_REFERENCE_KEY_ID), new QueryColumn(FlexBOMPart.class, MASTER_REFERENCE_KEY_ID));

		pqs.appendJoin(new QueryColumn(BOMPARTMASTER, FlexBOMPartMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID), new QueryColumn(FlexBOMPart.class, MASTER_REFERENCE_KEY_ID));

		// ONLY GET LATEST ITERATION OF BOM PART
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, CHECKOUT_INFO_STATE), WRK_STATE, Criteria.NOT_EQUAL_TO));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));
		  		
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));

		  
		// APPEND SEASON CRITERIA..
		(new SMMaterialForecastReportHelper()).appendSeasonCriteria(pqs);		 
	}
  
}

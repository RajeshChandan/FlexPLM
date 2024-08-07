package com.sportmaster.wc.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import wt.util.WTException;

import com.lcs.wc.client.web.FlexTypeGenerator;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn; 
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartMaster;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster; 
import com.lcs.wc.material.MaterialSupplierFlexTypeScopeDefinition;
import com.lcs.wc.product.LCSProduct; 
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
/**
 * 
 * @author 'true' BSC -PTC.
 * @version 'true' 1.0 version number.
 */
public class SMMaterialForecastReportHelper {

	private static final String SM_MS_NOMINATED = LCSProperties.get("com.sportmaster.reports.materialforecast.nominated.attKey","smMsNominated");

	private static final String bomStatus =  LCSProperties.get("bom.BOMSTATUS","vrdBOMStatus");
			
	private static final String CANCELLED_STATUS = "smCancelled";
	private static final String VERSION_ID = "versionInfo.identifier.versionId";

	private static final String LATEST_ITER_FLEX_SPECIFICATION = "LatestIterFlexSpecification";

	private static final String ID_A3MASTER_REFERENCE = "idA3masterReference";

	private static final String BRANCH_ID = "branchId";

	private static final String CCC = "smBOMContrastColorCombination";

	/**
	 * Constant MASTER_BRANCH.
	 */
	private static final String MASTER_BRANCH = "masterBranch";
	/**
	 * Constant MASTER_BRANCH_ID.
	 */
	private static final String MASTER_BRANCH_ID = "masterBranchId";
	/**
	 * Constant DIMENSION_ID.
	 */
	private static final String DIMENSION_ID = "dimensionId";
	/**
	 * Constant DIMENSION_NAME.
	 */
	private static final String DIMENSION_NAME = "dimensionName";
	/**
	 * Constant COLOR_REFERENCE_KEY_ID.
	 */
	private static final String COLOR_REFERENCE_KEY_ID = "colorReference.key.id";
	/**
	 * Constant DESTINATION_DIMENSION_REFERENCE_KEY_ID.
	 */
	private static final String DESTINATION_DIMENSION_REFERENCE_KEY_ID = "destinationDimensionReference.key.id";
	/**
	 * Constant SOURCE_DIMENSION_REFERENCE_KEY_ID.
	 */
	private static final String SOURCE_DIMENSION_REFERENCE_KEY_ID = "sourceDimensionReference.key.id";
	/**
	 * Constant COLOR_DIMENSION_REFERENCE_KEY_ID.
	 */
	private static final String COLOR_DIMENSION_REFERENCE_KEY_ID = "colorDimensionReference.key.id";
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
	 * Constant LCSMATERIALCOLOR.
	 */
	private static final String LCSMATERIALCOLOR = "LCSMATERIALCOLOR";
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
	 * Constant OVERRIDEN_ROW.
	 */
	private static final String OVERRIDEN_ROW = "overridenRow";

	private static final String FABRIC_MATERIAL = "FABRIC";

	private static final String LCSMATERIALSUPPLIER_FABRIC = "FABRIC_SUPPLIER";

	private static final String MATERIAL_FABRIC_TYPE = "Material\\Fabric";

	/**
	 * SMMaterialForecastReportQuery method.
	 */
	public SMMaterialForecastReportHelper(){

	}


	/**
	 * @param reportBean
	 * @param seasonOids
	 * @param productOids
	 * @return
	 * @throws WTException
	 */
	public com.lcs.wc.db.SearchResults getMaterialSuppFromBOMLinkData(SMMaterialForecastReportBean reportBean,Collection<String> seasonOids,
			Collection<String> productOids,boolean includeMatColor,boolean overriddenRowOnly,boolean includeOverridenMC,boolean includeMaterialOverride,
			boolean includeOvrMcOnly) throws WTException{	    	
		com.lcs.wc.db.PreparedQueryStatement pqs = new com.lcs.wc.db.PreparedQueryStatement();
		pqs.setDistinct(true);
		pqs.appendFromTable(FlexBOMLink.class, FLEX_BOM_LINK);
		pqs.appendFromTable(FlexBOMPart.class);
		FlexType bomType= FlexTypeCache.getFlexTypeFromPath("BOM\\Materials\\Product");
		pqs.appendFromTable(LCSMaterial.class,LCS_MATERIAL);	

		if(reportBean.isHasFabricType()){
			pqs.appendFromTable(LCSMaterial.class, FABRIC_MATERIAL); 
		}

		pqs.appendFromTable(FlexBOMPartMaster.class, BOMPARTMASTER); 
		pqs.appendSelectColumn(new QueryColumn(BOMPARTMASTER, FlexBOMPartMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, PARENT_REFERENCE_KEY_ID)); // PARENT MASTER ID
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, CHILD_REFERENCE_KEY_ID)); // CHILD MASTER ID
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, COLOR_REFERENCE_KEY_ID));    // COLOR ID
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, BRANCH_ID)); // BRANCH ID 
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, FlexBOMLink.YIELD)); // YIELD
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID)); 
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, COLOR_DIMENSION_REFERENCE_KEY_ID));    // COLOR DIMENSION ID
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, SOURCE_DIMENSION_REFERENCE_KEY_ID)); 
		pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, DESTINATION_DIMENSION_REFERENCE_KEY_ID)); //OVerride level

		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, LCSQuery.TYPED_BRANCH_ID));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, CHECKOUT_INFO_STATE));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, "ownerMasterReference.key.id"));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class,  bomType.getAttribute(ATT_NAME_KEY).getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, "bomType"));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, bomType.getAttribute(bomStatus).getColumnDescriptorName()));

		
		// LINK FLEXBOMLINK TO FLEXBOMPART MASTER.
		pqs.appendJoin(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, PARENT_REFERENCE_KEY_ID), new QueryColumn(FlexBOMPart.class, MASTER_REFERENCE_KEY_ID));

		pqs.appendJoin(new QueryColumn(BOMPARTMASTER, FlexBOMPartMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID), new QueryColumn(FlexBOMPart.class, MASTER_REFERENCE_KEY_ID));

		// ONLY GET LATEST ITERATION OF BOM PART
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, CHECKOUT_INFO_STATE), WRK_STATE, Criteria.NOT_EQUAL_TO));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, "bomType"), "?", Criteria.EQUALS), "MAIN");
		
		
		//Get BOM which are not status Cancelled - JIRA 956
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class, bomType.getAttribute(bomStatus).getColumnDescriptorName()), "?", Criteria.NOT_EQUAL_TO), CANCELLED_STATUS);
		

		// SPECIFY ONLY NON WIP RECORDS
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, "wip"), "0", Criteria.EQUALS));

		// SPECIFY ONLY NON DROPPED RECORDS
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, "dropped"), "0", Criteria.EQUALS));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, "outDate"), EMPTY_STRING, Criteria.IS_NULL)); 

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, "dimensionName"), EMPTY_STRING, Criteria.IS_NULL));

		if(overriddenRowOnly){
			appendBOMOverrideCriteria(pqs, bomType);
		}

		appendCriteriaForBomLinkData(reportBean, seasonOids, productOids, pqs,includeMatColor, overriddenRowOnly,includeOverridenMC,includeMaterialOverride,includeOvrMcOnly);
		return LCSQuery.runDirectQuery(pqs);
	}  



	/**
	 * @param pqs
	 * @param bomType
	 * @throws WTException
	 */
	private void appendBOMOverrideCriteria(
			com.lcs.wc.db.PreparedQueryStatement pqs, FlexType bomType)
					throws WTException {
		pqs.appendFromTable(com.lcs.wc.product.ProductDestination.class);
		FlexType pdType = FlexTypeCache.getFlexTypeFromPath("Product Destination");
		com.lcs.wc.flextype.FlexTypeAttribute nameAtt = pdType.getAttribute(ATT_NAME_KEY);
		String nameColumn = nameAtt.getColumnDescriptorName();
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.product.ProductDestination.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.product.ProductDestination.class, "destinationName"));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.product.ProductDestination.class, nameColumn));

		pqs.appendFromTable(FlexBOMLink.class, OVERRIDEN_ROW);
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, BRANCH_ID)); //OVerride level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID)); //OVerride level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, "yield")); //OVerride level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, DIMENSION_NAME)); //OVerride level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, DESTINATION_DIMENSION_REFERENCE_KEY_ID)); //OVerride level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, DIMENSION_ID)); 
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, bomType.getAttribute("quantity").getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, bomType.getAttribute("lossAdjustment").getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, bomType.getAttribute(CCC).getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, bomType.getAttribute("colorDescription").getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, bomType.getAttribute("priceOverride").getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, PARENT_REFERENCE_KEY_ID)); // PARENT MASTER ID
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, CHILD_REFERENCE_KEY_ID)); // CHILD MASTER ID
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, COLOR_DIMENSION_REFERENCE_KEY_ID));    // COLOR DIMENSION ID
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, SOURCE_DIMENSION_REFERENCE_KEY_ID));    // SOURCE DIMENSION ID
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, COLOR_REFERENCE_KEY_ID));    // COLOR ID

		pqs.appendJoin(new QueryColumn(FlexBOMLink.class, PARENT_REFERENCE_KEY_ID),new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, "parentReference.key.id"));

		pqs.appendJoin(new QueryColumn(FlexBOMLink.class, BRANCH_ID),new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, BRANCH_ID));

		pqs.appendJoin(new QueryColumn(com.lcs.wc.product.ProductDestination.class, "thePersistInfo.theObjectIdentifier.id(+)"),new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, DESTINATION_DIMENSION_REFERENCE_KEY_ID));

		// SPECIFY ONLY NON WIP RECORDS
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, "wip"), "0", Criteria.EQUALS));

		// SPECIFY ONLY NON DROPPED RECORDS
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, "dropped"), "0", Criteria.EQUALS));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, "outDate"), EMPTY_STRING, Criteria.IS_NULL));	 

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, "dimensionName"), EMPTY_STRING, Criteria.IS_NOT_NULL));
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
	public void appendCriteriaForBomLinkData(
			SMMaterialForecastReportBean reportBean,
			Collection<String> seasonOids, Collection<String> productOids,
			com.lcs.wc.db.PreparedQueryStatement pqs,boolean includeMatColor,boolean overriddenRowOnly,boolean includeOverridenMC
			,boolean includeMaterialOverride,boolean includeOvrMcOnly) throws WTException,
			NumberFormatException {	  
		pqs.appendFromTable(LCSProduct.class);
		pqs.appendSelectColumn(new QueryColumn(LCSProduct.class, LCSQuery.TYPED_BRANCH_ID));
		pqs.appendSelectColumn(new QueryColumn(LCSProduct.class, MASTER_REFERENCE_KEY_ID));
		
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, CHECKOUT_INFO_STATE), WRK_STATE, Criteria.NOT_EQUAL_TO));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, VERSION_ID), "A", Criteria.EQUALS));

		
		// Fix for max limit cannot be more than 1000 in a list - using appendor instead of appendin criteria - start
		// Using Append OR instead of AppendIN criteria
		if(productOids!=null){
			Iterator pdtIter = productOids.iterator();
			pqs.appendAndIfNeeded();
			pqs.appendOpenParen();
			String singleProductId = "";
			while(pdtIter.hasNext())  {
				   singleProductId = (String)pdtIter.next();
				   pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, MASTER_REFERENCE_KEY_ID), singleProductId, Criteria.EQUALS));
				   if (pdtIter.hasNext()) 
				   {
					   pqs.appendOr();
				   }
			}
			pqs.appendClosedParen();
		}
		
		/*if(productOids!=null){
			pqs.appendInCriteria(new QueryColumn(LCSProduct.class, MASTER_REFERENCE_KEY_ID), productOids); 
		}
		*/
		// Fix for max limit cannot be more than 1000 in a list - using appendor instead of appendin criteria - end
		
		
		pqs.appendJoin(new QueryColumn(LCSProduct.class, MASTER_REFERENCE_KEY_ID), new QueryColumn(FlexBOMPart.class, "ownerMasterReference.key.id"));

		//adding material criteria
		//Adding material table
		pqs.appendSelectColumn( new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE));
		pqs.appendSelectColumn( new QueryColumn(LCS_MATERIAL,LCSMaterial.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(LCS_MATERIAL,LCSMaterial.class,"iterationInfo.branchId"));

		if(includeMaterialOverride){
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW,FlexBOMLink.class, CHILD_REFERENCE_KEY_ID), new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE)); // MATERIAL
		}else{
			pqs.appendJoin(new QueryColumn(FlexBOMLink.class, CHILD_REFERENCE_KEY_ID), new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE)); // MATERIAL
		}

		//appending material type criteria
		appendMaterialTypeCriteria(reportBean, pqs);

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCS_MATERIAL,LCSMaterial.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));

		if(reportBean.isHasFabricType()){
			pqs=SMMaterialForecastReportFabricHelper.appendFabricMaterialType(pqs); 
		}

		pqs.appendFromTable(LCSMaterialMaster.class, MATERIALMASTER);
		pqs.appendSelectColumn(new QueryColumn(MATERIALMASTER, LCSMaterialMaster.class, ATT_NAME_KEY));
		pqs.appendSelectColumn(new QueryColumn(MATERIALMASTER, LCSMaterialMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendJoin(new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE), new QueryColumn(MATERIALMASTER, LCSMaterialMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(MATERIALMASTER, LCSMaterialMaster.class, ATT_NAME_KEY), "material_placeholder", Criteria.NOT_EQUAL_TO));

		if(includeMaterialOverride){
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, "supplierReference.key.id"),new QueryColumn(LCSMaterialSupplierMaster.class, "supplierMasterReference.key.id")); // SUPPLIER
		}else{
			pqs.appendJoin(new QueryColumn(FlexBOMLink.class, "supplierReference.key.id"),new QueryColumn(LCSMaterialSupplierMaster.class, "supplierMasterReference.key.id")); // SUPPLIER
		}
		//Adding supplier critieria.
		//Adding material supplier tables
		pqs.appendFromTable(com.lcs.wc.supplier.LCSSupplierMaster.class);
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.supplier.LCSSupplierMaster.class, "supplierName"));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.supplier.LCSSupplierMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID)); 

		pqs.appendFromTable(LCSMaterialSupplier.class);		
		pqs.appendFromTable(LCSMaterialSupplierMaster.class);
		pqs.appendSelectColumn(new QueryColumn(LCSMaterialSupplierMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.branchId"));
 
		pqs.appendJoin( new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE),new QueryColumn(LCSMaterialSupplierMaster.class, "materialMasterReference.key.id")); // MATERIAL

		pqs.appendJoin(new QueryColumn(LCSMaterialSupplier.class, MASTER_REFERENCE_KEY_ID), new QueryColumn(LCSMaterialSupplierMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "supplierMasterReference.key.id"), new QueryColumn(com.lcs.wc.supplier.LCSSupplierMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));

		//appending material supplier type criteria
		appendMaterialSupplierCriteria(reportBean, pqs);

		if(reportBean.isHasFabricType()){
			pqs=SMMaterialForecastReportFabricHelper.appendFabricSupplierType(pqs);

		}

		//Adding joins to get Material color data
		if(includeMatColor){
			appendMaterialColorCriteria(overriddenRowOnly,includeOverridenMC,includeMaterialOverride,includeOvrMcOnly, pqs);
		}
		else{
			if(overriddenRowOnly){
				pqs.appendAndIfNeeded();
				pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,FlexBOMLink.class, "colorReference.key.id"), "0", Criteria.EQUALS));
				//Fix for JIRA 993 - Start (when color is not changed in overridden row, its fetching the base material, supplier, color information instead of overridden material and supplier)
				//pqs.appendAndIfNeeded();
				//pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK,FlexBOMLink.class, "colorReference.key.id"), "0", Criteria.EQUALS));
				//Fix for JIRA 993 - End
			}else{
				pqs.appendAndIfNeeded();

				pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK,FlexBOMLink.class, "colorReference.key.id"), "0", Criteria.EQUALS));
			}
		}

		//applying specification criteria
		setFlexspecificationQueryColumns(pqs);

		//applying season, spec criteria
		appendSeasonCriteria(pqs);
		

		//Updated for JIRA - SMPLM-605 - getting product BOM which are associated with only selected season
		if(seasonOids!=null){
			pqs.appendInCriteria(new QueryColumn(LCSSeason.class, "iterationInfo.branchId"), seasonOids); 
		}

		//append select column from the table column.
		appendColumnFromTableColumn(reportBean, pqs,"LCSMATERIAL","LCSMATERIALSUPPLIER",includeMatColor,overriddenRowOnly);

	}


	

	/**
	 * @param pqs
	 * @throws WTException
	 */
	public void setFlexspecificationQueryColumns(
			com.lcs.wc.db.PreparedQueryStatement pqs) throws WTException {
		pqs.appendFromTable(FlexSpecToComponentLink.class);

		pqs.appendFromTable(LATEST_ITER_FLEX_SPECIFICATION);
		pqs.appendSelectColumn(new QueryColumn(LATEST_ITER_FLEX_SPECIFICATION, FlexSpecification.class, "specSourceReference.key.id"));

		pqs.appendJoin(new QueryColumn(FlexSpecToComponentLink.class, "componentReference.key.id"), new QueryColumn(FlexBOMPart.class, "masterReference.key.id"));
		pqs.appendAndIfNeeded();
		pqs.appendJoin(new QueryColumn(FlexBOMPart.class, VERSION_ID), new QueryColumn(FlexSpecToComponentLink.class, "componentVersion"));
		pqs.appendAndIfNeeded();
		pqs.appendJoin(new QueryColumn(FlexSpecToComponentLink.class, "specificationMasterReference.key.id"), new QueryColumn(LATEST_ITER_FLEX_SPECIFICATION, ID_A3MASTER_REFERENCE));
		pqs.appendAndIfNeeded();
		pqs.appendJoin(new QueryColumn(LATEST_ITER_FLEX_SPECIFICATION, "versionIdA2versionInfo"), new QueryColumn(FlexSpecToComponentLink.class, "specVersion"));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexSpecification.class, CHECKOUT_INFO_STATE), WRK_STATE, Criteria.NOT_EQUAL_TO));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexSpecification.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexSpecification.class, VERSION_ID), "A", Criteria.EQUALS));
	}


	/**
	 * Method appendSeasonCriteria - appendSeasonCriteria.
	 * @param pqs the querystatement.
	 * @throws WTException the WTException.
	 */
	public void appendSeasonCriteria(
			com.lcs.wc.db.PreparedQueryStatement pqs) throws WTException {
		pqs.appendFromTable(com.lcs.wc.specification.FlexSpecToSeasonLink.class);
		pqs.appendFromTable(com.lcs.wc.season.LCSSeason.class);
		pqs.appendJoin(new QueryColumn(com.lcs.wc.specification.FlexSpecToSeasonLink.class, "roleAObjectRef.key.id"), new QueryColumn(FlexSpecification.class, MASTER_REFERENCE_KEY_ID));
		pqs.appendJoin(new QueryColumn(com.lcs.wc.specification.FlexSpecToSeasonLink.class, "roleBObjectRef.key.id"), new QueryColumn(com.lcs.wc.season.LCSSeason.class, MASTER_REFERENCE_KEY_ID));

		pqs.appendJoin(new QueryColumn(LCSProduct.class, MASTER_REFERENCE_KEY_ID), new QueryColumn(FlexSpecification.class, "specOwnerReference.key.id"));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(com.lcs.wc.season.LCSSeason.class, CHECKOUT_INFO_STATE), WRK_STATE, Criteria.NOT_EQUAL_TO));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(com.lcs.wc.season.LCSSeason.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(com.lcs.wc.season.LCSSeason.class, VERSION_ID), "A", Criteria.EQUALS));

		pqs.appendFromTable(FlexSpecification.class);
		pqs.appendFromTable(com.lcs.wc.specification.FlexSpecToComponentLink.class);
		
		//pqs.appendSelectColumn(new QueryColumn(FlexSpecification.class, "iterationInfo.branchId"));

		pqs.appendJoin(new QueryColumn(com.lcs.wc.specification.FlexSpecToComponentLink.class, "componentReference.key.id"), new QueryColumn(BOMPARTMASTER, FlexBOMPartMaster.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendJoin(new QueryColumn(com.lcs.wc.specification.FlexSpecToComponentLink.class, "specificationMasterReference.key.id"), new QueryColumn(FlexSpecification.class, MASTER_REFERENCE_KEY_ID));

		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.specification.FlexSpecToComponentLink.class, "primaryComponent"));
	}


	/**
	 * Method appendMaterialColorCriteria - appendMaterialColorCriteria.
	 * @param includeMaterialColor the includeMaterialColor.
	 * @param pqs the pqs.
	 * @throws WTException the WTException.
	 */
	public void appendMaterialColorCriteria(
			boolean overriddenRowOnly,boolean includeOverridenMC,boolean includeMaterialOverride,boolean includeOvrMcOnly, 
			com.lcs.wc.db.PreparedQueryStatement pqs) throws WTException {
		pqs.appendFromTable(com.lcs.wc.color.LCSColor.class);
		pqs.appendFromTable("V_LCSMaterialColor", LCSMATERIALCOLOR);
		pqs.appendSelectColumn(new QueryColumn(LCSMATERIALCOLOR,LCSMaterialColor.class,"thePersistInfo.theObjectIdentifier.id"));
		pqs.appendSelectColumn(new QueryColumn(LCSMATERIALCOLOR, "statestate"));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.color.LCSColor.class, "colorName"));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.color.LCSColor.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.color.LCSColor.class, "colorHexidecimalValue"));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.color.LCSColor.class, "thumbnail"));

		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "materialMasterReference.key.id(+)"),new QueryColumn(LCSMATERIALCOLOR,LCSMaterialColor.class,"materialMasterReference.key.id"));

		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "supplierMasterReference.key.id(+)"), new QueryColumn(LCSMATERIALCOLOR, LCSMaterialColor.class,"supplierMasterReference.key.id"));

		
		if(includeMaterialOverride){
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW,FlexBOMLink.class, "colorReference.key.id"), new QueryColumn(LCSMATERIALCOLOR, LCSMaterialColor.class,"colorReference.key.id")); // COLOR

		}else if(includeOvrMcOnly && overriddenRowOnly && includeOverridenMC){
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW,FlexBOMLink.class, "colorReference.key.id"), new QueryColumn(LCSMATERIALCOLOR, LCSMaterialColor.class,"colorReference.key.id")); // COLOR
		
		}else if(!includeOvrMcOnly && overriddenRowOnly && includeOverridenMC){
			pqs.appendJoin(new QueryColumn(FLEX_BOM_LINK,FlexBOMLink.class, "colorReference.key.id"), new QueryColumn(LCSMATERIALCOLOR, LCSMaterialColor.class,"colorReference.key.id")); // COLOR
		
		}else if(overriddenRowOnly){
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW,FlexBOMLink.class, "colorReference.key.id"), new QueryColumn(LCSMATERIALCOLOR, LCSMaterialColor.class,"colorReference.key.id")); // COLOR
		}else{
			pqs.appendJoin(new QueryColumn(FLEX_BOM_LINK,FlexBOMLink.class, "colorReference.key.id"), new QueryColumn(LCSMATERIALCOLOR, LCSMaterialColor.class,"colorReference.key.id")); // COLOR
		}

		pqs.appendJoin(new QueryColumn("MATERIALMASTER", LCSMaterialMaster.class, "thePersistInfo.theObjectIdentifier.id(+)"),new QueryColumn(LCSMATERIALCOLOR, "idA3A10"));

		pqs.appendJoin(new QueryColumn(com.lcs.wc.supplier.LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id(+)"), new QueryColumn(LCSMATERIALCOLOR, "idA3C10"));

		pqs.appendJoin(new QueryColumn(com.lcs.wc.color.LCSColor.class, "thePersistInfo.theObjectIdentifier.id"),new QueryColumn(LCSMATERIALCOLOR, "idA3B10"));

		FlexTypeGenerator flexg = new FlexTypeGenerator();
		FlexType matColorflexType = FlexTypeCache.getFlexTypeRoot("Material Color");			 
		flexg.setScope(null);
		flexg.appendQueryColumns(matColorflexType,pqs);			

	}


	/**
	 * Method appendColumnFromTableColumn - method to appendColumnFromTableColumn.
	 * @param reportBean the reportBean.
	 * @param pqs the pqs.
	 * @throws WTException 
	 */
	public void appendColumnFromTableColumn(
			SMMaterialForecastReportBean reportBean,
			com.lcs.wc.db.PreparedQueryStatement pqs,String materialName, String materialSupplierType,boolean includeMatCol, boolean includeOverriden) throws WTException {
		String tableIndx=EMPTY_STRING;
		for (String attrIntName : reportBean.getAttList()) { 
			com.lcs.wc.client.web.TableColumn column= reportBean.getColumns().get(attrIntName);		
			tableIndx=column.getTableIndex();		

			if(!includeMatCol && tableIndx.substring(0,tableIndx.indexOf('.')).indexOf("LCSMATERIALCOLOR")>-1 ){
				continue;
			}else if(tableIndx.substring(0,tableIndx.toUpperCase().indexOf('.')).indexOf(materialName)>-1 || tableIndx.substring(0,tableIndx.indexOf('.')).indexOf("FLEXBOMLINK")>-1
					|| tableIndx.substring(0,tableIndx.indexOf('.')).indexOf("BOM")>-1){ 
				pqs.appendSelectColumn(new QueryColumn(tableIndx.substring(0,tableIndx.indexOf('.')), tableIndx.substring(tableIndx.indexOf('.')+1)));
				if(includeOverriden && tableIndx.substring(0,tableIndx.indexOf('.')).indexOf("FLEXBOMLINK")>-1){
					pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, tableIndx.substring(tableIndx.indexOf('.')+1)));
				}
			}else if(tableIndx.substring(0,tableIndx.toUpperCase().indexOf('.')).indexOf(materialSupplierType)>-1 || 
					tableIndx.substring(0,tableIndx.indexOf('.')).indexOf("LCSMATERIALCOLOR")>-1){  
				pqs.appendSelectColumn(new QueryColumn(tableIndx.substring(0,tableIndx.indexOf('.')), tableIndx.substring(tableIndx.indexOf('.')+1)));
			}else if(reportBean.isHasFabricType() && (tableIndx.substring(0,tableIndx.toUpperCase().indexOf('.')).equals("FABRIC_SUPPLIER") || 
					tableIndx.substring(0,tableIndx.indexOf('.')).equals("FABRIC"))){  
				pqs.appendSelectColumn(new QueryColumn(tableIndx.substring(0,tableIndx.indexOf('.')), tableIndx.substring(tableIndx.indexOf('.')+1)));
			}
		} 

		try{
			FlexTypeGenerator flexg = new FlexTypeGenerator();
			FlexType materialType = FlexTypeCache.getFlexTypeRoot("Material");
			flexg.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE);
			flexg.appendQueryColumns(materialType,pqs);
		}catch(WTException ex){
			ex.printStackTrace();
		}
	}



	/*	*//**
	 * @param reportBean
	 * @param pqs
	 * @throws WTException
	 */
	public void appendMaterialTypeCriteria(
			SMMaterialForecastReportBean reportBean, PreparedQueryStatement pqs)
					throws WTException {

		LCSQuery.addFlexTypeInformation(pqs,LCSMaterial.class);
		//APPEND Material Type CRITERIA
		if(reportBean.getSelectedMaterialTypeOids()!=null && !reportBean.getSelectedMaterialTypeOids().isEmpty())
		{
			boolean isfirstIteration=true;
			for(String materialTypeId: reportBean.getSelectedMaterialTypeOids()){
				FlexType tempType = FlexTypeCache.getFlexType(materialTypeId); 

				Collection<FlexType> viewableChildren = tempType.getAllViewableChildren();
				if(!isfirstIteration){
					pqs.appendOrIfNeeded();
				}else{
					pqs.appendAndIfNeeded();
					pqs.appendOpenParen();
				}
				pqs.appendCriteria(new Criteria(new QueryColumn(LCS_MATERIAL, LCSMaterial.class,"typeDefinitionReference.key.branchId"), "?", Criteria.EQUALS), 
						new Long(FormatHelper.getNumericFromOid(materialTypeId)));

				for (FlexType child : viewableChildren) {
					pqs.appendOr();
					pqs.appendCriteria(new Criteria(new QueryColumn(LCS_MATERIAL, LCSMaterial.class,"typeDefinitionReference.key.branchId"), "?", Criteria.EQUALS), 
							new Long(FormatHelper.getNumericFromOid(FormatHelper.getNumericObjectIdFromObject(child))));
				}
				isfirstIteration=false;
			}
			if(!isfirstIteration){
				pqs.appendClosedParen();
			}

		}
	}

	/*	*//**
	 * @param reportBean
	 * @param pqs
	 * @throws WTException
	 */
	public void appendMaterialSupplierCriteria(
			SMMaterialForecastReportBean reportBean, PreparedQueryStatement pqs)
					throws WTException {
		com.lcs.wc.client.web.FlexTypeGenerator flexg = new com.lcs.wc.client.web.FlexTypeGenerator();

		//APPEND SUPPLIER CRITERIA
		if((reportBean.getSelectedMatSupplierOids()!=null && !reportBean.getSelectedMatSupplierOids().isEmpty()) 
				|| reportBean.isNominatedSupplier())
		{
			if(reportBean.getSelectedMatSupplierOids()!=null && reportBean.getSelectedMatSupplierOids().size()>0 )
			{
				pqs.appendAndIfNeeded();
				boolean isfirstIteration=true;
				pqs.appendOpenParen();
				for(String supplierMasterId: reportBean.getSelectedMatSupplierOids()){
					if (com.lcs.wc.supplier.LCSSupplierQuery.PLACEHOLDERID.equals(supplierMasterId)) {
						continue;
					}

					if(!isfirstIteration){
						pqs.appendOrIfNeeded();
					}
					pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class, "supplierReference.key.id"), "?", Criteria.EQUALS), new Long(FormatHelper.getNumericFromOid(supplierMasterId)));

					isfirstIteration=false;
				}
				pqs.appendClosedParen();
			}

			if(reportBean.isNominatedSupplier()){	
				pqs.appendAndIfNeeded();

				FlexType materialType = FlexTypeCache.getFlexTypeFromPath("Material");				 
				flexg.setScope(com.lcs.wc.material.MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE);
				flexg.setLevel(null);
				flexg.appendAttCriteria(materialType.getAttribute(SM_MS_NOMINATED), "smYes", "smYes","smYes","LCSMaterialSupplier", pqs);
			}
		}
	}


	/**Method getSelectedCriteria - method to add the select criteria.
	 * @param inputSelectedMap the inputSelectedMap.
	 * @param reportBean the reportBean.
	 * @return SMMaterialForecastReportBean.
	 */
	public static SMMaterialForecastReportBean getSelectedCriteria(
			Map<String, Object> inputSelectedMap,
			SMMaterialForecastReportBean reportBean) {
		String selectedSeasonsIds = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("seasonIds"));
		if(!selectedSeasonsIds.isEmpty()){
			reportBean.setSelectedSeasonOids(MOAHelper.getMOACollection(selectedSeasonsIds));
		}
		String materialTypes = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("materialType"));
		if(!materialTypes.isEmpty()){
			reportBean.setSelectedMaterialTypeOids(MOAHelper.getMOACollection(materialTypes));
			for(String materialTypeId: reportBean.getSelectedMaterialTypeOids()){
				try {
					FlexType tempType = FlexTypeCache.getFlexType(materialTypeId);
					if(tempType.getFullNameDisplay(true).contains("Fabric")){
						reportBean.setHasFabricType(true);
					}else{
						reportBean.setHasOtherMaterialType(true);
					}
				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}

		String brands = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("brands"));
		if(!brands.isEmpty()){
			reportBean.setSelectedBrands(MOAHelper.getMOACollection(brands));
		}

		String materialSuppliers = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("materialSupplier"));
		if(!materialSuppliers.isEmpty()){
			reportBean.setSelectedMatSupplierOids(MOAHelper.getMOACollection(materialSuppliers));
		}		

		reportBean.setNominatedSupplier(FormatHelper.parseBoolean(EMPTY_STRING+inputSelectedMap.get("nominated")));		

		return reportBean;
	}


}
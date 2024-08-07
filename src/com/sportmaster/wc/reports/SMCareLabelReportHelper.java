package com.sportmaster.wc.reports;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.client.web.FlexTypeGenerator;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
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

import wt.util.WTException;
/**
 * The Class SMCareLabelReportHelper.
 *
 * @version 'true' 1.0 version number.
 * @author 'true' ITC.
 */
public class SMCareLabelReportHelper {
	/**
	 * Constant VERSION_ID.
	 */
	private static final String VERSION_ID = "versionInfo.identifier.versionId";
	/**
	 * Constant LATEST_ITER_FLEX_SPECIFICATION.
	 */
	private static final String LATEST_ITER_FLEX_SPECIFICATION = "LatestIterFlexSpecification";
	/**
	 * Constant ID_A3MASTER_REFERENCE.
	 */
	private static final String ID_A3MASTER_REFERENCE = "idA3masterReference";
	/**
	 * Constant BRANCH_ID.
	 */
	private static final String BRANCH_ID = "branchId";
	/**
	 * Constant CCC.
	 */
	private static final String CCC = LCSProperties.get("bomLink.CCC");

	/**
	 * Constant DIMENSION_ID.
	 */
	private static final String DIMENSION_ID = "dimensionId";
	/**
	 * Constant DIMENSION_NAME.
	 */
	private static final String DIMENSION_NAME = LCSProperties
			.get("bomLink.DIMENSIONNAME");
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
	/**
	 * Constant ALTPRIMARY.
	 */
	private static final String ATT_ALTPRIMARY_KEY = LCSProperties
			.get("bomLink.ALTPRIMARY");
	/**
	 * Constant PRIMARY.
	 */
	private static final String ATT_PRIMARY_KEY = LCSProperties
			.get("bomLink.PRIMARY");
	
	/**
	 * Constant SUPPLIER_MASTER_REFERENCE_KEY_ID.
	 */
	private static final String SUPPLIER_MASTER_REFERENCE_KEY_ID = "supplierMasterReference.key.id";
	/**
	 * Constant LOGGER
	 */
	private static final Logger LOGGER = Logger.getLogger("CARELABELREPORTLOG");
	
	/**
	 * Constant Managing Department.
	 */
	private static final String MANAGING_DEPART = LCSProperties.get("com.sportmaster.reports.careLabel.materialSupplierAttribute");
	
	/**
	 * Constant Pre-Retail.
	 */
	private static final String PRE_RETAIL = LCSProperties.get("com.sportmaster.reports.careLabel.smManagingDepartment");

	/**
	 * @param reportBean - reportBean
	 * @param seasonOids - seasonOids
	 * @param productOids - productOids
	 * @param includeMatColor - includeMatColor
	 * @param overriddenRowOnly - overriddenRowOnly
	 * @param includeOverridenMC - includeOverridenMC
	 * @param includeMaterialOverride - includeMaterialOverride
	 * @param includeOvrMcOnly - includeOvrMcOnly
	 * @return sr - sr
	 */
	public SearchResults getMaterialSuppFromBOMLinkData(
			SMCareLabelReportBean reportBean, Collection<String> seasonOids,
			Collection<String> productOids, boolean includeMatColor,
			boolean overriddenRowOnly, boolean includeOverridenMC,
			boolean includeMaterialOverride, boolean includeOvrMcOnly) {
		com.lcs.wc.db.PreparedQueryStatement pqs = new com.lcs.wc.db.PreparedQueryStatement();
		SearchResults sr = null;
		try {
			// Get distinct rows
			pqs.setDistinct(true);
			pqs.appendFromTable(FlexBOMLink.class, FLEX_BOM_LINK);
			pqs.appendFromTable(FlexBOMPart.class);
			FlexType bomType = FlexTypeCache
					.getFlexTypeFromPath("BOM\\Materials\\Product");
			pqs.appendFromTable(LCSMaterial.class, LCS_MATERIAL);
			// Query to get BOMPart, BOM Link information
			FlexType bomLinkType = FlexTypeCache
					.getFlexTypeFromPath("BOM\\Materials\\Product");
			pqs.appendFromTable(FlexBOMPartMaster.class, BOMPARTMASTER);
			pqs.appendSelectColumn(new QueryColumn(BOMPARTMASTER,
					FlexBOMPartMaster.class,
					THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, PARENT_REFERENCE_KEY_ID)); // PARENT
																	// MASTER ID
			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, CHILD_REFERENCE_KEY_ID)); // CHILD MASTER
																	// ID
			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, COLOR_REFERENCE_KEY_ID)); // COLOR ID
			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, BRANCH_ID)); // BRANCH ID
			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, FlexBOMLink.YIELD)); // YIELD
			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class,
					THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, COLOR_DIMENSION_REFERENCE_KEY_ID)); // COLOR
																			// DIMENSION
																			// ID
			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, SOURCE_DIMENSION_REFERENCE_KEY_ID));
			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, DESTINATION_DIMENSION_REFERENCE_KEY_ID)); // OVerride
																					// level

			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, DESTINATION_DIMENSION_REFERENCE_KEY_ID));

			pqs.appendSelectColumn(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, bomType.getAttribute("colorDescription")
							.getColumnDescriptorName()));
			
			pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class,
					LCSQuery.TYPED_BRANCH_ID));
			pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class,
					CHECKOUT_INFO_STATE));
			pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class,
					"ownerMasterReference.key.id"));
			pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, bomType
					.getAttribute(ATT_NAME_KEY).getColumnDescriptorName()));
			pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class, "bomType"));
			pqs.appendSelectColumn(new QueryColumn(FlexBOMPart.class,
					THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

			pqs.appendSelectColumn(new QueryColumn(FlexBOMLink.class,
					bomLinkType.getAttribute(ATT_PRIMARY_KEY)
							.getColumnDescriptorName()));
			pqs.appendSelectColumn(new QueryColumn(FlexBOMLink.class,
					bomLinkType.getAttribute(ATT_ALTPRIMARY_KEY)
							.getColumnDescriptorName()));

			// LINK FLEXBOMLINK TO FLEXBOMPART MASTER.
			pqs.appendJoin(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class,
					PARENT_REFERENCE_KEY_ID), new QueryColumn(
					FlexBOMPart.class, MASTER_REFERENCE_KEY_ID));

			pqs.appendJoin(new QueryColumn(BOMPARTMASTER,
					FlexBOMPartMaster.class,
					THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID),
					new QueryColumn(FlexBOMPart.class, MASTER_REFERENCE_KEY_ID));

			// ONLY GET LATEST ITERATION OF BOM PART
			pqs.appendAndIfNeeded();
			pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class,
					CHECKOUT_INFO_STATE), WRK_STATE, Criteria.NOT_EQUAL_TO));

			pqs.appendAndIfNeeded();
			pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class,
					LATEST_ITERATION_INFO), "1", Criteria.EQUALS));
			// BOM Type
			pqs.appendAndIfNeeded();
			pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMPart.class,
					"bomType"), "?", Criteria.EQUALS), "MAIN");

			// SPECIFY ONLY NON WIP RECORDS
			pqs.appendAndIfNeeded();
			pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, "wip"), "0", Criteria.EQUALS));

			// SPECIFY ONLY NON DROPPED RECORDS
			pqs.appendAndIfNeeded();
			pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, "dropped"), "0", Criteria.EQUALS));

			pqs.appendAndIfNeeded();
			pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, "outDate"), EMPTY_STRING,
					Criteria.IS_NULL));

			pqs.appendAndIfNeeded();
			pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK,
					FlexBOMLink.class, DIMENSION_NAME), EMPTY_STRING,
					Criteria.IS_NULL));
			pqs.appendAndIfNeeded();
			// Method to get overridden rows
			if (overriddenRowOnly) {
				appendBOMOverrideCriteria(pqs, bomType);
			}
			
			// Method to get Material, Supplier, Material Supplier infomations
			appendCriteriaForBomLinkData(reportBean, seasonOids, productOids,
					pqs, includeMatColor, overriddenRowOnly,
					includeOverridenMC, includeMaterialOverride,
					includeOvrMcOnly);
			
			// Run the query
			sr = LCSQuery.runDirectQuery(pqs);
		} catch (WTException e) {
			LOGGER.error("WTException in SMCareLabelReportHelper - getMaterialSuppFromBOMLinkData: "
					+ e.getMessage());
		} catch (NumberFormatException ne) {
			LOGGER.error("NumberFormatException in SMCareLabelReportHelper - getMaterialSuppFromBOMLinkData: "
					+ ne.getMessage());
		}
		return sr;
	}

	/**
	 * @param pqs - pqs
	 * @param bomType - bomType
	 * @throws WTException - WTException
	 */
	private void appendBOMOverrideCriteria(
			com.lcs.wc.db.PreparedQueryStatement pqs, FlexType bomType)
			throws WTException {
		pqs.appendFromTable(com.lcs.wc.product.ProductDestination.class);
		// Product Destination flextype
		FlexType pdType = FlexTypeCache
				.getFlexTypeFromPath("Product Destination");
		FlexType bomLinkType = FlexTypeCache
				.getFlexTypeFromPath("BOM\\Materials\\Product");
		com.lcs.wc.flextype.FlexTypeAttribute nameAtt = pdType
				.getAttribute(ATT_NAME_KEY);
		String nameColumn = nameAtt.getColumnDescriptorName();
		pqs.appendSelectColumn(new QueryColumn(
				com.lcs.wc.product.ProductDestination.class,
				THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(
				com.lcs.wc.product.ProductDestination.class, "destinationName"));
		pqs.appendSelectColumn(new QueryColumn(
				com.lcs.wc.product.ProductDestination.class, nameColumn));

		pqs.appendFromTable(FlexBOMLink.class, OVERRIDEN_ROW);
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, BRANCH_ID)); // OVerride level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID)); // OVerride
																				// level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, "yield")); // OVerride level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, DIMENSION_NAME)); // OVerride level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, DESTINATION_DIMENSION_REFERENCE_KEY_ID)); // OVerride
																				// level
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, DIMENSION_ID));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, bomType.getAttribute(
						LCSProperties.get("bomLink.QUANTITY"))
						.getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, bomType.getAttribute(CCC)
						.getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, bomType.getAttribute("colorDescription")
						.getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, PARENT_REFERENCE_KEY_ID)); // PARENT MASTER
																// ID
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, CHILD_REFERENCE_KEY_ID)); // CHILD MASTER ID
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, COLOR_DIMENSION_REFERENCE_KEY_ID)); // COLOR
																		// DIMENSION
																		// ID
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, SOURCE_DIMENSION_REFERENCE_KEY_ID)); // SOURCE
																		// DIMENSION
																		// ID
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, COLOR_REFERENCE_KEY_ID)); // COLOR ID
		
		// Fix to get Primary/ Alt Primary from Overridden row
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,FlexBOMLink.class,
				bomLinkType.getAttribute(ATT_PRIMARY_KEY)
						.getColumnDescriptorName()));
		pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW,FlexBOMLink.class,
				bomLinkType.getAttribute(ATT_ALTPRIMARY_KEY)
						.getColumnDescriptorName()));

		pqs.appendJoin(new QueryColumn(FlexBOMLink.class,
				PARENT_REFERENCE_KEY_ID), new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, "parentReference.key.id"));

		pqs.appendJoin(new QueryColumn(FlexBOMLink.class, BRANCH_ID),
				new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class, BRANCH_ID));

		pqs.appendJoin(new QueryColumn(
				com.lcs.wc.product.ProductDestination.class,
				"thePersistInfo.theObjectIdentifier.id(+)"), new QueryColumn(
				OVERRIDEN_ROW, FlexBOMLink.class,
				DESTINATION_DIMENSION_REFERENCE_KEY_ID));

		// SPECIFY ONLY NON WIP RECORDS
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, "wip"), "0", Criteria.EQUALS));

		// SPECIFY ONLY NON DROPPED RECORDS
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, "dropped"), "0", Criteria.EQUALS));
		// Out date is null to get latest row
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, "outDate"), EMPTY_STRING, Criteria.IS_NULL));

		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, DIMENSION_NAME), EMPTY_STRING,
				Criteria.IS_NOT_NULL));		
		
		// Fix for fetching overridden rows based on Primary/ Alt Primary Value: True - Start
		// Primary or Alt Primary has to be true, to pull the bom link on
		// report
		pqs.appendAndIfNeeded();
		pqs.appendOpenParen();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, bomLinkType
						.getAttribute(ATT_PRIMARY_KEY)
						.getColumnDescriptorName()), "1", Criteria.EQUALS));
		pqs.appendOrIfNeeded();
		pqs.appendOpenParen();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, bomLinkType
						.getAttribute(ATT_PRIMARY_KEY)
						.getColumnDescriptorName()), "", Criteria.IS_NULL));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMLink.class, bomLinkType
						.getAttribute(ATT_PRIMARY_KEY)
						.getColumnDescriptorName()), "1", Criteria.EQUALS));
		pqs.appendClosedParen();
		
		pqs.appendOrIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, bomLinkType.getAttribute(
						ATT_ALTPRIMARY_KEY).getColumnDescriptorName()),
				"1", Criteria.EQUALS));
		pqs.appendOrIfNeeded();
		pqs.appendOpenParen();
		pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,
				FlexBOMLink.class, bomLinkType.getAttribute(
						ATT_ALTPRIMARY_KEY).getColumnDescriptorName()),
				"", Criteria.IS_NULL));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(FlexBOMLink.class, bomLinkType.getAttribute(
						ATT_ALTPRIMARY_KEY).getColumnDescriptorName()),
				"1", Criteria.EQUALS));
		pqs.appendClosedParen();
		
		//Added for - 3.8.2.0 build - Start
		
		pqs.appendOrIfNeeded();
		String managingDeptColunName = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.material.LCSMaterialSupplier")
				.getAttribute(MANAGING_DEPART).getColumnDescriptorName();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, managingDeptColunName), PRE_RETAIL,
				Criteria.EQUALS));
		 
	//Added for - 3.8.2.0 build - End
		
		pqs.appendClosedParen();
		// Fix for fetching overridden rows based on Primary/ Alt Primary Value: True - End
		
	}

	/**
	 * Method appendCriteriaForBomLinkData - all criteria is added in this
	 * method.
	 * 
	 * @param reportBean - reportBean
	 * @param seasonOids - seasonOids
	 * @param productOids - productOids
	 * @param pqs - pqs
	 * @param includeMatColor - includeMatColor
	 * @param overriddenRowOnly - overriddenRowOnly
	 * @param includeOverridenMC - includeOverridenMC
	 * @param includeMaterialOverride - includeMaterialOverride
	 * @param includeOvrMcOnly - includeOvrMcOnly
	 * @throws WTException - WTException
	 * @throws NumberFormatException - NumberFormatException
	 */
	public void appendCriteriaForBomLinkData(SMCareLabelReportBean reportBean,
			Collection<String> seasonOids, Collection<String> productOids,
			com.lcs.wc.db.PreparedQueryStatement pqs, boolean includeMatColor,
			boolean overriddenRowOnly, boolean includeOverridenMC,
			boolean includeMaterialOverride, boolean includeOvrMcOnly)
			 {
		try {
			
		
		pqs.appendFromTable(LCSProduct.class);
		pqs.appendSelectColumn(new QueryColumn(LCSProduct.class,
				LCSQuery.TYPED_BRANCH_ID));
		pqs.appendSelectColumn(new QueryColumn(LCSProduct.class,
				MASTER_REFERENCE_KEY_ID));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class,
				CHECKOUT_INFO_STATE), WRK_STATE, Criteria.NOT_EQUAL_TO));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class,
				LATEST_ITERATION_INFO), "1", Criteria.EQUALS));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class,
				VERSION_ID), "A", Criteria.EQUALS));
		
		
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
		
		//updated for 3.9.0.0 Build, fixed JIRA-1261 STARTS
				if(seasonOids!=null){
					Iterator seasonIter = seasonOids.iterator();
					pqs.appendAndIfNeeded();
					pqs.appendOpenParen();
					String singleseasonId = "";
					while(seasonIter.hasNext())  {
						   singleseasonId = (String)seasonIter.next();
						   pqs.appendCriteria(new Criteria(new QueryColumn(LCSSeason.class, "iterationInfo.branchId"), singleseasonId, Criteria.EQUALS));
						   if (seasonIter.hasNext()) 
						   {
							   pqs.appendOr();
						   }
					}
					pqs.appendClosedParen();
				}
				//updated for 3.9.0.0 Build, fixed JIRA-1261 ENDS
		
		/*if (productOids != null) {
			pqs.appendInCriteria(new QueryColumn(LCSProduct.class,
					MASTER_REFERENCE_KEY_ID), productOids);
		}*/
		// Fix for max limit cannot be more than 1000 in a list - using appendor instead of appendin criteria - end
		
		pqs.appendJoin(new QueryColumn(LCSProduct.class,
				MASTER_REFERENCE_KEY_ID), new QueryColumn(FlexBOMPart.class,
				"ownerMasterReference.key.id"));
		// adding material criteria
		// Adding material table
		pqs.appendSelectColumn(new QueryColumn(LCS_MATERIAL,
				ID_A3MASTER_REFERENCE));
		pqs.appendSelectColumn(new QueryColumn(LCS_MATERIAL, LCSMaterial.class,
				THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(LCS_MATERIAL, LCSMaterial.class,
				"iterationInfo.branchId"));
		if (includeMaterialOverride) {
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class,
					CHILD_REFERENCE_KEY_ID), new QueryColumn(LCS_MATERIAL,
					ID_A3MASTER_REFERENCE)); // MATERIAL
		} else {
			pqs.appendJoin(new QueryColumn(FlexBOMLink.class,
					CHILD_REFERENCE_KEY_ID), new QueryColumn(LCS_MATERIAL,
					ID_A3MASTER_REFERENCE)); // MATERIAL
		}
		// appending material type criteria
		// appendMaterialTypeCriteria(reportBean, pqs);
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(LCS_MATERIAL,
				LCSMaterial.class, LATEST_ITERATION_INFO), "1", Criteria.EQUALS));

		pqs.appendFromTable(LCSMaterialMaster.class, MATERIALMASTER);
		pqs.appendSelectColumn(new QueryColumn(MATERIALMASTER,
				LCSMaterialMaster.class, ATT_NAME_KEY));
		pqs.appendSelectColumn(new QueryColumn(MATERIALMASTER,
				LCSMaterialMaster.class,
				THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendJoin(new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE),
				new QueryColumn(MATERIALMASTER, LCSMaterialMaster.class,
						THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(MATERIALMASTER,
				LCSMaterialMaster.class, ATT_NAME_KEY), "material_placeholder",
				Criteria.NOT_EQUAL_TO));

		if (includeMaterialOverride) {
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class,
					"supplierReference.key.id"), new QueryColumn(
					LCSMaterialSupplierMaster.class,
					SUPPLIER_MASTER_REFERENCE_KEY_ID)); // SUPPLIER
		} else {
			pqs.appendJoin(new QueryColumn(FlexBOMLink.class,
					"supplierReference.key.id"), new QueryColumn(
					LCSMaterialSupplierMaster.class,
					SUPPLIER_MASTER_REFERENCE_KEY_ID)); // SUPPLIER
		}
		// Adding supplier critieria.
		// Adding material supplier tables
		pqs.appendFromTable(com.lcs.wc.supplier.LCSSupplierMaster.class);
		pqs.appendSelectColumn(new QueryColumn(
				com.lcs.wc.supplier.LCSSupplierMaster.class, "supplierName"));
		pqs.appendSelectColumn(new QueryColumn(
				com.lcs.wc.supplier.LCSSupplierMaster.class,
				THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendFromTable(LCSMaterialSupplier.class);
		pqs.appendFromTable(LCSMaterialSupplierMaster.class);
		pqs.appendSelectColumn(new QueryColumn(LCSMaterialSupplierMaster.class,
				THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class,
				"iterationInfo.branchId"));
		pqs.appendJoin(new QueryColumn(LCS_MATERIAL, ID_A3MASTER_REFERENCE),
				new QueryColumn(LCSMaterialSupplierMaster.class,
						"materialMasterReference.key.id")); // MATERIAL
		pqs.appendJoin(new QueryColumn(LCSMaterialSupplier.class,
				MASTER_REFERENCE_KEY_ID), new QueryColumn(
				LCSMaterialSupplierMaster.class,
				THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class,
				SUPPLIER_MASTER_REFERENCE_KEY_ID), new QueryColumn(
				com.lcs.wc.supplier.LCSSupplierMaster.class,
				THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterialSupplier.class, LATEST_ITERATION_INFO), "1",
				Criteria.EQUALS));
		
		// Adding joins to get Material color data
		if (includeMatColor) {
			appendMaterialColorCriteria(overriddenRowOnly, includeOverridenMC,
					includeMaterialOverride, includeOvrMcOnly, pqs);
		} else {
			if (overriddenRowOnly) {
				pqs.appendAndIfNeeded();
				pqs.appendCriteria(new Criteria(new QueryColumn(OVERRIDEN_ROW,
						FlexBOMLink.class, COLOR_REFERENCE_KEY_ID), "0",
						Criteria.EQUALS));
				
				// Fix for overridden row, when there is only material change without changing the color in overridden row
			/*	pqs.appendAndIfNeeded();
				pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK,
						FlexBOMLink.class, COLOR_REFERENCE_KEY_ID), "0",
						Criteria.EQUALS));*/
			} else {
				pqs.appendAndIfNeeded();

				pqs.appendCriteria(new Criteria(new QueryColumn(FLEX_BOM_LINK,
						FlexBOMLink.class, COLOR_REFERENCE_KEY_ID), "0",
						Criteria.EQUALS));
			}
		}
		// applying specification criteria
		setFlexspecificationQueryColumns(pqs);
		// applying season, spec criteria
		appendSeasonCriteria(pqs);
		// append select column from the table column.
		appendColumnFromTableColumn(reportBean, pqs, "LCSMATERIAL",
				"LCSMATERIALSUPPLIER", includeMatColor, overriddenRowOnly);
		}catch(WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method appendColumnFromTableColumn - method to
	 * appendColumnFromTableColumn.
	 * 
	 * @param reportBean - reportBean
	 * @param pqs - pqs
	 * @param materialName - materialName
	 * @param materialSupplierType - materialSupplierType
	 * @param includeMatCol - includeMatCol
	 * @param includeOverriden - includeOverriden
	 * @throws WTException - WTException
	 */
	public void appendColumnFromTableColumn(SMCareLabelReportBean reportBean,
			com.lcs.wc.db.PreparedQueryStatement pqs, String materialName,
			String materialSupplierType, boolean includeMatCol,
			boolean includeOverriden) throws WTException {
		String tableIndx = EMPTY_STRING;
		// Iterate attlist
		for (String attrIntName : reportBean.getAttList()) {
			com.lcs.wc.client.web.TableColumn column = reportBean.getColumns()
					.get(attrIntName);
			tableIndx = column.getTableIndex();

			if (!includeMatCol
					&& tableIndx.substring(0, tableIndx.indexOf('.')).indexOf(
							"LCSMATERIALCOLOR") > -1) {
				continue;
			} else if (tableIndx.substring(0,
					tableIndx.toUpperCase().indexOf('.')).indexOf(materialName) > -1
					|| tableIndx.substring(0, tableIndx.indexOf('.')).indexOf(
							"FLEXBOMLINK") > -1
					|| tableIndx.substring(0, tableIndx.indexOf('.')).indexOf(
							"BOM") > -1) {
				pqs.appendSelectColumn(new QueryColumn(tableIndx.substring(0,
						tableIndx.indexOf('.')), tableIndx.substring(tableIndx
						.indexOf('.') + 1)));
				includeOverridenBOMLinks(includeOverriden, tableIndx, pqs);
			} else if (tableIndx.substring(0,
					tableIndx.toUpperCase().indexOf('.')).indexOf(
					materialSupplierType) > -1
					|| tableIndx.substring(0, tableIndx.indexOf('.')).indexOf(
							"LCSMATERIALCOLOR") > -1) {
				pqs.appendSelectColumn(new QueryColumn(tableIndx.substring(0,
						tableIndx.indexOf('.')), tableIndx.substring(tableIndx
						.indexOf('.') + 1)));
			}
		}
		FlexTypeGenerator flexg = new FlexTypeGenerator();
		FlexType materialType = FlexTypeCache.getFlexTypeRoot("Material");
		flexg.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE);
		flexg.appendQueryColumns(materialType, pqs);
	}

	/**
	 * @param includeOverriden - includeOverriden
	 * @param tableIndx - tableIndx
	 * @param pqs - pqs
	 */
	private void includeOverridenBOMLinks(boolean includeOverriden,
			String tableIndx, PreparedQueryStatement pqs) {
		if (includeOverriden
				&& tableIndx.substring(0, tableIndx.indexOf('.')).indexOf(
						"FLEXBOMLINK") > -1) {
			pqs.appendSelectColumn(new QueryColumn(OVERRIDEN_ROW, tableIndx
					.substring(tableIndx.indexOf('.') + 1)));
		}

	}

	/**
	 * Method appendSeasonCriteria - appendSeasonCriteria.
	 * 
	 * @param pqs
	 *            the querystatement.
	 * @throws WTException
	 *             the WTException.
	 */
	public void appendSeasonCriteria(com.lcs.wc.db.PreparedQueryStatement pqs)
			throws WTException {
		// Include Season criteria
		pqs.appendFromTable(com.lcs.wc.specification.FlexSpecToSeasonLink.class);
		pqs.appendFromTable(com.lcs.wc.season.LCSSeason.class);
		pqs.appendJoin(new QueryColumn(
				com.lcs.wc.specification.FlexSpecToSeasonLink.class,
				"roleAObjectRef.key.id"), new QueryColumn(
				FlexSpecification.class, MASTER_REFERENCE_KEY_ID));
		pqs.appendJoin(new QueryColumn(
				com.lcs.wc.specification.FlexSpecToSeasonLink.class,
				"roleBObjectRef.key.id"), new QueryColumn(
				com.lcs.wc.season.LCSSeason.class, MASTER_REFERENCE_KEY_ID));

		pqs.appendJoin(new QueryColumn(LCSProduct.class,
				MASTER_REFERENCE_KEY_ID), new QueryColumn(
				FlexSpecification.class, "specOwnerReference.key.id"));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(
				com.lcs.wc.season.LCSSeason.class, CHECKOUT_INFO_STATE),
				WRK_STATE, Criteria.NOT_EQUAL_TO));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(
				com.lcs.wc.season.LCSSeason.class, LATEST_ITERATION_INFO), "1",
				Criteria.EQUALS));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(
				com.lcs.wc.season.LCSSeason.class, VERSION_ID), "A",
				Criteria.EQUALS));

		pqs.appendFromTable(FlexSpecification.class);
		pqs.appendFromTable(com.lcs.wc.specification.FlexSpecToComponentLink.class);

		pqs.appendJoin(new QueryColumn(
				com.lcs.wc.specification.FlexSpecToComponentLink.class,
				"componentReference.key.id"), new QueryColumn(BOMPARTMASTER,
				FlexBOMPartMaster.class,
				THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));

		pqs.appendJoin(new QueryColumn(
				com.lcs.wc.specification.FlexSpecToComponentLink.class,
				"specificationMasterReference.key.id"), new QueryColumn(
				FlexSpecification.class, MASTER_REFERENCE_KEY_ID));

		pqs.appendSelectColumn(new QueryColumn(
				com.lcs.wc.specification.FlexSpecToComponentLink.class,
				"primaryComponent"));
	}

	/**
	 * @param pqs - pqs
	 * @throws WTException - WTException
	 */
	public void setFlexspecificationQueryColumns(
			com.lcs.wc.db.PreparedQueryStatement pqs) throws WTException {
		pqs.appendFromTable(FlexSpecToComponentLink.class);
		pqs.appendFromTable(LATEST_ITER_FLEX_SPECIFICATION);
		pqs.appendSelectColumn(new QueryColumn(LATEST_ITER_FLEX_SPECIFICATION,
				FlexSpecification.class, "specSourceReference.key.id"));
		// Link Spec and BOMPart
		pqs.appendJoin(new QueryColumn(FlexSpecToComponentLink.class,
				"componentReference.key.id"), new QueryColumn(
				FlexBOMPart.class, "masterReference.key.id"));
		pqs.appendAndIfNeeded();
		pqs.appendJoin(new QueryColumn(FlexBOMPart.class, VERSION_ID),
				new QueryColumn(FlexSpecToComponentLink.class,
						"componentVersion"));
		pqs.appendAndIfNeeded();
		pqs.appendJoin(new QueryColumn(FlexSpecToComponentLink.class,
				"specificationMasterReference.key.id"), new QueryColumn(
				LATEST_ITER_FLEX_SPECIFICATION, ID_A3MASTER_REFERENCE));
		pqs.appendAndIfNeeded();
		pqs.appendJoin(new QueryColumn(LATEST_ITER_FLEX_SPECIFICATION,
				"versionIdA2versionInfo"), new QueryColumn(
				FlexSpecToComponentLink.class, "specVersion"));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(
				FlexSpecification.class, CHECKOUT_INFO_STATE), WRK_STATE,
				Criteria.NOT_EQUAL_TO));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(
				FlexSpecification.class, LATEST_ITERATION_INFO), "1",
				Criteria.EQUALS));
		pqs.appendAndIfNeeded();
		pqs.appendCriteria(new Criteria(new QueryColumn(
				FlexSpecification.class, VERSION_ID), "A", Criteria.EQUALS));
	}

	/**
	 * Method appendMaterialColorCriteria - appendMaterialColorCriteria.
	 * 
	 * @param overriddenRowOnly - overriddenRowOnly
	 * @param includeOverridenMC - includeOverridenMC
	 * @param includeMaterialOverride - includeMaterialOverride
	 * @param includeOvrMcOnly - includeOvrMcOnly
	 * @param pqs - pqs
	 * @throws WTException - WTException
	 */
	public void appendMaterialColorCriteria(boolean overriddenRowOnly,
			boolean includeOverridenMC, boolean includeMaterialOverride,
			boolean includeOvrMcOnly, com.lcs.wc.db.PreparedQueryStatement pqs)
			throws WTException {
		// Material Color criteria
		pqs.appendFromTable(com.lcs.wc.color.LCSColor.class);
		pqs.appendFromTable("V_LCSMaterialColor", LCSMATERIALCOLOR);
		pqs.appendSelectColumn(new QueryColumn(LCSMATERIALCOLOR,
				LCSMaterialColor.class, "thePersistInfo.theObjectIdentifier.id"));
		pqs.appendSelectColumn(new QueryColumn(LCSMATERIALCOLOR, "statestate"));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.color.LCSColor.class,
				"colorName"));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.color.LCSColor.class,
				THE_PERSIST_INFO_THE_OBJECT_IDENTIFIER_ID));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.color.LCSColor.class,
				"colorHexidecimalValue"));
		pqs.appendSelectColumn(new QueryColumn(com.lcs.wc.color.LCSColor.class,
				"thumbnail"));
		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class,
				"materialMasterReference.key.id(+)"), new QueryColumn(
				LCSMATERIALCOLOR, LCSMaterialColor.class,
				"materialMasterReference.key.id"));

		pqs.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class,
				"supplierMasterReference.key.id(+)"), new QueryColumn(
				LCSMATERIALCOLOR, LCSMaterialColor.class,
				SUPPLIER_MASTER_REFERENCE_KEY_ID));
		// if includeMaterialOverride
		if (includeMaterialOverride) {
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class,
					COLOR_REFERENCE_KEY_ID), new QueryColumn(LCSMATERIALCOLOR,
					LCSMaterialColor.class, COLOR_REFERENCE_KEY_ID)); // COLOR

		} else if (includeOvrMcOnly && overriddenRowOnly && includeOverridenMC) {
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class,
					COLOR_REFERENCE_KEY_ID), new QueryColumn(LCSMATERIALCOLOR,
					LCSMaterialColor.class, COLOR_REFERENCE_KEY_ID)); // COLOR

		} else if (!includeOvrMcOnly && overriddenRowOnly && includeOverridenMC) {
			pqs.appendJoin(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class,
					COLOR_REFERENCE_KEY_ID), new QueryColumn(LCSMATERIALCOLOR,
					LCSMaterialColor.class, COLOR_REFERENCE_KEY_ID)); // COLOR

		} else if (overriddenRowOnly) {
			pqs.appendJoin(new QueryColumn(OVERRIDEN_ROW, FlexBOMLink.class,
					COLOR_REFERENCE_KEY_ID), new QueryColumn(LCSMATERIALCOLOR,
					LCSMaterialColor.class, COLOR_REFERENCE_KEY_ID)); // COLOR
		} else {
			pqs.appendJoin(new QueryColumn(FLEX_BOM_LINK, FlexBOMLink.class,
					COLOR_REFERENCE_KEY_ID), new QueryColumn(LCSMATERIALCOLOR,
					LCSMaterialColor.class, COLOR_REFERENCE_KEY_ID)); // COLOR
		}

		pqs.appendJoin(new QueryColumn("MATERIALMASTER",
				LCSMaterialMaster.class,
				"thePersistInfo.theObjectIdentifier.id(+)"), new QueryColumn(
				LCSMATERIALCOLOR, "idA3A10"));

		pqs.appendJoin(new QueryColumn(
				com.lcs.wc.supplier.LCSSupplierMaster.class,
				"thePersistInfo.theObjectIdentifier.id(+)"), new QueryColumn(
				LCSMATERIALCOLOR, "idA3C10"));

		pqs.appendJoin(new QueryColumn(com.lcs.wc.color.LCSColor.class,
				"thePersistInfo.theObjectIdentifier.id"), new QueryColumn(
				LCSMATERIALCOLOR, "idA3B10"));

		FlexTypeGenerator flexg = new FlexTypeGenerator();
		FlexType matColorflexType = FlexTypeCache
				.getFlexTypeRoot("Material Color");
		flexg.setScope(null);
		flexg.appendQueryColumns(matColorflexType, pqs);

	}

	/**
	 * Method getSelectedCriteria - method to add the select criteria.
	 * 
	 * @param inputSelectedMap
	 *            the inputSelectedMap.
	 * @param reportBean
	 *            the reportBean.
	 * @return SMMaterialForecastReportBean.
	 */
	public static SMCareLabelReportBean getSelectedCriteria(
			Map<String, Object> inputSelectedMap,
			SMCareLabelReportBean reportBean) {
		LOGGER.debug("SMCareLabelReportHelper - getSelectedCriteria method: inputSelectedMap= "
				+ inputSelectedMap);
		// get selected seasons
		String selectedSeasonsIds = FormatHelper.format(EMPTY_STRING
				+ inputSelectedMap.get("seasonIds"));
		if (FormatHelper.hasContent(selectedSeasonsIds)) {
			reportBean.setSelectedSeasonOid(selectedSeasonsIds);
		}
		// get selected products
		String selectedProductid = FormatHelper.format(EMPTY_STRING
				+ inputSelectedMap.get("productid"));
		if (FormatHelper.hasContent(selectedProductid)) {
			Collection<String> selectedProducts = MOAHelper
					.getMOACollection(selectedProductid);
			reportBean.setSelectedProductName(selectedProducts);
		}
		// get selected brands
		String selectedBrandId = FormatHelper.format(EMPTY_STRING
				+ inputSelectedMap.get("brandId"));
		if (FormatHelper.hasContent(selectedBrandId)) {
			Collection<String> selectedBrand = MOAHelper
					.getMOACollection(selectedBrandId);
			reportBean.setSelectedBrands(selectedBrand);
		}
		// get selected project
		String selectedProjectId = FormatHelper.format(EMPTY_STRING
				+ inputSelectedMap.get("projectId"));
		if (FormatHelper.hasContent(selectedProjectId)) {
			Collection<String> selectedProject = MOAHelper
					.getMOACollection(selectedProjectId);
			reportBean.setSelectedProject(selectedProject);
		}
		// get selected gender
		String selectedGenderId = FormatHelper.format(EMPTY_STRING
				+ inputSelectedMap.get("genderId"));
		if (FormatHelper.hasContent(selectedGenderId)) {
			Collection<String> selectedGender = MOAHelper
					.getMOACollection(selectedGenderId);
			reportBean.setSelectedGenders(selectedGender);
		}
		// get selected age
		String selectedAgeId = FormatHelper.format(EMPTY_STRING
				+ inputSelectedMap.get("ageId"));
		if (FormatHelper.hasContent(selectedAgeId)) {
			Collection<String> selectedAge = MOAHelper
					.getMOACollection(selectedAgeId);
			reportBean.setSelectedAges(selectedAge);
		}
		// get selected ProductionGroupId
		String selectedProductionGroupId = FormatHelper.format(EMPTY_STRING
				+ inputSelectedMap.get("productionGroupId"));
		if (FormatHelper.hasContent(selectedProductionGroupId)) {
			Collection<String> selectedProductionGroup = MOAHelper
					.getMOACollection(selectedProductionGroupId);
			reportBean.setSelectedProductionGroupOid(selectedProductionGroup);
		}
		// get selected ProducctTechnologistId
		String selectedProducctTechnologistId = FormatHelper
				.format(EMPTY_STRING
						+ inputSelectedMap.get("producctTechnologistId"));
		if (FormatHelper.hasContent(selectedProducctTechnologistId)) {
			reportBean
					.setSelectedProducctTechnologist(selectedProducctTechnologistId);
		}
		// get Selected Criteria Map
		getSelectedCriteriaMap(inputSelectedMap, reportBean);

		return reportBean;
	}

	/**
	 * @param inputSelectedMap - inputSelectedMap
	 * @param reportBean - reportBean
	 */
	private static void getSelectedCriteriaMap(
			Map<String, Object> inputSelectedMap,
			SMCareLabelReportBean reportBean) {
		// Set the available Product Map in the bean
		Map intProductMap = new HashMap();
		intProductMap.putAll((Map) inputSelectedMap.get("intProductMap"));
		if (!intProductMap.isEmpty()) {
			reportBean.setIntProductMap(intProductMap);
		}
		// Set the available Technologist Map in the bean
		Map intTechnologistMap = new HashMap();
		intTechnologistMap.putAll((Map) inputSelectedMap
				.get("intTechnologistMap"));
		if (!intTechnologistMap.isEmpty()) {
			reportBean.setIntTechnologistMap(intTechnologistMap);
		}
		// Set the available Project Map in the bean
		Map intProjectMap = new HashMap();
		intProjectMap.putAll((Map) inputSelectedMap.get("intProjectMap"));
		if (!intProjectMap.isEmpty()) {
			reportBean.setIntProjectMap(intProjectMap);
		}

	}

}

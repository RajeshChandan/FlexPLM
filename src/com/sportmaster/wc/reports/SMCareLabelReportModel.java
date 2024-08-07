package com.sportmaster.wc.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.client.web.FlexTypeGenerator;
import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.flexbom.BOMPartNameTableColumn;
import com.lcs.wc.flexbom.FlexBOMFlexTypeScopeDefinition;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FootwearApparelFlexTypeScopeDefinition;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.MaterialSupplierFlexTypeScopeDefinition;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.SourcingConfigFlexTypeScopeDefinition;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
/**
 * The Class SMCareLabelReportModel.
 *
 * @version 'true' 1.0 version number.
 * @author 'true' ITC.
 */
public class SMCareLabelReportModel {
	// Initializations
	/** The Constant BOM_MATERIALS_PRODUCT. */
	private static final String BOM_MATERIALS_PRODUCT
	= "BOM\\Materials\\Product";
	/** The Constant MATERIAL_FABRIC. */
	private static final String MATERIAL_FABRIC 
	= "Material\\Fabric";
	/** The Constant PRODUCT_NAME. */
	private static final String PRODUCT_NAME = LCSProperties
			.get("product.PRODUCTNAME");
	/** The Constant SKU_NAME. */
	private static final String SKU_NAME = LCSProperties
			.get("colorway.SKUNAME");
	/** The Constant SEASON_NAME. */
	private static final String SEASON_NAME = LCSProperties
			.get("season.SEASONNAME");
	/** The Constant BOM_PART_NAME. */
	private static final String BOM_PART_NAME = LCSProperties
			.get("bom.BOMNAME");
	/** The Constant PROJECT. */
	private static final String PROJECT = 
			LCSProperties.get("product.PROJECT");
	/** The Constant PRODUCTION_GROUP. */
	private static final String PRODUCTION_GROUP = LCSProperties
			.get("productSeason.PRODUCTIONGROUP");
	/** The Constant SUPPLIER_MDM_ID. */
	private static final String SUPPLIER_MDM_ID = LCSProperties
			.get("materialSupplier.VENDORMDM");
	/** The Constant WASH_CARE. */
	private static final String WASH_CARE = LCSProperties
			.get("material.WASHCARE");
	/** The Constant ALIGN_LEFT. */
	private static final String ALIGN_LEFT = "left";
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getLogger("CARELABELREPORTLOG");
	/** The Constant SMCareLabelReportModel. */
	public SMCareLabelReportModel() {
	}

	/**
	 * @param context - context
	 * @param inputSelectedMap - inputSelectedMap
	 * @param reportBean - reportBean
	 * @return SMCareLabelReportBean - object
	 */
	public SMCareLabelReportBean getReportColumnsData(ClientContext context,
			Map<String, Object> inputSelectedMap,
			SMCareLabelReportBean reportBean) {
		try {
			// Get flextype of Product
			FlexType productFlexType = FlexTypeCache
					.getFlexTypeFromPath("Product");
			Map columnsMap = new HashMap();
			Collection attList = new ArrayList();
			Collection finalColumn = new ArrayList();
			// columnkeys.
			Collection<String> columnsKeys = new ArrayList();
			// column map.
			Map columnMap = new HashMap();
			TableColumn column;
			// column property.
			String columnsPropertyValue = LCSProperties.get("com.sportmaster.reports.careLabel.columns");
			
			// get the columnlist from the property.
			Collection<String> columnLists = MOAHelper
					.getMOACollection(columnsPropertyValue);
			// attribute key list.
			Collection<String> attKeysLists = new ArrayList();
			StringTokenizer st1;
			String displayName;
			String typeName;
			String attKey;
			// columnList iteration to get the attribute key.
			for (String columnKey : columnLists) {
				// split the property entry based on ~
				st1 = new StringTokenizer(columnKey, "~");
				if (st1.countTokens() == 3) {
					while (st1.hasMoreTokens()) {
						displayName = st1.nextToken();
						typeName = st1.nextToken();
						attKey = st1.nextToken();
						if (FormatHelper.hasContent(typeName)
								&& FormatHelper.hasContent(attKey)) {
							columnsKeys.add(typeName + "." + attKey + "~"
									+ displayName);
							attKeysLists.add(attKey);
						}
					}
				}
			}


			// getcolumnmap for report object.
			getColumnsMapForReportObjects(productFlexType, columnMap,
					FormatHelper.format("" + inputSelectedMap.get("seasonIds")));
			String key;
			String columnDisplay;
			// Iterate columnkeys
			for (String colKey : columnsKeys) {
				// Split the column keys from property entry
				columnDisplay = colKey.substring(
						colKey.lastIndexOf('~') + 1);
				column = (TableColumn) columnMap.get(colKey.substring(0,
						colKey.lastIndexOf('~')));
				if (null != column) {
					// Set column properties
					column.setExcelHeaderWrapping(true);
					column.setExcelColumnWidthAutoFitContent(true);
					column.setExcelWrapping(true);
					column.setDisplayed(true);
					column.setHeaderLabel(columnDisplay); // set headerlabel
					column.setAlign(ALIGN_LEFT);
					// get key from column
					key = column.getTableIndex();
					//
					if (null != column.getFlexTypeAttribute()) {
						setFabricTableIndex(column);
					}
					// Add key to attList
					attList.add(key);
					finalColumn.add(column);
					// add column to columnsMap with key as key
					columnsMap.put(key, column);
				}
			}
			// adding attribute list to bean.
			reportBean.setAttList(attList);
			// attribute key list.
			reportBean.setAttKeyList(attKeysLists);
			LOGGER.debug("Report attKeysList..." + finalColumn);
			// final columns.
			reportBean.setFinalColumns(finalColumn);
			LOGGER.debug("Report columns..." + finalColumn);
			// columns.
			reportBean.setColumns(columnsMap);
			// column map.
			reportBean.setColumnsMap(columnMap);
		} catch (WTException e1) {
			LOGGER.error("WTException in SMCareLabelReportModel " 
					+"- getReportColumnsData:"+ e1.getMessage());
			e1.printStackTrace();
		}
		// Return the bean with all the required data for printing report
		return reportBean;
	}

	/**
	 * Method to set table index for fabric attributes
	 * 
	 * @param column - column
	 */
	private void setFabricTableIndex(TableColumn column) {
		// Setting table index for Fabric Finish attributes
		if (LCSProperties.get("materialFabric.FINISHING").equals(
				column.getFlexTypeAttribute().getAttKey())) {
			column.setTableIndex("FABRIC.vrdFinish");
		}
		// Setting table index for Fabric COMPOSITION attributes
		if (LCSProperties.get("materialFabric.COMPOSITION").equals(
				column.getFlexTypeAttribute().getAttKey())) {
			column.setTableIndex("FABRIC.vrdFiberContent");
		}
		// Setting table index for Fabric COMPOSITIONRU attributes
		if (LCSProperties.get("materialFabric.COMPOSITIONRU").equals(
				column.getFlexTypeAttribute().getAttKey())) {
			column.setTableIndex("FABRIC.smCompositionRU");
		}
		// Setting table index for Fabric LAMINATIONCOATING attributes
		if (LCSProperties.get("materialFabric.LAMINATIONCOATING").equals(
				column.getFlexTypeAttribute().getAttKey())) {
			column.setTableIndex("FABRIC.smLaminationCoating");
		}
	}

	/**
	 * Method getColumnMapForReportObjects - getColumnMapForReportObjects.
	 * 
	 * @param productFlexType
	 *            the productFlexType.
	 * @param columnMap
	 *            the columnMap
	 */
	private void getColumnsMapForReportObjects(FlexType productFlexType,
			Map columnMap, String selectedSeasonID) {
		try {
			// Add Season TableColumns
			FlexTypeGenerator flexgen = new FlexTypeGenerator();
			FlexType seasonType = FlexTypeCache.getFlexTypeRoot("Season");
			flexgen.setScope(null);
			flexgen.setLevel(null);
			flexgen.createTableColumns(seasonType, columnMap,
					seasonType.getAllAttributes(null, null, false), false,
					"Season.", null, false, "LCSSEASON");
			// Add Product TableColumns
			flexgen = new FlexTypeGenerator();
			flexgen.setScope(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE); // Product Scope
			flexgen.setLevel(FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL); // Product Level
			flexgen.createTableColumns(productFlexType,	columnMap,productFlexType
					.getAllAttributes(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE,
							FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL,
							false), false, "Product.", null, false,	"LCSPRODUCT");

			// Product Scope
			flexgen.setScope(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE);
			flexgen.setLevel(FootwearApparelFlexTypeScopeDefinition.SKU_LEVEL);
			flexgen.createTableColumns(productFlexType,	columnMap,productFlexType
					.getAllAttributes(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE,
							FootwearApparelFlexTypeScopeDefinition.SKU_LEVEL,
							false), false, "Colorway.", null, false,"LCSSKU");

			// Product Season Scope
			flexgen.setScope(FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE);
			flexgen.setLevel(null);
			flexgen.createTableColumns(productFlexType,columnMap,productFlexType
					.getAllAttributes(FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE,
							FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL,
							false), false, "ProductSeason.", null,true, "LCSPRODUCTSEASONLINK");

			flexgen.createTableColumns(productFlexType,columnMap,productFlexType
					.getAllAttributes(FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE,
							FootwearApparelFlexTypeScopeDefinition.PRODUCT_SKU_LEVEL,
							false), false, "ColorwaySeason.", null, true, "LCSSKUSEASONLINK");
			// Add Sourcing Config TableColumns
			FlexType scType = FlexTypeCache
					.getFlexTypeFromPath("Sourcing Configuration");
			flexgen.setScope(SourcingConfigFlexTypeScopeDefinition.SOURCING_CONFIG_SCOPE);
			flexgen.setLevel(SourcingConfigFlexTypeScopeDefinition.PRODUCT_LEVEL);
			flexgen.createTableColumns(productFlexType,columnMap,
					scType.getAllAttributes(SourcingConfigFlexTypeScopeDefinition.SOURCING_CONFIG_SCOPE,
							SourcingConfigFlexTypeScopeDefinition.PRODUCT_LEVEL,
							false), false, "Sourcing Configuration.", null, true, "LCSSOURCINGCONFIG");

			// Set Source to Season for Order destination, Factory
			flexgen.setScope(SourcingConfigFlexTypeScopeDefinition.SOURCE_TO_SEASON_SCOPE);
			flexgen.setLevel(SourcingConfigFlexTypeScopeDefinition.PRODUCT_LEVEL);
			flexgen.createTableColumns(scType, columnMap, scType.getAllAttributes(
					SourcingConfigFlexTypeScopeDefinition.SOURCE_TO_SEASON_SCOPE,
					SourcingConfigFlexTypeScopeDefinition.PRODUCT_LEVEL,
					false), false, "Sourcing Configuration.", null, true, "LCSSourceToSeasonLink");

			// Add Material TableColumns
			FlexType materialType = FlexTypeCache.getFlexTypeRoot("Material");
			FlexType colorType = FlexTypeCache.getFlexTypeRoot("Color");
			FlexType supplierType = FlexTypeCache.getFlexTypeRoot("Supplier");
			FlexType flexType = FlexTypeCache.getFlexTypeRoot("Material Color");
			FlexType bomType = FlexTypeCache
					.getFlexTypeFromPath(BOM_MATERIALS_PRODUCT);

			// Material Color
			flexgen.setScope(null);
			flexgen.setLevel(null);
			flexgen.createTableColumns(flexType, columnMap,
					flexType.getAllAttributes(null, null, false), false, false,
					"Material Color.", null, true, "LCSMATERIALCOLOR");

			// Material Type.
			flexgen = new FlexTypeGenerator();
			flexgen.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE);
			flexgen.createTableColumns(materialType, columnMap,materialType
					.getAllAttributes(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE,
							null, false), false, false, "Material.", null, true, "LCSMATERIAL");

			// Materail Supplier scope
			flexgen = new FlexTypeGenerator();
			flexgen.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE);
			flexgen.createTableColumns(materialType, columnMap,materialType
					.getAllAttributes(MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE,
							null, false), false, false,
					"Material Supplier.", null, true, "LCSMATERIALSUPPLIER");

			// Material Fabric Scope
			FlexType fabricType = FlexTypeCache
					.getFlexTypeFromPath(MATERIAL_FABRIC);
			flexgen.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE);
			flexgen.createTableColumns(fabricType, columnMap, fabricType
					.getAllAttributes(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE,
							null, false), false, false, "Material\\Fabric.", null, true, "FABRIC");

			// Color Type.
			flexgen = new FlexTypeGenerator();
			flexgen.createTableColumns(colorType, columnMap,
					colorType.getAllAttributes(null, null, false), false,
					false, "Color.", null, true, null);

			// Supplier Type.
			flexgen = new FlexTypeGenerator();
			flexgen.createTableColumns(supplierType, columnMap,
					supplierType.getAllAttributes(null, null, false), false,
					false, "Supplier.", null, true, null);

			// Add BOM TableColumns
			// Add BOM scope TableColumns
			flexgen = new FlexTypeGenerator();
			flexgen.setScope(com.lcs.wc.flexbom.FlexBOMFlexTypeScopeDefinition.BOM_SCOPE);
			flexgen.createTableColumns(bomType, columnMap, bomType
					.getAllAttributes(FlexBOMFlexTypeScopeDefinition.BOM_SCOPE,
							null), false, "BOM.", null, true, "FlexBOMPart");
			// Add BOM Link scope TableColumns
			flexgen = new FlexTypeGenerator();
			flexgen.setScope(com.lcs.wc.flexbom.FlexBOMFlexTypeScopeDefinition.LINK_SCOPE);
			flexgen.createTableColumns(bomType, columnMap, bomType
					.getAllAttributes(FlexBOMFlexTypeScopeDefinition.LINK_SCOPE, null),
					false, "BOM_LINK.", null, true, "FLEXBOMLINK");

			// Defining column for PartName(Placement)
			BOMPartNameTableColumn columnt = new BOMPartNameTableColumn();
			columnt.setHeaderLabel("Placement"); // set headerlabel
			columnt.setTableIndex(bomType.getAttribute(
					LCSProperties.get("bomLink.PLACEMENT"))
					.getSearchResultIndex());
			columnt.setDisplayed(true);
			columnt.setWrapping(false);
			// Adding column to main collection.
			columnMap.put("BOM_LINK.partName", columnt);
			// Method to add additional columns
			getHardColumnsForReportData(columnMap, flexgen, selectedSeasonID);
		} catch (WTException e) {
			LOGGER.error("WTException in SMCareLabelReportModel " 
					+"- getColumnMapForReportObjects:"
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param columnMap
	 *  			the columnMap
	 * @param flexgen
	 *  			the flexg
	 */
	private void getHardColumnsForReportData(Map columnMap,
			FlexTypeGenerator flexgen, String selectedSeasonID) {
		try {
			// Get FlexType of Product
			FlexType productType = FlexTypeCache.getFlexTypeRoot("Product");
			TableColumn column;
			// column for Material Supplier.
			column = new TableColumn();
			column.setDisplayed(true);
			column.setHeaderLabel("Material Supplier"); // set headerlabel
			column.setHeaderAlign(ALIGN_LEFT);
			column.setTableIndex("LCSSUPPLIERMASTER.SUPPLIERNAME");
			columnMap.put("Material Supplier." +
					"materialSupplierName", column);

			// column for Color.
			column = new TableColumn();
			column.setDisplayed(true);
			column.setTableIndex("LCSCOLOR.COLORNAME");
			column.setHeaderLabel("Color"); // set headerlabel
			column.setHeaderAlign(ALIGN_LEFT);
			columnMap.put("Color.name", column);

			// column for materialName.
			column = new TableColumn();
			column.setDisplayed(true);
			column.setHeaderLabel("Material"); // set headerlabel
			column.setTableIndex("MATERIALMASTER.NAME");
			column.setHeaderAlign(ALIGN_LEFT);
			columnMap.put("Material.materialName", column);

			// product name attribute.
			FlexTypeAttribute productNameAtt = productType
					.getAttribute(PRODUCT_NAME);
			// column for productName.
			column = flexgen.createTableColumn(productNameAtt, productType,
					false);
			column.setDisplayed(true);
			column.setTableIndex(productNameAtt.getSearchResultIndex());
			columnMap.put("Product.productName", column);

			// column for skuName.
			column = flexgen.createTableColumn(
					productType.getAttribute(SKU_NAME), productType, false);
			column.setDisplayed(true);
			column.setTableIndex(productType.getAttribute(SKU_NAME)
					.getSearchResultIndex());
			columnMap.put("Colorway.skuName", column);

			// column for seasonName.
			column = new TableColumn();
			column.setDisplayed(true);
			column.setHeaderLabel("Season"); // set headerlabel
			column.setLinkTableIndex("LCSSEASON.BRANCHIDITERATIONINFO");
			column.setTableIndex(FlexTypeCache.getFlexTypeRoot("Season")
					.getAttribute(SEASON_NAME).getSearchResultIndex());
			columnMap.put("ProductSeason.seasonName", column);

			// column for bomName.
			column = new TableColumn();
			column.setDisplayed(true);
			column.setHeaderLabel("BOM Name"); // set headerlabel
			column.setTableIndex(FlexTypeCache.getFlexTypeRoot("BOM")
					.getAttribute(BOM_PART_NAME).getSearchResultIndex());
			columnMap.put("BOM.ptcbomPartName", column);

			// column for Destination Variation.
			column = new TableColumn();
			column.setDisplayed(true);
			column.setHeaderLabel("Destination Variation"); // set headerlabel
			column.setTableIndex("PRODUCTDESTINATION.DESTINATIONNAME");
			columnMap.put("BOM_LINK.dimensionName", column);


			// column for bom link branchid 3.8.1.0 build - Start
			column = new TableColumn();
			column.setDisplayed(true);
			column.setHeaderLabel("BOM Link BranchID");
			column.setTableIndex("FLEXBOMLINK.BRANCHID");
			columnMap.put("FLEXBOMLINK.BRANCHID", column);
			// column for bom link branchid 3.8.1.0 build - End
			
			// column for bom part iteration info 3.8.1.0 build - Start
			column = new TableColumn();
			column.setDisplayed(true);
			column.setHeaderLabel("BOM Part BranchID");
			column.setTableIndex("FLEXBOMPART.BRANCHIDITERATIONINFO");
			columnMap.put("FLEXBOMPART.BRANCHIDITERATIONINFO", column);
			// column for bom part iteration info 3.8.1.0 build - End
			
			// Method to get additional columns for columnMap
			getHardColumnsForAttributes(columnMap, flexgen, selectedSeasonID);
		} catch (WTException e) {
			LOGGER.error("WTException in SMCareLabelReportModel - getHardColumnsForReport:"
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param columnMap - columnMap
	 * @param flexgen - flexgen
	 * @throws WTException - WTException
	 */
	private void getHardColumnsForAttributes(Map columnMap,
			FlexTypeGenerator flexgen, String selectedSeasonID) throws WTException {
		
		//Added for production group phase8 - Start
		LCSSeason season = (LCSSeason) LCSQuery
				.findObjectById("VR:com.lcs.wc.season.LCSSeason:"
						+ selectedSeasonID);
		
		FlexType productFlexType = season.getProductType();
		//Added for production group phase8 - End
		
		// Get flextype for Product\\APD
		FlexType productAPDFlexType = FlexTypeCache
				.getFlexTypeFromPath("Product\\APD");
		// Get flextype for Supplier
		FlexType supplierFlexType = FlexTypeCache
				.getFlexTypeFromPath("Supplier");
		// Get flextype for Material
		FlexType materialFlexType = FlexTypeCache
				.getFlexTypeFromPath("Material");
		// column for Project.
		TableColumn column = new TableColumn();
		column = flexgen.createTableColumn(
				productAPDFlexType.getAttribute(PROJECT), productAPDFlexType,
				false);
		column.setDisplayed(true);
		column.setTableIndex(productAPDFlexType.getAttribute(PROJECT)
				.getSearchResultIndex());
		columnMap.put("Product.smProject", column);

		// column for Production Group
		column = new TableColumn();
		//Added to avail for all prodcut types phase8 - Start
		column = flexgen.createTableColumn(
				productFlexType.getAttribute(PRODUCTION_GROUP),
				productFlexType, false);
		column.setDisplayed(true);
		column.setTableIndex(productFlexType.getAttribute(PRODUCTION_GROUP)
				.getSearchResultIndex());
		//Added to avail for all prodcut types phase8 - End
		columnMap.put("ProductSeason.smProductionGroup", column);
		// call this method to get TableColumn for supplier mdm id
		getHardColumnsForAttributes("Business Supplier MDM ID",
				supplierFlexType.getAttribute(SUPPLIER_MDM_ID)
				.getSearchResultIndex() + "_1",
				"Supplier.smMDMVENDOR_1", columnMap);
		getHardColumnsForAttributes("Factory MDM ID", supplierFlexType
				.getAttribute(SUPPLIER_MDM_ID)
				.getSearchResultIndex() + "_2",
				"Supplier.smMDMVENDOR_2", columnMap);
		getHardColumnsForAttributes("Material Supplier MDM ID",
				supplierFlexType.getAttribute(SUPPLIER_MDM_ID)
				.getSearchResultIndex() + "_3",
				"Supplier.smMDMVENDOR_3", columnMap);
		// call this method to get TableColumn for wash care
		getHardColumnsForAttributes("Wash",
				materialFlexType.getAttribute(WASH_CARE)
				.getSearchResultIndex() +"_1", 
				"Material.smWashCare_1", columnMap);
		getHardColumnsForAttributes("Bleach",
				materialFlexType.getAttribute(WASH_CARE)
				.getSearchResultIndex()	+ "_2",
				"Material.smWashCare_2", columnMap);
		getHardColumnsForAttributes("Iron",
				materialFlexType.getAttribute(WASH_CARE)
				.getSearchResultIndex()+ "_3", 
				"Material.smWashCare_3", columnMap);
		getHardColumnsForAttributes("Dry Clean",
				materialFlexType.getAttribute(WASH_CARE)
				.getSearchResultIndex()+ "_4", 
				"Material.smWashCare_4", columnMap);
		getHardColumnsForAttributes("Dry",
				materialFlexType.getAttribute(WASH_CARE)
				.getSearchResultIndex()+ "_5",
				"Material.smWashCare_5", columnMap);
		// call this method to get TableColumn for composition attributes
		getHardColumnsForAttributes("Layer 1/Composition",
				"FABRICOTHER.smLayer1_1", 
				"Material\\Fabric\\Other.smLayer1_1",
				columnMap);
		getHardColumnsForAttributes("Layer 1/Composition-RU",
				"FABRICOTHER.smLayer1_2", 
				"Material\\Fabric\\Other.smLayer1_2",
				columnMap);
		getHardColumnsForAttributes("Layer 2/Composition",
				"FABRICOTHER.smLayer2_1", 
				"Material\\Fabric\\Other.smLayer2_1",
				columnMap);
		getHardColumnsForAttributes("Layer 2/Composition-RU",
				"FABRICOTHER.smLayer2_2", 
				"Material\\Fabric\\Other.smLayer2_2",
				columnMap);

	}

	/**
	 * @param headerLabel - headerLabel
	 * @param tableIndex - tableIndex
	 * @param mapKey - mapKey
	 * @param columnMap - columnMap
	 */
	private void getHardColumnsForAttributes(String headerLabel,
			String tableIndex, String mapKey, Map columnMap) {
		// Creates TableColumn for the attributes
		TableColumn column = new TableColumn();
		// set header label
		column.setHeaderLabel(headerLabel); 
		column.setDisplayed(true);
		// set tableindex
		column.setTableIndex(tableIndex); 
		column.setHeaderAlign(ALIGN_LEFT);
		// Add the column to columnMap
		columnMap.put(mapKey, column);
	}
}
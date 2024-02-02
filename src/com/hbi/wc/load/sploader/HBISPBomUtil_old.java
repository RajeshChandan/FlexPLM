package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.Query;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartClientModel;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecHelper;
import com.lcs.wc.specification.FlexSpecLogic;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.specification.FlexSpecificationClientModel;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class HBISPBomUtil_old {

	private static final String SPECIFICATION_SELLING = "Specification\\Selling";
	private static final String DUMMY_ATTR_CODE = "------";
	// public static final String SP_UNITS_OF_MEASURE =
	// "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_UNITS_OF_MEASURE.xlsx";
	// public static final String SP_UNITS_OF_MEASURE_FILENAME =
	// "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_UNITS_OF_MEASURE";
	public static final String SP_PACKCASE_BOM_FILENAME = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_PACKCASE_BOM_IN";
	public static String materialFlexTypePathCC = LCSProperties.get("com.hbi.wc.cartonID", "Material\\Casing");
	
	// public static final int SP_UOM_FILES_SPLIT_COUNT = 5;
	public static final int SP_PKGCase_FILES_SPLIT_COUNT = 10;
	public static final int SP_UOM_EA_FILES_SPLIT_COUNT = 3;
	public static final String SP_UNITS_OF_MEASURE_EA_FILENAME = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_UNITS_OF_MEASURE_EA";
	public static final String SPBOMLOADER_IN = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SPBOMLOADER_IN.xlsx";
	public static final String PATH_SPBOMLOADER_IN = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\";
	public static final String SPBOMLOADER_IN_TYPE = ".xlsx";

	public static final String SP_GENERAL_ATTRIBUTES = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_GENERAL_ATTRIBUTES.xlsx";
	public static final String SP_PUTUP_CODES = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_PUTUP_CODES.xlsx";
	public static final String SP_SIZES = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_SIZES.xlsx";
	//custom.lcs.properties
	public static final String SELLING_PRODUCT = LCSProperties.get("com.hbi.wc.load.sploader.product.type","Product\\BASIC CUT & SEW - SELLING");
	public static boolean DEBUG = true;
	private static WTProperties wtproperties;
	private static final String temp = File.separator + "temp";
	public static final String PACKCASE_BOMTYPE = "BOM\\Materials\\HBI\\Selling\\Pack Case BOM";
	public static final String SALES_BOMTYPE = "BOM\\Materials\\HBI\\Sales BOM";
	public static final String SP_SALES_BOM_EXPORT = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_SALES_BOMS.xlsx";
	public static final String SP_SALES_ALT_BOM_EXPORT = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_ALTERNATE_BOMS.xlsx";
	public static final String MATERIAL_CASING_TYPE = "Material\\Casing";
	public static final String SP_BOM_REPORT = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\SP_BOM_REPORT.xlsx";
	public static final String SALES_BOM = "SALESBOM";
	public static final String ALT_BOM = "altBom";
	public static final String PKG_BOM = "pkgBom";
	public static final String ZFRT_BOM = "zfrtBom";
	public static final String PALLET_BOM = "PALLETBOM";

	private static final String PLACEHOLDER_VALUE = "1";
	private static final String IGNORED_SEASONS = LCSProperties.get("com.hbi.wc.load.sploader.ignoredseasons",
			"Validation Season");

	/**
	 * @param sap_key
	 * @param sp_GENERAL_ATTRIBUTES
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Collection<LCSProduct> getProductsFromSapKey(String sap_key, String sp_GENERAL_ATTRIBUTES)
			throws WTException, IOException {

		Collection<LCSProduct> products = new HashSet();
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(sp_GENERAL_ATTRIBUTES));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String cell0 = getCellValue(row, 0);
			System.out.println("cell0::::::::::::"+cell0);
						System.out.println("sap_key::::::::::::"+sap_key);


			if (sap_key.equals(cell0)) {

				// hbiErpAttributionCode
				String cell1 = HBISPBomUtil_old.getCellValue(row, 1);
							System.out.println("cell1::::::::::::"+cell1);

				String attribute = getAttributionIda2a2(cell1);

				// hbiSellingStyleNumber
				String bismt = HBISPBomUtil_old.getCellValue(row, 2);

				// hbiDescription
				String maktx = HBISPBomUtil_old.getCellValue(row, 3);

				// hbiAPSPackQuantity
				String cell4 = HBISPBomUtil_old.getCellValue(row, 4);
				String umren_PackQuantity = "hbi" + cell4.substring(0, cell4.length() - 2);

				// Get the product from FlexPLM with the SAP general attributes
				LCSProduct product = getProductByStyleNo(attribute, bismt, maktx, umren_PackQuantity);
				products.add(product);

			}

		}
		file.close();
		return products;
	}

	/**
	 * @param row
	 * @param i
	 * @return
	 */
	public static String getCellValue(Row row, int i) {
		Cell cell = row.getCell(i);
		String cellvalue = "";
		if (cell != null && FormatHelper.hasContent(cell.toString())) {
			cellvalue = cell.toString().trim();
		}
		return cellvalue;
	}

	/**
	 * @param attributionCode
	 * @return
	 * @throws WTException
	 */
	public static String getAttributionIda2a2(String attributionCode) throws WTException {

		String boIda2a2 = "";
		String businessObjectTypePath = "Business Object\\Automation Support Tables\\Attribution Codes and Descriptions";
		FlexType boFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjectTypePath);
		String hbiErpAttributionCode_DBColumn = boFlexTypeObj.getAttribute("hbiErpAttributionCode").getVariableName();
		String typeIdPath = String.valueOf(boFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());

		// Initializing the PreparedQueryStatement,
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendSelectColumn(
				new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendAndIfNeeded();
		statement
				.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, hbiErpAttributionCode_DBColumn),
						attributionCode, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeReference.key.id"), "?", "="),
				new Long(typeIdPath));

		// Get SearchResults instance from the given PreparedQueryStatement
		debug("Stmt ::" + statement.toString());
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if (results != null && results.getResultsFound() == 1) {
			FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
			boIda2a2 = flexObj.getString("LCSLifecycleManaged.IDA2A2");
			debug("BO Ida2a2 :: " + boIda2a2);
		} else {
			throw new WTException("Attribution code not found in FlexPLM :: [" + attributionCode + "]");
		}
		return boIda2a2;

	}

	/**
	 * @param hbiErpAttributionCode
	 * @param hbiSellingStyleNumber
	 * @param hbiDescription
	 * @param hbiAPSPackQuantity
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static LCSProduct getProductByStyleNo(String hbiErpAttributionCode, String hbiSellingStyleNumber,
			String hbiDescription, String hbiAPSPackQuantity) throws WTException {
		FlexType prdType = FlexTypeCache.getFlexTypeFromPath(SELLING_PRODUCT);
		String hbiSellingStyleNumber_DB_Col = prdType.getAttribute("hbiSellingStyleNumber").getVariableName();
		String hbiAPSPackQuantity_DB_Col = prdType.getAttribute("hbiAPSPackQuantity").getVariableName();
		String hbiDescription_DB_Col = prdType.getAttribute("hbiDescription").getVariableName();
		String hbiErpAttributionCode_DB_Col = prdType.getAttribute("hbiErpAttributionCode").getVariableName();

		LCSProduct product = null;
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		stmt.appendFromTable("prodarev", "product");
		stmt.appendSelectColumn("product", "ida2a2");
		stmt.appendOpenParen();
		stmt.appendCriteria(
				new Criteria("product", hbiSellingStyleNumber_DB_Col, hbiSellingStyleNumber, Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", hbiAPSPackQuantity_DB_Col, hbiAPSPackQuantity, Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", hbiDescription_DB_Col, hbiDescription, Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(
				new Criteria("product", hbiErpAttributionCode_DB_Col, hbiErpAttributionCode, Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", "flexTypeIdPath", prdType.getTypeIdPath(), Criteria.EQUALS));
		stmt.appendClosedParen();

		debug("stmt::{" + stmt.toString() + "}");
		Collection<FlexObject> output = new ArrayList();
		output = LCSQuery.runDirectQuery(stmt).getResults();
		debug("size::[ " + output.size() + " ]");
		if (output.size() == 1) {
			FlexObject obj = (FlexObject) output.iterator().next();
			product = (LCSProduct) LCSQuery
					.findObjectById("OR:com.lcs.wc.product.LCSProduct:" + obj.getData("PRODUCT.IDA2A2"));
			debug("******SELLING Product [ " + product.getName() + " ] found with hbiSellingStyleNumber ["
					+ hbiSellingStyleNumber + "]");
			return product;
		} else {
			throw new WTException("!!!! No SELLING Product found in FlexPLM with sp_StyleNo [" + hbiSellingStyleNumber
					+ "], " + "Attribution code [" + hbiErpAttributionCode + "]," + "[hbiDescription ::"
					+ hbiDescription + "]," + "[hbiAPSPackQuantity :: " + hbiAPSPackQuantity + "]");
		}

	}

	/**
	 * @param sap_key
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	public static Collection<LCSProduct> getProductsFromSapKey(String sap_key) throws WTException, IOException {
		Collection<LCSProduct> prods = getProductsFromSapKey(sap_key, SP_GENERAL_ATTRIBUTES);
		if (!prods.isEmpty()) {
			HBISPBomUtil_old.debug(
					" Below products fetched in flexPLM for [SAP_KEY :: " + sap_key + " ]" + printProductName(prods));
		} else {
			HBISPBomUtil_old.debug("!!! No products fetched in flexPLM for [SAP_KEY :: " + sap_key + " ]");
		}
		return prods;
	}

	/**
	 * @param prods
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String printProductName(Collection<LCSProduct> prods) {
		String prodNames = "\n ";
		Iterator itr = prods.iterator();
		while (itr.hasNext()) {
			LCSProduct prod = (LCSProduct) itr.next();
			prodNames = prod.getName() + "\n";
		}
		return prodNames;
	}

	/**
	 * @param sp
	 * @param sap_Grid_Size
	 * @return
	 * @throws WTException
	 */
	public static String getPlmSize1Value(LCSProduct sp, String sap_Grid_Size) throws WTException {

		String plm_Size_Literal = "";
		LCSLifecycleManaged sizeCategoryBO = (LCSLifecycleManaged) sp.getValue("hbiSellingSizeCategory");
		String sizeCatIda2a2 = FormatHelper.getNumericObjectIdFromObject(sizeCategoryBO);
		plm_Size_Literal = getPLMSizeLiteral(sizeCatIda2a2, sap_Grid_Size);

		return plm_Size_Literal;
	}

	/**
	 * @param sizeCatida2a2
	 * @param sap_Grid_Size
	 * @return
	 * @throws WTException
	 */
	public static String getPLMSizeLiteral(String sizeCatida2a2, String sap_Grid_Size) throws WTException {
		LCSLifecycleManaged businessObject = null;
		String plmSizeLiteral = "";
		String businessObjectTypePath = "Business Object\\Automation Support Tables\\Size Xref";
		FlexType boFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjectTypePath);
		String hbiAPSSizeCategory_DBColumn = boFlexTypeObj.getAttribute("hbiAPSSizeCategory").getVariableName();
		String hbiSAPGridSize_DBColumn = boFlexTypeObj.getAttribute("hbiSAPGridSize").getVariableName();
		String typeIdPath = String.valueOf(boFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());

		// Initializing the PreparedQueryStatement,
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendSelectColumn(
				new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, hbiAPSSizeCategory_DBColumn),
				sizeCatida2a2, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, hbiSAPGridSize_DBColumn),
				sap_Grid_Size, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeReference.key.id"), "?", "="),
				new Long(typeIdPath));

		// Get SearchResults instance from the given PreparedQueryStatement
		debug("Stmt ::" + statement.toString());
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if (results != null && results.getResultsFound() == 1) {
			FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
			businessObject = (LCSLifecycleManaged) LCSQuery.findObjectById(
					"OR:com.lcs.wc.foundation.LCSLifecycleManaged:" + flexObj.getString("LCSLifecycleManaged.IDA2A2"));
			plmSizeLiteral = (String) businessObject.getValue("hbiPLMSizeLiteral");
		} else {
			throw new WTException(
					"PLMSizeLiteral not found in FlexPlM for SAP Size Grid Value :: [" + sap_Grid_Size + "]");
		}
		return plmSizeLiteral;

	}

	/**
	 * @param sap_key
	 * @param sp_PUTUP_CODES
	 * @return
	 * @throws IOException
	 *             Map<Ref_Putup,Collection<putupcodes>>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Collection<String>> getValidPutUps(String sap_key, String sp_PUTUP_CODES)
			throws IOException {

		Map<String, Collection<String>> validputups = new HashMap();
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(sp_PUTUP_CODES));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			// sap_key
			String cell0 = getCellValue(row, 0);
			// PRIMARY_PUTUP
			// String primary_putup = row.getCell(2).toString();

			// if (cell0.equals(sap_key) && primary_putup.equals("Y")) {
			if (cell0.equals(sap_key)) {
				String ref_putup = getCellValue(row, 3);
				String putup = getCellValue(row, 1);
				Collection<String> putupCodes = validputups.get(ref_putup);
				if (putupCodes != null) {
					putupCodes.add(putup);

				} else {
					putupCodes = new ArrayList();
					putupCodes.add(putup);
				}
				validputups.put(ref_putup, putupCodes);
			}

		}
		return validputups;
	}

	/**
	 * @param sap_key
	 * @param sp_PUTUP_CODES
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Collection<String>> getValidPutUps(String sap_key) throws IOException {
		return getValidPutUps(sap_key, SP_PUTUP_CODES);
	}

	/**
	 * @param sp
	 * @param putup
	 * @return
	 * @throws WTException
	 */
	public static String getSPBomName(LCSProduct sp, String putup, String bomKey) throws WTException {
		String bomName = "";
		String attrCode = getAttributionCode(sp);
		if (DUMMY_ATTR_CODE.equals(attrCode)) {
		
				bomName = sp.getValue("hbiSellingStyleNumber") + "_" + putup + "_" + bomKey;
	
			
		} else {
			bomName = sp.getValue("hbiSellingStyleNumber") + "_" + attrCode + "_" + putup + "_" + bomKey;
		}
		System.out.println("bomName---------"+bomName);
		return bomName;
	}

	/**
	 * @param sp
	 * @return
	 * @throws WTException
	 */
	private static String getAttributionCode(LCSProduct sp) throws WTException {

		String attrCode = "";
		LCSLifecycleManaged bo = (LCSLifecycleManaged) sp.getValue("hbiErpAttributionCode");
		attrCode = (String) bo.getValue("hbiErpAttributionCode");
		return attrCode;

	}

	/**
	 * @param prod
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Collection<Row> getProductBomData(String prod, String file_extract) throws IOException {
		Collection<Row> rows = new ArrayList();
		FileInputStream file = new FileInputStream(new File(file_extract));

		// Create Workbook instance holding reference to .xlsx file
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getCell(0).toString().equals(prod)) {
				rows.add(row);
			}
		}
		file.close();
		return rows;
	}

	/**
	 * @param sp
	 * @param salesBomtype
	 * @return
	 * @throws WTException
	 */
	public static FlexBOMPart initiateBOM(LCSProduct sp, String salesBomtype, String bomName) throws WTException {
		FlexType bomType = FlexTypeCache.getFlexTypeFromPath(salesBomtype);
		FlexBOMPart newBom = null;
		FlexBOMPart oldBom = getExistingBom(sp, bomName);

		if (oldBom == null) {
			newBom = (new LCSFlexBOMLogic()).initiateBOMPart(sp, bomType, "MAIN");
			System.out.println("************* initiated BOM");

			String bomId = FormatHelper.getObjectId(newBom);

			FlexBOMPartClientModel bomPartClientModel = new FlexBOMPartClientModel();
			// Load Bom
			try {
				
				//LCSMaterial material = getMaterial(cartonId);
				
				bomPartClientModel.load(bomId);
				bomPartClientModel.setValue("name", bomName);
				
				//bomPartClientModel.setValue("hbiErpCartonID", material); //78861374
				//-- Commented out this line to Avoid wrong numbering
				bomPartClientModel.setValue("number", "");
				bomPartClientModel.save();
				bomPartClientModel.checkIn();
				newBom = bomPartClientModel.getBusinessObject();

			} catch (WTPropertyVetoException e) {
				String error = "WTPropertyVetoException Caught in initiateMergeBom method";
				System.out.println(error + e.getLocalizedMessage());
				throw new WTException(e);
			}
		} else {
			throw new WTException("BOM :: [" + bomName + "], already existing on Product [" + sp.getName() + "]");
		}
		return newBom;

	}



	/**
	 * @param linkedGP
	 * @param bomName
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static FlexBOMPart getExistingBom(LCSProduct sp, String bomName) throws WTException {
		FlexBOMPart bom = null;
		FlexSpecification flexSpec = null;
		FlexBOMPart existing=null;

		Collection<FlexBOMPart> bomParts = new ArrayList();
		if (sp != null && FormatHelper.hasContent(bomName)) {
			try {
				bomParts = (new LCSFlexBOMQuery()).findBOMPartsForOwner(sp, "A", "MAIN", (FlexSpecification) flexSpec);
                Iterator itr=bomParts.iterator();
				System.out.println("bomParts available:::::::::::::::"+bomParts);

                while (itr.hasNext()) {
					bom = (FlexBOMPart) itr.next();
					String name =bom.getName();
					System.out.println("Retrived BOM::::::::::::::::"+name);

					name = name.replaceAll("\\s","");
					System.out.println("Retrived BOM::::::::::::::::"+name);
					if(name.contains(bomName)) {
						System.out.println("-------------NEW BOM && OLD BOM MATCHED-----------"+name);

						if (VersionHelper.isCheckedOut(bom)) {
							System.out.println("Found check out bom on SP, so checking in [bomName :: " + bomName
									+ "], [ SP :: " + sp.getName() + " ]");
							bom = (FlexBOMPart) VersionHelper.checkin(bom);
						}
						
						existing=bom;
					}
					else {
						System.out.println("-------------NEW BOM && OLD BOM Not MATCHED-----------"+name);

						
					}
					

					
                }
				/*if (bomParts.size() == 1) {
					bom = (FlexBOMPart) bomParts.iterator().next();
					if (VersionHelper.isCheckedOut(bom)) {
						System.out.println("Found check out bom on SP, so checking in [bomName :: " + bomName
								+ "], [ SP :: " + sp.getName() + " ]");
						bom = (FlexBOMPart) VersionHelper.checkin(bom);
					}

				} else if (bomParts.size() > 1) {
					String error = "Error,Found Product with two Boms same name [ SP :: " + sp + " ], [ bomName :: "
							+ bomName + " ]";
					System.out.println("!!!!  " + error);
					throw new WTException(error);
				}*/

			} catch (WTException e) {
				String error = "Exception occured while searching Boms with [ bomName :: " + bomName + " ][ SP :: "
						+ sp.getName() + " ]";
				System.out.println("!!!!  " + error);
				throw new WTException(error);
			}

		}
		return existing;
	}

	/**
	 * @param sap_key_export
	 * @return
	 * @throws IOException
	 */
	public static Collection<String> getSapKeys(String sap_key_export) throws IOException {

		Collection<String> sap_keys = new HashSet<String>();
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(sap_key_export));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String sap_key = row.getCell(0).toString();
			// String sap_keys = row.getCell(0).toString();
			sap_keys.add(sap_key);
		}
		file.close();
		return sap_keys;
	}

	/**
	 * @param sp_GENERAL_ATTRIBUTES
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Collection<LCSProduct> getProductsFromInFile(String bom_loader_file_in)
			throws WTException, IOException {
		Collection<LCSProduct> products = new ArrayList();
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(bom_loader_file_in));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			// hbiErpAttributionCode
			String cell1 = getCellValue(row, 1);
			System.out.println("cell1::::::::::::"+cell1);
			String attribute = getAttributionIda2a2(cell1);

			// hbiSellingStyleNumber
			String bismt = getCellValue(row, 2);

			// hbiDescription
			String maktx = getCellValue(row, 4);

			// hbiAPSPackQuantity
			String cell7 = getCellValue(row, 7);
			String umren_PackQuantity = "hbi" + cell7.substring(0, cell7.length() - 2);

			// Get the product from FlexPLM with the SAP general attributes
			LCSProduct product = getProductByStyleNo(attribute, bismt, maktx, umren_PackQuantity);
			products.add(product);

		}

		return products;

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @return
	 * @throws WTException
	 */
	public static String getSpecName(String sap_key, LCSProduct sp, String putup) throws WTException {
		String specName = "";
		String attrCode = getAttributionCode(sp);
		if (DUMMY_ATTR_CODE.equals(attrCode)) {
			specName = sp.getValue("hbiSellingStyleNumber") + "_" + putup;
		} else {
			specName = sp.getValue("hbiSellingStyleNumber") + "_" + attrCode + "_" + putup;
		}
		return specName;
	}

	@SuppressWarnings("rawtypes")
	public static LCSSeason getSeasonObjectByNameWithProductRootType(String seasonName) throws WTException {
		LCSSeason seasonObj = null;
		if (FormatHelper.hasContent(seasonName)) {
			// Get the season object from season name and season type using ootb
			// api
			FlexType productFlexType = FlexTypeCache.getFlexTypeFromPath("Product");
			seasonName = seasonName.trim();

			// Query to get seasons from season name
			PreparedQueryStatement pqs = new PreparedQueryStatement();
			// Select Columns
			pqs.appendSelectColumn("LCSSEASON", "IDA2A2");
			// From Table
			pqs.appendFromTable("LCSSEASON");
			// Where
			debug("productFlexType.getIdNumber() " + productFlexType.getIdNumber());
			pqs.appendCriteria(new Criteria("LCSSEASON", "att1", seasonName, Criteria.EQUALS));
			pqs.appendAndIfNeeded();
			pqs.appendCriteria(new Criteria("LCSSEASON", "latestIterationInfo", "1", Criteria.EQUALS));
			pqs.appendAndIfNeeded();
			pqs.appendCriteria(new Criteria("LCSSEASON", "IDA3A12", productFlexType.getIdNumber(), Criteria.EQUALS));
			debug("pqs " + pqs);
			Collection seasonCollection = LCSQuery.runDirectQuery(pqs).getResults();
			Iterator seasonItr = seasonCollection.iterator();
			while (seasonItr.hasNext()) {
				FlexObject seasonIdFo = (FlexObject) seasonItr.next();
				debug("seasonIdFo " + seasonIdFo);
				String seasonId = seasonIdFo.getData("LCSSEASON.IDA2A2");
				seasonObj = (LCSSeason) LCSQuery.findObjectById("com.lcs.wc.season.LCSSeason:" + seasonId);
			}
			debug("##### Season Object fetched for[ " + seasonName + "] is :: " + seasonObj);
		}
		return seasonObj;
	}

	/**
	 * @param debug
	 */
	public static void debug(String debug) {

		if (DEBUG) {

			System.out.println("[SP_LOADER_DEBUG] " + debug);
		}
		write("[SP_LOADER_DEBUG] " + debug);
	}

	/**
	 * @param log
	 */
	public static void write(String log) {
		String outputFile = getObjectOutputFile();
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileWriter(outputFile, true));
			writer.println(log);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @return
	 */
	public static String getObjectOutputFile() {
		String outputFile = null;
		try {
			wtproperties = WTProperties.getLocalProperties();
			String wtHome = wtproperties.getProperty("wt.home");
			String outputFilePath = wtHome + temp + File.separator;
			outputFile = outputFilePath + "HBI_SP_LOADER_OUTPUT-log" + ".txt";
		} catch (IOException e) {
			exception(e.toString(), e);

		}
		return outputFile;
	}

	/**
	 * @param error
	 * @param e
	 */
	public static void exception(String error, Exception e) {
		System.out.println("[SP_LOADER_EXCEPTION]" + error);
		e.printStackTrace();
		writeErrorLog("[SP_LOADER_EXCEPTION] " + error + "\n [ERROR MESSAGE :: " + e.getLocalizedMessage() + "]"
				+ "\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!            END           !!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
	}

	/**
	 * @param log
	 */
	public static void writeErrorLog(String log) {
		String errorLogFile = getErrorLogFile();
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileWriter(errorLogFile, true));
			writer.println(log);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @return
	 */
	public static String getErrorLogFile() {
		String outputFile = null;
		try {
			wtproperties = WTProperties.getLocalProperties();
			String wtHome = wtproperties.getProperty("wt.home");
			String outputFilePath = wtHome + temp + File.separator;
			outputFile = outputFilePath + "SP_LOADER_ERROR-log" + ".txt";
			File log = new File(outputFile);
			log.createNewFile();
		} catch (IOException e) {
			exception(e.toString(), e);

		}
		return outputFile;
	}

	/**
	 * @param productMaster
	 * @return
	 * @author Manoj
	 * @throws WTException
	 *             This method get all the active seasons associated to a
	 *             product
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Collection<LCSSeason> getAllActiveSeasonsOfAProduct(LCSProduct prod) throws WTException {
		debug("#######Started getAllActiveSeasonsOfAProduct Method ######");
		String masterida2 = FormatHelper.getNumericObjectIdFromObject((WTObject) prod.getMaster());
		LCSProduct product = null;
		Collection allSeasons = new ArrayList();
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		stmt.appendFromTable("LCSProduct", "product");
		stmt.appendSelectColumn("product", "ida2a2");
		stmt.appendOpenParen();
		stmt.appendCriteria(new Criteria("product", "ida3Masterreference", masterida2, Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", "latestIterationInfo", "1", Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", "versionida2versioninfo", "A", Criteria.NOT_EQUAL_TO));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", "ClassNameKeyB12", "wt.part.WTPartMaster", Criteria.EQUALS));
		stmt.appendClosedParen();

		// debug("stmt........" + stmt.toString());
		Collection output = LCSQuery.runDirectQuery(stmt).getResults();
		// debug("size..."+output.size());
		Iterator itr = output.iterator();
		while (itr.hasNext()) {

			FlexObject obj = (FlexObject) itr.next();
			product = (LCSProduct) LCSQuery
					.findObjectById("OR:com.lcs.wc.product.LCSProduct:" + obj.getData("PRODUCT.IDA2A2"));

			// Get the active season associated to product
			LCSSeason season = com.lcs.wc.season.SeasonProductLocator.getSeasonRev(product);
			LCSSeasonProductLink spLink = LCSSeasonQuery.findSeasonProductLink(product, season);
			if (spLink != null && !spLink.isSeasonRemoved()) {
				allSeasons.add(season);
			}
		}
		debug("####### getAllActiveSeasonsOfAProduct allSeasons size ###### " + allSeasons.size());
		debug("#######Completed getAllActiveSeasonsOfAProduct Method ######");
		return allSeasons;

	}

	/**
	 * @param sp
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LCSSeason getSPSeason(LCSProduct sp) throws WTException {
		LCSSeason validSeason = null;
		String[] ignoreSeasonsArray = IGNORED_SEASONS.split(",");
		Collection<String> ignoreSeasonsList = Arrays.asList(ignoreSeasonsArray);

		Collection<LCSSeason> validSeasons = new ArrayList();
		Collection<LCSSeason> seasons = HBISPBomUtil_old.getAllActiveSeasonsOfAProduct(sp);
		Iterator itr = seasons.iterator();

		while (itr.hasNext()) {
			LCSSeason season = (LCSSeason) itr.next();
			String seasonName = season.getName();
			if (!ignoreSeasonsList.contains(seasonName)) {
				validSeasons.add(season);
			} else {
				debug("Found ignored season [" + seasonName + "], on the selling product [" + sp.getName() + "]");
			}
		}

		if (validSeasons.size() == 1) {
			validSeason = validSeasons.iterator().next();
		} else {
			throw new WTException(
					"Either no valid season or More than one valid Season linked to sp [" + sp.getName() + "]");
		}
		return validSeason;

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param season
	 * @param srcCfg
	 * @param specName
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws NumberFormatException
	 */
	@SuppressWarnings("rawtypes")
	public static FlexSpecification createProdSpec(String sap_key, LCSProduct sp, String putup, LCSSeason season,
			LCSSourcingConfig srcCfg, String specName)
			throws WTException, NumberFormatException, WTPropertyVetoException {
		FlexSpecificationClientModel flexSpecModel = new FlexSpecificationClientModel();
		boolean createSpec = true;
		FlexType specFlexType = FlexTypeCache.getFlexTypeFromPath(SPECIFICATION_SELLING);
		flexSpecModel.setFlexType(specFlexType);

		FlexSpecification spec = null;
		SearchResults sr = FlexSpecQuery.findExistingSpecs(sp, season, srcCfg);

		LCSLog.debug("1Spec sr size " + sr.getResultsFound());
		if (sr.getResults().size() > 0) {
			Iterator srItr = sr.getResults().iterator();
			while (srItr.hasNext()) {
				FlexObject specFo = (FlexObject) srItr.next();

				String name = specFo.getData("FLEXSPECMASTER.NAME");
				debug("createProdSpec name:: " + name);
				if (name.contains(specName)) {
					createSpec = false;
					break;
				}
			}

		}

		if (createSpec) {
			Collection<String> seasonIds = new ArrayList<String>();
			/** Adding seasons to it. */
			seasonIds.add(season.toString());

			WTPartMaster prodMaster = (WTPartMaster) sp.getMaster();

			/** Setting the specification name to product name. */
			flexSpecModel.setValue("specName", specName);
			/** Creating an array list for source. */
			Collection<LCSSourcingConfig> sourceIds = new ArrayList<LCSSourcingConfig>();
			/** Adding source to it. */
			debug("srcCfg.isLatestIteration()::::1:::::::::::::::::::::::::" + srcCfg.isLatestIteration());
			srcCfg = (LCSSourcingConfig) VersionHelper.latestIterationOf(srcCfg.getMaster());
			debug("srcCfg.isLatestIteration()::::2:::::::::::::::::::::::::" + srcCfg.isLatestIteration());

			// srcCfg=(LCSSourcingConfig)VersionHelper.checkout(srcCfg);
			sourceIds.add(srcCfg);

			Collection<String> componentIds = new ArrayList<String>();
			/** Creating a HashMap. */
			Map<String, String> addtionalParams = new HashMap<String, String>();
			flexSpecModel.setSpecOwnerReference(ObjectReference.newObjectReference(prodMaster));
			flexSpecModel.setSpecOwner(srcCfg.getProductMaster());
			/**
			 * Saving the spec by passing
			 * flexSpecModel,sourceIds,seasonIds,componentIds,addtionalParams
			 */
			debug("seasonIds " + seasonIds);
			debug("sourceIds " + sourceIds);

			FlexSpecHelper.service.saveSpec(flexSpecModel, sourceIds, seasonIds, componentIds, addtionalParams);

		}
		return spec;

	}

	/**
	 * @param sp
	 * @param season
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws NumberFormatException
	 */
	@SuppressWarnings("deprecation")
	public static LCSSourcingConfig getSrcCfg(LCSProduct sp, LCSSeason season)
			throws WTException, NumberFormatException, WTPropertyVetoException {
		LCSSourcingConfig srcCfg = null;
		//WTPartMaster prodMaster = (WTPartMaster) sp.getMaster();
		//WTPartMaster seasonMaster = (WTPartMaster) season.getMaster();
		LCSPartMaster prodMaster = (LCSPartMaster) sp.getMaster();
		LCSSeasonMaster seasonMaster = (LCSSeasonMaster) season.getMaster();
		srcCfg = LCSSourcingConfigQuery.getPrimarySource(prodMaster, seasonMaster);
		if (srcCfg == null) {

			FlexType sourceFlexType = FlexTypeCache.getFlexTypeFromPath("Sourcing Configuration");
			srcCfg = LCSSourcingConfig.newLCSSourcingConfig();
			LCSSourcingConfigMaster scMaster = new LCSSourcingConfigMaster();
			// System.out.println("linkedgp.getMasterReference()
			// "+linkedgp.getMasterReference());
			scMaster.setProductMasterReference(sp.getMasterReference());
			scMaster.setProductARevId(sp.getProductARevId());
			scMaster.setProductSeasonRevId(Double.parseDouble(FormatHelper.getNumericVersionIdFromObject(sp)));
			scMaster.setSeasonRevId(sp.getSeasonRevId());
			scMaster.setPrimarySource(true);
			srcCfg.setMaster(scMaster);
			srcCfg.setFlexType(sourceFlexType);
			srcCfg.setProductARevId(sp.getProductARevId());
			srcCfg.setProductSeasonRevId(Double.parseDouble(FormatHelper.getNumericVersionIdFromObject(sp)));
			srcCfg.setSeasonRevId(sp.getSeasonRevId());

			srcCfg.setOwnership(sp.getOwnership());
			srcCfg.setState(sp.getState());

			LCSSourcingConfigLogic scLogic = new LCSSourcingConfigLogic();

			scLogic.saveSourcingConfig(srcCfg);

		}
		return srcCfg;
	}

	/**
	 * @param product
	 * @param season
	 * @return
	 * @throws WTException
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	public static FlexSpecification findProdSpecByName(LCSProduct product, LCSSeason season, String specName)
			throws WTException {
		debug("Starting findProdSpec method ");
		FlexSpecification spec = null;
		//WTPartMaster seasonMaster = null;
		//WTPartMaster prodMaster = (WTPartMaster) product.getMaster();
		LCSSeasonMaster seasonMaster = null;
		LCSPartMaster prodMaster = (LCSPartMaster) product.getMaster();

		if (season != null) {
			seasonMaster = (LCSSeasonMaster) season.getMaster();
		}
		// Get the search result of all specs associated to Product and Season
		SearchResults searchResults = FlexSpecQuery.findSpecsByOwner(prodMaster, seasonMaster, null, null);
		Iterator results = searchResults.getResults().iterator();
		while (results.hasNext()) {
			FlexObject obj = (FlexObject) results.next();
			String specId = obj.getData("FLEXSPECIFICATION.BRANCHIDITERATIONINFO");
			String name = obj.getData("FLEXSPECMASTER.NAME");
			debug("FLEXSPECMASTER.NAME:: " + name);
			debug("Expected specName by Ref_Putup:: " + specName);
			// debug("FlexOBject::" + obj);
			if (FormatHelper.hasContent(specId) && name.contains(specName)) {
				spec = (FlexSpecification) LCSQuery
						.findObjectById("VR:com.lcs.wc.specification.FlexSpecification:" + specId);

			}

		}
		if (spec != null) {
			return spec;
		} else {
			throw new WTException(
					"No Spec found on the product [" + product.getName() + "], with SpecName :: [" + specName + "]");
		}

	}

	/**
	 * @param linkedGP
	 * @param newGPBom
	 * @param gpSpec
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static FlexSpecification createSpecBomLink(LCSProduct sp, FlexBOMPart bom, LCSSeason season,
			FlexSpecification spec) throws WTException {
		FlexSpecification linkedSpec = null;
		if (bom != null && spec != null && !VersionHelper.isCheckedOut(bom)) {
			try {
				// get latest iteration of spec
				spec = (FlexSpecification) VersionHelper.latestIterationOf(spec.getMaster());
				bom = (FlexBOMPart) VersionHelper.latestIterationOf(bom.getMaster());
				// Check if the spec already having bom as component
				if (!isBomASpecComponent(spec, bom)) {
					FlexSpecLogic specLogic = new FlexSpecLogic();
					FlexSpecToComponentLink link = specLogic.addBOMToSpec(spec, bom, null);
					FlexSpecLogic.setAsPrimaryBOM(link);

					linkedSpec = (FlexSpecification) VersionHelper.latestIterationOf(link.getSpecificationMaster());
					/*
					 * report("!!! Created New FlexSpecToComponentLink - [Merge BOM :: "
					 * +newGPBom.getName()+"], [gpSpec :: "+gpSpec.getName()
					 * +"] "+ ", [linkedGP :: "+ linkedGP.getName()+ "], " +
					 * "[gpSeason :: "+gpSeason.getName()+"],");
					 */

					debug(" Bom -[ BOM :: " + bom.getName() + " ], added as component to - [Spec ::"
							+ linkedSpec.getName() + "]");
				} else {
					/*
					 * report("!!! Existing FlexSpecToComponentLink Found - [Merge BOM :: "
					 * +newGPBom.getName()+"],"+ ", [linkedGP :: "+
					 * linkedGP.getName()+ "], " +
					 * "[gpSeason :: "+gpSeason.getName()+"], [gpSpec :: "
					 * +gpSpec.getName()+"]");
					 */
					debug("Skipping SpecBOM Link creation, As Found already existing on spec, [BOM :: " + bom.getName()
							+ " ], [spec ::" + spec.getName() + "]");
				}
			} catch (WTException e) {
				/*
				 * error("!!!! WTException occured while adding component [BOM :: "
				 * + newGPBom.getName() + "]," + "to Spec [gpSpec :: " +
				 * gpSpec.getName() + "]");
				 */
				e.printStackTrace();
			}

		}
		return linkedSpec;

	}

	/**
	 * @param gpSpec
	 * @param newGPBom
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isBomASpecComponent(FlexSpecification spec, FlexBOMPart bom) throws WTException {

		if (spec != null && bom != null) {

			Collection components = (Collection) FlexSpecQuery.getSpecComponents(spec, "BOM");
			Iterator itr = components.iterator();
			while (itr.hasNext()) {
				FlexBOMPart flexBOMPart = (FlexBOMPart) itr.next();
				// Return true if BOM is already component on Spec
				if (PersistenceHelper.isEquivalent(flexBOMPart.getMaster(), bom.getMaster())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param prd
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	public static Collection getPSDSize1Values(LCSProduct prd) throws WTException {
		Set<String> uniqueSize = new LinkedHashSet<String>();
		if (prd != null) {
			SearchResults sr = SizingQuery.findProductSizeCategoriesForProduct(prd);

			if (sr != null && sr.getResultsFound() > 0) {
				Collection sizeCatColl = sr.getResults();
				Collection<String> sizesPD = new ArrayList<String>();
				StringBuffer sizeAppend = new StringBuffer();
				Iterator sizeCatItr = sizeCatColl.iterator();
				while (sizeCatItr.hasNext()) {
					FlexObject fob = (FlexObject) sizeCatItr.next();
					// HBISPBomUtil.debug("PRODUCTSIZECATEGORY :: "+fob);
					String sizeValues = "";
					if (fob != null) {
						sizeValues = fob.getString("PRODUCTSIZECATEGORY.SIZEVALUES");
					}
					if (FormatHelper.hasContent(sizeValues)) {
						sizeAppend.append(sizeValues);

						if (sizeAppend.length() > 0) {
							StringTokenizer stToken = new StringTokenizer(sizeAppend.toString(), "|~*~|");
							while (stToken.hasMoreTokens()) {
								sizesPD.add(stToken.nextToken());
							}
						}
					}
				}

				// remove Duplicates
				uniqueSize.addAll(sizesPD);
			}
		}
		List<String> list = new ArrayList<String>(uniqueSize);
		return list;
	}

	/**
	 * @param compStyle
	 * @param aPS_Size_Code
	 * @return
	 * @throws WTException
	 */
	public static String getPlmSize1ApsValue(LCSProduct compStyle, String aPS_Size_Code) throws WTException {
		String plm_Size_Literal = "";
		LCSLifecycleManaged sizeCategoryBO = (LCSLifecycleManaged) compStyle.getValue("hbiSellingSizeCategory");
		String sizeCatIda2a2 = FormatHelper.getNumericObjectIdFromObject(sizeCategoryBO);
		plm_Size_Literal = getPLMSizeLiteralFromApsCode(sizeCatIda2a2, aPS_Size_Code);

		return plm_Size_Literal;

	}

	/**
	 * @param sizeCatIda2a2
	 * @param aPS_Size_Code
	 * @return
	 * @throws WTException
	 */
	private static String getPLMSizeLiteralFromApsCode(String sizeCatIda2a2, String aPS_Size_Code) throws WTException {

		LCSLifecycleManaged businessObject = null;
		String plmSizeLiteral = "";
		String businessObjectTypePath = "Business Object\\Automation Support Tables\\Size Xref";
		FlexType boFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjectTypePath);
		String hbiAPSSizeCategory_DBColumn = boFlexTypeObj.getAttribute("hbiAPSSizeCategory").getVariableName();
		String hbiAPSSizeCode_DBColumn = boFlexTypeObj.getAttribute("hbiAPSSizeCode").getVariableName();
		String typeIdPath = String.valueOf(boFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());

		// Initializing the PreparedQueryStatement,
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendSelectColumn(
				new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, hbiAPSSizeCategory_DBColumn),
				sizeCatIda2a2, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, hbiAPSSizeCode_DBColumn),
				aPS_Size_Code, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeReference.key.id"), "?", "="),
				new Long(typeIdPath));

		// Get SearchResults instance from the given PreparedQueryStatement
		debug("Stmt ::" + statement.toString());
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if (results != null && results.getResultsFound() == 1) {
			FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
			businessObject = (LCSLifecycleManaged) LCSQuery.findObjectById(
					"OR:com.lcs.wc.foundation.LCSLifecycleManaged:" + flexObj.getString("LCSLifecycleManaged.IDA2A2"));
			plmSizeLiteral = (String) businessObject.getValue("hbiPLMSizeLiteral");
		} else {
			throw new WTException(
					"PLMSizeLiteral not found in FlexPlM for aPS_Size_Code Value :: [" + aPS_Size_Code + "]");
		}
		return plmSizeLiteral;

	}

	/**
	 * @param allRows
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	public static String covertInToMergeString(Collection<FlexObject> allRows, String REQUIRED_BOM_COLUMNS)
			throws WTException {

		String dataString = "";
		Iterator itr = allRows.iterator();
		while (itr.hasNext()) {
			FlexObject row = (FlexObject) itr.next();
			dataString = getDataStringFromRow(row, REQUIRED_BOM_COLUMNS) + dataString;

		}

		return dataString;
	}

	/**
	 * @param row
	 * @return
	 * @throws WTException
	 */
	public static String getDataStringFromRow(FlexObject row, String REQUIRED_BOM_COLUMNS) throws WTException {
		String rowDelim = "|!#!|";
		String rowString = "";

		String[] rowColumns = REQUIRED_BOM_COLUMNS.split(",");
		Collection<String> columns = Arrays.asList(rowColumns);

		rowString = getRowColumnString(row, columns) + rowDelim;

		return rowString;
	}

	/**
	 * @param row
	 * @param bomAttrsList
	 * @return
	 * @throws WTException
	 */
	private static String getRowColumnString(FlexObject row, Collection<String> bomAttrsList) throws WTException {
		String rowColumnString = "";
		Iterator<String> itr = bomAttrsList.iterator();
		String coloumnDelim = "|-()-|";
		while (itr.hasNext()) {
			String key = (String) itr.next();
			if ("childId".equals(key)) {

				String childId = getChildId(row.getString("materialDescription"));
				rowColumnString = key + "|&^&|" + childId + coloumnDelim + rowColumnString;

			} else {
				rowColumnString = key + "|&^&|" + row.getString(key) + coloumnDelim + rowColumnString;
			}
		}
		debug("rowColumnString :: " + rowColumnString);
		return rowColumnString;
	}

	/**
	 * @param sp
	 * @param spec
	 * @param bom
	 * @param bomName
	 * @return
	 * @throws WTException
	 */
	public static boolean deleteSpecAndBoms(LCSProduct sp, FlexSpecification spec, FlexBOMPart bom, String bomName)
			throws WTException {
		LCSFlexBOMLogic bomLogic = new LCSFlexBOMLogic();
		FlexSpecToComponentLink link = FlexSpecQuery.getSpecToComponentLink(spec, bom);
	
		if (link != null) {
			FlexSpecLogic specLogic = new FlexSpecLogic();
			specLogic.deleteSpecToComponent(link);

		}
		bomLogic.deleteFlexBOMPart(bom);
		debug("Bom :: [" + bomName + "] deleted");
		return true;

	}

	/**
	 * @param sap_key
	 * @param row
	 * @param string
	 * @param e
	 */
	public static void report(String sap_key, int row, String string, Exception e) {
		// TODO Auto-generated method stub

	}

	public static void validateBOMLoadFiles() throws IOException, WTException {
		validateGE();
		validatePutUpCode();
		validateSize();
		validatePkgCaseBom();
		validateUOM_EA();
		validateSalesBom();
		validateAlternateBom();

	}

	/**
	 * @throws IOException
	 * @throws WTException
	 */
	private static void validateAlternateBom() throws IOException, WTException {
		debug("Started validating the SP_ALTERNATE_BOMS File ");

		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(SP_SALES_ALT_BOM_EXPORT));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		if (!getCellValue(headerRow, 0).equals("SAP_KEY") || !getCellValue(headerRow, 1).equals("COMMENTS")
				|| !getCellValue(headerRow, 2).equals("PARENT_PUTUP")
				|| !getCellValue(headerRow, 3).equals("COMPONENT_STYLE")
				|| !getCellValue(headerRow, 4).equals("COMPONENT_PUTUP")
				|| !getCellValue(headerRow, 5).equals("COMPONENT_COLOR")
				|| !getCellValue(headerRow, 6).equals("COMPONENT_SIZE")
				|| !getCellValue(headerRow, 7).equals("USAGE")) {
			file.close();
			throw new WTException("!!!! Validation failed in SP_ALTERNATE_BOMS Extract "
					+ "not having header rows as [A:: SAP_KEY|B:: COMMENTS|C:: "
					+ "PARENT_PUTUP|D:: COMPONENT_STYLE|E:: COMPONENT_PUTUP |F:: COMPONENT_COLOR"
					+ " |G:: COMPONENT_SIZE |H:: USAGE]");
		}
		file.close();
		debug("Completed validating the SP_ALTERNATE_BOMS File ");
	}

	/**
	 * @throws IOException
	 * @throws WTException
	 * 
	 */
	private static void validateUOM_EA() throws IOException, WTException {

		debug("Started validating the SP_UNITS_OF_MEASURE_EA Files ");
		for (int i = 1; i <= HBISPBomUtil_old.SP_UOM_EA_FILES_SPLIT_COUNT; i++) {
			FileInputStream file = new FileInputStream(new File(SP_UNITS_OF_MEASURE_EA_FILENAME + "_" + i + ".xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			Row headerRow = workbook.getSheetAt(0).getRow(0);
			if (!getCellValue(headerRow, 0).equals("SAP_KEY") || !getCellValue(headerRow, 1).equals("PUTUP")
					|| !getCellValue(headerRow, 2).equals("MEINH") || !getCellValue(headerRow, 3).equals("UMREZ")
					|| !getCellValue(headerRow, 4).equals("J_3ASIZE") || !getCellValue(headerRow, 5).equals("NTGEW")
					|| !getCellValue(headerRow, 6).equals("ZLAENG") || !getCellValue(headerRow, 7).equals("ZBREIT")
					|| !getCellValue(headerRow, 8).equals("ZHOEHE")) {
				file.close();
				throw new WTException("!!!! Validation failed in SP_UNITS_OF_MEASURE_EA_" + i
						+ " Extract not having header rows as [A:: SAP_KEY|B:: PUTUP|C:: "
						+ "MEINH|D:: UMREZ|E:: J_3ASIZE |F:: NTGEW" + " |G:: ZLAENG |H:: ZBREIT|I:: ZHOEHE]");
			}
			file.close();
		}

		debug("Completed validating the SP_UNITS_OF_MEASURE_EA Files ");
	}

	/**
	 * @throws IOException
	 * @throws WTException
	 */
	private static void validateSalesBom() throws IOException, WTException {

		debug("Started validating the SP_SALES_BOMS File ");
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(SP_SALES_BOM_EXPORT));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		if (!getCellValue(headerRow, 0).equals("SAP_KEY") || !getCellValue(headerRow, 1).equals("COMMENTS")
				|| !getCellValue(headerRow, 2).equals("PARENT_PUTUP")
				|| !getCellValue(headerRow, 3).equals("COMPONENT_STYLE")
				|| !getCellValue(headerRow, 4).equals("COMPONENT_PUTUP")
				|| !getCellValue(headerRow, 5).equals("COMPONENT_COLOR")
				|| !getCellValue(headerRow, 6).equals("COMPONENT_SIZE") || !getCellValue(headerRow, 7).equals("USAGE")
				|| !getCellValue(headerRow, 8).equals("PLM_SIZE_LITERAL")) {
			file.close();
			throw new WTException("!!!! Validation failed in SP_SALES_BOMS Extract "
					+ "not having header rows as [A:: SAP_KEY|B:: COMMENTS|C:: "
					+ "PARENT_PUTUP|D:: COMPONENT_STYLE|E:: COMPONENT_PUTUP |F:: COMPONENT_COLOR"
					+ " |G:: COMPONENT_SIZE |H:: USAGE]|I:: PLM_SIZE_LITERAL");
		}
		file.close();
		debug("Completed validating the SP_SALES_BOMS File ");

	}

	/**
	 * @throws WTException
	 * @throws IOException
	 */
	private static void validatePkgCaseBom() throws WTException, IOException {
		debug("Started validating the SP_PACKCASE_BOM Files ");
		for (int i = 1; i <= HBISPBomUtil_old.SP_PKGCase_FILES_SPLIT_COUNT; i++) {
			FileInputStream file = new FileInputStream(new File(SP_PACKCASE_BOM_FILENAME + "_" + i + ".xlsx"));

			XSSFWorkbook workbook = new XSSFWorkbook(file);
			Row headerRow = workbook.getSheetAt(0).getRow(0);
			if (!getCellValue(headerRow, 0).equals("SAP_KEY") || !getCellValue(headerRow, 1).equals("PUTUP")
					|| !getCellValue(headerRow, 2).equals("MEINH") || !getCellValue(headerRow, 3).equals("UMREZ")
					|| !getCellValue(headerRow, 4).equals("J_3ASIZE") || !getCellValue(headerRow, 5).equals("NTGEW")
					|| !getCellValue(headerRow, 6).equals("ZLAENG") || !getCellValue(headerRow, 7).equals("ZBREIT")
					|| !getCellValue(headerRow, 8).equals("ZHOEHE") || !getCellValue(headerRow, 9).equals("CARTON_ID")
					|| !getCellValue(headerRow, 10).equals("PLM_SIZE_LITERAL")) {
				file.close();
				throw new WTException("!!!! Validation failed in SP_PACKCASE_BOM_FILENAME_" + i
						+ " Extract not having header rows as [A:: SAP_KEY|B:: PUTUP|C:: "
						+ "MEINH|D:: UMREZ|E:: J_3ASIZE |F:: NTGEW |G:: ZLAENG"
						+ " |H:: ZBREIT |I:: ZHOEHE|J:: CARTON_ID|I:: PLM_SIZE_LITERAL]");
			}
			file.close();
		}

		debug("Completed validating the SP_PACKCASE_BOM Files ");

	}

	/**
	 * @throws IOException
	 * @throws WTException
	 * 
	 */
	private static void validateSize() throws IOException, WTException {

		debug("Started validating the SP_SIZES File ");
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(SP_SIZES));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);

		if (!getCellValue(headerRow, 0).equals("SAP_KEY") || !getCellValue(headerRow, 1).equals("J_3AKORD2")
				|| !getCellValue(headerRow, 2).equals("APS_SIZE")) {
			file.close();
			throw new WTException("!!!! Validation failed in SP_SIZES Extract "
					+ "not having header rows as [A:: SAP_KEY|B:: J_3AKORD2|C:: APS_SIZE]");
		}
		file.close();
		debug("Completed validating the SP_SIZES File ");
	}

	/**
	 * @throws WTException
	 * @throws IOException
	 * 
	 */
	private static void validatePutUpCode() throws WTException, IOException {
		debug("Started validating the SP_PUTUP_CODES File ");

		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(SP_PUTUP_CODES));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);

		if (!getCellValue(headerRow, 0).equals("SAP_KEY") || !getCellValue(headerRow, 1).equals("ATWRT")
				|| !getCellValue(headerRow, 2).equals("PRIMARY_PUTUP")
				|| !getCellValue(headerRow, 3).equals("REF_PUTUP")) {
			file.close();
			throw new WTException("!!!! Validation failed in SP_PUTUP_CODES Extract "
					+ "not having header rows as [A:: SAP_KEY|B:: ATWRT|C:: PRIMARY_PUTUP|D:: REF_PUTUP]");
		}
		file.close();
		debug("Completed validating the SP_PUTUP_CODES File ");
	}

	/**
	 * @throws IOException
	 * @throws WTException
	 */
	private static void validateGE() throws IOException, WTException {
		debug("Started validating the SP_GENERAL_ATTRIBUTES File ");

		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(SP_GENERAL_ATTRIBUTES));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		if (!getCellValue(headerRow, 0).equals("sap_key") || !getCellValue(headerRow, 1).equals("ATTRIBUTE")
				|| !getCellValue(headerRow, 2).equals("BISMT") || !getCellValue(headerRow, 3).equals("MAKTX")
				|| !getCellValue(headerRow, 4).equals("UMREN_PACKQUANTITY")) {
			file.close();
			throw new WTException("!!!! Validation failed in SP_GENERAL_ATTRIBUTES Extract "
					+ "not having header rows as [A:: sap_key|B:: ATTRIBUTE|C:: BISMT|D:: MAKTX|E:: UMREN_PACKQUANTITY]");
		}
		file.close();
		debug("Completed validating the SP_GENERAL_ATTRIBUTES File ");
	}

	/**
	 * @param loader_input_file
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Collection<Row>> groupBySapKeys(String loader_input_file) throws IOException {
		LinkedHashMap <String, Collection<Row>> sapKeyMap = new LinkedHashMap();
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(loader_input_file));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			// sap_key
			String sap_Key = getCellValue(row, 0);
			Collection<Row> rows = sapKeyMap.get(sap_Key);
			if (rows == null) {
				rows = new ArrayList();
				rows.add(row);
			} else {
				rows.add(row);
			}
			sapKeyMap.put(sap_Key, rows);
		}
		file.close();
		return sapKeyMap;
	}

	/**
	 * @param sap_key
	 * @param sap_key2
	 * @param sap_Key_Rows
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Collection<Row>> groupByRefPutUps(String bomType, String sap_key,
			Collection<Row> sap_Key_Rows) throws WTException {
		Map<String, Collection<Row>> ref_putup_Map = new HashMap();
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sap_Key_Rows.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			// sap_key

			String ref_putup = getRefPutUpByBomType(bomType, sap_key, row);
			Collection<Row> rows = ref_putup_Map.get(ref_putup);
			if (rows == null) {
				rows = new ArrayList();
				rows.add(row);

			} else {
				rows.add(row);
			}
			ref_putup_Map.put(ref_putup, rows);
		}
		return ref_putup_Map;
	}

	/**
	 * @param bomType
	 * @param sap_key
	 * @param row
	 * @return
	 * @throws WTException
	 *             PARENT_PUTUP/PUTUP code for PLM load should be of size 4
	 */
	private static String getRefPutUpByBomType(String bomType, String sap_key, Row row) throws WTException {
		String refPutup = null;
		String coloumn = "";
		if (ALT_BOM.equals(bomType) || SALES_BOM.equals(bomType) || ZFRT_BOM.equals(bomType)||PALLET_BOM.equals(bomType)) {
			coloumn = "PARENT_PUTUP";
			refPutup = getCellValue(row, 2);
			if (refPutup.length() == 3) {
				refPutup = "0" + refPutup;
			}

		} else if (PKG_BOM.equals(bomType)) {
			coloumn = "PUTUP";
			refPutup = getCellValue(row, 1);
		}
		if (refPutup.length() == 4) {
			return refPutup;
		} else {
			throw new WTException("!!!!! " + coloumn + " value length in extract is not equal to '4' . "
					+ "Please check data for SAP_KEY :: [" + sap_key + "], " + coloumn + ":: [" + refPutup + "]");
		}

	}

	/**
	 * @param bomType
	 * @param fileName
	 * @throws WTException
	 * @throws IOException
	 */
	public static void validateBOMBulkLoadFiles(String bomType, String fileName) throws WTException, IOException {
		if (ALT_BOM.equals(bomType) || SALES_BOM.equals(bomType)) {
			validateSalesBom(fileName);
		} else if (PKG_BOM.equals(bomType)) {
			validatePkgCaseBom(fileName);
		}else if (ZFRT_BOM.equals(bomType)){
			validateZFRTBom(fileName);
		}

	}

	/**
	 * @param input_filename
	 * @throws WTException
	 * @throws IOException
	 */
	private static void validatePkgCaseBom(String input_filename) throws WTException, IOException {
		debug("Started validating the BOM input File " + input_filename);
		String loader_input_file = HBISPBomUtil_old.PATH_SPBOMLOADER_IN + input_filename + HBISPBomUtil_old.SPBOMLOADER_IN_TYPE;
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(loader_input_file));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		if (!getCellValue(headerRow, 0).equals("SAP_KEY") || !getCellValue(headerRow, 1).equals("PUTUP")
				|| !getCellValue(headerRow, 2).equals("MEINH") || !getCellValue(headerRow, 3).equals("UMREZ")
				|| !getCellValue(headerRow, 4).equals("J_3ASIZE") || !getCellValue(headerRow, 5).equals("NTGEW")
				|| !getCellValue(headerRow, 6).equals("ZLAENG") || !getCellValue(headerRow, 7).equals("ZBREIT")
				|| !getCellValue(headerRow, 8).equals("ZHOEHE") || !getCellValue(headerRow, 9).equals("CARTON_ID")
				|| !getCellValue(headerRow, 10).equals("PLM_SIZE_LITERAL")
				|| !getCellValue(headerRow, 11).equals("ATTRIBUTE") || !getCellValue(headerRow, 12).equals("BISMT")
				|| !getCellValue(headerRow, 13).equals("MAKTX")
				|| !getCellValue(headerRow, 14).equals("UMREN_PACKQUANTITY")) {
			file.close();
			throw new WTException("!!!! Validation failed in " + loader_input_file
					+ " Extract not having header rows as [A:: SAP_KEY |B:: PUTUP"
					+ " |C:: MEINH |D:: UMREZ |E:: J_3ASIZE |F:: NTGEW |G:: ZLAENG"
					+ " |H:: ZBREIT |I:: ZHOEHE |J:: CARTON_ID |K:: PLM_SIZE_LITERAL"
					+ " |L:: ATTRIBUTE |M:: BISMT |N:: MAKTX |O:: UMREN_PACKQUANTITY" + " |P:: PLM_SIZE_LITERAL]");
		}
		file.close();

		debug("Completed validating the File " + loader_input_file);

	}

	/**
	 * @param fileName
	 * @throws WTException
	 * @throws IOException
	 */
	private static void validateSalesBom(String input_filename) throws WTException, IOException {

		debug("Started validating the BOM input File " + input_filename);
		String loader_input_file = HBISPBomUtil_old.PATH_SPBOMLOADER_IN + input_filename + HBISPBomUtil_old.SPBOMLOADER_IN_TYPE;
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(loader_input_file));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		if (!getCellValue(headerRow, 0).equals("SAP_KEY") || !getCellValue(headerRow, 1).equals("COMMENTS")
				|| !getCellValue(headerRow, 2).equals("PARENT_PUTUP")
				|| !getCellValue(headerRow, 3).equals("COMPONENT_STYLE")
				|| !getCellValue(headerRow, 4).equals("COMPONENT_PUTUP")
				|| !getCellValue(headerRow, 5).equals("COMPONENT_COLOR")
				|| !getCellValue(headerRow, 6).equals("COMPONENT_SIZE") || !getCellValue(headerRow, 7).equals("USAGE")
				|| !getCellValue(headerRow, 8).equals("PLM_SIZE_LITERAL")
				|| !getCellValue(headerRow, 9).equals("ATTRIBUTE") || !getCellValue(headerRow, 10).equals("BISMT")
				|| !getCellValue(headerRow, 11).equals("MAKTX")
				|| !getCellValue(headerRow, 12).equals("UMREN_PACKQUANTITY")
				|| !getCellValue(headerRow, 13).equals("COMP_ATTRIBUTE")
				|| !getCellValue(headerRow, 14).equals("COMP_BISMT")
				|| !getCellValue(headerRow, 15).equals("COMP_MAKTX")
				|| !getCellValue(headerRow, 16).equals("COMP_UMREN_PACKQUANTITY")) {
			file.close();
			throw new WTException("!!!! Validation failed in " + input_filename
					+ " not having header rows as [A:: SAP_KEY |B:: COMMENTS |C:: "
					+ "PARENT_PUTUP |D:: COMPONENT_STYLE |E:: COMPONENT_PUTUP |F:: COMPONENT_COLOR"
					+ " |G:: COMPONENT_SIZE |H:: USAGE] |I:: PLM_SIZE_LITERAL |J:: ATTRIBUTE |K:: BISMT |L:: MAKTX"
					+ " |M:: UMREN_PACKQUANTITY |N:: ATTRIBUTE |O:: BISMT |P:: MAKTX|" + " |Q:: UMREN_PACKQUANTITY]");
		}
		file.close();
		debug("Completed validating the File " + loader_input_file);

	}
	/**
	 * @param fileName
	 * @throws WTException
	 * @throws IOException
	 */
	private static void validateZFRTBom(String input_filename) throws WTException, IOException {

		debug("Started validating the BOM input File " + input_filename);
		String loader_input_file = HBISPBomUtil_old.PATH_SPBOMLOADER_IN + input_filename + HBISPBomUtil_old.SPBOMLOADER_IN_TYPE;
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(loader_input_file));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		if (!getCellValue(headerRow, 0).equals("SAP_KEY") 
				|| !getCellValue(headerRow, 1).equals("COMMENTS")
				|| !getCellValue(headerRow, 2).equals("PARENT_PUTUP")
				|| !getCellValue(headerRow, 3).equals("PARENT_COLOR")
				|| !getCellValue(headerRow, 4).equals("PARENT_SIZE")				
				|| !getCellValue(headerRow, 5).equals("COMPONENT_STYLE")
				|| !getCellValue(headerRow, 6).equals("COMPONENT_PUTUP")
				|| !getCellValue(headerRow, 7).equals("COMPONENT_COLOR")
				|| !getCellValue(headerRow, 8).equals("COMPONENT_SIZE") 
				|| !getCellValue(headerRow, 9).equals("USAGE")
				|| !getCellValue(headerRow, 10).equals("ATTRIBUTE") 
				|| !getCellValue(headerRow, 11).equals("BISMT")
				|| !getCellValue(headerRow, 12).equals("MAKTX")
				|| !getCellValue(headerRow, 13).equals("UMREN_PACKQUANTITY")
				|| !getCellValue(headerRow, 14).equals("COMP_ATTRIBUTE")
				|| !getCellValue(headerRow, 15).equals("COMP_BISMT")
				|| !getCellValue(headerRow, 16).equals("COMP_MAKTX")
				|| !getCellValue(headerRow, 17).equals("COMP_UMREN_PACKQUANTITY")) {
			file.close();
			throw new WTException("!!!! Validation failed in " + input_filename
					+ " not having header rows as [A:: SAP_KEY |B:: COMMENTS |C:: "
					+ "PARENT_PUTUP |D:: PARENT_COLOR |E:: PARENT_SIZE |F:: COMPONENT_STYLE |G:: COMPONENT_PUTUP |H:: COMPONENT_COLOR"
					+ " |I:: COMPONENT_SIZE |J:: USAGE] |K:: PLM_SIZE_LITERAL |L:: ATTRIBUTE |M:: BISMT |N:: MAKTX"
					+ " |O:: UMREN_PACKQUANTITY |P:: ATTRIBUTE |Q:: BISMT |R:: MAKTX|" + " |S:: UMREN_PACKQUANTITY]");
		}
		file.close();
		debug("Completed validating the File " + loader_input_file);

	}
	/**
	 * @param bomType
	 * @param sap_key
	 * @param sap_Key_Rows
	 * @return
	 * @throws WTException
	 */
	public static LCSProduct getProductFromSapKey(String SearchType, String sap_key, Row row) throws WTException {
		// All rows will have same product information
		String attribute = "";
		String attributionCode = "";
		String bismt = "";
		String maktx = "";
		String umren_PackQuantity = "";
		if (PKG_BOM.equals(SearchType)) {
			// hbiErpAttributionCode (L)
			attribute = HBISPBomUtil_old.getCellValue(row, 11);
			// hbiSellingStyleNumber (M)
			bismt = HBISPBomUtil_old.getCellValue(row, 12);

			// hbiDescription (N)
			maktx = HBISPBomUtil_old.getCellValue(row, 13);

			// hbiAPSPackQuantity (O)
			umren_PackQuantity = HBISPBomUtil_old.getCellValue(row, 14);
		} else if (ALT_BOM.equals(SearchType) || SALES_BOM.equals(SearchType) ||PALLET_BOM.equals(SearchType)) {
			// hbiErpAttributionCode (J)
			attribute = HBISPBomUtil_old.getCellValue(row, 9);
			// hbiSellingStyleNumber (K)
			bismt = HBISPBomUtil_old.getCellValue(row, 10);

			// hbiDescription (L)
			maktx = HBISPBomUtil_old.getCellValue(row, 11);

			// hbiAPSPackQuantity (M)
			umren_PackQuantity = HBISPBomUtil_old.getCellValue(row, 12);

		} else if (ZFRT_BOM.equals(SearchType)) {
			// hbiErpAttributionCode (K)
			attribute = HBISPBomUtil_old.getCellValue(row, 10);
			// hbiSellingStyleNumber (L)
			bismt = HBISPBomUtil_old.getCellValue(row, 11);

			// hbiDescription (M)
			maktx = HBISPBomUtil_old.getCellValue(row, 12);

			// hbiAPSPackQuantity (N)
			umren_PackQuantity = HBISPBomUtil_old.getCellValue(row, 13);

		}else if ("COMPONENT_STYLE".equals(SearchType)) {
			// hbiErpAttributionCode (N)
			attribute = HBISPBomUtil_old.getCellValue(row, 13);
			// hbiSellingStyleNumber (O)
			bismt = HBISPBomUtil_old.getCellValue(row, 14);
			// hbiDescription (P)
			maktx = HBISPBomUtil_old.getCellValue(row, 15);
			// hbiAPSPackQuantity (Q)
			umren_PackQuantity = HBISPBomUtil_old.getCellValue(row, 16);
		} else if ("Z_COMPONENT_STYLE".equals(SearchType)) {
			// hbiErpAttributionCode (O)
			attribute = HBISPBomUtil_old.getCellValue(row, 14);
			// hbiSellingStyleNumber (P)
			bismt = HBISPBomUtil_old.getCellValue(row, 15);
			// hbiDescription (Q)
			maktx = HBISPBomUtil_old.getCellValue(row, 16);
			// hbiAPSPackQuantity (R)
			umren_PackQuantity = HBISPBomUtil_old.getCellValue(row, 17);
		}

		attributionCode = HBISPBomUtil_old.getAttributionIda2a2(attribute);

		// Get the product from FlexPLM with the SAP general attributes
		return HBISPBomUtil_old.getProductByStyleNo(attributionCode, bismt, maktx, umren_PackQuantity);

	}

	/**
	 * @param carton_Id
	 * @return
	 * @throws WTException
	 */
	public static String getChildId(String carton_Id) throws WTException {
		String childId = "";
		if (FormatHelper.hasContent(carton_Id)) {
		
			LCSMaterialQuery materialQueryObject = new LCSMaterialQuery();

			// Bug in fetching the material has to fix
			LCSMaterial mat = materialQueryObject.findMaterialByNameType(carton_Id, null);
			if (mat != null) {
				String matMasterid = FormatHelper.getNumericObjectIdFromObject((WTObject) mat.getMaster());
				childId = getMatSupMaster(matMasterid, PLACEHOLDER_VALUE);
			} else {
				throw new WTException("Material not found in flexPLM for [carton_Id ::" + carton_Id + "]");
			}
		}
		return childId;

	}

	/**
	 * @param matMasterId
	 * @param placeHolder
	 * @return
	 * @throws WTException
	 */
	private static String getMatSupMaster(String matMasterId, String placeHolder) throws WTException {
		Query query = new Query();
		String matSupId = "";
		try {
			query.prepareForQuery();
			ResultSet results = query.runQuery("SELECT ida2a2 FROM LCSMaterialSupplierMaster WHERE idA3A6 = "
					+ matMasterId + " AND PLACEHOLDER = " + placeHolder);
			results.next();
			matSupId = results.getString(1);
			HBISPBomUtil_old.debug("MatSupId :: " + matSupId);
			results.close();
			query.cleanUpQuery();

		} catch (SQLException e) {
			throw new WTException("SQL Exception occured in LCSMaterialSupplierMaster method");
		}

		return matSupId;

	}
	private static LCSMaterial getMaterial(String cartonId) {
		// TODO Auto-generated method stub
		LCSMaterial material =null;
		 try {
			 LCSLog.debug("getMaterial cartonId:: "+cartonId);
			if(FormatHelper.hasContent(cartonId)){
				FlexType matCCFlexType = FlexTypeCache.getFlexTypeFromPath(materialFlexTypePathCC);
				LCSLog.debug("matCCFlexType getIdNumber "+matCCFlexType.getIdNumber());
				
				PreparedQueryStatement stmt = new PreparedQueryStatement();
		        stmt.appendFromTable("LCSMaterial");
		        stmt.appendSelectColumn("LCSMaterial", "BRANCHIDITERATIONINFO");
		        stmt.appendOpenParen();
				stmt.appendCriteria(new Criteria("LCSMaterial", "IDA3A11", matCCFlexType.getIdNumber(), Criteria.EQUALS));
				stmt.appendAnd();
		        stmt.appendCriteria(new Criteria("LCSMaterial", "latestIterationInfo", "1", Criteria.EQUALS));
		        stmt.appendAnd();
		        stmt.appendCriteria(new Criteria("LCSMaterial", "att1", cartonId, Criteria.EQUALS));
		        stmt.appendClosedParen();
		        Collection<FlexObject> output  = new ArrayList();
		        output = LCSQuery.runDirectQuery(stmt).getResults();
		        LCSLog.debug("size::[ " + output.size()+" ]");
		        if (output.size() == 1) {
		             FlexObject obj = (FlexObject) output.iterator().next();
		             material = (LCSMaterial) LCSQuery
		                     .findObjectById("VR:com.lcs.wc.material.LCSMaterial:" + obj.getData("LCSMaterial.BRANCHIDITERATIONINFO"));
		        }else{
		        	LCSLog.debug("******No material found with name ["+cartonId+"]");
		        }
			  }
			} catch (WTException e) {
				
				e.printStackTrace();
			}	

		return material;
		//(LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:78861374");
	}
}

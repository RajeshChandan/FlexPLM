package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.Query;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartClientModel;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class HBIGPBOMLoader implements RemoteAccess, Serializable {
	private static String[] bomTypeArray = { "ColorwayBOM" };
	private static RemoteMethodServer remoteMethodServer;
	private static final String ID = "ID";
	private static final String REQUIRED_BOM_COLUMNS = "ID,branchId,sortingNumber,partName,section,materialDescription,childId,dimensionName,colorId,colorDimensionId";
	public static final String SP_SALES_BOM_EXPORT = "D:\\ptc\\Windchill_11.2\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\";

	@SuppressWarnings("rawtypes")
	private static Collection validBomTypes = Arrays.asList(bomTypeArray);
	private static Map<String, String> dataMap = new HashMap<String, String>();

	static { 
		String loader_input_file = HBISPBomUtil.PATH_SPBOMLOADER_IN + "AA_Color_translation"
				+ HBISPBomUtil.SPBOMLOADER_IN_TYPE;
		//String path = "data/TestDataSheet.xlsx";
		FileInputStream fis;
		Workbook workbook;
		Sheet sheet=null;

		try {
			fis = new FileInputStream(loader_input_file);
			 workbook = new XSSFWorkbook(fis);
			  sheet = workbook.getSheetAt(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int lastRow = sheet.getLastRowNum();
		Map<String, Map<String, String>> excelFileMap = new HashMap<String, Map<String,String>>();
		//Looping over entire row
		for(int i=0; i<=lastRow; i++){
		Row row = sheet.getRow(i);
		//1st Cell as Value
		Cell valueCell = row.getCell(3);
		//0th Cell as Key
		Cell keyCell = row.getCell(0);
		String value = valueCell.getStringCellValue().trim();
		String key = keyCell.getStringCellValue().trim();
		//Putting key & value in dataMap
		dataMap.put(key, value);
		
    } 
		System.out.println("dataMap:::::::::::::"+dataMap);
	}
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws InvocationTargetException, WTException, IOException {
		if (args.length != 3) {
			System.out.println(
					"Accepted command and input parameters:: java com.hbi.wc.load.sploader.HBISPBomBulkLoader <bomType> <input_file_name> <create/recreate>");
			throw new WTException(
					"java com.hbi.wc.load.sploader.HBIGPBOMLoader <bomType> <input_file_name> <create/recreate>");
		}

		String bomType = args[0];
		if (!validBomTypes.contains(bomType)) {
			System.out.println("Only follwing are allowed bom types for bulk load :: " + validBomTypes);
		}
		String fileName = args[1];
		String mode = args[2];

		if (mode.equals("create") || mode.equals("recreate")) {
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			remoteMethodServer = RemoteMethodServer.getDefault();
			
			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			//authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID); //username here
			authenticator.setRemoteUser("prodadmin"); //username here
			remoteMethodServer.setAuthenticator(authenticator);
			WTPrincipal principal = SessionHelper.manager.getPrincipal();
			Class[] argTypes = { String.class, String.class, String.class };
			Object[] argValues = { bomType, fileName, mode };

			try {
				// load Bom data into FlexPLM
				remoteMethodServer.invoke("loadSPBOM", "com.hbi.wc.load.sploader.HBIGPBOMLoader", null, argTypes,
						argValues);

			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("only create/recreate is allowed as input parameter");
		}

	}

	/**
	 * @param str
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void loadSPBOM(String bomType, String input_filenames, String mode) throws WTException, IOException {
		String arg[] = input_filenames.split(",");
		HBISPBomUtil.debug(" Input Data files count :: " + arg.length);
		NumberFormat formatter = new DecimalFormat("#0.00000");
		int totalcount = 0;
		long totalstart = System.currentTimeMillis();
		for (String input_filename : arg) {
			int count = 0;
			// Prepare load files
			long start_Validation = System.currentTimeMillis();

			// Validation logic to be written
			validateGPBom(input_filename);

			long end_Vaidation = System.currentTimeMillis();
			HBISPBomUtil.debug(input_filename + " Validation completed in ["
					+ formatter.format((end_Vaidation - start_Validation) / 1000d) + "]");

			long start = System.currentTimeMillis();
			HBISPBomUtil.debug(
					"########## Started Loading [" + bomType + "] from file [" + input_filename + "] ###########");
			try {

				long start_ReadingFile = System.currentTimeMillis();

				String report_file = HBISPBomUtil.PATH_SPBOMLOADER_IN + input_filename + "_Report"
						+ HBISPBomUtil.SPBOMLOADER_IN_TYPE;

				// Write the workbook in file system
				FileOutputStream bom_report = new FileOutputStream(new File(report_file));
				// Create blank workbook
				XSSFWorkbook workbook = new XSSFWorkbook();
				// Create a blank sheet
				XSSFSheet spreadsheet = workbook.createSheet("SPBOMReport");

				int rowid = 0;
				// Create row object
				XSSFRow row_out = spreadsheet.createRow(rowid++);
				Cell cell0 = row_out.createCell(0);
				Cell cell1 = row_out.createCell(1);
				Cell cell2 = row_out.createCell(2);
				Cell cell3 = row_out.createCell(3);
				cell0.setCellValue("articlebom_product_code");
				cell1.setCellValue("STATUS");
				cell2.setCellValue("RUNTIME");
				cell3.setCellValue("COMMENTS");
				String loader_input_file = HBISPBomUtil.PATH_SPBOMLOADER_IN + input_filename
						+ HBISPBomUtil.SPBOMLOADER_IN_TYPE;
				// Get All the SAP_Keys that have to be loaded.
				Map<String, Collection<Row>> groupedBysapkeys_Rows = HBISPBomUtil.groupBySapKeys(loader_input_file);
				count = count + groupedBysapkeys_Rows.size() - 1;
				totalcount = totalcount + groupedBysapkeys_Rows.size() - 1;
				long End_ReadingFile = System.currentTimeMillis();
				HBISPBomUtil.debug("########## Total unique Style_Number found from File [" + input_filename + " ] is ["
						+ groupedBysapkeys_Rows.size() + "]");

				HBISPBomUtil.debug("########## Completed Reading data from excel [" + bomType + "], from"
						+ input_filename + " in [ " + formatter.format((End_ReadingFile - start_ReadingFile) / 1000d)
						+ " seconds ]");
				Iterator keys_Itr = groupedBysapkeys_Rows.keySet().iterator();
				while (keys_Itr.hasNext()) {
					String sap_key = (String) keys_Itr.next();
					if (!"articlebom_product_code".equals(sap_key)) {
						HBISPBomUtil.debug("########## Started Transactions For Style Number :: [" + sap_key + "]");
						// To get material for cartonId in SALES BOM
						Collection<Row> sap_Key_Rows = groupedBysapkeys_Rows.get(sap_key);
						Iterator<Row> rowIterator = sap_Key_Rows.iterator();
						while (rowIterator.hasNext()) {
							Row row = rowIterator.next();
							// sap_key
							break;
						}

						// Carton ID material which is of type Casing\Corrugated
						// Carton

						long start_sapKey = System.currentTimeMillis();
						row_out = spreadsheet.createRow(rowid++);
						cell0 = row_out.createCell(0);
						cell1 = row_out.createCell(1);
						cell2 = row_out.createCell(2);
						cell3 = row_out.createCell(3);

						cell0.setCellValue(sap_key);

						// Map<String, Collection<Row>> validPutups =
						// HBISPBomUtil.groupByRefPutUps(bomType,
						// sap_key,sap_Key_Rows);
						try {
							// Get respective products in FlexPLM for the
							// SAP_Key
							FlexBOMPart bom = null;

							LCSProduct gp = HBIGPBOMUtil.getProductByStyleNo(sap_key);
							String hbiPLMNo=(String)gp.getValue("hbiPLMNo");
							// Spec name logic to be written
							String specName = "PD – Existing – ALL";
							//LCSSeason season = HBISPBomUtil.getSPSeason(gp);
							LCSSeason season=HBIAASourcingLoader.getGPSeason(gp);
							LCSSourcingConfig srcCfg = HBISPBomUtil.getSrcCfg(gp, season);
							FlexSpecification refSpec = HBIGPBOMUtil.createProdSpec(sap_key, gp, season, srcCfg,
									specName);
							// BOM name logic to be written
							String bomName = hbiPLMNo + " Existing";
							bom = HBIGPBOMUtil.getExistingBom(gp, bomName);
							refSpec = HBISPBomUtil.findProdSpecByName(gp, season, specName);

							if (mode.equals("recreate")) {
								if (bom != null) {
									String nameOldBOM = bom.getName();
									if (nameOldBOM.contains(bomName)) {
										HBISPBomUtil.deleteSpecAndBoms(gp, refSpec, bom, bomName);
									}
								}
								FlexBOMPart gpBOM = creatGPBOMPart(sap_key, gp, bomName,
										"BOM\\Materials\\HBI\\Colorway");
								Collection<Row> rows = getProductBomData(sap_key,
										SP_SALES_BOM_EXPORT + input_filename + ".xlsx");

								gpBOM = createGPBOMLinks(sap_key, gp, gpBOM, rows, season);

								HBISPBomUtil.createSpecBomLink(gp, gpBOM, season, refSpec);

							}

							// Create Spec, Bom and update MOA ref_spec value

							HBISPBomUtil.debug("########## Completed Transactions for Style Number :: [" + sap_key
									+ "], successfully #######");
							cell1.setCellValue("SUCCESS");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue("");
						} catch (Exception e) {
							String error = "!!!! Exception occured for Style Number [" + sap_key + "]";
							HBISPBomUtil.debug(error + ",\n[ERROR MESSAGE :: " + e.getLocalizedMessage() + "]");
							HBISPBomUtil.debug("!!!!! Completed Transactions for Style Number :: [" + sap_key
									+ "], with Errors !!!!");

							// ERROR Report has to be implemented
							HBISPBomUtil.exception(error, e);
							// HBISPBomUtil.report(sap_key,row,"Failed",e);
							cell1.setCellValue("FAILED");

							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue(e.getLocalizedMessage());
						}
					} else {
						HBISPBomUtil.debug("!!!!! Skipping Header row found !!!!");

					}

				}

				// Write the Report details into file
				workbook.write(bom_report);
				bom_report.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			long end = System.currentTimeMillis();

			HBISPBomUtil.debug("##########              	                        ###########\n");

			HBISPBomUtil.debug("########## Completed " + bomType + " bulk loading of total Style Number count [" + count
					+ "], " + " from input file [ " + input_filename + ".xlsx], in [ "
					+ formatter.format((end - start) / 1000d) + " seconds ]    ###########\n");

		}

		long totalend = System.currentTimeMillis();
		HBISPBomUtil.debug("########## Completed " + bomType + " bulk loading of total SAP_KEY count [" + totalcount
				+ "], " + "from the following all input files [ " + input_filenames + "], in [ "
				+ formatter.format((totalend - totalstart) / 1000d) + " seconds ]    ###########\n");

		HBISPBomUtil.debug("########## Check Report and logs for more loader details ###########\n");

		HBISPBomUtil.debug("#####################         END              ###########\n");

	}

	private static void validateGPBom(String input_filename) throws WTException, IOException {

		String loader_input_file = HBISPBomUtil.PATH_SPBOMLOADER_IN + input_filename + HBISPBomUtil.SPBOMLOADER_IN_TYPE;
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(loader_input_file));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		/*
		 * if (!getCellValue(headerRow, 0).equals("SAP_KEY") ||
		 * !getCellValue(headerRow, 1).equals("COMMENTS") ||
		 * !getCellValue(headerRow, 2).equals("PARENT_PUTUP") ||
		 * !getCellValue(headerRow, 3).equals("COMPONENT_STYLE") ||
		 * !getCellValue(headerRow, 4).equals("COMPONENT_PUTUP") ||
		 * !getCellValue(headerRow, 5).equals("COMPONENT_COLOR") ||
		 * !getCellValue(headerRow, 6).equals("COMPONENT_SIZE") ||
		 * !getCellValue(headerRow, 7).equals("USAGE") ||
		 * !getCellValue(headerRow, 8).equals("PLM_SIZE_LITERAL") ||
		 * !getCellValue(headerRow, 9).equals("ATTRIBUTE") ||
		 * !getCellValue(headerRow, 10).equals("BISMT") ||
		 * !getCellValue(headerRow, 11).equals("MAKTX") ||
		 * !getCellValue(headerRow, 12).equals("UMREN_PACKQUANTITY") ||
		 * !getCellValue(headerRow, 13).equals("COMP_ATTRIBUTE") ||
		 * !getCellValue(headerRow, 14).equals("COMP_BISMT") ||
		 * !getCellValue(headerRow, 15).equals("COMP_MAKTX") ||
		 * !getCellValue(headerRow, 16).equals("COMP_UMREN_PACKQUANTITY")) {
		 * file.close(); throw new WTException("!!!! Validation failed in " +
		 * input_filename +
		 * " not having header rows as [A:: SAP_KEY |B:: COMMENTS |C:: " +
		 * "PARENT_PUTUP |D:: COMPONENT_STYLE |E:: COMPONENT_PUTUP |F:: COMPONENT_COLOR"
		 * +
		 * " |G:: COMPONENT_SIZE |H:: USAGE] |I:: PLM_SIZE_LITERAL |J:: ATTRIBUTE |K:: BISMT |L:: MAKTX"
		 * + " |M:: UMREN_PACKQUANTITY |N:: ATTRIBUTE |O:: BISMT |P:: MAKTX|" +
		 * " |Q:: UMREN_PACKQUANTITY]"); }
		 */
		file.close();

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param rows
	 * @param salesBomName
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	private static FlexBOMPart creatGPBOMPart(String sap_key, LCSProduct sp, String gpBomName, String gpbomType)
			throws WTException, WTPropertyVetoException, IOException {
		FlexBOMPart gpBom = null;
		FlexType bomType = FlexTypeCache.getFlexTypeFromPath(gpbomType);
		gpBom = (new LCSFlexBOMLogic()).initiateBOMPart(sp, bomType, "MAIN");
		String bomId = FormatHelper.getObjectId(gpBom);
		FlexBOMPartClientModel bomPartClientModel = new FlexBOMPartClientModel();
		// Load Bom
		try {

			bomPartClientModel.load(bomId);
			bomPartClientModel.setValue("name", gpBomName);

			bomPartClientModel.save();
			bomPartClientModel.checkIn();
			gpBom = bomPartClientModel.getBusinessObject();

		} catch (WTPropertyVetoException e) {
			String error = "WTPropertyVetoException Caught in initiateMergeBom method";
			throw new WTException(e);
		}

		return gpBom;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param bom
	 * @param rows
	 * @param season
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static FlexBOMPart createGPBOMLinks(String sap_key, LCSProduct sp, FlexBOMPart bom, Collection<Row> rows,
			LCSSeason season) throws IOException, WTException, WTPropertyVetoException {

		FlexBOMPart gpBom = bom;
		if (gpBom != null) {

			// Convert the BOM rows into FlexObjects
			Collection<FlexObject> bomLinks = convertInToFlexObjects(rows, sp, gpBom, season);

			// Covert data into a string with required delimiters
			String dataString = covertInToMergeString(bomLinks, REQUIRED_BOM_COLUMNS);

			FlexBOMPartClientModel bomPartClientModel = new FlexBOMPartClientModel();
			// Get latest version
			gpBom = (FlexBOMPart) VersionHelper.latestIterationOf(gpBom);
			// checkout
			gpBom = (FlexBOMPart) VersionHelper.checkout(gpBom);

			HBISPBomUtil.debug("************* Checked out BOM");

			// get Working Copy
			gpBom = (FlexBOMPart) VersionHelper.getWorkingCopy(gpBom);

			HBISPBomUtil.debug("************* Got Working copy of BOM");

			// get ObjectId of Bom
			String bomId = FormatHelper.getObjectId(gpBom);

			// Load Bom
			bomPartClientModel.load(bomId);

			HBISPBomUtil.debug("************* Loaded BOM");

			// Update BOM with MergedBomLineItems Data String
			bomPartClientModel.update(dataString);
			HBISPBomUtil.debug("************* Updated BOM with Merged Data");

			// save the bom
			bomPartClientModel.save();
			HBISPBomUtil.debug("************* BOM Saved");
			bomPartClientModel.checkIn();
			gpBom = bomPartClientModel.getBusinessObject();
			HBISPBomUtil.debug("########## Completed Loading GP BOM  ###########");
		} else {
			HBISPBomUtil.debug("BOM is Null");
		}
		return gpBom;
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

				String childId = HBIGPBOMUtil.getChildId(row.getString("materialDescription"));
				rowColumnString = key + "|&^&|" + childId + coloumnDelim + rowColumnString;

			} else {
				rowColumnString = key + "|&^&|" + row.getString(key) + coloumnDelim + rowColumnString;
			}
		}
		return rowColumnString;
	}

	/**
	 * @param rows
	 * @param sp
	 * @param salesBom
	 * @param season
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Collection<FlexObject> convertInToFlexObjects(Collection<Row> rows, LCSProduct sp,
			FlexBOMPart salesBom, LCSSeason season) throws WTException, IOException {

		Collection<FlexObject> bomLinks = new ArrayList();
		LCSFlexBOMLogic bomLogic = new LCSFlexBOMLogic();
		int maxBranchId = bomLogic.getMaxBranchId(salesBom);
		int maxSortingNumber = 0;

		// Iterate through each rows one by one
		Iterator<Row> rowIterator = rows.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			FlexObject linkObj = new FlexObject();

			maxBranchId = maxBranchId + 1;
			maxSortingNumber = maxSortingNumber + 1;

			// COMMENTS (B)
			linkObj.put("partName", HBISPBomUtil.getCellValue(row, 1));
			linkObj.put("section", HBISPBomUtil.getCellValue(row, 2));
			// String childId = getChildId(HBISPBomUtil.getCellValue(row, 3));
			// linkObj.put("childId", childId);
			linkObj.put("materialDescription", HBISPBomUtil.getCellValue(row, 3));

			linkObj.put("branchId", maxBranchId);
			linkObj.put("sortingNumber", maxSortingNumber);
			linkObj.put(ID, updateID(maxBranchId, salesBom));
			linkObj.put("DROPPED", "false");
			bomLinks.add(linkObj);
			// HBISPBomUtil.debug("FlexObject :: "+linkObj);
			Collection viewableSKUs = new ProductHeaderQuery().findSKUs(sp, null, season, true, false);
			Iterator viewableSKUitr = viewableSKUs.iterator();
			Collection viewableSKU = new ArrayList();
			while (viewableSKUitr.hasNext()) {
				FlexObject linkObjvar = new FlexObject();
				LCSSKU sku = (LCSSKU) viewableSKUitr.next();
				linkObjvar.put("dimensionName", ":SKU");
				String dimId = getDimId(maxBranchId, salesBom, sku);
				LCSColor color = (LCSColor) sku.getValue("color");
				String colorId = null;

				String hbiColorwayDescription=(String)color.getValue("hbiColorwayDescription");
				String mcid=hbiColorwayDescription;

				if(FormatHelper.hasContent(mcid) && "cutPartSpread".equals(HBISPBomUtil.getCellValue(row, 2))){
					System.out.println("---Color Mapping only for cutPartSpread------------------");
					PreparedQueryStatement statement=new PreparedQueryStatement();
					FlexType colorType = FlexTypeCache.getFlexTypeFromPath("Color\\AA Color");

					
					statement.appendSelectColumn(
							new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.id"));
					statement.appendFromTable(LCSColor.class);
					statement.appendAndIfNeeded();
					/*statement.appendCriteria(
							new Criteria(new QueryColumn(LCSColor.class, colorType.getAttribute("hbiColorServiceName").getVariableName()), "?", Criteria.EQUALS),
							mcid);*/
					statement.appendCriteria(
							new Criteria(new QueryColumn(LCSColor.class, colorType.getAttribute("hbiColorServiceName").getColumnName()), "?", Criteria.EQUALS),
							mcid);
					statement.appendAndIfNeeded();
					statement.appendCriteria(
							new Criteria(new QueryColumn("LCSColor", "BRANCHIDA2TYPEDEFINITIONREFE"), "?", Criteria.EQUALS),
							FormatHelper.getNumericObjectIdFromObject(colorType));
					SearchResults results = LCSQuery.runDirectQuery(statement);
					System.out.println("statement::::::::::"+statement);

					System.out.println("results::::::::::"+results);

					//
					if(results != null && results.getResultsFound() > 0)
					{
						FlexObject flexObj = (FlexObject) results.getResults().firstElement();
						colorId = flexObj.getString("LCSColor.IDA2A2");
					}
				
				String colorDimensionId = FormatHelper.getNumericObjectIdFromObject((WTObject) sku.getMaster());

				linkObjvar.put("ID", dimId);
				linkObjvar.put("colorId", colorId);
				linkObjvar.put("colorDimensionId", colorDimensionId);
				linkObjvar.put("branchId", maxBranchId);
				linkObjvar.put("sortingNumber", maxSortingNumber);
				bomLinks.add(linkObjvar);
				}

			}
		}

		return bomLinks;

	}

	/**
	 * @param secRow
	 * @param maxBranchId
	 * @param mergedBom
	 * @return
	 */
	public static String updateID(int maxBranchId, FlexBOMPart mergedBom) {

		String bomMasterID = FormatHelper.getNumericObjectIdFromObject((WTObject) mergedBom.getMaster());
		String topRowID = "-PARENT:wt.part.WTPartMaster:" + bomMasterID + "-REV:A-BRANCH:" + maxBranchId;
		return topRowID;
	}

	/**
	 * @param sap_key
	 * @param file_extract
	 * @param putup
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Collection<Row> getProductBomData(String sap_key, String file_extract) throws IOException {
		long start_getSalesData = System.currentTimeMillis();
		NumberFormat formatter = new DecimalFormat("#0.00000");

		Collection<Row> rows = new ArrayList();
		FileInputStream file = new FileInputStream(new File(file_extract));

		// Create Workbook instance holding reference to .xlsx file
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		boolean flag = false;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			// SAP_KEY (cell(0)) AND PARENT_PUTUP cell(2)

			if (sap_key.equals(HBISPBomUtil.getCellValue(row, 0))) {
				rows.add(row);
				flag = true;
			} else if (flag) {
				break;
			}
		}
		file.close();

		long end_getSalesData = System.currentTimeMillis();
		HBISPBomUtil.debug(sap_key + ", A Sales Bom Reading From Excels sheets time ::" + " ["
				+ formatter.format((end_getSalesData - start_getSalesData) / 1000d) + "]");

		return rows;

	}

	/**
	 * @param maxBranchId
	 * @param packBom
	 * @param size1Val
	 * @return
	 */
	private static String getDimId(int maxBranchId, FlexBOMPart packBom, LCSSKU sku) {

		String bomMasterID = FormatHelper.getNumericObjectIdFromObject((WTObject) packBom.getMaster());
		String dimId = "-PARENT:com.lcs.wc.part.LCSPartMaster:" + bomMasterID + "-REV:A-BRANCH:" + maxBranchId + "-SKU:"
				+ sku.getMaster();
		return dimId;
	}

}

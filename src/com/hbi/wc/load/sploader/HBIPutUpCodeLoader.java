package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import wt.enterprise.RevisionControlled;
import wt.httpgw.GatewayAuthenticator;
import wt.pom.Transaction;
import wt.session.SessionContext;
import wt.session.SessionHelper;

import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.io.FileWriter;
import com.hbi.wc.load.sploader.HBISPBomUtil;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MultiObjectHelper;
import com.lcs.wc.moa.LCSMOACollectionClientModel;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.util.FlexObjectUtil;

import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * HBIPutUpCodeLoader.java
 *
 * This class contains stand alone functions using to read data from excel file,
 * initialize business object, initialize product object & remove product links
 * 
 * @author Vijaya.Shetty@Hanes.com
 * @since March-10-2019
 * 
 * @author Manoj Konakalla
 * @since May-30-2019
 * Added new logic to add material number aswell in putup code
 */
public class HBIPutUpCodeLoader implements RemoteAccess {
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;

	private static String putUpCodeTypePath = LCSProperties.get(
			"com.hbi.wc.utility.HBIPutUpCodeLoader.putUpCodeTypePath",
			"Business Object\\Automation Support Tables\\SAP PutUp Codes");
	public static final String PATH_SPPUTUPCODE_IN = "D:\\ptc\\Windchill_11.2\\Windchill\\loadFiles\\lcsLoadFiles\\spPutUpCodeLoader\\";
	public static final String SPPUTUPCODE_LOADER_IN_FILETYPE = ".xlsx";
	private static String floderPhysicalLocation = "";
	static BufferedWriter logger = null;
	static DateFormat fullFormat = null;
	static Date objDate = null;

	static {
		try {
			WTProperties wtprops = WTProperties.getLocalProperties();
			String home = wtprops.getProperty("wt.home");
			floderPhysicalLocation = home + File.separator + "logs" + File.separator + "migration";
			if (!(new File(floderPhysicalLocation).exists())) {
				new File(floderPhysicalLocation).mkdir();
			}
		} catch (Exception exp) {
			LCSLog.debug("Exception in static block of the class HBIPutUpCodeLoader is : " + exp);
		}
	}

	/**
	 * Default executable method of the class HBIPutUpCodeLoader
	 * 
	 * @param args
	 *            - String[]
	 */

	public static void main(String[] args) {
		LCSLog.debug("### START HBIPutUpCodeLoader.main() ###");

		try {
			if (args.length != 1) {
				System.out.println("windchill com.hbi.wc.load.sploader.HBIPutUpCodeLoader <fileName>");
			}

			System.out.println("####### Starting Remote method server connection #####");
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			remoteMethodServer = RemoteMethodServer.getDefault();
			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser("prodadmin"); //username here
			remoteMethodServer.setAuthenticator(authenticator);
			WTPrincipal principal = SessionHelper.manager.getPrincipal();
			System.out.println("####### Successfully logged in #####");

			//This block of code is using to initialize RemoteMethodServer call parameters (RemoteMethodServer call argument types and argument values) and invoking RemoteMethodServer
			Class<?> argTypes[] = {String.class};
			Object[] argValues = { args[0] };
			
			remoteMethodServer.invoke("processProductPutUpCodeMoa", "com.hbi.wc.load.sploader.HBIPutUpCodeLoader", null,
					argTypes, argValues);
			System.exit(0);
	        
		} catch (Exception exp) {
			exp.printStackTrace();
			System.exit(1);
		}

		LCSLog.debug("### END HBIPutUpCodeLoader.main() ###");
	}

	/**
	 * @param putupCode_inFiles
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void processProductPutUpCodeMoa(String putupCode_inFiles)
			throws WTException, WTPropertyVetoException, IOException {
		LCSLog.debug("### START HBIPutUpCodeLoader.processProductPutUpCodeMoa(String putupCode_inFiles) ###");
		String[] putUp_input_files = putupCode_inFiles.split(",");
		NumberFormat formatter = new DecimalFormat("#0.00000");
		for (String putUp_input_file : putUp_input_files) {

			// Do basic Validation on PutUpCode load file
			validatePutUpCode(putUp_input_file);

			try {
				String putUpCode_loader_fileName = PATH_SPPUTUPCODE_IN + putUp_input_file
						+ SPPUTUPCODE_LOADER_IN_FILETYPE;

				long start_Validation = System.currentTimeMillis();

				long end_Vaidation = System.currentTimeMillis();
				LCSLog.debug("[" + putUpCode_loader_fileName + "], Validation completed in ["
						+ formatter.format((end_Vaidation - start_Validation) / 1000d) + "]");

				String report_file = PATH_SPPUTUPCODE_IN + putUp_input_file + "_Report"
						+ SPPUTUPCODE_LOADER_IN_FILETYPE;

				// Write the workbook in file system
				FileOutputStream putUp_report = new FileOutputStream(new File(report_file));
				// Create blank workbook
				XSSFWorkbook workbook = new XSSFWorkbook();
				// Create a blank sheet
				XSSFSheet spreadsheet = workbook.createSheet("SPPUTUPReport");

				int rowid = 0;
				// Create row object
				XSSFRow row_out = spreadsheet.createRow(rowid++);
				Cell cell0 = row_out.createCell(0);
				Cell cell1 = row_out.createCell(1);
				Cell cell2 = row_out.createCell(2);
				Cell cell3 = row_out.createCell(3);
				cell0.setCellValue("SAP_KEY");
				cell1.setCellValue("STATUS");
				cell2.setCellValue("RUNTIME");
				cell3.setCellValue("COMMENTS");

				Map<String, Collection<Row>> groupedBysapkeys_Rows = HBISPBomUtil
						.groupBySapKeys(putUpCode_loader_fileName);
				Iterator itr = groupedBysapkeys_Rows.keySet().iterator();
				while (itr.hasNext()) {
					String sap_key = (String) itr.next();
					if (!"SAP_KEY".equals(sap_key)) {
						LCSLog.debug("########## Started Transactions For SAP_KEY :: [" + sap_key + "]");

						long start_sapKey = System.currentTimeMillis();

						Collection<Row> moaRows = groupedBysapkeys_Rows.get(sap_key);
						try {
							row_out = spreadsheet.createRow(rowid++);
							cell0 = row_out.createCell(0);
							cell1 = row_out.createCell(1);
							cell2 = row_out.createCell(2);
							cell3 = row_out.createCell(3);
							cell0.setCellValue(sap_key);

							processProductPutUpCodeMoa(sap_key, moaRows);
							LCSLog.debug("########## Completed Transactions for SAP_KEY :: [" + sap_key
									+ "], successfully #######");

							cell1.setCellValue("SUCCESS");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue("");

						} catch (Exception e) {
							LCSLog.debug(
									"!!!!! Completed Transactions for SAP_KEY :: [" + sap_key + "], with Errors !!!!");

							cell1.setCellValue("FAILED");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue(e.getMessage());

							logError("!!!!! Error occured for [SAP_KEY :: " + sap_key + "], [ERROR_MESSAGE :: "
									+ e.getMessage() + "]");
							e.printStackTrace();
						}

					}
					
				}
				// Write the Report details into file
				workbook.write(putUp_report);
				putUp_report.close();
			} catch (IOException ioExp) {
				LCSLog.debug(
						"IOException in HBIPutUpCodeLoader.processProductPutUpCodeMoa(String putupCode_inFiles) is "
								+ ioExp);
				ioExp.printStackTrace();
			}
		}
		LCSLog.debug("### END HBIPutUpCodeLoader.processProductPutUpCodeMoa(String putupCode_inFiles) ###");
	}

	/**
	 * @param worksheet
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void processProductPutUpCodeMoa(String sap_key, Collection<Row> moaRows)
			throws WTException, WTPropertyVetoException, IOException {

		LCSLog.debug(
				"### START HBIPutUpCodeLoader.processProductPutUpCodeMoa(String sap_key,Collection<Row> moaRows) ###");

		// Get All the SAP_Keys that have to be loaded.
		Row row = (Row) moaRows.iterator().next();
		String attributionCode = HBISPBomUtil.getCellValue(row, 5);
		String sellingStyleNumber = HBISPBomUtil.getCellValue(row, 6);
		String description = HBISPBomUtil.getCellValue(row, 7);
		String apsPackQuantity = HBISPBomUtil.getCellValue(row, 8);

		attributionCode = HBISPBomUtil.getAttributionIda2a2(attributionCode);
		
		Transaction tr = null;
		try {
			tr = new Transaction();
			tr.start();

			LCSProduct sp = HBISPBomUtil.getProductByStyleNo(attributionCode, sellingStyleNumber, description,
					apsPackQuantity);
			LCSSeason season = HBISPBomUtil.getSPSeason(sp);

			String putUpCode = "";
			String primaryPutUp = "";
			String ref_putup = "";
			String material_number = "";
			Iterator itr = moaRows.iterator();
			while (itr.hasNext()) {
				row = (Row) itr.next();
				putUpCode = row.getCell(1).getStringCellValue();
				primaryPutUp = row.getCell(2).getStringCellValue().toLowerCase();
				ref_putup = row.getCell(3).getStringCellValue();
				material_number = row.getCell(4).getStringCellValue();

				// below method loads the putupcode moa table of a product
				new HBIPutUpCodeLoader().loadPutUpCodeMoaTable(sp, season, putUpCode, primaryPutUp, ref_putup, material_number);

			}

			tr.commit();
			tr = null;
		} finally {
			if (tr != null) {
				LCSLog.debug("!!!!!!!! PutUp code for [SAP_KEY :: " + sap_key
						+ "] Create/update transaction ended with some errors, "
						+ "so Rolling back the data saved in that DB transaction !!!!!!!!!");
				tr.rollback();
			}

		}
		LCSLog.debug(
				"### END HBIPutUpCodeLoader.processProductPutUpCodeMoa(String sap_key,Collection<Row> moaRows) ###");

	}

	/**
	 * @param attributeKey
	 * @param attributeValue
	 * @param businessObjTypePath
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static LCSLifecycleManaged getLifecycleManagedByCriteria(String attributeKey, String attributeValue,
			String businessObjTypePath) throws WTException, WTPropertyVetoException {
		LCSLog.debug(
				"### START HBIPutUpCodeLoader.getLifecycleManagedByCriteria(attributeKey, attributeValue, businessObjTypePath) ###");
		LCSLifecycleManaged attributionCodeRefObj = null;
		FlexType businessObjFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjTypePath);
		String criteriaAttDBColumn = businessObjFlexTypeObj.getAttribute(attributeKey).getColumnDescriptorName();//getVariableName();

		// Initializing the PreparedQueryStatement, which is using to get
		// Business-object based on the given set of parameters(like
		// FlexTypePath of the object)
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendSelectColumn(
				new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeIdPath"),
				businessObjFlexTypeObj.getTypeIdPath(), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, criteriaAttDBColumn),
				attributeValue.trim(), Criteria.EQUALS));
		System.out.println(statement);

		//
		SearchResults results = LCSQuery.runDirectQuery(statement);
		System.out.println("results = " + results);
		if (results != null && results.getResultsFound() > 0) {
			System.out.println(" Results-Found = " + results.getResultsFound());
			FlexObject flexObj = (FlexObject) results.getResults().firstElement();
			attributionCodeRefObj = (LCSLifecycleManaged) LCSQuery.findObjectById(
					"OR:com.lcs.wc.foundation.LCSLifecycleManaged:" + flexObj.getString("LCSLifecycleManaged.IDA2A2"));
		}

		LCSLog.debug(
				"### END HBIPutUpCodeLoader.getLifecycleManagedByCriteria(attributeKey, attributeValue, businessObjTypePath) ###");
		return attributionCodeRefObj;
	}

	
	/**
	 * @param productObj
	 * @param seasonObj
	 * @param putUpCode
	 * @param primaryPutUp
	 * @param ref_putup
	 * @param material_number
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void loadPutUpCodeMoaTable(LCSProduct productObj, LCSSeason seasonObj, String putUpCode, String primaryPutUp,
			String ref_putup, String material_number) throws WTException, WTPropertyVetoException, NumberFormatException, IOException {
		LCSLog.debug(
				"### START HBIPutUpCodeLoader.loadPutUpCodeMoaTable(LCSProduct productObj, LCSSeason seasonObj, String putUpCode, String primaryPutUp, String ref_putup) ###");

		LCSLifecycleManaged putUpCodeRefObj = getLifecycleManagedByCriteria("hbiPutUpCode", putUpCode,
				putUpCodeTypePath);
		if (putUpCodeRefObj == null) {
			throw new WTException("putup code is null  putUpCode = " + putUpCode + " primaryPutUp = " + primaryPutUp
					+ "ref_putup = " + ref_putup);
		}

		FlexSpecification ref_Spec = createRefSpec("", productObj, ref_putup, seasonObj);
		if (ref_Spec == null) {
			throw new WTException("ref_Spec is null putUpCode = " + putUpCode + " primaryPutUp = " + primaryPutUp
					+ "ref_putup = " + ref_putup);
		} else {

			boolean isExistingPutUpMoaRow = updatePutupCodeMOAData(productObj, ref_putup, putUpCode, primaryPutUp,
					ref_Spec, material_number);

			// if not already existing it will create a new row in putUpCode moa
			// table of a product
			if (!isExistingPutUpMoaRow) {
				createPutupCodeMOAData(productObj, putUpCodeRefObj, primaryPutUp, ref_Spec, material_number);
			}
		}

		LCSLog.debug(
				"### END HBIPutUpCodeLoader.loadPutUpCodeMoaTable(LCSProduct productObj, LCSSeason seasonObj, String putUpCode, String primaryPutUp, String ref_putup) ###");

	}

	/**
	 * @param productObj
	 * @param putUpCodeRefObj
	 * @param sortingNumber
	 * @param primaryPutUp
	 * @param material_number 
	 * @param ref_Spec
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	private boolean updatePutupCodeMOAData(LCSProduct sp, String ref_putup, String putupcode, String primaryPutUp,
			FlexSpecification refSpec, String material_number) throws WTPropertyVetoException, WTException {

		LCSMOATable putUpMOATab = (LCSMOATable) sp.getValue("hbiPutUpCode");
		if (putUpMOATab != null) {
			Collection<FlexObject> rowColl = putUpMOATab.getRows();
			if (rowColl.size() > 0) {
				return updatePutUpMoa(sp, putupcode, primaryPutUp, refSpec,material_number, rowColl);
			}
		}
		return false;
	}

	/**
	 * @param sp
	 * @param putupcode
	 * @param primaryPutUp
	 * @param refSpec
	 * @param material_number 
	 * @param rowColl
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	private static boolean updatePutUpMoa(LCSProduct sp, String putupcode, String primaryPutUp,
			FlexSpecification refSpec, String material_number, Collection<FlexObject> rowColl) throws WTPropertyVetoException, WTException {
		Iterator itr = rowColl.iterator();
		while (itr.hasNext()) {
			FlexObject moaRow = (FlexObject) itr.next();
			String key = moaRow.getString("OID");
			LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery
					.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + key);
			String putup = getPutupCode(moaObject);
			LCSLog.debug("[ putupcode:: " + putupcode + "],[ putup :: " + putup + "]");
			if (FormatHelper.hasContent(putup)) {
				if (putup.equals(putupcode)) {
					moaObject.setValue("hbiPrimaryPutup", primaryPutUp);
					moaObject.setValue("hbiReferenceSpecification", refSpec);
					moaObject.setValue("hbiMaterialNumber", material_number);
					LCSMOAObjectLogic.persist(moaObject, true);
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * @param productObj
	 * @param putUpCodeRefObj
	 * @param primaryPutUp
	 * @param ref_Spec
	 * @param material_number
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	public void createPutupCodeMOAData(LCSProduct productObj, LCSLifecycleManaged putUpCodeRefObj, String primaryPutUp,
			FlexSpecification ref_Spec,String material_number) throws WTException, WTPropertyVetoException {
		LCSLog.debug(
				"### START HBIPutUpCodeLoader.createPutupCodeMOAData(productObj, LCSLifecycleManaged putUpCodeObj, sortingNumber, primaryPutUp, ref_Spec) ###");
		String sortingNumber = "1";
		SearchResults moaResults = LCSMOAObjectQuery.findMOACollectionData((LCSPartMaster)productObj.getMaster(),
				productObj.getFlexType().getAttribute("hbiPutUpCode"), "LCSMOAObject.createStampA2", true);
		System.out.println("  moaResults    " + moaResults.getResults().size());
		Collection<FlexObject> moaData = moaResults.getResults();
		if (moaResults != null && moaResults.getResultsFound() > 0) {

			String maxSortingNumber = FlexObjectUtil.maxValueForFlexObjects(moaData, "LCSMOAOBJECT.SORTINGNUMBER",
					"int");
			sortingNumber = Integer.toString((Integer.parseInt(maxSortingNumber) + 1));
		}
		
		
		/* Commented Out
		
		
		// getOwnerVersion
		LCSMOAObject moaObject = LCSMOAObject.newLCSMOAObject();
		moaObject.setFlexType(productObj.getFlexType().getAttribute("hbiPutUpCode").getRefType());
		moaObject.setOwnerReference(((RevisionControlled) productObj).getMasterReference());
		moaObject.setOwnerVersion(productObj.getVersionIdentifier().getValue());
		//moaObject.setOwnerAttribute(productObj.getFlexType().getAttribute("hbiPutUpCode"));
		moaObject.setBranchId(Integer.parseInt(sortingNumber));
		moaObject.setDropped(false);
		moaObject.setSortingNumber(Integer.parseInt(sortingNumber));
		moaObject.getFlexType().getAttribute("hbiPrimaryPutup").setValue(moaObject, primaryPutUp);
		moaObject.getFlexType().getAttribute("hbiPutUpCode").setValue(moaObject, putUpCodeRefObj);
		moaObject.getFlexType().getAttribute("hbiReferenceSpecification").setValue(moaObject, ref_Spec);
		moaObject.getFlexType().getAttribute("hbiMaterialNumber").setValue(moaObject, material_number);
		LCSMOAObjectLogic.persist(moaObject);
		LCSLog.debug("  Owner Version  " + productObj.getVersionIdentifier().getValue());
		LCSMOATable.clearTableFromMethodContextCache((FlexTyped) productObj,
				productObj.getFlexType().getAttribute("hbiPutUpCode"));

		LCSLog.debug(
				"### END HBIPutUpCodeLoader.createPutupCodeMOAData(productObj, LCSLifecycleManaged putUpCodeObj, sortingNumber, primaryPutUp, ref_Spec) ###");
	
	
	
	*/
	
	
	
	
	
	
		LCSMOACollectionClientModel moaModel = new LCSMOACollectionClientModel();
		moaModel.load(FormatHelper.getObjectId(productObj), "hbiPutUpCode");
		StringBuffer dataBuffer = new StringBuffer();
		dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "ID", sortingNumber );
		dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "sortingnumber", sortingNumber );
		MultiObjectHelper.addAttribute(dataBuffer,"dropped", "false");
        MultiObjectHelper.addAttribute(dataBuffer,"hbiPrimaryPutup", primaryPutUp);
        MultiObjectHelper.addAttribute(dataBuffer,"hbiPutUpCode", FormatHelper.getNumericObjectIdFromObject(putUpCodeRefObj));
        MultiObjectHelper.addAttribute(dataBuffer,"hbiReferenceSpecification", String.valueOf(ref_Spec.getBranchIdentifier()));
        MultiObjectHelper.addAttribute(dataBuffer,"hbiMaterialNumber", material_number);
        dataBuffer.append(MultiObjectHelper.ROW_DELIMITER);
		moaModel.updateMOACollection(dataBuffer.toString());
	    LCSMOATable.clearTableFromMethodContextCache((FlexTyped)productObj, productObj.getFlexType().getAttribute("hbiPutUpCode"));
		
		
	}
	
	

	/*
	 * Creating an custom log file for MasterJobProcessor CloudIntegration.
	 * 
	 * @param infoMessage - String
	 */
	public static void logError(String infoMessage) {
		// Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd-HHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		String date = dateFormat.format(new Date());
		String logFileName = "HBIProblematicMOA-" + date;

		try {
			if (logger == null) {

				String location = "D:\\MOALog\\logs";

				// Creating custom log file using with the given filename,
				// initializing writer to write/populate custom info and debug
				// statement for the MasterJobProcessor.
				String strLogFile = location + File.separator + logFileName + ".log";
				logger = new BufferedWriter(new FileWriter(strLogFile, true));
				fullFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
				objDate = new Date();
			}

			// Populating the given infoMessage into the custom log file along
			// with the time stamp, flushing the content from the writer to the
			// physical file which is needed to display
			logger.append(infoMessage);
			logger.newLine();
			logger.flush();
		} catch (IOException ioExp) {
			LCSLog.debug(" IOException in HBIMasterJobProcessor custom log:: " + ioExp);
		}
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @return
	 * @throws NumberFormatException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws IOException
	 */
	private static FlexSpecification createRefSpec(String sap_key, LCSProduct sp, String ref_putup, LCSSeason season)
			throws NumberFormatException, WTPropertyVetoException, WTException, IOException {
		FlexSpecification refSpec = null;
		String specName = HBISPBomUtil.getSpecName(sap_key, sp, ref_putup);
		LCSSourcingConfig srcCfg = HBISPBomUtil.getSrcCfg(sp, season);
		// createSpecs
		HBISPBomUtil.createProdSpec(sap_key, sp, ref_putup, season, srcCfg, specName);

		refSpec = HBISPBomUtil.findProdSpecByName(sp, season, specName);

		return refSpec;
	}

	/**
	 * @param moaObject
	 * @return
	 * @throws WTException
	 */
	private static String getPutupCode(LCSMOAObject moaObject) throws WTException {
		LCSLifecycleManaged putup = (LCSLifecycleManaged) moaObject.getValue("hbiPutUpCode");
		if (putup != null) {
			return (String) putup.getValue("hbiPutUpCode");
		} else {
			return "";
		}

	}

	/**
	 * @param fileName
	 * @throws WTException
	 * @throws IOException
	 */
	private static void validatePutUpCode(String input_filename) throws WTException, IOException {

		LCSLog.debug("##### Started validating the PutUpCode input File [" + input_filename + "] #####");
		String loader_input_file = PATH_SPPUTUPCODE_IN + input_filename + SPPUTUPCODE_LOADER_IN_FILETYPE;
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(loader_input_file));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		if (!HBISPBomUtil.getCellValue(headerRow, 0).equals("SAP_KEY")
				|| !HBISPBomUtil.getCellValue(headerRow, 1).equals("ATWRT")
				|| !HBISPBomUtil.getCellValue(headerRow, 2).equals("PRIMARY_PUTUP")
				|| !HBISPBomUtil.getCellValue(headerRow, 3).equals("REF_PUTUP")
				|| !HBISPBomUtil.getCellValue(headerRow, 4).equals("MATL")
				|| !HBISPBomUtil.getCellValue(headerRow, 5).equals("ATTRIBUTE")
				|| !HBISPBomUtil.getCellValue(headerRow, 6).equals("BISMT")
				|| !HBISPBomUtil.getCellValue(headerRow, 7).equals("MAKTX")
				|| !HBISPBomUtil.getCellValue(headerRow, 8).equals("UMREN_PACKQUANTITY")) {
			file.close();
			throw new WTException("!!!! Validation failed in " + input_filename
					+ " not having header rows as [A:: SAP_KEY |B:: ATWRT |C:: "
					+ "PRIMARY_PUTUP |D:: REF_PUTUP |E:: MATL |F:: ATTRIBUTE |G:: BISMT"
					+ " |H:: MAKTX |I:: UMREN_PACKQUANTITY]");
		}
		file.close();
		LCSLog.debug("###### Completed validating the putUpCode input File [" + loader_input_file + "] #########");

	}
}
package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.hbi.wc.load.sploader.HBISPBomUtil;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.skusize.SKUSize;
import com.lcs.wc.skusize.SKUSizeQuery;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.pom.Transaction;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * HBISkuSizeLoader.java
 *
 * This class contains stand alone functions using to read data from excel file,
 * initialize business object, initialize product object & remove product links
 * 
 * @author UST
 * @since June-14-2019
 */

public class HBIColorwaySizeLoader implements RemoteAccess {
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	public static final String PATH_SKUSIZE_IN = "D:\\ptc\\Windchill_11.2\\Windchill\\loadFiles\\lcsLoadFiles\\spColorwaySizeLoader\\";
	public static final String SKUSIZE_LOADER_IN_FILETYPE = ".xlsx";
	private static final boolean SET_SKU_SIZE_AS_INACTIVE = true;
	static Date objDate = null;
	static DateFormat fullFormat = null;

	/**
	 * Default executable main method of the class HBIColorwaySizeLoader
	 * 
	 * @param args
	 *            - String[]
	 */

	public static void main(String[] args) {
		HBISPBomUtil.debug("### START HBIColorwaySizeLoader.main() ###");

		try {
			if (args.length != 1) {
				HBISPBomUtil.debug("windchill com.hbi.wc.load.sploader.HBIColorwaySizeLoader <fileName>");
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

			Class<?> argTypes[] = { String.class };
			Object[] argValues = { args[0] };
			remoteMethodServer.invoke("processProductSkuSizeData", "com.hbi.wc.load.sploader.HBIColorwaySizeLoader",
					null, argTypes, argValues);
			System.exit(0);
		} catch (Exception exp) {
			exp.printStackTrace();
			System.exit(1);
		}

		HBISPBomUtil.debug("### END HBIColorwaySizeLoader.main() ###");
	}

	/**
	 * @param skuSize_inFiles
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void processProductSkuSizeData(String skuSize_inFiles)
			throws WTException, WTPropertyVetoException, IOException {
		HBISPBomUtil.debug("### START HBIColorwaySizeLoader.processProductSkuSizeData(String skuSize_inFiles) ###");
		String[] skuSize_input_files = skuSize_inFiles.split(",");
		NumberFormat formatter = new DecimalFormat("#0.00000");
		for (String skuSize_input_file : skuSize_input_files) {

			try {
				String skuSize_loader_fileName = PATH_SKUSIZE_IN + skuSize_input_file + SKUSIZE_LOADER_IN_FILETYPE;

				long start_Validation = System.currentTimeMillis();

				// Do basic Validation on Colorway-Size load file
				validateSkuSize(skuSize_input_file);

				long end_Vaidation = System.currentTimeMillis();
				HBISPBomUtil.debug("[" + skuSize_loader_fileName + "], Validation completed in ["
						+ formatter.format((end_Vaidation - start_Validation) / 1000d) + "]");

				String report_file = PATH_SKUSIZE_IN + skuSize_input_file + "_Report" + SKUSIZE_LOADER_IN_FILETYPE;

				// Write the workbook in file system
				FileOutputStream skuSize_report = new FileOutputStream(new File(report_file));
				// Create blank workbook
				XSSFWorkbook workbook = new XSSFWorkbook();
				// Create a blank sheet
				XSSFSheet spreadsheet = workbook.createSheet("SkuSizeReport");

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
						.groupBySapKeys(skuSize_loader_fileName);
				Iterator itr = groupedBysapkeys_Rows.keySet().iterator();
				while (itr.hasNext()) {
					String sap_key = (String) itr.next();
					if (!"SAP_KEY".equals(sap_key) && FormatHelper.hasContent(sap_key)) {
						HBISPBomUtil.debug("########## Started Transactions For SAP_KEY :: [" + sap_key + "]");

						long start_sapKey = System.currentTimeMillis();

						// Get all rows corresponding to the sap_key
						Collection<Row> rows = groupedBysapkeys_Rows.get(sap_key);
						Transaction tr = null;
						try {
							tr = new Transaction();
							tr.start();

							row_out = spreadsheet.createRow(rowid++);
							cell0 = row_out.createCell(0);
							cell1 = row_out.createCell(1);
							cell2 = row_out.createCell(2);
							cell3 = row_out.createCell(3);
							cell0.setCellValue(sap_key);

							processProductSkuSizeData(sap_key, rows);
							HBISPBomUtil.debug("########## Completed Transactions for SAP_KEY :: [" + sap_key
									+ "], successfully #######");

							cell1.setCellValue("SUCCESS");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue("");

							tr.commit();
							tr = null;

						} catch (Exception e) {
							if (tr != null) {
								HBISPBomUtil.debug("!!!!! Completed Transactions for SAP_KEY :: [" + sap_key
										+ "], with Errors !!!!");
								tr.rollback();
							}
							cell1.setCellValue("FAILED");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue(e.getMessage());
							HBISPBomUtil.exception("!!!!! Error occured for [SAP_KEY :: " + sap_key
									+ "], [ERROR_MESSAGE :: " + e.getMessage() + "]", e);
							e.printStackTrace();

						}

					}

				}
				// Write the Report details into file
				workbook.write(skuSize_report);
				skuSize_report.close();
			} catch (IOException ioExp) {
				HBISPBomUtil
						.debug("IOException in HBIColorwaySizeLoader.processProductSkuSizeData(String skuSize_inFiles) is "
								+ ioExp);
				ioExp.printStackTrace();
			}
		}
		HBISPBomUtil.debug("### END HBIColorwaySizeLoader.processProductSkuSizeData(String skuSize_inFiles) ###");
	}

	/*
	 * @param sap_key
	 * 
	 * @param moaRows
	 * 
	 * @throws WTException
	 * 
	 * @throws WTPropertyVetoException
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void processProductSkuSizeData(String sap_key, Collection<Row> rows)
			throws WTException, WTPropertyVetoException, IOException {

		HBISPBomUtil
				.debug("### START HBIColorwaySizeLoader.processProductSkuSizeData(String sap_key,Collection<Row> moaRows) ###"
						+ rows.size());

		DecimalFormat formatter = new DecimalFormat("#0.00000");
		long start_Validation = System.currentTimeMillis();

		// Get the first row for the sap_key to get Product attributes and PSD
		Iterator itr = rows.iterator();

		Row row = (Row) itr.next();
		String sellingStyleNumber = HBISPBomUtil.getCellValue(row, 1);
		String description = HBISPBomUtil.getCellValue(row, 2);
		String attributionCode = HBISPBomUtil.getCellValue(row, 3);
		String apsPackQuantity = HBISPBomUtil.getCellValue(row, 4);
		String psdName = HBISPBomUtil.getCellValue(row, 5);
		String sizecategory = HBISPBomUtil.getCellValue(row, 6);

		// Below product search is based on SAP_KEY for future requirements and
		// not required now
		// LCSProduct prod = HBISPBomUtil.getProductBySapKey(sap_key);

		// get ida2a2 for attribution code
		attributionCode = HBISPBomUtil.getAttributionIda2a2(attributionCode);
		// get product using the attributes
		System.out.println("apsPackQuantity---->"+apsPackQuantity);
		/*apsPackQuantity =apsPackQuantity.replaceAll(".0", "");*/
				System.out.println("apsPackQuantity- after--->"+apsPackQuantity);

		/*LCSProduct prod = HBISPBomUtil.getProductByStyleNo(attributionCode, sellingStyleNumber, description,
				"hbi" + apsPackQuantity);*/
				LCSProduct prod = HBISPBomUtil.getProductByStyleNo(attributionCode, sellingStyleNumber, description,
				apsPackQuantity);

		// Get Season for the Product
		LCSSeason seasonObj = HBISPBomUtil.getSPSeason(prod);

		// Get PSD of the Product
		long start_Validation_psd = System.currentTimeMillis();
		ProductSizeCategory productSizeCategoryObj = getValidPSDForProduct(prod, psdName);
		long end_Validation_psd = System.currentTimeMillis();

		HBISPBomUtil.debug("Validation of PSD completed in ["
				+ formatter.format((end_Validation_psd - start_Validation_psd) / 1000d) + "]");

		// Get Selling size category of the Product
		LCSLifecycleManaged sizeCategoryBO = (LCSLifecycleManaged) prod.getValue("hbiSellingSizeCategory");
		if (sizeCategoryBO == null) {
			throw new WTException("!!! Sizing Category is blank in PLM for the product [" + prod.getName() + "]!!!");
		} else if (sizeCategoryBO != null && !sizecategory.equals(sizeCategoryBO.getName())) {
			throw new WTException("!!! Sizing Category is not matching with the product value in PLM !!!");
		}

		// Update all sku-sizes of a product PSD.
		updateSkuSizes(prod, seasonObj, productSizeCategoryObj, sizeCategoryBO, rows);

		long end_Vaidation = System.currentTimeMillis();
		HBISPBomUtil.debug(
				"### END HBIColorwaySizeLoader.processProductSkuSizeData(String sap_key,Collection<Row> moaRows) ###");

		HBISPBomUtil.debug("Processing of all SKU-SIZES of product:: [" + prod.getName() + "] completed in ["
				+ formatter.format((end_Vaidation - start_Validation) / 1000d) + "]");
	}

	/**
	 * @param prod
	 * @param seasonObj
	 * @param productSizeCategoryObj
	 * @param sizeCategoryBO
	 * @param rows
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("rawtypes")
	private static void updateSkuSizes(LCSProduct prod, LCSSeason seasonObj, ProductSizeCategory productSizeCategoryObj,
			LCSLifecycleManaged sizeCategoryBO, Collection<Row> rows) throws WTException, WTPropertyVetoException {
		boolean isAllInactive = false;
		// Get all skus for the Produt
		Collection allSKUs = new ProductHeaderQuery().findSKUs(prod, null, null, true, false);
		HBISPBomUtil.debug(":::::::::::::::allSKUs::::::::::::" + allSKUs.size());

		if (rows.size() == 1) {
			HBISPBomUtil.debug("Only one row found for the SAP_KEY in the extract");
			Row row = rows.iterator().next();
			String aps_color = HBISPBomUtil.getCellValue(row, 7);
			String apssize = HBISPBomUtil.getCellValue(row, 8);
			String sapgrid = HBISPBomUtil.getCellValue(row, 9);
			// If only making all sku-size inactive required, then the below
			// condition should pass
			if (!FormatHelper.hasContent(aps_color) && !FormatHelper.hasContent(apssize)
					&& !FormatHelper.hasContent(sapgrid)) {
				// Make all the existing SKU_SIZE's of a SP inactive
				setAllSkuSizesToInactive(allSKUs, prod, seasonObj, productSizeCategoryObj);
				HBISPBomUtil.debug("Made all SKU_Sizes inactive for the product [" + prod.getName() + "]");

				isAllInactive = true;
			}
		}
		// If all SKU-Size are not inactive and updation of active sku-sizes
		// required then enters below loop
		if (!isAllInactive) {
			// Get sku -color code map
			HashMap<String, String> skucolorMap = getSKUColorCodeMap(allSKUs);

			// Get SkuSize Attribute Rows for sap key
			HashMap<String, Row> skuSizeAttrColFromData = getSkuSizeAttrColFromDataFile(prod, sizeCategoryBO,
					skucolorMap, rows);

			// Set all Sku Sizes to inactive for a product
			// HBISPBomUtil.debug("ERP Material Type " +
			// prod.getValue("hbiErpMaterialType"));

			HashMap<String, SKUSize> skuSizeObjCol = setAllSkuSizesToInactive(allSKUs, prod, seasonObj,
					productSizeCategoryObj);

			// Get SkuSize Object Collection
			// HashMap<String, SKUSize> skuSizeObjCol =
			// getSkuSizeObjCol(allSKUs,prod, seasonObj,
			// productSizeCategoryObj);

			// Set Sku Size Attributes and set the updated ones to active
			setSKUSizeAttributes(skuSizeAttrColFromData, skuSizeObjCol);
		}
	}

	/**
	 * @param allSKUs
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static HashMap<String, String> getSKUColorCodeMap(Collection allSKUs) throws WTException {
		HashMap<String, String> map = new HashMap();
		Iterator itr = allSKUs.iterator();
		while (itr.hasNext()) {
			LCSSKU sku = (LCSSKU) itr.next();
			LCSColor color = (LCSColor) sku.getValue("color");
			if (color != null && FormatHelper.hasContent((String) color.getValue("hbiColorwayCodeNew"))) {
				map.put((String) color.getValue("hbiColorwayCodeNew"), (String) sku.getValue("skuName"));
			} else {
				HBISPBomUtil.debug("Found the colorway on a product which not having APSColorCode");
			}
		}
		return map;
	}

	/**
	 * @param skuSizeAttrColFromData
	 * @param skuSizeObjCol
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("rawtypes")
	private static void setSKUSizeAttributes(HashMap<String, Row> skuSizeAttrColFromData,
			HashMap<String, SKUSize> skuSizeObjCol) throws WTException, WTPropertyVetoException {
		DecimalFormat formatter = new DecimalFormat("#0.00000");
		long start_Validation = System.currentTimeMillis();
		HBISPBomUtil
				.debug("### START HBIColorwaySizeLoader.setSKUSizeAttributes(HashMap<String, Row> skuSizeAttrColFromData,\r\n"
						+ "			HashMap<String, SKUSize> skuSizeObjCol) ###");
		String externalColor = "";
		String externalColorDesc = "";
		String skuDesc = "";
		String externalSize = "";
		String externalStyle = "";
		if (!skuSizeObjCol.isEmpty()) {
			Iterator dataKeyItr = skuSizeAttrColFromData.keySet().iterator();
			while (dataKeyItr.hasNext()) {
				String dataKey = (String) dataKeyItr.next();
				HBISPBomUtil.debug("Data Key " + dataKey);
				Row dataRow = skuSizeAttrColFromData.get(dataKey);
				externalColor = HBISPBomUtil.getCellValue(dataRow, 10);
				externalColorDesc = HBISPBomUtil.getCellValue(dataRow, 11);
				skuDesc = HBISPBomUtil.getCellValue(dataRow, 12);
				externalSize = HBISPBomUtil.getCellValue(dataRow, 13);
				externalStyle = HBISPBomUtil.getCellValue(dataRow, 14);
				HBISPBomUtil.debug("#### Attribute Values externalColor " + externalColor + "\\n" + "externalColorDesc "
						+ externalColorDesc + "\n" + "skuDesc " + skuDesc + "\n" + "externalSize" + externalSize + "\n"
						+ "externalStyle " + externalStyle + "\n");

				SKUSize skusizeObj = skuSizeObjCol.get(dataKey);
				if (skusizeObj != null) {
					
					//LCSLogic.persist(skusizeObj, true);
					if (VersionHelper.isCheckedOut(skusizeObj)) {
						//skusizeObj.setValue("HbiAlreadySent", "hbiNo");
						skusizeObj.setValue("HbiAlreadySent", "hbiYes");
						
						skusizeObj.setValue("hbiZZDColor", externalColor);
						skusizeObj.setValue("hbiZZDColorDesc", externalColorDesc);
						skusizeObj.setValue("hbiZZDSKUDesc", skuDesc);
						skusizeObj.setValue("hbiZZDSize", externalSize);
						skusizeObj.setValue("hbiZZDStyle", externalStyle);
						skusizeObj.setActive(true);
						LCSLogic.persist((SKUSize) skusizeObj, true);
					}else{
						skusizeObj =VersionHelper.checkout(skusizeObj);
						//skusizeObj.setValue("HbiAlreadySent", "hbiNo");
						skusizeObj.setValue("HbiAlreadySent", "hbiYes");
						skusizeObj.setValue("hbiZZDColor", externalColor);
						skusizeObj.setValue("hbiZZDColorDesc", externalColorDesc);
						skusizeObj.setValue("hbiZZDSKUDesc", skuDesc);
						skusizeObj.setValue("hbiZZDSize", externalSize);
						skusizeObj.setValue("hbiZZDStyle", externalStyle);
						skusizeObj.setActive(true);
						VersionHelper.checkin(skusizeObj);
					}
					HBISPBomUtil.debug("Attributes updated for Colorway-Size >> " + dataKey);
				} else {
					throw new WTException("SKU-SIZE :: [" + dataKey + "], Present in extract but not found on Product");
				}

			}
		}

		long end_Vaidation = System.currentTimeMillis();
		HBISPBomUtil.debug("Attribute Updation completed in ["
				+ formatter.format((end_Vaidation - start_Validation) / 1000d) + "]");
		HBISPBomUtil
				.debug("### END HBIColorwaySizeLoader.setSKUSizeAttributes(HashMap<String, Row> skuSizeAttrColFromData,\r\n"
						+ "			HashMap<String, SKUSize> skuSizeObjCol) ###");

	}

	/**
	 * @param allSKUs
	 * @param prod
	 * @param seasonObj
	 * @param productSizeCategoryObj
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private static HashMap<String, SKUSize> getSkuSizeObjCol(Collection allSKUs, LCSProduct prod, LCSSeason seasonObj,
			ProductSizeCategory productSizeCategoryObj) throws WTException, WTPropertyVetoException {
		HBISPBomUtil
				.debug("### START HBIColorwaySizeLoader.getSkuSizeObjCol(Collection allSKUs, LCSProduct prod, LCSSeason seasonObj,\r\n"
						+ "			ProductSizeCategory productSizeCategoryObj) ###");
		long start_Validation = System.currentTimeMillis();
		boolean includeInActive = true;
		HashMap<String, SKUSize> skuSizesObjCol = new HashMap<String, SKUSize>();
		FlexType skuSizeType = prod.getFlexType().getReferencedFlexType("SKU_SIZE_TYPE_ID");

		Collection skuSizes = new ArrayList();
		// to get all SkuSizes made inactive - this should give all SkuSizes for
		// the Product & Season
		skuSizes = new SKUSizeQuery().findViewableSKUSizesForPSC(new HashMap(), new Vector(), skuSizeType,
				productSizeCategoryObj, seasonObj, null, null, null, null, true, false, false, false, allSKUs)
				.getResults();
		HBISPBomUtil.debug("inactive sku sizes collection " + skuSizes);
		HBISPBomUtil.debug("inactive sku sizes count " + skuSizes.size());
		if (!skuSizes.isEmpty()) {
			Iterator skusizeItr = skuSizes.iterator();
			while (skusizeItr.hasNext()) {
				FlexObject skuSizeFO = (FlexObject) skusizeItr.next();
				System.out.println("skuSizeFO>>>>>>>>>>>>>>>>>>>>>>"+skuSizeFO);
				SKUSize skusizeObj = (SKUSize) LCSQuery
						.findObjectById("OR:com.lcs.wc.skusize.SKUSize:" + skuSizeFO.getString("SKUSIZE.IDA2A2"));
				String colorName = skuSizeFO.getString("LCSSKU.PTC_STR_1TYPEINFOLCSSKU");
				String size = skuSizeFO.getString("SKUSIZEMASTER.SIZEVALUE");
				skuSizesObjCol.put(colorName + "-" + size, skusizeObj);
			}
		} else {
			//Update PSD to update SKU SIZE
			LCSLogic.persist(productSizeCategoryObj,true);
			skuSizes = new SKUSizeQuery().findViewableSKUSizesForPSC(new HashMap(), new Vector(), skuSizeType,
					productSizeCategoryObj, seasonObj, null, null, null, null, true, false, false, false, allSKUs)
					.getResults();
			HBISPBomUtil.debug("2 Inactive sku sizes collection " + skuSizes);
			HBISPBomUtil.debug("2 Inactive sku sizes count " + skuSizes.size());
			if (!skuSizes.isEmpty()) {
				Iterator skusizeItr = skuSizes.iterator();
				while (skusizeItr.hasNext()) {
					FlexObject skuSizeFO = (FlexObject) skusizeItr.next();
					SKUSize skusizeObj = (SKUSize) LCSQuery
							.findObjectById("OR:com.lcs.wc.skusize.SKUSize:" + skuSizeFO.getString("SKUSIZE.IDA2A2"));
					String colorName = skuSizeFO.getString("LCSSKU.PTC_STR_1TYPEINFOLCSSKU");
					String size = skuSizeFO.getString("SKUSIZEMASTER.SIZEVALUE");
					skuSizesObjCol.put(colorName + "-" + size, skusizeObj);
				}
			}else{
				throw new WTException(
						"No Sku Sizes available for the Product " + prod.getName() + " and season " + seasonObj.getName());
			}
			
			
		}

		long end_Validation = System.currentTimeMillis();
		HBISPBomUtil
				.debug("### END HBIColorwaySizeLoader.getSkuSizeObjCol(Collection allSKUs, LCSProduct prod, LCSSeason seasonObj,\r\n"
						+ "			ProductSizeCategory productSizeCategoryObj) ###");
		return skuSizesObjCol;
	}

	/*
	 * To set all SkuSizes to inactive
	 * 
	 * @param allSKUs
	 * 
	 * @param prod
	 * 
	 * @param seasonObj
	 * 
	 * @param productSizeCategoryObj
	 * 
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap<String, SKUSize> setAllSkuSizesToInactive(Collection allSKUs, LCSProduct prod,
			LCSSeason seasonObj, ProductSizeCategory productSizeCategoryObj)
			throws WTException, WTPropertyVetoException {
		HBISPBomUtil
				.debug("### START HBIColorwaySizeLoader.setAllSkuSizesToInactive(Collection allSKUs, LCSProduct prod, LCSSeason seasonObj,\r\n"
						+ "			ProductSizeCategory productSizeCategoryObj) ###");
		HashMap<String, SKUSize> skuSizesObjCol = new HashMap();
		boolean iNCLUDE_INACTIVE = true;
		DecimalFormat formatter = new DecimalFormat("#0.00000");
		long start = System.currentTimeMillis();

		FlexType skuSizeType = prod.getFlexType().getReferencedFlexType("SKU_SIZE_TYPE_ID");
		Collection skuSizes = new SKUSizeQuery()
				.findViewableSKUSizesForPSC(new HashMap(), new Vector(), skuSizeType, productSizeCategoryObj, seasonObj,
						null, null, null, null, iNCLUDE_INACTIVE, false, false, false, allSKUs)
				.getResults();
		HBISPBomUtil.debug("ALL Sku Size count " + skuSizes.size());
		if (skuSizes.size() == 0) {
			throw new WTException("No SKU Sizes available for the product [ " + prod.getName() + " ] !!!!");
		}
		Iterator skusizeitr = skuSizes.iterator();
		while (skusizeitr.hasNext()) {
			FlexObject skuSizeObj = (FlexObject) skusizeitr.next();
			SKUSize skusizeObj = (SKUSize) LCSQuery
					.findObjectById("OR:com.lcs.wc.skusize.SKUSize:" + skuSizeObj.getString("SKUSIZE.IDA2A2"));
			System.out.println("skuSizeObj>>>>>>>>>>>>>>>>>"+skuSizeObj);
			if (SET_SKU_SIZE_AS_INACTIVE) {
				
				if (VersionHelper.isCheckedOut(skusizeObj)) {
					skusizeObj.setActive(false);
					LCSLogic.persist((SKUSize) skusizeObj, true);
				}else{
					skusizeObj = VersionHelper.checkout(skusizeObj);
					skusizeObj.setActive(false);
					VersionHelper.checkin(skusizeObj);
				}
			} else {
				HBISPBomUtil.debug("Ignore setting SKU-SIZE status as In-active");
			}
			String colorName = skuSizeObj.getString("LCSSKU.PTC_STR_1TYPEINFOLCSSKU");
			String size = skuSizeObj.getString("SKUSIZEMASTER.SIZEVALUE");
			skuSizesObjCol.put(colorName + "-" + size, skusizeObj);

		}
		long end = System.currentTimeMillis();
		HBISPBomUtil.debug("Inactivation of Product [" + prod.getName() + "], SKU-Sizes completed in ["
				+ formatter.format((end - start) / 1000d) + "]");

		HBISPBomUtil
				.debug("### END HBIColorwaySizeLoader.setAllSkuSizesToInactive(Collection allSKUs, LCSProduct prod, LCSSeason seasonObj,\r\n"
						+ "			ProductSizeCategory productSizeCategoryObj) ###");
		return skuSizesObjCol;
	}

	/**
	 * @param prod
	 * @param sizeCategoryBO
	 * @param skucolorMap
	 * @param rows
	 * @return
	 * @throws WTException
	 */

	@SuppressWarnings("rawtypes")
	private static HashMap<String, Row> getSkuSizeAttrColFromDataFile(LCSProduct prod,
			LCSLifecycleManaged sizeCategoryBO, HashMap<String, String> skucolorMap, Collection<Row> rows)
			throws WTException {
		HBISPBomUtil.debug(
				"### START HBIColorwaySizeLoader.getSkuSizeAttrColFromDataFile(Collection<Row> skuSizeRows, LCSProduct sp) ###");
		HashMap<String, Row> skuSizesAttCol = new HashMap<String, Row>();
		DecimalFormat formatter = new DecimalFormat("#0.00000");
		long start = System.currentTimeMillis();
		Iterator skuRowItr = rows.iterator();
		while (skuRowItr.hasNext()) {
			Row skuSizeRow = (Row) skuRowItr.next();
			String aps_color = HBISPBomUtil.getCellValue(skuSizeRow, 7);

			// Get ApsColorName From Aps_color Code
			String colorname = skucolorMap.get(aps_color);
			if (!FormatHelper.hasContent(colorname)) {
				throw new WTException("No SKU found in FlexPlM for APS Color Code Value :: [" + aps_color + "]");
			}
			String apssize = HBISPBomUtil.getCellValue(skuSizeRow, 8);
			String sapgrid = HBISPBomUtil.getCellValue(skuSizeRow, 9);
			// Get PLM Size literal from Selling Size category, ApsSize and Sap
			// grid
			String plmSizeLiteral = getPlmSizeLiteral(sizeCategoryBO, apssize, sapgrid);
			skuSizesAttCol.put(colorname + "-" + plmSizeLiteral, skuSizeRow);
		}
		long end = System.currentTimeMillis();
		HBISPBomUtil.debug("Creation of Sku-Size Map completed in [" + formatter.format((end - start) / 1000d) + "]");

		HBISPBomUtil.debug(
				"### END HBIColorwaySizeLoader.getSkuSizeAttrColFromDataFile(Collection<Row> skuSizeRows, LCSProduct sp) ###");
		return skuSizesAttCol;
	}

	/**
	 * @param sizeCategoryBO
	 * @param apssize
	 * @param sapgrid
	 * @return
	 * @throws WTException
	 */
	private static String getPlmSizeLiteral(LCSLifecycleManaged sizeCategoryBO, String apssize, String sapgrid)
			throws WTException {
		HBISPBomUtil.debug(
				"### START HBIColorwaySizeLoader.getPlmSizeLiteral(String sizecategory, String apssize, String sapgrid) ###");
		DecimalFormat formatter = new DecimalFormat("#0.00000");
		long start = System.currentTimeMillis();
		LCSLifecycleManaged businessObject = null;
		String sizeCatIda2a2 = FormatHelper.getNumericObjectIdFromObject(sizeCategoryBO);
		String plmSizeLiteral = "";
		String businessObjectTypePath = "Business Object\\Automation Support Tables\\Size Xref";
		FlexType boFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjectTypePath);
		String hbiAPSSizeCategory_DBColumn = boFlexTypeObj.getAttribute("hbiAPSSizeCategory").getColumnDescriptorName();//getVariableName();
		String hbiAPSSizeCode_DBColumn = boFlexTypeObj.getAttribute("hbiAPSSizeCode").getColumnDescriptorName();//getVariableName();
		String hbiSAPGridSize_DBColumn = boFlexTypeObj.getAttribute("hbiSAPGridSize").getColumnDescriptorName();//getVariableName();
		String typeIdPath = boFlexTypeObj.getTypeIdPath();

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
				apssize, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, hbiSAPGridSize_DBColumn),
				sapgrid, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeIdPath"), "?", "="),
				typeIdPath);

		// Get SearchResults instance from the given PreparedQueryStatement
		HBISPBomUtil.debug("Stmt ::" + statement.toString());
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if (results != null && results.getResultsFound() == 1) {
			FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
			businessObject = (LCSLifecycleManaged) LCSQuery.findObjectById(
					"OR:com.lcs.wc.foundation.LCSLifecycleManaged:" + flexObj.getString("LCSLifecycleManaged.IDA2A2"));
			plmSizeLiteral = (String) businessObject.getValue("hbiPLMSizeLiteral");
			HBISPBomUtil
					.debug("apssize [" + apssize + "], sapgrid [" + sapgrid + "], plm Size Literal " + plmSizeLiteral);

		} else {
			throw new WTException("Either None, or More than one PLMSizeLiteral found in FlexPlM " + "for [ApsSize :: "
					+ apssize + "], [sapgrid ::" + sapgrid + "], [APSsizecategory ::" + sizeCategoryBO.getName() + "]");
		}
		long end = System.currentTimeMillis();

		HBISPBomUtil
				.debug("Fetching PLMsizeLiteral of PSD completed in [" + formatter.format((end - start) / 1000d) + "]");
		HBISPBomUtil.debug(
				"### END HBIColorwaySizeLoader.getPlmSizeLiteral(String sizecategory, String apssize, String sapgrid) ###");
		return plmSizeLiteral;
	}

	/**
	 * @param prod
	 * @param psdName
	 * @param season
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	private static ProductSizeCategory getValidPSDForProduct(LCSProduct prod, String psdName) throws WTException {

		// Get PSD's for the Product
		SearchResults results = new SizingQuery().findPSDByProductAndSeason(prod);
		HBISPBomUtil.debug(
				"Total PSDs Fetched [" + results.getResults().size() + "], for Product [" + prod.getName() + "]");
		if (results != null && results.getResultsFound() == 1) {
			Iterator scItr = results.getResults().iterator();
			FlexObject flexObj = (FlexObject) scItr.next();
			System.out.println("flexObj>>>>>>>>>>>>>>>>>>>>>>"+flexObj);
			if (FormatHelper.hasContent(psdName)) {
				ProductSizeCategory productSizeCategoryObj = (ProductSizeCategory) LCSQuery.findObjectById(
						"OR:com.lcs.wc.sizing.ProductSizeCategory:" + flexObj.getString("ProductSizeCategory.IDA2A2"));
				productSizeCategoryObj = (ProductSizeCategory) VersionHelper.latestIterationOf(productSizeCategoryObj);

				HBISPBomUtil.debug(
						"PSD found  [" + productSizeCategoryObj.getName() + "], on product [" + prod.getName() + "]");

				if (!FormatHelper.hasContent(flexObj.getString("LCSSEASON.PTC_STR_1TYPEINFOLCSSEASON"))) {
					throw new WTException("!!!! PSD [ " + productSizeCategoryObj.getName() + " ] for the product [ "
							+ prod.getName() + " ] not linked to season!!!!");
				}
				return productSizeCategoryObj;
				/*if (psdName.equals(productSizeCategoryObj.getName())) {
					return productSizeCategoryObj;
				} else {
					throw new WTException("!!!! PSD is not matching with the data file !!!!");

				}*/

			} else {
				throw new WTException("!!!! No PSD mentioned for Product in the data file !!!!");
			}
		} else {
			throw new WTException(
					"!!!!Either No Product Size definition or More than one Product Size Definition for the product!!! "
							+ prod.getName());
		}

	}

	/**
	 * @param fileName
	 * @throws WTException
	 * @throws IOException
	 *             A::SAP_KEY B::SELLING_STYLE C::PRODUCT_DESCRIPTION
	 *             D::ATTRIBUTE E::PACK_QTY F::PROD_SIZE_DEFN G::SIZE_CATEGORY
	 *             H::APS_COLOR I::APS_SIZE J::SAP_SIZE K::EXTERNAL_COLOR
	 *             L::EXTERNAL_COLOR_DESC M::SKU_DESC N::EXTERNAL_SIZE
	 *             O::EXTERNAL_STYLE
	 */
	private static void validateSkuSize(String input_filename) throws WTException, IOException {

		HBISPBomUtil.debug("##### START validating the Sku-Size input File [" + input_filename + "] #####");
		String loader_input_file = PATH_SKUSIZE_IN + input_filename + SKUSIZE_LOADER_IN_FILETYPE;
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(loader_input_file));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		if (!HBISPBomUtil.getCellValue(headerRow, 0).equals("SAP_KEY")
				|| !HBISPBomUtil.getCellValue(headerRow, 1).equals("SELLING_STYLE")
				|| !HBISPBomUtil.getCellValue(headerRow, 2).equals("PRODUCT_DESCRIPTION")
				|| !HBISPBomUtil.getCellValue(headerRow, 3).equals("ATTRIBUTE")
				|| !HBISPBomUtil.getCellValue(headerRow, 4).equals("PACK_QTY")
				|| !HBISPBomUtil.getCellValue(headerRow, 5).equals("PROD_SIZE_DEFN")
				|| !HBISPBomUtil.getCellValue(headerRow, 6).equals("SIZE_CATEGORY")
				|| !HBISPBomUtil.getCellValue(headerRow, 7).equals("APS_COLOR")
				|| !HBISPBomUtil.getCellValue(headerRow, 8).equals("APS_SIZE")
				|| !HBISPBomUtil.getCellValue(headerRow, 9).equals("SAP_SIZE")
				|| !HBISPBomUtil.getCellValue(headerRow, 10).equals("EXTERNAL_COLOR")
				|| !HBISPBomUtil.getCellValue(headerRow, 11).equals("EXTERNAL_COLOR_DESC")
				|| !HBISPBomUtil.getCellValue(headerRow, 12).equals("SKU_DESC")
				|| !HBISPBomUtil.getCellValue(headerRow, 13).equals("EXTERNAL_SIZE")
				|| !HBISPBomUtil.getCellValue(headerRow, 14).equals("EXTERNAL_STYLE")) {
			file.close();
			throw new WTException("!!!! Validation failed in " + input_filename
					+ " not having header rows as [	A ::SAP_KEY| B::SELLING_STYLE| C::PRODUCT_DESCRIPTION| D::ATTRIBUTE| E::PACK_QTY "
					+ "| F::PROD_SIZE_DEFN| G::SIZE_CATEGORY	"
					+ "| H::APS_COLOR| I::APS_SIZE| J::SAP_SIZE| K::EXTERNAL_COLOR| L::EXTERNAL_COLOR_DESC| M::SKU_DESC	"
					+ "| N::EXTERNAL_SIZE| O::EXTERNAL_STYLE]");
		}
		file.close();
		HBISPBomUtil.debug("###### END validating the Sku-Size input File [" + loader_input_file + "] #########");

	}

}

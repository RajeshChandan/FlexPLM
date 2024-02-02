

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
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
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

public class HBIDeactivateSKUSize implements RemoteAccess {
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	public static final String PATH_SKUSIZE_IN = "D:\\ptc\\Windchill_10.1\\Windchill\\loadFiles\\lcsLoadFiles\\spColorwaySizeLoader\\";
	public static final String SKUSIZE_LOADER_IN_FILETYPE = ".xlsx";
	private static final boolean SET_SKU_SIZE_AS_INACTIVE = true;
	static Date objDate = null;
	static DateFormat fullFormat = null;
	public static final String SELLING_PRODUCT = LCSProperties.get("com.hbi.wc.load.sploader.product.type","Product\\BASIC CUT & SEW - SELLING_1");


	/**
	 * Default executable main method of the class HBIColorwaySizeLoader
	 * 
	 * @param args
	 *            - String[]
	 */

	public static void main(String[] args) {
		HBISPBomUtil.debug("### START HBIDeactivateSKUSize.main() ###");

		try {
			if (args.length != 1) {
				HBISPBomUtil.debug("windchill com.hbi.wc.load.sploader.HBIDeactivateSKUSize <fileName>");
			}

			remoteMethodServer = RemoteMethodServer.getDefault();
			remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

			Class<?> argTypes[] = { String.class };
			String argValues[] = { args[0] };
			remoteMethodServer.invoke("deactivateSKUSize", "com.hbi.wc.load.sploader.HBIDeactivateSKUSize",
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
	public static void deactivateSKUSize(String skuSize_inFiles)
			throws WTException, WTPropertyVetoException, IOException {
		HBISPBomUtil.debug("### START HBIColorwaySizeLoader.processProductSkuSizeData(String skuSize_inFiles) ###");
		String[] skuSize_input_files = skuSize_inFiles.split(",");
		NumberFormat formatter = new DecimalFormat("#0.00000");
		for (String skuSize_input_file : skuSize_input_files) {

			try {
				String skuSize_loader_fileName = PATH_SKUSIZE_IN + skuSize_input_file + SKUSIZE_LOADER_IN_FILETYPE;



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
		String key_sap = HBISPBomUtil.getCellValue(row, 0);
		

		// Below product search is based on SAP_KEY for future requirements and
		// not required now
		// LCSProduct prod = HBISPBomUtil.getProductBySapKey(sap_key);

		// get ida2a2 for attribution code
		
		LCSProduct prod = getProductByStyleNo(key_sap);

		// Get Season for the Product
		LCSSeason seasonObj = HBISPBomUtil.getSPSeason(prod);

		// Get PSD of the Product
		long start_Validation_psd = System.currentTimeMillis();
		ProductSizeCategory productSizeCategoryObj = getValidPSDForProduct(prod);
		long end_Validation_psd = System.currentTimeMillis();


		// Update all sku-sizes of a product PSD.
		updateSkuSizes(prod, seasonObj, productSizeCategoryObj, rows);

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
			 Collection<Row> rows) throws WTException, WTPropertyVetoException {
		boolean isAllInactive = false;
		// Get all skus for the Produt
		Collection allSKUs = new ProductHeaderQuery().findSKUs(prod, null, null, true, false);

		if (rows.size() == 1) {
		
			// If only making all sku-size inactive required, then the below
		
				// Make all the existing SKU_SIZE's of a SP inactive
				setAllSkuSizesToInactive(allSKUs, prod, seasonObj, productSizeCategoryObj);

			
		}
		// If all SKU-Size are not inactive and updation of active sku-sizes
	
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
	private static void  setAllSkuSizesToInactive(Collection allSKUs, LCSProduct prod,
			LCSSeason seasonObj, ProductSizeCategory productSizeCategoryObj)
			throws WTException, WTPropertyVetoException {

		HashMap<String, SKUSize> skuSizesObjCol = new HashMap();
		boolean iNCLUDE_INACTIVE = true;
		DecimalFormat formatter = new DecimalFormat("#0.00000");
		long start = System.currentTimeMillis();

		FlexType skuSizeType = prod.getFlexType().getReferencedFlexType("SKU_SIZE_TYPE_ID");
		Collection skuSizes = new SKUSizeQuery()
				.findViewableSKUSizesForPSC(new HashMap(), new Vector(), skuSizeType, productSizeCategoryObj, seasonObj,
						null, null, null, null, iNCLUDE_INACTIVE, false, false, false, allSKUs)
				.getResults();
		

	
		Iterator skusizeitr = skuSizes.iterator();
		while (skusizeitr.hasNext()) {
			FlexObject skuSizeObj = (FlexObject) skusizeitr.next();
			SKUSize skusizeObj = (SKUSize) LCSQuery
					.findObjectById("OR:com.lcs.wc.skusize.SKUSize:" + skuSizeObj.getString("SKUSIZE.IDA2A2"));

				skusizeObj.setActive(false);
				LCSLogic.persist((SKUSize) skusizeObj, true);
			
		
		}
		
	}

	
	

	/**
	 * @param prod
	 * @param psdName
	 * @param season
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	private static ProductSizeCategory getValidPSDForProduct(LCSProduct prod) throws WTException {

		// Get PSD's for the Product
		SearchResults results = new SizingQuery().findPSDByProductAndSeason(prod);
		ProductSizeCategory productSizeCategoryObj=null;
		HBISPBomUtil.debug(
				"Total PSDs Fetched [" + results.getResults().size() + "], for Product [" + prod.getName() + "]");
		if (results != null && results.getResultsFound() == 1) {
			Iterator scItr = results.getResults().iterator();
			FlexObject flexObj = (FlexObject) scItr.next();

		
				 productSizeCategoryObj = (ProductSizeCategory) LCSQuery.findObjectById(
						"OR:com.lcs.wc.sizing.ProductSizeCategory:" + flexObj.getString("ProductSizeCategory.IDA2A2"));
				productSizeCategoryObj = (ProductSizeCategory) VersionHelper.latestIterationOf(productSizeCategoryObj);

				
				

		}
		return productSizeCategoryObj;


	}

	/**
	 * @param hbiErpAttributionCode
	 * @param hbiSellingStyleNumber
	 * @param hbiDescription
	 * @param hbiAPSPackQuantity
	 * @return
	 * @throws WTException
	 */
	public static LCSProduct getProductByStyleNo( String SAP_KEY) throws WTException {
		FlexType prdType = FlexTypeCache.getFlexTypeFromPath(SELLING_PRODUCT);
		String sapKey_DB_Col = prdType.getAttribute("hbiSAPKey").getColumnDescriptorName();
		
		LCSProduct product = null;
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		stmt.appendFromTable("prodarev", "product");
		stmt.appendSelectColumn("product", "ida2a2");
		stmt.appendOpenParen();
		stmt.appendCriteria(
				new Criteria("product", sapKey_DB_Col, SAP_KEY, Criteria.EQUALS));
		stmt.appendAnd();
	
		stmt.appendCriteria(new Criteria("product", "flexTypeIdPath", prdType.getTypeIdPath(), Criteria.EQUALS));
		stmt.appendClosedParen();

		Collection<FlexObject> output = new ArrayList();
		output = LCSQuery.runDirectQuery(stmt).getResults();
		if (output.size() == 1) {
			FlexObject obj = (FlexObject) output.iterator().next();
			product = (LCSProduct) LCSQuery
					.findObjectById("OR:com.lcs.wc.product.LCSProduct:" + obj.getData("PRODUCT.IDA2A2"));
			
		}
		return product; 

	}

}

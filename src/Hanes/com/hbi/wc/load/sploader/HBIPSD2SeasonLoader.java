package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sizing.ProdSizeCategoryToSeason;
import com.lcs.wc.sizing.ProdSizeCategoryToSeasonClientModel;
import com.lcs.wc.sizing.ProdSizeCategoryToSeasonMaster;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sizing.ProductSizeCategoryClientModel;
import com.lcs.wc.sizing.ProductSizeCategoryMaster;
import com.lcs.wc.sizing.ProductSizingLogic;
import com.lcs.wc.sizing.SizingHelper;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.skusize.SKUSizeUtility;
import com.lcs.wc.util.AttributeValueSetter;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

/**
 * 
 * @author Manoj
 * @since 02/6/2019
 * @program This program is used to load Product Size Categories to Selling
 *          Products and Selling product season. It takes four input attribute values to search the product
 *          and update or create a new Product Size Category.
 *
 */
public class HBIPSD2SeasonLoader implements RemoteAccess {
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;

	public static final String PATH_SP_PSD_IN = "D:\\ptc\\Windchill_11.2\\Windchill\\loadFiles\\lcsLoadFiles\\spPSDLoader\\";
	public static final String SP_PSD_LOADER_IN_FILETYPE = ".xlsx";
	private static final String MOA_DELIM = "\\|~\\*~\\|";//|~*~|
	protected static ProductSizingLogic PRODUCTSIZING_LOGIC = new ProductSizingLogic();
	public static void main(String[] args) {
		HBISPBomUtil.debug("### START HBIPSD2SeasonLoader.main() ###");

		try {
			if (args.length != 1) {
				System.out.println("windchill com.hbi.wc.load.sploader.HBIPSD2SeasonLoader <fileName>");
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
			String argValues[] = { args[0] };
			remoteMethodServer.invoke("processPSD", "com.hbi.wc.load.sploader.HBIPSD2SeasonLoader", null,
					argTypes, argValues);
			System.exit(0);
		} catch (Exception exp) {
			exp.printStackTrace();
			System.exit(1);
		}

		HBISPBomUtil.debug("### END HBIPSD2SeasonLoader.main() ###");
	}

	/**
	 * @param product
	 * @param season
	 * @param sizeDefTemplateName
	 * @param size1Vals
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	protected static boolean loadPSD(LCSProduct product, LCSSeason season, String sizeDefTemplateName, String size1Vals)
			throws WTPropertyVetoException, WTException {

		if (!FormatHelper.hasContent(size1Vals)) {
			throw new WTException("\n#ERROR: No size 1 found in the loader file");
		} else {
			ProductSizeCategory sizeDefTemplate = findSizeDefTemplate(sizeDefTemplateName, size1Vals);

			if (sizeDefTemplate != null) {
				String baseSize1Val = getbaseSize1Val(sizeDefTemplate, size1Vals);
				HBISPBomUtil.debug("\n#INFO::   'Size 1 Base Size' :: " + baseSize1Val);

				// Check if product is having already PSD created with
				// SizeDefination template
				ProductSizeCategory psdTemplate = findPSDTemplate(product, sizeDefTemplate);
				int psdCount = SizingQuery.findProductSizeCategoriesForProduct(product).getResultsFound();

				if (psdTemplate == null && psdCount == 0) {
					HBISPBomUtil.debug("\n#INFO: Creating new PSD ...");
					return createPSD(product, season, sizeDefTemplate, size1Vals, baseSize1Val);
				} else if (psdTemplate != null && psdCount == 1) {
					HBISPBomUtil.debug("\n#INFO:Updating ...Existing PSD found :: " + psdTemplate.getName());
					return updatePSD(product, season, psdTemplate, size1Vals, baseSize1Val);
				} else {
					throw new WTException(
							"ERROR !! Not more than one PSD allowed for a product :: [" + product.getName() + "]");
				}

			} else {
				throw new WTException("\n#ERROR:No Size Definition Template Found!!!");
			}

		}
	}

	/**
	 * @param product
	 * @param season
	 * @param sizeDefTemplate
	 * @param size1Vals
	 * @param baseSize1Val
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static boolean createPSD(LCSProduct product, LCSSeason season, ProductSizeCategory sizeDefTemplate,
			String size1Vals, String baseSize1Val) throws WTException, WTPropertyVetoException {
		HBISPBomUtil.debug("Started creating product new PSD");
		ProductSizeCategory psc = ProductSizeCategory.newProductSizeCategory();
		FlexType type = FlexTypeCache.getFlexTypeRoot("Size Definition");
		FlexType compatibleType = product.getFlexType().getReferencedFlexType("SIZING_TYPE_ID");
		if (compatibleType == null) {
			throw new WTException("\n#ERROR: The Product's type setting for Size Definition type is not defined\n");
		} else if (!compatibleType.isAssignableFrom(type)) {
			throw new WTException("\n#ERROR: compatibleType Issue occured");
		} else {
			psc.setSizeCategoryType("INSTANCE");
			psc.setSizeCategory(sizeDefTemplate.getSizeCategory());
			psc.setFullSizeRange(sizeDefTemplate.getFullSizeRange());
			psc.setSizeValues(size1Vals);
			psc.setBaseSize(baseSize1Val);
			psc.setFlexType(type);
		//	psc.setProductMaster((WTPartMaster) product.getMaster());
			psc.setProductMaster((LCSPartMaster) product.getMaster());
			HBISPBomUtil.debug("\nsizeDefTemplate.getMaster(): " + sizeDefTemplate.getMaster());
			psc.setSizeRange((ProductSizeCategoryMaster) sizeDefTemplate.getMaster());

			// below api saves the PSD
			psc = SizingHelper.service.saveProductSizeCategory(psc);

			// Link the PSD to a season
			createPSDSeason(product, season, psc);

		}
		HBISPBomUtil.debug("Completed creating product new PSD [" + product.getName() + "]");
		return true;
	}

	/**
	 * @param sizeDefTemplate
	 * @param size1Vals
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String getbaseSize1Val(ProductSizeCategory sizeDefTemplate, String size1Vals) {
		String baseSize1Val = sizeDefTemplate.getBaseSize();
		HBISPBomUtil.debug("\n#INFO: sizeDefTemplate's baseSize1Val " + baseSize1Val);

		String sizeArray[] = size1Vals.split(MOA_DELIM);
		Collection sizeList = Arrays.asList(sizeArray);
		HBISPBomUtil.debug("\n#INFO: size1 value List to be loaded :: " + sizeList);
		if (sizeList.contains(baseSize1Val)) {
			return baseSize1Val;
		} else {
			baseSize1Val = sizeArray[0];
		}
		return baseSize1Val;
	}

	/**
	 * @param product
	 * @param season
	 * @param psdTemplate
	 * @param size1Vals
	 * @param baseSize1Val
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	private static boolean updatePSD(LCSProduct product, LCSSeason season, ProductSizeCategory psdTemplate,
			String size1Vals, String baseSize1Val) throws WTPropertyVetoException, WTException {
		HBISPBomUtil.debug("Started updating product PSD");
		ProductSizeCategoryClientModel client = new ProductSizeCategoryClientModel();
		ProdSizeCategoryToSeason psdSeason = new SizingQuery().getProdSizeCategoryToSeason(psdTemplate, season);
		
		client.load(FormatHelper.getObjectId(psdTemplate));
		client.setSizeValues(size1Vals);
		client.setBaseSize(baseSize1Val);
		
		if (psdSeason != null) {
			ProdSizeCategoryToSeasonClientModel psc2SeasonModel = new ProdSizeCategoryToSeasonClientModel();
			psc2SeasonModel.load(FormatHelper.getObjectId(psdSeason));
			psc2SeasonModel.setSizeValues(size1Vals);
			psc2SeasonModel.save(client);	
			
		//updatePSDSeason(product, season, psd, psdSeason);
		} else {
			client.save();
			ProductSizeCategory psd = client.getBusinessObject();
			createPSDSeason(product, season, psd);
		}
		HBISPBomUtil.debug("Completed updating product PSD [" + product.getName() + "]");
		return true;

	}

	/**
	 * @param product
	 * @param season
	 * @param psc
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void createPSDSeason(LCSProduct product, LCSSeason season, ProductSizeCategory psc)
			throws WTException, WTPropertyVetoException {
		HBISPBomUtil.debug("Started creating product season PSD ");

		ProdSizeCategoryToSeason psc2season = ProdSizeCategoryToSeason.newProdSizeCategoryToSeason();

		ProdSizeCategoryToSeasonMaster master = ProdSizeCategoryToSeasonMaster.newProdSizeCategoryToSeasonMaster();
		//master.setSeasonMaster((WTPartMaster) season.getMaster());
		master.setSeasonMaster((LCSSeasonMaster) season.getMaster());
		master.setSizeCategoryMaster((ProductSizeCategoryMaster) psc.getMaster());
		psc2season.setMaster(master);

		psc2season.setFlexType(psc.getFlexType());
		psc2season.setSizeValues(psc.getSizeValues());

		SizingHelper.service.saveProdSizeCategoryToSeason(psc, psc2season);
		HBISPBomUtil.debug("Completed creating product season PSD [" + product.getName() + "]");
	}

	/**
	 * @param product
	 * @param season
	 * @param psd
	 * @param psdSeason
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	private static void updatePSDSeason(LCSProduct product, LCSSeason season, ProductSizeCategory psd,
			ProdSizeCategoryToSeason psdSeason) throws WTPropertyVetoException, WTException {
		HBISPBomUtil.debug("Started updating product season PSD");
		ProductSizeCategoryClientModel productSizeCategoryModel = new ProductSizeCategoryClientModel();
		ProdSizeCategoryToSeasonClientModel psc2SeasonModel = new ProdSizeCategoryToSeasonClientModel();
		psc2SeasonModel.save(productSizeCategoryModel);	
		/*psdSeasonModel.load(FormatHelper.getObjectId((ProdSizeCategoryToSeason)VersionHelper.latestIterationOf(psdSeason)));
		psdSeasonModel.setSizeValues(psd.getSizeValues());
		psdSeasonModel.save();*/
		psc2SeasonModel.load(FormatHelper.getObjectId((ProdSizeCategoryToSeason)VersionHelper.latestIterationOf(psdSeason)));
 	   productSizeCategoryModel.load(FormatHelper.getObjectId((ProductSizeCategory)VersionHelper.latestIterationOf(psc2SeasonModel.getBusinessObject().getSizeCategoryMaster())));
 	  //productSizeCategoryModel.save();
 	   psc2SeasonModel.setSizeValues(productSizeCategoryModel.getSizeValues());
 	   
 	 
		HBISPBomUtil.debug("Completed updating product season PSD [" + product.getName() + "]");
	}

	/**
	 * @param product
	 * @param sizeDefTemplate
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static ProductSizeCategory findPSDTemplate(LCSProduct product, ProductSizeCategory sizeDefTemplate)
			throws WTException {
		ProductSizeCategory psdTemp = null;
		SearchResults psdResults = SizingQuery.findProductSizeCategoriesForProduct(product);
		boolean isPSDUnique = true;
		if (psdResults.getResultsFound() > 0) {
			Collection<FlexObject> psdColl = psdResults.getResults();
			Iterator itr = psdColl.iterator();
			while (itr.hasNext()) {
				FlexObject psd = (FlexObject) itr.next();

				HBISPBomUtil.debug("\n#INFO: PSD OBJECT\n " + psd);
				String psdcatName = psd.getString("SIZECATEGORY.NAME");
				String psdFullSizeRange = psd.getString("FULLSIZERANGE.NAME");
				if (psdcatName.equals(sizeDefTemplate.getSizeCategory().getName())
						&& psdFullSizeRange.equals(sizeDefTemplate.getFullSizeRange().getName())) {
					if (isPSDUnique) {
						psdTemp = (ProductSizeCategory) LCSQuery
								.findObjectById("OR:com.lcs.wc.sizing.ProductSizeCategory:"
										+ psd.getString("PRODUCTSIZECATEGORY.IDA2A2"));
						isPSDUnique = false;
					} else {
						throw new WTException(
								"ERROR !! More than one PSD Template found on the product with same category and Full size range");

					}
				} else {

				}
			}
		} else {
			HBISPBomUtil.debug("\n#INFO: No Existing PSD's found on Product");

			psdTemp = null;
		}
		return psdTemp;
	}

	/**
	 * @param sizeDefinitionTemplate
	 * @param size1Vals
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	private static ProductSizeCategory findSizeDefTemplate(String sizeDefinitionTemplate, String size1Vals)
			throws WTException {
		ProductSizeCategory sizeDefTemp = null;
		Collection<ProductSizeCategory> asrCol = LCSQuery.getObjectsFromResults(
				SizingQuery.findActualSizeRangeData().getResults(), "OR:com.lcs.wc.sizing.ProductSizeCategory:",
				"PRODUCTSIZECATEGORY.IDA2A2");
		HBISPBomUtil.debug("\n#INFO: Total Size defination templates found in FlexPLM Library :: " + asrCol.size());

		sizeDefTemp = findSizeDefTemplateFromLibrary(asrCol, sizeDefinitionTemplate, size1Vals);

		return sizeDefTemp;
	}

	/**
	 * @param asrCol
	 * @param sizeDefinitionTemplate
	 * @param size1Vals
	 * @return
	 * @throws WTException
	 */
	private static ProductSizeCategory findSizeDefTemplateFromLibrary(Collection<ProductSizeCategory> asrCol,
			String sizeDefinitionTemplate, String size1Vals) throws WTException {

		ProductSizeCategory sizeCat = null;
		for (ProductSizeCategory psSzCat : asrCol) {
			String templateName = psSzCat.getName();
			String templateSize1Values = psSzCat.getSizeValues();
			if (FormatHelper.hasContent(templateName) && FormatHelper.hasContent(templateSize1Values)) {
				if (templateName.equals(sizeDefinitionTemplate)) {
					if (isHavingAllsize1Vals(size1Vals, templateSize1Values)) {

						sizeCat = (ProductSizeCategory) psSzCat.duplicate();
						break;
					} else {

						HBISPBomUtil.debug("\n#ERROR: All Size1 values [" + size1Vals
								+ "] not found in size definition templateSize1Values " + templateSize1Values);
						throw new WTException("!!! Exception All Size1 values [" + size1Vals
								+ "] not found in size definition templateSize1Values " + templateSize1Values);
					}
				} else {
					// HBISPBomUtil.debug("Not Matching psc");

				}
			} else {
				// HBISPBomUtil.debug("Missing psc");
			}
		}
		return sizeCat;

	}

	/**
	 * @param size1Vals
	 * @param templateSize1Values
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static boolean isHavingAllsize1Vals(String size1Vals, String templateSize1Values) {
		HBISPBomUtil.debug("\n#INFO: size1Vals to be loaded :: " + size1Vals);
		HBISPBomUtil.debug("\n#INFO: Existing size1Vals on the Template :: " + templateSize1Values);
		String tempSize[] = templateSize1Values.split(MOA_DELIM);
		Collection tempSizeList = Arrays.asList(tempSize);
		String size1ValsArray[] = size1Vals.split(MOA_DELIM);
		Collection size1ValsList = Arrays.asList(size1ValsArray);
		Iterator itr = size1ValsList.iterator();
		while (itr.hasNext()) {
			String size1Val = (String) itr.next();
			if (!tempSizeList.contains(size1Val)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param psd_inFiles
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void processPSD(String psd_inFiles) throws WTException, WTPropertyVetoException, IOException {
		HBISPBomUtil.debug("### START HBIPSD2SeasonLoader.processPSD(String psd_inFiles) ###");
		String[] psd_input_files = psd_inFiles.split(",");
		NumberFormat formatter = new DecimalFormat("#0.00000");
		for (String psd_input_file : psd_input_files) {

			// Do basic Validation on PSD load file
			validatePSD(psd_input_file);

			try {
				String psd_loader_fileName = PATH_SP_PSD_IN + psd_input_file + SP_PSD_LOADER_IN_FILETYPE;

				long start_Validation = System.currentTimeMillis();

				long end_Vaidation = System.currentTimeMillis();
				HBISPBomUtil.debug("[" + psd_loader_fileName + "], Validation completed in ["
						+ formatter.format((end_Vaidation - start_Validation) / 1000d) + "]");

				String report_file = PATH_SP_PSD_IN + psd_input_file + "_Report" + SP_PSD_LOADER_IN_FILETYPE;

				// Write the workbook in file system
				FileOutputStream psd_report = new FileOutputStream(new File(report_file));
				// Create blank workbook
				XSSFWorkbook workbook = new XSSFWorkbook();
				// Create a blank sheet
				XSSFSheet spreadsheet = workbook.createSheet("PSDReport");

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

				Map<String, Collection<Row>> groupedBysapkeys_Rows = HBISPBomUtil.groupBySapKeys(psd_loader_fileName);
				Iterator itr = groupedBysapkeys_Rows.keySet().iterator();
				while (itr.hasNext()) {
					String sap_key = (String) itr.next();
					if (!"SAP_KEY".equals(sap_key)) {
						HBISPBomUtil.debug("########## Started Transactions For SAP_KEY :: [" + sap_key + "]");

						long start_sapKey = System.currentTimeMillis();

						Collection<Row> psdRows = groupedBysapkeys_Rows.get(sap_key);
						try {
							row_out = spreadsheet.createRow(rowid++);
							cell0 = row_out.createCell(0);
							cell1 = row_out.createCell(1);
							cell2 = row_out.createCell(2);
							cell3 = row_out.createCell(3);
							cell0.setCellValue(sap_key);

							processPSD(sap_key, psdRows);
							HBISPBomUtil.debug("########## Completed Transactions for SAP_KEY :: [" + sap_key
									+ "], successfully #######");

							cell1.setCellValue("SUCCESS");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue("");

						} catch (Exception e) {
							String error = "!!!! Exception occured for sap_key [" + sap_key + "]";
							HBISPBomUtil.debug(
									"!!!!! Completed Transactions for SAP_KEY :: [" + sap_key + "], with Errors !!!!");

							cell1.setCellValue("FAILED");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue(e.getMessage());

							HBISPBomUtil.debug(error +",\n[ERROR MESSAGE :: "+e.getLocalizedMessage()+"]");
							// ERROR Report has to be implemented
							HBISPBomUtil.exception(error, e);
							e.printStackTrace();
						}

					}

				}
				// Write the Report details into file
				workbook.write(psd_report);
				psd_report.close();
			} catch (IOException ioExp) {
				HBISPBomUtil
						.debug("IOException in HBIPSD2SeasonLoader.processPSDs(String psd_inFiles) is " + ioExp);
				ioExp.printStackTrace();
			}
		}
		HBISPBomUtil.debug("### END HBIPSD2SeasonLoader.processPSDs(String psd_inFiles) ###");
	}

	/**
	 * @param sap_key
	 * @param psdRows
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("rawtypes")
	private static void processPSD(String sap_key, Collection<Row> psdRows)
			throws WTException, WTPropertyVetoException {
		HBISPBomUtil
				.debug("### START HBIPSD2SeasonLoader.processPSD(String sap_key,Collection<Row> moaRows) ###");

		// Get All the SAP_Keys that have to be loaded.
		Row row = (Row) psdRows.iterator().next();

		String sellingStyleNumber = HBISPBomUtil.getCellValue(row, 1);
		String description = HBISPBomUtil.getCellValue(row, 2);
		String apsPackQuantity = HBISPBomUtil.getCellValue(row, 3);
		String attributionCode = HBISPBomUtil.getCellValue(row, 4);

		attributionCode = HBISPBomUtil.getAttributionIda2a2(attributionCode);

		Transaction tr = null;
		try {
			tr = new Transaction();
			tr.start();

			LCSProduct sp = HBISPBomUtil.getProductByStyleNo(attributionCode, sellingStyleNumber, description,
					apsPackQuantity);
			LCSSeason season = HBISPBomUtil.getSPSeason(sp);

			String sizeDefTemplateName = "";
			String size1Vals = "";
			Iterator itr = psdRows.iterator();
			while (itr.hasNext()) {
				row = (Row) itr.next();
				sizeDefTemplateName = row.getCell(5).getStringCellValue();
				size1Vals = row.getCell(6).getStringCellValue();

				loadPSD(sp, season, sizeDefTemplateName, size1Vals);

			}

			tr.commit();
			tr = null;
		} finally {
			if (tr != null) {
				HBISPBomUtil.debug("!!!!!!!! PSD for [SAP_KEY :: " + sap_key
						+ "] Create/update transaction ended with some errors, "
						+ "so Rolling back the data saved in that DB transaction !!!!!!!!!");
				tr.rollback();
			}

		}
		HBISPBomUtil.debug("### END HBIPSD2SeasonLoader.processPSD(String sap_key,Collection<Row> moaRows) ###");

	}

	/**
	 * @param fileName
	 * @throws WTException
	 * @throws IOException
	 */
	private static void validatePSD(String input_filename) throws WTException, IOException {

		HBISPBomUtil.debug("##### Started validating the PSD input File [" + input_filename + "] #####");
		String loader_input_file = PATH_SP_PSD_IN + input_filename + SP_PSD_LOADER_IN_FILETYPE;
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(loader_input_file));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Row headerRow = workbook.getSheetAt(0).getRow(0);
		if (!HBISPBomUtil.getCellValue(headerRow, 0).equals("SAP_KEY")
				|| !HBISPBomUtil.getCellValue(headerRow, 1).equals("SELLING_STYLE")
				|| !HBISPBomUtil.getCellValue(headerRow, 2).equals("DESCRIPTION")
				|| !HBISPBomUtil.getCellValue(headerRow, 3).equals("PACKQUANTITY")
				|| !HBISPBomUtil.getCellValue(headerRow, 4).equals("ATTRIBUTIONCODE")
				|| !HBISPBomUtil.getCellValue(headerRow, 5).equals("SIZE_DEFINITION_TEMPLATE")
				|| !HBISPBomUtil.getCellValue(headerRow, 6).equals("SIZES1")) {
			file.close();
			throw new WTException("!!!! Validation failed in " + input_filename
					+ " not having header rows as [A:: SAP_KEY |B:: SELLING_STYLE |C:: "
					+ "DESCRIPTION |D:: PACKQUANTITY |E:: ATTRIBUTIONCODE |F:: SIZE_DEFINITION_TEMPLATE |G:: SIZES1]");
		}
		file.close();
		HBISPBomUtil.debug("###### Completed validating the PSD input File [" + loader_input_file + "] #########");

	}

}

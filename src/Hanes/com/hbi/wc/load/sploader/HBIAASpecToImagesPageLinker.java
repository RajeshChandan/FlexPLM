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

import com.hbi.wc.migration.loader.HBIPlantExtMOALoader;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.Query;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.delete.LCSDeleteHelper;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentQuery;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.FlexBOMPartClientModel;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.load.LoadCommon;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonLogic;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;
import com.lcs.wc.specification.FlexSpecLogic;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
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

public class HBIAASpecToImagesPageLinker implements RemoteAccess, Serializable {
	private static final String GP_PRODUCT = "Product\\BASIC CUT & SEW - GARMENT";

	private static RemoteMethodServer remoteMethodServer;
		public static final String SP_SALES_BOM_EXPORT = "D:\\ptc\\Windchill_11.2\\Windchill\\loadFiles\\lcsLoadFiles\\spBomLoader\\";

	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws InvocationTargetException, WTException, IOException {
		if (args.length != 2) {
			System.out.println(
					"Accepted command and input parameters:: java com.hbi.wc.load.sploader.HBIAASpecToImagesPageLinker  <input_file_name> <create/recreate>");
			throw new WTException(
					"java com.hbi.wc.load.sploader.HBIAASpecToImagesPageLinker  <input_file_name> <create/recreate>");
		}

		
		String fileName = args[0];
		String mode = args[1];

		if (mode.equals("create") || mode.equals("recreate")) {
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			remoteMethodServer = RemoteMethodServer.getDefault();
			
			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			//authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID); //username here
			authenticator.setRemoteUser("prodadmin"); //username here
			remoteMethodServer.setAuthenticator(authenticator);
			WTPrincipal principal = SessionHelper.manager.getPrincipal();
			
			Class[] argTypes = { String.class, String.class };
			Object[] argValues = { fileName, mode };

			try {
				// load Bom data into FlexPLM
				remoteMethodServer.invoke("linkImages", "com.hbi.wc.load.sploader.HBIAASpecToImagesPageLinker", null, argTypes,
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
	public static void linkImages(String input_filenames, String mode) throws WTException, IOException {
		System.out.println(">>>>>>>inside linkImages<<<<<<<<<<<<<");
		String arg[] = input_filenames.split(",");
		HBISPBomUtil.debug(" Input Data files count :: " + arg.length);
		NumberFormat formatter = new DecimalFormat("#0.00000");
		int totalcount = 0;
		long totalstart = System.currentTimeMillis();
		for (String input_filename : arg) {
			int count = 0;
			// Prepare load files
			long start_Validation = System.currentTimeMillis();
			long start = System.currentTimeMillis();
			HBISPBomUtil.debug(
					"########## Started Loading [" + "Source data" + "] from file [" + input_filename + "] ###########");
			try {

				long start_ReadingFile = System.currentTimeMillis();

				String report_file = HBISPBomUtil.PATH_SPBOMLOADER_IN + input_filename + "_Report"
						+ HBISPBomUtil.SPBOMLOADER_IN_TYPE;
				System.out.println(">>>>>>>>>>>>>>>>>>report_file path>>>>>>>>."+report_file);
				// Write the workbook in file system
				FileOutputStream bom_report = new FileOutputStream(new File(report_file));
				// Create blank workbook
				XSSFWorkbook workbook = new XSSFWorkbook();
				// Create a blank sheet
				XSSFSheet spreadsheet = workbook.createSheet("GPSpecReport");

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
				System.out.println(">>>>>>>path<<<<<<<"+loader_input_file);
				// Get All the SAP_Keys that have to be loaded.
				Map<String, Collection<Row>> groupedBysapkeys_Rows = HBISPBomUtil.groupBySapKeys(loader_input_file);
				count = count + groupedBysapkeys_Rows.size() - 1;
				totalcount = totalcount + groupedBysapkeys_Rows.size() - 1;
				long End_ReadingFile = System.currentTimeMillis();
				HBISPBomUtil.debug("########## Total unique Style_Number found from File [" + input_filename + " ] is ["
						+ groupedBysapkeys_Rows.size() + "]");

				HBISPBomUtil.debug("########## Completed Reading data from excel [" + "Source" + "], from"
						+ input_filename + " in [ " + formatter.format((End_ReadingFile - start_ReadingFile) / 1000d)
						+ " seconds ]");
				Iterator keys_Itr = groupedBysapkeys_Rows.keySet().iterator();
				while (keys_Itr.hasNext()) {
					String sap_key = (String) keys_Itr.next();
					System.out.println("sap_key:::::::::::::::::::"+sap_key);
					if (!"articlebom_product_code".equals(sap_key)) {
						HBISPBomUtil.debug("########## Started Transactions For Style Number :: [" + sap_key + "]");
						// To get material for cartonId in SALES BOM
						Collection<Row> sap_Key_Rows = groupedBysapkeys_Rows.get(sap_key);
						Iterator<Row> rowIterator = sap_Key_Rows.iterator();
						/*String fgs=null;
						while (rowIterator.hasNext()) {
							Row row = rowIterator.next();
							fgs=HBISPBomUtil.getCellValue(row, 1);
							// sap_key
							break;
						}*/

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
							// Get respective products in FlexPLM for the
							// SAP_Key
							FlexBOMPart bom = null;
                        try {
							LCSProduct gp = getProductByStyleNo(sap_key);
							String specName = "PD – Existing – ALL";
							LCSSeason season=HBIAASourcingLoader.getGPSeason(gp);							
							FlexSpecification refSpec = HBISPBomUtil.findProdSpecByName(gp, season, specName);
                            gp=(LCSProduct)VersionHelper.getVersion(gp, "A");
							Collection coll=LCSProductQuery.findImagePages(gp, null, null);
							Iterator imageitr=coll.iterator();
							FlexSpecLogic specLogic = new FlexSpecLogic();

							while(imageitr.hasNext()) {
								
								FlexObject imageObj=(FlexObject)imageitr.next();
								LCSDocument imageDoc=(LCSDocument)LCSDocumentQuery.findObjectById("VR:com.lcs.wc.document.LCSDocument:"+imageObj.getData("LCSDOCUMENT.BRANCHIDITERATIONINFO"));
								specLogic.addImagePageToSpec(refSpec, imageDoc, null);

							}
							
						
							cell1.setCellValue("SUCCESS");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue("");
						    
					}
                        catch(Exception e) {
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
					}
                        

				
					 else {
						HBISPBomUtil.debug("!!!!! Skipping Header row found !!!!");

					}
					 
					
					
					

				}	// Write the Report details into file
				workbook.write(bom_report);
				bom_report.close();

			
			long end = System.currentTimeMillis();

			HBISPBomUtil.debug("##########              	                        ###########\n");

			HBISPBomUtil.debug("########## Completed " + "Image linking" + " bulk loading of total Style Number count [" + count
					+ "], " + " from input file [ " + input_filename + ".xlsx], in [ "
					+ formatter.format((end - start) / 1000d) + " seconds ]    ###########\n");

		
			} catch (Exception e) {
				e.printStackTrace();
			}

		long totalend = System.currentTimeMillis();
		HBISPBomUtil.debug("########## Completed " + "Image linking" + " bulk loading of total SAP_KEY count [" + totalcount
				+ "], " + "from the following all input files [ " + input_filenames + "], in [ "
				+ formatter.format((totalend - totalstart) / 1000d) + " seconds ]    ###########\n");

		HBISPBomUtil.debug("########## Check Report and logs for more loader details ###########\n");

		HBISPBomUtil.debug("#####################         END              ###########\n");
		}

	}

	
	public static LCSProduct getProductByStyleNo(String SAP_KEY) throws WTException {
		FlexType prdType = FlexTypeCache.getFlexTypeFromPath(GP_PRODUCT);
		String sapKey_DB_Col = prdType.getAttribute("hbiProdNumber").getVariableName();

		LCSProduct product = null;
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		stmt.appendFromTable("prodarev", "product");
		stmt.appendSelectColumn("product", "ida2a2");
		stmt.appendOpenParen();
		stmt.appendCriteria(new Criteria("product", sapKey_DB_Col, SAP_KEY, Criteria.EQUALS));
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
	
	/**
	 * @param sp
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LCSSeason getGPSeason(LCSProduct sp) throws WTException {
		LCSSeason validSeason = null;

		Collection<LCSSeason> validSeasons = new ArrayList();
		Collection<LCSSeason> seasons = HBISPBomUtil.getAllActiveSeasonsOfAProduct(sp);
		Iterator itr = seasons.iterator();
		LCSSeason initialSeason=null;

		while (itr.hasNext()) {
			LCSSeason season = (LCSSeason) itr.next();
			String seasonName = season.getName();
			if(seasonName.contains("Initial Season")) {
				initialSeason=season;
			}
			else {
				
			validSeasons.add(season);
	
			}

			
		}
		
		if(validSeasons.size()==0) {
			
			validSeasons.add(initialSeason);
		}

		if (validSeasons.size() == 1) {
			validSeason = validSeasons.iterator().next();
		} else {
			throw new WTException(
					"Either no valid season or More than one valid Season linked to sp [" + sp.getName() + "]");
		}
		return validSeason;

	}
	
}

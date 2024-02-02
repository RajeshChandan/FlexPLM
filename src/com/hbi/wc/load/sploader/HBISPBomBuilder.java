package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.load.LoadCommon;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.pom.Transaction;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HBISPBomBuilder implements RemoteAccess, Serializable {
	private static final long serialVersionUID = 1L;
	private static RemoteMethodServer remoteMethodServer;
	private static final String SP_GENERAL_ATTRIBUTES = HBISPBomUtil.SP_GENERAL_ATTRIBUTES;

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws InvocationTargetException, WTException, IOException {
		if(args.length !=2){
			System.out.println("Accepted command and input parameters:: java com.hbi.wc.load.sploader.HBISPBomBuilder <input_file_name> <create/update>");
			throw new WTException("java com.hbi.wc.load.sploader.HBISPBomBuilder <input_file_name> <create/update>");
		}
		
		String fileName = args[0];
		String mode = args[1];
		
		if(mode.equals("create") || mode.equals("update")){
		
		
		
		
		
		
		
		
		
		
		
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
		Class[] argTypes = { String.class,String.class };
		Object[] argValues = { fileName,mode};
		
		
		
		
		
		
		
		
		try {
			//Prepare load files
			prepareLoadFiles();
			long start_Validation = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			
			//Do basic Validation on Bom load files
			HBISPBomUtil.validateBOMLoadFiles();
			LoadCommon.putCache("","Tes","tet");
			long end_Vaidation = System.currentTimeMillis();
			System.out.println(formatter.format((end_Vaidation - start_Validation) / 1000d));
			//load Bom data into FlexPLM
			remoteMethodServer.invoke("loadSPBoms", "com.hbi.wc.load.sploader.HBISPBomBuilder", null, argTypes,
					argValues);
			
			
			
			
			
			
			
			
			System.out.println("\n####### Ended Remote method server connection, please check logs in migration #####");
			System.out.println("####### Successfully logged off #####");
	        System.exit(0);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		}else{
			System.out.println("only create/update is allowed as input parameter");
		}

	}

	private static void prepareLoadFiles() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param str
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	public static void loadSPBoms(String input_filename,String mode) throws WTException {
		long start = System.currentTimeMillis();
		NumberFormat formatter = new DecimalFormat("#0.00000");
		HBISPBomUtil.debug("########## Started Loading SP BOMS  ###########");
		try {
			
			// Write the workbook in file system
			FileOutputStream bom_report = new FileOutputStream(new File(HBISPBomUtil.SP_BOM_REPORT));
			// Create blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();
			// Create a blank sheet
			XSSFSheet spreadsheet = workbook.createSheet("SPBOMReport");
			
			int rowid = 0;
			// Create row object
			XSSFRow row_out = spreadsheet.createRow(rowid++);;
			Cell cell0 = row_out.createCell(0);
			Cell cell1 = row_out.createCell(1);
			Cell cell2 = row_out.createCell(2);
			Cell cell3 = row_out.createCell(3);
			cell0.setCellValue("SAP_KEY");
			cell1.setCellValue("STATUS");
			cell2.setCellValue("RUNTIME");
			cell3.setCellValue("COMMENTS");
			String loader_input_file = HBISPBomUtil.PATH_SPBOMLOADER_IN+input_filename+HBISPBomUtil.SPBOMLOADER_IN_TYPE;
			// Get All the SAP_Keys that have to be loaded.
			Collection<String> sap_keys = HBISPBomUtil.getSapKeys(loader_input_file);
		
			// Get All the SAP_Keys that have to be loaded.
			Map<String, Collection<Row>> groupedBysapkeys_Rows = HBISPBomUtil.groupBySapKeys(loader_input_file);
			Iterator keys_Itr = sap_keys.iterator();
			String cartonId="";
			while (keys_Itr.hasNext()) {
				String sap_key = (String) keys_Itr.next();
				//To get material for cartonId in SALES BOM
				Collection<Row> sap_Key_Rows = groupedBysapkeys_Rows.get(sap_key);
				Iterator<Row> rowIterator = sap_Key_Rows.iterator();
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					// sap_key
					 cartonId = HBISPBomUtil.getCellValue(row, 18);
					 LCSLog.debug("HBISPBOMBuilder:cartonId "+cartonId);
					break;
				}
				
				//Carton ID material which is of type Casing\Corrugated Carton
				long start_sapKey = System.currentTimeMillis();
				row_out = spreadsheet.createRow(rowid++);
				 cell0 = row_out.createCell(0);
				 cell1 = row_out.createCell(1);
				 cell2 = row_out.createCell(2);
				 cell3 = row_out.createCell(3);
				
				cell0.setCellValue(sap_key);
				
				Map<String, Collection<String>> validPutups = HBISPBomUtil.getValidPutUps(sap_key);
				try {
					// Get respective products in FlexPLM for the SAP_Key
					Collection<LCSProduct> sps = HBISPBomUtil.getProductsFromSapKey(sap_key, SP_GENERAL_ATTRIBUTES);

					// create Spec, Bom and update spec as ref spec in Product
					// putup moa table
					updateProdPutups(sap_key, validPutups, sps,mode,cartonId);
					
					//HBISPBomUtil.report(sap_key,row,"successs",null);
					cell1.setCellValue("SUCCESS");
					long end_sapKey = System.currentTimeMillis();
					cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
					cell3.setCellValue("");
				} catch (Exception e) {
					String error = "!!!! Exception occured for sap_key [" + sap_key + "]";
					HBISPBomUtil.debug("error"+error);
					// ERROR Report has to be implemented
					HBISPBomUtil.exception(error, e);
					//HBISPBomUtil.report(sap_key,row,"Failed",e);
					cell1.setCellValue("FAILED");
					
					long end_sapKey = System.currentTimeMillis();
					cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
					cell3.setCellValue(e.getLocalizedMessage());
				}

			}
			
			//Write the Report details into file
			workbook.write(bom_report);
			bom_report.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		
		HBISPBomUtil.debug("########## Completed Loading SP BOMS[ "+formatter.format((end - start) / 1000d) + " seconds ###########");
	}

	/**
	 * @param sap_key
	 * @param validPutups
	 * @param sps
	 * @param mode 
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static void updateProdPutups(String sap_key, Map<String, Collection<String>> validPutups,
			Collection<LCSProduct> sps, String mode, String cartonId) throws WTPropertyVetoException, WTException, IOException {

		Iterator prodItr = sps.iterator();
		while (prodItr.hasNext()) {
			LCSProduct sp = (LCSProduct) prodItr.next();

			createRefPutups(sap_key, validPutups, sp,mode,cartonId);

		}

	}

	/**
	 * @param sap_key
	 * @param validPutups
	 * @param sp
	 * @param mode 
	 * @throws NumberFormatException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Collection<FlexSpecification> createRefPutups(String sap_key,
			Map<String, Collection<String>> validPutups, LCSProduct sp, String mode, String cartonId)
			throws NumberFormatException, WTPropertyVetoException, WTException, IOException {
		Collection<FlexSpecification> refSpecs = new ArrayList();
		LCSSeason season = HBISPBomUtil.getSPSeason(sp);
		Iterator itr = validPutups.keySet().iterator();
		while (itr.hasNext()) {
			String ref_putup = (String) itr.next();

			// Create the spec
			FlexSpecification refSpec = createRefPutup(sap_key, sp, ref_putup, season);

			// Update the Spec in Product's Moa PutCode table
			boolean ismoaUpdated = updateProductMoa(sap_key, sp, ref_putup, validPutups, refSpec);
			if (ismoaUpdated) {
				long start_Boms = System.currentTimeMillis();
				NumberFormat formatter = new DecimalFormat("#0.00000");
				
				// Create PackCase and Sales Boms and link to Spec
				refSpec = createSaleAndPkgBoms(sap_key, sp, ref_putup, season, refSpec,mode, cartonId);
				
				long end_Boms = System.currentTimeMillis();
				HBISPBomUtil.debug(sap_key +", Total Boms loading time :: ["+formatter.format((end_Boms - start_Boms) / 1000d)+"]");
				
				refSpecs.add(refSpec);
			} else {
				throw new WTException("No Moa table data found for [sap_key :: " + sap_key + "],[ref_putup :: "
						+ ref_putup + "]," + " [SP :: " + sp.getName() + "]");
			}

		}
		return refSpecs;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param putupcode2
	 * @param refSpec
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("rawtypes")
	private static boolean updateProductMoa(String sap_key, LCSProduct sp, String ref_putup,
			Map<String, Collection<String>> validPutups, FlexSpecification refSpec)
			throws WTPropertyVetoException, WTException {
		boolean updated = false;
		Collection<String> putupCodes = validPutups.get(ref_putup);
		Iterator itr = putupCodes.iterator();
		while (itr.hasNext()) {
			String putupcode = (String) itr.next();
			refSpec = updateMoaTable(sap_key, sp, ref_putup, putupcode, refSpec);
			if (refSpec != null) {
				updated = true;
			} else {
				throw new WTException("No Moa table data found for [sap_key :: " + sap_key + "], [putupcode :: "
						+ putupcode + "],[ref_putup :: " + ref_putup + "], [SP :: " + sp.getName() + "]");
			}
		}
		return updated;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param ref_putup
	 * @param putupcode
	 * @param refSpec
	 * @param refSpecbranchId
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	private static FlexSpecification updateMoaTable(String sap_key, LCSProduct sp, String ref_putup, String putupcode,
			FlexSpecification refSpec) throws WTException, WTPropertyVetoException {
		LCSMOATable putUpMOATab = (LCSMOATable) sp.getValue("hbiPutUpCode");
		if (putUpMOATab != null) {
			Collection<FlexObject> rowColl = putUpMOATab.getRows();
			if (rowColl.size() > 0) {
				refSpec = updatePutUpMoa(sap_key, sp, putupcode, refSpec, rowColl);
			} else {
				throw new WTException("!!!! Exception occured in updating putup moa table for [sap_key :: " + sap_key
						+ "], [putupcode :: " + putupcode + "], [ref_putup :: " + ref_putup + "], [SP :: "
						+ sp.getName() + "]");
			}
		} else {
			throw new WTException("No Moa table data found for [sap_key :: " + sap_key + "], [putupcode :: " + putupcode
					+ "],[ref_putup :: " + ref_putup + "], [SP :: " + sp.getName() + "]");
		}
		return refSpec;

	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putupcode
	 * @param refSpec
	 * @param refSpecbranchId
	 * @param rowColl
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	private static FlexSpecification updatePutUpMoa(String sap_key, LCSProduct sp, String putupcode,
			FlexSpecification refSpec, Collection<FlexObject> rowColl) throws WTPropertyVetoException, WTException {
		Iterator itr = rowColl.iterator();
		while (itr.hasNext()) {
			FlexObject moaRow = (FlexObject) itr.next();
			String key = moaRow.getString("OID");
			LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery
					.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + key);
			String putup = getPutupCode(moaObject);
			HBISPBomUtil.debug("[ putupcode:: " + putupcode + "],[ putup :: " + putup + "]");
			if (FormatHelper.hasContent(putup)) {
				if (putup.equals(putupcode)) {
					moaObject.setValue("hbiReferenceSpecification", refSpec);
					LCSMOAObjectLogic.persist(moaObject, true);
					return refSpec;

				}
			} else {
				throw new WTException("!!!! Exception not found PutUp code in Product moa table" + " [ sap_key ::"
						+ sap_key + "],[ putupcode :: " + putupcode + "],[ SP :: " + sp.getName() + "]");
			}
		}
		return null;
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
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @return
	 * @throws NumberFormatException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @throws IOException
	 */
	private static FlexSpecification createRefPutup(String sap_key, LCSProduct sp, String ref_putup, LCSSeason season)
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
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param season
	 * @param refSpec
	 * @param mode 
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static FlexSpecification createSaleAndPkgBoms(String sap_key, LCSProduct sp, String putup, LCSSeason season,
			FlexSpecification refSpec, String mode,String cartonId) throws WTPropertyVetoException, WTException, IOException {

		String salesBomName = HBISPBomUtil.getSPBomName(sp, putup, "SalesBOM");
		String altSaleBomName = HBISPBomUtil.getSPBomName(sp, putup, "SalesBOM_Alt");
		String pkgCaseName = HBISPBomUtil.getSPBomName(sp, putup, "PkgCase");

		FlexBOMPart salesBom = HBISPBomUtil.getExistingBom(sp, salesBomName);
		FlexBOMPart altSalesBom = HBISPBomUtil.getExistingBom(sp, altSaleBomName);
		FlexBOMPart pkgCaseBom = HBISPBomUtil.getExistingBom(sp, pkgCaseName);

		Transaction tr = null;
		try {
			tr = new Transaction();
			tr.start();
			long start_SalesBoms = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
		
			if (salesBom == null && altSalesBom == null) {
				// load Sales Bom
				salesBom = HbiSalesBomLoader.loadSalesBom(sap_key, sp, putup, season, refSpec, salesBomName,altSaleBomName,cartonId);
			} else if(mode.equals("create")) {
				throw new WTException("!!!! SALES BOM :: [" + salesBomName + "],or AltSales BOM :: [" + altSalesBom
						+ "] already existing on Product [" + sp.getName() + "]");
			}else if(mode.equals("update")){
				if(salesBom !=null){
				HBISPBomUtil.deleteSpecAndBoms(sp, refSpec, salesBom,salesBomName);
				}
				if(altSalesBom !=null){
					HBISPBomUtil.deleteSpecAndBoms(sp, refSpec, altSalesBom,altSaleBomName);
				}
				salesBom = HbiSalesBomLoader.loadSalesBom(sap_key, sp, putup, season, refSpec, salesBomName,
						altSaleBomName,cartonId);
			}
			long end_SalesBoms = System.currentTimeMillis();
			HBISPBomUtil.debug(sap_key +", Total Sales Boms loading time ::"
					+ " ["+formatter.format((end_SalesBoms - start_SalesBoms) / 1000d)+"]");
			
			long start_pkgBoms = System.currentTimeMillis();
			if (pkgCaseBom == null) {
				// load pack case Bom
				pkgCaseBom = HbiPackCaseBomLoader.loadPackCaseBom(sap_key, sp, putup, season, refSpec, pkgCaseName,null);
			} else if(mode.equals("create")){
				throw new WTException(
						"!!!! pkgCase BOM :: [" + pkgCaseName + "] already existing on Product [" + sp.getName() + "]");
			}else if(mode.equals("update") && pkgCaseBom !=null){
				HBISPBomUtil.deleteSpecAndBoms(sp, refSpec, pkgCaseBom,pkgCaseName);
				pkgCaseBom = HbiPackCaseBomLoader.loadPackCaseBom(sap_key, sp, putup, season, refSpec, pkgCaseName,null);
				
			}
			long end_pkgBoms = System.currentTimeMillis();
			HBISPBomUtil.debug(sap_key +", Total pkgcase Boms loading time ::"
					+ " ["+formatter.format((end_pkgBoms - start_pkgBoms) / 1000d)+"]");
		
			tr.commit();

			tr = null;
		} finally {
			if (tr != null) {
				HBISPBomUtil.debug("!!!!!!!! SP and PackCase Boms Create transaction ended with some errors, "
						+ "so Rolling back the data saved in that DB transaction !!!!!!!!!");
				tr.rollback();
			}
		}

		return refSpec;
	}

}

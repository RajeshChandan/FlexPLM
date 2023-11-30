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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;

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

public class HBISPBomBulkLoader implements RemoteAccess, Serializable {
	private static final String SALES_BOM = HBISPBomUtil.SALES_BOM;
	private static final String ALT_BOM = HBISPBomUtil.ALT_BOM;
	private static final String PKG_BOM = HBISPBomUtil.PKG_BOM;
	private static final String PALLET_BOM = HBISPBomUtil.PALLET_BOM;
	private static final String ZFRT_BOM = HBISPBomUtil.ZFRT_BOM;
	private static final long serialVersionUID = 1L;
	private static RemoteMethodServer remoteMethodServer;
	private static String [] bomTypeArray = {PKG_BOM,SALES_BOM,ALT_BOM,ZFRT_BOM,PALLET_BOM};
	@SuppressWarnings("rawtypes")
	private static Collection validBomTypes = Arrays.asList(bomTypeArray);
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws InvocationTargetException, WTException, IOException {
		if(args.length !=3){
			System.out.println("Accepted command and input parameters:: java com.hbi.wc.load.sploader.HBISPBomBulkLoader <bomType> <input_file_name> <create/recreate>");
			throw new WTException("java com.hbi.wc.load.sploader.HBISPBomBulkLoader <bomType> <input_file_name> <create/recreate>");
		}
		
		String bomType = args[0];
		if(!validBomTypes.contains(bomType)){
		System.out.println("Only follwing are allowed bom types for bulk load :: "+validBomTypes);
		}
		String fileName = args[1];
		String mode = args[2];
		
		if(mode.equals("create") || mode.equals("recreate")){
		
		
		
		
		
		
		
		
		
		System.out.println("####### Starting Remote method server connection #####");
		MethodContext mcontext = new MethodContext((String) null, (Object) null);
		SessionContext sessioncontext = SessionContext.newContext();
		remoteMethodServer = RemoteMethodServer.getDefault();
		GatewayAuthenticator authenticator = new GatewayAuthenticator();
		authenticator.setRemoteUser("prodadmin"); //username here
		remoteMethodServer.setAuthenticator(authenticator);
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		System.out.println("####### Successfully logged in #####");

		Class[] argTypes = { String.class,String.class,String.class };
		Object[] argValues = {bomType, fileName,mode};
		
		
		
		try {
				//load Bom data into FlexPLM
			remoteMethodServer.invoke("loadSPBoms", "com.hbi.wc.load.sploader.HBISPBomBulkLoader", null, argTypes,
					argValues);
			
			
			
			
			
			
			
			System.exit(0);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		}else{
			System.out.println("only create/recreate is allowed as input parameter");
		}

	}

	private static void prepareLoadFiles() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param str
	 * @throws WTException
	 * @throws IOException 
	 */
	@SuppressWarnings("rawtypes")
	public static void loadSPBoms(String bomType, String input_filenames, String mode) throws WTException, IOException {
		String arg[] = input_filenames.split(",");
		HBISPBomUtil.debug(" Input Data files count :: "+arg.length);
		NumberFormat formatter = new DecimalFormat("#0.00000");
		int totalcount = 0;
		long totalstart = System.currentTimeMillis();
		for (String input_filename : arg) {
			int count = 0;
			//Prepare load files
			prepareLoadFiles();
			long start_Validation = System.currentTimeMillis();
			
			//Do basic Validation on Bom load files
			HBISPBomUtil.validateBOMBulkLoadFiles(bomType,input_filename);
			
			long end_Vaidation = System.currentTimeMillis();
			HBISPBomUtil.debug(input_filename+" Validation completed in ["+ formatter.format((end_Vaidation - start_Validation) / 1000d)+"]");
		
			long start = System.currentTimeMillis();
			HBISPBomUtil.debug("########## Started Loading [" + bomType + "] from file [" + input_filename + "] ###########");
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
				cell0.setCellValue("SAP_KEY");
				cell1.setCellValue("STATUS");
				cell2.setCellValue("RUNTIME");
				cell3.setCellValue("COMMENTS");
				String loader_input_file = HBISPBomUtil.PATH_SPBOMLOADER_IN + input_filename
						+ HBISPBomUtil.SPBOMLOADER_IN_TYPE;
				// Get All the SAP_Keys that have to be loaded.
				Map<String, Collection<Row>> groupedBysapkeys_Rows = HBISPBomUtil.groupBySapKeys(loader_input_file);
				count = count + groupedBysapkeys_Rows.size()-1;
				totalcount = totalcount + groupedBysapkeys_Rows.size()-1;
				long End_ReadingFile = System.currentTimeMillis();
				HBISPBomUtil.debug("########## Total unique Sap_keys found from File ["+input_filename+" ] is ["+groupedBysapkeys_Rows.size() +"]" );
				
				HBISPBomUtil.debug("########## Completed Reading data from excel [" + bomType + "], from" + input_filename + " in [ "
						+ formatter.format((End_ReadingFile - start_ReadingFile) / 1000d) + " seconds ]");
				String cartonId="";
				Iterator keys_Itr = groupedBysapkeys_Rows.keySet().iterator();
				while (keys_Itr.hasNext()) {
					String sap_key = (String) keys_Itr.next();
					if (!"SAP_KEY".equals(sap_key)) {
						HBISPBomUtil.debug("########## Started Transactions For SAP_KEY :: [" + sap_key + "]");
						//To get material for cartonId in SALES BOM
						Collection<Row> sap_Key_Rows = groupedBysapkeys_Rows.get(sap_key);
						Iterator<Row> rowIterator = sap_Key_Rows.iterator();
						while (rowIterator.hasNext()) {
							Row row = rowIterator.next();
							// sap_key
							 cartonId = HBISPBomUtil.getCellValue(row, 17);
							 LCSLog.debug("HBISPBOMBulkLoader:cartonId "+cartonId);
							break;
						}
						
						//Carton ID material which is of type Material\Casing

						long start_sapKey = System.currentTimeMillis();
						row_out = spreadsheet.createRow(rowid++);
						cell0 = row_out.createCell(0);
						cell1 = row_out.createCell(1);
						cell2 = row_out.createCell(2);
						cell3 = row_out.createCell(3);

						cell0.setCellValue(sap_key);

						Map<String, Collection<Row>> validPutups = HBISPBomUtil.groupByRefPutUps(bomType, sap_key,sap_Key_Rows);
						try {
							// Get respective products in FlexPLM for the
							// SAP_Key
							LCSProduct sp = HBISPBomUtil.getProductFromSapKey(bomType, sap_key,
									sap_Key_Rows.iterator().next());

							// Create Spec, Bom and update MOA ref_spec value
							updateProdPutups(bomType, sap_key, validPutups, sp, mode, cartonId);

							HBISPBomUtil.debug("########## Completed Transactions for SAP_KEY :: [" + sap_key + "], successfully #######");
							cell1.setCellValue("SUCCESS");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue("");
						} catch (Exception e) {
							String error = "!!!! Exception occured for sap_key [" + sap_key + "]";
							HBISPBomUtil.debug(error +",\n[ERROR MESSAGE :: "+e.getLocalizedMessage()+"]");
							HBISPBomUtil.debug(
									"!!!!! Completed Transactions for SAP_KEY :: [" + sap_key + "], with Errors !!!!");

							// ERROR Report has to be implemented
							HBISPBomUtil.exception(error, e);
							// HBISPBomUtil.report(sap_key,row,"Failed",e);
							cell1.setCellValue("FAILED");

							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue(e.getLocalizedMessage());
						}
					}else{
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

			HBISPBomUtil.debug("########## Completed " + bomType +" bulk loading of total SAP_KEY count ["+ count +"], "
					+ " from input file [ " + input_filename + ".xlsx], in [ "
					+ formatter.format((end - start) / 1000d) + " seconds ]    ###########\n");
			
					}
		
		long totalend = System.currentTimeMillis();
		HBISPBomUtil.debug("########## Completed " + bomType +" bulk loading of total SAP_KEY count ["+ totalcount +"], "
				+ "from the following all input files [ " + input_filenames + "], in [ "
				+ formatter.format((totalend - totalstart) / 1000d) + " seconds ]    ###########\n");
		
		HBISPBomUtil.debug("########## Check Report and logs for more loader details ###########\n");
		
		HBISPBomUtil.debug("#####################         END              ###########\n");

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
	
	private static void updateProdPutups(String bomType,String sap_key, Map<String, Collection<Row>> validPutups,
			LCSProduct sp, String mode, String cartonId) throws WTPropertyVetoException, WTException, IOException {
			createRefPutups(bomType, sap_key, validPutups, sp,mode,  cartonId);
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
	private static Collection<FlexSpecification> createRefPutups(String bomType,String sap_key,
			Map<String, Collection<Row>> bomRowsGroupedByPutUp, LCSProduct sp, String mode, String cartonId)
			throws NumberFormatException, WTPropertyVetoException, WTException, IOException {
		Collection<FlexSpecification> refSpecs = new ArrayList();
		LCSSeason season = HBISPBomUtil.getSPSeason(sp);
		Iterator itr = bomRowsGroupedByPutUp.keySet().iterator();
		
		while (itr.hasNext()) {
			String ref_putup = (String) itr.next();
			Collection bomRows = bomRowsGroupedByPutUp.get(ref_putup);
			// Create the spec
			FlexSpecification refSpec = createRefPutup(sap_key, sp, ref_putup, season);

			// Update the Spec in Product's Moa PutCode table
			//boolean ismoaUpdated = updateProductMoa(sap_key, sp, ref_putup, validPutups, refSpec);
			//boolean ismoaUpdated = true;
			
			if (refSpec !=null) {
				long start_Boms = System.currentTimeMillis();
				NumberFormat formatter = new DecimalFormat("#0.00000");
				
				// Create PackCase and Sales Boms and link to Spec
				refSpec = createSaleAndPkgBoms(sap_key, sp, ref_putup, season, refSpec,mode,bomType,bomRows,  cartonId);
				
				long end_Boms = System.currentTimeMillis();
				HBISPBomUtil.debug(sap_key +", Total Boms loading time :: ["+formatter.format((end_Boms - start_Boms) / 1000d)+"]");
				
				refSpecs.add(refSpec);
			} else {
				throw new WTException("No Spec found for [sap_key :: " + sap_key + "],[putUp :: "
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
	@SuppressWarnings({ "rawtypes", "unused" })
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
		// Commented below spec creation steps as it handled in putUp Code bulk loader	
		//LCSSourcingConfig srcCfg = HBISPBomUtil.getSrcCfg(sp, season);
		//HBISPBomUtil.createProdSpec(sap_key, sp, ref_putup, season, srcCfg, specName);

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
	 * @param bomRows 
	 * @param bomType 
	 * @throws IOException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static FlexSpecification createSaleAndPkgBoms(String sap_key, LCSProduct sp, String putup, LCSSeason season,
			FlexSpecification refSpec, String mode, String bomType, Collection<Row> bomRows, String cartonId)
			throws WTPropertyVetoException, WTException, IOException {
			Transaction tr = null;
		try {
			tr = new Transaction();
			tr.start();
			String bomName ="";
			FlexBOMPart bom = null;
			long start_Bom = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			if (SALES_BOM.equals(bomType)) {
				bomName = HBISPBomUtil.getSPBomName(sp, putup, SALES_BOM);
			
			} 
			if (PALLET_BOM.equals(bomType)) {
				bomName = HBISPBomUtil.getSPBomName(sp, putup, PALLET_BOM);
			
			}
			
			else if (ALT_BOM.equals(bomType)) {
				bomName = HBISPBomUtil.getSPBomName(sp, putup, "SalesBOM_Alt");
			
			} else if (PKG_BOM.equals(bomType)) {
				bomName = HBISPBomUtil.getSPBomName(sp, putup, "PkgCase");

			}else if(ZFRT_BOM.equals(bomType)){
				bomName = HBISPBomUtil.getSPBomName(sp, putup, "SalesBOM_ZFRT");
			}
			bom = HBISPBomUtil.getExistingBom(sp, bomName);
			
			System.out.println("----------new BOM for Loading-------------- "+bomName);
			
			
		//-- Added code to delete duplicate BOMs	 - Start
         /*  if(mode.equals("recreate")) {
				//chetan
				FlexSpecification flexSpec = null;

				Collection<FlexBOMPart> bomParts = new ArrayList();

				bomParts = (new LCSFlexBOMQuery()).findBOMPartsForOwner(sp, "A", "MAIN", (FlexSpecification) flexSpec);
               Iterator itr= bomParts.iterator();
				while (itr.hasNext()) {
					if(bom.getFlexType().getFullName().contains(HBISPBomUtil.PACKCASE_BOMTYPE)) {
					bom = (FlexBOMPart)itr.next();
					if (VersionHelper.isCheckedOut(bom)) {
						System.out.println("Found check out bom on SP, so checking in [bomName :: " + bomName
								+ "], [ SP :: " + sp.getName() + " ]");
						bom = (FlexBOMPart) VersionHelper.checkin(bom);
					}

				
				LCSFlexBOMLogic bomLogic = new LCSFlexBOMLogic();
				FlexSpecToComponentLink link = FlexSpecQuery.getSpecToComponentLink(refSpec, bom);
			
				if (link != null) {
					FlexSpecLogic specLogic = new FlexSpecLogic();
					specLogic.deleteSpecToComponent(link);

				}
				System.out.println("Bom :: [" + bom.getName() + "] deleted");

				bomLogic.deleteFlexBOMPart(bom);
				}
				}
        	   
           }*/
			
   		//-- Added code to delete duplicate BOMs	 - End
	
			/*if (bom == null) {
				// load pack case Bom
				bom = loadBom(sap_key, sp, putup, season, refSpec, bomName, bomType, bomRows,"");
			} else if (mode.equals("create")) {
				System.out.println("check create ---------------------"+bomName);

				throw new WTException(
						"!!!! "+bomType +" :: [" + bomName + "] already existing on Product [" + sp.getName() + "]");
			}*/
           if (mode.equals("recreate")) {
        	   if(bom!=null)
				HBISPBomUtil.deleteSpecAndBoms(sp, refSpec, bom, bomName);
        		 
        	   
				bom = loadBom(sap_key, sp, putup, season, refSpec,bomName,bomType,bomRows,cartonId);
				
			}
			long end_Bom = System.currentTimeMillis();
			HBISPBomUtil.debug("sap_key :: "+ sap_key+ ",putup "+putup+", total Bom loading time ::" + " ["
					+ formatter.format((end_Bom - start_Bom) / 1000d) + "]");

			tr.commit();

			tr = null;
		} finally {
			if (tr != null) {
				HBISPBomUtil.debug("!!!!!!!! sap_key:: ["+sap_key+"] Boms Create transaction ended with some errors, "
						+ "so Rolling back the all respective BOMs data saved in that DB transaction !!!!!!!!!");
				tr.rollback();
			}
		}

		return refSpec;
	}

	/**
	 * @param sap_key
	 * @param sp
	 * @param putup
	 * @param season
	 * @param refSpec
	 * @param bomName
	 * @param bomType
	 * @param bomRows
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 * @throws WTException
	 */
	private static FlexBOMPart loadBom(String sap_key, LCSProduct sp, String putup, LCSSeason season,
			FlexSpecification refSpec, String bomName, String bomType, Collection<Row> bomRows,String cartonId) throws WTPropertyVetoException, IOException, WTException {
		FlexBOMPart bom = null;
		if(PKG_BOM.equals(bomType)){
			bom = HbiPackCaseBomLoader.loadPackCaseBom(sap_key, sp, putup, season, refSpec, bomName,bomRows);
			}else if(SALES_BOM.equals(bomType) || ALT_BOM.equals(bomType)||PALLET_BOM.equals(bomType)){
				bom = HbiSalesBomLoader.loadSalesBom(sap_key, sp, putup, season, refSpec, bomName,bomRows, cartonId,bomType);
			}else if (ZFRT_BOM.equals(bomType) ){
				bom = HbiZfrtBomLoader.loadZFRTBom(sap_key, sp, putup, season, refSpec, bomName, bomRows);
			}
		return bom;
	}

	

}

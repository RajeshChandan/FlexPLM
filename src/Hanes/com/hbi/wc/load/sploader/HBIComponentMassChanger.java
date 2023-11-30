package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import wt.enterprise.RevisionControlled;
import wt.httpgw.GatewayAuthenticator;
import wt.part.WTPartMaster;
import wt.pom.Transaction;
import wt.session.SessionContext;
import wt.session.SessionHelper;

import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.io.FileWriter;
import com.hbi.wc.load.sploader.HBISPBomUtil;
import com.hbi.wc.moa.HBILNMOAPlugin;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
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
 * HBILNBOMMigrator.java
 *
 * This class contains stand alone functions using to read data from excel file,
 * update the moa on material
 * 
 * @author ust
 * @since jan-15-2021
 */
public class HBIComponentMassChanger implements RemoteAccess {
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;

	
	public static final String PATH_BOM_IN = "D:\\ptc\\Windchill_11.2\\Windchill\\loadFiles\\lcsLoadFiles\\LTBOMLoader\\";
	public static final String PATH_BOM_IN_FILETYPE = ".xlsx";
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
			if (args.length != 2) {
				System.out.println("windchill com.hbi.wc.load.sploader.HBILNBOMMigrator <fileName> <type>");
			}

			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			remoteMethodServer = RemoteMethodServer.getDefault();
			
			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			//authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID); //username here
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID); //username here
			remoteMethodServer.setAuthenticator(authenticator);
			WTPrincipal principal = SessionHelper.manager.getPrincipal();

			
					String fileName = args[0];
					String type=args[1];

			
			Class[] argTypes = { String.class, String.class };
			Object[] argValues = {fileName, type };
			remoteMethodServer.invoke("processBOMMoa", "com.hbi.wc.load.sploader.HBIComponentMassChanger", null,
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
	public static void processBOMMoa(String putupCode_inFiles,String type)
			throws WTException, WTPropertyVetoException, IOException {
		LCSLog.debug("### START HBIPutUpCodeLoader.processProductPutUpCodeMoa(String putupCode_inFiles) ###");
		String[] BOM_input_files = putupCode_inFiles.split(",");
		NumberFormat formatter = new DecimalFormat("#0.00000");
		for (String BOM_input_file : BOM_input_files) {

			// Do basic Validation on PutUpCode load file
			//validatePutUpCode(putUp_input_file);

			try {
				String putUpCode_loader_fileName = PATH_BOM_IN + BOM_input_file
						+ PATH_BOM_IN_FILETYPE;

				long start_Validation = System.currentTimeMillis();

				long end_Vaidation = System.currentTimeMillis();
				LCSLog.debug("[" + putUpCode_loader_fileName + "], Validation completed in ["
						+ formatter.format((end_Vaidation - start_Validation) / 1000d) + "]");

				String report_file = PATH_BOM_IN + BOM_input_file + "_Report"
						+ PATH_BOM_IN_FILETYPE;

				// Write the workbook in file system
				FileOutputStream putUp_report = new FileOutputStream(new File(report_file));
				// Create blank workbook
				XSSFWorkbook workbook = new XSSFWorkbook();
				// Create a blank sheet
				XSSFSheet spreadsheet = workbook.createSheet("BOMReport");

				int rowid = 0;
				// Create row object
				XSSFRow row_out = spreadsheet.createRow(rowid++);
				Cell cell0 = row_out.createCell(0);
				Cell cell1 = row_out.createCell(1);
				Cell cell2 = row_out.createCell(2);
				Cell cell3 = row_out.createCell(3);
				cell0.setCellValue("PARENT");
				cell1.setCellValue("STATUS");
				cell2.setCellValue("RUNTIME");
				cell3.setCellValue("COMMENTS");

				Map<String, Collection<Row>> groupedBysapkeys_Rows = HBISPBomUtil
						.groupBySapKeys(putUpCode_loader_fileName);
				Iterator itr = groupedBysapkeys_Rows.keySet().iterator();
				while (itr.hasNext()) {
					String sap_key = (String) itr.next();
					if (!"Parent".equalsIgnoreCase(sap_key)) {
						LCSLog.debug("########## Started Transactions For PARENT :: [" + sap_key + "]");

						long start_sapKey = System.currentTimeMillis();

						Collection<Row> moaRows = groupedBysapkeys_Rows.get(sap_key);
						try {
							row_out = spreadsheet.createRow(rowid++);
							cell0 = row_out.createCell(0);
							cell1 = row_out.createCell(1);
							cell2 = row_out.createCell(2);
							cell3 = row_out.createCell(3);
							cell0.setCellValue(sap_key);

							processLTBOMMoa(sap_key, moaRows,type);
							LCSLog.debug("########## Completed Transactions for SAP_KEY :: [" + sap_key
									+ "], successfully #######");

							cell1.setCellValue("SUCCESS");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue("");

						} catch (Exception e) {
							LCSLog.debug(
									"!!!!! Completed Transactions for PARENT :: [" + sap_key + "], with Errors !!!!");

							cell1.setCellValue("FAILED");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue(e.getMessage());

							
							e.printStackTrace();
						}

					}
					
				}
				// Write the Report details into file
				workbook.write(putUp_report);
				putUp_report.close();
			} catch (IOException ioExp) {
				
				ioExp.printStackTrace();
			}
		}
		LCSLog.debug("### END HBILNBOMMigrator.processBOMMoa(String putupCode_inFiles) ###");
	}

	/**
	 * @param worksheet
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void processLTBOMMoa(String sap_key, Collection<Row> moaRows,String type)
			throws WTException, WTPropertyVetoException, IOException {

		LCSLog.debug(
				"### START HBILNBOMMigrator.processLTBOMMoa(String sap_key,Collection<Row> moaRows) ###");

		// Get All the SAP_Keys that have to be loaded.
		Row row = (Row) moaRows.iterator().next();
		String materialName = HBISPBomUtil.getCellValue(row, 0);
		type = HBISPBomUtil.getCellValue(row, 5);

		String path="";
		if("Dye Formula".equals(type)){
			path="Material\\Dye Formula";
		}
		else if("Prep Formula".equals(type)){
			path="Material\\Prep Formula";
		}
		else if("Finish Formula".equals(type)){
			path="Material\\Finish Formula";
		}
		LCSMaterial material=getMaterial(materialName,path);
		
		System.out.println("material object"+material);
		

		
		//attributionCode = HBISPBomUtil.getAttributionIda2a2(attributionCode);
		
		Transaction tr = null;
		try {
			tr = new Transaction();
			tr.start();

			

			String putUpCode = "";
			String primaryPutUp = "";
			String ref_putup = "";
			String material_number = "";
			Iterator itr = moaRows.iterator();
			while (itr.hasNext()) {
				row = (Row) itr.next();
				String supplier = HBISPBomUtil.getCellValue(row, 1);
				String matSupid=getMaterialSupplierMasterId(material,supplier);
				LCSMaterialSupplier matsup = (LCSMaterialSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterialSupplier:"+matSupid);
				String component = HBISPBomUtil.getCellValue(row, 2);
				System.out.println("component from excel:"+component);
				LCSMaterial componentMaterial=getMaterial(component,"Material\\Process Chemical");
				String componentNew = HBISPBomUtil.getCellValue(row, 4);
				System.out.println("componentNew from excel:"+componentNew);
				LCSMaterial componentMaterialNew=getMaterial(componentNew,"Material\\Process Chemical");

				
				new HBIComponentMassChanger().loadComponentMOA(matsup, componentMaterial,type,componentMaterialNew);


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

	private static LCSMaterial getMaterial(String name,String path) {
LCSMaterial material=null;
PreparedQueryStatement statement = new PreparedQueryStatement();
try {
	FlexType materialType=FlexTypeCache.getFlexTypeFromPath(path);
	statement.appendSelectColumn("LCSMaterial","branchiditerationinfo");
		statement.appendFromTable(LCSMaterial.class);
	statement.appendAndIfNeeded();
	statement.appendCriteria(
		new Criteria(new QueryColumn(LCSMaterial.class, materialType.getAttribute("name").getColumnDescriptorName()),"?", Criteria.EQUALS),name);//getVariableName()), "?", Criteria.EQUALS),name);
	statement.appendAndIfNeeded();
	statement.appendCriteria(new Criteria("LCSMaterial", "latestiterationinfo", "1", Criteria.EQUALS));
	statement.appendAndIfNeeded();

	statement.appendCriteria(
			new Criteria(new QueryColumn("LCSMaterial", "branchida2typedefinitionrefe"),"?", Criteria.EQUALS),FormatHelper.getNumericObjectIdFromObject(materialType));  //"IDA3A11"), "?", Criteria.EQUALS),
			//FormatHelper.getNumericObjectIdFromObject(materialType));
	SearchResults results = LCSQuery.runDirectQuery(statement);

	//
	if(results != null && results.getResultsFound() > 0)
	{
		FlexObject flexObj = (FlexObject) results.getResults().firstElement();
		System.out.println("flexObj:::::::::::::"+flexObj);

		material = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));
	}
	return material;
} catch (WTException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

return material;
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
		String criteriaAttDBColumn = businessObjFlexTypeObj.getAttribute(attributeKey).getVariableName();

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
	public void loadComponentMOA(LCSMaterialSupplier matsup, LCSMaterial component,String type,LCSMaterial componentNew) throws WTException, WTPropertyVetoException, NumberFormatException, IOException {
		LCSLog.debug(
				"### START HBIPutUpCodeLoader.loadPutUpCodeMoaTable(LCSProduct productObj, LCSSeason seasonObj, String putUpCode, String primaryPutUp, String ref_putup) ###");

		
		if (component == null) {
			throw new WTException("component is null");
		}

		 else {

			boolean isExisting = updateComponentMOAData(matsup, component,type,componentNew);
			if(("Dye Formula").equals(type)){
			matsup.setValue("hbiSendToAPSByPlant", new Date());
			}
			LCSLogic.persist(matsup,true);
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
	private boolean updateComponentMOAData(LCSMaterialSupplier ms, LCSMaterial component,String type,LCSMaterial componentNew) throws WTPropertyVetoException, WTException {
       String moaKey="";
		if("Dye Formula".equals(type)){
			moaKey="hbiDyeFormulaComponent";
        }else if("Finish Formula".equals(type)){
			moaKey="hbiFinishFormulaComponent";

        }
        else if("Prep Formula".equals(type)){
			moaKey="hbiPrepFormulaComponent";

        }
		LCSMOATable putUpMOATab = (LCSMOATable) ms.getValue(moaKey);
		if (putUpMOATab != null) {
			Collection<FlexObject> rowColl = putUpMOATab.getRows();
			if (rowColl.size() > 0) {
				return updateComponentMOA(ms, component,rowColl,componentNew);
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
	private static boolean updateComponentMOA(LCSMaterialSupplier ms, LCSMaterial comp,Collection<FlexObject> rowColl,LCSMaterial compNew) throws WTPropertyVetoException, WTException {
		Iterator itr = rowColl.iterator();
		while (itr.hasNext()) {
			FlexObject moaRow = (FlexObject) itr.next();
			String key = moaRow.getString("OID");
			LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery
					.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + key);
			LCSMaterial component = getComponent(moaObject);
		
				if (component==comp) {
					System.out.println("entered:::::::::::::::;;;;;");
					moaObject.getFlexType().getAttribute("hbiDyeFormulaComponent").setValue(moaObject, compNew);
					HBILNMOAPlugin.setComponentDescOnMOA(moaObject);

					LCSMOAObjectLogic.persist(moaObject, true);
					return true;
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
	public void createComponentMOAData(LCSMaterialSupplier ms, LCSMaterial comp, double usage,String uom,String type) throws WTException, WTPropertyVetoException {
		String moaKey="";
		if("Dye Formula".equals(type)){
			moaKey="hbiDyeFormulaComponent";
        }else if("Finish Formula".equals(type)){
			moaKey="hbiFinishFormulaComponent";

        }
        else if("Prep Formula".equals(type)){
			moaKey="hbiPrepFormulaComponent";

        }
		LCSMOATable putUpMOATab = (LCSMOATable) ms.getValue(moaKey);
		
		LCSLog.debug(
				"### START HBIPutUpCodeLoader.createPutupCodeMOAData(productObj, LCSLifecycleManaged putUpCodeObj, sortingNumber, primaryPutUp, ref_Spec) ###");
		String sortingNumber = "1";
		SearchResults moaResults = LCSMOAObjectQuery.findMOACollectionData( ms,
				ms.getFlexType().getAttribute(moaKey), "LCSMOAObject.createStampA2", true);
		System.out.println("  moaResults    " + moaResults.getResults().size());
		Collection<FlexObject> moaData = moaResults.getResults();
		if (moaResults != null && moaResults.getResultsFound() > 0) {

			String maxSortingNumber = FlexObjectUtil.maxValueForFlexObjects(moaData, "LCSMOAOBJECT.SORTINGNUMBER",
					"int");
			sortingNumber = Integer.toString((Integer.parseInt(maxSortingNumber) + 1));
		}
		// getOwnerVersion
		LCSMOAObject moaObject = LCSMOAObject.newLCSMOAObject();
		moaObject.setFlexType(ms.getFlexType().getAttribute(moaKey).getRefType());
		moaObject.setOwnerReference(((RevisionControlled) ms).getMasterReference());
		moaObject.setOwnerVersion(ms.getVersionIdentifier().getValue());
		//moaObject.setOwnerAttribute(ms.getFlexType().getAttribute(moaKey));
		moaObject.setBranchId(Integer.parseInt(sortingNumber));
		moaObject.setDropped(false);
		moaObject.setSortingNumber(Integer.parseInt(sortingNumber));
		moaObject.getFlexType().getAttribute("hbiDyeFormulaComponent").setValue(moaObject, comp);
		moaObject.getFlexType().getAttribute("hbiDyeFormulaComponentUsage").setValue(moaObject, usage);
		System.out.println("uom:::::::1::::::"+uom);
		if("%".equals(uom)){
			uom="hbiPercentage";
		}else if("G/L".equals(uom)){
			uom="hbiGL";
		}
		System.out.println("uom::::::2:::::::"+uom);

		moaObject.getFlexType().getAttribute("hbiDyeFormulaUsageType").setValue(moaObject, uom);
		LCSMOAObjectLogic.persist(moaObject);
		LCSMOATable.clearTableFromMethodContextCache((FlexTyped) ms,
				ms.getFlexType().getAttribute(moaKey));

		LCSLog.debug(
				"### END HBIPutUpCodeLoader.createPutupCodeMOAData(productObj, LCSLifecycleManaged putUpCodeObj, sortingNumber, primaryPutUp, ref_Spec) ###");
	}

	
	/**
	 * @param moaObject
	 * @return
	 * @throws WTException
	 */
	private static LCSMaterial getComponent(LCSMOAObject moaObject) throws WTException {
		LCSMaterial comp = (LCSMaterial) moaObject.getValue("hbiDyeFormulaComponent");
		if (comp != null) {
			return comp;
		} else {
			return null;
		}

	}

	private static String getMaterialSupplierMasterId(LCSMaterial mat,String Supplier) {
		// TODO Auto-generated method stub
		String matSupplierMasterid=null;
		try {
			SearchResults supplierresults = LCSMaterialSupplierQuery.findMaterialSuppliers(mat);
		   Collection coll=supplierresults.getResults();
		   Iterator itr=coll.iterator();
		   while(itr.hasNext()){
			   FlexObject obj=(FlexObject)itr.next();
			   System.out.println("obj::::::::::::::;"+obj);
			   String supp=obj.getData("LCSSUPPLIERMASTER.SUPPLIERNAME");
			   
			   if(Supplier.equals(supp)){
				   matSupplierMasterid= obj.getData("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO");
				   break;
			   }
		   }
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return matSupplierMasterid;
	}
}
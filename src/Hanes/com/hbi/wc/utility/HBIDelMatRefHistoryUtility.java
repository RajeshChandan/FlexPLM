package com.hbi.wc.utility;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.log4j.Logger;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

import com.hbi.wc.load.sploader.HBISPBomUtil;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.whereused.FAWhereUsedQuery;
import com.hbi.wc.util.logger.HBIUtilityLogger;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import wt.util.WTProperties;

import wt.fc.QueryResult;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.fc.PersistenceServerHelper;

/**
 * @author UST
 * September 2019
 * 
 */
public class HBIDelMatRefHistoryUtility implements RemoteAccess, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String FILE_IN_TYPE = ".xlsx";

	private static String floderPhysicalLocation = "";
	
	private static String listOfPlacesRemoved = "";

	private static RemoteMethodServer remoteMethodServer;
	
	public static final String logLevel = LCSProperties.get("com.hbi.util.logLevel");

	static Logger utilLogger = HBIUtilityLogger.createInstance(HBIDelMatRefHistoryUtility.class, logLevel, "HBIDelMatRefHistoryUtility.log");	

	static
	{
		try
		{
			
		WTProperties wtprops = WTProperties.getLocalProperties();
	        String home = wtprops.getProperty("wt.home");
	        floderPhysicalLocation = home + File.separator + "logs" + File.separator + "migration"+ File.separator;
	        if(!(new File(floderPhysicalLocation).exists()))
	        {
	        	new File(floderPhysicalLocation).mkdir();
	        }
		}
		catch (Exception exp)
		{
			utilLogger.debug("Exception in static block of the class HBIColorWhereUsedUtility is : "+ exp);
		}
	}	

	public static void main(String[] args) throws InvocationTargetException, WTException, IOException {
		if (args.length != 1) {
			utilLogger.debug(
					"Accepted command and input parameters:: java com.hbi.wc.utility.HBIDelMatRefHistoryUtility <fileName>");
			throw new WTException("java com.hbi.wc.utility.HBIDelMatRefHistoryUtility <fileName>");
		}

		String fileName = args[0];
		remoteMethodServer = RemoteMethodServer.getDefault();
		remoteMethodServer.setUserName("prodadmin");
		remoteMethodServer.setPassword("pass2014a");
		Class[] argTypes = { String.class };
		Object[] argValues = { fileName };

		try {
			
			remoteMethodServer.invoke("delObjRefHistory", "com.hbi.wc.utility.HBIDelMatRefHistoryUtility", null,
					argTypes, argValues);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}


	/**
	 * 
	 * @param args
	 * @throws WTException
	 */
	// Called from plugin entry
	public static void delMatRefHistory(LCSMaterial matObj) {
		NumberFormat formatter = new DecimalFormat("#0.00000");
		long startTime = System.currentTimeMillis();
		try {

			if (matObj != null) {

				utilLogger.debug("matObj ------>" + matObj.getName());
			
				Collection<FlexObject> objRefHis = new FAWhereUsedQuery().checkForObjectReferences(matObj, true);
				utilLogger.debug("Count of record in where used 1["+objRefHis.size()+"]");
				utilLogger.debug("objRefHis :: {"+objRefHis+"}");
				if(objRefHis.size() == 0) {
					utilLogger.debug("No Object Reference History available for the Material ------>"+ matObj.getName());			
				} else {				
					Iterator itr = objRefHis.iterator();
					while(itr.hasNext()) {
						FlexObject fObj = (FlexObject) itr.next();
						String strClass = fObj.getString("CLASS");
						String strType = strClass.substring(strClass.lastIndexOf(".")+1).toUpperCase();
						if(strType.equals("LCSPRODUCT")) {												
							String strIDa2a2 = fObj.getString(strType + ".IDA2A2");
							LCSProduct lcsProduct = null;
							if(FormatHelper.hasContent(strIDa2a2)) {
								lcsProduct = (LCSProduct) LCSQuery.findObjectById("OR:" + strClass + ":" + strIDa2a2);
								boolean cleared = clearMaterialObjRefForAllVersions(lcsProduct, fObj.getString("ATTKEY"), matObj);
								if(cleared) {
									utilLogger.debug("Material Object Reference Cleared from all versions from the Product History!!");
								} else {
									utilLogger.debug("Could not delete Material Object References from all versions from the Product History!!");
								}
							}
						}
					}
				}
				

			} 

			long totalTime = System.currentTimeMillis();

			utilLogger.debug("Final duration: " + formatter.format((totalTime - startTime) / 1000d) + " seconds");
			utilLogger.debug("Clearing Material history completed");

		} catch (Exception e1) {

			e1.printStackTrace();
		}
	}

	/**
	 * @param lcsProduct
	 * @param attKey
	 * @param matObj
	 */
	@SuppressWarnings("rawtypes")
	public static boolean clearMaterialObjRefForAllVersions(LCSProduct lcsProduct, String attKey, LCSMaterial matObj) {
		try { 
		QueryResult res = VersionControlHelper.service.allIterationsOf(((Versioned)lcsProduct).getMaster());
		//Collection data = new ArrayList();
		while(res.hasMoreElements()){
			Versioned ver = (Versioned) res.nextElement();
			/* To get Object Id
			ReferenceFactory referencefactory = new ReferenceFactory();
			WTReference wtreference = referencefactory.getReference(versioned);
			String obid = referencefactory.getReferenceString(wtreference);*/
			LCSProduct prodObj = (LCSProduct) ver;
			LCSMaterial matAtt = (LCSMaterial) prodObj.getValue(attKey);
			if(matAtt != null && matAtt.getName().equals(matObj.getName())) {		
				prodObj.setValue(attKey, "");
				PersistenceServerHelper.manager.update(prodObj);
				listOfPlacesRemoved = listOfPlacesRemoved + ", [ Product : " + prodObj.getName() + " , Version : " + VersionHelper.getFullVersionIdentifierValue(ver) 
											+ " ] ";
				utilLogger.debug("Material Object Reference deleted from the Product " + prodObj.getName());			
				utilLogger.debug("For the Version " +VersionHelper.getFullVersionIdentifierValue(ver));					
			}
		}
		return true;
		} catch(Exception e) {
			utilLogger.error("Exception occured " + e.getMessage() + ". Please check method server logs. ");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * @param str
	 * @throws WTException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void delObjRefHistory(String input_filenames) throws WTException, IOException {
		String arg[] = input_filenames.split(",");
		NumberFormat formatter = new DecimalFormat("#0.00000");
		int totalcount = 0;
		long totalstart = System.currentTimeMillis();
		for (String input_filename : arg) {
			int count = 0;

			long start = System.currentTimeMillis();
			utilLogger.debug("########## Started deleting object references for the materials from [" + input_filename + "] ###########");
			try {

				//long start_ReadingFile = System.currentTimeMillis();

				String report_file = floderPhysicalLocation + input_filename + "_Report" + FILE_IN_TYPE;

				// Write the workbook in file system
				FileOutputStream bom_report = new FileOutputStream(new File(report_file));
				// Create blank workbook
				XSSFWorkbook workbook = new XSSFWorkbook();
				// Create a blank sheet
				XSSFSheet spreadsheet = workbook.createSheet("MATDeleteReport");

				int rowid = 0;
				// Create row object
				XSSFRow row_out = spreadsheet.createRow(rowid++);
				Cell cell0 = row_out.createCell(0);
				Cell cell1 = row_out.createCell(1);
				Cell cell2 = row_out.createCell(2);
				Cell cell3 = row_out.createCell(3);
				Cell cell4 = row_out.createCell(4);
				cell0.setCellValue("MATERIAL NAME");
				cell1.setCellValue("STATUS");
				cell2.setCellValue("RUNTIME");
				cell3.setCellValue("COMMENTS");
				cell4.setCellValue("LIST OF PLACES REMOVED");
				String loader_input_file = floderPhysicalLocation + input_filename + FILE_IN_TYPE;
				// Get All the SAP_Keys that have to be loaded.
				Collection<String> matColl = getMatCollection(loader_input_file);
				count = count + matColl.size() - 1;
				totalcount = totalcount + matColl.size() - 1;
				long End_ReadingFile = System.currentTimeMillis();
				utilLogger.debug("########## Total Materials found from File [" + input_filename + " ] is ["
						+ matColl.size() + "]");

				Iterator itr = matColl.iterator();
				while (itr.hasNext()) {
					String mat = (String) itr.next();
					if (!"MATERAIL_NAME".equals(mat)) {
						utilLogger.debug("########## Started Transactions For MATERIAL :: [" + mat + "]");

						long start_sapKey = System.currentTimeMillis();
						row_out = spreadsheet.createRow(rowid++);
						cell0 = row_out.createCell(0);
						cell1 = row_out.createCell(1);
						cell2 = row_out.createCell(2);
						cell3 = row_out.createCell(3);
						cell4 = row_out.createCell(4);

						cell0.setCellValue(mat);

						try {
							// Get respective Material in FlexPLM for the
							// Name
							LCSMaterial matObj = getMatObj(mat);
							
							//Deleting logic
							delMatRefHistory(matObj);

							utilLogger.debug("########## Completed Transactions for Material :: [" + mat
									+ "], successfully #######");
							cell1.setCellValue("SUCCESS");
							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue("");
							cell4.setCellValue(listOfPlacesRemoved);
						} catch (Exception e) {
							String error = "!!!! Exception occured for Material :: [" + mat + "]";
							utilLogger.debug(error + ",\n[ERROR MESSAGE :: " + e.getLocalizedMessage() + "]");
							utilLogger.debug(
									"!!!!! Completed Transactions for Material :: [" + mat + "], with Errors !!!!");

							// ERROR Report has to be implemented
							HBISPBomUtil.exception(error, e);
							// HBISPBomUtil.report(sap_key,row,"Failed",e);
							cell1.setCellValue("FAILED");

							long end_sapKey = System.currentTimeMillis();
							cell2.setCellValue(formatter.format((end_sapKey - start_sapKey) / 1000d));
							cell3.setCellValue(e.getLocalizedMessage());
						}
					} else {
						utilLogger.debug("!!!!! Skipping Header row found !!!!");

					}

				}

				// Write the Report details into file
				workbook.write(bom_report);
				bom_report.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			long end = System.currentTimeMillis();

			utilLogger.debug("##########              	                        ###########\n");

			utilLogger.debug("########## Count of total deleted MATERIAL Object References  [" + count
					+ "], " + " from input file [ " + input_filename + ".xlsx], in [ "
					+ formatter.format((end - start) / 1000d) + " seconds ]    ###########\n");

		}

		long totalend = System.currentTimeMillis();
		utilLogger.debug("########## Completed  deletion of MATERIAL Object References count [" + totalcount
				+ "], " + "from the following all input files [ " + input_filenames + "], in [ "
				+ formatter.format((totalend - totalstart) / 1000d) + " seconds ]    ###########\n");

		utilLogger.debug("########## Check Report and logs for more loader details ###########\n");

		utilLogger.debug("#####################         END              ###########\n");

	}

	/**
	 * @param loader_input_file
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Collection<String> getMatCollection(String loader_input_file) throws IOException {
		Collection<String> matCol = new ArrayList();
		// Create Workbook instance holding reference to .xlsx file
		FileInputStream file = new FileInputStream(new File(loader_input_file));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String MatName = getCellValue(row, 0);
			matCol.add(MatName);

		}
		file.close();
		return matCol;
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
	 * @param matName
	 * @return
	 * @throws WTException
	 */
	public static LCSMaterial getMatObj(String matName) throws WTException {
		LCSMaterial matObj = null;
		if (FormatHelper.hasContent(matName)) {
			// FlexType finishFormulaMaterialType =
			// FlexTypeCache.getFlexTypeFromPath(MATERIAL_CASING_TYPE);
			LCSMaterialQuery materialQueryObject = new LCSMaterialQuery();
			matObj = materialQueryObject.findMaterialByNameType(matName, null);
			if (matObj != null) {
				return matObj;
			} else {
				throw new WTException("Material not found in flexPLM for [matName ::" + matName + "]");
			}
		}
		return matObj;

	}
	

}

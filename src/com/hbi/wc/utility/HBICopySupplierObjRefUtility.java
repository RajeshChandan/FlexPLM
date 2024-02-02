package com.hbi.wc.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.Date;

import com.lcs.wc.material.*;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTProperties;

import com.hbi.wc.util.logger.HBIUtilityLogger;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import wt.fc.WTObject;
import com.lcs.wc.foundation.LCSRevisionControlled;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTyped;

import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.foundation.LCSObject;

import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.supplier.LCSSupplierQuery;

import wt.part.WTPartMaster;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.whereused.FAWhereUsedQuery;
import com.lcs.wc.whereused.WhereUsedQuery;
import wt.pom.Transaction;

/**
 * @author UST
 * 
 * 
 */
public class HBICopySupplierObjRefUtility implements RemoteAccess{


	private static String floderPhysicalLocation = "";
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");	
	private static RemoteMethodServer remoteMethodServer;
    public static final String logLevel = LCSProperties.get("com.hbi.util.logLevel");


    static Logger utilLogger = HBIUtilityLogger.createInstance(HBICopySupplierObjRefUtility.class, logLevel, "HBICopySupplierObjRefUtility.log");
	static Transaction tr = null;
	
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
	
	//private static String fileName = floderPhysicalLocation + "supplierCopyList.xlsx";
	
	public static void main(String[] args) {
		utilLogger.debug("### START HBICopySupplierObjRefUtility.main() ###");

		try {
			if (args.length != 1) {
				utilLogger.debug("windchill com.hbi.wc.utility.HBICopySupplierObjRefUtility <fileName>");
			}

			remoteMethodServer = RemoteMethodServer.getDefault();
			remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

			Class<?> argTypes[] = { String.class };
			String argValues[] = { args[0] };
			remoteMethodServer.invoke("copySupplierObjRefs", "com.hbi.wc.utility.HBICopySupplierObjRefUtility",
					null, argTypes, argValues);
			System.exit(0);
		} catch (Exception exp) {
			exp.printStackTrace();
			System.exit(1);
		}

		utilLogger.debug("### END HBICopySupplierObjRefUtility.main() ###");
	}
	
	/**
	 * 
	 * @param args
	 * @throws WTException
	 */
	//Called from plugin entry
	public static void copySupplierObjRefs(String fileName) {
		NumberFormat formatter = new DecimalFormat("#0.00000");
		utilLogger.debug("Inside ........." + fileName);
		long startTime = System.currentTimeMillis();
		try{
				
				//Get supplier names from excel file and iterate each line.
				utilLogger.debug("File " + floderPhysicalLocation + fileName +".xlsx" + " opening");
				FileInputStream file = new FileInputStream(new File(floderPhysicalLocation + fileName +".xlsx"));
				
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				// Get first/desired sheet from the workbook
				XSSFSheet sheet = workbook.getSheetAt(0);
				// Iterate through each rows one by one
				Iterator<Row> rowIterator = sheet.iterator();
				utilLogger.debug("Started.........");
				DataFormatter cellformatter = new DataFormatter();
				
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					String srcSupplier = cellformatter.formatCellValue(row.getCell(0));
					String srcVendorMasterCd = cellformatter.formatCellValue(row.getCell(1));
					String tgtSupplier = cellformatter.formatCellValue(row.getCell(2));
					String tgtVendorMasterCd = cellformatter.formatCellValue(row.getCell(3));					
					String listOfPlacesRemoved = "";
					if(FormatHelper.hasContent(srcSupplier) && FormatHelper.hasContent(srcVendorMasterCd) && !srcSupplier.equals("FROM")) {
						LCSSupplier srcSupplierObj = getSupplier(srcSupplier, srcVendorMasterCd);
						LCSSupplier tgtSupplierObj = null;
						if(FormatHelper.hasContent(tgtSupplier) && FormatHelper.hasContent(tgtVendorMasterCd) && !tgtSupplier.equals("TO")) {
							tgtSupplierObj = getSupplier(tgtSupplier, tgtVendorMasterCd);
						}
						if(srcSupplierObj != null && tgtSupplierObj != null) {
							copySupplierDataFromSrcToTgt(srcSupplierObj, tgtSupplierObj);
						}

					} }

				file.close();
			      
			      FileOutputStream supplierHistory = new FileOutputStream(new File(floderPhysicalLocation+"supplierHistoryResult.xlsx"));
			      workbook.write(supplierHistory);
			      supplierHistory.close();

		long totalTime   = System.currentTimeMillis();
		 
		
		utilLogger.debug("Final duration: "+formatter.format((totalTime - startTime) / 1000d) + " seconds");
		utilLogger.debug("Copy process completed");
		
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
	}
	
	public static void copySupplierDataFromSrcToTgt(LCSSupplier srcSupplierObj, LCSSupplier tgtSupplierObj) {
		boolean copied = copyMaterialsFromSrcToTgt(srcSupplierObj, tgtSupplierObj);
		if(copied) {
			utilLogger.debug("MATERIALS COPY FROM " + srcSupplierObj.getSupplierName() + " TO " + tgtSupplierObj.getSupplierName() + " COMPLETED!!!!" );
		} else {
			utilLogger.debug("FAILED TO COPY MATERIALS FROM " + srcSupplierObj.getSupplierName() + " TO " + tgtSupplierObj.getSupplierName() + " !!!!" );
		}
		
		copied = copyObjRefsFromSrcToTgt(srcSupplierObj, tgtSupplierObj);
		if(copied) {
			utilLogger.debug("OBJECT REFERENCES COPY FROM " + srcSupplierObj.getSupplierName() + " TO " + tgtSupplierObj.getSupplierName() + " COMPLETED!!!!" );
		} else {
			utilLogger.debug("FAILED TO COPY MATERIALS FROM " + srcSupplierObj.getSupplierName() + " TO " + tgtSupplierObj.getSupplierName() + " !!!!" );
		}		
		
		/*copied = copyObjRefHistoryFromSrcToTgt(srcSupplierObj, tgtSupplierObj);
		if(copied) {
			utilLogger.debug("OBJECT REFERENCE HISTORY COPY FROM " + srcSupplierObj.getSupplierName() + " TO " + tgtSupplierObj.getSupplierName() + " COMPLETED!!!!" );
		} else {
			utilLogger.debug("FAILED TO COPY MATERIALS FROM " + srcSupplierObj.getSupplierName() + " TO " + tgtSupplierObj.getSupplierName() + " !!!!" );
		}*/
	}
	
	public static boolean copyObjRefHistoryFromSrcToTgt(LCSSupplier srcSupplierObj, LCSSupplier tgtSupplierObj) {
			boolean copied = false;
			try {
				tr = new Transaction();
				tr.start();		
				utilLogger.debug("new FAWhereUsedQuery() ------>"+  new FAWhereUsedQuery().checkForObjectReferences(srcSupplierObj, true));
				Collection<FlexObject> results =  new FAWhereUsedQuery().checkForObjectReferences(srcSupplierObj, true);
				if(results.size() == 0) {
					utilLogger.debug("No Object Reference History available for the Supplier ------>"+ srcSupplierObj.getSupplierName());
					return true;				
				} else {
					Iterator resultItr = results.iterator();
					while(resultItr.hasNext()) {
						FlexObject fobj = (FlexObject) resultItr.next();
						String strClass = fobj.getString("CLASS");
						utilLogger.debug("CLASS " + strClass);
						String strType = strClass.substring(strClass.lastIndexOf(".")+1).toUpperCase();
						String strIDa2a2 = fobj.getString(strType + ".IDA2A2");
						LCSObject lcsObject = null;
						if(FormatHelper.hasContent(strIDa2a2)) {
							WTObject obj = (WTObject) LCSQuery.findObjectById("OR:" + strClass + ":" + strIDa2a2);
							if(obj instanceof LCSMOAObject) {
								LCSMOAObject moa = (LCSMOAObject) obj; 
								utilLogger.debug("Owner of moa Object " + moa.getOwner());
								moa.setDropped(true);
								LCSMOAObjectLogic.deleteObject(moa);
							} else if(obj instanceof LCSSourcingConfig) {
								String strIter = fobj.getString(strType +".BRANCHIDITERATIONINFO");
								LCSSourcingConfig srcCfg = (LCSSourcingConfig) LCSQuery.findObjectById("VR:" + strClass + ":" + strIter);
								srcCfg = (LCSSourcingConfig) VersionHelper.latestIterationOf(srcCfg.getMaster());
								srcCfg.setValue(fobj.getString("ATTKEY"), srcCfg.getValue(fobj.getString("ATTKEY")));
								LCSLogic.persist(srcCfg,true);								
							} 
						}							
					}
				}				
				copied = true;
			} catch(Exception ex) {
				utilLogger.error(ex.getMessage() + ". Please check method server logs.");
				ex.printStackTrace();
				copied = false;
			}
			return 	copied;
	}
	
	public static boolean copyObjRefsFromSrcToTgt(LCSSupplier srcSupplierObj, LCSSupplier tgtSupplierObj) {
			boolean copied = false;
			try {
				tr = new Transaction();
				tr.start();			
				utilLogger.debug("new FAWhereUsedQuery() ------>"+ new FAWhereUsedQuery().checkForObjectReferences(srcSupplierObj));
				Collection<FlexObject> results = new FAWhereUsedQuery().checkForObjectReferences(srcSupplierObj);
				if(results.size() == 0) {
					utilLogger.debug("No Object References available for the Supplier ------>"+ srcSupplierObj.getSupplierName());
					return true;				
				} else {
					Iterator resultItr = results.iterator();
					while(resultItr.hasNext()) {
						FlexObject fobj = (FlexObject) resultItr.next();
						String strClass = fobj.getString("CLASS");
						utilLogger.debug("CLASS " + strClass);
						String strType = strClass.substring(strClass.lastIndexOf(".")+1).toUpperCase();
						String strIDa2a2 = fobj.getString(strType + ".IDA2A2");
						LCSObject lcsObject = null;
						if(FormatHelper.hasContent(strIDa2a2)) {
							WTObject obj = (WTObject) LCSQuery.findObjectById("OR:" + strClass + ":" + strIDa2a2);
							if(obj instanceof LCSSourcingConfig) {
								String strIter = fobj.getString(strType +".BRANCHIDITERATIONINFO");
								LCSSourcingConfig srcCfg = (LCSSourcingConfig) LCSQuery.findObjectById("VR:" + strClass + ":" + strIter);
								srcCfg = (LCSSourcingConfig) VersionHelper.latestIterationOf(srcCfg.getMaster());
								srcCfg.setValue(fobj.getString("ATTKEY"), tgtSupplierObj);
								LCSLogic.persist(srcCfg,true);									
							} else {
								lcsObject = (LCSObject) obj;
								lcsObject.setValue(fobj.getString("ATTKEY"), tgtSupplierObj);
								LCSLogic.persist(lcsObject,true);								
							}
							
						}
					}
					
				}
				tr.commit();
				copied = true;
			} catch(Exception ex) {
				utilLogger.error(ex.getMessage() + ". Please check method server logs.");
				ex.printStackTrace();
				tr.rollback();
				copied = false;
			}
			return 	copied;
	}
	
	public static boolean copyMaterialsFromSrcToTgt(LCSSupplier srcSupplierObj, LCSSupplier tgtSupplierObj) {
			boolean copied = false;
			try {
				tr = new Transaction();
				tr.start();				
				LCSMaterialSupplierQuery matSupQry = new LCSMaterialSupplierQuery();
				LCSSupplierMaster srcSupplierObjMaster = (LCSSupplierMaster)srcSupplierObj.getMaster();
				LCSSupplierMaster tgtSupplierObjMaster = (LCSSupplierMaster)tgtSupplierObj.getMaster();
				Collection<LCSMaterial> materialCol = matSupQry.findMaterials(srcSupplierObjMaster);

				utilLogger.debug("Materials for the Supplier ------>"+ materialCol);
				
				if(materialCol.size() == 0) {
					utilLogger.debug("No Materials available for the Supplier ------>"+ srcSupplierObj.getSupplierName());
					return true;
				}
				for(LCSMaterial materialObj : materialCol){
					boolean isLatestIteration = VersionHelper.isLatestIteration(materialObj);
					
					if(isLatestIteration) {
						utilLogger.debug("Source Supplier Master id " + FormatHelper.getObjectId((LCSSupplierMaster)srcSupplierObjMaster));							
						utilLogger.debug("Target Supplier Master id " + FormatHelper.getObjectId((LCSSupplierMaster)tgtSupplierObjMaster));
						LCSMaterialMaster materialMaster =(LCSMaterialMaster) materialObj.getMaster();
						LCSMaterialSupplier matSuppObj = new LCSMaterialSupplierQuery().findMaterialSupplier(materialMaster,srcSupplierObjMaster);
						if(matSuppObj != null) {

							LCSMaterialSupplierMaster matSupMaster = (LCSMaterialSupplierMaster) matSuppObj.getMaster();
							matSupMaster.setSupplierMaster(tgtSupplierObjMaster);
							
							//matSuppObj.setActive(true);
							LCSLogic.persist(matSupMaster, true);
						}
					}
				}
				copied = true;				
				tr.commit();
			} catch (Exception ex) {
				utilLogger.error(ex.getMessage() + ". Please check method server logs.");
				ex.printStackTrace();
				tr.rollback();
				copied = false;
			}
		return copied;				
	}


	/**
	 * @param vendorMasterCode 
	 * @param name
	 * 
	 * @throws NamingException
	 * @throws IOException
	 * @throws WTException
	 */
	public static LCSSupplier getSupplier(String name, String vendorMasterCode) throws WTException{
		LCSSupplier supplierObj = null;
		FlexType supplierType = FlexTypeCache.getFlexTypeFromPath("Supplier\\Supplier");
		if(!FormatHelper.hasContent(name)) {
			return supplierObj;
		}
		Collection supCol = findSuppliersByNameType(name, supplierType);
		Iterator supColItr = supCol.iterator();
		while(supColItr.hasNext()) {
			FlexObject obj = (FlexObject) supColItr.next();
			supplierObj = (LCSSupplier) LCSQuery.findObjectById(
				"OR:com.lcs.wc.supplier.LCSSupplier:" + obj.getString("LCSSUPPLIER.IDA2A2"));
			if(supplierObj.getValue("hbiMasterCode").equals(vendorMasterCode)) {
				return supplierObj;
			}
		}

		utilLogger.debug( "From Supplier Collection " + supCol);
		return supplierObj;

	}

	private static Collection findSuppliersByNameType(String name, FlexType supplierType) throws WTException {
	
		// TODO Auto-generated method stub
		Collection blank = new ArrayList();
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(LCSSupplierMaster.class);
		statement.appendFromTable(LCSSupplier.class);
		statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendJoin(new QueryColumn(LCSSupplierMaster.class, "thePersistInfo.theObjectIdentifier.id"),
				new QueryColumn(LCSSupplier.class, "masterReference.key.id"));
		statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplierMaster.class, "supplierName"), "?", "="),
				name);
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "checkoutInfo.state"), "wrk", "<>"));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "iterationInfo.latest"), "1", "="));
		if (supplierType != null) {
			String id = String.valueOf(supplierType.getPersistInfo().getObjectIdentifier().getId());
			statement.appendAnd();
			statement.appendCriteria(
					new Criteria(new QueryColumn(LCSSupplier.class, "flexTypeReference.key.id"), "?", "="),
					new Long(id));
		}	

		SearchResults results = LCSQuery.runDirectQuery(statement);
				utilLogger.debug("results size: "+results.getResults().size());
				if(results.getResults().size() > 0)
				{
					return results.getResults();
				}

		return blank;
	}
}

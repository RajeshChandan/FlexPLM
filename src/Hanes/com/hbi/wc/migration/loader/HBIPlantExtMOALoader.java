package com.hbi.wc.migration.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import wt.enterprise.RevisionControlled;
import wt.httpgw.GatewayAuthenticator;
import wt.part.WTPartMaster;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.io.FileWriter;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MultiObjectHelper;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.moa.LCSMOACollectionClientModel;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.util.FlexObjectUtil;
import com.lcs.wc.supplier.LCSSupplier;

import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

/**
 * HBIPlantExtMOALoader.java
 *
 * This class contains stand alone functions using to read data from excel file, Load plant data. 
 * This will not erase previous plants and will not load same plant again.
 * @author Jeyaganeshan.Ramachandran@Hanes.com
 * @since Nov-2019
 */
public class HBIPlantExtMOALoader implements RemoteAccess
{
	
	private static RemoteMethodServer remoteMethodServer;
	
	private static String attributionCodeTypePath = LCSProperties.get("com.hbi.wc.utility.HBIPlantExtMOALoader.attributionCodeTypePath", "Business Object\\Automation Support Tables\\Attribution Codes and Descriptions");
	private static String sapSpecialProcurementTypePath = LCSProperties.get("com.hbi.wc.utility.HBIPlantExtMOALoader.sapSpecialProcTypePath", "Business Object\\Automation Support Tables\\SAP Special Procurement");
	
	private static String sellingProductTypePath = LCSProperties.get("com.hbi.wc.utility.HBIPlantExtMOALoader.sellingProductTypePath", "Product\\BASIC CUT & SEW - SELLING");
	private static String factoryTypePath = LCSProperties.get("com.hbi.wc.utility.HBIPlantExtMOALoader.factoryTypePath", "Supplier\\Factory");
	private static String plantExtensionTypePath = LCSProperties.get("com.hbi.wc.migration.loader.HBIPlantExtMOALoader.plantExtensionTypePath", "Multi-Object\\Plant Extensions");
	private static String plantNameAttKey = LCSProperties.get("com.hbi.wc.migration.loader.HBIPlantExtMOALoader.plantNameAttKey", "hbiPlantName1");
	
	private static String primaryDelivery = LCSProperties.get("com.hbi.wc.migration.loader.HBIPlantExtMOALoader.plantStatusAttKey", "hbiPrimaryDeliverPlant");
	private static String plantType = LCSProperties.get("com.hbi.wc.migration.loader.HBIPlantExtMOALoader.plantType", "hbiPlantType");
	private static String maxLotSize = LCSProperties.get("com.hbi.wc.migration.loader.HBIPlantExtMOALoader.maxLotSize", "hbiMaxLotSize");
	private static String plannedDelTime = LCSProperties.get("com.hbi.wc.migration.loader.HBIPlantExtMOALoader.plannedDelTime", "hbiPlannedDelTime");
	private static String totalRepLeadTme = LCSProperties.get("com.hbi.wc.migration.loader.HBIPlantExtMOALoader.totalRepLeadTme", "hbiTotalRepLeadTme");
	private static String procurementType = LCSProperties.get("com.hbi.wc.migration.loader.HBIPlantExtMOALoader.procurementType", "hbiProcurementType");
	private static String specialProcurement = LCSProperties.get("com.hbi.wc.migration.loader.HBIPlantExtMOALoader.specialProcurement", "hbiSpecialProcurement");
	private static String hbiSynchedStatus = LCSProperties.get("com.hbi.wc.moa.hbiSynchedStatus", "hbiSynchedStatus");

	private static String rowLine ="rowLine";
	private static String sapKey ="sapKey";
	
	private static String plantSAPCode ="plantSAPCode";
	private static String sellingStyleNumber ="sellingStyleNumber";
	private static String description ="description";
	private static String apsPackQuantity ="apsPackQuantity";
	private static String attributionCode ="attributionCode";
	
	private static String floderPhysicalLocation = "";
	
	static BufferedWriter logger=null;
	static DateFormat fullFormat=null;
	static Date objDate=null;
	
	static
	{
		try
		{
			WTProperties wtprops = WTProperties.getLocalProperties();
	        String home = wtprops.getProperty("wt.home");
	        floderPhysicalLocation = home + File.separator + "logs" + File.separator + "migration";
	        if(!(new File(floderPhysicalLocation).exists()))
	        {
	        	new File(floderPhysicalLocation).mkdir();
	        }
		}
		catch (Exception exp)
		{
			LCSLog.debug("Exception in static block of the class HBIPlantExtMOALoader is : "+ exp);
		}
	}
	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		System.out.println("LoadPantMOA " + args[0]);
		remoteinvoke(args[0]);
	}
	
	/**
	 * Default executable method of the class HBIPlantExtMOALoader
	 * @param args - String[]
	 */
	
	public static void remoteinvoke(String option)
	{
		LCSLog.debug("### START HBIPlantExtMOALoader.main() ###");
		long start = System.currentTimeMillis();
		try
		{
			
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
			Object[] argValues = { option };
			remoteMethodServer.invoke("loadPlantExtensionFromWS", "com.hbi.wc.migration.loader.HBIPlantExtMOALoader", null, argTypes, argValues);
			long end = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.print("Execution time is :-->"
					+ formatter.format((end - start) / 1000d) + " seconds");
			
			System.out.println("\n####### Ended Remote method server connection, please check logs in migration #####");
			System.out.println("####### Successfully logged off #####");
	        System.exit(0);
		}
		catch (Exception exp)
		{	
			exp.printStackTrace();
			
		}
		
		LCSLog.debug("### END Plant MOA main() ###");
	}
	

	
	public static void loadPlantExtensionFromWS(String productDataFileName) throws WTException, WTPropertyVetoException, IOException
	{
		
		FileInputStream fileInputStreamObj = null;
		try
		{
			fileInputStreamObj = new FileInputStream(floderPhysicalLocation+File.separator+productDataFileName+".xls");
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);
			
			loadPlantExtension( worksheet);
		}
		catch (IOException ioExp)
		{
			LCSLog.debug("IOException in HBIPlantExtMOALoader.loadPlantExtensionFromWS() is "+ioExp);
			ioExp.printStackTrace();
		}
		finally
    	{
    		if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
    	}
		
		
	}
	
	public static void loadPlantExtension( HSSFSheet worksheet) throws WTException, WTPropertyVetoException, IOException
	{
		
		HSSFRow row = null;		
		DataFormatter formatter = new DataFormatter();
		//Iterating through each rows of the given excel, initialize 'Attribution Code', 'Description', 'APS Pack Quantity' and 'Selling Style Number' 
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row == null)
				break;
			HashMap<String,String> dataMap = new HashMap();
	
			dataMap.put(rowLine,Integer.toString(i));
			
			dataMap.put(sapKey,formatter.formatCellValue(row.getCell(0)));
	
			dataMap.put(plantSAPCode,formatter.formatCellValue(row.getCell(1)));
		
			dataMap.put(primaryDelivery,formatter.formatCellValue(row.getCell(2)));

			dataMap.put(plantType,formatter.formatCellValue(row.getCell(3)));
			
			dataMap.put(maxLotSize,formatter.formatCellValue(row.getCell(4)));
			
			dataMap.put(plannedDelTime,formatter.formatCellValue(row.getCell(5)));

			dataMap.put(totalRepLeadTme,formatter.formatCellValue(row.getCell(6)));
			
			dataMap.put(procurementType,formatter.formatCellValue(row.getCell(7)));
			
			dataMap.put(specialProcurement,formatter.formatCellValue(row.getCell(8)));
			
			dataMap.put(sellingStyleNumber,formatter.formatCellValue(row.getCell(9)));

			dataMap.put(description,formatter.formatCellValue(row.getCell(10)));
			
			dataMap.put(apsPackQuantity,formatter.formatCellValue(row.getCell(11)));
			
			dataMap.put(attributionCode,formatter.formatCellValue(row.getCell(12)));
			
			
			loadPlantExtensionMOA(dataMap );
		}
	}
	
	public static void loadPlantExtensionMOA(HashMap<String,String> dataMap ) throws WTException, WTPropertyVetoException, IOException
	{
		
		int attributionCodeIdentifier = 0;
		
		LCSLifecycleManaged attributionCodeRefObj = getLifecycleManagedByCriteria("hbiErpAttributionCode", dataMap.get(attributionCode), attributionCodeTypePath);
		if(attributionCodeRefObj != null)
			attributionCodeIdentifier = Integer.parseInt(FormatHelper.getNumericObjectIdFromObject(attributionCodeRefObj));
		
		LCSProduct productObj = findProductByCriteria(attributionCodeIdentifier, dataMap.get(description), dataMap.get(apsPackQuantity), dataMap.get(sellingStyleNumber));
		
		
		
		if(productObj == null)
		{
			logInfo(dataMap.get(sapKey)+"	"+"Product Not Found for Row"+"	"+dataMap.get(rowLine)+"	"+ dataMap.get(sellingStyleNumber) +"	"+ dataMap.get(attributionCode)+"	"+ dataMap.get(description)+"	"+ dataMap.get(apsPackQuantity) );
		}
		else
		{
			new HBIPlantExtMOALoader().loadPlantData(productObj, dataMap);
		}	
	}
		
	public static LCSLifecycleManaged getLifecycleManagedByCriteria(String attributeKey, String attributeValue, String businessObjTypePath) throws WTException, WTPropertyVetoException
	{
		
		LCSLifecycleManaged attributionCodeRefObj = null;
		FlexType businessObjFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjTypePath);
		String criteriaAttDBColumn = businessObjFlexTypeObj.getAttribute(attributeKey).getColumnDescriptorName();//.getColumnName();//.getVariableName();
		
		//Initializing the PreparedQueryStatement, which is using to get Business-object based on the given set of parameters(like FlexTypePath of the object) 
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeIdPath"), businessObjFlexTypeObj.getTypeIdPath(), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, criteriaAttDBColumn), attributeValue.trim(), Criteria.EQUALS));
		
		//
		System.out.println(">>>>>>>>>>>>>>getLifecycleManagedByCriteria LCSLifecycleManaged>>>>>>>>>"+statement);
		SearchResults results = LCSQuery.runDirectQuery(statement);
		
		if(results != null && results.getResultsFound() > 0)
		{
			
			FlexObject flexObj = (FlexObject) results.getResults().firstElement();
			attributionCodeRefObj = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+flexObj.getString("LCSLifecycleManaged.IDA2A2"));
		}
		
	
		return attributionCodeRefObj;
	}
	
	public static LCSProduct findProductByCriteria(int attributionCodeIdentifier, String description, String apsPackQuantityStr, String styleNumber) throws WTException
	{
		
		LCSProduct productObj = null;
		FlexType productFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(sellingProductTypePath);
		String attributionCodeDBColumn = productFlexTypeObj.getAttribute("hbiErpAttributionCode").getColumnDescriptorName();//.getColumnName();//.getVariableName();
		String descriptionDBColumn = productFlexTypeObj.getAttribute("hbiDescription").getColumnDescriptorName();//.getColumnName();//.getVariableName();
		String apsPackQtyDBColumn = productFlexTypeObj.getAttribute("hbiAPSPackQuantity").getColumnDescriptorName();//.getColumnName();//.getVariableName();
		String styleNumberDBColumn = productFlexTypeObj.getAttribute("hbiSellingStyleNumber").getColumnDescriptorName();//.getColumnName();//.getVariableName();
		//String typeIdPath = String.valueOf(productFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
		String objecId = FormatHelper.getObjectId(productFlexTypeObj);
		String OID = FormatHelper.getNumericFromOid(objecId);
		int apsPackQuantity = Integer.parseInt(apsPackQuantityStr);
		
		//Initializing the PreparedQueryStatement, which is using to get LCSProduct object based on the given set of parameters(productName and Product FlexType ID Path)
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendSelectColumn(new QueryColumn(LCSProduct.class, "iterationInfo.branchId"));
		statement.appendFromTable(LCSProduct.class);
		statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "typeDefinitionReference.key.branchId"), "?", "="), new Long(OID));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "iterationInfo.latest"), "1", Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "versionInfo.identifier.versionId"), "A", Criteria.EQUALS));
		
		//
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, attributionCodeDBColumn), "?", "="), new Long(attributionCodeIdentifier));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, descriptionDBColumn), description, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, apsPackQtyDBColumn), "hbi"+apsPackQuantity, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, styleNumberDBColumn), styleNumber, Criteria.EQUALS));
		//System.out.println(statement);
		
		//Get FlexObject from the SearchResults instance, using to form LCSProduct instance, which is needed to return from function header to calling function
		System.out.println(">>>>>>>>>>>>>>>.findProductByCriteria>>>>>>>>>>>>>>>>>"+statement);
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if(results != null && results.getResultsFound() > 0)
		{
			FlexObject flexObj = (FlexObject)results.getResults().get(0);
	       	productObj = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+ flexObj.getString("LCSProduct.BRANCHIDITERATIONINFO"));
	       	productObj = (LCSProduct) VersionHelper.latestIterationOf(productObj);
		}
		
		
		return productObj;
	}
	
	@SuppressWarnings("unchecked")
	public void loadPlantData(LCSProduct productObj,HashMap<String,String> dataMap ) throws WTException, WTPropertyVetoException, IOException
	{
	System.out.println("----------------------Started Loading data for Plant-----------------------");
		//LCSProduct productObj = SeasonProductLocator.getProductSeasonRev(seasonProductLinkObj);
		String sortingNumber = "1";
		boolean addPlantToPlantExt = true;
		
		LCSSupplier plantObject = getSAPPlanFromPlantCOde("hbiSAPPlantCode", dataMap.get(plantSAPCode), factoryTypePath);
		if(plantObject == null)
		{
			logInfo(dataMap.get(sapKey)+"	"+"Plant Not Found for Row"+"	"+dataMap.get(rowLine)+"	"+ dataMap.get(sellingStyleNumber) +"	"+ dataMap.get(attributionCode)+"	"+ dataMap.get(description)+"	"+ dataMap.get(plantSAPCode) );
		}
		else
		{
			SearchResults moaResults = LCSMOAObjectQuery.findMOACollectionData((LCSPartMaster)productObj.getMaster(), productObj.getFlexType().getAttribute("hbiErpPlantExtensions"), "LCSMOAObject.createStampA2", true);
			if(moaResults != null && moaResults.getResultsFound() > 0)
			{
				Collection<FlexObject> moaData = moaResults.getResults();
				FlexTypeAttribute fta = productObj.getFlexType().getAttribute("hbiErpPlantExtensions");

				
				LCSMOATable table=(LCSMOATable)productObj.getValue("hbiErpPlantExtensions");
				Collection coll=table.getRows();
				System.out.println("----------------------coll-----------------------"+coll);

				Iterator itr=coll.iterator();
				LCSMOAObjectLogic logic=new LCSMOAObjectLogic();

				while(itr.hasNext()){
					FlexObject obj=(FlexObject)itr.next();
					System.out.println("----------------------obj-----------------------"+obj);
					System.out.println("----------------------obj.getData-----------------------"+obj.getData("OID"));
					System.out.println("----------------------obj.getData-----------------------"+obj.getData("LCSMOAOBJECT.OID"));



				//	table.dropRow(obj.getData("OID"));
					LCSMOAObject moa=(LCSMOAObject)LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+obj.getData("OID"));
					System.out.println("----------------------deleted-moa----------------------"+moa);

					logic.delete(moa);

				}
				if(!VersionHelper.isCheckedOut(productObj)) {  
					System.out.println(">>>>>>>>>>>>>>>>>>  inside block count if ");
					VersionHelper.checkout(productObj);
                productObj.setValue("hbiErpPlantExtensions",table);
					VersionHelper.checkin(productObj);
				}
				/*String maxSortingNumber = FlexObjectUtil.maxValueForFlexObjects(moaData, "LCSMOAOBJECT.SORTINGNUMBER", "int");
				sortingNumber = Integer.toString((Integer.parseInt(maxSortingNumber) + 1));
				//Modify existing plant extension MOA and also will return boolean check whether to add new plant or now
				addPlantToPlantExt = getPlantFromPlantExtenstion(moaData,plantObject, dataMap);*/
				addPlantToPlantExt=true;
			}
       
			//
			if(addPlantToPlantExt)
			{
				System.out.println("----------------------addPlantToPlantExt-----------------------"+addPlantToPlantExt);

				populatePlantExtenstionData(productObj, plantObject, sortingNumber, dataMap);
			}
		}
		
	
	}
	
	public void populatePlantExtenstionData(LCSProduct productObj, LCSSupplier plantObj, String sortingNumber, HashMap<String,String> dataMap) throws WTException, WTPropertyVetoException
	{
		
		LCSMOACollectionClientModel moaModel = new LCSMOACollectionClientModel();
		moaModel.load(FormatHelper.getObjectId(productObj), "hbiErpPlantExtensions");
		StringBuffer dataBuffer = new StringBuffer();
		dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "ID", sortingNumber );
		dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "sortingnumber", sortingNumber );
		MultiObjectHelper.addAttribute(dataBuffer,"dropped", "false");
        //dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "user", FormatHelper.getNumericObjectIdFromObject(user) );
        //dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "comments", missingAttributes );
        MultiObjectHelper.addAttribute(dataBuffer,plantNameAttKey, String.valueOf(plantObj.getBranchIdentifier()));
        MultiObjectHelper.addAttribute(dataBuffer,primaryDelivery, dataMap.get(primaryDelivery));
        MultiObjectHelper.addAttribute(dataBuffer,plantType, dataMap.get(plantType));
        MultiObjectHelper.addAttribute(dataBuffer,maxLotSize, dataMap.get(maxLotSize));
        MultiObjectHelper.addAttribute(dataBuffer,plannedDelTime, dataMap.get(plannedDelTime));
        MultiObjectHelper.addAttribute(dataBuffer,totalRepLeadTme, dataMap.get(totalRepLeadTme));
        MultiObjectHelper.addAttribute(dataBuffer,procurementType, dataMap.get(procurementType));
        		//Loaded data from SAP, so it should be set to true as it is in Sync with SAP
        MultiObjectHelper.addAttribute(dataBuffer,hbiSynchedStatus, "true");
        
        
		
        String splPro =dataMap.get(specialProcurement);
		if(FormatHelper.hasContent(splPro)){
			LCSLifecycleManaged specialProcurementBO = getLifecycleManagedByCriteria("name", splPro.trim(), sapSpecialProcurementTypePath);
			//LCSLog.debug("specialProcurementBO "+specialProcurementBO);
			if(specialProcurementBO!=null){
				MultiObjectHelper.addAttribute(dataBuffer,specialProcurement,FormatHelper.getNumericObjectIdFromObject(specialProcurementBO));
			}else{
				logInfo(dataMap.get(sapKey)+"	"+"SpecialProcurement Not Found"+"	"+ splPro );
			
			}
		}
		dataBuffer.append(MultiObjectHelper.ROW_DELIMITER);
		moaModel.updateMOACollection(dataBuffer.toString());
		//Commented by Wipro Upgrade Team.
//		//getOwnerVersion
//		LCSMOAObject moaObject = LCSMOAObject.newLCSMOAObject();
//		moaObject.setFlexType(productObj.getFlexType().getAttribute("hbiErpPlantExtensions").getRefType());
//		moaObject.setOwnerReference(((RevisionControlled)productObj).getMasterReference());
//		moaObject.setOwnerVersion(productObj.getVersionIdentifier().getValue());
//		//moaObject.setOwnerAttribute(productObj.getFlexType().getAttribute("hbiErpPlantExtensions"));
//		moaObject.setBranchId(Integer.parseInt(sortingNumber));
//		moaObject.setDropped(false);
//		moaObject.setSortingNumber(Integer.parseInt(sortingNumber));
//		moaObject.getFlexType().getAttribute(plantNameAttKey).setValue(moaObject, plantObj);
//		moaObject.getFlexType().getAttribute(primaryDelivery).setValue(moaObject, dataMap.get(primaryDelivery));
//		moaObject.getFlexType().getAttribute(plantType).setValue(moaObject, dataMap.get(plantType));
//		moaObject.getFlexType().getAttribute(maxLotSize).setValue(moaObject, dataMap.get(maxLotSize));
//		moaObject.getFlexType().getAttribute(plannedDelTime).setValue(moaObject, dataMap.get(plannedDelTime));
//		moaObject.getFlexType().getAttribute(totalRepLeadTme).setValue(moaObject, dataMap.get(totalRepLeadTme));
//		moaObject.getFlexType().getAttribute(procurementType).setValue(moaObject, dataMap.get(procurementType));
//		//Loaded data from SAP, so it should be set to true as it is in Sync with SAP
//		moaObject.getFlexType().getAttribute(hbiSynchedStatus).setValue(moaObject, "true");
//		
//		String splPro =dataMap.get(specialProcurement);
//		if(FormatHelper.hasContent(splPro)){
//			LCSLifecycleManaged specialProcurementBO = getLifecycleManagedByCriteria("name", splPro.trim(), sapSpecialProcurementTypePath);
//			//LCSLog.debug("specialProcurementBO "+specialProcurementBO);
//			if(specialProcurementBO!=null){
//				moaObject.getFlexType().getAttribute(specialProcurement).setValue(moaObject, specialProcurementBO);
//			}else{
//				logInfo(dataMap.get(sapKey)+"	"+"SpecialProcurement Not Found"+"	"+ splPro );
//			
//			}
//		}
//
//		LCSMOAObjectLogic.persist(moaObject);
		
		LCSMOATable.clearTableFromMethodContextCache((FlexTyped)productObj, productObj.getFlexType().getAttribute("hbiErpPlantExtensions"));
		
		
	}
	
	public boolean getPlantFromPlantExtenstion(Collection<FlexObject> moaData, LCSSupplier plantObj, HashMap<String, String> dataMap)
	{
		
		int plantObjIdentifier = Integer.parseInt(FormatHelper.getNumericVersionIdFromObject(plantObj));
		//For new additions
		boolean addPlantToPlantExt = true;
		try {
		int plantNameIdentifier = 0;
		
		FlexType plantExtObj = FlexTypeCache.getFlexTypeFromPath(plantExtensionTypePath);
		
		String plantNameDBColumn = plantExtObj.getAttribute(plantNameAttKey).getColumnName().toUpperCase();
		
		for(FlexObject flexObj : moaData)
		{
			plantNameIdentifier = flexObj.getInt("LCSMOAOBJECT."+plantNameDBColumn);
		
			//for existing plants, update from excel.
			if(plantObjIdentifier == plantNameIdentifier ){
				
				String moaID = flexObj.getString("LCSMOAOBJECT.IDA2A2");
				LCSMOAObject moaObj = (LCSMOAObject) LCSQuery.findObjectById("com.lcs.wc.moa.LCSMOAObject:"+ moaID);
				moaObj.setValue(primaryDelivery, dataMap.get(primaryDelivery));
				
				moaObj.setValue(plantType, dataMap.get(plantType));
				
				moaObj.setValue(maxLotSize, dataMap.get(maxLotSize));
				
				moaObj.setValue(plannedDelTime, dataMap.get(plannedDelTime));
				moaObj.setValue(totalRepLeadTme, dataMap.get(totalRepLeadTme));
				moaObj.setValue(procurementType, dataMap.get(procurementType));
				//LCSLog.debug("Existing procurementType "+dataMap.get(procurementType));
				LCSLifecycleManaged specialProcurementBO = getLifecycleManagedByCriteria("name", dataMap.get(specialProcurement).trim(), sapSpecialProcurementTypePath);
				//LCSLog.debug("Existing specialProcurementBO "+specialProcurementBO);
				if(specialProcurementBO!=null){
					moaObj.getFlexType().getAttribute(specialProcurement).setValue(moaObj, specialProcurementBO);
				}else{
					logInfo(dataMap.get(sapKey)+"	"+"SpecialProcurement Not Found"+"	"+ dataMap.get(specialProcurement) );
				
				}
				LCSMOAObjectLogic.persist(moaObj);
				//logInfo(sapKey+"	"+"Plant exists for Row"+"	"+rowLine+"	"+ plantName );
				return false;
			}
		}
		
		} catch (WTException e) {
			
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return addPlantToPlantExt;
	}
	
	public static LCSSupplier getSAPPlanFromPlantCOde(String attributeKey, String attributeValue, String businessObjTypePath) throws WTException, WTPropertyVetoException
	{
		LCSSupplier attributionCodeRefObj = null;
		FlexType businessObjFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjTypePath);
		String criteriaAttDBColumn = businessObjFlexTypeObj.getAttribute(attributeKey).getColumnDescriptorName();//getColumnName();//.getVariableName();
		
		//Initializing the PreparedQueryStatement, which is using to get Business-object based on the given set of parameters(like FlexTypePath of the object) 
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "iterationInfo.branchId"));
    	statement.appendFromTable(LCSSupplier.class);
		statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "flexTypeIdPath"), businessObjFlexTypeObj.getTypeIdPath(), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, criteriaAttDBColumn), attributeValue.trim(), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "iterationInfo.latest"), "1", Criteria.EQUALS));
		
		//
		System.out.println(">>>>>>>>>>>>getSAPPlanFromPlantCOde LCSSupplier<<<<<<<<<<< "+statement);
		SearchResults results = LCSQuery.runDirectQuery(statement);
		
		if(results != null && results.getResultsFound() > 0)
		{
			
			FlexObject flexObj = (FlexObject) results.getResults().firstElement();
			attributionCodeRefObj = (LCSSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.supplier.LCSSupplier:"+flexObj.getString("LCSSupplier.BRANCHIDITERATIONINFO"));
			
		}
		
		return attributionCodeRefObj;
	}
	
	 /* Creating an custom log file for MasterJobProcessor CloudIntegration.
	 * @param infoMessage - String
	 */
	public static void logInfo(String infoMessage)
	{
		//Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd-HHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		String date = dateFormat.format(new Date());
		String logFileName = "PlantLog-" + date;
		try
		{
			if(logger == null)
			{
				//String location = "D:\\SP Load\\Plantlogs";
				//Creating custom log file using with the given filename, initializing writer to write/populate custom info and debug statement for the MasterJobProcessor.
				String strLogFile =  floderPhysicalLocation + File.separator + logFileName +".log";
				logger = new BufferedWriter(new FileWriter(strLogFile, true)); 
				fullFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
				objDate=new Date();
			}
			
			//Populating the given infoMessage into the custom log file along with the time stamp, flushing the content from the writer to the physical file which is needed to display
			logger.append(infoMessage);
			logger.newLine();
			logger.flush();
		}
		catch (IOException ioExp)
		{
			LCSLog.debug(" IOException in Plant MOA custom log:: "+ ioExp);
		}
	}
}
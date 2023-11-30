package com.hbi.wc.migration.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.part.WTPartMaster;
import wt.session.SessionContext;
import wt.session.SessionHelper;

import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;
import java.io.FileWriter;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

import com.lcs.wc.color.LCSColor;
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
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductHelper;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSKUSourcingLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.season.LCSSeasonMaster;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

/**
 * HBIColorwayLoader.java
 *
 * This class contain generic and specific functions which are using to read data from excel, initialize product object using unique parameters, initialize sku
 * object using product and color object, creating colorway-season link for the given season and colorway, populate colorway-season level data, persist sku data
 * @author Jeyaganeshan.Ramachandran@hanes.com
 * @since May-18-2019
 */
public class HBIColorwayLoader implements RemoteAccess
{
	
	private static RemoteMethodServer remoteMethodServer;
	
	private static String attributionCodeTypePath = LCSProperties.get("com.hbi.wc.migration.loader.HBIColorwayLoader.attributionCodeTypePath", "Business Object\\Automation Support Tables\\Attribution Codes and Descriptions");
	private static String sellingProductTypePath = LCSProperties.get("com.hbi.wc.migration.loader.HBIColorwayLoader.sellingProductTypePath", "Product\\BASIC CUT & SEW - SELLING");
	private static String colorColorwayTypePath = LCSProperties.get("com.hbi.wc.migration.loader.HBIColorwayLoader.colorColorwayTypePath", "Color\\Colorway");
	
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
			LCSLog.debug("Exception in static block of the class HBIColorwayLoader is : "+ exp);
		}
	}
	public static void main(String[] args) throws RemoteException, InvocationTargetException {
		System.out.println("LoadColorwayUtility " + args[0]);
		remoteinvoke(args[0]);
	}
	/**
	 * Default executable method of the class HBIColorwayLoader
	 * @param args - String[]
	 */
	public static void remoteinvoke(String option)
	{
		LCSLog.debug("### START HBIColorwayLoader.main() ###");
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
			
			remoteMethodServer.invoke("loadProductColorway", "com.hbi.wc.migration.loader.HBIColorwayLoader", null, argTypes, argValues);
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
		
		LCSLog.debug("### END HBIColorwayLoader.main() ###");
	}
	
	
	public static void loadProductColorway(String productColorwayDataFile) throws WTException, WTPropertyVetoException, IOException
	{	
		
		FileInputStream fileInputStreamObj = null;
		try
		{
			fileInputStreamObj = new FileInputStream(floderPhysicalLocation+File.separator+productColorwayDataFile+".xls");
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);
			
			new HBIColorwayLoader().loadProductColorwayFromWS( worksheet);
		}
		catch (IOException ioExp)
		{
			LCSLog.debug("IOException in HBIColorwayLoader.loadProductColorway() is "+ioExp);
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
	
	public void loadProductColorwayFromWS(HSSFSheet worksheet) throws WTException, WTPropertyVetoException, IOException
	{
		HSSFRow row = null;
		String attributionCode = "";
		String sapKey = "";
		String description = "";
		String apsPackQuantity = "";
		String sellingStyleNumber = "";
		String colorCode = "";
		DataFormatter formatter = new DataFormatter();
		//Iterating through each rows of the given excel file, initialize 'Attribution Code', 'Description', 'APS Pack Quantity' and 'Selling Style Number' 
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row == null)
				break;
			
			sapKey = formatter.formatCellValue(row.getCell(0));
			colorCode = formatter.formatCellValue(row.getCell(1));
			sellingStyleNumber = formatter.formatCellValue(row.getCell(2));
			description = formatter.formatCellValue(row.getCell(3));
			apsPackQuantity = formatter.formatCellValue(row.getCell(4));
			
			attributionCode = formatter.formatCellValue(row.getCell(5));
			
			loadProductColorway( colorCode, attributionCode, description, apsPackQuantity, sellingStyleNumber, sapKey, i);
		}
		
	}
	
	public void loadProductColorway( String colorCode, String attributionCode, String description, String apsPackQuantity, String sellingStyleNumber, String sapKey, int i) throws WTException, WTPropertyVetoException
	{
		int attributionCodeIdentifier = 0;
		
		LCSLifecycleManaged attributionCodeRefObj = getLifecycleManagedByCriteria("hbiErpAttributionCode", attributionCode, attributionCodeTypePath);
		if(attributionCodeRefObj != null)
			attributionCodeIdentifier = Integer.parseInt(FormatHelper.getNumericObjectIdFromObject(attributionCodeRefObj));
		
		LCSProduct productObj = findProductByCriteria(attributionCodeIdentifier, description, apsPackQuantity, sellingStyleNumber, sapKey);
	
		LCSColor colorObj = findColorByCriteria("hbiColorwayCodeNew", colorCode, colorColorwayTypePath);
		if(colorObj==null) {
			errorlogInfo(sapKey+"	"+"Row: "+i+"	"+"Color Not Found"+"	"+colorCode);
		}
		if(productObj==null) {
			errorlogInfo(sapKey+"	"+"Row: "+i+"	"+"Product Not Found"+"	"+"AttributionCode"+"	"+attributionCode+"	"+"Description"+"	"+description+"	"+"PackQuantity"+"	"+apsPackQuantity+"	"+"SellingStyle"+"	"+sellingStyleNumber);
		}
		
		if(productObj != null && colorObj != null )
		{   productObj = (LCSProduct) VersionHelper.latestIterationOf(productObj.getMaster());
		//Only one season should be there
			LCSSeason seasonObj = productObj.findSeasonUsed();
			System.out.println("seasonObj>>>>>>>>>>>>>>>"+seasonObj.getName());
			if(seasonObj!=null) {
				loadProductColorway(seasonObj, productObj, colorObj,  sapKey,  i, colorCode);
			}else {
				errorlogInfo(sapKey+"	"+"Row: "+i+"	"+"Season Not Found");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void loadProductColorway(LCSSeason seasonObj, LCSProduct productObj, LCSColor colorObj, String sapKey, int i, String colorCode )
	{
		Boolean loadColor=true;
		//To Check existing colorways with same color objects for avoiding unique name exception
		try {
		Collection<LCSSKU> skuObjects = productObj.findSKUObjects();
		if(skuObjects.isEmpty()) {
			LCSProduct productArev=(LCSProduct)VersionHelper.getVersion(productObj, "A");
			skuObjects=  productArev.findSKUObjects();
			if(!skuObjects.isEmpty()) {
				loadColor=false;
				errorlogInfo(sapKey+"	"+"Row: "+i+"	"+"SKU to Season"+"	"+"colorCode"+"	"+colorCode);
			}
			
		}
		LCSSKU skuObj = null;
		for(LCSSKU existingSku : skuObjects) {
			LCSColor color = (LCSColor) existingSku.getValue("color");
System.out.println("existingSku>>>>>>>>>>>"+existingSku.getName());
			if(color.equals(colorObj)) {
				//Do not load the color as it already exists
				loadColor=false;
				skuObj = existingSku ;
				errorlogInfo(sapKey+"	"+"Row: "+i+"	"+"Color Already Exists"+"	"+colorCode);
				
			}
		}
		
		if(loadColor) {
			
			skuObj = LCSSKU.newLCSSKU();
			skuObj.setFlexType(productObj.getFlexType());
//			skuObj.setProductMaster((WTPartMaster) productObj.getMaster());
			skuObj.setProductMaster((LCSPartMaster) productObj.getMaster());
			skuObj.setValue("skuName", colorObj.getName());
			skuObj.setName(colorObj.getName());
			skuObj.setValue("color", colorObj);
			//LCSProductLogic.deriveFlexTypeValues(skuObj);
			//skuObj = (LCSSKU) new LCSProductLogic().saveSKU(skuObj, seasonObj, true);
			skuObj = LCSProductHelper.service.saveSKU(skuObj, seasonObj);
		}
		//Below code will make sku active for the season and source of the product
		if(skuObj !=null){
		//	WTPartMaster prodMaster = (WTPartMaster) productObj.getMaster();
		//	WTPartMaster seasonMaster = (WTPartMaster) seasonObj.getMaster();
			LCSPartMaster prodMaster = (LCSPartMaster) productObj.getMaster();
			LCSSeasonMaster seasonMaster = (LCSSeasonMaster) seasonObj.getMaster();
			LCSSourcingConfig srcCfg = LCSSourcingConfigQuery.getPrimarySource(prodMaster, seasonMaster);
			System.out.println("prodMaster>>>>>>>>>>>>>>"+prodMaster.getName());
			System.out.println("seasonMaster>>>>>>>>>>>>>>>>>>>"+seasonMaster.getName());
			System.out.println("srcCfg"+srcCfg.getName());
			if(srcCfg!=null){
				LCSSKUSourcingLink ssl = new LCSSourcingConfigQuery().getSKUSourcingLink(srcCfg, skuObj, seasonObj);
				System.out.println("ssl>>>>>>>>>>>>>>>>>"+ssl);
				if(ssl!=null){
					ssl.setActive(true);
					LCSLogic.persist(ssl,true);
					System.out.println("ssl persisted>>>>>>>>>>>>>>>>"+ssl.getSourcedSKUName());
				}else{
					//new LCSSourcingConfigLogic().createNewSKUSourcingLinks(skuObj.getMaster(), seasonMaster);
					errorlogInfo(sapKey+"	"+"Row: "+i+"	"+"SKUSeasonLinkNull"+"	"+"colorCode"+"	"+colorCode);
				}
				
			}
			
		}
		
		} catch (WTException e) {
			errorlogInfo(sapKey+"	"+"Row: "+i+"	"+"WTException"+"	"+"colorCode"+"	"+colorCode);
				
			
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			errorlogInfo(sapKey+"	"+"Row: "+i+"	"+"1WTPropertyVetoException"+"	"+"colorCode"+"	"+colorCode);
			e.printStackTrace();
		}
		
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// Following Functions are using to Query Product, Business Object and Color /////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public LCSLifecycleManaged getLifecycleManagedByCriteria(String attributeKey, String attributeValue, String businessObjTypePath) throws WTException, WTPropertyVetoException
	{
		LCSLifecycleManaged attributionCodeRefObj = null;
		FlexType businessObjFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjTypePath);
		String criteriaAttDBColumn = businessObjFlexTypeObj.getAttribute(attributeKey).getColumnDescriptorName();//.getVariableName();
		
		//Initializing the PreparedQueryStatement, which is using to get Business-object based on the given set of parameters(like FlexTypePath of the object) 
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeIdPath"), businessObjFlexTypeObj.getTypeIdPath(), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, criteriaAttDBColumn), attributeValue.trim(), Criteria.EQUALS));
		
		//
		SearchResults results = LCSQuery.runDirectQuery(statement);
		
		if(results != null && results.getResultsFound() > 0)
		{
			
			FlexObject flexObj = (FlexObject) results.getResults().firstElement();
			attributionCodeRefObj = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+flexObj.getString("LCSLifecycleManaged.IDA2A2"));
		}
		
		return attributionCodeRefObj;
	}
	
	public LCSProduct findProductByCriteria(int attributionCodeIdentifier, String description, String apsPackQuantityStr, String styleNumber,String sapKey) throws WTException
	{
		
		LCSProduct productObj = null;
		FlexType productFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(sellingProductTypePath);
		String attributionCodeDBColumn = productFlexTypeObj.getAttribute("hbiErpAttributionCode").getColumnDescriptorName();//.getColumnName();//.getVariableName();
		String descriptionDBColumn = productFlexTypeObj.getAttribute("hbiDescription").getColumnDescriptorName();//.getVariableName();
		String apsPackQtyDBColumn = productFlexTypeObj.getAttribute("hbiAPSPackQuantity").getColumnDescriptorName();//.getVariableName();
		String styleNumberDBColumn = productFlexTypeObj.getAttribute("hbiSellingStyleNumber").getColumnDescriptorName();//.getVariableName();
		
		String sapKeyDBColumn = productFlexTypeObj.getAttribute("hbiSAPKey").getColumnDescriptorName();//.getVariableName();
		//String typeIdPath = String.valueOf(productFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
		
		String objecId = FormatHelper.getObjectId(productFlexTypeObj);
		String OID = FormatHelper.getNumericFromOid(objecId);

		int apsPackQuantity = Integer.parseInt(apsPackQuantityStr);
		
		//Initializing the PreparedQueryStatement, which is using to get LCSProduct object based on the given set of parameters(productName and Product FlexType ID Path)
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendSelectColumn(new QueryColumn(LCSProduct.class, "iterationInfo.branchId"));
		statement.appendFromTable(LCSProduct.class);
		//statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "flexTypeReference.key.id"), "?", "="), new Long(typeIdPath));
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
		//statement.appendAndIfNeeded();
		//statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, sapKeyDBColumn), sapKey, Criteria.EQUALS));


		//System.out.println("statement>>>>>>>>>>>>>"+statement.getSqlStatement());
		//Get FlexObject from the SearchResults instance, using to form LCSProduct instance, which is needed to return from function header to calling function
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if(results != null && results.getResultsFound() > 0)
		{
			FlexObject flexObj = (FlexObject)results.getResults().get(0);
	       	productObj = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+ flexObj.getString("LCSProduct.BRANCHIDITERATIONINFO"));
	       	productObj = (LCSProduct) VersionHelper.latestIterationOf(productObj);
		}
		
		return productObj;
	}
	
	/**
	 * This function is using to query LCSColor using color code and color type path, using this color object to initialize and create product colorway object
	 * @param colorCode - String
	 * @param colorTypePath - String
	 * @return colorObj - LCSColor
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSColor findColorByCriteria(String colorCodeKey, String colorCodeValue, String colorTypePath) throws WTException, WTPropertyVetoException
	{
		LCSColor colorObj = null;
		FlexType colorFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(colorTypePath);
		//String typeIdPath = String.valueOf(colorFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
		
		String objecId = FormatHelper.getObjectId(colorFlexTypeObj);
		String OID = FormatHelper.getNumericFromOid(objecId);
		String colorCodeDBColumn = colorFlexTypeObj.getAttribute(colorCodeKey).getColumnDescriptorName();//.getVariableName()
		
		//Initializing the PreparedQueryStatement, which is using to get LCSColor object based on the given set of parameters(like FlexTypePath, unique id's)
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendFromTable(LCSColor.class);
    	statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class,  "typeDefinitionReference.key.branchId"), "?", "="), new Long(OID));
    	statement.appendAndIfNeeded();
    	statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, colorCodeDBColumn), colorCodeValue, Criteria.EQUALS));
    	
    	//Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSColor instance/object and returning the Collection of LCSColor
        SearchResults results = LCSQuery.runDirectQuery(statement);
        if(results != null && results.getResultsFound() > 0)
        {
        	FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
        	colorObj = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:"+flexObj.getString("LCSColor.IDA2A2"));
        }
    	
		return colorObj;
	}
	
	
	 /* Creating an custom log file for MasterJobProcessor CloudIntegration.
	 * @param infoMessage - String
	 */
	public static void errorlogInfo(String infoMessage)
	{
		//Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		String date = dateFormat.format(new Date());
		
		String logFileName = "ColorwayReport-" + date;
		
		try
		{
			if(logger == null)
			{

				//String location = "D:\\SP Load\\ColorwayLogs";
				
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
			LCSLog.debug(" IOException in HBIColorwayLoader custom log:: "+ ioExp);
		}

	}
	
	
}
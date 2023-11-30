package com.hbi.wc.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.RetypeLogic;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.enterprise.RevisionControlled;
import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

/**
 * HBIMaterialTypeChangeUtility.java
 * 
 * This class contains a utility function as well as generic functions which are using to read the 'material sequence' and 'material type path' from the given XLS sheet, forming a Material 
 * object from the given 'material sequence' keeping some of the attributes data in cache, changing the object type (material and material-supplier) and populating certain attributes data  
 * @author Abdul.Patel@Hanes.com
 * @since July-08-2016
 */
public class HBIMaterialLabelUpdate implements RemoteAccess
{
	private static String sourceDataFileName = "MaterialTypeChangeDataFile.xls";
	private static String targetDataFileName = "MaterialTypeChangeDataFile_Sys.xls";
	private static String materialSeqKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.materialSeqKey", "hbiMatetrialSeq");
	private static String attributeCodeKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.attributeCodeKey", "hbiAttrCode");
	private static String colorCodeKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.colorCodeKey", "hbiColorCode");
	private static String sizeCodeKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.sizeCodeKey", "hbiSizeCode");
	private static String buyerCodeKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.buyerCodeKey", "hbiBuyerCode");
	
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	private static String floderPhysicalLocation = "";
	
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
			LCSLog.debug("Exception in static block of the class HBIMaterialTypeChangeUtility is : "+ exp);
		}
	}
	
	/* Default executable function of the class HBIMaterialTypeChangeUtility */
	public static void main(String[] args) 
	{
		LCSLog.debug("### START HBIMaterialTypeChangeUtility.main() ###");
		
		try
		{
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();

			remoteMethodServer = RemoteMethodServer.getDefault();
	        remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
	        remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
	        
	        GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);
	        
	        validateAndUpdateMaterialAndMaterialSupplierType(sourceDataFileName, targetDataFileName);
	        System.exit(0);
		}
		catch (Exception exception) 
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIMaterialTypeChangeUtility.main() ###");
	}
	
	public static void validateAndUpdateMaterialAndMaterialSupplierType(WTObject wtObj) throws WTException, WTPropertyVetoException, IOException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.validateAndUpdateMaterialAndMaterialSupplierType(WTObject wtObj) ###");
		
		if(wtObj instanceof LCSLifecycleManaged)
		{
			LCSLifecycleManaged businessObject = (LCSLifecycleManaged) wtObj;
			if("Business Object\\Integration\\Outbound\\Transaction BO".equalsIgnoreCase(businessObject.getFlexType().getFullName(true)))
			{
				String businessObjectName = businessObject.getName();
				if("RunMaterialTypeChangeUtility".equalsIgnoreCase(businessObjectName))
				{
					validateAndUpdateMaterialAndMaterialSupplierType(sourceDataFileName, targetDataFileName);
				}
			}
		}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.validateAndUpdateMaterialAndMaterialSupplierType(WTObject wtObj) ###");
	}
	
	/**
	 * This function is invoking from the default executable function of the class to initiate the process of material and material-supplier object type change to the newly given type path
	 * @param sourceDataFileName - String
	 * @param targetDataFileName - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	public static void validateAndUpdateMaterialAndMaterialSupplierType(String sourceDataFileName, String targetDataFileName) throws WTException, WTPropertyVetoException, IOException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.validateAndUpdateMaterialAndMaterialSupplierType(String sourceDataFileName, String targetDataFileName) ###");
		FileInputStream fileInputStreamObj = null;
		FileOutputStream fileOutputStreamObj = null;
		
		try
		{
			fileInputStreamObj = new FileInputStream(floderPhysicalLocation+File.separator+sourceDataFileName);
			fileOutputStreamObj = new FileOutputStream(floderPhysicalLocation+File.separator+targetDataFileName);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);
			
			//Calling a function which is using to read each line from the given document (XLS Sheet) then fetching material object from the given material sequence using for type change
			new HBIMaterialLabelUpdate().validateAndUpdateMaterialAndMaterialSupplierType(worksheet);
			
			workbook.write(fileOutputStreamObj);
		}
		catch(IOException ioExp)
		{
			ioExp.printStackTrace();
		}
		finally
    	{
    		if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
    		
    		if(fileOutputStreamObj != null)
    		{
    			fileOutputStreamObj.close();
    			fileOutputStreamObj = null;
    		}
    	}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.validateAndUpdateMaterialAndMaterialSupplierType(String sourceDataFileName, String targetDataFileName) ###");
	}
	
	/**
	 * This function is using to read each line from the given document then fetching material object from the given material sequence validating the material object new material type path
	 * @param worksheet - HSSFSheet
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void validateAndUpdateMaterialAndMaterialSupplierType(HSSFSheet worksheet) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.validateAndUpdateMaterialAndMaterialSupplierType(HSSFSheet worksheet) ###");
		LCSMaterial placeholderMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf( LCSMaterialQuery.PLACEHOLDER);
		HSSFRow row = null;
		HSSFCell statusCell = null;
		String materialTypePathNew = "";

		LCSMaterial newMaterialObj = null;
		String materialName = "";
		String colorCode = "";
		String attributeCode = "";
		String sizeCode = "";
		String materialTypePath = "";
		
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row != null)
			{
				//materialSeq = ((Double) row.getCell(0).getNumericCellValue()).intValue();
				materialName = row.getCell(0).getStringCellValue();
				colorCode = row.getCell(1).getStringCellValue();
				attributeCode = row.getCell(2).getStringCellValue();
				sizeCode = row.getCell(3).getStringCellValue();
				materialTypePath = row.getCell(4).getStringCellValue();
				materialTypePathNew = row.getCell(5).getStringCellValue();
				
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Material Name = "+ materialName + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Count = "+ i);
				
				//Calling a function to get Material object from the given type path, validate the material object and invoke internal functions to change the type path to new given path
				newMaterialObj = getMaterialObjectForCriteria(materialName, colorCode, attributeCode, sizeCode, materialTypePathNew);
				if(newMaterialObj != null && newMaterialObj != placeholderMaterialObj)
				{
					 String status=setLabelAsMinorCatValue(newMaterialObj, materialTypePathNew);
						
						statusCell = row.createCell((short) 6);
						statusCell.setCellValue(status);
					}
				
				else
				{
					statusCell = row.createCell((short) 6);
					statusCell.setCellValue("Material Already Exists in the new Type");
				}
			}
		}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.validateAndUpdateMaterialAndMaterialSupplierType(HSSFSheet worksheet) ###");
	}
	
	
	private String setLabelAsMinorCatValue(
			LCSMaterial materialObj, String materialTypePathNew) throws WTException {
		// TODO Auto-generated method stub
		
		System.out.println("materialTypePathNew------>"+materialTypePathNew);
		System.out.println("materialObj------>"+materialObj);
		System.out.println("minor value before------>"+materialObj.getValue("hbiMinorCategory"));
		
		if(materialObj.getValue("hbiMinorCategory")== null){
		if("Material\\Garment Label".equals(materialTypePathNew)){
			
			materialObj.setValue("hbiMinorCategory", "labels");
			
			 LCSMaterialHelper.service.saveMaterial(materialObj);
			 return "LABEL ADDED";
			 
		}
		}else{
			return "LABEL ALREADY EXIST";
		}
		
	return "BLANK";
	}

	/**
	 * This function is using to get LCSMaterial object for the given criteria (like Material Name, Color CD, Size CD and Material Type) and return LCSMaterial object from function header
	 * @param materialName - String
	 * @param materialType - String
	 * @param materialSequence - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial getMaterialObjectForCriteria(String materialName, String colorCode, String attributeCode, String sizeCode, String materialType) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.getMaterialObjectForCriteria(String materialName, String materialSequence, String materialType) ###");
		LCSLog.debug(" materialName = "+ materialName + " colorCode = "+ colorCode + " attributeCode = "+ attributeCode + " sizeCode = "+ sizeCode + " materialType = "+ materialType);
		LCSMaterial materialObj = null;
		
		//Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object and unique parameters)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
	    statement.appendFromTable(LCSMaterial.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "?", "="), new Long(1));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "c/i", Criteria.EQUALS));
		 
	    //Calling a function which is using to update the statement object with the given criteria's (like appending Material Name, Color Code, Size Code, Attribute Code and Type Path) 
	    statement = updatePreparedQueryStatementCriteria(statement, materialName, colorCode, attributeCode, sizeCode, materialType);
	    
	    //Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSMaterial instance/object and return the LCSMaterial object from the function
	    LCSLog.debug("Query to fetch Material Object = "+ statement);
	  	SearchResults results = LCSQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() == 1)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().iterator().next();
	  		materialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMaterial.BRANCHIDITERATIONINFO"));
	  		materialObj = (LCSMaterial) VersionHelper.latestIterationOf(materialObj);
	  	}
	    
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.getMaterialObjectForCriteria(String materialName, String materialSequence, String materialType) ###");
		return materialObj;
	}
	
	/**
	 * This function is using to validate the given criteria parameters(like Material Name, Color Code, Size Code, Attribute Code and Type Path), update the given statement object, return
	 * @param statement - PreparedQueryStatement
	 * @param materialName - String
	 * @param colorCode - String
	 * @param attributeCode - String
	 * @param sizeCode - String
	 * @param materialType - String
	 * @return statement - PreparedQueryStatement
	 * @throws WTException
	 */
	private PreparedQueryStatement updatePreparedQueryStatementCriteria(PreparedQueryStatement statement, String materialName, String colorCode, String attributeCode, String sizeCode, String materialType) throws WTException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.updatePreparedQueryStatementCriteria(statement, String materialName, String materialSequence, String materialType) ###");
		String materialNameDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("name").getVariableName();
		String colorCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiColorCode").getVariableName();
		String attributeCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiAttrCode").getVariableName();
		String sizeCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiSizeCode").getVariableName();
		
		//Validating the given Material Name, format the statement object/instance only if the invocation method providing an valid Material Name, which is using to get existing data
	    if(FormatHelper.hasContent(materialName))
	    {
	    	if("Material\\Material SKU".equalsIgnoreCase(materialType))
	    	{
	    		materialNameDBColumn = FlexTypeCache.getFlexTypeFromPath("Material\\Material SKU").getAttribute("hbiMaterialCode").getVariableName();
	    	}
	    	statement.appendAndIfNeeded();
		    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, materialNameDBColumn), materialName, Criteria.EQUALS));
	    }
	    
	    //Validating the given Attribute Code, format the statement object/instance only if the invocation method providing an valid Attribute Code, which is using to get existing data
	    if(FormatHelper.hasContent(attributeCode))
	    {
	    	statement.appendAndIfNeeded();
		    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, attributeCodeDBColumn), attributeCode, Criteria.EQUALS));
	    }
	    
	    //Validating the given Color Code, format the statement object/instance only if the invocation method providing an valid Color Code, which is using to get existing data
	    if(FormatHelper.hasContent(colorCode))
	    {
	    	statement.appendAndIfNeeded();
		    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, colorCodeDBColumn), colorCode, Criteria.EQUALS));
	    }
	    
	    //Validating the given Size Code, format the statement object/instance only if the invocation method providing an valid Size Code, which is using to get existing data
	    if(FormatHelper.hasContent(sizeCode))
	    {
	    	statement.appendAndIfNeeded();
		    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, sizeCodeDBColumn), sizeCode, Criteria.EQUALS));
	    }
	    
	    //Validating the given Material Type, format the statement object/instance only if the invocation method providing an valid Material Type, which is using to get existing data
	    if(FormatHelper.hasContent(materialType))
	  	{
	  		FlexType materialFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(materialType);
	  		String typeIdPath = String.valueOf(materialFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
	  		statement.appendAndIfNeeded();
	  		statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "flexTypeReference.key.id"), "?", "="), new Long(typeIdPath));
	  	}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.updatePreparedQueryStatementCriteria(statement, String materialName, String materialSequence, String materialType) ###");
		return statement;
	}
	
	/**
	 * This function is using to get LCSMaterial object for the given criteria (like Material Sequence Number and Material Type Path ID) and return LCSMaterial object from function header
	 * @param materialName - String
	 * @param materialType - String
	 * @param materialSequence - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial getMaterialBySequenceAndFlexTypePath(String materialSequence, String materialType) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.getMaterialBySequenceAndFlexTypePath(String materialSequence, String materialType) ###");
		LCSLog.debug("!!!!!!!!! materialSequence = "+ materialSequence +" !!!!!!!!!!!!!!!!!!!! materialType = "+ materialType);
		String materialSequenceDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiMatetrialSeq").getVariableName();
		LCSMaterial materialObj = null;
		
		//Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object and unique parameters)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
	    statement.appendFromTable(LCSMaterial.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "?", "="), new Long(1));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "c/i", Criteria.EQUALS));
		
	    //Validating the given Material Name, format the statement object/instance only if the invocation method providing an valid Material Sequence, which is using to get existing data
	    if(FormatHelper.hasContent(materialSequence))
	    {
	    	statement.appendAndIfNeeded();
		    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, materialSequenceDBColumn), "?", "="), new Long(materialSequence));
	    }
	    
	    //Validating the given Material Type, format the statement object/instance only if the invocation method providing an valid Material Type, which is using to get existing data
	    if(FormatHelper.hasContent(materialType))
	  	{
	  		FlexType materialFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(materialType);
	  		String typeIdPath = String.valueOf(materialFlexTypeObj.getPersistInfo().getObjectIdentifier().getId());
	  		statement.appendAndIfNeeded();
	  		statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "flexTypeReference.key.id"), "?", "="), new Long(typeIdPath));
	  	}
	    
	    //Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSMaterial instance/object and return the LCSMaterial object from the function
	    LCSLog.debug("Query to fetch Material Object = "+ statement);
	  	SearchResults results = LCSQuery.runDirectQuery(statement);
	  	if(results != null && results.getResultsFound() == 1)
	  	{
	  		FlexObject flexObj = (FlexObject)results.getResults().iterator().next();
	  		materialObj = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMaterial.BRANCHIDITERATIONINFO"));
	  		materialObj = (LCSMaterial) VersionHelper.latestIterationOf(materialObj);
	  	}
	    
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.getMaterialBySequenceAndFlexTypePath(String materialSequence, String materialType) ###");
		return materialObj;
	}
}
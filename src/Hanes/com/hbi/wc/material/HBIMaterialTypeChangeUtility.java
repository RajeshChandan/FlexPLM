package com.hbi.wc.material;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
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
import com.lcs.wc.flextype.RetypeLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

/**
 * HBIMaterialTypeChangeUtility.java
 * 
 * This class contains a utility function as well as generic functions which are using to read the 'material sequence' and 'material type path' from the given XLS sheet, forming a Material 
 * object from the given 'material sequence' keeping some of the attributes data in cache, changing the object type (material and material-supplier) and populating certain attributes data  
 * @author Abdul.Patel@Hanes.com
 * @since July-08-2016
 * Update Aug 29:2019 | Added check for blank color and size code from data file
 */
public class HBIMaterialTypeChangeUtility implements RemoteAccess
{
	public static String sourceDataFileName = "MaterialTypeChangeDataFile.xls";
	public static String targetDataFileName = "MaterialTypeChangeDataFile_Sys.xls";
	private static String materialSeqKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.materialSeqKey", "hbiMatetrialSeq");
	private static String attributeCodeKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.attributeCodeKey", "hbiAttrCode");
	private static String colorCodeKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.colorCodeKey", "hbiColorCode");
	private static String sizeCodeKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.sizeCodeKey", "hbiSizeCode");
	private static String buyerCodeKey = LCSProperties.get("com.hbi.wc.material.HBIMaterialTypeChangeUtility.buyerCodeKey", "hbiBuyerCode");
	
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	private static String floderPhysicalLocation = "";
	private static Logger log = LogManager.getLogger(HBIMaterialTypeChangeUtility.class);
	
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
			log.error("Exception in static block of the class HBIMaterialTypeChangeUtility is : "+ exp);
		}
	}
	
	/* Default executable function of the class HBIMaterialTypeChangeUtility */
	public static void main(String[] args) 
	{
		log.info("### START HBIMaterialTypeChangeUtility.main() ###");
		
		try
		{
			//These contexts are needed for establishing connection to method server- Do not remove these 2 below lines
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			//These contexts are needed for establishing connection to method server- Do not remove these 2 above lines
			
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
		
		log.info("### END HBIMaterialTypeChangeUtility.main() ###");
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
			new HBIMaterialTypeChangeUtility().validateAndUpdateMaterialAndMaterialSupplierType(worksheet);
			
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
		LCSMaterial materialObj = null;
		LCSMaterial newMaterialObj = null;
		String materialName = "";
		String colorCode = "";
		String attributeCode = "";
		String sizeCode = "";
		String materialTypePath = "";
		DataFormatter formatter = new DataFormatter();
		for(int i=1; i<=10000; i++)
		{
			row = worksheet.getRow(i);
			if(row != null)
			{
				//materialSeq = ((Double) row.getCell(0).getNumericCellValue()).intValue();
				materialName = formatter.formatCellValue(row.getCell(0));
				colorCode = formatter.formatCellValue(row.getCell(1));
				attributeCode =formatter.formatCellValue(row.getCell(2));
				sizeCode = formatter.formatCellValue(row.getCell(3));
				materialTypePath = formatter.formatCellValue(row.getCell(4));
				materialTypePathNew = formatter.formatCellValue(row.getCell(5));
				
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Material Name = "+ materialName + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Count = "+ i);
				if(!(FormatHelper.hasContent(materialName) && FormatHelper.hasContent(colorCode) && FormatHelper.hasContent(attributeCode) && FormatHelper.hasContent(sizeCode) && FormatHelper.hasContent(materialTypePath) && FormatHelper.hasContent(materialTypePathNew))) {
					continue;
				}
				
				//Calling a function to get Material object from the given type path, validate the material object and invoke internal functions to change the type path to new given path
				newMaterialObj = getMaterialObjectForCriteria(materialName, colorCode, attributeCode, sizeCode, materialTypePathNew);
				System.out.println("material exists in new type :: "+ newMaterialObj +", new Material Type = "+ materialTypePathNew);
				
				if(newMaterialObj == null || newMaterialObj == placeholderMaterialObj)
				{
					//Calling a function is using to get Material object from the given Material Sequence, validate the material object and invoke internal function using for type change
					materialObj = getMaterialObjectForCriteria(materialName, colorCode, attributeCode, sizeCode, materialTypePath);
					System.out.println("material from existing type :: "+ materialObj +", existing Material Type = "+ materialTypePath);
					//if(materialObj != null && materialObj != placeholderMaterialObj && !"true".equals(materialObj.getValue("hbiMasterMaterial")))
					
					if(materialObj != null && materialObj != placeholderMaterialObj)
					{
						materialObj = updateMaterialAndMaterialSupplierForTypeChange(materialObj, materialTypePathNew);
						
						statusCell = row.createCell((short) 6);
						statusCell.setCellValue("Type_Changed");
					}else{
						statusCell = row.createCell((short) 6);
						statusCell.setCellValue("Material not found");
					}
				}
				else
				{
					statusCell = row.createCell((short) 6);
					statusCell.setCellValue("Material Already Exists in the new Type");
				}
			}
			else
			{
				break;
			}
		}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.validateAndUpdateMaterialAndMaterialSupplierType(HSSFSheet worksheet) ###");
	}
	
	/**
	 * This function is using to change the type of the material object from an existing to the given path, copy certain attributes data and populate the same set of data on updated object
	 * @param materialObj - LCSMaterial
	 * @param materialTypePathNew - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial updateMaterialAndMaterialSupplierForTypeChange(LCSMaterial materialObj, String materialTypePathNew) throws WTException, WTPropertyVetoException
	{
		log.info("### START HBIMaterialTypeChangeUtility.updateMaterialAndMaterialSupplierForTypeChange(LCSMaterial materialObj, String materialTypePathNew) ###");
		FlexType materialFlexTypeObj = getMaterialFlexType(materialTypePathNew);
		
		//Get 'Material Sequence', 'Attribute Code', 'Color Code', 'Size Code' and "Material Name' from the object which are needed to re-populate on material after type changes 
		Integer materialSequence = ((Long) materialObj.getValue(materialSeqKey)).intValue();
		String attributeCode = (String) materialObj.getValue(attributeCodeKey);
		String colorCode = (String) materialObj.getValue(colorCodeKey);
		String sizeCode = (String) materialObj.getValue(sizeCodeKey);
		String buyerCode = (String) materialObj.getValue(buyerCodeKey);
		
		String materialCode = (String) materialObj.getValue("ptcmaterialName");
		String materialTypePath = materialObj.getFlexType().getFullName(true);
		if(materialTypePath.startsWith("Material\\Material SKU")) {
			materialCode = (String) materialObj.getValue("hbiMaterialCode");
		}
		
		//Get Type OID from the given FlexType, using RetypeLogic API to change the type of the Material object from an existing type to the newly given type and persist the object
		String materialTypeOID = FormatHelper.getObjectId(materialFlexTypeObj);
		materialObj = (LCSMaterial)RetypeLogic.changeType(materialObj, materialTypeOID, false);
		
		//Validating the given materialTypePathNew (target type path) and invoking functions which are using to copy the specific attributes data from source material to target material
		if(materialTypePathNew.startsWith("Material\\Material SKU"))
		{
			materialObj = updateSKUVersionMaterialAttributesData(materialObj, materialSequence, attributeCode, colorCode, sizeCode, buyerCode, materialCode);
		}
		else
		{
			materialObj = updateMasterMaterialAttributesData(materialObj, materialTypePathNew, materialCode, materialSequence, colorCode, buyerCode);
		}
		
		//Persisting the modified material and calling a function which is using to change 'Material-Supplier' type from an existing type to the newly given type and persisting the object
		materialObj = (LCSMaterial)LCSMaterialHelper.service.saveMaterial(materialObj);
		updateMaterialSupplierForTypeChange(materialObj, materialTypePathNew);
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.updateMaterialAndMaterialSupplierForTypeChange(LCSMaterial materialObj, String materialTypePathNew) ###");
		return materialObj;
	}
	
	/**
	 * This function is using/invoking when the target type path is 'Material\Material SKU' to copy the specific attributes data from source material object to the target material object
	 * @param materialObj - LCSMaterial
	 * @param materialSequence - Integer
	 * @param attributeCode - String
	 * @param colorCode - String
	 * @param sizeCode - String
	 * @param buyerCode - String
	 * @param materialCode - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial updateSKUVersionMaterialAttributesData(LCSMaterial materialObj, Integer materialSequence, String attributeCode, String colorCode, String sizeCode, String buyerCode, String materialCode) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.updateSKUVersionMaterialAttributesData(materialObj, materialSequence, attributeCode, colorCode, sizeCode, name) ###");
		
		materialObj.setValue(materialSeqKey, materialSequence);
		if(FormatHelper.hasContent(attributeCode))
		{
			materialObj.setValue(attributeCodeKey, attributeCode);
		}
		if(FormatHelper.hasContent(colorCode))
		{
			materialObj.setValue(colorCodeKey, colorCode);
		}
		if(FormatHelper.hasContent(sizeCode))
		{
			materialObj.setValue(sizeCodeKey, sizeCode);
		}
		if(FormatHelper.hasContent(buyerCode))		
		{
			materialObj.setValue(buyerCodeKey, buyerCode);
		}
		if(FormatHelper.hasContent(materialCode))	
		{
			materialObj.setValue("hbiMaterialCode", materialCode);
		}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.updateSKUVersionMaterialAttributesData(materialObj, materialSequence, attributeCode, colorCode, sizeCode, name) ###");
		return materialObj;
	}
	
	/**
	 * This function is using validating the MaterialTypePath (New) and updating the material object to manually set the data for specific attributes from source material to target object
	 * @param materialObj - LCSMaterial
	 * @param materialTypePathNew - String
	 * @param materialCode - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial updateMasterMaterialAttributesData(LCSMaterial materialObj, String materialTypePathNew, String materialCode, Integer materialSequence, String colorCode, String buyerCode) throws WTException, WTPropertyVetoException
	{
		log.info("### START HBIMaterialTypeChangeUtility.updateMasterMaterialAttributesData(LCSMaterial materialObj, String materialTypePathNew) ###");
		/*LCSMaterial placeholderMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf( LCSMaterialQuery.PLACEHOLDER);
		String materialTypePath = materialObj.getFlexType().getFullName(true);
		ObjectReference objRef = null;
		LCSMaterial oldMaterialObj = null;
		boolean exitLooping = true;*/
		
		//Validating the Material Type Path and updating the material object to manually set the data for specific attributes from source material object to target material object
		materialObj.setValue("ptcmaterialName", materialCode);
		materialObj.setValue(materialSeqKey, materialSequence);
		
		if(FormatHelper.hasContent(colorCode))
		{
			materialObj.setValue(colorCodeKey, colorCode);
		}
		if(FormatHelper.hasContent(buyerCode))		
		{
			materialObj.setValue(buyerCodeKey, buyerCode);
		}
		
		//Get Predecessor of the given Material object which is using to copy the Material attributes data (for a specific set of attributes) from previous version to the current version
		
		
		
		//Calling a function which is using to identify the type of the material and invoke the type specific functions for copying the data from predecessor to the latest object
		//materialObj = updateMasterMaterialAttributesData(materialObj, oldMaterialObj);
		
		log.info("### END HBIMaterialTypeChangeUtility.updateMasterMaterialAttributesData(LCSMaterial materialObj, String materialTypePathNew) ###");
		return materialObj;
	}
	
	/**
	 * This function is using to validate the given material object type (Elastic or Fabric Buy or Thread or Accessories) and invoking the type specific functions to update attribute data
	 * @param materialObj - LCSMaterial
	 * @param oldMaterialObj - LCSMaterial
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial updateMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj) throws WTException, WTPropertyVetoException
	{
		log.info("### START HBIMaterialTypeChangeUtility.updateMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj) ###");
		String materialTypePath = materialObj.getFlexType().getFullName(true);
		
		//
		if("Material\\Elastics".equalsIgnoreCase(materialTypePath))
		{
			materialObj = updateElasticMasterMaterialAttributesData(materialObj, oldMaterialObj, materialTypePath);
		}
		else if("Material\\Accessories".equalsIgnoreCase(materialTypePath))
		{
			materialObj = updateAccessoriesMasterMaterialAttributesData(materialObj, oldMaterialObj, materialTypePath);
		}
		else if("Material\\Garment Label".equalsIgnoreCase(materialTypePath))
		{
			materialObj = updateGarmentLabelMasterMaterialAttributesData(materialObj, oldMaterialObj, materialTypePath);
		}
		else if("Material\\Fabric\\Fabric Buy".equalsIgnoreCase(materialTypePath))
		{
			materialObj = updateFabricBuyMasterMaterialAttributesData(materialObj, oldMaterialObj, materialTypePath);
		}
		else if("Material\\Thread".equalsIgnoreCase(materialTypePath))
		{
			materialObj = updateThreadMasterMaterialAttributesData(materialObj, oldMaterialObj, materialTypePath);
		}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.updateMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj) ###");
		return materialObj;
	}
	
	/**
	 * This function is specific to Elastic Master Materials which is using to copy the data from predecessor version of the material object to the current version of the material object
	 * @param materialObj - LCSMaterial
	 * @param oldMaterialObj - LCSMaterial
	 * @param materialTypePath - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial updateElasticMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, String materialTypePath) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.updateElasticMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj) ###");
		
		String sizeCodeValue = (String) oldMaterialObj.getValue(sizeCodeKey);
		if(FormatHelper.hasContent(sizeCodeValue))
		{
			materialObj.setValue(sizeCodeKey, sizeCodeValue);
		}
		
		String attributeCodeValue = (String) oldMaterialObj.getValue(attributeCodeKey);
		if(FormatHelper.hasContent(attributeCodeValue))
		{
			materialObj.setValue(attributeCodeKey, attributeCodeValue);
		}
		
		String hbiBuyOrNotBuyValue = (String) oldMaterialObj.getValue("hbiBuyOrNotBuy");
		if(FormatHelper.hasContent(hbiBuyOrNotBuyValue))
		{
			materialObj.setValue("hbiBuyOrNotBuy", hbiBuyOrNotBuyValue);
		}
		
		
		
		/**
		String logoDetails = (String) oldMaterialObj.getValue("hbiElasticLogo");
		if(FormatHelper.hasContent(logoDetails))
		{
			materialObj.setValue("hbiElasticLogo", logoDetails);
		}
		
		String elasticType = (String) oldMaterialObj.getValue("hbiMaterialSubType");
		if(FormatHelper.hasContent(elasticType))
		{
			materialObj.setValue("hbiMaterialSubType", elasticType);
		}
		
		String specComments = (String) oldMaterialObj.getValue("hbiSpecComments");
		if(FormatHelper.hasContent(specComments))
		{
			materialObj.setValue("hbiSpecComments", specComments);
		}
		
		String leadTestingRequired = (String) oldMaterialObj.getValue("hbiLeadTestingRequired");
		if(FormatHelper.hasContent(leadTestingRequired))
		{
			materialObj.setValue("hbiLeadTestingRequired", leadTestingRequired);
		}
		
		LCSColor threadMatchColorObj = (LCSColor) oldMaterialObj.getValue("hbiThreadMatchColor");
		if(threadMatchColorObj != null)
		{
			materialObj.setValue("hbiThreadMatchColor", threadMatchColorObj);
		}
		
		double width = (Double) oldMaterialObj.getValue("hbiWidth");
		if(width != 0.0 && width != 0.0000)
		{
			materialObj.setValue("hbiWidth", width);
		}*/
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.updateElasticMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj) ###");
		return materialObj;
	}
	
	/**
	 * This function is specific to Accessories Master Materials which are using to copy the data from predecessor version of the material to the current version of the material instance
	 * @param materialObj - LCSMaterial
	 * @param oldMaterialObj - LCSMaterial
	 * @param materialTypePath - String
	 * @return  materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial updateAccessoriesMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, String materialTypePath) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.updateAccessoriesMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, materialTypePath) ###");
		
		String sizeCode = (String) oldMaterialObj.getValue(sizeCodeKey);
		if(FormatHelper.hasContent(sizeCode))
		{
			materialObj.setValue(sizeCodeKey, sizeCode);
		}
		
		String leadTestingRequired = (String) oldMaterialObj.getValue("hbiLeadTestingRequired");
		if(FormatHelper.hasContent(leadTestingRequired))
		{
			materialObj.setValue("hbiLeadTestingRequired", leadTestingRequired);
		}
		
		String accessoriesType = (String) oldMaterialObj.getValue("hbiMaterialSubType");
		if(FormatHelper.hasContent(accessoriesType))
		{
			materialObj.setValue("hbiMaterialSubType", accessoriesType);
		}
		
		LCSColor threadMatchColorObj = (LCSColor) oldMaterialObj.getValue("hbiThreadMatchColor");
		if(threadMatchColorObj != null)
		{
			materialObj.setValue("hbiThreadMatchColor", threadMatchColorObj);
		}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.updateAccessoriesMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, materialTypePath) ###");
		return materialObj;
	}
	
	/**
	 * This function is specific to Garment Label Master Materials which are using to copy the data from predecessor version of the material to the current version of the material instance
	 * @param materialObj - LCSMaterial
	 * @param oldMaterialObj - LCSMaterial
	 * @param materialTypePath - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial updateGarmentLabelMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, String materialTypePath) throws WTException, WTPropertyVetoException
	{
		log.info("### START HBIMaterialTypeChangeUtility.updateGarmentLabelMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, materialTypePath) ###");
		
		String sizeCode = (String) oldMaterialObj.getValue(sizeCodeKey);
		if(FormatHelper.hasContent(sizeCode))
		{
			materialObj.setValue(sizeCodeKey, sizeCode);
		}
		/**
		String application = (String) oldMaterialObj.getValue("hbiApplication");
		if(FormatHelper.hasContent(application))
		{
			materialObj.setValue("hbiApplication", application);
		}
		
		String garmentSize = (String) oldMaterialObj.getValue("hbiGarmentSize");
		if(FormatHelper.hasContent(garmentSize))
		{
			materialObj.setValue("hbiGarmentSize", garmentSize);
		}
		
		String country = (String) oldMaterialObj.getValue("hbiLabelCountry");
		if(FormatHelper.hasContent(country))
		{
			materialObj.setValue("hbiLabelCountry", country);
		}
		
		String labelFormat = (String) oldMaterialObj.getValue("hbiLabelFormat");
		if(FormatHelper.hasContent(labelFormat))
		{
			materialObj.setValue("hbiLabelFormat", labelFormat);
		}
		
		String language = (String) oldMaterialObj.getValue("hbiLanguage");
		if(FormatHelper.hasContent(language))
		{
			materialObj.setValue("hbiLanguage", language);
		}
		
		String labelType = (String) oldMaterialObj.getValue("hbiMatLabelType");
		if(FormatHelper.hasContent(labelType))
		{
			materialObj.setValue("hbiMatLabelType", labelType);
		}
		
		String retailMarket = (String) oldMaterialObj.getValue("hbiRetailMarket");
		if(FormatHelper.hasContent(retailMarket))
		{
			materialObj.setValue("hbiRetailMarket", retailMarket);
		}
		
		String styleGroup = (String) oldMaterialObj.getValue("hbistylegroupSL");
		if(FormatHelper.hasContent(styleGroup))
		{
			materialObj.setValue("hbistylegroupSL", styleGroup);
		}
		
		String padPrintInkColors = (String) oldMaterialObj.getValue("hbiPadPrintInkColors");
		if(FormatHelper.hasContent(padPrintInkColors))
		{
			materialObj.setValue("hbiPadPrintInkColors", padPrintInkColors);
		}
		
		LCSCountry countryObj = (LCSCountry) oldMaterialObj.getValue("hbiCountryCode");
		if(countryObj != null)
		{
			materialObj.setValue("hbiCountryCode", countryObj);
		}
		*/
		log.info("### END HBIMaterialTypeChangeUtility.updateGarmentLabelMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, materialTypePath) ###");
		return materialObj;
	}
	
	/**
	 * This function is specific to Fabric Buy Master Materials which are using to copy the data from predecessor version of the material to the current version of the material instance
	 * @param materialObj - LCSMaterial
	 * @param oldMaterialObj - LCSMaterial
	 * @param materialTypePath - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial updateFabricBuyMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, String materialTypePath) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.updateFabricBuyMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, materialTypePath) ###");
		
		String coreFabric = (String) oldMaterialObj.getValue("hbiCoreFabric");
		if(FormatHelper.hasContent(coreFabric) && "yes".equalsIgnoreCase(coreFabric))
		{
			materialObj.setValue("hbiCoreFabric", true);
		}
		else
		{
			materialObj.setValue("hbiCoreFabric", false);
		}
		
		Date developmentCompleteAct = (Date) oldMaterialObj.getValue("hbiDevelopmentComplete");
		if(developmentCompleteAct != null)
		{
			materialObj.setValue("hbiDevelopmentComplete", developmentCompleteAct);
		}
		
		String hbiFabConstruction = (String) oldMaterialObj.getValue("hbiFabConstruction");
		if(FormatHelper.hasContent(hbiFabConstruction))
		{
			materialObj.setValue("hbiFabConstruction", hbiFabConstruction);
		}
		
		String hbiFabricCategory = (String) oldMaterialObj.getValue("hbiFabricCategory");
		if(FormatHelper.hasContent(hbiFabricCategory))
		{
			materialObj.setValue("hbiFabricCategory", hbiFabricCategory);
		}
		
		String hbiFabricGroup = (String) oldMaterialObj.getValue("hbiFabricGroup");
		if(FormatHelper.hasContent(hbiFabricGroup))
		{
			materialObj.setValue("hbiFabricGroup", hbiFabricGroup);
		}
		
		double hbiFabWeightOz = (Double) oldMaterialObj.getValue("hbiFabWeightOz");
		if(hbiFabWeightOz != 0.0 && hbiFabWeightOz != 0.000)
		{
			materialObj.setValue("hbiFabWeightOz", hbiFabWeightOz);
		}
		
		String hbiFaceSide = (String) oldMaterialObj.getValue("hbiFaceSide");
		if(FormatHelper.hasContent(hbiFaceSide))
		{
			materialObj.setValue("hbiFaceSide", hbiFaceSide);
		}
		
		String hbiFiberBlend = (String) oldMaterialObj.getValue("hbiFiberBlend");
		if(FormatHelper.hasContent(hbiFiberBlend))
		{
			materialObj.setValue("hbiFiberBlend", hbiFiberBlend);
		}
		
		String hbiFiberType = (String) oldMaterialObj.getValue("hbiFiberType");
		if(FormatHelper.hasContent(hbiFiberType))
		{
			materialObj.setValue("hbiFiberType", hbiFiberType);
		}
		
		String hbiHeatSetTemp = (String) oldMaterialObj.getValue("hbiHeatSetTemp");
		if(FormatHelper.hasContent(hbiHeatSetTemp))
		{
			materialObj.setValue("hbiHeatSetTemp", hbiHeatSetTemp);
		}
		
		String hbiMachine = (String) oldMaterialObj.getValue("hbiMachine");
		if(FormatHelper.hasContent(hbiMachine))
		{
			materialObj.setValue("hbiMachine", hbiMachine);
		}
		
		String hbiMachineFinish = (String) oldMaterialObj.getValue("hbiMachineFinish");
		if(FormatHelper.hasContent(hbiMachineFinish))
		{
			materialObj.setValue("hbiMachineFinish", hbiMachineFinish);
		}
		
		String hbiMaterialSubType = (String) oldMaterialObj.getValue("hbiMaterialSubType");
		if(FormatHelper.hasContent(hbiMaterialSubType))
		{
			materialObj.setValue("hbiMaterialSubType", hbiMaterialSubType);
		}
		
		String hbiMoldedItem = (String) oldMaterialObj.getValue("hbiMoldedItem");
		if(FormatHelper.hasContent(hbiMoldedItem) && "true".equalsIgnoreCase(hbiMoldedItem))
		{
			materialObj.setValue("hbiMoldedItem", true);
		}
		else
		{
			materialObj.setValue("hbiMoldedItem", false);
		}
		
		String hbiCoreFabric = (String) oldMaterialObj.getValue("hbiCoreFabric");
		if(FormatHelper.hasContent(hbiCoreFabric) && "true".equalsIgnoreCase(hbiCoreFabric))
		{
			materialObj.setValue("hbiCoreFabric", true);
		}
		else
		{
			materialObj.setValue("hbiCoreFabric", false);
		}
		
		String hbiPutUp = (String) oldMaterialObj.getValue("hbiPutUp");
		if(FormatHelper.hasContent(hbiPutUp))
		{
			materialObj.setValue("hbiPutUp", hbiPutUp);
		}
		
		String hbiSpecComments = (String) oldMaterialObj.getValue("hbiSpecComments");
		if(FormatHelper.hasContent(hbiSpecComments))
		{
			materialObj.setValue("hbiSpecComments", hbiSpecComments);
		}
		
		String hbiSizeCode = (String) oldMaterialObj.getValue("hbiSizeCode");
		if(FormatHelper.hasContent(hbiSizeCode))
		{
			materialObj.setValue("hbiSizeCode", hbiSizeCode);
		}
		
		String attributeCode = (String) oldMaterialObj.getValue(attributeCodeKey);
		if(FormatHelper.hasContent(attributeCode))
		{
			materialObj.setValue(attributeCodeKey, attributeCode);
		}
		
		String hbiSpecialFeature = (String) oldMaterialObj.getValue("hbiSpecialFeature");
		if(FormatHelper.hasContent(hbiSpecialFeature))
		{
			materialObj.setValue("hbiSpecialFeature", hbiSpecialFeature);
		}
		
		String hbiStdCut = (String) oldMaterialObj.getValue("hbiStdCut");
		if(FormatHelper.hasContent(hbiStdCut))
		{
			materialObj.setValue("hbiStdCut", hbiStdCut);
		}
		
		String hbiWhereMake = (String) oldMaterialObj.getValue("hbiWhereMake");
		if(FormatHelper.hasContent(hbiWhereMake))
		{
			materialObj.setValue("hbiWhereMake", hbiWhereMake);
		}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.updateFabricBuyMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, materialTypePath) ###");
		return materialObj;
	}
	
	/**
	 * This function is specific to Thread Master Materials which are using to copy the data from predecessor version of the material to the current version of the material instance/data
	 * @param materialObj - LCSMaterial
	 * @param oldMaterialObj - LCSMaterial
	 * @param materialTypePath - String
	 * @return materialObj - LCSMaterial
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSMaterial updateThreadMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, String materialTypePath) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.updateThreadMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, materialTypePath) ###");
		
		String leadTestingRequired = (String) oldMaterialObj.getValue("hbiLeadTestingRequired");
		if(FormatHelper.hasContent(leadTestingRequired))
		{
			materialObj.setValue("hbiLeadTestingRequired", leadTestingRequired);
		}
		
		String accessoriesType = (String) oldMaterialObj.getValue("hbiMaterialSubType");
		if(FormatHelper.hasContent(accessoriesType))
		{
			materialObj.setValue("hbiMaterialSubType", accessoriesType);
		}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.updateThreadMasterMaterialAttributesData(LCSMaterial materialObj, LCSMaterial oldMaterialObj, materialTypePath) ###");
		return materialObj;
	}
	
	/**
	 * This function is using to get a collection of material-supplier for the given material and changing each material-supplier type from an existing type to the newly provided type path
	 * @param materialObj - LCSMaterial
	 * @param materialTypePathNew - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	private void updateMaterialSupplierForTypeChange(LCSMaterial materialObj, String materialTypePathNew) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIMaterialTypeChangeUtility.updateMaterialSupplierForTypeChange(LCSMaterial materialObj, String materialTypePathNew) ###");
		FlexType materialFlexTypeObj =  getMaterialFlexType(materialTypePathNew);
		String materialTypeOID = FormatHelper.getObjectId(materialFlexTypeObj);
		LCSMaterialSupplier materialSupplierObj = null;
		
		//Get all 'Material-Supplier' object from the given Material, iterate through the 'Material-Supplier' collection, validate the material-supplier and change the type to new path
		SearchResults results = LCSMaterialSupplierQuery.findMaterialSuppliers(materialObj);
		if(results != null && results.getResultsFound() > 0)
		{
			Collection<FlexObject> materialSupplierCollection = results.getResults();
			for(FlexObject flexObj : materialSupplierCollection)
			{
				//Validating the 'Material-Supplier' object and invoking an existing API's to change the type from an existing to the new type (new type is provided in data file)
				materialSupplierObj = (LCSMaterialSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterialSupplier:"+flexObj.getString("LCSMaterialSupplier.BRANCHIDITERATIONINFO"));
				if(materialSupplierObj != null && !materialSupplierObj.isPlaceholder())
				{
					// LCSLog.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Material-Supplier Type Before Changes = "+ materialSupplierObj.getFlexType().getFullName(true));
					materialSupplierObj = (LCSMaterialSupplier)RetypeLogic.changeType(materialSupplierObj, materialTypeOID, false);
					materialSupplierObj = (LCSMaterialSupplier) LCSMaterialHelper.service.saveMaterialSupplier(materialSupplierObj);
					// LCSLog.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Material-Supplier Type After Changes = "+ materialSupplierObj.getFlexType().getFullName(true));
				}
			}
		}
		
		// LCSLog.debug("### END HBIMaterialTypeChangeUtility.updateMaterialSupplierForTypeChange(LCSMaterial materialObj, String materialTypePathNew) ###");
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
		log.info(" materialName = "+ materialName + " colorCode = "+ colorCode + " attributeCode = "+ attributeCode + " sizeCode = "+ sizeCode + " materialType = "+ materialType);
		LCSMaterial materialObj = null;
		
		//Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object and unique parameters)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
	    statement.appendFromTable(LCSMaterial.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "?", "="), Long.toString(1));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "c/i", Criteria.EQUALS));
		
	    //Calling a function which is using to update the statement object with the given criteria's (like appending Material Name, Color Code, Size Code, Attribute Code and Type Path) 
	    statement = updatePreparedQueryStatementCriteria(statement, materialName, colorCode, attributeCode, sizeCode, materialType);
	    
	    //Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSMaterial instance/object and return the LCSMaterial object from the function
	    log.info("Query to fetch Material Object = "+ statement);
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
		String materialNameDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("ptcmaterialName").getColumnDescriptorName();
		String colorCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiColorCode").getColumnDescriptorName();
		String attributeCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiAttrCode").getColumnDescriptorName();
		String sizeCodeDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiSizeCode").getColumnDescriptorName();
		
		//Validating the given Material Name, format the statement object/instance only if the invocation method providing an valid Material Name, which is using to get existing data
	    if(FormatHelper.hasContent(materialName))
	    {
	    	if(materialType.startsWith("Material\\Material SKU"))
	    	{
	    		materialNameDBColumn = FlexTypeCache.getFlexTypeFromPath("Material\\Material SKU").getAttribute("hbiMaterialCode").getColumnDescriptorName();
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
	  		FlexType materialFlexTypeObj = getMaterialFlexType(materialType);
	  		String typeIdPath = materialFlexTypeObj.getIdNumber();
	  		statement.appendAndIfNeeded();
	  		statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, LCSMaterialQuery.TYPED_BRANCH_ID), "?", "="), Long.parseLong(typeIdPath));
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
		log.info("!!!!!!!!! materialSequence = "+ materialSequence +" !!!!!!!!!!!!!!!!!!!! materialType = "+ materialType);
		String materialSequenceDBColumn = FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("hbiMatetrialSeq").getColumnDescriptorName();
		LCSMaterial materialObj = null;
		
		//Initializing the PreparedQueryStatement, which is using to get LCSMaterial object based on the given set of parameters(like FlexTypePath of the object and unique parameters)
	    PreparedQueryStatement statement = new PreparedQueryStatement();
	    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
	    statement.appendFromTable(LCSMaterial.class);
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "?", "="), Long.toString(1));
	    statement.appendAndIfNeeded();
	    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "checkoutInfo.state"), "c/i", Criteria.EQUALS));
		
	    //Validating the given Material Name, format the statement object/instance only if the invocation method providing an valid Material Sequence, which is using to get existing data
	    if(FormatHelper.hasContent(materialSequence))
	    {
	    	statement.appendAndIfNeeded();
		    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, materialSequenceDBColumn), "?", "="), Long.parseLong(materialSequence));
	    }
	    
	    //Validating the given Material Type, format the statement object/instance only if the invocation method providing an valid Material Type, which is using to get existing data
	    if(FormatHelper.hasContent(materialType))
	  	{
	  		FlexType materialFlexTypeObj =  getMaterialFlexType(materialType);
	  		String typeIdPath = materialFlexTypeObj.getIdNumber();
	  		statement.appendAndIfNeeded();
	  		statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, LCSMaterialQuery.TYPED_BRANCH_ID), "?", "="), Long.parseLong(typeIdPath));
	  	}
	    
	    //Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSMaterial instance/object and return the LCSMaterial object from the function
	    log.info("Query to fetch Material Object = "+ statement);
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
	
	/**
	 * This function is using to all the sub types of Material, Iterate FlexTypes collection to validate with the given FlexTypePath and return the corresponding FlexType Object
	 * @param materialTypePath - String
	 * @return materialFlexTypeObj - FlexType
	 * @throws WTException
	 */
	public FlexType getMaterialFlexType(String materialTypePath) throws WTException
	{
		log.debug("### START HBIMaterialTypeChangeUtility.getMaterialFlexType(String materialTypePath) ###");
		FlexType materialFlexTypeObj = null;
		Collection<FlexType> flexTypesColl = new ArrayList<FlexType>();
		
		//Initialize all FlexTypes
		FlexType rootFlexType = FlexTypeCache.getFlexTypeFromPath("Material");
		flexTypesColl.add(rootFlexType);
		if(rootFlexType.getAllChildren() != null) {
			flexTypesColl.addAll(rootFlexType.getAllChildren());
		}
		
		//Iterate FlexTypes collection to validate with the given FlexTypePath and return the corresponding FlexType Object
		for(FlexType flexTypeObj : flexTypesColl)
		{
			if(materialTypePath.equalsIgnoreCase(flexTypeObj.getFullNameDisplay(true))) {
				materialFlexTypeObj = flexTypeObj;
				break;
			}
		}
		
		log.debug("### END HBIMaterialTypeChangeUtility.getMaterialFlexType(String materialTypePath) ###");
		return materialFlexTypeObj;
	}
}
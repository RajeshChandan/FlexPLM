package com.hbi.wc.interfaces.inbound.staging.processor;

import java.util.Collection;
import java.util.Locale;
import java.util.ArrayList;

import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import wt.util.WTException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexTypeHelper;

import com.hbi.stg.extractors.util.HBIConnectionUtil;

import wt.method.MethodContext;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;

import wt.util.WTPropertyVetoException;

/**
 * HBIAPSPlantGroupProcessor.java
 * 
 * This class contains a stand alone executable function and generic functions which are using to fetch Plant Group data from APS database (where APS table/schema is PLM_APS_PLANT_GROUPS)
 * iterating through the APS data collection(contains Plant_Group data), validating the existence of plant group in PLM, based on the status creating/updating Plant Group keys in FlexPLM
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since September-11-2016
 */
public class HBIAPSPlantGroupProcessor
{
	private static String plantGroupFlexTypePath = LCSProperties.get("com.hbi.wc.interfaces.inbound.staging.processor.HBIAPSPlantGroupProcessor.plantGroupFlexTypePath", "Business Object\\Plant Groupings");
	private static String plantGroupKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.staging.processor.HBIAPSPlantGroupProcessor.plantGroupKey", "hbiPlantGroup");
	private static String apsSchemaName = LCSProperties.get("com.hbi.wc.interfaces.inbound.staging.processor.HBIAPSPlantGroupProcessor.apsSchemaName", "DA.PLM_APS_PLANT_GROUPS@PROD1");
	private static String specialCharDelimiter = LCSProperties.get("com.hbi.wc.interfaces.inbound.staging.processor.HBIAPSPlantGroupProcessor.specialCharDelimiter", "[^a-zA-Z0-9]");
	
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	
	/* Default executable function of the class HBIAPSPlantGroupProcessor */
    public static void main(String args[]) 
	{
    	LCSLog.debug("### START HBIAPSPlantGroupProcessor.main() ###");
    	
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
			
			validateAndSyncPlantGroupAttribute();
			System.exit(0);	
        }
		catch (Exception exception) 
		{
            exception.printStackTrace();
			System.exit(1);
        }

        LCSLog.debug("### END HBIAPSPlantGroupProcessor.main() ###");
    }
    
    /**
     * This function is using as a invocation port of APS-FlexPLM Plant Group data integration, which will fetch the Plant Group data from APS, validate and update the Plant Group in PLM
     */
    public static void validateAndSyncPlantGroupAttribute()
    {
    	LCSLog.debug("### START HBIAPSPlantGroupProcessor.validateAndSyncPlantGroupAttribute() ###");
    	String plantGroupChoiceKey = "";
    	
    	try
    	{
    		//Calling a function which will return a collection of Plant Group from the APS schema based on the pre-configured clauses/where conditions to fetch the APS Plant Group data
    		Collection<String> apsPlantGroupsCollection = new HBIAPSPlantGroupProcessor().getAPSPlantGroupAttributesData();
    		for(String apsPlantGroup : apsPlantGroupsCollection)
    		{
    			plantGroupChoiceKey = apsPlantGroup.replaceAll(specialCharDelimiter, "");
    			
    			//
    			new HBIAPSPlantGroupProcessor().validateAndUpdatePLMPlantGroup(plantGroupChoiceKey, apsPlantGroup);
    		}
    		
    		System.exit(0);
    	}
    	catch (Exception Exp)
    	{
    		Exp.printStackTrace();
			System.exit(1);
		}
    
    	LCSLog.debug("### END HBIAPSPlantGroupProcessor.validateAndSyncPlantGroupAttribute() ###");
    }
    
    /**
     * This function is using to validate the existing plant group for the given choices key, based on the validation status proceeding with the Plant Group choices changes as per APS data
     * @param plantGroupChoiceKey - String
     * @param plantGroupChoiceValue - String
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public void validateAndUpdatePLMPlantGroup(String plantGroupChoiceKey, String plantGroupChoiceValue) throws WTException, WTPropertyVetoException
    {
    	
    	FlexType businessObjectFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(plantGroupFlexTypePath);
		FlexTypeAttribute plantGroupAttributeObj = businessObjectFlexTypeObj.getAttribute(plantGroupKey);
		AttributeValueList plantGroupAttributeList = plantGroupAttributeObj.getAttValueList();
		
		
		if(!plantGroupAttributeList.getKeys().contains(plantGroupChoiceKey))
		{
			plantGroupAttributeList.setValue(plantGroupChoiceKey, plantGroupChoiceValue, Locale.getDefault());
			plantGroupAttributeList.setSelectable(plantGroupChoiceKey, true);
			/* commented By Wipro upgrade team as this functionality not been used */
			//FlexTypeHelper.service.updateAttributeValueList(plantGroupAttributeList);
			System.out.println("  <<<<<<<<<<<<  Value List  added is >>>>>>>>> " +plantGroupChoiceKey );
		}
		
    	
    }
	
	/**
	 * This function is using to get attributes data (like Plant_Group) as a Collection<String> from APS schema (where table name is PLM_APS_PLANT_GROUPS) to create/update PLM Plant Group
	 * @return apsPlantGroupsCollection - Collection<String>
	 * @throws IOException
	 * @throws SQLException
	 */
	public Collection<String> getAPSPlantGroupAttributesData() throws IOException, SQLException
	{
		
		Collection<String> apsPlantGroupsCollection = new ArrayList<String>();
		String plantGroupName = "";
		
		Connection connectionObj = new HBIConnectionUtil().connect();
		Statement statementObj = connectionObj.createStatement();
		String sqlQuery = "SELECT DISTINCT PLANT_GROUP FROM "+apsSchemaName;
		
		ResultSet results = statementObj.executeQuery(sqlQuery);
        while (results.next()) 
		{
        	plantGroupName = results.getString("PLANT_GROUP");
        	apsPlantGroupsCollection.add(plantGroupName);
		}
		
        statementObj.close();
        statementObj = null;
        new HBIConnectionUtil().closeConnection(connectionObj);
        
		
		return apsPlantGroupsCollection;
	}
}
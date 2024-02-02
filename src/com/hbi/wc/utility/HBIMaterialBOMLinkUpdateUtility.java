package com.hbi.wc.utility;

import java.sql.SQLException;

import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Query;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIMaterialBOMLinkUpdateUtility.java
 * 
 * This class contains stand-alone functions to invoke from Windchill shell, update material FlexBOMLink to change 'Material Description'(ATT5) column data from old material to new material
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since November-02-2017
 */
public class HBIMaterialBOMLinkUpdateUtility implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "integrationuser");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "hbiIntPass");
	private static RemoteMethodServer remoteMethodServer;
	
	//SELECT ida3masterreference FROM WCADMIN1.FLEXBOMPART WHERE ATT1='BOM Part For: P-WO2182';
	//Using above query to get 'ida3masterreference' from FlexBOMPart which is equal to IDA3A5 in FlexBOMLink table, in above query we are using BOM Part Name that is taken from Garment Cut
	//BOM Master Material, the reason for choosing particular master material is this master material BOM has/contains material which is updated for 'Material Name' change.
	//SELECT ATT5 FROM WCADMIN1.FLEXBOMLINK WHERE IDA3A5=73369094;
	//Above query is using to just make sure the old material name exists in FlexBOMLink ATT5 column (FlexBOMLink ATT5 is for 'Material Description'), this column need to update as below.
	private static String flexBOMPartida3masterreference = "73564865";
	private static String oldMaterialName = "WO2184";
	private static String correctMaterialname = "WV22XX";
	
	/* Default executable function of the class HBIMaterialBOMLinkUpdateUtility */
	public static void main(String[] args) 
	{
		LCSLog.debug("### START HBIMaterialBOMLinkUpdateUtility.main() ###");
		
		try
		{
			//MethodContext mcontext = new MethodContext((String) null, (Object) null);
			//SessionContext sessioncontext = SessionContext.newContext();

			remoteMethodServer = RemoteMethodServer.getDefault();
	        remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
	        remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
	        
	        GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);
	        
			//This block of code is using to initialize RemoteMethodServer call parameters (RemoteMethodServer call argument types and argument values) and invoking RemoteMethodServer
			Class<?> argTypes[] = {String.class};
			String argValues[] = {flexBOMPartida3masterreference};
			remoteMethodServer.invoke("updateFlexBOMLinkMaterialDescChanges", "com.hbi.wc.utility.HBIMaterialBOMLinkUpdateUtility", null, argTypes, argValues);
	        System.exit(0);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIMaterialBOMLinkUpdateUtility.main() ###");
	}
	
	/**
	 * This function is using for updating FlexBOMLink record to change 'Material Name' on create event link because Query Builder report is reading material name from create event record
	 * @param flexBOMLinkIDA3A5 - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public static void updateFlexBOMLinkMaterialDescChanges(String flexBOMLinkIDA3A5) throws WTException, WTPropertyVetoException, SQLException
	{
		LCSLog.debug("### START HBIMaterialBOMLinkUpdateUtility.updateFlexBOMLinkMaterialDescChanges(String flexBOMLinkIDA3A5) ###");
		
		//Preparing SQL Query for updating FlexBOMLink record to change 'Material Name' on create event link, because Query Builder report is reading material name from material description
    	String materialDescriptionUpdateQuery = "UPDATE FLEXBOMLINK SET ATT5='"+correctMaterialname+"' WHERE ATT5='"+oldMaterialName+"' AND IDA3A5="+flexBOMLinkIDA3A5;
    	
    	//Initializing Query object(which internally take care of Database Connection and managing connection pool), using query instance to update FlexBOMLink for Material Name change
    	Query query = new Query();    
		query.prepareForQuery();
    	query.runUpdate(materialDescriptionUpdateQuery);
    	query.commit();
    	query.cleanUpQuery();
		
		LCSLog.debug("### END HBIMaterialBOMLinkUpdateUtility.updateFlexBOMLinkMaterialDescChanges(String flexBOMLinkIDA3A5) ###");
	}
}
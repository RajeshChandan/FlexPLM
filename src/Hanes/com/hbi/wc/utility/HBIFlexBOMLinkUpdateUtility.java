package com.hbi.wc.utility;

import java.sql.SQLException;

import com.lcs.wc.db.Query;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HBIFlexBOMLinkUpdateUtility implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "integrationuser");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "hbiIntPass");
	private static RemoteMethodServer remoteMethodServer;
	
	/* Default executable function of the class HBIFlexBOMLinkUpdateUtility */
	public static void main(String[] args) 
	{
		LCSLog.debug("### START HBIFlexBOMLinkUpdateUtility.main() ###");
		
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
			String argValues[] = {"73368676"};
			remoteMethodServer.invoke("updateMaterialDescOnCreateEventLink", "com.hbi.wc.utility.HBIFlexBOMLinkUpdateUtility", null, argTypes, argValues);
	        System.exit(0);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIFlexBOMLinkUpdateUtility.main() ###");
	}
	
	/**
	 * This function is using for updating FlexBOMLink record to change 'Material Name' on create event link because Query Builder report is reading material name from create event record
	 * @param materialIDA2A2 - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public static void updateMaterialDescOnCreateEventLink(String flexBOMLinkIDA2A2) throws WTException, WTPropertyVetoException, SQLException
	{
		LCSLog.debug("### START HBIFlexBOMLinkUpdateUtility.updateMaterialDescOnCreateEventLink(String flexBOMLinkIDA2A2) ###");
		
		//Preparing SQL Query for updating FlexBOMLink record to change 'Material Name' on create event link, because Query Builder report is reading material name from create event link
    	String materialNameUpdateQuery = "UPDATE FLEXBOMLINK SET ATT5='WV21XX' WHERE ATT5='WO2182' AND IDA2A2="+flexBOMLinkIDA2A2;
    	
    	//Initializing Query object(which internally take care of Database Connection and managing connection pool), using query instance to update FlexBOMLink for Material Name change
    	Query query = new Query();    
		query.prepareForQuery();
    	query.runUpdate(materialNameUpdateQuery);
    	query.commit();
    	query.cleanUpQuery();
		
		LCSLog.debug("### END HBIFlexBOMLinkUpdateUtility.updateMaterialDescOnCreateEventLink(String flexBOMLinkIDA2A2) ###");
	}
}
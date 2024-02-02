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

public class HBIMaterialCheckInUtility implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "integrationuser");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "hbiIntPass");
	private static RemoteMethodServer remoteMethodServer;
	private static String materialIDA2A2 = "76447981";
	
	/* Default executable function of the class HBIFlexBOMLinkUpdateUtility */
	public static void main(String[] args) 
	{
		LCSLog.debug("### START HBIMaterialCheckInUtility.main() ###");
		
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
			String argValues[] = {materialIDA2A2};
			remoteMethodServer.invoke("validateAndUpdateMaterialToCheckIn", "com.hbi.wc.utility.HBIMaterialCheckInUtility", null, argTypes, argValues);
	        System.exit(0);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIMaterialCheckInUtility.main() ###");
	}
	
	/**
	 * This function is using for updating LCSMaterial to change 'StateCheckOutInfo' from checked-out to check-in on latest iteration, because the latest iteration object is a checked-out.
	 * @param materialIDA2A2 - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws SQLException
	 */
	public static void validateAndUpdateMaterialToCheckIn(String materialIDA2A2) throws WTException, WTPropertyVetoException, SQLException
	{
		LCSLog.debug("### START validateAndUpdateMaterialToCheckIn(String materialIDA2A2) ###");
		String materialStateUpdateQuery = "UPDATE LCSMATERIAL SET statecheckoutinfo='c/i' WHERE IDA2A2="+materialIDA2A2;
		
		//Initializing Query object(which internally take care of Database Connection and managing connection pool), using query instance to update LCSMaterial for Material State change
    	Query query = new Query();    
		query.prepareForQuery();
    	query.runUpdate(materialStateUpdateQuery);
    	query.commit();
    	query.cleanUpQuery();
		
		LCSLog.debug("### END validateAndUpdateMaterialToCheckIn(String materialIDA2A2) ###");
	}
}
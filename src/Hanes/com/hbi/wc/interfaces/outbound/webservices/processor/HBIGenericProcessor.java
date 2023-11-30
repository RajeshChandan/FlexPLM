package com.hbi.wc.interfaces.outbound.webservices.processor;

import java.io.IOException;

import com.hbi.wc.interfaces.outbound.webservices.util.HBISFTPFunctionsUtil;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBIGenericProcessor.java
 * 
 * This class is using as a Processor(which will invoke data sync tool from source system to target system) to process all pending transactions by invoking object specific processors
 * @author Abdul.Patel@Hanes.com
 * @since  April-10-2015
 */
public class HBIGenericProcessor implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = "";
	private static String CLIENT_ADMIN_PASSWORD = "";
	private static RemoteMethodServer remoteMethodServer;
	
	static
	{
		try
		{
			CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "Administrator");
			CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "QAadmin");
		}
		catch (Exception exp)
		{
			LCSLog.debug("Exception in static block of the class HBIGenericProcessor is :: " + exp);
		}
	}
	
	/** Default executable function of the class HBIGenericProcessor */
	public static void main(String[] args)
	{
		LCSLog.debug("### START HBIGenericProcessor.main() ###");
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
			
			//remoteMethodServer.invoke("invokeMeasurementsDataProcessor", "com.hbi.wc.interfaces.outbound.webservices.processor.HBIGenericProcessor", null, null, null);
			
			//calling a function which will validate and sync Measurements(Template and Instance) data from source FlexPLM to target FlexPLM based on the user actions on source PLM
			invokeMeasurementsDataProcessor();
			System.exit(0);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIGenericProcessor.main() ###");
	}
	
	/**
	 * This function is using to invoke each FlexObject(LCSMeasurements, LCSPointsOfMeasure, etc) Processor to initialize Service Locator and invoke SOAP Protocol to perform actions
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	public static void invokeMeasurementsDataProcessor() throws WTException, WTPropertyVetoException, IOException
	{
		LCSLog.debug("### START HBIGenericProcessor.invokeMeasurementsDataProcessor() ###");
		
		//Calling a function to initialize Service Locator and invoke SOAP Protocol with java bean object to perform various actions on LCSMeasurements objects in target server
		HBIMeasurementsDataProcessor.invokeMeasurementsDataProcessor();
		
		//Calling a function to initialize Service Locator and invoke SOAP Protocol with java bean object to perform various actions on LCSPointsOfMeasure objects in target server
		HBIPointsOfMeasureDataProcessor.invokePointsOfMeasureDataProcessor();
		
		//calling a function to validate the FTPClient connectivity status within the context, if the FTPClient is connected, then disconnect from the connected Host(Predefined)
		//new HBISFTPFunctionsUtil().closeSFTPConnection();
		
		LCSLog.debug("### END HBIGenericProcessor.invokeMeasurementsDataProcessor() ###");
	}
}

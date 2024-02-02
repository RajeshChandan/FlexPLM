package com.hbi.wc.interfaces.inbound.esko;

import java.net.MalformedURLException;
import java.io.Serializable;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.method.RemoteAccess;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.httpgw.GatewayAuthenticator;

/**
 * HBIMaterialDataProcessor.java
 *
 * This class is using as a Processor(which will invoke data sync tool from source to target FlexPLM system) to process material data from ESKO server (based on the unique criteria's)
 * to FlexPLM server based on the given data map (a container, which contains attribute data for all set of attributes).
 * @author Abdul.Patel@Hanes.com
 * @since June-15-2015
 */
public class HBIMaterialDataProcessor implements RemoteAccess, Serializable
{
    private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "Administrator");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "Administrator");
    private static RemoteMethodServer remoteMethodServer;

    /**
     * Default executable function of the class HBIMaterialDataProcessor
     */
    public static void main(String[] args) 
    {
        //LCSLog.debug("### START HBIMaterialDataProcessor.main() ###");
    	System.out.println("### START HBIMaterialDataProcessor.main() ###");
        
        try 
        {
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			remoteMethodServer = RemoteMethodServer.getDefault();
			
			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID); //username here
			//authenticator.setRemoteUser("prodadmin"); //username here
			remoteMethodServer.setAuthenticator(authenticator);
			WTPrincipal principal = SessionHelper.manager.getPrincipal();
			Class[] argumentClass = {};
			Object[] argumentObject = {};
			
			remoteMethodServer.invoke("processESKOMaterialDataLoadRequest", "com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataProcessor", null, argumentClass, argumentObject);

           //processESKOMaterialDataLoadRequest();
            System.exit(0);
        }
        catch (Exception exception) 
        {
            //LCSLog.debug("Exception in HBIMaterialDataProcessor.main() = "+ exception);
            exception.printStackTrace();
            System.exit(1);
        }
		
        //LCSLog.debug("### END HBIMaterialDataProcessor.main() ###");
        System.out.println("### START HBIMaterialDataProcessor.main() ###");
    }

    /**
     * This function is using as a invocation port of ESKO-FlexPLM material integration, extracting data from ESKO server (based on the unique criteria's) & loading into FlexPLM server
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws MalformedURLException
     */
    public static void processESKOMaterialDataLoadRequest() throws WTException, WTPropertyVetoException, MalformedURLException
    {
        //LCSLog.debug("### START HBIMaterialDataProcessor.processESKOMaterialDataLoadRequest() ###");
    	System.out.println("### START HBIMaterialDataProcessor.processESKOMaterialDataLoadRequest() ###");
		
        //Calling a function which is a invocation port of ESKO-FlexPLM material integration, extracting data from ESKO server (based on the unique criteria's) & loading into FlexPLM
        HBIMaterialDataExtractor.validateAndProcessMaterialDataExtractRequest();
		
        System.out.println("### END HBIMaterialDataProcessor.processESKOMaterialDataLoadRequest() ###");
        //LCSLog.debug("### END HBIMaterialDataProcessor.processESKOMaterialDataLoadRequest() ###");
    }
}
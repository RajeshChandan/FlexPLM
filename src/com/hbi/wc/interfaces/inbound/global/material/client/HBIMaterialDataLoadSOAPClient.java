package com.hbi.wc.interfaces.inbound.global.material.client;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import com.hbi.wc.interfaces.inbound.global.material.server.HBIMaterialDataLoadService;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIMaterialDataLoadSOAPClient.java
 *
 * This class contains a function which is using to make a web-services call by passing the soap message, web-services function is responsible to material data load.
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-14-2018
 */
public class HBIMaterialDataLoadSOAPClient
{
	private static String SERVER_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "integrationuser");
	private static String SERVER_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "hbiIntPass");
	
	private static String serviceURL = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.server.HBIMaterialDataLoadService.serviceURL", "http://wsflexwebprd1v.res.hbi.net/Windchill/servlet/HBIMaterialDataLoadService?wsdl");
    private static String serviceURI = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.server.HBIMaterialDataLoadService.serviceURI", "http://server.material.global.inbound.interfaces.wc.hbi.com/");
    private static String serviceName = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.server.HBIMaterialDataLoadService.serviceName", "HBIMaterialDataLoadServiceImplService");
    
	/*No-Arg Default constructor of the HBIMaterialDataLoadSOAPClient */
	public HBIMaterialDataLoadSOAPClient()
	{
		
	}
	
	/*This inner class is using to provide the authentication to a service which is invoking within the context of a class*/
	public class HBIAuthenticator extends Authenticator
	{
	    private String userName;
	    private String password;
	    
	    public HBIAuthenticator(String userName, String password)
	    {
	    	this.userName = userName; 
	        this.password = password; 
	    }
	    
	    @Override 
	    protected PasswordAuthentication getPasswordAuthentication()
	    {
	    	LCSLog.debug("getPasswordAuthenticator, protocol is " + this.getRequestingProtocol() + " userName= " + userName +" password= "+ password); 
	        
	        return new PasswordAuthentication(userName, password.toCharArray()); 
	    } 
	}
	
	/**
	 * Default executable method of the class HBIMaterialDataLoadSOAPClient, this function begin the execution to complete the global raw material load process
	 * @param args - String[]
	 */
	public static void main(String[] args) throws MalformedURLException, Exception
	{
		LCSLog.debug("### START HBIMaterialDataLoadSOAPClient.main() ###");
		
		//Calling a function which is using to make a web-services call by passing the soap message, web-services function is responsible to material data load
		//validateAndLoadGlobalRawMaterials();
		
		LCSLog.debug("### END HBIMaterialDataLoadSOAPClient.main() ###");
	}
	
	/**
     * This function is using to make a web-services call by passing the soap message, web-services function is responsible to material data load.
     * @param soapMessage - String
     * @return serviceResponse - String
     * @throws MalformedURLException
     * @throws Exception
     */
	public static String validateAndLoadGlobalRawMaterials(String soapMessage) throws MalformedURLException, Exception
	{
		// LCSLog.debug("### START HBIMaterialDataLoadProcessor.validateAndLoadGlobalRawMaterials(String soapMessage) ###");
		
		//Providing Authentication(username and password) to SOAP Client to fetch the data from server using web-services logic
        Authenticator.setDefault(new HBIMaterialDataLoadSOAPClient().new HBIAuthenticator(SERVER_ADMIN_USER_ID, SERVER_ADMIN_PASSWORD));
        
        //Forming URL instance using WSDL address from server, forming QName instance using 'Service Name', initializing 'Service' using URL and QName instances
        URL url = new URL(serviceURL);
        QName qname = new QName(serviceURI, serviceName);
        Service service = Service.create(url, qname);
        
        //Get Service instance (which contains the server information needed to establish connection for virtual communication) and invoke Service Interface function
        HBIMaterialDataLoadService serviceInterfaceObj = service.getPort(HBIMaterialDataLoadService.class);
        String serviceResponse = serviceInterfaceObj.loadGlobalRawMaterialToPLM(soapMessage);
        LCSLog.debug("### HBIMaterialDataLoadSOAPClient.loadGlobalRawMaterialToPLM :: serviceResponse = "+ serviceResponse);
        
        // LCSLog.debug("### END HBIMaterialDataLoadProcessor.validateAndLoadGlobalRawMaterials(String soapMessage) ###");
        return serviceResponse;
	}
}
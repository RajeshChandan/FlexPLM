package com.hbi.wc.interfaces.inbound.esko.utility;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor;
import com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataLoader;
import com.hbi.wc.interfaces.outbound.webservices.util.HBIProperties;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIInsertTypeMaterialProcessor.java
 *
 * This class contains various functions which are using to get ESKO Part based on the given Project_ID, get Attribute, Document and Supplier data to perform create/update event in FlexPLM
 * and this Processor class is developed specific to 'Insert Type' Material processing (as the existing Integration Framework does not including the configuration for 'Insert Type' process)
 * @author Abdul.Patel@Hanes.com
 * @since October-31-2016
 */
public class HBIInsertTypeMaterialProcessor implements RemoteAccess
{
	private static String eskoPartNumberKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.PartNumber", "flexAttname");
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "integrationuser");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "hbiIntPass");
    private static RemoteMethodServer remoteMethodServer;
    
    private static String projectIDSet = "00002_0000040504";
    private static String materialFlexTypePath = "Material\\Packaging\\Paperboard";
    private static String sourcedToProduction = "true";
    //private static String majorCategory = "printedPaperboard";
    //private static String minorCategory = "plainInserts";

    /**
     * Default executable function of the class HBIMaterialIntegrationUtility
     */
    public static void main(String[] args) 
    {
        LCSLog.debug("### START HBIInsertTypeMaterialProcessor.main() ###");
        
        try 
        {
            MethodContext mcontext = new MethodContext((String) null, (Object) null);
            SessionContext sessioncontext = SessionContext.newContext();

            remoteMethodServer = RemoteMethodServer.getDefault();
            remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
            remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID); //username here
			remoteMethodServer.setAuthenticator(authenticator);
			
            validateAndProcessESKOProjectDataExtractor();
            System.exit(0);
        }
        catch (Exception exception) 
        {
            exception.printStackTrace();
            System.exit(1);
        }

        LCSLog.debug("### END HBIInsertTypeMaterialProcessor.main() ###");
    }
    
    public static void validateAndProcessESKOProjectDataExtractor() throws WTException, WTPropertyVetoException, Exception
    {
    	LCSLog.debug("### START HBIInsertTypeMaterialProcessor.validateAndProcessESKOProjectDataExtractor() ###");
    	
    	if(projectIDSet.contains(","))
    	{
    		for(String projectID : projectIDSet.split(","))
    		{
    			validateAndProcessESKOProjectDataExtractor(projectID);
    		}
    	}
    	else
    	{
    		validateAndProcessESKOProjectDataExtractor(projectIDSet);
    	}
    	
    	LCSLog.debug("### END HBIInsertTypeMaterialProcessor.validateAndProcessESKOProjectDataExtractor() ###");
    }
    
    /**
     * This method is using to validate the project document approval status (vendor approved) for the given project id and invoke internal functions to load material data into FlexPLM
     * @return materialSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws Exception
     */
    public static void validateAndProcessESKOProjectDataExtractor(String projectID) throws WTException, WTPropertyVetoException, Exception
    {
    	// LCSLog.debug("### START HBIInsertTypeMaterialProcessor.validateAndProcessESKOProjectDataExtractor(String projectID) ###");
    	
    	 //Invoking method to get project approval status (vendor approved) for the given project id, which is then used to sync/integrate material data from ESKO to FlexPLM
        boolean projectApprovalStatus = new HBIMaterialDataExtractor().getESKOProjectVendorApprovalStatus(projectID);
        if(projectApprovalStatus) 
        {
        	 //Get Element from a specific Project Node and invoke internal function to get 'All Attributes' for the given 'Project ID' which are using to update/cascade in FlexPLM
            Map<String, Object> eskoMaterialDataMapObj = getAllESKOAttributesFromProject(projectID);
            LCSLog.debug("HBIMaterialDataExtractor.validateAndProcessESKOProjectDataExtractor(elementObj, eskoMaterialDataMapObj) eskoMaterialDataMapObj :: " + eskoMaterialDataMapObj);
            
            //Get 'Part Number' from the given data map and invoke internal functions which is using to validate the given data and load material data from ESKO to FlexPLM
            String partNumber = (String) eskoMaterialDataMapObj.get(eskoPartNumberKey);
            
            //If the part number contains multiple materials separated by comma, then invoke the overloaded method of this method validateAndProcessESKOProjectDataExtractor
            if(FormatHelper.hasContent(partNumber) && partNumber.contains(",")) 
            {
            	new HBIMaterialDataExtractor().validateAndProcessESKOProjectDataExtractor(projectID, partNumber, eskoMaterialDataMapObj);
            }
            else if(FormatHelper.hasContent(partNumber)) 
            {
            	//Calling a function which is using to validate the given 'Part Number' size and split into 'Material Name', 'Color Code', 'Attribute Code' and 'Size Code' attribute value
                eskoMaterialDataMapObj = new HBIMaterialDataExtractor().validateAndPopulateFlexPLMMaterialSKUAttributeData(partNumber, eskoMaterialDataMapObj);
                partNumber = ""+(String)eskoMaterialDataMapObj.get(eskoPartNumberKey);
                
                //Calling a method from HBIMaterialDataLoader which is used to Process Material(perform create/update event) in FlexPLM based on the given material data map from ESKO
                new HBIMaterialDataLoader().validateAndProcessMaterialDataLoadRequest(eskoMaterialDataMapObj);
                new HBIMaterialDataExtractor().validateAndProcessESKOSupplierDataExtractor(projectID, partNumber, eskoMaterialDataMapObj);
            }
        }
    	
    	// LCSLog.debug("### END HBIInsertTypeMaterialProcessor.validateAndProcessESKOProjectDataExtractor(String projectID) ###");
    }
    
    /**
     * This method is used to format/initialize getAttributes URL from the given Product_Node and Element, format Attributes data map and invoke internal function for FlexPLM event
     * @param projectID - String
     * @return eskoMaterialDataMapObj - Map<String, Object>
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static Map<String, Object> getAllESKOAttributesFromProject(String projectID) throws IOException, ParserConfigurationException, SAXException 
    {
        // LCSLog.debug("### START HBIInsertTypeMaterialProcessor.getAllESKOAttributesFromProject(projectNodes, elementObj) ###");
        LCSLog.debug("### HBIMaterialDataExtractor.getAllESKOProjectsForURL() Project ID = " + projectID);
        Element attElementObj = null;
        String attributeName = "";
        String attributeValue = "";
        Map<String, Object> eskoMaterialDataMapObj = new HashMap<String, Object>();

        //initialize all required parameters(parameters needed to get all all attributes) and construct a URL using 'GetAttributes.jsp' as target file to retrieve the data(Attributes)
        String projectAttributesCriteriaURL = new HBIMaterialDataExtractor().getESKOGenericURLFor("GetAttributes.jsp");
        projectAttributesCriteriaURL = projectAttributesCriteriaURL.concat("&projectid=" + projectID).trim();
        URL projectAttributesURLObj = new URL(projectAttributesCriteriaURL);
        LCSLog.debug("### HBIMaterialDataExtractor.getAllAttributesFromProject() URL to get All Attributes for a Project :: " + projectAttributesCriteriaURL);

        //Initializing Document Builder, get XML from the given ESKO URL and parse the XML to Document object, get a List of 'Attribute' Nodes from an existing document object 
        DocumentBuilderFactory docBuilderFactObj = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilderObj = docBuilderFactObj.newDocumentBuilder();
        Document documentObj = docBuilderObj.parse(projectAttributesURLObj.openStream());
        NodeList attributeNodes = documentObj.getElementsByTagName("att");

        //Iterating on each 'Attribute' Node, each node contains attribute display name, attribute data type and attribute value which are using to update/cascade from ESKO-FlexPLM
        for (int nodeIndex = 0; nodeIndex < attributeNodes.getLength(); nodeIndex++) 
        {
            attElementObj = (Element) attributeNodes.item(nodeIndex);
            if (attElementObj.getElementsByTagName("an").getLength() > 0 && attElementObj.getElementsByTagName("av").getLength() > 0) 
            {
                //Get Attribute_Name and Attribute_Value from the given Element Node, invoking/calling internal function to prepare and return Attribute data map for the given Element
                attributeName = attElementObj.getElementsByTagName("an").item(0).getTextContent();
                attributeValue = attElementObj.getElementsByTagName("av").item(0).getTextContent();
                eskoMaterialDataMapObj = new HBIMaterialDataExtractor().getESKOAttributesDataMapForElement(attElementObj, attributeName, attributeValue, eskoMaterialDataMapObj);
            }
        }
        
        //This block of code is added specific to 'Insert Type' Material processing because 'Insert Type' material mappings are not defined in configuration file
        eskoMaterialDataMapObj.put(HBIProperties.materialFlexTypePathKey, materialFlexTypePath);
        eskoMaterialDataMapObj.put(HBIProperties.flexTypeAttributeKeyAppender.concat(HBIProperties.hbiBuyOrNotBuyKey), sourcedToProduction);
        
        // LCSLog.debug("### END HBIInsertTypeMaterialProcessor.getAllESKOAttributesFromProject(projectNodes, elementObj) ###");
        return eskoMaterialDataMapObj;
    }
}
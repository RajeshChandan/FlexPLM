package com.hbi.wc.interfaces.inbound.esko;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.fop.pdf.PDFDocument;
import org.apache.fop.render.pdf.PDFRenderer;
import org.apache.fop.render.pdf.PDFRenderingContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import wt.fc.delete.DeleteHelper;
import wt.method.RemoteAccess;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.hbi.wc.interfaces.outbound.product.HBIInterfaceUtil;
import com.hbi.wc.interfaces.outbound.webservices.util.HBIProperties;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.country.LCSCountryQuery;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.load.LoadCommon;
import com.lcs.wc.load.LoadMaterial;
import com.lcs.wc.load.LoadSku;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.util.DeleteFileHelper;
import com.lcs.wc.util.FileLocation;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;


/**
 * HBIMaterialDataExtractor.java
 *
 * This class contains generic functions which are using to get project data, material data extraction, formating unique criteria's and invoking internal functions for data load in PLM
 * @author Mallikarjun.Sulekar@Hanes.com
 * @since June-8-2015
 * Update April 2020-Garment Label Material sub type added 
 */
public class HBIMaterialDataExtractor implements RemoteAccess 
{
    private static String eskoFlexPLMAttMappingPropertyEntryKey = "com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.";
    private static String eskoFlexPLMSupplierMappingPropertyEntryKey = "com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.SupplierName.";
    private static String eskoSingleListAttributesKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.eskoSingleListAttributes", "Division,SourcedProductionOnly,HEIDivision,LabelType,Retailcountriesintended,LabelFormat,BrandUsage,GarmentLabelSub-Type,Language,Hemisphere,COOprintedonart,Usage");
    //April 2020 - LabelType added
	//July 28 - HEIBagType Added
    private static String eskoFlexTypeAttributesKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.eskoFlexTypeAttributes", "Type,BagType,StickerType,CaseType,DisplayType,HEIStickerTypes,InsertType,LabelType,HEIBagType");
    public static String eskoPartNumberKey = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.PartNumber", "flexAttname");
    private static String businessObjectFiberPath ="Business Object\\Fiber Code & Content";
    public static final String subURLFolder = LCSProperties.get("flexPLM.windchill.subURLFolderLocation");
    public static final String DEFAULT_ENCODING = LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset","UTF-8");
    static final String IMAGE_URL = LCSProperties.get("com.lcs.wc.content.imageURL", "/LCSWImages");
    static String FILE_PATH = FormatHelper.formatOSFolderLocation(LCSProperties.get("com.lcs.wc.content.imagefilePath", "\\images"));
    public static final boolean DEBUG = LCSProperties.getBoolean("jsp.main.ImageFileProcessor.verbose");
    static long POST_SIZE = -1;
    static boolean imagefilePathOverride = false;
    static String MULTIPLE_FILE_PATH = null;
    static String CLEAR_CELL_VALUE = null;
    static long updateTime=0;
    /**
     * This function is using as a invocation port of ESKO-FlexPLM material integration, extracting data from ESKO server (based on the unique criteria's) & loading into FlexPLM server
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws MalformedURLException
     * called from com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataProcessor
     * which is called from bat file in codebase/com/hbi ESKO\ESKOFLEXPLMMATERIALINTEGRATION.bat
     */
    
    static {
        try {
        	updateTime=getTimePeriodForUpdate();
            LCSLog.debug("::::::::::::::getting updateTime::::::::::::"+updateTime);
            WTProperties wtproperties = WTProperties.getLocalProperties();
            String wtHome = wtproperties.getProperty("wt.home");
            String imageMaxFileSize = LCSProperties.get("com.lcs.wc.content.imageMaxFileSize", "104857600");
            POST_SIZE = Long.parseLong(imageMaxFileSize);
            CLEAR_CELL_VALUE = LCSProperties.get("com.lcs.wc.LCSLogic.ClearCellValue", "!CLR");
            if(LCSProperties.get("com.lcs.wc.content.imagefilePathOverride") != null && !"".equals(LCSProperties.get("com.lcs.wc.content.imagefilePathOverride").trim())){
                FILE_PATH = LCSProperties.get("com.lcs.wc.content.imagefilePathOverride").trim();
                MULTIPLE_FILE_PATH = FILE_PATH;
                StringTokenizer st = new StringTokenizer(FILE_PATH, ",");
                FILE_PATH = st.nextToken();
                imagefilePathOverride = true;
            }else{
                FILE_PATH = wtHome + FILE_PATH;
                imagefilePathOverride = false;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void validateAndProcessMaterialDataExtractRequest() throws WTException, WTPropertyVetoException, MalformedURLException
    {
       // LCSLog.debug("### START HBIMaterialDataExtractor.validateAndProcessMaterialDataExtractRequest() ###");
    	System.out.println("### START HBIMaterialDataExtractor.validateAndProcessMaterialDataExtractRequest() ###");
        String eskoSavedSearchForDocument = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.eskoSavedSearchForDocument", "HBI_Project_Approvals");
        
      new HBIMaterialDataExtractor().validateAndProcessESKODocumentDataExtractor(eskoSavedSearchForDocument);
    	
        //Initialize all required parameters(parameters needed to get all ESKO Projects) and construct a URL using 'GetProjects.jsp' as target file to retrieve the data(Projects)
        String type = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.type", "4");
        String numdays = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.numdays", "1");
        String eskoProjectsCriteria = "&type=".concat(type).concat("&numdays=").concat(numdays);
        String eskoProjectsCriteriaURL = new HBIMaterialDataExtractor().getESKOGenericURLFor("GetProjects.jsp");
        eskoProjectsCriteriaURL = eskoProjectsCriteriaURL.concat(eskoProjectsCriteria).trim();
        //LCSLog.debug("### HBIMaterialDataExtractor.validateAndProcessMaterialDataExtractRequest() URL to get All Projects :: " + eskoProjectsCriteriaURL);
       // System.out.println("### HBIMaterialDataExtractor.validateAndProcessMaterialDataExtractRequest() URL to get All Projects :: " + eskoProjectsCriteriaURL);
        //Calling a function which is using to get all ESKO Projects from the given URL, iterate on each Project to get Project_ID then get 'All Attributes' from each Project
        new HBIMaterialDataExtractor().getAllESKOProjectsForURL(eskoProjectsCriteriaURL);
        
       // LCSLog.debug("### END HBIMaterialDataExtractor.validateAndProcessMaterialDataExtractRequest() ###");
        System.out.println("### END HBIMaterialDataExtractor.validateAndProcessMaterialDataExtractRequest() ###");
    }

    /**
     * This function returns a generic ESKO function URL(contains home_location, target JSP file name, user name and password) based on the given data retrieval criteria(JSP file)
     * @param jspFileName - String
     * @return eskoProjectsGenericURL - String
     */
    public String getESKOGenericURLFor(String jspFileName) 
    {
        // LCSLog.debug("### START HBIMaterialDataExtractor.getESKOGenericURLFor() ###");

        String ESKO_HOME = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.ESKO_HOME", "http://hbipds.hanesbi.net/WebCenter/");
        String username = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.usename", "Admin");
        String password = LCSProperties.get("com.hbi.wc.interfaces.inbound.esko.HBIMaterialDataExtractor.password", "nomorestrings");

        // Formating the ESKO URL based on the given criteria's(ESKO_HOME location, JSP file to retrieve the data, username and password of the server for log-in authentication)
        String eskoProjectsGenericURL = ESKO_HOME.concat(jspFileName).concat("?").concat("username=" + username).concat("&password=" + password).trim();

        // LCSLog.debug("### END HBIMaterialDataExtractor.getESKOGenericURLFor() ###");
        return eskoProjectsGenericURL;
    }

    /**
     * This function is used to get all the ESKO projects (FlexPLM material) from the XML generated from the given ESKO URL Based on the XML parsing project list Node Element is parsed
     * For each project, a Node Element is parsed. This node elements of XML are parsed and used for data population
     * @param eskoProjectsCriteriaURL - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws MalformedURLException
     */
    public void getAllESKOProjectsForURL(String eskoProjectsCriteriaURL) throws WTException, WTPropertyVetoException, MalformedURLException
    {
        // LCSLog.debug("### START HBIMaterialDataExtractor.getAllESKOProjectsForURL(eskoProjectsGenericURL) ###");
        URL eskoProjectsURLObj = new URL(eskoProjectsCriteriaURL);
        Element elementObj = null;
        try 
        {
            //Initializing Document Builder, get XML from the given ESKO URL and parse the XML to Document object, get a List of 'Project' Nodes from an existing document object 
            DocumentBuilderFactory docBuilderFactObj = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilderObj = docBuilderFactObj.newDocumentBuilder();
            Document documentObj = docBuilderObj.parse(eskoProjectsURLObj.openStream());
            NodeList projectNodes = documentObj.getElementsByTagName("project");
			long start = System.currentTimeMillis();

            //Iterating on each 'Project' Nodes, to get Element from each Node get 'Project' Node specific attributes(like get Project_ID from Project Node Element)
            for(int nodeIndex = 0; nodeIndex < projectNodes.getLength(); nodeIndex++) 
            {
                //Get Element from a specific Project Node and invoke internal functions which are used to format data set using an existing mappings and invoke FlexPLM triggers
                elementObj = (Element) projectNodes.item(nodeIndex);
                
              
                
                String projectID = elementObj.getAttribute("id");
    	    	String updatedTime= elementObj.getElementsByTagName("modified_date").item(0).getTextContent();
    			

                long updatedTimeStamp=Long.parseLong(updatedTime);
               long time= (long) ((start - updatedTimeStamp) / 1000d);
   			  //LCSLog.debug(":::::::time since update has been made ::::::::::::"+time);
               System.out.println(":::::::time since update has been made ::::::::::::"+time);
              
   			

                 if(time<updateTime){
                processESKOProjectDataExtractor(projectNodes, projectID);
                 }
                 else{
                  //LCSLog.debug("::::::project updated more than an hour ago hence ignoring::::::::::"+projectID);
                	 System.out.println("::::::project updated more than an hour ago hence ignoring::::::::::"+projectID);
	 
                 }
                 }
            

        }
        catch (IOException ioExp) 
        {
        	ioExp.printStackTrace();
            //LCSLog.debug("IOException in HBIMaterialDataExtractor.getAllESKOProjectsForURL() is :: " + ioExp);
        	//System.out.println("IOException in HBIMaterialDataExtractor.getAllESKOProjectsForURL() is :: " + ioExp);
        } 
        catch (ParserConfigurationException parserExp) 
        {
        	parserExp.printStackTrace();
            //LCSLog.debug("ParserConfigurationException in HBIMaterialDataExtractor.getAllESKOProjectsForURL() is :: " + parserExp);
        	//System.out.println("ParserConfigurationException in HBIMaterialDataExtractor.getAllESKOProjectsForURL() is :: " + parserExp);
        }
        catch (SAXException saxExp) 
        {
        	saxExp.printStackTrace();
            //LCSLog.debug("SAXException in HBIMaterialDataExtractor.getAllESKOProjectsForURL() is :: " + saxExp);
        	
        }

    }
    
    private static long getTimePeriodForUpdate() {
		// TODO Auto-generated method stub
    	long duration=3600;
    	try {
			LCSLifecycleManaged bo=(LCSLifecycleManaged)new HBIInterfaceUtil().getLifecycleManagedByNameType("name", "Job Administration", "Business Object\\PLM Job Administration");
			if(bo!=null){
				duration=(long)bo.getValue("hbiDuration"); 
				//duration = duration1.longValue();
				//LCSLog.debug(":::::::Duration to pick the ESKO Materials:::::::::::::::::"+duration);
				System.out.println(":::::::Duration to pick the ESKO Materials:::::::::::::::::"+duration);
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return duration;
	}

	/**
     * This function is using to get 'Part Number' from the given Project ID, validate the 'Part Number' to check for multiple part numbers in one project, extract data from all attribute
     * @param projectNodes - NodeList
     * @param elementObj - Element
     * @return materialSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public String processESKOProjectDataExtractor(NodeList projectNodes,  String projectID ) throws WTException, WTPropertyVetoException, IOException, SAXException, ParserConfigurationException
    {
    	
    	//http://hbipds.hanesbi.net/WebCenter/projdetailsgeninfo.jsp?projectID=00002_0000055805&folderID=&menu_file=projsearchresults
    	
    	String materialSyncStatus = "";
    	//Get Element from a specific Project Node and invoke internal function to get 'All Attributes' for the given 'Project ID' which are using to update/cascade in FlexPLM
        Map<String, Object> eskoMaterialDataMapObj = getAllESKOAttributesFromProject(projectNodes, projectID);
    	String imageUrl = getThumbnail(projectID);
    	eskoMaterialDataMapObj.put("imageUrl", imageUrl);


        //LCSLog.debug("HBIMaterialDataExtractor.validateAndProcessESKOProjectDataExtractor(elementObj, eskoMaterialDataMapObj) eskoMaterialDataMapObj :: " + eskoMaterialDataMapObj);

        //Get 'Part Number' from the given data map and invoke internal functions which is using to validate the given data and load material data from ESKO to FlexPLM
        String partNumber = (String) eskoMaterialDataMapObj.get(eskoPartNumberKey);
        //LCSLog.debug("<<<<<<<<<<ESKO Part Number from the batch JOB>>>>>>>>>>>>>>>" + partNumber);
        boolean projectApprovalStatus = getESKOProjectVendorApprovalStatus(projectID);
       /// LCSLog.debug("<<<<<<<<<<ESKO Part Number from the batch JOB projectApprovalStatus>>>>>>>>>>>>>>>" + projectApprovalStatus);

        
      if(FormatHelper.hasContent(partNumber)&&!partNumber.startsWith("E") &&projectApprovalStatus ){
        //Garment Label material - size range
        List<String> sizeCodeValues =new ArrayList();
        //Checking whether the in coming material is a Garment material
        if(eskoMaterialDataMapObj.containsKey("flexAtthbiApplication")) {

        	// eskoMaterialDataMapObj.put("eskoDocName",imageUrl)
        	String ValidationForAccessories=checkForAccessoriesMaterialType(eskoMaterialDataMapObj);
        	if(!FormatHelper.hasContent(ValidationForAccessories)){

        	String sizeCode = (String) eskoMaterialDataMapObj.get("hbiSizeCode");
        	String hbiGarmentSize = (String) eskoMaterialDataMapObj.get("hbiGarmentSize");


        	if(FormatHelper.hasContent(partNumber) && partNumber.contains(",")) {
	        	if(FormatHelper.hasContent(hbiGarmentSize) && hbiGarmentSize.contains(",")) {
	        		 sizeCodeValues = getCommaSeparatedToList(hbiGarmentSize);

	        		List<String> eskoPartNumberValues = getCommaSeparatedToList(partNumber);

	             	if(sizeCodeValues.isEmpty()) {
	             		for (int i = 0; i < eskoPartNumberValues.size(); i++) 
	                 	{
	                 		partNumber = eskoPartNumberValues.get(i);
	                 		
	                 		//Calling a function to validate the given 'Part Number', based on the 'Part Number' invoking functions to extract attributes data (mapped attributes) and load to PLM
	                 		materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
	                 	}
	             	}else {
	             		if(sizeCodeValues.size()==1) {
	             			materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
	             		}else {
	             			if(eskoPartNumberValues.size()==sizeCodeValues.size()) {
		             			for (int i = 0; i < eskoPartNumberValues.size(); i++) 
			                 	{
			                 		partNumber = eskoPartNumberValues.get(i);
			                 		eskoMaterialDataMapObj.put("hbiSizeCode", sizeCodeValues.get(i));
			                 		eskoMaterialDataMapObj.put("hbiGarmentSize", sizeCodeValues.get(i));

			                 		materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
			                 	}
		             		}else {
		             			LCSLog.debug("Invalid SizeRange in the given Material Data where ESKO Project ID = " + projectID);
		             			new ESKOLogEntryForFailures().logTransaction(new Date(), partNumber, "Invalid SizeRange in the given Material Data where ESKO Project ID = "+projectID , false);
		             		}
	             		}
	             		
	             	}
	        	} else  {
	        		materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
	        	}
	        }else if(FormatHelper.hasContent(partNumber)) {
	        	materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
	        }
        	}
        	else{
        		//LCSLog.debug("--------------SKIPPED MATERIAL CREATION DUE TO ERROR----------------"+ValidationForAccessories);
        		System.out.println("--------------SKIPPED MATERIAL CREATION DUE TO ERROR----------------"+ValidationForAccessories);
        		boolean accessories=true;
        		new ESKOLogEntryForFailures().logTransaction(new Date(), partNumber, ValidationForAccessories,accessories);

        	}
        }else {
        	//LCSLog.debug("in-coming material is not Garment label material");
        	System.out.println("in-coming material is not Garment label material");
        	 //If the part number contains multiple materials separated by comma, then invoke the overloaded method of this function to validateAndProcessESKOProjectDataExtractor
            if(FormatHelper.hasContent(partNumber) && partNumber.contains(",")) 
            {
            	List<String> eskoPartNumberValues = getCommaSeparatedToList(partNumber);
            	for (int i = 0; i < eskoPartNumberValues.size(); i++) 
             	{
             		partNumber = eskoPartNumberValues.get(i);
             		
             		//Calling a function to validate the given 'Part Number', based on the 'Part Number' invoking functions to extract attributes data (mapped attributes) and load to PLM
             		materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
             	}
            
            }
            else if(FormatHelper.hasContent(partNumber)) 
            {
            	//Calling a function to validate the given 'Part Number', based on the 'Part Number' invoking functions to extract attributes data (mapped attributes) and load to PLM
            	materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
            }
        }
      //LCSLog.debug("materialSyncStatus:::::::::::::::::::::::::"+materialSyncStatus);
      }
	  else if(FormatHelper.hasContent(partNumber)&&partNumber.startsWith("E")){
        //Garment Label material - size range
        List<String> sizeCodeValues =new ArrayList();
        //Checking whether the in coming material is a Garment material
        if(eskoMaterialDataMapObj.containsKey("flexAtthbiApplication")) {

        	// eskoMaterialDataMapObj.put("eskoDocName",imageUrl)
        	String ValidationForAccessories=checkForAccessoriesMaterialType(eskoMaterialDataMapObj);
        	if(!FormatHelper.hasContent(ValidationForAccessories)){

        	String sizeCode = (String) eskoMaterialDataMapObj.get("hbiSizeCode");
        	String hbiGarmentSize = (String) eskoMaterialDataMapObj.get("hbiGarmentSize");


        	if(FormatHelper.hasContent(partNumber) && partNumber.contains(",")) {
	        	if(FormatHelper.hasContent(hbiGarmentSize) && hbiGarmentSize.contains(",")) {
	        		 sizeCodeValues = getCommaSeparatedToList(hbiGarmentSize);

	        		List<String> eskoPartNumberValues = getCommaSeparatedToList(partNumber);

	             	if(sizeCodeValues.isEmpty()) {
	             		for (int i = 0; i < eskoPartNumberValues.size(); i++) 
	                 	{
	                 		partNumber = eskoPartNumberValues.get(i);
	                 		
	                 		//Calling a function to validate the given 'Part Number', based on the 'Part Number' invoking functions to extract attributes data (mapped attributes) and load to PLM
	                 		materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
	                 	}
	             	}else {
	             		if(sizeCodeValues.size()==1) {
	             			materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
	             		}else {
	             			if(eskoPartNumberValues.size()==sizeCodeValues.size()) {
		             			for (int i = 0; i < eskoPartNumberValues.size(); i++) 
			                 	{
			                 		partNumber = eskoPartNumberValues.get(i);
			                 		eskoMaterialDataMapObj.put("hbiSizeCode", sizeCodeValues.get(i));
			                 		eskoMaterialDataMapObj.put("hbiGarmentSize", sizeCodeValues.get(i));

			                 		materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
			                 	}
		             		}else {
		             			LCSLog.debug("Invalid SizeRange in the given Material Data where ESKO Project ID = " + projectID);
		             			new ESKOLogEntryForFailures().logTransaction(new Date(), partNumber, "Invalid SizeRange in the given Material Data where ESKO Project ID = "+projectID , false);
		             		}
	             		}
	             		
	             	}
	        	} else  {
	        		materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
	        	}
	        }else if(FormatHelper.hasContent(partNumber)) {
	        	materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
	        }
        	}
        	else{
        		//LCSLog.debug("--------------SKIPPED MATERIAL CREATION DUE TO ERROR----------------"+ValidationForAccessories);
        		boolean accessories=true;
        		new ESKOLogEntryForFailures().logTransaction(new Date(), partNumber, ValidationForAccessories,accessories);

        	}
        }else {
        	//LCSLog.debug("in-coming material is not Garment label material");
        	 //If the part number contains multiple materials separated by comma, then invoke the overloaded method of this function to validateAndProcessESKOProjectDataExtractor
            if(FormatHelper.hasContent(partNumber) && partNumber.contains(",")) 
            {
            	List<String> eskoPartNumberValues = getCommaSeparatedToList(partNumber);
            	for (int i = 0; i < eskoPartNumberValues.size(); i++) 
             	{
             		partNumber = eskoPartNumberValues.get(i);
             		
             		//Calling a function to validate the given 'Part Number', based on the 'Part Number' invoking functions to extract attributes data (mapped attributes) and load to PLM
             		materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
             	}
            
            }
            else if(FormatHelper.hasContent(partNumber)) 
            {
            	//Calling a function to validate the given 'Part Number', based on the 'Part Number' invoking functions to extract attributes data (mapped attributes) and load to PLM
            	materialSyncStatus = checkHEIESKOProjectDataExtractor(projectID,partNumber, eskoMaterialDataMapObj);
            }
        }
      //LCSLog.debug("materialSyncStatus:::::::::::::::::::::::::"+materialSyncStatus);
      }
      else{
          //LCSLog.debug("Project Not Approved in ESKO hence Ignoring :::::::::::::::::::::::::"+projectID);
          //LCSLog.debug("Project Not Approved PartNumber is :::::::::::::::::::::::::"+partNumber);


      }
    	return materialSyncStatus;
    }

    private String checkForAccessoriesMaterialType(Map<String, Object> eskoMaterialDataMapObj) {
    	//The size (blank), COO (blank or “not included”, Fiber code (blank). Label format is “brand”.
    	StringBuffer ValidationError=new StringBuffer();
    	String materialName=(String) eskoMaterialDataMapObj.get("flexAttname");
    	if(FormatHelper.hasContent(materialName)&&materialName.contains("AQ")){
    		if(!eskoMaterialDataMapObj.containsKey("flexAtthbiLabelFormat")){
    			ValidationError.append("Label Format is  not selected in ESKO , For Accessories materials its shuld be 'brand' \n");
    		}
    		else if(!"brand".equalsIgnoreCase((String)eskoMaterialDataMapObj.get("flexAtthbiLabelFormat"))){
    			ValidationError.append("Label Format is having different value,  For Accessories materials its shuld be 'brand \n'");
            }
    		
    		if(eskoMaterialDataMapObj.containsKey("hbiGarmentSize") && FormatHelper.hasContent((String) eskoMaterialDataMapObj.get("hbiGarmentSize"))){
    			ValidationError.append(" For Accessories Materials it is expected to have SIZE as blank \n");

    		}
    		if(eskoMaterialDataMapObj.containsKey("flexAtthbiLabelCountry") && FormatHelper.hasContent((String) eskoMaterialDataMapObj.get("flexAtthbiLabelCountry"))&&"Not Included".equals((String) eskoMaterialDataMapObj.get("flexAtthbiLabelCountry"))){
    			ValidationError.append("SIZE is not blank in ESKO , For Accessories Materials it is expected to be BLANK");

    		}
    	}
    	
    	//flexAtthbiLabelCountry
    	//flexAttname starts with AQ
    	
    	
    	return ValidationError.toString();
		// TODO Auto-generated method stub
		
	}

	/**
     * This method is using to validate the project document approval status (vendor approved) for the given project id and invoke internal functions to load material data into FlexPLM
     * @param projectNodes - NodeList
     * @param elementObj - Element
     * @return materialSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public String checkHEIESKOProjectDataExtractor(String projectID, String partNumber, Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException, IOException, SAXException, ParserConfigurationException
    {
       String materialSyncStatus = "";
        //If 'Part Number' starts with 'E' stands, this Part belongs to HEI business unit, which has specific business process in terms of vendor/supplier proofs approvals life-cycle  
        if(partNumber.startsWith("E"))
        {
        	new HEIMaterialDataExtractor().validateAndProcessESKOProjectDataExtractor(partNumber, eskoMaterialDataMapObj);
        }
        else
        {
        	 //Invoking method to get project approval status (vendor approved) for the given project id, which is then used to sync/integrate material data from ESKO to FlexPLM
            boolean projectApprovalStatus = getESKOProjectVendorApprovalStatus(projectID);
            
           // LCSLog.debug("HBIMaterialDataExtractor eskoPartNumberKey = "+eskoMaterialDataMapObj.get(eskoPartNumberKey));
           // LCSLog.debug("HBIMaterialDataExtractor projectApprovalStatus = "+projectApprovalStatus); 
            if(projectApprovalStatus) 
            {
            	//This block of code is specific to 'Insert_Boards', to validate 'Source Production Only?' flag status and other attributes to update the value as per the process.
            	String eskoProjectType = new HEIMaterialDataExtractor().getESKOProjectType(eskoMaterialDataMapObj);
            	if(FormatHelper.hasContent(eskoProjectType) && "Insert_Boards".equals(eskoProjectType))
            	{
            		eskoMaterialDataMapObj = new HEIMaterialDataExtractor().updateMaterialDataMapForInsertBoards(eskoMaterialDataMapObj);
            	}
            	
            	//Calling a function which is using to validate the given 'Part Number' size and split into 'Material Name', 'Color Code', 'Attribute Code' and 'Size Code' attribute value
            	if(validateAndPopulateFlexPLMMaterialSKUAttributeData(partNumber, eskoMaterialDataMapObj)!=null) {
            		eskoMaterialDataMapObj = validateAndPopulateFlexPLMMaterialSKUAttributeData(partNumber, eskoMaterialDataMapObj);
            		partNumber = (String)eskoMaterialDataMapObj.get(eskoPartNumberKey);
                    
                	//Calling a method from HBIMaterialDataLoader which is used to Process Material(perform create/update event) in FlexPLM based on the given material data map from ESKO
                	materialSyncStatus = new HBIMaterialDataLoader().validateAndProcessMaterialDataLoadRequest(eskoMaterialDataMapObj);
                	String materialSupplierSyncStatus = validateAndProcessESKOSupplierDataExtractor(projectID, partNumber, eskoMaterialDataMapObj);
                	//LCSLog.debug("HBIMaterialDataExtractor.validateAndProcessESKOProjectDataExtractor(elementObj, eskoMaterialDataMapObj) :: materialSyncStatus = " + materialSyncStatus + ", and materialSupplierSyncStatus = " + materialSupplierSyncStatus);	
            	}
            	
            }
        }
        
        return materialSyncStatus;
    }

    /**
     * This method is used to validate the project document approval status (vendor approved) for the given project id and invoke internal functions to load material data into FlexPLM
     * @param projectID - String
     * @param partNumber - String
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return materialSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public String validateAndProcessESKOProjectDataExtractor(String projectID, String partNumber, Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException, IOException, SAXException, ParserConfigurationException 
    {
       
        List<String> eskoPartNumberValues = getCommaSeparatedToList(partNumber);
       String materialSupplierSyncStatus = "";
        String materialSyncStatus = "";

        //Iterating on each 'Part Number' from the given eskoPartNumberValues and initializing 'Part Number', 'Size Range' from the existing container and invoking an internal functions
        for (int i = 0; i < eskoPartNumberValues.size(); i++) 
        {
            partNumber = eskoPartNumberValues.get(i);
           // LCSLog.debug("String HBIMaterialDataExtractor starting for partNumber"+partNumber);

            //Calling a function which is using to validate the given 'Part Number' size and split into 'Material Name', 'Color Code', 'Attribute Code' and 'Size Code' attribute value
           
            if(validateAndPopulateFlexPLMMaterialSKUAttributeData(partNumber, eskoMaterialDataMapObj)!=null) {
            	 eskoMaterialDataMapObj = validateAndPopulateFlexPLMMaterialSKUAttributeData(partNumber, eskoMaterialDataMapObj);
            	 //Calling a method which is used to Process Material(perform create/update event) in FlexPLM based on the given material data map from ESKO server
                partNumber = (String)eskoMaterialDataMapObj.get(eskoPartNumberKey);
                //eskoMaterialDataMapObj.put(eskoSizeRangeKey, sizeRange);
               
                materialSyncStatus = new HBIMaterialDataLoader().validateAndProcessMaterialDataLoadRequest(eskoMaterialDataMapObj);
                materialSupplierSyncStatus = validateAndProcessESKOSupplierDataExtractor(projectID, partNumber, eskoMaterialDataMapObj);
               // LCSLog.debug("S HBIMaterialDataExtractor.validateAndProcessESKOProjectDataExtractor(projectNodes, elementObj, eskoMaterialDataMapObj) :: materialSyncStatus = " + materialSyncStatus + ", and materialSupplierSyncStatus = " + materialSupplierSyncStatus);
            }
           
        }

        return materialSyncStatus;
    }
    
    /**
     * This function is using to validate the given 'Part Number' size, if the 'Part Number' is 17 Digits then split into 'Material Name', 'Color Code', 'Attribute Code' and 'Size Code'
     * @param partNumber - String
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return eskoMaterialDataMapObj - Map<String, Object>
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public Map<String, Object> validateAndPopulateFlexPLMMaterialSKUAttributeData(String partNumber, Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException
    {	
    	boolean validMaterial=true;
		String materialDetails= partNumber;//"100006.0000.------.000"; QA PFE001 1234 ------ 123
		//LCSLog.debug("START HBIMaterialDataExtractor.validateAndPopulateFlexPLMMaterialSKUAttributeData materialDetails "+materialDetails);
		if(materialDetails.contains(".")) {
			int count = StringUtils.countMatches(materialDetails, ".");
			//LCSLog.debug("### START HBIMaterialDataExtractor.validateAndPopulateFlexPLMMaterialSKUAttributeData count "+count);
			if(count==3) {
				String[] parts = materialDetails.split("\\.");
    			
    			if(!(parts[0].length()<=6)) { // material code should be less or equal to 6 
    				validMaterial=false; 
    				
    			}
    			
    			if(!(parts[1].length()==3 || parts[1].length()==4)) { //Color Code - 3or4
    				validMaterial=false;  
    				
    			}
    			if(!(parts[2].length()==6)) { //Attribute Code 6
    				validMaterial=false;  
    				

    			}
    			if(!(parts[3].length()==2||parts[3].length()==3)) { //Size code - 2or3
    				validMaterial=false;  
    				

    	        }
    			
    			if(validMaterial) {
    				//LCSLog.debug("validateAndPopulateFlexPLMMaterialSKUAttributeData Loading CorrectData "+eskoMaterialDataMapObj.get(eskoPartNumberKey));
    				//Split the 'Part Number' into 'Material Name'(6 digits set), 'Color Code'(3or4 characters), 'Attribute Code'(6 digits set) and 'Size Code'(which is 2 digits set)
    	    		String materialName = parts[0];
    	    		String colorCode = parts[1];
    	    		String attributeCode =parts[2];
    	    		String sizeCode = parts[3];
    	    		
    	    		//populating/updating 'Material Name', 'Color Code', 'Attribute Code' and 'Size Code' to the given Map<String, Object> and returning map object from the function header
    	    		eskoMaterialDataMapObj.put(eskoPartNumberKey, materialName);
    	    		eskoMaterialDataMapObj.put(HBIProperties.hbiColorCodeKey, colorCode);
    	    		eskoMaterialDataMapObj.put(HBIProperties.hbiAttrCodeKey, attributeCode);
    	    		eskoMaterialDataMapObj.put(HBIProperties.hbiSizeCodeKey, sizeCode);
    	    		
    	    		return eskoMaterialDataMapObj;
    			}
    			
			}
			
		}else{
			
			 if(materialDetails.length()<=6){
				// LCSLog.debug("validateAndPopulateFlexPLMMaterialSKUAttributeData Loading JustMaterialName "+eskoMaterialDataMapObj.get(eskoPartNumberKey));
 				
				 	eskoMaterialDataMapObj.put(eskoPartNumberKey, materialDetails);
		    		eskoMaterialDataMapObj.put(HBIProperties.hbiColorCodeKey, "000");
		    		eskoMaterialDataMapObj.put(HBIProperties.hbiAttrCodeKey, "------");
		    		eskoMaterialDataMapObj.put(HBIProperties.hbiSizeCodeKey, "00");
		    		
		    		return eskoMaterialDataMapObj;
			 }

		}
    	//LCSLog.debug("validateAndPopulateFlexPLMMaterialSKUAttributeData NotLoading badData "+eskoMaterialDataMapObj.get(eskoPartNumberKey));
    	try{
		new ESKOLogEntryForFailures().logTransaction(new Date(), partNumber, "BAD DATA PLEASE CORRECT in ESKO " , false);
    	}
    	catch(Exception e){
        	//LCSLog.debug("error-------- "+e);
    		e.printStackTrace();

    	}

    	// 
    	return null;
    }
    
    /**
     * This function is using to get 'Material Object' from the 'Part Number' and Supplier Names from Project_Id, invoke internal functions to propagate supplier data into FlexPLM
     * @param projectID - String
     * @param partNumber - String
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return materialSupplierSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public String validateAndProcessESKOSupplierDataExtractor(String projectID, String partNumber, Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException, IOException, ParserConfigurationException, SAXException 
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.validateAndProcessESKOSupplierDataExtractor(partNumber, eskoMaterialDataMapObj) ###");
    	LCSMaterial materialObj = null;
    	
    	//Get Material Type/Id Path from the given data map, which is using to get LCSMaterial instance(get Material instance for the given Material Name & Material FlexType Id Path)
    	String materialFlexTypePath = (String) eskoMaterialDataMapObj.get(HBIProperties.materialFlexTypePathKey);
    	
    	if(FormatHelper.hasContent(partNumber)&&partNumber.startsWith("AQ")){
			materialFlexTypePath="Material\\Accessories";

    	}
		if(FormatHelper.hasContent(partNumber) && FormatHelper.hasContent(materialFlexTypePath)) 
		{
			//Get FlexType instance from the given FlexType Path and calling an OOTB API to get Material object/instance based on the given 'Material Name' & 'Material FlexType Path'
			//FlexType materialFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(materialFlexTypePath);
			//materialObj = new LCSMaterialQuery().findMaterialByNameType(partNumber, materialFlexTypeObj);
		
			//Calling a function which is using to get Material object from FlexPLM based on the given parameters (Material Name, Material Type, Attribute Code, Color Code and Size Code)
			materialObj = new HBIMaterialDataLoader().findMaterialByMaterialSKUAndMaterialType(eskoMaterialDataMapObj, partNumber, materialFlexTypePath);
		}
		
		//Calling a function which is using to get Collection<String> of vendor/supplier name for the given Project_ID, which are using to propagate/associate into FlexPLM 
		Collection<String> supplierNamesCollection = getESKOSupplierNamesForProject(projectID);
		//Calling a function which is using to validate the given Material and Supplier data then invoke an internal function to create/establish Material-Supplier link in FlexPLM
		String materialSupplierSyncStatus = validateAndProcessESKOSupplierDataExtractor(materialObj, supplierNamesCollection, eskoMaterialDataMapObj);
		
    	// LCSLog.debug("### END HBIMaterialDataExtractor.validateAndProcessESKOSupplierDataExtractor(partNumber, eskoMaterialDataMapObj) ###");
    	return materialSupplierSyncStatus;
    }
    
    /**
     * This function is using to validate the given Material and Supplier data, based on the validation status invoking internal functions to associate/establish Material-Supplier Link
     * @param materialObj - LCSMaterial
     * @param supplierNamesCollection - Collection<String>
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return materialSupplierSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public String validateAndProcessESKOSupplierDataExtractor(LCSMaterial materialObj, Collection<String> supplierNamesCollection, Map<String, Object> eskoMaterialDataMapObj) throws WTException, WTPropertyVetoException, IOException, ParserConfigurationException, SAXException 
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.validateAndProcessESKOSupplierDataExtractor(materialObj, supplierNamesCollection, eskoMaterialDataMapObj) ###");
    	LCSMaterial placeHolderMaterialObj = (LCSMaterial) VersionHelper.latestIterationOf(LCSMaterialQuery.PLACEHOLDER);
    	String materialSupplierSyncStatus = "";
    	List<String> supplierNames = null;

    	//Validate an existing 'Material Object' and 'Vendor/Supplier Names Collection', based on the validation status invoking an internal functions for data update in FlexPLM
    	if(materialObj != null && materialObj != placeHolderMaterialObj && supplierNamesCollection.size() > 0)
    	{
    		for(String supplierName : supplierNamesCollection)
    		{
    			//Calling a function which is using to validate and format the given Supplier Name, format property entry key to get the mapping FlexPLM Vendor or Supplier Name

    			supplierName = getMappingFlexPLMSupplierName(supplierName);
    					
    			//Validating the Supplier Name (is a single supplier or group of supplier mapping to a single ESKO supplier/vendor) and invoking the function to establish material-supplier link
    			if(FormatHelper.hasContent(supplierName) && supplierName.contains(","))
    			{
    				supplierNames = getCommaSeparatedToList(supplierName);
    				for(String vendorSupplierName : supplierNames)
    				{
    					//Calling a function which is using to validate and propagate/establish the 'Material-Supplier' association in FlexPLM for the given 'Material' and 'Supplier' data
    					materialSupplierSyncStatus = new HBIMaterialDataLoader().validateAndProcessSupplierDataLoadRequest(materialObj, vendorSupplierName, eskoMaterialDataMapObj);
    				}
    			}
    			else if(FormatHelper.hasContent(supplierName))
    			{

    				//Calling a function which is using to validate and propagate/establish the 'Material-Supplier' association in FlexPLM for the given 'Material' and 'Supplier' data
    				materialSupplierSyncStatus = new HBIMaterialDataLoader().validateAndProcessSupplierDataLoadRequest(materialObj, supplierName, eskoMaterialDataMapObj);
    			}
    		}
    	}
    	
    	// LCSLog.debug("### END HBIMaterialDataExtractor.validateAndProcessESKOSupplierDataExtractor(materialObj, supplierNamesCollection, eskoMaterialDataMapObj) ###");
    	return materialSupplierSyncStatus;
    }
    
    /**
     * This function is using to get Documents (of type Proofs) which are approved in last one day, then get Project ID to invoke internal functions for loading material data into FlexPLM
     * @param eskoSavedSearchForDocument - String
     * @return materialSupplierSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws MalformedURLException
     */
    public String validateAndProcessESKODocumentDataExtractor(String eskoSavedSearchForDocument) throws WTException, WTPropertyVetoException, MalformedURLException
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.validateAndProcessESKODocumentDataExtractor(String eskoSavedSearchForDocument) ###");  
    	String eskoProjectsCriteriaURL = new HBIMaterialDataExtractor().getESKOGenericURLFor("GetSavedSearchList.jsp");
    	URL eskoProjectsURLObj = new URL(eskoProjectsCriteriaURL);
    	String materialSupplierSyncStatus = "";  
    	Element elementObj = null;
    	
    	try
    	{
    		//Initializing Document Builder, get XML from the given ESKO URL and parse the XML to Document object, get a List of 'Saved Search' Nodes from an existing document object 
            DocumentBuilderFactory docBuilderFactObj = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilderObj = docBuilderFactObj.newDocumentBuilder();
            Document documentObj = docBuilderObj.parse(eskoProjectsURLObj.openStream());
            NodeList savedSearchNodes = documentObj.getElementsByTagName("saved_search");
            
            //Iterating on each 'Saved Search' Nodes, to get Element from each Node get 'Saved Search' Node specific attributes(Saved_Search_ID, Saved_Search_Name and other details)
            for(int nodeIndex = 0; nodeIndex < savedSearchNodes.getLength(); nodeIndex++) 
            {
                //Get Element from a specific Saved_Search Node and invoke internal functions which are used to format data set using an existing mappings and invoke FlexPLM triggers
                elementObj = (Element) savedSearchNodes.item(nodeIndex);
                materialSupplierSyncStatus = validateAndProcessESKODocumentDataExtractor(eskoSavedSearchForDocument, savedSearchNodes, elementObj);
            }
    	}
    	catch (IOException ioExp) 
        {
    		ioExp.printStackTrace();
            //LCSLog.debug("IOException in HBIMaterialDataExtractor.validateAndProcessESKODocumentDataExtractor() is :: " + ioExp);
        } 
        catch (ParserConfigurationException parserExp) 
        {
        	parserExp.printStackTrace();
            //LCSLog.debug("ParserConfigurationException in HBIMaterialDataExtractor.validateAndProcessESKODocumentDataExtractor() is :: " + parserExp);
        }
        catch (SAXException saxExp) 
        {
        	saxExp.printStackTrace();
            //LCSLog.debug("SAXException in HBIMaterialDataExtractor.validateAndProcessESKODocumentDataExtractor() is :: " + saxExp);
        }
    	
    	// LCSLog.debug("### END HBIMaterialDataExtractor.validateAndProcessESKODocumentDataExtractor(String eskoSavedSearchForDocument) ###");
    	return materialSupplierSyncStatus;
    }
    
    /**
     * This function is using to get all documents from saved_search (Global search to get all documents which are created/modified in last one day) which contains Project ID/Project Data
     * @param eskoSavedSearchForDocument - String
     * @param savedSearchNodes - NodeList
     * @param elementObj - Element
     * @return materialSupplierSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws MalformedURLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public String validateAndProcessESKODocumentDataExtractor(String eskoSavedSearchForDocument, NodeList savedSearchNodes, Element elementObj) throws WTException, WTPropertyVetoException, MalformedURLException, IOException, ParserConfigurationException, SAXException
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.validateAndProcessESKODocumentDataExtractor(eskoSavedSearchForDocument. savedSearchNodes, elementObj) ###");
    	String eskoSavedSearchID = elementObj.getAttribute("id");
    	String eskoDocumentSearchCriteria = "&saved_search_id=".concat(eskoSavedSearchID).concat("&actionVal=executeSavedSearch");
    	String eskoSavedSearchName = elementObj.getElementsByTagName("name").item(0).getTextContent();
    	String materialSupplierSyncStatus = "";
    	Element projecttElementObj = null;
    	
    	//Validate the Saved_Search for documents, get Saved_Search ID to get all the documents which are created/modified last one day which is using to get the corresponding Project ID
    	if(FormatHelper.hasContent(eskoSavedSearchName) && eskoSavedSearchName.equalsIgnoreCase(eskoSavedSearchForDocument) && FormatHelper.hasContent(eskoSavedSearchID))
    	{
        	String eskoDocumentSearchCriteriaURL = new HBIMaterialDataExtractor().getESKOGenericURLFor("DocumentSearch.jsp");
        	eskoDocumentSearchCriteriaURL = eskoDocumentSearchCriteriaURL.concat(eskoDocumentSearchCriteria).trim();
        	URL eskoProjectsURLObj = new URL(eskoDocumentSearchCriteriaURL);
        	
        	//Initializing Document Builder, get XML from the given ESKO URL and parse the XML to Document object, get a List of 'Project' Nodes from an existing document object 
            DocumentBuilderFactory docBuilderFactObj = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilderObj = docBuilderFactObj.newDocumentBuilder();
            Document documentObj = docBuilderObj.parse(eskoProjectsURLObj.openStream());
            NodeList projectNodes = documentObj.getElementsByTagName("project");
            
          //Iterating on each 'Document Search' Nodes, to get Element from each Node get 'Project' Node specific attributes(Project_ID, Document_ID and other details)
            for(int nodeIndex = 0; nodeIndex < projectNodes.getLength(); nodeIndex++) 
            {
                //Get Element from a specific Project Search Node and invoke internal functions which are used to format data set using an existing mappings and invoke FlexPLM triggers
            	projecttElementObj = (Element) projectNodes.item(nodeIndex);
            	materialSupplierSyncStatus = validateAndProcessESKODocumentDataExtractor(projecttElementObj);
            }
    	}
    	
    	// LCSLog.debug("### END HBIMaterialDataExtractor.validateAndProcessESKODocumentDataExtractor(eskoSavedSearchForDocument. savedSearchNodes, elementObj) ###");
    	return materialSupplierSyncStatus;
    }
    
    /**
     * This function is using to get Project_ID from the given Element, formating URL to get Project data based on the given parameter and invoke internal function to trigger data loading
     * @param projecttElementObj - Element
     * @return materialSupplierSyncStatus - String
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws MalformedURLException
     */
    public String validateAndProcessESKODocumentDataExtractor(Element projecttElementObj) throws WTException, WTPropertyVetoException, MalformedURLException
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.validateAndProcessESKODocumentDataExtractor(Element projecttElementObj) ###");
    	String materialSupplierSyncStatus = "";
    	
    	//Initialize all required parameters(parameters needed to get specific ESKO Project) and construct a URL using 'GetProjects.jsp' as target file to retrieve the Project data
    	String projectID = projecttElementObj.getAttribute("id");
    	
		String eskoProjectsCriteria = "&type=6".concat("&projectid=").concat(projectID);
        String eskoProjectsCriteriaURL = new HBIMaterialDataExtractor().getESKOGenericURLFor("GetProjects.jsp");
        eskoProjectsCriteriaURL = eskoProjectsCriteriaURL.concat(eskoProjectsCriteria).trim();
        LCSLog.debug("### HBIMaterialDataExtractor.validateAndProcessMaterialDataExtractRequest() URL to get Specific Project by Project ID = " + eskoProjectsCriteriaURL);
        
        //Calling a function which is using to get Specific ESKO Project (based on the given Project_ID) from the given URL, then get 'All Attributes' data from ESKO Project for sync
        new HBIMaterialDataExtractor().getAllESKOProjectsForURL(eskoProjectsCriteriaURL);
             
    	
    
    	// LCSLog.debug("### END HBIMaterialDataExtractor.validateAndProcessESKODocumentDataExtractor(Element projecttElementObj) ###");
    	return materialSupplierSyncStatus;
    }
    
    /**
     * This function is using to get all documents from the given project, validate the document folder name and document approval status from the vendor and return validation status
     * @param projectID - String
     * @return true/false - boolean
     * @throws WTException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public boolean getESKOProjectVendorApprovalStatus(String projectID) throws WTException, IOException, ParserConfigurationException, SAXException
    {
        // LCSLog.debug("### START HBIMaterialDataExtractor.getESKOProjectVendorApprovalStatus(projectID) ###");
    	Element documentElementObj = null;
        String projectApprovalStatus = "";
        String folderName = "";

        //Initialize all required parameters(parameters needed to get all proofs documents) and construct a URL using 'GetDocumentList.jsp' as target file to retrieve all documents
        String projectDocumentsCriteriaURL = new HBIMaterialDataExtractor().getESKOGenericURLFor("GetDocumentList.jsp");
        projectDocumentsCriteriaURL = projectDocumentsCriteriaURL.concat("&projectid=" + projectID).trim();
        URL projectDocumentsURLObj = new URL(projectDocumentsCriteriaURL);
        LCSLog.debug("### HBIMaterialDataExtractor.getESKOProjectVendorApprovalStatus() URL to get All Documents for a Project :: " + projectDocumentsCriteriaURL);

        //Initializing Document Builder, get XML from the given ESKO URL and parse the XML to Document object, get a List of 'Documents' Nodes from an existing document object 
        DocumentBuilderFactory docBuilderFactObj = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilderObj = docBuilderFactObj.newDocumentBuilder();
        Document documentObj = docBuilderObj.parse(projectDocumentsURLObj.openStream());
        NodeList documentNodes = documentObj.getElementsByTagName("document");
         
        //Iterating on each 'Document' Node, each node contains document name, document folder and document approval status which are using to update/cascade from ESKO-FlexPLM
        for(int nodeIndex = 0; nodeIndex < documentNodes.getLength(); nodeIndex++) 
        {
            documentElementObj = (Element) documentNodes.item(nodeIndex);
            if(documentElementObj.getElementsByTagName("folder_name").getLength() > 0 && documentElementObj.getElementsByTagName("approval_info_string").getLength() > 0) 
            {
                //Get Folder_Name and Approval_Status from the given Project ID, validate Folder_Name and Approval_Status and return Approval Status based on the validation.
                folderName = documentElementObj.getElementsByTagName("folder_name").item(0).getTextContent();
                projectApprovalStatus = documentElementObj.getElementsByTagName("approval_info_string").item(0).getTextContent();
                LCSLog.debug("### HBIMaterialDataExtractor.getESKOProjectVendorApprovalStatus() :: documentElementObj = " + documentElementObj +  ", folderName = " + folderName + " and projectApprovalStatus = " + projectApprovalStatus);
                
                if((folderName.contains("Proofs") || folderName.contains("Proof") || !FormatHelper.hasContent(folderName)) && (projectApprovalStatus.contains("Approved"))) 
                {
                    return true;
                }
            }
        }
       
        // LCSLog.debug("### END HBIMaterialDataExtractor.getESKOProjectVendorApprovalStatus(projectID) ###");
        return false;
    }
    /**
     * This function is using to get getThumbnail from mechanical from the given project
     * @param projectID - String
     * @return true/false - boolean
     * @throws WTException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public String getThumbnail(String projectID) throws WTException, IOException, ParserConfigurationException, SAXException
    {
        // LCSLog.debug("### START HBIMaterialDataExtractor.getESKOProjectVendorApprovalStatus(projectID) ###");
    	Element documentElementObj = null;
        String imageUrl = "";
        String folderName = "";

        //Initialize all required parameters(parameters needed to get all proofs documents) and construct a URL using 'GetDocumentList.jsp' as target file to retrieve all documents
       String projectDocumentsCriteriaURL = new HBIMaterialDataExtractor().getESKOGenericURLFor("ProjectGetThumbnailData.jsp");
        projectDocumentsCriteriaURL = projectDocumentsCriteriaURL.concat("&projectID=" + projectID);
        projectDocumentsCriteriaURL=projectDocumentsCriteriaURL.concat("&folderID=");
        projectDocumentsCriteriaURL=projectDocumentsCriteriaURL.concat("&menu_file="+"projsearchresults").trim();
        
        URL projectDocumentsURLObj = new URL(projectDocumentsCriteriaURL);
        BufferedImage urlImage = ImageIO.read(projectDocumentsURLObj);
        // png file is getting hard coded
        if(urlImage!=null){
        String  value = (new StringBuilder()).append(LoadCommon.LOAD_DIRECTORY).append("Images").append(File.separator).append(projectID+".png").toString();
        File outputfile = new File( value);
        ImageIO.write(urlImage, "png", outputfile);
        String uploadvalue= LoadCommon.uploadFile(value);
         uploadvalue = (new StringBuilder()).append(LoadSku.IMAGE_URL).append("/").append(URLEncoder.encode(uploadvalue, DEFAULT_ENCODING)).toString();
         imageUrl=uploadvalue;
         DeleteFileHelper.deleteFile(value);
        }


       /* File destFile = new File((new StringBuilder()).append(FileLocation.imageLocation).append(File.separator).append(outputfile.getName()).toString());
        
        String  fileName = destFile.getAbsolutePath();
        imageUrl= fileName;
        System.out.println("imageUrl::::::::::::::"+imageUrl);
        
        copyFile(value,fileName);*/

       
        //Initializing Document Builder, get XML from the given ESKO URL and parse the XML to Document object, get a List of 'Documents' Nodes from an existing document object 
       /* DocumentBuilderFactory docBuilderFactObj = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilderObj = docBuilderFactObj.newDocumentBuilder();
        Document documentObj = docBuilderObj.parse(projectDocumentsURLObj.openStream());
        NodeList documentNodes = documentObj.getElementsByTagName("document");
         
        //Iterating on each 'Document' Node, each node contains document name, document folder and document approval status which are using to update/cascade from ESKO-FlexPLM
        for(int nodeIndex = 0; nodeIndex < documentNodes.getLength(); nodeIndex++) 
        {
            documentElementObj = (Element) documentNodes.item(nodeIndex);
            if(documentElementObj.getElementsByTagName("Thumbnail").getLength()>0)            
            {
            	imageUrl = documentElementObj.getElementsByTagName("Thumbnail").item(0).getTextContent();
                System.out.println("imageUrl:::::::::::::"+imageUrl);
               
            } 
        }*/
       
        // LCSLog.debug("### END HBIMaterialDataExtractor.getESKOProjectVendorApprovalStatus(projectID) ###");
        return imageUrl;
    }

    /**
     * This method is used to format/initialize getAttributes URL from the given Product_Node and Element, format Attributes data map and invoke internal function for FlexPLM event
     * @param projectNodes - NodeList
     * @param elementObj - Element
     * @return eskoMaterialDataMapObj - Map<String, Object>
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Map<String, Object> getAllESKOAttributesFromProject(NodeList projectNodes, String projectID) throws IOException, ParserConfigurationException, SAXException 
    {
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
                if("Division".equals(attributeName)||("Brand Usage").equals(attributeName)){
                eskoMaterialDataMapObj.put(attributeName,attributeValue);
                }
                System.out.println("attributeName>>>>>>>>>>>>"+attributeName+"_ attributeValue_"+attributeValue);
                eskoMaterialDataMapObj = getESKOAttributesDataMapForElement(attElementObj, attributeName, attributeValue, eskoMaterialDataMapObj);
                System.out.println("eskoMaterialDataMapObj after>>>>>>>>>>>>"+eskoMaterialDataMapObj);
            }
        }
        return eskoMaterialDataMapObj;
    }

    /**
     * This function is using to validate and format the given 'Attribute_Name', mapping the ESKO Attribute with the FlexPLM Attribute and preparing data map(Key-Value) for returning
     * @param attElementObj - Element
     * @param attributeName - String
     * @param attributeValue - String
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return eskoMaterialDataMapObj - Map<String, Object>
     */
    public Map<String, Object> getESKOAttributesDataMapForElement(Element attElementObj, String attributeName, String attributeValue, Map<String, Object> eskoMaterialDataMapObj) 
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.getESKOAttributesDataMapForElement(attElementObj, attributeName, attributeValue, eskoMaterialDataMapObj) ###");
        LCSLog.debug("### HBIMaterialDataExtractor.getESKOAttributesDataMapForElement() before mapping ::  attributeName = " + attributeName + " attributeValue = " + attributeValue);
        String countryToFindCode=null;
        //Validate the given attribute type, if the attribute type is of 'RichText' then validate the attribute-value and replace any special characters (like <p>... </p>)
        if(attElementObj.getElementsByTagName("at").getLength() > 0 && "RichText".equalsIgnoreCase(attElementObj.getElementsByTagName("at").item(0).getTextContent())) 
        {
        	//attributeValue = attributeValue.replaceAll("<p>", "").replaceAll("</p>", "").trim();
        	attributeValue = attributeValue.replaceAll("\\<[^>]*>","");
        }

        //Replacing special characters(like #, " ") from Attribute_Name, which is using to form property entry key (unique parameter using to get property entry value)
        if(attributeName.contains("#") || attributeName.contains("?") || attributeName.contains(" ")) 
        {
        	attributeName = attributeName.replaceAll("#", "").replaceAll("\\?", "").replaceAll(" ", "").trim();
        }
        
        //Validating the Web-Center attributeValue and trimming the data (this step is to avoid the special characters before and after the attribute-value specially with Material Name 
        if(FormatHelper.hasContent(attributeValue))
        {
        	attributeValue = attributeValue.replaceAll("&nbsp;", "");
        	attributeValue = attributeValue.trim();
        }

        //Get ESKO SingleList Attributes from properties file, validate the given attribute with the single list attributes container and format the given attribute value as mapped
        List<String> eskoAttributesList = getCommaSeparatedToList(eskoSingleListAttributesKey);
        if(eskoAttributesList.size() > 0 && eskoAttributesList.contains(attributeName)) 
        {

            attributeValue = formatAndReturnMappableDataString(attributeValue);
            
            /*if("COOprintedonart".equals(attributeName)){
            	countryToFindCode=attributeValue;
            	if(FormatHelper.hasContent(countryToFindCode)){
            		LCSCountry co=getCountryByCriteria(countryToFindCode.toUpperCase(), "Country");
               	 	eskoMaterialDataMapObj.put("hbiCountryCode", co);

               	 	}

            }*/
            String eskoSingleListAttributeValueKey = eskoFlexPLMAttMappingPropertyEntryKey.concat(attributeName).concat(".").concat(attributeValue);
            LCSLog.debug("### HBIMaterialDataExtractor.eskoSingleListAttributeValueKey :: "+eskoSingleListAttributeValueKey);
            attributeValue = LCSProperties.get(eskoSingleListAttributeValueKey);
            
        }
        LCSLog.debug("### HBIMaterialDataExtractor.getESKOAttributesDataMapForElement() before attributeValue :: "+attributeValue);
        //Calling a function which is using to validate the given attribute (is an type mapping attribute), based on the validation updating the given data map to populate type mapping
        //Material type name is there in property file hard coded and is chosen from the ESKO url Attributes name by formatting and fitting to property
        if("LabelType".equalsIgnoreCase(attributeName)) {
       	 	eskoMaterialDataMapObj.put(HBIProperties.materialFlexTypePathKey, "Material\\Garment Label");
       	 	//This attribute not there in WebCenter but it is required in PLM and used in HBIWorkflowPlugin code
       	 	eskoMaterialDataMapObj.put("flexAtthbiBuyerGroup", "hbiLabelRCM");
       	 	eskoMaterialDataMapObj.put("flexAtthbiMajorCategory", "accessories");
       	 	eskoMaterialDataMapObj.put("flexAtthbiMinorCategory", "labels");
       	 	
       	 
        }else {
    	   eskoMaterialDataMapObj = getESKOAttributesDataMapForFlexTypes(attributeName, attributeValue, eskoMaterialDataMapObj);
        }
        
       
        

        //Format Property Entry Key based on the existing 'Attribute_Name', get FlexTypeAttribute_Key from Properties entry, validate and add the data(Key-Value) to the Map
        LCSLog.debug("### HBIMaterialDataExtractor.getESKOAttributesDataMapForElement() before formatting :: "+attributeName);
        String attName=attributeName;
        
        attributeName = eskoFlexPLMAttMappingPropertyEntryKey.concat(attributeName);
        LCSLog.debug("### HBIMaterialDataExtractor.getESKOAttributesDataMapForElement()key:: "+attributeName);
        attributeName = LCSProperties.get(attributeName);
        LCSLog.debug("### HBIMaterialDataExtractor.getESKOAttributesDataMapForElement() after formatting :: "+attributeName);
        if (FormatHelper.hasContent(attributeName)) 
        {	
        	if("flexAtthbiFiberCodeNew".equalsIgnoreCase(attributeName)) {
        		 //For Garment Label - Business Object -  to get attribute value BO
        		LCSLifecycleManaged fiberCodeBO = getLifecycleManagedByCriteria("hbiFiberCode", attributeValue,businessObjectFiberPath);
        		 eskoMaterialDataMapObj.put(attributeName, fiberCodeBO);
        	}else {
        		eskoMaterialDataMapObj.put(attributeName, attributeValue);
        	}
            LCSLog.debug("### HBIMaterialDataExtractor.getESKOAttributesDataMapForElement() after mapping ::  attributeName = " + attributeName + " attributeValue = " + attributeValue);
        }
        
        
        
        return eskoMaterialDataMapObj;
    }
    
    /**
	 * @param attributeKey
	 * @param attributeValue
	 * @param businessObjTypePath
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static LCSCountry getCountryByCriteria(String attributeValue,
			String countryObjTypePath)  {
		
		LCSCountry co = null;
		try {
		FlexType countryObjFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(countryObjTypePath);
		
		String criteriaAttDBColumn = countryObjFlexTypeObj.getAttribute("name").getColumnDescriptorName();

		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendSelectColumn(
				new QueryColumn("LCSCountry", "branchiditerationinfo"));
		statement.appendFromTable(LCSCountry.class);
		statement.appendCriteria(new Criteria(new QueryColumn(LCSCountry.class, "flexTypeIdPath"),
				countryObjFlexTypeObj.getTypeIdPath(), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSCountry.class, criteriaAttDBColumn),
				attributeValue.trim(), Criteria.EQUALS));
           SearchResults results = LCSQuery.runDirectQuery(statement);
	
		if (results != null && results.getResultsFound() > 0) {
			
			FlexObject flexObj = (FlexObject) results.getResults().firstElement();
			co = (LCSCountry) LCSQuery.findObjectById(			
					"VR:com.lcs.wc.country.LCSCountry:" + flexObj.getString("LCSCountry.BRANCHIDITERATIONINFO"));
		}
		} catch (WTException e) {
			
			e.printStackTrace();
		}
		return co;
	}
    
	 /**
		 * @param attributeKey
		 * @param attributeValue
		 * @param businessObjTypePath
		 * @return
		 * @throws WTException
		 * @throws WTPropertyVetoException
		 */
		public static LCSLifecycleManaged getLifecycleManagedByCriteria(String attributeKey, String attributeValue,
				String businessObjTypePath)  {
			
			LCSLifecycleManaged bo = null;
			try {
			FlexType businessObjFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(businessObjTypePath);
			
			String criteriaAttDBColumn = businessObjFlexTypeObj.getAttribute(attributeKey).getColumnDescriptorName();

			PreparedQueryStatement statement = new PreparedQueryStatement();
			statement.appendSelectColumn(
					new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
			statement.appendFromTable(LCSLifecycleManaged.class);
			statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeIdPath"),
					businessObjFlexTypeObj.getTypeIdPath(), Criteria.EQUALS));
			statement.appendAndIfNeeded();
			statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, criteriaAttDBColumn),
					attributeValue.trim(), Criteria.EQUALS));
			
			SearchResults results = LCSQuery.runDirectQuery(statement);
		
			if (results != null && results.getResultsFound() > 0) {
				
				FlexObject flexObj = (FlexObject) results.getResults().firstElement();
				bo = (LCSLifecycleManaged) LCSQuery.findObjectById(
						"OR:com.lcs.wc.foundation.LCSLifecycleManaged:" + flexObj.getString("LCSLifecycleManaged.IDA2A2"));
			}
			} catch (WTException e) {
				
				e.printStackTrace();
			}
			return bo;
		}

    /**
     * This method is used to validate the given attribute (is an type mapping attribute), based on the validation updating the given data map to populate ESKO-FlexPLM type mappings
     * @param attributeName - String
     * @param attributeValue - String
     * @param eskoMaterialDataMapObj - Map<String, Object>
     * @return eskoMaterialDataMapObj - Map<String, Object>
     */
    public Map<String, Object> getESKOAttributesDataMapForFlexTypes(String attributeName, String attributeValue, Map<String, Object> eskoMaterialDataMapObj) 
    {
    	
        //Get ESKO FlexType Attributes from properties file, validate the given attributes with the ESKO FlexType attributes list and format the given attribute value as mapped
        List<String> eskoFlexTypeAttributesList = getCommaSeparatedToList(eskoFlexTypeAttributesKey);
        if (eskoFlexTypeAttributesList.size() > 0 && eskoFlexTypeAttributesList.contains(attributeName)) 
        {
            attributeValue = formatAndReturnMappableDataString(attributeValue);

            //Format PropertyEntryKey which is using to get FlexPLM FlexTypes Mapping for the given ESKO Attribute Name and Attribute Value, update the Types mapping to the data map 
            String eskoFlexPLMFlexTypesMappingKey = eskoFlexPLMAttMappingPropertyEntryKey.concat(attributeName).concat(".").concat(attributeValue);

            attributeValue = LCSProperties.get(eskoFlexPLMFlexTypesMappingKey);
            if (FormatHelper.hasContent(attributeValue)) {
                eskoMaterialDataMapObj.put(HBIProperties.materialFlexTypePathKey, attributeValue);
                LCSLog.debug("### HBIMaterialDataExtractor MaterialFlexTypePath = " + attributeValue);
            }
            
            //This block of code is using to map 'Major Category' and 'Minor Category' Attribute data based on the given ESKO type values and type path of the material object in PLM
            eskoFlexPLMFlexTypesMappingKey = eskoFlexPLMFlexTypesMappingKey.concat(".MajorMinorCategory");
            String majorMinorCategory = LCSProperties.get(eskoFlexPLMFlexTypesMappingKey);
            if(FormatHelper.hasContent(majorMinorCategory))
            {
            	eskoMaterialDataMapObj.put("MajorMinorCategory", majorMinorCategory);
            }
        }

        return eskoMaterialDataMapObj;
    }

    /**
     * This method is using to replace special characters (like /, \, " ") from the given attribute value which is used to form property entry key (unique parameter to get value)
     * @param attributeValue - String
     * @return attributeValue - String
     */
    public String formatAndReturnMappableDataString(String attributeValue) 
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.formatAndReturnMappableDataString(attributeValue) ###");
    	
        //Replacing special characters(like /) from the given Attribute_Value, which is using to form property entry key (unique parameter using to get property entry value)
        if (attributeValue.contains("/")) 
        {
            attributeValue = attributeValue.replaceAll("/", "").trim();
        }

        //Replacing special characters(like \) from the given Attribute_Value, which is using to form property entry key (unique parameter using to get property entry value)
        if (attributeValue.contains("\\")) 
        {
            attributeValue = attributeValue.replaceAll("\\", "").trim();
        }

        //Replacing special characters(like " ") from the given Attribute_Value, which is using to form property entry key (unique parameter using to get property entry value)
        if (attributeValue.contains(" ")) 
        {
            attributeValue = attributeValue.replaceAll(" ", "").trim();
        }

        // LCSLog.debug("### END HBIMaterialDataExtractor.formatAndReturnMappableDataString(attributeValue) ###");
        return attributeValue;
    }

    /**
     * This method is used to get ArrayList (contains set of Attributes/objects as string) from the given property entry (a unique key which is using to get value from properties)
     * @param propertyEntryValue - String
     * @return eskoAttributesList - List<String>
     */
    public static List<String> getCommaSeparatedToList(String propertyEntryValue) 
    {
        List<String> eskoAttributesList = new ArrayList<String>();
        if (FormatHelper.hasContent(propertyEntryValue) && propertyEntryValue.contains(",")) 
        {
            //Construct a ArrayList from an ESKO Attributes which are registered in properties file, using this ArrayList we can process validate Hane's business logic for integration
            StringTokenizer strTokenEskoAttributes = new StringTokenizer(propertyEntryValue, ",");
            while (strTokenEskoAttributes.hasMoreTokens()) 
            {
                String strStagingObjType = strTokenEskoAttributes.nextToken().trim();
                eskoAttributesList.add(strStagingObjType);
            }
        }
        else
        {
        	eskoAttributesList.add(propertyEntryValue);
        }
        return eskoAttributesList;
    }
    
    /**
     * This function is using to get all documents from the given project, validate the document folder name and document approval status from the vendor and invoke internal functions
     * @param projectID - String
     * @return supplierNamesCollection - Collection<String>
     * @throws WTException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Collection<String> getESKOSupplierNamesForProject(String projectID) throws WTException, IOException, ParserConfigurationException, SAXException 
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.getESKOSupplierNamesForProject(projectID) ###");
    	Collection<String> supplierNamesCollection = new ArrayList<String>();
    	Element documentElementObj = null;
        String projectApprovalStatus = "";
        String folderName = "";
        
    	//Initialize all required parameters(parameters needed to get all proofs documents) and construct a URL using 'GetDocumentList.jsp' as target file to retrieve all documents
        String projectDocumentsCriteriaURL = new HBIMaterialDataExtractor().getESKOGenericURLFor("GetDocumentList.jsp");
        projectDocumentsCriteriaURL = projectDocumentsCriteriaURL.concat("&projectid=" + projectID).trim();
        URL projectDocumentsURLObj = new URL(projectDocumentsCriteriaURL);
        LCSLog.debug("### HBIMaterialDataExtractor.getESKOSupplierNamesForProject() URL to get All Documents for a Project :: " + projectDocumentsCriteriaURL);

        //Initializing Document Builder, get XML from the given ESKO URL and parse the XML to Document object, get a List of 'Documents' Nodes from an existing document object 
        DocumentBuilderFactory docBuilderFactObj = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilderObj = docBuilderFactObj.newDocumentBuilder();
        Document documentObj = docBuilderObj.parse(projectDocumentsURLObj.openStream());
        NodeList documentNodes = documentObj.getElementsByTagName("document");
         
        //Iterating on each 'Document' Node, each node contains document name, document folder and document approval status which are using to update/cascade from ESKO-FlexPLM
        for(int nodeIndex = 0; nodeIndex < documentNodes.getLength(); nodeIndex++) 
        {
            documentElementObj = (Element) documentNodes.item(nodeIndex);
            if(documentElementObj.getElementsByTagName("folder_name").getLength() > 0 && documentElementObj.getElementsByTagName("approval_info_string").getLength() > 0) 
            {
            	//Get Folder_Name and Approval_Status from the given Project ID, validate Folder_Name and Approval_Status and return Approval Status based on the validation.
                folderName = documentElementObj.getElementsByTagName("folder_name").item(0).getTextContent();
                projectApprovalStatus = documentElementObj.getElementsByTagName("approval_info_string").item(0).getTextContent();
                LCSLog.debug("### HBIMaterialDataExtractor.getESKOSupplierNamesForProject() :: documentElementObj = " + documentElementObj +  ", folderName = " + folderName + " and projectApprovalStatus = " + projectApprovalStatus);
                
                if((folderName.contains("Proofs") || folderName.contains("Proof") || !FormatHelper.hasContent(folderName)) && (projectApprovalStatus.contains("Approved"))) 
                {
                	supplierNamesCollection = getESKOSupplierNamesForProject(documentElementObj, supplierNamesCollection);
                }
            }
        }
        
      LCSLog.debug("### END HBIMaterialDataExtractor.getESKOSupplierNamesForProject(projectID) ###"+supplierNamesCollection);
    	return supplierNamesCollection;
    }
    
    /**
     * This function is using to get vendor/supplier name as a collection for the given element (which is derived from proofs 'Documents' node) using to propagate/update in FlexPLM
     * @param documentElementObj - Element
     * @param supplierNamesCollection - Collection<String>
     * @return supplierNamesCollection - Collection<String>
     * @throws WTException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Collection<String> getESKOSupplierNamesForProject(Element documentElementObj, Collection<String> supplierNamesCollection) throws WTException, IOException, ParserConfigurationException, SAXException 
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.getESKOSupplierNamesForProject(projectID, documentElementObj) ###");
    	
    	//Get Author (uploaded by/the user who uploaded the proofs document) from the given document object and initialize 'Author_ID' from the given Document Object to get UserInfo
        if(documentElementObj.getElementsByTagName("author").getLength() > 0) 
        {
        	Element authorElementObj = (Element) documentElementObj.getElementsByTagName("author").item(0);
            String authorId = authorElementObj.getAttribute("id");
            
            //Initialize all required parameters(parameters needed to get all vendor/supplier) and construct a URL using 'GetUserInfo.jsp' as target file to retrieve related suppliers
            String documentSupplierCriteriaURL = new HBIMaterialDataExtractor().getESKOGenericURLFor("GetUserInfo.jsp");
            documentSupplierCriteriaURL = documentSupplierCriteriaURL.concat("&askeduserid=" + authorId).trim();
            URL documentSupplierURLObj = new URL(documentSupplierCriteriaURL);
            LCSLog.debug("### HBIMaterialDataExtractor.getESKOSupplierNamesForProject() URL to get All UserInfo for a Project Document :: " + documentSupplierCriteriaURL);
            
            //Initializing Document Builder, get XML from the given ESKO URL and parse the XML to Document object, get a List of 'Documents' Nodes from an existing document object 
            DocumentBuilderFactory docBuilderFactObj = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilderObj = docBuilderFactObj.newDocumentBuilder();
            Document documentObj = docBuilderObj.parse(documentSupplierURLObj.openStream());
            NodeList documentUserNodes = documentObj.getElementsByTagName("user");
            
            //Get Element from 'User' Node (which is a sub-set of document), get 'UserName' from an existing User Node Element and add to the Collection<String> using to return
            Element userElementObj = (Element) documentUserNodes.item(0);
            if(userElementObj.getElementsByTagName("username").getLength() > 0) 
            { 
				String supplierName = userElementObj.getElementsByTagName("username").item(0).getTextContent();
				supplierNamesCollection.add(supplierName);
				LCSLog.debug("### HBIMaterialDataExtractor.getESKOSupplierNamesForProject(projectID, documentElementObj) :: " + supplierName);
            }
        }
        
    	LCSLog.debug("### END HBIMaterialDataExtractor.getESKOSupplierNamesForProject(projectID, documentElementObj) ###"+supplierNamesCollection);
    	return supplierNamesCollection;
    }
    
    /**
     * This method is using to replace special characters (like -, " ") from the given supplier name which is used to form property entry key (unique parameter to get corresponding value)
     * @param supplierName - String
     * @return supplierName - String
     * @throws WTException
     */
    public String getMappingFlexPLMSupplierName(String supplierName) throws WTException 
    {
    	// LCSLog.debug("### START HBIMaterialDataExtractor.getMappingFlexPLMSupplierName(supplierName) ###");
    	
        //Replacing special character(like -) from the given supplier name, which is using to form property entry key (unique parameter using to get corresponding property entry value)
        if(FormatHelper.hasContent(supplierName) && supplierName.contains("-")) 
        {
        	supplierName = supplierName.replaceAll("-", "").trim();
        }
        
        //Replacing special character(like &) from the given supplier name, which is using to form property entry key (unique parameter using to get corresponding property entry value)
        if(FormatHelper.hasContent(supplierName) && supplierName.contains("&")) 
        {
        	supplierName = supplierName.replaceAll("&", "").trim();
        }
        
        //Replacing special character(like " ") from the given supplier name, which is using to form property entry key (unique parameter using to get corresponding property entry value)
        if(FormatHelper.hasContent(supplierName) && supplierName.contains(" ")) 
        {
        	supplierName = supplierName.replaceAll(" ", "").trim();
        }
        
        //Forming Property Entry Key (which is the combination of Pre-defined String and Supplier Name without any special characters) to get the mapping FlexPLM Vendor or Supplier Name
        String eskoFlexPLMSupplierMappingKey = eskoFlexPLMSupplierMappingPropertyEntryKey.concat(supplierName);
        supplierName = LCSProperties.get(eskoFlexPLMSupplierMappingKey);

        // LCSLog.debug("### START HBIMaterialDataExtractor.getMappingFlexPLMSupplierName(supplierName) ###");
        return supplierName;
    }
    
    static void copyFile(String input, String output)
            throws IOException
        {
            FileInputStream fis;
            FileOutputStream fos;
            fis = null;
            fos = null;
            fis = new FileInputStream(input);
            fos = new FileOutputStream(output);
            byte buf[] = new byte[8192];
            for(int read = 0; (read = fis.read(buf)) != -1;)
            {
                fos.write(buf, 0, read);
            }

            if(fos != null)
            {
                fos.close();
            }
            if(fis != null)
            {
                fis.close();
            }
           
            if(fos != null)
            {
                fos.close();
            }
            if(fis != null)
            {
                fis.close();
            }
        }
}


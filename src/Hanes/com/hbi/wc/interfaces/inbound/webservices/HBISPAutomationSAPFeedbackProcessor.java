package com.hbi.wc.interfaces.inbound.webservices;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.jws.WebService;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOACollectionClientModel;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.skusize.SKUSize;
import com.lcs.wc.skusize.SKUSizeQuery;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MultiObjectHelper;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.wf.WFHelper;

import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;


/**
 * HBISPAutomationSAPFeedbackProcessor.java
 * 
 * This file is to receive the feedback from SAP/IIB via web services. 
 * @author UST
 */
//public class HBISPAutomationSAPFeedbackProcessor //implements RemoteAccess
@WebService(endpointInterface="com.hbi.wc.interfaces.inbound.webservices.HBISPAutomationSAPFeedbackService",
portName="hbiSPAutomationSAPFeedbackServicePort",
serviceName="hbiSPAutomationSAPFeedbackService")
public class HBISPAutomationSAPFeedbackProcessor implements HBISPAutomationSAPFeedbackService
{
	public static final String SKU_ALREADY_SENT = LCSProperties.get("com.hbi.wc.interfaces.outbound.sku.HbiAlreadySent",
			"HbiAlreadySent");

	public static final String SKU_ALREADY_SENT_YES = LCSProperties.get("com.hbi.wc.interfaces.outbound.sku.hbiYes",
			"hbiYes");

		private static String feedbackMessage="";
			
	public static String getFeedbackMessage() {
		return feedbackMessage;
	}



	public String setFeedbackMessage(String feedbackMessage) throws Exception, WebServiceException
	{
		String successStatus = "";
		try{
		this.feedbackMessage = feedbackMessage;
		if(FormatHelper.hasContent(HBISPAutomationSAPFeedbackProcessor.getFeedbackMessage())){
		System.out.println(">>>>>>>>>>>>>>>>>>Feedback Message Recieved>>>>>>>>>>>>>>>>>>>>> "+feedbackMessage);
		successStatus="Message Successfully received in PLM from IIB";
		 processSellingProductAPSFeedback();		 
		}else{
		successStatus="No message contents received in PLM from IIB";	
		}
		
		}
		catch(WebServiceException wbse){
			successStatus=wbse.getLocalizedMessage();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return successStatus;
	}

	

	/**
	 * This function is invoking from the default executable function of the class to initiate the process of End Of End Color  object.
	* @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void processSellingProductAPSFeedback() throws Exception
	{
				String SOAPMessage = HBISPAutomationSAPFeedbackProcessor.getFeedbackMessage();
				LCSLog.debug(">>>>>>>>>>>>SOAPMessage>>>>>>>>>>> "+SOAPMessage);
				System.out.println(">>>>>>>>>>>>SOAPMessage>>>>>>>>>>> "+SOAPMessage);
				if(FormatHelper.hasContent(SOAPMessage)){
				new HBISPAutomationSAPFeedbackProcessor().loadSellingProductDataToAPS(SOAPMessage);	
				new HBISPAutomationSAPFeedbackProcessor().loadSellingProductPutUpCodeDataToAPS(SOAPMessage);
				new HBISPAutomationSAPFeedbackProcessor().loadSellingProductPlantExtCodeDataToAPS(SOAPMessage);
				}
    }
	
	
	
	public Map<String, String> getSellingDataFromSOAPMessage(String soapMessage) throws SOAPException,IOException
	{
		Map<String, String> materialAttributesDataMap = new HashMap<String, String>();
		Node materialNode = null;
		String flexTypeAttributeKey = "";
		String flexTypeAttributeValue = "";
		
		MessageFactory messageFactory = MessageFactory.newInstance();
	    SOAPMessage soapMessageObj = messageFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soapMessage.getBytes(Charset.forName("UTF-8"))));
	    SOAPBody body = soapMessageObj.getSOAPBody();
	
	    NodeList materialAttributesList = body.getChildNodes();
	    
	    //Iterating through each node from a params node, initializing attribute key and attribute value, preparing a dataMap contains attribute key and value
	    for(int nodeIndex = 0; nodeIndex < materialAttributesList.getLength(); nodeIndex++)
        {
	    	materialNode = materialAttributesList.item(nodeIndex);
	    	flexTypeAttributeKey = materialNode.getNodeName();
	    	flexTypeAttributeValue = ""+materialNode.getTextContent();
	    	materialAttributesDataMap.put(flexTypeAttributeKey, flexTypeAttributeValue);
        }
	    
	    System.out.println(">>>>>>>>>>>>>>>>>>>>>> SOAP map >>>>>>>>>>>>>>>>>>>>>>>"+materialAttributesDataMap);
		return materialAttributesDataMap;
	}
	
	public void loadSellingProductDataToAPS(String soapMessage) throws Exception
	{
		//This function is using to convert SOAP message into a Map containing Key and Value to prepare a dataMap contains attribute key and value. 
		Map<String, String> SellingProductAttributesDataMap = getSellingDataFromSOAPMessage(soapMessage);
		
		System.out.println(">>>>>>>>>>>>>>>>>>> SellingProductAttributesDataMap >>>>>>>>>>>>"+SellingProductAttributesDataMap);
		String transactionID = SellingProductAttributesDataMap.get("hbiTransactionID");
		System.out.println(" <<<<<<   transactionID >>>>>>" +transactionID);
		
		String[] transactionIDArray = transactionID.split("-");
		String productBranchID = transactionIDArray[0];
		String seasonBranchID = transactionIDArray[1];
		String MaterialGrid = SellingProductAttributesDataMap.get("hbimaterialGrid");		
		String status = SellingProductAttributesDataMap.get("hbiSAPFeedbackStatus");		
		String Desc = SellingProductAttributesDataMap.get("hbiDesc");
		setSAPFeedbackOnSellingProduct(productBranchID,seasonBranchID,MaterialGrid,status,Desc, soapMessage);
		
	}
	
	public void setSAPFeedbackOnSellingProduct(String productBranchID,String seasonBranchID,String MaterialGrid,String status,String Desc, String soapMessage) throws Exception
	{
		
		LCSProduct producObj = (LCSProduct)LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+productBranchID);
		
		
		LCSSeason seasonObj = (LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+seasonBranchID);
		
		LCSSeasonProductLink  seasonProductLinkObj = LCSSeasonQuery.findSeasonProductLink(producObj,seasonObj);		
		LCSProduct prodarev=(LCSProduct)VersionHelper.getVersion(producObj, "A");
		
		LCSLog.debug("Printing Feedback File "+soapMessage);
		System.out.println("Printing Feedback File "+soapMessage);
		
		printSoapMessageFile(soapMessage, prodarev);
		
		
		//Double count=(Double)prodarev.getValue("hbiTransferCount");
		Long count=(Long)prodarev.getValue("hbiTransferCount");
		

		if("1".equalsIgnoreCase(status))
		{
		
		LCSLog.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Message from SAP>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Message from SAP>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		Desc=getStatusvalue(prodarev,count);
		count++;
		
		prodarev.setValue("hbiFeedbackStatus",status);
		 String existingMasterGrid=(String)prodarev.getValue("hbiMasterGrid");
				if(!FormatHelper.hasContent(existingMasterGrid)){
				prodarev.setValue("hbiMasterGrid",MaterialGrid);
				}
		    prodarev.setValue("hbiFeedback",Desc);
		    prodarev.setValue("hbiTransferCount",count);
			if("hbiESUInProgress".equals( (String)prodarev.getValue("hbiSellingProductStatus"))){
		   /* prodarev.setValue("hbiSellingProductStatus","hbiESU");
		    prodarev.setValue("hbiProductEditLockStatus", "hbiLock");*/
	    	//LCSProductLogic.persist(prodarev,true);
		   // new LCSProductLogic().saveProduct(prodarev);
		    
		    if(!VersionHelper.isCheckedOut(prodarev)) {
		    	prodarev = VersionHelper.checkout(prodarev);
		    	prodarev.setValue("hbiSellingProductStatus","hbiESU");
		    	 prodarev.setValue("hbiFeedback"," ");
			    prodarev.setValue("hbiProductEditLockStatus", "hbiLock");
			    System.out.println(">>>>>>before check in if block>>>>>>>");
			    VersionHelper.checkin(prodarev);
		    	
		    }
		    

			State hbiLCState = State.toState("INWORK");
			LCSProduct product = SeasonProductLocator.getProductSeasonRev(seasonProductLinkObj);
			product = (LCSProduct)VersionHelper.latestIterationOf(product.getMaster());
			WFHelper.service.terminateAllRelatedProcesses(product);
			LifeCycleHelper.service.setLifeCycleState(product, hbiLCState); 
			}else{
			State hbiLCState = State.toState("INWORK");
			LCSProduct product = SeasonProductLocator.getProductSeasonRev(seasonProductLinkObj);
			product = (LCSProduct)VersionHelper.latestIterationOf(product.getMaster());
			WFHelper.service.terminateAllRelatedProcesses(product);
			prodarev.setValue("hbiSellingProductStatus","hbiOTCSynched");
			prodarev.setValue("hbiOTCSynchedAlready","hbiYes");
		    prodarev.setValue("hbiProductEditLockStatus", "hbiLock");
		    
		    /*if(!VersionHelper.isCheckedOut(prodarev)) {
		    	prodarev = VersionHelper.checkout(prodarev);
		    	prodarev.setValue("hbiSellingProductStatus","hbiOTCSynched");
				prodarev.setValue("hbiOTCSynchedAlready","hbiYes");
			    prodarev.setValue("hbiProductEditLockStatus", "hbiLock");
			    System.out.println(">>>>>>before check in>>>>>>>");
			    VersionHelper.checkin(prodarev);
		    	
		    }*/

			//LCSProductLogic.persist(prodarev,true);
			//new LCSProductLogic().saveProduct(prodarev);
		    new com.lcs.wc.product.LCSProductLogic().saveProduct(prodarev);

			//if(prodarev.getValue("hbiOmniSelection")!=null && "true".equalsIgnoreCase((String) prodarev.getValue("hbiOmniSelection"))) {
			//LifeCycleHelper.service.setLifeCycleState(product, hbiLCState);   
		//}
		
					LifeCycleHelper.service.setLifeCycleState(product, hbiLCState);   

			
			}
			
			Collection skuSizes = new ArrayList();
			LCSProduct product = SeasonProductLocator.getProductARev(seasonProductLinkObj);
			Collection viewableSKUs = new ProductHeaderQuery().findSKUs(product, null, seasonObj, true, false);
			System.out.println("viewableSKUs::::::::::::"+viewableSKUs.size());
			FlexType skuSizeType = product.getFlexType().getReferencedFlexType("SKU_SIZE_TYPE_ID");
			Iterator viewableSKUitr = viewableSKUs.iterator();
			SizingQuery query = new SizingQuery();
			SearchResults psd = query.findPSDByProductAndSeason(product);
			Collection psdcoll = psd.getResults();
			Iterator itr = psdcoll.iterator();
			String ida2a2 = null;
			ProductSizeCategory psd1 = null;
			LCSSourcingConfigMaster sourceMaster = null;

			if (psdcoll != null && psdcoll.size() == 1) {
				while (itr.hasNext()) {
					FlexObject flexObj = (FlexObject) itr.next();
					ida2a2 = flexObj.getString("PRODUCTSIZECATEGORY.IDA2A2");
					psd1 = (ProductSizeCategory) LCSQuery
							.findObjectById("OR:com.lcs.wc.sizing.ProductSizeCategory:" + ida2a2);

				}
			}
			System.out.println("psd1::::::::::::"+psd1);

			while (viewableSKUitr.hasNext()) {

				Collection viewableSKU = new ArrayList();
				LCSSKU sku = (LCSSKU) viewableSKUitr.next();
				viewableSKU.add(sku);
				System.out.println("viewableSKU::::::::::::"+viewableSKU);

			skuSizes = new SKUSizeQuery().findViewableSKUSizesForPSC(new HashMap(), new Vector(), skuSizeType, psd1,
					seasonObj, null, null, null, null, false, true, false, false, viewableSKU).getResults();
			Iterator skuSizesItr = skuSizes.iterator();
			System.out.println("skuSizes::::::::::::"+skuSizes.size());

			while (skuSizesItr.hasNext()) {
				FlexObject fob = (FlexObject) skuSizesItr.next();

				String skusize = fob.getData("SKUSIZE.IDA2A2");
				SKUSize skusizeobj = (SKUSize) LCSQuery
						.findObjectById("OR:com.lcs.wc.skusize.SKUSize:" + skusize);
				
				System.out.println("skusizeobj::::::::::::"+skusizeobj);
				if ("1".equals(fob.getData("SKUSIZE.ACTIVE"))) {
				String HbiAlreadySent = (String) skusizeobj.getValue(SKU_ALREADY_SENT);
				if (!SKU_ALREADY_SENT_YES.equals(HbiAlreadySent)) {
					System.out.println("HbiAlreadySent::::::::::::"+HbiAlreadySent);
				if(!VersionHelper.isCheckedOut(skusizeobj)) {
					skusizeobj = VersionHelper.checkout(skusizeobj);
					skusizeobj.setValue(SKU_ALREADY_SENT, SKU_ALREADY_SENT_YES);
					VersionHelper.checkin(skusizeobj);
				}
				//skusizeobj.setValue(SKU_ALREADY_SENT, SKU_ALREADY_SENT_YES);
				//LCSLogic.persist(skusizeobj, true);
				
				

				}
				else { 
					
					System.out.println("-------------No need to set as its already set-----------"+HbiAlreadySent);
	
				}
				}
			}
			}
		}
		else		
		{
			
				LCSLog.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<FAILURE from SAP>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			    String existingMasterGrid=(String)prodarev.getValue("hbiMasterGrid");
				if(!FormatHelper.hasContent(existingMasterGrid)){
				prodarev.setValue("hbiMasterGrid",MaterialGrid);
				}
				prodarev.setValue("hbiFeedbackStatus",status);
				prodarev.setValue("hbiFeedback","ERROR>>>>>>>>>> "+Desc);
				if("hbiESUInProgress".equals( (String)prodarev.getValue("hbiSellingProductStatus"))){
					prodarev.setValue("hbiSellingProductStatus","hbiTransferFailed");
					System.out.println(">>>>>>>>>>>>>>>>>> hbiESUInProgress inside if block");
					/*if(!VersionHelper.isCheckedOut(prodarev)) {
						System.out.println(">>>>>>>>>>>>>>>>>> hbiESUInProgress inside if if block");
						VersionHelper.checkout(prodarev);
						prodarev.setValue("hbiSellingProductStatus","hbiTransferFailed");
						VersionHelper.checkin(prodarev);
					}*/
				}
				else {				
					prodarev.setValue("hbiSellingProductStatus","hbiTransferFailedFSU");
					System.out.println(">>>>>>>>>>>>>>>>>>  inside else block");
					/*if(!VersionHelper.isCheckedOut(prodarev)) {
						System.out.println(">>>>>>>>>>>>>>>>>>  inside else if  block");
						VersionHelper.checkout(prodarev);
						prodarev.setValue("hbiSellingProductStatus","hbiTransferFailedFSU");
						VersionHelper.checkin(prodarev);
					}*/
				}
				//LCSLogic.persist(prodarev,true);
				new com.lcs.wc.product.LCSProductLogic().saveProduct(prodarev);
				

				State hbiLCState1 = State.toState("INWORK");
				LCSProduct product1 = SeasonProductLocator.getProductSeasonRev(seasonProductLinkObj);
				product1 = (LCSProduct)VersionHelper.latestIterationOf(product1.getMaster());
				Double countDouble = count.doubleValue();
				//if(count.isNAN() || count==0.0 ) {
				if(countDouble.isNaN() || countDouble==0.0) {
					LCSLog.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<count>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+count);
					System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<count>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+count);

					//prodarev.setValue("hbiProductEditLockStatus", "hbiUnLock");
					//LCSLogic.persist(prodarev,true);	
					//new com.lcs.wc.product.LCSProductLogic().saveProduct(prodarev);
					
					if(!VersionHelper.isCheckedOut(prodarev)) {  
						System.out.println(">>>>>>>>>>>>>>>>>>  inside block count if ");
						VersionHelper.checkout(prodarev);
						prodarev.setValue("hbiProductEditLockStatus", "hbiUnLock");
						VersionHelper.checkin(prodarev);
					}

				}
				WFHelper.service.terminateAllRelatedProcesses(product1);

				LifeCycleHelper.service.setLifeCycleState(product1, hbiLCState1); 	
			
			
		}
	}

	private  String getStatusvalue(LCSProduct prodarev, Long count) {
		
		String desc=null;
		
		try {
			LCSLog.debug("Migrated Date>>>>>>>>>>>>>>>>"+prodarev.getValue("hbiProductMigratedOn"));
			LCSLog.debug("count>>>>>>>>>>>>>>>>"+count);
			System.out.println("Migrated Date>>>>>>>>>>>>>>>>"+prodarev.getValue("hbiProductMigratedOn"));
			System.out.println("count>>>>>>>>>>>>>>>>"+count);
			if((prodarev.getValue("hbiProductMigratedOn")!=null )|| count>0) {
				desc="Selling Product updated Successfully in SAP";
			}
			else {
				
				desc="Selling Product Created Successfully in SAP";

			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return desc;
	
	}



	public void loadSellingProductPutUpCodeDataToAPS(String soapMessage) throws Exception
	{
		//This function is using to convert SOAP message into a Map containing Key and Value to prepare a dataMap contains attribute key and value. 
		//Preparing SOAPMessage using the given XML format string (this string contains soap message in the form of XML data) to initialize SOAP Body NodesList
		Node putUpNode = null;
		Node materialNumNode = null;
		String flexTypeAttributeKey = "";
		String flexTypeAttributeValue = "";
		String flexTypeAttributePutUpCodeKey = "";
		String flexTypeAttributePutUpCodeValue = "";
		String flexTypeAttributeMatNumKey = "";
		String flexTypeAttributeMatNumValue = "";
		String productBranchID = "";
		String seasonBranchID="";
		Map<String, String> putUpCodeAttributesDataMap = new HashMap<String, String>();
		
		MessageFactory messageFactory = MessageFactory.newInstance();
	    SOAPMessage soapMessageObj = messageFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soapMessage.getBytes(Charset.forName("UTF-8"))));
	    SOAPBody body = soapMessageObj.getSOAPBody();
	
	    NodeList putUpAttributesList = body.getElementsByTagName("PUTUP_CODE_MOA_ROW");
    	String transactionID = body.getElementsByTagName("hbiTransactionID").item(0).getTextContent();	
		String[] transactionIDArray = transactionID.split("-");
		productBranchID = transactionIDArray[0];
		seasonBranchID = transactionIDArray[1];

		putUpCodeAttributesDataMap.put("productBranchID",productBranchID);
		putUpCodeAttributesDataMap.put("seasonBranchID",seasonBranchID);

	    //Iterating through each node from a params node, initializing attribute key and attribute value, preparing a dataMap contains attribute key and value
	    NodeList putUpAttributesChildNodeList = null;
    	for(int nodeIndex = 0; nodeIndex < putUpAttributesList.getLength(); nodeIndex++)
        {
    	    putUpAttributesChildNodeList = putUpAttributesList.item(nodeIndex).getChildNodes();
	    	putUpNode = putUpAttributesChildNodeList.item(0);
	    	materialNumNode = putUpAttributesChildNodeList.item(1);
	    	flexTypeAttributeKey = ""+putUpNode.getNodeName();
	    	flexTypeAttributeValue = ""+putUpNode.getTextContent();
	    	if(flexTypeAttributeKey.equals("hbiPutUpCode")){
	    		flexTypeAttributePutUpCodeKey = flexTypeAttributeKey;
	    		flexTypeAttributePutUpCodeValue = ""+putUpNode.getTextContent();
	    		flexTypeAttributeMatNumKey = materialNumNode.getNodeName();
	    		flexTypeAttributeMatNumValue = ""+materialNumNode.getTextContent();
		    	putUpCodeAttributesDataMap.put(flexTypeAttributePutUpCodeValue, flexTypeAttributeMatNumValue);
	    	}
        }
    	setSAPFeedbackOnPutUpCodeSellingProduct(putUpCodeAttributesDataMap);
	}
	
	public void setSAPFeedbackOnPutUpCodeSellingProduct(Map<String, String> putUpCodeMap) throws Exception
	{
		String productBranchID = (String)putUpCodeMap.get("productBranchID");
		String seasonBranchID = (String)putUpCodeMap.get("seasonBranchID");
		LCSSeason seasonObj = (LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+seasonBranchID);
		LCSProduct prd = (LCSProduct)LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+productBranchID);
		System.out.println(">>>>>>>>>>>>>>>> productBranchID,seasonBranchID "+seasonBranchID);
		LCSSeasonProductLink  seasonProductLinkObj = LCSSeasonQuery.findSeasonProductLink(prd,seasonObj);		
	

		if(prd != null && (prd.getValue("hbiPutUpCode") != null)){	
		LCSMOATable putUpMOATable = (LCSMOATable) prd.getValue("hbiPutUpCode");
		Collection<FlexObject> rowIdColl =putUpMOATable.getRows();
			if(rowIdColl.size() >= 0)
		{
				for(FlexObject flexObj : rowIdColl)
				{
					String hbiMOAPutUpCode = flexObj.getString("HBIPUTUPCODE");
					String key = flexObj.getString("OID");
					LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
					String putUpCodeValue = "";
					String materialNumber = "";
					LCSLifecycleManaged putUpBO = (LCSLifecycleManaged)moaObject.getValue("hbiPutUpCode");
					if(putUpBO != null){
					}
					for (Map.Entry<String, String> entry : putUpCodeMap.entrySet()) {
						putUpCodeValue = "0"+entry.getKey();
						materialNumber = entry.getValue();
						System.out.println("Condition check ##:"+
								FormatHelper.hasContent(hbiMOAPutUpCode) +
						FormatHelper.hasContent(putUpCodeValue) +
						(moaObject != null)+(putUpBO != null)+
						putUpBO.getValue("hbiPutUpCode").equals(putUpCodeValue)+"  "+
						putUpCodeValue
								);
						if(FormatHelper.hasContent(hbiMOAPutUpCode) && FormatHelper.hasContent(putUpCodeValue) &&
								moaObject != null && putUpBO != null &&
										putUpBO.getValue("hbiPutUpCode").equals(putUpCodeValue))
						{
							System.out.println(">>>>>>materialNumber>>>>>> "+ materialNumber);
							moaObject.setValue("hbiMaterialNumber",materialNumber);
							/*LCSMOACollectionClientModel moaModel = new LCSMOACollectionClientModel();
							StringBuffer dataBuffer = new StringBuffer();
							dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "hbiMaterialNumber", materialNumber );
							moaModel.load(FormatHelper.getObjectId(moaObject),"hbiPutUpCode");
							moaModel.updateMOACollection(dataBuffer.toString());*/
							
							LCSMOAObjectLogic.persist(moaObject,true);
							System.out.println(">>>>>>moaModel saved>>>>>>");
						}
					}
				}
			}
		}
	}
	public void loadSellingProductPlantExtCodeDataToAPS(String soapMessage) throws Exception
	{
		System.out.println("Track1 ------------------------------------------------------>");
		//This function is using to convert SOAP message into a Map containing Key and Value to prepare a dataMap contains attribute key and value. 
		//Preparing SOAPMessage using the given XML format string (this string contains soap message in the form of XML data) to initialize SOAP Body NodesList
		Node plantExtNode = null;
		Node syncStatusNode = null;
		String flexTypeAttributeKey = "";
		String flexTypeAttributeValue = "";
		String flexTypeAttributePlantExtCodeKey = "";
		String flexTypeAttributePlantExtValue = "";
		String flexTypeAttributeSynchStatusKey = "";
		String flexTypeAttributeSynchStatusValue = "";
		String productBranchID = "";
		Map<String, String> plantExtAttributesDataMap = new HashMap<String, String>();
		
		MessageFactory messageFactory = MessageFactory.newInstance();
	    SOAPMessage soapMessageObj = messageFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soapMessage.getBytes(Charset.forName("UTF-8"))));
	    SOAPBody body = soapMessageObj.getSOAPBody();
	
	    NodeList plantExtAttributesList = body.getElementsByTagName("Plant_MOA_ROW");
    	String transactionID = body.getElementsByTagName("hbiTransactionID").item(0).getTextContent();
		String[] transactionIDArray = transactionID.split("-");
		productBranchID = transactionIDArray[0];
		plantExtAttributesDataMap.put("productBranchID",productBranchID);
	    //Iterating through each node from a params node, initializing attribute key and attribute value, preparing a dataMap contains attribute key and value
	    NodeList plantExtAttributesChildNodeList = null;
    	for(int nodeIndex = 0; nodeIndex < plantExtAttributesList.getLength(); nodeIndex++)
        {
    		plantExtAttributesChildNodeList = body.getElementsByTagName("Plant_MOA_ROW").item(nodeIndex).getChildNodes();

    		plantExtNode = plantExtAttributesChildNodeList.item(0);
    		syncStatusNode = plantExtAttributesChildNodeList.item(1);
    		flexTypeAttributeKey = plantExtNode.getNodeName();
    		flexTypeAttributeValue = ""+plantExtNode.getTextContent();
    		if(flexTypeAttributeKey.equals("hbiPlantName1")){
    			flexTypeAttributePlantExtCodeKey = flexTypeAttributeKey;
    			flexTypeAttributePlantExtValue = ""+plantExtNode.getTextContent();
    			flexTypeAttributeSynchStatusKey = syncStatusNode.getNodeName();
    			flexTypeAttributeSynchStatusValue = ""+syncStatusNode.getTextContent();
    			plantExtAttributesDataMap.put(flexTypeAttributePlantExtValue, flexTypeAttributeSynchStatusValue);
	    	}
        }
    	setSAPFeedbackOnPlantExtSellingProduct(plantExtAttributesDataMap);
	}	
	
	public void setSAPFeedbackOnPlantExtSellingProduct(Map<String, String> plantExtCodeMap) throws Exception
	{
		String productBranchID = (String)plantExtCodeMap.get("productBranchID");
		LCSProduct prd = (LCSProduct)LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+productBranchID);
		if(prd != null && (prd.getValue("hbiErpPlantExtensions") != null)){	
		LCSMOATable plantExtMOATable = (LCSMOATable) prd.getValue("hbiErpPlantExtensions");
		Collection<FlexObject> rowIdColl =plantExtMOATable.getRows();	
		if(rowIdColl.size() >= 0)
		{
				for(FlexObject flexObj : rowIdColl)
				{
					String hbiMOAPlantExtCode = flexObj.getString("HBIPLANTNAME1");
					String key = flexObj.getString("OID");
					LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
					String plantExtValue = "";
					String synchStatus = "";
					LCSSupplier supplier = (LCSSupplier)moaObject.getValue("hbiPlantName1");
					if(supplier != null){//this if can be removed
					}
					for (Map.Entry<String, String> entry : plantExtCodeMap.entrySet()) {
						plantExtValue = entry.getKey();
						synchStatus = entry.getValue();
						if(FormatHelper.hasContent(hbiMOAPlantExtCode) && FormatHelper.hasContent(plantExtValue) &&
								moaObject != null && supplier != null &&
										supplier.getName().equals(plantExtValue))
						{
							boolean synchStatusBoolean = false;
							if(synchStatus.equalsIgnoreCase("yes")){
									synchStatusBoolean = true;		
							}
							moaObject.setValue("hbiSynchedStatus",synchStatusBoolean);
							System.out.println(">>>>>>>>synchStatusBoolean "+String.valueOf(synchStatusBoolean)+"synchStatusBoolean "+synchStatusBoolean);
							LCSMOAObjectLogic.persist(moaObject,true);
							/*LCSMOACollectionClientModel moaModel = new LCSMOACollectionClientModel();
							StringBuffer dataBuffer = new StringBuffer();
							System.out.println(">>>>>>>>synchStatusBoolean "+String.valueOf(synchStatusBoolean)+"synchStatusBoolean "+synchStatusBoolean);
							dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "hbiSynchedStatus", String.valueOf(synchStatusBoolean));
							moaModel.load(FormatHelper.getObjectId(moaObject),"hbiPlantName1");
							moaModel.updateMOACollection(dataBuffer.toString());*/
						}
					}
				}
			}
		}
	}
	
	public static String printSoapMessageFile(String message, LCSProduct productObj) {
		String outputFile = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
			String date = dateFormat.format(new Date());
			String temp = File.separator + "SOAP_MESSAGES_GENEREATED";
			WTProperties wtproperties = WTProperties.getLocalProperties();
			String wtHome = wtproperties.getProperty("wt.home");
			String outputFilePath = wtHome + temp + File.separator;

			String styleNo = (String) productObj.getValue("hbiSellingStyleNumber");
			String plmNo = (String) productObj.getValue("hbiPLMNo");
			System.out.println(">>>>>>>>>>>>outputFilePath>>>>>>>>>>"+outputFilePath);
			outputFile = outputFilePath + plmNo + "-" + styleNo + "-Feedback-" + date + ".xml";

			File log = new File(outputFile);
			log.createNewFile();
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true));
			writer.println(message);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputFile;

	}
}	
	
	
	
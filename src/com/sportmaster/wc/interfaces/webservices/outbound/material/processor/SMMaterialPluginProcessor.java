package com.sportmaster.wc.interfaces.webservices.outbound.material.processor;

import java.util.List;

import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.bean.DecorationInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.FabricMaterialInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.MaterialInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.PackagingInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.PlmEndpointService;
import com.sportmaster.wc.interfaces.webservices.bean.PlmWS;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;
import com.sportmaster.wc.interfaces.webservices.bean.ShippingInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.TrimsInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialHleper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialBean;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * SMMaterialPluginProcessor.java
 * This class is using to call process clas method.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMMaterialPluginProcessor {
	private static final String COMMON_ATTRIBUTES_LITERAL = "_COMMON_ATTRIBUTES";

	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMaterialPluginProcessor.class);

	private SMMaterialBean bean;


	/**
	 * Creating request for creation of Material.
	 * @param beanObj ---SMMaterialBean 
	 * @throws WTException --WTException
	 * @throws WTPropertyVetoException --WTPropertyVetoException
	 */
	public void creteRequest(SMMaterialBean beanObj) throws WTException{

		//setting bean object
		bean=beanObj;
		bean.setConstant(new SMOutboundWebServiceConstants());
		int reuestId=new SMMaterialUtill().getRequestID(SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_TYPEES);
		//setting request id
		bean.setSmRequestId(reuestId);
		bean.setSmObjectID(FormatHelper.getNumericFromReference(bean.getMaterial().getMasterReference()));
		//setting name
		bean.setSmObjectName(bean.getMaterial().getName());
		LOGGER.debug("Processing material: "+bean.getMaterial().getName()+" to send SOAP Request");
		//creating object of PlmEndpointService
		getObjectByType(bean.getObjectTye());

	}

	/**
	 * this method updates correct composition attribute key on key list, based on property entry.
	 * @param requestType - String
	 */
	public void replaceComposiotn(String requestType) {

		LOGGER.debug("requestType>>>>>"+requestType);

		String type = bean.getMaterial().getFlexType().getFullNameDisplay().toUpperCase();
		LOGGER.debug("Actual Material type>>>>>"+type);

		if (!"ALL_TYPE".equalsIgnoreCase(requestType) && !type.equalsIgnoreCase("FABRIC\\NON-WOVEN")) {
			
			LOGGER.debug("Not common type of material, so returning");
			return;

		}

		type=type.replace("\\", "_");
		type=type.replace(" ", "_");
		type=type.replace("_&", "");
		type=type.replace("-", "");
		
		LOGGER.debug("getting composition value,>"+type+"_COMPOSITION");
		String composition = bean.getPluginHelper().getField(bean.getConstant(),type+"_COMPOSITION");

		if(FormatHelper.hasContent(composition)) {

			String attrKey = bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + COMMON_ATTRIBUTES_LITERAL)
					.replace("smComposition", composition);
			
			if(type.equalsIgnoreCase("FABRIC_NONWOVEN")) {
				attrKey = bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + COMMON_ATTRIBUTES_LITERAL)
						.replace("vrdFiberContent", composition);
			}
			bean.setAttrKeyList((List<?>) FormatHelper.commaSeparatedListToCollection(attrKey));
		}

	}

	/**
	 * Getting Material type Object.
	 * @param type
	 */
	public void getObjectByType(String requestType){
		
		String type=requestType;

		type=type.replace("\\", "_");
		type=type.split("_")[0];
		type=type.replace(" ", "_");
		type=type.replace("_&", "");

		//3.9.2.0 BUILD
		if(bean.isCommonMaterialType()) {

			type = "ALL_TYPE";
		}
		//setting type
		bean.setType(type.toUpperCase());
		String className=bean.getPluginHelper().getField(bean.getConstant(), bean.getType()+"_CLASSNAME");
		try {
			// creating request class object
			Class<?> classDclr = Class.forName(SMOutboundWebServiceConstants.COMMON_BEAN_PACKAGE + "." + className);
			Object obj = classDclr.newInstance();
			bean.setAttrKeyList((List<?>) FormatHelper.commaSeparatedListToCollection(
					bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + COMMON_ATTRIBUTES_LITERAL)));

			replaceComposiotn(type);

			bean.setAttrMappedElementList((List<?>) FormatHelper.commaSeparatedListToCollection(
					bean.getPluginHelper().getField(bean.getConstant(), bean.getType() + "_COMMON_MAPPED_ELEMENTS")));

			// setting material field values
			bean.getHelper().setMaterialFieldVlaues(obj, bean);
			// setting material supplier , supplier , moa field values
			bean.getHelper().setMaterialSupplierFileds(obj, bean);
			// invoking the web service client
			invkeWebServiceClient(obj);

		} catch (ClassNotFoundException classNotFoundExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, classNotFoundExp);
		} catch (InstantiationException instabttionExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, instabttionExp);
		} catch (IllegalAccessException illegalAccessExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, illegalAccessExp);
		}
	}

	/**
	 * Invoking the web service request.
	 * @param obj --Object
	 */
	public void invkeWebServiceClient(Object obj){

		Object response=null;
		PlmEndpointService service = new PlmEndpointService();
		PlmWS ws = service.getPlmEndpointPort();

		if(!bean.isForScheduleQueue()){
			//setting fake mdm id
			bean.getPluginHelper().set(obj, "mdmId",SMOutboundWebServiceConstants.FAKE_MDM_ID);
		}
		//setting unique request id
		bean.getPluginHelper().set(obj, "requestId",bean.getSmRequestId());

		String matTypeWithinHierarchy = bean.getMaterial().getFlexType().getFullName(true);
		matTypeWithinHierarchy = matTypeWithinHierarchy.replace("&", "And");
		//matTypeWithinHierarchy
		bean.getPluginHelper().set(obj, "matTypeWithinHierarchy",matTypeWithinHierarchy);
		//adding service timeout time.
		((BindingProvider)ws).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, SMOutboundWebServiceConstants.MATERIAL_TIMEOUT_IN_MINUTES*60*1000);

		try{

			//invoking request for trim
			if(obj.getClass().getName().contains("TrimsInformationUpdatesRequest")){
				LOGGER.debug("Generating XML File  for request object : TrimsInformationUpdatesRequest");
				bean.getXmlHelper().generateXMLForMaterial((TrimsInformationUpdatesRequest)obj, bean.getObjectTye().replace("\\", "_"));
				LOGGER.debug("XML File Generated for request object : TrimsInformationUpdatesRequest, Invoking SOAP Request");
				//invoking soap request
				response=ws.trimsInformationUpdatesRequest((TrimsInformationUpdatesRequest) obj);

				//invoking request for fabric
			}else {
				response=invkeWebServiceClient(obj, ws);
			}

		} catch (ClientTransportException clientExc) {

			bean.setServiceError(true);
			bean.setSmErrorReason(clientExc.getLocalizedMessage());
			LOGGER.error(
					SMOutboundWebServiceConstants.OUTBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ obj.getClass().getName() + SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE,
					clientExc);
			// setting fail count
			bean.setTotalFailCount(1);

		} catch (SOAPFaultException soapExp) {

			bean.setServiceError(true);
			bean.setSmErrorReason(soapExp.getLocalizedMessage());
			LOGGER.error(
					SMOutboundWebServiceConstants.OUTBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ obj.getClass().getName() + SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE,
					soapExp);
			bean.setTotalFailCount(1);

		} catch (ServerSOAPFaultException serverSoapEXP) {

			bean.setServiceError(true);
			bean.setSmErrorReason(serverSoapEXP.getLocalizedMessage());
			LOGGER.error(
					SMOutboundWebServiceConstants.OUTBOUND_SCHEMA_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE
					+ obj.getClass().getName() + SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE,
					serverSoapEXP);
			bean.setTotalFailCount(1);

		} catch (WebServiceException webservcExp) {

			bean.setServiceError(true);
			bean.setSmErrorReason(webservcExp.getLocalizedMessage());
			LOGGER.error(
					SMOutboundWebServiceConstants.OUTBOUND_RESPONSE_TIMEOUT_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ obj.getClass().getName() + SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE,
					webservcExp);
			bean.setTotalFailCount(1);

		} catch (SQLException_Exception sqlExp) {

			bean.setServiceError(true);
			bean.setSmErrorReason(sqlExp.getLocalizedMessage());
			LOGGER.error(
					SMInboundWebserviceConstants.WEBSERVICE_INBOUND_TIMEOUT_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ obj.getClass().getName() + SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE,
					sqlExp);
			// setting fail count
			bean.setTotalFailCount(1);

		}
		//calling method to save material and log entry according to response
		new SMMaterialHleper().postServiceLoad(bean,response);

	}

	/**
	 * Invoking the web service request for other four material types.
	 * @param obj
	 * @param ws
	 * @return
	 * @throws ClientTransportException
	 * @throws SOAPFaultException
	 * @throws ServerSOAPFaultException
	 * @throws WebServiceException
	 * @throws SQLException_Exception
	 */
	public Object invkeWebServiceClient(Object obj, PlmWS ws) throws SQLException_Exception  {

		Object response=null;
		if(obj.getClass().getName().contains("FabricMaterialInformationUpdatesRequest")){

			LOGGER.debug("Generating XML File  for request object : FabricMaterialInformationUpdatesRequest");
			bean.getXmlHelper().generateXMLForMaterial((FabricMaterialInformationUpdatesRequest)obj, bean.getObjectTye().replace("\\", "_"));
			LOGGER.debug("XML File Generated for request object : FabricMaterialInformationUpdatesRequest, Invoking SOAP Request");
			//invoking soap request 
			response=ws.fabricMaterialInformationUpdatesRequest((FabricMaterialInformationUpdatesRequest) obj);

		}else if(obj.getClass().getName().contains("DecorationInformationUpdatesRequest")){

			LOGGER.debug("Generating XML File  for request object : DecorationInformationUpdatesRequest");
			bean.getXmlHelper().generateXMLForMaterial((DecorationInformationUpdatesRequest)obj, bean.getObjectTye());
			LOGGER.debug("XML File Generated for request object : DecorationInformationUpdatesRequest, Invoking SOAP Request");
			//invoking soap request 
			response=ws.decorationInformationUpdatesRequest((DecorationInformationUpdatesRequest) obj);

		}else if(obj.getClass().getName().contains("PackagingInformationUpdatesRequest")){

			LOGGER.debug("Generating XML File  for request object : PackagingInformationUpdatesRequest");
			bean.getXmlHelper().generateXMLForMaterial((PackagingInformationUpdatesRequest)obj, bean.getObjectTye().replace("\\", "_").replace(" ", "_"));
			LOGGER.debug("XML File Generated for request object : PackagingInformationUpdatesRequest, Invoking SOAP Request");
			//invoking soap request 
			response=ws.packagingInformationUpdatesRequest((PackagingInformationUpdatesRequest)obj);

		}else if(obj.getClass().getName().contains("ShippingInformationUpdatesRequest")){

			LOGGER.debug("Generating XML File  for request object : ShippingInformationUpdatesRequest");
			bean.getXmlHelper().generateXMLForMaterial((ShippingInformationUpdatesRequest)obj, bean.getObjectTye().replace("\\", "_").replace(" & ", "_"));
			LOGGER.debug("XML File Generated for request object : ShippingInformationUpdatesRequest, Invoking SOAP Request");
			response=ws.shippingInformationUpdatesRequest((ShippingInformationUpdatesRequest)obj);
		}else if(obj.getClass().getName().contains("MaterialInformationUpdatesRequest")){

			LOGGER.debug("Generating XML File  for request object : MaterialInformationUpdatesRequest");
			bean.getXmlHelper().generateXMLForMaterial((MaterialInformationUpdatesRequest)obj, bean.getObjectTye().replace("\\", "_").replace(" ", "_"));
			LOGGER.debug("XML File Generated for request object : MaterialInformationUpdatesRequest, Invoking SOAP Request");
			//invoking soap request 
			response=ws.materialInformationUpdatesRequest((MaterialInformationUpdatesRequest) obj);

		}
		return response;

	}

}

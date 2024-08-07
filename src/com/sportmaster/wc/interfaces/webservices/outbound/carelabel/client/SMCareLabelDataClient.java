/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.client;

import java.util.Iterator;


import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.BOMProductAttributes;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.BOMProductSeasonAttributes;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareLabelEndpointService;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareLabelReportRequest;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareLabelReportRequestResponse;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareLabelWS;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper.SMCareLabelIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.processor.SMCareLabelLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.client.SMCareLabelProductRequestProcessor;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

/**
 * SMCareLabelDataClient.
 * 
 * @author 'true' ITC.
 * @version 'true' 1.0 version number
 * @since Feb 23, 2018
 */
public class SMCareLabelDataClient {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCareLabelDataClient.class);


	/**
	 * Declaring Error Message.
	 */
	private String errorMsg;
	/**
	 * Constructor.
	 */
	public SMCareLabelDataClient() {
		// protected constructor.
	}

	public void careLabelRequest(List careLabelDatabyProd, SMCareLabelIntegrationBean integrationBean) {

		LOGGER.debug("########## Invoking care label Client #############");
		CareLabelReportRequestResponse careLabelRequestResponse=null;
		errorMsg="";

		try{

			// getting endpoint service
			CareLabelEndpointService careLabelEndPointService = new CareLabelEndpointService();
			// getting endpoint port
			CareLabelWS careLabelWS = careLabelEndPointService.getCareLabelEndpointPort();
			// creating object for request
			CareLabelReportRequest careLabelRequest = new CareLabelReportRequest();

			// START : Setting data to the bean.

			// set request ID
			integrationBean.setCareLabelRequestID(new com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelUtil()
			.generateCareLabelOutboundIntegrationRequestID());
			careLabelRequest.setRequestId(integrationBean.getCareLabelRequestID());

			//initializing . 
			LCSProduct prodObj = null;
			FlexObject eachRowBOMdata = null;
			LCSProductSeasonLink prodSeasonlink = null;
			LCSSeason seasObj = null;

			// Get the product and product-season from the flex object
			Iterator<?> iterCareLabl = careLabelDatabyProd.iterator();
			if (iterCareLabl.hasNext()) {

				eachRowBOMdata = (FlexObject) iterCareLabl.next();
				//LOGGER.debug("eachRowBOMdata    >>>>" + eachRowBOMdata);

				String prodOid = (String) eachRowBOMdata.getData("LCSPRODUCT.BRANCHIDITERATIONINFO");
				String seasOid = (String) eachRowBOMdata.getData("LCSPRODUCTSEASONLINK.SEASONREVID");

				// Getting product object
				if (FormatHelper.hasContent(prodOid)
						&& FormatHelper.hasContent(seasOid)) {
					prodObj = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+ prodOid);
					seasObj = (LCSSeason) LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+ seasOid);
					prodSeasonlink = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(prodObj, seasObj);
				}

			}

			//object is null then return.
			if(prodObj==null || prodSeasonlink==null){
				return;
			}

			//set data to the processor
			setDatatoRequestProcessor(careLabelDatabyProd, integrationBean,
					careLabelRequest, prodObj, eachRowBOMdata, prodSeasonlink);

			//Process request
			careLabelRequestResponse = processesRequest(careLabelWS, careLabelRequest);


		}catch(ClientTransportException clientExc){
			errorMsg = clientExc.getLocalizedMessage();
			integrationBean.setResponseErrorReason("Connectivity issue >>> " +errorMsg);
			LOGGER.error(SMCareLabelConstants.OUTBOUND_CL_CONNECTIVITY_ERROR_CODE+SMCareLabelConstants.OUTBOUND_CL_CONNECTIVITY_ERROR_MESG+errorMsg);
			clientExc.printStackTrace();
		}catch (ServerSOAPFaultException serverSoapEXP) {
			errorMsg = serverSoapEXP.getLocalizedMessage();
			integrationBean.setResponseErrorReason("ServerSOAPFaultException >>> " + errorMsg);
			LOGGER.error(SMCareLabelConstants.OUTBOUND_CL_SCHEMA_ERROR_CODE+SMCareLabelConstants.OUTBOUND_CL_SCHEMA_ERROR_MESG+errorMsg);
			serverSoapEXP.printStackTrace();
		}catch(SOAPFaultException soapExp){
			errorMsg = soapExp.getLocalizedMessage();
			integrationBean.setResponseErrorReason("SOAPFaultException >>> " +errorMsg);
			LOGGER.error(SMCareLabelConstants.OUTBOUND_CL_CONNECTIVITY_ERROR_CODE+SMCareLabelConstants.OUTBOUND_CL_CONNECTIVITY_ERROR_MESG+errorMsg);
			soapExp.printStackTrace();
		}catch(WebServiceException webSrcvExcp){
			errorMsg = webSrcvExcp.getLocalizedMessage();
			integrationBean.setResponseErrorReason("WebServiceException connectivity issue" +errorMsg);
			LOGGER.error(SMCareLabelConstants.OUTBOUND_CL_CONNECTIVITY_ERROR_CODE+SMCareLabelConstants.OUTBOUND_CL_CONNECTIVITY_ERROR_MESG+errorMsg);
			webSrcvExcp.printStackTrace();
		}
		catch (WTException e) {
			LOGGER.error("WTException while invoking Client >>>" +e);
			e.printStackTrace();
		} 

		//process response and set Log Entry.
		SMCareLabelResponseProcessor.processResponse(careLabelRequestResponse,integrationBean);

	}

	private void setDatatoRequestProcessor(List careLabelDatabyProd,
			SMCareLabelIntegrationBean integrationBean,
			CareLabelReportRequest careLabelRequest, LCSProduct prodObj,
			FlexObject eachRowBOMdata, LCSProductSeasonLink prodSeasonlink) {

		try{
			// set Product attributes.
			LOGGER.debug("START Processing for the product >>> " +prodObj.getName());
			SMCareLabelProductRequestProcessor smCareLabelProdReqProcessor = new SMCareLabelProductRequestProcessor();
			BOMProductAttributes bomProductattBean = smCareLabelProdReqProcessor.setProductAttributes(prodObj, eachRowBOMdata);
			careLabelRequest.setBOMProductAttributes(bomProductattBean);

			// set Product season attributes
			BOMProductSeasonAttributes bomProdSeasonBean = smCareLabelProdReqProcessor.setProductSeasonAttributes(prodSeasonlink, eachRowBOMdata);
			careLabelRequest.setBOMProductSeasonAttributes(bomProdSeasonBean);

			// Set BOM records.
			SMCareLabelRequestProcessor smCareLabelReqProcessor = new SMCareLabelRequestProcessor();
			smCareLabelReqProcessor.setProductBOMData(careLabelRequest, careLabelDatabyProd);


			// code to generate XML File for data sent.
			generateXMLForRequest(careLabelRequest, integrationBean);

			//Creating Log entry
			SMCareLabelLogEntryProcessor.createLogEntryForRequest(prodSeasonlink, integrationBean);

		}catch (WTException e) {
			LOGGER.error("WTException while invoking Client >>>" +e);
			e.printStackTrace();
		} catch (WTPropertyVetoException wpt) {
			LOGGER.error("WTPropertyVetoException while invoking Client >>>" +wpt);
			wpt.printStackTrace();
		}catch (DatatypeConfigurationException dtc) {
			dtc.printStackTrace();
		}
	}


	/**
	 * Generating XML file for the request bean.
	 * @param careLabelRequest
	 */
	private void generateXMLForRequest(CareLabelReportRequest careLabelRequest, SMCareLabelIntegrationBean integrationBean) {

		// check if XML generation is set true.
		//LOGGER.debug("SMCareLabelConstants.IS_CARE_LABEL_INTEGRATION_XML_GENERATION>>>." +SMCareLabelConstants.IS_CARE_LABEL_INTEGRATION_XML_GENERATION);
		if (SMCareLabelConstants.IS_CARE_LABEL_INTEGRATION_XML_GENERATION && null != careLabelRequest) {
			LOGGER.debug("START Generating xml ********************************");
			// generate XML file.
			com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelXMLGenerationUtil.generateXMLFileForCareLabelIntegrationRequest(careLabelRequest, "CARE_LABEL", integrationBean);
		}
	}
	/**
	 * Processing request
	 * @param careLabelWS the CalreLableWS.
	 * @param careLabelRequest the request.
	 */
	private CareLabelReportRequestResponse processesRequest(CareLabelWS careLabelWS,
			CareLabelReportRequest careLabelRequest) {
		// Sending Request and Getting Response.

		CareLabelReportRequestResponse careLabelRequestResponse = careLabelWS.careLabelReportRequest(careLabelRequest);
		LOGGER.debug("Response received  :::::::::::::::::   "+careLabelRequestResponse);

		if(null != careLabelRequestResponse){

			//print response details in logs.

			LOGGER.debug("Request ID          ===============>>    "+careLabelRequestResponse.getRequestId());
			LOGGER.debug("PLM ID in response  ===============>>    "+careLabelRequestResponse.getProductSeasonPLMId());
			LOGGER.debug("MDM ID in response  ===============>>    "+careLabelRequestResponse.getProductSeasonMDMId());
			LOGGER.debug("Integration Status  ===============>>    "+careLabelRequestResponse.isIntegrationStatus());
			LOGGER.debug("Error Reeason       ===============>>    "+careLabelRequestResponse.getErrorMessage());
		}

		return careLabelRequestResponse;
	}



}

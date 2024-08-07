/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareLabelReportRequestResponse;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper.SMCareLabelIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

/**
 * SMCareLabelLogEntryProcessor.
 * 
 * @author 'true' ITC.
 * @version 'true' 1.0 version number
 * @since March 13, 2018
 */
public class SMCareLabelLogEntryProcessor {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCareLabelLogEntryProcessor.class);
	/**
	 * Log Entry class.
	 */
	private static final String LOG_ENTRY_CLASS = "com.lcs.wc.foundation.LCSLogEntry:";
	/**
	 * ida2a2.
	 */
	private static final String IDA2A2 = "IDA2A2";
	/**
	 * LogEntry ida2a2.
	 */
	private static final String LOG_ENTRY_IDA2A2 = "LCSLOGENTRY.IDA2A2";/**
	 * THE CONSTANT BR.
	 */
	private final static String BR = "\n";



	//constructor.
	protected SMCareLabelLogEntryProcessor(){
		//constructor.
	}



	/**
	 * Create Log Entry for Care Label Integration.
	 * @param psl - LCSProductSeasonLink
	 * @throws WTException the exception.
	 * @throws WTPropertyVetoException the exception.
	 */
	public static void createLogEntryForRequest(LCSProductSeasonLink psl, SMCareLabelIntegrationBean integrationBean) throws WTException, WTPropertyVetoException{
		LOGGER.debug("Creating Log Entry record for Care Label Integration Request ................");
		//set Log Entry Path.
		FlexType careLabelLogEntryType = FlexTypeCache.getFlexTypeFromPath(SMCareLabelConstants.CARE_LABEL_INTEGARTION_LOG_ENTRY_PATH);
		LCSLogEntry careLabelLogEntry = LCSLogEntry.newLCSLogEntry();
		careLabelLogEntry.setFlexType(careLabelLogEntryType);
		//set request ID.
		//SMCareLabelDataClient smCareLabelDataClient = new SMCareLabelDataClient();
		careLabelLogEntry.setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_REQUEST_ID, String.valueOf(integrationBean.getCareLabelRequestID()));
		//set MDM ID.
		careLabelLogEntry.setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_MDM_ID, psl.getValue(SMCareLabelConstants.PRODUCT_SEASON_LINK_MDM_ID));
		//check if plm ID is not null.
		if(null != SMCareLabelUtil.getProductSeasonLinkPLMID(psl)){
			//set PLM ID.
			careLabelLogEntry.setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_PLM_ID, SMCareLabelUtil.getProductSeasonLinkPLMID(psl));
		}
		//set Error Reason
		careLabelLogEntry.setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_ERROR_REASON, "");
		//check if product season link is valid.
		if(null != SMCareLabelUtil.getObjectDetails(psl)){
			//set Object Details.
			careLabelLogEntry.setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_OBJECT_DETAILS, SMCareLabelUtil.getObjectDetails(psl));
		}


		//set user who triggered integration.
		String authentcaionName = ((wt.org.WTUser)wt.session.SessionHelper.manager.getPrincipal()).getAuthenticationName();
		careLabelLogEntry.setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_INTEGRATION_TRIGGERED_BY, authentcaionName);


		//SMCareLabelIntegrationBean smIntegrbean = new SMCareLabelIntegrationBean();
		String strFilterApplier = getFilterApplied(integrationBean);


		careLabelLogEntry.setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_FILTERS_APPLIED, strFilterApplier);

		//set Status to PENDING.
		careLabelLogEntry.setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_INTEGRATION_STATUS, SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_PENDING_STATUS);

		//persist log entry.
		persistCareLabelLogEntryRecord(careLabelLogEntry);
	}


	/**
	 * This method get the filter applier details in the string.
	 * @param integrationBean
	 */
	private static  String getFilterApplied(
			SMCareLabelIntegrationBean integrationBean) {

		StringBuffer strFilterApp = new StringBuffer();

		LOGGER.debug("Selected season name " +integrationBean.getSelectedSeasonName());
		LOGGER.debug("Selected Product name " +integrationBean.getSelectedProductName());
		LOGGER.debug("Selected Brands name " +integrationBean.getSelectedBrands());
		LOGGER.debug("Selected age name " +integrationBean.getSelectedAges());
		LOGGER.debug("Selected Gender name " +integrationBean.getSelectedGenders());
		LOGGER.debug("Selected Tecnologist name " +integrationBean.getSelectedProducctTechnologist());
		LOGGER.debug("Selected ProductionGroup name " +integrationBean.getSelectedProductionGroup());

		//String strFilterApp ="";
		//Apeend Season
		strFilterApp.append("Season: ");
		strFilterApp.append(integrationBean.getSelectedSeasonName());
		strFilterApp.append(BR);
		//Append Product
		strFilterApp.append("Product Name: ");
		strFilterApp.append(integrationBean.getSelectedProductName());
		strFilterApp.append(BR);

		//Append Projects
		if(FormatHelper.hasContent(integrationBean.getSelectedProject())){

			strFilterApp.append("Projects: ");
			strFilterApp.append(integrationBean.getSelectedProject());
			strFilterApp.append(BR);
		}

		//Append Brands
		if(FormatHelper.hasContent(integrationBean.getSelectedBrands())){

			strFilterApp.append("Brands: ");
			strFilterApp.append(integrationBean.getSelectedBrands());
			strFilterApp.append(BR);
		}

		//Append  Genders
		if(FormatHelper.hasContent(integrationBean.getSelectedGenders())){
			strFilterApp.append("Genders: ");
			strFilterApp.append(integrationBean.getSelectedGenders());
			strFilterApp.append(BR);

		}

		//Append  Ages
		if(FormatHelper.hasContent(integrationBean.getSelectedAges())){
			strFilterApp.append("Ages: ");
			strFilterApp.append(integrationBean.getSelectedAges());
			strFilterApp.append(BR);

		}
		//Append  production group
		if(FormatHelper.hasContent(integrationBean.getSelectedProductionGroup())){
			strFilterApp.append("Production Groups: ");
			strFilterApp.append(integrationBean.getSelectedProductionGroup());
			strFilterApp.append(BR);

		}

		//Append  Tecnologist
		if(FormatHelper.hasContent(integrationBean.getSelectedProducctTechnologist())){
			strFilterApp.append("Product Tecnologist: ");
			strFilterApp.append(integrationBean.getSelectedProducctTechnologist());
			strFilterApp.append(BR);

		}



		return strFilterApp.toString();
	}

	/**
	 * Update Log Entry after getting response from PLM Gate.
	 * @param careLabelResponseItem - CareLabelReportRequestResponse
	 * @param entry - Map<String, LCSLogEntry>
	 */
	public static void updateLogEntryOnResponse(CareLabelReportRequestResponse careLabelResponseItem, Map.Entry<String, LCSLogEntry> entry){
		LOGGER.debug("Updating Log Entry record for Care Label Integration Response  ..............");
		if(careLabelResponseItem.isIntegrationStatus()){
			entry.getValue().setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_INTEGRATION_STATUS, SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_PROCESSED_STATUS);
			entry.getValue().setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_ERROR_REASON, "");
		}else{
			entry.getValue().setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_INTEGRATION_STATUS, SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_PENDING_STATUS);
			if(FormatHelper.hasContent(careLabelResponseItem.getErrorMessage())){
				entry.getValue().setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_ERROR_REASON, careLabelResponseItem.getErrorMessage());
			}
		}
		//persist log entry.
		persistCareLabelLogEntryRecord(entry.getValue());
	}

	/**
	 * Query Log Entry for Care Label.
	 * @param attribute - String.
	 * @return - Map<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryCareLabelLogEntry(String attribute){

		//Hashmap to store Log entries
		Map<String, LCSLogEntry> careLabelLogEntryCollection=new HashMap<String, LCSLogEntry>();
		try{
			com.lcs.wc.flextype.FlexType careLabelLogEntryType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMCareLabelConstants.CARE_LABEL_INTEGARTION_LOG_ENTRY_PATH);
			com.lcs.wc.db.PreparedQueryStatement careLabelStmt = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
			careLabelStmt.appendFromTable(LCSLogEntry.class);
			careLabelStmt.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, careLabelLogEntryType.getAttribute(attribute).getColumnName());//append column
			careLabelStmt.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, IDA2A2);//append column
			careLabelStmt.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, SMOutboundWebServiceConstants.FLEXTYPEIDPATH, careLabelLogEntryType.getIdPath(),Criteria.EQUALS));//adding criteria
			com.lcs.wc.db.SearchResults results = null;
			//executing  statement
			results =LCSQuery.runDirectQuery(careLabelStmt);
			List<?> careLabelCollection= results.getResults();
			FlexObject fo=null;
			if (careLabelCollection.size() > 0) {
				for(Object obj:careLabelCollection){
					fo= (FlexObject) obj;
					LCSLogEntry logEntry=(LCSLogEntry) LCSQuery.findObjectById(LOG_ENTRY_CLASS+fo.getString(LOG_ENTRY_IDA2A2));
					//storing in hash map
					careLabelLogEntryCollection.put((String)logEntry.getValue(attribute), logEntry);
				}	
			}

		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage());
			we.printStackTrace();
		}

		//returning log entry
		return (HashMap<String, LCSLogEntry>) careLabelLogEntryCollection;
	}


	/**
	 * Persists log entry object.
	 * @param logEntryObj - LCSLogEntry
	 */
	public static void persistCareLabelLogEntryRecord(LCSLogEntry logEntryRecord){
		LCSLogEntryLogic careLabelLogEntryLogic = new LCSLogEntryLogic();
		try {
			LOGGER.info("Persisting care Label log entry record ..................");
			//Save Log Entry object
			careLabelLogEntryLogic.saveLog(logEntryRecord, true);
		} catch (WTException e) {
			LOGGER.error("ERROR in persisting Log Entry Object !!!!!!  "+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}




	/**
	 * Update Log Entry after for any fault exception.
	 * @param careLabelResponseItem - CareLabelReportRequestResponse
	 * @param entry - Map<String, LCSLogEntry>
	 */
	public static void updateLogEntryForError(SMCareLabelIntegrationBean integrationBean, Map.Entry<String, LCSLogEntry> entry){
		
		LOGGER.debug("Updating Log Entry record for Care Label Integration Error reason  ..............");

		entry.getValue().setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_INTEGRATION_STATUS, SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_PENDING_STATUS);
		if(FormatHelper.hasContent(integrationBean.getResponseErrorReason())){
			entry.getValue().setValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_ERROR_REASON, integrationBean.getResponseErrorReason());

			//persist log entry.
			persistCareLabelLogEntryRecord(entry.getValue());
		}
	}
}

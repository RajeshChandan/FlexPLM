/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import wt.util.WTException;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.client.SMProductInboundFeedbackDataRequestClient;
import com.sportmaster.wc.interfaces.webservices.inbound.product.helper.SMProductInboundHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationStatus;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationStatusItem;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationStatusResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationStatusResponseItem;
import com.sportmaster.wc.interfaces.webservices.productbean.GetStatusProductRequestResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationStatus;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationStatusItem;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationStatusResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationStatusResponseItem;

/**
 * @author Carrier
 *
 */
public class SMProductInboundFeedbackLogEntryProcessor {

	/**
	 * THE LOGGER.
	 */
	private static final Logger LOGGER=Logger.getLogger(SMProductInboundFeedbackLogEntryProcessor.class);
	/**
	 * Constructor.
	 */
	protected SMProductInboundFeedbackLogEntryProcessor(){
		//Constructor.
	}
	/**
	 * Product Season Link Feedback Collection.
	 */
	private static Map<String, LCSLogEntry>productSeasonLinkFeedbackCollection;
	/**
	 * Colorway Season Link Feedback Collection.
	 */
	private static Map<String, LCSLogEntry>colorwaySeasonLinkFeedbackCollection;

	/**
	 * Process Log Enrty for Feedback Processing.
	 * @throws WTException 
	 */
	public static ProductSeasonLinkInformationStatus processLogEntryForProductFeedbackStatus() throws WTException{
		//set log entry data to collection.
		setProductSeasonLinkFeedbackCollection(queryProductSeasonFeedbackLogEntry());
		ProductSeasonLinkInformationStatus prodSeasonLinkFeedbackStatus = new ProductSeasonLinkInformationStatus();
		if(getProductSeasonLinkFeedbackCollection() != null && getProductSeasonLinkFeedbackCollection().size() > 0){
			for(Map.Entry<String, LCSLogEntry> prodMapEnrty : getProductSeasonLinkFeedbackCollection().entrySet()){
				LOGGER.info("Adding product season item from list to feedback ##############");
				ProductSeasonLinkInformationStatusItem prodInboundStatusItem = new ProductSeasonLinkInformationStatusItem();
				prodInboundStatusItem.setMdmId(prodMapEnrty.getValue().getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_MDM_ID).toString());
				prodInboundStatusItem.setPlmId(prodMapEnrty.getValue().getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID).toString());
				prodInboundStatusItem.setRequestStatus(prodMapEnrty.getValue().getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_INTEGRATION_STATUS).toString());
				//Added for PHASE-8 SEPD changes
				prodInboundStatusItem.setIterationId(setItterationId(prodMapEnrty.getValue()));
				//check if XMLGeneration is set.
				if(SMProductInboundWebServiceConstants.GENERATE_RESPONSE_FOR_FEEDBACK_DATA){
					LOGGER.info("Generating XML for product season inbound feedback  *****************");
					SMProductInboundHelper.generateXMLForProductInboundFeedbackResponse(prodInboundStatusItem);
				}
				//add item to list.
				prodSeasonLinkFeedbackStatus.getProductSeasonLinkInformationStatusItem().add(prodInboundStatusItem);
			}
		}
		return prodSeasonLinkFeedbackStatus;
	}
	public static ColorwaySeasonLinkInformationStatus processLogEntryForColorwayFeedbackStatus() throws WTException{
		//set log entry data to collection.
		setColorwaySeasonLinkFeedbackCollection(queryColorwaySeasonFeedbackLogEntry());
		ColorwaySeasonLinkInformationStatus colorwaySeasonLinkFeedbackStatus = new ColorwaySeasonLinkInformationStatus();
		if(getColorwaySeasonLinkFeedbackCollection() != null && getColorwaySeasonLinkFeedbackCollection().size() > 0){
			for(Map.Entry<String, LCSLogEntry> skuMapEntry : getColorwaySeasonLinkFeedbackCollection().entrySet()){
				LOGGER.info("Adding colorway season item from list to feedback ##############");
				ColorwaySeasonLinkInformationStatusItem colorwayInboundStatusItem = new ColorwaySeasonLinkInformationStatusItem();
				colorwayInboundStatusItem.setMdmId(skuMapEntry.getValue().getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_MDMID).toString());
				colorwayInboundStatusItem.setPlmId(skuMapEntry.getValue().getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID).toString());
				colorwayInboundStatusItem.setRequestStatus(skuMapEntry.getValue().getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_INTEGARTION_STATUS).toString());
				//Added for PHASE-8 SEPD changes
				colorwayInboundStatusItem.setIterationId(setItterationId(skuMapEntry.getValue()));
				//check if XML generation is set.
				if(SMProductInboundWebServiceConstants.GENERATE_RESPONSE_FOR_FEEDBACK_DATA){
					LOGGER.info("Generating XML for colorway season inbound feedback   **************");
					SMProductInboundHelper.generateXMLForColorwayInboundFeedbackResponse(colorwayInboundStatusItem);
				}
				//add item to list.
				colorwaySeasonLinkFeedbackStatus.getColorwaySeasonLinkInformationStatusItem().add(colorwayInboundStatusItem);
			}
		}
		return colorwaySeasonLinkFeedbackStatus;
	}

	/**
	 * Queries the Product Season Log Entry.
	 * @param flexPath - String
	 * @throws WTException - WTException
	 * @returns hashMapLogEntry - HashMap<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryProductSeasonFeedbackLogEntry(){
		try{
			LOGGER.debug("Inside Query Product Season Link Inbound Log Entry method !!!!!!!!!!");
			//Hashmap to store Log entries
			Map<String, LCSLogEntry> hashMapProductSeasonLinkLogEntry=new HashMap<String, LCSLogEntry>();
			com.lcs.wc.flextype.FlexType logType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PATH);
			com.lcs.wc.db.PreparedQueryStatement prodSeasonStatement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
			prodSeasonStatement.appendFromTable(LCSLogEntry.class);
			prodSeasonStatement.appendSelectColumn(SMProductInboundWebServiceConstants.LCSLOGENTRY, logType.getAttribute(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID).getColumnName());//append column
			prodSeasonStatement.appendSelectColumn(SMProductInboundWebServiceConstants.LCSLOGENTRY, "IDA2A2");//append column
			prodSeasonStatement.appendCriteria(new Criteria(SMProductInboundWebServiceConstants.LCSLOGENTRY, "flexTypeIdPath", logType.getIdPath(),Criteria.EQUALS));//adding criteria
			prodSeasonStatement.appendAndIfNeeded();
			prodSeasonStatement.appendCriteria(new Criteria(SMProductInboundWebServiceConstants.LCSLOGENTRY,logType.getAttribute(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_FEEDBACK_STATUS).getColumnName(),SMProductInboundWebServiceConstants.PENDING,Criteria.EQUALS));
			com.lcs.wc.db.SearchResults results = null;
			//executing  statement
			results =LCSQuery.runDirectQuery(prodSeasonStatement);
			List<?> dataCollection= results.getResults();
			FlexObject fo=null;
			//LOGGER.debug("Log Entry data collection Size >>>>>>>\t"+dataCollection.size());
			if (dataCollection.size() > 0) {
				for(Object obj:dataCollection){
					fo= (FlexObject) obj;
					LCSLogEntry logEntry=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+fo.getString("LCSLOGENTRY.IDA2A2"));
					//System.out.println("Product Log Entry PLM ID  >>>   "+logEntry.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID));
					//storing in hashmap
					hashMapProductSeasonLinkLogEntry.put((String)logEntry.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID), logEntry);
				}	
			}
			//returning log entry
			return (HashMap<String, LCSLogEntry>) hashMapProductSeasonLinkLogEntry;
		}catch(WTException exp){
			exp.printStackTrace();
			return null;
		}
	}

	/**
	 * Queries the Colorway Season Link Log Entry.
	 * @param flexPath - String
	 * @throws WTException - WTException
	 * @returns hashMapLogEntry - HashMap<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryColorwaySeasonFeedbackLogEntry(){
		try{
			LOGGER.debug("Inside query Colorway Season Link Inbound method !!!!!!!!!!");
			//Hashmap to store Log entries
			Map<String, LCSLogEntry> hashColorwaySeasonLogEntryMapEntry=new HashMap<String, LCSLogEntry>();
			com.lcs.wc.flextype.FlexType logType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PATH);
			com.lcs.wc.db.PreparedQueryStatement skuSeasonStatement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
			skuSeasonStatement.appendFromTable(LCSLogEntry.class);
			skuSeasonStatement.appendSelectColumn(SMProductInboundWebServiceConstants.LCSLOGENTRY, logType.getAttribute(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID).getColumnName());//append column
			skuSeasonStatement.appendSelectColumn(SMProductInboundWebServiceConstants.LCSLOGENTRY, "IDA2A2");//append column
			skuSeasonStatement.appendCriteria(new Criteria(SMProductInboundWebServiceConstants.LCSLOGENTRY, "flexTypeIdPath", logType.getIdPath(),Criteria.EQUALS));//adding criteria
			skuSeasonStatement.appendAndIfNeeded();
			skuSeasonStatement.appendCriteria(new Criteria(SMProductInboundWebServiceConstants.LCSLOGENTRY,logType.getAttribute(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_FEEDBACK_STATUS).getColumnName(), SMProductInboundWebServiceConstants.PENDING,Criteria.EQUALS));
			com.lcs.wc.db.SearchResults results = null;
			//executing  statement
			results =LCSQuery.runDirectQuery(skuSeasonStatement);
			List<?> dataCollection= results.getResults();
			FlexObject fo=null;
			LOGGER.debug("Log Entry data collection Size >>>>>>>\t"+dataCollection.size());
			if (dataCollection.size() > 0) {
				for(Object obj:dataCollection){
					fo= (FlexObject) obj;
					LCSLogEntry logEntry=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+fo.getString("LCSLOGENTRY.IDA2A2"));
					//System.out.println("Colorway Log Entry PLM ID  >>>   "+logEntry.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID));
					//storing in hashmap
					hashColorwaySeasonLogEntryMapEntry.put((String)logEntry.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID), logEntry);
				}	
			}
			//returning log entry
			return (HashMap<String, LCSLogEntry>) hashColorwaySeasonLogEntryMapEntry;
		}catch(WTException wExp){
			wExp.printStackTrace();
			return null;
		}
	}

	/**
	 * Process response received in Feedback.
	 * @param prodFeedbackResponse - GetStatusProductRequestResponse
	 * @throws WTException 
	 */
	public static void processFeedbackResponse(GetStatusProductRequestResponse prodFeedbackResponse) throws WTException{
		if(prodFeedbackResponse != null){
			ProductSeasonLinkInformationStatusResponse prodSeasonLinkStatusResponse = prodFeedbackResponse.getProductSeasonLinkInformationStatusResponse();
			ColorwaySeasonLinkInformationStatusResponse colorwaySeasonLinkStatusResponse = prodFeedbackResponse.getColorwaySeasonLinkInformationStatusResponse();

			if(null != prodSeasonLinkStatusResponse){
				List<ProductSeasonLinkInformationStatusResponseItem> prodSeasonStatusResponseItem = prodSeasonLinkStatusResponse.getProductSeasonLinkInformationStatusResponseItem();

				setProductSeasonFeedbackResponsetoLogEntry(prodSeasonStatusResponseItem);
			}

			if(null != colorwaySeasonLinkStatusResponse){
				List<ColorwaySeasonLinkInformationStatusResponseItem> colorwaySeasonStatusResponseItem = colorwaySeasonLinkStatusResponse.getColorwaySeasonLinkInformationStatusResponseItem();

				setColorwaySeasonFeedbackResponsetoLogEntry(colorwaySeasonStatusResponseItem);
			}

		}
	}

	/**
	 * Set Feeback on Log Entry.
	 * @param prodSeasonStatusResponseItem - ProductSeasonLinkInformationStatusResponseItem
	 * @throws WTException 
	 */
	public static void setProductSeasonFeedbackResponsetoLogEntry(List<ProductSeasonLinkInformationStatusResponseItem> prodSeasonStatusResponseItem) throws WTException{
		if(prodSeasonStatusResponseItem != null && prodSeasonStatusResponseItem.size() > 0){
			LOGGER.debug("RESPONSE FOR PRODUCT SEASON LINK INBOUND FEEDBACK  :  ");
			for(ProductSeasonLinkInformationStatusResponseItem prodFeedbackItem : prodSeasonStatusResponseItem){
				LOGGER.debug("Received Status Value for PLM ID >>>  "+prodFeedbackItem.getPlmId()+" and MDM ID  >>>  "+prodFeedbackItem.getMdmId()+"  is  >>>>  "+prodFeedbackItem.getRequestStatus());
				LCSLogEntry prodSeasonLogEntryObj = getProductSeasonLinkFeedbackCollection().get(prodFeedbackItem.getPlmId());
				//System.out.println("Prod Season Log Entry Details ::::  "+prodSeasonLogEntryObj.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID));
				if(prodSeasonLogEntryObj != null){
					prodSeasonLogEntryObj.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID, prodFeedbackItem.getPlmId());
					if(SMProductInboundWebServiceConstants.RECEIVED_VALID_FEEDBACK.equalsIgnoreCase(prodFeedbackItem.getRequestStatus())){
						LOGGER.info("received valid  ......setting status to Processed !!!!!!!!");
						prodSeasonLogEntryObj.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PROCESSED);
					}else{
						LOGGER.info("received invalid  ......setting status to PENDING !!!!!!!!");
						prodSeasonLogEntryObj.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
					}
					prodSeasonLogEntryObj.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_REQUEST_ID, String.valueOf(SMProductInboundFeedbackDataRequestClient.getFeedbackRequestID()));
					SMProductInboundUtil.persistInboundLogEntry(prodSeasonLogEntryObj);
				}else{
					LOGGER.error("PRODUCT SEASON LINK OBJECT ID in RESPONSE DOES NOT MATCH THAT IN LOG ENTRY");
					SMProductInboundFeedbackDataRequestClient.setFeedbackFailedCount(SMProductInboundFeedbackDataRequestClient.getFeedbackFailedCount()+1);
				}
			}
		}
	}

	/**
	 * Set Feeback on Log Entry.
	 * @param prodSeasonStatusResponseItem - ProductSeasonLinkInformationStatusResponseItem
	 * @throws WTException 
	 */
	public static void setColorwaySeasonFeedbackResponsetoLogEntry(List<ColorwaySeasonLinkInformationStatusResponseItem> prodSeasonStatusResponseItem) throws WTException{
		if(prodSeasonStatusResponseItem != null && prodSeasonStatusResponseItem.size() > 0){
			LOGGER.debug("RESPONSE FOR COLORWAY SEASON LINK INBOUND FEEDBACK  :  ");
			for(ColorwaySeasonLinkInformationStatusResponseItem prodFeedbackItem : prodSeasonStatusResponseItem){
				LOGGER.debug("Received Status Value for PLM ID >>>  "+prodFeedbackItem.getPlmId()+" and MDM ID  >>>  "+prodFeedbackItem.getMdmId()+"  is  >>>>  "+prodFeedbackItem.getRequestStatus());
				//System.out.println("map >>>>>"+getProductSeasonLinkFeedbackCollection());
				LCSLogEntry prodSeasonLogEntryObj = getColorwaySeasonLinkFeedbackCollection().get(prodFeedbackItem.getPlmId());
				//System.out.println("SKU Season Log Entry details  ::::: "+prodSeasonLogEntryObj.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID));
				if(prodSeasonLogEntryObj != null){
					prodSeasonLogEntryObj.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID, prodFeedbackItem.getPlmId());
					if(SMProductInboundWebServiceConstants.RECEIVED_VALID_FEEDBACK.equalsIgnoreCase(prodFeedbackItem.getRequestStatus())){
						LOGGER.info("received Valid  ......setting status to PROCESSED !!!!!!!!");
						prodSeasonLogEntryObj.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PROCESSED);
					}else{
						LOGGER.info("received invalid  ......setting status to PENDING !!!!!!!!");
						prodSeasonLogEntryObj.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
					}
					prodSeasonLogEntryObj.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_REQUEST_ID, String.valueOf(SMProductInboundFeedbackDataRequestClient.getFeedbackRequestID()));
					SMProductInboundUtil.persistInboundLogEntry(prodSeasonLogEntryObj);
				}else{
					LOGGER.error("COLORWAY SEASON LINK OBJECT ID in RESPONSE DOES NOT MATCH THAT IN LOG ENTRY");
					SMProductInboundFeedbackDataRequestClient.setFeedbackFailedCount(SMProductInboundFeedbackDataRequestClient.getFeedbackFailedCount()+1);
				}
			}
		}
	}
	
	private static String setItterationId(LCSLogEntry logEntry) throws WTException {
		String itterationIdVal=String.valueOf(logEntry.getValue(SMProductInboundWebServiceConstants.ITERATION_ID));
		
		if(FormatHelper.hasContent(itterationIdVal)) {
			return itterationIdVal;
		}
		
		return SMProductInboundWebServiceConstants.ITERATION_ID_DEFAULT_VAL;
	}
/**
 * @return the productSeasonLinkFeedbackCollection
 */
public static Map<String, LCSLogEntry> getProductSeasonLinkFeedbackCollection() {
	return productSeasonLinkFeedbackCollection;
}
/**
 * @param productSeasonLinkFeedbackCollection the productSeasonLinkFeedbackCollection to set
 */
public static void setProductSeasonLinkFeedbackCollection(
		Map<String, LCSLogEntry> productSeasonLinkFeedbackCollection) {
	SMProductInboundFeedbackLogEntryProcessor.productSeasonLinkFeedbackCollection = productSeasonLinkFeedbackCollection;
}
/**
 * @return the colorwaySeasonLinkFeedbackCollection
 */
public static Map<String, LCSLogEntry> getColorwaySeasonLinkFeedbackCollection() {
	return colorwaySeasonLinkFeedbackCollection;
}
/**
 * @param colorwaySeasonLinkFeedbackCollection the colorwaySeasonLinkFeedbackCollection to set
 */
public static void setColorwaySeasonLinkFeedbackCollection(
		Map<String, LCSLogEntry> colorwaySeasonLinkFeedbackCollection) {
	SMProductInboundFeedbackLogEntryProcessor.colorwaySeasonLinkFeedbackCollection = colorwaySeasonLinkFeedbackCollection;
}
}

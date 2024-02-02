package com.hbi.wc.interfaces.outbound.webservices.feedback;

import java.util.Collection;
import java.util.Map;

import com.hbi.wc.interfaces.outbound.webservices.util.HBIProperties;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLifecycleManagedHelper;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.LCSLog;

import wt.method.RemoteAccess;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBIMeasurementsFeedbackService.java
 * 
 * This class contains LCSMeasurements/LCSPointsOfMeasure transaction object status(completed/failed) & comments(issue details) update functions, which are invoking from LCSMeasurements
 * or LCSPointsOfMeasure services functions to perform create/update/delete of Library POM, Measurement Template, Grade Rules and Product-Measurement Set action based on SOAP request
 * @author Abdul.Patel@Hanes.com
 * @since  May-7-2015
 */
public class HBIMeasurementsFeedbackService implements RemoteAccess
{
	/**
	 * This function is using to get the transaction object for the given Map<String, Object>(contains transaction id, transaction status and transaction comments) and update for status
	 * @param hbiMeasurementsObjDataMap - Map<String, Object>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void updateMeasurementsTransactionStatus(Map<String, Object> hbiMeasurementsObjDataMap) throws WTException, WTPropertyVetoException
	{
		LCSLog.debug("### START HBIMeasurementsFeedbackService.updateMeasurementsTransactionStatus(hbiMeasurementsObjDataMap) ###");
		String hbiTransactionId = (String) hbiMeasurementsObjDataMap.get("hbiTransactionId");
		String hbiFlexObjectClassName = (String) hbiMeasurementsObjDataMap.get("hbiFlexObjectClassName");
		String hbiIntegrationStatus = (String) hbiMeasurementsObjDataMap.get("hbiIntegrationStatus");
		String measurementsFeedbackMessage = ""+(String) hbiMeasurementsObjDataMap.get("measurementsFeedbackMessage");
		
		//Calling a function to get Transaction BO for the given 'Transaction ID', update the Transaction BO to mark 'Transaction Status' and 'Transaction Comments' as per feedback
		LCSLifecycleManaged transactionObj = new HBIMeasurementsFeedbackService().getHBITransactionBusinessObjectForProcessingStatus(hbiTransactionId, hbiFlexObjectClassName);
		LCSLog.debug("### HBIMeasurementsFeedbackService.updateMeasurementsTransactionStatus(): Transaction BO updating from Feedback service is : "+ transactionObj);
		if(transactionObj != null)
		{
			transactionObj.setValue(HBIProperties.hbiCommentsKey, measurementsFeedbackMessage);
			transactionObj.setValue(HBIProperties.hbiTransactionStatusKey, hbiIntegrationStatus);
			//LCSLifecycleManagedHelper.getService().saveLifecycleManaged(transactionObj);										This code will work for FlexPLM 9.2 Version
			LCSLifecycleManagedHelper.service.saveLifecycleManaged(transactionObj);												//This code added as a part of Upgrade to 10.1
		}
		
		LCSLog.debug("### END HBIMeasurementsFeedbackService.updateMeasurementsTransactionStatus(hbiMeasurementsObjDataMap) ###");
	}
	
	/**
	 * This function is using to validate and return LCSLifecycleManaged object (contains Business Object from Integration\Outbound\Transaction BO) for the given FlexObject Class Name
	 * @param hbiTransactionId - String
	 * @param hbiFlexObjectClassName - String
	 * @return transactionObj - LCSLifecycleManaged
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public LCSLifecycleManaged getHBITransactionBusinessObjectForProcessingStatus(String hbiTransactionId, String hbiFlexObjectClassName) throws WTException
	{
		// LCSLog.debug("### START HBIIntegrationClientUtil.getHBITransactionBusinessObjectForProcessingStatus(hbiTransactionId, hbiFlexObjectClassName) ###");
		FlexType transactionObjectFlexType = FlexTypeCache.getFlexTypeFromPath(HBIProperties.hbiTransactionBOFlexType);
		String typeIdPath = String.valueOf(transactionObjectFlexType.getPersistInfo().getObjectIdentifier().getId());
		String flexObjectClassNameDBColumn = transactionObjectFlexType.getAttribute(HBIProperties.hbiFlexObjectClassNameKey).getColumnDescriptorName();
		String transactionIDDBColumn = transactionObjectFlexType.getAttribute(HBIProperties.hbiTransactionIdKey).getColumnDescriptorName();
		//String transactionStatusDBColumn = transactionObjectFlexType.getAttribute(HBIProperties.hbiTransactionStatusKey).getColumnDescriptorName();
		LCSLifecycleManaged transactionObj = null;
		
		//Initializing the PreparedQueryStatement, which is using to get LCSLifecycleManaged object based on the given set of parameters(FlexObject Class Name and Transaction ID)
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, flexObjectClassNameDBColumn), hbiFlexObjectClassName, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, transactionIDDBColumn), "?", "="), new Long(hbiTransactionId));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, "flexTypeReference.key.id"), "?", "="), new Long(typeIdPath));
		//statement.appendAndIfNeeded();
		//statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, transactionStatusDBColumn), "processing", Criteria.EQUALS));
		statement.setDistinct(true);
		
		// LCSLog.debug(" Query to fetch Processing Transaction BO to update Feedback comments : "+ statement);
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if(results != null && results.getResultsFound() > 0)
		{
			Collection<FlexObject> hbiTransactionObjectCollection = results.getResults();
			FlexObject flexObj = hbiTransactionObjectCollection.iterator().next();
			transactionObj = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+ flexObj.getString("LCSLIFECYCLEMANAGED.IDA2A2"));
		}
		
		// LCSLog.debug("### START HBIIntegrationClientUtil.getHBITransactionBusinessObjectForProcessingStatus(hbiTransactionId, hbiFlexObjectClassName) ###");
		return transactionObj;
	}
}
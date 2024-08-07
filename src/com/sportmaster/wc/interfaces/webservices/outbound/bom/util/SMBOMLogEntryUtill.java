package com.sportmaster.wc.interfaces.webservices.outbound.bom.util;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.util.WTException;

/**
 * @author BSC
 *
 */
public class SMBOMLogEntryUtill {
	
	/**
	 * public constructor.
	 */
	public SMBOMLogEntryUtill() {
		super();
	}

	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMBOMLogEntryUtill.class);
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
	private static final String LOG_ENTRY_IDA2A2 = "LCSLOGENTRY.IDA2A2";

	/**
	 * qriues logentry object according to logentry object type. 
	 * @param plmId - String
	 * @param triggeringObj - Object
	 */
	public LCSLogEntry getLogentry(String plmId) {
		
		LCSLogEntry logEntryObj = null;
		try{
			//Creating Statement.
			PreparedQueryStatement qryStmt = new PreparedQueryStatement();
			
			qryStmt.appendFromTable(LCSLogEntry.class);
			
			//append column
			qryStmt.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, IDA2A2);
			
			// adding criteria
			qryStmt.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,
					SMOutboundWebServiceConstants.FLEXTYPEIDPATH, getFlexType().getIdPath(), Criteria.EQUALS));

			qryStmt.appendAndIfNeeded();
			//adding plm id criteria
			qryStmt.appendCriteria(new Criteria(
					SMOutboundWebServiceConstants.LCSLOGENTRY, getFlexType()
							.getAttribute(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_PLM_ID).getColumnName(),
					plmId, Criteria.EQUALS));
			
			//executing  statement
			SearchResults qryResult =LCSQuery.runDirectQuery(qryStmt);
			FlexObject fo;
			for(Object qryResultObj:qryResult.getResults()) {
				fo= (FlexObject) qryResultObj;
				logEntryObj=(LCSLogEntry) LCSQuery.findObjectById(LOG_ENTRY_CLASS+fo.getString(LOG_ENTRY_IDA2A2));
				
			}
			
			return logEntryObj;
			
		}catch(WTException we){
			
			LOGGER.error("ERROR FOUND:-",we);
			return null;
			
		}
	}

	/**
	 * returns flextype according to response object type.
	 * @param obj - object class available from java lang package
	 * @return flextype object
	 * @throws WTException - the exception
	 */
	private FlexType getFlexType() throws WTException {
		
		FlexType flexType=FlexTypeCache.getFlexTypeFromPath(SMBOMOutboundWebServiceConstants.LOG_ENTRY_BOM_OUT_BOUND_PATH);

		return flexType;

	}
}

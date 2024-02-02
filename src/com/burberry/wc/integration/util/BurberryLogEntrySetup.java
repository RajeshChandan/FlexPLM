package com.burberry.wc.integration.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
/**
 * A Helper class to create Log Entry On
 * Failed Transactions.
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryLogEntrySetup {

	/**
	 * Default Constructor.
	 */
	private BurberryLogEntrySetup() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryLogEntrySetup.class);

	/**
	 * Method to Create a Log Entry.
	 * 
	 * @param statusCode
	 *            statusCode
	 * @param errorMessage
	 *            errorMessage
	 * @param parameters
	 *            parameters
	 * @param lStartTime
	 *            lStartTime
	 * @param flexTypePath
	 *            flexTypePath
	 * @throws WTException
	 *             WTException
	 * @throws WTPropertyVetoException
	 *             WTPropertyVetoException
	 */
	public static void createLogEntry(int statusCode, String errorMessage,
			MultivaluedMap<String, String> parameters, long lStartTime,
			String flexTypePath) throws WTException, WTPropertyVetoException {
		String methodName = "createLogEntry() ";

		// Log Entry Code: Start
		if (BurConstant.LOG_ENTRY_FAILED_ENABLED.equalsIgnoreCase("true")) {
			// Initialisation
			LCSLogEntry logEntry = new LCSLogEntry();
			DateFormat formatter = new SimpleDateFormat(BurConstant.dateFormat);

			logger.info(methodName + " Status Code: " + statusCode
					+ " Start Date: " + formatter.format(lStartTime)
					+ " Parameters: " + parameters.toString() + " Message: "
					+ errorMessage);

			// Get Flex Type Path
			FlexType logEntryType = FlexTypeCache
					.getFlexTypeFromPath(flexTypePath);
			// Set the values
			logEntry.setFlexType(logEntryType);
			logEntry.setValue(BurConstant.LOG_ENTRY_BEGIN_DATE,
					formatter.format(lStartTime));
			logEntry.setValue(BurConstant.LOG_ENTRY_END_DATE,
					formatter.format(new Date()));
			logEntry.setValue(BurConstant.LOG_ENTRY_PARAMETERS_PASSED,
					parameters.toString());
			// if (statusCode == 200) {
			// logEntry.setValue("burStatus", "burSuccess");
			// } else {
			logEntry.setValue(BurConstant.LOG_ENTRY_STATUS, "burFailure");
			// }
			logEntry.setValue(BurConstant.LOG_ENTRY_STATUS_MESSAGE, statusCode
					+ " " + errorMessage);
			logEntry.setValue(BurConstant.LOG_ENTRY_TOTAL_TIME,
					System.currentTimeMillis() - lStartTime);
			// Save Log Entry
			new LCSLogEntryLogic().saveLog(logEntry);
		}
		// Log Entry Code: End

	}
}

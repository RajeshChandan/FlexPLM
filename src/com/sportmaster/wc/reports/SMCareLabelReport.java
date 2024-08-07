package com.sportmaster.wc.reports;

import java.util.Map;

import org.apache.log4j.Logger;
/**
 * The Class SMCareLabelReport.
 *
 * @version 'true' 1.0 version number.
 * @author 'true' ITC.
 */
public final class SMCareLabelReport {
	/**
	 * Constant LOGGER
	 */
	private static final Logger LOGGER = 
			Logger.getLogger("CARELABELREPORTLOG");
	/**
	 * private constructor
	 */
	private SMCareLabelReport(){
		
	}
	
	/**
	 * @param context - context
	 * @param inputSelectedMap - inputSelectedMap
	 * @return SMCareLabelReportBean - SMCareLabelReportBean
	 */
	public static SMCareLabelReportBean execute(
			com.lcs.wc.client.ClientContext context,
			Map<String, Object> inputSelectedMap) {
		LOGGER.debug("SMCareLabelReportBean - execute - Start");
		// Call runReportQuery method to get the report data
		SMCareLabelReportBean reportBean = SMCareLabelReportQuery
				.runReportQuery(context, inputSelectedMap);
		LOGGER.debug("SMCareLabelReportBean - execute - End");
		return reportBean;
	}
}

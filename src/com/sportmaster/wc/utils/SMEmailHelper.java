package com.sportmaster.wc.utils;

import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.lcs.wc.util.EmailHelper;
import com.lcs.wc.util.LCSProperties;

import wt.org.WTPrincipal;
import wt.org.WTUser;

/**
 * SMEmailHelper.
 * 
 * The Send_Email class contains methods to send Email notifications.
 * 
 * @version 1.0
 */
public class SMEmailHelper {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMEmailHelper.class);

	/**
	 * Constructor.
	 */
	public SMEmailHelper() {
		// constructor
	}

	/**
	 * sendEmail.
	 * 
	 * @param strFromMailId for strFromMailId
	 * @param strTo         for strTo
	 * @param strMailBody   for strMailBody
	 * @param strSubject    for strSubject
	 * @return void
	 */
	public void sendEmail(WTUser strFromMailId, Collection<WTPrincipal> strTo, String strMailBody, String strSubject,
			String mailHeader) {
		LOGGER.info("start - Inside CLASS--SMEmailHelper and METHOD--sendEmail");
		try {
			String systemInfo = LCSProperties.get("com.sportmaster.wc.utils.sendEmail.SystemInfo");
			StringBuilder sb = new StringBuilder();
			sb.append(mailHeader).append(strMailBody).append(systemInfo);
			String totalContent = sb.toString();
			EmailHelper eH = new EmailHelper();
			LOGGER.debug("strTo=bef send mail=="+strTo);
			eH.sendMail(new Vector( strTo), strFromMailId, totalContent, strSubject);
			eH.send();
			LOGGER.debug(" new Vector(strTo) after sending mail=="+new Vector( strTo));
		} catch (Exception e) {
			LOGGER.error("WTException in sendEmail Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMEmailHelper and METHOD--sendEmail");
	}
}
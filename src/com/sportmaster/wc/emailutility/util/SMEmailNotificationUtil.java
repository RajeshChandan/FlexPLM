package com.sportmaster.wc.emailutility.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.util.EmailHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;

import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

public class SMEmailNotificationUtil {
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMEmailNotificationUtil.class);

	/**
	 * Queries tasks that are created from prev date to current date and returns the
	 * open task collection
	 * 
	 * @param strTaskName
	 * @param prevDate
	 * @return
	 * @throws WTException
	 */
	public static ArrayList findOpenTasks(String strTaskName, Timestamp prevDate) throws WTException {
		LOGGER.debug("## SMEmailNotificationUtil.findOpenTasks method - START ####");

		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(WorkItem.class);
		statement.appendFromTable(WfAssignedActivity.class);
		// Add this line
		statement.appendFromTable(WfAssignedActivityTemplate.class);
		statement.appendSelectColumn(new QueryColumn(WorkItem.class, "primaryBusinessObject.key.classname"));
		// statement.appendSelectColumn(new QueryColumn(WorkItem.class, "role"));
		statement.appendSelectColumn(new QueryColumn(WorkItem.class, SMEmailUtilConstants.OBJECT_ID));
		statement.appendSelectColumn(new QueryColumn(WfAssignedActivity.class, "name"));
		statement.appendSelectColumn(new QueryColumn(WfAssignedActivity.class, SMEmailUtilConstants.OBJECT_ID));
		statement.appendSelectColumn(new QueryColumn(WorkItem.class, "thePersistInfo.createStamp"));

		statement.appendAndIfNeeded();
		// to query only open tasks
		statement.appendOpenParen();
		statement.appendCriteria(new Criteria(new QueryColumn(WorkItem.class, "status"), "POTENTIAL", Criteria.EQUALS));

		statement.appendOrIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(WorkItem.class, "status"), "ACCEPTED", Criteria.EQUALS));
		statement.appendClosedParen();
		// Task Name criteria
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(WfAssignedActivity.class, "name"), strTaskName, Criteria.LIKE));

		// query only tasks created from prev time stamp to current time
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(WorkItem.class, "thePersistInfo.createStamp"), "?",
				Criteria.GREATER_THAN_EQUAL), prevDate);

		statement.appendJoin(new QueryColumn(WorkItem.class, "source.key.id"),
				new QueryColumn(WfAssignedActivity.class, SMEmailUtilConstants.OBJECT_ID));
		// Add this line
		statement.appendJoin(new QueryColumn(WfAssignedActivity.class, "template.key.id"),
				new QueryColumn(WfAssignedActivityTemplate.class, SMEmailUtilConstants.OBJECT_ID));

		SearchResults results = LCSQuery.runDirectQuery(statement);
		LOGGER.debug("Query statement=" + statement);

		ArrayList worklist = new ArrayList(results.getResults());
		LOGGER.debug("## SMEmailNotificationUtil.findOpenTasks method - END ####");
		return worklist;
	}

	/**
	 * this method fetches the the timing from which the tasks are to be sent for
	 * notificaiton
	 * 
	 * @param timezone
	 * @param pickTasksFromLastHours
	 * @return
	 */
	public static Timestamp getPrevDate(String timezone, Long pickTasksFromLastHours) {
		LOGGER.debug("## SMEmailNotificationUtil.getPrevDate method - START ####");
		// Declare a null timestamp
		Timestamp timeStamp = null;
		// Get the current calendar date/time
		Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone(timezone));
		LOGGER.debug("currentTime=" + currentTime);
		timeStamp = new Timestamp(currentTime.getTimeInMillis() - pickTasksFromLastHours * 60 * 60 * 1000);
		LOGGER.debug("## SMEmailNotificationUtil.getPrevDate method - END ####");
		return timeStamp;
	}

	/**
	 * get list of tasks from property entry
	 * 
	 * @param taskProperties
	 * @return
	 */
	public static HashMap getTasksFromProperties(String taskProperties) {
		LOGGER.debug("## SMEmailNotificationUtil.taskProperties method - START ####");
		HashMap hmTasks = new HashMap();
		ArrayList vTasks = (ArrayList) new ArrayList(MOAHelper.getMOACollection(taskProperties));
		Iterator italTasks = vTasks.iterator();
		StringTokenizer stTask;
		while (italTasks.hasNext()) {
			String task = (String) italTasks.next();
			stTask = new StringTokenizer(task, "||");
			hmTasks.put(stTask.nextElement(), stTask.nextElement());
		}
		LOGGER.debug("## SMEmailNotificationUtil.taskProperties method - END ####");
		return hmTasks;
	}

	/**
	 * Method to get the users attributes/ group for whom notificaion will be sent
	 * Written as generic method to handle both users and groups
	 * 
	 * @param strUserGroupProperty
	 * @return
	 */
	public static HashMap getUserAttributes(String strUserGroupProperty) {
		LOGGER.debug("## SMEmailNotificationUtil.getUserAttributes method - START ####");
		HashMap hmUserGroupMap = new HashMap();
		String str1 = "";
		String str2 = "";
		// EX:
		// user^LCSProductSeasonLink$vrdDesigner|~*~|LCSProductSeasonLink$vrdDesigner##group^Brand
		// Manager|~*~|Design Lead|~*~|
		// Tokenize using "##"
		StringTokenizer stUserGroups = new StringTokenizer(strUserGroupProperty, "##");
		// First token will be for user
		if (stUserGroups.hasMoreElements()) {
			str1 = stUserGroups.nextToken();
		}
		// Second token will be for groups
		if (stUserGroups.hasMoreElements()) {
			str2 = stUserGroups.nextToken();
		}
		// If users are present, split with ^ to get string "user" and attribute key
		if (FormatHelper.hasContent(str1)) {
			StringTokenizer stUserArray = new StringTokenizer(str1, "^");
			hmUserGroupMap.put(stUserArray.nextElement(),
					new ArrayList(MOAHelper.getMOACollection((String) stUserArray.nextElement())));
		}
		// If users are present, split with ^ to get string "group" and group name
		if (FormatHelper.hasContent(str2)) {
			StringTokenizer stGroupArray = new StringTokenizer(str2, "^");
			hmUserGroupMap.put(stGroupArray.nextElement(),
					new ArrayList(MOAHelper.getMOACollection((String) stGroupArray.nextElement())));
		}
		LOGGER.debug("## SMEmailNotificationUtil.getUserAttributes method - END ####");
		return hmUserGroupMap;
	}

	/**
	 * Method to get the user list attribute value and flexobject
	 * 
	 * @param wtObj
	 * @param att
	 * @param keyTaskNo
	 * @param strUserToExclude
	 * @return
	 */
	public static List getUser(WTObject wtObj, String att) {
		LOGGER.debug("## SMEmailNotificationUtil.getUser method - START ####");
		List<Object> userList = new ArrayList<Object>();
		FlexObject fo = null;
		String userAttName = "";
		WTUser user = null;

		try {
			if (wtObj instanceof LCSSeasonProductLink) {
				fo = (FlexObject) ((LCSSeasonProductLink) wtObj).getValue(att);
			}
			// START Phase 13 - SEPD Product Sample Workflow. This loop is added to get
			// attribute from source to season link object.
			else if (wtObj instanceof LCSSourceToSeasonLink) {
				fo = (FlexObject) ((LCSSourceToSeasonLink) wtObj).getValue(att);
			}
			// END Phase 13 - SEPD Product Sample Workflow.
			if (fo != null && fo.containsKey(SMEmailUtilConstants.FO_NAME)
					&& FormatHelper.hasContent((String) fo.getData(SMEmailUtilConstants.FO_NAME))) {
				userAttName = (String) fo.getData(SMEmailUtilConstants.FO_NAME);

				if (FormatHelper.hasContent(userAttName)) {
					try {
						user = wt.org.OrganizationServicesHelper.manager.getUser(userAttName);
						userList.add(user);
						userList.add(userAttName);

					} catch (WTException e) {
						LOGGER.error("## WTException in SMEmailNotificationUtil.getUser method - " + e.getMessage());
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (WTException e1) {
			LOGGER.error("## WTException in SMEmailNotificationUtil.getUser method - " + e1.getMessage());
			e1.printStackTrace();
		}

		LOGGER.debug("## SMEmailNotificationUtil.getUser method - END ####");
		return userList;
	}

	/**
	 * generic method to get object display values for list attributes
	 * 
	 * @param wtObj
	 * @param strAtt
	 * @param ft
	 * @return
	 */
	public static String getObjectValue(WTObject wtObj, String strAtt, FlexType ft) {
		LOGGER.debug("## SMEmailNotificationUtil.getObjectValue method - START ####");
		String strAttValue = "";
		try {
			if (wtObj instanceof LCSProduct) {
				strAttValue = (String) ft.getAttribute(strAtt).getDisplayValue(((LCSProduct) wtObj));
			} else if (wtObj instanceof LCSProductSeasonLink) {
				strAttValue = (String) ft.getAttribute(strAtt).getDisplayValue(((LCSProductSeasonLink) wtObj));
			}
			// START Phase 13 - FPD & SEPD Product Sample Workflow. This loop is added to
			// get sample object display values for list attributes.
			else if (wtObj instanceof LCSSample) {
				strAttValue = (String) ft.getAttribute(strAtt).getDisplayValue(((LCSSample) wtObj));
			}
			// END Phase 13 - FPD & SEPD Product Sample Workflow.
		} catch (WTException e) {
			LOGGER.error("## WTException in SMEmailNotificationUtil.getObjectValue method - " + e.getMessage());

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.debug("strAtt=" + strAtt);
		LOGGER.debug("strAttValue=" + strAttValue);
		LOGGER.debug("## SMEmailNotificationUtil.getObjectValue method - END ####");
		return strAttValue;
	}

	/**
	 * Method to send email to users
	 * 
	 * @param strFromMailId
	 * @param strTo
	 * @param strMailBody
	 * @param strSubject
	 * @param mailHeader
	 * @param systemInfo
	 */
	public void sendEmail(WTUser strFromMailId, ArrayList strTo, String strMailBody, String strSubject,
			String mailHeader, String systemInfo) {
		LOGGER.info("start - Inside CLASS--SMEmailNotificationUtil and METHOD--sendEmail");
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(mailHeader).append(strMailBody).append(systemInfo);
			String totalContent = sb.toString();
			LOGGER.debug("Final Message=" + totalContent);
			EmailHelper eH = new EmailHelper();
			eH.sendMail(new Vector(strTo), strFromMailId, totalContent, strSubject);
			eH.send();
		} catch (Exception e) {
			LOGGER.error("WTException in sendEmail Method -" + e.getMessage());
			// e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMEmailNotificationUtil and METHOD--sendEmail");
	}

	/**
	 * Method to get user list attribute value
	 * 
	 * @param spLink
	 * @param string
	 * @return
	 */
	public static String getUserToExcludeNotification(WTObject spLink, String strAtt) {
		LOGGER.debug("## SMEmailNotificationUtil.getUserToExcludeNotification method - START ####");
		LOGGER.debug("User Attribute to exclude (if same as defined in send notification)= " + strAtt);
		FlexObject fo = null;
		String userAttNameValue = "";
		WTUser user = null;
		try {
			if (spLink instanceof LCSSeasonProductLink) {
				fo = (FlexObject) ((LCSSeasonProductLink) spLink).getValue(strAtt);
			}
			if (fo != null && fo.containsKey(SMEmailUtilConstants.FO_NAME)
					&& FormatHelper.hasContent((String) fo.getData(SMEmailUtilConstants.FO_NAME))) {
				userAttNameValue = (String) fo.getData(SMEmailUtilConstants.FO_NAME);
			}
		} catch (WTException e1) {
			LOGGER.error("## WTException in SMEmailNotificationUtil.getUserToExcludeNotification method - "
					+ e1.getMessage());
			e1.printStackTrace();
		}
		LOGGER.debug("User Attribute value to exclude (if same as defined in send notification)= " + userAttNameValue);
		LOGGER.debug("## SMEmailNotificationUtil.getUserToExcludeNotification method - END ####");
		return userAttNameValue;
	}
}

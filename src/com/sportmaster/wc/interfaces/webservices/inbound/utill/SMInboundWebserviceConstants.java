package com.sportmaster.wc.interfaces.webservices.inbound.utill;

import com.lcs.wc.util.LCSProperties;
/**
 * SMInboundWebserviceConstants.java
 * This class is using for constants from the property file.
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMInboundWebserviceConstants {


	/**
	 * constructor.
	 */
	public SMInboundWebserviceConstants() {
		super();
	}

	/**
	 * EXECUTE.
	 */
	public static final String EXECUTE=LCSProperties.get("com.sportmaster.wc.interfaces.queue.action.EXECUTE");
	/**
	 * SCHEDULE.
	 */
	public static final String SCHEDULE=LCSProperties.get("com.sportmaster.wc.interfaces.queue.action.SCHEDULE");

	/**
	 * Declaration constants for Queue start ERROR CODE.
	 */
	public static final String QUEUE_START_ERROR_CODE=LCSProperties.get("com.wc.sm.intefaces.errorcode.queuestart");

	/**
	 * Declaration constants for Queue RESCHEDULE ERROR CODE.
	 */
	public static final String QUEUE_RESCHEDULE_ERROR_CODE=LCSProperties.get("com.wc.sm.intefaces.errorcode.queuereschedule");
	/**
	 * Declaration constants for web service connectivity ERROR CODE.
	 */
	public static final String WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE=LCSProperties.get("com.wc.sm.intefaces.errorcode.webservice.inbound.conectivity");
	/**
	 * Declaration constants for web service schema ERROR CODE.
	 */

	public static final String WEBSERVICE_INBOUND_SCHEMA_ERROR_CODE=LCSProperties.get("com.wc.sm.intefaces.errorcode.webservice.inbound.schema");

	/**
	 * Declaration constants for web service connectivity ERROR CODE.
	 */
	public static final String WEBSERVICE_OUTBOUND_CONNECTIVITY_ERROR_CODE=LCSProperties.get("com.wc.sm.intefaces.errorcode.webservice.outbound.conectivity");
	/**
	 * Declaration constants for web service schema ERROR CODE.
	 */

	public static final String WEBSERVICE_OUTBOUND_SCHEMA_ERROR_CODE=LCSProperties.get("com.wc.sm.intefaces.errorcode.webservice.outbound.schema");

	/**
	 * Declaration constants forWEBSERVICE_OUTBOUND_TIMEOUT_ERROR_CODE.
	 */

	public static final String WEBSERVICE_OUTBOUND_TIMEOUT_ERROR_CODE=LCSProperties.get("com.wc.sm.intefaces.errorcode.webservice.outbound.timeout");

	/**
	 * Declaration constants forWEBSERVICE_INBOUND_TIMEOUT_ERROR_CODE.
	 */

	public static final String WEBSERVICE_INBOUND_TIMEOUT_ERROR_CODE=LCSProperties.get("com.wc.sm.intefaces.errorcode.webservice.inbound.timeout");

	/**
	 * Declaration constants for QUEUE START ERROR CODE.
	 */
	public static final String QUEUE_START_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.interfaces.queue.starterrormassage");

	/**
	 * Declaration constants for Queue RESCHEDULE ERROR CODE message.
	 */
	public static final String QUEUE_RESCHEDULE_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.interfaces.queue.rescheduleerrormassage");

	/**
	 * Declaration constants for QUEUE START ERROR CODE.
	 */
	public static final String WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.interfaces.webservice.connectivity.commonmassage");
	/**
	 * Declaration constants for WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE.
	 */
	public static final String WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.interfaces.webservice.schema.commonmassage");

	/**
	 * Declaration constants for WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE.
	 */
	public static final String WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.interfaces.webservice.timeout.commonmassage");

	/**
	 * Declaration constants for Client Transport Error.
	 */
	public static final String CLIENT_TRANSPORT_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.interfaces.webservice.ClientTransportException");
	/**
	 * Declaration constants for Fault Error.
	 */
	public static final String SOAP_FAULT_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.interfaces.webservice.SOAPFaultException");
	/**
	 * Declaration constants for SSOAP Fault Error.
	 */
	public static final String SERVER_SOAP_FAULT_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.interfaces.webservice.ServerSOAPFaultException");
	/**
	 * Declaration constants for WEB_SERVICE_TIMEOUT_ERROR_MESSAGE.
	 */
	public static final String WEB_SERVICE_TIMEOUT_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.interfaces.webservice.WebServiceException");
	/**
	 * Declaration for private ListValue_QUEUENAME attribute.
	 */
	public static final String LIST_VALUES_DATA_REQUEST_QUEUE_NAME= LCSProperties.get("com.sportmaster.wc.interfaces.inbound.listvaluesdatarequest.queuename");

	/**
	 * Declaration constants for list Enumeration VAlue.
	 */
	public static final String LIST_VALUES_ENUM_VALUES=LCSProperties.get("com.sportmaster.wc.interfaces.outbound.listvalues.type");

	/**
	 * Declaration constants for BO ida2a2.
	 */
	public static final String BUSINESS_OBJECT_IDA2A2 = "LCSLIFECYCLEMANAGED.IDA2A2";

	/**
	 * Declaration constants for lifecyclemanaged.
	 */
	public static final String BUSINESS_OBJECT_CLASS_NAME = "LCSLIFECYCLEMANAGED";

	/**
	 * Declaration constants for list values request type.
	 */
	public static final String LIST_VALUES_INTEGRATION_TYPES=LCSProperties.get("com.sportmaster.wc.interfaces.inbound.listvaluesdatarequest.types");
	/**
	 * Declaration constants for list values integration filed.
	 */
	public static final String LIST_VALUES_INTEGRATION_FILEDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.attributes");
	/**
	 * INTEGRATION_FIELDS.
	 */
	public static final String LOG_ENTRY_LIST_VALUES_INTEGRATION_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.outbound.listvaluesfeedback.Attributes");

	/**
	 * Declaration constants for Class ENUM Value.
	 */
	public static final String BO_CLASS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.Class");
	/**
	 * Declaration constants for Sub Class ENUM Value.
	 */
	public static final String BO_SUBCLASS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.SubClass");
	/**
	 * Declaration constants for Category ENUM Value.
	 */
	public static final String BO_CATEGORY =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.Category");
	/**
	 * Declaration constants for Sub Category ENUM Value.
	 */
	public static final String BO_SUBCATEGORY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.SubCategory");
	/**
	 * Declaration constants for Group of merchandise ENUM Value.
	 */
	public static final String BO_GOM =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.GoM");
	/**
	 * Declaration constants for SubClass BO type.

	 */
	public static final String SUBCLASS_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.Sub_Class");
	/**
	 * Declaration constants for SubCategory BO type.
	 */
	public static final String SUBCATEGORY_TYPE =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.Sub_Category");
	/**
	 * Declaration constants for Group of merchandise BO type.
	 */
	public static final String GOM_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.GroupOfMerchandise");

	/**
	 * Declaration constants for LogEntry List value type.
	 */
	public static final String LOGENTRY_LISTVALUE_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.LOGENTRY_LISTVALUE_NMAE");

	/**
	 * Declaration constants for List value status as active.
	 */
	public static final String STATUS_ACTIVE_KEY =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smActive");
	/**
	 * Declaration constants for List value status as in active.
	 */
	public static final String STATUS_INACTIVE_KEY =  LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smInactive");
	/**
	 * Declaration constants for List value update.
	 */
	public static final String BO_UPDATE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.UPDATE");

	/**
	 * Declaration constants for List value delete.
	 */
	public static final String BO_DELETE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.DELETE");
	/**
	 * Declaration constants for List value Status.
	 */
	public static final String LOGENTRY_LIST_VALUE_STATUS_KEY= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smStatus");

	/**
	 * Declaration constants for List value Status not sent.
	 */
	public static final String LOGENTRY_STATUS_VALUE_NOTSENT= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smNotSent");
	/**
	 * Declaration constants for List value Status  sent.
	 */
	public static  final String LOGENTRY_STATUS_VALUE_SENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.Processed");

	/**
	 * Declaration constants for List value integrated.
	 */
	public static final String LOGENTRY_LIST_VALUE_INTEGRATED =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.INTEGRATED");


	/**
	 * Declaration constants for List value Status not integrated.
	 */
	public static final String LOGENTRY_LIST_VALUE_NOT_INTEGRATED =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.NO_INTEGRATED");
	/**
	 * Declaration constants for List value Status not integrated.
	 */

	public static final String LOGENTRY_LIST_VALUE_NMAE_KEY =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smName");

	/**
	 * Declaration constants for List value Status not integrated.
	 */
	public static final String LOGENTRY_LIST_VALUE_MDMID_KEY =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smMDMId");
	/**
	 * Declaration constants for List value  object Id.
	 */
	public static final String LOGENTRY_LIST_VALUE_OBJECTID_KEY =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smObjectId");
	/**
	 * Declaration constants for List value business object type.
	 */
	public static final String LOGENTRY_LIST_VALUE_BUSSINESS_OBJECT_TYPE =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.logentry.businessobjecttype");
	/**
	 * Declaration constants for List value  request type.
	 */
	public static final String LOGENTRY_LIST_VALUE_REQUEST_TYPE =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.logentry.requesttype");

	/**
	 * Declaration constants for List value Status not integrated.
	 */
	public static final String LOGENTRY_LIST_VALUE_INT_STATUS_KEY =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smIntegrationStatus");
	/**
	 * Declaration constants for List value ERROR
	 */
	public static final String LOGENTRY_LIST_VALUE_ERROR_KEY =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smErrorReason");
	/**
	 * Declaration constants for List value NAME.
	 */
	public static final String BO_NAME =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.name");
	/**
	 * Declaration constants for List value MDMBO.
	 */
	public static final String BO_MDMID =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smMDMBO");
	/**
	 * Declaration constants for List value Business Object\\Class.
	 */
	public static final String LIST_VALUE_BO_CLASS =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.LIST_VALUE_BO_CLASS");
	/**
	 * Declaration constants for List value Business Object\\Sub-Class.
	 */
	public static final String LIST_VALUE_BO_SUBCLASS = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.LIST_VALUE_BO_SUBCLASS");
	/**
	 * Declaration constants for List value Business Object\\Category.
	 */
	public static final String LIST_VALUE_BO_CATEGORY =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.LIST_VALUE_BO_CATEGORY");
	/**
	 * Declaration constants for List value Business Object\\Sub-Category.
	 */
	public static final String LIST_VALUE_BO_SUBCATEGORY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.LIST_VALUE_BO_SUBCATEGORY");
	/**
	 * Declaration constants for List value Business Object\\Group Of Merchandise.
	 */
	public static final String LIST_VALUE_BO_GOM =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.LIST_VALUE_BO_GOM");

	/**
	 * Declaration constants for Business object Status.
	 */
	public static final String BO_STATUS_ENUM  =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smBOStatus");
	/**
	 * Declaration constants for LCSLIFECYCLEMANAGED MDM ID.
	 */
	public static final String LCSLIFECYCLEMANAGED_MDMID_COLUMN_NAME = "LCSLIFECYCLEMANAGED.PTC_STR_10TYPEINFOLCSLIFECYC";
	/**
	 * Declaration constants for Subdivision tree log entry.
	 */
	public static final String LOGENTRY_SUBDIVISIONTREE_NMAE =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.LOGENTRY_SUBDIVISIONTREE_NMAE");
	/**
	 * Declaration constants for Subdivision tree log entry.
	 */

	public static final String LOGENTRY_SUBDIVISIONTREE_ATTRIBUTE =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.LOGENTRY_SUBDIVISIONTREE_ATTRIBUTE");
	/**
	 * Declaration constants for list value xml file location.
	 */
	public static final String LISTVALUES_FILE_LOCATION =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.listvaluesfilelocation");
	/**
	 * Declaration constants for received invalid.
	 */
	public static final String RECEIVED_INVALID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.RECEIVED_INVALID");
	/**
	 * Declaration constants for request Id.
	 */
	public static final String REQUEST_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.smRequestId");
	/**
	 * Declaration constants for request Id constant value.
	 */
	public static final String REQUEST_ID_CONST_VALUE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.requestIdConstantValue");
	/**
	 * Declaration constants for feedback file location.
	 */
	public static final String FEEDBACK_FILE_LOCATION=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvaluesfeedback.feedbackfilelocation");
	/**
	 * Declaration constants for time zone.
	 */
	public static final String TIME_ZONE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.timeZone");
	/**
	 * Declaration constants for list value schedule time.
	 */
	public static final String LISTVALUES_SCHEDULE_TIME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.time");
	/**
	 * Declaration constants for Subdivision tree log entry.
	 */
	public static final String LISTVALUES_SCHEDULE_TIME_AM_PM=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvalues.util.AM_PM");
	/**
	 * Declaration constants for feedback queue schedule time.
	 */
	public static final String FEEDBACK_SCHEDULE_TIME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.feedback.util.time");
	/**
	 * Declaration constants for Subdivision tree log entry.
	 */
	public static final String FEEDBACK_SCHEDULE_TIME_AM_PM=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.feedback.util.AM_PM");

	/**
	 * Declaration constants for feedback queue name.
	 */
	private String listValuesDataFeedbackQueueName=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.feedback.queuename");

	/**
	 * @return the listValuesDataFeedbackQueueName
	 */
	public String getListValuesDataFeedbackQueueName() {
		return listValuesDataFeedbackQueueName;
	}

	/**
	 * LIST_VALUES_QUEUE_INTERVAL.
	 */
	public static final long LIST_VALUES_QUEUE_INTERVAL= LCSProperties.get("com.sportmaster.wc.interfaces.inbound.listvaluesdatarequest.queueinterval", 480);
	/**
	 * FEEDBACK_QUEUE_INTERVAL.
	 */
	public static final long FEEDBACK_QUEUE_INTERVAL=LCSProperties.get("com.sportmaster.wc.interfaces.inbound.feedback.queueinterval", 480);
	/**
	 * SUBCLASS_TRIMEOUT.
	 */
	public static final int SUBCLASS_TIMEOUT= LCSProperties.get("com.sportmaster.wc.interfaces.inbound.feedback.subclasstimeout", 180000);
	/**
	 * CLASS_TRIMEOUT.
	 */
	public static final int CLASS_TIMEOUT= LCSProperties.get("com.sportmaster.wc.interfaces.inbound.feedback.classtimeout", 180000);
	/**
	 * SUBCATEGORY_TRIMEOUT.
	 */
	public static final int SUBCATEGORY_TIMEOUT= LCSProperties.get("com.sportmaster.wc.interfaces.inbound.feedback.subcategorytimeout", 180000);
	/**
	 * CATEGORY_TRIMEOUT.
	 */
	public static final int CATEGORY_TIMEOUT= LCSProperties.get("com.sportmaster.wc.interfaces.inbound.feedback.categorytimeout", 180000);
	/**
	 * GOM_TRIMEOUT.
	 */
	public static final int GOM_TIMEOUT= LCSProperties.get("com.sportmaster.wc.interfaces.inbound.feedback.gomtimeout", 180000);

	/**
	 * NOT_VAILD_MDMBO.
	 */

	public static final String NOT_VAILD_MDMBO= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.processor.ERROR_MDMID_BO");
	/**
	 * BO_NOTFOUND.
	 */

	public static final String BO_NOTFOUND= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.processor.ERROR_BO_NOTFOUND");


	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Sub Division Tree Constants

	/////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Path for Business Object/Sub Class Division Tree
	 */
	public static final String strSubDivTreePath = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.subDivTreePath");
	/**
	 * Path for Log Entry/Sub Class Division Tree
	 */
	public static final String logEntrySubDivisionPath = LCSProperties
			.get("com.sm.wc.interface.LCSLifeCycleManaged.logEntrySubDivisionPath");
	/**
	 * Sub Division Interface Name.
	 */
	public static final String SUB_DIVISION_INTERFACE_NAME = LCSProperties
			.get("com.sm.wc.interface.LCSLifeCycleManaged.subDivsionTreeInterfaceName");
	/**
	 * Path for Business Object/Category
	 */
	static String strCategoryPath = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.categoryPath");
	/**
	 * Path for Business Object/Class
	 */
	static String strClassPath = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.classPath");
	/**
	 * Path for Business Object/Sub Category
	 */
	static String strSubCategoryPath = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.subCategoryPath");
	/**
	 * Path for Business Object/Sub Class
	 */
	static String strSubClassPath = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.subClassPath");
	/**
	 * Path for Business Object/GOM
	 */
	static String strGOMPath = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.gomPath");
	/**
	 * Path for Log Entry/Inbound/Sub Class Division Tree
	 */
	public static final String strLogEntryPath = LCSProperties
			.get("com.sm.wc.interface.LCSLifeCycleManaged.logEntryInboundSubClassDivTreePath");
	/**
	 * Internal name for attribute MDMID
	 */
	public static final String strMDMID = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.mdmID");
	/**
	 * Internal name for attribute name
	 */
	public static final String SUB_DIVISION_BO_NAME = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.subDivTreeName");

	/**
	 * Internal name for attribute Product Class on Business Object/Sub Class Division Tree
	 */
	public static final String strClassName = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.class");
	/**
	 * Internal name for attribute Sub Class on Business Object/Sub Class Division Tree
	 */
	public static final String strSubClassName = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.subClass");
	/**
	 * Internal name for attribute Product GoM on Business Object/Sub Class Division Tree
	 */
	public static final String strGomName = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.gom");
	/**
	 * Internal name for attribute Product Category on Business Object/Sub Class Division Tree
	 */
	public static final String strCategoryName = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.category");
	/**
	 * Internal name for attribute Product Sub Category on Business Object/Sub Class Division Tree
	 */
	public static final String strSubCategoryName = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.subCategory");
	/**
	 * Internal name for attribute GUID on Business Object/Sub Class Division Tree
	 */
	static String strGuiID = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.guiID");
	/**
	 * Internal name for attribute Status on Business Object/Sub Class Division Tree
	 */
	public static final String strStatus = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.subDivTreeStatus");
	/**
	 * Internal name for list Value Active for Status attribute on Business Object/Sub Class Division Tree
	 */
	public static final String STATUS_ACTIVE = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.statusActive");
	/**
	 * Internal name for list Value Inactive for Status attribute on Business Object/Sub Class Division Tree
	 */
	public static final String STATUS_INACTIVE = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.statusInactive");
	/**
	 * Class name for LCSLifeCycleManaged, used in DB query
	 */
	public static final String LCSLIFECYCLE_CLASS_NAME = LCSProperties
			.get("com.sm.wc.interface.LCSLifeCycleManaged.lcsLifeCycleManagedClassName");
	/**
	 * Error string for Empty or Null Object Reference
	 */
	public static final String EMPTY_VALUE_ERROR = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.nullMsg");
	/**
	 * Error string if Object Reference does not exist
	 */
	public static final String OBJ_REFERENCE_ABSENT = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.objRefAbsent");
	/**
	 * Request Type Update
	 */
	public static final String UPDATE_REQUEST = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.updateRequest");
	/**
	 * Request Type Delete
	 */
	public static final String DELETE_REQUEST = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.deleteRequest");
	/**
	 * Integration Status - Successful
	 */
	public static final String STATUS_INTEGRATED = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.statusIntegrated");
	/**
	 * Integration Status - Unsuccessful
	 */
	public static final String STATUS_NOT_INTEGRATED = LCSProperties.get("com.sm.wc.interface.LCSLifeCycleManaged.statusNotIntegrated");
	/**
	 * Log Entry MDMID key
	 */
	public static final String LOG_ENTRY_MDMID_KEY = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryMDMID");
	/**
	 * Log Entry Object ID key
	 */
	public static final String LOG_ENRTY_OBJECTID_KEY = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryObjectID");
	/**
	 * Log Entry Name key
	 */
	public static final String LOG_ENRTY_NAME = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryName");
	/**
	 * Log Entry STATUS key
	 */
	public static final String LOG_ENTRY_STATUS = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryStatus");
	/**
	 * Log Entry PENDING STATUS key
	 */
	public static final String LOG_ENTRY_PENDING_STATUS = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryPendingStatus");
	/**
	 * Log Entry PENDING STATUS
	 */
	public static final String LOG_ENTRY_STATUS_PENDING = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryStatusPending");
	/**
	 * Log Entry PROCESSED STATUS key
	 */
	public static final String LOG_ENTRY_PROCESSED_STATUS = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryProcessedStatus");
	/**
	 * SUB DIVISION Name key
	 */
	public static final String SUB_DIVISION_NAME = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.subDivisionName");
	/**
	 * Log Entry Integration Status key
	 */
	public static final String LOG_ENRTY_INTEGRATION_STATUS_KEY = LCSProperties
			.get("com.sm.wc.interface.LCSLogEntry.logEntryIntegrationStatusKey");
	/**
	 * Log Entry Request Type key
	 */
	public static final String LOG_ENRTY_REQUEST_TYPE = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryRequestType");
	/**
	 * Log Entry RequestID key
	 */
	public static final String LOG_ENRTY_REQUEST_ID = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryRequestID");
	/**
	 * Log Entry BO Type key
	 */
	public static final String LOG_ENRTY_BO_TYPE = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryBOType");
	/**
	 * Log Entry Error Reason key
	 */
	public static final String LOG_ENRTY_ERROR_REASON = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryErrorReason");
	/**
	 * Log Entry Object Name key
	 */
	public static final String LOG_ENRTY_OBJECT_NAME = LCSProperties.get("com.sm.wc.interface.LCSLogEntry.logEntryObjectName");
	/**
	 * List Value DB Column Name
	 *//*
	 * public static final String LIST_VALUE_NAME_DB_COLUMN =
	 * LCSProperties.get(
	 * "com.sm.wc.interface.LCSLifeCycleManaged.listValueDBColumnName");
	 */
	/**
	 * Sub Division Queue Name
	 */
	public static final String SUB_DIVISION_QUEUE_NAME = LCSProperties.get("com.sm.wc.interface.queue.subDivisionQueueName");
	/**
	 * Sub Division XML FILE LOCATION
	 */
	public static final String SUB_DIVISION_FILE_LOCATION = LCSProperties.get("com.sm.wc.interface.xmlGeneration.subDivisionFileLocation");
	/**
	 * Request ID for first run
	 */
	//public static Integer REQUEST_ID = 1000000000;
	/**
	 * Sub Division Tree Type for XML file name
	 */
	public static final String SUB_DIVISION_TYPE = LCSProperties.get("com.sm.wc.interface.xmlGeneration.subDivisionType");
	/**
	 * INITIALIZING String NO_ERROR
	 */
	public static final String NO_ERROR = "";
	/**
	 * Error String for MDM ID NOT PRESENT IN RESPONSE
	 */
	public static final String MDM_ID_NOT_PRESENT = LCSProperties.get("com.sm.wc.interface.responseValidation.mdmIDNotPresent");
	/**
	 * Error String for CLASS NOT PRESENT IN RESPONSE
	 */
	public static final String CLASS_NOT_PRESENT = LCSProperties.get("com.sm.wc.interface.responseValidation.classNotPresent");
	/**
	 * Error String for SUB CLASS NOT PRESENT IN RESPONSE
	 */
	public static final String SUB_CLASS_NOT_PRESENT = LCSProperties.get("com.sm.wc.interface.responseValidation.subClassNotPresent");
	/**
	 * Error String for CATEGORY NOT PRESENT IN RESPONSE
	 */
	public static final String CATEGORY_NOT_PRESENT = LCSProperties.get("com.sm.wc.interface.responseValidation.categoryNotPresent");
	/**
	 * Error String for SUB CATEGORY NOT PRESENT IN RESPONSE
	 */
	public static final String SUB_CATEGORY_NOT_PRESENT = LCSProperties.get("com.sm.wc.interface.responseValidation.subCategoryNotPresent");
	/**
	 * Error String for NAME NOT PRESENT IN RESPONSE
	 */
	public static final String NAME_NOT_PRESENT = LCSProperties.get("com.sm.wc.interface.responseValidation.nameNotPresent");
	/**
	 * Error String for GoM NOT PRESENT IN RESPONSE
	 */
	public static final String GOM_NOT_PRESENT = LCSProperties.get("com.sm.wc.interface.responseValidation.gomNotPresent");
	/**
	 * Error String for GUID NOT PRESENT IN RESPONSE
	 */
	public static final String GUI_ID_NOT_PRESENT = LCSProperties.get("com.sm.wc.interface.responseValidation.guIDNotPresent");
	/**
	 * Error String for REQUEST TYPE NOT PRESENT IN RESPONSE
	 */
	public static final String REQUEST_TYPE_NOT_PRESENT = LCSProperties.get("com.sm.wc.interface.responseValidation.requestTypeNotPresent");
	/**
	 * Sub Division Queue start time
	 */
	public static final String SUB_DIVISION_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.util.time");
	/**
	 * AM or PM for SubDivision Queue
	 */
	public static final String SUB_DIVISION_QUEUE_AM = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.util.AM_PM");
	/**
	 * Interval in minutes for SubDivision Queue
	 */
	public static final long SUB_DIVISION_QUEUE_INTERVAL_IN_MINS = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.util.intervalInMinutes", 480);
	/**
	 * Timeout for SubDivision Webservice in Minutes
	 */
	public static final int SUB_DIVISION_TIMEOUT_IN_MINUTES = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.util.webServiceTimeOutinMinutes", 3);
}
/**
 *
 */
package com.sportmaster.wc.interfaces.webservices.outbound.supplier.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.bean.Contact;
import com.sportmaster.wc.interfaces.webservices.bean.ContactInformation;
import com.sportmaster.wc.interfaces.webservices.bean.MdmList;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.client.SMSupplierOutboundDataRequestClient;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

/**
 * @author 'true' ITC_Infotech.
 *
 */
public class SMSupplierUtil {

	/**
	 * IDA2A2.
	 */
	private static final String IDA2A2 = "IDA2A2";
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierUtil.class);
	/**
	 * Get total count of UPDATE_PENDING records.
	 */
	private static int totalUpdatePendingCount;
	/**
	 * List of Business Supplier MDM ID related to factory.
	 */
	private static Set<String> businessSupplierFactoryMDMIdList;
	/**
	 * Constructor.
	 */
	protected SMSupplierUtil(){
		//Constructor.
	}



	/**
	 * Classify supplier collection to respective types.
	 * @param supplierObjectMap - Map<String, String>
	 */
	public static void classifySupplier(Map<String, LCSSupplier> supplierObjectMap){
		LOGGER.debug("Inside classifySupplier !!!!!!!!!!!!");
		LOGGER.debug(supplierObjectMap.size());
		Map<String, LCSSupplier> businessSuppCollection = new HashMap<>();
		Map<String, LCSSupplier> materialSuppCollection = new HashMap<>();
		Map<String, LCSSupplier> factorySuppCollection = new HashMap<>();
		for (Map.Entry<String, LCSSupplier> entry : supplierObjectMap.entrySet()){
			LOGGER.debug("KEY >>>>  "+entry.getKey()+"    ################    "+entry.getValue().getName());
			if(SMOutboundWebServiceConstants.BUSINESS_SUPPLIER.equals(entry.getValue().getFlexType().getTypeDisplayName())){
				businessSuppCollection.put(entry.getKey(), entry.getValue());
			}else if(SMOutboundWebServiceConstants.MATERIAL_SUPPLIER.equals(entry.getValue().getFlexType().getTypeDisplayName())){
				materialSuppCollection.put(entry.getKey(), entry.getValue());
			}else if(SMOutboundWebServiceConstants.FACTORY.equals(entry.getValue().getFlexType().getTypeDisplayName())){
				factorySuppCollection.put(entry.getKey(), entry.getValue());
			}else{
				LOGGER.debug("INVALID !!!!!!!!!!");
			}
		}
		//Set Collection to zero
		resetCollection();
		com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing.setBusinessSupplierCollection(businessSuppCollection);
		com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing.setMaterialSupplierCollection(materialSuppCollection);
		com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing.setFactoryCollection(factorySuppCollection);
		LOGGER.debug("Business Supplier Size >>>>>>>>>>>>>    "+com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing.getBusinessSupplierCollection().size());
		LOGGER.debug("Material Supplier Size >>>>>>>>>>>>>    "+com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing.getMaterialSupplierCollection().size());
		LOGGER.debug("Factory Collection size >>>>>>>>>>>>    "+com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing.getFactoryCollection().size());
	}

	/**
	 * Clear Supplier Collection.
	 */
	public static void resetCollection(){
		com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing.getBusinessSupplierCollection().clear();
		com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing.getMaterialSupplierCollection().clear();
		com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing.getFactoryCollection().clear();
	}

	/**
	 * Getting Contact MOA data.
	 * @param lcsSupplier - LCSSupplier
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws DatatypeConfigurationException
	 * @throws ParseException
	 */
	public static Contact getSupplierContactInformationFromMOA(LCSSupplier lcsSupplier) throws WTPropertyVetoException, WTException, DatatypeConfigurationException, ParseException{
		LOGGER.debug("Inside getSupplierContactInformationFromMOA !!!!!!!!!");
		String strFormat= "yyyy-MM-dd hh:mm:ss.S";
		LCSMOATable supplierContactMOATable = (LCSMOATable) lcsSupplier.getValue(SMOutboundWebServiceConstants.SUPPLIER_CONTACT_MOA_KEY);
		@SuppressWarnings("unchecked")
		//Get MOA rows
		Collection<FlexObject> contactMOARows =  supplierContactMOATable.getRows();
		Contact contact = null;
		if (!contactMOARows.isEmpty()) {
			contact = new Contact();
			for(FlexObject fObj : contactMOARows){
				//Contact contact = new Contact();
				ContactInformation contactInformation = new ContactInformation();
				contactInformation.setName(fObj.getString(SMOutboundWebServiceConstants.SUPPLIER_CONTACT_MOA_NAME_KEY));
				contactInformation.setPosition(fObj.getString(SMOutboundWebServiceConstants.SUPPLIETR_CONTACT_MOA_TITLE_KEY));
				contactInformation.setEmail(fObj.getString(SMOutboundWebServiceConstants.SUPPLIER_CONTACT_MOA_EMAIL_KEY));
				contactInformation.setPhone(fObj.getString(SMOutboundWebServiceConstants.SUPPLIER_CONTACT_MOA_PHONE_KEY));
				if(FormatHelper.hasContent(fObj.getString(SMOutboundWebServiceConstants.SUPPLIETR_CONTACT_MOA_END_DATE_KEY))){
					contactInformation.setSmSupplierContactEndDate(com.sportmaster.wc.interfaces.webservices.outbound.supplier.helper.SMSupplierHelper.getXMLGregorianCalendarFormat(new SimpleDateFormat(strFormat).parse(fObj.getString(SMOutboundWebServiceConstants.SUPPLIETR_CONTACT_MOA_END_DATE_KEY))));
				}
				contactInformation.setSmSupplierContactAreaOfResponsibility(fObj.getString(SMOutboundWebServiceConstants.SUPPLIER_CONTACT_MOA_AREA_OF_RESPONSIBILITY_KEY));
				contact.getContactInformation().add(contactInformation);
			}
		}
		return contact;
	}

	/**
	 * Get Country MDMID from country Object.
	 * @param lcsSupplier - LCSSupplier
	 * @throws WTException - WTException
	 */
	public static String getCountryMDMIDFromSupplier(LCSSupplier lcsSupplier) throws WTException{
		LOGGER.debug("Inside getCountryMDMID !!!!!!!!! ");
		LOGGER.debug("##############################" + lcsSupplier.getValue("vrdCountry"));
		com.lcs.wc.country.LCSCountry country = (com.lcs.wc.country.LCSCountry) LCSQuery.findObjectById(lcsSupplier.getValue(SMOutboundWebServiceConstants.SUPPLIER_COUNTRY_OBJ_REF_KEY).toString());
		String countryMDMID = (String) country.getValue(SMOutboundWebServiceConstants.COUNTRY_MDMID_KEY);
		LOGGER.debug("Country MDMID >>>>>>>>>>>>>   "+countryMDMID);
		return countryMDMID;
	}

	/**
	 * Get Supplier MDM ID from Relationship MOA.
	 * @param lcsSupplier - LCSSupplier
	 * @throws WTException - WTException
	 */
	public static MdmList getMDMIDFromMOA(LCSSupplier lcsSupplier) throws WTException{
		LOGGER.debug("Inside getMDMIDFromMOA !!!!!!!!!");
		//String supplierMDMID = "";
		MdmList mdmList = new MdmList();
		LCSMOATable supplierRelationshipMOATable = (LCSMOATable) lcsSupplier.getValue("vrdRelationships");
		@SuppressWarnings("unchecked")
		Collection<FlexObject> rows =  supplierRelationshipMOATable.getRows();
		for(FlexObject fo: rows){
			LCSSupplier supplierReferenceInMOA ;
			supplierReferenceInMOA=(LCSSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.supplier.LCSSupplier:"+fo.getString("vrdSupplierRef".toUpperCase()));
			String supplierMDMID = (String) supplierReferenceInMOA.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY);
			LOGGER.debug("SUPPLIER MDM ID in supplier Relationship table   >>>>>>>>>>>>>     "+supplierMDMID);
			if(FormatHelper.hasContent(supplierMDMID)){
				mdmList.getMdmId().add(supplierMDMID);
			}
		}
		return mdmList;
	}

	/**
	 * Check if Business Supplier exists on Relationship MOA.
	 * @param lcsSupplier - LCSSupplier
	 * @throws WTException - WTException
	 */
	public static boolean getSupplierMDMIDFromFactoryMOA(List<LCSSupplier> businessSupplierList, LCSSupplier factoryObj) throws WTException{
		LOGGER.debug("Inside getSupplierMDMIDFromFactoryMOA !!!!!!!!!");
		int count =0;
		boolean valid = false;
		Set<String> bsMDMList = new HashSet<>();
		for(LCSSupplier lcsSupplier : businessSupplierList){
			LCSMOATable factoryRelationshipTable = (LCSMOATable) lcsSupplier.getValue("vrdRelationships");
			@SuppressWarnings("unchecked")
			Collection<FlexObject> rows =  factoryRelationshipTable.getRows();
			for(FlexObject fo: rows){
				LCSSupplier businessSupplierReferenceInMOA ;
				businessSupplierReferenceInMOA=(LCSSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.supplier.LCSSupplier:"+fo.getString("vrdSupplierRef".toUpperCase()));
				//LOGGER.debug("SUPPLIER TYPE IN MOA >>>>>   "+businessSupplierReferenceInMOA.getFlexType().getFullNameDisplay());
				if(SMOutboundWebServiceConstants.FACTORY.equals(businessSupplierReferenceInMOA.getFlexType().getFullNameDisplay())){
					String factoryName = businessSupplierReferenceInMOA.getName();
					String businessSupplierMDMID = (String) lcsSupplier.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY);
					LOGGER.debug(" Factory Name in Business Supplier Relationship table   >>>>>>>>>>>>>     " + factoryName);
					if(factoryObj.getName().equals(factoryName)){
						bsMDMList.add(businessSupplierMDMID);
						setBusinessSupplierFactoryMDMIdList(bsMDMList);
						//setBusinessSupplierMDMIDForFactory(bsMDMList);
						count++;
					}
				}
			}
		}
		if(count > 0){
			valid = true;
		}
		return valid;
	}

	/**
	 * Sets Business Supplier MDMID for Factory.
	 * @param MDMIdList
	 */
	public static MdmList setBusinessSupplierMDMIDForFactory(){
		MdmList businessSupplierMDMList = new MdmList();
		for(String mdmID : getBusinessSupplierFactoryMDMIdList()){
			businessSupplierMDMList.getMdmId().add(mdmID);
		}
		return businessSupplierMDMList;
	}



	/**
	 * Determine Supplier Type.
	 * @param supplierObj - LCSSupplier
	 * @return String
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static String determineSupplierType(LCSSupplier supplierObj) throws WTException, WTPropertyVetoException {
		//Check supplier type
		if(SMOutboundWebServiceConstants.BUSINESS_SUPPLIER.equals(supplierObj.getFlexType().getTypeDisplayName())){
			LOGGER.info("Supplier is of type Business Supplier !!!");
			return SMOutboundWebServiceConstants.LOG_ENRTY_BUSINESS_SUPPLIER_OUTBOUND_PATH;
		}else if(SMOutboundWebServiceConstants.MATERIAL_SUPPLIER.equals(supplierObj.getFlexType().getTypeDisplayName())){
			LOGGER.info("Supplier is of type Material Supplier !!!");
			return SMOutboundWebServiceConstants.LOG_ENRTY_MATERIAL_SUPPLIER_OUTBOUND_PATH;
		}else if(SMOutboundWebServiceConstants.FACTORY.equals(supplierObj.getFlexType().getTypeDisplayName())){
			LOGGER.info("Supplier is of type Factory !!!");
			return SMOutboundWebServiceConstants.LOG_ENRTY_FACTORY_OUTBOUND_PATH;
		}
		return null;
	}

	/**
	 * Queries Log Entry for Update_pending and returns Supplier Object Collection.
	 * @param attribute - String
	 * @return supplierObjectMap - Map<String, LCSSupplier>
	 * @throws WTException - WTException
	 */
	public static Map<String, LCSSupplier> querySupplierLogEntry(String attribute) throws WTException{
		LOGGER.debug("Inside querySupplierLogEntry method #############");
		//Hashmap to store Log entries
		Map<String, LCSSupplier> supplierObjectMap = new HashMap<>();
		//Map<String, String> supplierObjectCollection = new HashMap<String, String>();
		//String supplierLogEntry="LCSLogEntry";
		com.lcs.wc.flextype.FlexType supplierLogEntryType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_BUSINESS_SUPPLIER_OUTBOUND_PATH);
		//LOGGER.debug("ID PATH ##############################   "+supplierLogEntryType.getIdPath());
		PreparedQueryStatement statement = new PreparedQueryStatement();//Creating Statement.
		statement.appendFromTable(LCSLogEntry.class);
		statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, supplierLogEntryType.getAttribute(attribute).getColumnName());//append column
		statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, IDA2A2);//append column
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, SMOutboundWebServiceConstants.FLEXTYPEIDPATH, supplierLogEntryType.getIdPath(),Criteria.EQUALS));//adding criteria
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,supplierLogEntryType.getAttribute(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS).getColumnName(), SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PENDING,Criteria.EQUALS));
		//addCriteriaSupplier(statement, supplierLogEntryType);

		/*statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria("LCSLogEntry",supplierLogEntryType.getAttribute(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS).getColumnName(), SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PENDING,Criteria.EQUALS));*/
		statement.appendOrIfNeeded();

		supplierLogEntryType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_MATERIAL_SUPPLIER_OUTBOUND_PATH);
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, SMOutboundWebServiceConstants.FLEXTYPEIDPATH, supplierLogEntryType.getIdPath(),Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,supplierLogEntryType.getAttribute(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS).getColumnName(), SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PENDING,Criteria.EQUALS));
		statement.appendOrIfNeeded();
		supplierLogEntryType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_FACTORY_OUTBOUND_PATH);
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, SMOutboundWebServiceConstants.FLEXTYPEIDPATH, supplierLogEntryType.getIdPath(),Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,supplierLogEntryType.getAttribute(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS).getColumnName(), SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PENDING,Criteria.EQUALS));
		com.lcs.wc.db.SearchResults results = null;
		//executing  statement
		results =LCSQuery.runDirectQuery(statement);
		List<?> suppLogEntryCollection= results.getResults();
		FlexObject fo=null;
		LOGGER.debug("Supplier Log Entry Outbound data collection Size >>>>>>>\t"+suppLogEntryCollection.size());
		LCSSupplier supplierObject;
		if (!suppLogEntryCollection.isEmpty()) {
			//LCSLogEntry logEntryObj;
			for(Object obj:suppLogEntryCollection){
				LOGGER.debug("#########################################################################");
				fo= (FlexObject) obj;
				//LOGGER.debug("Supplier LogEntry Object >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  "+fo);
				LCSLogEntry logEntryObj=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+fo.getString("LCSLOGENTRY.IDA2A2"));
				//storing in hashmap
				supplierObject = com.sportmaster.wc.interfaces.webservices.outbound.supplier.helper.SMSupplierHelper.getSupplierObjectFromMaster((String)logEntryObj.getValue(attribute));
				LOGGER.debug("Supplier Object >>>>>>     "+supplierObject.getName());
				supplierObjectMap.put((String)logEntryObj.getValue(attribute), supplierObject);
			}
		}
		setTotalUpdatePendingCount(supplierObjectMap.size());
		//returning log entry
		return supplierObjectMap;
	}

	/**
	 * Queries the Log Entry.
	 * @param String flexPath
	 * @throws wt.util.WTException exceptions
	 * @returns HashMap<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryOutboundSuplierLogEntry(String attribute) throws WTException{
		LOGGER.debug("Inside queryLogEntry method !!!!!!!!!!");
		//Hashmap to store Log entries
		Map<String, LCSLogEntry> logEntrySupplierOutboundMap = new HashMap<>();
		//String supplierLogEntry="LCSLogEntry";
		com.lcs.wc.flextype.FlexType supplierLogEntryType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_BUSINESS_SUPPLIER_OUTBOUND_PATH);
		//LOGGER.debug("ID PATH ##############################   "+supplierLogEntryType.getIdPath());
		com.lcs.wc.db.PreparedQueryStatement statement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
		statement.appendFromTable(LCSLogEntry.class);
		statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, supplierLogEntryType.getAttribute(attribute).getColumnName());//append column
		statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, IDA2A2);//append column
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, SMOutboundWebServiceConstants.FLEXTYPEIDPATH, supplierLogEntryType.getIdPath(),Criteria.EQUALS));//adding criteria
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,supplierLogEntryType.getAttribute(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS).getColumnName(), SMOutboundWebServiceConstants.LOG_ENTRY_OBJECT_MISSING,Criteria.NOT_EQUAL_TO));

		statement.appendOrIfNeeded();
		supplierLogEntryType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_MATERIAL_SUPPLIER_OUTBOUND_PATH);
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, SMOutboundWebServiceConstants.FLEXTYPEIDPATH, supplierLogEntryType.getIdPath(),Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,supplierLogEntryType.getAttribute(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS).getColumnName(), SMOutboundWebServiceConstants.LOG_ENTRY_OBJECT_MISSING,Criteria.NOT_EQUAL_TO));

		statement.appendOrIfNeeded();
		supplierLogEntryType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_FACTORY_OUTBOUND_PATH);
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, SMOutboundWebServiceConstants.FLEXTYPEIDPATH, supplierLogEntryType.getIdPath(),Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,supplierLogEntryType.getAttribute(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS).getColumnName(), SMOutboundWebServiceConstants.LOG_ENTRY_OBJECT_MISSING,Criteria.NOT_EQUAL_TO));

		com.lcs.wc.db.SearchResults results = null;
		//executing  statement
		results =LCSQuery.runDirectQuery(statement);
		List<?> outboundLogEntryDataCollection= results.getResults();
		FlexObject fo=null;
		LOGGER.debug("Log Entry Outbound data collection Size >>>>>>>\t"+outboundLogEntryDataCollection.size());
		if (!outboundLogEntryDataCollection.isEmpty()) {
			for(Object obj:outboundLogEntryDataCollection){
				fo= (FlexObject) obj;
				//LOGGER.debug("Supplier LogEntry Object >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  "+fo);
				LCSLogEntry logEntry=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+fo.getString("LCSLOGENTRY.IDA2A2"));
				//storing in hashmap
				logEntrySupplierOutboundMap.put((String)logEntry.getValue(attribute), logEntry);
			}
		}
		//returning log entry
		return logEntrySupplierOutboundMap;
	}

	/**
	 * Get Supplier Master Reference from Supplier.
	 * @param lcsSupplier - LCSSupplier
	 * @return ida3MasterRef - String
	 * @throws WTException - WTException
	 */
	public static String getSupplierMasterReferenceFromSupplier(
			LCSSupplier lcsSupplier) throws WTException {
		wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
		String refString = rf.getReferenceString(lcsSupplier.getMasterReference());
		LOGGER.debug("rf ***********************     "+refString);
		char colon = ':';
		String ida3MasterRef = refString.substring(refString.lastIndexOf(colon) + 1);
		LOGGER.debug("IDA3MasterReference >>>>>>>>>>>>>>>>>>>>>>>    "+ida3MasterRef);
		return ida3MasterRef;
	}



	/**
	 * Prints summary for one integration run.
	 */
	public static void printSupplierUpdateSummaryAfterScheduleQueueRun(){
		LOGGER.debug("failed count >>>>>>>>   "+SMSupplierOutboundDataRequestClient.getFailedUpdateCount());
		//int successCount= getTotalUpdatePendingCount()-SMSupplierOutboundDataRequestClient.getFailedUpdateCount();
		StringBuilder supplierIntegrationRunSummary = new StringBuilder();
		supplierIntegrationRunSummary.append("\n#####################################################################################");
		supplierIntegrationRunSummary.append("\n###############    SUMMARY OF SUPPLIER OUTBOUND INTEGRATION SCHEDULE QUEUE RUN    ###############");
		supplierIntegrationRunSummary.append("\n#########   TOTAL NUMBER OF OBJECTS IN UPDATE PENDING STATUS BEFORE RUNNING QUEUE \t\t\t\t---------->   ");
		supplierIntegrationRunSummary.append(getTotalUpdatePendingCount());
		supplierIntegrationRunSummary.append("\n#########   TOTAL NUMBER OF OBJECTS IN UPDATE PROCESSED STATUS AFTER RUNNING THE QUEUE (SUCCESS)\t\t---------->   ");
		supplierIntegrationRunSummary.append(String.valueOf(getTotalUpdatePendingCount()-SMSupplierOutboundDataRequestClient.getFailedUpdateCount()));
		supplierIntegrationRunSummary.append("\n#########   TOTAL NUMBER OF OBJECTS STILL IN UPDATE PENDING STATUS AFTER RUNNING QUEUE (FAILED)\t\t\t---------->   ");
		supplierIntegrationRunSummary.append(SMSupplierOutboundDataRequestClient.getFailedUpdateCount());
		supplierIntegrationRunSummary.append("\n#####################################################################################");
		LOGGER.debug(supplierIntegrationRunSummary);
		//Reset all counts
		SMSupplierOutboundDataRequestClient.setFailedUpdateCount(0);
		//successCount = 0;
		setTotalUpdatePendingCount(0);
		SMSupplierOutboundDataRequestClient.setFailedCountDueToException(false);
	}


	/**
	 * Queries the Supplier Business Supplier.
	 * @throws WTException - WTException
	 * @returns businessSupplierObjectList - List<LCSSupplier>
	 */
	public static List<LCSSupplier> queryBusinessSupplier() throws WTException{
		//Hash Map to store Business Supplier Objects.
		List<LCSSupplier> businessSupplierObjectList = new ArrayList<>();
		LOGGER.debug("Inside Query Business Supplier Method !!!!!!!!");
		com.lcs.wc.flextype.FlexType supplierType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.BUSINESS_SUPPLIER_PATH);
		com.lcs.wc.db.PreparedQueryStatement businessSupplierStmt = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
		businessSupplierStmt.appendFromTable(LCSSupplier.class);
		businessSupplierStmt.appendSelectColumn("LCSSupplier", IDA2A2);//appending columns
		//adding criteria
		businessSupplierStmt.appendCriteria(new Criteria("LCSSUPPLIER", "flexTypeIdPath", supplierType.getIdPath(),Criteria.EQUALS));

		businessSupplierStmt.appendAndIfNeeded();
		businessSupplierStmt.appendCriteria(new Criteria("LCSSUPPLIER",supplierType.getAttribute(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY).getColumnName()," ",Criteria.NOT_EQUAL_TO));
		com.lcs.wc.db.SearchResults businessSupplierResults = null;
		//executing  statement
		businessSupplierResults =LCSQuery.runDirectQuery(businessSupplierStmt);
		List<?> businessSupplierCollection= businessSupplierResults.getResults();
		LOGGER.debug("List Size >>>>>>>>>   "+businessSupplierCollection.size());
		FlexObject fo=null;
		if (!businessSupplierCollection.isEmpty()) {
			//iterating collection
			for(Object obj:businessSupplierCollection){
				fo= (FlexObject) obj;
				//LOGGER.debug("flexObject >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  "+fo);
				LCSSupplier supplierObj=(LCSSupplier) LCSQuery.findObjectById("com.lcs.wc.supplier.LCSSupplier:"+fo.getString("LCSSUPPLIER.IDA2A2"));
				supplierObj=(LCSSupplier)VersionHelper.latestIterationOf(supplierObj.getMaster());
				//Adding object to Hashmap
				businessSupplierObjectList.add(supplierObj);
			}
		}
		//LOGGER.debug("BUSINESS SUPPLIER LIST SIZE  >>>>>>>>>>>   "+businessSupplierObjectList.size());
		return businessSupplierObjectList;
	}

	/**
	 * Gets the total Update Pending Objects in Log Entry.
	 * @return the totalUpdatePendingCount - int
	 */
	public static int getTotalUpdatePendingCount() {
		return totalUpdatePendingCount;
	}

	/**
	 * Sets the total log entry count with status as UPDATE_PENDING.
	 * @param totalUpdatePendingCount the totalUpdatePendingCount to set - int
	 */
	public static void setTotalUpdatePendingCount(int totalUpdatePendingCount) {
		SMSupplierUtil.totalUpdatePendingCount = totalUpdatePendingCount;
	}



	/**
	 * @return the businessSupplierFactoryMDMIdList
	 */
	public static Set<String> getBusinessSupplierFactoryMDMIdList() {
		return businessSupplierFactoryMDMIdList;
	}



	/**
	 * @param businessSupplierFactoryMDMIdList the businessSupplierFactoryMDMIdList to set
	 */
	public static void setBusinessSupplierFactoryMDMIdList(
			Set<String> bsList) {
		businessSupplierFactoryMDMIdList = bsList;
	}
	// Phase 14 - EMP-449 - Start
	public static String getSupplierPrefix(LCSSupplier supplier)throws WTPropertyVetoException, WTException{
		return (String)supplier.getValue(SMOutboundWebServiceConstants.SUPPLIER_PREFIX_KEY);
	}
	// Phase 14 - EMP-449 - End
}

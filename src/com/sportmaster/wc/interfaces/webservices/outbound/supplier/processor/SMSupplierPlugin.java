package com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.queue.service.SMProcessingQueueService;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.client.SMSupplierOutboundDataRequestClient;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.util.SMSupplierUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.fc.WTObject;
import wt.pom.PersistenceException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.NonLatestCheckoutException;
import wt.vc.wip.WorkInProgressException;

/**
 *
 * @author 'true' ITC_Infotech.
 *
 */
public class SMSupplierPlugin {

	private static final String ERROR_FOUND = "ERROR FOUND :-";
	private static final String CURRENT_LIFECYCLE_STATE = "Current Lifecycle state >>>>>>>>>>>>>    ";
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierPlugin.class);
	/**
	 * Constructor.
	 */
	protected SMSupplierPlugin(){
		//protected Constructor
	}
	private static boolean updateFlag;
	/**
	 * Supplier Object.
	 */
	private static LCSSupplier lcsSupplier;
	/**
	 * Current Lifecycle State.
	 */
	private static String currLifeCycleState;

	/**
	 * Plugin triggered on Supplier Object Save.
	 * @param wtObj - WTObject
	 * @throws SQLException_Exception
	 * @throws WTException
	 */
	public static void processSupplierOutboundService(WTObject wtObj) throws SQLException_Exception, WTException {
		try{
			LOGGER.debug("  ##############   Plugin Trigerred   ################   ");
			setLcsSupplier((LCSSupplier)wtObj);
			String supplierType = getLcsSupplier().getFlexType().getFullNameDisplay();

			//checking supplier is of valid type
			if(validateSupplierObject(supplierType)){
				setCurrLifeCycleState(getLcsSupplier().getLifeCycleState().toString());
				// LOGGER.debug("Internal Type name >>>>>>>>>>>>
				// "+lcsSupplier.getDisplayType());
				Map<String, String> supplierObjectIDMap = new HashMap<>();
				String ida3MasterRef = SMSupplierUtil.getSupplierMasterReferenceFromSupplier(getLcsSupplier());
				//set ida3MasterRef to map
				final Class<?>[] supplierArgTypesx = { Map.class };
				final Object[] supplierArgValuex = { supplierObjectIDMap };
				LOGGER.debug(">>>>  processSupplierOutboundService method !!");
				LOGGER.debug("Supplier Object >>>>>>>  "+getLcsSupplier().getSupplierName());

				// phase 13 changes- retrieving to integrate value
				boolean toIntegrate = FormatHelper.parseBoolean(
						String.valueOf(getLcsSupplier().getValue(SMOutboundWebServiceConstants.TO_INTEGRATE)));

				//Check if MDM ID is present
				if(!FormatHelper.hasContent((String) getLcsSupplier().getValue(SMOutboundWebServiceConstants.SUPPLIER_MDMID))){
					LOGGER.debug("MDM ID id not present !!!!!!!!!!!!!   Generating FAKE MDM ID !!!!!!!");
					LOGGER.debug("CALLING SUPPLIER OUTBOUND WEB CLIENT !!!!!!!!!!");
					getLcsSupplier().setValue(SMOutboundWebServiceConstants.SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY, getCurrLifeCycleState());
					//initialize map
					supplierObjectIDMap.put(ida3MasterRef, SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST);
					SMSupplierOutboundDataRequestClient.supplierDataRequest(getLcsSupplier(), supplierObjectIDMap);
					updateFlag=false;
					// phase 13 changes- starts
				} else if (toIntegrate) {

					// initialize map
					supplierObjectIDMap.put(SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST, ida3MasterRef);
					// adding entry to processing qurese for update request to MDM
					SMProcessingQueueService.addQueueEntry(SMOutboundWebServiceConstants.SUPPLIER_PROCESSING_QUEUE_NAME,
							supplierArgTypesx, supplierArgValuex, "processDataFromProcessQueue",
							"com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierLogEntryProcessor");
					// phase 13 changes- ends
				}else{
					processLifeCycleChanges(supplierObjectIDMap, ida3MasterRef,
							supplierArgTypesx, supplierArgValuex);
				}
			}
		}catch(WTException wtExp){
			LOGGER.error(ERROR_FOUND, wtExp);
		}catch(SQLException sqlExp){
			LOGGER.error(ERROR_FOUND, sqlExp);
		} finally {
			try {
				if(!updateFlag){
					setPreviousLifeCycleState();
				}
			} catch (WTPropertyVetoException propExpt) {
				LOGGER.error(ERROR_FOUND, propExpt);
			}
		}
	}

	/**
	 * @param supplierObjectIDMap
	 * @param ida3MasterRef
	 * @param supplierArgTypesx
	 * @param supplierArgValuex
	 * @throws WTException
	 * @throws SQLException
	 */
	public static void processLifeCycleChanges(
			Map<String, String> supplierObjectIDMap, String ida3MasterRef,
			final Class<?>[] supplierArgTypesx, final Object[] supplierArgValuex)
					throws WTException, SQLException {
		//MDM ID is present. Trigger Update on State Change
		updateFlag=true;
		//SMSupplierHelper.persistSupplier(lcsSupplier);
		String prevLifeCycleState = (String)getLcsSupplier().getValue(SMOutboundWebServiceConstants.SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY);
		LOGGER.debug("Attribute before >>>>>>>>>>    "+prevLifeCycleState);
		if(!getCurrLifeCycleState().equalsIgnoreCase(prevLifeCycleState) && !"INWORK".equalsIgnoreCase(getCurrLifeCycleState())){
			updateFlag=false;
		}
		if (checkUnderReviewToReleased(getLcsSupplier(), prevLifeCycleState) || checkReleasedToCanceled(getLcsSupplier())
				|| checkUnderReviewToCanceled(getLcsSupplier())) {
			// LOGGER.debug("BOOLEAN VALUE FOR UPDATE >>>>>>>>>>>>>> " +
			// lcsSupplier.getValue("smUpdate"));

			//check for lifecycle state change
			LOGGER.debug("MDM EXTERNAL ID is present !! Create or Update Log Entry Record !!!!!");
			//initialize map
			supplierObjectIDMap.put(SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST,ida3MasterRef);
			//call processing queue
			SMProcessingQueueService.addQueueEntry(SMOutboundWebServiceConstants.SUPPLIER_PROCESSING_QUEUE_NAME, supplierArgTypesx, supplierArgValuex, "processDataFromProcessQueue", "com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierLogEntryProcessor");
			updateFlag=false;
		}
	}



	/**
	 * Sets previous lifecycle state attribute.
	 * @throws WTException
	 * @throws WorkInProgressException
	 * @throws NonLatestCheckoutException
	 * @throws WTPropertyVetoException
	 * @throws PersistenceException
	 */
	public static void setPreviousLifeCycleState() throws WTException,
	WorkInProgressException, NonLatestCheckoutException,
	WTPropertyVetoException, PersistenceException {
		boolean checkout=false;
		LCSSupplier supplier= (LCSSupplier) VersionHelper.latestIterationOf(getLcsSupplier().getMaster());
		if(!VersionHelper.isCheckedOut(supplier)){
			supplier= (LCSSupplier) wt.vc.wip.WorkInProgressHelper.service.checkout(supplier,wt.vc.wip.WorkInProgressHelper.service.getCheckoutFolder(), "").getWorkingCopy();
			checkout=true;
			LOGGER.debug("checking out >>>>"+checkout);
		}
		supplier.setValue(SMOutboundWebServiceConstants.SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY, getCurrLifeCycleState());
		supplier.setValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY, getLcsSupplier().getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY));
		// phase 13 - reset To Integrate value once supplier is updated.
		supplier.setValue(SMOutboundWebServiceConstants.TO_INTEGRATE, "false");
		//Using persist to avoid calling the plugin again on Save of Supplier Object.
		setLcsSupplier((LCSSupplier) LCSLogic.persist(supplier, true));
		if(checkout){
			supplier=(LCSSupplier) wt.vc.wip.WorkInProgressHelper.service.checkin(getLcsSupplier(), null);
		}
		setLcsSupplier(supplier);
		LOGGER.debug("After setting previous value>>>>" +getLcsSupplier().getValue(SMOutboundWebServiceConstants.SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY));
	}


	/**
	 * Check for State Change.
	 * @param lcsSupplier - LCSSupplier
	 * @param prevLifeCycleState
	 * @return boolean
	 * @throws WTException - WTException
	 */
	public static boolean checkUnderReviewToReleased(LCSSupplier lcsSupplierObject, String prevLifeCycleState) throws WTException{
		LOGGER.debug("Inside checkUnderReviewToReleased !!!!!!");
		String currentLifeCycleState = lcsSupplierObject.getLifeCycleState().toString();
		LOGGER.debug(CURRENT_LIFECYCLE_STATE+currentLifeCycleState);
		if(FormatHelper.hasContent((String)lcsSupplierObject.getValue(SMOutboundWebServiceConstants.SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY))){

			if (SMOutboundWebServiceConstants.SUPPLIER_LIFECYCLE_STATE_UNDER_REVIEW.equalsIgnoreCase(prevLifeCycleState.toUpperCase())
					&& SMOutboundWebServiceConstants.SUPPLIER_LIFECYCLE_STATE_RELEASED.equals(currentLifeCycleState.toUpperCase())) {
				LOGGER.debug("*************************");
				return true;
			}
		}
		return false;
	}

	/**
	 * Check for State Change.
	 * @param lcsSupplier - LCSSupplier
	 * @param prevLifeCycleState2
	 * @return boolean
	 * @throws WTException - WTException
	 */
	public static boolean checkUnderReviewToCanceled(LCSSupplier lcsSupplierObject) throws WTException {
		LOGGER.debug("Inside checkUnderReviewToCanceled !!!!!!");
		String currentLifeCycleState = lcsSupplierObject.getLifeCycleState().toString();
		LOGGER.debug(CURRENT_LIFECYCLE_STATE+lcsSupplierObject.getLifeCycleState().toString());
		if(FormatHelper.hasContent((String)lcsSupplierObject.getValue(SMOutboundWebServiceConstants.SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY))){
			String prevLifeCycleState = (String)lcsSupplierObject.getValue(SMOutboundWebServiceConstants.SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY);
			if(SMOutboundWebServiceConstants.SUPPLIER_LIFECYCLE_STATE_UNDER_REVIEW.equalsIgnoreCase(prevLifeCycleState.toUpperCase()) && SMOutboundWebServiceConstants.SUPPLIER_LIFECYCLE_STATE_CANCELED.equalsIgnoreCase(currentLifeCycleState.toUpperCase())){
				LOGGER.debug("####################");
				return true;
			}
		}
		return false;

	}

	/**
	 * Check for State Change.
	 * @param lcsSupplier - LCSSupplier
	 * @param prevLifeCycleState
	 * @return boolean
	 * @throws WTException - WTException
	 */
	public static boolean checkReleasedToCanceled(LCSSupplier lcsSupplierObject) throws WTException {
		LOGGER.debug("Inside checkReleasedToCanceled !!!!!!");
		String currentLifeCycleState = lcsSupplierObject.getLifeCycleState().toString();
		LOGGER.debug(CURRENT_LIFECYCLE_STATE+lcsSupplierObject.getLifeCycleState().toString());
		if(FormatHelper.hasContent((String)lcsSupplierObject.getValue(SMOutboundWebServiceConstants.SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY))){
			String prevLifeCycleState = (String)lcsSupplierObject.getValue(SMOutboundWebServiceConstants.SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY);
			if(SMOutboundWebServiceConstants.SUPPLIER_LIFECYCLE_STATE_RELEASED.equalsIgnoreCase(prevLifeCycleState.toUpperCase()) && SMOutboundWebServiceConstants.SUPPLIER_LIFECYCLE_STATE_CANCELED.equalsIgnoreCase(currentLifeCycleState.toUpperCase())){
				LOGGER.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				return true;
			}
		}
		return false;
	}


	/**
	 * Validates Supplier Object is of particular type.
	 * @return boolean
	 */
	public static boolean validateSupplierObject(String supplierTypeName){
		if(supplierTypeName.equalsIgnoreCase(SMOutboundWebServiceConstants.BUSINESS_SUPPLIER) || supplierTypeName.equalsIgnoreCase(SMOutboundWebServiceConstants.MATERIAL_SUPPLIER) || supplierTypeName.equalsIgnoreCase(SMOutboundWebServiceConstants.FACTORY)){
			updateFlag=false;
			return true;
		}
		return false;
	}

	/**
	 * Get current lifecycle.
	 * @return the currLifeCycleState
	 */
	public static String getCurrLifeCycleState() {
		return currLifeCycleState;
	}

	/**
	 * Set Current Lifecycle.
	 * @param currLifeCycleState the currLifeCycleState to set
	 */
	public static void setCurrLifeCycleState(String currLifeCycleState) {
		SMSupplierPlugin.currLifeCycleState = currLifeCycleState;
	}

	/**
	 * Get Supplier.
	 * @return the lcsSupplier
	 */
	public static LCSSupplier getLcsSupplier() {
		return lcsSupplier;
	}

	/**
	 * Set Supplier.
	 * @param lcsSupplier the lcsSupplier to set
	 */
	public static void setLcsSupplier(LCSSupplier lcsSupplier) {
		SMSupplierPlugin.lcsSupplier = lcsSupplier;
	}


}



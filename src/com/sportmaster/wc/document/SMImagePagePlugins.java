package com.sportmaster.wc.document;

import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.util.FlexContainerHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.folder.Folder;
import wt.inf.container.ExchangeContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.StandardWfEngineService;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.engine.WfTransition;

/**
 * This class contains the logic for getting image page Document and deriving a
 * new name for Image Page, assigning new lifecycle template, starting workflow for the first task during first check in/ publish.
 * 
 * And formatImageName() is helper method to format the name by appending given
 * parameters.
 * 
 * @author 'true'
 * @version 'true' 1.0 version number
 */
public class SMImagePagePlugins {

	/**
	 * LOGGER.
	 */
	public static final Logger logger = Logger.getLogger(SMImagePagePlugins.class);

	/**
	 * pageType key
	 */
	public static final String PAGETYPE = LCSProperties.get("com.sportmaster.wc.document.SMImagePageWFPlugin.pageType");
	/**
	 * Detail Sketch key
	 */
	public static final String DETAILSKETCH = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.detailSketch");

	/**
	 * Image Page Name key
	 */
	public static final String IMGPAGENAME = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imgPageName");

	/**
	 * ownerReference key
	 */
	public static final String OWNERREFERENCE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.ownerReference");

	/**
	 * primaryBusinessObject
	 */
	static final String PRIMARY_BUSINESS_OBJECT = "primaryBusinessObject";

	/**
	 * Season key
	 */
	static final String SM_IMAGEPAGE_SEASON = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.smSeason", "smSeason");

	/**
	 * Page Type key
	 */
	public static final String SM_IMAGEPAGE_PAGE_TYPE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePagePlugins.pageType");

	/**
	 * Sample Type key
	 */
	public static final String SM_IMAGEPAGE_SAMPLE_TYPE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePagePlugins.sampleType");

	/**
	 * Description key
	 */
	public static final String SM_IMAGEPAGE_DESCRIPTION = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePagePlugins.pageDescription");

	/**
	 * IP Name key
	 */
	public static final String SM_IMAGEPAGE_NAME = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePagePlugins.name");

	/**
	 * SEPD IP LC Template key
	 */
	public static final String IMGSEPDLCTEMPLATE = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.imgLCTemplate");
	
	/**
	 * Partmaster key
	 */
	public static final String LCSPARTMASTER = "com.lcs.wc.part.LCSPartMaster";
	
	/**
	 * Image Life Cycle State In Work.
	 */
	public static final String IMAGE_LIFECYCLE_STATE_INWORK = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imageLifeCycleStateInWork");
	
	/**
	 * Designer approval key
	 */
	public static final String DESIGNERAPPROVAL = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smDesignerApprovalSEPD");

	/**
	 * Designer approval - Ready For Review key
	 */
	public static final String DESIGNERAPPROVALREADYFORREVIEW = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.smDesignerApprovalSEPD.readyForReview");
	
	/**
	 * Sportmaster custom method to derive name and set derived name as image page
	 * name.
	 * 
	 * @param wtobject
	 * @return
	 * 
	 */
	public static void setImagePageName(WTObject obj) {
		logger.debug("Start - Inside CLASS--SMImagePagePlugins and METHOD--setImagePageName");
		LCSPartMaster partMasterObj;
		LCSSeason seasonObj;
		LCSDocument imgObj;

		String currentImageName;
		String seasonName;
		String pageTypeVal;
		String sampleTypeVal;
		String partMaster;
		String styleName = "";
		String oridinalNumber;
		String pageDescription;

		try {
			// Check if obj instanceof LCSDocument
			if (obj instanceof LCSDocument) {
				imgObj = (LCSDocument) obj;
				seasonObj = (LCSSeason) imgObj.getValue(SM_IMAGEPAGE_SEASON);
				
				if(seasonObj != null){
					seasonName = seasonObj.getName();
					partMaster = (String) imgObj.getValue(OWNERREFERENCE);

					currentImageName = imgObj.getName();
					oridinalNumber = getOrdinalNumber(currentImageName);
					logger.debug("image page name plugin oridinalNumber="+oridinalNumber);
					// Check if partMaster contains value
					if (FormatHelper.hasContent(partMaster) && partMaster.contains(LCSPARTMASTER)) {
						partMasterObj = (LCSPartMaster) LCSQuery.findObjectById(partMaster);
						// Check if partMaster is not null
						if (partMasterObj != null) {
							styleName = partMasterObj.getName();
						}
					}
					pageTypeVal = imgObj.getFlexType().getAttribute(SM_IMAGEPAGE_PAGE_TYPE).getAttValueList()
							.getValue((String) imgObj.getValue(SM_IMAGEPAGE_PAGE_TYPE), null);
					logger.debug("pageTypeVal="+pageTypeVal);			
					
					sampleTypeVal = imgObj.getFlexType().getAttribute(SM_IMAGEPAGE_SAMPLE_TYPE).getAttValueList()
							.getValue((String) imgObj.getValue(SM_IMAGEPAGE_SAMPLE_TYPE), null);
					
					logger.debug("sampleTypeVal="+sampleTypeVal);
					
					pageDescription = (String) imgObj.getValue(SM_IMAGEPAGE_DESCRIPTION);
					logger.debug("pageDescription="+pageDescription);
					
					String imageName = formatImageName(oridinalNumber, pageTypeVal, styleName, seasonName, sampleTypeVal,
							pageDescription);
					logger.debug("derived image Name="+imageName);
					imgObj.setValue(SM_IMAGEPAGE_NAME, imageName);
				}
			}
		} catch (WTException e) {
			logger.error("WTException in setImagePageName-"+e.getLocalizedMessage());
			e.printStackTrace();
		}
		logger.debug("End - Inside CLASS--SMImagePagePlugins and METHOD--setImagePageName");
	}

	/**
	 * @param currentImageName
	 * @return
	 */
	private static String getOrdinalNumber(String currentImageName) {
		String oridinalNumber;
		oridinalNumber = FormatHelper.hasContent(currentImageName) && currentImageName.matches("[0-9].*")
				? currentImageName.split(":")[0].trim()
				: "";
		return oridinalNumber;
	}

	/**
	 * formatImageName() is helper method to format the name by appending passed
	 * parameters.
	 * 
	 * @param wtobject
	 * @return
	 * 
	 */
	public static String formatImageName(String oridinalNumber, String pageTypeVal, String styleName, String seasonName,
			String sampleTypeVal, String pageDescription) {
		logger.debug("Start - Inside CLASS--SMImagePagePlugins and METHOD--formatImageName");
		StringBuilder strBuilder = new StringBuilder();

		if (FormatHelper.hasContent(oridinalNumber)) {
			strBuilder.append(oridinalNumber).append(" ").append(":").append(" ");
		}
		strBuilder.append(pageTypeVal);

		if (FormatHelper.hasContent(styleName)) {
			strBuilder.append(" ").append(styleName);
		}

		strBuilder.append(" ").append(seasonName);

		if (FormatHelper.hasContent(sampleTypeVal)) {
			strBuilder.append(" ").append(sampleTypeVal);
		}

		if (FormatHelper.hasContent(pageDescription)) {
			strBuilder.append(" ").append(pageDescription);
		}
		logger.debug("image page name strBuilder.toString()="+strBuilder.toString());
		logger.debug("End - Inside CLASS--SMImagePagePlugins and METHOD--formatImageName");
		return strBuilder.toString();
	}

	/*
	 **
	 * setIPLifecycleTemplate - to set the LC Template for each product type.
	 * This method is called from PRE_CHECK_IN Event, so the template name change and task assignment will be done only on Publish from AI and not on upload thumbnail
	 * 
	 * @param object for object.
	 * 
	 * @return void. PRE_CHECK_IN
	 */
	public static void setIPLifecycleTemplatePreCheckIn(WTObject object) {
		
		logger.debug("start - Inside CLASS--SMImagePagePlugins and METHOD--setIPLifecycleTemplatePreCheckIn");
		LCSDocument imgPageObj = (LCSDocument) object;
		logger.debug("imgPageObj in setIPLifecycleTemplatePreCheckIn ="+imgPageObj);
		logger.debug("imgPageObj co info in setIPLifecycleTemplatePreCheckIn ="+imgPageObj.getCheckoutInfo().getState().toString());
		String imagePgType;
		try {
			imagePgType = (String) imgPageObj.getValue(PAGETYPE);
			String smImagePageSeasAtt = SM_IMAGEPAGE_SEASON;
			String partMaster = (String) imgPageObj.getValue(OWNERREFERENCE);
			LCSPartMaster partMasterObj;
			LCSProduct product;
			String newTemplate = "";
			String currentTemplateName = imgPageObj.getLifeCycleName();

			// Check if partMaster contains value
			if (FormatHelper.hasContent(partMaster) && partMaster.contains(LCSPARTMASTER)) {
				partMasterObj = (LCSPartMaster) LCSQuery.findObjectById(partMaster);
				LCSSeason season = (LCSSeason) imgPageObj.getValue(smImagePageSeasAtt);
				// Check if partMaster is not null
				if (partMasterObj != null && season != null) {
					product = VersionHelper.latestIterationOf(partMasterObj);
					logger.debug("Product Type==" + product.getFlexType().getFullName());

					newTemplate = getNewTemplateForEachProductType(product, newTemplate);
					logger.debug("currentTemplateName=" + currentTemplateName);
					logger.debug("newTemplate=" + newTemplate);
					// Reassign template only when if Document type = Images Page and Page Type =
					// Detail Sketch
					if (imgPageObj.getFlexType().getFullName(true).startsWith(IMGPAGENAME)
							&& imagePgType.equalsIgnoreCase(DETAILSKETCH)) {
						// reassingn LC Template
						reassignExistingImagePageLCTemplate(imgPageObj, newTemplate);
					}
				}
			}
		} catch (WTException e) {
			logger.error("WTException in setIPLifecycleTemplatePreCheckIn-"+e.getLocalizedMessage());
			e.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMImagePagePlugins and METHOD--setIPLifecycleTemplatePreCheckIn");
	}

	/**
	 * reassignExistingImagePageLCTemplate.
	 * 
	 * @param bReassignLC
	 * 
	 * @param object      for object.
	 * @return String.
	 * @throws WTPropertyVetoException
	 * 
	 *
	 */
	public static void reassignExistingImagePageLCTemplate(LCSDocument imgObj, String newTemplate) {
		logger.debug("start - Inside CLASS--SMImagePagePlugins and METHOD--reassignExistingImagePageLCTemplate");
		LCSProductSeasonLink psLink = null;
		String currentTemplateName = imgObj.getLifeCycleName();

		// Check if Image Page type lifecycle is not same as product type, then reassign
		// template.
		// Check if Image Page new life cycle contains value
		if (FormatHelper.hasContent(newTemplate) && FormatHelper.hasContent(currentTemplateName)
				&& !currentTemplateName.equals(newTemplate)) {
			// Method to reassign the LC template
			reassignExistingImagePageLC(imgObj, newTemplate);
		}
		logger.debug("end - Inside CLASS--SMImagePagePlugins and METHOD--reassignExistingImagePageLCTemplate");
	}

	/**
	 * Get the LC Template to be assigned for each product type
	 * @param product
	 * @param newTemplate
	 * @return
	 */
	private static String getNewTemplateForEachProductType(LCSProduct product, String newTemplate) {
		String strProductType = product.getFlexType().getFullName();
		logger.debug("strProductType=" + strProductType);
		if (strProductType.startsWith("SEPD")) {
			newTemplate = "Sportmaster SEPD Image Page Lifecycle";
		} else if (strProductType.startsWith("FPD")) {
			newTemplate = "Sportmaster Image Page Lifecycle";
		}
		return newTemplate;
	}

	/**
	 * Method to reassign Existing Image Page LC to new template and to change state and start WF
	 * @param obj
	 * @param newTemplate
	 */
	public static void reassignExistingImagePageLC(WTObject obj, String newTemplate) {
		logger.debug("start - Inside CLASS--SMImagePagePlugins and METHOD--reassignExistingImagePageLC");
		try {
			WTContainerRef exchangeRef;
			exchangeRef = WTContainerHelper.service.getExchangeRef();
			ExchangeContainer container = (ExchangeContainer) exchangeRef.getObject();
			DirectoryContextProvider dcp = container.getContextProvider();
			wt.org.WTOrganization org = OrganizationServicesHelper.manager
					.getOrganization(FlexContainerHelper.getOrganizationContainerName(), dcp);
			WTContainerRef containerRef = WTContainerHelper.service.getOrgContainerRef(org);
			LCSDocument imgObj = (LCSDocument) obj;
			logger.debug("imgObj in reassignExistingImagePageLC="+imgObj);
			logger.debug("imgObj co info in reassignExistingImagePageLC="+imgObj.getCheckoutInfo().getState().toString());
			logger.debug("newTemplate=" + newTemplate);
			final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
			try {
				LifeCycleTemplate typeNewLifecycleTemplate = LifeCycleHelper.service.getLifeCycleTemplate(newTemplate,
						containerRef);
				LifeCycleTemplateReference typeLifecycleRef = typeNewLifecycleTemplate.getLifeCycleTemplateReference();
				WTList objList = new WTArrayList();
				objList.add(obj);
				LCSDocumentLogic dl = new LCSDocumentLogic();
				// Check if Image Page type is checked out
				if (WorkInProgressHelper.isCheckedOut((Workable) imgObj)) {
					logger.debug("in reassignExistingImagePageLC method - isCheckedOut loop -imgObj="+imgObj);
					logger.debug("checkout info imgObj from isCheckedOut loop="+imgObj.getCheckoutInfo().getState().toString());
					logger.debug("objList from isCheckedOut loop=" + objList);
					WorkInProgressHelper.service.checkin(((Workable) imgObj), "");
					
					LifeCycleHelper.service.reassign(objList, typeLifecycleRef,
							((LCSDocument) obj).getContainerReference(), true);
					logger.debug("imgObj in isCheckedOut loop after reassign="+imgObj);
					logger.debug("c/o imgObj - isCheckedOut after reassign="+imgObj.getCheckoutInfo().getState().toString());
					
					PersistenceHelper.manager.refresh((Persistable) imgObj);
					
					final Folder msCheckedout = WorkInProgressHelper.service.getCheckoutFolder();
					Workable msWorkable = (Workable) imgObj;
					logger.debug("imgObj in isCheckedOut loop="+imgObj);
					logger.debug("check out info from isCheckedOut loop="+imgObj.getCheckoutInfo().getState().toString());
					
					// Check out the object again
					msWorkable = (Workable) WorkInProgressHelper.service.checkout(msWorkable, msCheckedout, "")
							.getWorkingCopy();
					
					logger.debug("imgObj in reassignExistingImagePageLC in isCheckedOut loop="+imgObj);
					logger.debug("checout info imgObj from isCheckedOut loop="+imgObj.getCheckoutInfo().getState().toString());
					logger.debug("msWorkable in reassignExistingImagePageLC in isCheckedOut loop="+msWorkable);
					logger.debug("checout info msWorkable from isCheckedOut loop="+msWorkable.getCheckoutInfo().getState().toString());
									
					// call method to set state as in work and start the IP task 1 on publish of IP only for SEPD IP WF
					setStateInWorkAndstartWFTaskFromAIPreCheckIn((LCSDocument)msWorkable);
				} else {
					logger.debug("objList from else=" + objList);
					imgObj = (LCSDocument) PersistenceHelper.manager.refresh((Persistable) imgObj);
					LifeCycleHelper.service.reassign(objList, typeLifecycleRef,
							((LCSDocument) obj).getContainerReference(), true);
					
					setStateInWorkAndstartWFTaskFromAIPreCheckIn((LCSDocument)imgObj);
					
					PersistenceHelper.manager.refresh((Persistable) imgObj);
				}
			} catch (LifeCycleException e) {
				logger.error("LifeCycleException in reassignExistingImagePageLC method: " + e.getMessage());
				e.printStackTrace();
			} finally {
				SessionServerHelper.manager.setAccessEnforced(old_enforced);
				imgObj = (LCSDocument) VersionHelper.latestIterationOf(imgObj.getMaster());
			}

		} catch (WTPropertyVetoException e) {
			logger.error("WTPropertyVetoException in reassignExistingImagePageLC method: " + e.getMessage());
			e.printStackTrace();
		} catch (WTException e) {
			logger.error("WTException in reassignExistingImagePageLC method: " + e.getMessage());
			e.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMImagePagePlugins and METHOD--reassignExistingImagePageLC");

	}

	private static LCSDocument setStateInWorkAfterReassingTemplate(LCSDocument imgObj)
			throws WTException, WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMImagePagePlugins and METHOD--setStateInWorkAfterReassingTemplate");
		logger.debug("setStateInWorkAfterReassingTemplate imgObj=="+imgObj);
		logger.debug("setStateInWorkAfterReassingTemplate checout info imgObj=="+imgObj.getCheckoutInfo().getState().toString());
		//SMSEPDImagePageWFPlugin.setSEPDImagePageData(imgObj);
		// Get PS Link object
		LCSProductSeasonLink lcsSeasonalProd = (LCSProductSeasonLink) SMImagePagePlugins
				.getSeasonalProductsForImagePages(imgObj);
		if (lcsSeasonalProd != null) {
			String strProductType = lcsSeasonalProd.getFlexType().getFullName();
			String currentTemplateName = imgObj.getLifeCycleName();
			LCSDocumentLogic dl = new LCSDocumentLogic();
			
			logger.debug("imgObj.isAImanaged()=="+dl.isAImanaged(imgObj));			
			logger.debug("In setStateInWorkAfterReassingTemplate method: currentTemplateName==" + currentTemplateName);
			logger.debug("setStateInWorkAfterReassingTemplate - strProductType="+strProductType);
			
			// Check if Image Page belongs to a Seasonal Product and the lifecycle tempalte
			// is "Sportmaster SEPD Image Page Lifecycle"
			if (strProductType.startsWith("SEPD") && currentTemplateName.equals(IMGSEPDLCTEMPLATE)) {
				logger.debug("setStateInWorkAfterReassingTemplate - if before setstate=="+imgObj.getLifeCycleState());
				logger.debug("setStateInWorkAfterReassingTemplate imgObj=="+imgObj);
				logger.debug("setStateInWorkAfterReassingTemplate checout info imgObj=="+imgObj.getCheckoutInfo().getState().toString());
				String designerApproval = (String) imgObj.getValue(DESIGNERAPPROVAL);
				if (FormatHelper.hasContent(designerApproval)
						&& designerApproval.equalsIgnoreCase(DESIGNERAPPROVALREADYFORREVIEW)
						&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_INWORK)) {
						if(!dl.isAImanaged(imgObj)){
							logger.debug("From not isAImanaged before calling setImagePageDataMethod=="+imgObj.getLifeCycleState().toString());
							SMSEPDImagePageWFPlugin.setImagePageDataMethod(imgObj);
							logger.debug("From not isAImanaged after calling setImagePageDataMethod=="+imgObj.getLifeCycleState().toString());

							// Save api, inturn will trigger all plugins again, this will call setSEPDImagePageStatePostUpdatePersist internally and start the in work WF task
							imgObj = dl.save(imgObj);
							logger.debug("From not isAImanaged after calling setImagePageDataMethod imgObj=="+imgObj);
							logger.debug("From not isAImanaged after calling setImagePageDataMethod LC State=="+imgObj.getLifeCycleState().toString());
						}
						else if(dl.isAImanaged(imgObj)) {
							logger.debug("From isAImanaged setStateInWorkAfterReassingTemplate imgObj=="+imgObj);
							logger.debug("From isAImanaged setStateInWorkAfterReassingTemplate checout info imgObj=="+imgObj.getCheckoutInfo().getState().toString());
							logger.debug("From isAImanaged before calling setStateInWork=="+imgObj.getLifeCycleState().toString());
							SMSEPDImagePageWFPlugin.setStateInWork(imgObj);
							logger.debug("From isAImanaged after calling setStateInWork=="+imgObj.getLifeCycleState().toString());
							logger.debug("From isAImanaged setStateInWorkAfterReassingTemplate imgObj=="+imgObj);
							logger.debug("From isAImanaged setStateInWorkAfterReassingTemplate checout info imgObj=="+imgObj.getCheckoutInfo().getState().toString());
							
							// Calling this instead of dl.save, because, save will check in the object and from AI DS, the object still needs to be checked out when publishing. Otherwise, it will throw error from DS, object is not chekced out.
							wt.fc.PersistenceServerHelper.manager.update(imgObj, false);
							
							logger.debug("From isAImanaged after calling update api=="+imgObj.getLifeCycleState().toString());
							logger.debug("From isAImanaged after calling update api imgObj=="+imgObj);
							logger.debug("From isAImanaged after calling update api checout info imgObj=="+imgObj.getCheckoutInfo().getState().toString());
						}
						 
				}				
			}
		}
		logger.debug("end - Inside CLASS--SMImagePagePlugins and METHOD--setStateInWorkAfterReassingTemplate");
		return imgObj;
	}

	/**
	 * getSeasonalProductsForImagePages - return the product season object.
	 * 
	 * @param bReassignLC
	 * 
	 * @param object      for object.
	 * @return String.
	 * @throws WTPropertyVetoException
	 * 
	 *
	 */
	public static WTObject getSeasonalProductsForImagePages(LCSDocument imgObj) {
		logger.debug("start - Inside CLASS--SMImagePagePlugins and METHOD--getSeasonalProductsForImagePages");
		LCSProductSeasonLink psLink = null;
		String smSeasonImgPage = SM_IMAGEPAGE_SEASON;
		try {
			String imagePgType = (String) imgObj.getValue(PAGETYPE);
			LCSPartMaster partMasterObj;
			LCSProduct product;
			String partMaster = (String) imgObj.getValue(OWNERREFERENCE);

			// Check if partMaster contains value
			if (FormatHelper.hasContent(partMaster) && partMaster.contains(LCSPARTMASTER)) {
				partMasterObj = (LCSPartMaster) LCSQuery.findObjectById(partMaster);
				// Check if partMaster is not null
				if (partMasterObj != null) {
					LCSSeason season = (LCSSeason) imgObj.getValue(smSeasonImgPage);
					if (season != null) {
						product = VersionHelper.latestIterationOf(partMasterObj);
						logger.debug("ProductType=" + product.getFlexType().getFullName());
						psLink = getPSLinkObject(imgObj, psLink, imagePgType, product, season);

					}
				}
			}
		} catch (WTException e) {
			logger.error(
					"WTException in SMImagePagePlugins - getSeasonalProductsForImagePages method: " + e.getMessage());
			e.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMImagePagePlugins and METHOD--getSeasonalProductsForImagePages");
		return psLink;
	}

	/**
	 * @param imgObj
	 * @param psLink
	 * @param imagePgType
	 * @param product
	 * @param season
	 * @return
	 * @throws WTException
	 */
	private static LCSProductSeasonLink getPSLinkObject(LCSDocument imgObj, LCSProductSeasonLink psLink,
			String imagePgType, LCSProduct product, LCSSeason season) throws WTException {
		logger.debug("start - Inside CLASS--SMImagePagePlugins and METHOD--getPSLinkObject");
		if (imgObj.getFlexType().getFullName(true).startsWith(IMGPAGENAME)
				&& imagePgType.equalsIgnoreCase(DETAILSKETCH)) {
			String strProductType = product.getFlexType().getFullName();
			// Getting psLink inside the loop, so the workflow will be triggered only for
			// the below product types. (this method will be called from fpd, sepd image page workflow java files
			if (strProductType.startsWith("SEPD") || strProductType.startsWith("FPD")) {
				psLink = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(product, season);
			}
			logger.debug("psLink=" + psLink);
		}
		logger.debug("end - Inside CLASS--SMImagePagePlugins and METHOD--getPSLinkObject");
		return psLink;
	}

	// changed private to public for Phase 13, and moved to SMImagePagePlugins class
	/**
	 * @param obj
	 * @param wfTemplateName
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static LCSDocument startWorkflow(WTObject obj, String wfTemplateName)
			throws WTException, WTPropertyVetoException {
		LCSDocument imgObj = (LCSDocument) obj;
		LCSDocument imgObjFirstVersion;
		logger.debug("SMImagePagePlugins startWorkflow method - Start");
		if (obj instanceof LCSDocument) {
			WTContainerRef exchangeRef = WTContainerHelper.service.getExchangeRef();
			ExchangeContainer container = (ExchangeContainer) exchangeRef.getObject();
			DirectoryContextProvider dcp = container.getContextProvider();
			WTOrganization org = OrganizationServicesHelper.manager
					.getOrganization(FlexContainerHelper.getOrganizationContainerName(), dcp);
			WTContainerRef containerRef = WTContainerHelper.service.getOrgContainerRef(org);
			WfProcessDefinition wfProcd = WfDefinerHelper.service.getProcessDefinition(wfTemplateName, containerRef);
			logger.debug("PersistenceHelper.isPersistent(wfProcd)=" + PersistenceHelper.isPersistent(wfProcd));
			logger.debug("wfProcd---->>>" + wfProcd);
			logger.debug("wfProcd.getName()---->>>" + wfProcd.getName());

			if (PersistenceHelper.isPersistent(wfProcd)) {
				logger.debug("imgObj---->>>" + imgObj);

				// Get first version of Image Page Object (only with the first version object,
				// we
				// are able to start the workflow task properly, as this logic internally
				// assigns some team templates)
				imgObjFirstVersion = (LCSDocument) VersionHelper.getFirstVersion(imgObj);
				PersistenceHelper.manager.refresh(imgObjFirstVersion);

				logger.debug("imgObj.getContainerReference()=== " + imgObj.getContainerReference());
				logger.debug("imgObjFirstVersion.getContainerReference()=First vers== "
						+ imgObjFirstVersion.getContainerReference());

				logger.debug("imgObj first vetrsion---->>>" + imgObjFirstVersion);
				try {
					WfProcessDefinition wfpd = WfDefinerHelper.service.getProcessDefinition(wfTemplateName,
							containerRef);

					logger.debug("wfpd ID= " + FormatHelper.getObjectId(wfpd));
					WfProcessTemplate wfpt = wfpd.getProcessTemplate();
					WfProcess aProcess = WfEngineHelper.service.createProcess(wfpt, imgObjFirstVersion,
							imgObjFirstVersion.getContainerReference());

					logger.debug("Object ID= " + FormatHelper.getObjectId(imgObj));
					logger.debug("aProcess ID= " + FormatHelper.getObjectId(aProcess));
					logger.debug(" aProcess--->>>" + aProcess);
					// Set the process name
					String processName = wfpd.getName() + " " + imgObj.getName();
					aProcess.setName(processName);
					logger.debug("processName ==" + processName);

					ProcessData context = aProcess.getContext();

					// Set the sample in the primaryBusinessObject attribute WF.
					context.setValue(PRIMARY_BUSINESS_OBJECT, imgObjFirstVersion);
					logger.debug("aProcess.getName()---->>>" + aProcess.getName());
					aProcess = WfEngineHelper.service.startProcess(aProcess, context, 1);
					logger.debug("Process==" + aProcess);
					// Refresh the object to avoid collection contains stale exception
					PersistenceHelper.manager.refresh(imgObjFirstVersion);
					PersistenceHelper.manager.refresh(imgObj);

				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error(
							"WTException in SMImagePagePlugins - startWorkflow method---" + e.getLocalizedMessage());

				}

			}
		}
		logger.debug("startWorkflow method - End");
		return imgObj;
	}

	/**
	 * Method to terminate the running workflow tasks
	 * 
	 * @param obj
	 * @throws WTPropertyVetoException
	 */
	public static void terminateWorkflowTask(WTObject obj) throws WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMImagePagePlugins and METHOD--terminateWorkflowTask");

		StandardWfEngineService service = new StandardWfEngineService();
		Enumeration processes;
		try {
			// Get the current running workflow tasks
			processes = service.getAssociatedProcesses(obj, WfState.OPEN_RUNNING);
			// Auto complete the current WF task
			while (processes.hasMoreElements()) {
				WfProcess process = (WfProcess) processes.nextElement();
				logger.debug(" Process--->>>>" + process);
				try {
					service.changeState(process, WfTransition.TERMINATE);
				} catch (WTException e) {
					e.printStackTrace();
					logger.error("WTException in terminateWorkflowTask=" + e.getMessage());
				}
			}
		} catch (WTException e1) {
			e1.printStackTrace();
			logger.error("WTException in terminateWorkflowTask=" + e1.getMessage());
		}
		logger.debug("end - Inside CLASS--SMImagePagePlugins and METHOD--terminateWorkflowTask");
	}
	
	
	/**
	 * Method to terminate the running workflow tasks
	 * 
	 * @param obj
	 * @throws WTPropertyVetoException
	 */
	public static void setStateInWorkAndstartWFTaskFromAIPreCheckIn(WTObject obj) {
		logger.debug("start - Inside CLASS--SMImagePagePlugins and METHOD--setStateInWorkAndstartWFTaskFromAIPreCheckIn");
		LCSDocument imgObj = (LCSDocument) obj;
		logger.debug("setStateInWorkAndstartWFTaskFromAIPreCheckIn imgObj=="+imgObj);

		// Method to call and set LC State as In Work from Created (only for first time publish) 
		try {
			LCSDocumentLogic dl = new LCSDocumentLogic();
			
			// Call set state in work method, which will execute only for SEPD product type
			imgObj = setStateInWorkAfterReassingTemplate(imgObj);
			
			logger.debug("setStateInWorkAndstartWFTaskFromAIPreCheckIn if after calling setStateInWorkAfterReassingTemplate=="+imgObj);
			logger.debug("setStateInWorkAndstartWFTaskFromAIPreCheckIn co="+imgObj.getCheckoutInfo().getState().toString());
			logger.debug("setStateInWorkAndstartWFTaskFromAIPreCheckIn imgObj=="+imgObj);
			
			// if published from Design Suite, expilcitely call start workflow method to start the task 1
			// For IP created from FlexPLM, we are calling save API from setStateInWorkAfterReassingTemplate, which will trigger post update persist method again to start the workflow for task1
			if(dl.isAImanaged(imgObj)) {
				logger.debug("imgObj in setStateInWorkAndstartWFTaskFromAIPreCheckIn if loop=="+imgObj.getLifeCycleState());
				logger.debug("Before start in work state task in method startWFTaskForEachState=="+imgObj.getLifeCycleState().toString());

				String designerApproval = (String) imgObj.getValue(DESIGNERAPPROVAL);
				if (FormatHelper.hasContent(designerApproval)
						&& designerApproval.equalsIgnoreCase(DESIGNERAPPROVALREADYFORREVIEW)
						&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_INWORK)) {
					logger.debug("Before start in work state task in method setStateInWorkAndstartWFTaskFromAIPreCheckIn from if loop=="+imgObj.getLifeCycleState().toString());
					
					// Call method to Start Task
					startWorkflow(imgObj,
							LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.inWorkTask"));
					
					logger.debug("After start in work state task in method setStateInWorkAndstartWFTaskFromAIPreCheckIn imgObj=="+imgObj);
					logger.debug("After start in work state task in method setStateInWorkAndstartWFTaskFromAIPreCheckIn lc state=="+imgObj.getLifeCycleState().toString());
				}
				
				logger.debug("After start in work state task in method setStateInWorkAndstartWFTaskFromAIPreCheckIn c/o info=="+imgObj.getCheckoutInfo().getState().toString());
			}
		} catch (WTPropertyVetoException e) {
			logger.error("WTPropertyVetoException in setStateInWorkAndstartWFTaskFromAIPreCheckIn=" + e.getMessage());
			e.printStackTrace();
		} catch (WTException e) {
			logger.error("WTException in setStateInWorkAndstartWFTaskFromAIPreCheckIn=" + e.getMessage());
			e.printStackTrace();
		}		
		logger.debug("end - Inside CLASS--SMImagePagePlugins and METHOD--setStateInWorkAndstartWFTaskFromAIPreCheckIn");
	}
}

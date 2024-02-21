package com.lowes.massimport.document;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.lcs.wc.document.IteratedDocumentReferenceLink;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentClientModel;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.VersionHelper;
import com.lowes.massimport.util.MassImport;

import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.util.WTException;

/***
 * Server side plugin to validate the Mass Import document and Primary Product
 * 
 * @author Samikkannu Manickam (samikkannu.manickam@lowes.com)
 *
 */
public class DocumentRefLinkServerSidePlugin {

	private static final Logger LOGGER = LogR.getLogger(DocumentRefLinkServerSidePlugin.class.getName());

	public static void validateMassImportDocument(WTObject object) throws Exception {
		LOGGER.info("Entering into DocumentRefLinkServerSidePlugin! ");
		if (object instanceof IteratedDocumentReferenceLink) {
			IteratedDocumentReferenceLink refLink = (IteratedDocumentReferenceLink) object;
			Persistable roleAObject = refLink.getRoleAObject();
			if (!(roleAObject instanceof FlexSpecification)) {
				LOGGER.info("ROLE A object is not a Flex Specification");
				return;
			}
			FlexSpecification flexSpec = (FlexSpecification) roleAObject;
			WTDocumentMaster roleBObject = (WTDocumentMaster) refLink.getRoleBObject();
			WTObject wtObject = VersionHelper.latestIterationOf(roleBObject);
			/** Checking if the ROLE B object is Mass Import Document */
			if (wtObject instanceof LCSDocument) {
				LCSDocument massImportDoc = (LCSDocument) wtObject;
				FlexType flexType = massImportDoc.getFlexType();
				String typeName = flexType.getTypeName();
				LOGGER.debug("Document type: " + typeName);
				/** Validate the RFP Product info only when adding the Mass Import document */
				if (MassImport.MASSIMPORT_DOC_TYPE.equals(typeName)) {
					validateRFPProduct(flexSpec, massImportDoc);
				}
			}

		}
	}

	private static void validateRFPProduct(FlexSpecification flexSpec, LCSDocument massImportDoc) throws Exception {
		LCSPartMaster partMaster = (LCSPartMaster) flexSpec.getSpecOwner();
		LCSProduct rfpProduct = (LCSProduct) VersionHelper.getVersion(partMaster, "A");
		LOGGER.info("Product: "+rfpProduct);
		String rfpValue = (String) rfpProduct.getValue(MassImport.PRODUCT_RFP_INTERNAL_ATTR);
		if (!MassImport.MASSIMPORT_DOC_RFP_ATTR_VALUE.equalsIgnoreCase(rfpValue)
				&& !MassImport.MASSIMPORT_DOC_PRIMARY_RFP_ATTR_VALUE.equalsIgnoreCase(rfpValue)) {
			throw new LCSException(
					"Mass Import document can't be associated with Non-RFP Product. Please associate the Mass Import document with RFP product or Primary RFP Product.");
		}
		LCSSeason season = null;
		Collection<?> seasonProductLinks = MassImport
				.getProductSeasonLinks(String.valueOf(rfpProduct.getBranchIdentifier()));
		Iterator<?> itr = seasonProductLinks.iterator();
		while (itr.hasNext()) {
			LCSProductSeasonLink seasonProductLink = (LCSProductSeasonLink) itr.next();
			season = (LCSSeason) VersionHelper.latestIterationOf(seasonProductLink.getSeasonMaster());
		}
		LOGGER.info("Season: "+season);
		if (season == null) {
			throw new LCSException(
					"RFP Product does not have an associatied season. Mass Import document can't be created");
		}

		WTPrincipal principal = massImportDoc.getModifier().getPrincipal();
		Map<String, Set<String>> authorizedUsersMap = MassImport.getAuthorizedUsers(principal);
		if (authorizedUsersMap.size() == 0) {
			throw new LCSException(
					"User does not have permission to create Mass Import. Only valid vendor user should able to create Mass Import document");
		}
		LCSSupplier supplier = getSupplier(flexSpec, authorizedUsersMap);
		if (supplier == null) {
			throw new LCSException(
					"RFP Product does not have valid sourcingconfig. Mass Import document can't be created");
		}
		LCSDocumentClientModel documentClient = new LCSDocumentClientModel();
		/** Update the document with RFP Product ref **/
		documentClient.load(FormatHelper.getObjectId(massImportDoc));
		documentClient.setValue(MassImport.MASSIMPORT_DOC_SEASON_ATTRIBUTE, season);
		documentClient.setValue(MassImport.MASSIMPORT_DOC_VENDOR_ATTRIBUTE, supplier);
		documentClient.setValue(MassImport.MASSIMPORT_DOC_RFP_INTERNAL_ATTR, rfpProduct);
		documentClient.save();
		massImportDoc = documentClient.getBusinessObject();
		/**
		 * Updating the Mass Import Document's Lifecycle State to INWORK to trigger the
		 * Workflow
		 **/
		massImportDoc = (LCSDocument) LifeCycleHelper.service.setLifeCycleState(massImportDoc, State.INWORK);
		PersistenceHelper.manager.refresh(massImportDoc);

	}

	private static LCSSupplier getSupplier(FlexSpecification flexSpec, Map<String, Set<String>> authoriziedUserMap)
			throws WTException {
		boolean isVendorUser = false;
		Set<String> userGroups = new HashSet<String>();
		if (authoriziedUserMap.containsKey(MassImport.VENDORS_GROUP_KEY)) {
			isVendorUser = true;
			userGroups = authoriziedUserMap.get(MassImport.VENDORS_GROUP_KEY);
		} else {
			userGroups = authoriziedUserMap.get(MassImport.ADMIN_GROUP_KEY);
		}
		LCSSourcingConfigMaster sourcingMaster = (LCSSourcingConfigMaster) flexSpec.getSpecSource();
		LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourcingMaster);
		LCSSupplier supplier = (LCSSupplier) sourcingConfig.getValue(MassImport.VENDOR);
		LOGGER.info("Supplier : " + supplier);
		if (supplier != null) {
			/** Check if user is vendor user **/
			if (isVendorUser && userGroups.contains(supplier.getName())) {
				return supplier;
			} /** If user is not vendor user then he should be admin user */
			else if (!isVendorUser && userGroups.size() > 0) {
				return supplier;
			}
		}
		return null;
	}

}

package com.lowes.type.metadata.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;

import com.lowes.type.metadata.pojo.AttributesMetaData;
import com.lowes.type.metadata.pojo.EnumEntry;
import com.lowes.type.metadata.pojo.EnumMetaData;
import com.lowes.type.metadata.pojo.TypeAttributesMetaData;
import com.ptc.core.lwc.common.TypeDefinitionService;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionViewComparator;
import com.ptc.core.lwc.common.view.ConstraintRuleDefinitionReadView;
import com.ptc.core.lwc.common.view.EnumerationDefinitionReadView;
import com.ptc.core.lwc.common.view.EnumerationEntryReadView;
import com.ptc.core.lwc.common.view.GroupDefinitionReadView;
import com.ptc.core.lwc.common.view.GroupMemberView;
import com.ptc.core.lwc.common.view.GroupMembershipReadView;
import com.ptc.core.lwc.common.view.LayoutComponentReadView;
import com.ptc.core.lwc.common.view.LayoutDefinitionReadView;
import com.ptc.core.lwc.common.view.PropertyValueReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.meta.container.common.impl.DiscreteSetConstraint;
import com.ptc.core.meta.container.common.impl.ValueRequiredConstraint;

import wt.access.NotAuthorizedException;
import wt.inf.container.WTContainerException;
import wt.log4j.LogR;
import wt.services.ServiceFactory;
import wt.util.WTException;

/****
 * Service to extract Flex PLM attributes and their constraints
 * 
 * @author Samikkannu Manickam (samikkannu.manickam@lowes.com)
 *
 */
public class PLMTypeAttributesMetadataService {

	private static final String FLEX_VARIABLE_TYPE = "flexVariableType";
	private static final String RETAIL_LAYOUT = "Retail";
	private static final String DISPLAYNAME = "displayName";
	private static final String SELECTABLE = "selectable";
	private static final String FLEX_OBJECT_REF = "object_ref";
	private static final String FLEX_OTHER_SIDE_CLASS = "other_side_class";
	private static PLMTypeAttributesMetadataService plmTypeAttributesMetadataService = null;

	private static final Logger LOGGER = LogR.getLogger(PLMTypeAttributesMetadataService.class.getName());
	private static final TypeDefinitionService TYPE_DEF_SERVICE = ServiceFactory
			.getService(TypeDefinitionService.class);

	private PLMTypeAttributesMetadataService() {
	}

	public static PLMTypeAttributesMetadataService getTypeSeriveInstance() {
		if (plmTypeAttributesMetadataService == null) {
			plmTypeAttributesMetadataService = new PLMTypeAttributesMetadataService();
		}
		return plmTypeAttributesMetadataService;
	}

	public TypeAttributesMetaData getPLMTypelMetaData(String typeName)
			throws NotAuthorizedException, WTContainerException, WTException {
		TypeDefinitionReadView typeDefReadView = TYPE_DEF_SERVICE.getTypeDefView(typeName);
		LOGGER.info("Root Type definition name: " + typeDefReadView.getName());
		TypeAttributesMetaData plmTypeMetaData = extractTypeMetaData(typeDefReadView);
		return plmTypeMetaData;
	}

	private TypeAttributesMetaData extractTypeMetaData(TypeDefinitionReadView typeDefReadView) throws WTException {
		TypeAttributesMetaData plmTypeMetaData = new TypeAttributesMetaData();
		String internalName = typeDefReadView.getName();
		String displayName = typeDefReadView.getDisplayName();
		LOGGER.info("Type display name: " + displayName + " and internal name: " + internalName);
		plmTypeMetaData.setTypeDisplayName(displayName);
		plmTypeMetaData.setTypeInternalName(internalName);
		plmTypeMetaData.setFlexTypeId(typeDefReadView.getId());
		String typePathId = typeDefReadView.getTypePath();
		if (typePathId == null || typePathId.isEmpty()) {
			throw new WTException(
					displayName + " type does not have valid type path. Type path should not be null or empty.");
		}
		plmTypeMetaData.setType(typePathId);
		plmTypeMetaData.setAttributeMetaDataMap(extractAttributesMetaData(typeDefReadView));
		return plmTypeMetaData;
	}

	private Map<String, AttributesMetaData> extractAttributesMetaData(TypeDefinitionReadView typeDefinitionReadView)
			throws WTException {
		Collection<LayoutDefinitionReadView> layoutCollections = typeDefinitionReadView.getAllLayouts();
		LayoutDefinitionReadView retailLayoutDefReadView = null;
		for (Iterator<LayoutDefinitionReadView> itr = layoutCollections.iterator(); itr.hasNext();) {
			LayoutDefinitionReadView layoutDefReadView = itr.next();
			String layoutName = layoutDefReadView.getName();
			LOGGER.info("Layout name: " + layoutName);
			if (RETAIL_LAYOUT.equals(layoutName)) {
				retailLayoutDefReadView = layoutDefReadView;
				break;
			}
		}
		if (retailLayoutDefReadView == null) {
			LOGGER.error("No Retail layout found for [" + typeDefinitionReadView.getName() + "]");
			return new HashMap<>();
		}
		return getFlexAttributesMetaData(retailLayoutDefReadView);
	}

	private Map<String, AttributesMetaData> getFlexAttributesMetaData(LayoutDefinitionReadView retailLayoutDefReadView)
			throws WTException {
		Map<String, AttributesMetaData> attributeMetaDataMap = new HashMap<>();
		Collection<?> componentCollections = retailLayoutDefReadView.getAllLayoutComponents();
		for (Iterator<?> compIter = componentCollections.iterator(); compIter.hasNext();) {
			LayoutComponentReadView layoutCompReadView = (LayoutComponentReadView) compIter.next();
			List<?> grpMembershipReadViews = new ArrayList<>();
			if (layoutCompReadView instanceof GroupDefinitionReadView) {
				GroupDefinitionReadView grpMemberShipReadDefView = (GroupDefinitionReadView) layoutCompReadView;
				grpMembershipReadViews = grpMemberShipReadDefView.getAllMembers();
			}
			for (Iterator<?> grpMemIter = grpMembershipReadViews.iterator(); grpMemIter.hasNext();) {
				GroupMembershipReadView grpMemberShipReadView = (GroupMembershipReadView) grpMemIter.next();
				GroupMemberView grpMemberView = grpMemberShipReadView.getMember();
				if (grpMemberView instanceof AttributeDefinitionReadView) {
					AttributeDefinitionReadView attrDefView = (AttributeDefinitionReadView) grpMemberView;
					String internalName = attrDefView.getName();
					String displayName = attrDefView.getDisplayName();
					LOGGER.info("Internal name: " + internalName + " display name: " + displayName);
					/** Flex Attribute data type */
					String flexDataType = (String) attrDefView.getPropertyValueByName(FLEX_VARIABLE_TYPE).getValue();
					LOGGER.info("Flex data type: " + flexDataType);
					AttributesMetaData attributeMetaData = new AttributesMetaData();
					attributeMetaData.setAttributeDisplayName(displayName);
					attributeMetaData.setAttributeInternalName(internalName);
					/** Attribute data type */
					String attributeDataType = attrDefView.getDatatype().getName();
					LOGGER.info("Attribute data type: " + attributeDataType);
					attributeMetaData.setFlexDataType(flexDataType);
					if(FLEX_OBJECT_REF.equals(flexDataType)) {
						String objectRefClass = (String) attrDefView.getPropertyValueByName(FLEX_OTHER_SIDE_CLASS).getValue();
						attributeMetaData.setObjectRefClass(objectRefClass);
					}

					TreeSet<ConstraintDefinitionReadView> attrConstraintSet = new TreeSet<>(
							ConstraintDefinitionViewComparator.getInstance());
					attrConstraintSet.addAll(attrDefView.getAllConstraints());
					boolean isRequiredAttribute = hasRequiredValueConstraint(attrConstraintSet);
					attributeMetaData.setRequiredAttribute(isRequiredAttribute);
					List<EnumMetaData> enumValueList = getEnumConstraint(attrConstraintSet);
					attributeMetaData.setEnumValueList(enumValueList);
					attributeMetaDataMap.put(displayName, attributeMetaData);
				}
			}
		}
		return attributeMetaDataMap;

	}

	private List<EnumMetaData> getEnumConstraint(TreeSet<ConstraintDefinitionReadView> attrConstraintSet)
			throws WTException {
		List<EnumMetaData> enumerationList = new ArrayList<>();
		for (Iterator<?> itr = attrConstraintSet.iterator(); itr.hasNext();) {
			ConstraintDefinitionReadView consReadView = (ConstraintDefinitionReadView) itr.next();
			LOGGER.info("Enumeration Constraint attribute: " + consReadView.getAttName());
			boolean isConstraintDisabled = consReadView.isDisabled();
			LOGGER.info("Is Constraint disabled: " + isConstraintDisabled);
			ConstraintRuleDefinitionReadView ruleDef = consReadView.getRule();
			if (ruleDef == null || isConstraintDisabled) {
				continue;
			}
			String constraintClassName = ruleDef.getRuleClassname();
			if (DiscreteSetConstraint.class.getName().equalsIgnoreCase(constraintClassName)) {
				EnumerationDefinitionReadView enumDefReadView = consReadView.getEnumDef();
				String enumMasterName = enumDefReadView.getName();
				long enumMasterId = enumDefReadView.getId();
				if (enumMasterName == null || enumMasterId == 0.0) {
					continue;
				}
				EnumMetaData enumMetaData = getMaterailEnumMetaData(enumDefReadView, enumMasterName, enumMasterId);
				enumerationList.add(enumMetaData);
			}

		}
		return enumerationList;
	}

	private boolean hasRequiredValueConstraint(TreeSet<ConstraintDefinitionReadView> attrConstraintSet) {
		boolean isRequiredValue = false;
		for (Iterator<?> itr = attrConstraintSet.iterator(); itr.hasNext();) {
			ConstraintDefinitionReadView consReadView = (ConstraintDefinitionReadView) itr.next();
			boolean isConstraintDisabled = consReadView.isDisabled();
			ConstraintRuleDefinitionReadView ruleDef = consReadView.getRule();
			LOGGER.info("Is Constraint disabled: " + isConstraintDisabled);
			if (ruleDef == null || isConstraintDisabled) {
				continue;
			}
			String constraintClassName = ruleDef.getRuleClassname();
			if (ValueRequiredConstraint.class.getName().equalsIgnoreCase(constraintClassName)) {
				LOGGER.info("Found Required value constraint for attribute: " + consReadView.getAttName());
				return true;
			}
		}
		return isRequiredValue;
	}

	private EnumMetaData getMaterailEnumMetaData(EnumerationDefinitionReadView readView, String enumMasterName,
			long enumMasterId) throws WTException {
		EnumMetaData enumMetaData = new EnumMetaData();
		enumMetaData.setEnumInternalName(enumMasterName);
		if (readView != null) {
			String enumMasterDisplayName = (String) readView.getPropertyValueByName(DISPLAYNAME).getValue();
			enumMetaData.setEnumDisplayName(enumMasterDisplayName);
			enumMetaData.setFlexEnumMasterId(enumMasterId);
			LOGGER.info("Enumeration master display name: " + enumMasterDisplayName);
			Map<String, EnumerationEntryReadView> enumEntryMap = readView.getAllEnumerationEntries();
			List<EnumEntry> enumEntryList = new ArrayList<>();
			for (Map.Entry<String, EnumerationEntryReadView> entryMap : enumEntryMap.entrySet()) {
				String enumEntryInternalName = entryMap.getKey();
				LOGGER.info("Enumeration Entry internal name: " + enumEntryInternalName);
				EnumerationEntryReadView enumEntryReadView = entryMap.getValue();
				PropertyValueReadView selectablepropReadView = enumEntryReadView.getPropertyValueByName(SELECTABLE);
				if (selectablepropReadView == null) {
					continue;
				}
				boolean isSeletable = (boolean) selectablepropReadView.getValue();
				/** We are extracting enumeration entries which are in the selectable list */
				if (!isSeletable) {
					continue;
				}
				String enumEntryDisplayName = "";
				PropertyValueReadView propvalueReadView = enumEntryReadView.getPropertyValueByName(DISPLAYNAME);
				if (propvalueReadView != null) {
					enumEntryDisplayName = (String) propvalueReadView.getValue();
					LOGGER.info("Enumeration Entry display name: " + enumEntryDisplayName);
					EnumEntry enumEntry = new EnumEntry(enumEntryDisplayName, enumEntryInternalName);
					enumEntryList.add(enumEntry);
				}

			}
			enumMetaData.setEnumEntries(enumEntryList);
			LOGGER.info("Enumeration entry List : " + enumEntryList);

		}
		LOGGER.info("Enumeration object: " + enumMetaData.toString());
		return enumMetaData;
	}

}

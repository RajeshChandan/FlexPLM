package com.lowes.notification.service;

import com.google.gson.Gson;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lowes.model.CostsheetModel;
import com.lowes.model.SourcingConfigurationModel;
import com.lowes.notification.model.config.*;
import org.apache.logging.log4j.Logger;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.vc.Iterated;

import java.util.*;

/**
 * This method checks the criteria for triggering email notification
 */
public class NotificationCriteriaValidator {

    private static final Logger LOGGER = LogR.getLogger(NotificationCriteriaValidator.class.getName());
    private static final String EXACT_CONDITION = "EXACT_CONDITION";
    private static final String EXACT_CONDITION_SATISFIED = "EXACT_CONDITION_SATISFIED";

    private static final String ANY_CHANGE = "ANY_CHANGE";
    private static final String ANY_CHANGE_SATISFIED = "ANY_CHANGE_SATISFIED";

    private static final String FIXED_CHANGE = "FIXED_CHANGE";
    private static final String FIXED_CHANGE_SATISFIED = "FIXED_CHANGE_SATISFIED";

    private static final String YES = "YES";
    private static final String NO = "NO";
    private static final String EQUALS = "==";
    private static final String NOT_EQUALS = "!=";

    /**
     * Check criteria
     *
     * @param wtObj              WTObject
     * @param notificationObject NotificationObject
     * @param prevWtobj          WTObject
     * @return Boolean
     * @throws WTException WTException
     */
    public Boolean checkCriteria(WTObject wtObj, NotificationObject notificationObject, WTObject prevWtobj)
            throws WTException {
        LOGGER.debug("checkCriteria- Start");
        Boolean triggerEmail = false;

        //validationType = and / or
        ValidationType validationType = notificationObject.getValidationType();
        List<Criteria> criteriaArr = notificationObject.getCriteria();
        if (criteriaArr.isEmpty()) {
            LOGGER.debug("checkCriteria- No Criteria Values, no email");
            LOGGER.debug("checkCriteria- validation return Ended");
            return false;
        }
        LOGGER.debug("checkCriteria- Total criteria to be processed {}", criteriaArr.size());
        LOGGER.debug("checkCriteria- criteria to be processed {}", criteriaArr);

        HashMap<String, Boolean> statusMap = new HashMap<>();

        for (Criteria criteria : criteriaArr) {
            CriteriaType criteriaType = criteria.getCriteriaType();
            switch (criteriaType) {
                case TRIGGERED_OBJECT:
                    statusMap.putAll(validateSubCriteria(criteria, wtObj, prevWtobj));
                    break;
                case LINKED_OBJECT:
                case REFERENCED_OBJECT:
                    statusMap.putAll(linkedObjectCriteriaCheck(wtObj, criteria));
                    break;
                default:
                    break;
            }
        }
        LOGGER.debug("checkCriteria- statusMap {}", statusMap);

        //if type add
        if (validationType.equals(ValidationType.AND)) {
            triggerEmail = !statusMap.containsValue(Boolean.FALSE);

        } else if (validationType.equals(ValidationType.OR)) {
            triggerEmail = statusMap.containsValue(Boolean.TRUE);
        }
        LOGGER.debug("checkCriteria- triggerEmail {} end", triggerEmail);
        return triggerEmail;
    }

    /**
     * To check the linked object
     *
     * @param wtObj    WTObject
     * @param criteria Criteria
     * @return HashMap<String, Boolean>
     * @throws WTException
     */
    private HashMap<String, Boolean> linkedObjectCriteriaCheck(WTObject wtObj, Criteria criteria) throws WTException {

        LOGGER.debug("linkedObjectCriteriaCheck-  Start");

        HashMap<String, Boolean> statusMap = new HashMap<>();
        Gson gson = new Gson();
        Criteria criteriaClone = gson.fromJson(gson.toJson(criteria), Criteria.class);
        CriteriaType criteriaType = criteriaClone.getCriteriaType();
        String criteriaValue = criteriaClone.getCriteriaValue();
        if (!criteriaValue.contains("~")) {
            //return false as condition not valid.
            statusMap.put(criteriaClone.toString(), false);
            LOGGER.debug("linkedObjectCriteriaCheck-  statusMap {}", statusMap);
            LOGGER.debug("linkedObjectCriteriaCheck-  End");
            return statusMap;
        }
        if (!criteriaClone.getCriteriaSubType().equals(CriteriaSubType.EXACT_CONDITION)) {
            //return false as condition not valid.
            statusMap.put(criteriaClone.toString(), false);
            LOGGER.debug("linkedObjectCriteriaCheck - statusMap {}", statusMap);
            LOGGER.debug("linkedObjectCriteriaCheck-  End");
            return statusMap;
        }

        String objVal = criteriaValue.split("~")[0];
        criteriaClone.setCriteriaValue(criteriaValue.split("~")[1]);
        WTObject flexTyped = null;
        WTObject prevFlexTyped = null;

        switch (criteriaType) {
            case LINKED_OBJECT:
                flexTyped = getFlexObject(objVal, wtObj);
                break;
            case REFERENCED_OBJECT:
                flexTyped = (WTObject) ((FlexTyped) wtObj).getValue(objVal);
                break;
            default:
                break;
        }

        if (Objects.isNull(flexTyped)) {
            statusMap.put(criteriaClone.toString(), false);
            LOGGER.debug("linkedObjectCriteriaCheck- flexTyped is null>>>>> {}", flexTyped);
            LOGGER.debug("linkedObjectCriteriaCheck- statusMap {}", statusMap);
            LOGGER.debug("linkedObjectCriteriaCheck -  End");

            return statusMap;
        }
        prevFlexTyped = (WTObject) VersionHelper.predecessorOf((Iterated) flexTyped);
        statusMap = validateSubCriteria(criteriaClone, flexTyped, prevFlexTyped);
        LOGGER.debug("linkedObjectCriteriaCheck- statusMap {}", statusMap);
        LOGGER.debug("linkedObjectCriteriaCheck - End");
        return statusMap;
    }

    /**
     * To validate sub criteria
     *
     * @param criteria  Criteria
     * @param wtObj     WTObject
     * @param prevWtObj WTObject
     * @return HashMap<String, Boolean>
     * @throws WTException WTException
     */
    private HashMap<String, Boolean> validateSubCriteria(Criteria criteria, WTObject wtObj, WTObject prevWtObj) throws WTException {

        LOGGER.debug("validateSubCriteria- Start");

        HashMap<String, Boolean> statusMap = new HashMap<>();
        boolean status = false;
        if (Objects.isNull(wtObj)) {
            statusMap.put(criteria.toString(), false);
            return statusMap;
        }
        FlexTyped fTypedCurrent = (FlexTyped) wtObj;
        FlexTyped fTypedPrevious = null;
        if (Objects.nonNull(prevWtObj)) {
            fTypedPrevious = (FlexTyped) prevWtObj;
        }
        if (wtObj instanceof Iterated) {
            fTypedPrevious = (FlexTyped) VersionHelper.predecessorOf((Iterated) fTypedCurrent);
        }
        LOGGER.debug("validateSubCriteria-  previous {}", fTypedPrevious);
        LOGGER.debug("validateSubCriteria-  current {}", fTypedCurrent);

        CriteriaSubType criteriaSubType = criteria.getCriteriaSubType();
        switch (criteriaSubType) {
            case EXACT_CONDITION:
                LOGGER.debug("validateSubCriteria-  inside EXACT_CONDITION {}", criteriaSubType.value());
                status = exactChangeCriteriaCheck(fTypedCurrent, criteria);
                statusMap.put(criteria.toString(), status);
                break;
            case ANY_CHANGE:
                LOGGER.debug("validateSubCriteria-  inside ANY_CHANGE {}", criteriaSubType.value());
                status = anyChangeCriteriaCheck(fTypedCurrent, fTypedPrevious, criteria);
                statusMap.put(criteria.toString(), status);
                break;
            case FIXED_CHANGE:
                LOGGER.debug("validateSubCriteria- inside FIXED_CHANGE {}", criteriaSubType.value());
                status = fixedChangeCriteriaCheck(fTypedCurrent, fTypedPrevious, criteria);
                statusMap.put(criteria.toString(), status);
                break;
            default:
                LOGGER.debug("validateSubCriteria-  inside default {}", criteriaSubType.value());
                statusMap.put(criteria.toString(), status);
                break;
        }
        LOGGER.debug("validateSubCriteria-  statusMap {}", statusMap);
        LOGGER.debug("validateSubCriteria- End");
        return statusMap;
    }

    /**
     * To check for exact change in attribute value from previous to current
     *
     * @param ftypedCurrent FlexTyped
     * @param criteria      Criteria
     * @throws WTException WTException
     */
    private boolean exactChangeCriteriaCheck(FlexTyped ftypedCurrent, Criteria criteria) throws WTException {

        String methodName = "exactChangeCriteriaCheck- ";
        LOGGER.debug(methodName, "{} Start");

        HashMap<String, Boolean> exactChangeMap = new HashMap<>();
        boolean valid = false;
        String attKey = "";
        String attValue = "";
        String[] keyValue;
        //present value to be json attribute value
        //check fort equal sign == or not equal sign !=
        if (criteria.getCriteriaValue().contains(NOT_EQUALS)) {
            keyValue = criteria.getCriteriaValue().split(NOT_EQUALS);
            attKey = keyValue[0];
            attValue = keyValue[1];
            valid = validateNotEquals(ftypedCurrent, attKey, attValue);
        }

        if (criteria.getCriteriaValue().contains(EQUALS)) {
            keyValue = criteria.getCriteriaValue().split(EQUALS);
            attKey = keyValue[0];
            attValue = keyValue[1];
            valid = validateEquals(ftypedCurrent, attKey, attValue);
        }
        //For e.g. lwsQuoteStatus=released
        exactChangeMap.put(EXACT_CONDITION, true);
        exactChangeMap.put(EXACT_CONDITION_SATISFIED, valid);

        LOGGER.debug(methodName, "{} exactChangeMap  {}", exactChangeMap);
        LOGGER.debug(methodName, "{} End");
        return valid;
    }


    /**
     * SUPPORTS NEED 2INPUT VALUE ATTR1==VALUE1|VALUE2
     *
     * @param ftypedCurrent  FlexTyped
     * @param ftypedPrevious FlexTyped
     * @param criteria       Criteria
     * @throws WTException WTException
     */
    private boolean fixedChangeCriteriaCheck(FlexTyped ftypedCurrent, FlexTyped ftypedPrevious, Criteria criteria) throws WTException {

        LOGGER.debug("fixedChangeCriteriaCheck-  Start");

        if (Objects.isNull(ftypedPrevious)) {
            return false;
        }
        HashMap<String, String> fixedChangeMap = new HashMap<>();
        boolean valid = false;
        fixedChangeMap.put(FIXED_CHANGE, YES);
        fixedChangeMap.put(FIXED_CHANGE_SATISFIED, NO);

        //This is for FIXED_CHANGE in attributes listed in the JSON file
        String[] attributeKeyValue = criteria.getCriteriaValue().split(EQUALS);
        if (attributeKeyValue[1].contains("|")) {
            String[] attributeValue = attributeKeyValue[1].split("|");
            //attribute value changed from attributeValue[0] to attributeValue[1]
            LOGGER.debug("fixedChangeCriteriaCheck-  Checking if attribute value changed from + attributeValue[0] {} to attributeKeyValue[1] {}", attributeValue[0], attributeKeyValue[1]);

            LOGGER.debug("fixedChangeCriteriaCheck- ftypedPrevious.getValue(attributeKeyValue[0]) {}", ftypedPrevious.getValue(attributeKeyValue[0]));
            LOGGER.debug("fixedChangeCriteriaCheck-  ftypedCurrent.getValue(attributeKeyValue[0]) {}", ftypedCurrent.getValue(attributeKeyValue[0]));
            String prevValue = (String) ftypedPrevious.getValue(attributeKeyValue[0]);
            String currentValue = (String) ftypedCurrent.getValue(attributeKeyValue[0]);

            if (prevValue.equalsIgnoreCase(attributeValue[0]) && currentValue.equalsIgnoreCase(attributeValue[1])) {
                //Condition matched, send mail
                fixedChangeMap.put(FIXED_CHANGE_SATISFIED, YES);
                valid = true;
            } else {
                fixedChangeMap.put(FIXED_CHANGE_SATISFIED, NO);
                valid = false;
            }
        } else {
            LOGGER.debug("fixedChangeCriteriaCheck- Checking if attribute value changed from any to attributeKeyValue[1] {}", attributeKeyValue[1]);
            //attribute value changed from any to attributeKeyValue[1]
            LOGGER.debug("fixedChangeCriteriaCheck-  ftypedPrevious.getValue(attributeKeyValue[0]) {}", ftypedPrevious.getValue(attributeKeyValue[0]));
            LOGGER.debug("fixedChangeCriteriaCheck-  ftypedCurrent.getValue(attributeKeyValue[0]) {}", ftypedCurrent.getValue(attributeKeyValue[0]));
            String prevValue = (String) ftypedPrevious.getValue(attributeKeyValue[0]);
            String currentValue = (String) ftypedCurrent.getValue(attributeKeyValue[0]);

            if (!prevValue.equalsIgnoreCase(attributeKeyValue[1]) && currentValue.equalsIgnoreCase(attributeKeyValue[1])) {
                //Condition matched, send mail
                fixedChangeMap.put(FIXED_CHANGE_SATISFIED, YES);
                valid = true;
            } else {
                fixedChangeMap.put(FIXED_CHANGE_SATISFIED, NO);
                valid = false;
            }
        }
        LOGGER.debug("fixedChangeCriteriaCheck-  fixedChangeMap  {}", fixedChangeMap);
        LOGGER.debug("fixedChangeCriteriaCheck- End");
        return valid;
    }

    /**
     * WITHOUT INPUT VALUE
     * WITH SPEFICIC INPUT VLAUE (ANY THING TO GIVEN INPUT VALUE)
     * EX: ATT1,ATTR2,ATTR3==RELEASED,ATTR4
     *
     * @param ftypedCurrent  FlexTyped
     * @param ftypedPrevious FlexTyped
     * @param criteria       Criteria
     * @return boolean
     * @throws WTException WTException
     */
    private boolean anyChangeCriteriaCheck(FlexTyped ftypedCurrent, FlexTyped ftypedPrevious, Criteria criteria) throws WTException {

        LOGGER.info("anyChangeCriteriaCheck-  starts");
        boolean valid = false;
        HashMap<String, String> anyChangeMap = new HashMap<>();
        anyChangeMap.put(ANY_CHANGE, YES);
        anyChangeMap.put(ANY_CHANGE_SATISFIED, NO);
        List<String> attributeValueChangedList = new ArrayList<>();
        //This is for any change in attributes listed in the JSON file
        StringTokenizer flexAttKeys = new StringTokenizer(criteria.getCriteriaValue(), ",");
        while (flexAttKeys.hasMoreTokens()) {
            String attributeRule = flexAttKeys.nextToken();
            String attributeKey = attributeRule;
            String attributeValue = "";
            if (attributeRule.contains(EQUALS)) {
                attributeKey = attributeRule.split(EQUALS)[0];
                attributeValue = attributeRule.split(EQUALS)[1];
            }
            LOGGER.debug("anyChangeCriteriaCheck- attributeRule {}", attributeRule);
            LOGGER.debug("anyChangeCriteriaCheck- attributeKey {}", attributeKey);
            LOGGER.debug("anyChangeCriteriaCheck- attributeValue {}", attributeValue);
            LOGGER.debug("anyChangeCriteriaCheck- ftypedCurrent.getValue(attributeKey) {}", ftypedCurrent.getValue(attributeKey));

            String prevValue = getAttributeValue(ftypedPrevious, attributeKey);
            String currentValue = (String) ftypedCurrent.getValue(attributeKey);
            LOGGER.debug("anyChangeCriteriaCheck- currentValue {}", currentValue);
            LOGGER.debug("anyChangeCriteriaCheck- prevValue {}", prevValue);
            /*
             * All the attributes should be changed then only it is considered for email.
             * Used AND logic below for Any Change
             */
            if (FormatHelper.hasContent(attributeValue) && !attributeValue.equalsIgnoreCase(prevValue) && attributeValue.equalsIgnoreCase(currentValue)) {
                attributeValueChangedList.add(YES);
            } else if (!FormatHelper.hasContent(attributeValue) && Boolean.TRUE.equals(isAttributeValueChanged(ftypedPrevious.getValue(attributeKey), ftypedCurrent.getValue(attributeKey)))) {
                //Change in attribute value, send email
                LOGGER.debug("anyChangeCriteriaCheck- Change in attribute value");
                attributeValueChangedList.add(YES);
            } else {
                //No mail even if one attribute is not changed
                LOGGER.debug("anyChangeCriteriaCheck- No change in attribute value ");
                attributeValueChangedList.add(NO);
            }
        }
        LOGGER.debug("anyChangeCriteriaCheck- attributeValueChangedList {}", attributeValueChangedList);

        if (!attributeValueChangedList.contains(NO)) {
            anyChangeMap.put(ANY_CHANGE_SATISFIED, YES);
            valid = true;
        }
        LOGGER.debug("anyChangeCriteriaCheck- anyChangeMap  {}", anyChangeMap);
        LOGGER.info("anyChangeCriteriaCheck- ends");
        return valid;
    }

    /**
     * @param linkScope String
     * @param wtObject  WTObject
     * @return WTObject
     * @throws WTException WTException
     */
    private WTObject getFlexObject(String linkScope, WTObject wtObject) throws WTException {
        WTObject wtObject1 = null;
        Collection<FlexTyped> flexTypedCollection = new ArrayList<>();
        LOGGER.info("getFlexObject() linkScope    >>>{}  {}", linkScope, wtObject.getClass().getName());
        switch (wtObject.getClass().getName()) {
            case "com.lcs.wc.sourcing.LCSSourcingConfig":
                flexTypedCollection = new SourcingConfigurationModel().getObject(wtObject, linkScope);
                break;
            case "com.lcs.wc.sourcing.LCSProductCostSheet":
                flexTypedCollection = new CostsheetModel().getObject(wtObject, linkScope);
                break;
            default:
                break;
        }
        LOGGER.debug("getFlexObject() flexTypedCollection   {}", flexTypedCollection);
        if (!flexTypedCollection.isEmpty()) {
            wtObject1 = (WTObject) flexTypedCollection.iterator().next();
        }
        return wtObject1;
    }

    /**
     * @param ftypedPrevious FlexTyped
     * @param attributeKey   String
     * @return String
     * @throws WTException WTException
     */
    private String getAttributeValue(FlexTyped ftypedPrevious, String attributeKey) throws WTException {
        String prevValue = "";
        if (Objects.nonNull(ftypedPrevious) && ftypedPrevious.getValue(attributeKey) != null) {
            LOGGER.debug("getAttributeValue() ftypedPrevious.getValue(attributeKey) {}", ftypedPrevious.getValue(attributeKey));
            prevValue = String.valueOf(ftypedPrevious.getValue(attributeKey));
        }
        return prevValue;
    }

    /**
     * Check if any changes between previous and current value
     *
     * @param prevObjAttValue Object
     * @param objAttValue     Object
     * @return Boolean
     */
    private Boolean isAttributeValueChanged(Object prevObjAttValue, Object objAttValue) {
        LOGGER.debug("isAttributeValueChanged() Start");
        //Return true if any change found in attribute values
        if (!Objects.equals(prevObjAttValue, objAttValue)) {
            LOGGER.debug("isAttributeValueChanged() Attribute Value Changed");
            LOGGER.debug("isAttributeValueChanged() End");
            return true;
        }
        LOGGER.debug("isAttributeValueChanged() Attribute Value Not Changed");
        LOGGER.debug("isAttributeValueChanged() end");
        return false;
    }

    /**
     * @param flexTyped FlexTyped
     * @param attKey    String
     * @param attValue  String
     * @return boolean
     * @throws WTException WTException
     */
    private boolean validateEquals(FlexTyped flexTyped, String attKey, String attValue) throws WTException {
        LOGGER.info("validateEquals-  starts");
        boolean valid = false;

        if (flexTyped.getValue(attKey) == null) {
            LOGGER.debug("validateEquals-   {}  attribute value is null ", attKey);
            LOGGER.info("NotificationCriteriaValidator.validateEquals() null return ends");
            return valid;
        }
        LOGGER.debug("validateEquals-  provided input value {}", attValue);
        LOGGER.debug("validateEquals-   flexTyped.getValue()  {}", flexTyped.getValue(attKey));
        if (flexTyped.getValue(attKey).equals(attValue)) {
            valid = true;
        }

        LOGGER.info("validateEquals-  ends");
        return valid;
    }

    /**
     * @param flexTyped FlexTyped
     * @param attKey    String
     * @param attValue  String
     * @return boolean
     * @throws WTException WTException
     */
    private boolean validateNotEquals(FlexTyped flexTyped, String attKey, String attValue) throws WTException {

        LOGGER.info("validateNotEquals-  starts");
        boolean valid = false;
        if (flexTyped.getValue(attKey) == null) {
            LOGGER.debug("validateNotEquals-  {}  attribute value is null ", attKey);
            LOGGER.debug("validateNotEquals-  null return ends ");

            return valid;
        }
        LOGGER.debug("validateNotEquals-  provided input value {}", attValue);
        LOGGER.debug("validateNotEquals-  flexTyped.getValue()  {}", flexTyped.getValue(attKey));
        if (!flexTyped.getValue(attKey).equals(attValue)) {
            valid = true;
        }
        LOGGER.info("validateNotEquals-  ends");
        return valid;
    }
}

package com.limited.exporter.helper;

import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.util.FormatHelper;
import org.apache.log4j.Logger;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class VSAttExtractHelper {

    /**
     * LOGGER.
     */
    public static final Logger LOGGER = Logger.getLogger(VSAttExtractHelper.class);
    /**
     * The moa delim.
     */
    public static final String MOADELIM = "|~*~|";
    /**
     * The linefeed.
     */
    protected static final String LINEFEED = "\r\n";

    /**
     * The linefeed key.
     */
    protected static final String LINE_FEED_KEY = "<-----New Line----->";

    /**
     * constructor.
     */
    public VSAttExtractHelper() {
        super();
    }

    /**
     * returns BigInteger.
     *
     * @param stringValue - String
     * @return BigInteger
     */
    public static BigInteger getBigInteger(String stringValue) {
        BigInteger integerValue;
        integerValue = BigInteger.valueOf(FormatHelper.parseInt(stringValue));
        return integerValue;
    }

    /**
     * convert string to list.
     *
     * @param value
     * @return list
     */
    public static List<String> getList(String value) {
        List<String> valueList = new ArrayList<>();
        StringTokenizer valToken = new StringTokenizer(value, MOADELIM);
        while (valToken.hasMoreTokens()) {
            valueList.add(valToken.nextToken());
        }
        return valueList;
    }

    /**
     * Sets the value.
     *
     * @param flextyped the flextyped
     * @param attKeys   the att keys
     * @param attValues the att values
     * @return the string
     * @throws WTException             the WT exception
     * @throws WTPropertyVetoException the WT property veto exception
     */
    public static Object getAttributeValues(FlexTyped flextyped, FlexTypeAttribute moaAttribute, String attKeys, String attValues)
            throws WTPropertyVetoException, WTException {
        String attKey = attKeys;
        FlexTypeAttribute attribute = moaAttribute;
        Object attObject = null;
        if (attValues == null || (!FormatHelper.hasContent(attValues) && " ".equals(attValues))) {
            return null;
        }
        String attValue = attValues.trim();
        attValue = attValue.replaceAll(LINE_FEED_KEY, LINEFEED);
        String[] values = attKey.split("=");
        attKey = values[0];
        if (flextyped != null) {
            attribute = flextyped.getFlexType().getAttribute(attKey);
        }

        String attributeType = attribute.getAttVariableType();
        attObject = attValue;
        if ("integer".equals(attributeType)) {
            attObject = VSAttributeHelper.getIntegerValue(attValue);
        } else if ("currency".equals(attributeType) || "uom".equals(attributeType)) {
            attObject = VSAttributeHelper.getDoubleValue(attValue);
        } else {
            attObject = getAttributeAndDataToFlex(attValue, attribute, attributeType,
                    attObject);
        }
        return attObject;
    }

    /**
     * Sets the attribute and data to flex.
     *
     * @param attValue      the att value
     * @param attribute     the attribute
     * @param attributeType the attribute type
     * @param attObject     the att object
     * @return the object
     * @throws WTException the WT exception
     */
    public static Object getAttributeAndDataToFlex(String attValue,
                                                   FlexTypeAttribute attribute, String attributeType, Object attObject) throws WTException {
        Object attObj = attObject;
        if ("textArea".equals(attributeType)) {
            attObj = attValue.replace("\n", "").replace("\r", " ");
        } else if ("float".equals(attributeType)) {
            attObj = VSAttributeHelper.getFloatValue(attValue);
        } else if ("driven".equals(attributeType) || "choice".equals(attributeType) || "colorSelect".equals(attributeType)) {
            try {
                attObj = VSAttributeHelper.getListAttributeValue(attribute, attValue);
            } catch (WTException ex) {
                LOGGER.debug(ex.getLocalizedMessage());
            }
        } else if ("boolean".equals(attributeType)) {
            attObj = VSAttributeHelper.getBooleanValue(attValue);
        } else {
            attObj = getValuesAndDataToFlex(attValue, attribute, attributeType,
                    attObj);
        }
        return attObj;
    }

    /**
     * Sets the values and data to flex.
     *
     * @param attValue      the att value
     * @param attribute     the attribute
     * @param attributeType the attribute type
     * @param attObjs       the att objs
     * @return the object
     * @throws WTException the WT exception
     */
    public static Object getValuesAndDataToFlex(String attValue,
                                                FlexTypeAttribute attribute, String attributeType, Object attObjs) throws WTException {
        Object attObjValue = attObjs;
        if (attributeType.startsWith("object_ref") && FormatHelper.hasContent(attValue) && !"0".equals(attValue)) {

            attObjValue = VSAttributeHelper.checkObjectRefernce(attValue, attribute);

        } else if (attributeType.startsWith("moa") || "composite".equals(attributeType)) {
            try {
                attObjValue = VSAttributeHelper.getMultiValue(attribute, attValue, "moaList".equals(attributeType));
            } catch (WTException ex) {
                LOGGER.error("ERROR OCCURED :-", ex);
            }
        }
        return attObjValue;
    }

    /**
     * returns string.
     *
     * @param value - String
     * @return String
     */
    public String getString(String value) {
        return String.valueOf(value);
    }

    /**
     * returns float.
     *
     * @param value - String
     * @return float
     */
    public float getFloat(String value) {
        return FormatHelper.parseFloat(value);
    }

    /**
     * returns long.
     *
     * @param value - String
     * @return long
     */
    public long getLong(String value) {
        return Long.valueOf(value);
    }

    /**
     * returns  int.
     *
     * @param value - String
     * @return int
     */
    public int getInt(String value) {
        return FormatHelper.parseInt(value);
    }

}

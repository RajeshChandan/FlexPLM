package com.limited.exporter.helper;

import com.lcs.wc.client.web.FlexTypeGenerator;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.ForiegnKeyDefinition;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.util.WTException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VSAttributeHelper {

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
     * SINGLE_LIST_EROOR_LOG.
     */
    private static final String SINGLE_LIST_EROOR_LOG = "SINGLE_LIST_VALUE_NOT_MATCH : Invalid Value '";
    /**
     * Declaration for ERROR_LOGGER attributes.
     */
    private static final Logger ERROR_LOGGER = Logger.getLogger(VSAttributeHelper.class);
    /**
     * The attribute value list values.
     */
    private static final Map<FlexTypeAttribute, HashMap<String, String>> ATTRIBUTE_VALUE_LIST = new HashMap<>();
    /**
     * The true values.
     */
    private static final String[] TRUEVALUES = new String[]{"true", "t", "yes", "y", "1"};

    /**
     * constructor.
     */
    protected VSAttributeHelper() {

    }

    /**
     * Declaring Protected Constructor.
     */

    /**
     * Gets the list value.
     *
     * @param attribute       the attribute
     * @param stringListValue the string value
     * @return the list value
     * @throws WTException the exception
     */
    public static String getListAttributeValue(FlexTypeAttribute attribute, String stringListValue) throws WTException {

        if (!FormatHelper.hasContent(stringListValue)) {
            return null;
        }

        String listValue = getAttributeValueList(attribute).get(stringListValue.toUpperCase());
        if (listValue == null) {
            throw new WTException(SINGLE_LIST_EROOR_LOG + stringListValue + "' Specificied For Attribute '"
                    + attribute.getAttKey() + "' !!!");
        } else if (listValue.equals(stringListValue)) {
            ERROR_LOGGER.debug(SINGLE_LIST_EROOR_LOG + stringListValue);
        }

        return listValue;
    }

    /**
     * Gets the attribute value list.
     *
     * @param attribute the attribute
     * @return the attribute value list
     * @throws WTException the exception
     */
    public static Map<String, String> getAttributeValueList(FlexTypeAttribute attribute) throws WTException {
        HashMap<String, String> listAttrVlaue = ATTRIBUTE_VALUE_LIST.get(attribute);
        if (listAttrVlaue == null) {
            String key = null;
            listAttrVlaue = new HashMap<>();
            Iterator<?> itr = attribute.getAttValueList().getKeys().iterator();
            while (itr.hasNext()) {
                key = (String) itr.next();
                listAttrVlaue.put(key.toUpperCase(), attribute.getAttValueList().getValue(key, null));
            }
            ATTRIBUTE_VALUE_LIST.put(attribute, listAttrVlaue);
        }
        return listAttrVlaue;
    }

    /**
     * Gets the attribute code list.
     *
     * @param attribute the attribute
     * @return the attribute code list
     * @throws WTException the exception
     */
    public static Map<String, String> getAttributeCodeList(FlexTypeAttribute attribute) throws WTException {
        HashMap<String, String> list = null;
        list = new HashMap<>();
        Collection<FlexObject> dataSet = attribute.getAttValueList().getDataSet();
        for (FlexObject flexobject : dataSet) {
            list.put((String) flexobject.get("DESCRIPTION"), flexobject.get("VALUE").toString().toUpperCase());
        }
        return list;
    }

    /**
     * returns XMLGregorianCalendar.
     *
     * @param value - Timestamp
     * @return XMLGregorianCalendar
     */
    public static XMLGregorianCalendar getXMLGregorianCalendarVlaue(Timestamp value) {
        XMLGregorianCalendar created = null;
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(value.getTime());
        try {
            created = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            ERROR_LOGGER.error("### ERROR Ocured>>>>", e);
        }
        return created;
    }

    /**
     * check for integer Vlaue.
     *
     * @param str the string
     * @return boolean
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the multi value.
     *
     * @param attribute   the attribute
     * @param stringValue the string value
     * @param list        the list
     * @return the multi value
     * @throws WTException the exception
     */
    public static String getMultiValue(FlexTypeAttribute attribute, String stringValue, boolean list)
            throws WTException {
        StringTokenizer multiToken = null;
        String multiValue = "";
        /*
         * if (values.length > 2) { delimiter = values[2]; }
         */
        /*
         * if (delimiter.equals(MOADELIM)) { return stringValue; }
         */
        StringBuilder returnValue = new StringBuilder();
        multiToken = new StringTokenizer(stringValue, MOADELIM);
        // String[] listValues = stringValue.split(delimiter);
        while (multiToken.hasMoreTokens()) {
            multiValue = multiToken.nextToken();
            if (list) {
                multiValue = getListAttributeValue(attribute, multiValue);
            }
            if (!FormatHelper.hasContent(multiValue)) {
                continue;
            }
            returnValue = returnValue.append(multiValue).append(MOADELIM);
        }
        String value = StringUtils.stripStart(returnValue.toString(), MOADELIM);
        value = StringUtils.stripEnd(value, MOADELIM);
        return value.trim();
    }

    /**
     * Gets the double value.
     *
     * @param stringValue the string value
     * @return the double value
     */
    public static Double getDoubleValue(String stringValue) {
        return FormatHelper.parseDouble(stringValue);
    }

    /**
     * Gets the double value.
     *
     * @param stringValue the string value
     * @return the double value
     */
    public static Float getFloatValue(String stringValue) {
        Float floatValue = null;
        floatValue = Float.parseFloat(stringValue);
        return floatValue;
    }

    /**
     * Gets the integer value.
     *
     * @param stringValue the string value
     * @return the double value
     */
    public static BigInteger getIntegerValue(String stringValue) {
        BigInteger integerValue = null;
        integerValue = BigInteger.valueOf(FormatHelper.parseInt(stringValue));
        return integerValue;
    }

    /**
     * Gets the date value.
     *
     * @param stringKey       the string key
     * @param stringAttrValue the string value
     * @param format          the format
     * @return the date value
     */
    public static Timestamp getDateAttributeValue(String stringKey, String stringAttrValue, String format) {
        Timestamp timestampValue = null;
        String strFormat = format;
        if ("CURRENT_TIME".equals(stringKey) && stringAttrValue == null) {
            return new Timestamp(new Date().getTime());
        }

        try {
            if (stringAttrValue != null) {
                timestampValue = new Timestamp(new SimpleDateFormat(strFormat).parse(stringAttrValue).getTime());
            }
        } catch (ParseException pe) {
            ERROR_LOGGER.error("### ERROR Ocured " + pe.getMessage() + "  ###");
        }
        return timestampValue;
    }

    /**
     * Gets the boolean value.
     *
     * @param stringValue the string value
     * @return the boolean value
     */
    public static boolean getBooleanValue(String stringValue) {
        if (stringValue == null) {
            return false;
        }
        for (int i = 0; i < TRUEVALUES.length; ++i) {
            if (!stringValue.equalsIgnoreCase(TRUEVALUES[i])) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * Gets the user.
     *
     * @param stringValue the string value
     * @return the user
     * @throws WTException the WT exception
     */
    public static WTUser getUser(String stringValue) throws WTException {
        return OrganizationServicesHelper.manager.getAuthenticatedUser(stringValue);
    }

    /**
     * Gets the object reference value.
     *
     * @param attVal
     * @return the object reference value
     * @throws WTException the wT exception
     */
    public static String getObjectReference(FlexTypeAttribute att, String attVal, String diffAtt)
            throws wt.util.WTException {

        String attValue = null;
        ForiegnKeyDefinition def = att.getRefDefinition();

        if (FormatHelper.hasContent(attVal) && !"0".equals(attVal)) {
            if ("version".equals(def.getRefType())) {
                attValue = getObjectReferenceValue(attVal, def, "VR:", diffAtt);
            } else {
                attValue = getObjectReferenceValue(attVal, def, "OR:", diffAtt);
            }
        } else {
            ERROR_LOGGER.debug("### Object Reference is Null #####");
        }
        return attValue;
    }

    /**
     * getObjectReferenceValue
     *
     * @param attVal
     * @param def
     * @param ref
     * @param diffAtt
     * @return
     * @throws WTException
     */
    private static String getObjectReferenceValue(String attVal, ForiegnKeyDefinition def, String ref, String diffAtt)
            throws WTException {
        String val = attVal;
        LCSColor color;
        if (def.getFlexTypeClass().indexOf("LCSColor") > -1) {
            color = (LCSColor) LCSQuery.findObjectById(ref + def.getFlexTypeClass() + ":" + val);
            if (FormatHelper.hasContent(diffAtt) && color.getValue(diffAtt) != null) {
                val = (String) color.getValue(diffAtt);
            } else {
                val = color.getName();
            }

        }
        return val;
    }

    /**
     * Display.
     *
     * @param object the object
     */
    public static void display(Object object) {
        ERROR_LOGGER.debug("object>>>" + object);
    }

    /**
     * Check object refernce.
     *
     * @param attValue  the att value
     * @param attribute the attribute
     * @return the object
     * @throws WTException the WT exception
     */
    public static Object checkObjectRefernce(String attValue, FlexTypeAttribute attribute) throws WTException {
        String attObj = null;
        attObj = getObjectReference(attribute, attValue, "");
        if (attObj == null) {
            ERROR_LOGGER.debug("ERROR_OBJECT_REFERENCE_NOT_FOUND : Object '" + attValue
                    + "' does not exists in FlexPLM for field :  " + attribute.getAttDisplay());
        }
        return attObj;
    }
}

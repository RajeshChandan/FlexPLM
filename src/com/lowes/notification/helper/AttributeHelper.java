package com.lowes.notification.helper;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;
import wt.util.WTException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Common class used for getting attribute values
 */
public class AttributeHelper {
    /**
     * To hide the public implicit one
     */
    private AttributeHelper() {
    }

    /**
     * To get attribute's string value by passing FlexTyped and attribute key
     *
     * @param flexTyped FlexTyped
     * @param attKey    String
     * @return String
     * @throws WTException WTException
     */
    public static String getAttributeStringValue(FlexTyped flexTyped, String attKey) throws WTException {

        String attributeValue ;
        FlexType flextype = flexTyped.getFlexType();
        FlexTypeAttribute att = flextype.getAttribute(attKey);
        String attVariableType = att.getAttVariableType();
        Object attValue = flexTyped.getValue(attKey);
        if (Objects.nonNull(attValue)) {
            if ("driven".equals(attVariableType) || "choice".equals(attVariableType)) {
                attributeValue = (String) attValue;
                attributeValue = flextype.getAttribute(attKey).getAttValueList().getValue(attributeValue, Locale.getDefault());
            } else if ("moaList".equals(attVariableType)) {
                attributeValue = att.getStringValue(flexTyped);
                attributeValue = MOAHelper.parseOutDelimsLocalized(attributeValue, true, att.getAttValueList(), Locale.getDefault());

            } else if ("integer".equals(attVariableType)) {

                double dValue = (Double) attValue;
                int iValue = (int) dValue;
                attributeValue = Integer.toString(iValue);

            } else if (("float").equals(attVariableType)) {
                attributeValue = (String) attValue;
            } else if (("date").equals(attVariableType)) {

                attributeValue = getDateStringValue(attValue);

            } else if ("object_ref".equals(attVariableType) || "object_ref_list".equals(attVariableType)) {

                attributeValue = new AttributeHelper().getObjectRefName(attValue);

            } else {
                attributeValue = (String) attValue;
            }
        } else {
            attributeValue = "";
        }
        return attributeValue;

    }

    /**
     * To get date value in String format "MM/dd/yyyy"
     *
     * @param attValue Object
     * @return String
     */
    private static String getDateStringValue(Object attValue) {
        Date dateObj = (Date) attValue;
        String date = "";
        if (dateObj != null) {
            date = FormatHelper.applyFormat(dateObj, "MM/dd/yyyy");
        }
        return date;
    }

    /**
     * To get object reference attribute's object's name
     *
     * @param object Object
     * @return String
     */
    public String getObjectRefName(Object object) {
        String name = "";
        if (Objects.isNull(object) || !(object instanceof FlexTyped)) {
            return name;
        }
        try {
            FlexTyped flexTyped = (FlexTyped) object;
            Method method = flexTyped.getClass().getMethod("getName");
            if (Objects.nonNull(method.invoke(flexTyped))) {
                name = (String) method.invoke(flexTyped);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return name;
    }
}
package com.burberry.wc.integration.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WrappedTimestamp;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.flextype.FootwearApparelFlexTypeScopeDefinition;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSRevisionControlled;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.material.LCSMaterialColor;

/**
 * Utility class operate on data.
 *
 * 
 *
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
public final class BurberryDataUtil {

	/**
	 * STRING_BLANK_SPACE.
	 */
	private static final String STRING_BLANK_SPACE = " ";

	/**
	 * CHAR_EMPTY_SPACE.
	 */
	private static final char CHAR_EMPTY_SPACE = ' ';

	/**
	 * STRING_HASH_DOT.
	 */
	private static final String STRING_HASH_DOT = "#.";

	/**
	 * STRING_HASH.
	 */
	private static final String STRING_HASH = "#";

	/**
	 * BurberryDataUtil.
	 */
	private BurberryDataUtil() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryDataUtil.class);

	/**
	 * getData:method is for getting Data.
	 * 
	 * @param obj
	 *            WTObject
	 * @param key
	 *            String
	 * @param params
	 *            Map
	 * @return String
	 * @throws WTException
	 */
	public static String getData(final WTObject obj, final String key,
			final Map<String, Object> params) throws WTException {

		String methodName = "getData() ";
		String data = null;

			final FlexTyped ftyped = getFlexTyped(obj);
			logger.debug(methodName + "FlexTyped: " + ftyped);
			Object attValue = BurConstant.STRING_EMPTY;
			if (ftyped != null && ftyped.getFlexType().attributeExist(key)) {
				final FlexTypeAttribute att = ftyped.getFlexType()
						.getAttribute(key);
				logger.debug(methodName + "FlexType Attribute for Key " + key
						+ " is " + att.getAttDisplay());
				final String attType = att.getAttVariableType();
				logger.debug(methodName + "Attribute Type: " + attType);

				if (FootwearApparelFlexTypeScopeDefinition.PRODUCT_SKU_LEVEL
						.equalsIgnoreCase(att.getAttObjectLevel())
						&& obj instanceof LCSSKU) {
					attValue = ((LCSSKU) ftyped).getLogicalValue(key);

				} else {
					attValue = ftyped.getValue(key);
				}
				logger.debug(methodName + "attValue: <<" + attValue + ">>");
				// Production Issue Fix (21-Nov-2017): Start
				if (attValue == null && BurConstant.BOOLEAN.equals(attType)) {
					attValue = false;
				}
				// Production Issue Fix (21-Nov-2017): End
				logger.debug(obj + "   " + methodName + "Attribute type is "
						+ attType + " and value is " + attValue);
				if (attValue != null) {
					final String value = getValueForLiteralType(attType, att,
							attValue);
					String objectValue = FormatHelper.hasContent(value) ? value
							: getValueForObjectType(attType, att, attValue,
									ftyped, key);
					data = FormatHelper.hasContent(objectValue) ? objectValue
							: null;
					logger.debug(methodName
							+ "Data Retrieved for the attribute: " + key
							+ "Value: " + data);
				} else {
					logger.debug(methodName
							+ "Unable to Retrieve Data for the attribute: "
							+ key);
				}
			}

		
		// Return Statement
		return data;
	}

	/**
	 * getData:getValueForObjectType.
	 * 
	 * @param attType
	 *            String
	 * @param att
	 *            FlexTypeAttribute
	 * @param attValue
	 *            Object
	 * @param ftyped
	 *            FlexTyped
	 * @param key
	 *            String
	 * @return String
	 * @throws LCSException
	 * @throws WTException
	 */
	public static String getValueForObjectType(final String attType,
			final FlexTypeAttribute att, final Object attValue,
			final FlexTyped ftyped, final String key) throws LCSException,
			WTException {

		// define variables
		String methodName = "getValueForObjectType() ";
		String value = null;
		if (BurConstant.CHOICE.equals(attType)
				|| BurConstant.DRIVEN.equals(attType)) {
			value = ftyped
					.getFlexType()
					.getAttribute(key)
					.getAttValueList()
					.getValue(
							(String) attValue,
							com.lcs.wc.client.ClientContext.getContext()
									.getLocale());
		}

		if (BurConstant.MOA_LIST.equals(attType)
				|| BurConstant.MOA_ENTRY.equals(attType)) {

			value = MOAHelper.parseOutDelimsLocalized((String) attValue,
					BurConstant.STRING_COMMA, att.getAttValueList(), null);
		}

		if (BurConstant.COMPOSITE.equals(attType)) {
			value = getValueForComposite((String) attValue, ftyped, key);
		}

		if (BurConstant.OBJECT_REF.equals(attType)
				|| BurConstant.OBJECT_REF_LIST.equals(attType)) {
	
			value = getDataForObjRef(attValue);
		}

		if (BurConstant.USER_LIST.equals(attType)) {
			value = ((FlexObject) attValue).getString(BurConstant.FULLNAME);
		}

		if (BurConstant.SEQUENCE.equals(attType)) {
			value = BurConstant.STRING_EMPTY
					+ ((int) (Double.parseDouble(attValue.toString())));

		}
		logger.debug(methodName + "Att: <<" + att.getAttDisplay()
				+ ">> Returning value: " + value + " for attribute of Type: "
				+ attType);
		return value;
	}

	/**
	 * getData:getValueForLiteralType.
	 * 
	 * @param attType
	 *            String
	 * @param att
	 *            FlexTypeAttribute
	 * @param attValue
	 *            Object
	 *
	 * @return String
	 *
	 */
	public static String getValueForLiteralType(final String attType,
			final FlexTypeAttribute att, final Object attValue) {

		// define variables
		String methodName = "getValueForLiteralType() ";
		String value = null;

		if (FormatHelper.hasContent(attValue.toString())) {

			// BOOLEAN
			if (BurConstant.BOOLEAN.equals(attType)) {
				value = (Boolean) attValue ? "Yes" : "No";
			}

			// FLOAT
			else if (BurConstant.FLOAT.equals(attType)) {
				value = getValueForFloat(attValue, att);
			}

			// INTEGER
			else if (BurConstant.INTEGER.equals(attType)) {
				value = BurConstant.STRING_EMPTY
						+ FormatHelper.parseInt(attValue.toString());
			}
			// DATE
			else if (BurConstant.DATE.equals(attType)) {
				value = getValueForDate(attValue);
			}
			// CURRENCY
			else if (BurConstant.CURRENCY.equals(attType)) {
				value = getValueForCurrency(attValue, att);
			} else {
				value = getValueForString(attValue, attType);
			}
		}

		logger.debug(methodName + "Returning value: " + value
				+ " for attribute of Type: " + attType);
		return value;
	}

	/**
	 * @param attValue
	 * @param attType
	 * @return
	 */
	private static String getValueForString(Object attValue, String attType) {

		String value = null;
		// STRING
		if (BurConstant.DERIVED_STRING.equals(attType)
				|| BurConstant.TEXT.equals(attType)
				|| BurConstant.TEXT_AREA.equals(attType)
				|| BurConstant.CONSTANT.equalsIgnoreCase(attType)) {
			
			value = attValue.toString().replaceAll("\\r\\n", " ")
					.replaceAll("\\t", " ");
			value=StringEscapeUtils.unescapeHtml(value);
		}
		else if(BurConstant.HYPERLINK_URL.equals(attType)){
			value = attValue.toString();
		}
		return value;
	}

	/**
	 * getValueForComposite.
	 *
	 * @param stringValue
	 *            String
	 * @param ftyped
	 *            FlexTyped
	 * @param key
	 *            String
	 * @return String
	 * @throws LCSException
	 * @throws WTException
	 */
	private static String getValueForComposite(final String stringValue,
			final FlexTyped ftyped, final String key) throws LCSException,
			WTException {

		// define variables
		String methodName = "getValueForComposite() ";
		final StringBuilder sb = new StringBuilder();
		final StringTokenizer compositeST = new StringTokenizer(stringValue,
				BurConstant.STRING_REG_PATTERN);
		while (compositeST.hasMoreElements()) {
			final String token = compositeST.nextToken();
			if (isValid(token)) {

				if (compositeST.hasMoreElements()) {

					sb.append(getPercentValue(token) + STRING_BLANK_SPACE
							+ getKeyValue(token, ftyped, key)
							+ BurConstant.STRING_COMMA);
				} else {

					sb.append(getPercentValue(token) + STRING_BLANK_SPACE
							+ getKeyValue(token, ftyped, key));
				}
			}
		}
		logger.debug(methodName + "Returning Composite value " + sb.toString());
		return sb.toString();
	}

	/**
	 * getKeyValue.
	 *
	 * @param token
	 *            String
	 * @param ftyped
	 *            FlexTyped
	 * @param key
	 *            String
	 * @return String
	 * @throws LCSException
	 * @throws WTException
	 */
	private static String getKeyValue(final String token,
			final FlexTyped ftyped, final String key) throws LCSException,
			WTException {

		return ftyped
				.getFlexType()
				.getAttribute(key)
				.getAttValueList()
				.getValue(
						getCompositeKey(token),
						com.lcs.wc.client.ClientContext.getContext()
								.getLocale());
	}

	/**
	 * getCompositeKey.
	 *
	 * @param token
	 *            String
	 * @return String
	 */
	private static String getCompositeKey(final String token) {

		return token.substring(token.indexOf(CHAR_EMPTY_SPACE) + 1,
				token.length());

	}

	/**
	 * getPercentValue.
	 *
	 * @param token
	 *            String
	 * @return String
	 */
	private static String getPercentValue(final String token) {

		return token.substring(0, token.indexOf(CHAR_EMPTY_SPACE));
	}

	/**
	 * Check for null and empty string
	 * 
	 * @param token
	 *            String
	 * @return boolean
	 */
	private static boolean isValid(final String token) {

		return (token != null && token.indexOf(CHAR_EMPTY_SPACE) != -1);
	}

	/**
	 * getValueForDate.
	 *
	 * @param attValuete
	 *            Object
	 * @return String
	 */
	public static String getValueForDate(final Object attValue) {

		// define variables
		String methodName = "getValueForDate() ";

		final WrappedTimestamp time = (WrappedTimestamp) attValue;

		final DateFormat formatter = new SimpleDateFormat(
				BurConstant.dateFormat);

		final TimeZone timeZone = TimeZone.getTimeZone(BurConstant.GMT);
		formatter.setTimeZone(timeZone);
		logger.debug(methodName + "Returning Date Value "
				+ formatter.format(time));
		return formatter.format(time);
	}

	/**
	 * getValueForFloat.
	 *
	 * @param attValue
	 *            Object
	 * @param att
	 *            FlexTypeAttribute
	 * @return String
	 */
	private static String getValueForFloat(final Object attValue,
			final FlexTypeAttribute att) {

		// define variables
		String methodName = "getValueForFloat() ";
		String value = null;

		// get attribute value
		final Double d = (Double) attValue;
		// get attribute decimal digit places
		final int precision = att.getAttDecimalFigures();
		logger.debug(methodName + "Attribute Decimal Figures Precision "
				+ precision);

		// True - if attribute has decimal digits specified in Type Manager
		if (precision != 0) {
			String str = BurConstant.STRING_EMPTY;
			StringBuilder build = new StringBuilder(BurConstant.STRING_EMPTY);
			for (int i = 0; i < precision; i++) {
				build = build.append(STRING_HASH);
			}
			str = build.toString();
			final DecimalFormat twoDForm = new DecimalFormat(STRING_HASH_DOT
					+ str);
			value = BurConstant.STRING_EMPTY + twoDForm.format(d);
			logger.debug(methodName + "Formatted Value: " + value);
		} else {
			value = BurConstant.STRING_EMPTY + d.intValue();
			logger.debug(methodName + "Non-Formatted Int Value: " + value);
		}
		logger.debug(methodName + "Returning Final Float Value " + value);
		// return
		return value;

	}

	/**
	 * getFlexTyped.
	 *
	 * @param obj
	 *            WTObject
	 * @param key
	 *            String
	 * @return FlexTyped
	 * @throws WTException
	 */
	public static FlexTyped getFlexTyped(final WTObject obj) throws WTException {
		FlexTyped fTyped = null;
		
		if (obj instanceof FlexTyped) {

			final FlexTyped typed = (FlexTyped) obj;
			fTyped = (typed.getFlexType() != null) ? typed : null;

		}

		return fTyped;
	}

	/**
	 * getDataForObjRef
	 *
	 * @param attValue
	 *            Object
	 * @return String
	 * @throws WTException
	 */
	private static String getDataForObjRef(final Object attValue)
			throws WTException {

		// define variables
		String methodName = "getDataForObjRef() ";
		String value = null;
		if (attValue instanceof LCSSKU) {
			value = (String) ((LCSSKU) attValue).getValue(BurConstant.SKUNAME);
		} else if (attValue instanceof LCSRevisionControlled) {
			value = ((LCSRevisionControlled) attValue).getName();
		} else if (attValue instanceof LCSColor) {
			value = ((LCSColor) attValue).getName();
		}else if (attValue instanceof LCSProduct){ //ADDED to get Product Name CR R11
			value = (String) ((LCSProduct) attValue).getName();
		} else {
			value = getDataForObjReference(attValue);
		}
		logger.debug(methodName + "Returning object reference Value " + value);
		return value;
	}

	private static String getDataForObjReference(final Object attValue)
			throws WTException {
		// define variables
		String methodName = "getDataForObjReference() ";
		String value = null;
		if (attValue instanceof LCSLifecycleManaged) {
			value = ((LCSLifecycleManaged) attValue).getName();
		}
		//BURBERRY-1485: Object Material-Supplier Attribute Base Fabric Material Color Obj Ref
		else if (attValue instanceof LCSMaterialColor) {
			value = ((LCSMaterialColor) attValue).getName();
		}
		logger.debug(methodName + "Returning object reference Value " + value);
		return value;
	}

	public static String getLastModify(WTObject obj) throws WTException {

		// define variables
		String methodName = "getLastModify() ";

		SimpleDateFormat formatter = new SimpleDateFormat(
				BurConstant.dateFormat);
		Timestamp modifyTime = obj.getModifyTimestamp();
		logger.debug(methodName + "formatter " + formatter + " modify "
				+ modifyTime);
		return formatter.format(modifyTime);
	}

	// Build v2.0 Release 2 - Start
	/**
	 * getValueForCurrency.
	 *
	 * @param attValue
	 *            Object
	 * @param att
	 *            FlexTypeAttribute
	 * @return String
	 */
	private static String getValueForCurrency(final Object attValue,
			final FlexTypeAttribute att) {

		// define variables
		String methodName = "getValueForCurrency() ";
		String value = null;

		// get attribute value
		final Double d = (Double) attValue;
		// get attribute decimal digit places
		final int precision = att.getAttDecimalFigures();
		if (precision != 0) {
			String str = BurConstant.STRING_EMPTY;
			StringBuilder build = new StringBuilder(BurConstant.STRING_EMPTY);
			for (int i = 0; i < precision; i++) {
				build = build.append(STRING_HASH);
			}
			str = build.toString();
			final DecimalFormat twoDForm = new DecimalFormat(STRING_HASH_DOT
					+ str);
			value = BurConstant.STRING_EMPTY + twoDForm.format(d);
		} else {
			value = BurConstant.STRING_EMPTY + d.intValue();
		}
		logger.debug(methodName + "Returning Currency Value " + value);
		return value;
	}

	// Build v2.0 Release 2 - End

	/**
	 * Method to get Creator.
	 * 
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static String getCreator(WTObject obj) throws WTException {
		// define variables
		String methodName = "getCreator() ";
		String creator = null;
		if (obj instanceof LCSRevisionControlled) {
			creator = ((LCSRevisionControlled) obj).getCreatorFullName();
		} else if (obj instanceof LCSDocument) {
			creator = ((LCSDocument) obj).getCreatorFullName();
		} else if (obj instanceof LCSSampleRequest) {
			creator = ((LCSSampleRequest) obj).getCreatorFullName();
		}
		logger.debug(methodName + "Create " + creator);
		return creator;
	}

	/**
	 * Method to get Modifier.
	 * 
	 * @param object
	 * @return
	 */
	public static Object getModifier(WTObject object) {
		// define variables
		String methodName = "getModifier() ";
		String modifier = null;
		if (object instanceof LCSRevisionControlled) {
			modifier = ((LCSRevisionControlled) object).getModifierFullName();
		} else if (object instanceof LCSDocument) {
			modifier = ((LCSDocument) object).getCreatorFullName();
		} else if (object instanceof LCSSampleRequest) {
			modifier = ((LCSSampleRequest) object).getModifier().getFullName();
		}
		logger.debug(methodName + "Modifier " + modifier);
		return modifier;
	}

}

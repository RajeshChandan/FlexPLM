package com.sportmaster.wc.interfaces.webservices.inbound.helper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.load.LoadCommon;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMInboundIntegrationHelper {


	/**
	 *
	 * Declaration for ERROR_LOGGER attributes.
	 */
	private static final Logger ERROR_LOGGER = Logger.getLogger(SMInboundIntegrationHelper.class);

	/** The linefeed. */
	protected static final String LINEFEED = "\r\n";

	/** The linefeed key. */
	protected static final String LINEFEED_KEY = "<-----New Line----->";

	/** The attribute value list values. */
	private static final Map<FlexTypeAttribute, HashMap<String, String>> ATTRIBUTEVALUELISTVALUES = new HashMap<>();
	/** The moa delim. */
	public static final String MOADELIM = "|~*~|";

	/** The true values. */
	private static final String[] TRUE_VALUES = new String[]{"true", "t", "yes", "y", "1"};

	/** The date format. */
	private static final String DATE_FORMAT = LCSProperties.get("com.sm.wc.CustomEIHelper.DATE_FORMAT", "MM/dd/yyyy");

	//private final static String DATEFORMAT = "MM/dd/yyyy";

	/**
	 * the SINGLE_LIST_ERROR_LOG.
	 */
	private static final String SINGLE_LIST_ERROR_LOG = LCSProperties.get("com.sm.wc.intefaces.helper.singleListMessage");



	/**
	 * Declaring Protected Constructor.
	 */
	protected SMInboundIntegrationHelper() {

	}

	/**
	 * Gets the string.
	 *
	 * @param strProperty the property
	 * @param strDefaultValue the default value
	 * @return the string
	 */
	public static String getString(String strProperty, String strDefaultValue) {
		return getObject(strProperty, strDefaultValue).toString();
	}

	/**
	 * Gets the string.
	 *
	 * @param strProp the property
	 * @return the string
	 */
	public static String getString(String strProp) {
		return getObject(strProp, "").toString();
	}

	/**
	 * Gets the boolean.
	 *
	 * @param strProprty the property
	 * @return the boolean
	 * @throws WTException the WT exception
	 */
	public static boolean getBoolean(String strProprty) {
		return getBooleanValue(getString(strProprty));
	}


	/**
	 * Gets the object.
	 *
	 * @param property the property
	 * @param defaultValue the default value
	 * @return the object
	 * @throws WTException the WT exception
	 */
	public static Object getObject(String property, String defaultValue) {

		return LCSProperties.get(property, defaultValue);
	}

	/**
	 * Gets the integer.
	 *
	 * @param property the property
	 * @param defaultValue the default value
	 * @return the integer
	 */
	public static int getInteger(String property, int defaultValue) {
		return getDoubleValue(getString(property, String.valueOf(defaultValue))).intValue();
	}

	/**
	 * Gets the date.
	 *
	 * @param stringKey the string key
	 * @param stringValue the string value
	 * @return the date
	 */
	public static Timestamp getDate(String stringKey, String stringValue) {
		return getDateValue(stringKey, stringValue, DATE_FORMAT);
	}
	/**
	 * Gets the list value.
	 *
	 * @param attribute the attribute
	 * @param stringValue the string value
	 * @return the list value
	 * @throws WTException the exception
	 */
	public static String getListValue(FlexTypeAttribute attribute,
			String stringValue) throws WTException {
		if (!FormatHelper.hasContent(stringValue)) {
			return null;
		}
		String value = getAttributeValueList(attribute).get(stringValue.toUpperCase());
		if (value == null) {
			throw new WTException(SINGLE_LIST_ERROR_LOG+ stringValue + "' Specificied For Attribute '" + attribute.getAttKey() );
		}else if(value.equals(stringValue)){
			ERROR_LOGGER.debug(SINGLE_LIST_ERROR_LOG+ stringValue );
		}

		return value;
	}

	/**
	 * Gets the list Description value.
	 *
	 * @param attribute the attribute
	 * @param stringValue the string value
	 * @return the list value
	 * @throws WTException the exception
	 */
	public static String getListDescValue(FlexTypeAttribute attribute,
			String stringValue) throws WTException {
		if (!FormatHelper.hasContent(stringValue)) {
			return null;
		}
		String value = getAttributeValueList(attribute).get(getAttributeCodeList(attribute).get(stringValue));
		if (value == null) {
			throw new WTException(SINGLE_LIST_ERROR_LOG+ stringValue + "' Specificied For Attribute '" + attribute.getAttKey() );
		}else if(value.equals(stringValue)){
			ERROR_LOGGER.debug(SINGLE_LIST_ERROR_LOG+ stringValue );
		}

		return value;
	}

	/**
	 * Gets the attribute value list.
	 *
	 * @param flextyAttribute the attribute
	 * @return the attribute value list
	 * @throws WTException the exception
	 */
	public static Map<String, String> getAttributeValueList(
			FlexTypeAttribute flextyAttribute) throws WTException {
		HashMap<String, String> listMap = ATTRIBUTEVALUELISTVALUES.get(flextyAttribute);
		if (listMap == null) {
			String strKey = null;
			listMap = new HashMap<>();
			Iterator<?> itrIterator = flextyAttribute.getAttValueList().getSelectableKeys(null,true).iterator();
			while(itrIterator.hasNext()){
				strKey =(String) itrIterator.next();
				listMap.put(flextyAttribute.getAttValueList().getValue(strKey, null).toUpperCase(), strKey);
			}
			ATTRIBUTEVALUELISTVALUES.put(flextyAttribute, listMap);
		}
		return listMap;
	}

	/**
	 * Gets the attribute code list.
	 *
	 * @param ftAttribute the attribute
	 * @return the attribute code list
	 * @throws WTException the exception
	 */
	public static Map<String, String> getAttributeCodeList(
			FlexTypeAttribute ftAttribute) throws WTException {
		HashMap<String, String> listHashMap =null;
		listHashMap = new HashMap<>();
		Collection<FlexObject> dataSetCol=ftAttribute.getAttValueList().getDataSet();

		for(FlexObject flexobject : dataSetCol ){
			listHashMap.put((String)flexobject.get("DESCRIPTION"),flexobject.get("VALUE").toString().toUpperCase());
		}
		return listHashMap;
	}


	/**
	 * Gets the multi value.
	 *
	 * @param flexAttr the attribute
	 * @param strValue the string value
	 * @param strAryValues the values
	 * @param listBolean the list
	 * @return the multi value
	 * @throws WTException the exception
	 */
	public static String getMultiValue(FlexTypeAttribute flexAttr,
			String strValue, String[] strAryValues, boolean listBolean)

					throws WTException {
		String strDelimiter = MOADELIM;
		if (strAryValues.length > 2) {
			strDelimiter = strAryValues[2];
		}
		if (strDelimiter.equals(MOADELIM)) {
			return strValue;
		}
		display("Value = '" + strValue + "'\t Delimiter = '" + strDelimiter + "'");
		StringBuilder returnValue = new StringBuilder();
		String[] listValues = strValue.split(strDelimiter);
		for (int i = 0; i < listValues.length; ++i) {
			display("Value = " + listValues[i]);
			if (listBolean) {
				listValues[i] = getListValue(flexAttr, listValues[i]);
			}
			if (!FormatHelper.hasContent(listValues[i])){
				continue;
			}
			returnValue =returnValue.append(listValues[i]).append(strDelimiter);
		}
		return returnValue.toString();
	}



	/**
	 * Gets the double value.
	 * @param stringValue the string value
	 *
	 * @return the double value
	 */
	public static Double getDoubleValue(String stringValue) {
		return FormatHelper.parseDouble(stringValue.trim());
	}



	/**
	 * Gets the date value.
	 *
	 * @param stringKey the string key
	 * @param stringValue the string value
	 * @param format the format
	 * @return the date value
	 */
	public static Timestamp getDateValue(String stringKey, String stringValue,
			String format) {
		Timestamp timestamp = null;
		String strFormat=format;
		if ("CURRENT_TIME".equals(stringKey) && stringValue == null) {
			return new Timestamp(new Date().getTime());
		}
		/*if (values.length > 1) {
			stringKey = values[0];
		}*/
		try {
			if (stringValue != null) {
				timestamp = new Timestamp(new SimpleDateFormat(strFormat).parse(stringValue).getTime());
			}
		}
		catch(java.text.ParseException pe){
			ERROR_LOGGER.error("### ERROR Ocured "+pe.getMessage()+"  ###");
		}
		return timestamp;
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
		for (int i = 0; i < TRUE_VALUES.length; ++i) {
			if (!stringValue.equalsIgnoreCase(TRUE_VALUES[i])){
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
		return wt.org.OrganizationServicesHelper.manager.getAuthenticatedUser(stringValue);
	}

	/**
	 * Gets the object.
	 *
	 * @param attribute the attribute
	 * @param stringValue the string value
	 * @param values the values
	 * @return the object
	 * @throws WTException the WT exception
	 */
	public static Object getObjectReference(FlexTypeAttribute attribute,
			String stringValue, String[] values) throws WTException {
		Object object = null;
		com.lcs.wc.flextype.ForiegnKeyDefinition def = attribute.getRefDefinition();
		if (def == null) {
			ERROR_LOGGER.debug("No ForiegnKeyDefinition Exists For Attribute '" + attribute );
			return null;
		}
		if ("USER".equals(def.getChooserModule())) {
			wt.org.WTUser user = LoadCommon.getUserById(stringValue);
			if(user != null)
			{
				object = LoadCommon.getId(user);
			}
			return object;
		}
		com.lcs.wc.flextype.FlexType attributeRefType = attribute.getRefType();
		if (attributeRefType == null){
			attributeRefType = FlexTypeCache.getFlexTypeRootByClass(def.getFlexTypeClass());
			if (attributeRefType == null){
				ERROR_LOGGER.debug("No FlexType Exists For Foriegn Key Definition '" + def );
				return null;
			}
		}
		String searchField="name";
		if(values.length>=2){
			searchField=values[1];
		}
		object = LoadCommon.getObjectByAttributes(attributeRefType, searchField, stringValue);
		if (object == null) {
			ERROR_LOGGER.debug("No Object Found With Criteria = " + attribute );
		}
		return object;
	}

	/**
	 * Display.
	 *
	 * @param object the object
	 */
	public static void display(Object object) {
		ERROR_LOGGER.debug("object>>>"+object);
	}

	/**
	 * Sets the value.
	 *
	 * @param flextyped the flextyped
	 * @param attKeys the att keys
	 * @param attValues the att values
	 * @return the string
	 * @throws WTException the WT exception
	 * @throws WTPropertyVetoException the WT property veto exception
	 */
	public static String setAttributeValues(com.lcs.wc.flextype.FlexTyped flextyped,
			String attKeys, String attValues)
					throws WTException, WTPropertyVetoException {
		String strAttValue = attValues;
		String attStrKey = attKeys;
		if (!FormatHelper.hasContent(strAttValue)) {
			return null;
		}
		strAttValue = strAttValue.replaceAll(LINEFEED_KEY, LINEFEED);
		String[] strAryvalues = attStrKey.split("=");
		attStrKey = strAryvalues[0];
		FlexTypeAttribute attribute = flextyped.getFlexType().getAttribute(attStrKey);
		String attributeType = attribute.getAttVariableType();
		Object attObject = strAttValue;
		if ("integer".equals(attributeType) || "float".equals(attributeType) || "currency".equals(attributeType) || "uom".equals(attributeType)) {
			attObject = getDoubleValue(strAttValue);
		} else if ("date".equals(attributeType)) {
			attObject = getDate(attStrKey, strAttValue);
		} else if ("boolean".equals(attributeType)) {
			attObject = String.valueOf(getBooleanValue(strAttValue));
		} else{
			attObject = setAttributeAndDataToFlex(strAttValue, strAryvalues, attribute,
					attributeType, attObject);
		}

		flextyped.setValue(attStrKey, attObject);
		return strAttValue;
	}

	/**
	 * Sets the attribute and data to flex.
	 *
	 * @param attValue the att value
	 * @param values the values
	 * @param attribute the attribute
	 * @param attributeType the attribute type
	 * @param attObject the att object
	 * @return the object
	 * @throws WTException the WT exception
	 */
	public static Object setAttributeAndDataToFlex(String attValue,
			String[] values, FlexTypeAttribute attribute, String attributeType,
			Object attObject) throws WTException {
		Object attObj = attObject;
		if ("userList".equals(attributeType)) {
			attObj = getUser(attValue);
		}else if (attributeType.startsWith("object_ref") && FormatHelper.hasContent(attValue) && !"0".equals(attValue)) {

			attObj = checkObjectRefernce(attValue, values, attribute);

		} else if (attributeType.startsWith("moa")) {
			try {
				attObj = getMultiValue(attribute, attValue, values, "moaList".equals(attributeType));
			}
			catch (WTException ex) {
				ERROR_LOGGER.debug(ex.getLocalizedMessage());
			}
		}else{
			attObj = setValuesAndDataToFlex(attValue, attribute, attributeType,
					attObj);
		}
		return attObj;
	}

	/**
	 * Sets the values and data to flex.
	 *
	 * @param attValue the att value
	 * @param attribute the attribute
	 * @param attributeType the attribute type
	 * @param attObjs the att objs
	 * @return the object
	 * @throws WTException the WT exception
	 */
	public static Object setValuesAndDataToFlex(String attValue,
			FlexTypeAttribute attribute, String attributeType, Object attObjs ) throws WTException {
		Object attObj = attObjs;
		if ("driven".equals(attributeType) || "choice".equals(attributeType) || "colorSelect".equals(attributeType)) {

			try {
				attObj = getListValue(attribute, attValue);
			}
			catch (WTException ex) {
				ERROR_LOGGER.debug(ex.getLocalizedMessage());
			}
		}
		return attObj;
	}

	/**
	 * Check object refernce.
	 *
	 * @param attValue the att value
	 * @param values the values
	 * @param attribute the attribute
	 * @return the object
	 * @throws WTException the WT exception
	 */
	public static Object checkObjectRefernce(String attValue, String[] values,
			FlexTypeAttribute attribute) throws WTException {
		Object attObj;
		attObj = getObjectReference(attribute, attValue, values);
		if(attObj==null){
			ERROR_LOGGER.debug("ERROR_OBJECT_REFERENCE_NOT_FOUND : Object '"+attValue+"' does not exists in FlexPLM for field :  "+attribute.getAttDisplay());
		}
		return attObj;
	}

	/**
	 *  creates unique list of string.
	 * @param FileName - FileName
	 * @param nameList - nameList
	 * @return List
	 */
	public static List<String> uniqueFileName(String fileName,
			List<String> nameList) {
		List<String> fileNameList=nameList;
		if(!fileNameList.contains(fileName)){
			fileNameList.add(fileName);
		}
		return fileNameList;
	}

	/**
	 *  Sets the HTSCode attribute values.
	 *
	 * @param dataValues the data Values
	 * @param attKeyColumnMap the attribute Key Column Map
	 * @param flextyped the flextyped
	 * @param excludeAttributes the exclude Attributes
	 * @param descValueKeys - descValueKeys
	 * @return return sucess status as boolean
	 * @throws WTException the WT Exception
	 * @throws WTPropertyVetoException the WT Property Veto Exception
	 */
	public static boolean setAttributeValues(Map<String, String> dataValues, Map<String, String> attKeyColumnMap, FlexObject fo,
			FlexTyped flextyped, boolean newItem, String keyword) throws WTException,
	WTPropertyVetoException {
		//phase 13 - added for soanr fix, unused method parameter
		Objects.nonNull(fo);
		Objects.nonNull(newItem);

		String flexHtsKey = null;
		boolean persist=false;
		for (Map.Entry<String, String> entry : attKeyColumnMap.entrySet()) {
			String values = entry.getValue();
			if(values.contains(keyword)){
				flexHtsKey = entry.getKey();
				Object asiValue = dataValues.get(flexHtsKey);
				//setting values for placeholder Attribute
				setAttributeValues(flextyped ,flexHtsKey, String.valueOf(asiValue));
				persist=true;
			}else{
				ERROR_LOGGER.debug("### ERROR occured in setting Attribute Values  ###");
			}
		}
		return persist;
	}

}

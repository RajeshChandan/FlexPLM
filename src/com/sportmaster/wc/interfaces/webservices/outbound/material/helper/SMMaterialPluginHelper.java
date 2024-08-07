package com.sportmaster.wc.interfaces.webservices.outbound.material.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.helper.SMOutboundIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

/**
 * SMMaterialPluginHelper.java
 * This class is using to call the methods defined in helper class.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMMaterialPluginHelper {


	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMaterialPluginHelper.class);


	/**
	 * Getting Log entry data values.
	 * @param attKeyColumnMap ---Map
	 * @param fo----FlexObject
	 * @return---- dataValues
	 */
	public Map<String, String> getLogEntryDataValues(Map<String, String> attKeyColumnMap,FlexObject fo){
		
		Map<String, String> dataValues= new HashMap<>();
		
		for(Map.Entry<String, String> keySet:attKeyColumnMap.entrySet()){
			dataValues.put(keySet.getKey(), fo.getString(keySet.getValue()));
		}
		
		return dataValues;
	}

	/**
	 * Getting log  entry type.
	 * @param materialType---String 
	 * @param flexType --boolean
	 * @return returnTypeValue
	 */
	public String getLogEntryType(String materialType,boolean flexType,SMOutboundWebServiceConstants constant){
		
		String type;
		String returnTypeValue;
		type=materialType.replace("\\", "_");
		if(SMOutboundWebServiceConstants.TRIMS_ZIPPER_TYPE.equalsIgnoreCase(materialType)){
			
			returnTypeValue= type;
			
		}else if(SMOutboundWebServiceConstants.TRIMS_FLAT_KNIT_RIB_TYPE.equalsIgnoreCase(materialType)){
			
			returnTypeValue=type.replace(" ", "");
			
		}else if(SMOutboundWebServiceConstants.TRIMS_HOOK_LOOP_TYPE.equalsIgnoreCase(materialType)){
			
			returnTypeValue=type.replace(" ", "");
			returnTypeValue=returnTypeValue.replace("&", "_");
			
		}else if(SMOutboundWebServiceConstants.FABRIC_KNIT_TYPE.equalsIgnoreCase(materialType)){
			
			returnTypeValue= type;
			
		}else{
			
			String[] typeAry=type.split("_");
			returnTypeValue= typeAry[0];
			
			if(returnTypeValue.contains(" ")){
				
				returnTypeValue=returnTypeValue.replace(" ", "");
				returnTypeValue=returnTypeValue.replace("&", "");
			}
		}
		
		if(flexType){
			returnTypeValue=getField(constant, "MATERIAL_LOGENTRY_"+returnTypeValue.split("_")[0].toUpperCase()+"_TYPE");
		}
		return returnTypeValue;
	}

	/**
	 * Getting Material Map type.
	 * @param attr --Map
	 * @return
	 */
	public Map<String, String> getLogEntryMaterialMap(String attr){
		
		String[] key;
		Map<String, String> keyValueMap=new HashMap<>();
		Collection<?> attrColl=FormatHelper.commaSeparatedListToCollection(attr);
		
		for(Object obj:attrColl){
			key=String.valueOf(obj).split("_");
			keyValueMap.put(key[1], key[0]);
		}
		
		return keyValueMap;
	}

	/**
	 * setting fields values.
	 * @param object ---Object
	 * @param fieldName--String
	 * @param fieldValue----Object
	 * @return
	 */
	public boolean set(Object object, String fieldName, Object fieldValue) {
		
		Field field = null;
		Class<?> clazz = object.getClass();
		
		if (clazz != null && FormatHelper.hasContent(String.valueOf(fieldValue))) {
			try {
				field = clazz.getDeclaredField(fieldName);
				//setting accessible true for private and protected fields
				field.setAccessible(true);
				//setting field value
				field.set(object, fieldValue);
				return true;
				
			} catch (NoSuchFieldException noSuchMethodExp) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, noSuchMethodExp);
			} catch (IllegalArgumentException e) {
				
				//calling method to set illegal fields
				setIllegalArgumentValue(object, fieldName, fieldValue);
				
			} catch (IllegalAccessException illAcessExp) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, illAcessExp);
			} 
		}
		return false;
	}
	/**
	 * Setting Arguments.
	 * @param object --Object
	 * @param fieldName --String
	 * @param fieldValue--- object
	 */
	private void setIllegalArgumentValue(Object object,String fieldName, Object fieldValue){
		
		Field field;
		Class<?> clazz = object.getClass();
		SMOutboundIntegrationHelper helper= new SMOutboundIntegrationHelper();
		
		try {
			field=clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			String type=field.getType().getSimpleName();
			field.set(object, getValueByInvokingMethod("get"+type.substring(0, 1).toUpperCase() + type.substring(1), helper,String.valueOf(fieldValue)));
			
		} catch (NoSuchFieldException noSuchMethodExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, noSuchMethodExp);
		} catch (IllegalArgumentException illArgEx) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, illArgEx);
		} catch (IllegalAccessException illAccesExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, illAccesExp);
		} 
	}

	/**
	 * Getting fields value.
	 * @param object ---Object
	 * @param fieldName--- String
	 * @return
	 */
	public String getField(Object object, String fieldName) {
		
		Class<?> clazz = object.getClass();
		
		if (clazz != null) {
			try {
				//creating field object
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				return String.valueOf(field.get(object));
				
			} catch (NoSuchFieldException noSuchMethodExp) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, noSuchMethodExp);
			} catch (IllegalArgumentException illArgEx) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, illArgEx);
			} catch (IllegalAccessException illAccesExp) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, illAccesExp);
			} 
		}
		return "";
	}

	/**
	 * Comparing Material types.
	 * @param materailType --String
	 * @param reqType ---String
	 * @return
	 */
	public boolean compareType(String requestMaterialType,String requestType){
		
		String reqType=requestType;
		String materailType=requestMaterialType;
		if(reqType.contains("_")){
			reqType=reqType.replace("_", " ");
		}
		
		if(materailType.contains("\\")){
			materailType=materailType.replace("\\", "-");
			String[]  matType=materailType.split("-");
			materailType=matType[0];
		
		
		}
		
		//comparing types required for integration
		if(materailType.toUpperCase().contains(reqType)){
			return true;
		}
		return false;
	}

	/**
	 * Getting Object type.
	 * @param packageName---string
	 * @param className---string
	 * @return obj
	 */
	public Object getObject(String packageName,String className){
		
		Object obj=null;
		
		try {
			
			Class<?> classDclr=Class.forName(packageName+"."+className);
			//creating objecct of required class
			obj=classDclr.newInstance();
			//returning object
			return obj;
			
		} catch (ClassNotFoundException clsNoFEx) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, clsNoFEx);
		} catch (InstantiationException instExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, instExp);
		} catch (IllegalAccessException illAccesExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, illAccesExp);
		}
		return obj;
	}

	/**
	 * Getting value by invoking the method.
	 * @param MethodNmae --string
	 * @param obj --Object
	 * @param value----String
	 * @return
	 */
	public Object getValueByInvokingMethod(String methodNmae,Object obj,String value){
		
		Method method;
		Object returnVlaue;
		
		try {
			
			if(!FormatHelper.hasContent(value)){
				
				method = obj.getClass().getMethod(methodNmae);
				returnVlaue=method.invoke(obj);
				return returnVlaue;
			}
			
			method = obj.getClass().getMethod(methodNmae, String.class);
			returnVlaue=method.invoke(obj, value);
			return returnVlaue;
			
		} catch (NoSuchMethodException noSuMeExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, noSuMeExp);
		} catch (SecurityException secExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, secExp);
		} catch (IllegalAccessException illAccesExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, illAccesExp);
		} catch (IllegalArgumentException illArgExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, illArgExp);
		} catch (InvocationTargetException invTarExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, invTarExp);
		}
		return null;
	}
}

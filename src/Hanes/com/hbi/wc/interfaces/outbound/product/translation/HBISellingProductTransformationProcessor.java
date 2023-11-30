package com.hbi.wc.interfaces.outbound.product.translation;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Locale;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.io.IOException;

import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProductHelper;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.hbi.wc.interfaces.outbound.product.HBIInterfaceUtil;
import com.hbi.wc.interfaces.outbound.product.HBISellingProductExtractor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.sizing.ProdSizeCategoryToSeason;
import com.lcs.wc.sizing.ProductSizeCategoryMaster;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.sizing.SizingHelper;

import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

/**
 * HBISellingProductTransformationProcessor.java
 * 
 * This class contains a utility function for searching seasonal product based
 * on the ID provided by IIB MQ and perform transformation based on the Key
 * Value pair,key will be specific to SAP system and PLM needs to perform
 * transformation for value. And Key-Value will come as SOAp message in a Queue.
 * 
 * @author vijayalaxmi.shetty@Hanes.com
 * @since SEP-09-2018
 */
public class HBISellingProductTransformationProcessor implements RemoteAccess {
	private static String productObjectTypePath = LCSProperties.get(
			"com.hbi.wc.product.interfaces.outbound.processor.productObjectTypePath",
			"Product\\BASIC CUT & SEW - SELLING");
	private static String businessObjDerivationTabTypePath = LCSProperties.get(
			"com.hbi.wc.product.interfaces.outbound.processor.businessObjDerivationTabTypePath",
			"Business Object\\Automation Support Tables\\SAP Division Derivation Table");
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	public static final String SALES_ORG = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiSalesOrg",
			"hbiSalesOrg");
	public static final String ERP_BRAND_GROUP = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiErpBrandGroup", "hbiErpBrandGroup");
	public static final String ERP_BRAND_NAME = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiErpBrandName", "hbiErpBrandName");
	public static final String ERP_BRAND_OWNER_GROUP = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiErpBrandOwnerGroup", "hbiErpBrandOwnerGroup");
	public static final String SAP_PRODUCT_GROUP = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiSAPProductGroup", "hbiSAPProductGroup");
	public static final String OMNI_SELECTION = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.product.hbiOmniSelection", "hbiOmniSelection");
	public static final String ERP_DIVISION = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiErpDivision",
			"hbiErpDivision");
	public static final String BO_NAME = "SAP Division Derivation Table";
	public static final String MOA_DIVISION = LCSProperties
			.get("com.hbi.wc.interfaces.outbound.bo.hbiSAPDivisionDerivationTable", "hbiSAPDivisionDerivationTable");

	/*
	 * Default executable function of the class
	 * HBISellingProductTransformationProcessor
	 */
	public static void main(String[] args) {
		LCSLog.debug("### START HBISellingProductTransformationProcessor.main() ###");

		try {
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();

			remoteMethodServer = RemoteMethodServer.getDefault();
			remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);

			System.exit(0);
		} catch (Exception exception) {
			exception.printStackTrace();
			System.exit(1);
		}

		LCSLog.debug("### END HBISellingProductTransformationProcessor.main() ###");
	}

	/**
	 * This function is used to get SAP code from Alternate Keys from
	 * Product\BASIC CUT & SEW - SELLING using key and choices.
	 * 
	 * @param hbiKey
	 *            - String
	 * @param choiceValue
	 *            - String
	 * @return SAPCODE - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	
	public String getCODEFromAlternateKeys(String hbiKey, String choiceValue, String alternateKeys)
			throws WTException, WTPropertyVetoException {
		FlexType productFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(productObjectTypePath);
		//System.out.println(">>>>productObjectTypePath>>>>"+productObjectTypePath);
		//System.out.println(">>>>productFlexTypeObj>>>>"+productFlexTypeObj);
		Map<String, Map<String, String>> mainPickListKeyValuesMap = new HashMap<String, Map<String, String>>();
		Map<String, String> subPickListKeyValuesMap = new HashMap<String, String>();
		String alternateValue = "";

		// Get all FlexTypeAttribute from the given FlexType object, Iterate on
		// each attributes to validate the data type of the attribute as we need
		// to process only master data attributes
		FlexTypeAttribute flexTypeAttributeObj = productFlexTypeObj.getAttribute(hbiKey);
		/*System.out.println(">>>>hbiKey>>>>"+hbiKey+" >>choiceValue>>"+choiceValue+" >>>>alternateKeys>>"+alternateKeys);
		System.out.println(">>>>flexTypeAttributeObj>>>>"+flexTypeAttributeObj.getAttValueList());
		
		System.out.println("#########################################################################");
		System.out.println(">>>>flexTypeAttributeObj.getAttVariableType()>>>> "+flexTypeAttributeObj.getAttVariableType());
		System.out.println("#########################################################################");
		*/

		// Validate the data type of the attribute with ('Single List' 'Multi
		// List', 'Composite' and 'Driven'), get all pickList key-values from
		// each FlexTypeAttribute
		if ("choice".equals(flexTypeAttributeObj.getAttVariableType())
				|| "moaList".equals(flexTypeAttributeObj.getAttVariableType())
				|| "composite".equals(flexTypeAttributeObj.getAttVariableType())
				|| "driven".equals(flexTypeAttributeObj.getAttVariableType())) {
			//mainPickListKeyValuesMap = flexTypeAttributeObj.getAttValueList().getList();
			//mainPickListKeyValuesMap = flexTypeAttributeObj.getAttValueList().getList();
			
			alternateValue = flexTypeAttributeObj.getAttValueList().getValueFromValueStore(choiceValue, alternateKeys);
		//	System.out.println(">>>>>>>>alternateValue>>>>"+alternateValue);
			//flexTypeAttributeObj.getAttValueList().getL
			/*for (String pickListKey : mainPickListKeyValuesMap.keySet()) {
				// Get all key-values from a pickList value, iterate on each
				// keys, validate the key with the alternate keys registered in
				// properties file and invoke internal function
				subPickListKeyValuesMap = mainPickListKeyValuesMap.get(pickListKey);
				for (String alternateKey : subPickListKeyValuesMap.keySet()) {
					// LCSLog.debug("### subPickListKeyValuesMap ###"
					// +subPickListKeyValuesMap);
					String attributeValue = subPickListKeyValuesMap.get(alternateKey);
					// LCSLog.debug("### attributeValue ###" +attributeValue);
					if (choiceValue.equalsIgnoreCase(attributeValue)) {
						alternateValue = subPickListKeyValuesMap.get(alternateKeys);
					}

				}
			}*/
			
		}
		return alternateValue;
	}
	
	/**
	 * This function is using to get LCSLifecycleManaged object for the given
	 * hbiErpGender and hbiErpGenderCat, concatenate the hbiErpGender and
	 * hbiErpGenderCat to get Gender Age.
	 * 
	 * @param productObj
	 *            - LCSProduct
	 * @param divisionIncluded
	 * @param divisionval
	 * @param hbiErpGenderCat
	 *            - String
	 * @param businessObjDerivationTabTypePath
	 *            - String
	 * @return genderAge - String
	 * @throws WTException
	 */
	public Map findDivisonFromDerivationTable(LCSProduct productObj, String divisionval, boolean divisionIncluded)
			throws WTException {
		Map divisionMap = new HashMap();
		Map filter = new HashMap();
		String hbiErpBrandGroup = null;
		String hbiErpBrandName = null;
		String hbiErpBrandOwnerGroup = null;
		String hbiSAPProductGroup = null;
		if (divisionIncluded) {
			filter.put("hbiSAPDivision", divisionval);
		}
		FlexTypeAttribute hbiSalesOrgatt = productObj.getFlexType().getAttribute(SALES_ORG);

		String hbiSalesOrg = hbiSalesOrgatt.getAttValueList().getValue((String) productObj.getValue(SALES_ORG), null);
		filter.put(SALES_ORG, hbiSalesOrg);

		// This function is used to get SAP code and ISO Code from Alternate
		// Keys from Product\BASIC CUT & SEW - SELLING using key and choices.
		try {
			hbiErpBrandGroup = new HBISellingProductTransformationProcessor().getCODEFromAlternateKeys(ERP_BRAND_GROUP,
					(String) productObj.getValue(ERP_BRAND_GROUP), "_CUSTOM_SAP_CODE");
			filter.put(ERP_BRAND_GROUP, hbiErpBrandGroup);
			hbiErpBrandName = new HBISellingProductTransformationProcessor().getCODEFromAlternateKeys(ERP_BRAND_NAME,
					(String) productObj.getValue(ERP_BRAND_NAME), "_CUSTOM_SAP_CODE");
			filter.put(ERP_BRAND_NAME, hbiErpBrandName);
			hbiErpBrandOwnerGroup = new HBISellingProductTransformationProcessor().getCODEFromAlternateKeys(
					ERP_BRAND_OWNER_GROUP, (String) productObj.getValue(ERP_BRAND_OWNER_GROUP), "_CUSTOM_SAP_CODE");
			filter.put(ERP_BRAND_OWNER_GROUP, hbiErpBrandOwnerGroup);
			hbiSAPProductGroup = new HBISellingProductTransformationProcessor().getCODEFromAlternateKeys(
					SAP_PRODUCT_GROUP, (String) productObj.getValue(SAP_PRODUCT_GROUP), "_CUSTOM_SAP_CODE");
			filter.put(SAP_PRODUCT_GROUP, hbiSAPProductGroup);
			
			System.out.println("omni channel "+productObj.getValue(OMNI_SELECTION));
			String hbiOmniSelection = String.valueOf( productObj.getValue(OMNI_SELECTION));
			//Boolean hbiOmniSelection = (Boolean) productObj.getValue(OMNI_SELECTION);
			System.out.println(">>>>>>>>>>> All Atts "+hbiSalesOrg+" "+hbiErpBrandGroup+"  "+hbiErpBrandName+" "+hbiErpBrandOwnerGroup+" "+hbiSAPProductGroup+" "+" "+hbiOmniSelection);
			if ("true".equalsIgnoreCase(hbiOmniSelection)) {
				hbiOmniSelection = "1";

			} else {
				hbiOmniSelection = "0";
			}
			filter.put(OMNI_SELECTION, hbiOmniSelection);
		//	System.out.println(">>>>>>>>>>>>>>>>>>>> filter >>>>>>>>>>>>>>>>>"+filter);

		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String division = "";
		LCSLifecycleManaged businessObject = null;

		businessObject = new HBIInterfaceUtil().getLifecycleManagedByNameType("name", BO_NAME,
				businessObjDerivationTabTypePath);
	//	System.out.println(">>>>>>>>>>>>>> businessObject "+businessObject);
		LCSMOATable derivationtable = null;
		if (businessObject != null) {
			derivationtable = (LCSMOATable) businessObject.getValue(MOA_DIVISION);
		//	System.out.println(">>>>>>>>>>>>>> derivationtable "+ derivationtable);
		}
		if (derivationtable != null) {
			//Collection coll = derivationtable.getRows(filter);
			Collection coll = (Collection) new LCSMOATable(businessObject,MOA_DIVISION).getRows(filter);

		//	System.out.println(">>>>>>>>>>>>>> Collection >>>"+ coll);
			
			
			if (coll.size() == 0) {
				divisionMap.put("Error", "No Divsion Found for the combination of the data ");

			}

			else if (coll.size() > 1) {
				divisionMap = getDivisionValues(coll);
				String errorString = null;
				StringTokenizer st2 = new StringTokenizer((String) divisionMap.get("hbiSAPDivision"), "|~*~|");
				while (st2.hasMoreTokens()) {
					String value = (String) st2.nextElement();
					value = productObj.getFlexType().getAttribute(ERP_DIVISION).getAttValueList()
							.getValue(value, null);
					if (FormatHelper.hasContent(errorString)) {
						errorString += " / " + value;
					} else {
						errorString = value;
					}
				}
				divisionMap.put("Error",
						" Multiple Divsion values found , Please select the one of the Division (BI) value out of these Values : "
								+ errorString);
			} else {
				divisionMap = getDivisionValues(coll);

			}
		}

		return divisionMap;

	}

	private Map getDivisionValues(Collection coll) {
		// TODO Auto-generated method stub
		Map divMap = new HashMap();
		String division = null;
		String legacyDivision = null;
		Iterator itr = coll.iterator();
		try {
			while (itr.hasNext()) {
				FlexObject fob = (FlexObject) itr.next();

				LCSMOAObject moa = (LCSMOAObject) LCSQuery
						.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + fob.getData("OID"));
				if (FormatHelper.hasContent(division)) {
					division += "|~*~|" +"hbi"+(String) moa.getValue("hbiSAPDivision");
					legacyDivision += "|~*~|" + (String) moa.getValue("hbiLegacyDivision");
				} else {
					division ="hbi"+ (String) moa.getValue("hbiSAPDivision");
					legacyDivision = (String) moa.getValue("hbiLegacyDivision");

				}

			}
			divMap.put("hbiSAPDivision", division);
			divMap.put("hbiLegacyDivision", legacyDivision);

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return divMap;
	}

}

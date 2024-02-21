package com.lowes.massimport.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.LCSProperties;
import com.lowes.type.metadata.pojo.TypeAttributesMetaData;
import com.lowes.type.metadata.service.PLMTypeAttributesMetadataService;

import wt.access.NotAuthorizedException;
import wt.fc.ObjectIdentifier;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainerException;
import wt.log4j.LogR;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.session.SessionServerHelper;
import wt.util.WTException;

/***
 * Helper class for Mass Import functionality
 * 
 * @author Samikkannu Manickam (samikkannu.manickam@lowes.com)
 *
 */
public class MassImport {

	public static final int HEADER_ROWS = 4;
	public static final String WORKBOOK_NAME = "Item";
	public static final String LOG_PREFIX = "Mass-Import: ";
	public static final String PRODUCT_COLUMN = "Product";
	public static final String SOURCING_COLUMN = "Sourcing";
	public static final String PACKAGING_COLUMN = "Packaging";
	public static final String COSTSHEET_COLUMN = "Costsheet";
	public static final String PRODUCT_DESCRIPTION_DISPLAY_ATTR = "Product Description";
	public static final String PRODUCT_DESCRIPTION_INTERNAL_ATTR = "vrdDescription";
	public static final String PRODUCT_MODELNUMBER_DISPLAY_ATTR = "Model Number";
	public static final String PRODUCT_MODELNUMBER_INTERNAL_ATTR = "lwsModelNumber";
	public static final String PRODUCT_LOWEST_LEVEL_MARKETING_CATEGORY = "Lowest Level Marketing Category";
	public static final String PRODUCT_PRODUCTSTATUS_INTERNAL_ATTR = "vrdStatus";
	public static final String PRODUCT_PRODUCTSTATUS_INTERNAL_VALUE = "vrdDevelopment";
	public static final String PRODUCT_ITEMSTATUS_INTERNAL_ATTR = "lwsItemStatus";
	public static final String PRODUCT_ITEMSTATUS_INTERNAL_VALUE = "lwsNew";
	public static final String PRODUCT_RFP_INTERNAL_ATTR = "RFP";
	public static final String PRODUCT_RFP_INTERNAL_VALUE = "lwsItem";
	public static final String MASSIMPORT_DOC_RFP_INTERNAL_ATTR = "lwsRFPRef";
	public static final String MASSIMPORT_DOC_SEASON_ATTRIBUTE = "lwsSeason";
	public static final String MASSIMPORT_DOC_VENDOR_ATTRIBUTE = "lwsVendorOR";
	public static final String MASSIMPORT_DOC_RFP_ATTR_VALUE = "lwsrfp";
	public static final String MASSIMPORT_DOC_PRIMARY_RFP_ATTR_VALUE = "lwsPrimaryRFP";
	public static final String MASSIMPORT_DOC_TYPE = "lwsMassImport";
	public static final String SUPPLIER_RELEASE_TO_VENDOR_INTERNAL_ATTR = "vrdReleaseToSupplier";
	public static final String SUPPLIER_RELEASE_TO_VENDOR_VALUE = "vrdYes";
	public static final String PARENT_VENDOR_GROUP = "VENDORS";
	public static final String VENDORS_GROUP_KEY = "VENDOR_GROUPS";
	public static final String ADMIN_GROUP_KEY = "ADMIN_GROUPS";

	public static final String SOURCE_LOWEST_LEVEL_MARKETING_CATEGORY = "Country Of Origin";
	public static final String LCSPRODUCT_TYPE = "com.lcs.wc.product.LCSProduct";
	public static final String LCSSOUCINGCONFIG_TYPE = "com.lcs.wc.sourcing.LCSSourcingConfig";
	public static final String LCSCOSTSHEET_TYPE = "com.lcs.wc.sourcing.LCSProductCostSheet";
	public static final Set<String> FLEX_STRING_TYPE = new HashSet<String>(
			Arrays.asList("text", "textArea", "choice", "moaList"));
	public static final String FLEX_MULTIENTRY_TYPE = "moaList";
	public static final String FLEX_BOOLEAN_TYPE = "boolean";
	public static final String FLEX_DATE_TYPE = "date";
	public static final String FLEX_INTEGER_TYPE = "integer";
	public static final String FLEX_FLOAT_TYPE = "float";
	public static final String FLEX_CURRENCY_TYPE = "currency";
	public static final String FLEX_OBJECT_REF_TYPE = "object_ref";
	public static final String FLEX_OBJECT_REF_COUNTRY = "com.lcs.wc.country.LCSCountry";
	public static final String FLEX_OBJECT_REF_MARKETING_CATEGORY = "com.lcs.wc.foundation.LCSLifecycleManaged";
	public static final String PIPE_SEPARATOR = "|";
	public static final String SPACE_SEPARATOR = " ";
	public static final String VENDOR = "vendor";
	public static final String CHOICE_SEPARATOR = "|~*~|";
	public static final String COSTSHEET_TYPE = "PRODUCT";
	public static final String COSTSHEET_FLEX_TYPE = "Cost Sheet\\lwsLowes";

	public static final String OBJECT_IDENTIFIER_KEY = "thePersistInfo.theObjectIdentifier.id";
	public static final String CHECKOUT_INFO = "checkoutInfo.state";
	public static final String LATEST_ITERATION = "iterationInfo.latest";
	public static final String MASTER_IDENTIFIER_KEY = "masterReference.key.id";
	public static final String VERSION_INFO = "versionInfo.identifier.versionId";

	private static final Logger LOGGER = LogR.getLogger(MassImport.class.getName());
	private PLMTypeAttributesMetadataService typeService = PLMTypeAttributesMetadataService.getTypeSeriveInstance();

	private static final String LOWEST_LOWEL_CATEGORY_TYPE = "Business Object\\lwsLowestLevelMarketingCategory";
	private static final String COUNTRY_TYPE = "Country";
	private static Set<String> authorizedUserSet = new HashSet<String>();
	private static Map<String, LCSLifecycleManaged> lowestLevelCategoryMap = new HashMap<String, LCSLifecycleManaged>();
	private static Map<String, LCSCountry> countryMap = new HashMap<String, LCSCountry>();
	public static final String SLASH = "\\";

	private TypeAttributesMetaData productTypeAttributes = null;
	private TypeAttributesMetaData sourceTypeAttributes = null;
	private TypeAttributesMetaData costSheetTypeAttributes = null;

	private static MassImport instance = null;

	private MassImport() {
	}

	public static MassImport getInstance() {
		if (instance == null) {
			instance = new MassImport();
		}
		return instance;
	}

	public TypeAttributesMetaData getTypeAttributes(String objectType)
			throws NotAuthorizedException, WTContainerException, WTException {
		boolean accessEnabled = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			if (LCSPRODUCT_TYPE.equals(objectType)) {
				if (productTypeAttributes == null) {
					productTypeAttributes = typeService.getPLMTypelMetaData(objectType);
				}
				return productTypeAttributes;
			} else if (LCSSOUCINGCONFIG_TYPE.equals(objectType)) {
				if (sourceTypeAttributes == null) {
					sourceTypeAttributes = typeService.getPLMTypelMetaData(objectType);
				}
				return sourceTypeAttributes;
			} else if (LCSCOSTSHEET_TYPE.equals(objectType)) {
				if (costSheetTypeAttributes == null) {
					costSheetTypeAttributes = typeService.getPLMTypelMetaData(objectType);
				}
				return costSheetTypeAttributes;
			} else {
				return null;
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(accessEnabled);
		}

	}

	public static LCSSupplier querySupplier(String supplierName) throws WTException {
		LCSSupplier supplier = null;
		PreparedQueryStatement preparedQueryStatement = new PreparedQueryStatement();
		preparedQueryStatement.appendFromTable(LCSSupplier.class);
		preparedQueryStatement.appendFromTable(LCSSupplierMaster.class);
		preparedQueryStatement.appendSelectColumn(new QueryColumn(LCSSupplier.class, OBJECT_IDENTIFIER_KEY));
		preparedQueryStatement.appendJoin(new QueryColumn(LCSSupplierMaster.class, OBJECT_IDENTIFIER_KEY),
				new QueryColumn(LCSSupplier.class, MASTER_IDENTIFIER_KEY));
		preparedQueryStatement.appendCriteria(new Criteria(new QueryColumn(LCSSupplierMaster.class, "supplierName"),
				supplierName, Criteria.EQUALS, true));
		preparedQueryStatement.appendAndIfNeeded();
		preparedQueryStatement.appendCriteria(
				new Criteria(new QueryColumn(LCSSupplier.class, CHECKOUT_INFO), "wrk", Criteria.NOT_EQUAL_TO));
		preparedQueryStatement.appendAndIfNeeded();
		preparedQueryStatement.appendCriteria(
				new Criteria(new QueryColumn(LCSSupplier.class, LATEST_ITERATION), "1", Criteria.EQUALS));
		preparedQueryStatement.appendSortBy(new QueryColumn(LCSSupplier.class, OBJECT_IDENTIFIER_KEY), "DESC");
		LOGGER.info("SQL Query to find LCSSupplier : " + preparedQueryStatement.getSqlStatement());
		supplier = (LCSSupplier) LCSQuery.getObjectFromResults(preparedQueryStatement,
				"OR:com.lcs.wc.supplier.LCSSupplier:", "LCSSupplier.IDA2A2");
		return supplier;
	}

	public static LCSLifecycleManaged getLowetLevelCategory(String categoryName) throws WTException {
		LCSLifecycleManaged lowestLevelCategory = null;
		if (lowestLevelCategoryMap.containsKey(categoryName)) {
			lowestLevelCategory = lowestLevelCategoryMap.get(categoryName);
		} else {
			lowestLevelCategory = queryMarketingCategory(categoryName);
			if (lowestLevelCategory != null) {
				lowestLevelCategoryMap.put(categoryName, lowestLevelCategory);
			}
		}

		return lowestLevelCategory;
	}

	public static LCSLifecycleManaged queryMarketingCategory(String categoryName) throws WTException {
		FlexType flexType = FlexTypeCache.getFlexTypeFromPath(LOWEST_LOWEL_CATEGORY_TYPE);
		String nameColumn = flexType.getAttribute("name").getColumnDescriptorName();
		LCSLifecycleManaged category = null;
		PreparedQueryStatement prepQuery = new PreparedQueryStatement();
		prepQuery.appendFromTable(LCSLifecycleManaged.class);
		prepQuery.appendSelectColumn(new QueryColumn(LCSLifecycleManaged.class, OBJECT_IDENTIFIER_KEY));
		prepQuery.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, nameColumn), categoryName,
				Criteria.EQUALS, true));
		category = (LCSLifecycleManaged) LCSQuery.getObjectFromResults(prepQuery,
				"OR:com.lcs.wc.foundation.LCSLifecycleManaged:", "LCSLifecycleManaged.IDA2A2");
		return category;
	}

	public static LCSCountry getCountry(String countryName) throws WTException {
		LCSCountry country = null;
		if (countryMap.containsKey(countryName)) {
			country = countryMap.get(countryName);
		} else {
			country = queryCountry(countryName);
			if (country != null) {
				countryMap.put(countryName, country);
			}
		}
		return country;
	}

	public static LCSCountry queryCountry(String countryName) throws WTException {
		FlexType flexType = FlexTypeCache.getFlexTypeFromPath(COUNTRY_TYPE);
		String nameColumn = flexType.getAttribute("name").getColumnDescriptorName();
		LCSCountry country = null;
		PreparedQueryStatement prepQuery = new PreparedQueryStatement();
		prepQuery.appendFromTable(LCSCountry.class);
		prepQuery.appendSelectColumn(new QueryColumn(LCSCountry.class, OBJECT_IDENTIFIER_KEY));
		prepQuery.appendCriteria(
				new Criteria(new QueryColumn(LCSCountry.class, nameColumn), countryName, Criteria.EQUALS, true));
		prepQuery.appendAndIfNeeded();
		prepQuery.appendCriteria(
				new Criteria(new QueryColumn(LCSCountry.class, CHECKOUT_INFO), "wrk", Criteria.NOT_EQUAL_TO));
		prepQuery.appendAndIfNeeded();
		prepQuery.appendCriteria(
				new Criteria(new QueryColumn(LCSCountry.class, LATEST_ITERATION), "1", Criteria.EQUALS));
		prepQuery.appendSortBy(new QueryColumn(LCSCountry.class, OBJECT_IDENTIFIER_KEY), "DESC");
		country = (LCSCountry) LCSQuery.getObjectFromResults(prepQuery, "OR:com.lcs.wc.country.LCSCountry:",
				"LCSCountry.IDA2A2");
		return country;
	}

	public static Set<String> getAuthorizedUsers() {
		if (authorizedUserSet.size() == 0) {
			String groups = LCSProperties.get("com.lowes.massimport.authorizedUserGroups",
					"Administrators,ORG ADMIN,Lowes Support Admin");
			String[] groupArray = groups.split(",");
			for (String groupName : groupArray) {
				authorizedUserSet.add(groupName.trim());
			}
		}
		return authorizedUserSet;
	}

	public static LCSProduct queryProduct(String productDescription, String modelNumber, FlexType productFlexType,
			String rfpProductId) throws WTException {
		LCSProduct product = null;
		FlexTypeAttribute productDecAttr = productFlexType.getAttribute(PRODUCT_DESCRIPTION_INTERNAL_ATTR);
		FlexTypeAttribute modelNumberAttr = productFlexType.getAttribute(PRODUCT_MODELNUMBER_INTERNAL_ATTR);
		FlexTypeAttribute rfpAttr = productFlexType.getAttribute(PRODUCT_RFP_INTERNAL_ATTR);
		FlexTypeAttribute rfpReferenceAttr = productFlexType.getAttribute(MASSIMPORT_DOC_RFP_INTERNAL_ATTR);
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		stmt.appendFromTable(LCSProduct.class);
		stmt.appendSelectColumn(new QueryColumn(LCSProduct.class, MassImport.OBJECT_IDENTIFIER_KEY));
		stmt.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, productDecAttr.getColumnDescriptorName()),
				productDescription, Criteria.EQUALS, true));
		stmt.appendAndIfNeeded();
		stmt.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, modelNumberAttr.getColumnDescriptorName()),
				modelNumber, Criteria.EQUALS, true));
		stmt.appendAndIfNeeded();
		stmt.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, rfpReferenceAttr.getColumnDescriptorName()),
				rfpProductId, Criteria.EQUALS));
		stmt.appendAndIfNeeded();
		stmt.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, rfpAttr.getColumnDescriptorName()),
				PRODUCT_RFP_INTERNAL_VALUE, Criteria.EQUALS, true));
		stmt.appendAndIfNeeded();
		stmt.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, LATEST_ITERATION), "1", Criteria.EQUALS));
		stmt.appendAndIfNeeded();
		stmt.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, VERSION_INFO), "A", Criteria.EQUALS));
		stmt.appendAndIfNeeded();
		stmt.appendCriteria(
				new Criteria(new QueryColumn(LCSProduct.class, CHECKOUT_INFO), "wrk", Criteria.NOT_EQUAL_TO));

		product = (LCSProduct) LCSProductQuery.getObjectFromResults(stmt, "OR:com.lcs.wc.product.LCSProduct:",
				"LCSProduct.idA2A2");

		return product;
	}

	public static LCSProductSeasonLink getProductSeasonLink(String productARevId, String seasonRevId)
			throws WTException {
		LCSProductSeasonLink productSeasonLink = null;
		PreparedQueryStatement query = new PreparedQueryStatement();
		query.appendFromTable(LCSProductSeasonLink.class);
		query.appendSelectColumn(new QueryColumn(LCSProductSeasonLink.class, OBJECT_IDENTIFIER_KEY));
		query.appendCriteria(
				new Criteria(new QueryColumn(LCSProductSeasonLink.class, "seasonRemoved"), "0", Criteria.EQUALS));
		query.appendAndIfNeeded();
		query.appendCriteria(
				new Criteria(new QueryColumn(LCSProductSeasonLink.class, "effectLatest"), "1", Criteria.EQUALS));
		query.appendAndIfNeeded();
		query.appendCriteria(new Criteria(new QueryColumn(LCSProductSeasonLink.class, "productARevId"), productARevId,
				Criteria.EQUALS));
		query.appendAndIfNeeded();
		query.appendCriteria(
				new Criteria(new QueryColumn(LCSProductSeasonLink.class, "seasonRevId"), seasonRevId, Criteria.EQUALS));
		query.appendSortBy(new QueryColumn(LCSProductSeasonLink.class, "thePersistInfo.updateStamp"), "DESC");
		LOGGER.debug("Product Season Query: " + query.getSqlStatement());
		productSeasonLink = (LCSProductSeasonLink) LCSQuery.getObjectFromResults(query,
				"OR:com.lcs.wc.season.LCSProductSeasonLink:", "LCSProductSeasonLink.IDA2A2");
		LOGGER.debug("LCSProductSeasonLink : " + productSeasonLink);
		return productSeasonLink;
	}

	public static Collection<?> getProductSeasonLinks(String productARevId) throws WTException {
		PreparedQueryStatement query = new PreparedQueryStatement();
		query.appendFromTable(LCSProductSeasonLink.class);
		query.appendSelectColumn(new QueryColumn(LCSProductSeasonLink.class, OBJECT_IDENTIFIER_KEY));
		query.appendCriteria(
				new Criteria(new QueryColumn(LCSProductSeasonLink.class, "seasonRemoved"), "0", Criteria.EQUALS));
		query.appendAndIfNeeded();
		query.appendCriteria(
				new Criteria(new QueryColumn(LCSProductSeasonLink.class, "effectLatest"), "1", Criteria.EQUALS));
		query.appendAndIfNeeded();
		query.appendCriteria(new Criteria(new QueryColumn(LCSProductSeasonLink.class, "productARevId"), productARevId,
				Criteria.EQUALS));
		query.appendSortBy(new QueryColumn(LCSProductSeasonLink.class, "thePersistInfo.updateStamp"), "DESC");
		LOGGER.debug("Product Season Query: " + query.getSqlStatement());
		Collection<?> result = LCSQuery.getObjectsFromResults(query, "OR:com.lcs.wc.season.LCSProductSeasonLink:",
				"LCSProductSeasonLink.IDA2A2");
		LOGGER.debug("LCSProductSeasonLink result size: " + result.size());
		return result;
	}
	
	public static Map<String, Set<String>> getAuthorizedUsers(WTPrincipal principal) throws WTException {
		Map<String, Set<String>> authoriziedUsersMap = new HashMap<>();
		Map<ObjectIdentifier, ObjectIdentifier> userGroupsMap = OrganizationServicesHelper.manager
				.parentGroupMap(principal);
		if (userGroupsMap == null || userGroupsMap.size() == 0) {
			LOGGER.error("Invalid user access. User is not mapped with any Windchill Group! ");
			return authoriziedUsersMap;
		}

		boolean accessEnabled = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			for (Map.Entry<ObjectIdentifier, ObjectIdentifier> set : userGroupsMap.entrySet()) {
				Persistable persisObj = PersistenceHelper.manager.refresh(set.getKey());
				if (persisObj instanceof WTGroup) {
					WTGroup grp = (WTGroup) persisObj;
					String groupName = grp.getName();
					LOGGER.info("Group Name: " + groupName);
					if (getAuthorizedUsers().contains(groupName)) {
						addEntry(ADMIN_GROUP_KEY, authoriziedUsersMap, groupName);
					} else if (isVendorGroup(grp)) {
						addEntry(VENDORS_GROUP_KEY, authoriziedUsersMap, groupName);
					}
				}
			}

		} finally {
			SessionServerHelper.manager.setAccessEnforced(accessEnabled);
		}
		return authoriziedUsersMap;
	}

	private static void addEntry(String key, Map<String, Set<String>> authoriziedUsersMap, String value) {
		if (authoriziedUsersMap.containsKey(key)) {
			authoriziedUsersMap.get(key).add(value);
		} else {
			Set<String> grpList = new HashSet<>();
			grpList.add(value);
			authoriziedUsersMap.put(key, grpList);
		}
	}

	private static boolean isVendorGroup(WTGroup group) throws WTException {
		Enumeration<?> parentGroups = group.parentGroups(false);
		while (parentGroups.hasMoreElements()) {
			WTPrincipalReference parentRef = (WTPrincipalReference) parentGroups.nextElement();
			if (parentRef != null && parentRef.getObject() instanceof WTGroup) {
				WTGroup parentGroup = (WTGroup) parentRef.getObject();
				String groupName = parentGroup.getName();
				if (PARENT_VENDOR_GROUP.equalsIgnoreCase(groupName)) {
					return true;
				}
			}

		}
		return false;
	}

}

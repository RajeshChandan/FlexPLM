package com.hbi.wc.load.sploader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import wt.fc.Persistable;
import wt.fc.PersistenceServerHelper;
import wt.fc.WTObject;
import wt.method.MethodContext;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.client.web.FlexTypeGenerator;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.document.FileRenamer;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSLifecycleManagedQuery;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.load.LoadCommon;
import com.lcs.wc.load.LoadFlexTyped;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.report.FiltersList;
import com.lcs.wc.sizing.FullSizeRange;
import com.lcs.wc.sizing.ProdSizeCategoryToSeason;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sizing.ProductSizeCategoryMaster;
import com.lcs.wc.sizing.ProductSizingLogic;
import com.lcs.wc.sizing.SizeCategory;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

/**
 * 
 * @author Manoj
 * @since 19/3/2019
 * @program This program is used to load Product Size Categories to Selling
 *          Products. It takes four input attribute values to search the product
 *          and update or create a new Product Size Category.
 *
 */
public class HBILoadProductSizeCategory {
	//private static final String MOA_DELIM = "|~*~|";
	private static final String MOA_DELIM =	"\\|~\\*~\\|";
	protected static ProductSizingLogic PRODUCTSIZING_LOGIC = new ProductSizingLogic();
	protected static String FAILED_FILE_LOG_PATH;
	private static String failedFilePath;

	static {
		try {
			String wtHome = WTProperties.getLocalProperties().getProperty("wt.home");
			String logFilePath = FormatHelper
					.formatOSFolderLocation(LCSProperties.get("com.lcs.wc.content.CsvLoadFileLogPath", "none"));
			FAILED_FILE_LOG_PATH = wtHome + logFilePath + "failedExportLines.txt";
			failedFilePath = FAILED_FILE_LOG_PATH;
		} catch (IOException e) {
			throw new RuntimeException("Failed to get required system property:wt.home", e);
		}
	}

	/**
	 * @param dataValues
	 * @param commandLine
	 * @param returnObjects
	 * @return
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("rawtypes")
	public static boolean migratePSC(Hashtable dataValues, Hashtable commandLine, Vector returnObjects)
			throws WTPropertyVetoException {
		return migratePSC(dataValues, LoadCommon.getValue(commandLine, "FileName", false));
	}

	/**
	 * @param dataValues
	 * @param fileName
	 * @return
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("rawtypes")
	protected static boolean migratePSC(Hashtable dataValues, String fileName) throws WTPropertyVetoException {
		try {
			String productType = LoadCommon.getValue(dataValues, "productType", true);
			FlexType prdType = FlexTypeCache.getFlexTypeFromPath(productType);
			LCSProduct product = findProduct(dataValues, prdType);
			LoadCommon.display("\n#INFO: Product Fetched");
			if (product == null) {
				LoadCommon.display("\n#ERROR: No Product Fetched :: ");
				return false;
			} else {
				LoadCommon.display("\n#INFO: Product Fetched :: "+product.getName());
				String size1Vals = LoadCommon.getValue(dataValues, "size1Vals", true);

				if (!FormatHelper.hasContent(size1Vals)) {
					LoadCommon.display("\n#ERROR:the values for size 1 are required");
					return false;
				} else {
					String sizeDefTemplateName = LoadCommon.getValue(dataValues, "sizeDefinitionTemplate", true);
					ProductSizeCategory sizeDefTemplate = findSizeDefTemplate(sizeDefTemplateName, size1Vals);
					
					if (sizeDefTemplate != null) {
						String baseSize1Val = getbaseSize1Val(sizeDefTemplate, size1Vals);
						LoadCommon.display("\n#INFO::   'Size 1 Base Size' :: "+baseSize1Val);
						
						//Check if product is having already PSD created with SizeDefination template
						ProductSizeCategory psdTemplate = findPSDTemplate(product, sizeDefTemplate);
						if (psdTemplate == null) {
							LoadCommon.display("\n#INFO: Creating new PSD ...");
							return createPSD(dataValues, fileName, product, sizeDefTemplate, size1Vals, baseSize1Val);
						} else {
							LoadCommon.display("\n#INFO:Updating ...Existing PSD found :: "+psdTemplate.getName());
							return updatePSD(dataValues, fileName, product, psdTemplate, size1Vals, baseSize1Val);
						}

					} else {
						LoadCommon.display("\n#ERROR:No Size Definition Template Found!!!");
						return false;
					}

				}
			}
		} catch (WTException e) {
			e.printStackTrace();
			LoadCommon.display("\n#WTException : " + e.getLocalizedMessage());
		}
		return false;
	}

	/**
	 * @param dataValues
	 * @param fileName
	 * @param product
	 * @param sizeDefTemplate
	 * @param size1Vals
	 * @param baseSize1Val
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean createPSD(Hashtable dataValues, String fileName, LCSProduct product,
			ProductSizeCategory sizeDefTemplate, String size1Vals, String baseSize1Val) throws WTException {
		ProductSizeCategory psc = ProductSizeCategory.newProductSizeCategory();
		FlexType type = FlexTypeCache.getFlexTypeRoot("Size Definition");
		FlexType compatibleType = product.getFlexType().getReferencedFlexType("SIZING_TYPE_ID");
		SizeCategory sizeCategory = new SizeCategory();
		FullSizeRange fullSizeRange = new FullSizeRange();

		if (compatibleType == null) {
			LoadCommon.display("\n#ERROR: The Product's type setting for Size Definition type is not defined\n");
			return false;
		} else if (!compatibleType.isAssignableFrom(type)) {
			// Prints compatible type errors in log
			printErrorsInLog(dataValues, fileName, product, compatibleType, compatibleType, size1Vals, baseSize1Val);
			return true;
		} else {
			dataValues.put("Type", type.getFullName(true));
			if ((psc = (ProductSizeCategory) LoadCommon.getObjectByCriteria(fileName, psc, dataValues)) == null) {
				return true;
			} else {
				try {
					
					psc.setSizeCategoryType("INSTANCE");
					psc.setSizeCategory(sizeDefTemplate.getSizeCategory());
					psc.setFullSizeRange(sizeDefTemplate.getFullSizeRange());
					psc.setSizeValues(size1Vals);
					psc.setBaseSize(baseSize1Val);
					psc.setFlexType(type);
					//psc.setProductMaster((WTPartMaster) product.getMaster());
					psc.setProductMaster((LCSPartMaster) product.getMaster());
					LoadCommon.display("\nsizeDefTemplate.getMaster(): " + sizeDefTemplate.getMaster());
					psc.setSizeRange((ProductSizeCategoryMaster) sizeDefTemplate.getMaster());
					return save(psc, dataValues, fileName);
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
					LoadCommon.display("\n#WTPropertyVetoException : " + e.getLocalizedMessage());
				}
			}
		}
		return false;
	}

	/**
	 * @param sizeDefTemplate
	 * @param size1Vals
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String getbaseSize1Val(ProductSizeCategory sizeDefTemplate, String size1Vals) {
		String baseSize1Val = sizeDefTemplate.getBaseSize();
		LoadCommon.display("\n#INFO: sizeDefTemplate's baseSize1Val " + baseSize1Val);
		
		String sizeArray[] = size1Vals.split(MOA_DELIM);
		Collection sizeList = Arrays.asList(sizeArray);
		LoadCommon.display("\n#INFO: size1 value List to be loaded :: " + sizeList);
		if (sizeList.contains(baseSize1Val)) {
			return baseSize1Val;
		} else {
			baseSize1Val = sizeArray[0];
		}
		return baseSize1Val;
	}

	/**
	 * @param dataValues
	 * @param fileName
	 * @param psdTemplate
	 * @param size1Vals
	 * @param baseSize1Val
	 * @return
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	private static boolean updatePSD(Hashtable dataValues, String fileName, LCSProduct product,
			ProductSizeCategory psdTemplate, String size1Vals, String baseSize1Val)
			throws WTPropertyVetoException, WTException {
		psdTemplate.setSizeValues(size1Vals);
		psdTemplate.setBaseSize(baseSize1Val);
		return save(psdTemplate, dataValues, fileName);

	}

	/**
	 * @param product
	 * @param sizeDefTemplate
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static ProductSizeCategory findPSDTemplate(LCSProduct product, ProductSizeCategory sizeDefTemplate)
			throws WTException {
		ProductSizeCategory psdTemp = null;
		//SearchResults psdResults = new SizingQuery().findPSDByProductAndSeason(product);
		SearchResults psdResults = SizingQuery.findProductSizeCategoriesForProduct(product);
		boolean isPSDUnique = true;
		if (psdResults.getResultsFound() > 0) {
			Collection<FlexObject> psdColl = psdResults.getResults();
			Iterator itr = psdColl.iterator();
			while (itr.hasNext()) {
				FlexObject psd = (FlexObject) itr.next();
				LoadCommon.display("\n#INFO: PSD OBJECT\n " + psd);
				String psdcatName = psd.getString("SIZECATEGORY.NAME");
				String psdFullSizeRange = psd.getString("FULLSIZERANGE.NAME");
				if (psdcatName.equals(sizeDefTemplate.getSizeCategory().getName())
						&& psdFullSizeRange.equals(sizeDefTemplate.getFullSizeRange().getName())) {
					if (isPSDUnique) {
						psdTemp = (ProductSizeCategory) LCSQuery
								.findObjectById("OR:com.lcs.wc.sizing.ProductSizeCategory:"
										+ psd.getString("PRODUCTSIZECATEGORY.IDA2A2"));
						isPSDUnique = false;
					} else {
						throw new WTException(
								"ERROR !! More than one PSD Template found on the product with same category and Full size range");

					}
				}else{
					
				}
			}
		} else {
			LoadCommon.display("\n#INFO: NO Existing PSD's found on Product");
			
			psdTemp = null;
		}
		return psdTemp;
	}

	/**
	 * @param sizeDefinitionTemplate
	 * @param size1Vals
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	private static ProductSizeCategory findSizeDefTemplate(String sizeDefinitionTemplate, String size1Vals)
			throws WTException {
		ProductSizeCategory sizeDefTemp = null;
		Collection<ProductSizeCategory> asrCol = LCSQuery.getObjectsFromResults(
				SizingQuery.findActualSizeRangeData().getResults(), "OR:com.lcs.wc.sizing.ProductSizeCategory:",
				"PRODUCTSIZECATEGORY.IDA2A2");
		LoadCommon.display("\n#INFO: Total Size defination templates found in FlexPLM Library :: " + asrCol.size());
		
		sizeDefTemp = findSizeDefTemplateFromLibrary(asrCol, sizeDefinitionTemplate, size1Vals);

		return sizeDefTemp;
	}

	/**
	 * @param asrCol
	 * @param sizeDefinitionTemplate
	 * @param size1Vals
	 * @return
	 * @throws WTException
	 */
	private static ProductSizeCategory findSizeDefTemplateFromLibrary(Collection<ProductSizeCategory> asrCol,
			String sizeDefinitionTemplate, String size1Vals) throws WTException {

		ProductSizeCategory sizeCat = null;
		for (ProductSizeCategory psSzCat : asrCol) {
			String templateName = psSzCat.getName();
			String templateSize1Values = psSzCat.getSizeValues();
			if (FormatHelper.hasContent(templateName) && FormatHelper.hasContent(templateSize1Values)) {
				if (templateName.equals(sizeDefinitionTemplate)) {
					if (isHavingAllsize1Vals(size1Vals, templateSize1Values)) {

						sizeCat = (ProductSizeCategory) psSzCat.duplicate();
						break;
					} else {

						LoadCommon.display("\n#ERROR: ALL Size1 values [" + size1Vals
								+ "] not found in size definition templateSize1Values " + templateSize1Values);
						throw new WTException("!!! Exception ALL Size1 values [" + size1Vals
								+ "] not found in size definition templateSize1Values " + templateSize1Values);
					}
				} else {

				}
			} else {

			}
		}
		return sizeCat;

	}

	/**
	 * @param size1Vals
	 * @param templateSize1Values
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static boolean isHavingAllsize1Vals(String size1Vals, String templateSize1Values) {
		LoadCommon.display("\n#INFO: size1Vals to be loaded :: "+size1Vals);
		LoadCommon.display("\n#INFO: Existing size1Vals on the Template :: "+templateSize1Values);
		String tempSize[] = templateSize1Values.split(MOA_DELIM);
		Collection tempSizeList = Arrays.asList(tempSize);
		String size1ValsArray[] =  size1Vals.split(MOA_DELIM);
		Collection size1ValsList = Arrays.asList(size1ValsArray);
		Iterator itr = size1ValsList.iterator();
		while(itr.hasNext()){
			String size1Val = (String) itr.next();
			if(!tempSizeList.contains(size1Val)){
				return false;
			}
		}
		return true;
	}

	/**
	 * @param dataValues
	 * @param fileName
	 * @param product
	 * @param compatibleType
	 * @param type
	 * @param size1Vals
	 * @param baseSize1Val
	 */
	@SuppressWarnings("rawtypes")
	private static void printErrorsInLog(Hashtable dataValues, String fileName, LCSProduct product,
			FlexType compatibleType, FlexType type, String size1Vals, String baseSize1Val) {
		LoadCommon.display(
				"\n#ERROR: The type of Product Size Definition is not compatible with the Product's type setting for Size Definition type. Please check the latest \"failedExportLines\" log file for details.");
		outputToFailedLogFile(" ");
		outputToFailedLogFile(
				"\n#ERROR: The type of Product Size Definition is not compatible with the Product's type setting for Size Definition type ");
		outputToFailedLogFile("#Product (Id:" + product.getBranchIdentifier() + ", Name:" + product.getName() + ")");
		outputToFailedLogFile("#Product Size Definition (oldId:" + dataValues.get("flexAttoldId:SearchCriteria") + ")");
		outputToFailedLogFile(
				"#The type of Product Size Definition/Size Definition Template is " + type.getFullName(true));
		outputToFailedLogFile(
				"#The Product's type setting for Size Definition type is " + compatibleType.getFullName(true) + "\n");
		StringBuilder sb = new StringBuilder();
		sb.append("MigrateProductSizeCategory").append("\t");
		sb.append(product.getBranchIdentifier()).append("\t");
		sb.append(size1Vals).append("\t");

		sb.append(baseSize1Val).append("\t");
		sb.append(dataValues.get("flexAttoldId:SearchCriteria"));
		outputToFailedLogFile(sb.toString());
		LoadCommon.putCache(fileName, "EntityLoadSuccess", new Boolean(false));
	}

	/**
	 * @param dataValues
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static LCSProduct findProduct(Hashtable dataValues, FlexType prdType) throws WTException {
		LCSProduct product = null;
		String sellingStyleNumber = LoadCommon.getValue(dataValues, "hbiSellingStyleNumber", true);
		String productDescription = LoadCommon.getValue(dataValues, "hbiDescription", true);
		String apsPackQuantity = LoadCommon.getValue(dataValues, "hbiAPSPackQuantity", true);
		String erpAttributionCode = LoadCommon.getValue(dataValues, "hbiErpAttributionCode", true);
		AttributeValueList avList = prdType.getAttribute("hbiAPSPackQuantity").getAttValueList();
		Map<String, Map<String, String>> avMap = avList.getList();
		if (FormatHelper.hasContent(apsPackQuantity) && !avMap.isEmpty()) {
			for (Entry<String, Map<String, String>> entry : avMap.entrySet()) {
				if (apsPackQuantity.equals(entry.getValue().get("VALUE"))) {
					apsPackQuantity = entry.getKey();
					break;
				}
			}
		}
		HashMap<String, String> criteria = new HashMap<String, String>();
		Collection<FlexTypeAttribute> attCols = new ArrayList<FlexTypeAttribute>();
		if (FormatHelper.hasContent(sellingStyleNumber) && FormatHelper.hasContent(productDescription)
				&& FormatHelper.hasContent(apsPackQuantity) && FormatHelper.hasContent(erpAttributionCode)) {
			criteria.put("hbiSellingStyleNumber", sellingStyleNumber);
			criteria.put("hbiDescription", productDescription);
			criteria.put("hbiAPSPackQuantity", apsPackQuantity);
			criteria.put("hbiErpAttributionCode", erpAttributionCode);
			FlexTypeAttribute fTypSellingStyleNum = prdType.getAttribute("hbiSellingStyleNumber");
			FlexTypeAttribute fTypProdDesc = prdType.getAttribute("hbiDescription");
			FlexTypeAttribute fTypPackQty = prdType.getAttribute("hbiAPSPackQuantity");
			FlexTypeAttribute fTypAttributionCode = prdType.getAttribute("hbiErpAttributionCode");
			attCols.add(fTypSellingStyleNum);
			attCols.add(fTypProdDesc);
			attCols.add(fTypPackQty);
			attCols.add(fTypAttributionCode);
		} else {
			LoadCommon.display(
					"\n#ERROR:Mandatory Attributes Selling Style Number or Product Description or APS Pack quantity"
							+ " or Attribution Code is missing");
			return product;
		}
		Collection<FlexObject> prodColl = findSPProductsByCriteria(criteria, prdType, attCols, null);
		String productId = "";
		if (prodColl.size() > 0) {
			for (FlexObject flx : prodColl) {
				productId = flx.getString("LCSPRODUCT.BRANCHIDITERATIONINFO");
			}
		}
		if (!FormatHelper.hasContent(productId)) {
			LoadCommon.display(
					"\n#ERROR:Id of associated Product is null. No product exists for the product attributes in data file");
			return product;
		}
		product = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + productId);

		if (product == null) {
			LoadCommon.display("\n#ERROR:Cannot find any Product for the product ID. Query returned null !!!");
			return product;
		}
		return product;

	}

	/**
	 * @param map
	 * @param type
	 * @param attCols
	 * @param filter
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Collection findSPProductsByCriteria(Map map, FlexType type, Collection attCols, FiltersList filter)
			throws WTException {
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(LCSProduct.class);
		statement.appendFromTable("prodarev");

		statement.appendSelectColumn(new QueryColumn(LCSProduct.class, "iterationInfo.branchId"));
		statement.appendSelectColumn(new QueryColumn("LCSPRODUCT", type.getAttribute("productName").getVariableName()));
		statement.addLatestIterationClause(LCSProduct.class);

		FlexTypeGenerator flexg = new FlexTypeGenerator();
		flexg.setScope("PRODUCT");
		flexg.setLevel("PRODUCT");
		statement.appendFromTable(FlexType.class);
		statement.appendJoin(new QueryColumn(LCSProduct.class, "flexTypeReference.key.id"),
				new QueryColumn(FlexType.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.addPossibleSearchCriteria(new QueryColumn(LCSProduct.class, "versionInfo.identifier.versionId"), "A",
				true);
		statement = flexg.generateSearchCriteria(type, statement, map, true, "prodarev");

		statement = flexg.appendQueryColumns(attCols, type, statement, "prodarev", null);
		statement.appendJoin(new QueryColumn(LCSProduct.class, "masterReference.key.id"),
				new QueryColumn("prodarev", "idA3masterReference"));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(
				new QueryColumn("LCSPRODUCT", type.getAttribute("hbiSellingStyleNumber").getVariableName()),
				(String) map.get("hbiSellingStyleNumber"), "="));
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn("LCSPRODUCT", type.getAttribute("hbiDescription").getVariableName()),
						(String) map.get("hbiDescription"), "="));
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn("LCSPRODUCT", type.getAttribute("hbiAPSPackQuantity").getVariableName()),
						(String) map.get("hbiAPSPackQuantity"), "="));
		String attrCode = (String) map.get("hbiErpAttributionCode");
		if (FormatHelper.hasContent(attrCode)) {
			Map criteria = new HashMap();
			FlexType attrCodeType = FlexTypeCache.getFlexTypeFromPath(
					"Business Object\\Automation Support Tables\\Attribution Codes and Descriptions");
			FlexTypeAttribute att = attrCodeType.getAttribute("hbiErpAttributionCode");
			String searchIndex = att.getSearchCriteriaIndex();
			criteria.put(searchIndex, attrCode);
			SearchResults attrCodeSR = new LCSLifecycleManagedQuery().findLifecycleManagedsByCriteria(criteria,
					attrCodeType, null, null, null);
			if (attrCodeSR.getResultsFound() > 0) {
				Collection attrCodeColl = attrCodeSR.getResults();
				FlexObject attrCodeFO = (FlexObject) attrCodeColl.iterator().next();
				attrCode = attrCodeFO.getString("LCSLIFECYCLEMANAGED.IDA2A2");
			}
			statement.appendAndIfNeeded();
			statement.appendCriteria(new Criteria(
					new QueryColumn("LCSPRODUCT", type.getAttribute("hbiErpAttributionCode").getVariableName()),
					attrCode, "="));
		}
		statement.appendSortBy(new QueryColumn("prodarev",
				FlexTypeCache.getFlexTypeRoot("Product").getAttribute("productName").getVariableName()), false);

		return LCSQuery.runDirectQuery(statement).getResults();
	}

	/**
	 * 
	 */
	public static void initializeFailedLogPath() {
		File outputLogFile = new File(FAILED_FILE_LOG_PATH);
		failedFilePath = FileRenamer.rename(outputLogFile).getAbsolutePath();
	}

	/**
	 * @param line
	 */
	private static void outputToFailedLogFile(String line) {
		File outputLogFile = new File(failedFilePath);
		PrintWriter out = null;
		try {
			if (outputLogFile.exists()) {
				out = new PrintWriter(new FileOutputStream(outputLogFile, true));
			} else {
				out = new PrintWriter(new FileOutputStream(outputLogFile));
				out.println(
						"#WARNING: Modifying size range in this file and re-importing could result in mismatched POMs and BOM override records.");
			}
			out.println(line);
			return;
		} catch (FileNotFoundException e) {
			LoadCommon.display("Fatal Error: file cannot be opened" + failedFilePath);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception ex) {
					LoadCommon.display("Fatal Error: file cannot be closed:" + failedFilePath);
				}
			}
		}
	}

	/**
	 * 
	 */
	public HBILoadProductSizeCategory() {
	}

	/**
	 * @param flextyped
	 * @param dataValues
	 * @param fileName
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	protected static boolean save(FlexTyped flextyped, Hashtable dataValues, String fileName) throws WTException {
		LoadCommon.display("Started Saving... "+ dataValues);

		String deriveStringsValue = null;
		if (dataValues.containsKey("DERIVE_STRINGS")) {
			deriveStringsValue = (String) dataValues.get("DERIVE_STRINGS");
		} else {
			deriveStringsValue = "false";
		}
		MethodContext.getContext().put("DERIVE_STRINGS", deriveStringsValue);

		String deriveNumericValue = null;
		if (dataValues.containsKey("DERIVE_NUMERICS")) {
			deriveNumericValue = (String) dataValues.get("DERIVE_NUMERICS");
		} else {
			deriveNumericValue = "false";
		}
		MethodContext.getContext().put("DERIVE_NUMERICS", deriveNumericValue);

		if ((!(flextyped instanceof LCSProduct)) && (!(flextyped instanceof LCSSKU))) {
			LoadFlexTyped.setAttributes(flextyped, dataValues, fileName);
		}
		String updateMode = LoadCommon.getValue(dataValues, "UPDATEMODE");
		LoadCommon.display("Saving " + flextyped + " With UpdateMode = " + updateMode);

		if ((wt.fc.PersistenceHelper.isPersistent(flextyped)) && (updateMode != null)) {
			if (updateMode.equalsIgnoreCase("IGNORE")) {
				LoadCommon.display("The Object Has Not Been Saved Yet ...");
			} else if (updateMode.equalsIgnoreCase("NOITERATE_PLUGIN")) {
				LCSLogic.deriveFlexTypeValues(flextyped);
				flextyped = (FlexTyped) LCSLogic.persist((Persistable) flextyped);
			} else if (updateMode.equalsIgnoreCase("NOITERATE_NOPLUGIN")) {
				PersistenceServerHelper.manager.update((Persistable) flextyped);

				flextyped = (FlexTyped) PersistenceServerHelper.manager.restore((WTObject) flextyped);
				LCSLogic.loadMethodContextCache((Persistable) flextyped);
			} else {
				LoadCommon
						.display("Invalid Value For UPDATEMODE '" + updateMode + "' The Object Has Not Been Saved !!!");
			}

			LoadCommon.putCache(fileName, flextyped);

			MethodContext.getContext().remove("DERIVE_STRINGS");
			MethodContext.getContext().remove("DERIVE_NUMERICS");
			return true;
		}
		FlexTyped seasonalFlexTyped = (FlexTyped) LoadCommon.getCache(fileName, "CURRENT_SEASONPRODUCTLINK");

		if ((flextyped instanceof ProductSizeCategory)) {
			flextyped = PRODUCTSIZING_LOGIC.saveProductSizeCategory((ProductSizeCategory) flextyped);
			LoadCommon.display("INFO:: !!!! ProductSizeCategory saved");
		} else if ((flextyped instanceof ProdSizeCategoryToSeason)) {
			flextyped = PRODUCTSIZING_LOGIC.saveProdSizeCategoryToSeason((ProdSizeCategoryToSeason) flextyped);
			LoadCommon.display("INFO:: !!!! ProdSizeCategoryToSeason saved");
		} else {
			throw new WTException("Unable To Identify The Class Of The Object (" + flextyped + ") TO Be Saved !!!");
		}

		if (flextyped == null) {
			MethodContext.getContext().remove("DERIVE_STRINGS");
			MethodContext.getContext().remove("DERIVE_NUMERICS");

			return false;
		}

		if ((flextyped = LoadCommon.postLoadProcess(flextyped, dataValues, fileName)) == null) {
			MethodContext.getContext().remove("DERIVE_STRINGS");
			MethodContext.getContext().remove("DERIVE_NUMERICS");

			return false;
		}

		LoadCommon.putCache(fileName, flextyped);

		MethodContext.getContext().remove("DERIVE_STRINGS");
		MethodContext.getContext().remove("DERIVE_NUMERICS");
		LoadCommon.display("Completed Saving... ");
		return true;
	}

}

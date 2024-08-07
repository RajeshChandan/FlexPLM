package com.sportmaster.wc.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.client.web.FlexTypeGenerator;
import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.construction.ConstructionFlexTypeScopeDefinition;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FootwearApparelFlexTypeScopeDefinition;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;

/**
 * 
 * @author ITC.
 * @version 1.1
 * 
 *
 *This class generates Workmanship Dispersion Report which contain 
 *construction detail used by product within selected Season.
 */
public class SMWorkmanshipReport {
	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger("WORKMANSHIPREPORTLOG");
	/**
	 * constant LCSCONSTRUCTIONDETAIL2.
	 */
	private static final String LCSCONSTRUCTIONDETAIL2 = "LCSCONSTRUCTIONDETAIL2";
	/**
	 * constant CONSTRUCTION.
	 */
	private static final String CONSTRUCTION = "Construction";
	/**
	 * constant IDA3MASTERREFERENCE.
	 */
	private static final String IDA3MASTERREFERENCE = "IDA3MASTERREFERENCE";
	/**
	 * constant LCSCONSTRUCTIONDETAIL.
	 */
	private static final String LCSCONSTRUCTIONDETAIL = "LCSCONSTRUCTIONDETAIL";
	/**
	 * constant LCSCONSTRUCTIONINFOMASTER2.
	 */
	private static final String LCSCONSTRUCTIONINFOMASTER2 = "LCSCONSTRUCTIONINFOMASTER2";
	/**
	 * constant LCSCONSTRUCTIONINFOMASTER1.
	 */
	private static final String LCSCONSTRUCTIONINFOMASTER1 = "LCSCONSTRUCTIONINFOMASTER1";
	/**
	 * constant LATESTITERATIONINFO.
	 */
	private static final String LATESTITERATIONINFO = "LATESTITERATIONINFO";
	/**
	 * constant IDA2A2.
	 */
	private static final String IDA2A2 = "IDA2A2";
	/**
	 * constant LCSCONSTRUCTIONINFO2.
	 */
	private static final String LCSCONSTRUCTIONINFO2 = "LCSCONSTRUCTIONINFO2";
	/**
	 * constant LCSCONSTRUCTIONINFO.
	 */
	private static final String LCSCONSTRUCTIONINFO = "LCSCONSTRUCTIONINFO";
	/**
	 * constant BRANCHIDITERATIONINFO.
	 */
	private static final String BRANCHIDITERATIONINFO = "BRANCHIDITERATIONINFO";
	/**
	 * Constant LCSPRODUCTSEASONLINK.
	 */
	private static final String LCSPRODUCTSEASONLINK = "LCSPRODUCTSEASONLINK";
	/**
	 * Constant LCSSEASON.
	 */
	private static final String LCSSEASON = "LCSSEASON";
	/**
	 * Constant for LCSProduct.
	 */
	private static final String LCSPRODUCT = "LCSPRODUCT";
	/**
	 * DELIMITER To split the Attribute internal name and Attribute type.
	 */
	private static final String SPLIT_DELIM = LCSProperties
			.get("com.sportmaster.wc.reports.delimiter");
	/**
	 * Contants are used as Map Keys
	 * 
	 * Constant for Product Type.
	 */
	private static final String PRODUCT_TYPE = "Product";
	/**
	 * Constant for Season Type.
	 */
	private static final String SEASON_TYPE = "Season";
	/**
	 * Constant for Product Season Link Type.
	 */
	private static final String PRODUCTSEASONLINK_TYPE = "ProductSeasonLink";
	/**
	 * Constant for Construction Detail.
	 */
	private static final String CONSTRUCTIONDETAIL_TYPE = "Construction Detail";
	
	/**
	 * Constant for Specification Type.
	 */
	private static final String SPECIFICATION = "Specification";
	/**
	 * Constant for Construction Info.
	 */
	private static final String CONSTRUCTIONINFOTYPE = "Construction Info";
	/**
	 * Constant for Construction Template.
	 */
	private static final String CONSTRUCTIONTEMPLATETYPE = "Construction Template";

	/**
	 * Constant for Sub class division Type.
	 */
	private static  String subclassdivisionHierarchy ;

	/**
	 * Variable for excelColumns.
	 */
	private static Collection<String> excelColumns;

	/**
	 * List type variable for attribute names.
	 */
	private static List<String> attributes;

	// List of Report TableColumn
	private Collection<TableColumn> reportColumns;
	/**
	 * Variable for TableColumn map.
	 */
	private Map<String, TableColumn> columnMap;

	/**
	 * Variable for array list productFlexTypeNames.
	 */
	private static Collection<String> productFlexTypeNames = new ArrayList<String>();
	/**
	 * Variable for array list productSeasonFlexTypeNames.
	 */
	private static Collection<String> productSeasonFlexTypeNames = new ArrayList<String>();

	/**
	 * Variable for array list seasonFlexTypeNames.
	 */
	private static Collection<String> seasonFlexTypeNames = new ArrayList<String>();
	/**
	 * Variable for array list specificationFlexTypeNames.
	 */
	private static Collection<String> specificationFlexTypeNames = new ArrayList<String>();
	/**
	 * Variable for map of Business Object FlexType.
	 */
	private static Map<String, FlexType> flexTypeMap = new HashMap<String, FlexType>();

	/**
	 * Variable for map attrInternalNameMap.
	 */
	private static Map<String, String> attrInternalNameMap = new HashMap<String, String>();
	/**
	 * Variable for map attrColumnDisplayMap.
	 */
	private static Map<String, String> attrColumnDisplayMap = new HashMap<String, String>();
	/**
	 * Variable for map (ProductLatestID and productARevID Map),
	 * prodLatestIdAndARevMap.
	 */
	private Map<String, String> prodLatestIdAndARevMap = new HashMap<String, String>();
	/**
	 * Variable for array list seasonFlexTypes.
	 */
	private static List<FlexType> seasonFlexTypes = new ArrayList<FlexType>();
	/**
	 * Variable for list prodFlexTypes.
	 */
	private static List<FlexType> prodFlexTypes = new ArrayList<FlexType>();
	/**
	 * Variable for list prodSeasonFlexTypes.
	 */
	private static List<FlexType> prodSeasonFlexTypes = new ArrayList<FlexType>();
	/**
	 * Variable for list constructionInfoFlexTypes.
	 */
	private static List<FlexType> constructionInfoFlexTypes = new ArrayList<FlexType>();
	
	/**
	 * Variable for list specificationFlexTypes.
	 */
	private static List<FlexType> specificationFlexTypes = new ArrayList<FlexType>();

	/**
	 * Variable for map of DB TableName, tableNameMap.
	 */
	private static Map<String, String> tableNameMap = new HashMap<String, String>();

	/**
	 * Variable for Map selectedParams, Initialized in constructor.
	 */
	private Map<String, String> selectedParams;
	/**
	 * Variable for Map seasonProdResultData, matched ProductSeaosn SearchResult
	 * is saved in this map.
	 */
	private Map<String, FlexObject> seasonProdResultData = new HashMap<String, FlexObject>();

	// Construction Detail Attributes for matching
	/**
	 * Attribute for construction Detail Number from Property.
	 */
	private String cdNumberKey = LCSProperties
			.get("com.sportmaster.wc.reports.constructionDetail.numberKey");
	
	/**
	 * Attribute for Season department from Property.
	 */
	private String seasonDeptKey = LCSProperties
			.get("com.sportmaster.wc.reports.season.departmentKey");
	// Product Attributes
	/**
	 * Attribute for Product sub class from Property.
	 */
	private String prodsubClassKey = LCSProperties
			.get("com.sportmaster.wc.reports.product.subClassKey");
	// Business Object/Sub Class Division Tree Attributes
	/**
	 * Attribute for BO sub category from Property.
	 */
	private String boStyleSubCategoryKey = LCSProperties
			.get("com.sportmaster.wc.reports.bussObjSubDivTree.subCategoryKey");
	/**
	 * Attribute for BO Style Class from Property.
	 */
	private String boStyleClassKey = LCSProperties
			.get("com.sportmaster.wc.reports.bussObjSubDivTree.classKey");
	/**
	 * Attribute for technologist from Property.
	 */
	// ProductSeasonLink Attribute
	private String pslTechnologistKey = LCSProperties
			.get("com.sportmaster.wc.reports.productSeasonLink.technologistKey");
	

	/**
	 * Remove hyperlink attribute list
	 */
		private String removeHyperLinkattributes = LCSProperties.
				get("com.sportmaster.wc.reports.removehyperLink");

	public SMWorkmanshipReport(){

	}
	//Parameterized Constructor 
	public SMWorkmanshipReport(Map<String,String> selectedParams) {
		// Setting selected params to run a query
		this.selectedParams = selectedParams; 		
	}

	/**
	 * setting flextypes and Attributes from property entry on class load.
	 * Static block to initialize FlexType and HashMaps.
	 */
	static{
		try {

			subclassdivisionHierarchy = LCSProperties.get("com.sportmaster.wc.reports.subClassDivTreeFlexType");

			excelColumns = MOAHelper.getMOACollection(LCSProperties.get("com.sportmaster.wc.reports.excelColumns"));

			// FlexType Path, Root and ChildPath list
			productFlexTypeNames = MOAHelper.getMOACollection(LCSProperties.get("com.sportmaster.wc.reports.productTypes"));			
			productSeasonFlexTypeNames = MOAHelper.getMOACollection(LCSProperties.get("com.sportmaster.wc.reports.productSeasonFlexTypes"));			
			seasonFlexTypeNames = MOAHelper.getMOACollection(LCSProperties.get("com.sportmaster.wc.reports.seasonTypes"));			
			specificationFlexTypeNames = MOAHelper.getMOACollection(LCSProperties.get("com.sportmaster.wc.reports.specificationTypes"));

			

			// Map to get DB Table name by type			  
			tableNameMap.put(PRODUCT_TYPE, "LCSPRODUCT");
			tableNameMap.put(SEASON_TYPE, "LCSSEASON");
			tableNameMap.put(PRODUCTSEASONLINK_TYPE, "LCSPRODUCTSEASONLINK");
			tableNameMap.put(CONSTRUCTIONDETAIL_TYPE, "Construction Detail");
			tableNameMap.put(CONSTRUCTIONINFOTYPE, LCSCONSTRUCTIONINFO);
			tableNameMap.put(CONSTRUCTIONTEMPLATETYPE, LCSCONSTRUCTIONINFO2);
			tableNameMap.put(SPECIFICATION, "FLEXSPECIFICATION");						 

			initExcelColumn();	
			
		} catch (WTException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Method to initialize attribute list, Column header text and attribute internal name map.
	 * @throws WTException the WTException.
	 */
	private static void initExcelColumn() throws WTException{
		if (excelColumns != null) {
			attributes = new ArrayList<String>();
			for (String column : excelColumns) {
				String[] values = column.split(SPLIT_DELIM);

				// List of all the attribute internal names for reference
				String key = values[0]+"."+values[2];			 

				attributes.add(key);
				attrColumnDisplayMap.put(key, values[1]);
				attrInternalNameMap.put(key, values[2]);
			}

			/**
			 * Adding flexType to map by flexPath list Calling getFlexType
			 * methods to initialize FlexType map. Maps are initialized based on
			 * flex type set to attribute in property entry.
			 */			
			getFlexTypesByPath(productFlexTypeNames, prodFlexTypes);
			getFlexTypesByPath(productSeasonFlexTypeNames,prodSeasonFlexTypes);
			getFlexTypesByPath(seasonFlexTypeNames, seasonFlexTypes);
			getFlexTypesByPath(specificationFlexTypeNames,specificationFlexTypes); 	
		}
	}

	/**
	 * Getting FlexType by flex path
	 * @param flexTypePaths the flexTypePaths.
	 * @param flexTypes the flexTypes.
	 * @throws WTException the WTException.
	 */
	private static void getFlexTypesByPath(Collection<String> flexTypePaths,
			List<FlexType> flexTypes) throws WTException {
		
		for (String typePath : flexTypePaths) {
			flexTypes.add(FlexTypeCache.getFlexTypeFromPath(typePath));
		}
	}

	/**
	 * Method is called to get list of lastest ProductSeason ID's that matches filters i.e., Season, Department, Technologist and Product Sub-Class.
	 * @param selectedSeasons the selectedSeasons.
	 * @param selectedDepts the selectedDepts. 
	 * @param selectedTechnologiest the selectedTechnologiest.
	 * @param selectedSubclasses the selectedSubclasses.
	 * @return Result depending on filter selected i.e., Season,Department,Technologist and Style Subclass.
	 */
	private SearchResults queryProductsIDbySeasonAndDept(String selectedSeasons,
			String selectedDepts, String selectedTechnologiest,
			String selectedSubclasses) {
		List<String> selectedMOASeason = (List<String>) MOAHelper.getMOACollection(selectedSeasons);		
		List<String> selectedMOADept = (List<String>) MOAHelper.getMOACollection(selectedDepts);		
		List<String> selectedMOATechnologiest = (List<String>) MOAHelper.getMOACollection(selectedTechnologiest);		
		List<String> selectedMOASubClass = (List<String>) MOAHelper.getMOACollection(selectedSubclasses);

		PreparedQueryStatement statement = new PreparedQueryStatement();

		try {
			statement.appendFromTable("LCSSeason");

			statement.appendFromTable(LCSProductSeasonLink.class);

			statement.appendFromTable("prodarev", LCSPRODUCT);

			statement.appendSelectColumn(new QueryColumn(LCSSEASON, BRANCHIDITERATIONINFO));

			statement.appendSelectColumn(new QueryColumn(LCSSEASON, IDA2A2));
			
			statement.appendSelectColumn(new QueryColumn(LCSSEASON, IDA3MASTERREFERENCE));	 	


			statement.appendSelectColumn(new QueryColumn(LCSPRODUCT, IDA2A2));

			statement.appendSelectColumn(new QueryColumn(LCSPRODUCT, BRANCHIDITERATIONINFO));			

			statement.appendSelectColumn(new QueryColumn(LCSPRODUCT, IDA3MASTERREFERENCE));	 	

			statement.appendSelectColumn(new QueryColumn(LCSProductSeasonLink.class, "thePersistInfo.theObjectIdentifier.id"));			
			/**
			 * for loop to get attribute internal names from attribute internal
			 * names list. And attribute type name from attrInternalNameMap map.
			 */
			FlexTypeGenerator flexg = new FlexTypeGenerator();

			//add select columns.
			statement = addQueryColumns(statement, flexg);

			statement.appendJoin(new QueryColumn(LCSSEASON,BRANCHIDITERATIONINFO),new QueryColumn(LCSPRODUCTSEASONLINK,"SEASONREVID"));			

			statement.appendJoin(new QueryColumn(LCSPRODUCT, BRANCHIDITERATIONINFO), new QueryColumn(LCSPRODUCTSEASONLINK, "productARevId"));

			statement.appendCriteria(new Criteria(new QueryColumn(LCSSEASON, LATESTITERATIONINFO), "1",Criteria.EQUALS));

			statement.appendAnd();			
			statement.appendCriteria(new Criteria(new QueryColumn(LCSPRODUCT, LATESTITERATIONINFO), "1",Criteria.EQUALS));			 

			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn(LCSPRODUCTSEASONLINK, "SEASONREMOVED"), "0",Criteria.EQUALS));

			statement.appendAnd();			
			statement.appendCriteria(new Criteria(new QueryColumn(LCSPRODUCTSEASONLINK, "EFFECTLATEST"), "1",Criteria.EQUALS));			

			statement.appendInCriteria(new QueryColumn(LCSSEASON,BRANCHIDITERATIONINFO), selectedMOASeason);

			if (selectedMOADept != null && selectedMOADept.size() > 0){
				statement.appendInCriteria(new QueryColumn(LCSSEASON,getAttributeColumnName(SEASON_TYPE,seasonDeptKey)), selectedMOADept);
			}				

			if (selectedMOATechnologiest != null && selectedMOATechnologiest.size() > 0){
				statement.appendInCriteria(new QueryColumn(LCSPRODUCTSEASONLINK,getAttributeColumnName(PRODUCTSEASONLINK_TYPE,pslTechnologistKey)),selectedMOATechnologiest);
			}

			if (selectedMOASubClass != null && selectedMOASubClass.size() > 0){
				statement.appendInCriteria(new QueryColumn(LCSPRODUCT,getAttributeColumnName(PRODUCT_TYPE,prodsubClassKey)), selectedMOASubClass);
			}			

			statement.setDistinct(true);			

			return LCSQuery.runDirectQuery(statement);	 

		} catch (WTException e) {
			e.printStackTrace();
		}

		return null;
	}
	/**
	 * Method addSelectColumn.
	 * @param statement the query statement.
	 * @param flexg the flexpetygenerator.
	 * @return statement.
	 * @throws WTException the WTException.
	 */
	private PreparedQueryStatement addQueryColumns(
			PreparedQueryStatement st, FlexTypeGenerator flexg)
			throws WTException {
		PreparedQueryStatement statement = st;
		for (String attrIntName : attributes) {

			String attrbFromType = attrInternalNameMap.get(attrIntName);
			String attrbInternalName =attrIntName.substring(0,attrIntName.indexOf('.'));
			

			
			 // if condition to check attribute type with constant variable.
			  //Column names is fetched based on attribute type
			 
			if (PRODUCT_TYPE.equalsIgnoreCase(attrbFromType) || attrbFromType.contains(PRODUCT_TYPE+"\\")) {	
				FlexType type=FlexTypeCache.getFlexTypeFromPath(attrbFromType);
				FlexTypeAttribute att=type.getAttribute(attrbInternalName);

				if("object_ref_list".equals(att.getAttVariableType()) ||  "object_ref".equals(att.getAttVariableType())){  					
					flexg.setScope(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE);
					flexg.setLevel(FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL); 					        
					List usedAttKeys=new ArrayList();
					usedAttKeys.add(att);					        
					statement = flexg.appendQueryColumns(usedAttKeys,type, statement);
				}else{
					statement.appendSelectColumn(new QueryColumn(tableNameMap
							.get(PRODUCT_TYPE), getAttributeColumnName(
									PRODUCT_TYPE, attrbInternalName)));	
				}				 

			} else if (PRODUCTSEASONLINK_TYPE
					.equalsIgnoreCase(attrbFromType)) {
				statement.appendSelectColumn(new QueryColumn(tableNameMap
						.get(PRODUCTSEASONLINK_TYPE),
						getAttributeColumnName(PRODUCTSEASONLINK_TYPE,
								attrbInternalName)));
			} else if (SEASON_TYPE.equalsIgnoreCase(attrbFromType)) {
				statement.appendSelectColumn(new QueryColumn(tableNameMap
						.get(SEASON_TYPE), getAttributeColumnName(
								SEASON_TYPE, attrbInternalName)));
			}
		}
		return statement;
	}

	/**
	 * Call this method to get cosntruction detail of product within season.
	 * @param productIdList the productIdList.
	 * @return SearchResults from DB based on productseason id's selected.
	 */
	private SearchResults queryConstructionDataByProductId(
			Collection<String> productIdList) {
		SearchResults results = null;

		if (productIdList == null || productIdList.size() == 0) {
			return results;
		}
		try {
	
			// PrepareStatement to get Construction Data to generate Excel
			// object
			PreparedQueryStatement queryStatement = new PreparedQueryStatement();
  
			queryStatement.appendFromTable("LCSProduct");

			queryStatement.appendFromTable("LCSPartMaster");

			queryStatement.appendFromTable("LCSConstructionInfoMaster",LCSCONSTRUCTIONINFOMASTER1);

			queryStatement.appendFromTable("LCSConstructionInfoMaster",LCSCONSTRUCTIONINFOMASTER2);

			queryStatement.appendFromTable("LCSConstructionDetail",LCSCONSTRUCTIONDETAIL);

			queryStatement.appendFromTable("LCSConstructionInfo",LCSCONSTRUCTIONINFO);

			queryStatement.appendFromTable("LCSConstructionInfo",LCSCONSTRUCTIONINFO2);

			queryStatement.appendFromTable("FlexSpecToComponentLink");

			queryStatement.appendFromTable("FlexSpecification");

			queryStatement.appendFromTable("FlexSpecMaster");

			queryStatement.appendFromTable("LCSSeasonMaster");

			queryStatement.appendFromTable("FlexSpecToSeasonLink");
			
			queryStatement.appendFromTable("SpecToLatestIterSeason");
			
			queryStatement.appendFromTable("LatestIterFlexSpecification");

			queryStatement.appendSelectColumn(new QueryColumn(LCSPRODUCT,BRANCHIDITERATIONINFO));

			queryStatement.appendSelectColumn(new QueryColumn(LCSPRODUCT, IDA2A2));
			
			queryStatement.appendSelectColumn(new QueryColumn("LCSSEASONMASTER", IDA2A2));
			

			FlexTypeGenerator flexg = new FlexTypeGenerator();

			queryStatement = addSelectColumns(queryStatement, flexg);

			queryStatement.appendJoin(new QueryColumn("LCSPARTMASTER", IDA2A2),new QueryColumn(LCSPRODUCT, IDA3MASTERREFERENCE));

			queryStatement.appendJoin(new QueryColumn(LCSCONSTRUCTIONINFOMASTER1,"IDA3A6"), new QueryColumn("LCSPARTMASTER", IDA2A2));

			queryStatement.appendJoin(new QueryColumn(LCSCONSTRUCTIONDETAIL,"IDA3A5"), new QueryColumn(LCSCONSTRUCTIONINFOMASTER1,IDA2A2));
 

			queryStatement.appendFromTable("LCSConstructionDetail",LCSCONSTRUCTIONDETAIL2);
			queryStatement.appendAndIfNeeded();

			queryStatement.appendCriteria(new Criteria(new QueryColumn(LCSCONSTRUCTIONDETAIL2, "EFFECTOUTDATE"), "",Criteria.IS_NULL));
			queryStatement.appendAnd();

			queryStatement.appendCriteria(new Criteria(new QueryColumn(LCSCONSTRUCTIONDETAIL2, "DROPPED"), "0",Criteria.EQUALS));
			queryStatement.appendAnd();

			queryStatement.appendCriteria(new Criteria(new QueryColumn(LCSCONSTRUCTIONDETAIL2, "IDA3A5"), "0",Criteria.EQUALS));
			queryStatement.appendAndIfNeeded();			


			queryStatement.appendSelectColumn(new QueryColumn(LCSCONSTRUCTIONINFOMASTER2, "CONSTRUCTIONINFONAME"));
			
			queryStatement.appendJoin(new QueryColumn(LCSCONSTRUCTIONDETAIL2,getAttributeColumnName(CONSTRUCTIONDETAIL_TYPE,cdNumberKey)), 
					new QueryColumn(LCSCONSTRUCTIONDETAIL,getAttributeColumnName(CONSTRUCTIONDETAIL_TYPE,cdNumberKey)));

			//statement.appendJoin(new QueryColumn("LCSCONSTRUCTIONDETAIL2",getAttributeColumnName(CONSTRUCTIONDETAIL_TYPE,cdDescriptionKey)),	new QueryColumn("LCSCONSTRUCTIONDETAIL",getAttributeColumnName(CONSTRUCTIONDETAIL_TYPE,cdDescriptionKey)));

			//statement.appendJoin(new QueryColumn("LCSCONSTRUCTIONDETAIL2",getAttributeColumnName(CONSTRUCTIONDETAIL_TYPE,cdDetailsKey)),	new QueryColumn("LCSCONSTRUCTIONDETAIL",getAttributeColumnName(CONSTRUCTIONDETAIL_TYPE,cdDetailsKey)));

			// ADDING NEW JOIN
			//queryStatement.appendJoin(new QueryColumn(LCSPRODUCT, "ida3b12"),new QueryColumn("LCSSEASONMASTER", "IDA2A2"));

			queryStatement.appendJoin(new QueryColumn("FLEXSPECTOSEASONLINK","ida3b5"), new QueryColumn("LCSSEASONMASTER", IDA2A2));

			queryStatement.appendJoin(new QueryColumn("FLEXSPECMASTER", IDA2A2),new QueryColumn("FLEXSPECTOSEASONLINK", "IDA3A5"));

			queryStatement.appendJoin(new QueryColumn("FLEXSPECMASTER", IDA2A2),new QueryColumn("FLEXSPECTOCOMPONENTLINK", "IDA3A4"));

			queryStatement.appendJoin(new QueryColumn(LCSCONSTRUCTIONINFO,IDA3MASTERREFERENCE), new QueryColumn(LCSCONSTRUCTIONINFOMASTER1, IDA2A2));

			queryStatement.appendJoin(new QueryColumn(LCSCONSTRUCTIONINFOMASTER1,"IDA3B6"), new QueryColumn(LCSCONSTRUCTIONINFOMASTER2,IDA2A2));

			queryStatement.appendJoin(new QueryColumn(LCSCONSTRUCTIONINFO2,IDA3MASTERREFERENCE), new QueryColumn(LCSCONSTRUCTIONINFOMASTER2, IDA2A2));

			queryStatement.appendJoin(new QueryColumn("FLEXSPECTOCOMPONENTLINK","IDA3B4"), new QueryColumn(LCSCONSTRUCTIONINFOMASTER1,IDA2A2));

			queryStatement.appendJoin(new QueryColumn("FLEXSPECIFICATION",IDA3MASTERREFERENCE), new QueryColumn("FLEXSPECMASTER",IDA2A2));
			queryStatement.appendAndIfNeeded();
			
			
			   queryStatement.appendJoin(new QueryColumn("SpecToLatestIterSeason", com.lcs.wc.specification.FlexSpecToSeasonLink.class, "roleAObjectRef.key.id(+)"), new QueryColumn("LatestIterFlexSpecification", "idA3masterReference"));
		        queryStatement.appendJoin(new QueryColumn("LatestIterFlexSpecification", com.lcs.wc.specification.FlexSpecification.class, "specOwnerReference.key.id"), new QueryColumn("LCSProduct", "idA3masterReference"));


			
			queryStatement.appendCriteria(new Criteria(new QueryColumn("FLEXSPECIFICATION", LATESTITERATIONINFO), "1",Criteria.EQUALS));
			queryStatement.appendAnd();

			queryStatement.appendCriteria(new Criteria(new QueryColumn(LCSCONSTRUCTIONDETAIL, "EFFECTOUTDATE"), "",Criteria.IS_NULL));
			queryStatement.appendAnd();

			queryStatement.appendCriteria(new Criteria(new QueryColumn(LCSCONSTRUCTIONDETAIL, "DROPPED"), "0",Criteria.EQUALS));
			queryStatement.appendAnd();

			queryStatement.appendCriteria(new Criteria(new QueryColumn(LCSCONSTRUCTIONINFO, LATESTITERATIONINFO), "1",Criteria.EQUALS));
			queryStatement.appendAnd();

			queryStatement.appendCriteria(new Criteria(new QueryColumn(LCSCONSTRUCTIONINFO2, LATESTITERATIONINFO), "1",Criteria.EQUALS));
			queryStatement.appendAnd();

			queryStatement.appendCriteria(new Criteria(new QueryColumn(LCSPRODUCT,LATESTITERATIONINFO), "1", Criteria.EQUALS));
			queryStatement.appendAndIfNeeded();

			queryStatement.appendCriteria(new Criteria(new QueryColumn(LCSCONSTRUCTIONINFOMASTER1,"CONSTRUCTIONTYPE"), "INSTANCE", Criteria.EQUALS));


			queryStatement.appendInCriteria(new QueryColumn(LCSPRODUCT, IDA2A2), productIdList);

			queryStatement.setDistinct(true);
			results = LCSQuery.runDirectQuery(queryStatement);

		} catch (WTException e) {
			// Exception
			e.printStackTrace();
		}
		return results;
	}
	/**
	 * 
	 * Method addSelectColumns - To add select column from different table.
	 * @param statement the statement.
	 * @param flexg the flexgenerator object.
	 * @return statement.
	 * @throws WTException the WTException.
	 */
	private PreparedQueryStatement addSelectColumns(
			PreparedQueryStatement st, FlexTypeGenerator flexg)
			throws WTException {
		PreparedQueryStatement statement = st;
		for (String attrIntName : attributes) {
			String attrbFromType = attrInternalNameMap.get(attrIntName);

			if (CONSTRUCTIONDETAIL_TYPE.equalsIgnoreCase(attrbFromType)
					|| SPECIFICATION.equalsIgnoreCase(attrbFromType)
					|| CONSTRUCTIONINFOTYPE
					.equalsIgnoreCase(attrbFromType)
					|| CONSTRUCTIONTEMPLATETYPE
					.equalsIgnoreCase(attrbFromType)) {
				flexg.setScope(null);
				String attrbInternalName = attrIntName.substring(0,attrIntName.indexOf('.'));

				if (CONSTRUCTIONDETAIL_TYPE.equalsIgnoreCase(attrbFromType) || CONSTRUCTIONTEMPLATETYPE
						.equalsIgnoreCase(attrbFromType)){
					flexg.setScope(ConstructionFlexTypeScopeDefinition.DETAIL_SCOPE);
					attrbFromType=CONSTRUCTION;
					FlexType type=FlexTypeCache.getFlexTypeFromPath(attrbFromType);
					FlexTypeAttribute att=type.getAttribute(attrbInternalName);

					List usedAttKeys=new ArrayList();
					usedAttKeys.add(att);
					statement = flexg.appendQueryColumns(usedAttKeys,type, statement,"LCSConstructionDetail");	
				}else if (CONSTRUCTIONINFOTYPE.equalsIgnoreCase(attrbFromType)){
					flexg.setScope(ConstructionFlexTypeScopeDefinition.PRODUCT_SCOPE);
					attrbFromType=CONSTRUCTION;
					FlexType type=FlexTypeCache.getFlexTypeFromPath(attrbFromType);
					FlexTypeAttribute att=type.getAttribute(attrbInternalName);

					List usedAttKeys=new ArrayList();
					usedAttKeys.add(att);
					statement = flexg.appendQueryColumns(usedAttKeys,type, statement,"LCSConstructionInfo");	
				}else{ 
					attrbFromType=SPECIFICATION;
					statement.appendSelectColumn(new QueryColumn(tableNameMap.get(attrbFromType),getAttributeColumnName(attrbFromType,attrbInternalName)));
				}

			}
		}
		return statement;
	}

	/**
	 * Call this method to filter productseason SearchResults queried by  queryProductSeasonById method.
	 * Filter based on product attribute sub-class, which has BO i.e., Style class and Product sub category.
	 * @param results the results.
	 * @param selectedStyleClass the selectedStyleClass.
	 * @param selectedProdSubCats the selectedProdSubCats.
	 */
	private void filterProductByAttr(SearchResults results,
			String selectedStyleClass, String selectedProdSubCats) {

		Boolean isClassMatched = true;
		Boolean isSubcatMatched = true;
		List<String> selectedMOAProdClass = (List<String>) MOAHelper
				.getMOACollection(selectedStyleClass);
		List<String> selectedMOAProdSubCat = (List<String>) MOAHelper
				.getMOACollection(selectedProdSubCats);
		try {
			if (results != null) {
				List queryResults = results.getResults();
				if (queryResults != null && queryResults.size() > 0) {
					for (Object obj : queryResults) {
						FlexObject fo = (FlexObject) obj;
						String subClassId = (String) fo.get("LCSPRODUCT."+ getAttributeColumnName(PRODUCT_TYPE,prodsubClassKey));

						if (subClassId != null 	&& FormatHelper.hasContent(subClassId)) {
							LCSLifecycleManaged subClass = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+ subClassId);

							

							isClassMatched = isValueMatched(isClassMatched,
									selectedMOAProdClass, subClass,boStyleClassKey);
							
							isSubcatMatched = isValueMatched(isSubcatMatched,
									selectedMOAProdSubCat, subClass,boStyleSubCategoryKey);
							
					

						}
						if (isClassMatched && isSubcatMatched) {
							seasonProdResultData.put((String) fo.get("LCSPRODUCT.branchiditerationinfo")+"_"+fo.get("LCSSEASON.IDA3MASTERREFERENCE"),fo);
						}
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Method isValueMatched - to check the BO values are matching.
	 * @param matched the boolean flag , matched.
	 * @param selectedValue the selected value.
	 * @param subClass the BO.
	 * @return true/false.
	 * @throws WTException the WTException.
	 */
	private Boolean isValueMatched(Boolean matched,
			List<String> selectedValue, LCSLifecycleManaged subClass,String attrKey)
			throws WTException {
		
		Boolean isValueMatched = matched;
		String value = (subClass.getValue(attrKey) != null) ? FormatHelper.getNumericFromOid(subClass.getValue(attrKey).toString()) : "";

		//if value is not null and the selected value is not empty, then iterate.
		if (value != null	&& selectedValue.size() > 0) {
			for (String item : selectedValue) {
				//if value is matching anytime, set the flag value to true.
				if (value.equals(item)) {
					// operation upon successful match
					isValueMatched = true;
					break;
				}
				isValueMatched = false;
			}
		}
		return isValueMatched;
	}

	/**
	 * This method populate the data in workbook object.
	 * @param results the results.
	 * @return Collection<FlexObject>, which contain Construction details with TableColumn and value.
	 */
	private Collection<FlexObject> createExcelData(SearchResults results) {
		Collection<FlexObject> results1 = new ArrayList<FlexObject>();
		List<Object> queryResults = results.getResults();
		
		if (queryResults != null && queryResults.size() > 0) {
			try { 

				for (Object obj : queryResults) {
					FlexObject foTemp = new FlexObject();
					FlexObject fo = (FlexObject) obj;
					String productid = (String) fo.get("LCSPRODUCT.BRANCHIDITERATIONINFO");
					String prodARevid = prodLatestIdAndARevMap.get(productid)+"_"+fo.get("LCSSEASONMASTER.IDA2A2");
 					if (FormatHelper.hasContent(prodARevid)) {
						FlexObject flexObj = (FlexObject) seasonProdResultData.get(prodARevid);
						if(flexObj==null){
							continue;
						}
						foTemp.putAll(flexObj);
						foTemp.putAll(fo);

						//iterate through values and find the appropriate value and set it for table columns.
						setTableData(foTemp, fo, flexObj);
						results1.add(foTemp);
					}
				}

			} catch (WTException e) {
				e.printStackTrace();
				LOGGER.debug(e.getMessage());
				LOGGER.debug("#### SM--> Construction report failed!!!!!");
				return java.util.Collections.emptyList();
			}
		}
		return results1;
	}
	/**
	 * Method setTableData.
	 * @param foTemp the foTemp.
	 * @param fo the foTemp.
	 * @param flexObj the flexObj.
	 * @throws WTException the WTException.
	 */
	private void setTableData(FlexObject foTemp, FlexObject fo,
			FlexObject flexObj) throws WTException {
		for (String attrIntName : attributes) {

			String value = "";
			FlexObject resultFlexObj;
			String columnIndex=attrIntName;
			String attrbOfType = attrInternalNameMap.get(attrIntName); 							
			attrIntName =attrIntName.substring(0,attrIntName.indexOf('.'));

			String tableName = tableNameMap.get(attrbOfType);
			String internalName = getActualInternalName(attrIntName);
			/**
			 * Check if attribute is of product or season
			 * flextype. SearchResults are selected based on
			 * attribute flextype. Since season and product
			 * information are queried separately.
			 */
			if (PRODUCTSEASONLINK_TYPE.equals(attrbOfType)|| SEASON_TYPE.equals(attrbOfType)|| PRODUCT_TYPE.equals(attrbOfType)) {
				resultFlexObj = flexObj;
			} else {
				resultFlexObj = fo;
			}
			String columnName = getAttributeColumnName(attrbOfType, internalName);
			
			//get appropriate table value.
			getTableValue(foTemp, fo, flexObj, columnIndex,
					attrbOfType, internalName, columnName); 

			if (internalName.equals(pslTechnologistKey)){
				value = (String) resultFlexObj.get(tableName+ "." + columnName);
				foTemp.put(((TableColumn) columnMap.get(columnIndex)).getTableIndex(), value);
			} 							 
		}
	}
	/**
	 * Method getTableValue.
	 * @param foTemp the foTemp.
	 * @param fo the fo.
	 * @param flexObj the flexObj.
	 * @param columnIndex the columnIndex.
	 * @param attrbOfType the attribute type.
	 * @param internalName the atteibute internal name.
	 * @param columnName the columnName.
	 * @throws WTException the exception , WtException.
	 */
	private void getTableValue(FlexObject foTemp, FlexObject fo,
			FlexObject flexObj, String columnIndex, String attrbOfType,
			String internalName, String columnName) throws WTException {
		String value;
		/**
		 * Checking attribute type to get appropriate value.
		 */
		if (CONSTRUCTIONINFOTYPE.equals(attrbOfType) && "name".equals(internalName)) {
			value = (String) fo.get("LCSCONSTRUCTIONINFO."+ columnName);
			foTemp.put(((TableColumn) columnMap.get(columnIndex)).getTableIndex(), value);
		}else if(CONSTRUCTIONTEMPLATETYPE.equals(attrbOfType) && !"name".equals(internalName)) {
			value = (String) fo.get("LCSCONSTRUCTIONINFO2."+ columnName);
			foTemp.put(((TableColumn) columnMap.get(columnIndex)).getTableIndex(), value);
		} else if (subclassdivisionHierarchy.equals(attrbOfType)) {
			String subClassValue = (String) flexObj.get("LCSPRODUCT."+ getAttributeColumnName(PRODUCT_TYPE,prodsubClassKey));
			if(FormatHelper.hasContent(subClassValue)){
				LCSLifecycleManaged subDivisionLifecycleManaged = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+ subClassValue);
				if (subDivisionLifecycleManaged != null) {
					value = (String) subDivisionLifecycleManaged.getValue(internalName); 
					foTemp.put(((TableColumn) columnMap.get(columnIndex)).getTableIndex(), value);
				}									
			}
		}
	}


	/**
	 * call this method to get attributes database column name by it's flextype.
	 * @param flexType, 
	 * @param attrbInternalName
	 * @return String Attribute Column name
	 * @throws WTException
	 */
	private String getAttributeColumnName(final String flexType,
			final String attrbInternalName) throws WTException {
		FlexType type = null;
		
		String internalName = getActualInternalName(attrbInternalName);
		type = getFlexTypeForAttrb(flexType, internalName); 
		return type.getAttribute(internalName).getColumnName();
	}

	/**
	 * utility method to get attribute, trims if contains '$' char in attribute internalname.
	 * Character '$' is added to attribute name to make unqiue name while initializing, in initExcelColumn() call.
	 * @param attrbInternalName
	 * @return
	 */
	private String getActualInternalName(final String attrbInternalName) {
		return (attrbInternalName.indexOf('$') > 0) ? attrbInternalName.substring(0, attrbInternalName.indexOf('$')): attrbInternalName;
	}

	/**
	 * call method to get FlexType of attribute from flextype maps initialized while initExcelColumn() call.
	 * @param flexType the flexType.
	 * @param attrbInternalName the attribute internal name.
	 * @return FlexType, FlexType in which Attribute belongs.
	 * @throws WTException.
	 */
	private FlexType getFlexTypeForAttrb(String typeString,	String attrKey) throws WTException {
		String attrbInternalName = attrKey;
		String flexType = typeString;
		if(attrbInternalName.indexOf('.')>-1){
			attrbInternalName=attrbInternalName.substring(0,attrbInternalName.indexOf('.'));
		} 
		FlexType type = null;
		if (SEASON_TYPE.equals(flexType)) {
			type = getTypeByTypeList(seasonFlexTypes, attrbInternalName);
		} else if (PRODUCT_TYPE.equals(flexType)) {
			type = getTypeByTypeList(prodFlexTypes, attrbInternalName);
		} else if (PRODUCTSEASONLINK_TYPE.equals(flexType)) {
			type = getTypeByTypeList(prodSeasonFlexTypes, attrbInternalName);
		} else if (CONSTRUCTIONDETAIL_TYPE.equals(flexType)) {
			if (CONSTRUCTIONDETAIL_TYPE.equalsIgnoreCase(flexType)){
				flexType=CONSTRUCTION;
			}
 			type = FlexTypeCache.getFlexTypeFromPath(flexType);
		} else {
			 type = getFlexType(flexType, attrbInternalName);
		}
 
		return type;
	}
	/**
	 * Method getFlexTypeForAttribute.
	 * @param flexType the flextype.
	 * @param attrbInternalName the attrbInternalname
	 * @return Flextype .
	 * @throws WTException the WTException.
	 */
	private FlexType getFlexType(String flexTypeStr,
			String attrbInternalName) throws WTException {
		FlexType type; 
		String flexType = flexTypeStr;
		//this is to get the flex type for remaining attribute object type.
		if (CONSTRUCTIONINFOTYPE.equals(flexType)|| CONSTRUCTIONTEMPLATETYPE.equals(flexType)) {
				type = getTypeByTypeList(constructionInfoFlexTypes,	attrbInternalName);
				if(type==null){
					type = FlexTypeCache.getFlexTypeFromPath(CONSTRUCTION);
				}
					
			} else if (SPECIFICATION.equals(flexType)) {
				type = FlexTypeCache.getFlexTypeFromPath(SPECIFICATION);
			} else if(flexTypeMap.containsKey(flexType)) {
				type = flexTypeMap.get(flexType);
			}else{
				if (CONSTRUCTIONDETAIL_TYPE.equalsIgnoreCase(flexType)){
					flexType=CONSTRUCTION;
				}
				type = FlexTypeCache.getFlexTypeFromPath(flexType);
			}
		return type;
	}

	/**
	 * Method getTypeByTypeList.
	 * @param flexTypesMap the flexTypesMap.
	 * @param attrbInternalName the attrbInternalName.
	 * @return FlexType, From Map by Attribute Name.
	 * @throws WTException the WTException.
	 */
	protected FlexType getTypeByTypeList(Collection<FlexType> flexTypes,
			String attrbInternalName) throws WTException {
		for (FlexType type : flexTypes) {
			if (type != null&& type.attributeExist(getActualInternalName(attrbInternalName))) {
				return type;
			}
		}
		return null;
	}


	/**
	 * Method to get the report columns.
	 * @return report columns.
	 */
	public Collection getReportColumns() {
		return this.reportColumns;
	}

	/**
	 * Method setReportColumns.
	 */
	public void setReportColumns() {
		// Set Report Columns
		this.columnMap = new HashMap<String, TableColumn>();
		FlexTypeGenerator ftg = new FlexTypeGenerator();
		// Season//
		ftg.setScope(null);
		ftg.setLevel(null);
		TableColumn column;

		try {
			for (String attrInternalName : attributes) {

				String internalName = getActualInternalName(attrInternalName);
				String attrOfType = attrInternalNameMap.get(attrInternalName);
				FlexType flexType = getFlexTypeForAttrb(attrOfType,	attrInternalName);
				
				//attribute which does not need hyper links
				Collection removeLinkColl = MOAHelper.getMOACollection(removeHyperLinkattributes);
				
				String columnIndx=attrInternalName;
				if (flexType == null) {
					LOGGER.debug("Flex type is null" + attrOfType + " <----> "+ attrInternalName);
					continue;
					
				}
				
				FlexType constructionType = FlexTypeCache.getFlexTypeFromPath(CONSTRUCTION);
				FlexType specType = FlexTypeCache.getFlexTypeFromPath(SPECIFICATION);

				if(attrOfType.contains(CONSTRUCTION)){
					flexType = constructionType;
					ftg.setScope(ConstructionFlexTypeScopeDefinition.DETAIL_SCOPE);
				}

				if(attrOfType.contains(SPECIFICATION)){
					flexType = specType;
					ftg.setScope(com.ptc.windchill.enterprise.requirement.RequirementConstants.MANAGED_SPECIFICATION_TYPE_IDENTIFIER_STRING);
				}
				ftg.createTableColumns(flexType, columnMap, false);

				internalName =internalName.substring(0,internalName.indexOf('.'));			
				FlexTypeAttribute att=flexType.getAttribute(internalName);
				if(removeLinkColl.contains(internalName)){
					column= ftg.createTableColumn(att, flexType);
					column.setLinkMethod(null);
					column.setAllClickFunction(null);
				}else{
					column = ftg.createTableColumn(att, flexType);
				}

				column.setHeaderLabel(attrColumnDisplayMap.get(attrInternalName));

				
				//setting table index.
				setTableIndex(column, internalName, attrOfType, flexType, att);

				columnMap.put(columnIndx, column);
			}			

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Method setTableIndex.
	 * @param column the table column.
	 * @param internalName the attribute internalname.
	 * @param attrOfType the attribute type.
	 * @param flexType the flextype.
	 * @param att the flex attribute.
	 * @throws WTException the WTException.
	 */
	private void setTableIndex(TableColumn column, String internalName,
			String attrOfType, FlexType flexType, FlexTypeAttribute att)
			throws WTException {
		if("object_ref_list".equals(att.getAttVariableType()) ||  "object_ref".equals(att.getAttVariableType())){  		
			
			String tableName = att.getSearchResultsTableName(com.lcs.wc.flextype.FlexTypeScopeDefinitionFactory.getDefinition(flexType.getTypeScopeDefinition()),
					FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE, FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL, false, flexType);
			if(attrOfType.contains(CONSTRUCTION)){
				tableName = att.getSearchResultsTableName(com.lcs.wc.flextype.FlexTypeScopeDefinitionFactory.getDefinition(flexType.getTypeScopeDefinition()),
						ConstructionFlexTypeScopeDefinition.DETAIL_SCOPE,null, false, flexType);
			}

			com.lcs.wc.flextype.ForiegnKeyDefinition fkDef=att.getRefDefinition();
			FlexType fkType = FlexTypeCache.getFlexTypeRootByClass(fkDef.getFlexTypeClass());
			FlexTypeAttribute fkAtt = fkType.getAttribute(fkDef.getFlexTypedDisplay());
			// COLUMN FOR DISPLAY 			            
			column.setTableIndex(tableName+"."+ fkAtt.getColumnName(null));
		}else{
			if("Construction Template".equals(attrOfType) && "name".equals(internalName)){
				//
				column.setTableIndex("LCSCONSTRUCTIONINFOMASTER2.CONSTRUCTIONINFONAME");

			}else{
				column.setTableIndex(flexType.getAttribute(internalName).getSearchResultIndex());
			}
		}
	}

	/**
	 * Call this method to generate a report
	 * 
	 * @return Collection<FlexObject>, Construction Detail with TableColumn and
	 *         value.
	 * @throws WTException the WTException.
	 */
	public Collection<FlexObject> generateReport() throws WTException {
		// Getting Product selected Season and by selected Department
		String selectedSeason = selectedParams.get("season");
		String selectedDept = selectedParams.get("deparment");
		String selectedSubClass = selectedParams.get("prodSubClass");
		String selectedSubCategory = selectedParams.get("prodSubCat");
		String selectedClass = selectedParams.get("prodClass");
		String selectedTechnologist = selectedParams.get("technologist");

		//setting report column.
		setReportColumns();

		//updating features.
		updateTableColumn();

		List<String> pIds = new ArrayList<String>();

		SearchResults queryResult = queryProductsIDbySeasonAndDept(selectedSeason,selectedDept, selectedTechnologist, selectedSubClass);
		
 		if (queryResult.getResults() != null && queryResult.getResults().size() > 0) {
			for (Object obj : queryResult.getResults()) {
				FlexObject fo = (FlexObject) obj;
				pIds.add((String) fo.get("LCSPRODUCT.IDA2A2"));			 
				prodLatestIdAndARevMap.put(String.valueOf(fo.get("LCSPRODUCT.BRANCHIDITERATIONINFO")),
						String.valueOf(fo.get("LCSPRODUCT.BRANCHIDITERATIONINFO")));				
			}
		}		 

		filterProductByAttr(queryResult, selectedClass, selectedSubCategory);	
		if (!seasonProdResultData.isEmpty()) {
			SearchResults results = null;
			results = queryConstructionDataByProductId(pIds);
	 		LOGGER.debug("res results .."+results);

			if (results != null) {
				return createExcelData(results);
			}
		}
		return java.util.Collections.emptyList();
	}
	
	
	/**
	 * updateTableColumn - to update table column features.
	 */
	private void updateTableColumn() {
		TableColumn column;
		this.reportColumns = new ArrayList<TableColumn>();
		for (String reportColumn : attributes) {
			column = this.columnMap.get(reportColumn);
			if (column != null) {
				TableColumn emptySpecColumn = new TableColumn();
				column.copyState(emptySpecColumn);
				emptySpecColumn.setColumnClass("HIGHLIGHT_RED");
				column.setShowCriteriaOverride(true);
				column.addOverrideOption("EMPTY_SPEC", "1", emptySpecColumn);
				column.setWrapping(true);
				this.reportColumns.add(column);
			}
		}
	}
}

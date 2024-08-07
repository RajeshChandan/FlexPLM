package com.sportmaster.wc.reports;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.client.web.FlexTypeGenerator;
import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.flexbom.BOMPartNameTableColumn;
import com.lcs.wc.flexbom.FlexBOMFlexTypeScopeDefinition;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTypeDescriptorTableColumn;
import com.lcs.wc.flextype.FootwearApparelFlexTypeScopeDefinition;
import com.lcs.wc.material.MaterialSupplierFlexTypeScopeDefinition;
import com.lcs.wc.sourcing.SourcingConfigFlexTypeScopeDefinition;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
/**
 * 
 * @author 'true' BSC -PTC.
 * @version 'true' 1.0 version number.
 */
public final class SMMaterialForecastReportModel {
	
	private static final org.apache.log4j.Logger LOGGER=Logger.getLogger("MFDRLOG");

	private static final String BOM_MATERIALS_PRODUCT = "BOM\\Materials\\Product";
	private static final String MATERIAL_FABRIC = "Material\\Fabric";
	private static final String MATERIAL_PROD_PACKAGING = "Material\\Product Packaging";
	private static final String MATERIAL_DECORATION = "Material\\Decoration";
	private static final String PRODUCT_NAME = "productName";
	private static final String SKU_NAME = "skuName";
	private static final String SEASON_NAME = "seasonName";
	private static final String BOM_PART_NAME = "ptcbomPartName";
	private static final String ALIGN_LEFT = "left";

	/**
	 * SMMaterialForecastReportQuery method.
	 */
	public SMMaterialForecastReportModel(){

	}

	/**
	 * Method getReportColumnsData - this method reports column.
	 * @param inputSelectedMap the inputSelectedMap.
	 * @return true/false.
	 * @throws WTException the WTException.
	 * @throws SQLException the SQLException.
	 * @throws ParseException the ParseException.
	 * @throws WTPropertyVetoException the WTPropertyVetoException.
	 */
	public SMMaterialForecastReportBean getReportColumnsData(ClientContext context, Map<String, Object> inputSelectedMap,SMMaterialForecastReportBean reportBean) throws WTException, SQLException, ParseException, WTPropertyVetoException{
		//Define Apparel flextype to get maximum columns for display
		FlexType productFlexType = FlexTypeCache.getFlexTypeFromPath("Product\\APD"); 

		Map columns=new HashMap();
		Collection attList = new ArrayList();
		Collection finalColumns=new ArrayList();

		//columnkeys.
		Collection<String> columnsKeys =new ArrayList();

		//column map.
		Map columnMap=new HashMap();

		TableColumn column;

		//column property.
		String columnsProperty=LCSProperties.get("com.sportmaster.reports.materialforecast.columns");


		//column property.
		//Collection<String> columnsRUProperty=MOAHelper.getMOACollection(LCSProperties.get("com.sportmaster.reports.materialforecast.destination.RU"));

		//column property.
		//Collection<String> columnsCNProperty=MOAHelper.getMOACollection(LCSProperties.get("com.sportmaster.reports.materialforecast.destination.CN"));

		//column property.
		//Collection<String> columnsUAProperty=MOAHelper.getMOACollection(LCSProperties.get("com.sportmaster.reports.materialforecast.destination.UA"));


		//get the columnlist from the property.
		Collection<String> columnList=MOAHelper.getMOACollection(columnsProperty);

		//formulabased column keys.
		Collection<String> formulaeBasedColKeys=new ArrayList();

		//attribute key list.
		Collection<String> attKeysList=new ArrayList();

		//columnList iteration to get the attribute key.
		for(String columnKey: columnList){ 		
			StringTokenizer st1 = new StringTokenizer(columnKey,"~");
			if(st1.countTokens()==3){
				while(st1.hasMoreTokens()){
					String displayName=st1.nextToken();	 
					String typeName=st1.nextToken();

					String attKey=st1.nextToken();
					if(FormatHelper.hasContent(typeName) && FormatHelper.hasContent(attKey) ){
						getFormulaBasedColumns(columnsKeys,
								formulaeBasedColKeys, displayName, typeName,
								attKey);
						attKeysList.add(attKey);
					} 
				}
			}else{
				//Add the columns with calculations in a seperate collection
				if(st1.hasMoreTokens()){
					String displayName=st1.nextToken();	 
					columnsKeys.add("CALC~"+displayName+"~");
				}
			}
		}
		//getcolumnmap for report object.
		getColumnMapForReportObjects(productFlexType, columnMap);

		String key;	
		String columnDisplay;	

		//iterate through column keys and 
		//create the table columns.
		String cwColumnKey;
		String cIndex;
		for(String colKey:columnsKeys){
			//if columns is of calculation.
			if(colKey.indexOf("CALC~")>-1){
				column = new TableColumn();
				column.setDisplayed(true);
				column.setHeaderLabel(colKey.substring(colKey.indexOf('~')+1,colKey.lastIndexOf('~')));	    
				cIndex=colKey.substring(colKey.lastIndexOf('~')+1);
				column.setTableIndex(cIndex);
				column.setExcelWrapping(true);
				column.setAttributeType("float");
				column.setExcelHeaderWrapping(true);
				if(cIndex.indexOf('/')>-1){
					column.setFormat(FormatHelper.PERCENTAGE_FORMAT);
					column.setDecimalPrecision(2);
				}else{
					column.setFormat(FormatHelper.FLOAT_FORMAT);
					column.setDecimalPrecision(0);
				}

				column.setAlign(ALIGN_LEFT);
				column.setExcelColumnWidthAutoFitContent(true);
				if(column.getHeaderLabel().contains("RU")){
					column.setShowCriteriaTarget("DESTINATION.RU");
					column.setShowCriteria("Yes");
					reportBean.getAttRUList().add(cIndex);
				}

				if(column.getHeaderLabel().contains("CN")){
					column.setShowCriteriaTarget("DESTINATION.CN");
					column.setShowCriteria("Yes");
					reportBean.getAttCNList().add(cIndex);

				}	
				
				if(column.getHeaderLabel().contains("UA")){
					column.setShowCriteriaTarget("DESTINATION.UA");
					column.setShowCriteria("Yes");
					reportBean.getAttUAList().add(cIndex);

				}				
				 
				columns.put(colKey.substring(colKey.lastIndexOf('~')+1),column);
				finalColumns.add(column);
			}else{
				//Split the column keys from property entry
 
				columnDisplay=colKey.substring(colKey.lastIndexOf('~')+1);
				column = (TableColumn)columnMap.get(colKey.substring(0,colKey.lastIndexOf('~'))); 
				cwColumnKey=colKey.substring(colKey.indexOf('.')+1,colKey.lastIndexOf('~'));
				if(column!=null){
					column.setExcelHeaderWrapping(true);
					column.setExcelColumnWidthAutoFitContent(true);
					column.setExcelWrapping(true);
					column.setDisplayed(true);
					column.setHeaderLabel(columnDisplay);
					column.setAlign(ALIGN_LEFT);
					if(colKey.indexOf("Material\\Fabric") >-1|| colKey.indexOf("MaterialSupplier\\Fabric")>-1){
						column.setShowCriteriaTarget("IS_FABRIC_TYPE");
						column.setShowCriteria("Yes");
 					}

					key=column.getTableIndex();					
					 

					if(column.getHeaderLabel().contains("UA")){
						column.setShowCriteriaTarget("DESTINATION.UA");
						column.setShowCriteria("Yes");
						reportBean.getAttUAList().add(key);

					}
					
					if(column.getHeaderLabel().contains("RU")){
						column.setShowCriteriaTarget("DESTINATION.RU");
						column.setShowCriteria("Yes");
						reportBean.getAttRUList().add(key);

					}

					if(column.getHeaderLabel().contains("CN")){
						column.setShowCriteriaTarget("DESTINATION.CN");
						column.setShowCriteria("Yes");
						reportBean.getAttCNList().add(key);

					}	

					if(column.getFlexTypeAttribute()!=null && 
							"smRetailDestinationSync".equals(column.getFlexTypeAttribute().getAttKey())){
						column.setTableIndex("LCSSKUSEASONLINK.RETAILDESTINATION");
					}

					if(column.getFlexTypeAttribute()!=null && 
							"smApplicationTechnique".equals(column.getFlexTypeAttribute().getAttKey())){
						column.setTableIndex("MATERIAL.APPLICATIONTECHNICQUE");
					}

					attList.add(key);
					finalColumns.add(column);
					columns.put(key,column);	
				}
			}
		} 
		//adding to finalcolumns.
		finalColumns.add(columnMap.get("EXCHANGERATE_RMB.smFxExchangeRate"));
		finalColumns.add(columnMap.get("EXCHANGERATE_RUB.smFxExchangeRate"));		 

		//adding attribute list to bean.
		reportBean.setAttList(attList);
		//attribute key list.
		reportBean.setAttKeyList(attKeysList);
		LOGGER.debug("Report attKeysList..."+finalColumns);

		//formulabased keys.
		reportBean.setFormulaeBasedColKeys(formulaeBasedColKeys);
		//final columns.
		reportBean.setFinalColumns(finalColumns);

		LOGGER.debug("Report columns..."+finalColumns);
		//columns.
		reportBean.setColumns(columns);
		//column map.
		reportBean.setColumnsMap(columnMap);

		//Return the bean with all the required data for printing report
		return reportBean;
	}

	/**
	 * Method getFormulaBasedColumns - the getFormulaBasedColumns.
	 * @param columnsKeys the columnsKeys.
	 * @param formulaeBasedColKeys the formulaeBasedColKeys.
	 * @param displayName the displayName.
	 * @param typeName the typeName.
	 * @param attKey the attKey.
	 */
	private void getFormulaBasedColumns(Collection<String> columnsKeys,
			Collection<String> formulaeBasedColKeys, String displayName,
			String typeName, String attKey) {
		if("calculation".equalsIgnoreCase(typeName)){
			columnsKeys.add("CALC~"+displayName+"~"+attKey);
			formulaeBasedColKeys.add(attKey);
		}else{
			columnsKeys.add(typeName+"."+attKey+"~"+displayName);
		}
	}

	/**
	 * Method getColumnMapForReportObjects - getColumnMapForReportObjects.
	 * @param productFlexType the productFlexType.
	 * @param columnMap the columnMap.
	 * @throws WTException the WTException.
	 */
	private void getColumnMapForReportObjects(FlexType productFlexType,
			Map columnMap) throws WTException {
		FlexTypeGenerator flexg = new FlexTypeGenerator();

		flexg.setScope(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE);
		flexg.setLevel(FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL);
		flexg.createTableColumns(productFlexType, columnMap, productFlexType.getAllAttributes(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE, 
				FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL, false), false, "Product.", null, false, "LCSPRODUCT");

		flexg.setScope(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE);
		flexg.setLevel(FootwearApparelFlexTypeScopeDefinition.SKU_LEVEL);
		flexg.createTableColumns(productFlexType, columnMap, productFlexType.getAllAttributes(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE, 
				FootwearApparelFlexTypeScopeDefinition.SKU_LEVEL, false), false, "Colorway.", null, false, "LCSSKU");


		flexg.setScope(FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE);
		flexg.setLevel(null);
		flexg.createTableColumns(productFlexType, columnMap, productFlexType.getAllAttributes(FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE, 
				FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL, false), false, "ProductSeason.", null, true, "LCSPRODUCTSEASONLINK");

		flexg.createTableColumns(productFlexType, columnMap, productFlexType.getAllAttributes(FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE, 
				FootwearApparelFlexTypeScopeDefinition.PRODUCT_SKU_LEVEL, false), false, "ColorwaySeason.", null, true, "LCSSKUSEASONLINK");

		FlexType scType = FlexTypeCache.getFlexTypeFromPath("Sourcing Configuration");
		flexg.setScope(SourcingConfigFlexTypeScopeDefinition.SOURCING_CONFIG_SCOPE);
		flexg.setLevel(SourcingConfigFlexTypeScopeDefinition.PRODUCT_LEVEL); 
		flexg.createTableColumns(productFlexType, columnMap, scType.getAllAttributes(SourcingConfigFlexTypeScopeDefinition.SOURCING_CONFIG_SCOPE, 
				SourcingConfigFlexTypeScopeDefinition.PRODUCT_LEVEL, false), false, "Sourcing Configuration.", null, true, "LCSSOURCINGCONFIG");
		
		
		//flexg = new FlexTypeGenerator();
		//Set Source to Season for Order destination - Carryover CR
		flexg.setScope(SourcingConfigFlexTypeScopeDefinition.SOURCE_TO_SEASON_SCOPE);
		flexg.setLevel(SourcingConfigFlexTypeScopeDefinition.PRODUCT_LEVEL);
		flexg.createTableColumns(scType, columnMap, scType.getAllAttributes(SourcingConfigFlexTypeScopeDefinition.SOURCE_TO_SEASON_SCOPE, 
				SourcingConfigFlexTypeScopeDefinition.PRODUCT_LEVEL, false), false, "Sourcing Configuration.", null, true, "LCSSourceToSeasonLink");
		
		
		
		FlexType materialType = FlexTypeCache.getFlexTypeRoot("Material");
		FlexType colorType = FlexTypeCache.getFlexTypeRoot("Color");
		FlexType supplierType = FlexTypeCache.getFlexTypeRoot("Supplier");
		FlexType flexType = FlexTypeCache.getFlexTypeRoot("Material Color");			 
		FlexType bomType = FlexTypeCache.getFlexTypeFromPath(BOM_MATERIALS_PRODUCT);

		flexg.setScope(null);
		flexg.setLevel(null);
		flexg.createTableColumns(flexType, columnMap, flexType.getAllAttributes(null, null, false), false, false, "Material Color.", null, true, "LCSMATERIALCOLOR");

		//Material Type.
		flexg = new FlexTypeGenerator();
		flexg.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE);
		flexg.createTableColumns(materialType, columnMap, materialType.getAllAttributes(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE, null, false), false, false, "Material.", null, true, "LCSMATERIAL");


		flexg = new FlexTypeGenerator();
		flexg.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE);
		flexg.createTableColumns(materialType, columnMap, materialType.getAllAttributes(MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE, null, false), false, false, "Material Supplier.", null, true, "LCSMATERIALSUPPLIER");

		//if(reportBean.isHasFabricType()){
		FlexType fabricType = FlexTypeCache.getFlexTypeFromPath(MATERIAL_FABRIC);		
		flexg.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE);		
		flexg.createTableColumns(fabricType, columnMap, fabricType.getAllAttributes(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE, null, false), false, false, "Material\\Fabric.", null, true, "FABRIC");


		//if(reportBean.isHasFabricType()){
		FlexType prdPackagingType = FlexTypeCache.getFlexTypeFromPath(MATERIAL_PROD_PACKAGING);		
		flexg.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE);		
		flexg.createTableColumns(prdPackagingType, columnMap, prdPackagingType.getAllAttributes(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE, null, false), false, false, "Material\\Decoration.", null, true, "MATERIAL_PROD_PACKAGING");

		//if(reportBean.isHasFabricType()){
		FlexType decorationType = FlexTypeCache.getFlexTypeFromPath(MATERIAL_DECORATION);		
		flexg.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE);		
		flexg.createTableColumns(decorationType, columnMap, decorationType.getAllAttributes(MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE, null, false), false, false, "Material\\Decoration.", null, true, "MATERIAL_DECORATION");

		flexg.setScope(MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE);
		flexg.createTableColumns(fabricType, columnMap, fabricType.getAllAttributes(MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE, null, false),
				false, false, "MaterialSupplier\\Fabric.", null, true, "FABRIC_SUPPLIER");
		//}
		//Color Type.
		flexg = new FlexTypeGenerator();
		flexg.createTableColumns(colorType, columnMap, colorType.getAllAttributes(null, null, false), false, false, "Color.", null, true, null);

		//Supplier Type.
		flexg = new FlexTypeGenerator();
		flexg.createTableColumns(supplierType, columnMap, supplierType.getAllAttributes(null, null, false), false, false, "Supplier.", null, true, null);

		flexg.setScope(com.lcs.wc.flexbom.FlexBOMFlexTypeScopeDefinition.BOM_SCOPE);
		flexg.createTableColumns(bomType, columnMap, bomType.getAllAttributes(FlexBOMFlexTypeScopeDefinition.LINK_SCOPE, null), false, "BOM.", null, true, "FlexBOMPart");


		flexg.setScope(com.lcs.wc.flexbom.FlexBOMFlexTypeScopeDefinition.LINK_SCOPE);
		flexg.createTableColumns(bomType, columnMap, bomType.getAllAttributes(FlexBOMFlexTypeScopeDefinition.LINK_SCOPE, null), false, "BOM_LINK.", null, true, "FLEXBOMLINK");
		
		
	
		
		//Defining column for PartName(Placement)
		BOMPartNameTableColumn columnt = new BOMPartNameTableColumn();
		columnt.setHeaderLabel("Placement");
		columnt.setTableIndex(bomType.getAttribute("partName").getSearchResultIndex());
		columnt.setDisplayed(true);
		columnt.setWrapping(false);	 
		//Adding column to main collection.
		columnMap.put("BOM_LINK.partName", columnt);

		getHardColumnsForReport(columnMap, flexg);
	}

	/**
	 * Method getHardColumnsForReport - getHardColumnsForReport.
	 * @param columnMap the columnMap.
	 * @param flexg the flexg.
	 * @throws WTException the WTException.
	 */
	private void getHardColumnsForReport(Map columnMap, FlexTypeGenerator flexg)
			throws WTException {
		TableColumn column;

		//column for Material Supplier.
		column = new TableColumn();
		column.setDisplayed(true);
		column.setHeaderLabel("Material Supplier");
		column.setHeaderAlign(ALIGN_LEFT);
		column.setTableIndex("LCSSUPPLIERMASTER.SUPPLIERNAME");
		columnMap.put("Material Supplier.materialSupplierName", column);


		//column for Material Supplier.
		column = new TableColumn();
		column.setDisplayed(true);
		column.setTableIndex("LCSCOLOR.COLORNAME");

		column.setHeaderLabel("Color");
		column.setHeaderAlign(ALIGN_LEFT);
		columnMap.put("Color.name", column);

		//column for materialName.
		column = new TableColumn();
		column.setDisplayed(true);
		column.setHeaderLabel("Material");
		column.setTableIndex("MATERIALMASTER.NAME");

		column.setHeaderAlign(ALIGN_LEFT);
		columnMap.put("Material.materialName", column);

		//column for materialType
		//Defining the FlexTypeDescriptorTableColumn to display the Flextype name.
		column = new FlexTypeDescriptorTableColumn();
		column.setDisplayed(true);
		column.setHeaderLabel("Type");
		column.setHeaderAlign("left");
		column.setTableIndex("WTTYPEDEFINITION.BRANCHIDITERATIONINFO");
		columnMap.put("Material.typeDisplay", column);

		FlexType productType=FlexTypeCache.getFlexTypeRoot("Product");

		//product name attribute.
		FlexTypeAttribute productNameAtt = productType.getAttribute(PRODUCT_NAME);

		//column for productName.
		column = flexg.createTableColumn(productType.getAttribute(PRODUCT_NAME), productType, false);
		column.setDisplayed(true);
		column.setTableIndex(productNameAtt.getSearchResultIndex());
		columnMap.put("Product.productName", column);


		//column for skuName.
		column = flexg.createTableColumn(productType.getAttribute(SKU_NAME), productType, false);
		column.setDisplayed(true);
		column.setTableIndex(productType.getAttribute(SKU_NAME).getSearchResultIndex());
		columnMap.put("Colorway.skuName", column);

		//column for seasonName.
		column = new TableColumn();
		column.setDisplayed(true);
		column.setHeaderLabel("Season");	    
		column.setLinkTableIndex("LCSSEASON.BRANCHIDITERATIONINFO");
		column.setTableIndex(FlexTypeCache.getFlexTypeRoot("Season").getAttribute(SEASON_NAME).getSearchResultIndex());
		columnMap.put("ProductSeason.seasonName", column);


		//column for bomName.
		column = new TableColumn();
		column.setDisplayed(true);
		column.setHeaderLabel("BOM Name");	    
		column.setTableIndex(FlexTypeCache.getFlexTypeRoot("BOM").getAttribute(BOM_PART_NAME).getSearchResultIndex());
		columnMap.put("BOM.ptcbomPartName", column);

		//column for bomName.
		column = new TableColumn();
		column.setDisplayed(true);
		column.setHeaderLabel("Destination Variation");	    
		column.setTableIndex("PRODUCTDESTINATION.DESTINATIONNAME");
		columnMap.put("BOM_LINK.dimensionName", column);


		column = new TableColumn();
		//update HEADER LEBEL FROM "Primary Source" TO "Primary Source (Season)" for MFRD change-PHASE4
		column.setHeaderLabel("Primary Source (Season)");
		column.setDisplayed(true);
		column.setTableIndex("SOURCINGCONFIG.PRIMARYSOURCE");
		columnMap.put("Sourcing Configuration.primarySource", column);

		//column for Exchange rate USD/RMB.
		column = new TableColumn();
		column.setDisplayed(true);
		column.setHeaderLabel("Exchange rate USD/RMB");	    
		column.setFormat(FormatHelper.FLOAT_FORMAT);
		column.setAttributeType("float");
		column.setDecimalPrecision(2);
		column.setTableIndex("Exchange Rate(RMB).smFxExchangeRate");
		columnMap.put("EXCHANGERATE_RMB.smFxExchangeRate", column);

		//column for Exchange rate USD/RUB.
		column = new TableColumn();
		column.setDisplayed(true);
		column.setHeaderLabel("Exchange rate USD/RUB");	    
		column.setDecimalPrecision(2);
		column.setFormat(FormatHelper.FLOAT_FORMAT);
		column.setAttributeType("float");

		column.setTableIndex("Exchange Rate(RUB).smFxExchangeRate");
		columnMap.put("EXCHANGERATE_RUB.smFxExchangeRate", column);
	}  

}

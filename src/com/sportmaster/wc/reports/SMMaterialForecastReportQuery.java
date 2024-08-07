package com.sportmaster.wc.reports;

import java.sql.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialColorQuery;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.MaterialPricingEntryQuery;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSKUSourcingLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

/**
 * 
 * @author 'true' BSC -PTC.
 * @version 'true' 1.0 version number.
 */
public final class SMMaterialForecastReportQuery extends LCSQuery  implements java.io.Serializable {
	
	

	private static final String SM_FORECAST_UNITS_STYLE = "smForecastUnitsStyle";

	private static final String DATE_FORMAT = "yyyyMMddHHmmss";

	private static final String SEASON_NAME = "seasonName";

	private static final String VRD_BRAND = "vrdBrand";

	private static final String SM_RUB = "smRUB";

	private static final String VRD_CNY = "vrdCny";

	private static final String FLEXBOMLINK_IDA2A2 = "FLEXBOMLINK.IDA2A2";


	/**
	 * LOGGER .
	 */
	private static final org.apache.log4j.Logger LOGGER=Logger.getLogger("MFDRLOG");

	/**
	 * Flex Path.
	 */
	public static final String EXCHANGE_RATE_FLEX_PATH = LCSProperties.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.flexPath","Business Object\\Lookup Tables\\smSeasonalExchangeRates");


	/**
	 * Currency.
	 */
	public static final String BO_CURRENCY = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.smFxCurrency","smFxCurrency");

	/**
	 * Season.
	 */
	public static final String BO_SEASON = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.smFxSeason","smFxSeason");
	/**
	 * Exchange Rate.
	 */
	public static final String BO_EXCHANGE_RATE = LCSProperties
			.get("com.sportmaster.wc.seasonproduct.SMCostSheetPlugin.smFxExchangeRate","smFxExchangeRate");

	/**
	 * SMMaterialForecastReportQuery method.
	 */
	private SMMaterialForecastReportQuery(){
		//	utiltiy constructor.
	}

	/**
	 * Method runReportQuery - runReportQuery.
	 * @param inputSelectedMap the inputSelectedMap.
	 * @return true/false.
	 * @throws WTException the WTException.
	 * @throws SQLException the SQLException.
	 * @throws ParseException the ParseException.
	 * @throws WTPropertyVetoException the WTPropertyVetoException.
	 */
	public static void runReportQuery(Map<String, Object> inputSelectedMap, Boolean async) throws WTException, SQLException, ParseException, WTPropertyVetoException{
		com.lcs.wc.client.ClientContext context=com.lcs.wc.client.ClientContext.getContext();
		SMMaterialForecastReportBean reportBean=runReportQuery(context,inputSelectedMap);

		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		Date date = new Date(); 
		String downloadFileKey =dateFormat.format(date);					

		com.lcs.wc.client.web.ExcelGenerator excelg=new com.lcs.wc.client.web.ExcelGenerator();

		SMMaterialForecastReportHeader ethg = new SMMaterialForecastReportHeader(reportBean);
		if(ethg != null){
			excelg.setExcelTableHeaderGenerator(ethg);
		}
		String fileURL = excelg.drawTable(reportBean.getReportData(), reportBean.getFinalColumns(), context, "MaterialForecastReport_" + downloadFileKey, "");
		LOGGER.debug("Creating Material Forecast report document and uploading the generated report...");
		SMMaterialForecastReportDocumentHelper.createReportDocumentInLibrary(inputSelectedMap, fileURL, reportBean);	
	} 


	
	/**
	 * Method runReportQuery - runReportQuery.
	 * @param inputSelectedMap the inputSelectedMap.
	 * @return true/false.
	 * @throws WTException the WTException.
	 * @throws SQLException the SQLException.
	 * @throws ParseException the ParseException.
	 * @throws WTPropertyVetoException the WTPropertyVetoException.
	 */
	public static SMMaterialForecastReportBean runReportQuery(com.lcs.wc.client.ClientContext context, Map<String, Object> inputSelectedMap) throws WTException, SQLException, ParseException, WTPropertyVetoException{

		SMMaterialForecastReportBean reportBean=new SMMaterialForecastReportBean();

		SMMaterialForecastReportModel reportModel=new SMMaterialForecastReportModel();

		reportBean=reportModel.getReportColumnsData(context, inputSelectedMap, reportBean);		

		FlexType seasonFlexType = FlexTypeCache.getFlexTypeFromPath("Season");	 

		reportBean = SMMaterialForecastReportHelper.getSelectedCriteria(inputSelectedMap,reportBean);

		Collection<FlexObject> reportData =new ArrayList<FlexObject>();
		if(!reportBean.getSelectedSeasonOids().isEmpty()){	  

			FlexType pdType = FlexTypeCache.getFlexTypeFromPath("Product Destination");
			FlexTypeAttribute nameAtt = pdType.getAttribute("name");
			String destinationNameColumn = nameAtt.getSearchResultIndex();

			FlexType bomType= FlexTypeCache.getFlexTypeFromPath("BOM");
			FlexTypeAttribute quantityAtt = bomType.getAttribute("quantity");
			String quantityAttColumn = quantityAtt.getSearchResultIndex();
			Collection totalBOMData=null;
			for(String seasonOid : reportBean.getSelectedSeasonOids()){
				Collection<FlexObject> linesheetData =new ArrayList<FlexObject>();

				LCSSeason season=(LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+seasonOid);
				if(seasonOid==null){
					continue;
				}

				String classNAME = "LCSLIFECYCLEMANAGED"; 				
				final Long seasonBranchId = season.getBranchIdentifier();
				FlexType boType=FlexTypeCache.getFlexTypeFromPath(EXCHANGE_RATE_FLEX_PATH);
				final com.lcs.wc.db.PreparedQueryStatement statement = new com.lcs.wc.db.PreparedQueryStatement();
				statement.appendFromTable(com.lcs.wc.foundation.LCSLifecycleManaged.class);
				statement.appendSelectColumn(classNAME,
						boType.getAttribute(BO_EXCHANGE_RATE).getColumnName());

				statement.appendSelectColumn(classNAME,
						boType.getAttribute(BO_CURRENCY).getColumnName());
				statement.appendOpenParen();
				statement.appendCriteria(new Criteria(classNAME, boType.getAttribute(
						BO_CURRENCY).getColumnName(), VRD_CNY, Criteria.EQUALS));
				statement.appendOrIfNeeded();
				statement.appendCriteria(new Criteria(classNAME, boType.getAttribute(
						BO_CURRENCY).getColumnName(), SM_RUB, Criteria.EQUALS));
				statement.appendClosedParen();
				statement.appendAndIfNeeded();
				statement.appendCriteria(new Criteria(classNAME, boType.getAttribute(
						BO_SEASON).getColumnName(), seasonBranchId.toString(),
						Criteria.EQUALS));
				SearchResults rs=LCSQuery.runDirectQuery(statement);
				double rmbExRate=0;
				double rubExRate=0;
				String currenyType="";
				if(rs.getResultsFound()>0){
					Iterator itr=rs.getResults().iterator();
					
					while(itr.hasNext()){
						FlexObject fo=(FlexObject) itr.next();
						currenyType=fo.getData("LCSLIFECYCLEMANAGED."+boType.getAttribute(BO_CURRENCY).getColumnName().toUpperCase());
						//get rmbExRate.
						//Updated for JIRA 852, Exchange Rate
						if(VRD_CNY.equals(currenyType)){
						rmbExRate = getRateValues(boType,
								currenyType, fo,BO_EXCHANGE_RATE,VRD_CNY);
						}
						
						if(SM_RUB.equals(currenyType)){
						//get rubExRate.
						rubExRate = getRateValues(boType,
								currenyType, fo,BO_EXCHANGE_RATE,SM_RUB);
						
						}

					}
				} 

				com.lcs.wc.season.LineSheetQuery lsq = new com.lcs.wc.season.LineSheetQuery();

				Map cMap=new HashMap();

				FlexType productType=season.getProductType();
				if(reportBean.getSelectedBrands()!=null){
					cMap.put(productType.getAttribute(VRD_BRAND).getSearchCriteriaIndex(),inputSelectedMap.get("brands"));
				}

				Collection<FlexObject> lsDataCol = lsq.runSeasonProductReport(null,  //LCSProduct product
						season, //LCSSeason season
						null, //LCSSourcingConfig config
						true, //boolean skus
						cMap, //Map criteria
						true, //boolean postProcessing
						null, //String materialGroupId
						true, //boolean sourcing
						true, //boolean secondarySourcing
						false, //boolean primaryCostOnly
						false, //boolean whatifCosts
						reportBean.getAttKeyList(), //Collection usedAttKeys
						false, //boolean includeRemoved
						false, //boolean includePlaceHolders
						null, //Collection seasonGroupIds
						true, //Include cost spec (only applied if sourcing is true)
						null, // cost Spec which is not filtered out here
						null, // PreparedStatement
						true, // execute this at sku level to enable rollup of color information
						false, // exclude Inactive costsheets
						-1
						);

				LOGGER.debug("linesheet data for season :"+season.getName()+" >>>>>>"+lsDataCol.size());				

				
				Map lsDataByProduct = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(lsDataCol, "LCSPRODUCT.IDA3MASTERREFERENCE");
				Collection<String> prdMasterOids=new ArrayList<String>();
				Iterator lsRowsItr = lsDataByProduct.entrySet().iterator();
				while(lsRowsItr.hasNext()){	
					Map.Entry me=(Map.Entry) lsRowsItr.next();	
					prdMasterOids.add((String)me.getKey());
				}
				if(prdMasterOids.isEmpty()){
					continue;					
				}

				Collection<String> seasonOids=new ArrayList<String>();
				//Updated for JIRA - SMPLM-605 - getting numeric ID, instead complete class with id.
				seasonOids.add(LCSQuery.getNumericFromOid(FormatHelper.getVersionId(season)));

				//Main query to get the bomlink data by products and season
				SMMaterialForecastReportHelper smReportHelper=new SMMaterialForecastReportHelper();

				SearchResults bomTopRows=smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,seasonOids,prdMasterOids,false,false,false,false,false);		

				SearchResults ovrBomData=smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,seasonOids,prdMasterOids,false,true,false,false,false);				
				LOGGER.debug("bomTopRows>>>>>>>>>>>>>>>>>>>>>>>>"+bomTopRows.getResultsFound());
				LOGGER.debug("ovrBomData>>>>>>>>>>>>>>>>>>>>>>>>"+ovrBomData.getResultsFound());
				SearchResults bomTopMatColorRows=smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,seasonOids,prdMasterOids,true,false,false,false,false);		

				SearchResults ovrBomMatColorData=smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,seasonOids,prdMasterOids,true,true,false,false,false);

				SearchResults matColorOvrdnData=smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,seasonOids,prdMasterOids,true,true,true,false,false);
				
				SearchResults matColorOvrdnData2=smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,seasonOids,prdMasterOids,true,true,true,false,true);

				LOGGER.debug("bomTopMatColorRows>>>>>>>>>>>>>>>>>>>>>>>>"+bomTopMatColorRows.getResultsFound());
				
				LOGGER.debug("ovrBomMtColorData>>>>>>>>>>>>>>>>>>>>>>>>"+ovrBomMatColorData.getResultsFound());
				LOGGER.debug("matColorOvrdnData>>>>>>>>>>>>>>>>>>>>>>>>"+matColorOvrdnData.getResultsFound());
				
				
			    // LOGGER.debug("matColorOvrdnDat2a>>>>>>>>>>>>>>>>>>>>>>>>"+matColorOvrdnData2.getResultsFound());

				SearchResults ovrMatOvrBomData=smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,seasonOids,prdMasterOids,false,true,false,true,false);				
				SearchResults ovrMatColOvrBomData=smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,seasonOids,prdMasterOids,true,true,true,true,false);				

				LOGGER.debug("ovrMatOvrBomData>>>>>>>>>>>>>>>>>>>>>>>>"+ovrMatOvrBomData.getResultsFound());
				LOGGER.debug("ovrMatColOvrBomData>>>>>>>>>>>>>>>>>>>>>>>>"+ovrMatColOvrBomData.getResultsFound());
				
				totalBOMData=bomTopRows.getResults(); 
				totalBOMData.addAll(bomTopMatColorRows.getResults());		

				Collection<FlexObject> overridenRows=ovrMatOvrBomData.getResults();
				overridenRows.addAll(ovrMatColOvrBomData.getResults());	

				Map ovrBomLinkMap = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(overridenRows, "OVERRIDENROW.IDA2A2");
				if(ovrBomData.getResultsFound()>0){
					for(FlexObject fo:(Collection<FlexObject>)ovrBomData.getResults()){
						String bomlinkOid=fo.getData("OVERRIDENROW.IDA2A2");
						if(!ovrBomLinkMap.containsKey(bomlinkOid)){
							overridenRows.add(fo);
						} 
					}
				}
				
				if(matColorOvrdnData2.getResultsFound()>0){
					for(FlexObject fo:(Collection<FlexObject>)matColorOvrdnData2.getResults()){
						String bomlinkOid=fo.getData("OVERRIDENROW.IDA2A2");
						if(!ovrBomLinkMap.containsKey(bomlinkOid)){
							overridenRows.add(fo);
						} 
					}
				}


				if(matColorOvrdnData.getResultsFound()>0){
					for(FlexObject fo:(Collection<FlexObject>)matColorOvrdnData.getResults()){
						String bomlinkOid=fo.getData("OVERRIDENROW.IDA2A2");
						if(!ovrBomLinkMap.containsKey(bomlinkOid)){
							overridenRows.add(fo);
						} 
					}
				}

				if(ovrBomMatColorData.getResultsFound()>0){
					for(FlexObject fo:(Collection<FlexObject>)ovrBomMatColorData.getResults()){
						String bomlinkOid=fo.getData("OVERRIDENROW.IDA2A2");
						if(!ovrBomLinkMap.containsKey(bomlinkOid)){
							overridenRows.add(fo);
						} 
					}
				}

				reportData.addAll(frameDataForReport(reportBean, seasonFlexType, linesheetData,
						destinationNameColumn, quantityAttColumn, season,
						lsDataCol,totalBOMData,overridenRows,rmbExRate,rubExRate));	

			}

			reportBean.setReportData(reportData);
		} 
		return reportBean;
	}

	/**
	 * @param boType
	 * @param rmbExRate
	 * @param currenyType
	 * @param fo
	 * @return
	 * @throws WTException
	 */
	private static double getRateValues(FlexType boType, 
			String currenyType, FlexObject fo,String attKey,String typeString) throws WTException {
		double value = 0.0;
		if(typeString.equals(currenyType)){
			value=fo.getDouble("LCSLIFECYCLEMANAGED."+boType.getAttribute(attKey).getColumnName().toUpperCase());
		}
		return value;
	}

	/**
	 * Method frameDataForReport - frameDataForReport.
	 * @param reportBean the reportBean.
	 * @param seasonFlexType the seasonFlexType.
	 * @param linesheetData the linesheetData.
	 * @param destinationNameColumn the destinationNameColumn.
	 * @param quantityAttColumn the quantityAttColumn.
	 * @param season the season.
	 * @param lsDataCol the lsDataCol.
	 * @param rubExRate  the rubExRate.
	 * @param rmbExRate the rmbExRate.
	 * @throws WTException the WTException.
	 */
	private static Collection<FlexObject> frameDataForReport(
			SMMaterialForecastReportBean reportBean, FlexType seasonFlexType,
			Collection<FlexObject> linesheetData, String destinationNameColumn,
			String quantityAttColumn, LCSSeason season,
			Collection<FlexObject> lsDataCol,Collection<FlexObject> topBomRows,Collection<FlexObject> overriddnBOMRows, double rmbExRate, double rubExRate) throws WTException {
		if(!lsDataCol.isEmpty()){
			Map bomRowMap = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(topBomRows, "LCSPRODUCT.IDA3MASTERREFERENCE");

			Map ovrdBomRowMap = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(overriddnBOMRows, "LCSPRODUCT.IDA3MASTERREFERENCE");

			String fcAttColumn=season.getProductType().getAttribute(SM_FORECAST_UNITS_STYLE).getSearchResultIndex();
			Map lsGroupdata = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(lsDataCol, "LCSPRODUCT.IDA3MASTERREFERENCE");			

			for(FlexObject lsData:lsDataCol){
				String prdMasterOid = (String)lsData.getData("LCSPRODUCT.IDA3MASTERREFERENCE");
				String prdSrcMasterOid = (String)lsData.getData("LCSSOURCINGCONFIG.IDA3MASTERREFERENCE");
				String prdForecastQty = (String)lsData.getData(fcAttColumn);
				double initialFcQty=Double.parseDouble(prdForecastQty);
				Map lsSkudata = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(((ArrayList)lsGroupdata.get(prdMasterOid)), "LCSSKUSEASONLINK.IDA2A2");
				int skuSize=lsSkudata.size();
				initialFcQty=Math.round(initialFcQty/skuSize);
				if(bomRowMap.get(prdMasterOid)==null){
					continue;
				}
				
				//("lsData >>>>>>>>>>>>>>>>>>>" +lsData);
				
				
				Collection<FlexObject> bomRows=(Collection<FlexObject>) bomRowMap.get(prdMasterOid);
				Map bomRowsByBOMPart = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(bomRows, "FLEXBOMPART.IDA2A2");
				
				Collection<FlexObject> ovrdBomRows=(Collection<FlexObject>) ovrdBomRowMap.get(prdMasterOid);
				if(ovrdBomRows==null){
					ovrdBomRows=new ArrayList();
				}
				Map ovrBomLinkMap = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(ovrdBomRows, FLEXBOMLINK_IDA2A2);

				Iterator bomMSRowsItr = bomRowsByBOMPart.entrySet().iterator();
				while(bomMSRowsItr.hasNext()){					
					Map.Entry me=(Map.Entry) bomMSRowsItr.next();	
					Map bomRowsByBranchId= com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(((ArrayList)me.getValue()),"FLEXBOMLINK.BRANCHID");
					Iterator bomRowsItr = bomRowsByBranchId.entrySet().iterator();
					while(bomRowsItr.hasNext()){					
						Map.Entry me1=(Map.Entry) bomRowsItr.next();	
						FlexObject mainRow=(FlexObject) ((ArrayList)me1.getValue()).get(0);

					(new SMMaterialForecastReportQuery()). setReportDataRows(reportBean,seasonFlexType, quantityAttColumn,linesheetData,
								destinationNameColumn, season, rmbExRate,
								rubExRate, ovrBomLinkMap, fcAttColumn, lsData,
								prdSrcMasterOid, initialFcQty,
								mainRow);
					}
				}
			}
		}
		return linesheetData;
	}

	/**
	 * @param seasonFlexType
	 * @param linesheetData
	 * @param destinationNameColumn
	 * @param season
	 * @param rmbExRate
	 * @param rubExRate
	 * @param bomLinkMap
	 * @param fcAttColumn
	 * @param lsData
	 * @param prdSrcMasterOid
	 * @param initialFcQty
	 * @param bomRowsByDestinations
	 * @throws WTException
	 */
	private  void setReportDataRows(SMMaterialForecastReportBean reportBean,FlexType seasonFlexType,
			String quantityAttColumn,
			Collection<FlexObject> linesheetData, String destinationNameColumn,
			LCSSeason season, double rmbExRate, double rubExRate,
			Map ovrBomLinkMap, String fcAttColumn, FlexObject lsData,
			String prdSrcMasterOid, double initialFcQty,
			FlexObject mainRow) throws WTException {
		
		String mainRowOid=mainRow.getData(FLEXBOMLINK_IDA2A2);
		String bomSrcMasterid=mainRow.getString("LATESTITERFLEXSPECIFICATION.IDA3B12");
		
		

		//checking BOM is belongs to Product
		if(!prdSrcMasterOid.equals(bomSrcMasterid)){
			return;
		}

		String bomOwnerOid = mainRow.getData("FLEXBOMPART.IDA3A12");
		boolean hasRU=false;
		boolean hasCN=false;
		boolean hasUA=false;

		//checking BOM should belongs product owner.
		if(!FormatHelper.hasContent(bomOwnerOid)){
			return;
		} 

		
		Collection<FlexObject> ovrRows=(Collection<FlexObject>) ovrBomLinkMap.get(mainRowOid);

		if(ovrRows==null){
			ovrRows=new ArrayList();
		}
		String scOid = (String)lsData.getData("LCSSOURCINGCONFIG.BRANCHIDITERATIONINFO");		
		String skuMasterid = (String)lsData.getData("LCSSKU.IDA3MASTERREFERENCE");
		String seasOid = (String)lsData.getData("LCSPRODUCTSEASONLINK.SEASONREVID");
		
		/*String skuId = (String)lsData.getData("LCSSKU.BRANCHIDITERATIONINFO");
		if (!isSKUisSelectedonSource(scOid,skuId, season )) {
			return;
		}*/
		
		//Check for the colorway is selected in the source to season link - JIRA 848
		HashMap sourceskuMap = (HashMap)isSKUSelectedOnSourceSeason(scOid,seasOid);

		Map ovrRowsByColor = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(ovrRows, "OVERRIDENROW.idA3E5");
		Collection<FlexObject> ovrFoByColor=(Collection<FlexObject>) ovrRowsByColor.get(skuMasterid);

		Map ovrRowsTemp=new HashMap();		
		ovrRowsTemp.put("", mainRow);
		mainRow.put("DESTINATION.UA","Yes");
		mainRow.put("DESTINATION.CN","Yes");
		mainRow.put("DESTINATION.RU","Yes");


		//Getting Destination row
		Map destVariationMapTemp=new HashMap();
		Map destRows = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(ovrRows, "OVERRIDENROW.DIMENSIONNAME");
		if(destRows.containsKey(":DESTINATION")){
			Collection<FlexObject> destOrRows=(Collection<FlexObject>) destRows.get(":DESTINATION");
			for(FlexObject fo:destOrRows){
				FlexObject destFo=new FlexObject();
				destFo.putAll(fo);
 				destVariationMapTemp.put(destFo.get("PRODUCTDESTINATION.DESTINATIONNAME"), destFo);
			} 				
		}

		//colorway variation, if not main row
		if(ovrFoByColor!=null && ovrFoByColor.size()>0){
			Map colorRows = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(ovrFoByColor, "OVERRIDENROW.DIMENSIONNAME");
			
			if(colorRows.containsKey(":SKU")){
 				FlexObject fo=(FlexObject)((ArrayList)colorRows.get(":SKU")).get(0);
 				FlexObject colorFo=new FlexObject();
 				colorFo.putAll(fo);
				colorFo.put("PRODUCTDESTINATION.DESTINATIONNAME", "");
				//LOGGER.debug("clolor name in Color..."+colorFo.getData("LCSCOLOR.COLORNAME"));

				setOverridenRowValues(colorFo,null, null,mainRow);
				ovrRowsTemp.put("", colorFo);
			}			

			//SKU destination variation, if get destination row, if not get color
			if(colorRows.containsKey(":SKU:DESTINATION")){
				Collection<FlexObject> destOrRows=(Collection<FlexObject>) colorRows.get(":SKU:DESTINATION");
				for(FlexObject fo:destOrRows){ 	
					FlexObject colrDestFo=new FlexObject();
					colrDestFo.putAll(fo);
					FlexObject colorFo=(FlexObject) ovrRowsTemp.get("");
					FlexObject destFo=(FlexObject) destVariationMapTemp.get(colrDestFo.get("PRODUCTDESTINATION.DESTINATIONNAME"));
					if(destFo==null){
						destFo=new FlexObject();
					}
					//LOGGER.debug("clolor name in Color-dest.Before."+colrDestFo.get("OVERRIDENROW.IDA3D5")+"."+colrDestFo.getData("LCSCOLOR.COLORNAME"));

					setOverridenRowValues(colrDestFo, destFo,colorFo,mainRow);				
					

					ovrRowsTemp.put(colrDestFo.get("PRODUCTDESTINATION.DESTINATIONNAME"), colrDestFo);								
				} 				
			}
			
		}

		//If destination link itertaion, add color or main row
		Iterator bomDestRowsItr = destVariationMapTemp.entrySet().iterator();
		while(bomDestRowsItr.hasNext()){					
			Map.Entry me=(Map.Entry) bomDestRowsItr.next();
			if(!ovrRowsTemp.containsKey(me.getKey())){
				FlexObject fo=(FlexObject) me.getValue();
				FlexObject destFo=new FlexObject();
				destFo.putAll(fo);
				FlexObject colorFo=(FlexObject) ovrRowsTemp.get("");
				setOverridenRowValues(destFo, null,colorFo,mainRow);
				ovrRowsTemp.put(me.getKey(), destFo);
			}
		}


		if(ovrRowsTemp.containsKey("UA")){
			mainRow.put("DESTINATION.UA","no");
			hasUA=true;
		} 
		if(ovrRowsTemp.containsKey("CN")){
			mainRow.put("DESTINATION.CN","no");
			hasCN=true;
		}
		if(ovrRowsTemp.containsKey("RU")){
			mainRow.put("DESTINATION.RU","no");
			hasRU=true;
		}

		if(ovrRowsTemp.isEmpty()){
			mainRow.put("DESTINATION.UA","Yes");
			mainRow.put("DESTINATION.CN","Yes");
			mainRow.put("DESTINATION.RU","Yes");
			ovrRowsTemp.put("", mainRow);
		}
		Collection<String> parentRowColumnsProperty=new ArrayList();
		Iterator bomMSRowsItr = ovrRowsTemp.entrySet().iterator();
		while(bomMSRowsItr.hasNext()){					
			Map.Entry me=(Map.Entry) bomMSRowsItr.next();
			String destName=(String) me.getKey();
			FlexObject oFo=(FlexObject) me.getValue();

			FlexObject fo=new FlexObject();
			fo.putAll(mainRow);
			fo.putAll(lsData);
			fo.put("WTTYPEDEFINITION.BRANCHIDITERATIONINFO",mainRow.get("WTTYPEDEFINITION.BRANCHIDITERATIONINFO"));
			fo.put(fcAttColumn,initialFcQty);
			
		
			
			setRetailDestinationValue(lsData, fo);

			if(((String)mainRow.get("WTTYPEDEFINITION.NAME")).indexOf("_Fabric")>-1 ){
				fo.put("IS_FABRIC_TYPE","Yes");
			}

			if(((String)mainRow.get("WTTYPEDEFINITION.NAME")).indexOf("Embellishments")>-1 ||
					((String)mainRow.get("WTTYPEDEFINITION.NAME")).indexOf("ProductPackaging")>-1){
				LCSMaterial material=(LCSMaterial)LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:"+fo.get("LCSMATERIAL.BRANCHIDITERATIONINFO"));
				fo.put("MATERIAL.APPLICATIONTECHNICQUE", material.getValue("smApplicationTechnique"));
			}
			if(oFo!=null){
				fo.putAll(oFo);
				setOverridenRowValues(fo, oFo);
			}

			if("UA".equalsIgnoreCase(destName)){
				fo.put("DESTINATION.UA","Yes");
				fo.put("DESTINATION.CN","no");
				fo.put("DESTINATION.RU","no");
				parentRowColumnsProperty=clearNonDestinationValue(reportBean, parentRowColumnsProperty,"UA", fo); 

			} else if("CN".equalsIgnoreCase(destName)){
				fo.put("DESTINATION.CN","Yes");
				fo.put("DESTINATION.UA","no");
				fo.put("DESTINATION.RU","no");
				parentRowColumnsProperty=clearNonDestinationValue(reportBean, parentRowColumnsProperty,"CN", fo); 

			}else if("RU".equalsIgnoreCase(destName)){
				parentRowColumnsProperty=clearNonDestinationValue(reportBean, parentRowColumnsProperty,"RU", fo); 
				fo.put("DESTINATION.UA","no");
				fo.put("DESTINATION.CN","no");
				fo.put("DESTINATION.RU","Yes");				
			}

			
			//Setting Sourcing config primary details.
			//added season id to retrive source to season primary value
			setSCPrimaryData(fo, scOid,seasOid);
			
			//Order destination from source to season - changes for CarryOver CR
			setOrderDestinationFromSourcetoSeason(fo, scOid, seasOid);
			
			
			double yield=fo.getDouble(quantityAttColumn);
			fo.put(seasonFlexType.getAttribute(SEASON_NAME).getSearchResultIndex(), season.getName());
			String dest1=null;
			if("Yes".equals(""+fo.get("DESTINATION.RU"))){
				dest1="RU";
			}
			String dest2=null;
			if("Yes".equals(""+fo.get("DESTINATION.CN"))){
				dest2="CN";
			}
			String dest3=null;

			if("Yes".equals(""+fo.get("DESTINATION.UA"))){
				dest3="UA";
			}
			if(!FormatHelper.hasContent(destName)){
				Collection<String> excludeAttKeys=new ArrayList();

				if(hasRU){				
					excludeAttKeys.addAll(reportBean.getAttRUList());
				}

				if(hasCN){
					excludeAttKeys.addAll(reportBean.getAttCNList());
				}

				if(hasUA){
					excludeAttKeys.addAll(reportBean.getAttUAList());
				}

				for (String attrIntName : excludeAttKeys) { 
					fo.put(attrIntName,0);
				}

			}
			SMMaterialForecastReportDocumentHelper.buildDataFromColIndex(reportBean, quantityAttColumn, fo,yield, lsData,dest1,dest2,dest3);

			
			// JIRA 993 : fix to print the material color attributes only if the material color belongs to the material supplier - Start
			LCSMaterialSupplier materialSupplier = (LCSMaterialSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterialSupplier:" +fo.getString("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO"));	
			String matSupID="VR:com.lcs.wc.material.LCSMaterialSupplier:" + fo.getString("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO");
			
			String materialColorId="";
			LCSMaterialColor materialColor=null;
			LCSMaterialSupplier materialSupplierForMatCol = null;			
			if((fo.getString("LCSMATERIALCOLOR.IDA2A2") != (null) && "null"!=fo.getString("LCSMATERIALCOLOR.IDA2A2"))){
				materialColorId = "OR:com.lcs.wc.material.LCSMaterialColor:" + fo.getString("LCSMATERIALCOLOR.IDA2A2");
				materialColor = (LCSMaterialColor) LCSQuery.findObjectById(materialColorId);
				materialSupplierForMatCol = VersionHelper.latestIterationOf(materialColor.getMaterialSupplierMaster());
				// If the material color does not belong to material supplier, set the material color object as null
				if(!materialSupplierForMatCol.toString().equals(materialSupplier.toString())) {
					materialColor = null;
				}				
			}	
								
			double mpePrice = 0d; 
			//double mpecolPrice=0d;
			Date effDate =new Date();
			String smTechnique = FlexTypeCache.getFlexTypeFromPath("Material Color").getAttribute("smTechnique").getSearchResultIndex();
			String smMscLLT = FlexTypeCache.getFlexTypeFromPath("Material Color").getAttribute("smMscLLT").getSearchResultIndex();
			String vrdColorSpecificPrice = FlexTypeCache.getFlexTypeFromPath("Material Color").getAttribute("vrdColorSpecificPrice").getSearchResultIndex();
			String smTechniqueValue = "";
			boolean smMscLLTValue = false;
			// Setting blank values for mat color attributes, otherwise, its retaining the previous flexobject's value even though there is no material color for the current flexobject
			fo.put(smTechnique, smTechniqueValue);
			fo.put(smMscLLT, smMscLLTValue);
			fo.put(vrdColorSpecificPrice, mpePrice);
			
			// Fix when material color is changed on CW Dest row (especially on add existing color scenario) - Start
			LCSMaterialColor materialColorCWDest = null;
			LCSColor colorCWDest = null;
			if(!"0".equals(fo.getData("OVERRIDENROW.IDA3D5")) && ":SKU:DESTINATION".equalsIgnoreCase(fo.getData("OVERRIDENROW.DIMENSIONNAME")) && !fo.getData("OVERRIDENROW.IDA3D5").equalsIgnoreCase("LCSCOLOR.IDA2A2")){
				colorCWDest = (LCSColor) LCSQuery.findObjectById("com.lcs.wc.color.LCSColor:"+fo.getData("OVERRIDENROW.IDA3D5"));
				if(colorCWDest != null) {
					materialColorCWDest = (LCSMaterialColor) LCSMaterialColorQuery.findMaterialColorsForMaterialSupplierAndColor(materialSupplier.getMaterialMaster().toString(), materialSupplier.getSupplierMaster().toString(), colorCWDest.toString());					
					fo.put("LCSCOLOR.IDA2A2", fo.getData("OVERRIDENROW.IDA3D5"));
					fo.put("LCSCOLOR.COLORNAME", colorCWDest.getName());
					// set materialColor with correct material color as per CW Dest Row color
					if(materialSupplier != null && materialColorCWDest != null){
						materialColor = materialColorCWDest;
					}
				}
			}
			// Fix when material color is changed on CW Dest row (especially on add existing color scenario) - End
			
			if(materialSupplier != null && materialColor != null){
				smTechniqueValue = "";
				smMscLLTValue = false;
				try {
					smTechniqueValue = (String) materialColor.getValue("smTechnique");
				 	if(materialColor.getValue("smMscLLT") !=null) {
				 		smMscLLTValue = (boolean) materialColor.getValue("smMscLLT");
				 	}
					mpePrice = new MaterialPricingEntryQuery().getPrice(materialSupplier, materialColor ,effDate);
					
					if(mpePrice<=0.0){
						mpePrice=(Double) materialColor.getValue("vrdColorSpecificPrice");
					}
					fo.put("LCSCOLOR.COLORNAME", materialColor.getColor().getColorName());
					
					fo.put(smTechnique, smTechniqueValue);
					fo.put(smMscLLT, smMscLLTValue);
					fo.put(vrdColorSpecificPrice, materialColor.getValue("vrdColorSpecificPrice"));
					// JIRA 993 : fix to print the material color attributes only if the material color belongs to the material supplier - End
									
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			else if(materialSupplier != null){
				try {
					mpePrice = new MaterialPricingEntryQuery().getPrice(materialSupplier, effDate);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			
			if(mpePrice>0.0){
				fo.put(FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("materialPrice").getSearchResultIndex(), mpePrice);
			}
			
			fo.put("Exchange Rate(RUB).smFxExchangeRate",rubExRate);
			fo.put("Exchange Rate(RMB).smFxExchangeRate",rmbExRate);
			//JIRA 848 - filtering colorway which are not selected on source
			if(sourceskuMap.containsKey(fo.get("LCSSKU.BRANCHIDITERATIONINFO"))) {
			linesheetData.add(fo); 
			}
		}
	}

	
	

	/**
	 * 
	 * @param fo
	 * @param foTemp
	 * @param override
	 */
	private static void setUpperLevelValues(FlexObject fo, FlexObject foTemp, boolean override) {
		Iterator bomOverridenRowsItr = fo.entrySet().iterator();
		while(bomOverridenRowsItr.hasNext()){					
			Map.Entry me2=(Map.Entry) bomOverridenRowsItr.next();	
			String key=(String) me2.getKey();
			if(key.contains("OVERRIDENROW") && !me2.getValue().equals("0") 
					&& (override ||"0".equals(foTemp.get(me2.getKey())))){ 
				foTemp.put(""+me2.getKey(), me2.getValue());
			} 
		}
	}


	
	/**
	 * @param lsData
	 * @param fo
	 * @return
	 * @throws WTException
	 */
	private static LCSSKUSeasonLink setRetailDestinationValue(
			FlexObject lsData, FlexObject fo) throws WTException {
		LCSSKUSeasonLink skspl=(LCSSKUSeasonLink)LCSQuery.findObjectById("OR:com.lcs.wc.season.LCSSKUSeasonLink:"+lsData.get("LCSSKUSEASONLINK.IDA2A2"));
		try {
			if(skspl!=null){
				if(FormatHelper.hasContent(""+skspl.getValue("smRetailDestinationSync"))){
					fo.put("LCSSKUSEASONLINK.RETAILDESTINATION", skspl.getValue("smRetailDestinationSync"));
				}else{
					LCSProductSeasonLink psl=(LCSProductSeasonLink)LCSQuery.findObjectById("OR:com.lcs.wc.season.LCSProductSeasonLink:"+lsData.get("LCSPRODUCTSEASONLINK.IDA2A2"));
					fo.put("LCSSKUSEASONLINK.RETAILDESTINATION", psl.getValue("smRetailDestinationSync"));
				}
			}
		} catch (WTException e) {
			LOGGER.error(e.getMessage());
		}
		return skspl;
	}
	
	
	/**
	 * This method to set carry over CR - ger Order destination from source to season.
	 * @param fo
	 * @param scOid
	 * @param seasOid
	 * @throws WTException 
	 */
	private void setOrderDestinationFromSourcetoSeason(FlexObject fo,
			String sourcOid, String seasOid) throws WTException {
		
		LCSSourcingConfig sourc=(LCSSourcingConfig)LCSQuery.findObjectById("VR:com.lcs.wc.sourcing.LCSSourcingConfig:"+sourcOid);
		LCSSeason season=(LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+seasOid);
		LCSSourceToSeasonLink stsl=null;
		if(sourc != null && season != null){
			stsl = (new LCSSourcingConfigQuery()).getSourceToSeasonLink(sourc, season);
			if(stsl != null){
			fo.put("LCSSOURCESEASONLINK.ORDERDESTINATION", stsl.getValue("smSSLDestination"));
			}
		}
		
		
		
		
	}

	/**
	 * phase 4 update:
	 * added season id to get primary sorece season value.
	 * @param lsData
	 * @param scOid
	 * @return
	 * @throws WTException
	 */
	private static FlexObject setSCPrimaryData(FlexObject lsData,
			String scOid,String seasOid) throws WTException {
		LCSSourcingConfig sc=(LCSSourcingConfig)LCSQuery.findObjectById("VR:com.lcs.wc.sourcing.LCSSourcingConfig:"+scOid);
		LCSSeason season=(LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+seasOid);
		LCSSourceToSeasonLink stsl=null;
		if(sc!=null && season != null){
			stsl = (new LCSSourcingConfigQuery()).getSourceToSeasonLink(sc, season);
			if(stsl != null && stsl.isPrimarySTSL()){
				lsData.put("SOURCINGCONFIG.PRIMARYSOURCE", "Yes");
			}else if(stsl != null) {
				lsData.put("SOURCINGCONFIG.PRIMARYSOURCE", "No");
			}
		}else if(sc!=null){
			lsData.put("SOURCINGCONFIG.PRIMARYSOURCE", "No");
		}
		return lsData;
	}
	
	
	/**
	 * Getting the collection of colrways which are selected on the source to season.
	 * @param scOid
	 * @param seasOid
	 * @return
	 * @throws WTException
	 */
	private static Map isSKUSelectedOnSourceSeason(String scOid,String seasOid) throws WTException {
		
		
		LCSSKUSourcingLink scToSkuObj=null;
		LCSSKU skuObj=null;
		Map skuMap = new HashMap();
		
		LCSSourcingConfig sc=(LCSSourcingConfig)LCSQuery.findObjectById("VR:com.lcs.wc.sourcing.LCSSourcingConfig:"+scOid);
		LCSSeason season=(LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+seasOid);
		LOGGER.info("Season Name >>>>>>>>>>>>>>>"+season.getName());
		
		
		// Fetching Colorway Sourcing Link Data from sConf and Season Objects.
		Collection<FlexObject> skuToSrcCol = new LCSSourcingConfigQuery().getSKUSourcingLinkDataForConfig(sc, season, true).getResults();
		LOGGER.info("skuToSource Col : " + skuToSrcCol.size());

		
		for (FlexObject fo : skuToSrcCol) {
			// Getting Source To Colorway Object.
			scToSkuObj = (LCSSKUSourcingLink) LCSQuery.findObjectById(
					"com.lcs.wc.sourcing.LCSSKUSourcingLink:" + fo.getString("LCSSKUSOURCINGLINK.IDA2A2"));

			// Colorway object fetching from source To Colorway link.
			skuObj = VersionHelper.latestIterationOf(scToSkuObj.getSkuMaster());
			skuMap.put(fo.get("LCSSKU.BRANCHIDITERATIONINFO"),skuObj.getName());
		
			LOGGER.info("SKU Map which are selected on source >>>>>>>>>>> : " + skuMap);
		}
		
		return skuMap;
	}
	
	

	/**
	 * @param fo
	 * @param overriddnRow
	 */
	private static FlexObject setOverridenRowValues(FlexObject fo,
			FlexObject overriddnRow) {
		Iterator bomOverridenRowsItr = overriddnRow.entrySet().iterator();
		while(bomOverridenRowsItr.hasNext()){					
			Map.Entry me2=(Map.Entry) bomOverridenRowsItr.next();	
			String key=(String) me2.getKey();
			if(key.contains("OVERRIDENROW") && !me2.getValue().equals("0") && me2.getValue()!=null
					&& FormatHelper.hasContent(""+me2.getValue())){ 
				fo.put(key.replace("OVERRIDENROW", "FLEXBOMLINK"), me2.getValue());
			} 
		}
		return fo;
	} 

	/**
	 * 
	 * @param fo
	 * @param destRow
	 * @param colorwayRow
	 * @param mainRow
	 * @return
	 */
	private static FlexObject setOverridenRowValues(FlexObject fo,FlexObject destRow,
			FlexObject colorwayRow,FlexObject mainRow) { 
		
		
		if(destRow!=null){	
			// C14931417 and C15029063 - Start
			boolean isMatChangedForDestRowInCWDestVar = false;
			
			if (!destRow.isEmpty()
					&& mainRow.getData("LCSMATERIAL.BRANCHIDITERATIONINFO").equals(fo.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")) 
					&& !"0".equals(destRow.getData("LCSMATERIAL.BRANCHIDITERATIONINFO"))
					){ 
				isMatChangedForDestRowInCWDestVar = true;
			}
			// C14931417 and C15029063 - End
			for(String key:destRow.getIndexes()){
				
				// C14931417 and C15029063 - Fix for "when variation done on all levels
				// Ex. Material added to base row, on only cw or only dest change material,
				// Then do CW-Dest combination variation and change bom link attribute - it pulls base row material, supplier information"
				if (isMatChangedForDestRowInCWDestVar){
					if(key.startsWith("MATERIALMASTER.") || key.startsWith("LCSMATERIAL.") || key.startsWith("LCSMATERIALSUPPLIER.") 
							|| key.startsWith("FABRIC.") || key.startsWith("FABRIC_SUPPLIER.") || key.startsWith("LCSSUPPLIER.") 
							|| key.startsWith("LCSSUPPLIERMASTER."))
					{
						fo.setData(key, destRow.getString(key)); 
					}					
				} 
				// C14931417 and C15029063 - End
				
				
				/*if(key.equalsIgnoreCase(FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("materialPrice").getSearchResultIndex())
						&& destRow.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")!=fo.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")){
					continue;
				}*/
				
				/*Fix for JIRA - 547 
				 *  if overridden row has different material and material level attribute is empty it was taking from top row.
				 *  Fixed, flexobject shouldn't merge with top if attributes at material, material-supplier if different materials.
				 */
				if(isMaterialChangeatOverridenrow(fo,destRow,key)){
					continue;
				}
						 
				//To handle overidden mat-colo is empty on main coll, then get from colorway var. to handle colo added at parent level
				if("OVERRIDENROW.IDA3D5".equals(key) && "0".equals(fo.getData("OVERRIDENROW.IDA3D5")) ){
 					fo.setData("LCSMATERIALCOLOR.IDA2A2",destRow.getString("LCSMATERIALCOLOR.IDA2A2"));
				}
				else if(FormatHelper.hasContent(destRow.getData(key)) && !"0".equals(destRow.getData(key))
						&& (!FormatHelper.hasContent(fo.getData(key)) || "0".equals(fo.getData(key)))){

					fo.setData(key, destRow.getData(key));
				}
			}
		}
		
		
		
		if(colorwayRow!=null){
			boolean isSet = false;
			// C14931417 and C15029063 - Start
			boolean isMatChangedForCWRowInCWDestVar = false;
			
			if (!colorwayRow.isEmpty()
					&& mainRow.getData("LCSMATERIAL.BRANCHIDITERATIONINFO").equals(fo.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")) 
					&& !"0".equals(colorwayRow.getData("LCSMATERIAL.BRANCHIDITERATIONINFO"))
					){ 
				isMatChangedForCWRowInCWDestVar = true;
			}
			// C14931417 and C15029063 - End
			
			for(String key:colorwayRow.getIndexes()){
				/*if(key.equalsIgnoreCase(FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("materialPrice").getSearchResultIndex())
						&& colorwayRow.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")!=fo.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")){
					continue;
				}*/
			
				// C14931417 and C15029063 - Start				
				// C14931417 and C15029063 - Fix for "when variation done on all levels
				// Ex. Material added to base row, on only cw or only dest change material,
				// Then do CW-Dest combination variation and change bom link attribute - it pulls base row material, supplier information"
				
				if (isMatChangedForCWRowInCWDestVar){
					if(key.startsWith("MATERIALMASTER.") || key.startsWith("LCSMATERIAL.") || key.startsWith("LCSMATERIALSUPPLIER.") 
							|| key.startsWith("FABRIC.") || key.startsWith("FABRIC_SUPPLIER.") || key.startsWith("LCSSUPPLIER.") 
							|| key.startsWith("LCSSUPPLIERMASTER.")) 
					{
						fo.setData(key, colorwayRow.getString(key)); 
					}
				}
				// C14931417 and C15029063 - End
											
				/*Fix for JIRA - 547 
				 *  if overridden row has different material and material level attribute is empty it was taking from top row.
				 *  Fixed, flexobject shouldn't merge with top if attributes at material, material-supplier if different materials.
				 */
				if(isMaterialChangeatOverridenrow(fo,colorwayRow,key)){
					continue;
				}

				
				//To handle overidden mat-colo is empty on main coll, then get from colorway var. to handle colo added at parent level
			
				if("OVERRIDENROW.IDA3D5".equals(key) && "0".equals(fo.getData("OVERRIDENROW.IDA3D5")) && !":DESTINATION".equalsIgnoreCase(fo.getData("OVERRIDENROW.DIMENSIONNAME"))){
 					fo.setData("LCSMATERIALCOLOR.IDA2A2",colorwayRow.getString("LCSMATERIALCOLOR.IDA2A2"));
 					isSet = true;
				}else if(FormatHelper.hasContent(colorwayRow.getData(key)) && !"0".equals(colorwayRow.getData(key))
						&& (!FormatHelper.hasContent(fo.getData(key)) || "0".equals(fo.getData(key)))){
					
					fo.setData(key, colorwayRow.getData(key));
				}else if("LCSMATERIALCOLOR.IDA2A2".equals(key) && destRow == null && "null"!=colorwayRow.getString("LCSMATERIALCOLOR.IDA2A2") 
						&& colorwayRow.getString("LCSMATERIALCOLOR.IDA2A2")!=null &&
						!colorwayRow.getString("LCSMATERIALCOLOR.IDA2A2").equals(""+fo.getString("LCSMATERIALCOLOR.IDA2A2")) && isSet){
					fo.setData(key, colorwayRow.getData(key));
				}
				
				//To handle destination level color change/ colorway level color change 
				if(destRow != null && "OVERRIDENROW.IDA3D5".equals(key) && "0".equals(fo.getData("OVERRIDENROW.IDA3D5")) ){
 					fo.setData("LCSMATERIALCOLOR.IDA2A2",destRow.getString("LCSMATERIALCOLOR.IDA2A2"));
				}
				
			}			
		}
		
		

		if(mainRow!=null){
			for(String key:mainRow.getIndexes()){
				
				/*if(key.equalsIgnoreCase(FlexTypeCache.getFlexTypeFromPath("Material").getAttribute("materialPrice").getSearchResultIndex())
						&& mainRow.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")!=fo.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")){
					continue;
				}*/
					
				/*Fix for JIRA - 547 
				 *  if overridden row has different material and material level attribute is empty it was taking from top row.
				 *  Fixed, flexobject shouldn't merge with top if attributes at material, material-supplier if different materials.
				 */
					if(isMaterialChangeatOverridenrow(fo,mainRow,key)){
						continue;
					}
					
				if(FormatHelper.hasContent(mainRow.getData(key)) && !"0".equals(mainRow.getData(key))
						&& (!FormatHelper.hasContent(fo.getData(key)) || "0".equals(fo.getData(key)))){
					fo.setData(key, mainRow.getData(key));
				}
			}
		}

		return fo;
	} 


	private static Collection<String>  clearNonDestinationValue(
			SMMaterialForecastReportBean reportBean,
			Collection<String> parentRowColumnsProperty, String destName,
			FlexObject fo) {
		Collection<String> excludeAttKeys=new ArrayList();
		if("RU".equalsIgnoreCase(destName)){
			excludeAttKeys.addAll(reportBean.getAttCNList());
			excludeAttKeys.addAll(reportBean.getAttUAList());
			parentRowColumnsProperty.addAll(reportBean.getAttRUList());
		}

		if("CN".equalsIgnoreCase(destName)){ 
			excludeAttKeys.addAll(reportBean.getAttRUList());
			excludeAttKeys.addAll(reportBean.getAttUAList());
			parentRowColumnsProperty.addAll(reportBean.getAttCNList());
		}

		if("UA".equalsIgnoreCase(destName)){
			excludeAttKeys.addAll(reportBean.getAttCNList());
			excludeAttKeys.addAll(reportBean.getAttRUList());
			parentRowColumnsProperty.addAll(reportBean.getAttUAList());
		}

		for (String attrIntName : excludeAttKeys) { 
			fo.put(attrIntName,0);
		}
		return parentRowColumnsProperty;
	}

	
	
	/**
	 * if material/material-supplier diffrent from top row to overriden row values then don't merge the values from top row.
	 * @return boolean.
	 */
	private static boolean isMaterialChangeatOverridenrow(FlexObject fo, FlexObject actObj, String key){
		boolean isMaterialDiffOnOverrow=false;
		
		if(key.contains("LCSMATERIAL.")
				&& actObj.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")!=fo.getData("LCSMATERIAL.BRANCHIDITERATIONINFO")){
			isMaterialDiffOnOverrow=true;
		}
		if(key.contains("LCSMATERIALSUPPLIER.")
				&& actObj.getData("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO")!=fo.getData("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO")){
			isMaterialDiffOnOverrow=true;
		}
		if(key.contains("FABRIC.")
				&& actObj.getData("FABRIC.IDA2A2")!=fo.getData("FABRIC.IDA2A2")){
			isMaterialDiffOnOverrow=true;
		}
		if(key.contains("FABRIC_SUPPLIER.")
				&& actObj.getData("FABRIC_SUPPLIER.BRANCHIDITERATIONINFO")!=fo.getData("FABRIC_SUPPLIER.BRANCHIDITERATIONINFO")){
			isMaterialDiffOnOverrow=true;
		}
		
		
		return isMaterialDiffOnOverrow;
		
	}
	
	
}

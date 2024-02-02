package com.hbi.wc.product;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOACollectionClientModel;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLifecycleManagedQuery;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
/**
 * 
 * @author John S. Reeno
 * @since Jan-11-2019
 *  This class is used to populate values in MOA Sizing table in Product based on the unique size values
 *  in PSD and match it with the SizeXref values in the Business Object
 *
 */
public class HBISizingMOATablePlugin {
	public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
	public static final String BASIC_CUT_AND_SEW_SELLING = "BASIC CUT & SEW - SELLING";
	public static LCSLifecycleManaged sizeCategoryBO = null;
	public static LCSLifecycleManaged sizeXrefBO = null;

	private static final String SIZE_XREF_TYPE = LCSProperties
			.get("com.lcs.wc.product.HBISizingMOATablePlugin.sizexreftype");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void updateSizingMOATable(WTObject obj)throws WTException, WTPropertyVetoException{
		if(obj instanceof LCSProduct){

			LCSProduct prd = (LCSProduct)obj;
			LCSProduct prdPrev = (LCSProduct)VersionHelper.predecessorOf(prd);
			boolean doesStringcontainDigit = false;
			doesStringcontainDigit = containsDigit(prd);
			Map criteria = new HashMap();

			if(prd != null && prd.getFlexType().getTypeName().equals(BASIC_CUT_AND_SEW_GARMENT) ||
					prd.getFlexType().getTypeName().equals(BASIC_CUT_AND_SEW_SELLING)){	

				deleteModifiedMOARows(prd,prdPrev);
				//Get BO Size details
				Collection sizeUniqueColl = new ArrayList();
				sizeUniqueColl = getUniqueSizes(prd);
				FlexTypeAttribute ftypeAtt = prd.getFlexType().getAttribute("hbiGarmentSizeTable");
				LCSMOACollectionClientModel moaModel = new LCSMOACollectionClientModel();
				LCSLifecycleManaged sizeCategoryBO = (LCSLifecycleManaged)prd.getValue("hbiSellingSizeCategory");
				FlexType sizeRefTypeBO = FlexTypeCache.getFlexTypeFromPath(SIZE_XREF_TYPE);
				SearchResults srBO = null;
				if(sizeCategoryBO != null){
					if(sizeUniqueColl != null && sizeUniqueColl.size()>0){
						Iterator sizesItr = sizeUniqueColl.iterator();
						String apsSizeConcat = "";
						int iterCounter =0;
						while(sizesItr.hasNext()){
							String size = (String)sizesItr.next();
							apsSizeConcat = sizeCategoryBO.getIdentity()+" - "+size;
							criteria.put("quickSearchCriteria", apsSizeConcat);
							srBO = new LCSLifecycleManagedQuery().findLifecycleManagedsByCriteria(criteria,sizeRefTypeBO,null,null,null);
							if(srBO.getResultsFound()>0){
								Collection sizeRefColl = srBO.getResults();
								FlexObject sizeRefFlexObj = (FlexObject)sizeRefColl.iterator().next();
								sizeXrefBO = (LCSLifecycleManaged)LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+
										sizeRefFlexObj.getString("LCSLIFECYCLEMANAGED.IDA2A2"));
								if(sizeXrefBO != null){
									String plmPatternSize = (String)sizeXrefBO.getValue("hbiPLMSizeLiteral");
									String plmGarmentSize = (String)sizeXrefBO.getValue("hbiPLMGarmentSizeLiteral");
									String apsSizeCode = (String)sizeXrefBO.getValue("hbiAPSSizeCode");
									String s3Code = (String)sizeXrefBO.getValue("hbiS3Code");

									LCSMOATable sizingMOATable = (LCSMOATable) prd.getValue("hbiGarmentSizeTable");
									Collection<FlexObject> rowIdColl = new ArrayList<FlexObject>();
									if(sizingMOATable != null){
										rowIdColl =sizingMOATable.getRows();
									}
									int idInt=0;
									String data ="";
									if(rowIdColl.size()==0){
										idInt = 1;
										data = "sortingNumber"+"|&^&|"+idInt+"|-()-|"+
												"ID"+"|&^&|"+idInt+"|-()-|"+
												"DROPPED"+"|&^&|"+"false"+"|-()-|"+
												"hbiplmPatternSize"+"|&^&|"+plmPatternSize+"|-()-|"+
												"hbiGarmentSize"+"|&^&|"+plmGarmentSize+"|-()-|"+
												"hbiSizeCodeText"+"|&^&|"+apsSizeCode+"|-()-|"+
												"hbiS3CodeText"+"|&^&|"+s3Code+"|-()-|"+"|!#!|";

										if(doesStringcontainDigit){
											//	moaModel.load(FormatHelper.getObjectId(prd),FormatHelper.getObjectId(ftypeAtt));
											//Wipro Team Upgrade
											moaModel.load(FormatHelper.getObjectId(prd),ftypeAtt.getAttKey());
											moaModel.updateMOACollection(data);
										}
									}else if(rowIdColl.size() > 0){
										idInt = rowIdColl.size()+iterCounter;
										boolean dupMOARowExists =checkMOArowAlreadyExists(rowIdColl,sizeXrefBO);
										if(!dupMOARowExists){
											data = "sortingNumber"+"|&^&|"+idInt+"|-()-|"+
													"ID"+"|&^&|"+idInt+"|-()-|"+
													"DROPPED"+"|&^&|"+"false"+"|-()-|"+
													"hbiplmPatternSize"+"|&^&|"+plmPatternSize+"|-()-|"+
													"hbiGarmentSize"+"|&^&|"+plmGarmentSize+"|-()-|"+
													"hbiSizeCodeText"+"|&^&|"+apsSizeCode+"|-()-|"+
													"hbiS3CodeText"+"|&^&|"+s3Code+"|-()-|"+"|!#!|";
										}
										if(doesStringcontainDigit){
											//moaModel.load(FormatHelper.getObjectId(prd),FormatHelper.getObjectId(ftypeAtt));
											//Wipro Team Upgrade
											moaModel.load(FormatHelper.getObjectId(prd),ftypeAtt.getAttKey());
											moaModel.updateMOACollection(data);
										}
									}
								}
							}
							iterCounter++;
						}
						
						//Added for HBI Sort MOA Sizing table Start - 30-1-2019
						LCSMOATable sizingMOATab = (LCSMOATable) prd.getValue("hbiGarmentSizeTable");
						Collection<FlexObject> rowColl = new ArrayList<FlexObject>();
						if(sizingMOATab != null){
							rowColl=sizingMOATab.getRows();
							if(rowColl.size()>0)
							sortMOATable(sizeUniqueColl,rowColl);
						}
						//Added for HBI Sort MOA Sizing table End - 30-1-2019
					}
				}	
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static Collection getUniqueSizes(LCSProduct prd) throws WTException{
		Set<String> uniqueSize = new LinkedHashSet<String>();
		if(prd != null){
			SearchResults sr = SizingQuery.findProductSizeCategoriesForProduct(prd);

			if (sr != null && sr.getResultsFound()>0){
				Collection sizeCatColl = sr.getResults();
				Collection <String> sizesPD = new ArrayList<String>();
				StringBuffer sizeAppend = new StringBuffer();
				Iterator sizeCatItr = sizeCatColl.iterator();
				while(sizeCatItr.hasNext()){
					FlexObject fob = (FlexObject)sizeCatItr.next();
					String sizeValues = "";
					if(fob != null){
						sizeValues = fob.getString("PRODUCTSIZECATEGORY.SIZEVALUES");
					}
					if(FormatHelper.hasContent(sizeValues)){
						sizeAppend.append(sizeValues);

						if(sizeAppend.length()>0){
							StringTokenizer stToken = new StringTokenizer(sizeAppend.toString(),"|~*~|");
							while(stToken.hasMoreTokens()){
								sizesPD.add(stToken.nextToken());
							}
						}
					}
				}

				//remove Duplicates
				uniqueSize.addAll(sizesPD);
			}
		}
		List<String> list = new ArrayList<String>(uniqueSize);
		return list;
	}
	@SuppressWarnings("rawtypes")
	public static boolean checkMOArowAlreadyExists(Collection <FlexObject> rowIdColl, LCSLifecycleManaged sizeXrefBO) throws WTException{
		String plmPatternSize = "";
		plmPatternSize = (String)sizeXrefBO.getValue("hbiPLMSizeLiteral");
		String plmGarmentSize = "";
		plmGarmentSize = (String)sizeXrefBO.getValue("hbiPLMGarmentSizeLiteral");
		String apsSizeCode = ""; 
		apsSizeCode = (String)sizeXrefBO.getValue("hbiAPSSizeCode");
		String s3Code = ""; 
		s3Code = (String)sizeXrefBO.getValue("hbiS3Code");
		String name=sizeXrefBO.getName();
		boolean status=false;
		for(FlexObject flexObj : rowIdColl)
		{
			String patSizeFO = flexObj.getString("HBIPLMPATTERNSIZE");
			String garSizeFO = flexObj.getString("HBIGARMENTSIZE");
			String sizeCodeFO = flexObj.getString("HBISIZECODETEXT");
			String s3CodeFO = flexObj.getString("HBIS3CODETEXT");
			Map <String,String> map = new HashMap<String,String>();
			map.put("plmPatternSize", plmPatternSize);
			map.put("plmGarmentSize", plmGarmentSize);
			map.put("apsSizeCode", apsSizeCode);
			map.put("s3Code", s3Code);
			map.put("patSizeFO", patSizeFO);
			map.put("garSizeFO", garSizeFO);
			map.put("sizeCodeFO", sizeCodeFO);
			map.put("s3CodeFO", s3CodeFO);

			Map modMap = addNullLiteralForEmptyString(map);

			plmPatternSize = (String)modMap.get("plmPatternSize");
			plmGarmentSize = (String)modMap.get("plmGarmentSize");
			apsSizeCode = (String)modMap.get("apsSizeCode");
			s3Code = (String)modMap.get("s3Code");
			patSizeFO = (String)modMap.get("patSizeFO");
			garSizeFO = (String)modMap.get("garSizeFO");
			sizeCodeFO = (String)modMap.get("sizeCodeFO");
			s3CodeFO = (String)modMap.get("s3CodeFO");
			
			if(FormatHelper.hasContent(name)&&name.contains("HEI")) {
				
					
					if	((plmPatternSize.equals(patSizeFO) &&
							plmGarmentSize.equals(garSizeFO)&&
							apsSizeCode.equals(sizeCodeFO)&&
							s3Code.equals(s3CodeFO)))
				{
					status = true;
				}

				
			}

			else 
				{
				if
				((plmPatternSize.equals(patSizeFO) &&
					plmGarmentSize.equals(garSizeFO)&&
					apsSizeCode.equals(sizeCodeFO))){

				status = true;

			}
				}
		}
		return status;
	}

	public static Map<String,String> addNullLiteralForEmptyString(Map<String,String> mapString) throws WTException{
		Map<String,String> nullAdded = new HashMap<String,String>();
		String plmPatternSize = (String)mapString.get("plmPatternSize");
		String plmGarmentSize = (String)mapString.get("plmGarmentSize");
		String apsSizeCode = (String)mapString.get("apsSizeCode");
		String s3Code = (String)mapString.get("s3Code");
		String patSizeFO = (String)mapString.get("patSizeFO");
		String garSizeFO = (String)mapString.get("garSizeFO");
		String sizeCodeFO = (String)mapString.get("sizeCodeFO");
		String s3CodeFO = (String)mapString.get("s3CodeFO");


		if(!FormatHelper.hasContent(plmPatternSize)){
			plmPatternSize = "null";
		}
		if(!FormatHelper.hasContent(plmGarmentSize)){
			plmGarmentSize = "null";
		}

		if(!FormatHelper.hasContent(apsSizeCode)){
			apsSizeCode = "null";
		}
		if(!FormatHelper.hasContent(s3Code)){
			s3Code = "null";
		}		
		if(!FormatHelper.hasContent(patSizeFO)){
			patSizeFO = "null";
		}

		if(!FormatHelper.hasContent(garSizeFO)){
			garSizeFO = "null";
		}

		if(!FormatHelper.hasContent(sizeCodeFO)){
			sizeCodeFO = "null";
		}
		if(!FormatHelper.hasContent(s3CodeFO)){
			s3CodeFO = "null";
		}
		nullAdded.put("plmPatternSize", plmPatternSize);
		nullAdded.put("plmGarmentSize", plmGarmentSize);
		nullAdded.put("apsSizeCode", apsSizeCode);
		nullAdded.put("s3Code", s3Code);
		nullAdded.put("patSizeFO", patSizeFO);
		nullAdded.put("garSizeFO", garSizeFO);
		nullAdded.put("sizeCodeFO", sizeCodeFO);
		nullAdded.put("s3CodeFO", s3CodeFO);

		return nullAdded;
	}

	public static void refreshMOATable(LCSProduct prd) throws WTException {
		LCSMOATable moaTable = (LCSMOATable) prd.getValue("hbiGarmentSizeTable");
		Collection<FlexObject> rowIdColl =moaTable.getRows();	
		if(rowIdColl != null && rowIdColl.size() >= 0)
		{
			for(FlexObject flexObj : rowIdColl)
			{
				String key = flexObj.getString("OID");
				LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
				new LCSMOAObjectLogic().delete(moaObject);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void deleteModifiedMOARows(LCSProduct prd, LCSProduct prdPrev) throws WTException{
		//Get PSD Sizes

		SearchResults srBO = new SearchResults();
		LCSLifecycleManaged sizeXrefBO = new LCSLifecycleManaged();
		LCSLifecycleManaged sizeCategoryBO = (LCSLifecycleManaged)prd.getValue("hbiSellingSizeCategory");
		String sizeCategoryStr = "";
		String sizeCategoryStrPrev = "";
		if(sizeCategoryBO != null){
			sizeCategoryStr = sizeCategoryBO.getIdentity();
			if(!FormatHelper.hasContent(sizeCategoryStr)) sizeCategoryStr = "null";
		}
		if(prdPrev != null){
			LCSLifecycleManaged sizeCategoryBOPrev = (LCSLifecycleManaged)prdPrev.getValue("hbiSellingSizeCategory");
			if(sizeCategoryBOPrev != null){
				sizeCategoryStrPrev = sizeCategoryBOPrev.getIdentity();
				if(!FormatHelper.hasContent(sizeCategoryStrPrev)) sizeCategoryStrPrev = "null";
			}
		}
		FlexType sizeRefTypeBO = FlexTypeCache.getFlexTypeFromPath(SIZE_XREF_TYPE);
		Map criteria = new HashMap();

		Collection sizeUniqueColl = new ArrayList();
		sizeUniqueColl = getUniqueSizes(prd);

		LCSMOATable sizingMOATable = (LCSMOATable) prd.getValue("hbiGarmentSizeTable");
		Collection<FlexObject> rowIdColl = new ArrayList<FlexObject>();
		if(sizingMOATable != null){
			rowIdColl =sizingMOATable.getRows();
		}
		LCSLifecycleManaged sizeCat = (LCSLifecycleManaged)prd.getValue("hbiSellingSizeCategory");
		String apsSizeConcat;
		if (((sizeCategoryBO == null) || (sizeUniqueColl.size()<1)) && (rowIdColl != null && rowIdColl.size() > 0)){

			for(FlexObject flexObj : rowIdColl)
			{
				String key = flexObj.getString("OID");
				LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
				new LCSMOAObjectLogic().delete(moaObject);
			}


		} else

			if(!sizeCategoryStr.equals(sizeCategoryStrPrev) && (rowIdColl != null && rowIdColl.size() > 0)){
				for(FlexObject flexObj : rowIdColl)
				{
					String key = flexObj.getString("OID");
					LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
					new LCSMOAObjectLogic().delete(moaObject);
				}

			}else

				if(rowIdColl != null && rowIdColl.size() >= 0)
				{
					for(FlexObject flexObj : rowIdColl)
					{
						String key = flexObj.getString("OID");
						LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);

						String garmentSizeMOA = (String)moaObject.getValue("hbiGarmentSize");
						if(!FormatHelper.hasContent(garmentSizeMOA)){
							garmentSizeMOA = "null";
						}
						String apsSizeCodeMOA = (String)moaObject.getValue("hbiSizeCodeText");
						if(!FormatHelper.hasContent(apsSizeCodeMOA)){
							apsSizeCodeMOA = "null";
						}
						String s3CodeMOA = (String)moaObject.getValue("hbiS3CodeText");
						if(!FormatHelper.hasContent(s3CodeMOA)){
							s3CodeMOA = "null";
						}						
						String plmSizeMOA = (String)moaObject.getValue("hbiplmPatternSize");
						if(!FormatHelper.hasContent(plmSizeMOA)){
							plmSizeMOA = "null";
						}
						boolean isSizeAvailable = findAvailableSizes(plmSizeMOA, sizeUniqueColl);
						if(!isSizeAvailable){
							new LCSMOAObjectLogic().delete(moaObject);
						}
						if(sizeCat != null){
							apsSizeConcat = sizeCat.getIdentity() +" - "+plmSizeMOA; 
							criteria.put("quickSearchCriteria", apsSizeConcat);
							srBO = new LCSLifecycleManagedQuery().findLifecycleManagedsByCriteria(criteria,sizeRefTypeBO,null,null,null);
							if(srBO.getResultsFound()>0){
								Collection sizeRefColl = srBO.getResults();
								FlexObject sizeRefFlexObj = (FlexObject)sizeRefColl.iterator().next();
								sizeXrefBO = (LCSLifecycleManaged)LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:"+
										sizeRefFlexObj.getString("LCSLIFECYCLEMANAGED.IDA2A2"));
								if(sizeXrefBO != null){
									String garmentSizeBO = (String)sizeXrefBO.getValue("hbiPLMGarmentSizeLiteral");
									if(!FormatHelper.hasContent(garmentSizeBO)){
										garmentSizeBO = "null";
									}
									String apsSizeCodeBO = (String)sizeXrefBO.getValue("hbiAPSSizeCode");
									if(!FormatHelper.hasContent(apsSizeCodeBO)){
										apsSizeCodeBO = "null";
									}
									String s3CodeBO = (String)sizeXrefBO.getValue("hbis3Code");
									if(!FormatHelper.hasContent(s3CodeBO)){
										s3CodeBO = "null";
									}									
									String plmSizeBO = (String)sizeXrefBO.getValue("hbiPLMSizeLiteral");
									if(!FormatHelper.hasContent(plmSizeBO)){
										plmSizeBO = "null";
									}
									if(!(garmentSizeMOA.equals(garmentSizeBO) && apsSizeCodeMOA.equals(apsSizeCodeBO) &&
											plmSizeMOA.equals(plmSizeBO))){
										new LCSMOAObjectLogic().delete(moaObject);
									}
								}
							}
						}
					}
				}
	}
	public static boolean findAvailableSizes(String str, Collection<String> sizeColl){
		boolean sizeExists=false;
		for(String size: sizeColl){
			if(str.equals(size)){
				sizeExists=true;
			}
		}
		return sizeExists;
	}

	public static final boolean containsDigit(LCSProduct prd) {
		boolean containsDigit = false;
		String prdStr = prd.toString();
		prdStr = prdStr.substring(0,prdStr.indexOf("Identity:"));
		if (FormatHelper.hasContent(prdStr)) {
			for (char c : prdStr.toCharArray()) {
				if (containsDigit = Character.isDigit(c)) {
					break;
				}
			}
		}

		return containsDigit;
	}
	/**
	 * @author John Reeno
	 * @date 31-1-2019
	 * @method This method is used to sort the Sizing MOA table based on the Size Definition sort order
	 * @param sizeUniqueColl
	 * @param rowIdColl
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void sortMOATable(Collection<String> sizeUniqueColl,Collection<FlexObject>rowIdColl) throws WTException, WTPropertyVetoException{
		int count=0;
		for(String siz:sizeUniqueColl){
			for(FlexObject flx:rowIdColl){
				if(siz.equals(flx.getString("HBIPLMPATTERNSIZE"))){
					String key = flx.getString("OID");
					LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
					if(moaObject!=null){
					moaObject.setSortingNumber(count);
					LCSMOAObjectLogic.persist(moaObject,true);
					count++;
					}
				}
			}
		}
	}
}

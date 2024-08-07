/** SMCostSheetBOMPlugin.java
 * Created on April 23, 2013, 11:00 AM
 *
 */

package com.sportmaster.wc.costsheet;

import org.apache.log4j.Logger;

import com.lcs.wc.client.web.TableData;
import com.lcs.wc.client.web.TableDataUtil;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFindFlexBOMHelper;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.MaterialPriceList;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.product.ReferencedTypeKeys;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sourcing.LCSCostSheetMaster;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.*;
import com.lcs.wc.util.json.JSONHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.fc.WTObject;
import wt.fc.PersistenceServerHelper;
import wt.method.MethodContext;
import wt.part.WTPartMaster;
import wt.util.WTException;

import java.util.*;

/** Popolates the BOM section total to cost sheet.
 * @author Prabhaker
 *
 */
public class SMCostSheetBOMPlugin

{
	private static final String BOMRollUp = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.BOMRollUp", "vrdDoBOMRollup");
	private static final String BOMRollUpDate = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.BOMRollUpDate", "vrdDateBOMRollup");
	private static final String BOMSection = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.BOMSection", "section");
	private static final String MaterialName = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.MaterialName", "name");
	private static final String SampleMaterialPrice = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.SampleMaterialPrice", "vrdSampleMaterialPrice");
	private static final String MaterialPrice = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.MaterialPrice");
	private static final String ColorSpecificPrice = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.ColorSpecificPrice");
	private static final String PriceOverride = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.PriceOverride", "priceOverride");
	private static final String MaterialQuantity = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.MaterialQuantity", "quantity");
	private static final String LossAdjustment = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.LossAdjustment", "lossAdjustment");
	private static final String MarkUp = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.MarkUp", "markUp");
	private static final String EFFECTIVE_DATE_ATT = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.EffectiveDate", "vrdEffectiveDate");
    private static final String DEFAULT_EFFECTIVE_DATE_ATT = LCSProperties.get("com.vrd.reports.PublishBOMPlugin.DefaultEffectiveDate","Season.vrdEffectiveDate");
	private static final String BOMRefName = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.BOMReference", "vrdBOMReference");
	private static final String BOMTotal = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.BOMTotal", "vrdBOMTotal");
	private static final String CIFPERCENT = LCSProperties.get("com.sportmaster.BOMLink.CIFPercent", "smCIFPercent");
	//private static final String CIFPRICE = LCSProperties.get("com.sportmaster.BOMLink.CIFPrice", "smCIFPrice");

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMCostSheetBOMPlugin.class);
	
	/** Method to poulate the BOM section total to cost sheet.
	 * @param wtobject
	 * @return
	 * @throws WTException
	 */
	public static final WTObject populateBOMSectionTotalToCostSheet(WTObject wtobject)
			throws WTException
	{
		LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: START ("+wtobject+")");
		LCSProductCostSheet lcscostsheet = null;
		if(wtobject instanceof LCSProductCostSheet) {
			lcscostsheet = (LCSProductCostSheet)wtobject;
		} else {
			throw new WTException("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: Object is not instance of LCSProductCostSheet");
		}
		// BOM Rollup Calculations

		FlexType cType = lcscostsheet.getFlexType();
		boolean bomRollUp = false;
		if(cType.getAttributeKeyList().contains(BOMRollUp.toUpperCase())){
			if(lcscostsheet.getValue(BOMRollUp) != null) {
				bomRollUp = ((Boolean)lcscostsheet.getValue(BOMRollUp)).booleanValue();
			}
		}
      		
		if(bomRollUp)
		{
			try 
			{
				LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: Executing BOMRollup -- ");
				StringBuilder buf = new StringBuilder();
				// DETERMINE DEFAULT EFFECTIVE DATE
				Date effectiveDate = null;
				try {
					if(FormatHelper.hasContent(DEFAULT_EFFECTIVE_DATE_ATT) && lcscostsheet.getValue(EFFECTIVE_DATE_ATT) == null) {
						StringTokenizer st = new StringTokenizer(DEFAULT_EFFECTIVE_DATE_ATT,".");
						String attType = st.nextToken();
						String attKey = st.nextToken();
						if("Season".equals(attType)) {
							LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(lcscostsheet.getSeasonMaster());
							effectiveDate = (Date) season.getValue(attKey);
						} else if("SC".equals(attType)) {
							LCSSourcingConfig sourceConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(lcscostsheet.getSourcingConfigMaster());
							effectiveDate = (Date) sourceConfig.getValue(attKey);
						} else if("Product".equals(attType)) {
							LCSProduct productRevA = (LCSProduct) VersionHelper.getVersion(lcscostsheet.getProductMaster(), "A");
							effectiveDate = (Date) productRevA.getValue(attKey);
						} else if("Product-Season".equals(attType)) {
							LCSProduct productRevA = (LCSProduct) VersionHelper.getVersion(lcscostsheet.getProductMaster(), "A");
							LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(lcscostsheet.getSeasonMaster());
							LCSSeasonProductLink productLink = LCSSeasonQuery.findSeasonProductLink(productRevA,season);
							effectiveDate = (Date) productLink.getValue(attKey);
						}
						if(effectiveDate != null) {
							lcscostsheet.setValue(EFFECTIVE_DATE_ATT,effectiveDate);
						}
					}
					effectiveDate = (Date) lcscostsheet.getValue(EFFECTIVE_DATE_ATT);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(effectiveDate == null) {
					effectiveDate = new Date();
					buf.append("\n -- No effective Date Specified.  Using Today's Date \n");
				}
				// Get the Specification
				FlexSpecification spec = null;
				if(lcscostsheet.getSpecificationMaster() != null){
					spec = (FlexSpecification)VersionHelper.latestIterationOf(lcscostsheet.getSpecificationMaster());
					buf.append(" -- SpecMaster Found : "+spec.getName());
				}
				else	{
					buf.append(" -- Spec is Null.  No more rollup.  Exit. -- \n");
					LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: -- END -- \n"+buf);
					return lcscostsheet;
				}
				
				// Get the SourcingConfig 
				String scMasterId = "";
				if(lcscostsheet.getSourcingConfigMaster()!=null) {
					scMasterId = FormatHelper.getObjectId(lcscostsheet.getSourcingConfigMaster());
					buf.append(" -- Sourcing Config Found: "+lcscostsheet.getSourcingConfigMaster().getSourcingConfigName()+"\n");
				} 
				else	 {
					buf.append(" -- SourcingConfig is Null.  No more rollup.  Exit. -- \n");
					LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: -- END -- \n"+buf);
					return lcscostsheet;
				}

				String skuMasterId = "";
				Map csDimLinks = null;
				String repColorId = "";
				String destDimId = "";
				try 
				{
					csDimLinks = (Map)MethodContext.getContext().get("COSTSHEET_DIM_LINKS");					
					if(csDimLinks != null)	{
						// Colorway
						repColorId  = (String) csDimLinks.get("REPCOLOR");
						// Destination
						destDimId = (String) csDimLinks.get("REPDESTINATION"); 
					}
					else 	{
						
						///////////////////////////////////////////////////////////////////////////////////////
						// Code added to fix the issue with BOM CostSheet Roll-up.  
						//  Original code would ignore if "COSTSHEET_DIM_LINKS" wasn't available
						//  Fix now uses the representativeColor and representativeDimension
						//
						//  Author: Kiran  (2014-05-20)						
						///////////////////////////////////////////////////////////////////////////////////////
						buf.append(" -- No CostSheet Dimensions available from the Method Context. -- \n");
						// Representative Color during Update
						Collection representativeColorCol = LCSCostSheetQuery.getRepresentativeColor((LCSCostSheetMaster)lcscostsheet.getMaster());
					    Iterator repColorColIter = representativeColorCol.iterator();
					    FlexObject fo = null;
					    while(repColorColIter.hasNext()) {
					    	fo = (FlexObject)repColorColIter.next();
					        repColorId = "VR:com.lcs.wc.product.LCSSKU:" + fo.getString("LCSSKU.BRANCHIDITERATIONINFO");
					    }
						buf.append(" -- CostSheet Colors: RepColorId"+repColorId+"\n");
						
						// Representative Destination
						 Collection representativeDestCol = LCSCostSheetQuery.getRepresentativeDestination((LCSCostSheetMaster)lcscostsheet.getMaster());
						 Iterator repDestColIter = representativeDestCol.iterator();
						 while(repDestColIter.hasNext()) {
							 fo = (FlexObject)repDestColIter.next();
							 destDimId = "OR:com.lcs.wc.product.ProductDestination:" + fo.getString("PRODUCTDESTINATION.IDA2A2");
						 }
						 ///////////////////////////////////////////////////////////////////////////////////////
						 //  END -- Fix
						 ///////////////////////////////////////////////////////////////////////////////////////
					}
					if(FormatHelper.hasContent(repColorId)) {
						LCSSKU sku = (LCSSKU) LCSQuery.findObjectById(repColorId);
						if(sku!=null) {
							skuMasterId = FormatHelper.getObjectId((WTPartMaster)sku.getMaster());
						}
					}
				} catch (Exception e) {
					buf.append(" *** ERROR *** while trying to acess CostSheet__Dimension__Links or the SKU \n");
					LOGGER.error(e);
				} 

				// Sizing
				String size1 = FormatHelper.format(lcscostsheet.getRepresentativeSize());
				String size2 = FormatHelper.format(lcscostsheet.getRepresentativeSize2());
				buf.append(" -- Sizes :: "+size1+" -- Size 2: "+size2+"\n");
              
				// BOM Roll-up
				FlexType materialType = FlexTypeCache.getFlexTypeFromPath("Material");
				String timestamp = null;
				String section;
				HashMap sectionMap = null;
				FlexObject sectionObj;
				String bomName = "";
				LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: "+buf.toString());
				
				//Null check for Spec
			 	if(spec != null) {				   
					//Referenced BOM Value
					String strBOMName = (String)lcscostsheet.getValue(BOMRefName);
					LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: -- BOM String:: "+strBOMName);
					
					//Find the Referenced BOM Object linked with the selected Spec and BOM 
					FlexBOMPart flexrefBompart = SMCostSheetBOMPlugin.findSpecRefBOMObj(spec,strBOMName);
					LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: -- BOM Found:: "+flexrefBompart);
					
				   //Null check for the BOM object  
					if(flexrefBompart != null) {  
						FlexType bomType = flexrefBompart.getFlexType();
						
						// Condition to check if BOM is type LABOR and the BOM is checked out
						if(!"LABOR".equals(flexrefBompart.getBomType()))
						{
							if(VersionHelper.isCheckedOutByUser(flexrefBompart))
							{
								// If yes, then get the last version of the same
								FlexBOMPart flexbompartOld  = (FlexBOMPart) VersionHelper.latestIterationOf(flexrefBompart);
								// making sure that it's not working copy
								if(!VersionHelper.isWorkingCopy(flexbompartOld))
									flexrefBompart = flexbompartOld  ;          
							}

							FlexTypeAttribute sectionAtt = bomType.getAttribute(BOMSection);
							bomName = flexrefBompart.getName();
							Collection usedAttList = Arrays.asList(PriceOverride, MaterialQuantity, MaterialPrice, SampleMaterialPrice, LossAdjustment, MarkUp, ColorSpecificPrice, BOMSection, CIFPERCENT);
							Collection multiLevelData = LCSFindFlexBOMHelper.findBOM(flexrefBompart, skuMasterId, scMasterId, size1, size2, destDimId, LCSFlexBOMQuery.EFFECTIVE_ONLY, timestamp, true, materialType, usedAttList);
							Collection bomData = groupDataToBranchId(multiLevelData, bomType);
							/// GET SUMMARY DATA
							Map dataMap = TableDataUtil.groupIntoCollections(bomData, BOMSection);
							AttributeValueList sectionAttList = bomType.getAttribute(BOMSection).getAttValueList();
							Iterator sectionIter = sectionAttList.getKeys().iterator();
							sectionMap = new HashMap();
							while (sectionIter.hasNext()) {
								section = (String) sectionIter.next();
								if (sectionMap.get(section) == null) {
									sectionObj = new FlexObject();
								} else {
									sectionObj = (FlexObject) sectionMap.get(section);
								}

								if (sectionAtt.getAttValueList().isSelectable(section)) {
									sectionObj.put("display", sectionAtt.getDisplayValue(section));
									Double sectionTotal = 0d;
									if (sectionObj.get("totalCost") != null) {
										sectionTotal = FormatHelper.parseDouble((String) sectionObj.get("totalCost"));
									}

									Collection sectionCol = (Collection) dataMap.get(section);
									if (sectionCol != null) {
										sectionObj.put("totalCost", sectionTotal + calculateBOMRollUpTotal(flexrefBompart, sectionCol, effectiveDate));
									}
									if (sectionObj.get("sortingNumber") == null) {
										sectionObj.put("sortingNumber", bomType.getAttribute(BOMSection).getAttValueList().get(section, "ORDER"));
									}
									sectionMap.put(section, sectionObj);
								}
							}
						}//End of Labour condition
						else{
							LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: -- Bills of Labor are Ignored -- ");
							lcscostsheet.setValue(BOMRollUp,false);							
						}				   
					} //If the BOM is not null
					
					lcscostsheet.setValue(BOMRollUp,false);  // No Roll up if no BOM selected
				} //If Spec is not null
				else{
					lcscostsheet.setValue(BOMRollUp,false); // No Roll up if no Spec selected
				}				
                
                if(sectionMap != null) {  				
					/// SETTING BOM SECTIONS TO SPECIFIC ATTRIBUTES
					Iterator sectionIter = sectionMap.keySet().iterator();
					Double bomTotal = 0d;
					while(sectionIter.hasNext()) {
						section = (String) sectionIter.next();
						sectionObj = (FlexObject) sectionMap.get(section);
						if(sectionObj != null && bomRollUp) {
							try {
								LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: The Section total is:"+sectionObj.get("totalCost"));
								bomTotal +=FormatHelper.parseDouble((String)sectionObj.get("totalCost"));
								lcscostsheet.setValue(section,""+sectionObj.get("totalCost"));
								lcscostsheet.setValue(BOMRollUpDate,new Date());
								lcscostsheet.setValue(BOMRefName,bomName);
								lcscostsheet.setValue(BOMRollUp,false);
							} catch (Exception e) {
								LOGGER.error(e);
							}
						}
					}
					lcscostsheet.setValue(BOMTotal,bomTotal);
               } //If the section map is not null
				else
				{ //If the SectionMap is null , the roll up is not done and will not reset the values for sections
				  lcscostsheet.setValue(BOMRollUp,false);
				}				
			} catch(Exception e) {
				LOGGER.error(e);
			}
		} // if BOM roll up is true
		LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: FINISH.");
		
		// 2015-01-23:  Adding logic to run CostSheet calculations 
		// Issue:  FSS-20
		// Trigger derive plugins 
		LCSLogic.deriveFlexTypeValues(lcscostsheet, false);
		return lcscostsheet;
	}

	/**
	 * Method to calculate section Total for each section.
	 *
	 * @param bomPart
	 * @param bomData
	 * @param effDate
	 * @return
	 * @throws WTException
	 */
	public static double calculateBOMRollUpTotal(FlexBOMPart bomPart, Collection bomData, Date effDate)
			throws WTException {
		Iterator bomIter = bomData.iterator();
		TableData branch;
		double materialPrice;
		double materialSupPrice;
		double materialColorPrice;
		double mpePrice;
		double quantity;
		double topLevelQuantity = 1;
		double lossAdjustment;
		double priceOverride;
		//SPORTMASTER EXTENSION OF VRD START
		double cifPrice;
		double cifPercent;
        //SPORTMASTER EXTENSION OF VRD END
		String materialSupplierMasterId = "";
		String materialColorId;
		String markUp;

		Collection matSups = new ArrayList();
		Collection matSupColors = new ArrayList();

		Map matSup = null;
		Map matSupColor = null;
		Map<String, FlexObject> complexLinksTopRows = getComplexLinksTopRows(bomData);

		FlexType bomType = bomPart.getFlexType();
		FlexType matType = bomType.getReferencedFlexType(ReferencedTypeKeys.MATERIAL_TYPE);
		FlexType matColorType = FlexTypeCache.getFlexTypeFromPath("Material Color");

		while (bomIter.hasNext()) {
			branch = (TableData) bomIter.next();
			matSupColor = new HashMap();
			matSup = new HashMap();
			materialColorId = FormatHelper.format(branch.getData("MATERIALCOLORID"));
			materialSupplierMasterId = FormatHelper.format(branch.getData("MATERIALSUPPLIERMASTERID"));
			matSup.put("materialSupplierMasterId", materialSupplierMasterId);
			matSupColor.put("materialSupplierMasterId", materialSupplierMasterId);
			matSupColor.put("materialColorId", materialColorId);
			matSups.add(matSup);
			matSupColors.add(matSupColor);
		}
		double rowTotal, bomTotal = 0d;
		MaterialPriceList mpl = new MaterialPriceList(matSups, matSupColors, effDate);
		bomIter = bomData.iterator();
		while (bomIter.hasNext()) {
			branch = (TableData) bomIter.next();
			//VRD-176 start
			topLevelQuantity = 1;
			//VRD-176 end
			if (!isMasterBranch(branch) && complexLinksTopRows.containsKey(branch.getData("MASTERBRANCHID"))) {
				FlexObject topBranch = complexLinksTopRows.get(branch.getData("MASTERBRANCHID"));
				topLevelQuantity = topBranch.getDouble(MaterialQuantity);
				if (hasPriceValue(topBranch, bomType, matType, matColorType, mpl)) {
					topLevelQuantity = 0;
				}
			}
			materialPrice = FormatHelper.parseDouble(branch.getData(SampleMaterialPrice));
			materialSupPrice = FormatHelper.parseDouble(branch.getData(MaterialPrice));
			materialColorPrice = FormatHelper.parseDouble(branch.getData(ColorSpecificPrice));
			priceOverride = FormatHelper.parseDouble(branch.getData(PriceOverride));
			
			//SPORTMASTER EXTENSION OF VRD START
			cifPercent = FormatHelper.parseDouble(branch.getData(CIFPERCENT));
	                //SPORTMASTER EXTENSION OF VRD END

			quantity = topLevelQuantity * FormatHelper.parseDouble(branch.getData(MaterialQuantity));
			lossAdjustment = FormatHelper.parseDouble(branch.getData(LossAdjustment));
			materialColorId = FormatHelper.format(branch.getData("MATERIALCOLORID"));
			materialSupplierMasterId = FormatHelper.format(branch.getData("MATERIALSUPPLIERMASTERID"));
			markUp = FormatHelper.format(branch.getData(MarkUp));
			mpePrice = mpl.getPrice(materialSupplierMasterId, materialColorId);
			
			// SPORTMASTER Customization for Prive Override - START
			materialPrice = mpePrice;

			/*if(materialSupPrice > 0d) {
				materialPrice = materialSupPrice;
			}
			if(mpePrice > 0d){
				materialPrice = mpePrice;
			}
			if(materialColorPrice > 0d){
				materialPrice = materialColorPrice;
			}*/
			// SPORTMASTER Customization for Prive Override - END

			if(priceOverride > 0d){
				materialPrice = priceOverride;
			}
			
			
			//SPORTMASTER EXTENSION OF VRD START
			cifPrice = materialPrice*(1.0d+(cifPercent/100));
           
			
			if(lossAdjustment > 0d){
				quantity = quantity * (1.0d + lossAdjustment/100);
			}
			
			
			LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: The materialPrice is:"+materialPrice);
			rowTotal = quantity * cifPrice;
			
			//SPORTMASTER EXTENSION OF VRD END
			
			if(!"delete".equals(markUp)) {
				bomTotal += rowTotal;
			}
		}
			LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.populateBOMSectionTotalToCostSheet: The bomTotal is:"+bomTotal);

		return bomTotal;
		}

	private static boolean isMasterBranch(TableData branch) {
		return "1".equals(branch.getData("MASTERBRANCH")) || "1".equals(branch.getData("LINKEDBOM"));
	}

	private static boolean hasPriceValue(TableData branch, FlexType bomType, FlexType matType, FlexType matColorType, MaterialPriceList mpl) throws WTException {
		double materialPrice = FormatHelper.parseDouble(branch.getData(SampleMaterialPrice));
		double materialSupPrice = FormatHelper.parseDouble(branch.getData(MaterialPrice));
		double materialColorPrice = FormatHelper.parseDouble(branch.getData(ColorSpecificPrice));
		double priceOverride = FormatHelper.parseDouble(branch.getData(PriceOverride));
		String materialColorId = FormatHelper.format(branch.getData("MATERIALCOLORID"));
		String materialSupplierMasterId = FormatHelper.format(branch.getData("MATERIALSUPPLIERMASTERID"));
		double mpePrice = mpl.getPrice(materialSupplierMasterId, materialColorId);
		return materialSupPrice > 0d || mpePrice > 0d || materialColorPrice > 0d || priceOverride > 0d || materialPrice > 0d;
	}

	private static Map<String, FlexObject> getComplexLinksTopRows(Collection sectionData) {
		HashMap<String, FlexObject> map = new HashMap();
		FlexObject row;
		for (Object aSectionData : sectionData) {
			row = (FlexObject) aSectionData;
			if (FormatHelper.hasContent(row.getData("MASTERBRANCH")) || FormatHelper.hasContent(row.getData("LINKEDBOM"))) {
				if (FormatHelper.hasContent(row.getData("BRANCHID")))
					map.put(row.getData("BRANCHID"), row);
			}
		}
		return map;
	}

	/**
	 * Grouping the data. Using logic same as in ViewBOM.jsp.
	 *
	 * @param data
	 * @param type
	 * @return
	 * @throws WTException
	 */
	public static final Collection groupDataToBranchId(Collection data, FlexType type) throws WTException {
		if (data == null || data.size() < 1) {
			return new Vector();
		}
		Map table = TableDataUtil.groupIntoCollections(data, "masterBranchId");
		Vector groupedVector = new Vector();
		List current = (List) table.get("0");
		if(current == null){
			return new Vector();
		}
		//groupedVector.addAll(sortTopLevel(current, type));
		current = new Vector(sortTopLevel(current, type));
		table.remove("0");
		Set keys = table.keySet();
		for (int i = 0; i < current.size(); i++) {
			TableData td = (TableData) current.get(i);
			groupedVector.add(td);
			String value = td.getData("branchId");
			if (keys.contains(value)) {
				//LC: Sort the sub bom items by partName before adding to the vector
				Collection colSubBranches = (Collection) table.get(value);
				colSubBranches = SortHelper.sortFlexObjectsByNumber(colSubBranches, "FLEXBOMLINK.SORTINGNUMBER");
				groupedVector.addAll(colSubBranches);
			}
		}
		return groupedVector;
	}

	/**
	 * Method to sort. Using logic same as in ViewBOM.jsp.
	 *
	 * @param data
	 * @param type
	 * @return
	 * @throws WTException
	 */
	public static final Collection sortTopLevel(Collection data, FlexType type) throws WTException {
		if (data == null || data.size() < 1) {
			return new Vector();
		}
		Vector groupedVector = new Vector();
		Map table = TableDataUtil.groupIntoCollections(data, BOMSection);
		Collection sections = type.getAttribute(BOMSection).getAttValueList().getSelectableKeys(null, true);
		String key = "";
		Iterator i = sections.iterator();
		Collection subSet = null;
		while (i.hasNext()) {
			key = (String) i.next();
			subSet = (Collection) table.get(key);
			if (subSet != null) {
				subSet = SortHelper.sortFlexObjectsByNumber(subSet, "sortingNumber");
				groupedVector.addAll(subSet);
			}
		}
		return groupedVector;
	}


	
	/**
	 * Returns a list of BOMs along with Specs given a Season, Product and/or Source
	 * Returns a JSON Object in the following format
	 *    var <jsonVarName> = new Array();
	 *    <jsonVarName>[<branchId of Spec>] = {<branchId of BOM>:<Name of BOM>};
	 *    ...,
	 * @param prod
	 * @param ssn
	 * @param sc  can be NULL
	 * @param jsonVarName
	 * @return
	 * @throws WTException
	 * TODO:  Create one query that avoids the loop
	 */
	@SuppressWarnings("unchecked")
	public static String findBOMs(LCSProduct prod, LCSSeason ssn, LCSSourcingConfig sc, String jsonVarName) throws Exception 	{
		LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.findBOMs: START");
		// 1.  Find Specifications for the Season
		@SuppressWarnings("unchecked")
		Collection<FlexSpecification> specs= new ProductHeaderQuery().findSpecifications(prod, sc, ssn);
		LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.findBOMs: Specs Found : "+specs);
		Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>(); 

		// 2.  Loop through the Specifications to get BOMs.
		String specId;
		Map<String, String> bomNames = null;
		JSONObject jsonData = new JSONObject();
		for(FlexSpecification spec : specs)	{
			@SuppressWarnings("static-access")
			Collection<FlexObject> boms = new LCSFlexBOMQuery().findBOMs(prod, sc, spec, "MAIN", true/*No Duplicate BOMs*/);
			LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.findBOMs: for Spec: "+spec+" -- BOMs: "+boms);
			specId = Long.toString(spec.getBranchIdentifier());
			bomNames = new HashMap<String, String>();
			
			// Only attributes that are relevant are BranchId and Name (shouldn't be hard-coded).
			JSONArray jsonArr = new JSONArray();
			for(FlexObject obj: boms)	{
				bomNames.put(obj.getString("FLEXBOMPART.BRANCHIDITERATIONINFO"), obj.getString("FLEXBOMPART.PTC_STR_1TYPEINFOFLEXBOMPART"));
				JSONObject json = new JSONObject();
				json.put("id", obj.getString("FLEXBOMPART.BRANCHIDITERATIONINFO"));
				json.put("name", obj.getString("FLEXBOMPART.PTC_STR_1TYPEINFOFLEXBOMPART"));
				jsonArr.put(json);
			}
			data.put(specId, bomNames);
			jsonData.put("specId", specId);
			jsonData.put("data", jsonArr);
			LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.findBOMs: In Process Data: "+data);
		}
		
		String str = JSONHelper.toJSONKeyedArray(jsonVarName, data);
		LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.findBOMs: FINISH -----  Raw DATA : "+data +" -- JSON String:"+jsonData.toString()+" -- String:: "+str);
		return str;
		/*
		 * >>>>>>>>>>>>>>>>> In Process Data: {452893={452807=null}, 452799={452807=null}}
		 *                                    {"data":[{"id":"452807"}],"specId":"452893"}
		 * 
		 * 
		INFO  : wt.system.out kkari - Wed Jun 17 02:35:17 GMT 2015:  >>>>>>>>>>>>>>>>> END -----  Raw DATA : {452893={452807=001
				 : BOM}, 452799={452807=001 : BOM}} -- JSON String:var bomData = new Array();
				INFO  : wt.system.out kkari - bomData['452893'] = {'452807':'001 : BOM'}
				INFO  : wt.system.out kkari - bomData['452799'] = {'452807':'001 : BOM'}
				INFO  : wt.system.out kkari -
		*/		
	}
	
	/**
	 * Checks and returns the referenced BOM object associated with the selected spec
	 * @param spec
	 * @param strRefBomName
	 * @return
	 * @throws WTException
	 * TODO:  Return the Referenced BOM Object
	 */
	public static FlexBOMPart findSpecRefBOMObj(FlexSpecification spec, String strRefBomName) throws WTException {
		
		Collection specBOMs = new Vector();
		//Get the list of BOMs associated with the selected Spec
		specBOMs = FlexSpecQuery.getSpecComponents(spec,"BOM");	
		
        //Iterate the list to find the Referenced BOM object		
		Iterator specBOMiter = specBOMs.iterator();					
		FlexBOMPart flexbompart =  null;
		FlexBOMPart flexbompartObj = null;
		String strFlexBomName = "";
		while(specBOMiter.hasNext()) {
			flexbompart = (FlexBOMPart) specBOMiter.next();
			strFlexBomName = flexbompart.getName();
			//Compare the Referenced BOM with the BOMs associated with Spec
			if(strFlexBomName.equals(strRefBomName))			{
			 flexbompartObj = flexbompart;
			 break;		
			}
	    }
		//Return the correct Flex BOM object for the Referenced BOM
		return flexbompartObj;
	}
		
	public static final WTObject blockCostSheet(WTObject wtobject)
			throws WTException
	{
		LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.blockCostSheet: START ("+wtobject+")");
		LCSProductCostSheet lcscostsheet = null;
		if(wtobject instanceof LCSProductCostSheet) {
			lcscostsheet = (LCSProductCostSheet)wtobject;
		} else {
			throw new WTException("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.blockCostSheet: Object is not instance of LCSProductCostSheet");
		}
		
		String isBlockedString = (String) lcscostsheet.getValue("smBlockedCostSheet");
		
		LOGGER.debug("SPORTMASTER>>>>>> SMCostSheetBOMPlugin.blockCostSheet: isBlockedString (" + isBlockedString + ")");
		
		if(isBlockedString != null && isBlockedString.equalsIgnoreCase("vrdYes")) {
			lcscostsheet.setValue("smBlockedCostSheetTechnical","vrdYes");
		} else {
			lcscostsheet.setValue("smBlockedCostSheetTechnical","vrdNo");
		}
		
		PersistenceServerHelper.manager.update(lcscostsheet, false);
		
		return lcscostsheet;
	}
	
}


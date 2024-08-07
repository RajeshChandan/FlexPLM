/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelConstants;



/**
 * SMCareLabelHelper .
 * 
 * @author 'true' ITC
 * @version 'true' 1.0 version number
 * @since March 13, 2018
 */
public class SMCareLabelHelper {

	private static final String PRODUCT = "Product";

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCareLabelHelper.class);

	/**
	 * Constant EMPTY_STRING.
	 */
	private static final String EMPTY_STRING = "";

	/**
	 * Constructor.
	 */
	protected SMCareLabelHelper(){
		//constructor.
	}

	/**Method setSelectedCriteria - method to add the select criteria.
	 * @param inputSelectedMap the inputSelectedMap.
	 * @param reportBean the reportBean.
	 * @return SMMaterialForecastReportBean.
	 */
	public static SMCareLabelIntegrationBean setSelectedCriteria( Map<String, Object> inputSelectedMap, SMCareLabelIntegrationBean integrationBean) 
	{
		LOGGER.debug("SMCareLabelHelper - getSelectedCriteria method: inputSelectedMap= "+inputSelectedMap);
		LCSSeason season=null;
		try {
			//set selected season name
			String selectedSeasonsId = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("seasonIds"));
			if(FormatHelper.hasContent(selectedSeasonsId)){
				integrationBean.setSelectedSeasonOid(selectedSeasonsId);
				season = (LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+selectedSeasonsId);
				integrationBean.setSelectedSeasonName(season.getName());
			}
			//set selected product names.
			String selectedProductid = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("productid"));
			if(FormatHelper.hasContent(selectedProductid)){
				String prodDisplayName = getProdSelectedVlaue(selectedProductid, integrationBean);
				integrationBean.setSelectedProductName(prodDisplayName);
			}
			//set selected BRANDs.
			String selectedBrandId = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("brandId"));
			if(FormatHelper.hasContent(selectedBrandId)){
				Collection<String> selectedBrand=MOAHelper.getMOACollection(selectedBrandId);
				String slectedBrandDisplayValue = getSelectedValue(SMCareLabelConstants.BRAND, PRODUCT, selectedBrand );
				integrationBean.setSelectedBrands(slectedBrandDisplayValue);
			}
			//set selected Projects.
			String selectedProjectId = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("projectId"));
			if(FormatHelper.hasContent(selectedProjectId)){
				String slectedProjDisplayValue = getSelectedProjects(
						inputSelectedMap, selectedProjectId);
				
				integrationBean.setSelectedProject(slectedProjDisplayValue);
			}
			//set selected GENDERs.
			String selectedGenderId = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("genderId"));
			if(FormatHelper.hasContent(selectedGenderId)){
				Collection<String> selectedGender=MOAHelper.getMOACollection(selectedGenderId);
				String slectedGenderDisplayValue = getSelectedValue(SMCareLabelConstants.GENDER, PRODUCT, selectedGender);
				integrationBean.setSelectedGenders(slectedGenderDisplayValue);
			}
			//set selected AGE.
			String selectedAgeId = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("ageId"));
			if(FormatHelper.hasContent(selectedAgeId)){
				Collection<String> selectedAge=MOAHelper.getMOACollection(selectedAgeId);
				String slectedAgeDisplayValue = getSelectedValue(SMCareLabelConstants.AGE, PRODUCT, selectedAge);
				integrationBean.setSelectedAges(slectedAgeDisplayValue);
			}
			//set selected PRODUCTION Group.
			String selectedProductionGroupId = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("productionGroupId"));
			if(FormatHelper.hasContent(selectedProductionGroupId)){
				Collection<String> selectedProductionGroup=MOAHelper.getMOACollection(selectedProductionGroupId);
				String slectedProdGrpDisplayValue = getSelectedValue(SMCareLabelConstants.PRODUCTION_GROUP, "Product\\APD", selectedProductionGroup);
				integrationBean.setSelectedProductionGroup(slectedProdGrpDisplayValue);
			}
			//set selected Tecnologist..
			String selectedProducctTechnologistId = FormatHelper.format(EMPTY_STRING+inputSelectedMap.get("producctTechnologistId"));
			if(FormatHelper.hasContent(selectedProducctTechnologistId)){

				Map technologist = (Map)inputSelectedMap.get("intTechnologistMap");
				String slectedProdTechDisplayValue = (String)technologist.get(selectedProducctTechnologistId);
				integrationBean.setSelectedProducctTechnologist(slectedProdTechDisplayValue);
			}


		} catch (WTException e) {
			e.printStackTrace();
		}
		return integrationBean;

	}

	private static String getSelectedProjects(
			Map<String, Object> inputSelectedMap, String selectedProjectId) {
		Collection<String> selectedProject=MOAHelper.getMOACollection(selectedProjectId);
		String slectedProjDisplayValue="";
		Map projMap = (Map)inputSelectedMap.get("intProjectMap");
		LOGGER.debug("intProjectMap >>>>>>>>>>" +projMap);
		for(String projId:selectedProject){
			//slectedProjDisplayValue = (String)projMap.get(projId);
		 if(FormatHelper.hasContent(slectedProjDisplayValue)){
			 slectedProjDisplayValue=slectedProjDisplayValue+", "+(String)projMap.get(projId);
			}else{
				slectedProjDisplayValue=(String)projMap.get(projId);
			}
		
		}
		return slectedProjDisplayValue;
	}

	/**
	 * This to get selected product names.
	 * @param selectedProdid the String.
	 * @param getProdSelectedVlaue the SMCareLabelIntegrationBean.
	 * @return the String
	 * @throws WTException the exception.
	 */
	private static String getProdSelectedVlaue(String selectedProdid, SMCareLabelIntegrationBean getProdSelectedVlaue)
			throws WTException {
		Collection<String> selectedProducts=MOAHelper.getMOACollection(selectedProdid);
		String prodDisplayName="";
		//iterate all the slected product oid's
		for(String productId:selectedProducts){
			getProdSelectedVlaue.setSelectedProductID(productId);
			LCSProduct product=(LCSProduct)LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+productId);
			if(FormatHelper.hasContent(prodDisplayName)){
				prodDisplayName=prodDisplayName+", "+product.getName();
			}else{
				prodDisplayName=product.getName();
			}
		}
		return prodDisplayName;
	}


	/**
	 * This method gives display values for the selected values on Criterila page.
	 * @param strAttKey the String.
	 * @param flexTypeName the String.
	 * @param multiSelectedCriteria the collection.
	 * @return the string.
	 */
	private static String getSelectedValue(String strAttKey, String flexTypeName, Collection<String> multiSelectedCriteria ){

		String displayValue="";
		FlexType flexType;
		try {
			//get flex type
			flexType = FlexTypeCache.getFlexTypeFromPath(flexTypeName);

			FlexTypeAttribute fta = flexType.getAttribute(strAttKey);
			//get attribute list
			AttributeValueList attList = fta.getAttValueList();
			//iterate the collection of attribute selected on filter page.
			for(String selectedAtt:multiSelectedCriteria){ 	
				LOGGER.debug("selectedAtt>>>>>>>>>>" +selectedAtt);
				LOGGER.debug("attList>>>>>>>>>>" +attList);
				//Append the display value if multi selected.
				if(FormatHelper.hasContent(displayValue)){ 						
					displayValue=displayValue+", "+attList.getValue(selectedAtt,ClientContext.getContextLocale()); 
				}else{	
					displayValue=attList.getValue(selectedAtt,ClientContext.getContextLocale());
				}
			}
		} catch (WTException e) {

			e.printStackTrace();
		}
		return displayValue;
	}

}

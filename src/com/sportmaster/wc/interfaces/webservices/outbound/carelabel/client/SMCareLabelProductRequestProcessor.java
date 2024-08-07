/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.client;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.BOMProductAttributes;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.BOMProductSeasonAttributes;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelUtil;

/**
 * SMCareLabelProductRequestProcessor.
 * 
 * @author 'true' ITC.
 * @version 'true' 1.0 version number
 * @since Feb 23, 2018
 */
public class SMCareLabelProductRequestProcessor {

	

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER =  Logger.getLogger(SMCareLabelProductRequestProcessor.class);



	/**
	 * Constructor.
	 */
	protected SMCareLabelProductRequestProcessor(){
		//protected constructor.
	}



	/**
	 * This method sets Product season levele attributes.
	 * @param prodSeasonlink the productseasonlink.
	 * @param eachRowcareLabledata the FlexObject.
	 * @return the BOMProductSeasonAttributes.
	 * @throws WTException the exception.
	 */
	public BOMProductSeasonAttributes setProductSeasonAttributes(
			LCSProductSeasonLink prodSeasonlink, FlexObject eachRowcareLabledata) throws WTException {
		
		LOGGER.debug("Start setting product-season level data");

		BOMProductSeasonAttributes bomProdSeasAtt = new BOMProductSeasonAttributes();
		String smProdTecnologist="";
		String smProdGroupAttColumn = prodSeasonlink.getFlexType().getAttribute(SMCareLabelConstants.PRODUCTION_GROUP).getSearchResultIndex();

		//Need to handle only for APD
		if(prodSeasonlink.getFlexType().getFullName().contains("APD") ){
			smProdTecnologist=SMCareLabelUtil.getUserdetail(prodSeasonlink, SMCareLabelConstants.TECNOLOGIST);

		}

		//Getting Season object.
		LCSSeason season = (LCSSeason)SeasonProductLocator.getSeasonRev(prodSeasonlink);

		//set season MDM ID.
		bomProdSeasAtt.setProductSeasonMDMId(SMCareLabelUtil.getMDMID(prodSeasonlink));
		//set product-season PLM ID.
		bomProdSeasAtt.setProductSeasonPLMId(SMCareLabelUtil.getProductSeasonLinkPLMID(prodSeasonlink));
		//set Product-season PLM ID.
		bomProdSeasAtt.setSeasonMDMId(SMCareLabelUtil.getMDMID(season));
		//set season name.
		bomProdSeasAtt.setSeasonName(season.getName());
		//set product Technologist.
		if(FormatHelper.hasContent(smProdTecnologist)){
			bomProdSeasAtt.setSmProductTechnologist(smProdTecnologist);
		}

		//set Production group, optional
		if(FormatHelper.hasContent(smProdGroupAttColumn) && FormatHelper.hasContent(eachRowcareLabledata.getString(smProdGroupAttColumn)) ){
			bomProdSeasAtt.setSmProductionGroup(eachRowcareLabledata.getString(smProdGroupAttColumn));
		}


		return bomProdSeasAtt;
	}

	/**
	 * This method set the product attributes.
	 * @param prodObj the LCSProduct.
	 * @param eachRowcareLabledata the FlexObject.
	 * @return the BOMProductAttributes.
	 * @throws WTException the exception.
	 */
	public BOMProductAttributes setProductAttributes(
			LCSProduct prodObj, FlexObject eachRowcareLabledata) throws WTException {
		
		LOGGER.debug("Start setting product level data");
		
		BOMProductAttributes bomProductAtt = new BOMProductAttributes();
		
		
		//set product MDM ID
		bomProductAtt.setProductMDMId(SMCareLabelUtil.getMDMID(prodObj));

		//set produt plm ID
		bomProductAtt.setProductPLMId(SMCareLabelUtil.getProductPLMID(prodObj));

		//set product name.
		bomProductAtt.setProductName(prodObj.getName());

		//set Style number.
		//String smVrdStyleNumAttColumn=prodObj.getFlexType().getAttribute("vrdStyleNum").getSearchResultIndex();
		bomProductAtt.setVrdStyleNum(eachRowcareLabledata.getData(prodObj.getFlexType().getAttribute(SMCareLabelConstants.STYLE_NUM).getSearchResultIndex()));

		//set Brand
		//String smVrdBrandAttColumn=prodObj.getFlexType().getAttribute("vrdBrand").getSearchResultIndex();
		bomProductAtt.setVrdBrand(eachRowcareLabledata.getData(prodObj.getFlexType().getAttribute(SMCareLabelConstants.BRAND).getSearchResultIndex()));


		//set Age.
		String smAgeAttColumn=prodObj.getFlexType().getAttribute(SMCareLabelConstants.AGE).getSearchResultIndex();
		if(FormatHelper.hasContent(smAgeAttColumn)){
			bomProductAtt.setSmAge(eachRowcareLabledata.getData(smAgeAttColumn));
		}

		//set Project,Need to handle only for APD,
		String smProjectAttColumn="";
		if(prodObj.getFlexType().getFullName().contains("APD") ){
			smProjectAttColumn=prodObj.getFlexType().getAttribute(SMCareLabelConstants.PROJECT).getSearchResultIndex();
		}
		if(FormatHelper.hasContent(smProjectAttColumn) && FormatHelper.hasContent(eachRowcareLabledata.getData(smProjectAttColumn))){
			bomProductAtt.setSmProject(eachRowcareLabledata.getData(smProjectAttColumn));
		}
		
		

		return bomProductAtt;
	}




}

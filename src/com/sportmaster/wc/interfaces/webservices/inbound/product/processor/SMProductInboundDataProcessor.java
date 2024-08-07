/**
 *
 */
package com.sportmaster.wc.interfaces.webservices.inbound.product.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.product.client.SMProductInboundDataRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationItem;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationItem;



/**
 * @author ITC_Infotech.
 *
 */
public class SMProductInboundDataProcessor {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductInboundDataProcessor.class);
	/**
	 * Product Failed Count.
	 */
	private static int productFailedCount;
	/**
	 * Colorway Failed Count.
	 */
	private static int colorwayFailedCount;


	/*
	 * protected constructor.
	 */
	protected SMProductInboundDataProcessor(){
		//protected constructor
	}

	/**
	 * Process product inbound response.
	 * @param productInboundList - ProductSeasonLinkInformationItem
	 */
	public static void processProductInboundResponse(ProductSeasonLinkInformationItem productInboundList){
		try{
			LCSProductSeasonLink prodSeasonLink = SMProductInboundUtil.getProductSeasonLinkFromPLMID(productInboundList.getPlmId());
			//Updated code for null pointer issue if the MDM ID is empty on the PSL link
			String pslMdmId=null;
			if(prodSeasonLink != null){
				pslMdmId=String.valueOf(prodSeasonLink.getValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_MDMID));
				LOGGER.info("Product Season MDMID ::: " +pslMdmId);

			}

			if (prodSeasonLink != null && FormatHelper.hasContent(pslMdmId) && !pslMdmId.equalsIgnoreCase("null")
					&& pslMdmId.equals(productInboundList.getMdmId())) {
				SMProductInboundDataProcessor.setDataOnProductSeasonLink(productInboundList, prodSeasonLink);
			}else{
				SMProductInboundDataRequestWebClient.setErrorMsg("PRODUCT SEASON LINK IS NOT FOUND ::::  PLM ID  >>>  "+productInboundList.getPlmId()+"   MDM ID  >>>  "+productInboundList.getMdmId());
				LOGGER.error(SMProductInboundDataRequestWebClient.getErrorMsg());
				setFailedCount(productFailedCount+1);
				SMProductInboundLogEntryProcessor.setProductSeasonLinkInboundLogEntry(null, productInboundList);
			}
		}catch(WTException wexp){
			LOGGER.error(wexp.getLocalizedMessage());
			wexp.printStackTrace();
		}
	}

	/**
	 * Process Colorway Inbound data.
	 * @param colorwayInboundList - ColorwaySeasonLinkInformationItem
	 */
	public static void processColorwayInboundResponse(ColorwaySeasonLinkInformationItem colorwayInboundList){
		try{
			LCSSKUSeasonLink skuSeasonLink = SMProductInboundUtil.getSKUSeasonLinkFromPLMID(colorwayInboundList.getPlmId());
			//Updated code for null pointer issue if the MDM ID is empty on the skuSeason link
			String sslMdmId=null;
			if(skuSeasonLink != null){
				sslMdmId=String.valueOf(skuSeasonLink.getValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_MDMID));
				LOGGER.info("Colorway Season MDMID ::: " +sslMdmId);

			}
			if (skuSeasonLink != null && FormatHelper.hasContent(sslMdmId) && !sslMdmId.equalsIgnoreCase("null")
					&& sslMdmId.equals(colorwayInboundList.getMdmId())) {
				SMProductInboundDataProcessor.setDataOnColorwaySeasonLink(colorwayInboundList, skuSeasonLink);
			}else{
				SMProductInboundDataRequestWebClient.setErrorMsg("COLORWAY SEASON LINK NOT FOUND ::::  PLM ID  >>>  "+colorwayInboundList.getPlmId()+"   MDM ID  >>>  "+colorwayInboundList.getMdmId());
				LOGGER.error(SMProductInboundDataRequestWebClient.getErrorMsg());
				setFailedCount(colorwayFailedCount+1);
				SMProductInboundLogEntryProcessor.setColorwaySeasonLinkInboundLogEntry(null, colorwayInboundList);
			}
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage());
			we.printStackTrace();
		}
	}

	/**
	 * Set data on Product Season Link.
	 * @param productInboundList - ProductSeasonLinkInformationItem
	 * @param productSeasonLink - LCSProductSeasonLink
	 */
	public static void setDataOnProductSeasonLink(ProductSeasonLinkInformationItem productInboundList, LCSProductSeasonLink productSeasonLink){
		try{
			//set Product Intake Date.
			setProductIntakeDate(productInboundList, productSeasonLink);
			//set values for Product First Fore cast On Hold.
			setProductFirstForecastOnHold(productInboundList, productSeasonLink);
			//set Product First Forecast Values.
			setProductFirstForecast(productInboundList, productSeasonLink);
			//set Product Second Forecast Values.
			setProductSecondForecast(productInboundList, productSeasonLink);
			//Set Product Bulk Order values.
			setProductBulkOrder(productInboundList, productSeasonLink);

			//Persist product season link
			SMProductInboundUtil.persistProductSeasonLink(productSeasonLink);

			// Phase -13, customization (Transfer composition from PS to P)- start
			// process composition data
			new SMProductCompositionProcessor().processComposiotnTransfer(productSeasonLink);
			// Phase -13, customization (Transfer composition from PS to P)- end

			//Set Log Entry.
			SMProductInboundLogEntryProcessor.setProductSeasonLinkInboundLogEntry(productSeasonLink, productInboundList);
		}catch (WTPropertyVetoException e1) {
			LOGGER.error(e1.getLocalizedMessage());
			e1.printStackTrace();
			SMProductInboundDataRequestWebClient.setIntegartionFailurePSL(true);
		} catch (WTException e2) {
			LOGGER.error(e2.getLocalizedMessage());
			e2.printStackTrace();
			SMProductInboundDataRequestWebClient.setIntegartionFailurePSL(true);
		}
	}

	/**
	 * Set Product Bulk order values.
	 * @param productInboundList
	 * @param productSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setProductBulkOrder(
			ProductSeasonLinkInformationItem productInboundList,
			LCSProductSeasonLink productSeasonLink) throws WTException,
	WTPropertyVetoException {
		if(productInboundList.getSmBulkOrderUnitsStyleRU() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_BULK_ORDER_RUSSIA, productInboundList.getSmBulkOrderUnitsStyleRU());
		}
		if(productInboundList.getSmBulkOrderUnitsStyleUA() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_BULK_ORDER_UKRAINE, productInboundList.getSmBulkOrderUnitsStyleUA());
		}
		if(productInboundList.getSmBulkOrderUnitsStyleCH() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_BULK_ORDER_CHINA, productInboundList.getSmBulkOrderUnitsStyleCH());
		}
	}

	/**
	 * Set Product 2nd Forecast.
	 * @param productInboundList
	 * @param productSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setProductSecondForecast(
			ProductSeasonLinkInformationItem productInboundList,
			LCSProductSeasonLink productSeasonLink) throws WTException,
	WTPropertyVetoException {
		if(productInboundList.getSmForecastUnitsStyle2NdRU() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_SECOND_FORECAST_RUSSIA, productInboundList.getSmForecastUnitsStyle2NdRU());
		}
		if(productInboundList.getSmForecastUnitsStyle2NdUA() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_SECOND_FORECAST_UKRAINE, productInboundList.getSmForecastUnitsStyle2NdUA());
		}
		if(productInboundList.getSmForecastUnitsStyle2NdCH() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_SECOND_FORECAST_CHINA, productInboundList.getSmForecastUnitsStyle2NdCH());
		}
	}

	/**
	 * Set Product First Forecast.
	 * @param productInboundList
	 * @param productSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setProductFirstForecast(
			ProductSeasonLinkInformationItem productInboundList,
			LCSProductSeasonLink productSeasonLink) throws WTException,
	WTPropertyVetoException {
		if(productInboundList.getSmForecastUnitsStyle1StRU() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_FIRST_FORECAST_RUSSIA, productInboundList.getSmForecastUnitsStyle1StRU());
		}
		if(productInboundList.getSmForecastUnitsStyle1StUA() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_FIRST_FORECAST_UKRAINE, productInboundList.getSmForecastUnitsStyle1StUA());
		}
		if(productInboundList.getSmForecastUnitsStyle1StCH() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_FIRST_FORECAST_CHINA, productInboundList.getSmForecastUnitsStyle1StCH());
		}
	}

	/**
	 * Set Product 1st Forecast On Hold.
	 * @param productInboundList
	 * @param productSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setProductFirstForecastOnHold(
			ProductSeasonLinkInformationItem productInboundList,
			LCSProductSeasonLink productSeasonLink) throws WTException,
	WTPropertyVetoException {
		if(productInboundList.getSmForecastUnitsStyle1StRUonHold() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_FIRST_FORECAST_ON_HOLD_RUSSIA, productInboundList.getSmForecastUnitsStyle1StRUonHold());
		}
		if(productInboundList.getSmForecastUnitsStyle1StUAonHold() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_FIRST_FORECAST_ON_HOLD_UKRAINE, productInboundList.getSmForecastUnitsStyle1StUAonHold());
		}
		if(productInboundList.getSmForecastUnitsStyle1StCHonHold() != null){
			productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_FIRST_FORECAST_ON_HOLD_CHINA, productInboundList.getSmForecastUnitsStyle1StCHonHold());
		}
	}

	/**
	 * Set Product Intake Date.
	 * @param productInboundList
	 * @param productSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setProductIntakeDate(
			ProductSeasonLinkInformationItem productInboundList,
			LCSProductSeasonLink productSeasonLink){
		try{
			if(productInboundList.getSmIntakeDateStyleRussia() != null){
				productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_INTAKE_DATE_RUSSIA, getDateObject(productInboundList.getSmIntakeDateStyleRussia()));
			}
			if(productInboundList.getSmIntakeDateStyleChina() != null){
				productSeasonLink.setValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_INTAKE_DATE_CHINA, getDateObject(productInboundList.getSmIntakeDateStyleChina()));
			}
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage());
			we.printStackTrace();
		}catch(WTPropertyVetoException wep){
			LOGGER.error(wep.getLocalizedMessage());
			wep.printStackTrace();
		}
	}

	/**
	 * @param productInboundList
	 */
	public static Object getDateObject(
			XMLGregorianCalendar xmlCalendar){
		Date date = xmlCalendar.toGregorianCalendar().getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		String dateStr = formatter.format(date);
		LOGGER.info("Date *****************   "+dateStr);
		//Object dateObj = dateStr;
		return dateStr;
	}

	/**
	 * Set Colorway Season Link Data.
	 * @param colorwayInboundList
	 * @param skuSeasonLink
	 */
	public static void setDataOnColorwaySeasonLink(ColorwaySeasonLinkInformationItem colorwayInboundList, LCSSKUSeasonLink skuSeasonLink){
		try{
			//Set Colorway Season Intake Date Values.
			setColorwayIntakeDate(colorwayInboundList, skuSeasonLink);
			//Set Colorway Season 1st Forecast On Hold
			setColorwayFirstForecastOnHold(colorwayInboundList, skuSeasonLink);
			//Set SKU-Season First Forecast Values.
			setColorwayFirstForecast(colorwayInboundList, skuSeasonLink);
			//Set SKU-Season 2nd Forecast Values.
			setColorwaySecondForecast(colorwayInboundList, skuSeasonLink);
			//Set SKU-Season Bulk Order.
			setColorwayBulkOrder(colorwayInboundList, skuSeasonLink);

			//Save SKU-Season Link.
			SMProductInboundUtil.persistColorwaySeasonLink(skuSeasonLink);

			//Set Log Entry.
			SMProductInboundLogEntryProcessor.setColorwaySeasonLinkInboundLogEntry(skuSeasonLink, colorwayInboundList);
		}catch (WTPropertyVetoException e3) {
			LOGGER.error(e3.getLocalizedMessage());
			e3.printStackTrace();
			SMProductInboundDataRequestWebClient.setIntegrationFailureSSL(true);
		} catch (WTException e4) {
			LOGGER.error(e4.getLocalizedMessage());
			e4.printStackTrace();
			SMProductInboundDataRequestWebClient.setIntegrationFailureSSL(true);
		}
	}

	/**
	 * @param colorwayInboundList
	 * @param skuSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setColorwayBulkOrder(
			ColorwaySeasonLinkInformationItem colorwayInboundList,
			LCSSKUSeasonLink skuSeasonLink) throws WTException,
	WTPropertyVetoException {
		if(colorwayInboundList.getSmBulkOrderUnitsColorwayRU() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_BULK_ORDER_RUSSIA, colorwayInboundList.getSmBulkOrderUnitsColorwayRU());
		}
		if(colorwayInboundList.getSmBulkOrderUnitsColorwayUA() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_BULK_ORDER_UKRAINE, colorwayInboundList.getSmBulkOrderUnitsColorwayUA());
		}
		if(colorwayInboundList.getSmBulkOrderUnitsColorwayCH() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_BULK_ORDER_CHINA, colorwayInboundList.getSmBulkOrderUnitsColorwayCH());
		}
	}

	/**
	 * @param colorwayInboundList
	 * @param skuSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setColorwaySecondForecast(
			ColorwaySeasonLinkInformationItem colorwayInboundList,
			LCSSKUSeasonLink skuSeasonLink) throws WTException,
	WTPropertyVetoException {
		if(colorwayInboundList.getSmForecastUnitsColorway2NdRU() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_SECOND_FORECAST_RUSSIA, colorwayInboundList.getSmForecastUnitsColorway2NdRU());
		}
		if(colorwayInboundList.getSmForecastUnitsColorway2NdUA() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_SECOND_FORECAST_UKRAINE, colorwayInboundList.getSmForecastUnitsColorway2NdUA());
		}
		if(colorwayInboundList.getSmForecastUnitsColorway2NdCH() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_SECOND_FORECAST_CHINA, colorwayInboundList.getSmForecastUnitsColorway2NdCH());
		}
	}

	/**
	 * @param colorwayInboundList
	 * @param skuSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setColorwayFirstForecast(
			ColorwaySeasonLinkInformationItem colorwayInboundList,
			LCSSKUSeasonLink skuSeasonLink) throws WTException,
	WTPropertyVetoException {
		if(colorwayInboundList.getSmForecastUnitsColorway1StRU() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_FIRST_FORECAST_RUSSIA, colorwayInboundList.getSmForecastUnitsColorway1StRU());
		}
		if(colorwayInboundList.getSmForecastUnitsColorway1StUA() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_FIRST_FORECAST_UKRAINE, colorwayInboundList.getSmForecastUnitsColorway1StUA());
		}
		if(colorwayInboundList.getSmForecastUnitsColorway1StCH() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_FIRST_FORECAST_CHINA, colorwayInboundList.getSmForecastUnitsColorway1StCH());
		}
	}

	/**
	 * @param colorwayInboundList
	 * @param skuSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setColorwayFirstForecastOnHold(
			ColorwaySeasonLinkInformationItem colorwayInboundList,
			LCSSKUSeasonLink skuSeasonLink) throws WTException,
	WTPropertyVetoException {
		if(colorwayInboundList.getSmForecastUnitsColorway1StRUonHold() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_FIRST_FORECAST_ON_HOLD_RUSSIA, colorwayInboundList.getSmForecastUnitsColorway1StRUonHold());
		}
		if(colorwayInboundList.getSmForecastUnitsColorway1StUAonHold() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_FIRST_FORECAST_ON_HOLD_UKRAINE, colorwayInboundList.getSmForecastUnitsColorway1StUAonHold());
		}
		if(colorwayInboundList.getSmForecastUnitsColorway1StCHonHold() != null){
			skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_FIRST_FORECAST_ON_HOLD_CHINA, colorwayInboundList.getSmForecastUnitsColorway1StCHonHold());
		}
	}

	/**
	 * @param colorwayInboundList
	 * @param skuSeasonLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setColorwayIntakeDate(
			ColorwaySeasonLinkInformationItem colorwayInboundList,
			LCSSKUSeasonLink skuSeasonLink){
		try{
			if(colorwayInboundList.getSmIntakeDateColorwayRussia() != null){
				skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_INTAKE_DATE_RUSSIA, getDateObject(colorwayInboundList.getSmIntakeDateColorwayRussia()));
			}
			if(colorwayInboundList.getSmIntakeDateColorwayChina() != null){
				skuSeasonLink.setValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_INTAKE_DATE_CHINA, getDateObject(colorwayInboundList.getSmIntakeDateColorwayChina()));
			}
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage());
			we.printStackTrace();
		}catch(WTPropertyVetoException wep){
			LOGGER.error(wep.getLocalizedMessage());
			wep.printStackTrace();
		}
	}

	/**
	 * @return the failedCount
	 */
	public static int getFailedCount() {
		return productFailedCount;
	}

	/**
	 * @param failedCount the failedCount to set
	 */
	public static void setFailedCount(int productFails) {
		SMProductInboundDataProcessor.productFailedCount = productFails;
	}

	/**
	 * @return the colorwayFailedCount
	 */
	public static int getColorwayFailedCount() {
		return colorwayFailedCount;
	}

	/**
	 * @param colorwayFailedCount the colorwayFailedCount to set
	 */
	public static void setColorwayFailedCount(int colorwayFails) {
		SMProductInboundDataProcessor.colorwayFailedCount = colorwayFails;
	}
}

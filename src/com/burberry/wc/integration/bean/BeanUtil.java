package com.burberry.wc.integration.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.apache.log4j.Logger;

import com.burberry.wc.integration.bean.Pricing.PricingStyle;
import com.burberry.wc.integration.bean.Pricing.PricingStyle.PricingColourways;
import com.burberry.wc.integration.bean.Pricing.PricingStyle.PricingColourways.PricingColourway;
import com.burberry.wc.integration.bean.Pricing.PricingStyle.PricingColourways.PricingColourway.PricingSeasons;
import com.burberry.wc.integration.bean.Pricing.PricingStyle.PricingColourways.PricingColourway.PricingSeasons.PricingSeason;
import com.burberry.wc.integration.bean.Pricing.PricingStyle.PricingColourways.PricingColourway.PricingSeasons.PricingSeason.ColourwayCost;
import com.burberry.wc.integration.bean.Products.Style;
import com.burberry.wc.integration.bean.Products.Style.Colourways;
import com.burberry.wc.integration.bean.Products.Style.Colourways.Colourway;
import com.burberry.wc.integration.bean.Products.Style.Colourways.Colourway.Seasons;
import com.burberry.wc.integration.bean.Products.Style.Colourways.Colourway.Seasons.Season;
import com.burberry.wc.integration.bean.Products.Style.Dimensions;
import com.burberry.wc.integration.bean.Products.Style.PlanningAttributes;
import com.burberry.wc.integration.util.BurConstant;
import com.lcs.wc.util.FormatHelper;

/**
 * Bean utility class to create and update bean data.
 * 
 * 
 * Utility methods help to create bean object and set data from/to map.
 *
 *
 *
 * 
 * Each method is written to handle to create and update bean data.
 * 
 * Class contains method which are specific to Bean type. Each method create and
 * update specific bean object and return same.
 *
 *
 * 
 * 
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */

public final class BeanUtil {

	/**
	 * BeanUtil.
	 */
	private BeanUtil() {

	}

	/**
	 * Create and return updated Style bean.
	 * 
	 * Method responsible to create an empty Style bean object using
	 * ObjectFactory and set bean object property by using given Map.
	 *
	 *
	 *
	 * Map contains all the data extracted from DB for this Particular bean .
	 *
	 * Each bean object will be filled with the data in map. Each value is
	 * mapped to a key and to retrieve value you must pass a valid key.
	 *
	 * 
	 * @param productMap
	 * @param beanType
	 * @return Object
	 */
	
	/**
	 * EMPTY_STRING
	 */
	private static final String EMPTY_STRING = "";

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BeanUtil.class);
	
	public static Object putStyleData(final Map<String, Object> productMap,
			final BeanType beanType) {
		
		String methodName = "putStyleData() ";
		
		Object styleBean=null;
		
		logger.debug(methodName + "productMap: " + productMap);
		
		//setting
		//pricingStyleBean
		//data
		//from Map to Bean
		if (beanType.equals(BeanType.PRICING)) {
			 Pricing.PricingStyle pricingStyleBean = ObjectFactory
					.createPricingStyle();
			pricingStyleBean.setLastModifiedTimestamp(getValue(productMap
					.get(BurConstant.MODIFY)));
			pricingStyleBean.setDivision(getValue(productMap
					.get(BurConstant.BUR_DIVISION)));
			pricingStyleBean.setBrand(getValue(productMap
					.get(BurConstant.BUR_BRAND)));
			pricingStyleBean.setOperationalCategory(getValue(productMap
					.get(BurConstant.BUR_OPERATIONAL_CATEGORY_APP)));
			pricingStyleBean.setAccessoryGroup(getValue(productMap
					.get(BurConstant.BUR_ACCESSORY_GROUP)));
			pricingStyleBean.setType(getValue(productMap
					.get(BurConstant.BUR_PRODUCT_TYPE)));
			pricingStyleBean.setDepartment(getValue(productMap
					.get(BurConstant.BUR_DEPT_GROUP)));
			pricingStyleBean.setSubGroup(getValue(productMap
					.get(BurConstant.BUR_SUB_GROUP)));
			pricingStyleBean.setAgeGroup(getValue(productMap
					.get(BurConstant.BUR_AGE_GROUP)));
			pricingStyleBean
					.setName(getValue(productMap.get(BurConstant.BUR_STYLENAME)));
			pricingStyleBean.setMainRMId(getValue(productMap
					.get(BurConstant.BUR_MAIN_RM_CODE)));
			pricingStyleBean.setId(getValue(productMap
					.get(BurConstant.VRD_STYLE_NUM)));
			pricingStyleBean.setLegacyId(getValue(productMap
					.get(BurConstant.BUR_LEGACY_PLM_STYLE_ID)));
			
			logger.debug(methodName + "PRICING productStyleBean: " + pricingStyleBean);
			styleBean= pricingStyleBean;

		}
		//setting
		//productStyleBean
		//data
		//from Map to Bean		
		else if (beanType.equals(BeanType.PRODUCT)) {					
			final Products.Style productStyleBean = ObjectFactory
					.createProductsStyle();

			productStyleBean.setLastModifiedTimestamp(getValue(productMap
					.get(BurConstant.MODIFY)));
			productStyleBean.setDivision(getValue(productMap
					.get(BurConstant.BUR_DIVISION)));
			productStyleBean.setBrand(getValue(productMap.get(BurConstant.BUR_BRAND)));
			productStyleBean.setOperationalCategory(getValue(productMap
					.get(BurConstant.BUR_OPERATIONAL_CATEGORY_APP)));
			productStyleBean.setAccessoryGroup(getValue(productMap
					.get(BurConstant.BUR_ACCESSORY_GROUP)));
			productStyleBean.setType(getValue(productMap
					.get(BurConstant.BUR_PRODUCT_TYPE)));
			productStyleBean.setDepartment(getValue(productMap
					.get(BurConstant.BUR_DEPT_GROUP)));
			productStyleBean.setSubGroup(getValue(productMap
					.get(BurConstant.BUR_SUB_GROUP)));
			productStyleBean.setAgeGroup(getValue(productMap
					.get(BurConstant.BUR_AGE_GROUP)));
			productStyleBean.setName(getValue(productMap.get(BurConstant.BUR_STYLENAME)));
			productStyleBean.setMainRMId(getValue(productMap
					.get(BurConstant.BUR_MAIN_RM_CODE)));
			productStyleBean
					.setId(getValue(productMap.get(BurConstant.VRD_STYLE_NUM)));
			productStyleBean.setLegacyId(getValue(productMap
					.get(BurConstant.BUR_LEGACY_PLM_STYLE_ID)));

			productStyleBean.setBranding(getValue(productMap
					.get(BurConstant.BUR_BRANDING_TYPE)));
			productStyleBean.setCheckBranding(getValue(productMap
					.get(BurConstant.BUR_CHECK_BRANDING)));
			productStyleBean.setCitesClassification(getValue(productMap
					.get(BurConstant.BUR_CITES)));
			productStyleBean.setCommodityCode(getValue(productMap
					.get(BurConstant.BUR_COMMODITY_CODE)));
			productStyleBean.setDescription(getValue(productMap
					.get(BurConstant.VRD_DESCRIPTION)));
			productStyleBean.setDownWeight(getValue(productMap
					.get(BurConstant.BUR_DOWN_WEIGHTS_WEIGHT)));
			productStyleBean
					.setExotic(getValue(productMap.get(BurConstant.BUR_EXOTIC)));
			productStyleBean.setFabricType(getValue(productMap
					.get(BurConstant.BUR_FABRIC_TYPE)));
			productStyleBean.setFit(getValue(productMap.get(BurConstant.BUR_FIT)));
			productStyleBean.setHeelHeight(getValue(productMap
					.get(BurConstant.BUR_HEEL_HEIGHT)));
			productStyleBean.setKaff(getValue(productMap
					.get(BurConstant.BUR_KAFF_FLAG)));
			productStyleBean.setMainRMComposition(getValue(productMap
					.get(BurConstant.BUR_MAIN_RM)));
			productStyleBean.setMarketingDescription(getValue(productMap
					.get(BurConstant.BUR_MARKETING_DESCRIPTION)));
			productStyleBean.setMaterialPackagingGroup(getValue(productMap
					.get(BurConstant.BUR_SHIP_BOXED_HANGING)));
			productStyleBean.setProduction(getValue(productMap
					.get(BurConstant.BUR_PRODUCTION_TYPE)));
			productStyleBean.setSellingSizeRange(getValue(productMap
					.get(BurConstant.BUR_SIZE_RANGE)));
			productStyleBean.setShape(getValue(productMap.get(BurConstant.BUR_SHAPE)));
			productStyleBean.setSilhouette(getValue(productMap
					.get(BurConstant.BUR_SIL_HOUETTE)));
			productStyleBean.setSizeGrouping(getValue(productMap
					.get(BurConstant.BUR_SIZE_GROUPING)));
			productStyleBean.setStyleDetailing(getValue(productMap
					.get(BurConstant.BUR_STYLE_DETAILING)));
			productStyleBean.setSubWorld(getValue(productMap
					.get(BurConstant.BUR_SUB_WORLD)));

			productStyleBean.setTrimMaterial(getValue(productMap
					.get(BurConstant.BUR_TRIM)));
			productStyleBean.setWashCare(getValue(productMap
					.get(BurConstant.BUR_WASH_INSTRUCTIONS)));
			productStyleBean.setWeightGroup(getValue(productMap
					.get(BurConstant.BUR_WEIGHT_GROUP)));
			productStyleBean.setWorld(getValue(productMap.get(BurConstant.BUR_WORLD)));
			
			logger.debug(methodName + "PRODUCT productStyleBean: " + productStyleBean);
			styleBean= productStyleBean;
		}
		logger.info(methodName + "styleBean: " + styleBean);		
		return styleBean;

	}

	/**
	 * Create and return updated colorway bean.
	 * 
	 * Method responsible to create an empty bean object using ObjectFactory and
	 * set bean object property by using given Map.
	 *
	 *
	 *
	 * Map contains all the data extracted from DB for this Particular bean .
	 *
	 *
	 * Each bean object will be filled with the data in map. Each value is
	 * mapped to a key and to retrieve value you must pass a valid key.
	 *
	 *
	 * 
	 * @param skuMap
	 * @param beanType
	 * @return Object
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	private static Object setColourwaysData(final Map<String, Object> skuMap,
			final BeanType beanType) throws NoSuchFieldException, SecurityException {
		
		String methodName = "setColourwaysData() ";
		Object skuBean=null;
		logger.debug(methodName + "skuMap: " + skuMap);
		
		//setting
		//colorwayBean 
		//for pricing
		//data
		//from Map to Bean
		if (beanType.equals(BeanType.PRICING)) {
			final PricingColourway colorwayBean = ObjectFactory
					.createPricingColourway();
			colorwayBean.setLastModifiedTimestamp(skuMap
					.get(BurConstant.MODIFY).toString());
			colorwayBean.setId(getValue(skuMap.get(BurConstant.BUR_SAP_MATERIAL_NUMBER)));
			colorwayBean.setLegacyId(getValue(skuMap
					.get(BurConstant.BUR_LEGACY_PLMID)));
			logger.debug(methodName + "PRICING colorwayBean: " + colorwayBean);					
			skuBean= colorwayBean;
		}
		//setting
		//colorwayBean 
		//for products
		//data
		//from Map to Bean
		else if (beanType.equals(BeanType.PRODUCT)) {

			final Colourway colorwayBean = ObjectFactory
					.createProductColourway();

			colorwayBean.setLastModifiedTimestamp(getValue(skuMap
					.get(BurConstant.MODIFY)));
			colorwayBean.setId(getValue(skuMap
					.get(BurConstant.BUR_SAP_MATERIAL_NUMBER)));
			colorwayBean.setLegacyId(getValue(skuMap
					.get(BurConstant.BUR_LEGACY_PLMID)));
			colorwayBean.setCheckDescription(getValue(skuMap
					.get(BurConstant.BUR_CHECK_DESCRIPTION)));
			colorwayBean.setColourCode(getValue(skuMap
					.get(BurConstant.BUR_COLOUR_CODE)));
			colorwayBean.setColourGroup(getValue(skuMap
					.get(BurConstant.BUR_COLOUR_GROUP)));
			colorwayBean.setExitStrategy(getValue(skuMap
					.get(BurConstant.BUR_EXIT_ROUTE)));
			colorwayBean
					.setGender(getValue(skuMap.get(BurConstant.BUR_GENDER)));
			colorwayBean.setHeritage(getValue(skuMap
					.get(BurConstant.BUR_HERITAGE)));
			colorwayBean
					.setIconic(getValue(skuMap.get(BurConstant.BUR_ICONIC)));
			colorwayBean.setIntent(getValue(skuMap
					.get(BurConstant.BUR_DESIGN_INTENT_CHECK)));
			colorwayBean.setIP(getValue(skuMap.get(BurConstant.BUR_IP)));
			colorwayBean.setMarketingColour(getValue(skuMap
					.get(BurConstant.NAME)));
			colorwayBean.setPersonalisable(getValue(skuMap
					.get(BurConstant.BUR_PERSONALISATION)));
			colorwayBean.setPrintPattern(getValue(skuMap
					.get(BurConstant.BUR_PRINT_PATTERN)));
			colorwayBean.setRetailEndDate(getValue(skuMap
					.get(BurConstant.BUR_RETAIL_END_DATE)));
			colorwayBean.setRetailStartDate(getValue(skuMap
					.get(BurConstant.BUR_RETAIL_START_DATE)));	
			colorwayBean.setTreatment(getValue(skuMap
					.get(BurConstant.BUR_TREATMENT)));
			colorwayBean.setLegacyColourCode(getValue(skuMap
					.get(BurConstant.BUR_LEGACYCOLOURCODE)));

			// Phase 3 Sprint 2 September
			colorwayBean.setColourType(getValue(skuMap
					.get(BurConstant.BUR_COLOURTYPE)));
					
			logger.debug(methodName + "PRODUCT colorwayBean: " + colorwayBean);
			skuBean= colorwayBean;
		}
		logger.info(methodName + "skuBean: " + skuBean);
		return skuBean;
	}

	/**
	 * Check for null and empty string.
	 * 
	 *
	 *
	 * 
	 * @param obj
	 *            Object
	 * @return String
	 */
	private static String getValue(final Object obj) {
		return (obj != null && !obj.toString().trim().isEmpty()) ? obj
				.toString().trim() : null;
	}

	/**
	 * create and return updated Season bean.
	 * 
	 * Method responsible to create an empty bean object using ObjectFactory and
	 * set bean object property by using given Map.
	 *
	 *
	 *
	 * Map contains all the data extracted from DB for this Particular bean .
	 *
	 *
	 * Each bean object will be filled with the data in map. Each value is
	 * mapped to a key and to retrieve value you must pass a valid key.
	 *
	 *
	 *
	 * 
	 * @param seasonMap
	 * @param skuMap
	 * @param beanType
	 * @return Object
	 */
	private static Object setSeasontData(final Map<String, Object> seasonMap,
			final BeanType beanType) {
		
		String methodName = "setSeasontData() ";
		
		Object skuseasonBean=null;
		logger.debug(methodName + "seasonMap: " + seasonMap);
		
		//setting
		//seasonBean 
		//for pricing
		//data
		//from Map to Bean
		if (beanType.equals(BeanType.PRICING)) {

			final PricingSeason seasonBean = ObjectFactory
					.createPricingSeason();
			seasonBean.setLastModifiedTimestamp(getValue(seasonMap
					.get(BurConstant.MODIFY)));
			seasonBean.setCollection(getValue(seasonMap
					.get(BurConstant.BUR_COLLECTION_MARKET)));
			seasonBean.setTheme(getValue(seasonMap
					.get(BurConstant.BUR_THEME_FLOORSET)));
			seasonBean.setFsr(getValue(seasonMap
					.get(BurConstant.BUR_FSR_STATUS)));
			seasonBean.setVendorId(getValue(seasonMap
					.get(BurConstant.BUR_SAP_VENDOR_ID)));
			seasonBean.setVendorName(getValue(seasonMap
					.get(BurConstant.SUPPLIER_NAME)));
			seasonBean.setSeasonId(getValue(seasonMap
					.get(BurConstant.BUR_SEASON)));
			seasonBean.setVendorCountry(getValue(seasonMap
					.get(BurConstant.BUR_ISO)));
			seasonBean.setStatus(getValue(seasonMap
					.get(BurConstant.BUR_COLOURWAY_STATUS)));
			seasonBean.setCarryForward(getValue(seasonMap
					.get(BurConstant.BUR_CARRYFORWARD_COLOURWAY)));

			logger.debug(methodName + "PRICING seasonBean: " + seasonBean);					
			skuseasonBean= seasonBean;
		} 
		//setting
		//seasonBean 
		//for product
		//data
		//from Map to Bean
		else if (beanType.equals(BeanType.PRODUCT)) {

			final Season seasonBean = ObjectFactory.createProductSeason();

			seasonBean.setLastModifiedTimestamp(getValue(seasonMap
					.get(BurConstant.MODIFY)));
			seasonBean.setCollection(getValue(seasonMap
					.get(BurConstant.BUR_COLLECTION_MARKET)));
			seasonBean.setTheme(getValue(seasonMap
					.get(BurConstant.BUR_THEME_FLOORSET)));
			seasonBean.setFsr(getValue(seasonMap
					.get(BurConstant.BUR_FSR_STATUS)));
			seasonBean.setVendorId(getValue(seasonMap
					.get(BurConstant.BUR_SAP_VENDOR_ID)));
			seasonBean.setVendorName(getValue(seasonMap
					.get(BurConstant.SUPPLIER_NAME)));
			seasonBean.setAdCampaign(getValue(seasonMap
					.get(BurConstant.BUR_AD_CAMPAIGN)));
			seasonBean.setBrandBuy(getValue(seasonMap
					.get(BurConstant.BUR_BRAND_BUY)));
			seasonBean.setCapsule(getValue(seasonMap
					.get(BurConstant.BUR_CAPSULE)));
			seasonBean.setCarryForward(getValue(seasonMap
					.get(BurConstant.BUR_CARRYFORWARD_COLOURWAY)));
			seasonBean.setCarryOutCode(getValue(seasonMap
					.get(BurConstant.BUR_CARRY_OUT_CODING)));
			seasonBean.setClimate(getValue(seasonMap
					.get(BurConstant.BUR_CLIMATE)));
			seasonBean.setEndDate(getValue(seasonMap
					.get(BurConstant.BUR_RETAIL_SEASON_END_DATE)));
			seasonBean.setFixture(getValue(seasonMap
					.get(BurConstant.BUR_FIXTURE)));
			seasonBean.setGsr(getValue(seasonMap.get(BurConstant.BUR_GSR)));
			seasonBean.setId(getValue(seasonMap.get(BurConstant.BUR_SEASON)));
			seasonBean.setKeyDriver(getValue(seasonMap
					.get(BurConstant.BUR_KEY_DRIVER)));
			seasonBean.setLimitedAvailability(getValue(seasonMap
					.get(BurConstant.BUR_LIMITED_AVAILABILITY)));
			seasonBean.setOvermakes(getValue(seasonMap
					.get(BurConstant.BUR_OVER_MAKES)));
			seasonBean.setStartDate(getValue(seasonMap
					.get(BurConstant.BUR_COLOURWAY_LAUNCH_DATE)));
			seasonBean.setStatus(getValue(seasonMap
					.get(BurConstant.BUR_COLOURWAY_STATUS)));
			seasonBean.setStillValidReason(getValue(seasonMap
					.get(BurConstant.BUR_STILL_VALID_REASON)));
			seasonBean.setTheme(getValue(seasonMap
					.get(BurConstant.BUR_THEME_FLOORSET)));
			seasonBean.setCoo(getValue(seasonMap
					.get(BurConstant.BUR_ISO)));
					
			// Phase 3 Sprint 2 September		
			seasonBean.setLookBook(getValue(seasonMap
					.get(BurConstant.BUR_LOOKBOOK)));

			//Phase 3 Sprint 5 January JIRA BURBERRY-937
			seasonBean.setRunway(getValue(seasonMap.get(BurConstant.BUR_RUNWAY)));
			
			//Phase 3 Sprint 8 JIRA BURBERRY-1396
			seasonBean.setCore(getValue(seasonMap.get(BurConstant.BUR_CORE)));
			
			logger.debug(methodName + "PRODUCT seasonBean: " + seasonBean);					
			skuseasonBean= seasonBean;
		}
		logger.info(methodName + "skuseasonBean: " + skuseasonBean);
		return skuseasonBean;
	}

	/**
	 * create and return Product style object.
	 * 
	 * Method responsible to create an empty bean object using ObjectFactory and
	 * set bean object property by using given Map.
	 *
	 *
	 *
	 * Map contains all the data extracted from DB for this Particular bean .
	 *
	 *
	 * Each bean object will be filled with the data in map. Each value is
	 * mapped to a key and to retrieve value you must pass a valid key.
	 *
	 * 
	 * @param productMap
	 * @param skuMaps
	 * @param skuseasonMaps
	 * @param endDate 
	 * @param startDate 
	 * @return Products.Style
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 */
	public static Products.Style putProductData(
			final Map<String, Object> productMap,
			final List<Map<String, Object>> skuMaps,
			final List<Map<String, Object>> skuseasonMaps) throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String methodName = "putProductData() ";
		Style styleBean = null;
		logger.debug(methodName + "skuMaps: " + skuMaps);
		logger.debug(methodName + "skuseasonMaps: " + skuseasonMaps);

		styleBean = (Products.Style) putStyleData(productMap, BeanType.PRODUCT);
		final Dimensions dimBean = putDimensionData(productMap);
		final PlanningAttributes planBean = putPlanData(productMap);
		final Iterator<Map<String, Object>> skuItr = skuMaps.iterator();
		final Colourways colourways = ObjectFactory.createProductColourways();
		Colourway colourway = ObjectFactory.createProductColourway();
		while (skuItr.hasNext()) {
			final Seasons seasons = ObjectFactory.createProductSeasons();
			final Map<String, Object> skuMap = skuItr.next();
			colourway = (Colourway) setColourwaysData(skuMap, BeanType.PRODUCT);
			colourways.getColourway().add(colourway);
			final Iterator<Map<String, Object>> skuSeasonItr = skuseasonMaps
					.iterator();
			while (skuSeasonItr.hasNext()) {
				final Map<String, Object> seasonMap = skuSeasonItr.next();
				if (skuMap.get(BurConstant.NAME).equals(
						seasonMap.get(BurConstant.NAME))) {
					final Products.Style.Colourways.Colourway.Seasons.Season season = (Season) setSeasontData(
							seasonMap, BeanType.PRODUCT);
					if (FormatHelper.hasContent(getValue(seasonMap
							.get(BurConstant.BUR_SWING_TICKET_ID)))) {
						styleBean.setSwingTicketType(getValue(seasonMap
								.get(BurConstant.BUR_SWING_TICKET_ID)));
					}
					if (FormatHelper.hasContent(getValue(seasonMap
							.get(BurConstant.PM_QUANTITY)))) {
						styleBean.setFabricConsumption(getValue(seasonMap
								.get(BurConstant.PM_QUANTITY)));
					}
					validationForRequiredAttributes(season);
					seasons.getSeason().add(season);
					colourway.setSeasons(seasons);
					validationForRequiredAttributes(colourway);
				}
			}

		}
		validationForRequiredAttributes(dimBean);
		validationForRequiredAttributes(planBean);
		styleBean.setColourways(colourways);
		styleBean.setDimensions(dimBean);
		styleBean.setPlanningAttributes(planBean);
		validationForRequiredAttributes(styleBean);
		logger.info(methodName + "styleBean: " + styleBean);	
		return styleBean;
	}

	/**
	 * create and return Planning bean.
	 * 
	 * Method responsible to create an empty bean object using ObjectFactory and
	 * set bean object property by using given Map. Map contains all the data
	 * extracted from DB for this Particular bean .
	 *
	 *
	 * Each bean object will be filled with the data in map. Each value is
	 * mapped to a key and to retrieve value you must pass a valid key.
	 *
	 * 
	 * @param productMap
	 * @return PlanningAttributes
	 */
	private static PlanningAttributes putPlanData(
			final Map<String, Object> productMap) {
		
		String methodName = "putPlanData() ";
		logger.debug(methodName + "productMap: " + productMap);
		
		//setting
		//planBean 
		//for product
		//data
		//from Map to Bean
		final PlanningAttributes planBean = ObjectFactory
				.createProductPlanningAttributes();
		planBean.setLength(getValue(productMap.get(BurConstant.BUR_LENGTH_APP)));
		planBean.setPlanGroup(getValue(productMap
				.get(BurConstant.BUR_PLAN_GROUP)));
		planBean.setSleeve(getValue(productMap.get(BurConstant.BUR_SLEEVE)));
		logger.debug(methodName + "planBean: " + planBean);
		return planBean;
	}

	/**
	 * Create and return Dimension bean.
	 * 
	 * Method responsible to create an empty bean object using ObjectFactory and
	 * set bean object property by using given Map. Map contains all the data
	 * extracted from DB for this Particular bean .
	 *
	 * Each bean object will be filled with the data in map. Each value is
	 * mapped to a key and to retrieve value you must pass a valid key.
	 * 
	 * @param productMap
	 * @return Dimensions
	 */
	private static Dimensions putDimensionData(
			final Map<String, Object> productMap) {
		
		String methodName = "putDimensionData() ";
		
		//setting
		//dimBean 
		//for product
		//data
		//from Map to Bean
		final Dimensions dimBean = ObjectFactory.createProductsDimensions();
		dimBean.setHeightCM(getValue(productMap.get(BurConstant.BUR_HEIGHT)));
		dimBean.setWidthCM(getValue(productMap.get(BurConstant.BUR_WIDTH)));
		dimBean.setLengthCM(getValue(productMap.get(BurConstant.BUR_LENGTH)));
		logger.debug(methodName + "dimBean: " + dimBean);
		return dimBean;
	}

	/**
	 * Return Pricing style object.
	 * 
	 * Method responsible to create an empty bean object using ObjectFactory and
	 * set bean object property by using given Map. Map contains all the data
	 * extracted from DB for this Particular bean .
	 * 
	 * Each bean object will be filled with the data in map. Each value is
	 * mapped to a key and to retrieve value you must pass a valid key.
	 *
	 * 
	 * @param productMap
	 * @param listOfSkuMaps
	 * @param listOfSkuseasonMaps
	 * @param listOfskucostMaps 
	 * @return Pricing.PricingStyle
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 */
	public static Pricing.PricingStyle putPricingData(
			final Map<String, Object> productMap,
			final List<Map<String, Object>> listOfSkuMaps,
			final List<Map<String, Object>> listOfSkuseasonMaps) throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		String methodName = "putPricingData() ";
		PricingStyle styleBean = null;
		logger.debug(methodName + "listOfSkuMaps: " + listOfSkuMaps);
		logger.debug(methodName + "listOfSkuseasonMaps: " + listOfSkuseasonMaps);

		styleBean = (Pricing.PricingStyle) putStyleData(productMap,
				BeanType.PRICING);
		final Iterator<Map<String, Object>> skuItr = listOfSkuMaps.iterator();
		final PricingColourways colourways = ObjectFactory
				.createPricingColourways();
		PricingColourway colourway = ObjectFactory.createPricingColourway();
		while (skuItr.hasNext()) {
			final PricingSeasons seasons = ObjectFactory.createPricingSeasons();
			final Map<String, Object> skuMap = skuItr.next();
			colourway = (PricingColourway) setColourwaysData(skuMap,
					BeanType.PRICING);
			colourways.getColourway().add(colourway);
			final Iterator<Map<String, Object>> skuSeasonItr = listOfSkuseasonMaps
					.iterator();
			while (skuSeasonItr.hasNext()) {
				final Map<String, Object> seasonMap = skuSeasonItr.next();
				if (skuMap.get(BurConstant.NAME).equals(
						seasonMap.get(BurConstant.NAME))) {
					final Pricing.PricingStyle.PricingColourways.PricingColourway.PricingSeasons.PricingSeason season = (PricingSeason) setSeasontData(
							seasonMap, BeanType.PRICING);
					final List<Map<String, Object>> listcostsheet = (List<Map<String, Object>>) seasonMap.get("costsheets");
					logger.debug(methodName+"listcostsheet "+listcostsheet.size());
					season.getColourwayCost().addAll(setCostsheetData(listcostsheet,seasonMap));
					validationForRequiredAttributes(season);
					seasons.getSeason().add(season);
					colourway.setSeasons(seasons);
					validationForRequiredAttributes(colourway);
				}

			}

		}

		styleBean.setColourways(colourways);
		validationForRequiredAttributes(styleBean);
		logger.debug(methodName + "styleBean: " + styleBean);
		return styleBean;
	}
	private static Collection<? extends ColourwayCost> setCostsheetData(
			List<Map<String, Object>> listcostsheet,
			Map<String, Object> seasonMap) {
		List<ColourwayCost> cost=new ArrayList<ColourwayCost>();
		logger.debug("costsheet list "+listcostsheet);
		for( Map<String, Object> costMap : listcostsheet){
			if(seasonMap.get(BurConstant.SEASON_NAM).equals(costMap.get(BurConstant.SEASON_NAM))){
				final Pricing.PricingStyle.PricingColourways.PricingColourway.PricingSeasons.PricingSeason.ColourwayCost costBean = setskucost(
						costMap,seasonMap);
				cost.add(costBean);
			}
		}
		return cost;
	}

	private static ColourwayCost setskucost(Map<String, Object> skucostMap, Map<String, Object> seasonMap) {
		ColourwayCost cost = new ColourwayCost();
		cost.setFinishedGoodsCost(getValue(skucostMap
				.get(BurConstant.BUR_VENDORFGCOST)));
		cost.setFinishedGoodsCostCurrency(getValue(seasonMap
				.get(BurConstant.BUR_PARTNER_TRADING_CURRENCY)));
		cost.setLandedCostGBP(getValue(skucostMap.get(BurConstant.VRD_ELC)));
		cost.setAdjustedRetailEUR(getValue(seasonMap
				.get(BurConstant.BUR_ADJUSTED_EUR_RETAIL)));
		cost.setAdjustedRetailHKD(getValue(seasonMap
				.get(BurConstant.BUR_ADJUSTED_HKD_RETAIL)));
		cost.setAdjustedRetailUSD(getValue(seasonMap
				.get(BurConstant.BUR_ADJUSTED_USD_RETAIL)));
		cost.setActualRetailGBP(getValue(seasonMap
				.get(BurConstant.BUR_PRODUCTSEASONACTUALRETAILGBP)));

		// Phase 3 Sprint 1 August
		cost.setFinishedGoodsCostGBP(getValue(skucostMap
				.get(BurConstant.BUR_FGCOSTGBP)));

		// Phase 3 Sprint 2 September
		cost.setFreightPercentage(getValue(skucostMap
				.get(BurConstant.BUR_FREIGHTPERCENTOFFGCOSTGBP)));
		cost.setDutyPercentage(getValue(skucostMap
				.get(BurConstant.BUR_PRODUCTDUTYRATE)));
		cost.setCostScenarioCOO(getValue(skucostMap
				.get(BurConstant.BUR_COSTSCENARIOCOO)));
		
		cost.setCostSheetNo(getValue(skucostMap
				.get(BurConstant.VRD_CS_NUM)));

		return cost;
	}

	static void validationForRequiredAttributes(Object obj) throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		java.lang.reflect.Field[] field=obj.getClass().getDeclaredFields();
		for(int i=0;i<field.length;i++){
			java.lang.reflect.Field f=field[i];
			f.setAccessible(true);
			XmlElement m=(XmlElement)f.getAnnotation(XmlElement.class);
			if(m!=null && (f.get(obj)==null)){
				f.set(obj, EMPTY_STRING);
			}
		}
		
	}
}

package com.burberry.wc.integration.streamline.sourcingconfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.burberry.wc.integration.streamline.image.BRCreateImage;
import com.burberry.wc.integration.streamline.util.BRJSONValidationException;
import com.burberry.wc.integration.streamline.util.BRStreamlineAPIHelper;
import com.burberry.wc.integration.streamline.util.BRStreamlineConstants;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductHelper;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecLogic;
import com.lcs.wc.specification.FlexSpecMaster;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;

public class BRCreateSourcingCofig {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(BRCreateSourcingCofig.class);

	public static final String FLEXSPEC_FOLDERLOCATION = LCSProperties
			.get("com.lcs.wc.specification.FlexSpecification.rootFloder", "/Specification");

	/**
	 * 
	 * @param product
	 * @param lcsSeason
	 * @param scjson
	 * @param imageArray
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public HashMap CreateSource(LCSProduct product, final LCSSeason lcsSeason, final JSONObject scjson,
			final JSONArray imageArray) {
		LCSSourcingConfigLogic srcConfigLogic = new LCSSourcingConfigLogic();
		LCSPartMaster productMaster = (LCSPartMaster) product.getMaster();
		LCSSeasonMaster seasonMaster = product.getSeasonMaster();
		ArrayList<String> imagePageIDA2A2List = new ArrayList<String>();
		LCSSourcingConfig sc;
		String error = null;
		final HashMap returnMap = new HashMap();

		try {
			sc = LCSSourcingConfigQuery.getPrimarySource(productMaster, seasonMaster);

			if (sc != null && sc.isPrimarySource()) {

				LOGGER.debug("BRCreateSourcingCofig ::	Sourcing Config : " + sc.getName());

				LCSSourcingConfig srcConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(sc);
				LOGGER.debug("BRCreateSourcingCofig ::	SRC OBJECT : " + srcConfig + "   NAME : " + srcConfig.getName()
						+ "  is iteration latest : " + VersionHelper.isLatestVersion(srcConfig) + "  is CHECKED OUT : "
						+ VersionHelper.isCheckedOut(srcConfig) + " SRC CONFIG VERSION : "
						+ srcConfig.getIterationDisplayIdentifier());

				// boolean checkout = false;

				LOGGER.debug("BRCreateSourcingCofig ::	Sourcing Config is : " + scjson);

				JSONArray sckeys = scjson.names();
				for (int i = 0; i < sckeys.length(); ++i) {
					String key = sckeys.getString(i);
					LOGGER.debug("BRCreateSourcingCofig ::	key : " + key + " : " + "value : " + scjson.opt(key));

					srcConfig = setValuesOfSourcingConfig(key, scjson.optString(key), srcConfig);
				}

				srcConfigLogic.saveSourcingConfig(srcConfig, true);
				product = LCSProductHelper.service.saveProduct(product);
				LOGGER.debug("BRCreateSourcingCofig ::	Creating Specification after saving Source");

				imagePageIDA2A2List = CreateSpecification(product, lcsSeason, srcConfig, imageArray);
			} else {
				error = "SourcingConfig is not created due to absence of primary source";
				returnMap.put("Sourcingerror", error);
			}

		} catch (Exception e) {
			LOGGER.debug("BRCreateSourcingCofig ::	Error: " + e.getLocalizedMessage());
		}
		returnMap.put("imagePageIDA2A2List", imagePageIDA2A2List);
		LOGGER.debug("BRCreateSourcingCofig ::   response is : " + imagePageIDA2A2List);
		return returnMap;
	}

	public LCSSourcingConfig setValuesOfSourcingConfig(String key, String value, LCSSourcingConfig srcConfig) {

		try {
			if (BRStreamlineConstants.VENDOR.equalsIgnoreCase(key)) {
				String vendorid = value;
				int vendorID = Integer.parseInt(vendorid);
				LCSSupplier supplier = BRStreamlineAPIHelper.getVendorFromID(vendorID);
				srcConfig.setValue(key, supplier);

				srcConfig.setSourcingConfigName(supplier.getName());
				srcConfig.setValue(BRStreamlineConstants.SUPPLIER_NAME, supplier.getName());

			} else if (BRStreamlineConstants.VRD_COUNTRY_OF_ORIGIN.equalsIgnoreCase(key)) {
				String countryid = value;
				int countryID = Integer.parseInt(countryid);
				LCSCountry country = BRStreamlineAPIHelper.getCountryFromID(countryID);
				srcConfig.setValue(key, country);

			} else {
				srcConfig.setValue(key, value);

			}

		} catch (Exception e) {
			LOGGER.debug("BRCreateSourcingCofig ::	Error: " + e.getLocalizedMessage());

		}
		return srcConfig;

	}

	/**
	 * 
	 * @param lcsPdt
	 * @param lcsSeason
	 * @param lcsSource
	 * @param imageArray
	 * @return
	 */
	public ArrayList<String> CreateSpecification(final LCSProduct lcsPdt, final LCSSeason lcsSeason,
			final LCSSourcingConfig lcsSource, final JSONArray imageArray) {
		String error = null;
		FlexSpecification flexSpec = null;
		LCSDocument imagePage = null;
		ArrayList<String> imagePageIDA2A2List = new ArrayList<String>();
		String imagePageIDA2A2 = null;
		BRCreateImage image = new BRCreateImage();
		String validJson = null;
		try {
			LOGGER.debug("BRCreateSourcingCofig ::	got details of : " + lcsPdt.getNumber() + " " + lcsSeason.getName()
					+ " " + lcsSource.getName() + " " + imageArray);
			SearchResults srSpecResults = FlexSpecQuery.findExistingSpecs(lcsPdt, lcsSeason, lcsSource);
			LOGGER.debug("BRCreateSourcingCofig ::	SpecResults Size : " + (srSpecResults.getResults()).size());
			if ((srSpecResults.getResults()).size() == 0) {
				FlexSpecLogic flexSpecLogic = new FlexSpecLogic();
				flexSpec = FlexSpecification.newFlexSpecification();
				FlexType specFlexType = null;
				String name = null;
				LOGGER.debug("BRCreateSourcingCofig ::	FlexType : " + lcsPdt.getFlexType().getFullName().toString());
				if (lcsPdt.getFlexType().getFullName().toString().toLowerCase()
						.contains(BRStreamlineConstants.FLEXTYPE_ACCESSORIES)) {
					specFlexType = FlexTypeCache.getFlexTypeFromPath(BRStreamlineConstants.SPECFLEXTYPE_ACCESSORIES);
					name = BRStreamlineConstants.SPECNAME_ACCESSORIES;

				} else if (lcsPdt.getFlexType().getFullName().toString().toLowerCase()
						.contains(BRStreamlineConstants.FLEXTYPE_APPAREL)) {
					specFlexType = FlexTypeCache.getFlexTypeFromPath(BRStreamlineConstants.SPECFLEXTYPE_APPAREL);
					name = BRStreamlineConstants.SPECNAME_APPAREL;

				} else if (lcsPdt.getFlexType().getFullName().toString().toLowerCase()
						.contains(BRStreamlineConstants.FLEXTYPE_FOOTWEAR)) {
					specFlexType = FlexTypeCache.getFlexTypeFromPath(BRStreamlineConstants.SPECFLEXTYPE_FOOTWEAR);
					name = BRStreamlineConstants.SPECNAME_FOOTWEAR;

				}
				LOGGER.debug("BRCreateSourcingCofig ::	specFlexType : " + specFlexType.getFullName());
				flexSpec.setFlexType(specFlexType);
				LCSLogic.assignFolder(FLEXSPEC_FOLDERLOCATION, flexSpec);
				flexSpec.setMaster(new FlexSpecMaster());
				flexSpec.setSpecOwner(lcsPdt.getMaster());
				flexSpec.setSpecSource(lcsSource.getMaster());
				flexSpec.setValue("specName", flexSpec.getName() + name);
				flexSpecLogic.saveSpec(flexSpec, true);
				FlexSpecToSeasonLink link = flexSpecLogic.addSpecToSeason(flexSpec.getMaster(), lcsSeason.getMaster());
				if (link != null) {

					LOGGER.debug("BRCreateSourcingCofig ::	flexSpec" + flexSpec.getName());
					LOGGER.debug("BRCreateSourcingCofig :: " + "is FLexLink primary : " + link.isPrimarySpec());

					if (imageArray != null) {
						for (int i = 0; i < imageArray.length(); ++i) {
							final JSONObject imagePageObj = imageArray.getJSONObject(i);
							LOGGER.debug("BRCreateSourcingCofig :: " + "ImagePage JSON : " + imagePageObj);
							try {
								BRStreamlineAPIHelper.ValidateJson(imagePageObj,
										BRStreamlineConstants.CREATE_IMAGE_REQ_ATTS);
							} catch (BRJSONValidationException e) {

								imagePageIDA2A2List
								.add("JSON for "+ imagePageObj.optString("imageName") +" is missing a required attribute "
										);
							}

							imagePageIDA2A2 = null;
							imagePage = null;
							final File file = image.saveImage(imagePageObj.optString(BRStreamlineConstants.IMAGEURL),
									imagePageObj.optString(BRStreamlineConstants.IMAGENAME));
							if (file != null && file.exists()) {
								imagePage = image.createImagePage(lcsPdt, file, imagePageObj);
							}

							if (imagePage != null) {

								imagePageIDA2A2 = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(imagePage));
								LOGGER.debug("BRCreateSourcingCofig ::	imagePage IDA2A2 : " + imagePageIDA2A2);
								final FlexSpecToComponentLink specCompLink = flexSpecLogic.addComponentToSpec(flexSpec,
										(WTObject) imagePage);
								LOGGER.debug("BRCreateSourcingCofig ::	specCompLink : " + specCompLink);
								LOGGER.debug("BRCreateSourcingCofig ::	specCompLink component : "
										+ specCompLink.getComponent());
								imagePageIDA2A2List.add(imagePageIDA2A2);

							} else {
								imagePageIDA2A2 = " ";
								imagePageIDA2A2List.add(imagePageIDA2A2);
							}

						}

					}

					LOGGER.debug("BRCreateSourcingCofig ::	After creation of Specification");
				} else {
					error = "Specification could not be added to the season";
					imagePageIDA2A2List.add(error);

				}

			}
		} catch (Exception e) {
			// e.printStackTrace();
			LOGGER.debug("BRCreateSourcingCofig ::	Error: " + e.getLocalizedMessage());
		}
		LOGGER.debug("BRCreateSourcingCofig ::   response is : " + imagePageIDA2A2List);
		return imagePageIDA2A2List;

	}

}

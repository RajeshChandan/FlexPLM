package com.burberry.wc.integration.streamline.bom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.burberry.wc.integration.streamline.util.BRJSONValidationException;
import com.burberry.wc.integration.streamline.util.BRStreamlineAPIHelper;
import com.burberry.wc.integration.streamline.util.BRStreamlineConstants;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flexbom.StandardFlexBOMService;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecLogic;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

@Path("/BurberryStreamline")
@Api(value = "/BOM")
public class BRCreateBOM {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(BRCreateBOM.class);
	/**
	 * DIM_COL.
	 */
	public static final String DIM_COL = "FLEXBOMLINK.DIMENSIONNAME";
	/**
	 * DIMID_COL.
	 */
	public static final String DIMID_COL = "FLEXBOMLINK.DIMENSIONID";

	@SuppressWarnings("unchecked")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("/CreateBOM")
	@ApiOperation(value = "POST operation on creating BOM in FlexPLM", notes = "Creates BOM", response = String.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "CSRF_NONCE", value = "The CSRF nonce as returned from the CSRF Protection endpoint.", required = true, dataType = "string", paramType = "header") })

	public Object createBom(@ApiParam(name = "JsonString", value = "The JSON String for processing") String paramString)
			throws Exception {
		String bomID = null;

		LOGGER.debug(" The Value Entered : " + paramString);

		JSONObject jsonObject;
		JSONObject scjson;
		LCSSeason season = null;
		LCSProduct product = null;
		String bomType = "BOM\\Materials\\Product\\";
		FlexSpecification specification = null;
		Collection components = null;
		String validJson = null;
		FlexBOMPart flexBOMPart = null;
		final HashMap returnMap = new HashMap();
		Map<Status, Object> response = new HashMap<Status, Object>();
		String error = null;
		StandardFlexBOMService service = new StandardFlexBOMService();
		ArrayList specIDList = new ArrayList();
		String bType = "MAIN";
		LCSSourcingConfig sc = null;
		LCSSourcingConfig srcConfig = null;
		String pid = null;
		String flexTypestr = null;
		String sid = null;
		LCSPartMaster productMaster = null;
		LCSSeasonMaster seasonMaster = null;
		FlexType bomFlexType = null;

		try {

			jsonObject = new JSONObject(paramString);
			BRStreamlineAPIHelper.ValidateJson(jsonObject, BRStreamlineConstants.CREATE_BOM_REQ_ATTS);
		} catch (BRJSONValidationException e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("JSON MISSING REQUIRED FIELD").build());
		}catch (JSONException e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("Invalid JSON String").build());
		}

		JSONArray keys = jsonObject.names();
		

			try {
				// Get product
				pid = (String) jsonObject.get(BRStreamlineConstants.BUR_STYLE_ID);
				flexTypestr = (String) jsonObject.opt(BRStreamlineConstants.PRODUCT_FLEX_TYPE);
				int styleId = Integer.parseInt(pid);
				product = BRStreamlineAPIHelper.getProductFromID(styleId, flexTypestr);

				if (flexTypestr.toLowerCase().contains(BRStreamlineConstants.APPAREL))
					bomType = bomType + "Apparel";
				else if (flexTypestr.toLowerCase().contains(BRStreamlineConstants.ACCESSORIES))
					bomType = bomType + "burAccessories";
				else if (flexTypestr.toLowerCase().contains(BRStreamlineConstants.FOOTWEAR))
					bomType = bomType + "Footwear";
				LOGGER.debug("bomType : " + bomType);
				bomFlexType = FlexTypeCache.getFlexTypeFromPath(bomType);
				LOGGER.debug("BRCreateBOM :: bomFlexType :: " + bomFlexType);

				// Get season
				sid = (String) jsonObject.get(BRStreamlineConstants.SEASON_ID);
				int seasonId = Integer.parseInt(sid);
				season = BRStreamlineAPIHelper.getSeasonFromID(seasonId);

				// Get source
				productMaster = (LCSPartMaster) product.getMaster();
				seasonMaster = product.getSeasonMaster();
				sc = LCSSourcingConfigQuery.getPrimarySource(productMaster, seasonMaster);
				srcConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(sc);

				// Get spec
				specification = BRStreamlineAPIHelper.getSpecification(product, season, srcConfig);

			} catch (Exception e) {

				LOGGER.debug("Error: " + e.getLocalizedMessage());
			}

			if (product != null) {
				LOGGER.debug("BRCreateBOM ::product :: " + product);

				if (season != null) {
					LOGGER.debug("BRCreateBOM :: season :: " + season.getName());

					if (sc != null) {
						LOGGER.debug("BRCreateBOM ::Sourcing Config : " + sc.getName());

						LOGGER.debug("BRCreateBOM :: srcConfig is :" + srcConfig);

						if (srcConfig != null) {

							if (specification != null) {

								LOGGER.debug("BRCreateBOM :: specification :" + specification.getName());

								String specId = "OR:" + specification;
								LOGGER.debug("BRCreateBOM :: specId :::: " + specId);
								specIDList.add(specId);

								LOGGER.debug("BRCreateBOM :: specIDList : " + specIDList);

								components = FlexSpecQuery.getSpecComponents(specification, "BOM");
								if (components.isEmpty()) {
									LOGGER.debug("COMPONENT LIST IS NULL CREATING NEW BOM");
									LOGGER.debug("BRCreateBOM :: before initiating : bomFlexType::::" + bomFlexType);
									flexBOMPart = service.initiateBOMPart(product, bomFlexType, bType, specIDList);
									LOGGER.debug("BRCreateBOM :: After initiating bompart: " + flexBOMPart);
									FlexSpecLogic specLogic = new FlexSpecLogic();
									if (flexBOMPart != null) {
										FlexSpecToComponentLink speclink = specLogic.addBOMToSpec(specification,
												flexBOMPart, null, true);
										if (speclink != null) {
											FlexSpecLogic.setAsPrimaryBOM(speclink);
											LOGGER.debug("BRCreateBOM :: flexBOMPart:: " + flexBOMPart.getName());
											createNewBOMLinks(flexBOMPart, jsonObject);
										}

										bomID = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(flexBOMPart));
									}

									LOGGER.debug("BRCreateBOM :: bomID::" + bomID);
									returnMap.put("BOMID", bomID);

								} else {
									LOGGER.debug("BOM EXISTS::DELETING BOM LINKS");
									Iterator iterator = components.iterator();
									while (iterator.hasNext()) {
										Object objBOMPart = iterator.next();
										if (objBOMPart != null && objBOMPart instanceof FlexBOMPart) {
											flexBOMPart = (FlexBOMPart) objBOMPart;
											if (!"LABOR".equals(flexBOMPart.getBomType()))
												LOGGER.debug("BRCreateBOM ::  EXISTING flexBOMPart :: " + flexBOMPart);
											FlexSpecLogic specLogic = new FlexSpecLogic();
											boolean isPrimaryBOM = specLogic.isPrimaryComponent(specification,
													flexBOMPart);
											if (isPrimaryBOM == true) {

												Collection<FlexBOMLink> bomLinkColl = null;
												bomLinkColl = LCSFlexBOMQuery.findFlexBOMLinks(flexBOMPart, null, null,
														null, null, null, LCSFlexBOMQuery.WIP_ONLY, null, false, null,
														null, null, null);

												LOGGER.debug(" BRCreateBOM ::  BOM Link collection Size ::  "
														+ bomLinkColl.size());
												LCSFlexBOMLogic logic = new LCSFlexBOMLogic();
												LOGGER.debug("BRCreateBOM ::  DELETING OLD BOMLINKS:::: ");
												logic.deleteLinks(bomLinkColl);
												LOGGER.debug("BRCreateBOM ::  CREATING NEW BOMLINKS::: ");
												createNewBOMLinks(flexBOMPart, jsonObject);

												bomID = FormatHelper
														.getNumericFromOid(FormatHelper.getObjectId(flexBOMPart));
												LOGGER.debug("BRCreateBOM ::  bomID ::" + bomID);
												returnMap.put("BOMID", bomID);
											}
										}

									}

								}
							} else {
								error = "Could not find Specification for the given styleID: " + pid + " and seasonID: "
										+ sid;
							}
						}
					} else {
						error = "Could not find primary source for the given styleID: " + pid + " and seasonID: " + sid;
					}
				} else {
					error = "Could not find season with given seasonID: " + sid;
				}

			} else {
				error = "Could not find product with given styleID: " + pid + " and Type: " + flexTypestr;
			}

		

		if (error != null)
			returnMap.put("error", error);
		response = returnMap;
		LOGGER.debug("final response is : " + response);
		return response;

	}

	private void createNewBOMLinks(FlexBOMPart flexBOMPart, JSONObject jsonObject)
			throws WTException, WTPropertyVetoException, JSONException {
		// TODO Auto-generated method stub
		JSONArray BOMLinkArray = null;
		JSONArray sectionVariationArray = null;
		JSONArray BOMArray = null;
		String section = null;

		if ("MAIN".equals(flexBOMPart.getBomType())) {

			// FlexTypeAttribute sectionAtt =
			// flexBOMPart.getFlexType().getAttribute("section");
			// AttributeValueList sectionAttList =
			// flexBOMPart.getFlexType().getAttribute("section").getAttValueList();
			// Iterator sectionIter = sectionAttList.getKeys().iterator();
			LCSFlexBOMLogic logic = new LCSFlexBOMLogic();
			int branchid = logic.getMaxBranchId(flexBOMPart);
			LOGGER.debug("BRCreateBOM ::  initial branchid : " + logic.getMaxBranchId(flexBOMPart));

			if (jsonObject.opt("BOM") instanceof JSONArray) {
				BOMArray = jsonObject.optJSONArray("BOM");
				// createSpec.CreateImage(product, season, srcConfig,flexSpec,imagePage);
			}
			// FlexBOMLink bomLinkBean = getBOMLinkBean(flexBOMPart);
			for (int i = 0; i < BOMArray.length(); i++) {
				JSONObject BOMArraySON = BOMArray.getJSONObject(i);
				if (BOMArraySON.opt("BOMLink") instanceof JSONArray) {
					BOMLinkArray = BOMArraySON.optJSONArray("BOMLink");
					LOGGER.debug("BRCreateBOM ::  BOMLinkArray:: " + BOMLinkArray.length());
				}
				for (int x = 0; x < BOMLinkArray.length(); x++) {
					JSONObject bomLinkJSON = BOMLinkArray.getJSONObject(x);
					LOGGER.debug("BRCreateBOM ::  bomLinkJSON : " + bomLinkJSON);
					FlexTypeAttribute sectionAtt = flexBOMPart.getFlexType().getAttribute("section");
					AttributeValueList sectionAttList = flexBOMPart.getFlexType().getAttribute("section")
							.getAttValueList();
					Iterator sectionIter = sectionAttList.getKeys().iterator();
					while (sectionIter.hasNext()) {
						section = (String) sectionIter.next();
						branchid++;
						LOGGER.debug("BRCreateBOM ::  section :: " + section);
						LOGGER.debug("BRCreateBOM ::  branchid :: " + branchid);
						if (section.equalsIgnoreCase(bomLinkJSON.optString("section"))) {
							LOGGER.debug("BRCreateBOM ::  <<<<<-------inside section if loop----->>>>: ");
							if (bomLinkJSON.opt("sectionVariation") instanceof JSONArray) {
								sectionVariationArray = bomLinkJSON.optJSONArray("sectionVariation");
							}
							LOGGER.debug("BRCreateBOM :: creating new Link for section :: " + section);

							// createNewBOMLinks(flexBOMPart,sectionVariationArray,branchid,section);
							FlexBOMLink bomLink = FlexBOMLink.newFlexBOMLink();
							bomLink.setFlexType(flexBOMPart.getFlexType());
							bomLink.setParent(flexBOMPart.getMaster());
							bomLink.setParentRev(flexBOMPart.getVersionIdentifier().getValue());
							bomLink.setParentReference(flexBOMPart.getMasterReference());
							bomLink.setBranchId(branchid);
							bomLink.setValue("wcPartName", "");
							bomLink.setValue("section", section);
							try {
								bomLink = setBOMLinkValues(bomLink, sectionVariationArray);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							bomLink.setInDate(new java.sql.Timestamp(new java.util.Date().getTime()));
							bomLink.setOutDate(null);
							bomLink.setWip(false);
							bomLink.setSequence(0);
							bomLink.calculateDimensionId();
							LCSFlexBOMLogic.deriveFlexTypeValues(bomLink);

							LCSFlexBOMLogic.persist(bomLink, true);

							logic.addBranchToBOM(flexBOMPart, bomLink);
							LOGGER.debug("bomLink----->>>>: " + bomLink);

						}

					}
				}
			}

		}

	}

	public FlexBOMLink setBOMLinkValues(FlexBOMLink bomLink, JSONArray sectionVariationArray) throws JSONException {

		LOGGER.debug("BRCreateBOM :: <<<<<----------setBOMLinkValues----->>>>: ");

		for (int i = 0; i < sectionVariationArray.length(); i++) {
			JSONObject sectionVariation = sectionVariationArray.getJSONObject(i);
			LOGGER.debug("BRCreateBOM :: sectionVariation::::" + sectionVariation);
			String placement = sectionVariation.optString(BRStreamlineConstants.PLACEMENT);
			placement.concat("|~*~|");
			bomLink.setValue(BRStreamlineConstants.PLACEMENT, placement);
			bomLink.setValue(BRStreamlineConstants.PARTNAME, sectionVariation.optString(BRStreamlineConstants.PLACEMENTNOTES));
			bomLink.setValue(BRStreamlineConstants.MATERIAL_DESCRIPTION, sectionVariation.optString(BRStreamlineConstants.MATERIAL_DESCRIPTION));
			bomLink.setValue(BRStreamlineConstants.COLOR_DESCRIPTION, sectionVariation.optString(BRStreamlineConstants.COLOR_DESCRIPTION));
		}
		return bomLink;

	}

}

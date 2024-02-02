package com.burberry.wc.integration.streamline.sample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import javax.ws.rs.core.Response.Status;

import com.burberry.wc.integration.streamline.util.BRStreamlineAPIHelper;
import com.burberry.wc.integration.streamline.util.BRStreamlineConstants;
import com.burberry.wc.integration.streamline.util.BRJSONValidationException;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleHelper;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.sample.SampleRequestFlexTypeScopeDefinition;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

@Path("/BurberryStreamline")
@Api(value = "/CreateSample")
public class BRCreateSample {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(BRCreateSample.class);
	private static final String FOLDERLOCATION = LCSProperties.get("com.lcs.wc.sample.LCSSample.rootFolder",
			"/Samples");

	@SuppressWarnings("unchecked")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("/CreateSample")
	@ApiOperation(value = "POST operation on creating Sample in FlexPLM", notes = "Creates Sample", response = String.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "CSRF_NONCE", value = "The CSRF nonce as returned from the CSRF Protection endpoint.", required = true, dataType = "string", paramType = "header") })

	/**
	 * creates Sample object.
	 */
	public Object createSample(
			@ApiParam(name = "JsonString", value = "The JSON String for processing") String paramString)
			throws Exception {
		String sampleID = null;

		LOGGER.debug(" The Value Entered : " + paramString);

		JSONObject jsonObject = null;
		JSONObject scjson;
		LCSSeason season = null;
		LCSProduct product = null;
		final HashMap returnMap = new HashMap();
		FlexSpecification specification = null;
		LCSSourcingConfig srcConfig = null;
		LCSSourcingConfig sc = null;
		LCSPartMaster productMaster = null;
		LCSSeasonMaster seasonMaster = null;
		String validJson = null;
		Map<Status, Object> response = new HashMap<Status, Object>();
		String error = null;
		LCSSampleRequest sampleRequest = null;

		String pid = null;
		String flexTypestr = null;
		String sid = null;

		try {

			jsonObject = new JSONObject(paramString);
			BRStreamlineAPIHelper.ValidateJson(jsonObject, BRStreamlineConstants.CREATE_SAMPLE_REQ_ATTS);
			

		} 
		catch (BRJSONValidationException e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("JSON MISSING REQUIRED FIELD").build());
		}
		catch (JSONException e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("Invalid JSON String").build());
		}
		
			try {
				// Get product
				pid = (String) jsonObject.get(BRStreamlineConstants.BUR_STYLE_ID);
				flexTypestr = (String) jsonObject.opt(BRStreamlineConstants.PRODUCT_FLEX_TYPE);
				int styleId = Integer.parseInt(pid);
				product = BRStreamlineAPIHelper.getProductFromID(styleId, flexTypestr);

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
				LOGGER.debug("BurberryCreateSampleAPI::::product : " + product);

				if (season != null) {
					LOGGER.debug("BurberryCreateSampleAPI::::season : " + season.getName());

					if (sc != null) {
						LOGGER.debug("BurberryCreateSampleAPI::::Sourcing Config : " + sc.getName());

						LOGGER.debug("BurberryCreateSampleAPI::::srcConfig is :" + srcConfig);

						if (srcConfig != null) {

							if (specification != null) {

								LOGGER.debug("BurberryCreateSampleAPI::::specification is :" + specification.getName());
								sampleRequest = LCSSampleRequest.newLCSSampleRequest();
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

			if (sampleRequest != null) {
				String sampleflexTypestr = (String) jsonObject.opt(BRStreamlineConstants.SAMPLE_FLEX_TYPE);
				// LOGGER.debug("***********In Create Sample**********sampleflexTypestr :" +
				// sampleflexTypestr);
				FlexType flexType = FlexTypeCache.getFlexTypeFromPath(sampleflexTypestr);
				// LOGGER.debug("***********In Create Sample**********flexType :" + flexType);
				sampleRequest.setFlexType(flexType);
				LCSLogic.setFlexTypedDefaults(sampleRequest, SampleRequestFlexTypeScopeDefinition.SAMPLEREQUEST_SCOPE,
						null);
				LCSLogic.assignFolder(FOLDERLOCATION, sampleRequest);
				sampleRequest.setOwnerMaster(productMaster);
				sampleRequest.setSourcingMaster(srcConfig.getMaster());
				sampleRequest.setSpecMaster(specification.getMaster());

				sampleRequest = setSampleRequestValues(sampleRequest, jsonObject, season);

				LCSSampleHelper.service.saveSampleRequest(sampleRequest, null);

				LOGGER.debug("BurberryCreateSampleAPI::::sampleRequest after save ::" + sampleRequest.getName());

				LCSSample sample = LCSSample.newLCSSample();

				sample.setFlexType(flexType);
				LCSLogic.setFlexTypedDefaults(sample, SampleRequestFlexTypeScopeDefinition.SAMPLE_SCOPE, null);
				LCSLogic.assignFolder(FOLDERLOCATION, sample);

				sample.setSampleRequest(sampleRequest);
				sample.setSourcingMaster(srcConfig.getMaster());
				sample.setSourcingMasterReference(srcConfig.getMasterReference());
				sample.setSpecMaster(specification.getMaster());
				sample.setSpecMasterReference(specification.getMasterReference());
				sample.setOwnerMaster(productMaster);
				sample.setOwnerMasterReference(product.getMasterReference());

				LCSSampleHelper.service.saveSample(sample);
				LOGGER.debug("BurberryCreateSampleAPI::::SAMPLE :" + sample);

				sample = BRStreamlineAPIHelper.createFitSamples(sample, product, specification, srcConfig);

				LOGGER.debug("BurberryCreateSampleAPI::::Fit  sample :" + sample);

				sampleID = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(sampleRequest));
				LOGGER.debug("BurberryCreateSampleAPI::::sampleID ::" + sampleID);
				returnMap.put("sampleFlexId", sampleID);
			}
		
		if (error != null)
			returnMap.put("error", error);
		
		response = returnMap;
		LOGGER.debug("final response is : " + response);
		return response;

	}

	static LCSSampleRequest setSampleRequestValues(LCSSampleRequest sampleRequest, JSONObject jsonObject,
			LCSSeason season) throws WTPropertyVetoException, WTException {
		// TODO Auto-generated method stub
		if (sampleRequest != null) {
			sampleRequest.setValue(BRStreamlineConstants.SAMPLE_REQUEST_TYPE,
					jsonObject.opt(BRStreamlineConstants.SAMPLE_REQUEST_TYPE));
			sampleRequest.setValue(BRStreamlineConstants.SAMPLE_SEASON_REQUESTED, season);
			sampleRequest.setValue(BRStreamlineConstants.SAMPLE_ORDER_UNITS,
					jsonObject.opt(BRStreamlineConstants.SAMPLE_ORDER_UNITS));
			sampleRequest.setValue(BRStreamlineConstants.SAMPLE_SHIP_TO_DESTINATION,
					jsonObject.opt(BRStreamlineConstants.SAMPLE_SHIP_TO_DESTINATION));
			sampleRequest.setValue(BRStreamlineConstants.SAMPLE_SEND_NOTIFICATION,
					jsonObject.opt(BRStreamlineConstants.SAMPLE_SEND_NOTIFICATION));
			sampleRequest.setValue(BRStreamlineConstants.SAMPLE_COST_CURRENCY,
					jsonObject.opt(BRStreamlineConstants.SAMPLE_COST_CURRENCY));
			sampleRequest.setValue(BRStreamlineConstants.SAMPLE_COST,
					jsonObject.opt(BRStreamlineConstants.SAMPLE_COST));
			sampleRequest.setValue(BRStreamlineConstants.SAMPLE_COST_STATUS,
					jsonObject.opt(BRStreamlineConstants.SAMPLE_COST_STATUS));
			sampleRequest.setValue(BRStreamlineConstants.SAMPLE_REQUEST_COMMENTS,
					jsonObject.opt(BRStreamlineConstants.SAMPLE_REQUEST_COMMENTS));

			SimpleDateFormat simpledateformat = new SimpleDateFormat(BRStreamlineConstants.SIMPLE_DATE_FORMAT);
			// SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
			String sampleDueDate = (String) jsonObject.opt(BRStreamlineConstants.SAMPLE_DUE_DATE);

			if (sampleDueDate != null) {

				TimeZone gmt = TimeZone.getTimeZone("GMT");
				// SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				simpledateformat.setTimeZone(gmt);
				GregorianCalendar cal = new GregorianCalendar(gmt);
				try {
					cal.setTime(simpledateformat.parse(sampleDueDate));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// LOGGER.debug("***********In Create Sample**********sampleDueDate:::::>>>" +
				// cal.getTime());
				sampleRequest.setValue(BRStreamlineConstants.SAMPLE_DUE_DATE, cal.getTime());

			}
			// SimpleDateFormat simpledateformat1 = new
			// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			// SimpleDateFormat outputFormat1 = new SimpleDateFormat("dd-MM-yyyy");
			String sampleRequestDate = (String) jsonObject.opt(BRStreamlineConstants.SAMPLE_REQUEST_DATE_JSON);
			if (sampleRequestDate != null) {

				TimeZone gmt = TimeZone.getTimeZone("GMT");
				simpledateformat.setTimeZone(gmt);
				GregorianCalendar cal = new GregorianCalendar(gmt);
				try {
					cal.setTime(simpledateformat.parse(sampleRequestDate));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// LOGGER.debug("***********In Create
				// Sample**********sampleDueDate:::::sampleRequestDate......"
				// + cal.getTime());
				sampleRequest.setValue(BRStreamlineConstants.SAMPLE_REQUEST_DATE, cal.getTime());

			}
		}
		return sampleRequest;
	}

}

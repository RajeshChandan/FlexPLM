package com.burberry.wc.integration.streamline.measurement;

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
import org.json.JSONException;
import org.json.JSONObject;

import com.burberry.wc.integration.streamline.util.BRJSONValidationException;
import com.burberry.wc.integration.streamline.util.BRStreamlineAPIHelper;
import com.burberry.wc.integration.streamline.util.BRStreamlineConstants;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.measurements.LCSMeasurements;
import com.lcs.wc.measurements.LCSMeasurementsQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.VersionHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("/BurberryStreamline")
@Api(value = "/MeasurementAPI")
public class BRStreamlineMeasurementAPI {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(BRStreamlineMeasurementAPI.class);

	@SuppressWarnings("unchecked")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("/Measurement")
	@ApiOperation(value = "POST operation on Measurement in FlexPLM", notes = "Measurement", response = String.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "CSRF_NONCE", value = "The CSRF nonce as returned from the CSRF Protection endpoint.", required = true, dataType = "string", paramType = "header") })

	public String measurementValidator(
			@ApiParam(name = "JsonString", value = "The JSON String for processing") String paramString)
			throws Exception {

		JSONObject jsonObject;
		LCSSeason season = null;
		LCSProduct product = null;
		FlexSpecification specification = null;
		LCSSourcingConfig srcConfig = null;
		LCSSourcingConfig sc = null;
		Collection measurements = null;
		String validJson=null;
		//final HashMap returnMap = new HashMap();
		String response = null ;
		String error = null;
		String pid = null;
		String flexTypestr = null;
		String sid = null;
		LCSPartMaster productMaster = null;
		LCSSeasonMaster seasonMaster = null;
		FlexType bomFlexType = null;
		try {

			jsonObject = new JSONObject(paramString);
			BRStreamlineAPIHelper.ValidateJson(jsonObject, BRStreamlineConstants.MEASUREMENT_REQ_ATTS);
		} catch (BRJSONValidationException e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("JSON MISSING REQUIRED FIELD").build());
		}catch (JSONException e) {

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
								measurements = LCSMeasurementsQuery.findMeasurements(product, srcConfig, specification);
								if (measurements.isEmpty()) {
									response= "false";
								} else {
									response= "true";
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
		response = error;
		LOGGER.debug("final response is : " + response);
		return response;
		

		//LOGGER.debug(" The Value Entered : " + paramString);

	}
}

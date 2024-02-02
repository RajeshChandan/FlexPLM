package com.burberry.wc.integration.streamline.product;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.burberry.wc.integration.streamline.image.BRUpdateImage;
import com.burberry.wc.integration.streamline.sourcingconfig.BRUpdateSourcingConfig;
import com.burberry.wc.integration.streamline.util.BRJSONValidationException;
import com.burberry.wc.integration.streamline.util.BRStreamlineAPIHelper;
import com.burberry.wc.integration.streamline.util.BRStreamlineConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductHelper;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.VersionHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import wt.org.WTUser;

@Path("/BurberryStreamline")
@Api(value = "/Product")
public class BRUpdateProduct {
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(BRCreateProduct.class);

	/**
	 * 
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("/ProductUpdate")
	@ApiOperation(value = "POST operation on creating style in FlexPLM", notes = "Does something(hopefully) !!!!", response = String.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "CSRF_NONCE", value = "The CSRF nonce as returned from the CSRF Protection endpoint.", required = true, dataType = "string", paramType = "header") })
    
	public Object updateProduct(@ApiParam(name = "JsonString", value = "The JSON String for processing") String paramString)
			throws Exception {

		LOGGER.debug(" BRUpdateProduct	:: The Value Entered : " + paramString);
		Map<Status, Object> response = new HashMap<Status, Object>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValueAsString(response);
		LCSSeason season = null;
		JSONObject jsonObject;
		LCSProduct product = null;
		JSONObject scjson = null;
		String vendorType="NoVendor";
		String error = null;
		int vendorID;
		WTUser userID = null;
		String validJson = null;
		final HashMap returnMap = new HashMap();
		HashMap sourceReturnMap = new HashMap();
		JSONArray imageArray =null;
		String pid = null;
		String sid = null;
		String flexTypestr = null;
		String modifierid = null;
		
		BRCreateProduct createProduct = new BRCreateProduct();
		BRUpdateImage brUpdateImage = new BRUpdateImage();
		
		BRUpdateSourcingConfig updateSourcingConfig = new BRUpdateSourcingConfig();
		

		try {

			jsonObject = new JSONObject(paramString);
			BRStreamlineAPIHelper.ValidateJson(jsonObject, BRStreamlineConstants.UPDATE_PRODUCT_REQ_ATTS);
		} catch (BRJSONValidationException e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("JSON MISSING REQUIRED FIELD").build());
		}catch (JSONException e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("Invalid JSON String").build());
		}



			try {
				modifierid = (String) jsonObject.get(BRStreamlineConstants.MODIFIED_BY);
				int userid = Integer.parseInt(modifierid);
				userID = BRStreamlineAPIHelper.getUserFromID(userid);

				// Get product
				pid = jsonObject.optString(BRStreamlineConstants.STYLE_ID);
				flexTypestr = jsonObject.optString(BRStreamlineConstants.TYPE);

				int styleId = Integer.parseInt(pid);
				product = BRStreamlineAPIHelper.getProductFromID(styleId, flexTypestr);

				// Get season
				sid = jsonObject.optString(BRStreamlineConstants.SEASON_ID);
				int seasonId = Integer.parseInt(sid);
				season = BRStreamlineAPIHelper.getSeasonFromID(seasonId);

			} catch (Exception e) {

				LOGGER.debug(" BRUpdateProduct	:: Error: " + e.getLocalizedMessage());
				
			}

			if (userID != null) {
				wt.session.SessionHelper.manager.setPrincipal(userID.getName());
				System.out.println("session user : " + wt.session.SessionHelper.manager.getPrincipal().getName());

				if (product != null) {

					LOGGER.debug(" BRUpdateProduct	:: product : " + product.getName());

					if (season != null) {

						LOGGER.debug(" BRUpdateProduct	:: season : " + season.getName());

						if (jsonObject.opt(BRStreamlineConstants.PRODUCT) instanceof JSONObject) {

							JSONObject productJson = (JSONObject) jsonObject.opt(BRStreamlineConstants.PRODUCT);
							JSONArray keys = productJson.names();

							try {
								if (productJson.opt(BRStreamlineConstants.SOURCINGCONFIG) instanceof JSONObject) {
									scjson = productJson.optJSONObject(BRStreamlineConstants.SOURCINGCONFIG);
									String vendorIdstr = scjson.optString(BRStreamlineConstants.VENDOR);
									if (vendorIdstr != null) {
										vendorType = null;
										vendorID = Integer.parseInt(vendorIdstr);
										LCSSupplier supplier = BRStreamlineAPIHelper.getVendorFromID(vendorID);
										LOGGER.debug(" BRUpdateProduct	:: supplier flex type : "+ supplier.getFlexType().getTypeName());
										vendorType = supplier.getFlexType().getTypeName().toString();
										LOGGER.debug(" BRUpdateProduct	:: VendorType :"+vendorType);
									}

								}
							} catch (Exception e) {

								LOGGER.debug(" BRUpdateProduct	:: Error: " + e.getLocalizedMessage());
							}

							if ("Finished Good Supplier".equalsIgnoreCase(vendorType)
									|| "NoVendor".equalsIgnoreCase(vendorType)) {

								for (int i = 0; i < keys.length(); ++i) {
									String key = keys.getString(i);
									if ((BRStreamlineConstants.SOURCINGCONFIG.equalsIgnoreCase(key))
											|| (BRStreamlineConstants.IMAGEPAGE.equalsIgnoreCase(key))) {

										continue;

									} else {
										String value = productJson.optString(key);
										LOGGER.debug(" BRUpdateProduct	:: "+key + " : " + productJson.get(key));
										product = createProduct.setValuesOfProduct(key, value, flexTypestr, product);

									}

								}
								LCSProductLogic prodLogic = new LCSProductLogic();
								//product = prodLogic.saveProduct(product, true);
								product = (LCSProduct) prodLogic.save(product);
								//product = LCSProductHelper.service.saveProduct(product);
								returnMap.put("success", "The product with StyleID: " + pid + " is updated");
								LOGGER.debug("BRUPDATEProduct ::   After product saved version :"
										+ product.getIterationDisplayIdentifier());

								if ((productJson.opt(BRStreamlineConstants.SOURCINGCONFIG) instanceof JSONObject)
										&& (productJson.opt(BRStreamlineConstants.SOURCINGCONFIG) != null)) {

									JSONObject scJson = (JSONObject) productJson
											.opt(BRStreamlineConstants.SOURCINGCONFIG);
									product = (LCSProduct) VersionHelper.latestIterationOf(product);
									
									LOGGER.debug("********************BRUPDATEProduct ::  updating source for latest iteration of proudct :"
											+ product.getIterationDisplayIdentifier());
									sourceReturnMap = updateSourcingConfig.updateSource(product, season, scJson);
									if(sourceReturnMap.containsKey("error")) {
										error = (String) sourceReturnMap.get("error");
									}else if(sourceReturnMap.containsKey("product")) {
										product=(LCSProduct) sourceReturnMap.get("product");
									}

								}
								LOGGER.debug(" BRUpdateProduct	:: Before entering for imagepage");
								if ((productJson.opt(BRStreamlineConstants.IMAGEPAGE) instanceof JSONArray)) {

									imageArray = (JSONArray) productJson.opt(BRStreamlineConstants.IMAGEPAGE);
									LOGGER.debug(" BRUpdateProduct	:: imageArray is : " + imageArray);
									product = (LCSProduct) VersionHelper.latestIterationOf(product);
									
									LOGGER.debug("********************BRUPDATEProduct :: updating IMAGE for  latest iteration of proudct :"
											+ product.getIterationDisplayIdentifier());
									ArrayList<String> updateImagePageList = brUpdateImage.updateImagePage(product,
											season, imageArray);
									returnMap.put("imagePage", updateImagePageList);

								}
							} else {
								error = "The vendor selected is not a Finished Goods Vendor";
							}

						}
					} else {
						error = "Could not find season with given seasonID: " + sid;
					}
				} else {
					error = "Could not find product with given styleID: " + pid + " and Type: " + flexTypestr;
				}
			} else {
				error = "User does not have access to create data on FlexPLM";
			}
		
		
		if(error!=null) {
			returnMap.put("error", error);
			LOGGER.debug(" BRUpdateProduct	:: error : "+ error);
		}
		
		response = returnMap;
		LOGGER.debug(" BRUpdateProduct	:: final response is : "+response);
		return response;
	}
	
	

}

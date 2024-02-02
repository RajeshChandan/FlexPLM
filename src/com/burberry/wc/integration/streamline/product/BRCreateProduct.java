
package com.burberry.wc.integration.streamline.product;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

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

import com.burberry.wc.integration.streamline.sourcingconfig.BRCreateSourcingCofig;
import com.burberry.wc.integration.streamline.util.BRJSONValidationException;
import com.burberry.wc.integration.streamline.util.BRStreamlineAPIHelper;
import com.burberry.wc.integration.streamline.util.BRStreamlineConstants;
import com.burberry.wc.migration.util.BRLoadUtil;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductHelper;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonHelper;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import wt.access.NotAuthorizedException;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException; 

@Path("/BurberryStreamline")
@Api(value = "/Product")
public class BRCreateProduct {
	
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
	@Path("/ProductCreate")
	@ApiOperation(value = "POST operation on creating style in FlexPLM", notes = "Does something(hopefully) !!!!", response = String.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "CSRF_NONCE", value = "The CSRF nonce as returned from the CSRF Protection endpoint.", required = true, dataType = "string", paramType = "header") })
    
	public Object productOps(@ApiParam(name = "JsonString", value = "The JSON String for processing") String paramString)
			throws Exception {

		LOGGER.debug("BRCreateProduct ::    The Value Entered : " + paramString);
		Map<Status, Object> response = new HashMap<Status, Object>();
		WTUser userID = null;
		LCSSeason season = null;
		final HashMap returnMap = new HashMap();
		ArrayList<String> imagePageIDA2A2List = new ArrayList<String>(); 
		String error = null;
		JSONObject jsonObject;
		JSONObject scjson = null;
		String vendorType="NoVendor";
		JSONArray imageArray =null;
		String validJson = null;
		HashMap sourceReturnMap = new HashMap();
		int seasonId = 0 ;
		LCSSupplier supplier = null;
		
		BRCreateSourcingCofig source = new BRCreateSourcingCofig();
		
		try {

			jsonObject = new JSONObject(paramString);
			BRStreamlineAPIHelper.ValidateJson(jsonObject, BRStreamlineConstants.CREATE_PRODUCT_REQ_ATTS);
			
		}catch (BRJSONValidationException e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("JSON MISSING REQUIRED FIELD").build());
		}catch (JSONException e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("Invalid JSON String").build());
		}
		
	
			JSONArray keys = jsonObject.names();

			String creatorid = (String) jsonObject.get(BRStreamlineConstants.CREATED_BY);
			int userid = Integer.parseInt(creatorid);
			try {
				userID = BRStreamlineAPIHelper.getUserFromID(userid);
				if (jsonObject.opt(BRStreamlineConstants.SOURCINGCONFIG) instanceof JSONObject) {
					vendorType=null;
					scjson = jsonObject.optJSONObject(BRStreamlineConstants.SOURCINGCONFIG);
					String vendorIdstr = (String) scjson.opt(BRStreamlineConstants.VENDOR);
					int vendorID = Integer.parseInt(vendorIdstr);

					supplier = BRStreamlineAPIHelper.getVendorFromID(vendorID);
					LOGGER.debug("BRCreateProduct ::   supplier flex type : " + supplier.getFlexType().getTypeName());
					vendorType = supplier.getFlexType().getTypeName().toString();
					LOGGER.debug("BRCreateProduct ::   vendor Type : " +vendorType);

				}

			} catch (Exception e) {

				LOGGER.debug("BRCreateProduct ::   Error: " + e.getLocalizedMessage());
			}
			

			if (userID != null) {
				
				wt.session.SessionHelper.manager.setPrincipal(userID.getName());
				LOGGER.debug("BRCreateProduct ::	session user : " + wt.session.SessionHelper.manager.getPrincipal().getName());
				
				

				if ("Finished Good Supplier".equalsIgnoreCase(vendorType) || "NoVendor".equalsIgnoreCase(vendorType)) {

					try {

						LCSProduct product = LCSProduct.newLCSProduct();
						product.setMaster(new LCSPartMaster());
						String flexTypestr = (String) jsonObject.opt(BRStreamlineConstants.TYPE);
						LOGGER.debug("BRCreateProduct ::   flexType : " + flexTypestr);
						FlexType flexType = FlexTypeCache.getFlexTypeFromPath(flexTypestr);
						product.setFlexType(flexType);

						for (int i = 0; i < keys.length(); ++i) {
							String key = keys.getString(i);
							LOGGER.debug("BRCreateProduct ::	" + key + " : " + jsonObject.get(key));

							if (((BRStreamlineConstants.FLEXTYPE_PRODUCT_APPAREL_MENS.equalsIgnoreCase(flexTypestr))
									|| (BRStreamlineConstants.FLEXTYPE_PRODUCT_APPAREL_WOMENS
											.equalsIgnoreCase(flexTypestr)))
									&& BRStreamlineConstants.BUROPERATIONALCATEGORY.equalsIgnoreCase(key)) {

								String opc = (String) jsonObject.opt(key);
								key = BRStreamlineConstants.BUROPERATIONALCATEGORYAPP;
								product.setValue(key, opc);

							} else if (BRStreamlineConstants.APP_ID.equalsIgnoreCase(key)) {

								String appid = (String) jsonObject.opt(key);
								key = BRStreamlineConstants.BURSTREAMLINEPRODUCTID;
								product.setValue(key, appid);
							}

							else if (BRStreamlineConstants.SEASON_ID.equalsIgnoreCase(key)
									|| BRStreamlineConstants.BURDEVELOPMENTSEASON.equalsIgnoreCase(key)) {
								String id = (String) jsonObject.get(BRStreamlineConstants.SEASON_ID);
								seasonId = Integer.parseInt(id);

								try {
									season = BRStreamlineAPIHelper.getSeasonFromID(seasonId);
								} catch (Exception e) {
									LOGGER.debug("BRCreateProduct ::   Error: " + e.getLocalizedMessage());
								}

							} else if (BRStreamlineConstants.TYPE.equalsIgnoreCase(key)
									|| BRStreamlineConstants.SOURCINGCONFIG.equalsIgnoreCase(key)
									|| BRStreamlineConstants.IMAGEPAGE.equalsIgnoreCase(key)
									|| BRStreamlineConstants.CREATED_BY.equalsIgnoreCase(key)) {
								continue;

							} else {
								product = setValuesOfProduct(key, jsonObject.optString(key), flexTypestr, product);
							}
						}

						product = LCSProductHelper.service.saveProduct(product);
						LOGGER.debug("BRCreateProduct ::   After product saved version :"
								+ product.getIterationDisplayIdentifier());

						if (season != null) {
							LOGGER.debug("BRCreateProduct ::   SeasonName : " + season.getName());
							product.setValue(BRStreamlineConstants.BURDEVELOPMENTSEASON, season);
							LCSSeasonHelper.service.addProduct(product, season);

							product = LCSProductHelper.service.saveProduct(product);
							if (jsonObject.opt(BRStreamlineConstants.IMAGEPAGE) instanceof JSONArray) {
								imageArray = jsonObject.optJSONArray(BRStreamlineConstants.IMAGEPAGE);
								LOGGER.debug("BRCreateProduct ::   Image Array is : " + imageArray);
							}

							if (jsonObject.opt(BRStreamlineConstants.SOURCINGCONFIG) instanceof JSONObject) {
								scjson = jsonObject.optJSONObject(BRStreamlineConstants.SOURCINGCONFIG);
								LOGGER.debug("BRCreateProduct ::   Sourcing Config is : " + scjson);
							}

							if (scjson != null && scjson.length() != 0) {
								sourceReturnMap = source.CreateSource(product, season, scjson, imageArray);
								if (!sourceReturnMap.isEmpty()) {
									if (sourceReturnMap.containsKey("imagePageIDA2A2List")) {
										imagePageIDA2A2List = (ArrayList<String>) sourceReturnMap
												.get("imagePageIDA2A2List");
									} else if (sourceReturnMap.containsKey("Sourcingerror")) {
										error = (String) sourceReturnMap.get("Sourcingerror");
									}
								}

							} else {
								if (imageArray != null)
									error = "Image Pages could not be created because primary source/specification is not found";
							}
						} else {
							error = "Season could not be found with id : " + seasonId
									+ ". So could not proceed further";
						}

						final long styleId = (long) product.getValue(BRStreamlineConstants.STYLE_NUM);
						returnMap.put("Product Id", styleId);
						if (!imagePageIDA2A2List.isEmpty()) {
							returnMap.put("Image Id", imagePageIDA2A2List);
						}

						LOGGER.debug("BRCreateProduct ::   output : " + returnMap);
					}catch (wt.vc.VersionControlException e) {
						LOGGER.debug("BRCreateSourcingCofig ::	Error: " + e.getLocalizedMessage());

						if (e.getCause() != null && e.getCause() instanceof NotAuthorizedException) {
							LOGGER.debug("BRCreateSourcingCofig ::	Error: " + e.getLocalizedMessage());
							error = "User does not have access to create data on FlexPLM";
						}
					}
				} else {
					error = "The vendor selected is not a Finished Goods Vendor";
				}
			} else {
				error = "User does not have access to create data on FlexPLM" ;
			}
		
		if(error!=null) {
			returnMap.put("error", error);
			LOGGER.debug("BRCreateProduct ::   error : "+ error);
		}
		response = returnMap;
		LOGGER.debug("BRCreateProduct ::   final response is : "+response);
		return response;

	}
	
	public LCSProduct setValuesOfProduct(String key, String value, String flexTypestr, LCSProduct product) {

		FlexTypeAttribute flexAtt;
		String refObjectName;
		String refObjectId = null;
		WTUser userID = null;

		try {
			LOGGER.debug("BRCreateProduct ::   setValue : " + key + " : " + value);
			FlexType flexType = FlexTypeCache.getFlexTypeFromPath(flexTypestr);

			if (flexTypestr.toLowerCase().contains(BRStreamlineConstants.FLEXTYPE_CHILDRENS)
					&& BRStreamlineConstants.BURAGEGROUP.equalsIgnoreCase(key)) {

				product.setValue(key, value);

			} else if (key.toLowerCase().contains(BRStreamlineConstants.BRAND)) {

				if ((BRStreamlineConstants.FLEXTYPE_PRODUCT_APPAREL_MENS.equalsIgnoreCase(flexTypestr))
						|| (BRStreamlineConstants.FLEXTYPE_PRODUCT_APPAREL_WOMENS.equalsIgnoreCase(flexTypestr))) {

					product.setValue(key, value);

				} else {
					key = BRStreamlineConstants.VRDBRAND;
					product.setValue(key, value);

				}

			} else if (BRStreamlineConstants.BURLAST.equalsIgnoreCase(key)) {

				if (flexTypestr.toLowerCase().contains(BRStreamlineConstants.FLEXTYPE_FOOTWEAR)) {
					product.setValue(key, value);
				}
			}

			else if (BRStreamlineConstants.BURACCESSORIESGROUP.equalsIgnoreCase(key)) {

				if (flexTypestr.toLowerCase().contains(BRStreamlineConstants.FLEXTYPE_ACCESSORIES)) {
					product.setValue(key, value);
				}

			} else {
				flexAtt = flexType.getAttribute(key);
				if (FormatHelper.hasContent((value))) {
					if ("object_ref".equals(flexAtt.getAttVariableType())) {
						refObjectName = flexAtt.getRefType().getFullNameDisplay(true);
						refObjectId = BRLoadUtil.getReferenceObject(value, refObjectName);
						if (refObjectId != null) {
							product.setValue(key, refObjectId);
						}
					} else if ("boolean".equals(flexAtt.getAttVariableType())) {
						if (value.contains("Yes")) {
							product.setValue(key, true);
						} else if (value.contains("No")) {
							product.setValue(key, false);
						}
					} else if ("userList".equals(flexAtt.getAttVariableType())) {
						String userid = value;
						int id = Integer.parseInt(userid);
						try {
							userID = BRStreamlineAPIHelper.getUserFromID(id);
						} catch (Exception e) {
							LOGGER.debug("BRCreateProduct ::   Error: " + e.getLocalizedMessage());
						}

						if (userID != null) {
							product.setValue(key, userID);
						} else {
							LOGGER.debug("BRCreateProduct ::   Given user " + value + " does not find on the system");
						}
					} else {
						product.setValue(key, value);
					}
				}
			}

		} catch (Exception e) {

			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("Invalid JSON String").build());
		}
		return product;

	}
	
}
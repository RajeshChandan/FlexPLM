package com.burberry.wc.integration.streamline.image;

import java.io.File;
import java.util.ArrayList;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.burberry.wc.integration.streamline.sourcingconfig.BRUpdateSourcingConfig;
import com.burberry.wc.integration.streamline.util.BRJSONValidationException;
import com.burberry.wc.integration.streamline.util.BRStreamlineAPIHelper;
import com.burberry.wc.integration.streamline.util.BRStreamlineConstants;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentClientModel;
import com.lcs.wc.document.LCSDocumentLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecLogic;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.foundation.LCSLogic;

import wt.access.NotAuthorizedException;
import wt.fc.WTObject;
import wt.org.WTPrincipal;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.NonLatestCheckoutException;
import wt.vc.wip.WorkInProgressException;

public class BRUpdateImage {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(BRUpdateImage.class);

	/**
	 * 
	 * @param product
	 * @param lcsSeason
	 * @param imageArray
	 * @return
	 */
	public ArrayList<String> updateImagePage(LCSProduct product, LCSSeason lcsSeason, JSONArray imageArray) {

		JSONObject imagePageObj = null;

		new ArrayList<String>();
		String validJson = null;
		ArrayList<String> updateImagePageList = new ArrayList<String>();

		new BRUpdateSourcingConfig();
		LOGGER.debug("BRUpdateImage 	:: inside updateImagePage method");

		try {

			if (imageArray != null) {
				for (int i = 0; i < imageArray.length(); ++i) {

					imagePageObj = imageArray.getJSONObject(i);
					try {
						BRStreamlineAPIHelper.ValidateJson(imagePageObj, BRStreamlineConstants.UPDATE_IMAGE_REQ_ATTS);
					} catch (BRJSONValidationException e) {

						String error = " Missing a required field in the input json";
						updateImagePageList.add(error);
					}

					if ("CREATE".equalsIgnoreCase(imagePageObj.optString(BRStreamlineConstants.CRUD))) {
						String imagePageIda2a2 = createImage(product, lcsSeason, imagePageObj);
						updateImagePageList.add(imagePageIda2a2);

					} else if ("UPDATE".equalsIgnoreCase(imagePageObj.optString(BRStreamlineConstants.CRUD))) {
						String response = updateImage(product, lcsSeason, imagePageObj);
						updateImagePageList.add(response);

					} else if ("DELETE".equalsIgnoreCase(imagePageObj.optString(BRStreamlineConstants.CRUD))) {

						WTPrincipal user = SessionHelper.manager.getPrincipal();
						SessionHelper.manager.setPrincipal("wcadmin");
						long start = System.currentTimeMillis();
						String response = deleteImage(product, lcsSeason, imagePageObj);
						long end = System.currentTimeMillis();
						long total = end - start;
						LOGGER.debug(
								"BRUpdateImage 	:: Total time taken for delete ImagePage in milliseconds : " + total);
						updateImagePageList.add(response);
						SessionHelper.manager.setPrincipal(user.getName());
					}

				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			LOGGER.debug("BRUpdateImage 	:: Error : " + e.getLocalizedMessage());
		}
		LOGGER.debug("BRUpdateImage 	:: Response : " + updateImagePageList);
		return updateImagePageList;

	}

	/**
	 * 
	 * @param product
	 * @param lcsSeason
	 * @param imagePageObj
	 * @return
	 */
	public String createImage(LCSProduct product, LCSSeason lcsSeason, JSONObject imagePageObj) {

		LCSSourcingConfig lcsSource = null;
		FlexSpecification flexSpec = null;
		LCSDocument imagePage = null;
		String imagePageIda2a2 = null;
		try {

			BRCreateImage image = new BRCreateImage();

			lcsSource = BRStreamlineAPIHelper.getLatestSrcConfig(product, lcsSeason);
			if (lcsSource != null && lcsSource.isPrimarySource()) {

				flexSpec = BRStreamlineAPIHelper.getSpecification(product, lcsSeason, lcsSource);
				if (flexSpec != null) {

					LOGGER.debug(
							"BRUpdateImage 	:: imageURL : " + imagePageObj.optString(BRStreamlineConstants.IMAGEURL)
									+ " imageName : " + imagePageObj.optString(BRStreamlineConstants.IMAGENAME));
					final File file = image.saveImage(imagePageObj.optString(BRStreamlineConstants.IMAGEURL),
							imagePageObj.optString(BRStreamlineConstants.IMAGENAME));
					if ((file != null) && (file.exists())) {

						imagePage = image.createImagePage(product, file, imagePageObj);
						if (imagePage != null) {

							imagePageIda2a2 = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(imagePage));
							FlexSpecLogic flexSpecLogic = new FlexSpecLogic();
							FlexSpecToComponentLink specCompLink;
							specCompLink = flexSpecLogic.addComponentToSpec(flexSpec, (WTObject) imagePage);

							LOGGER.debug(
									"BRUpdateImage 	:: specCompLink component : " + specCompLink.getComponent());

						}
					}
				}
			}

		} catch (WTException e) {
			// e.printStackTrace();
			LOGGER.debug("BRUpdateImage 	:: Error : " + e.getLocalizedMessage());
		}
		if (imagePageIda2a2 == null)
			imagePageIda2a2 = " ";

		LOGGER.debug("BRUpdateImage ::   response is : " + imagePageIda2a2);
		return imagePageIda2a2;

	}

	/**
	 * 
	 * @param product
	 * @param lcsSeason
	 * @param imagePageObj
	 * @return
	 * @throws WTException
	 * @throws PersistenceException
	 * @throws WTPropertyVetoException
	 * @throws WorkInProgressException
	 * @throws NonLatestCheckoutException
	 */
	public String updateImage(LCSProduct product, LCSSeason lcsSeason, JSONObject imagePageObj)
			throws NonLatestCheckoutException, WorkInProgressException, WTPropertyVetoException, PersistenceException,
			WTException {

		LCSDocument imagePage = null;
		File file = null;
		String response = null;
		String fileName = "";
		int numberOfFiles = 1;

		LCSDocumentClientModel documentModel = new LCSDocumentClientModel();
		BRCreateImage image = new BRCreateImage();
		String imagePageIda2a2 = imagePageObj.optString(BRStreamlineConstants.IMAGEID);
		JSONArray keys = imagePageObj.names();
		LOGGER.debug("BRUpdateImage 	:: keys : " + keys);
		
		LOGGER.debug("********************BRUpdateImage ::   latest iteration of proudct :"
				+ product.getIterationDisplayIdentifier());
		
		LOGGER.debug("********************BRUpdateImage ::   is latest????? :"
				+ product.isLatestIteration());
		
		LOGGER.debug("********************BRUpdateImage ::   is checked out????? :"
				+ VersionHelper.isCheckedOut(product));
		
		

		try {

			try {

				imagePage = (LCSDocument) LCSQuery
						.findObjectById("OR:com.lcs.wc.document.LCSDocument:" + imagePageIda2a2);
				LOGGER.debug("BRUpdateImage 	:: imagePage is :" + imagePage);

			} catch (Exception e) {

				LOGGER.debug("BRUpdateImage 	:: Error: " + e.getLocalizedMessage());
			}

			if (imagePage != null) {

				imagePage = (LCSDocument) VersionHelper.latestIterationOf(imagePage);

				imagePageIda2a2 = FormatHelper.getNumericFromOid(FormatHelper.getObjectId(imagePage));
				documentModel.load("OR:com.lcs.wc.document.LCSDocument:" + imagePageIda2a2);

				for (int j = 0; j < keys.length(); ++j) {
					String key = keys.getString(j);
					LOGGER.debug("BRUpdateImage 	:: key : " + key);
					if (BRStreamlineConstants.IMAGEID.equalsIgnoreCase(key)
							|| BRStreamlineConstants.CRUD.equalsIgnoreCase(key)
							|| BRStreamlineConstants.IMAGEURL.equalsIgnoreCase(key)
							|| BRStreamlineConstants.IMAGENAME.equalsIgnoreCase(key)) {
						continue;
					} else {
						documentModel.setValue(key, imagePageObj.optString(key));
					}

				}

				if (!imagePageObj.isNull(BRStreamlineConstants.IMAGEURL)
						&& !imagePageObj.isNull(BRStreamlineConstants.IMAGENAME)) {
					file = image.saveImage(imagePageObj.optString(BRStreamlineConstants.IMAGEURL),
							imagePageObj.optString(BRStreamlineConstants.IMAGENAME));
					LOGGER.debug("BRUpdateImage 	:: saved file : " + file);
					if ((file != null) && (file.exists())) {
						documentModel.save();
						fileName = file.getAbsolutePath();
						LOGGER.debug("BRUpdateImage 	:: file Path : " + file.getAbsolutePath());
						numberOfFiles++;

						String[] secondaryContentFiles = new String[numberOfFiles];
						secondaryContentFiles[1] = fileName;

						documentModel.removeContents();
						documentModel.setContentFile(fileName);
						LOGGER.debug("BRUpdateImage 	:: contentFile : " + documentModel.getContentFile());
						documentModel.setSecondaryContentFiles(secondaryContentFiles);
						documentModel
								.setTargetProducts("|~*~|" + FormatHelper.getObjectId((WTObject) product.getMaster()));
						LOGGER.debug("BRUpdateImage 	:: After setting the content");
						if (imagePageObj.opt(BRStreamlineConstants.PAGETYPE).toString()
								.equalsIgnoreCase(BRStreamlineConstants.COVERPAGE)) {
							LOGGER.debug("***********SETTING THUMBNAIL************");
							documentModel.setProductThumbnail("1");
							// documentModel.setProductThumbnail(thumb);
						}
						
						
						LOGGER.debug("********************BRUpdateImage ::   is latest????? :"
								+ product.isLatestIteration());
						
						LOGGER.debug("********************BRUpdateImage ::   is checked out????? :"
								+ VersionHelper.isCheckedOut(product));
						documentModel.save();
						//VersionHelper.checkin(product);
						response = "ImagePage with id: " + imagePageIda2a2 + " is updated";
					} else {
						response = "Image could not be saved with the given url";
					}
				}
			} else {
				response = "ImagePage could not be found in Flex with ID: " + imagePageIda2a2;
			}

			/*
			 * if (imagePageObj.opt("pageType").toString().equalsIgnoreCase("coverpage")) {
			 * LOGGER.debug("BRUpdateImage 	:: Setting the image to thumbnail : ");
			 * documentModel.setProductThumbnail("1"); }
			 */
			// imagePage = (LCSDocument)
			// wt.vc.wip.WorkInProgressHelper.service.checkin(imagePage, null);
			// docLogic.save(imagePage);
			// documentModel.save();

		} catch (Exception e) {
			// e.printStackTrace();
			LOGGER.debug("BRUpdateImage 	:: Error : " + e.getLocalizedMessage());
		}

		LOGGER.debug("BRUpdateImage ::   response is : " + response);
		return response;
	}

	public String deleteImage(LCSProduct product, LCSSeason lcsSeason, JSONObject imagePageObj) {

		LCSSourcingConfig lcsSource = null;
		FlexSpecification flexSpec = null;
		LCSDocument imagePage = null;
		String imagePageIda2a2 = null;
		String response = null;

		try {

			try {

				lcsSource = BRStreamlineAPIHelper.getLatestSrcConfig(product, lcsSeason);
				LOGGER.debug("BRUpdateImage 	:: lcsSource : " + lcsSource);
				if (lcsSource != null) {
					flexSpec = BRStreamlineAPIHelper.getSpecification(product, lcsSeason, lcsSource);
					imagePageIda2a2 = imagePageObj.optString(BRStreamlineConstants.IMAGEID);
					imagePage = (LCSDocument) LCSQuery
							.findObjectById("OR:com.lcs.wc.document.LCSDocument:" + imagePageIda2a2);
				}

			} catch (Exception e) {

				LOGGER.debug("BRUpdateImage 	:: Error: " + e.getLocalizedMessage());
			}

			if (flexSpec != null) {

				if (imagePage != null) {

					LOGGER.debug("BRUpdateImage 	:: imagePage is :" + imagePage);
					LOGGER.debug("BRUpdateImage 	:: flexSpec : " + flexSpec);
					FlexSpecLogic flexSpecLogic = new FlexSpecLogic();
					FlexSpecToComponentLink SpecLink = FlexSpecQuery.getSpecToComponentLink(flexSpec, imagePage);
					LOGGER.debug("BRUpdateImage 	:: SpecLink : " + SpecLink);

					Transaction trnsaction = new Transaction();
					trnsaction.start();
					boolean accessEnforced = SessionServerHelper.manager.setAccessEnforced(false);
					try {

						if (SpecLink != null) {
							long start = System.currentTimeMillis();
							flexSpecLogic.deleteSpecToComponent(SpecLink);
							long end = System.currentTimeMillis();
							long total = end - start;
							LOGGER.debug("BRUpdateImage 	:: Time taken for delete spec in milliseconds : " + total);
							LOGGER.debug("BRUpdateImage 	:: After removing component to spec");
						}

						LCSDocumentLogic docLogic = new LCSDocumentLogic();
						long start = System.currentTimeMillis();
						docLogic.deleteDocument(imagePage);
						long end = System.currentTimeMillis();
						long total = end - start;
						LOGGER.debug("BRUpdateImage 	:: Time taken for delete imagePage in milliseconds : " + total);
						response = "ImagePage with id: " + imagePageIda2a2 + " is deleted";
						LOGGER.debug("BRUpdateImage 	:: After deleting the imagePage");

						trnsaction.commit();
					} catch (wt.vc.VersionControlException e) {
						System.out.println("getCause" + e.getCause());
						System.out.println("getCause.getCause" + e.getCause().getCause());
						LOGGER.debug("BRCreateSourcingCofig ::	Error: " + e.getLocalizedMessage());
						trnsaction.rollback();
						if (e.getCause() != null && e.getCause() instanceof NotAuthorizedException) {

							LOGGER.debug("BRCreateSourcingCofig ::	Error: " + e.getLocalizedMessage());
							response = "User does not have access to delete data on FlexPLM";
						}

					} catch (Exception e) {
						// e.printStackTrace();
						trnsaction.rollback();
						response = "ImagePage with id: " + imagePageIda2a2 + " is could not be deleted";
					} finally {
						SessionServerHelper.manager.setAccessEnforced(accessEnforced);
					}

				} else {
					response = "ImagePage could not be found in Flex with ID: " + imagePageIda2a2;
				}
			} else {
				response = "Flex Specification could not be found with given productID and SeasonID";
			}
		} catch (WTException e) {
			// e.printStackTrace();
			LOGGER.debug("BRUpdateImage 	:: Error : " + e.getLocalizedMessage());
		}

		LOGGER.debug("BRUpdateImage :: 	response is : " + response);
		return response;

	}

}

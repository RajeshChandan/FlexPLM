package com.burberry.wc.integration.streamline.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.burberry.wc.integration.streamline.util.BRStreamlineConstants;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentClientModel;
import com.lcs.wc.document.LCSDocumentLogic;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.WTException;

public class BRCreateImage {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogR.getLogger(BRCreateImage.class.getName());

	/**
	 * 
	 * @param str
	 * @param name
	 * @return
	 */
	public File saveImage(String str, String name) {
		BufferedImage image = null;
		File file = null;
		try {

			LOGGER.debug("BRCreateProduct :: 	imageURL : " + str);
			URL url = new URL(str);
			String fileName = name;
			LOGGER.debug(" BRCreateImage :: 	images Path : " + BRStreamlineConstants.IMAGESPATH);
			file = new File(BRStreamlineConstants.IMAGESPATH + File.separator + fileName);
			String ext = fileName.substring(fileName.lastIndexOf(".") + 1).trim();
			LOGGER.debug(" BRCreateImage :: 	ext : " + ext);
			LOGGER.debug(" BRCreateImage :: 	File Absolute Path : " + file.getAbsolutePath());
			image = ImageIO.read(url);
			LOGGER.debug(" BRCreateImage :: 	Reading of image done : ");
			LOGGER.debug(" BRCreateImage :: 	Type  : " + image.getType());
			Boolean isWrite = ImageIO.write(image, ext, file);
			LOGGER.debug(" BRCreateImage :: 	Writing of image done : " + isWrite);

		} catch (IOException e) {
			e.printStackTrace();

			// LOGGER.debug("BRCreateImage :: Error: " + e.getLocalizedMessage());

		}
		LOGGER.debug("BRCreateImage ::   response is : " + file);
		return file;
	}

	/**
	 * 
	 * @param lcsPdt
	 * @param image
	 * @param imagePage
	 * @return
	 * @throws WTException
	 */
	public LCSDocument createImagePage(LCSProduct lcsPdt, File image, JSONObject imagePage) throws WTException {

		int numberOfFiles = 1;
		String fileName = "";
		LCSDocument doc = null;
		LCSDocumentClientModel documentModel = new LCSDocumentClientModel();
		try {
			if ((image != null) && (image.exists())) {
				fileName = image.getAbsolutePath();
				LOGGER.debug(" BRCreateImage :: 	" + image.getAbsolutePath());
				numberOfFiles++;
			}
			LOGGER.debug("********************BRCreateImage ::   latest iteration of proudct :"
					+ lcsPdt.getIterationDisplayIdentifier());
			
			LOGGER.debug("********************BRCreateImage ::   is latest????? :"
					+ lcsPdt.isLatestIteration());
			
			LOGGER.debug("********************BRCreateImage ::   is checked out????? :"
					+ VersionHelper.isCheckedOut(lcsPdt));
			

			/*
			 * if (VersionHelper.isCheckedOut(lcsPdt)) {
			 * LOGGER.debug("*******GETTING WORKING COPY********"); lcsPdt = (LCSProduct)
			 * wt.vc.wip.WorkInProgressHelper.service .checkout(lcsPdt,
			 * wt.vc.wip.WorkInProgressHelper.service.getCheckoutFolder(), "")
			 * .getWorkingCopy();
			 * 
			 * } else { LOGGER.debug("BRCreateImage 	:: PRODUCT IS CHECKED OUT!!! ");
			 * lcsPdt = (LCSProduct) VersionHelper.checkout(lcsPdt); }
			 */

			// Logic to create Image page and attach images start-------------------

			documentModel
					.setTypeId(FormatHelper.getObjectId(FlexTypeCache.getFlexTypeFromPath("Document\\Images Page")));

			String ownerId = FormatHelper.getObjectId((wt.fc.WTObject) lcsPdt.getMaster());

			// Set the files
			documentModel.setValue(BRStreamlineConstants.PAGETYPE, imagePage.opt(BRStreamlineConstants.PAGETYPE));
			documentModel.setValue(BRStreamlineConstants.PAGELAYOUT, imagePage.opt(BRStreamlineConstants.PAGELAYOUT));
			documentModel.setValue(BRStreamlineConstants.PAGEDESCRIPTION,
					imagePage.opt(BRStreamlineConstants.PAGEDESCRIPTION));
			documentModel.setValue(BRStreamlineConstants.OWNERREFERENCE, ownerId);
			documentModel.save();

			String[] secondaryContentFiles = new String[numberOfFiles];
			secondaryContentFiles[1] = fileName; // required

			documentModel.setContentFile(fileName);
			LOGGER.debug(" BRCreateImage :: 	getContentFile() : " + documentModel.getContentFile());
			documentModel.setSecondaryContentFiles(secondaryContentFiles);
			documentModel.setTargetProducts("|~*~|" + FormatHelper.getObjectId((WTObject) lcsPdt.getMaster()));

			// if
			// (imagePage.opt(BRStreamlineConstants.PAGETYPE).toString().equalsIgnoreCase(BRStreamlineConstants.COVERPAGE)
			// && imagePage.isNull("CRUD")) {
			
			
			// VersionHelper.checkin(lcsPdt);
			//LCSDocumentLogic docLogic = new LCSDocumentLogic();
			//docLogic.save(documentModel);
			if (imagePage.opt(BRStreamlineConstants.PAGETYPE).toString()
					.equalsIgnoreCase(BRStreamlineConstants.COVERPAGE)) {
				LOGGER.debug("***********SETTING THUMBNAIL************");
				documentModel.setProductThumbnail("1");
				// documentModel.setProductThumbnail(thumb);
			}
			LOGGER.debug("********************BRCreateImage ::   is latest????? :"
					+ lcsPdt.isLatestIteration());
			
			LOGGER.debug("********************BRCreateImage ::   is checked out????? :"
					+ VersionHelper.isCheckedOut(lcsPdt));
			documentModel.save();
			// documentModel.checkin();
			
			doc = documentModel.getBusinessObject();

			LOGGER.debug(" BRCreateImage :: 	document saved.Check in the ui");
			LOGGER.debug(" BRCreateImage :: 	documentModel : " + documentModel.getBranchIdentifier());

		} catch (Exception e) {

			// e.printStackTrace();
			LOGGER.debug("BRCreateImage ::	Error: " + e.getLocalizedMessage());
		}
		LOGGER.debug("BRCreateImage ::   response is : " + doc);

		return doc;

	}

}

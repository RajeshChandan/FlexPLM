package com.lowes.massimport.service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductClientModel;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonLogic;
import com.lowes.massimport.excel.pojo.MassImportHeader;
import com.lowes.massimport.excel.pojo.MassImportItem;
import com.lowes.massimport.util.MassImport;

import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/***
 * Service class to create and update the product during Mass Import process
 * 
 * @author Samikkannu Manickam (Samikkannu.manicka@lowes.com)
 *
 */
public class ProductService {
	private static final Logger LOGGER = LogR.getLogger(ProductService.class.getName());
	private static final String PRODUCTTYPE = "PRODUCT";
	private static ProductService productServiceInstance = null;

	private ProductService() {
	}

	public static ProductService getProductService() {
		if (productServiceInstance == null) {
			productServiceInstance = new ProductService();
		}
		return productServiceInstance;
	}

	/****
	 * 
	 * @param massImportItem
	 * @return LCSProduct
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSProduct createAndUpdateProduct(MassImportItem massImportItem)
			throws WTException, WTPropertyVetoException {
		LOGGER.info("Product creation service: create/update mass import items.");
		MassImportHeader header = massImportItem.getMassImportHeader();
		String productDesc = massImportItem.getProductDescription();
		String modelNumber = massImportItem.getModelNumer();
		LCSProduct rfpProduct = header.getRfpProductRef();
		int rowNum = massImportItem.getRowNum() + 1;
		LCSSeason season = header.getSeason();

		LOGGER.info("Row# " + rowNum + " processing records for part create/update.");
		LCSProduct product = getExistingProduct(productDesc, modelNumber, rfpProduct);
		if (product == null) {
			/*** Create a new product ***/
			LOGGER.info("Product does exist in the system and creating a new one");
			LCSProductClientModel productClientModel = getProductClientModel(header, productDesc, modelNumber);
			setProductAttributes(productClientModel, massImportItem);
			productClientModel.save();
			product = productClientModel.getBusinessObject();
			new LCSSeasonLogic().addProduct(product, season);
		} else {
			/*** update existing product **/
			LOGGER.info("Product already exist in the system and updating its attributes.");
			LCSProductClientModel productClientModel = new LCSProductClientModel();
			productClientModel.load(product);
			setProductAttributes(productClientModel, massImportItem);
			productClientModel.save();
			product = productClientModel.getBusinessObject();
			/** If product is not linked with season and linking it with season */
			if (!hasSeasonLink(season, product)) {
				new LCSSeasonLogic().addProduct(product, season);
			}

		}

		return product;

	}

	/***
	 * 
	 * @param header
	 * @param productDesc
	 * @param modelNumber
	 * @return LCSProductClientModel
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private LCSProductClientModel getProductClientModel(MassImportHeader header, String productDesc, String modelNumber)
			throws WTException, WTPropertyVetoException {
		LCSProductClientModel productClientModel = new LCSProductClientModel();
		LCSProduct rfpProduct = header.getRfpProductRef();
		FlexType productFlexType = rfpProduct.getFlexType();

		/**** Setting Product base attributes ***/
		productClientModel.setFlexType(productFlexType);
		LCSLogic.setFlexTypedDefaults(productClientModel, PRODUCTTYPE, PRODUCTTYPE, false);
		productClientModel.setValue(MassImport.PRODUCT_DESCRIPTION_INTERNAL_ATTR, productDesc);
		productClientModel.setValue(MassImport.PRODUCT_MODELNUMBER_INTERNAL_ATTR, modelNumber);
		productClientModel.setValue(MassImport.MASSIMPORT_DOC_RFP_INTERNAL_ATTR, rfpProduct);

		productClientModel.setValue(MassImport.PRODUCT_PRODUCTSTATUS_INTERNAL_ATTR,
				MassImport.PRODUCT_PRODUCTSTATUS_INTERNAL_VALUE);
		productClientModel.setValue(MassImport.PRODUCT_ITEMSTATUS_INTERNAL_ATTR,
				MassImport.PRODUCT_ITEMSTATUS_INTERNAL_VALUE);
		productClientModel.setValue(MassImport.PRODUCT_RFP_INTERNAL_ATTR, MassImport.PRODUCT_RFP_INTERNAL_VALUE);
		return productClientModel;
	}

	/***
	 * 
	 * @param productClientModel
	 * @param massImportItem
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	private void setProductAttributes(LCSProductClientModel productClientModel, MassImportItem massImportItem)
			throws WTPropertyVetoException, WTException {
		/** Setting String attributes */
		for (Map.Entry<String, String> entry : massImportItem.getStringProductAttributes().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			productClientModel.setValue(key, value);
		}

		/** Setting Boolean attributes */
		for (Map.Entry<String, Boolean> entry : massImportItem.getBooleanProductAttributes().entrySet()) {
			String key = entry.getKey();
			Boolean value = entry.getValue();
			productClientModel.setValue(key, value);
		}

		/** Setting Object Ref attributes */
		for (Map.Entry<String, Object> entry : massImportItem.getObjectRefProductAttributes().entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof LCSLifecycleManaged) {
				LCSLifecycleManaged obj = (LCSLifecycleManaged) value;
				productClientModel.setValue(key, obj); 
			}
		}

		/** Setting Float and currency attributes */
		for (Map.Entry<String, Double> entry : massImportItem.getFloatProductAttributes().entrySet()) {
			String key = entry.getKey();
			Double value = entry.getValue();
			productClientModel.setValue(key, value);
		}

		/** Setting number attributes */
		for (Map.Entry<String, Long> entry : massImportItem.getNumberProductAttributes().entrySet()) {
			String key = entry.getKey();
			Long value = entry.getValue();
			productClientModel.setValue(key, value);
		}

		/** Setting date attributes */
		for (Map.Entry<String, Date> entry : massImportItem.getDateProductAttributes().entrySet()) {
			String key = entry.getKey();
			Date value = entry.getValue();
			productClientModel.setValue(key, value);
		}

	}

	private LCSProduct getExistingProduct(String productDesc, String modelNumber, LCSProduct refProduct)
			throws WTException {
		LCSProduct product = null;
		String refProductId = String.valueOf(refProduct.getBranchIdentifier());
		LCSProduct existingProduct = MassImport.queryProduct(productDesc, modelNumber, refProduct.getFlexType(),
				refProductId);
		if (existingProduct != null) {
			product = existingProduct;
		}
		return product;
	}

	private boolean hasSeasonLink(LCSSeason season, LCSProduct product) throws WTException {
		Collection<?> seasonProductLinks = MassImport
				.getProductSeasonLinks(String.valueOf(product.getBranchIdentifier()));
		if (seasonProductLinks.size() == 0) {
			return false;
		}
		double seasonId = season.getBranchIdentifier();
		Iterator<?> itr = seasonProductLinks.iterator();
		while (itr.hasNext()) {
			LCSProductSeasonLink seasonProductLink = (LCSProductSeasonLink) itr.next();
			if (seasonProductLink.getSeasonRevId() == seasonId) {
				return true;
			}
		}
		return false;
	}

}

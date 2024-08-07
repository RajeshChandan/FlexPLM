/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTProperties;

import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMProductWSDLValidator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.Colorway;
import com.sportmaster.wc.interfaces.webservices.productbean.Product;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLink;
import com.sportmaster.wc.interfaces.webservices.productbean.Thumbnail;

/**
 * @author ITC_Infotech.
 *
 */
public class SMProductBeanDataProcessor {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductBeanDataProcessor.class);
	/**
	 * protected constructor.
	 */
	protected SMProductBeanDataProcessor(){
		//protected construtor.
	}

	/**
	 * set data on product bean.
	 * @param product - LCSProduct
	 * @param prodInfoRequest - Product
	 * @param psl - LCSProductSeasonLink
	 * @throws WTException - WTException
	 * @throws DatatypeConfigurationException
	 * @throws IOException 
	 */
	public Product setDataForProductBean(LCSProduct product, Product prodInfoRequest, LCSProductSeasonLink psl,
			SMProductOutboundIntegrationBean bean)
			throws WTException, DatatypeConfigurationException, IOException {

		//validate Product WSDL.
		if(new SMProductWSDLValidator().validateProductWSDL(product, psl, bean)){
			//server hostname.
			String serverHostName = WTProperties.getLocalProperties().getProperty("wt.rmi.server.hostname");

			LOGGER.info("Setting data on product bean     --------");
			//set mdm id.


			if(FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY))){
				prodInfoRequest.setMdmId(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY).toString());
			}

			//set flex type.
			prodInfoRequest.setObjectType(bean.getProdHelper().modifyObjectType(product.getFlexType().getFullNameDisplay()));
			//Phase -8 SEPD Chanegs
			prodInfoRequest.setType1Level(bean.getProdHelper().modifyObjectType(product.getFlexType().getFullNameDisplay().split("\\\\")[0]));
			prodInfoRequest.setType2Level(bean.getProdHelper().modifyObjectType(product.getFlexType().getFullNameDisplay().split("\\\\")[1]));
			
			//updaed logic for FPD Changes
			String productType=product.getFlexType().getFullName();

			//set plmID.
			LCSProduct prod = (LCSProduct) VersionHelper.getFirstVersion(product);
			prod = (LCSProduct) VersionHelper.latestIterationOf(prod);
			LOGGER.info("Prod Object  ********************    "+prod);
			LOGGER.info("Product Branch ID  set on Bean   >>>>>>>    "+prod.getBranchIdentifier());
			prodInfoRequest.setPlmId(String.valueOf(prod.getBranchIdentifier()));

			//set lifecycle state.
			prodInfoRequest.setLifeCycleState(product.getLifeCycleState().toString());
			
			// added for phasge -8 3.8.1.0 build starts
			if (bean.isCancelRequest()) {
				prodInfoRequest.setLifeCycleState(SMProductOutboundWebServiceConstants.CANCELLED);
			}
			// added for phasge -8 3.8.1.0 build ends
			// set macrobrand.
			LCSLifecycleManaged macrobrand = (LCSLifecycleManaged) product
					.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MACROBRAND);

			if (null != macrobrand) {
				prodInfoRequest.setSmMacrobrand(macrobrand.getName());
			}

			// Phase 13 | Splitted fro sonar fix | added
			// setAdditionalDataForProductBean function to set data for product
			// integration.
			setAdditionalDataForProductBean(product, prodInfoRequest, productType);

			//set product thumbnail url.
			if(FormatHelper.hasContent(product.getPartPrimaryImageURL())){
				Thumbnail prodThumbnail = new Thumbnail();
				prodThumbnail.setThumbnailURL("https://"+serverHostName+FormatHelper.formatImageUrl(product.getPartPrimaryImageURL()));
				//set MD5 hash.
				prodThumbnail.setThumbnailHash(DigestUtils.md5Hex(FormatHelper.formatImageUrl(product.getPartPrimaryImageURL())));

				//set thumbnail.
				prodInfoRequest.setThumbnail(prodThumbnail);
			}



			//set created on.
			prodInfoRequest.setCreatedOn(bean.getProdUtill().getXMLGregorianCalendarFormat(product.getCreateTimestamp()));


			setDataForAssociatedObjects(product, psl, prodInfoRequest, bean);

			//set created on.
			prodInfoRequest.setCreatedOn(bean.getProdUtill().getXMLGregorianCalendarFormat(product.getCreateTimestamp()));
			//set created by.
			prodInfoRequest.setCreatedBy(bean.getProdProcessor().getProductCreator(product));
			//set last updated on.
			prodInfoRequest.setLastUpdated(bean.getProdUtill().getXMLGregorianCalendarFormat(product.getModifyTimestamp()));
			//set last updated by.
			prodInfoRequest.setLastUpdatedBy(bean.getProdProcessor().getProductModifier(product));
			
			// Phase 14 - EMP-481 - Start
			setLastTriggeredDetails(bean, product, prodInfoRequest);
			// Phase 14 - EMP-481 - End

			return prodInfoRequest;
		}else{
			LCSProduct prod = (LCSProduct) VersionHelper.getFirstVersion(product);
			prod = (LCSProduct) VersionHelper.latestIterationOf(prod);
			LOGGER.info("Product ID   >>>>>>>>>>>>>>>    "+FormatHelper.getNumericObjectIdFromObject(prod));
			String plmID = String.valueOf(prod.getBranchIdentifier());
			bean.getLogEntryProcessor().setLogEntryForProductOutboundIntegration(plmID, "WSDL_VALIDATION_FAILED", prod, bean);
			return null;
		}
	}

	// Phase 14 - EMP-481 - Start
	/**
	 * @param bean
	 * @param product
	 * @param prodInfoRequest
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 */
	private void setLastTriggeredDetails(SMProductOutboundIntegrationBean bean,
			LCSProduct product, Product prodInfoRequest) throws WTException, DatatypeConfigurationException {
		// TODO Auto-generated method stub
		//Date lastTriggeredOn = (Date)product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_TRIGGERED_ON);
		Date lastTriggeredOn = new Date();
		//if(lastTriggeredOn!=null){
			prodInfoRequest.setSmLastIntTriggered(bean.getProdUtill().getXMLGregorianCalendarFormat(lastTriggeredOn));
			prodInfoRequest.setSmUserIntTriggered(bean.getProdProcessor().getProductModifier(product));
		//}
	}
	// Phase 14 - EMP-481 - End

	/**
	 * Phase 13 | added setAdditionalDataForProductBean function to set data for
	 * product integration.
	 * 
	 * @param product
	 * @param prodInfoRequest
	 * @param productType
	 * @throws WTException
	 */
	private void setAdditionalDataForProductBean(LCSProduct product, Product prodInfoRequest, String productType) throws WTException {



		// Phase -8 sepd chnages
		if (!productType.startsWith("SEPD")) {
			// set Project.
			LCSLifecycleManaged project = (LCSLifecycleManaged) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_PROJECT);

			if (null != project) {
				prodInfoRequest.setSmProject(project.getName());
			}
		}

		// set brand
		prodInfoRequest.setVrdBrand(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_BRAND).toString());

		// updated for 3.9.0.0 Build
		// set description.
		if (FormatHelper.hasContent(String.valueOf(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_DESCRIPTION)))) {

			prodInfoRequest.setVrdDescription(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_DESCRIPTION).toString());
		}

		// set style code.
		LOGGER.debug("Style Code for product  >>>>   " + product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_STYLE_CODE));
		if (FormatHelper.hasContent(String.valueOf(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_STYLE_CODE)))) {
			prodInfoRequest.setSmStyleCode(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_STYLE_CODE).toString());
		}

		// set age.
		prodInfoRequest.setSmAge(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_AGE).toString());

		// style number
		prodInfoRequest.setVrdStyleNum(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_NUMBER).toString());

		// setting gender.
		prodInfoRequest.setVrdGender(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_GENDER).toString());

		// set product data.
		setProductAttributesOnBean(product, prodInfoRequest);

		// Phase -8 sepd chnages
		if (productType.startsWith("APD") || productType.startsWith("smAccessories")) {
			// set style name ru
			if (FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_STYLE_NAME_RU))) {
				prodInfoRequest.setSmStyleNameRu(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_STYLE_NAME_RU).toString());
			}
			if (FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_FIT))) {
				// set fit.
				prodInfoRequest.setSmFit(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_FIT).toString());
			}
		}

		// updated for 3.9.0.0 Build
		// added SPECIFIC_PRODUCT attribute for all divisions of product
		if (FormatHelper.hasContent(String.valueOf(product.getValue(SMProductOutboundWebServiceConstants.SPECIFIC_PRODUCT_NEW)))) {
			// set setSmSEPDSpecificOfProduct.
			prodInfoRequest.setSmSEPDSpecificOfProduct(
					String.valueOf(product.getValue(SMProductOutboundWebServiceConstants.SPECIFIC_PRODUCT_NEW)));

		}

		// Phase -8 sepd chnages
		if (productType.startsWith("SEPD")
				&& FormatHelper.hasContent(String.valueOf(product.getValue(SMProductOutboundWebServiceConstants.SPECIFIC_PRODUCT)))) {

				// set setSmSEPDSpecificOfProduct.
				prodInfoRequest.setSmSEPDSpecificOfProduct(
						String.valueOf(product.getValue(SMProductOutboundWebServiceConstants.SPECIFIC_PRODUCT)));

		}

	}

	/**
	 * @param product
	 * @param psl
	 * @throws WTException
	 */
	public void setDataForAssociatedObjects(LCSProduct product, LCSProductSeasonLink psl,
			Product prodInfoRequest, SMProductOutboundIntegrationBean bean) throws WTException {

		if(FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY))){
			LOGGER.info("Product cancelled Flag  >>>>   "+bean.isProductCancelledFlag());
			LOGGER.info("Product IS Update PSL  ?? ******** >>>>   "+bean.isUpdateProductSeasonLink());
			//find colorways.
			validateColorwaysForProduct(product, prodInfoRequest, bean);
		}


		if (!SMProductOutboundWebServiceConstants.CANCELLED.equalsIgnoreCase(product.getLifeCycleState().toString())
				|| bean.isProductCancelledFlag() && FormatHelper.hasContent(
						(String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY))) {
			//set data on product season link.
			validateProductSeasonLinkOnProduct(psl, prodInfoRequest, bean);
		}
	}

	/**
	 * @param psl
	 * @param prodInfoRequest
	 */
	public void validateProductSeasonLinkOnProduct(LCSProductSeasonLink psl, Product prodInfoRequest,
			SMProductOutboundIntegrationBean bean) {
		if(null != psl){
			//set data on product season link.
			ProductSeasonLink productSeasonLinkDataBean = bean.getProdProcessor()
					.setDataForProductSeasonLinkOutboundRequest(psl, new ProductSeasonLink(), bean);
			
			if(null != productSeasonLinkDataBean){
				prodInfoRequest.getProductSeasonLink().add(productSeasonLinkDataBean);
			}
		}
	}

	/**
	 * @param product
	 * @param prodInfoRequest
	 */
	public void validateColorwaysForProduct(LCSProduct product, Product prodInfoRequest,
			SMProductOutboundIntegrationBean bean) {
		if (!SMProductOutboundWebServiceConstants.CANCELLED.equalsIgnoreCase(product.getLifeCycleState().toString())
				&& bean.isProductCancelledFlag()
				&& null != bean.getProdProcessor().findColorwaysForProduct(product)) {
			
			getColorwaysForProduct(product, prodInfoRequest, bean);
			
		} else if (SMProductOutboundWebServiceConstants.CANCELLED
				.equalsIgnoreCase(product.getLifeCycleState().toString())
				&& bean.isUpdateProductSeasonLink()) {
			
			LOGGER.debug("processing cancelled request");
			getColorwaysForProduct(product, prodInfoRequest, bean);
			
		}
	}

	/**
	 * @param product
	 * @param prodInfoRequest
	 */
	public void getColorwaysForProduct(LCSProduct product, Product prodInfoRequest,
			SMProductOutboundIntegrationBean bean) {
		List<LCSSKU> colorwayList = bean.getProdProcessor().findColorwaysForProduct(product);
		Colorway colorwayDataBean;
		LOGGER.info("Product Colorway List Size   >>>>>     "+colorwayList.size());
		for(LCSSKU sku : colorwayList){
			//set data on colorway
			colorwayDataBean = bean.getProdProcessor().setDataForColorwayOutboundRequest(sku, new Colorway(), bean);
			if(null != colorwayDataBean){
				prodInfoRequest.getColorway().add(colorwayDataBean);
			}
		}
	}

	/**
	 * @param product
	 * @param prodInfoRequest
	 * @throws WTException
	 */
	public void setProductAttributesOnBean(LCSProduct product,
			Product prodInfoRequest) throws WTException {
		LCSProduct styleAnalogue = (LCSProduct) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_STYLE_ANALOGUE);
		if(null != styleAnalogue &&  FormatHelper.hasContent((String) styleAnalogue.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY))){
			prodInfoRequest.setSmStyleAnalogue(styleAnalogue.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY).toString());
		}

		//updated logic for FPD Changes
		String productType=product.getFlexType().getFullName();
		//validates FPD Type product
		//-- Phase 14 - EMP_498 - Start : Added check for SEPD --//
		if(productType.startsWith("APD") || productType.startsWith("smAccessories") || productType.startsWith("SEPD")) {
			//fabric group.
			LOGGER.debug("--productType : "+productType);
			LOGGER.debug("--Attribute_Key: Material Group : "+SMProductOutboundWebServiceConstants.PRODUCT_FABRIC_GROUP);
			String materialGroup = (String)product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_FABRIC_GROUP);
			LOGGER.debug("-- materialGroup Value : "+materialGroup);
			if(FormatHelper.hasContent(materialGroup)){
				prodInfoRequest.setVrdMaterialGroup(materialGroup);
			}
		}
		//-- Phase 14 - EMP_498 - END: Added check for SEPD --//

		//set sub division MDM ID.
		LCSLifecycleManaged subDivisionObj = (LCSLifecycleManaged) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SUB_DIVISION);
		if(null != subDivisionObj && FormatHelper.hasContent((String) subDivisionObj.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SUB_DIVISION_MDMID))){
			prodInfoRequest.setSmMDMDIV(subDivisionObj.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SUB_DIVISION_MDMID).toString());
		}else{
			prodInfoRequest.setSmMDMDIV(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
		}
	}
}

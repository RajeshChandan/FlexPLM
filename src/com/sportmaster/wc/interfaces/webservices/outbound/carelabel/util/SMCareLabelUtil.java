/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.Logger;
import wt.util.WTException;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;


/**
 * SMCareLabelUtil - Functionality .
 * 
 * @author 'true' ITC
 * @version 'true' 1.0 version number
 * @since March 13, 2018
 */
public class SMCareLabelUtil {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCareLabelUtil.class);
	/**
	 * ida2a2.
	 */
	private static final String IDA2A2 = "idA2A2";
	/**
	 * log entry.
	 */
	private static final String LCSLOGENTRY = "LCSLogEntry";
	/**
	 * flex type id path.
	 */
	private static final String FLEXTYPE_ID_PATH = "flexTypeIdPath";
	/**
	 * Request ID.
	 */
	public static final Integer  CARELABEL_OUTBOUND_INTEGRATION_REQUEST_ID=Integer.valueOf(SMCareLabelConstants.LOG_ENTRY_CARELABEL_OUTBOUND_INTEGRATION_INITIAL_REQUEST_ID);
	/**
	 * Constructor.
	 */
	public SMCareLabelUtil(){
		//constructor.
	}

	/**
	 * Convert Date Object to XMLGregorianCalendar.
	 * @param date - Date.
	 * @return xmlGregorianCalendar - XMLGregorianCalendar.
	 * @throws DatatypeConfigurationException - DatatypeConfigurationException.
	 */
	public static javax.xml.datatype.XMLGregorianCalendar getXMLGregorianCalendarFormat(Date date) throws DatatypeConfigurationException{
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		//set date.
		gregorianCalendar.setTime(date);
		//return XMLGregorian Calendar Date.
		return javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
	}

	/**
	 * Return unique request id for Care Label.
	 * @return int - supplierRequestID.
	 * @throws WTException - WTException.
	 */
	public int  generateCareLabelOutboundIntegrationRequestID(){
		int careLabelRequestID=CARELABEL_OUTBOUND_INTEGRATION_REQUEST_ID;
		try{
			LOGGER.info("### Generating Request ID for CARE LABEL Outbound Integration !! ####");
			String careLabelLogEntry=LCSLOGENTRY;
			FlexType careLabelLogType =  com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMCareLabelConstants.CARE_LABEL_INTEGARTION_LOG_ENTRY_PATH);
			com.lcs.wc.flextype.FlexTypeAttribute careLabelReqIDAttr = careLabelLogType.getAttribute(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_REQUEST_ID);
			SearchResults careLabelSearchResult = null;
			PreparedQueryStatement carelabelstmt = new PreparedQueryStatement();//Creating Statement.

			//stmt.appendFromTable(supplierLogEntry);
			carelabelstmt.appendSelectColumn(new QueryColumn(careLabelLogEntry, IDA2A2));
			carelabelstmt.appendSelectColumn(careLabelLogEntry, careLabelReqIDAttr.getColumnName());
			//add tables
			carelabelstmt.appendFromTable(careLabelLogEntry);
			carelabelstmt.appendCriteria(new Criteria(careLabelLogEntry, FLEXTYPE_ID_PATH, careLabelLogType.getIdPath(),Criteria.EQUALS));

			careLabelSearchResult=LCSQuery.runDirectQuery(carelabelstmt);
			List<FlexObject> careLabelData=careLabelSearchResult.getResults();
			int reqId;
			careLabelData=(List<FlexObject>) com.lcs.wc.util.SortHelper.sortFlexObjects(careLabelData, careLabelLogEntry+"."+careLabelReqIDAttr.getColumnName());

			//generate request ID.
			if(!careLabelData.isEmpty()){
				reqId=careLabelData.get(careLabelData.size()-1).getInt( careLabelLogEntry+"."+careLabelReqIDAttr.getColumnName());
				if(reqId==0){
					careLabelRequestID=careLabelRequestID+1;
				}else{
					careLabelRequestID =reqId+1;
				}
				return careLabelRequestID;
			}
		}catch(WTException excpt){
			LOGGER.error(excpt.getLocalizedMessage());
			excpt.printStackTrace();
		}
		LOGGER.debug("Setting request ID >>>>>>>" +careLabelRequestID);
		return careLabelRequestID+1;
	}

	/**
	 * Get Product Season details.
	 * @param psl - LCSProductSeasonLink.
	 * @return String.
	 */
	public static String getObjectDetails(LCSProductSeasonLink psl){
		try{
			LCSProduct productSeasonRev = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+(int)psl.getProductSeasonRevId());

			LCSSeason seasonObj = (LCSSeason) VersionHelper.latestIterationOf(psl.getSeasonMaster());

			return seasonObj.getName()+" , "+productSeasonRev.getName();
		}catch(WTException exp){
			LOGGER.error(exp.getLocalizedMessage());
			exp.printStackTrace();
			return null;
		}
	}

	/**
	 * this method returns source to season link.
	 * @param sourcOid - String.
	 * @param seasOid - String.
	 * @return - LCSSourceToSeasonLink.
	 * @throws WTException - exception.
	 */
	public static LCSSourceToSeasonLink getSourcetoSeasonLink(String sourcOid, String seasOid) throws WTException {

		LCSSourcingConfig sourc=(LCSSourcingConfig)LCSQuery.findObjectById("VR:com.lcs.wc.sourcing.LCSSourcingConfig:"+sourcOid);
		LCSSeason season=(LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+seasOid);
		LCSSourceToSeasonLink stsl=null;
		if(sourc != null && season != null){
			stsl = (new com.lcs.wc.sourcing.LCSSourcingConfigQuery()).getSourceToSeasonLink(sourc, season);
		}


		return stsl;

	}

	/**
	 * returns user login Id.
	 * @param proseasLink the LCSProductSeasonLink.
	 * @param key the string.
	 * @return String.
	 * @throws WTException the exception.
	 */
	public static String getUserdetail(LCSProductSeasonLink proseasLink, String key) throws WTException{

		FlexObject user = (FlexObject) proseasLink.getValue(key);
		String userLogin="";
		String userDetail="";
		if(null != user){
			String userEmail = "";
			userLogin = user.getString("AUTHENTICATIONNAME");

			//LOGGER.info("USER LOGIN >>>>>>>>>>>>   "+userLogin);

			wt.org.WTUser wtuser = com.lcs.wc.util.UserGroupHelper.getWTUser(userLogin);
			if(null != wtuser){
				userEmail = wtuser.getEMail();
			}

			//set brand manager.
			if(FormatHelper.hasContent(userEmail)){
				userDetail =  userEmail;
			}else if(FormatHelper.hasContent(userLogin)){
				userDetail =  userLogin;
			}else {
				String userName = user.getString("NAME");

				userDetail= userName;
			}
		}

		LOGGER.info("USER NAME **************   "+userDetail);
		return userDetail;
	}

	/**
	 * Get Product Season Link.
	 * @param psl - LCSProductSeasonLink the LCSProductSeasonLink.
	 * @return - String.
	 */
	public static String getProductSeasonLinkPLMID(LCSProductSeasonLink psl){
		try{
			/*if(FormatHelper.hasContent(String.valueOf(psl.getValue(SMCareLabelConstants.PRODUCT_SEASON_LINK_AD_HOC_PLM_ID)))){
				return String.valueOf(psl.getValue(SMCareLabelConstants.PRODUCT_SEASON_LINK_AD_HOC_PLM_ID));
			}else{*/
			LCSProduct prod = SeasonProductLocator.getProductARev(psl);
			LCSSeason season = SeasonProductLocator.getSeasonRev(psl);
			wt.fc.ReferenceFactory refFact = new wt.fc.ReferenceFactory();
			String refString = refFact.getReferenceString(prod.getMasterReference());
			char delimiter = ':';
			String ida3MasterReference = refString.substring(refString.lastIndexOf(delimiter) + 1);
			//String seasonID = FormatHelper.getNumericObjectIdFromObject(season);
			String seasonMasterID = refFact.getReferenceString(season.getMasterReference());
			String seasonMasterReferenceID = seasonMasterID.substring(seasonMasterID.lastIndexOf(delimiter) + 1);
			return ida3MasterReference+"-"+seasonMasterReferenceID;
			//}
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage());
			we.printStackTrace();
			return null;
		}
	}

	/**
	 * This method gets the MDMID.
	 * @param obj the WTObject.
	 * @return the String.
	 * @throws WTException the exception.
	 */
	public static String getMDMID (wt.fc.WTObject obj) throws WTException{

		String strMDMID="EmptyMDMID";
		if(obj instanceof LCSProduct){

			strMDMID = (String)((LCSProduct)obj).getValue("smMDMPRO");
		}
		if(obj instanceof LCSSeason){
			strMDMID = (String)((LCSSeason)obj).getValue("smMDMSEA");
		}
		if(obj instanceof LCSProductSeasonLink){
			strMDMID = (String)((LCSProductSeasonLink)obj).getValue("smMDMPSL");
		}
		if(obj instanceof LCSSKU){
			strMDMID = (String)((LCSSKU)obj).getValue("smMDMSKU");
		}
		if(obj instanceof LCSSKUSeasonLink){
			strMDMID = (String)((LCSSKUSeasonLink)obj).getValue("smMDMSSL");
		}
		if(obj instanceof LCSMaterial){
			strMDMID = (String)((LCSMaterial)obj).getValue("smMDMMAT");
		}
		if(obj instanceof LCSSupplier){
			strMDMID = (String)((LCSSupplier)obj).getValue("smMDMVENDOR");
		}
		if(obj instanceof com.lcs.wc.color.LCSColor){
			strMDMID = (String)((com.lcs.wc.color.LCSColor)obj).getValue("smMDMCOL");
		}

		//Setting emptyMDMID if no MDM ID
		if(!FormatHelper.hasContent(strMDMID)){
			strMDMID="EmptyMDMID";

		}
		return strMDMID;

	}



	/**
	 * Get Colorway Season Link PLM ID.
	 * @param ssl - LCSSKUSeasonLink.
	 * @return - String.
	 */
	public static String getColorwaySeasonPLMID(LCSSKUSeasonLink ssl){
		try{
			/*if(FormatHelper.hasContent(String.valueOf(ssl.getValue(SMCareLabelConstants.COLORWAY_SEASON_LINK_AD_HOC_PLM_ID)))){
				return String.valueOf(ssl.getValue(SMCareLabelConstants.COLORWAY_SEASON_LINK_AD_HOC_PLM_ID));
			}else{*/
			LCSSKU colorway = SeasonProductLocator.getSKUARev(ssl);
			LCSSeason seasonObj = SeasonProductLocator.getSeasonRev(ssl);
			wt.fc.ReferenceFactory skuRf = new wt.fc.ReferenceFactory();
			String skuRefString = skuRf.getReferenceString(colorway.getMasterReference());
			char delim = ':';
			String skuIDA3MasterRef = skuRefString.substring(skuRefString.lastIndexOf(delim) + 1);
			//String seasonID = FormatHelper.getNumericObjectIdFromObject(season);
			String colorwaySeasonMasterID = skuRf.getReferenceString(seasonObj.getMasterReference());
			String seasonMasterReferenceID = colorwaySeasonMasterID.substring(colorwaySeasonMasterID.lastIndexOf(delim) + 1);
			return skuIDA3MasterRef+"-"+seasonMasterReferenceID;
			//}
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage());
			we.printStackTrace();
			return null;
		}
	}

	/**
	 * Get PLM ID for Product.
	 * @param product - LCSProduct
	 * @return - String.
	 * @throws WTException 
	 */
	public static String getProductPLMID(LCSProduct product) throws WTException{
		LCSProduct prod = (LCSProduct) VersionHelper.getFirstVersion(product);
		prod = (LCSProduct) VersionHelper.latestIterationOf(prod);

		return String.valueOf(prod.getBranchIdentifier());
	}

	/**
	 * Get PLM ID for Colorway.
	 * @param sku - LCSSKU.
	 * @return - String.
	 * @throws WTException 
	 */
	public static String getColorwayPLMID(LCSSKU sku) throws WTException{
		LCSSKU colorway = (LCSSKU) VersionHelper.getFirstVersion(sku);
		colorway = (LCSSKU) VersionHelper.latestIterationOf(colorway);

		return String.valueOf(colorway.getBranchIdentifier());
	}

	/**
	 * Get PLM ID for Supplier.
	 * @param supplier - LCSSupplier.
	 * @return - String.
	 * @throws WTException - WTException.
	 */
	public static String getSupplierPLMID(LCSSupplier supplier) {
		String supplierMasterReference="";
		try{
			wt.fc.ReferenceFactory suppRef = new wt.fc.ReferenceFactory();
			String supplierRefString = suppRef.getReferenceString(supplier.getMasterReference());
			char colon = ':';
			supplierMasterReference = supplierRefString.substring(supplierRefString.lastIndexOf(colon) + 1);

		}catch(WTException excpt){
			LOGGER.error(excpt.getLocalizedMessage());
			excpt.printStackTrace();
		}
		return supplierMasterReference;
	}


	/**
	 * This method return supplier object.
	 * @param eachRowBOMdata the flexObject.
	 * @return the LCSSupplier.
	 * @throws WTException
	 */
	public static LCSSupplier getSupplier(FlexObject eachRowBOMdata) throws WTException {
		String supplierId="";
		LCSSupplier suppObj=null;
		LCSMaterialSupplier matSupp = (LCSMaterialSupplier)LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterialSupplier:" +eachRowBOMdata.getString("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO"));
		if(matSupp != null){
			supplierId = FormatHelper.getNumericVersionIdFromObject((LCSSupplier) VersionHelper.latestIterationOf(matSupp.getSupplierMaster()));
		}

		if(FormatHelper.hasContent(supplierId)){
			suppObj = (LCSSupplier)LCSQuery.findObjectById("VR:com.lcs.wc.supplier.LCSSupplier:"+supplierId);

		}

		return suppObj;
	}

	/**
	 * Get PLM ID for Material.
	 * @param material - LCSMaterial
	 * @return - String.
	 */
	public static String getMaterialPLMID(LCSMaterial material){
		return FormatHelper.getNumericFromReference(material.getMasterReference());
	}

	/**
	 * Get PLM ID for Material-Supplier.
	 * @param material - LCSMaterialSupplier.
	 * @return - String.
	 */
	public static String getMaterialSupplierPLMID(com.lcs.wc.material.LCSMaterialSupplier materialSupplier){
		return FormatHelper.getNumericFromReference(materialSupplier.getMasterReference());
	}
}

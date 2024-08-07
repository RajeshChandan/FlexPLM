/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

/**
 * @author BSC
 *
 */
public class SMProductOutboundUtil {

	/**
	 * AS_INT.
	 */
	private static final String AS_INT = " AS INT))";
	
	/**
	 * MAX_CAST_LCSLOGENTRY.
	 */
	private static final String MAX_CAST_LCSLOGENTRY = "max( CAST(LCSLOGENTRY";
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundUtil.class);
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
	public static final Integer  PRODUCT_OUTBOUND_INTEGRATION_REQUEST_ID=Integer.valueOf(SMProductOutboundWebServiceConstants.LOG_ENTRY_PRODUCT_OUTBOUND_INTEGRATION_INITIAL_REQUEST_ID);
	/**
	 * Constructor.
	 */
	public SMProductOutboundUtil(){
		//constructor.
	}

	/**
	 * Convert Date Object to XMLGregorianCalendar.
	 * @param date - Date
	 * @return xmlGregorianCalendar - XMLGregorianCalendar 
	 * @throws DatatypeConfigurationException - DatatypeConfigurationException
	 */
	public XMLGregorianCalendar getXMLGregorianCalendarFormat(Date date) throws DatatypeConfigurationException{
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		//set date.
		gregorianCalendar.setTime(date);
		//return XMLGregorian Calendar Date.
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
	}

	/**
	 * Return unique request id for Product Season.
	 * @return int - supplierRequestID
	 * @throws WTException - WTException
	 */
	/**
	 * @return
	 */
	public int  generateProductSeasonOutboundIntegrationRequestID(){
		int prodSeasonRequestID=PRODUCT_OUTBOUND_INTEGRATION_REQUEST_ID;
		try{
			LOGGER.info("Generating Request ID for Product Season Outbound Integration !!");
			String prodSeasonLogEntry=LCSLOGENTRY;
			FlexType prodSeasonLogType = FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_PRODUCT_SEASON_OUTBOUND_PATH);
			FlexTypeAttribute prodSeasonAttr = prodSeasonLogType.getAttribute(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID);
			SearchResults prodSeasonResult = null;
			
			PreparedQueryStatement prdSeasonstmt = new PreparedQueryStatement();//Creating Statement.
			
			prdSeasonstmt.appendSelectColumn(MAX_CAST_LCSLOGENTRY, prodSeasonAttr.getColumnName()+AS_INT);
			
			//add tables
			prdSeasonstmt.appendFromTable(prodSeasonLogEntry);
			
			prdSeasonstmt.appendCriteria(new Criteria(prodSeasonLogEntry, FLEXTYPE_ID_PATH, prodSeasonLogType.getIdPath(),Criteria.EQUALS));

			prodSeasonResult=LCSQuery.runDirectQuery(prdSeasonstmt);
			
			List<FlexObject> prodSeasonData=prodSeasonResult.getResults();
			
			int reqId;

			//generate request ID.
			if(!prodSeasonData.isEmpty()){
				reqId=prodSeasonData.get(prodSeasonData.size()-1).getInt( MAX_CAST_LCSLOGENTRY+"."+prodSeasonAttr.getColumnName()+AS_INT);
				if(reqId==0){
					prodSeasonRequestID=prodSeasonRequestID+1;
				}else{
					prodSeasonRequestID =reqId+1;
				}
				return prodSeasonRequestID;
			}
		}catch(WTException excpt){
			LOGGER.error(excpt.getLocalizedMessage(), excpt);
		}
		return prodSeasonRequestID+1;
	}

	/**
	 * Return unique request id for Colorway Season.
	 * @return int - supplierRequestID
	 * @throws WTException - WTException
	 */
	public int  generateColorwaySeasonOutboundIntegrationRequestID(){
		int skuSeasonRequestID=PRODUCT_OUTBOUND_INTEGRATION_REQUEST_ID;
		try{
			LOGGER.info("Generating Request ID for Colorway Season Outbound Integration !!");
			
			String skuLogEntry=LCSLOGENTRY;
			FlexType skuLogType = FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_COLORWAY_SEASON_OUTBOUND_PATH);
			FlexTypeAttribute skuAttr = skuLogType.getAttribute(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID);
			
			SearchResults skuresult = null;
			
			PreparedQueryStatement skustmt = new PreparedQueryStatement();//Creating Statement.
			
			skustmt.appendSelectColumn(MAX_CAST_LCSLOGENTRY, skuAttr.getColumnName()+AS_INT);
			
			//add tables
			skustmt.appendFromTable(skuLogEntry);
			
			skustmt.appendCriteria(new Criteria(skuLogEntry, FLEXTYPE_ID_PATH, skuLogType.getIdPath(),Criteria.EQUALS));

			skuresult=LCSQuery.runDirectQuery(skustmt);
			
			List<FlexObject> skudata=skuresult.getResults();
			int reqId;
			
			//generate request ID.
			if(!skudata.isEmpty()){
				reqId=skudata.get(skudata.size()-1).getInt( MAX_CAST_LCSLOGENTRY+"."+skuAttr.getColumnName()+AS_INT);
				if(reqId==0){
					skuSeasonRequestID=skuSeasonRequestID+1;
				}else{
					skuSeasonRequestID =reqId+1;
				}
				return skuSeasonRequestID;
			}
		}catch(WTException excpt){
			LOGGER.error(excpt.getLocalizedMessage(), excpt);
		}
		return skuSeasonRequestID+1;
	}

	/**
	 * Return unique request id for Product Season.
	 * @return int - supplierRequestID
	 * @throws WTException - WTException
	 */
	public int  generateProductOutboundIntegrationRequestID(){
		int productRequestID=PRODUCT_OUTBOUND_INTEGRATION_REQUEST_ID;
		try{
			LOGGER.info("Generating Request ID for Product Outbound Integration !!");
			String prodLogEntry=LCSLOGENTRY;
			
			FlexType prodLogType = FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_PRODUCT_OUT_BOUND_PATH);
			FlexTypeAttribute prodAttr = prodLogType.getAttribute(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID);
			
			SearchResults result = null;
			PreparedQueryStatement stmt = new PreparedQueryStatement();//Creating Statement.
			
			stmt.appendSelectColumn(MAX_CAST_LCSLOGENTRY, prodAttr.getColumnName()+AS_INT);
			
			//add tables
			stmt.appendFromTable(prodLogEntry);
			
			stmt.appendCriteria(new Criteria(prodLogEntry, FLEXTYPE_ID_PATH, prodLogType.getIdPath(),Criteria.EQUALS));

			result=LCSQuery.runDirectQuery(stmt);
			
			List<FlexObject> data=result.getResults();
			int reqId;
			
			//generate request ID.
			if(!data.isEmpty()){
				reqId=data.get(data.size()-1).getInt(MAX_CAST_LCSLOGENTRY+"."+prodAttr.getColumnName()+AS_INT);
				if(reqId==0){
					productRequestID=productRequestID+1;
				}else{
					productRequestID =reqId+1;
				}
				return productRequestID;
			}
		}catch(WTException excpt){
			LOGGER.error(excpt.getLocalizedMessage(), excpt);
		}
		return productRequestID+1;
	}

	/**
	 * Return unique request id for Product Season.
	 * @return int - supplierRequestID
	 * @throws WTException - WTException
	 */
	public int  generateColorwayOutboundIntegrationRequestID(){
		int colorwayRequestID=PRODUCT_OUTBOUND_INTEGRATION_REQUEST_ID;
		try{
			LOGGER.info("Generating Request ID for Colorway Outbound Integration !!");
			String colorwayLogEntry=LCSLOGENTRY;
			
			FlexType colorwayLogType = FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_COLORWAY_OUTBOUND_PATH);
			FlexTypeAttribute colorwayAttr = colorwayLogType.getAttribute(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID);
			
			SearchResults result = null;
			
			PreparedQueryStatement colorwaystmt = new PreparedQueryStatement();//Creating Statement.
			
			colorwaystmt.appendSelectColumn(MAX_CAST_LCSLOGENTRY, colorwayAttr.getColumnName()+AS_INT);
			
			//add tables
			colorwaystmt.appendFromTable(colorwayLogEntry);
			
			colorwaystmt.appendCriteria(new Criteria(colorwayLogEntry, FLEXTYPE_ID_PATH, colorwayLogType.getIdPath(),Criteria.EQUALS));

			result=LCSQuery.runDirectQuery(colorwaystmt);
			
			List<FlexObject> colorwaydata=result.getResults();
			
			int reqId;
			
			//generate request ID.
			if(!colorwaydata.isEmpty()){
				reqId=colorwaydata.get(colorwaydata.size()-1).getInt( MAX_CAST_LCSLOGENTRY+"."+colorwayAttr.getColumnName()+AS_INT);
				if(reqId==0){
					colorwayRequestID=colorwayRequestID+1;
				}else{
					colorwayRequestID =reqId+1;
				}
				return colorwayRequestID;
			}
		}catch(WTException excpt){
			LOGGER.error(excpt.getLocalizedMessage(), excpt);
		}
		return colorwayRequestID+1;
	}


	/**
	 * Validates active department.
	 * @param product - LCSProduct.
	 * @throws WTException 
	 */
	public boolean validateActiveDepartment(WTObject obj) throws WTException {
		LCSProduct product = null;
		//product obj.
		if(obj instanceof LCSProduct){
			product = (LCSProduct) obj;
		}else if(obj instanceof LCSSKU){
			LCSSKU sku = (LCSSKU) obj;
			product = sku.getProduct();

		}else if(obj instanceof LCSProductSeasonLink){
			LCSProductSeasonLink psl = (LCSProductSeasonLink) obj;
			product = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+(int)psl.getProductSeasonRevId());

		}else if(obj instanceof LCSSKUSeasonLink){
			LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) obj;
			product = SeasonProductLocator.getProductARev(ssl);
		}
		if(null != product){
			//getting product departments.
			String productDept = product.getFlexType().getFullName();
			//adding all departments to list.
			List<String> activeDeptList = FormatHelper.commaSeparatedListToList(SMProductOutboundWebServiceConstants.ACTIVE_DEPARTMENTS_FOR_INTEGRATION);
			for(String currentDept : activeDeptList){
				//compare if product is one of current department.
				if(productDept.startsWith(currentDept)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * persist colorway season link.
	 * @param ssl
	 */
	public void persistColorwaySeasonLinkObject(LCSSKUSeasonLink ssl){
		try{
			LOGGER.info("persisting colorway season link object .......");
			com.lcs.wc.foundation.LCSLogic.persist(ssl, true);
		}catch(WTException wExp){
			LOGGER.error(wExp.getLocalizedMessage(), wExp);
		}
	}
	
	/**
	 * persists product season Link.
	 * @param psl
	 */
	public void persistProductSeasonLinkObject(LCSProductSeasonLink psl){
		try{
			LOGGER.info("persisting product season link object .......");
			com.lcs.wc.foundation.LCSLogic.persist(psl, true);
		}catch(WTException wp){
			LOGGER.error(wp.getLocalizedMessage(), wp);
		}
	}
	
	/**
	 * persists colorway object.
	 * @param sku - LCSSKU
	 * @throws WTException 
	 */
	public void persistColorwayObject(LCSSKU colorway) throws WTException{
		try{
			LOGGER.info("persisting colorway object .......");
			LCSSKU sku = colorway;
			LOGGER.info("SKU Version >>>>>>>>>>>>  "+sku.getVersionIdentifier().getValue());
			com.lcs.wc.part.LCSPart part=sku;
			com.lcs.wc.part.LCSPart workingCopy=(com.lcs.wc.part.LCSPart) VersionHelper.checkout(part);
			sku=(LCSSKU) part.copyState(workingCopy);
			sku= (LCSSKU) com.lcs.wc.product.LCSProductLogic.persist(sku, true);
			if(VersionHelper.isCheckedOut(sku)){
				sku=(LCSSKU) wt.vc.wip.WorkInProgressHelper.service.checkin(sku,"");
				com.lcs.wc.foundation.LCSLogic.loadMethodContextCache(sku);
			}
		}catch(WTException we){
			
			if(VersionHelper.isCheckedOut(colorway)){
				VersionHelper.undoCheckout(colorway);
			}
			LOGGER.error(we.getLocalizedMessage(), we);
			
		} catch (WTPropertyVetoException pv) {
			if(VersionHelper.isCheckedOut(colorway)){
				VersionHelper.undoCheckout(colorway);
			}
			LOGGER.error(pv.getLocalizedMessage(), pv);
		}
	}
	
	/**
	 * persists product object.
	 * @param product - LCSProduct
	 * @throws WTPropertyVetoException 
	 */
	public void persistProductObject(LCSProduct prdct) {
		try{
			LOGGER.info("persisting product object .......");
			LOGGER.info("Product Version ----------->   "+prdct.getVersionIdentifier().getValue());
			LCSProduct product = prdct;
			com.lcs.wc.part.LCSPart part=product;
			com.lcs.wc.part.LCSPart workingCopy=(com.lcs.wc.part.LCSPart) VersionHelper.checkout(part);
			product=(LCSProduct) part.copyState(workingCopy);
			product= (LCSProduct) com.lcs.wc.product.LCSProductLogic.persist(product, true);
			if(VersionHelper.isCheckedOut(product)){
				product=(LCSProduct) wt.vc.wip.WorkInProgressHelper.service.checkin(product,"");
				com.lcs.wc.foundation.LCSLogic.loadMethodContextCache(product);
			}
		}catch(WTException wt){
			
			LOGGER.error(wt.getLocalizedMessage(), wt);
			
		} catch (WTPropertyVetoException pvE) {
			
			LOGGER.error(pvE.getLocalizedMessage(), pvE);
		}
	}
}

/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.bom.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMLogic;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;

import wt.util.WTException;

/**
 * @author BSC
 *
 */
public class SMBOMOutboundUtil {

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
	private static final Logger LOGGER = Logger.getLogger(SMBOMOutboundUtil.class);
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
	public static final Integer  BOM_OUTBOUND_INTEGRATION_REQUEST_ID=Integer.valueOf(SMBOMOutboundWebServiceConstants.LOG_ENTRY_BOM_OUTBOUND_INTEGRATION_INITIAL_REQUEST_ID);
	/**
	 * Constructor.
	 */
	public SMBOMOutboundUtil(){
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
	 * Return unique request id for BOM Part.
	 * @return int -bomPartRequestID
	 * @throws WTException - WTException
	 */
	public int  generateBOMPartOutboundIntegrationRequestID(){
		
		int bomPartRequestID=BOM_OUTBOUND_INTEGRATION_REQUEST_ID;
		try{
			LOGGER.info("Generating Request ID for BOMPart Outbound Integration !!");
			String bomLogEntry=LCSLOGENTRY;
			
			FlexType bomLogType = FlexTypeCache.getFlexTypeFromPath(SMBOMOutboundWebServiceConstants.LOG_ENTRY_BOM_OUT_BOUND_PATH);
			FlexTypeAttribute bomAttr = bomLogType.getAttribute(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_REQUEST_ID);
			
			SearchResults result = null;
			PreparedQueryStatement stmt = new PreparedQueryStatement();//Creating Statement.
			
			stmt.appendSelectColumn(MAX_CAST_LCSLOGENTRY, bomAttr.getColumnName()+AS_INT);
			
			//add tables
			stmt.appendFromTable(bomLogEntry);
			
			stmt.appendCriteria(new Criteria(bomLogEntry, FLEXTYPE_ID_PATH, bomLogType.getIdPath(),Criteria.EQUALS));

			result=LCSQuery.runDirectQuery(stmt);
			
			List<FlexObject> data=result.getResults();
			int reqId;
			
			//generate request ID.
			if(!data.isEmpty()){
				reqId=data.get(data.size()-1).getInt(MAX_CAST_LCSLOGENTRY+"."+bomAttr.getColumnName()+AS_INT);
				if(reqId==0){
					bomPartRequestID=bomPartRequestID+1;
				}else{
					bomPartRequestID =reqId+1;
				}
				return bomPartRequestID;
			}
		}catch(WTException excpt){
			LOGGER.error(excpt.getLocalizedMessage(), excpt);
		}
		return bomPartRequestID+1;
	}




	/**
	 * persist FlexBOMPart.
	 * @param bomPart
	 */
	public void persistColorwaySeasonLinkObject(FlexBOMPart bomPart){
		try{
			LOGGER.info("persisting bomPart object .......");
			LCSFlexBOMLogic flexBomLogic = new LCSFlexBOMLogic();
			flexBomLogic.saveBOMPart(bomPart, true);
		}catch(WTException wExp){
			LOGGER.error(wExp.getLocalizedMessage(), wExp);
		}
	}
	

}

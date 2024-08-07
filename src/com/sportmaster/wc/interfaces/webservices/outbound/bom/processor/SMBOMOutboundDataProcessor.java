/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.bom.processor;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.bombean.BOMPart;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundIntegrationBean;

import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author ITC_Infotech.
 *
 */
public class SMBOMOutboundDataProcessor {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = LogR.getLogger(SMBOMOutboundDataProcessor.class.getName());
	/**
	 * protected constructor.
	 */
	public SMBOMOutboundDataProcessor(){
		//public constructor.
	}

	/**
	 * Process BOM module.
	 * @param bomPart - FlexBOMPart.
	 * @param bomInfoRequest - BOMPart.
	 */
	public BOMPart setDataForBOMOutboundRequest(FlexBOMPart bomPart, BOMPart bomInfoRequest,
			SMBOMOutboundIntegrationBean bean) {
		try{

			//set data on product bean.
			BOMPart bomDataBean = new SMBOMBeanDataProcessor().setDataForBOMPartBean(bomPart, bomInfoRequest, bean);
			if(null != bomDataBean ){
				return bomDataBean;
			}else{
				LOGGER.info("BOM Bean Object is NULL ****");
				return null;
			}

		}catch(WTException e){
			LOGGER.error("WTException details..");
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		} catch (javax.xml.datatype.DatatypeConfigurationException de) {
			LOGGER.error("DatatypeConfigurationException details..");
			LOGGER.error(de.getLocalizedMessage(), de);
			return null;
		} catch (IOException ioex) {
			LOGGER.error("DatatypeConfigurationException details..");
			LOGGER.error(ioex.getLocalizedMessage(), ioex);
			return null;
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		} catch (ServiceException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		}
		
	}

	
	/**
	 * Returns creator email/name.
	 * @param bomPart - FlexBOMPart
	 * @return String
	 */
	public String getBOMCreator(FlexBOMPart bomPart){
		if(FormatHelper.hasContent(bomPart.getCreatorEMail())){
			return bomPart.getCreatorEMail();
		}
		else{
			return bomPart.getCreatorFullName();
		}
	}
	
	

	/**
	 * Returns modifier email/name.
	 * @param bomPart - FlexBOMPart
	 * @return String
	 */
	public String getBOMModifier(FlexBOMPart bomPart){
		if(FormatHelper.hasContent(bomPart.getModifierEMail())){
			return bomPart.getModifierEMail();
		}
		else{
			return bomPart.getModifierFullName();
		}
	}
	




}

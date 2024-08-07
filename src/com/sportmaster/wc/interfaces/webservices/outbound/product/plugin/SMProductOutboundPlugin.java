/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.plugin;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.sportmaster.wc.interfaces.webservices.outbound.product.processor.SMProductPluginProcessor;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author ITC_Infotech.
 *
 */
public class SMProductOutboundPlugin {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundPlugin.class);
	
	/**
	 * protected constructor.
	 */
	protected SMProductOutboundPlugin(){
		//constructor.
	}

	public static void processProductOutboundData(WTObject obj){
		LOGGER.debug(" ###############   PLUGIN TRIGGERRED FOR  PRODUCT  OUTBOUND  INTEGRATION  STARTS###############");
		try{
			SMProductPluginProcessor pluginProcessor =  new SMProductPluginProcessor();
			//validate object type. 
			if(obj instanceof LCSProduct){

				LOGGER.debug("PRODUCT is CREATED/UPDATED");
				pluginProcessor.triggerMDMRequestForProduct(obj);

			}else if(obj instanceof LCSSKU){

				LOGGER.debug("COLORWAY is CREATED/UPDATED");
				pluginProcessor.triggerMDMRequestForColorway(obj);

			}else if(obj instanceof LCSProductSeasonLink){

				LOGGER.debug("PRODUCT SEASON LINK is CREATED/UPDATED");
				pluginProcessor.triggerRequestForProductSeasonLink(obj);

			}else if(obj instanceof LCSSKUSeasonLink){

				LOGGER.debug("COLORWAY SEASON LINK is CREATED/UPDATED");
				pluginProcessor.triggerMDMRequestOnColorwaySeason(obj);
			}
		}catch(WTPropertyVetoException wtProp){
			LOGGER.error(wtProp.getLocalizedMessage(),wtProp);
		}catch(WTException wtEx){
			LOGGER.error(wtEx.getLocalizedMessage(),wtEx);
		} catch (SQLException e) {
			LOGGER.error(e.getLocalizedMessage(),e);
		}	
		LOGGER.debug(" ###############   PLUGIN TRIGGERRED FOR  PRODUCT  OUTBOUND  INTEGRATION  ENDS###############");
	}

	
}

/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.plugin;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.sportmaster.wc.interfaces.webservices.outbound.product.processor.SMLifeCylcePluginProcessor;

import wt.fc.WTObject;

/**
 * @author BSC
 *
 */
public class SMProductOutboundLifecyclePlugin {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundLifecyclePlugin.class);
	/**
	 * protected constructor.
	 */
	protected SMProductOutboundLifecyclePlugin(){
		//constructor.
	}
	/**
	 * Trigger plugin on lifecycle state change.
	 * @param obj - WTObject
	 * @throws SQLException 
	 */
	public static void lifecycleStatePlugin(WTObject obj) {
		
		LOGGER.debug("############################PRODUCT INTEGRATION LIFE CYCLE STATE CHNAGE PLUGIN STARTS############################");
		try {
			new SMLifeCylcePluginProcessor().triggerStaeChange(obj);
		} catch (SQLException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		LOGGER.debug("#############################PRODUCT INTEGRATION LIFE CYCLE STATE CHNAGE PLUGIN ENDS#############################");
	}
	
}

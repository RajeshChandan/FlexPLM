package com.burberry.wc.integration.streamline.sourcingconfig;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.burberry.wc.integration.streamline.util.BRStreamlineAPIHelper;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductHelper;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;
import com.lcs.wc.specification.FlexSpecification;

public class BRUpdateSourcingConfig {
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(BRCreateSourcingCofig.class);

	/**
	 * 
	 * @param product
	 * @param lcsSeason
	 * @param scJson
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap updateSource(LCSProduct product, final LCSSeason lcsSeason, final JSONObject scJson) {
		product.getMaster();
		product.getSeasonMaster();
		BRCreateSourcingCofig CreateSourcingConfig = new BRCreateSourcingCofig();
		LCSSourcingConfigLogic srcConfigLogic = new LCSSourcingConfigLogic();
		LCSSourcingConfig srcConfig = null;
		FlexSpecification flexSpec = null;
		BRCreateSourcingCofig createSource;
		String error = null;
		final HashMap returnMap = new HashMap();
		
		try {
			try {
				srcConfig = BRStreamlineAPIHelper.getLatestSrcConfig(product, lcsSeason);
			}catch (Exception e) {
				LOGGER.debug("BRCreateSourcingCofig ::	Error: " + e.getLocalizedMessage());
			}
			
			if (srcConfig != null && srcConfig.isPrimarySource()) {
				LOGGER.debug(" BRUpdateSourcingConfig 	:: Sourcing Config is : " + scJson);

				JSONArray sckeys = scJson.names();
				/* boolean checkout = false;
				  if (!VersionHelper.isCheckedOut(srcConfig) && sckeys != null) {
					srcConfig = (LCSSourcingConfig) wt.vc.wip.WorkInProgressHelper.service
							.checkout(srcConfig, wt.vc.wip.WorkInProgressHelper.service.getCheckoutFolder(), "")
							.getWorkingCopy();
					checkout = true;
					LOGGER.debug("Is src config chekced out 444444   ??   ^^^^^^^^^^^^^^^^^^^^^^^^^^  "
							+ VersionHelper.isCheckedOut(srcConfig));
					LOGGER.debug("CHECK OUT 44444 (inside if) ***********************    " + checkout);
				}*/
				for (int i = 0; i < sckeys.length(); ++i) {
					String key = sckeys.getString(i);
					LOGGER.debug(" BRUpdateSourcingConfig 	:: key : " + key + " : " + "value : " + scJson.opt(key));
					
					srcConfig = CreateSourcingConfig.setValuesOfSourcingConfig(key, scJson.optString(key), srcConfig);
					
				}

				srcConfigLogic.saveSourcingConfig(srcConfig, true);
				LOGGER.debug(" BRUpdateSourcingConfig 	:: Sourcing Config : " + srcConfig.getName());

				//product = LCSProductHelper.service.saveProduct(product);
				LCSProductLogic prodLogic = new LCSProductLogic();
				product=(LCSProduct) prodLogic.save(product);
			}else {
				 {
						error = "Source is not found in FlexPLM, because product is not added to a season";
					}
			}
			
			
			if (srcConfig!=null && srcConfig.isPrimarySource()) {
				LOGGER.debug(" BRUpdateSourcingConfig 	:: Creating Specification after saving Source");
				flexSpec = BRStreamlineAPIHelper.getSpecification(product, lcsSeason, srcConfig);
				if(flexSpec==null) {
					createSource = new BRCreateSourcingCofig();
					createSource.CreateSpecification(product, lcsSeason, srcConfig, null);
				}
				
			}

		} catch (Exception e) {
			//e.printStackTrace();
			LOGGER.debug(" BRUpdateSourcingConfig	:: Error: " + e.getLocalizedMessage());
		}
		
		LOGGER.debug("BRUpdateSourcingConfig ::   response is : "+product);
		returnMap.put("product", product);
		if(error!=null)
			returnMap.put("error",error);
		return returnMap;
		
		
	}

	
	
}

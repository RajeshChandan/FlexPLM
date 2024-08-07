/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.processor;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.client.SMCareLabelDataClient;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper.SMCareLabelHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper.SMCareLabelIntegrationBean;
import com.sportmaster.wc.reports.SMCareLabelReportBean;
import com.sportmaster.wc.reports.SMCareLabelReportQuery;


/**
 * SMCareLabelDataProcessor.
 * 
 * @author 'true' ITC.
 * @version 'true' 1.0 version number
 * @since Feb 23, 2018
 */
public class SMCareLabelDataProcessor {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCareLabelDataProcessor.class);


	/**
	 * protected constructor.
	 */
	protected SMCareLabelDataProcessor(){
		//constructor.
	}

	
	/**
	 * This method start processing the data from the processing queue.
	 * @param inputSelectedMap
	 * @throws WTException
	 * @throws SQLException
	 */
	public static void processCareLabelDataFromQueue(Map<String, Object> inputSelectedMap) throws WTException, SQLException {


		LOGGER.debug(" ############### START :processCareLabelDataFromQueue  INTEGRATION  ###############");

		//LOGGER.debug("inputSelectedMap >>> : " +inputSelectedMap);
		com.lcs.wc.client.ClientContext context = null;

		SMCareLabelReportBean reportBean = SMCareLabelReportQuery.runReportQuery(context, inputSelectedMap);

		LOGGER.debug("Total number of records for the care label integration  >>> :" +reportBean.getReportData().size());

		Map databyProduct = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(reportBean.getReportData(), "LCSPRODUCT.IDA3MASTERREFERENCE");

		LOGGER.debug("Total number of products" +databyProduct.size());
		
		
		//set Filters applied.
		SMCareLabelIntegrationBean integrationBean = new SMCareLabelIntegrationBean();
		SMCareLabelHelper.setSelectedCriteria(inputSelectedMap, integrationBean);
		
		LOGGER.debug("Selected season name  " +integrationBean.getSelectedSeasonName());
		LOGGER.debug("Selected Product name  " +integrationBean.getSelectedProductName());
		LOGGER.debug("Selected Brands name " +integrationBean.getSelectedBrands());
		LOGGER.debug("Selected age name " +integrationBean.getSelectedAges());
		LOGGER.debug("Selected Gender name " +integrationBean.getSelectedGenders());
		LOGGER.debug("Selected Tecnologist name " +integrationBean.getSelectedProducctTechnologist());
		LOGGER.debug("Selected ProductionGroup name " +integrationBean.getSelectedProductionGroup());


		//Iterating bean to set the data to the careLabel Integration.
		Iterator<?> carLadataItr = databyProduct.entrySet().iterator();
		while(carLadataItr.hasNext()){
			Map.Entry me1=(Map.Entry) carLadataItr.next();	

			List careLabelDatabyProd=(ArrayList) me1.getValue();
			LOGGER.debug("Size of the records for each Product >>>>> : " +careLabelDatabyProd.size());

			//Calling client to invoke for each product
			SMCareLabelDataClient smCareLabDataClient = new SMCareLabelDataClient();
			smCareLabDataClient.careLabelRequest(careLabelDatabyProd, integrationBean);


		}

		LOGGER.debug(" ############### END : processCareLabelDataFromQueue  INTEGRATION  ###############");
	}


}

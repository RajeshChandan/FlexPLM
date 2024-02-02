package com.hbi.etl.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.hbi.etl.dao.HbiEtlTracker;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.NewHibernateUtil;
import com.lcs.wc.util.LCSProperties;

public class HbiExtractorUtil {
	static String logFile = LCSProperties.get("com.hbi.stg.extractors.STGLawsonVendorAgreement.logFile", "C:\\lawsondata\\ETL.log");
	static Logger log = PLMETLLogger.getLogger(HbiExtractorUtil.class, logFile, "INFO");

	private HbiEtlTracker getLawsonETLTracker() { 
		
	 
		Session session = null;
		HbiEtlTracker etlTracker = null;
        try {
			 session = NewHibernateUtil.getSessionFactory().openSession();
			 log.debug("Inside findExtractStartTime");
			 Query query = session.createQuery("FROM HbiEtlTracker WHERE STATUS = 'SUCCESS' AND LAWSONEXPORT != 'FAIL' AND RUNMODE = 'DE' AND ROWNUM = 1 ORDER BY LOADERCREATETIME " );
			 List<HbiEtlTracker> etlTrackerList = query.list();     
			 for(HbiEtlTracker tracker : etlTrackerList){
				 etlTracker =  tracker;					
			 }			 

		
		} catch (HibernateException e) {
			e.printStackTrace();
		}finally{
			if(session != null){
				session.close();
			}
		}
		return etlTracker;

	}


}

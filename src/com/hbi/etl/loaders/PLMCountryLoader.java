/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.loaders;

import com.hbi.etl.dao.HbiCountry;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.NewHibernateUtil;
import com.lcs.wc.util.LCSProperties;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author UST
 */
public class PLMCountryLoader implements PLMETLLoader {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMCountryLoader.class, logLevel);
    Session session = NewHibernateUtil.getSessionFactory().openSession();
    Transaction txn = session.beginTransaction();
    Date date = new Date();
    java.sql.Timestamp dbtimestamp = new java.sql.Timestamp(date.getTime());

    @Override
    public void load(Vector objList) {

        etlLogger.info("Start HbiCountry Loader");

        int i;
        for (i = 0; i < objList.size(); i++) {
            
            HbiCountry transObj = (HbiCountry) objList.elementAt(i);

            Query query = session.createQuery("from HbiCountry where branchiditerationinfo = :branchiditerationinfo");
            query.setParameter("branchiditerationinfo", transObj.getBranchiditerationinfo());
            if (query.uniqueResult() == null) {
                transObj.setLoadercreatetime(dbtimestamp);
                transObj.setLoaderupdatetime(dbtimestamp);
                session.save(transObj);
                
                if (i % 20 == 0) {
                    //flush a batch of inserts and release memory:
                    session.flush();
                    session.clear();
                }

            } else {
                HbiCountry updObj = (HbiCountry) query.uniqueResult();
                transObj.setLoadercreatetime(updObj.getLoadercreatetime());
                transObj.setPrimarykey(updObj.getPrimarykey());
                System.out.println("Data exists GO update");
                session.evict(updObj);
                update(transObj);
                if (i % 20 == 0) {
                    //flush a batch of updates and release memory:
                    session.flush();
                    session.clear();
                }
            }

        }
        txn.commit();
        if(txn.wasCommitted()){
            etlLogger.info("HbiCountry Loaded Successfully!");
        } else {
            txn.rollback();
            etlLogger.info("HbiCountry Load Failed!");
        }         
        session.close();
    }

    public void update(HbiCountry obj) {


        obj.setLoaderupdatetime(dbtimestamp);
        session.update(obj);

    }

}

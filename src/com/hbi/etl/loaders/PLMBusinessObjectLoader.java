/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.loaders;

import com.hbi.etl.dao.HbiBusinessObject;
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
public class PLMBusinessObjectLoader implements PLMETLLoader {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMBusinessObjectLoader.class, logLevel);
    Session session = NewHibernateUtil.getSessionFactory().openSession();
    Transaction txn = session.beginTransaction();
    Date date = new Date();
    java.sql.Timestamp dbtimestamp = new java.sql.Timestamp(date.getTime());

    @Override
    public void load(Vector objList) {

        etlLogger.info("Start HbiBusinessObject Loader");
        int i;
        etlLogger.debug(objList);
        for (i = 0; i < objList.size(); i++) {      
            HbiBusinessObject transObj = (HbiBusinessObject) objList.elementAt(i);            
            Query query = session.createQuery("from HbiBusinessObject where ida2a2 = :ida2a2 ");
            query.setParameter("ida2a2", transObj.getIda2a2());
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
                HbiBusinessObject updObj = (HbiBusinessObject) query.uniqueResult();
                transObj.setLoadercreatetime(updObj.getLoadercreatetime());
                transObj.setPrimarykey(updObj.getPrimarykey());
                etlLogger.debug("Data exists GO update");
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
            etlLogger.info("HbiBusinessObject Loaded Successfully!");
        } else {
            txn.rollback();
            etlLogger.info("HbiBusinessObject Load Failed!");
        }
        session.close();
    }

    public void update(HbiBusinessObject obj) {

        obj.setLoaderupdatetime(dbtimestamp);
        session.update(obj);

    }

}

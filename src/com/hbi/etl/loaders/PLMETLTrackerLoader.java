/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.loaders;

import com.hbi.etl.dao.HbiEtlTracker;
import com.hbi.etl.util.NewHibernateUtil;
import java.sql.Timestamp;
import java.util.Date;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author UST
 */
public class PLMETLTrackerLoader {

    public HbiEtlTracker loadTracker(String runMode, Date startDate) {

        Session session = NewHibernateUtil.getSessionFactory().openSession();
        Transaction txn = session.beginTransaction();
        Timestamp startDateTime = new java.sql.Timestamp(startDate.getTime());
        HbiEtlTracker etlTracker = new HbiEtlTracker();
        etlTracker.setRunmode(runMode);
        etlTracker.setLoadercreatetime(startDateTime);
        etlTracker.setDatasourcesystem("PLM");
        session.save(etlTracker);
        txn.commit();
        session.close();

        return etlTracker;

    }

    public void updateTracker(HbiEtlTracker obj, Date endDate, String status) {
        
        Session session = NewHibernateUtil.getSessionFactory().openSession();
        Transaction txn = session.beginTransaction();
        Query query = session.createQuery("from HbiEtlTracker where Primarykey = :Primarykey ");
        query.setParameter("Primarykey", obj.getPrimarykey());
        if (query.uniqueResult() != null) {

            Timestamp endDateTime = new java.sql.Timestamp(endDate.getTime());
            HbiEtlTracker updObj = (HbiEtlTracker) query.uniqueResult();
            System.out.println("Data exists GO update");
            updObj.setLoaderupdatetime(endDateTime);
            updObj.setStatus(status);
            session.update(updObj);
            txn.commit();
            session.close();
        }

    }

}

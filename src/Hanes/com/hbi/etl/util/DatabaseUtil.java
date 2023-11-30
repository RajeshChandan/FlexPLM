/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.util;

import com.hbi.etl.logger.PLMETLLogger;
import com.lcs.wc.util.LCSProperties;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

/**
 *
 * @author UST
 */
public class DatabaseUtil {
    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger logger = PLMETLLogger.getLogger(DatabaseUtil.class, null, logLevel);
    public void truncateTable(String tableName) throws PLMETLException{
        
        Session session = NewHibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try{
            String hql = "TRUNCATE TABLE " + tableName;
            SQLQuery query = session.createSQLQuery(hql);
            query.executeUpdate();
            logger.info("Truncate Query Executed");
            session.close();
        }catch (Exception ex){
            throw new PLMETLException(ex);
        }
    }
  
  
}

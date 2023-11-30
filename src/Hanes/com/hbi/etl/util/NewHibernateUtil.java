/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.util;

import java.util.Properties;
import java.io.FileInputStream;
import com.hbi.etl.logger.PLMETLLogger;
import com.lcs.wc.util.LCSProperties;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author UST
 */
public class NewHibernateUtil {

    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    static Logger etlLogger = PLMETLLogger.createInstance(NewHibernateUtil.class, "ERROR");

    static {
        try {
        	//System.out.println("**************************Inside NewHibernateUtil******************");
            Configuration configuration = new Configuration().configure("hbi_hibernate.cfg.xml");

            serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (HibernateException he) {
            etlLogger.error("Error creating Session: " + he.getLocalizedMessage());
            //throw new ExceptionInInitializerError(he);
        } catch (Exception e) {

        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

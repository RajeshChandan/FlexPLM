/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl;

import com.hbi.etl.util.PLMETLException;
import com.lcs.wc.util.*;
import wt.fc.PersistenceHelper;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.queue.ProcessingQueue;
import wt.queue.QueueEntry;
import wt.queue.QueueHelper;
import wt.session.SessionHelper;
import wt.util.*;

/**
 *
 * @author UST
 */
public class PLMETLAddQueueEntry implements RemoteAccess {

    public static void addFullRefreshToQueue() throws WTException {

        Class classList[] = {};
        Object objectList[] = {};
        String integrationClass = LCSProperties.get("com.hbi.etl.IntegrationClass");
        String integrationMethod = LCSProperties.get("com.hbi.etl.IntegrationMethodFull");
        String queueName = LCSProperties.get("com.hbi.etl.QueueName");

        ProcessingQueue queue = QueueHelper.manager.getQueue(queueName);

        if (queue == null) {
            System.out.println("Creating Processing Queue '" + queueName + "' !!!");
            //queue = QueueHelper.manager.createQueue(queueName);
            queue = ProcessingQueue.newProcessingQueue(queueName);
                                               // queue.setExecutionHost(BACKGROUND_METHODSERVER_QUEUEGROUP);
            //  System.out.println( "BACKGROUND_METHODSERVER_QUEUEGROUP=" + BACKGROUND_METHODSERVER_QUEUEGROUP);
            queue = (ProcessingQueue) PersistenceHelper.manager.save(queue);
        }
        queue.addEntry(SessionHelper.manager.getAdministrator(), integrationMethod, integrationClass, classList, objectList);

    }

    public static void addIncrToQueue() throws WTException {

        Class classList[] = {};
        Object objectList[] = {};
        String integrationClass = LCSProperties.get("com.hbi.etl.IntegrationClass");
        String integrationMethod = LCSProperties.get("com.hbi.etl.IntegrationMethodIncr");
        String queueName = LCSProperties.get("com.hbi.etl.QueueName");

        ProcessingQueue queue = QueueHelper.manager.getQueue(queueName);

        if (queue == null) {
            System.out.println("Creating Processing Queue '" + queueName + "' !!!");
            //queue = QueueHelper.manager.createQueue(queueName);
            queue = ProcessingQueue.newProcessingQueue(queueName);
                                              //  queue.setExecutionHost(BACKGROUND_METHODSERVER_QUEUEGROUP);
            //  System.out.println( "BACKGROUND_METHODSERVER_QUEUEGROUP=" + BACKGROUND_METHODSERVER_QUEUEGROUP);
            queue = (ProcessingQueue) PersistenceHelper.manager.save(queue);
        }
        queue.addEntry(SessionHelper.manager.getAdministrator(), integrationMethod, integrationClass, classList, objectList);

    }
}

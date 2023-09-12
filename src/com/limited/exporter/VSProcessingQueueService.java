package com.limited.exporter;

import org.apache.log4j.Logger;
import wt.org.WTPrincipal;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.io.Serializable;

public class VSProcessingQueueService implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * the LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(VSProcessingQueueService.class);
    protected VSProcessingQueueService() {

    }

    /**
     * Creates the queue.
     *
     * @param strQueueName the str queue name
     * @return the processing queue
     * @throws WTException the wT exception
     */
    private static ProcessingQueue createQueue(final String strQueueName) throws WTException {
        LOGGER.debug(" exectuing on WINCHILL METHOD SERVER");
        ProcessingQueue pQueue = null;
        try {
            pQueue = QueueHelper.manager.createQueue(strQueueName, true);
        } catch (final WTException e) {
            e.printStackTrace();
        }
        LOGGER.debug(" exectuing on WINCHILL METHOD SERVER DONE");
        return pQueue;
    }

    // Method to add Entries into the processing queue

    /**
     * Adds the queue entry.
     *
     * @param strQueueName the str queue name
     * @param argTypesx    the arg typesx
     * @param argValuesx   the arg valuesx
     * @throws WTException the wT exception
     */
    public void addQueueEntry(final String strQueueName, Class<?>[] argTypesx, Object[] argValuesx, String methodName,
                              String className) throws WTException {
        LOGGER.debug("executing addQueueEntry on WINCHILL METHOD SERVER");
        ProcessingQueue queue = null;
        queue = QueueHelper.manager.getQueue(strQueueName);
        LOGGER.debug("executing addQueueEntry on WINCHILL METHOD SERVER  " + queue);
        // checking if queue is not null
        if (null != queue) {
            // Checking if queue is started
            if ("STARTED".equals(queue.getQueueState())) {
                final String processQ = methodName;
                SessionServerHelper.manager.setAccessEnforced(false);
                final WTPrincipal wtprincipal = SessionHelper.manager.getAdministrator();
                wt.session.SessionContext.setEffectivePrincipal(wtprincipal);
                LOGGER.debug("****Processed till addEntry*****");
                // Calling the processing Method
                queue.addEntry(wtprincipal, processQ, className, argTypesx, argValuesx);
                LOGGER.debug("****Processed after addEntry********");
                SessionServerHelper.manager.setAccessEnforced(true);
            }
            // If queue is not started start the queue
            else { // starting queue
                QueueHelper.manager.startQueue(queue);
                // Calling the add queue Entry
                addQueueEntry(strQueueName, argTypesx, argValuesx, methodName, className);
            }
        } else {
            createQueue(strQueueName);
            // Calling the add queue Entry
            addQueueEntry(strQueueName, argTypesx, argValuesx, methodName, className);
        }
        LOGGER.debug(" executing addQueueEntry on WINCHILL METHOD SERVER done");
    }

}

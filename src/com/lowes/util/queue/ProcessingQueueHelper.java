package com.lowes.util.queue;

import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;
import wt.org.WTPrincipal;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.util.Objects;

public class ProcessingQueueHelper {

    private static final Logger LOGGER = LogR.getLogger(ProcessingQueueHelper.class.getName());


    /**
     * Creates the queue.
     *
     * @param strQueueName the str queue name
     */
    private ProcessingQueue createQueue(final String strQueueName) {
        LOGGER.debug(" executing on WINDCHILL METHOD SERVER");
        ProcessingQueue pQueue = null;
        try {
            pQueue = QueueHelper.manager.createQueue(strQueueName, true);
        } catch (WTException e) {
            LOGGER.error(e);
        }
        LOGGER.debug(" executing on WINDCHILL METHOD SERVER DONE");
        return pQueue;
    }


    // Method to add Entries into the processing queue

    /**
     * Adds the queue entry.
     *
     * @param strQueueName the str queue name
     * @param argsTypes    the arg Types
     * @param argsValues   the arg Values
     * @throws WTException the wT exception
     */
    public void addQueueEntry(final String strQueueName,
                              Class<?>[] argsTypes, Object[] argsValues, String methodName, String className) throws WTException {
        LOGGER.debug("executing addQueueEntry on WINDCHILL METHOD SERVER");

        SessionServerHelper.manager.setAccessEnforced(false);
        final WTPrincipal wtprincipal = SessionHelper.manager.getAdministrator();
        wt.session.SessionContext.setEffectivePrincipal(wtprincipal);

        ProcessingQueue queue;
        queue = QueueHelper.manager.getQueue(strQueueName);

        if (Objects.isNull(queue)) {
            queue = createQueue(strQueueName);
        }
        LOGGER.debug("executing addQueueEntry on WINDCHILL METHOD SERVER {} ", queue);

        // checking if queue is not null
        if (Objects.nonNull(queue)) {

            //start queue if not started
            if (!("STARTED".equals(queue.getQueueState()))) {
                QueueHelper.manager.startQueue(queue);
            }

            // Checking if queue is started
            if ("STARTED".equals(queue.getQueueState())) {

                LOGGER.debug("****Processed till addEntry*****");
                // Calling the processing Method
                queue.addEntry(wtprincipal, methodName, className, argsTypes, argsValues);
                LOGGER.debug("****Processed after addEntry********");
            }
        }

        SessionServerHelper.manager.setAccessEnforced(true);
        LOGGER.debug(" executing addQueueEntry on WINDCHILL METHOD SERVER done");
    }
}

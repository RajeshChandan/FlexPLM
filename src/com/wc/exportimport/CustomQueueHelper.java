package com.custom.wc.exportimport;

import com.lcs.wc.load.LoadCommon;
import com.lcs.wc.util.LCSProperties;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import wt.queue.*;
import wt.session.SessionHelper;
import wt.util.*;

// To Execute
//windchill com.custom.wc.exportUtilities.CustomQueueHelper DataIntegrationQueue

public class CustomQueueHelper {

    protected static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(LCSProperties.get("com.custom.exportimport.timeFormat", "MMM dd yyyy hh:mm:ss"));
    protected static int DEFAULT_QUEUE_INTERVAL = LCSProperties.get("com.custom.exportimport.timeInterval", 30000);
    protected static boolean DEBUG = LCSProperties.getBoolean("com.custom.wc.exportimport.debugFlag");

    static {
        DATE_FORMAT.setTimeZone(WTContext.getContext().getTimeZone());
    }

    public static boolean initCustomQueue(Hashtable dataValues, Hashtable commandLine, Vector returnObjects) {
        return true;
    }

    public static boolean createCustomQueue(Hashtable dataValues, Hashtable commandLine, Vector returnObjects) throws WTException {
        String queueName = LoadCommon.getValue(dataValues, "QueueName", true);
        if (queueName == null) {
            return false;
        }

        String entryClass = LoadCommon.getValue(dataValues, "EntryClass", true);
        if (entryClass == null) {
            return false;
        }

        String entryMethod = LoadCommon.getValue(dataValues, "EntryMethod", true);
        if (entryMethod == null) {
            return false;
        }

        String entryTime = LoadCommon.getValue(dataValues, "EntryTime");
        String entryInterval = LoadCommon.getValue(dataValues, "EntryInterval");


        try {
            createCustomQueue(queueName, entryClass, entryMethod, entryTime);
            return true;
        } catch (WTException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static boolean deleteCustomQueue(Hashtable dataValues, Hashtable commandLine, Vector returnObjects) throws WTException {
        String queueName = LoadCommon.getValue(dataValues, "QueueName", true);
        if (queueName == null) {
            return false;
        }

        deleteCustomQueue(queueName);
        return true;
    }

    public static void createCustomQueue(String queueName, String entryClass, String entryMethod, String entryTime) throws WTException {
        ScheduleQueue scheduleQueue = getScheduleQueue(queueName);
        if (scheduleQueue == null) {
            display("Creating Custom Queue '" + queueName + "' ...");
            scheduleQueue = QueueHelper.manager.createScheduleQueue(queueName);
            display("Created  Custom Queue '" + queueName + "' !!!");
        } else {
            display("Custom Queue '" + queueName + "' Already Created !!!");
        }

        if (getScheduleQueueEntry(scheduleQueue, entryClass, entryMethod) != null) {
            display("Custom Queue Entry Already Created !!!");
            return;
        }

        Timestamp timestamp = getTimestamp(entryTime);

        Class classList[] = {};
        Object objectList[] = {};

        display("Creating Custom Queue Entry '" + entryClass + ":" + entryMethod + "' To Start At '" + entryTime + "' ...");
        ScheduleQueueEntry scheduleEntry = scheduleQueue.addEntry(SessionHelper.manager.getAdministrator(), entryMethod, entryClass, classList, objectList, timestamp);
        display("Created  Custom Queue Entry '" + entryClass + ":" + entryMethod + "' To Start At '" + entryTime + "' !!!");
    }

    public static StatusInfo executeInterface() throws WTException {
        display("----------------------------------------------------------------");
        display("Interface Started   At " + DATE_FORMAT.format(new Date()));
        display("Interface Completed At " + DATE_FORMAT.format(new Date()));
        display("----------------------------------------------------------------");
        return setStatus();
    }

    public static void deleteCustomQueue(String queueName) throws WTException {
        ScheduleQueue queue = getScheduleQueue(queueName);
        if (queue == null) {
            throw new WTException("Queue '" + queueName + "' Does Not Exist !!!");
        }
        QueueHelper.manager.deleteQueue(queue);
    }

    public static ScheduleQueue getScheduleQueue(String queueName) throws WTException {
        return (ScheduleQueue) QueueHelper.manager.getQueue(queueName, ScheduleQueue.class);
    }

    public static ProcessingQueue getProcessQueue(String queueName) throws WTException {
        return (ProcessingQueue) QueueHelper.manager.getQueue(queueName, ProcessingQueue.class);
    }

    public static ScheduleQueueEntry getScheduleQueueEntry(ScheduleQueue scheduleQueue, String className, String methodName) throws WTException {
        ScheduleQueueEntry queueEntry = null;

        Enumeration enumeration = QueueHelper.manager.queueEntries(scheduleQueue);
        while (enumeration.hasMoreElements()) {
            queueEntry = (ScheduleQueueEntry) enumeration.nextElement();
            if (queueEntry.getTargetClass().equals(className) && queueEntry.getTargetMethod().equals(methodName)) {
                return queueEntry;
            }
        }

        return null;
    }

    public static StatusInfo setStatus() {
        StatusInfo statusinfo = null;
        try {
            statusinfo = StatusInfo.newStatusInfo("RESCHEDULE");
            statusinfo.setRescheduleTime(new Timestamp(System.currentTimeMillis() + CustomQueueHelper.DEFAULT_QUEUE_INTERVAL));
        } catch (WTPropertyVetoException ex) {
            ex.printStackTrace();
        } catch (WTException ex) {
            ex.printStackTrace();
        }

        return statusinfo;
    }

    public static Timestamp getTimestamp(String timeString) {
        try {
            if (timeString != null) {
                return new Timestamp(DATE_FORMAT.parse(timeString).getTime());
            }
        } catch (Exception ex) {
            display(ex.getLocalizedMessage());
        }
        return new Timestamp((new Date()).getTime());
    }

    public static void display(Object message) {
        //if(DEBUG)
        System.out.println(message);
    }

    public static void deleteEntriesInProcessingQueue(ProcessingQueue queue) throws WTException {
       // Use this to delete invalid entries in a Queue
        
        QueueHelper.manager.stopQueue(queue);
        QueueHelper.manager.deleteEntries(queue);
        QueueHelper.manager.startQueue(queue);
    }

    public static void main(String argv[]) throws WTException {
        LoadCommon.SERVER_MODE = false;

        ProcessingQueue queue = getProcessQueue(argv[0]);
        deleteEntriesInProcessingQueue (queue);

        // deleteCustomQueue(argv[0]);
        //createCustomQueue(argv[0],argv[1],argv[2],new Timestamp(System.currentTimeMillis()).toString());


        //deleteInvalidEntries();
        //System.out.println(getTimestamp("Mar 21 2001 12:00:00"));
        //String ts = null;
        //if(argv.length > 0)
        //	ts = argv[0];
        //System.out.println(getTimestamp(ts));
        //System.out.println();

        System.out.println("");
        //executeInterface();
        LoadCommon.SERVER_MODE = true;
    }
}
package com.limited.exporter;

import com.lcs.wc.util.FormatHelper;
import com.ptc.core.meta.common.RemoteWorker;
import com.ptc.core.meta.common.RemoteWorkerHandler;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.method.RemoteMethodServer;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class VSExporter {

    private static final Logger logger = LogR.getLogger(VSExporter.class.getName());

    private static final String USAGE_MSG = "Usage:\n\t Extract data details"
            + "\n\t\t windchill com.limited.exporter.VSExporter [-u <userName> -p <password>]"
            + "\n\t\t example: windchill com.limited.exporter.VSExporter -u wcadmin -p pr0dt3(h";

    public static void main(String[] args) {

        String userName = "";
        String password = "";
        boolean stopExecution = true;

        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-u":
                    if (i + 1 < args.length) {
                        userName = args[++i];
                    }
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        password = args[++i];
                    }
                    break;
                default:
                    break;
            }
        }
        if (FormatHelper.hasContent(userName)) {
            RemoteMethodServer.getDefault().setUserName(userName);
            if (FormatHelper.hasContent(password)) {
                RemoteMethodServer.getDefault().setPassword(password);
                stopExecution = false;
            }
        }

        if (!stopExecution) {
            try {
                SessionHelper.manager.getPrincipal();
                RemoteWorkerHandler.handleRemoteWorker(new VSExporterRemoteWorker(), args);
            } catch (Exception e) {
                logger.error("", e);
            }
        } else {
            System.out.println(USAGE_MSG);
        }

    }

    void doTheJob() {


        try (Scanner sc = new Scanner(new File(WTProperties.getServerProperties().getProperty("wt.codebase.location")
                + "\\" + "customDataExport.txt"));) {

            while (sc.hasNextLine()) {
                Object[] arrayOfObject = {sc.nextLine()};
                Class<?>[] ARG_TYPES = {String.class};

                new VSProcessingQueueService().addQueueEntry("VSDataExportQueue", ARG_TYPES, arrayOfObject,
                        "extractData", VSDataExtract.class.getName());
            }
        } catch (WTException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        }

    }
}

class VSExporterRemoteWorker extends RemoteWorker {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 9030244136773691640L;

    @Override
    public Object doWork(Object arg0) throws Exception {
        new VSExporter().doTheJob();
        return null;
    }
}
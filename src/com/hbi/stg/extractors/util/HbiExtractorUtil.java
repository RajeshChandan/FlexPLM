package com.hbi.stg.extractors.util;

import com.hbi.etl.dao.HbiColor;
import com.hbi.etl.dao.HbiEtlTracker;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.NewHibernateUtil;
import com.lcs.wc.client.ClientContext;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;

public class HbiExtractorUtil {

    static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel","DEBUG");
    static Logger log = PLMETLLogger.createInstance(HbiExtractorUtil.class, logLevel, true);
    public static String delimiter = LCSProperties.get("com.hbi.etl.po25delimiter", "|");
    public static String RECIPIENT_EMAIL = LCSProperties.get("com.hbi.stg.extractors.lawsonexportEmailGroup", "HBI_UST_FLEXPLM_SUPPORT@hanes.com");
    public static String SMTP_HOST = "";
    private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "Administrator");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "Administrator");
    
    static {
        try {
            WTProperties wtproperties = WTProperties.getLocalProperties();
            SMTP_HOST = wtproperties.getProperty("wt.mail.mailhost");
            log.info("Mail Host = " + SMTP_HOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public HbiEtlTracker getLawsonETLTracker() {

        Session session = null;
        HbiEtlTracker etlTracker = null;
        try {
            session = NewHibernateUtil.getSessionFactory().openSession();
            log.debug("Inside findExtractStartTime");

            Query query = session.createQuery("FROM HbiEtlTracker as A where to_char(A.loadercreatetime,'dd/mm/yyyy hh24:mi:ss' ) ="
                    + "(select to_char(min(B.loadercreatetime),'dd/mm/yyyy hh24:mi:ss' ) "
                    //+ " from HbiEtlTracker as B where  ( B.lawsonexport is null OR B.lawsonexport = 'FAIL')"
                    + " from HbiEtlTracker as B where  ( B.lawsonexport = 'FAIL' OR B.lawsonexport is null )"
                    + " and B.datasourcesystem = 'PLM' and  B.status = 'SUCCESS' and B.runmode='DE') "
                    + " and A.runmode='DE' and  A.datasourcesystem='PLM' AND ( A.lawsonexport = 'FAIL' OR A.lawsonexport is null ) ");

            List<HbiEtlTracker> etlTrackerList = query.list();
            log.info("ETL tracker records" + etlTrackerList.size());
            for (HbiEtlTracker tracker : etlTrackerList) {
                etlTracker = tracker;
                log.info("Found an ETLTracker record - NUM1 = " + etlTracker.getPtc_dbl_1());
               
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.flush();
                session.close();
            }
        }
        return etlTracker;

    }

    public HbiEtlTracker getDeltaETLTracker() {

        Session session = null;
        HbiEtlTracker etlTracker = null;
        try {
        	System.out.println("**************************Inside getDeltaETLTracker******************");
            session = NewHibernateUtil.getSessionFactory().openSession();
            log.debug("Inside findExtractStartTime");

            Query query = session.createQuery("FROM HbiEtlTracker as A where to_char(A.loaderupdatetime,'dd/mm/yyyy hh24:mi:ss' ) ="
                    +"(select to_char(max(B.loaderupdatetime),'dd/mm/yyyy hh24:mi:ss' ) FROM HbiEtlTracker as B where B.status='SUCCESS' "
                    +" and B.runmode='DE' and B.datasourcesystem='PLM')"
                    +" and A.runmode='DE' and A.datasourcesystem='PLM'");
            //System.out.println("query>>>>>>>>>>>>>>>>>>"+query.getQueryString());
            List<HbiEtlTracker> etlTrackerList = query.list();
            log.debug("ETL tracker records" + etlTrackerList.size());
            for (HbiEtlTracker tracker : etlTrackerList) {
                etlTracker = tracker;
                log.info("Found an ETLTracker record to run delta export");                
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.flush();
                session.close();
            }
        }
        return etlTracker;

    }

    public List<HbiEtlTracker> getLawsonETLTrackerFullRecords() {

        Session session = null;
        List<HbiEtlTracker> etlTrackerList = null;
        try {
            session = NewHibernateUtil.getSessionFactory().openSession();
            log.debug("Inside findExtractStartTime");

            Query query = session.createQuery(" FROM HbiEtlTracker as B where  ( B.lawsonexport is null )"
                    + " and B.datasourcesystem = 'PLM' ");

            etlTrackerList = query.list();
            log.info("ETL tracker records" + etlTrackerList.size());

        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.flush();
                session.close();
            }
        }
        return etlTrackerList;

    }

    public StringBuilder addPadding(StringBuilder builder, String regionUsed, int totalPositionsToCapture) {

        log.info("regionUsed " + regionUsed);
        log.info("totalPositionsToCapture " + totalPositionsToCapture);
        int positionsUsed = 0;
        if (regionUsed != null && regionUsed.length() > 0) {

            if (regionUsed.length() > totalPositionsToCapture) {

                regionUsed = regionUsed.substring(0, totalPositionsToCapture);
            }
            positionsUsed = regionUsed.length();
        }
        log.info("positionsUsed " + positionsUsed);
        builder.append(regionUsed); // Append or truncate the string to match position and then append
        //log.info(builder.toString());
        int positionsToPad = totalPositionsToCapture - positionsUsed;
        if (positionsToPad < 0) {
            return builder;
        }

        char[] pad = new char[positionsToPad];
        Arrays.fill(pad, ' ');
        builder.append(pad);

        return builder;
    }

    public StringBuilder addField(StringBuilder builder, String regionUsed) {

        if (regionUsed != null) {
            builder.append(regionUsed); // Append the filed and then append delimiter , if field is null , just append the delimiter
        }
        builder.append(delimiter);
        return builder;
    }

    public String getLineCount(int lineCount) {
        String str = String.valueOf(lineCount);
        StringBuilder sb = new StringBuilder();

        for (int i = 6 - str.length(); i > 0; i--) {
            sb.append('0');
        }

        sb.append(str);
        String result = sb.toString();
        return result;
    }

    public static void sendErrorFiles(String ERRFILE) throws Exception {

        RemoteMethodServer remoteMethodServer = RemoteMethodServer.getDefault();
        //GatewayAuthenticator auth = new GatewayAuthenticator();
        //auth.setRemoteUser("Administrator");
       // rms.setAuthenticator(auth);
        MethodContext mcontext = new MethodContext((String) null, (Object) null);
        SessionContext sessioncontext = SessionContext.newContext();

       // remoteMethodServer = RemoteMethodServer.getDefault();
        remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
        remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
        
        
        
        ClientContext lcsContext = ClientContext.getContext();
        String SENDER_NAME = lcsContext.getUserName();
        String SENDER_EMAIL = "";
        try {
            WTUser wtuserToName = lcsContext.getUser();
            if (wtuserToName != null && FormatHelper.hasContent(wtuserToName.getEMail())) {
                SENDER_EMAIL = wtuserToName.getEMail().toString();
            } else {
                SENDER_EMAIL = lcsContext.getUserName();
            }
        } catch (java.lang.NullPointerException wte) {
            SENDER_EMAIL = lcsContext.getUserName();
            wte.printStackTrace();
        }

        //String RECIPIENT_EMAIL = "anoop.sasikumar@hanes.com";
        String BCCRECIPIENT_EMAIL = "HBI_UST_FLEXPLM_SUPPORT@hanes.com";
        String EMAIL_SUBJECT = "Lawson Extract - Rejected records";
        String EMAIL_TEXT = "Attached files contain the error records which got rejected while extracting from Flex PLM for Lawson load";
        int SMTP_PORT = 25;

        // Create the message and transport objects:
        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", SMTP_HOST);
        javax.mail.Session sessionMail = javax.mail.Session.getDefaultInstance(prop, null);
        MimeMessage msg = new MimeMessage(sessionMail);

        msg.setFrom(new InternetAddress(SENDER_EMAIL, MimeUtility.encodeText(SENDER_NAME, "UTF-8", "B")));
        InternetAddress[] iAdressArray = InternetAddress.parse(RECIPIENT_EMAIL);
        msg.setRecipients(Message.RecipientType.TO, iAdressArray);
        //msg.setRecipient(Message.RecipientType.TO, new InternetAddress(RECIPIENT_EMAIL));
        msg.setSubject(EMAIL_SUBJECT);
        msg.setRecipient(Message.RecipientType.BCC, new InternetAddress(BCCRECIPIENT_EMAIL));
        // Send it! Catch the TransportException.
        try {

            // Attach file with message
            File file1 = new File(ERRFILE);

            if (file1.exists()) {
                // create and fill the first message part
                javax.mail.Part mbp1 = new MimeBodyPart();
                mbp1.setContent(EMAIL_TEXT, "text/html;charset=utf-8");

                // create the second message part
                MimeBodyPart mbp2 = new MimeBodyPart();

                // attach the file to the message
                FileDataSource fds = null;

                // create the Multipart and its parts to it
                Multipart mp = new MimeMultipart("mixed");
                mp.addBodyPart((MimeBodyPart) mbp1);
                if (file1.exists()) {
                    fds = new FileDataSource(ERRFILE);
                    mbp2 = new MimeBodyPart();
                    mbp2.setDataHandler(new DataHandler(fds));
                    mbp2.setFileName(fds.getName());
                    mp.addBodyPart(mbp2, mp.getCount());
                }
                // add the Multipart to the message
                msg.setContent(mp);

            } else {
                msg.setContent(EMAIL_TEXT, "text/html;charset=utf-8");
            }

            if (msg.getRecipients(Message.RecipientType.TO) != null || msg.getRecipients(Message.RecipientType.CC) != null) {
                Transport.send(msg);
            } else {
                log.info("Sending Failed : NO Email Addresses");
            }
        } catch (Exception te) {
            log.info("Transport Exception: " + te.getLocalizedMessage());
        }
    }

    public static void copyFile(String src, String dest) {

        File sourceFile = new File(src);
        File destFile = new File(dest);
        /* verify whether file exist in source location */
        if (!sourceFile.exists()) {
            //System.out.println("Source File Not Found!");
        }
        /* if file not exist then create one */
        if (!destFile.exists()) {
            try {
                destFile.createNewFile();
                //System.out.println("Destination file doesn't exist. Creating one!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            /**
             * getChannel() returns unique FileChannel object associated a file
             * output stream.
             */
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

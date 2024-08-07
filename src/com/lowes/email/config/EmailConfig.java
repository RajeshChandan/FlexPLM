package com.lowes.email.config;

import com.lcs.wc.util.FormatHelper;
import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTProperties;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class EmailConfig {
    private static final Logger logger = LogR.getLogger(EmailConfig.class.getName());
    public static String getUrl = "";
    public static String WindchillContext = "/Windchill";
    public static String SMTP_HOST = "";
    public static String SMTP_PORT = "25";
    public static String SMTP_USERNAME = "";
    public static String SMTP_PASSWORD = "";
    public static String SENDER_EMAIL = "";
    public static String SMTP_PROTOCOL = "";
    public static String SMTP_ENABLE_TLS;

    static {
        try {
            WTProperties wtproperties = WTProperties.getLocalProperties();
            getUrl = wtproperties.getProperty("wt.server.codebase", "");
            String mailPropertiesPath = wtproperties.getProperty("wt.mail.properties");

            if (mailPropertiesPath != null) {
                wtproperties.load(new BufferedInputStream(new FileInputStream(mailPropertiesPath)));
            }

            SMTP_USERNAME = wtproperties.getProperty("wt.mail.smtp.username");
            SMTP_PASSWORD = wtproperties.getProperty("wt.mail.smtp.password");
            SMTP_PROTOCOL = wtproperties.getProperty("wt.mail.transport.protocol");

            SENDER_EMAIL = wtproperties.getProperty("wt.mail.from");

            if (!FormatHelper.hasContent(SMTP_PROTOCOL)) {
                SMTP_PROTOCOL = "smtp";
            }

            SMTP_HOST = wtproperties.getProperty("wt.mail.mailhost");
            int colonIdx = SMTP_HOST.indexOf(':');
            if (colonIdx >= 0) {
                SMTP_PORT = SMTP_HOST.substring(colonIdx + 1, SMTP_HOST.length());
                SMTP_HOST = SMTP_HOST.substring(0, colonIdx);
            }

            SMTP_ENABLE_TLS = wtproperties.getProperty("wt.mail.smtp.starttls.enable");


        } catch (Exception e) {
            logger.error("", e);
        }
    }
}

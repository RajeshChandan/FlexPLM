package com.lowes.email.Helper;

import com.infoengine.au.IEProperties;
import com.infoengine.au.NamingService;
import com.lcs.wc.util.FormatHelper;
import com.lowes.email.config.EmailConfig;
import com.lowes.email.model.EmailModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTProperties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class EmailHelper {
    private static final Logger logger = LogR.getLogger(EmailHelper.class.getName());

    public boolean sendMail(EmailModel emailModel) throws MessagingException, IOException {
        if (!validateData(emailModel)) {
            return false;
        }

        Session sessionMail;

        // Create the message and transport objects:
        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", EmailConfig.SMTP_HOST);
        prop.put("mail.smtp.port", EmailConfig.SMTP_PORT);

        if (FormatHelper.hasContent(EmailConfig.SMTP_USERNAME) && FormatHelper.hasContent(EmailConfig.SMTP_PASSWORD)) {
            prop.put("mail.smtp.user", EmailConfig.SMTP_USERNAME);
            prop.put("mail.smtp.from", EmailConfig.SMTP_USERNAME);
            prop.put("mail.smtp.password", EmailConfig.SMTP_PASSWORD);
            prop.put("mail.smtp.auth", "true");
        }

        IEProperties ieprops = NamingService.getIEProperties();
        String ieSmtpTimeout = ieprops.getProperty("com.infoengine.mail.smtp.timeout", "3600000");
        String ieSmtpConnectionTimeout = ieprops.getProperty("com.infoengine.mail.smtp.connectiontimeout", "120000");
        prop.put("mail.smtp.timeout", ieSmtpTimeout);
        prop.put("mail.smtp.connectiontimeout", ieSmtpConnectionTimeout);

        if (FormatHelper.hasContent(EmailConfig.SMTP_ENABLE_TLS)) {
            prop.put("mail.smtp.starttls.enable", "true");
        }

        if (FormatHelper.hasContent(EmailConfig.SMTP_USERNAME) && FormatHelper.hasContent(EmailConfig.SMTP_PASSWORD)) {
            sessionMail = Session.getInstance(prop, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(EmailConfig.SMTP_USERNAME, EmailConfig.SMTP_PASSWORD);
                        }
                    }
            );
        } else {
            sessionMail = Session.getInstance(prop);
        }
        Transport transport = sessionMail.getTransport(EmailConfig.SMTP_PROTOCOL);

        MimeMessage msg = new MimeMessage(sessionMail);

        Address senderAddress = buildRecipient(EmailConfig.SENDER_EMAIL);
        if (FormatHelper.hasContent(emailModel.getSENDER_EMAIL())) {
            senderAddress = buildRecipient(emailModel.getSENDER_EMAIL());
        }
        logger.info("config address>>>>>>{}", EmailConfig.SENDER_EMAIL);
        logger.info("model address>>>>>>{}", emailModel.getSENDER_EMAIL());
        logger.info("adderresdsds>>>>{}", senderAddress);
        msg.setFrom(senderAddress);


        Address[] recipients = buildRecipients(emailModel.getRECIPIENT_EMAIL());
        if (recipients.length > 0) {
            msg.setRecipients(Message.RecipientType.TO, recipients);
        }
        recipients = buildRecipients(emailModel.getCC_RECIPIENT_EMAIL());
        if (recipients.length > 0) {
            msg.setRecipients(Message.RecipientType.CC, recipients);
        }
        recipients = buildRecipients(emailModel.getBCC_RECIPIENT_EMAIL());
        if (recipients.length > 0) {
            msg.setRecipients(Message.RecipientType.BCC, recipients);
        }


        msg.setSubject(MimeUtility.encodeText(emailModel.getEMAIL_SUBJECT(), "UTF-8", "B"));
       // msg.setContent(emailModel.getEMAIL_CONTENT(), "text/html;charset=utf-8");
        msg.setContent(buildMailBody(emailModel.getEMAIL_CONTENT()));
        Transport.send(msg);

        return true;
    }

    private MimeMultipart buildMailBody(String content) throws MessagingException, IOException {
        WTProperties wtProperties = WTProperties.getLocalProperties();
        String wtHome = wtProperties.getProperty("wt.home");
        String logoFile = wtHome + "/codebase/rfa/images/lowes_logo2.png";

        MimeMultipart multipart = new MimeMultipart("related");

        // first part (the html)
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(content, "text/html");
        // add it
        multipart.addBodyPart(messageBodyPart);

        // second part (the image)
        messageBodyPart = new MimeBodyPart();
        DataSource fds = new FileDataSource(logoFile);

        messageBodyPart.setDataHandler(new DataHandler(fds));
        messageBodyPart.setHeader("Content-ID", "<image>");

        // add image to the multipart
        multipart.addBodyPart(messageBodyPart);
        return multipart;
    }

    private Address[] buildRecipients(List<String> emails) {
        List<Address> addressList = new ArrayList<>();
        if (Objects.isNull(emails) || emails.isEmpty()) {
            return new Address[0];
        }
        for (String emailAddress : emails) {
            try {
                new InternetAddress(emailAddress).validate();
                addressList.add(new InternetAddress(emailAddress));
            } catch (Exception e) {
                logger.info("Handled Exception - May be Ignored", e);
            }
        }

        Address[] adds = new Address[addressList.size()];
        adds = addressList.toArray(adds);
        return adds;
    }

    private Address buildRecipient(String emailAddress) {
        Address address = null;
        if (StringUtils.isBlank(emailAddress)) {
            return address;
        }
        try {
            new InternetAddress(emailAddress).validate();
            address = new InternetAddress(emailAddress);
        } catch (Exception e) {
            logger.info("Handled Exception - May be Ignored", e);
        }

        return address;
    }

    private boolean validateData(EmailModel emailModel) {

        //recipient
        boolean valid = !emailModel.getRECIPIENT_EMAIL().isEmpty();
        //subject
        if (emailModel.getEMAIL_SUBJECT().isEmpty()) {
            valid = false;
        }
        //content
        if (emailModel.getEMAIL_CONTENT().isEmpty()) {
            valid = false;
        }

        return valid;
    }
}

package com.lowes.notification.service;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.lowes.email.Helper.EmailHelper;
import com.lowes.email.model.EmailModel;
import com.lowes.notification.model.CostSheetEmailModel;
import com.lowes.notification.model.DocumentEmailModel;
import com.lowes.notification.model.SampleEmailModel;
import com.lowes.notification.model.SourcingEmailModel;
import com.lowes.notification.model.config.RecipientType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.notify.NotificationException;
import wt.util.WTProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author R Jeyaganeshan
 */
public class NotificationQueueProcessor {
    private static final Logger LOGGER = LogR.getLogger(NotificationQueueProcessor.class.getName());

    /**
     * To hide the public implicit one
     */
    private NotificationQueueProcessor() {
    }

    /**
     * This method processQueueEntry is processed in the LOWES_NOTIFICATION_QUEUE
     *
     * @param roles            String
     * @param notificationType String
     * @param subscriber       String
     * @param recipientType    String
     * @param objectId         String
     * @throws NotificationException NotificationException
     */
    public static void processQueueEntry(String roles, String notificationType, String subscriber, String recipientType,
                                         String objectId) throws NotificationException {

        LOGGER.debug("processQueueEntry- Start");
        try {
            //To return if empty values received for mandatory params
            if (StringUtils.isEmpty(roles) || StringUtils.isEmpty(notificationType) || StringUtils.isEmpty(recipientType)) {
                LOGGER.error("processQueueEntry-  Provided input is null or empty");
                return;
            }

            WTObject wtObj = (WTObject) LCSQuery.findObjectById(objectId);
            List<String> rolesList = FormatHelper.commaSeparatedListToList(roles);
            RecipientType type = RecipientType.valueOf(recipientType);
            //To get Flex Object specific emailModel
            EmailModel emailModel;
            switch (wtObj.getClass().getName()) {
                case "com.lcs.wc.sourcing.LCSSourcingConfig":
                    emailModel = new SourcingEmailModel().getEmailModel(wtObj, rolesList, type, notificationType);
                    break;
                case "com.lcs.wc.sample.LCSSample":
                    emailModel = new SampleEmailModel().getEmailModel(wtObj, rolesList, type, notificationType);
                    break;
                case "com.lcs.wc.sourcing.LCSProductCostSheet":
                    emailModel = new CostSheetEmailModel().getEmailModel(wtObj, rolesList, type, notificationType);
                    break;
                case "com.lcs.wc.document.LCSDocument":
                    emailModel = new DocumentEmailModel().getEmailModel(wtObj, rolesList, type, notificationType);
                    break;
                default:
                    emailModel = null;
                    break;
            }

            if (Objects.nonNull(emailModel) && !emailModel.getRECIPIENT_EMAIL().isEmpty()) {
                LOGGER.debug("processQueueEntry- Email Model getRECIPIENT_EMAIL {}", emailModel.getRECIPIENT_EMAIL());
                // As of now, only subscribers enabled for LCSDocument && DocumentPublished
                if (wtObj instanceof LCSDocument && notificationType.equalsIgnoreCase("DocumentPublished")) {
                    emailModel.setRECIPIENT_EMAIL(null);
                }
                emailModel.getRECIPIENT_EMAIL().addAll(getSubscribers(subscriber));

                LOGGER.debug("processQueueEntry-  Email Model getRECIPIENT_EMAIL {}", emailModel.getRECIPIENT_EMAIL());

                emailModel.setRECIPIENT_EMAIL(emailModel.getRECIPIENT_EMAIL().stream().distinct().collect(Collectors.toList()));

                LOGGER.debug("processQueueEntry-  Distinct Email Model getRECIPIENT_EMAIL final {}", emailModel.getRECIPIENT_EMAIL());
                //add server prefix fro non prod servers
                String subjectPreFix = getSubjectPrefix();
                if (FormatHelper.hasContent(subjectPreFix)) {
                    emailModel.setEMAIL_SUBJECT(subjectPreFix + emailModel.getEMAIL_SUBJECT());
                }
                boolean status = new EmailHelper().sendMail(emailModel);
                LOGGER.debug("processQueueEntry-  Sent Mail = {}", status);
            }

        } catch (Exception e) {
            throw new NotificationException(e);
        }

        LOGGER.debug("processQueueEntry-  End");
    }

    /**
     * @param subscribers String
     * @return List<String>
     */
    private static List<String> getSubscribers(String subscribers) {
        if (!FormatHelper.hasContent(subscribers)) {
            return new ArrayList<>();
        }
        return FormatHelper.commaSeparatedListToList(subscribers);
    }

    /**
     * @return String
     * @throws IOException IOException
     */
    private static String getSubjectPrefix() throws IOException {
        String preFix = "";

        WTProperties wtproperties = WTProperties.getLocalProperties();
        String hostname = wtproperties.getProperty("wt.rmi.server.hostname", "");
        //To add subject prefix while sending mails from non production servers
        if (!hostname.contains("lowes-prod")) {
            hostname = hostname.replaceAll(".ptcmscloud.com", "");
            // Test Mail | lowes-TEST Server
            preFix = preFix.concat("[");
            preFix = preFix.concat("Test Mail | ");
            preFix = preFix.concat(hostname);
            preFix = preFix.concat(" Server ] ");
        }
        return preFix;
    }
}

package com.lowes.notification.model;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.DownloadURLHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lowes.email.config.EmailConfig;
import com.lowes.email.model.EmailModel;
import com.lowes.email.model.EmailTemplates;
import com.lowes.email.template.EmailTemplateProcessor;
import com.lowes.notification.helper.NotificationEmailHelper;
import com.lowes.notification.util.RecipientUtil;
import com.lowes.util.PLMObjectLinkUtil;
import org.apache.logging.log4j.Logger;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTProperties;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

public class DocumentRefLinkEmailModel {
    public static final String DEFAULT_ENCODING = LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset", "UTF-8");
    private static final Logger logger = LogR.getLogger(DocumentRefLinkEmailModel.class.getName());
    private final NotificationEmailHelper notificationEmailHelper = new NotificationEmailHelper();

    public EmailModel buildEmail(List<String> rolesList, LCSDocument document, LCSProduct product, FlexSpecification specification, List<LCSSeason> seasons) throws IOException, PropertyVetoException, WTException {

        String message = "A New Document" + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildDocumentLink(document), document.getName()) + " has been added to " + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildProductLink(product, specification), product.getName() + ".");
        message = message.concat("<br>");
        message = message.concat("<br>");

        List<String> emailRecipients = new RecipientUtil().getRecipients(product, seasons, new ArrayList<>(rolesList));

        if (emailRecipients.isEmpty()) {
            logger.warn("No recipient found for role {}", rolesList);
            return null;
        }

        EmailTemplateProcessor emailTemplateProcessor = new EmailTemplateProcessor();
        emailTemplateProcessor.buildTemplate(message);
        String attachment = buildAttachments(document);

        if (FormatHelper.hasContent(attachment)) {
            emailTemplateProcessor = new EmailTemplateProcessor(EmailTemplates.DEFAULT_ATTACHMENT.getTemplate());
            emailTemplateProcessor.buildTemplate(message);
            emailTemplateProcessor.buildAttachment(attachment);
        }
        emailTemplateProcessor.buildSalutation(notificationEmailHelper.getEmailSalutation(rolesList));
        EmailModel emailModel = new EmailModel();
        emailModel.setSENDER_EMAIL(EmailConfig.SENDER_EMAIL);
        emailModel.setRECIPIENT_EMAIL(emailRecipients);

        emailModel.setEMAIL_SUBJECT("A Document is Added to " + product.getName());
        emailModel.setEMAIL_CONTENT(emailTemplateProcessor.getTemplateContent());

        return emailModel;
    }

    private String buildAttachments(LCSDocument document) throws PropertyVetoException, WTException, IOException {

        Map<String, String> attachments = new HashMap<>();
        WTProperties wtproperties = WTProperties.getLocalProperties();
        String windchillURL = wtproperties.getProperty("wt.server.codebase", "");
        windchillURL = windchillURL.concat("/rfa/jsp/main/");

        if (Objects.isNull(document)) {
            return "";
        }

        try {
            document = (LCSDocument) VersionHelper.latestIterationOf(document);
        } catch (Exception e) {
        }

        document = (LCSDocument) ContentHelper.service.getContents(document);
        ApplicationData primaryApplicationData = (ApplicationData) ContentHelper.getPrimary(document);

        if (primaryApplicationData != null) {
            String fileName = primaryApplicationData.getFileName();
            fileName = URLDecoder.decode(fileName, DEFAULT_ENCODING);
            String link = DownloadURLHelper.getReusableAuthenticatedDownloadURL(primaryApplicationData, document);
            attachments.put(fileName, windchillURL + link);
        }

        Vector applicationDataCol = ContentHelper.getApplicationData(document);
        Vector contentList = ContentHelper.getContentList(document);

        int var33;
        for (int i = 0; i < contentList.size(); ++i) {
            applicationDataCol.elementAt(i);
        }
        if (!applicationDataCol.isEmpty()) {
            for (var33 = 0; var33 < applicationDataCol.size(); ++var33) {
                ApplicationData ad = (ApplicationData) applicationDataCol.elementAt(var33);
                String fileName = ad.getFileName();
                fileName = URLDecoder.decode(fileName, DEFAULT_ENCODING);
                String link = DownloadURLHelper.getReusableAuthenticatedDownloadURL(ad, document);

                attachments.put(fileName, windchillURL + link);
            }
        }
        logger.info("attachments>>>>{}", attachments);

        if (!attachments.isEmpty()) {
            return EmailTemplateProcessor.drawTable(attachments);
        }
        return "";
    }
}

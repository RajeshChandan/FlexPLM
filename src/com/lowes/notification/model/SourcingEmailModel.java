package com.lowes.notification.model;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.VersionHelper;
import com.lowes.email.model.EmailModel;
import com.lowes.email.template.EmailTemplateProcessor;
import com.lowes.notification.helper.NotificationEmailHelper;
import com.lowes.notification.model.config.RecipientType;
import com.lowes.notification.util.NotificationConstants;
import com.lowes.notification.util.NotificationUtil;
import com.lowes.notification.util.RecipientUtil;
import com.lowes.util.ObjectUtil;
import com.lowes.util.PLMObjectLinkUtil;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SourcingEmailModel {

    private final RecipientUtil recipientUtil = new RecipientUtil();
    private final NotificationEmailHelper notificationEmailHelper = new NotificationEmailHelper();

    public EmailModel getEmailModel(WTObject wtObject, List<String> rolesList, RecipientType type, String notificationType) throws WTException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        EmailModel emailModel = new EmailModel();
        if (Objects.isNull(wtObject) || !(wtObject instanceof LCSSourcingConfig)) {
            return emailModel;
        }


        LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) wtObject;
        LCSProduct product = VersionHelper.latestIterationOf(sourcingConfig.getProductMaster());
        List<LCSSeason> seasons = ObjectUtil.getSeasonsForSource(sourcingConfig);
        LCSSupplier supplier = ObjectUtil.getVendorFromSource(sourcingConfig);

        //recipients
        emailModel.getRECIPIENT_EMAIL().addAll(recipientUtil.getRecipientByType(type, new ArrayList<>(rolesList), supplier, seasons, product, sourcingConfig));

        //subject
        emailModel.setEMAIL_SUBJECT(getEmailSubject(notificationType));
        //body
        EmailTemplateProcessor emailTemplateProcessor = new EmailTemplateProcessor();
        emailTemplateProcessor.buildTemplate(getEmailBody(notificationType, product, seasons));
        emailTemplateProcessor.buildSalutation(notificationEmailHelper.getEmailSalutation(rolesList));
        emailModel.setEMAIL_CONTENT(emailTemplateProcessor.getTemplateContent());
        return emailModel;
    }

    private String getEmailSubject(String notificationType) {
        String subject;

        if (notificationType.equals("SourcingConfig")) {
            subject = "PLM Action Required: New RFP Assigned";
        } else {
            subject = "PLM Action Required: New RFP Assigned to Vendor";
        }
        return subject;
    }

    /**
     * @param notificationType String
     * @param product          LCSProduct
     * @param seasons          List<LCSSeason>
     * @return String
     * @throws IOException IOException
     * @throws WTException WTException
     */
    private String getEmailBody(String notificationType, LCSProduct product, List<LCSSeason> seasons) throws IOException, WTException {
        String body;
        WTUser user = recipientUtil.getRecipientUser(product, seasons, NotificationConstants.PDM);
        if (notificationType.equals("SourcingConfig")) {
            body = "Congratulations, You have been invited to participate in "
                    + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildProductLink(product, "'PRODUCT", ""), product.getName());

            body = body.concat(". Please login to Lowe's PLM and take the next steps as guided by PDM " + NotificationUtil.formatUser(user) + ".");
            body = body.concat(notificationEmailHelper.getEmailGenericBodyFooter());
        } else {
            body = "Congratulations, You have been invited to participate in RFP. Please login to Lowe's PLM and take the next steps as guided by PDM";
        }
        return body;
    }

}

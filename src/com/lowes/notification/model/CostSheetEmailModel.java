package com.lowes.notification.model;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSProductCostSheet;
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

/**
 * Cost Sheet specific Email Model
 */
public class CostSheetEmailModel {
    private final RecipientUtil recipientUtil = new RecipientUtil();
    private final NotificationEmailHelper notificationEmailHelper = new NotificationEmailHelper();

    /**
     * @param wtObject         WTObject
     * @param rolesList        List<String>
     * @param type             RecipientType
     * @param notificationType String
     * @return EmailModel
     * @throws WTException               WTException
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException     NoSuchMethodException
     * @throws IllegalAccessException    IllegalAccessException
     * @throws IOException               IOException
     */
    public EmailModel getEmailModel(WTObject wtObject, List<String> rolesList, RecipientType type, String notificationType) throws WTException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        EmailModel emailModel = new EmailModel();
        if (Objects.isNull(wtObject) || !(wtObject instanceof LCSProductCostSheet)) {
            return emailModel;
        }

        LCSProductCostSheet costSheet = (LCSProductCostSheet) wtObject;
        LCSProduct product = VersionHelper.latestIterationOf(costSheet.getProductMaster());
        LCSSeason season = VersionHelper.latestIterationOf(costSheet.getSeasonMaster());
        LCSSourcingConfig sourcingConfig = VersionHelper.latestIterationOf(costSheet.getSourcingConfigMaster());
        LCSSupplier supplier = ObjectUtil.getVendorFromSource(sourcingConfig);

        //recipients
        emailModel.getRECIPIENT_EMAIL().addAll(recipientUtil.getRecipientByType(type, new ArrayList<>(rolesList), supplier, List.of(season), product, costSheet));

        //subject
        emailModel.setEMAIL_SUBJECT(getEmailSubject(notificationType, season, supplier));
        //body
        EmailTemplateProcessor emailTemplateProcessor = new EmailTemplateProcessor();
        emailTemplateProcessor.buildTemplate(getEmailBody(notificationType, season, supplier, product, costSheet));
        emailTemplateProcessor.buildSalutation(notificationEmailHelper.getEmailSalutation(rolesList));
        emailModel.setEMAIL_CONTENT(emailTemplateProcessor.getTemplateContent());


        return emailModel;
    }

    /**
     * @param notificationType String
     * @param season           LCSSeason
     * @param supplier         LCSSupplier
     * @return String
     */
    private String getEmailSubject(String notificationType, LCSSeason season, LCSSupplier supplier) {
        String emailSubject;
        //Different email subject based on notificationType used in the notificationTriggerConfig JSON file
        switch (notificationType) {
            case "CostSheetRequested":
                emailSubject = "PLM Action Required: Cost Sheet Requested for " + season.getName();
                break;
            case "CostSheetSubmitted":
                emailSubject = "PLM Update: Cost Sheet Submitted for Review for " + season.getName() + " by " + supplier.getName();
                break;
            case "CostSheetRevision":
                emailSubject = "PLM Action Required: Cost Sheet Revision Requested for " + season.getName();
                break;
            default:
                emailSubject = "PLM Action Required: Cost Sheet Revision Requested for season";
                break;
        }
        return emailSubject;
    }

    /**
     * @param notificationType String
     * @param season           LCSSeason
     * @param supplier         LCSSupplier
     * @param product          LCSProduct
     * @param costSheet        LCSProductCostSheet
     * @return String
     * @throws IOException IOException
     * @throws WTException IOException
     */
    private String getEmailBody(String notificationType, LCSSeason season, LCSSupplier supplier, LCSProduct product, LCSProductCostSheet costSheet) throws IOException, WTException {
        String body = "";

        WTUser pdmUser = recipientUtil.getRecipientUser(product, List.of(season), NotificationConstants.PDM);
        //Different email body based on notificationType used in the notificationTriggerConfig JSON file
        switch (notificationType) {
            case "CostSheetRequested":
                body = body.concat("Please provide costing inputs in PLM for the requested items in season "
                        + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildLineSheetLink(season), season.getName() + "."));
                body = body.concat(" Please login to Lowe's PLM and take the next steps as guided by PDM " + NotificationUtil.formatUser(pdmUser) + ".");
                body = body.concat(notificationEmailHelper.getEmailGenericBodyFooter());
                break;
            case "CostSheetSubmitted":

                body = body.concat("The cost sheet is submitted by " + EmailTemplateProcessor.buildAsBold(supplier.getName()));
                body = body.concat(" for items under season " + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildLineSheetLink(season), season.getName()));
                body = body.concat(" and " + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildProductCostSheetLink(costSheet), product.getName()));
                body = body.concat(". Please review and take appropriate action. ");

                break;
            case "CostSheetRevision":
                body = body.concat("Please provide " + EmailTemplateProcessor.buildAsBold("revised") + " costing inputs in PLM for the ");
                body = body.concat("requested item " + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildProductCostSheetLink(costSheet), product.getName()) + " in season - " +
                        EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildLineSheetLink(season), season.getName() + "."));
                body = body.concat(" Please login to Lowe's PLM and take the next steps as guided by PDM " + NotificationUtil.formatUser(pdmUser) + ".");

                body = body.concat(notificationEmailHelper.getEmailGenericBodyFooter());
                break;
            default:
                body = "PLM Action Required: Cost Sheet Revision Requested for season.";
                break;
        }
        return body;
    }

}

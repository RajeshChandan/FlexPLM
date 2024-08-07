package com.lowes.notification.model;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecification;
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
 * Sample specific email model
 */
public class SampleEmailModel {

    private static final String SAMPLE_CONSTANT = "SAMPLE";
    private static final String SAMPLE_DETAIL_TAB = "sampleDetailTab";
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

        if (Objects.isNull(wtObject) || !(wtObject instanceof LCSSample)) {
            return emailModel;
        }

        LCSSample sample = (LCSSample) wtObject;
        LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(sample.getSourcingMaster());
        FlexSpecification specification = VersionHelper.latestIterationOf(sample.getSpecMaster());
        LCSProduct product = VersionHelper.latestIterationOf(sourcingConfig.getProductMaster());
        List<LCSSeason> seasons = ObjectUtil.getSeasonsForSpecification(specification);
        LCSSupplier supplier = ObjectUtil.getVendorFromSource(sourcingConfig);

        //recipients

        emailModel.getRECIPIENT_EMAIL().addAll(recipientUtil.getRecipientByType(type, new ArrayList<>(rolesList), supplier, seasons, product, sample));

        //subject
        emailModel.setEMAIL_SUBJECT(getEmailSubject(notificationType, product, supplier));
        //body
        EmailTemplateProcessor emailTemplateProcessor = new EmailTemplateProcessor();
        emailTemplateProcessor.buildTemplate(getEmailBody(notificationType, supplier, seasons, product, sample));
        emailTemplateProcessor.buildSalutation(notificationEmailHelper.getEmailSalutation(rolesList));
        emailModel.setEMAIL_CONTENT(emailTemplateProcessor.getTemplateContent());


        return emailModel;
    }

    /**
     * @param notificationType String
     * @param product          LCSProduct
     * @param supplier         LCSSupplier
     * @return String
     */
    private String getEmailSubject(String notificationType, LCSProduct product, LCSSupplier supplier) {
        String emailSubject;
        //Different email subject based on notificationType used in the notificationTriggerConfig JSON file
        switch (notificationType) {
            case "SampleRequested":
                emailSubject = "PLM Action Required: New Sample Request for Item " + product.getName();
                break;
            case "SampleResubmit":
                emailSubject = "PLM Action Required: Resubmitted Sample Request for Item " + product.getName();
                break;
            case "SampleShipped":
                emailSubject = "PLM Update: Sample is Shipped for Item " + product.getName() + " by vendor " + supplier.getName();
                break;
            default:
                emailSubject = "PLM Update: Sample is updated for item " + product.getName();

        }
        return emailSubject;
    }

    /**
     * @param notificationType String
     * @param supplier         LCSSupplier
     * @param seasons          List<LCSSeason>
     * @param product          LCSProduct
     * @param sample           LCSSample
     * @return String
     * @throws IOException IOException
     * @throws WTException WTException
     */
    private String getEmailBody(String notificationType, LCSSupplier supplier, List<LCSSeason> seasons, LCSProduct product, LCSSample sample) throws IOException, WTException {
        String body = "";

        WTUser pdmUser = recipientUtil.getRecipientUser(product, seasons, NotificationConstants.PDM);
        String productDescription = String.valueOf(product.getValue("vrdDescription"));
        //Different email body based on notificationType used in the notificationTriggerConfig JSON file
        switch (notificationType) {
            case "SampleRequested":
                body = body.concat("Please provide details for the requested sample " + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildProductSampleLink(sample), sample.getName()));
                body = body.concat(" for Item "
                        + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildProductLink(product, SAMPLE_CONSTANT, SAMPLE_DETAIL_TAB), productDescription + "."));
                body = body.concat(" Please login to Lowe's PLM and take the next steps as guided by PDM " + NotificationUtil.formatUser(pdmUser) + ".");
                body = body.concat(notificationEmailHelper.getEmailGenericBodyFooter());
                break;
            case "SampleResubmit":

                body = body.concat(" Please resubmit sample details for the requested sample " + recipientUtil);
                body = body.concat(" for Item "
                        + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildProductLink(product, SAMPLE_CONSTANT, SAMPLE_DETAIL_TAB), productDescription + "."));

                body = body.concat(" Please login to Lowe's PLM and take the next steps as guided by PDM " + NotificationUtil.formatUser(pdmUser) + ".");
                body = body.concat(notificationEmailHelper.getEmailGenericBodyFooter());
                break;
            case "SampleShipped":
                body = body.concat("The sample " + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildProductSampleLink(sample), sample.getName()) + " is shipped by " + EmailTemplateProcessor.buildAsBold(supplier.getName()));
                body = body.concat(" for item " + EmailTemplateProcessor.buildLink(PLMObjectLinkUtil.buildProductLink(product, SAMPLE_CONSTANT, SAMPLE_DETAIL_TAB), productDescription + "."));
                body = body.concat(" Please review and take appropriate action. ");
                break;
            default:
                body = "PLM Update: Sample is updated for season ";

        }
        return body;
    }

}

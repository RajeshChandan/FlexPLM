package com.lowes.notification.model;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.util.VersionHelper;
import com.lowes.email.model.EmailModel;
import com.lowes.email.template.EmailTemplateProcessor;
import com.lowes.notification.helper.AttributeHelper;
import com.lowes.notification.model.config.RecipientType;
import com.lowes.notification.util.RecipientUtil;
import com.lowes.util.PLMObjectLinkUtil;
import wt.fc.WTObject;
import wt.util.WTException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * Document specific Email Model
 */
public class DocumentEmailModel {
    private final RecipientUtil recipientUtil = new RecipientUtil();

    /**
     * @param wtObject         WTObject
     * @param rolesList        List<String>
     * @param type             RecipientType
     * @param notificationType String
     * @return EmailModel EmailModel
     * @throws WTException               WTException
     * @throws IOException               IOException
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException     NoSuchMethodException
     * @throws IllegalAccessException    IllegalAccessException
     */
    public EmailModel getEmailModel(WTObject wtObject, List<String> rolesList, RecipientType type, String notificationType) throws WTException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        EmailModel emailModel = new EmailModel();
        if (Objects.isNull(wtObject) || !(wtObject instanceof LCSDocument)) {
            return emailModel;
        }
        LCSDocument document = (LCSDocument) wtObject;

        String merchandiseSubDivision = AttributeHelper.getAttributeStringValue(document, "lowesMerchandiseSubDivision");
        //This will consider the last word and will be used in custom query as suffix to get groups and email list
        String divisionLastWord = merchandiseSubDivision.substring(merchandiseSubDivision.lastIndexOf(" ") + 1);

        //get Recipients by passing the last word of divison from document object to get respective groups and subsequently user emails
        emailModel.getRECIPIENT_EMAIL().addAll(recipientUtil.getRecipientsForRoles(List.of(divisionLastWord), true));
        // get Recipients by passing roles in the JSON
        emailModel.getRECIPIENT_EMAIL().addAll(recipientUtil.getRecipientByType(type, rolesList, null, null, null, document));
        //subject
        emailModel.setEMAIL_SUBJECT(getEmailSubject(notificationType, document));
        //body
        EmailTemplateProcessor emailTemplateProcessor = new EmailTemplateProcessor();
        //Get the email body
        emailTemplateProcessor.buildTemplate(getEmailBody(notificationType, document));
        //Set the email content
        emailModel.setEMAIL_CONTENT(emailTemplateProcessor.getTemplateContent());

        //return the emailModel
        return emailModel;
    }

    /**
     * @param notificationType String
     * @param document         LCSDocument
     * @return String
     */
    private String getEmailSubject(String notificationType, LCSDocument document) {

        String emailSubject;
        if (notificationType.equals("DocumentPublished")) {
            emailSubject = "Performance Specification Update Notification - " + document.getName();
        } else {
            emailSubject = "Document Update Notification - " + document.getName();
        }
        return emailSubject;
    }

    /**
     * @param notificationType String
     * @param document         LCSDocument
     * @return String
     * @throws WTException WTException
     * @throws IOException IOException
     */
    private String getEmailBody(String notificationType, LCSDocument document) throws WTException, IOException {

        String body;
        if (notificationType.equals("DocumentPublished")) {
            body = getDocumentPublishedMailMessage(document);
        } else {
            body = "Document Update Notification - " + document.getName();
        }
        return body;
    }

    /**
     * @param document LCSDocument
     * @return String
     * @throws WTException WTException
     * @throws IOException IOException
     */
    public String getDocumentPublishedMailMessage(LCSDocument document) throws WTException, IOException {

        String documentName = document.getName();
        String version = document.getIterationDisplayIdentifier().toString();
        //This try block is used for update scenario for LCSDocument for PRE_Update event on the SSP
        try {
            //This will give exception on the update scenario for LCSDocument for PRE_Update event on the SSP
            VersionHelper.getOriginalCopy(document);
        } catch (Exception e) {
            //The version obtained is the older one for LCSDocument for PRE_Update event on the SSP
            String regex = "\\.";
            //Adding 1 to the version to compensate for previous version details received
            version = version.split(regex)[0] + "." + (Integer.parseInt(version.split(regex)[1]) + 1);
        }
        // merchandiseDivision value
        String merchandiseDivision = AttributeHelper.getAttributeStringValue(document, "lowesMerchandiseDivision");
        // merchandiseSubDivision value
        String merchandiseSubDivision = AttributeHelper.getAttributeStringValue(document, "lowesMerchandiseSubDivision");
        // productGroup value
        String productGroup = AttributeHelper.getAttributeStringValue(document, "lowesProductGroup");
        // brand value
        String brand = AttributeHelper.getAttributeStringValue(document, "lwsBrand");
        // effectiveDate value
        String effectiveDate = AttributeHelper.getAttributeStringValue(document, "lwsDate");
        // documentLink value
        String documentLink = PLMObjectLinkUtil.buildDocumentLink(document);

        String body = "";
        body = body.concat("Global Merchandising Team, ");
        body = body.concat(EmailTemplateProcessor.addLineBrake(3));
        body = body.concat("Please note, the following Private Brand Performance Specification has been released or revised: " + EmailTemplateProcessor.buildAsBold(documentName));
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));
        body = body.concat("Link to document: " + EmailTemplateProcessor.buildLink(documentLink, document.getName()));
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));
        body = body.concat(EmailTemplateProcessor.buildAsBold("Required Action: "));
        body = body.concat(" PDM / Sourcing Team, please communicate this document to existing and " + "prospective vendors/factories for this product category. " + "Please utilize the bold text below in your communication email to the vendors/factories.");
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));

        body = body.concat(EmailTemplateProcessor.buildAsBold("\t\t\t\t•\tMerchandise Division:\t\t") + merchandiseDivision);
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));
        body = body.concat(EmailTemplateProcessor.buildAsBold("\t\t\t\t•\tMerchandise Sub-Division:\t\t") + merchandiseSubDivision);
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));
        body = body.concat(EmailTemplateProcessor.buildAsBold("\t\t\t\t•\tProduct Group #:\t\t") + productGroup);
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));
        body = body.concat(EmailTemplateProcessor.buildAsBold("\t\t\t\t•\tEffective Date:\t\t") + effectiveDate);
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));
        body = body.concat(EmailTemplateProcessor.buildAsBold("\t\t\t\t•\tBrand:\t\t") + brand);
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));
        body = body.concat(EmailTemplateProcessor.buildAsBold("\t\t\t\t•\tVersion:\t\t") + version);


        body = body.concat(EmailTemplateProcessor.addLineBrake(4));
        body = body.concat(EmailTemplateProcessor.buildAsBold("It is Lowe’s policy that all private brand items must undergo performance testing in product " + "categories where a released performance specification exists.  Going forward, testing is " + "required for items in this category meeting the following conditions: (1) Newly added Private " + "Brand items or (2) Existing Private Brand items which are awarded business (carried forward) via a PLR/BR "));
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));
        body = body.concat("Please reach out to your contact in US Product Engineering or SHO Production Engineering regarding any support " + "needed in coordination of Private Brand Performance Specification program testing to be conducted by vendors.  " + "You can also send an email to productengineering@lowes.com ");
        body = body.concat(EmailTemplateProcessor.addLineBrake(2));
        body = body.concat("For future reference, all released Lowe’s Private Brand Performance Specifications can be found here: " + EmailTemplateProcessor.buildLink("https://lowes.sharepoint.com/sites/lgsourcing/PSpecs/Forms/AllItems.aspx?viewid=044674a6-c6f5-447c-a03d-aab802532142", "Lowe's Global Merchandising Sharepoint/Performance Specifications"));

        return body;
    }

}

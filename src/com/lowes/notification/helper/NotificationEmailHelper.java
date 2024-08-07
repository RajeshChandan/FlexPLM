package com.lowes.notification.helper;

import com.lowes.email.template.EmailTemplateProcessor;
import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;

import java.util.List;

/**
 * @author R Jeyaganeshan
 */
public class NotificationEmailHelper {

    private static final Logger logger = LogR.getLogger(NotificationEmailHelper.class.getName());

    /**
     * To get email body footer
     *
     * @return String
     */
    public String getEmailGenericBodyFooter() {
        String body = "";
        body = body.concat(EmailTemplateProcessor.addLineBrake(4));
        body = body.concat("Please do not reply to this email as it is system-generated.");
        body = body.concat(EmailTemplateProcessor.addLineBrake(1));
        body = body.concat(EmailTemplateProcessor.DASHED_LINE);
        body = body.concat("For all support related queries to PLM, please follow "
                + EmailTemplateProcessor.buildLink("https://vendorgateway.lowes.com/gateway/resources", "PLM Training Gateway Link")
                + " or you can reach out to our support team.");
        return body;
    }

    public String getEmailSalutation(List<String> rolesList) {
        logger.info("NotificationEmailHelper.getEmailSalutation() started");
        logger.debug("role list: {} ", rolesList);
        String salutation = "Hi All";
        if (rolesList.contains("VENDORS")) {
            salutation = "Hi Vendor partner";
        }
        if (rolesList.contains("PDM")) {
            salutation = "Hi PDMs";
        }
        logger.info("NotificationEmailHelper.getEmailSalutation() salutation ={} executed successfully", salutation);
        return salutation;
    }

}

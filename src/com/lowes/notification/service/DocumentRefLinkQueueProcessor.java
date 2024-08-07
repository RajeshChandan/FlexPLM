package com.lowes.notification.service;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;
import com.lowes.email.Helper.EmailHelper;
import com.lowes.email.model.EmailModel;
import com.lowes.notification.exceptions.NotificationRuntimeException;
import com.lowes.notification.model.DocumentRefLinkEmailModel;
import org.apache.logging.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

import javax.mail.MessagingException;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.*;

public class DocumentRefLinkQueueProcessor {
    private static final Logger logger = LogR.getLogger(DocumentRefLinkQueueProcessor.class.getName());
    private static final List<String> ROLES = List.of("PDM");

    public static void processQueueEntry(Map<String, String> inputMap) throws NotificationRuntimeException {

        LCSProduct product;
        LCSDocument document;
        FlexSpecification specification = null;
        List<LCSSeason> seasons = new ArrayList<>();
        try {
            product = (LCSProduct) LCSQuery.findObjectById(inputMap.get("LCSProduct"));
            document = (LCSDocument) LCSQuery.findObjectById(inputMap.get("LCSDocument"));
            if (FormatHelper.hasContent(inputMap.get("specification"))) {
                specification = (FlexSpecification) LCSQuery.findObjectById(inputMap.get("specification"));
            }
            if (FormatHelper.hasContent(inputMap.get("seasons"))) {
                Collection<String> seasonColl = MOAHelper.getMOACollection(inputMap.get("seasons"));
                for (String o : seasonColl) {
                    LCSSeason season = findSeasonByID(o);
                    seasons.add(season);
                }
            }

            new DocumentRefLinkQueueProcessor().notifyUsers(product, document, specification, seasons);
        } catch (Exception e) {
            throw new NotificationRuntimeException(e);
        }
    }

    private static LCSSeason findSeasonByID(String seasonID) {
        LCSSeason season = null;
        try {
            season = (LCSSeason) LCSQuery.findObjectById(seasonID);
        } catch (WTException e) {
            logger.error(e);
        }
        return season;
    }

    private void notifyUsers(LCSProduct product, LCSDocument document, FlexSpecification specification, List<LCSSeason> seasons) throws WTException, MessagingException, IOException, PropertyVetoException {
        logger.info("--------DocumentRefLinkQueueProcessor.notifyUsers() started--------");
        if (Objects.isNull(product) || Objects.isNull(document) || Objects.isNull(seasons) || seasons.isEmpty()) {
            logger.debug("Input param is null {} or {} or {}", product, document, seasons);
            return;
        }
        boolean status = false;
        EmailModel emailModel = new DocumentRefLinkEmailModel().buildEmail(ROLES, document, product, specification, seasons);

        if (Objects.nonNull(emailModel) && !emailModel.getRECIPIENT_EMAIL().isEmpty()) {
            status = new EmailHelper().sendMail(emailModel);
        }
        logger.debug("Sent Mail = {}", status);
        logger.info("--------DocumentRefLinkQueueProcessor.notifyUsers() executed--------");
    }

}

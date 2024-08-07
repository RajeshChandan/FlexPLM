package com.lowes.notification.plugins;

import com.lcs.wc.document.IteratedDocumentReferenceLink;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.VersionHelper;
import com.lowes.notification.util.NotificationConstants;
import com.lowes.util.ObjectUtil;
import com.lowes.util.queue.ProcessingQueueHelper;
import org.apache.logging.log4j.Logger;
import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.part.WTPartReferenceLink;
import wt.util.WTException;

import java.util.*;

/***
 * Server side plugin to validate when a new document is added to a Product.
 *
 * @author Rajesh Chandan Sahu (rajeshchandan.sahu@lowes.com)
 */
public class DocumentRefLinkPlugin {
    private static final Logger logger = LogR.getLogger(DocumentRefLinkPlugin.class.getName());

    /**
     * plugin entry : com.lcs.wc.foundation.LCSPluginManager.eventPlugin.2002=targetClass|com.lcs.wc.document.IteratedDocumentReferenceLink^targetType|ALL^pluginClass|com.lowes.notification.plugins.DocumentRefLinkPlugin^pluginMethod|validateNewDocument^event|PRE_CREATE_PERSIST^priority|1000
     *
     * @param wtObject - triggered obejct
     */
    public static void validateNewDocument(WTObject wtObject) {
        logger.info("--------DocumentRefLinkPlugin.validateNewDocument() started--------");
        try {
            if (wtObject instanceof IteratedDocumentReferenceLink) {
                IteratedDocumentReferenceLink refLink = (IteratedDocumentReferenceLink) wtObject;
                logger.info("refLink>>>>{}", refLink);
                Persistable roleAObject = refLink.getRoleAObject();
                Persistable roleBObject = refLink.getRoleBObject();
                //validate role A, if it is a product, specification?
                if (roleAObject instanceof FlexSpecification && roleBObject instanceof WTDocumentMaster) {
                    WTObject roleBWtObject = VersionHelper.latestIterationOf(roleBObject);
                    LCSDocument document = (LCSDocument) roleBWtObject;

                    FlexSpecification specification = (FlexSpecification) roleAObject;
                    LCSPartMaster ownerMaster = specification.getSpecOwner();
                    WTObject owner = VersionHelper.latestIterationOf(ownerMaster);
                    if (owner instanceof LCSProduct) {
                        LCSProduct product = (LCSProduct) owner;
                        logger.info("(LCSProduct)owner>>>>>>{}", product);
                        List<LCSSeason> seasons = ObjectUtil.getSeasonsForSpecification(specification);
                        logger.info("seasons>>>>>>{}", seasons);

                        new DocumentRefLinkPlugin().addToQueue(product, document, specification, seasons);
                    }
                }
                logger.info("{}<<<<<>>>>>>>{}", roleAObject, roleBObject);
            }
        } catch (Exception e) {
            logger.error(e);
        }
        logger.info("--------DocumentRefLinkPlugin.validateNewDocument() executed--------");

    }

    /**
     * plugin entry : com.lcs.wc.foundation.LCSPluginManager.eventPlugin.2003=targetClass|wt.part.WTPartReferenceLink^targetType|ALL^pluginClass|com.lowes.notification.plugins.DocumentRefLinkPlugin^pluginMethod|validateNewProductDocument^event|PRE_CREATE_PERSIST^priority|1000
     *
     * @param wtObject - triggered object
     */
    public static void validateNewProductDocument(WTObject wtObject) {
        logger.info("--------DocumentRefLinkPlugin.validateNewProductDocument() started--------");
        try {
            if (wtObject instanceof WTPartReferenceLink) {
                WTPartReferenceLink refLink = (WTPartReferenceLink) wtObject;
                Persistable roleAObject = refLink.getRoleAObject();
                Persistable roleBObject = refLink.getRoleBObject();
                logger.debug("{}<<<<<>>>>>>>>{}", roleAObject, roleBObject);

                //validate role A, if it is a product?
                if (roleAObject instanceof LCSProduct && roleBObject instanceof WTDocumentMaster) {
                    LCSProduct product = (LCSProduct) roleAObject;
                    List<LCSSeason> seasons = new ArrayList<>();

                    if (Objects.nonNull(product.getSeasonMaster())) {
                        LCSSeason season = VersionHelper.latestIterationOf(product.getSeasonMaster());
                        seasons.add(season);
                    } else {
                        Collection<?> seasonsCol = new LCSSeasonQuery().findSeasons(product);
                        for (Object object : seasonsCol) {
                            LCSSeasonMaster seasonMaster = (LCSSeasonMaster) object;
                            LCSSeason lcsSeason = VersionHelper.latestIterationOf(seasonMaster);
                            seasons.add(lcsSeason);
                        }
                    }

                    WTObject roleBWtObject = VersionHelper.latestIterationOf(roleBObject);
                    LCSDocument document = (LCSDocument) roleBWtObject;
                    new DocumentRefLinkPlugin().addToQueue(product, document, null, seasons);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        logger.info("--------DocumentRefLinkPlugin.validateNewProductDocument() executed--------");

    }

    private void addToQueue(LCSProduct product, LCSDocument document, FlexSpecification specification, List<LCSSeason> seasons) throws WTException {
        logger.info("--------DocumentRefLinkPlugin.AddToQueue() started--------");
        if (Objects.isNull(product) || Objects.isNull(document) || Objects.isNull(seasons) || seasons.isEmpty()) {
            logger.debug("Input param is null {} or {} or {}", product, document, seasons);
            return;
        }
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("LCSProduct", FormatHelper.getObjectId(product));
        inputMap.put("LCSDocument", FormatHelper.getObjectId(document));
        if (Objects.nonNull(specification)) {
            inputMap.put("FlexSpecification", FormatHelper.getObjectId(specification));
        }
        String seasonStr = "";
        for (LCSSeason season : seasons) {
            seasonStr = seasonStr.concat(MOAHelper.DELIM).concat(FormatHelper.getObjectId(season));
        }
        inputMap.put("seasons", seasonStr);

        final Class<?>[] argTypes = {Map.class};
        final Object[] argValue = {inputMap};
        new ProcessingQueueHelper().addQueueEntry(NotificationConstants.NOTIFICATION_QUEUE_NAME, argTypes, argValue, "processQueueEntry", "com.lowes.notification.service.DocumentRefLinkQueueProcessor");

        logger.info("--------DocumentRefLinkPlugin.AddToQueue() executed--------");
    }


}

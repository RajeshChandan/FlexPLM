package com.lowes.util;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSCostSheet;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.IOException;
import java.util.Objects;

public class PLMObjectLinkUtil {

    public static final String SERVER_HOSTNAME = "wt.rmi.server.hostname";

    private PLMObjectLinkUtil() {
    }

    public static String buildDocumentLink(LCSDocument document) throws IOException {
        String link;
        WTProperties wtproperties = WTProperties.getLocalProperties();
        link = wtproperties.getProperty(SERVER_HOSTNAME, "");
//
        link = link.concat("/Windchill/rfa/jsp/main/Main.jsp?activity=VIEW_DOCUMENT&tabPage=DOCUMENTS&templateType=FRAMES&oid=");
        String oid = FormatHelper.getObjectId(document);
        if (FormatHelper.hasContent(oid)) {
            link = link.concat(oid);
        } else {
            link = link.concat(ObjectUtil.getVR(document));
        }
        return link;
    }

    public static String buildLineSheetLink(LCSSeason season) throws IOException {
        String link;
        WTProperties wtproperties = WTProperties.getLocalProperties();
        link = wtproperties.getProperty(SERVER_HOSTNAME, "");
        link = link.concat("/Windchill/rfa/jsp/main/Main.jsp?activity=VIEW_LINE_PLAN&action=INIT&templateType=FRAMES&oid=");
        link = link.concat(ObjectUtil.getVR(season));
        return link;
    }

    public static String buildProductLink(LCSProduct product, String tab, String tabId) throws IOException {
        String link;

        WTProperties wtproperties = WTProperties.getLocalProperties();
        link = wtproperties.getProperty(SERVER_HOSTNAME, "");
        //DOCUMENTS, PRODUCT

        link = link.concat("/Windchill/rfa/jsp/main/Main.jsp?activity=VIEW_SEASON_PRODUCT_LINK&action=INIT&tabId=" + tabId + "&tabPage=" + tab + "&templateType=FRAMES&oid=");
        link = link.concat(ObjectUtil.getVR(product));

        return link;
    }

    public static String buildProductLink(LCSProduct product, FlexSpecification specification) throws IOException, WTException {
        String link;

        WTProperties wtproperties = WTProperties.getLocalProperties();
        link = wtproperties.getProperty(SERVER_HOSTNAME, "");

        if (Objects.nonNull(specification)) {
            LCSSeason season = product.findSeasonUsed();
            if (Objects.nonNull(season)) {
                FlexSpecToSeasonLink specLink = FlexSpecQuery.findSpecToSeasonLink(specification.getMaster(), season.getMaster());
                link = link.concat("/Windchill/rfa/jsp/main/Main.jsp?activity=VIEW_SEASON_PRODUCT_LINK&action=INIT&tabPage=SPEC_SUMMARY&templateType=FRAMES&oid=");
                link = link.concat(FormatHelper.getObjectId(specLink));
            } else {
                link = link.concat("/Windchill/rfa/jsp/main/Main.jsp?activity=VIEW_SEASON_PRODUCT_LINK&action=INIT&tabPage=SPEC_SUMMARY&templateType=FRAMES&oid=");
                link = link.concat(ObjectUtil.getVR(specification));
            }
        } else {
            link = link.concat("/Windchill/rfa/jsp/main/Main.jsp?activity=VIEW_SEASON_PRODUCT_LINK&action=INIT&tabPage=DOCUMENTS&templateType=FRAMES&oid=");
            link = link.concat(ObjectUtil.getVR(product));
        }

        return link;
    }

    public static String buildProductCostSheetLink(LCSCostSheet costSheet) throws IOException {
        String link;

        WTProperties wtproperties = WTProperties.getLocalProperties();
        link = wtproperties.getProperty(SERVER_HOSTNAME, "");

        link = link.concat("/Windchill/rfa/jsp/main/Main.jsp?activity=VIEW_SEASON_PRODUCT_LINK&tabPage=COSTING&templateType=FRAMES&oid=");
        link = link.concat(ObjectUtil.getVR(costSheet));

        return link;
    }


    public static String buildProductSampleLink(LCSSample sample) throws IOException {
        String link;

        WTProperties wtproperties = WTProperties.getLocalProperties();
        link = wtproperties.getProperty(SERVER_HOSTNAME, "");
        ///Windchill/rfa/jsp/main/Main.jsp?activity=VIEW_SAMPLE&action=INIT&tabId=&tabPage=SAMPLES&templateType=FRAMES&oid=
        link = link.concat("/Windchill/rfa/jsp/main/Main.jsp?activity=VIEW_SAMPLE&action=INIT&tabId=&tabPage=SAMPLES&templateType=FRAMES&oid=");
        link = link.concat(FormatHelper.getObjectId(sample));

        return link;
    }
}

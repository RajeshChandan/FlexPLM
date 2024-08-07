package com.lowes.notification.util;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.SortHelper;
import com.lowes.notification.model.config.RecipientType;
import org.apache.logging.log4j.Logger;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.org.*;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.util.WTException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * RecipientUtil used to get the recipient details
 */
public class RecipientUtil {
    public static final String VR_LCSPRODUCT = "VR:com.lcs.wc.product.LCSProduct:";
    public static final String TEAM_ROLE = "TEAM.ROLE";

    public static final String SECONDARY_VENDOR_EMAIL = "lwsSecondaryVendorEmail";

    public static final String VENDORS = "VENDORS";
    public static final String OBJECT_OWNER = "OBJECT_OWNER";
    private static final Logger LOGGER = LogR.getLogger(RecipientUtil.class.getName());

    /**
     * @param type      RecipientType
     * @param rolesList List<String>
     * @param supplier  LCSSupplier
     * @param seasons   List<LCSSeason>
     * @param product   LCSProduct
     * @param flexTyped FlexTyped
     * @return List<String>
     * @throws WTException               WTException
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException     NoSuchMethodException
     * @throws IllegalAccessException    IllegalAccessException
     */
    public List<String> getRecipientByType(RecipientType type, List<String> rolesList, LCSSupplier supplier, List<LCSSeason> seasons, LCSProduct product, FlexTyped flexTyped) throws WTException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        LOGGER.debug("getRecipientByType- {} Start");

        List<String> emails = new ArrayList<>();

        if (rolesList.isEmpty() || Objects.isNull(type)) {
            LOGGER.debug("getRecipientByType- roles list or recipient type is blank");
            return emails;
        }
        if (rolesList.contains(VENDORS) && Objects.isNull(supplier)) {
            LOGGER.debug("getRecipientByType-  roles list has vendors as role, provided supplier is null");
            return emails;
        }

        if (rolesList.contains(OBJECT_OWNER) && Objects.isNull(flexTyped)) {
            LOGGER.debug("getRecipientByType-  roles list has object owner, provided flexTyped is null");
            return emails;
        }
        if (RecipientType.TEAM_TEMPLATE.equals(type) && Objects.isNull(product) && Objects.isNull(seasons)) {
            LOGGER.debug("getRecipientByType-  Recipient type is team template, provided product or seasons is null or empty");
            return emails;
        }

        switch (type) {
            case TEAM_TEMPLATE:
                //role has vendor
                if (rolesList.contains(VENDORS)) {
                    rolesList.remove(VENDORS);
                    emails.addAll(getVendorRecipients(supplier));
                }
                //role has obejct owner
                if (rolesList.contains(OBJECT_OWNER)) {
                    rolesList.remove(OBJECT_OWNER);
                    emails.add(getObjectOwnerEmail(flexTyped));
                }
                emails.addAll(getRecipients(product, seasons, rolesList));
                break;
            case SYSTEM_ROLEUSER:
                emails.addAll(getRecipientsForRoles(rolesList, false));
                break;
            default:
                break;
        }

        LOGGER.debug("getRecipientByType-  End");
        return emails;
    }

    /**
     * @param product LCSProduct
     * @param seasons List<LCSSeason> seasons
     * @param roles   List<String>
     * @return List<String>
     * @throws WTException WTException
     */
    public List<String> getRecipients(LCSProduct product, List<LCSSeason> seasons, List<String> roles) throws WTException {

        LOGGER.debug("getRecipients-  Start");

        if (Objects.isNull(product) || Objects.isNull(seasons) || seasons.isEmpty()) {
            LOGGER.debug("Input params are null {} or {}", product, seasons);
            return new ArrayList<>();
        }
        List<String> emails = new ArrayList<>();
        List<LCSProduct> rfps = getRFP(product, seasons);
        for (LCSProduct rfp : rfps) {
            String prodObjOid = FormatHelper.getNumericVersionIdFromObject(rfp);
            TeamManaged teamManaged = (TeamManaged) LCSQuery.findObjectById(VR_LCSPRODUCT + prodObjOid);
            Team team = TeamHelper.service.getTeam(teamManaged);
            Object obj = LCSQuery.findObjectById(VR_LCSPRODUCT + prodObjOid);
            Collection results = com.lcs.wc.team.TeamHelper.getTeamRoleParticipants(team, (Persistable) obj);
            results = SortHelper.sortFlexObjects(results, TEAM_ROLE);
            for (Object o : results) {
                FlexObject fo = (FlexObject) o;
                if (roles.contains(fo.getString(TEAM_ROLE))) {
                    emails.addAll(getEmails(fo.getString("TEAM.PRINCIPALID")));
                }
            }
        }
        LOGGER.debug("getRecipients-  End");
        return emails;
    }

    /**
     * @param product LCSProduct
     * @param seasons List<LCSSeason>
     * @param role    String
     * @return WTUser
     * @throws WTException WTException
     */
    public WTUser getRecipientUser(LCSProduct product, List<LCSSeason> seasons, String role) throws WTException {

        LOGGER.debug("getRecipientUser- Start");

        WTUser user = null;
        if (Objects.isNull(product) || Objects.isNull(seasons) || seasons.isEmpty()) {
            LOGGER.debug("Input param is null {} or {}", product, seasons);
            return null;
        }
        List<LCSProduct> rfps = getRFP(product, seasons);
        for (LCSProduct rfp : rfps) {
            String prodObjOid = FormatHelper.getNumericVersionIdFromObject(rfp);
            TeamManaged teamManaged = (TeamManaged) LCSQuery.findObjectById(VR_LCSPRODUCT + prodObjOid);
            Team team = TeamHelper.service.getTeam(teamManaged);
            Object obj = LCSQuery.findObjectById(VR_LCSPRODUCT + prodObjOid);
            Collection results = com.lcs.wc.team.TeamHelper.getTeamRoleParticipants(team, (Persistable) obj);
            results = SortHelper.sortFlexObjects(results, TEAM_ROLE);
            for (Object o : results) {
                FlexObject fo = (FlexObject) o;
                if (role.equalsIgnoreCase(fo.getString(TEAM_ROLE))) {
                    user = getUser(fo.getString("TEAM.PRINCIPALID"));
                }
            }
        }
        LOGGER.debug("getRecipientUser- End");
        return user;
    }

    /**
     * @param product LCSProduct
     * @param seasons List<LCSSeason>
     * @return List<LCSProduct>
     * @throws WTException WTException
     */
    private List<LCSProduct> getRFP(LCSProduct product, List<LCSSeason> seasons) throws WTException {

        LOGGER.debug("getRFP-  Start");

        List<LCSProduct> rfps = new ArrayList<>();
        String productType = String.valueOf(product.getValue("RFP"));

        if ("lwsItem".equalsIgnoreCase(productType)) {
            //get product season link
            for (LCSSeason seasonObj : seasons) {
                LCSSeasonProductLink seasonProductLink = LCSSeasonQuery.findSeasonProductLink(product, seasonObj);
                LCSProduct rfpRef = (LCSProduct) seasonProductLink.getValue("lwsRFPReference");//lwsRFPReference
                rfps.add(LCSSeasonQuery.getProductForSeason(rfpRef, seasonObj.getMaster()));
            }
        } else {
            for (LCSSeason seasonObj : seasons) {
                LCSSeasonProductLink seasonProductLink = LCSSeasonQuery.findSeasonProductLink(product, seasonObj);
                rfps.add(SeasonProductLocator.getProductSeasonRev(seasonProductLink));
            }
        }
        LOGGER.debug("getRFP-  End");
        return rfps;
    }

    /**
     * @param principalId String
     * @return String
     * @throws WTException WTException
     */
    private WTUser getUser(String principalId) throws WTException {

        LOGGER.debug("getUser-  Start");

        WTUser user = null;
        WTPrincipal principal = (WTPrincipal) LCSQuery.findObjectById(principalId);
        if (principal == null) {
            return null;
        }

        if (principal instanceof WTGroup) {
            WTGroup wtgroup = (WTGroup) principal;
            Enumeration<?> members = wtgroup.members();
            while (members.hasMoreElements()) {
                principal = (WTPrincipal) members.nextElement();
            }
        }
        if (principal instanceof WTUser) {
            user = (WTUser) principal;
        }
        LOGGER.debug("getUser-  End");
        return user;
    }

    /**
     * @param principalId String
     * @return List<String>
     * @throws WTException WTException
     */
    private List<String> getEmails(String principalId) throws WTException {

        LOGGER.debug("getEmails-  Start");

        List<String> emails = new ArrayList<>();
        WTPrincipal principal = (WTPrincipal) LCSQuery.findObjectById(principalId);
        if (principal == null) {
            return emails;
        }

        if (principal instanceof WTUser) {
            WTUser user = (WTUser) principal;
            emails.add(user.getEMail());
        }
        if (principal instanceof WTGroup) {
            WTGroup wtgroup = (WTGroup) principal;
            Enumeration<?> members = wtgroup.members();
            while (members.hasMoreElements()) {
                WTPrincipal wtp = (WTPrincipal) members.nextElement();
                if ((wtp instanceof WTUser)) {
                    WTUser user = (WTUser) wtp;
                    emails.add(user.getEMail());
                }
            }
        }
        LOGGER.debug("getEmails-  End");
        return emails;
    }

    /**
     * @param roles  List<String>
     * @param preFix boolean
     * @return List<String>
     * @throws WTException WTException
     */
    public List<String> getRecipientsForRoles(List<String> roles, boolean preFix) throws WTException {

        LOGGER.debug("getRecipientsForRoles-  Start");

        List<String> emails = new ArrayList<>();
        QuerySpec querySpec = new QuerySpec();
        int principalID = querySpec.appendClassList(WTPrincipal.class, true);
        int groupID = querySpec.appendClassList(WTGroup.class, false);
        int memberLinkID = querySpec.appendClassList(MembershipLink.class, false);

        querySpec.appendJoin(memberLinkID, _MembershipLink.MEMBER_ROLE, principalID);

        querySpec.appendJoin(memberLinkID, _MembershipLink.GROUP_ROLE, groupID);

        for (int i = 0; i < roles.size(); i++) {

            if (i != 0) {
                querySpec.appendOr();
            }
            if (preFix) {
                querySpec.appendWhere(new SearchCondition(WTGroup.class, _WTPrincipal.NAME, SearchCondition.LIKE, "%" + roles.get(i), false), new int[]{groupID});
            } else {
                querySpec.appendWhere(new SearchCondition(WTGroup.class, _WTPrincipal.NAME, SearchCondition.LIKE, roles.get(i) + "%", false), new int[]{groupID});
            }
        }

        LOGGER.debug("getRecipientsForRoles-  sql statement>>>>{}", querySpec);

        QueryResult queryResult = PersistenceHelper.manager.find(querySpec);
        LOGGER.debug("getRecipientsForRoles-  queryResult result >>>{}", queryResult.size());

        while (queryResult.hasMoreElements()) {
            Persistable[] prsistables = (Persistable[]) queryResult.nextElement();
            WTUser user = (WTUser) prsistables[principalID];
            emails.add(user.getEMail());
        }
        LOGGER.debug("getRecipientsForRoles-  emails emails>>>>{}", emails);
        LOGGER.debug("getRecipientsForRoles-  End");
        return emails;
    }

    /**
     * @param supplier LCSSupplier
     * @return List<String>
     * @throws WTException WTException
     */
    public List<String> getVendorRecipients(LCSSupplier supplier) throws WTException {

        LOGGER.debug("getVendorRecipients- Start");

        List<String> recipients = new ArrayList<>();
        if (Objects.isNull(supplier) || supplier.getValue("vrdVendorGroup") == null) {
            LOGGER.debug("getVendorRecipients-  Supplier is null or it does has group name mapped");
            return recipients;
        }
        recipients = getRecipientsForRoles(Collections.singletonList(String.valueOf(supplier.getValue("vrdVendorGroup"))), false);

        if (supplier.getValue(SECONDARY_VENDOR_EMAIL) != null && FormatHelper.hasContent(String.valueOf(supplier.getValue(SECONDARY_VENDOR_EMAIL)))) {

            recipients.addAll(FormatHelper.commaSeparatedListToList(String.valueOf(supplier.getValue(SECONDARY_VENDOR_EMAIL))));
        }
        LOGGER.debug("getVendorRecipients-  End");
        return recipients;

    }

    /**
     * @param flexTyped FlexTyped
     * @return String
     * @throws NoSuchMethodException     NoSuchMethodException
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException    IllegalAccessException
     */
    public String getObjectOwnerEmail(FlexTyped flexTyped) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        LOGGER.debug("getObjectOwnerEmail-  Start");

        String email = "";
        if (Objects.isNull(flexTyped)) {
            LOGGER.debug("getObjectOwnerEmail-  {}Flex typed is null");
            return email;
        }
        Method method = flexTyped.getClass().getMethod("getCreatorEMail");
        if (Objects.nonNull(method.invoke(flexTyped))) {
            email = (String) method.invoke(flexTyped);
        }
        LOGGER.debug("getObjectOwnerEmail-  End");
        return email;
    }
}

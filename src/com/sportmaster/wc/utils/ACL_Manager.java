package com.sportmaster.wc.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import wt.access.AccessControlHelper;
import wt.access.AccessPolicyRule;
import wt.access.AccessSelector;
import wt.access.WTAclEntry;
import wt.admin.AdminDomainRef;
import wt.admin.AdministrativeDomain;
import wt.admin.AdministrativeDomainHelper;
import wt.admin.Selector;
import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.State;
import wt.load.LoadServerHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipalReference;
import wt.org.WTRolePrincipal;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTProperties;

/**
 * This class includes the code of a loader used to remove ACL
 *
 * @author Benjamin Garnier
 *
 */
public class ACL_Manager {

    public static final boolean VERBOSE;

    static {
        try {
            WTProperties properties = WTProperties.getLocalProperties();
            VERBOSE = properties.getProperty("wt.part.load.verbose", false);
        }
        catch (Throwable t)
        {
            System.err.println("Error initializing " + ACL_Manager.class.getName ());
            t.printStackTrace(System.err);
            throw new ExceptionInInitializerError(t);
        }
    }

    /**
     * Delete an access control policy rule.
     *
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     *
     * @param nv
     *            Name/value pairs of meta data to set on the access control
     *            rule
     * @param cmd_line
     *            Command line argument that can be substituted into the load
     *            data
     * @param return_objects
     *            Object(s) created by this method used by
     *            <code>wt.load.StandardLoadService</code> for user feedback
     *            messages.
     **/

    @SuppressWarnings("rawtypes")
    public static boolean deleteAccessControlRule(Hashtable nv, Hashtable cmd_line, Vector return_objects) {

        boolean isDeleted = true;

        String containerPath = LoadServerHelper.getValue("containerPath", nv, cmd_line, LoadServerHelper.REQUIRED);
        if (containerPath == null)
            return false;

        String domainPath = LoadServerHelper.getValue("domain", nv, cmd_line, LoadServerHelper.REQUIRED);
        if (domainPath == null)
            return false;

        String typeId = LoadServerHelper.getValue("typeId", nv, cmd_line, LoadServerHelper.REQUIRED);
        if (typeId == null)
            return false;

        String principal = LoadServerHelper.getValue("principal", nv, cmd_line, LoadServerHelper.REQUIRED);
        if (principal == null)
            return false;

        String statename = LoadServerHelper.getValue("state", nv, cmd_line, LoadServerHelper.NOT_REQUIRED);

        try {
            if (VERBOSE) System.out.println("----- Delete Rule <" + domainPath + "," + typeId + "," + principal + "," + statename + ">");

            String persisted_type = null;
            String external_type_id = TypedUtility.getExternalTypeIdentifier(typeId);
            if (external_type_id == null) {
                // type id is not a persisted type, see if it is a valid
                // external or logical type ID
                persisted_type = TypedUtility.getPersistedType(typeId);
            } else if (typeId.equals(TypedUtility.getPersistedType(external_type_id))) {
                // type id is a persisted type and type is not deleted
                persisted_type = typeId;
            }
            if (persisted_type == null) {
                LoadServerHelper.printMessage(typeId + " type is invalid");
                return false;
            }


            WTContainerRef containerRef = WTContainerHelper.service.getByPath(containerPath);

            deleteAccessControl(statename, principal, domainPath, typeId, persisted_type, return_objects, containerRef.getContainer());

        } catch (WTException e) {

            LoadServerHelper.printMessage(e.getLocalizedMessage());
            e.printStackTrace();
            isDeleted = false;
        }

        return isDeleted;
    }


    @SuppressWarnings("unchecked")
    public static void deleteAccessControl(String statename, String principal, String domainPath, String typeId, String persisted_type, Vector return_objects, WTContainer cont) throws WTException {
        WTContainerRef containerRef = WTContainerRef.newWTContainerRef(cont);

        AdministrativeDomain admDomain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);

        if (VERBOSE) System.out.println("Checking : " + cont.getName());

        if (admDomain == null) {
            LoadServerHelper.printMessage("Domain \"" + domainPath + "\" not found.");
            return;
        }

        AdminDomainRef domainRef = AdminDomainRef.newAdminDomainRef(admDomain);

        // Get the context associated with the domain, to use when searching for
        // principals
        boolean oldEnforce = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            containerRef = WTContainerHelper.getContainerRef((AdministrativeDomain) domainRef.getObject());
        } finally {
            SessionServerHelper.manager.setAccessEnforced(oldEnforce);
        }

        State lifecycleState;
        if ((statename != null) && (statename.equals("ALL")))
            lifecycleState = null;
        else
            lifecycleState = State.toState(statename);

        WTPrincipalReference principalRef = null;
        if (principal.equals("OWNER")) {
            principalRef = WTPrincipalReference.OWNER;
        } else if (principal.equals("ALL")) {
            principalRef = WTPrincipalReference.ALL;
        } else {

            principalRef = OrganizationServicesHelper.manager.getPrincipalReference(principal, (String) null);
            try {
                if (principalRef == null) {
                    WTRolePrincipal theRolePrincipal = OrganizationServicesHelper.manager.getRolePrincipal(principal, containerRef, false, null);

                    if (theRolePrincipal == null) {
                        throw new wt.org.AmbiguousPrincipalException(null, "wt.org.orgResource", wt.org.orgResource.MULTI_DB_HIT, new Object[] { principal });
                    }

                    principalRef = WTPrincipalReference.newWTPrincipalReference(theRolePrincipal);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (principalRef == null) {
                LoadServerHelper.printMessage("Principal \"" + principal + "\" not found.");
            }
        }

        // See if the rule already exists
        AccessSelector selector = AccessSelector.newAccessSelector(domainRef, persisted_type, (lifecycleState == null ? Selector.ALL_STATES : statename));
        AccessPolicyRule rule = AccessControlHelper.manager.getAccessPolicyRule(selector);

        boolean ruleFound = false;

        if (rule != null) {
            // See if permissions exist for the principal
            Enumeration<?> entries = AccessControlHelper.manager.getEntries(rule);
			/*
			 * System.out.println("rule ="+rule);
			 * System.out.println("Entries ="+entries);
			 */
            while (entries.hasMoreElements()) {
                WTAclEntry entry = (WTAclEntry) entries.nextElement();
                if (VERBOSE) System.out.println("WTAclEntry ="+entry);
                if (entry.getPrincipalReference().equals(principalRef)) {
                    ruleFound = true;
                    if (VERBOSE) System.out.println("			Access Rule found:" + entry);
                }
            }
        }

        if (ruleFound) {

            AccessControlHelper.manager.deleteAccessControlRule(domainRef, typeId, lifecycleState, principalRef, false);

            if (VERBOSE) LoadServerHelper.printMessage("=====>Rule  deleted <" + domainPath + "," + typeId + "," + principal + "," + statename + ">");
        }

        String msg = "Rule =" + domainPath + "," + typeId + "," + principal + ",";
        return_objects.addElement(msg);
    }


    /**
     * Deletes all acls for the given domain and the container.
     *
     * @param nv
     *            Name/value pairs of meta data to set on the access control
     *            rule
     * @param cmd_line
     *            Command line argument that can be substituted into the load
     *            data
     * @param return_objects
     *            Object(s) created by this method used by
     *            <code>wt.load.StandardLoadService</code> for user feedback
     *            messages.
     * @return boolean
     */
    public static boolean deleteAllAccessControlRule(Hashtable nv, Hashtable cmd_line, Vector return_objects) {

        boolean isDeleted = true;

        String containerPath = LoadServerHelper.getValue("containerPath", nv, cmd_line, LoadServerHelper.REQUIRED);
        if (containerPath == null)
            return false;

        String domainPath = LoadServerHelper.getValue("domain", nv, cmd_line, LoadServerHelper.REQUIRED);
        if (domainPath == null)
            return false;

        try {
            if (VERBOSE) System.out.println("----- Delete all Rule <" + containerPath + "," + domainPath + ">");

            WTContainerRef containerRef = WTContainerHelper.service.getByPath(containerPath);

            AdministrativeDomain admDomain = AdministrativeDomainHelper.manager.getDomain(domainPath, containerRef);

            if (VERBOSE) System.out.println("Checking : " + containerRef.getContainer().getName());

            if (admDomain == null) {
                LoadServerHelper.printMessage("Domain \"" + domainPath + "\" not found.");
                return false;
            }
            AdminDomainRef domainRef = AdminDomainRef.newAdminDomainRef(admDomain);
            Enumeration rules=AccessControlHelper.manager.getAccessPolicyRules(domainRef);
            while(rules.hasMoreElements()){
                AccessPolicyRule   rule=(AccessPolicyRule )rules.nextElement();
                if(rule.getDomainRef().equals(domainRef)){
                    if (VERBOSE) LoadServerHelper.printMessage("=====>Rule  deleting <" + rule+ ">");
                    PersistenceHelper.manager.delete(rule);
                    if (VERBOSE) LoadServerHelper.printMessage("=====>Rule  deleted");
                }
            }

        } catch (WTException e) {

            LoadServerHelper.printMessage(e.getLocalizedMessage());
            e.printStackTrace();
            isDeleted = false;
        }

        return isDeleted;
    }


}

package com.sportmaster.wc.workitem.validators;

import com.ptc.core.htmlcomp.tableview.TableViewCriterion;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.core.ui.validation.*;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.work.NmWorkItemCommands;
import com.ptc.windchill.enterprise.work.assignmentslist.server.AssignmentsTableUtilityHelper;
import com.sportmaster.wc.org.SMAccessControlService;
import org.apache.log4j.Logger;
import wt.fc.Persistable;
import wt.fc.WTReference;
import wt.inf.container.WTContainerRef;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.projmgmt.execution.ProjectWorkItem;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.workflow.work.WorkItem;

import java.util.*;
import java.util.function.Function;

import static com.ptc.core.ui.validation.UIValidationStatus.*;

/**
 * NOTES:
 * - Created to override com.ptc.windchill.enterprise.workitem.validators.MultiReassignActionValidator
 * - All private methods were changed to default fot test purposes
 */
@SuppressWarnings("unused")
public class MultiReassignActionValidator extends DefaultUIComponentValidator {

    private static final Logger logger = Logger.getLogger(MultiReassignActionValidator.class);

    public MultiReassignActionValidator() {
    }

    public UIValidationResultSet performFullPreValidation(
            UIValidationKey key, UIValidationCriteria criteria, Locale locale
    ) throws WTException {

        logger.debug("==>Entering MultiReassignActionValidator.performFullPreValidation()  validationKey: " + key
                + ", validationCriteria: " + criteria + ", locale: " + locale);

        Function<UIValidationStatus, UIValidationResultSet> resultSetCreation = status -> {
            UIValidationResult result = UIValidationResult.newInstance(key, status, (WTReference) null);
            return UIValidationResultSet.newInstance(result);
        };

        if (criteria == null || criteria.getText() == null) {
            logger.debug(" - criteria is null or criteria.getText() returned null");
            return resultSetCreation.apply(ENABLED);
        }

        String tableId = criteria.getText().get("TABLE_ID");
        if (tableId == null) {
            logger.trace(" - tableId is null");
            return resultSetCreation.apply(ENABLED);
        }

        logger.debug("==> MultiReassignActionValidator.performFullPreValidation()  tableID is " + tableId
                + ". Action being rendered on " + "assignments table");

        Vector<TableViewCriterion> currViewCriterions = AssignmentsTableUtilityHelper.getAllCriterionsForCurrentView(tableId);
        HashSet<?> classTypes = AssignmentsTableUtilityHelper.getClassTypesFromTheCriterions(currViewCriterions);
        TableViewCriterion tCriterion = AssignmentsTableUtilityHelper.getOpenAssignmentsCriterion(currViewCriterions);
        Boolean boolAttr = AssignmentsTableUtilityHelper.getBooleanAttributeValue(tCriterion, null);

        if (hideDueToTableCriterion(tCriterion, boolAttr)) {
            logger.debug(" - hide due to tableCriterion is null or its attribute is false");
            return resultSetCreation.apply(HIDDEN);
        } else if (hideIfObjIsNotWorkItem(classTypes)) {
            logger.debug(" - hide because there are not-work-item classes in the set");
            return resultSetCreation.apply(HIDDEN);
        }

        return resultSetCreation.apply(ENABLED);
    }

    boolean hideDueToTableCriterion(TableViewCriterion tCriterion, Boolean boolAttr) {

        return tCriterion != null && !boolAttr;
    }

    boolean hideIfObjIsNotWorkItem(HashSet<?> classTypes) {

        return !classTypes.contains(WorkItem.class) && !classTypes.contains(ProjectWorkItem.class);
    }

    /*
     ***** Validation of selected work_items before reassign screen showing ******
     */

    public UIValidationResultSet validateSelectedMultiSelectAction(
            UIValidationKey key, UIValidationCriteria criteria, Locale locale
    ) throws WTException {
        boolean permitted = validateSelectedAssignments(criteria);
        return validateSelectedMultiSelectAction(key, criteria, locale, permitted);
    }

    UIValidationResultSet validateSelectedMultiSelectAction(
            UIValidationKey key, UIValidationCriteria criteria, Locale locale, boolean permitted
    ) {
        if (!permitted) {
            String msgResource = "com.ptc.netmarkets.work.workResource";
            UIValidationFeedbackMsg feedbackMsg = UIValidationFeedbackMsg.newInstance(
                    WTMessage.getLocalizedMessage(msgResource, "26", null, locale), FeedbackType.FAILURE);
            UIValidationResult result = UIValidationResult.newInstance(
                    key, DENIED, criteria.getContextObject(), Collections.singletonList(feedbackMsg));
            return UIValidationResultSet.newInstance(result);
        }

        UIValidationResult result = UIValidationResult.newInstance(key, PERMITTED, criteria.getContextObject());
        return UIValidationResultSet.newInstance(result);
    }

    boolean validateSelectedAssignments(UIValidationCriteria criteria) throws WTException {
        return validateSelectedAssignments(criteria, null, null, null);
    }

    boolean validateSelectedAssignments(
            UIValidationCriteria criteria,
            WTPrincipalReference principalRef,
            Hashtable<WorkItem, WTContainerRef> itemToContainerHTable,
            SMAccessControlService accessService
    ) throws WTException {

        List<?> selectedOIDs = criteria.getSelectedOidForPopup();
        logger.debug("==> MultiReassignActionValidator.validateSelectedAssignments number of objects selected::" + selectedOIDs.size());

        ArrayList<WTReference> list = new ArrayList<>();

        for (Object oid : selectedOIDs) {
            WTReference ref = ((NmOid) oid).getWtRef();
            Persistable obj = ref.getObject();
            if (!(obj instanceof WorkItem)) {
                logger.debug(" - Obj is not WorkItem");
                return false;
            }
            list.add(ref);
        }

        if (itemToContainerHTable == null)
            itemToContainerHTable = NmWorkItemCommands.getAllContainerReferenceForWI(list);

        if (itemToContainerHTable == null) {
            logger.debug(" - Items-To-Containers Hashtable is NULL");
            return false;
        }

        if (!areContainersTheSame(itemToContainerHTable)) {
            logger.debug(" - Different containers were found in hashtable");
            return false;
        }

        WTPrincipal principal;
        if (principalRef == null)
            principal = SessionHelper.manager.getPrincipal();
        else
            principal = principalRef.getPrincipal();

        Enumeration<WorkItem> workItems = itemToContainerHTable.keys();
        while (workItems.hasMoreElements()) {

            WorkItem item = workItems.nextElement();

            if (accessService == null)
                accessService = new SMAccessControlService(logger);
            if (accessService.isAdmin(itemToContainerHTable.get(item), principal)) {
                logger.debug("User is Admin for WorkItem's Container");
                // principal and containerRef(here) are the same for any item, so it could be break
                break;
            }

            if (principalRef == null)
                principalRef = WTPrincipalReference.newWTPrincipalReference(principal);
            WTPrincipalReference ownerRef = item.getOwnership().getOwner();
            logger.trace(" - Owner DN is: " + ownerRef.getPrincipal().getDn());
            logger.trace(" - User DN is: " + principalRef.getPrincipal().getDn());
            if (!ownerRef.equals(principalRef)
                    && !accessService.hasReassignAccess(principalRef, ownerRef)) {
                logger.debug(" -- User has NO access to this task");
                return false;
            }
        }

        return true;
    }

    /*
     * Check that all work_items refer to the same container
     */
    boolean areContainersTheSame(Hashtable<WorkItem, WTContainerRef> itemToContainerHTable) {

        Enumeration<WorkItem> workItems = itemToContainerHTable.keys();
        WTContainerRef prevContainerRef = null;
        while (workItems.hasMoreElements()) {

            WorkItem item = workItems.nextElement();
            WTContainerRef containerRef = itemToContainerHTable.get(item);

            if (prevContainerRef == null)
                prevContainerRef = containerRef;
            else if (!prevContainerRef.equals(containerRef))
                return false;
        }
        return true;
    }

}

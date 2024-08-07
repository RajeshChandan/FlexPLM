package com.sportmaster.wc.workitem.validators;

import com.ptc.core.ui.validation.*;
import com.sportmaster.wc.org.SMAccessControlService;
import org.apache.log4j.Logger;
import wt.fc.Persistable;
import wt.fc.WTReference;
import wt.org.WTPrincipalReference;
import wt.util.WTException;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

import java.util.Locale;
import java.util.function.Supplier;

import static com.ptc.core.ui.validation.UIValidationStatus.*;

/**
 * NOTES:
 * - Created to override com.ptc.windchill.enterprise.workitem.validators.WorkItemAdminActionsValidator
 * - All private methods were changed to default fot test purposes
 */
@SuppressWarnings("unused")
public class WorkItemAdminActionsValidator extends DefaultUIComponentValidator {

    private static final Logger logger = Logger.getLogger(WorkItemAdminActionsValidator.class);

    @SuppressWarnings("unused")
    public WorkItemAdminActionsValidator() { }

    public UIValidationResultSet performFullPreValidation(
            UIValidationKey key, UIValidationCriteria criteria, Locale locale) {

        logger.debug("==>Entering WorkItemReassignValidator.performFullPreValidation()  validationKey " + key
                + " validationCriteria " + criteria + " locale " + locale);

        try {

            return UIValidationResultSet.newInstance(getValidationResult(key, criteria));

        } catch (Exception e) {//only WTException expected, but watch some time for sure

            if(!(e instanceof WTException)){
                logger.error("!!! UNEXPECTED TYPE OF EXCEPTION !!!");
            }
            logger.debug("Problem validating action in WorkItemReassignValidator.getValidationResult()  " +
                    "validationKey = " + key + " validationCriteria = " + criteria);
            e.printStackTrace();
            logger.error(e);
        }

        UIValidationResult result = UIValidationResult.newInstance(key, HIDDEN, (WTReference) null);
        return UIValidationResultSet.newInstance(result);
    }


    private UIValidationResult getValidationResult(UIValidationKey key, UIValidationCriteria criteria) throws WTException {

            return getValidationResult(key, criteria, new SMAccessControlService(logger));
    }

    /*
     * Parameter (SMAccessControlService)accessService is needed for testing purposes
     */
    UIValidationResult getValidationResult(UIValidationKey key, UIValidationCriteria criteria, SMAccessControlService accessService) throws WTException {

        WTPrincipalReference principalRef = criteria.getUser();
        WTReference contextObjRef = criteria.getContextObject();
        Supplier<UIValidationResult> hiddenResultSupplier
                = () -> UIValidationResult.newInstance(key, HIDDEN, contextObjRef);
        logger.debug("==>Entering WorkItemReassignValidator.getValidationResult()"
                + " currPrincipalRef " + principalRef + " currContextRef " + contextObjRef);
        Persistable contextObj = contextObjRef.getObject();

        if (!(contextObj instanceof WorkItem))
            return hiddenResultSupplier.get();

        WorkItem workItem = (WorkItem) contextObj;
        if (workItem.isComplete())
            return hiddenResultSupplier.get();

        logger.trace(" - check workflow-assignment-state");
        if (hideDueToWorkItemState(key, workItem)) return hiddenResultSupplier.get();

        if (accessService.isAdmin(workItem, principalRef)) {
            logger.debug(" - User is Admin");
            return UIValidationResult.newInstance(key, ENABLED, contextObjRef);
        } else if (!"updateDeadline".equals(key.getComponentID())) {
            WTPrincipalReference ownerRef = workItem.getOwnership().getOwner();
            logger.trace(" - Owner DN is: " + ownerRef.getPrincipal().getDn());
            logger.trace(" - User DN is: " + principalRef.getPrincipal().getDn());
            if (principalRef.equals(ownerRef)
                    || accessService.hasReassignAccess(principalRef, ownerRef)) {
                logger.debug(" -- User has access");
               return UIValidationResult.newInstance(key, ENABLED, contextObjRef);
            }
        }

        return hiddenResultSupplier.get();
    }

    boolean hideDueToWorkItemState(UIValidationKey key, WorkItem workItem) {
        WfAssignmentState wfAssignmentState;
        if ("accept".equals(key.getComponentID())) {
            wfAssignmentState = workItem.getStatus();
            if (wfAssignmentState.equals(WfAssignmentState.ACCEPTED)) {
                logger.debug(" - Element was hidden because of assignment state: key is accept and state is ACCEPTED");
                return true;
            }
        } else if ("unaccept".equals(key.getComponentID())) {
            wfAssignmentState = workItem.getStatus();
            if (wfAssignmentState.equals(WfAssignmentState.POTENTIAL)) {
                logger.debug(" - Element was hidden because of assignment state: key is unaccept and state is POTENTIAL");
                return true;
            }
        }
        return false;
    }

}

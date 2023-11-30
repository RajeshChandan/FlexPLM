/**
 * 
 */
package com.hbi.wc.calendar;

import wt.fc.WTObject;
import wt.fc.ObjectReference;
import wt.fc.PersistenceServerHelper;
import wt.workflow.engine.WfActivity;
import wt.util.WTException;
import java.sql.Timestamp;

import com.lcs.wc.calendar.LCSCalendar;
import com.lcs.wc.calendar.LCSCalendarTask;
import com.lcs.wc.calendar.LCSCalendarQuery;

/**
*  Author - Abhishek Sinha, PTC
*/
public class DeriveDeadlineFromCalendar extends WfActivity{
	private WTObject PBO;
	private String taskName;
	private int days;
	
	public void setDeadline(WTObject PBO, ObjectReference ref, String taskName) throws WTException {
		setDeadline(PBO, ref, taskName, 0);
	}
	
	public void setDeadline(WTObject PBO, ObjectReference ref, String taskName, int days) throws WTException {
		wt.workflow.engine.WfActivity activity = (wt.workflow.engine.WfActivity) ref.getObject();
		this.PBO = PBO;
		this.taskName = taskName;
		this.days = days;
		this.setTemplate(activity.getTemplate());
		this.setContext(activity.getContext());
		activity.setDeadline(findTargetOrEndDate(days));
		PersistenceServerHelper.manager.update(activity);
		
	}
	
	public Timestamp findTargetOrEndDate(int days) throws WTException {

		LCSCalendar calendar = null;				
		WTObject wtObject = (wt.fc.WTObject) PBO;
		
		calendar = new LCSCalendarQuery().findByOwner(wtObject);
		if (calendar == null) {
			System.out.println ("CLASS: DeriveDeadlineFromCalendar - No calendar found for owner " +wtObject);
			return null;
		}
		
		LCSCalendarTask task = LCSCalendarQuery.getTask(calendar, taskName);
		if (task == null) {
			System.out.println ("CLASS: DeriveDeadlineFromCalendar - No calendar task " +taskName+ " found for calendar " +calendar);
			return null;
		}
		
		Timestamp targetDate = task.getTargetDate();
		Timestamp endDate = task.getEstEndDate();	
		if (targetDate != null) {
			return calculateDeadline(targetDate, days);
			//return targetDate;
		} else if (endDate != null) {
			return calculateDeadline(endDate, days);
			//return endDate;
		} else {
			return null;
		}
	}
	
	public Timestamp calculateDeadline(Timestamp tStamp, int duration) throws WTException {
		//logic to add/subtract days from calendar milestone
		if (duration == 0) {
			return tStamp;
		} else {
 			long timestamp = tStamp.getTime();
  			timestamp = timestamp + (duration* 24l * 60l * 60l * 1000l);
 			return new Timestamp (timestamp);
		}
	}

	@Override
	public String getConceptualClassname() {
		// TODO Auto-generated method stub
		return null;
	}
	


}
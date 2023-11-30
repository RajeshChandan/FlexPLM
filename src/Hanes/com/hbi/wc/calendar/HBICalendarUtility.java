package com.hbi.wc.calendar;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.calendar.LCSCalendar;
import com.lcs.wc.calendar.LCSCalendarQuery;
import com.lcs.wc.calendar.LCSCalendarTask;
import com.lcs.wc.calendar.LCSCalendarTaskLogic;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;

/**
 * The Class HBICalendarUtility.
 */
public class HBICalendarUtility implements RemoteAccess{

	/** The Constant TASK_FINALPACK_HANDOFF. */
	public static final String	TASK_FINALPACK_HANDOFF	=	LCSProperties.get("com.hbi.calendar.task.finalpackhandsoff", "Final Packaging Hand-Off");

	/** The Constant TASK_DESIGN_HANDOFF. */
	public static final String  TASK_DESIGN_HANDOFF		=	LCSProperties.get("com.hbi.calendar.task.designhandoff", "Design Hand-Off");

	/** The Constant TASK_DEVELOPMENT_TECHPACK_ISSUED. */
	public static final String  TASK_DEVELOPMENT_TECHPACK_ISSUED = LCSProperties.get("com.hbi.calendar.task.developmenttechpackissued", "Development Techpack Issued");

	/** The Constant TASK_FINAL_COLORWAY_HANDOFF. */
	public static final String  TASK_FINAL_COLORWAY_HANDOFF = LCSProperties.get("com.hbi.calendar.task.finalcolorwayhandoff", "Final Colorway Hand-Off");

	/** The Constant TASK_BLUE_SEAL. */
	public static final String  TASK_BLUE_SEAL = LCSProperties.get("com.hbi.calendar.task.blueseal", "Blue Seal");

	/** The Constant TASK_GREEN_SEAL. */
	public static final String  TASK_GREEN_SEAL = LCSProperties.get("com.hbi.calendar.task.greenseal", "Green Seal"); 

	/** The Constant TASK_RED_SEAL. */
	public static final String  TASK_RED_SEAL = LCSProperties.get("com.hbi.calendar.task.redseal", "Red Seal");	

	/** The Constant TASK_FINAL_PACKAGING_HANDOFF. */
	public static final String  TASK_FINAL_PACKAGING_HANDOFF = LCSProperties.get("com.hbi.calendar.task.finalpackaginghandoff", "Final Packaging Hand-Off");	

	/** The Constant TASK_SC_SYSTEMS_LOADED. */
	public static final String  TASK_SC_SYSTEMS_LOADED =  LCSProperties.get("com.hbi.calendar.task.scsystemsloaded", "SC Systems Loaded");

	/** The Constant TASK_EX_FACTORY. */
	public static final String  TASK_EX_FACTORY =  LCSProperties.get("com.hbi.calendar.task.exfactory", "Ex Factory");	

	/** The Constant TASK_IN_DC. */
	public static final String  TASK_IN_DC =  LCSProperties.get("com.hbi.calendar.task.indc", "In DC");	

	/** The Constant TASK_LINESHEET. */
	public static final String  TASK_LINESHEET = LCSProperties.get("com.hbi.calendar.task.linesheet","Linesheet");

	/** The Constant TASK_COLOR_PALETTE_INITIATION. */
	public static final String  TASK_COLOR_PALETTE_INITIATION = LCSProperties.get("com.hbi.calendar.task.colorpaletteinitiation","Color Palette Initiation");

	/** The Constant TASK_INTERNAL_LINE_REVIEW. */
	public static final String  TASK_INTERNAL_LINE_REVIEW = LCSProperties.get("com.hbi.calendar.task.internallinereview","Internal Line Review");

	/** The Constant TASK_PRINT_PATTERN_HAND_OFF. */
	public static final String  TASK_PRINT_PATTERN_HAND_OFF = LCSProperties.get("com.hbi.calendar.task.printpatternhandoff","Print & Pattern Hand-Off");

	/** The Constant TASK_FINAL_COLOR_PALETTE. */
	public static final String  TASK_FINAL_COLOR_PALETTE = LCSProperties.get("com.hbi.calendar.task.finalcolorpalette","Final Color Palette");

	/** The Constant TASK_PACKAGING_MECHANICAL_HAND_OFF. */
	public static final String  TASK_PACKAGING_MECHANICAL_HAND_OFF = LCSProperties.get("com.hbi.calendar.task.packagingmechanicalhandoff","Packaging Mechanical Hand-Off");	

	/** The Constant TASK_GARMENT_SPEC_COMPLETE. */
	public static final String  TASK_GARMENT_SPEC_COMPLETE = LCSProperties.get("com.hbi.calendar.task.garmentspeccomplete","Garment Spec Complete");

	/** The Constant TASK_SELLING_STYLE_SPEC_COMPLETE. */
	public static final String  TASK_SELLING_STYLE_SPEC_COMPLETE = LCSProperties.get("com.hbi.calendar.task.sellingstylespeccomplete","Selling Style Spec Complete");

	/** The Constant TASK_PO_WORK_ORDER_ISSUED. */
	public static final String  TASK_PO_WORK_ORDER_ISSUED = LCSProperties.get("com.hbi.calendar.task.poworkorderissued","PO/Work Order Issued");

	/** The Constant TASKS. */
	public static final String  TASKS = LCSProperties.get("com.hbi.calendar.tasks.setdates");

	/** The Constant TASKS_WITH_NO_LINK. */
	public static final String  TASKS_WITH_NO_LINK = LCSProperties.get("com.hbi.calendar.tasks.setdates.nolink");	
	
	/** The Constant TASK_FINAL_COLORWAY_HANDOFF_COMPLETE. */
	public static final String	TASK_FINAL_COLORWAY_HANDOFF_COMPLETE="Final Colorway Hand-Off-Complete";
	
	/** The Constant TASK_PRE_ROAD_MAP_MEETING. */
	public static final String  TASK_PRE_ROAD_MAP_MEETING = LCSProperties.get("com.hbi.calendar.task.preroadmap", "Pre Roadmap Meeting");

	/**
	 * Method used to validate and Start Calendar Task.
	 *
	 * @param wtObject the wt object
	 * @param taskName the task name
	 * @throws WTException the wT exception
	 */
	public static void startTask(WTObject wtObject, String taskName) throws WTException{

		LCSCalendar 	calendar 	= LCSCalendarQuery.findByOwner(wtObject);
		if(calendar != null){
			LCSCalendarTask	calTask		= LCSCalendarQuery.getTask(calendar, taskName);
			if(calTask != null){
				new LCSCalendarTaskLogic().startTask(wtObject, taskName);
			}
		}
	}

	/**
	 * Method used to validate and End calendar task.
	 *
	 * @param wtObject the wt object
	 * @param taskName the task name
	 * @throws WTException the wT exception
	 */
	public static void endTask(WTObject wtObject, String taskName) throws WTException{

		LCSCalendar 	calendar 	= LCSCalendarQuery.findByOwner(wtObject);
		if(calendar != null){
			LCSCalendarTask	calTask		= LCSCalendarQuery.getTask(calendar, taskName);
			if(calTask != null){
				new LCSCalendarTaskLogic().endTask(wtObject, taskName);
			}
		}
	}

	/**
	 * Method used to set the Workflow activity deadline based in calendar task target or end date
	 * and Start the Calendar Task.
	 *
	 * @param wtObject the wt object
	 * @param ref the ref
	 * @param taskName the task name
	 * @param days the days
	 * @throws WTException the wT exception
	 */
	public static void setWfActDeadLineAndStartTask(WTObject	wtObject, ObjectReference ref, String taskName, int days) throws WTException{

		LCSCalendar 	calendar 	= LCSCalendarQuery.findByOwner(wtObject);
		if(calendar != null){
			LCSCalendarTask	calTask	= LCSCalendarQuery.getTask(calendar, taskName);
			if(calTask != null){
				//Set the Workflow activity Deadline
				HBICalendarUtility.setWfActDeadLine(wtObject, ref, taskName, days);
				//Start the Task
				new LCSCalendarTaskLogic().startTask(wtObject, taskName);
			}
		}		
	}

	/**
	 * Method used to set the Workflow activity deadline based in calendar task target or end date.
	 *
	 * @param wtObject the wt object
	 * @param ref the ref
	 * @param taskName the task name
	 * @param days the days
	 * @throws WTException the wT exception
	 */
	public static void setWfActDeadLine(WTObject wtObject, ObjectReference ref, String taskName, int days) throws WTException{
		//Set the Workflow Activity deadline based on Task target or end date
		DeriveDeadlineFromCalendar deriveDeadlineLogic = new DeriveDeadlineFromCalendar();
		deriveDeadlineLogic.setDeadline(wtObject, ref, taskName, days);		
	}

	/**
	 * Gets the task.
	 *
	 * @param calendar the calendar
	 * @param taskKeyName the task key name
	 * @return the task
	 * @throws WTException the wT exception
	 */
	public static LCSCalendarTask getTask(LCSCalendar calendar, String taskKeyName) throws WTException{

		Collection<?>	tasksColl 	=	LCSCalendarQuery.findTasks(calendar);
		Iterator<?>		tasksIter	=	tasksColl.iterator();
		LCSCalendarTask	calTask		=	null;
		while(tasksIter.hasNext()){
			LCSCalendarTask	task	=	(LCSCalendarTask)tasksIter.next();
			String			key		=	(String)task.getValue("hbTaskKeyName");
			if(FormatHelper.hasContent(key) && key.equalsIgnoreCase(taskKeyName)){
				calTask	=	task;
			}
		}
		return calTask;
	}

	/**
	 * Method used to set the Start and End Dates for the activity Design Hand-off and Development TechPack Issued.
	 *
	 * @param wtObject the wt object
	 * @param ref the ref
	 * @param taskName the task name
	 * @param days the days
	 * @throws WTException the wT exception
	 * @throws WTPropertyVetoException the wT property veto exception
	 * @author Kiran Narala - ITC
	 */
	public static void setStartEndDates(WTObject	wtObject, ObjectReference ref, String taskName, int days) throws WTException, WTPropertyVetoException{		
		LCSCalendar 	calendar 	= LCSCalendarQuery.findByOwner(wtObject);
		if(calendar != null){
			LCSCalendarTask	calTask	= LCSCalendarQuery.getTask(calendar, taskName);
			if(calTask != null){				
				if (days != 0) {					
					Calendar cal = Calendar.getInstance();
					cal.setTime (new Date()); // convert your date to Calendar object
					cal.add(Calendar.DATE, days);
					Date date = cal.getTime();
					long timestamp = date.getTime();
					Timestamp difference = new Timestamp(timestamp);
					calTask.setStartDate(difference);
					calTask.setEndDate(difference);
					new LCSCalendarTaskLogic().saveCalendarTask(calTask);
				}
			}
		}
	}
	/**
	 * Method used to Check whether the Product is Linked or Not.
	 *
	 * @param wtObject the wt object
	 * @param ref the ref
	 * @throws WTException the wT exception
	 * @throws WTPropertyVetoException the wT property veto exception
	 * @author Kiran Narala - ITC
	 */
	public static boolean isProductLinked(WTObject	wtObject, ObjectReference ref) throws WTException, WTPropertyVetoException{
		LCSProduct product = (LCSProduct)wtObject;	
		LCSProduct linkedProduct = null;
		boolean isProductAssociated = false;
		LCSCalendar calendar = LCSCalendarQuery.findByOwner(wtObject);
		Collection childLinks = LCSProductQuery.getLinkedProducts(FormatHelper.getObjectId((WTObject) product.getMaster()), true, false);
		if(childLinks != null && childLinks.size()>0){
			isProductAssociated = true;
		}
		else{
			isProductAssociated = false;

		}
		return isProductAssociated;
	
	}
	/**
	 * Method used to set the Start and End Dates for the activity Design Hand-off and Development TechPack Issued.
	 *
	 * @param wtObject the wt object
	 * @param ref the ref
	 * @throws WTException the wT exception
	 * @throws WTPropertyVetoException the wT property veto exception
	 * @author Kiran Narala - ITC
	 */
	public static void setEndDatesForCalendarTasks(WTObject	wtObject, ObjectReference ref) throws WTException, WTPropertyVetoException{	
		LCSProduct product = (LCSProduct)wtObject;	
		LCSProduct linkedProduct = null;
		LCSCalendar calendar = LCSCalendarQuery.findByOwner(wtObject);
		Collection childLinks = LCSProductQuery.getLinkedProducts(FormatHelper.getObjectId((WTObject) product.getMaster()), true, false);
		if(childLinks != null && childLinks.size()>0){
			Iterator i = childLinks.iterator();
			FlexObject fobj = null;
			while(i.hasNext()) {
				fobj = (FlexObject)i.next(); 
				if("Garment-Selling".equalsIgnoreCase((String)fobj.get("PRODUCTTOPRODUCTLINK.LINKTYPE"))) {
					String linkedProductId = (String)fobj.get("CHILDPRODUCT.IDA2A2"); 
					linkedProduct = (LCSProduct)LCSQuery.findObjectById("OR:com.lcs.wc.product.LCSProduct:"+linkedProductId); 
					if(linkedProduct != null ){		                    	 
						break;           	              
					}
				}
			}
			Collection milestoneTaskDates=MOAHelper.getMOACollection(TASKS);
			Map milestoneTaskMappedDates=new LinkedHashMap();
			boolean partialValuesExist=false;
			Iterator itr =milestoneTaskDates.iterator();
			while(itr.hasNext()){
				String mappedTask =(String) itr.next();
				StringTokenizer mappedTaskAttkey=new StringTokenizer(mappedTask,":");
				if(mappedTaskAttkey.hasMoreTokens()){
					String task=(String) mappedTaskAttkey.nextToken();
					if(mappedTaskAttkey.hasMoreTokens()){
						String taskDateAttKey = (String)mappedTaskAttkey.nextToken();
						if(linkedProduct.getValue(taskDateAttKey)!=null){
							milestoneTaskMappedDates.put(task, (Date) linkedProduct.getValue(taskDateAttKey));
						}else{
							partialValuesExist=true; 
							milestoneTaskMappedDates.put(task,null);
						}
					}
				}
			}			
			if(!partialValuesExist){
				if(calendar != null){
					setDatesFromMappedAttributes(linkedProduct, calendar,milestoneTaskMappedDates);
				}
			}else{
				if(calendar != null){
					setPartialDatesFromMappedAttributes(linkedProduct, calendar,milestoneTaskMappedDates);
				}
			}

		}
		else{				
			if(calendar != null){
				//Commented the code after the Changes Requested by the Customer 

				/*
				List<String> taskList = Arrays.asList(TASKS_WITH_NO_LINK.split(","));    	
				Iterator<String> taskListIterator = taskList.iterator();
				while(taskListIterator.hasNext()) {
					LCSCalendarTask	calTask	= LCSCalendarQuery.getTask(calendar, taskListIterator.next());
					if(calTask != null){
						Calendar cal = Calendar.getInstance();
						cal.setTime (new Date());
						Date date = cal.getTime();
						long timestamp = date.getTime();
						Timestamp difference = new Timestamp(timestamp);
						calTask.setStartDate(difference);
						calTask.setEndDate(difference);
						new LCSCalendarTaskLogic().saveCalendarTask(calTask);
					}			              	               
				}
			*/}

		}
	}


	/**
	 * Sets the dates from mapped attributes.
	 *
	 * @param linkedProduct the linked product
	 * @param calendar the calendar
	 * @param milestoneTaskMappedDates the milestone task mapped dates
	 * @throws WTException the wT exception
	 * @throws WTPropertyVetoException the wT property veto exception
	 * @author Kiran Narala - ITC
	 */
	private static void setDatesFromMappedAttributes(LCSProduct linkedProduct,LCSCalendar calendar, Map milestoneTaskMappedDates)throws WTException, WTPropertyVetoException {
		Set keys=milestoneTaskMappedDates.keySet();
		Iterator it =keys.iterator();
		boolean flag = false;
		while(it.hasNext()) {
			String taskName=(String) it.next();
			LCSCalendarTask	calTask	;
			if(TASK_FINAL_COLORWAY_HANDOFF_COMPLETE.equalsIgnoreCase(taskName)){
				calTask= LCSCalendarQuery.getTask(calendar,TASK_FINAL_COLORWAY_HANDOFF);
			}else{
				calTask= LCSCalendarQuery.getTask(calendar,taskName);
			}			
			setDatesForValidateTasks(linkedProduct, milestoneTaskMappedDates,taskName, calTask,flag);			              	               
		}
	}


	/**
	 * Sets the partial dates from mapped attributes.
	 *
	 * @param linkedProduct the linked product
	 * @param calendar the calendar
	 * @param milestoneTaskMappedDates the milestone task mapped dates
	 * @throws WTException the wT exception
	 * @throws WTPropertyVetoException the wT property veto exception
	 * @author Kiran Narala - ITC
	 */
	private static void setPartialDatesFromMappedAttributes(LCSProduct linkedProduct,LCSCalendar calendar, Map milestoneTaskMappedDates)throws WTException, WTPropertyVetoException {
		Iterator it =milestoneTaskMappedDates.entrySet().iterator();
		boolean flagPartial = false;
		boolean flagPrecedor = false;
		while(it.hasNext()) {
			Entry taskEntry=(Entry) it.next();
			String taskName=(String) taskEntry.getKey();
			Date endDate=null;
			if(milestoneTaskMappedDates.containsKey(taskName)){
				endDate=(Date) taskEntry.getValue();
			}							
			if(endDate==null){
				LCSCalendarTask	calTask	;
				if(TASK_FINAL_COLORWAY_HANDOFF_COMPLETE.equalsIgnoreCase(taskName)){
					calTask= LCSCalendarQuery.getTask(calendar,TASK_FINAL_COLORWAY_HANDOFF);
					flagPrecedor = true;
				}else{
					calTask= LCSCalendarQuery.getTask(calendar,taskName);
				}
				getPredecessorEndDate(calTask,calendar,flagPrecedor);
			}else{			 
				LCSCalendarTask	calTask	;
				flagPartial = true;
				if(TASK_FINAL_COLORWAY_HANDOFF_COMPLETE.equalsIgnoreCase(taskName)){
					calTask= LCSCalendarQuery.getTask(calendar,TASK_FINAL_COLORWAY_HANDOFF);
					flagPrecedor = true;
				}else{
					calTask= LCSCalendarQuery.getTask(calendar,taskName);
				}
				 
				setDatesForValidateTasks(linkedProduct,	milestoneTaskMappedDates, taskName, calTask,flagPartial);

			}
		}
	}
	/**
	 * Gets the getPredecessorEndDate dates from Predecessor Tasks.
	 *
	 * @param linkedProduct the linked product
	 * @param calendar the calendar
	 * @param milestoneTaskMappedDates the milestone task mapped dates
	 * @throws WTException the wT exception
	 * @throws WTPropertyVetoException the wT property veto exception
	 * @author Kiran Narala - ITC
	 */
	private static void getPredecessorEndDate(LCSCalendarTask calTask, LCSCalendar calendar, boolean flag)throws WTPropertyVetoException, WTException {
		if(TASK_FINAL_COLORWAY_HANDOFF.equalsIgnoreCase(calTask.getName())&& flag){
			LCSCalendarTask tempCalTask= LCSCalendarQuery.getTask(calendar,TASK_FINAL_COLORWAY_HANDOFF);
			LCSCalendarTask calMilestoneTaskTmp = tempCalTask.getPrecedent();
			Date precedentEndDate = calMilestoneTaskTmp.getEndDate();
			if(precedentEndDate == null){			 
				getPredecessorEndDate(calMilestoneTaskTmp,calendar,false);
				precedentEndDate = calMilestoneTaskTmp.getEndDate();
			}
			tempCalTask.setEndDate(new Timestamp(precedentEndDate.getTime()));
			new LCSCalendarTaskLogic().saveCalendarTask(tempCalTask); 			
		}else if(TASK_FINAL_COLORWAY_HANDOFF.equalsIgnoreCase(calTask.getName())&&!flag){
			LCSCalendarTask tempCalTask= LCSCalendarQuery.getTask(calendar,TASK_FINAL_COLORWAY_HANDOFF);
			LCSCalendarTask calMilestoneTaskTmp = tempCalTask.getPrecedent();
			Date precedentEndDate = calMilestoneTaskTmp.getEndDate();
			if(precedentEndDate == null){						 
				getPredecessorEndDate(calMilestoneTaskTmp,calendar,false);
				precedentEndDate = calMilestoneTaskTmp.getEndDate();
			}
			tempCalTask.setStartDate(new Timestamp(precedentEndDate.getTime()));
			new LCSCalendarTaskLogic().saveCalendarTask(tempCalTask); 
		}else{
			LCSCalendarTask calMilestoneTaskTmp = calTask.getPrecedent();
			Date precedentEndDate = calMilestoneTaskTmp.getEndDate();
			if(precedentEndDate == null){						 
				getPredecessorEndDate(calMilestoneTaskTmp,calendar,false);
				precedentEndDate = calMilestoneTaskTmp.getEndDate();
			}
			calTask.setStartDate(new Timestamp(precedentEndDate.getTime()));
			calTask.setEndDate(new Timestamp(precedentEndDate.getTime()));
			new LCSCalendarTaskLogic().saveCalendarTask(calTask);
		}

	}

	/**
	 * Sets the dates for validate tasks.
	 *
	 * @param linkedProduct the linked product
	 * @param milestoneTaskMappedDates the milestone task mapped dates
	 * @param taskName the task name
	 * @param calTask the cal task
	 * @throws WTException the wT exception
	 * @throws WTPropertyVetoException the wT property veto exception
	 * @author Kiran Narala - ITC
	 */
	private static void setDatesForValidateTasks(LCSProduct linkedProduct,Map milestoneTaskMappedDates, String taskName,LCSCalendarTask calTask, boolean flag) throws WTException,WTPropertyVetoException {
		if(calTask != null){
			Calendar cal = Calendar.getInstance();
			if(!flag){				
				String st = (String) calTask.getName();
				if(TASK_FINAL_COLORWAY_HANDOFF.equalsIgnoreCase(st)){	
					Date start = (Date)linkedProduct.getValue("hbiFinalColorwayHandOffStart");
					Date end = (Date)linkedProduct.getValue("hbiFinalColorwayHandOffCom");
						long startTimestamp = start.getTime();
						long endTimestamp = end.getTime();
						Timestamp startTS = new Timestamp(startTimestamp);
						Timestamp endTs = new Timestamp(endTimestamp);
						calTask.setStartDate(startTS);
						calTask.setEndDate(endTs);
						new LCSCalendarTaskLogic().saveCalendarTask(calTask);
				}else{
					setDatesFromAttributes(milestoneTaskMappedDates, taskName,calTask, cal);								
				}
			}else{
				if(TASK_FINAL_COLORWAY_HANDOFF_COMPLETE.equalsIgnoreCase(taskName)){
					Date end = (Date)linkedProduct.getValue("hbiFinalColorwayHandOffCom");
					long endTimestamp = end.getTime();
					Timestamp endTs = new Timestamp(endTimestamp);
					calTask.setEndDate(endTs);
					new LCSCalendarTaskLogic().saveCalendarTask(calTask);
				}else if(TASK_FINAL_COLORWAY_HANDOFF.equalsIgnoreCase(taskName)){
					    Date start = (Date)linkedProduct.getValue("hbiFinalColorwayHandOffStart");
						long startTimestamp = start.getTime();
						Timestamp startTS = new Timestamp(startTimestamp);
						calTask.setStartDate(startTS);
						new LCSCalendarTaskLogic().saveCalendarTask(calTask);
				}else{
					setDatesFromAttributes(milestoneTaskMappedDates, taskName,calTask, cal);
				}					
				
			}			
		}
	}

	/**
	 * @param milestoneTaskMappedDates
	 * @param taskName
	 * @param calTask
	 * @param cal
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @author Kiran Narala - ITC
	 */
	private static void setDatesFromAttributes(Map milestoneTaskMappedDates,
			String taskName, LCSCalendarTask calTask, Calendar cal)
			throws WTPropertyVetoException, WTException {
		cal.setTime ((Date) milestoneTaskMappedDates.get(taskName));
		Date date = cal.getTime();
		long timestamp = date.getTime();
		Timestamp difference = new Timestamp(timestamp);
		calTask.setStartDate(difference);
		calTask.setEndDate(difference);
		new LCSCalendarTaskLogic().saveCalendarTask(calTask);
	}
	
	
	/**
	 * @param milestoneTaskMappedDates
	 * @param taskName
	 * @param calTask
	 * @param cal
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @author Kiran Narala - ITC
	 */
	public static void setStartDateForGarmentSpecComplete(WTObject	wtObject, String taskName)throws WTPropertyVetoException, WTException {
		LCSCalendar 	calendar 	= LCSCalendarQuery.findByOwner(wtObject);
		if(calendar != null){
			LCSCalendarTask	calTask	= LCSCalendarQuery.getTask(calendar, taskName);
			if(calTask != null){
				LCSCalendarTask redSealTask = calTask.getPrecedent();
				Date redSealEndDate = redSealTask.getEndDate();
				if(redSealEndDate.after(new Date())){
					Calendar cal = Calendar.getInstance();
					cal.setTime (new Date()); // convert your date to Calendar object
					Date date = cal.getTime();
					long timestamp = date.getTime();
					Timestamp redSealTaskEndDate = new Timestamp(timestamp);
					calTask.setStartDate(redSealTaskEndDate);
					new LCSCalendarTaskLogic().saveCalendarTask(calTask);
				}
				else{
					long redSealEndTimestamp = redSealEndDate.getTime();
					Timestamp redSealTaskEndDate = new Timestamp(redSealEndTimestamp);
					calTask.setStartDate(redSealTaskEndDate);
					new LCSCalendarTaskLogic().saveCalendarTask(calTask);
				}
				
			}
			
		}
		
	}
	
	/**
	 * Method to Set the Dates for PreRoad MapMeeting activity for Season Workflow
	 * @param wtObject
	 * @param taskName
	 * @param days
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 * @author Kiran Narala - ITC
	 */
	public static void setStartDateForPreRoadMapMeeting(WTObject	wtObject,String taskName, int days)throws WTPropertyVetoException, WTException {		
		LCSCalendar 	calendar 	= LCSCalendarQuery.findByOwner(wtObject);
		System.out.println("calendar = "+calendar);
		System.out.println("taskName = "+taskName);
		System.out.println("days = "+days);
	
		if(calendar != null){
			LCSCalendarTask	calTask	= LCSCalendarQuery.getTask(calendar, taskName);		
			System.out.println("calTask = "+calTask);
			LCSCalendarTask	calTaskLineSheet	= LCSCalendarQuery.getTask(calendar, TASK_LINESHEET);
			System.out.println("calTaskLineSheet = "+calTaskLineSheet);
			if(days != 0){				
				Date startDateOfLineSheet = calTaskLineSheet.getEstStartDate();
				System.out.println("startDateOfLineSheet = "+startDateOfLineSheet);
				long timestamp = startDateOfLineSheet.getTime();
				Timestamp timeStampOfLineSheet = new Timestamp(timestamp);
				System.out.println("timeStampOfLineSheet = "+timeStampOfLineSheet);
				DeriveDeadlineFromCalendar deriveDeadline = new DeriveDeadlineFromCalendar();
				Timestamp difference =   deriveDeadline.calculateDeadline(timeStampOfLineSheet, days);		
				System.out.println("difference = "+difference);
				calTask.setStartDate(difference);
				new LCSCalendarTaskLogic().saveCalendarTask(calTask);
				}
		}
		
		
	}


}

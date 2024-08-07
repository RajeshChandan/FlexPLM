package com.sportmaster.tools;

import java.io.FileWriter;

import org.apache.log4j.Logger;

import wt.fc.QueryResult;
import wt.inf.container.LookupSpec;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfExportImportHandler;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.definer.WfProcessTemplateMaster;

public class ExtractWorkflow implements RemoteAccess{
	
	private static final Logger LOGGER = Logger
			.getLogger(ExtractWorkflow.class);
	
	private static final String USAGE_MSG = "Usage:\n\t Extract a single workflow using its name:"
			+ "\n\t\t windchill com.sportmaster.tools.ExtractWorkflow [-u <username> -p <password>] -l <Workflow Name> -f <Result File Path> -CONT_PATH <Source Container Path>"
			+ "\n\t\t example: windchill com.sportmaster.tools.ExtractWorkflow -u wcadmin -p wcadmin -w \"Sportmaster Object Initialization Workflow\" -f /home/ptcuser/Sportmaster_Object_Initialization_Workflow.csv -CONT_PATH /wt.inf.container.OrgContainer=ptc";
	
	private ExtractWorkflow(){}

	public static void main(String[] args) {

		RemoteMethodServer rm = RemoteMethodServer.getDefault();

		int argsLength = args.length;
		String wfName = "";
		String filePath = "";
		String contPath = "";
		boolean stopExecution = false;
		
		for (int i = 0; i < argsLength; i++) {
			switch (args[i]) {
			case "-u":
				if (i + 1 < argsLength) {
					rm.setUserName(args[i+1]);
				}
				break;
			case "-p":
				if (i + 1 < argsLength) {
					rm.setPassword(args[i+1]);
				}
				break;
			case "-usage":
				System.out.println(USAGE_MSG);
				stopExecution = true;
				break;
			case "-w":
				if (i + 1 < argsLength) {
					wfName = args[i+1];
				}
				break;
			case "-f":
				if (i + 1 < argsLength) {
					filePath = args[i+1];
				}
				break;
			case "-CONT_PATH" :
				if (i + 1 < argsLength) {
					contPath = args[i+1];
				}
				break;
			default:
				break;
			}
		}

		if(!stopExecution && !wfName.isEmpty() && !filePath.isEmpty() && !contPath.isEmpty()) {
			try {
				Class<?>[] argTypes = { String.class, String.class, String.class, String[].class };
				Object[] objArgs = { wfName, filePath, contPath, args };
				RemoteMethodServer.getDefault().invoke("doTheJob", ExtractWorkflow.class.getName(), null, argTypes, objArgs);

			} catch (Exception e) {
				LOGGER.error("An Error occured while executing the utility...", e);
			}
		} else {
			System.out.println(USAGE_MSG);
		}
		
	}
	
	public static void doTheJob(String wfName, String filePath, String contPath, String[] args) throws Exception {

		WTContainerRef containerRef = null;
		if ((contPath!=null) && !contPath.isEmpty()){
			containerRef=WTContainerHelper.service.getByPath(contPath);
		}
		
		WfProcessTemplate wf = getWfProcessTemplate(wfName, containerRef);
		
		StringBuffer buf = new StringBuffer();
		
		buf = WfExportImportHandler.exportTemplateAsStringBuffer(wf, buf);
		FileWriter file = new FileWriter(filePath);
		file.write (buf.toString ());
		file.flush ();
		file.close ();
	}
	
	public static WfProcessTemplate getWfProcessTemplate(String templateName, WTContainerRef contextRef) throws WTException {
		
		WfProcessTemplateMaster aMaster = null;

		QueryResult qr = null;
		LookupSpec ls = null;
		

		QuerySpec qs = new QuerySpec(WfProcessTemplateMaster.class);
		qs.appendWhere(new SearchCondition(WfProcessTemplateMaster.class, WfProcessTemplateMaster.NAME, SearchCondition.EQUAL, templateName), new int[] { 0 });
				
		ls = new LookupSpec(qs, contextRef);
		try {
			ls.setFirstMatchOnly(false);

			ls.setFilterOverrides(true);
		} catch (WTPropertyVetoException wpve) {
			throw new WTException(wpve);
		}
		qr = WTContainerHelper.service.lookup(ls);
		if (qr.hasMoreElements()) {
			aMaster = (WfProcessTemplateMaster) qr.nextElement();
		}
		if (aMaster == null)
			throw new WTException("No Workflow template found with name "
					+ templateName + ", context " + contextRef + ", "
					+ contextRef.getName());
		return WfDefinerHelper.service.getLatestIteration(aMaster);

	}
}

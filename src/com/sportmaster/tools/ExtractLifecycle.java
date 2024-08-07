package com.sportmaster.tools;

import java.io.FileWriter;

import org.apache.log4j.Logger;

import com.lcs.wc.util.FormatHelper;
import com.ptc.core.meta.common.RemoteWorker;
import com.ptc.core.meta.common.RemoteWorkerHandler;

import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleExportImportHandler;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.method.RemoteMethodServer;
import wt.session.SessionHelper;

public class ExtractLifecycle {

	private static final Logger LOGGER = Logger
			.getLogger(ExtractLifecycle.class);
	
	private static final String USAGE_MSG = "Usage:\n\t Extract a single lifecycle using its name:"
			+ "\n\t\t windchill com.sportmaster.tools.ExtractLifecycle [-u <username> -p <password>] -l <Lifecycle Name> -f <Result File Path> -CONT_PATH <Source Container Path>"
			+ "\n\t\t example: windchill com.sportmaster.tools.ExtractLifecycle -u wcadmin -p wcadmin -l \"Sportmaster Style Lifecycle\" -f /home/ptcuser/Sportmaster_Style_Lifecycle.csv -CONT_PATH /wt.inf.container.OrgContainer=ptc";
	
	private ExtractLifecycle(){}

	public static void main(String[] args) {

		int argsLength = args.length;
		String lcName = "";
		String filePath = "";
		String contPath = "";
		String userName = "";
		String password = "";
		boolean stopExecution = false;
		
		for (int i = 0; i < argsLength; i++) {
			switch (args[i]) {
			case "-u":
				if (i + 1 < argsLength) {
					userName = args[++i];
				}
				break;
			case "-p":
				if (i + 1 < argsLength) {
					password = args[++i];
				}
				break;
			case "-usage":
				stopExecution = true;
				break;
			case "-l":
				if (i + 1 < argsLength) {
					lcName = args[++i];
				}
				break;
			case "-f":
				if (i + 1 < argsLength) {
					filePath = args[++i];
				}
				break;
			case "-CONT_PATH" :
				if (i + 1 < argsLength) {
					contPath = args[++i];
				}
				break;
			default:
				break;
			}
		}
		
		if(FormatHelper.hasContent(userName)){
			RemoteMethodServer.getDefault().setUserName(userName);
			if(FormatHelper.hasContent(password)){
				RemoteMethodServer.getDefault().setPassword(password);
			}
		}

		if(!stopExecution && !lcName.isEmpty() && !filePath.isEmpty() && !contPath.isEmpty()) {
			try {
				SessionHelper.manager.getPrincipal();
				RemoteWorkerHandler.handleRemoteWorker(new ExtractLifecycleRemoteWorker(), args);
			} catch (Exception e) {
				LOGGER.error("An Error occured while executing the utility...", e);
			}
		} else {
			System.out.println(USAGE_MSG);
		}
		
	}
	
	public static void doTheJob(Object input) throws Exception {

		String lcName = "";
		String filePath = "";
		String contPath = "";
		
		String[] args = (String[]) input;
		for(int i = 0 ; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-l") && i + 1 < args.length) {
				lcName = args[++i];
			} else if (args[i].equalsIgnoreCase("-f") && i + 1 < args.length) {
				filePath = args[++i];
			} else if (args[i].equalsIgnoreCase("-CONT_PATH") && i + 1 < args.length) {
				contPath = args[++i];
			}
		}
		
		WTContainerRef containerRef = null;
		if ((contPath!=null) && !contPath.isEmpty()){
			containerRef=WTContainerHelper.service.getByPath(contPath);
		}
		
		LifeCycleTemplate lc = LifeCycleHelper.service.getLifeCycleTemplate(lcName, containerRef);
		
		StringBuffer buf = new StringBuffer();
		
		buf = LifeCycleExportImportHandler.exportTemplateAsStringBuffer(lc, buf);
		FileWriter file = new FileWriter(filePath);
		file.write (buf.toString ());
		file.flush ();
		file.close ();
	}

}

class ExtractLifecycleRemoteWorker extends RemoteWorker {
	static final long serialVersionUID = 1L;
	
	@Override
	public Object doWork(Object input) throws Exception {
		ExtractLifecycle.doTheJob(input);
		return null;
	}
	
}


package com.hbi.etl.mq;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.ibm.mq.*;
import com.lcs.wc.util.LCSProperties;

public class WINJAVAMQ  {

	
	/*static String logFile = LCSProperties.get("com.hbi.stg.extractors.STGLawsonExport.logFile", "C:\\lawsondata\\LawsonExport.log");
	static String  hostname = LCSProperties.get("com.hbi.etl.mq.hostname", "WSMQCLT01");
	static String  queueManager = LCSProperties.get("com.hbi.etl.mq.queueManager", "CLT.TESTD.A");
	static String  IC11queueName = LCSProperties.get("com.hbi.etl.mq.IC11queueName","IC11.LAWSON.IN");
	static String  PO25queueName = LCSProperties.get("com.hbi.etl.mq.PO25queueName", "PO25.CLT.LAWSON.IN");
	static int port = Integer.parseInt(LCSProperties.get("com.hbi.etl.mq.port", "42000"));
	static String chnlName = LCSProperties.get("com.hbi.etl.mq.chnlName","SYSTEM.ADMIN.SVRCONN");*/
	
	static String logFile = LCSProperties.get("com.hbi.stg.extractors.STGLawsonExport.logFile", "C:\\lawsondata\\LawsonExport.log");
	static String  hostname = null;
	static String  queueManager = null;
	static String  IC11queueName = null;
	static String  PO25queueName = null;
	static int port ;
	static String chnlName = null;

	
	static Properties prop = new Properties();
	static InputStream input = null;
	String host_name,qmanager,icll_queue,po25_queue,chnl;
	static{
	try {	 
		input = new FileInputStream("C:/slbs/config.properties");	 
		prop.load(input);
		hostname= prop.getProperty("HostName");
		queueManager= prop.getProperty("Queue_Manager");
		IC11queueName= prop.getProperty("IC11_Queue");
		PO25queueName= prop.getProperty("PO25_Queue");
		port  = Integer.parseInt(prop.getProperty("Port"));
		chnlName = prop.getProperty("Chnl_Name");
		System.out.println("MQ Properties "+ prop);
		
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WINJAVAMQ mqObj = new WINJAVAMQ();
		try {
			mqObj.IC11("IC11_STRING");
			mqObj.PO25("PO25_STRING");
		} catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1); 
		}

	}
	public void IC11(String IC11_data) throws MQException {


		String qManager = queueManager;
		String queue = IC11queueName;
		MQQueueManager qMgr = null;
		MQQueue myQueue = null;
		try
		{

			MQEnvironment.hostname = hostname;
			MQEnvironment.channel  = chnlName;
			MQEnvironment.port     = port;
		}
		catch (IllegalArgumentException e)
		{
			System.exit(1);
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(1);
		}


		try {

			qMgr = new MQQueueManager(qManager);

			int openOptions =
					MQC.MQOO_OUTPUT
					| MQC.MQOO_SET_IDENTITY_CONTEXT;

			myQueue = qMgr.accessQueue(queue, openOptions, null, null, null);
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options =
					MQC.MQPMO_LOGICAL_ORDER
					| MQC.MQPMO_SET_IDENTITY_CONTEXT
					| MQC.MQPMO_SYNCPOINT;
			MQMessage m = new MQMessage();
			m.format = MQC.MQFMT_STRING;
			//  String temp = "hello";
			m.writeString(IC11_data);
			myQueue.put(m, pmo);

			qMgr.commit();


		} catch (Exception e) {
			e.printStackTrace();
		}finally{

			myQueue.close();
			qMgr.disconnect();

		}

	}
	public void PO25(String PO25_data) throws MQException {

		 String qManager = queueManager;
		 String queue = PO25queueName;
		 MQQueueManager qMgr = null;
		 MQQueue myQueue = null;
		try
		{

			MQEnvironment.hostname = hostname;
			MQEnvironment.channel  = "SYSTEM.ADMIN.SVRCONN";
			MQEnvironment.port     = port;
		}
		catch (IllegalArgumentException e)
		{
			System.exit(1);
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.exit(1);
		}


		try {

			qMgr = new MQQueueManager(qManager);

			int openOptions =
					MQC.MQOO_OUTPUT
					| MQC.MQOO_SET_IDENTITY_CONTEXT;

			myQueue = qMgr.accessQueue(queue, openOptions, null, null, null);
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options =
					MQC.MQPMO_LOGICAL_ORDER
					| MQC.MQPMO_SET_IDENTITY_CONTEXT
					| MQC.MQPMO_SYNCPOINT;
			MQMessage m = new MQMessage();
			m.format = MQC.MQFMT_STRING;

			m.writeString(PO25_data);
			myQueue.put(m, pmo);

			qMgr.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{

			myQueue.close();
			qMgr.disconnect();

		}

	}

}

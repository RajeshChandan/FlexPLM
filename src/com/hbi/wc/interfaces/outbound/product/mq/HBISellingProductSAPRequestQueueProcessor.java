package com.hbi.wc.interfaces.outbound.product.mq;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


import com.ibm.mq.MQC;
//import com.ibm.mq.MQConstants;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQException;

public class HBISellingProductSAPRequestQueueProcessor
{
	static String  hostname = null;
	static String  queueManager = null;
	static String  PLMqueueName = null;
	static int port ;
	static String chnlName = null;
	static String userName = null;


	static Properties prop = new Properties();
	static InputStream input = null;
	
	static
	{
		try
		{	 
			input = new FileInputStream("C:/slbs/SellingProductSAPRequestQueue.properties");	 
			prop.load(input);
			hostname= prop.getProperty("HostName");
			queueManager= prop.getProperty("Queue_Manager");
			PLMqueueName= prop.getProperty("PLM_Queue");
			port  = Integer.parseInt(prop.getProperty("Port"));
			chnlName = prop.getProperty("Chnl_Name");
			userName = prop.getProperty("user_Name");

			System.out.println("MQ Properties111 "+ prop);
		}
		catch (IOException ex) 
		{
			ex.printStackTrace();
		}
		finally 
		{
			if (input != null) 
			{
				try 
				{
					input.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void plmSellingProductRequest(String PLM_Request_XML) throws MQException 
	{
		System.out.println(" <<< I am inside plmSellingProductRequest >>>"+ prop);
		String qManager = queueManager;
		String queue = PLMqueueName;
		MQQueueManager qMgr = null;
		MQQueue myQueue = null;
		try
		{
			MQEnvironment.hostname = hostname;
			MQEnvironment.channel  = chnlName;
			MQEnvironment.port     = port;
			MQEnvironment.userID   =userName;

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

		try 
		{
			qMgr = new MQQueueManager(qManager);

			int openOptions = MQC.MQOO_OUTPUT
					| MQC.MQOO_SET_IDENTITY_CONTEXT;

			myQueue = qMgr.accessQueue(queue, openOptions, null, null, null);
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options = MQC.MQPMO_LOGICAL_ORDER | MQC.MQPMO_SET_IDENTITY_CONTEXT | MQC.MQPMO_SYNCPOINT;
			MQMessage m = new MQMessage();
			m.format = MQC.MQFMT_STRING;
			m.writeString(PLM_Request_XML);
			myQueue.put(m, pmo);

			qMgr.commit();


		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if((myQueue != null) && (qMgr != null))
			{
				myQueue.close();
				qMgr.disconnect();
			}
		}
	}	
}

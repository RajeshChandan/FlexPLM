package com.hbi.wc.interfaces.inbound.webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import com.ibm.mq.MQException;

import java.lang.Exception;
@WebService
@SOAPBinding(style = Style.RPC)
public interface HBISPAutomationSAPFeedbackService {

	@WebMethod public String setFeedbackMessage(String feedbackMessage) throws Exception;
	
}
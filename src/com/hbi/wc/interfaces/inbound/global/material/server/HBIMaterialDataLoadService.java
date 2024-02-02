package com.hbi.wc.interfaces.inbound.global.material.server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/**
 * HBIMaterialDataLoadService.java
 * 
 * This interface is using as a service point to declare the invocation method, specify the method arguments, using this interface in implementation class, any
 * changes to a method name or arguments list should reflect in the implementation class, the same interface is deploying in server as well as in client system
 * 
 * IMP NOTE: We are deploying this file in server as well as in client, while deploying in server compile this file AS IS, but while deploying this file in the
 * client system (where we deploy SOAPClient) remove 'throws Exception' code from line number 33, save the file and compile modified interface in client server
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since April-13-2018
 */
@WebService
@SOAPBinding(style = Style.RPC)
public interface HBIMaterialDataLoadService
{
	/**
	 * This function is using as service point which is responsible for establishing the communication between server (FlexPLM 10.1 instance) and client IIB MQ
	 * @param soapMessage - String
	 * @return xmlString - String
	 * @throws Exception
	 */
	@WebMethod
	String loadGlobalRawMaterialToPLM(String soapMessage) throws Exception;
}
package com.hbi.wc.interfaces.inbound.global.material.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.lcs.wc.util.LCSProperties;

/**
 * HBIMaterialSOAPMessageUtil.java
 *
 * This class contains function is using to prepare a SOAP Message within a specific format using the given set of attribute keys-values, return soap message from header.
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-14-2018
 */
public class HBIMaterialSOAPMessageUtil
{
	private static String serverURI = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.client.HBIMaterialSOAPMessageUtil.serviceURI", "http://server.material.global.inbound.interfaces.wc.hbi.com/");
    private static String serviceName = LCSProperties.get("com.hbi.wc.interfaces.inbound.global.material.client.HBIMaterialSOAPMessageUtil.serviceName", "loadGlobalRawMaterialToPLM");
    
    /**
     * This function is using to prepare a SOAP Message within a specific format using the given set of attribute keys-values, return soap message from header
     * @param materialDataFileDataMap - Map<String, String>
     * @return soapMessage - String
     * @throws SOAPException
     * @throws IOException
     */
    public String getRawMaterialSOAPMessage(Map<String, String> materialDataFileDataMap) throws SOAPException, IOException
    {
    	// LCSLog.debug("### START HBIMaterialSOAPMessageUtil.getRawMaterialSOAPMessage(Map<String, String> materialDataFileDataMap) ###");
    	String soapMessage = "";
    	SOAPElement childElement = null;
    	
    	//Creating Message Factory, initializing SOAPMessage using messageFactory, get soapPart from the soapMessage, initialize soapEnvelope and add addNamespaceDeclaration to Envelope
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessageObj = messageFactory.createMessage();
		SOAPPart soapPart = soapMessageObj.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		soapEnvelope.addNamespaceDeclaration("proc", serverURI);
    	
		//Initializing SOAPBody using an existing SOAPEnvelope instance, adding ChildElement with additional parameter (material level attributes as parameter)
		SOAPBody soapBody = soapEnvelope.getBody();
		SOAPElement serviceElement = soapBody.addChildElement(serviceName, "proc");
		SOAPElement paramsElement = serviceElement.addChildElement("params");
		
		for(String attributeKey : materialDataFileDataMap.keySet())
		{
			childElement = paramsElement.addChildElement(attributeKey);
			childElement.addTextNode(materialDataFileDataMap.get(attributeKey));
		}
		
		//Converting SOAPMessage to String using ByteArrayOutputStream and String format as 'utf-8' and returning to the converting string from the function header to calling function
		soapMessageObj.saveChanges();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		soapMessageObj.writeTo(baos);
		soapMessage = new String(baos.toByteArray(), "UTF-8");
		
    	// LCSLog.debug("### END HBIMaterialSOAPMessageUtil.getRawMaterialSOAPMessage(Map<String, String> materialDataFileDataMap) ###");
    	return soapMessage;
    }
}
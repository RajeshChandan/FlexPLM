package com.burberry.wc.integration.bean;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * This Bean class will be return as response if exception occurred.
 *
 * 
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
@XmlRootElement
public class ErrorMessage implements Serializable {
	
	/**
	 * errorMessage.
	 */
	private String errorMessage;
	/**
	 * errorCode.
	 */
	private int errorCode;
	/**
	 * error detail.
	 */
	private String documentation;
	
	/**
	 * DEFAULT CONSTRUCTOR .
	 */
	public ErrorMessage() {
	
	}
	
	/**
	 * CONSTRUCTOR.
	 */
	public ErrorMessage(final String errorMessage, final int errorCode,
			final String documentation) {
	
		super();
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.documentation = documentation;
	}
	
	/**
	 * getErrorMessage.
	 * 
	 * @return String
	 */
	public String getErrorMessage() {
	
		return errorMessage;
	}
	
	/**
	 * setErrorMessage.
	 * 
	 * @param errorMessage
	 *          .
	 */
	public void setErrorMessage(final String errorMessage) {
	
		this.errorMessage = errorMessage;
	}
	
	/**
	 * getErrorCode.
	 * 
	 * @return int
	 */
	public int getErrorCode() {
	
		return errorCode;
	}
	
	/**
	 * setErrorCode.
	 * 
	 * @param errorCode
	 */
	public void setErrorCode(final int errorCode) {
	
		this.errorCode = errorCode;
	}
	
	/**
	 * getDocumentation.
	 * 
	 * @return String
	 */
	public String getDocumentation() {
	
		return documentation;
	}
	
	/**
	 * setDocumentation.
	 * 
	 * @param documentation
	 */
	public void setDocumentation(final String documentation) {
	
		this.documentation = documentation;
	}
	
}

/**
 * 
 */
package com.burberry.wc.integration.exception;

/**
 * InvalidInputException exception.
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
public class InvalidInputException extends BurException {
	
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7960603732511160556L;
	
	/**
	 * InvalidInputException.
	 * 
	 * @param message
	 */
	public InvalidInputException(final String message) {
		
		super(message);
	}
	
}

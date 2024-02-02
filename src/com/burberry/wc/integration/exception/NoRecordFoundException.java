/**
 * 
 */
package com.burberry.wc.integration.exception;

/**
 * NoRecordFoundException type exception.
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
public class NoRecordFoundException extends BurException {
	
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8243126587756762605L;
	
	/**
	 * NoRecordFoundException.
	 * 
	 * @param message
	 */
	public NoRecordFoundException(final String message) {
	
		super(message);
	}
	
}

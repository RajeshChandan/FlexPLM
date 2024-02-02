/**
 * 
 */
package com.burberry.wc.integration.exception;

/**
 * Root exception type for Burberry.
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
public class BurException extends Exception {
	
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6116699204825185262L;
	
	/**
	 * BurException constructor.
	 */
	public BurException() {
	
	}
	
	/**
	 * BurException constructor.
	 * 
	 * @param message Error Message
	 */
	public BurException(final String message) {
		
		super(message);
	}
	
	/**
	 * BurException constructor.
	 * 
	 * @param cause Throw Cause
	 */
	public BurException(final Throwable cause) {
		
		super(cause);
	}
	
	/**
	 * BurException constructor.
	 * 
	 * @param message Error Message
	 * @param cause  Throw Cause
	 */
	public BurException(final String message, final Throwable cause) {
		
		super(message, cause);
	}
	
	/**
	 * BurException constructor.
	 * 
	 * @param message Error Message
	 * @param cause Throw Cause
	 * @param enableSuppression Suppression
	 * @param writableStackTrace Stack trace
	 */
	public BurException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}

/**
 * 
 */
package com.burberry.wc.integration.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Root application responsible to start the defined services. It instantiate
 * and add service provider class to a Set.
 *
 * 
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
public class BurberryDataExtractionServiceApplication extends Application {
	
	/**
	 * singletons set object.
	 */
	private final Set<Object> singletons = new HashSet<Object>();
	
	/**
	 * constructor.
	 */
	public BurberryDataExtractionServiceApplication() {
		
		singletons.add(new BurberryDataServiceImpl());
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	@Override
	public Set<Object> getSingletons() {
		
		return singletons;
	}
}

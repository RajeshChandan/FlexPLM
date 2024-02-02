package com.burberry.wc.integration.services;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.burberry.wc.integration.util.BurConstant;

/**
 * Class responsible to intercept all URL and Authenticate and Authorize user.
 * 
 * With the given credential it validate if user belongs to any particular group
 * or not. It user found matching to the valid group then only filter will allow
 * to proceed further to fetch data. In case of failure it will return to login
 * or throw exception.
 * 
 * 
 *
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
public class BurberryBasicAuthenticationService implements javax.servlet.Filter {
	
	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryBasicAuthenticationService.class);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(final ServletRequest request,
			final ServletResponse response, final FilterChain filter) {
	
		if (request instanceof HttpServletRequest) {
			
			boolean authenticationStatus = false;
			String methodName = "doFilter() ";
			try {
				authenticationStatus = authenticateUserCredentials();
				logger.info(methodName + "authenticationStatus: " + authenticationStatus); 
			} catch (final WTException e) {
				logger.error("Failed to authorize user detail : ", e);
			}
			
			if (authenticationStatus) {
				try {
					filter.doFilter(request, response);
				} catch (final IOException e) {
					logger.error("Failed to authorize  user detail : ", e);
				} catch (final ServletException e) {
					logger.error("Failed to authorize  user detail : ", e);
				}
			} else {
				if (response instanceof HttpServletResponse) {
					final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
					httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
		}
	}
	
	/**
	 * Return true if user belongs to the integration group
	 * 
	 * @return boolean
	 * @throws WTException
	 *			WTException
	 */
	private boolean authenticateUserCredentials() throws WTException {
		
		boolean authenticateUser = false;
		String methodName = "authenticateUserCredentials() ";
		// get current user logged in
		WTUser wtuser = (WTUser) SessionHelper.getPrincipal();
		logger.info(methodName + "wtuser: " + wtuser); 
		// get list of groups user belongs to
		Enumeration enumGroups = wtuser.parentGroupNames();
		logger.info(methodName + "enumGroups: " + enumGroups); 

		String groupName = null;
		while(enumGroups.hasMoreElements()){
			// Get the Group Names
			groupName = (String)enumGroups.nextElement();
			logger.info(methodName + "groupName: " + groupName);
			
			// To check whether User belongs to Integration group
			if(groupName.equalsIgnoreCase(BurConstant.REST_APP_GROUP)){
				// set true
				authenticateUser = true;
				// break the loop if found
				break;
			}
		}
		logger.info(methodName + "authenticateUser return parameter: " + authenticateUser); 
		// return
		return authenticateUser;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(final FilterConfig arg0) throws ServletException {
	
	}
}

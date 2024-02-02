package com.burberry.wc.integration.streamline.util;

import javax.ws.rs.core.Response;

public class BRJSONValidationException extends Exception{
	
	public BRJSONValidationException(Response response) 
    { 
        // Call constructor of parent Exception 
        super(); 
    }

}

/**
 * 
 */
package com.burberry.wc.integration.services;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;



/**
 * Root interface to declare services.
 *
 * 
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */
/**
 * @author wcadmin
 *
 */
@Path("/BDEService")
public interface BurberryDataService {

	/**
	 * Extract Product data and generate XML as output as per the T2/Pricing.
	 * 
	 * 
	 * @param startDate
	 *          start date
	 * @param endDate
	 *          end date
	 * @param seasons
	 *          season
	 * @return Response
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IOException 
	 */
	@GET
	@Path("/pricing")
	@Produces(MediaType.APPLICATION_XML)
	public Response getPricingData(@QueryParam("start") final String startDate,
			@QueryParam("end") final String endDate,
			@QueryParam("season") final String seasons) throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException;
	
	/**
	 * Extract Product data and generate XML as output as per the T1/Product.
	 * 
	 * 
	 * @param startDate
	 *          start date
	 * @param endDate
	 *          end date
	 * @param seasons
	 *          season
	 * @return Response
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IOException 
	 */
	@GET
	@Path("/product")
	@Produces(MediaType.APPLICATION_XML)
	public Response getProductData(@QueryParam("start") final String startDate,
			@QueryParam("end") final String endDate,
			@QueryParam("season") final String seasons) throws NoSuchMethodException, NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException;
			
	/**
	 * @param ui
	 * @param request
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws WTException
	 * @throws BurException
	 * @throws ParseException
	 * @throws IOException
	 */
	@GET
    @Path("/productAPI")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductAPIData(@Context UriInfo ui,@Context HttpServletRequest request) 
                            throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, WTException, BurException, ParseException, IOException;


    
    /**
     * @param ui
     * @param request
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws WTException
     * @throws BurException
     * @throws ParseException
     * @throws IOException
     */
    @GET 
     @Path("/paletteMaterialAPI")
     @Produces(MediaType.APPLICATION_JSON)
     public Response getPaletteMaterialAPIData(@Context UriInfo ui,@Context HttpServletRequest request)
                             throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, WTException, BurException, ParseException, IOException;

    
   
    /**
     * @param ui
     * @param request
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws WTException
     * @throws BurException
     * @throws ParseException
     * @throws IOException
     */
    @GET 
     @Path("/productBOMAPI")
     @Produces(MediaType.APPLICATION_JSON)
     public Response getProductBOMAPIData(@Context UriInfo ui,@Context HttpServletRequest request)
                             throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, WTException, BurException, ParseException, IOException;

    /**
     * @param ui
     * @param request
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws WTException
     * @throws BurException
     * @throws ParseException
     * @throws IOException
     */
    @GET 
     @Path("/sampleAPI")
     @Produces(MediaType.APPLICATION_JSON)
     public Response getSampleAPIData(@Context UriInfo ui,@Context HttpServletRequest request)
                             throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, WTException, BurException, ParseException, IOException;
    
    
    /**
     * @param ui
     * @param request
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws WTException
     * @throws BurException
     * @throws ParseException
     * @throws IOException
     */
    @GET 
     @Path("/productCostingAPI")
     @Produces(MediaType.APPLICATION_JSON)
     public Response getProductCostingAPIData(@Context UriInfo ui,@Context HttpServletRequest request)
                             throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, WTException, BurException, ParseException, IOException;
							 
	
     /**
     * @param ui
     * @param request
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws WTException
     * @throws BurException
     * @throws ParseException
     * @throws IOException
     */
    @GET 
     @Path("/planningAPI")
     @Produces(MediaType.APPLICATION_JSON)
     public Response getPlanningAPIData(@Context UriInfo ui,@Context HttpServletRequest request)
                             throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException,  IllegalArgumentException, InvocationTargetException, WTException, BurException, ParseException, IOException;
 
     
  
    
     /**
     * @param ui
     * @param request
     * @return
     */
    @GET 
     @Path("/cancelRequest")
     @Produces(MediaType.APPLICATION_JSON)
     public Response getCancelReqest(@Context UriInfo ui,@Context HttpServletRequest request);
}

package com.burberry.wc.integration.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.lcs.wc.util.FormatHelper;

public final class BurberryAPISessionTrack {
	
	private ConcurrentHashMap<String,Map<String, Set<Thread>>> seesionThreadMap = new ConcurrentHashMap<String,Map<String, Set<Thread>>>();
	//BURBERRY-1372: Start
	private ConcurrentHashMap<String, Set<String>> userSessionMap = new ConcurrentHashMap<String, Set<String>>();
	//BURBERRY-1372: End
	private volatile static BurberryAPISessionTrack obj;
	 
    private BurberryAPISessionTrack() {}
 
    public static BurberryAPISessionTrack getInstance()
    {
        if (obj == null)
        {
            // To make thread safe
            synchronized (BurberryAPISessionTrack.class)
            {
                // check again as multiple threads
                // can reach above step
               
                    obj = new BurberryAPISessionTrack();
                
            }
        }
        return obj;
    }
    
    /**
	 * @return the seesionThreadMap
	 */
	public ConcurrentHashMap<String, Map<String, Set<Thread>>> getSeesionThreadMap() {
		return seesionThreadMap;
	}

	/**
	 * @param seesionThreadMap the seesionThreadMap to set
	 */
	public void setSeesionThreadMap(
			ConcurrentHashMap<String, Map<String, Set<Thread>>> seesionThreadMap) {
		this.seesionThreadMap = seesionThreadMap;
	}

    public Set<Thread> getThreadsBySessionAndAPI(String sessionID, String apiType){
    	if(!validateSessionIDAndAPIContent(sessionID, apiType) || seesionThreadMap.get(sessionID)==null){
    		return java.util.Collections.emptySet();
    	}
    	
    	return seesionThreadMap.get(sessionID).get(apiType.toLowerCase());
    }
	
    /**
     * Method to add API Session ID and Thread Object reference.
     * @param apitype
     * @param sessionID
     * @param t
     */
    public void addAPISessionThread(String apitype, String sessionID,Thread t){
    	Map<String, Set<Thread>> apiThreadMap = null;
    	if(seesionThreadMap.get(sessionID)==null){
    		apiThreadMap = new HashMap<String, Set<Thread>>();
    	}else{
    		apiThreadMap = seesionThreadMap.get(sessionID);
    	}
    	if(apiThreadMap.get(apitype)==null){
    		apiThreadMap.put(apitype,new HashSet<Thread>());
    	}
    	apiThreadMap.get(apitype).add(t);
    	seesionThreadMap.put(sessionID,apiThreadMap);
    }
	
    /**
     * Method to remove the thread reference from the SessionThreadMap  after request is completed processing.
     * @param sessionID
     * @param apiType
     * @param t
     */
    synchronized public void removedThreadForSessionAndAPI(String sessionID, String apiType,Thread t){
    	
    	Set<Thread> threadList = getThreadsBySessionAndAPI(sessionID, apiType);
    	Set<Thread> activeThreads = new HashSet<Thread>();
    	
    	if(threadList!=null && !threadList.isEmpty()){    		
    		for(Thread thread : threadList){
    			if(t!=thread){
    				//Add thread 
    				activeThreads.add(thread);    				
    			}
    		}	
    		seesionThreadMap.get(sessionID).put(apiType, activeThreads);
    	}
    }
    
    /**
     * Method to interrupt the running thread.
     * @param sessionID
     * @param apiType
     * @return
     */
    public boolean interruptAPIExecution(String sessionID, String apiType){
    	Set<Thread> threadList = getThreadsBySessionAndAPI(sessionID, apiType.toLowerCase());
    	if(threadList==null || threadList.isEmpty()){	
    		return false; 
    	}
    	
    	for(Thread thread : threadList){
    		thread.interrupt();
    		
    	}
    	seesionThreadMap.get(sessionID).remove(apiType.toLowerCase());
    	return true;
    }
    
    /**
     * 
     * @param sessionID
     * @param apiType
     * @return
     */
	public boolean validateSessionIDAndAPIContent(String sessionID,
			String apiType) {
		return FormatHelper.hasContent(sessionID) && FormatHelper.hasContent(apiType);
	}

	
	//BURBERRY-1372: Start
	/**Method to set user id and session id.
	 * @param userID String
	 * @param sessionID String
	 */
	public void setUserSessionMap(String userID,String sessionID){
		if(!userSessionMap.containsKey(userID)){
			Set<String> sessionIds = new HashSet<String>();
			sessionIds.add(sessionID);
			userSessionMap.putIfAbsent(userID, sessionIds);
		}else{
			Set<String> sessionIds = userSessionMap.get(userID);
			sessionIds.add(sessionID);
		}
	}
	
	/**Method to interrupt api execution by user.
	 * @param userID String
	 * @param apiType String
	 * @return boolean
	 */
	synchronized public boolean interruptAPIExecutionByUser(String userID,String apiType){
		boolean terminateFlag = false;
		if(FormatHelper.hasContent(userID)&&FormatHelper.hasContent(apiType)){
			
			Set<String> sessionIds = trackUserSession(userID);
			Set<String> terminatedSessionIds=new HashSet<String>();
			for(String session : sessionIds){
				terminateFlag = interruptAPIExecution(session,apiType);
				//sessionIds.remove(session);
			}
			userSessionMap.put(userID, terminatedSessionIds);
		}
		return terminateFlag;
	}
	
	/**Method to track user session.
	 * @param userID String
	 * @return Set<String>
	 */
	private  Set<String> trackUserSession(String userID){
		Set<String> userSession= new HashSet<String>();
		if(userSessionMap.containsKey(userID)){
			userSession=userSessionMap.get(userID);
		}
		return userSession;
	}
	//BURBERRY-1372: End
}

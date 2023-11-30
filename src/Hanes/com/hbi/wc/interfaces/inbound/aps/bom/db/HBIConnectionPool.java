package com.hbi.wc.interfaces.inbound.aps.bom.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import com.lcs.wc.util.LCSLog;

/**
 * HBIConnectionPool.java
 *
 * This class contains generic functions which are using to manage connection pool (like creating new connection, get existing connection from a connection pool, releasing used connections 
 * @author Abdul.Patel@Hanes.com
 * @since October-18-2017
 */
public class HBIConnectionPool
{
	// This variable represents the number of initial connections
	private static int connectionCount = 20;
	
	// A list of available connections for use.
	private Vector<Connection> availableConnections = new Vector<Connection>();
	
	// A list of connections being used currently.
	private Vector<Connection> usedConnections = new Vector<Connection>();
	
	// The URL string, username and password, using to connect to the staging database
	private String urlString = null;
	private String userName = null;
	private String userPassword = null;
	
	/**
	 * This constructor is using to initialize 'Server URL', username and password using to establish database connection, this constructor also has the code to manage available connection 
	 * @param theURLString - String
	 * @param theUserName - String
	 * @param theUserPassword - String
	 * @throws SQLException
	 */
	public HBIConnectionPool(String theURLString, String theUserName, String theUserPassword) throws SQLException 
	{
		LCSLog.debug("### START HBIConnectionPool.HBIConnectionPool(String theURLString, String theUserName, String theUserPassword) ###");
		urlString = theURLString;
		userName = theUserName;
		userPassword = theUserPassword;

		for (int connection = 0; connection < connectionCount; connection++)
		{
			// Add a new connection to the available list.
			availableConnections.addElement(getConnection());
		}
		
		LCSLog.debug("### START HBIConnectionPool.HBIConnectionPool(String theURLString, String theUserName, String theUserPassword) ###");	
	}
	
	/**
	 * This function is using to return a connection object(in this function we are calling DriverManager.getConnection() along with the existing URL(server detail), username and password)
	 * @return Connection
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection(urlString, userName, userPassword);
	}
	
	/**
	 * This method looks at the available list to see if there are any ready made connections that can be returned. If there are, it returns the last connection in the available list after
	 * moving it to the used list. If there are no ready-made connections to be returned, this method goes ahead and creates a connection object and returns the newly created object after 
	 * adding it to the used list of connections.
	 * @throws SQLException
	 */
	public synchronized Connection checkout() throws SQLException
	{
		// LCSLog.debug("### START HBIConnectionPool.checkout() ###");
		Connection connectionObj = null;
		LCSLog.debug("  checkout - availableConnections.size() =" + availableCount());
		LCSLog.debug( "  checkout - usedConnections.size() =" + usedConnections.size());
		
		if (availableCount() == 0)
		{
			// Out of connections. Create one more.
			connectionObj = getConnection();
			// Add this connection to the "Used" list.
			usedConnections.addElement(connectionObj);
			// We don't have to do anything else since this is a new connection.
		}
		else
		{
			// Connections exist ! Get a connection object
			connectionObj = (Connection) availableConnections.lastElement();
			// Remove it from the available list.
			availableConnections.removeElement(connectionObj);
			// Add it to the used list.
			usedConnections.addElement(connectionObj);
		}

		// LCSLog.debug("### END HBIConnectionPool.checkout() ###");
		return connectionObj;
	}

	/**
	 * This method merely moves the passed connection object from the used connections list to the available connections list, which will help to maintain connection pool for re-utilizing
	 * @param connectionObj - Connection
	 * @throws SQLException
	 */
	public synchronized void checkin(Connection connectionObj) throws SQLException
	{
		// LCSLog.debug("### START HBIConnectionPool.checkin(Connection connectionObj) ###");
		if (connectionObj != null)
		{
			// Remove from used list.
			usedConnections.removeElement(connectionObj);
			// Add to the available list
			availableConnections.addElement(connectionObj);
		}
		
		// Call to cleanupConnection so that the open connections can be contended to the maximum number of connections allowed - CHECK THIS METHOD TO IDENTIFY THE UNUSED CONNECTIONS
		cleanupConnection();
		
		// LCSLog.debug("### END HBIConnectionPool.checkin(Connection connectionObj) ###");
	}
	
	/**
	 * This function is using to check connection count, compare with pre-defined connection count, based on validation empty the connection pool and remove/disconnect from the database
	 * @throws SQLException
	 */
	public synchronized void cleanupConnection() throws SQLException
	{
		// LCSLog.debug("### START HBIConnectionPool.cleanupConnection() ###");
		
		//check available connection count with the pre-defined connection count and clean up extra available connections if exists.
		while(availableCount() > connectionCount)
		{
			Connection connection = (Connection) availableConnections.lastElement();
			availableConnections.removeElement(connection);

			// Close the connection to the database.
			if (!connection.isClosed())
			{
				connection.close();
			}
		}
		
		// LCSLog.debug("### END HBIConnectionPool.cleanupConnection() ###");
	}
		
	/**
	 * This is using to manage available connections(managing connections based on pre-defined connection count) and it returns the count of currently available connection to caller method 
	 * @return availableConnection - int
	 */
	public int availableCount()
	{
		return availableConnections.size();
	}
}
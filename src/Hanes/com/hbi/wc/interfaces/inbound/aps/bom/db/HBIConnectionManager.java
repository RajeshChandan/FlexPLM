package com.hbi.wc.interfaces.inbound.aps.bom.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIConnectionManager.java
 *
 * This class contains generic functions which are using to get the connection object, release connection and manage the connection pool using the HBIConnectionPool with the given database
 * @author Abdul.Patel@Hanes.com
 * @since October-18-2017
 */
public class HBIConnectionManager
{
	// JDBC database connection parameters.
	private static String HOSTNAME = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.db.HBIConnectionManager.HOSTNAME", "avyx");
	private static String PORT_NUMBER = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.db.HBIConnectionManager.PORT_NUMBER", "1522");
	private static String SERVICE_NAME = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.db.HBIConnectionManager.SERVICE_NAME", "prod1");
	private static String DB_USER = LCSProperties.get("com.hbi.stg.extractors.apsuser", "opruprod");
	private static String DB_PWD = LCSProperties.get("com.hbi.stg.extractors.apspwd", "testutest");
	
	private static HBIConnectionPool cp = null;
	
	static
	{
		try
		{
			//Get HOST_NAME, PORT_NUMBER, SERVICE_NAME OR DATABASE_NAME from properties file which are using as an environment to remotely connect to the APS database for data retrievals
			String JDBC_URL = "jdbc:oracle:thin:@"+HOSTNAME+":"+PORT_NUMBER+":"+SERVICE_NAME;
			
			cp = new HBIConnectionPool(JDBC_URL, DB_USER, DB_PWD);
		}
		catch (SQLException sqlExp) 
		{
			LCSLog.error("SQLException in static block of the class HBIConnectionManager is "+sqlExp);
		}
	}
	
	/**
	 * This method gets a connection from the connection pool, sets autoCommit as false (by default we want it will be true) and returning modified connection object to the calling method 
	 * @return connection - Connection
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException
	{
		// LCSLog.debug("### START HBIConnectionManager.getConnection() ###");
		Connection connection = null;
		
		if (cp != null)
		{
			try
			{
				connection = cp.checkout();
				connection.setAutoCommit(false);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw e;
			}
		}
		
		// LCSLog.debug("### END HBIConnectionManager.getConnection() ###");
		return connection;
	}

	/**
	 * This method releases the connection to the pool
	 * @param connection - Connection
	 */
	public static void releaseConnection(Connection connection) throws SQLException
	{
		// LCSLog.debug("### START HBIConnectionManager.releaseConnection(Connection connection) ###");
		
		if (cp != null)
		{
			cp.checkin(connection);
		}
	
		// LCSLog.debug("### END HBIConnectionManager.releaseConnection(Connection connection) ###");
	}
}
package com.hbi.wc.interfaces.inbound.aps.bom.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.lcs.wc.util.LCSLog;

public class HBIConnctionTest
{
	public static void main(String[] args) throws SQLException
	{
	//	LCSLog.debug("### START HBIConnctionTest.main() ###");
		System.out.println("### START HBIConnctionTest.main() ###");
		Connection connectionObj = null;
		Statement statementObj = null;
		ResultSet resultSetObj = null;
		
		try
		{
			String queryString = "SELECT ITEM_TYPE_CD FROM STYLE WHERE STYLE_CD='NBAU4E'";
			connectionObj = HBIConnectionManager.getConnection();
			statementObj = connectionObj.createStatement();
			resultSetObj = statementObj.executeQuery(queryString);
			if(resultSetObj.next())
			{
				String itemTypeCode = resultSetObj.getString("ITEM_TYPE_CD");
				System.out.println("Item Type Code = "+ itemTypeCode);
			}
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
		}
		finally
		{
			if(resultSetObj != null)
			{
				resultSetObj.close();
				resultSetObj = null;
			}
			if(statementObj != null)
			{
				statementObj.close();
				statementObj = null;
			}
			
			HBIConnectionManager.releaseConnection(connectionObj);
		}
		
		LCSLog.debug("### END HBIConnctionTest.main() ###");
	}
}
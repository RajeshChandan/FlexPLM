/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.stg.extractors;

import com.hbi.etl.logger.PLMETLLogger;
import com.lcs.wc.util.LCSProperties;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import wt.pom.DBProperties;
import org.apache.log4j.Logger;

/**
 *
 * @author sobabu
 */
public class HBIAPSMOAFixUtil 
{

    Connection con = null;
    private Properties info = new Properties();
    private static String dbUserName = LCSProperties.get("com.hbi.stg.extractors.apsuser");
    private static String dbPassword = LCSProperties.get("com.hbi.stg.extractors.apspwd");
    static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    //static Logger logger = PLMETLLogger.createInstance(HBIAPSMOAFixUtil.class, logLevel, true);
	static Logger logger = PLMETLLogger.createInstance(HBIAPSMOAFixUtil.class, logLevel, "APSMOAFix");
    public Connection connect() throws IOException 
	{

        Properties properties = DBProperties.getDBProperties();
        String dbHost = properties.getProperty("wt.pom.jdbc.host");
        String dbPort = properties.getProperty("wt.pom.jdbc.port");
        String dbSID = properties.getProperty("wt.pom.jdbc.service");

        info.put("user", dbUserName);
        info.put("password", dbPassword);
       //String driverName = "oracle.jdbc.OracleDriver";

        String url = "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + ":" + dbSID;
		//System.out.println("URL"+url);
        logger.info(url);
        try 
		{
            con = DriverManager.getConnection(url, info);
        } catch (Exception e) 
		{
            logger.error("Error while connecting to database");
            e.printStackTrace();
        }
        return con;

    }

    public void closeConnection(Connection con) 
	{
        try 
		{
            con.close();
        } catch (SQLException e) 
		{
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String args[]) 
	{
        try 
		{
		
            HBIAPSMOAFixUtil apsMoaFixUtil = new HBIAPSMOAFixUtil();
			//System.out.println("HBIAPSMOAFixUtil");
            Connection con = apsMoaFixUtil.connect();
			logger.info("Connecting...");
            Statement st = null;
			logger.info("Creating Statement...");
            st = con.createStatement();
			logger.info("Statement Created...");
			//String selectSql = "SELECT DISTINCT a.att1 as styleName,c.primarykey FROM plmstg.hbimaterial a,plmstg.hbimaterialsupplier b,plmstg.hbimoaobject c WHERE a.branchiditerationinfo = b.materialref AND b.branchiditerationinfo = c.ownerkey AND LOWER ( a.att68 ) = 'true' AND b.date5 IS NOT NULL AND a.att82 = 'Required' and not exists ( select 1 from plmstg.aps_corp_purchase_sku d where a.att1 = d.style_cd)";
			String selectSql = "SELECT DISTINCT "
+ "  CASE "
+ "    WHEN a.flextypeidpath='\\16052\\236273254' "
+ "    THEN UPPER(a.att25) "
+ "    ELSE UPPER(a.att1) "
+ "  END styleName, "
+ "  c.primarykey "
+ "   FROM plmstg.hbimaterial a  , "
+ "  plmstg.hbimaterialsupplier b, "
+ "  plmstg.hbimoaobject c "
+ "  WHERE a.branchiditerationinfo = b.materialref "
+ "AND b.branchiditerationinfo     = c.ownerkey "
+ "AND LOWER ( a.att68 )           = 'true' "
+ "AND b.date5                    IS NOT NULL "
+ "AND a.att82                     = 'Required' "
+ "AND NOT EXISTS "
+ "  (SELECT 1 "
+ "     FROM plmstg.aps_corp_purchase_sku d "
+ "    WHERE "
+ "    CASE "
+ "      WHEN a.flextypeidpath='\\16052\\236273254' "
+ "      THEN UPPER(a.att25) "
+ "      ELSE UPPER(a.att1) "
+ "    END = d.style_cd "
+"	  AND NVL(Trim(Upper(a.att12)), '000')=d.color_cd"
+"	  AND NVL(Trim(Upper(a.att5)), '------')=d.attribute_cd"
+"    AND NVL(Trim(Upper(a.att40)), '00')=d.size_cd"
+ "  )";
			//System.out.println("String select sql "+selectSql);       
			logger.info("Executing Select Statement...");
			ResultSet rs = st.executeQuery(selectSql);
			logger.info("Selection Completed...");
			logger.info("Selected Styles For Process...");
			int rowCount=0;
            while (rs.next()) 
			{
				rowCount++;
				if(rowCount == 0)
				{
					logger.info("No materials to flow...");
					st.close();
					apsMoaFixUtil.closeConnection(con);
				}
				else
				{
					String styleName = rs.getString("styleName");
					logger.info("Style Name: "+styleName+"..." + "\n");
				}
            }
			
			String deleteSql="Delete from plmstg.plm_aps_error_log";
			logger.info("Executing Delete Statement...");
			int deleteCount = st.executeUpdate(deleteSql);
			logger.info("Delete Completed..."+deleteCount);
			if(deleteCount == 0) 
			{
				logger.info("No Records For Deletion...");
			}
			else 
			{
				logger.info("Deleted Successfullly...");
				logger.info("Total Rows Deleted"+deleteCount+"...");
			}
			// include conditions for exact check f stylesAND NVL(Trim(Upper(a.att12)), '000')=d.color_cd",AND NVL(Trim(Upper(a.att5)), '------')=d.attribute_cd",AND NVL(Trim(Upper(a.att40)), '00')=d.size_cd"
			//String updateSql ="UPDATE plmstg.hbimoaobject SET loaderupdatetime = SYSDATE WHERE primarykey IN ( SELECT DISTINCT c.primarykey FROM plmstg.hbimaterial a , plmstg.hbimaterialsupplier b , plmstg.hbimoaobject c WHERE a.branchiditerationinfo = b.materialref AND b.branchiditerationinfo = c.ownerkey AND LOWER ( a.att68 ) = 'true' AND b.date5 IS NOT NULL AND a.att82 = 'Required' AND NOT EXISTS ( SELECT 1 FROM plmstg.aps_corp_purchase_sku d WHERE a.att1 = d.style_cd ))";      
			String updateSql = "UPDATE plmstg.hbimoaobject "
+ "SET loaderupdatetime = SYSDATE "
+ "  WHERE primarykey  IN "
+ "  ( SELECT DISTINCT c.primarykey "
+ "     FROM plmstg.hbimaterial a   , "
+ "    plmstg.hbimaterialsupplier b , "
+ "    plmstg.hbimoaobject c "
+ "    WHERE a.branchiditerationinfo = b.materialref "
+ "  AND b.branchiditerationinfo     = c.ownerkey "
+ "  AND LOWER ( a.att68 )           = 'true' "
+ "  AND b.date5                    IS NOT NULL "
+ "  AND a.att82                     = 'Required' "
+ "  AND NOT EXISTS "
+ "    (SELECT 1 "
+ "       FROM plmstg.aps_corp_purchase_sku d "
+ "      WHERE "
+ "      CASE "
+ "        WHEN a.flextypeidpath='\\16052\\236273254' "
+ "        THEN UPPER(a.att25) "
+ "        ELSE UPPER(a.att1) "
+ "      END = d.style_cd "
+"	  AND NVL(Trim(Upper(a.att12)), '000')=d.color_cd"
+"	  AND NVL(Trim(Upper(a.att5)), '------')=d.attribute_cd"
+"    AND NVL(Trim(Upper(a.att40)), '00')=d.size_cd"
+ "    ) "
+ "  )";
			logger.info("Executing Update Statement...");
			int updateCount = st.executeUpdate(updateSql);
			logger.info("Update Completed..."+updateCount);
			if(updateCount == 0) 
			{
				logger.info("No Records For Updation...");
			}
			else 
			{
				logger.info("Updated Successfullly...");
				logger.info("Total No Of Styles Updated"+updateCount+"...");
			}
			apsMoaFixUtil.closeConnection(con);
			
            
        } 
		catch (Exception ex) 
		{
            logger.error(ex);
		}

    }
}

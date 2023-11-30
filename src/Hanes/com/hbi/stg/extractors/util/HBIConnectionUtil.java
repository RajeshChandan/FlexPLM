/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.stg.extractors.util;

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
 * @author UST
 */
public class HBIConnectionUtil {

    Connection con = null;
    private Properties info = new Properties();
    private static String dbUserName = LCSProperties.get("com.hbi.stg.extractors.apsuser");
    private static String dbPassword = LCSProperties.get("com.hbi.stg.extractors.apspwd");
    static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger logger = PLMETLLogger.createInstance(HBIConnectionUtil.class, logLevel, true);

    public Connection connect() throws IOException {

        Properties properties = DBProperties.getDBProperties();
        String dbHost = properties.getProperty("wt.pom.jdbc.host");
        String dbPort = properties.getProperty("wt.pom.jdbc.port");
        String dbSID = properties.getProperty("wt.pom.jdbc.service");

        info.put("user", dbUserName);
        info.put("password", dbPassword);
       // String driverName = "oracle.jdbc.OracleDriver";

        String url = "jdbc:oracle:thin:@" + dbHost + ":" + dbPort + ":" + dbSID;
        logger.info(url);
        try {
            con = DriverManager.getConnection(url, info);
        } catch (Exception e) {
            logger.error("Error while connecting to database");
            e.printStackTrace();
        }
        return con;

    }

    public void closeConnection(Connection con) {

        try {
            con.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {
        try {
            HBIConnectionUtil connUtil = new HBIConnectionUtil();
            Connection con = connUtil.connect();
            Statement st = null;
            st = con.createStatement();
            String sql = "select count(*) from plmstg.APS_CORP_PURCHASE_SKU "
                    + "where style_cd = '" + "2135"
                    + "' and lw_vendor_no = " + 84044
                    + " and CUSTOMS_UOM_CD is not null"
                    + " and PUR_UNIT_OF_MEASURE is not null";

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("Material is not setup in APS. Not loading to Lawson");
                    st.close();
                    connUtil.closeConnection(con);
                } else {
                    System.out.println(rs.getInt(1));
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }

    }
}

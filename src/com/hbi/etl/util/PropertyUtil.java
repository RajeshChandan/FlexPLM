/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.util;

import com.hbi.etl.logger.PLMETLLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author UST
 */
public class PropertyUtil {
    
    static org.apache.log4j.Logger etlLogger = PLMETLLogger.createInstance(PropertyUtil.class, "ERROR");
    private Vector FullModeDates = null;

    /**
     * Get the value of FullModeDates
     *
     * @return the value of FullModeDates
     */
    public Vector getFullModeDates() throws PLMETLException {
        FileInputStream fIn=null;
        ObjectInputStream oIn=null;
        File f = new File("D:\\ptc\\Windchill_11.2\\Windchill\\codebase\\.restart");
        if(f.exists()){
            try {
                fIn = new FileInputStream(f);
                oIn = new ObjectInputStream(fIn);

                Vector<Date> tmp = (Vector<Date>) oIn.readObject();
                this.FullModeDates = tmp;
            } catch (IOException ex) {
                //Logger.getLogger(PropertyUtil.class.getName()).log(Level.SEVERE, null, ex);
                throw new PLMETLException(ex);
            } catch (ClassNotFoundException ex) {
                //Logger.getLogger(PropertyUtil.class.getName()).log(Level.SEVERE, null, ex);
                throw new PLMETLException(ex);
            } finally {
                try {
                    fIn.close();
                    oIn.close();
                } catch (IOException ex) {
                    throw new PLMETLException(ex);
                }
            }
        }
        return FullModeDates;
    }

    /**
     * Set the value of FullModeDates
     *
     * @param FullModeDates new value of FullModeDates
     */
    public void setFullModeDates(Vector FullModeDates) throws PLMETLException {
        FileOutputStream fOut = null;
        ObjectOutputStream oOut = null;
        File f = new File("D:\\ptc\\Windchill_11.2\\Windchill\\codebase\\.restart");
        try {
            f.delete();
            boolean createNewFile = f.createNewFile();
            if(createNewFile){
            fOut = new FileOutputStream(f);
            oOut = new ObjectOutputStream (fOut);
            
            oOut.writeObject(FullModeDates);
            }
            
        } catch (FileNotFoundException ex) {
            throw new PLMETLException(ex);
        } catch (IOException ex) {
            throw new PLMETLException(ex);
        } finally {
            try {
                oOut.flush();
                oOut.close();
                fOut.close();
            } catch (IOException ex) {
                throw new PLMETLException(ex);
            }
        }
    
        this.FullModeDates = FullModeDates;
         
    }

    public void deleteFile(){
        File f = new File("D:\\ptc\\Windchill_11.2\\Windchill\\codebase\\.restart");
        if(f.exists())
            f.deleteOnExit();
    }
    
    public String getEntitiesfromXML(String xmlFile) throws PLMETLException {
        String mode = null;
        try {
            File file = new File(xmlFile);
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            Integer i = 1;
            while (enuKeys.hasMoreElements()) {
                String key = "entity" + i.toString();
                
                String value = properties.getProperty(key);
                System.out.println(key + ": " + value);
                mode = value;
                Object nextElement = enuKeys.nextElement();
                i++;
            }
        } catch (FileNotFoundException e) {
            throw new PLMETLException(e);
        } catch (IOException e) {
            throw new PLMETLException(e);
        }

        return mode;
    }

    public String getModeFromXML() throws PLMETLException {
        String mode = null;
        try {
            File file = new File("D:\\ptc\\Windchill_11.2\\Windchill\\codebase\\entity_config.xml");
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();
                if (key.equals("Mode")) {
                    String value = properties.getProperty(key);
                    System.out.println(key + ": " + value);
                    mode = value;
                }
            }
        } catch (FileNotFoundException e) {
            throw new PLMETLException(e);
        } catch (IOException e) {
            throw new PLMETLException(e);
        }

        return mode;
    }

    public String getDateForFullMode() throws PLMETLException {
        String date = null;
        try {
            File file = new File("D:\\ptc\\Windchill_11.2\\Windchill\\codebase\\date_range.xml");
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();
                if (key.equals("startdate")) {
                    String value = properties.getProperty(key);
                    System.out.println(key + ": " + value);
                    date = value;   // date will be "MMDDYYYY"
                }
            }
        } catch (FileNotFoundException e) {
            throw new PLMETLException(e);
        } catch (IOException e) {
            throw new PLMETLException(e);
        }

        return date;    // date will be "MMDDYYYY"
    }

    public int getMonthRangeForFullMode() throws PLMETLException {
        int range = 0;
        try {
            File file = new File("D:\\ptc\\Windchill_11.2\\Windchill\\codebase\\date_range.xml");
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();
                if (key.equals("monthrange")) {
                    String value = properties.getProperty(key);
                    System.out.println(key + ": " + value);
                    range = Integer.parseInt(value);
                }
            }
        } catch (FileNotFoundException e) {
            throw new PLMETLException(e);
        } catch (IOException e) {
            throw new PLMETLException(e);
        }
        return range;
    }

    public String getStartDateIncrMode() throws PLMETLException {
        String date = null;
        date = getStartDateTimeIncrMode();
        return date;
    }

    public String getCurrentDateIncrMode() {
        String date = null;
        date = getCurrentDateTimeIncrMode();
        return date;
    }
    public Date getNewDateForFull(Date fromDate, int range) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        cal.add(Calendar.MONTH, range);
       // cal.add(Calendar.MONTH, -1);
        System.out.println(cal.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date newDate = dateFormat.parse(dateFormat.format(cal.getTime()));
        return newDate;
    }

    public String getBeforeTime(String time) throws PLMETLException {
        String newTime = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            Date d = df.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, -10);
            newTime = df.format(cal.getTime());
        } catch (ParseException e) {
            throw new PLMETLException(e);
        }
        return newTime;
    }

    public String getStartDateTimeIncrMode() throws PLMETLException {
        String[] outputDateTime = null;
        outputDateTime = new String[3];

        Calendar cal = Calendar.getInstance();
        //cal.setTime(new Date());
        //Timestamp myTimeStamp = new java.sql.Timestamp(cal.getTime().getTime());
        SimpleDateFormat dateformat = new SimpleDateFormat("MMddyyyy");
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
        String outputDate = dateformat.format(cal.getTime());
        String outputTime = timeformat.format(cal.getTime());
        String beforeTime = getBeforeTime(outputTime);
        String outputDatePlusTime = new String(outputDate + " " + beforeTime);

        outputDateTime[0] = outputDate;
        outputDateTime[1] = outputTime;
        outputDateTime[2] = outputDatePlusTime;
        System.out.println("getOutputDateTime() DateTime = " + outputDatePlusTime);
        return outputDatePlusTime;
    }

    public String getCurrentDateTimeIncrMode() {
        String[] outputDateTime = null;
        outputDateTime = new String[3];

        Calendar cal = Calendar.getInstance();
        //cal.setTime(new Date());
        //Timestamp myTimeStamp = new java.sql.Timestamp(cal.getTime().getTime());
        SimpleDateFormat dateformat = new SimpleDateFormat("MMddyyyy");
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
        String outputDate = dateformat.format(cal.getTime());
        String outputTime = timeformat.format(cal.getTime());
        String outputDatePlusTime = new String(outputDate + " " + outputTime);

        outputDateTime[0] = outputDate;
        outputDateTime[1] = outputTime;
        outputDateTime[2] = outputDatePlusTime;
        System.out.println("getOutputDateTime() DateTime = " + outputDatePlusTime);
        return outputDatePlusTime;
    }
    
    public String getYearValueFromDate(String fromDate) throws ParseException {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd HH:mm");
        Date date = dateformat.parse(fromDate);
        String year = "";
        int yearValue = -1;
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            yearValue = cal.get(Calendar.YEAR);
        }
        year = "" + yearValue;
        return year;
    }
    
    public String getCurrentSystemDate() {
        String date = new SimpleDateFormat("yyyyMMdd HH:mm").format(Calendar.getInstance().getTime()); 
        return date;
    }
    public String incrementYear(String fromDate) {
        try {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd HH:mm");
            Date firstdate = dateformat.parse(fromDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(firstdate);
            cal.add(Calendar.YEAR, 1); 
            return dateformat.format(cal.getTime());
        } catch (Exception ee) {
            etlLogger.error("Error in Increment Year", ee);
            return null;
        }
    }
    public String incrementMonthWithRange(String fromDate, int range) {
        try {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd HH:mm");
            Date firstdate = dateformat.parse(fromDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(firstdate);
            cal.add(Calendar.MONTH, range); 
            return dateformat.format(cal.getTime());
        } catch (Exception ee) {
            etlLogger.error("Error in Increment Month",ee);
            return null;
        }
    }

    public int getMonthValueFromStrDate(String fromDate) throws ParseException {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd HH:mm");
        Date date = dateformat.parse(fromDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        return month;
    }
}

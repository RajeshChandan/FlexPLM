package com.burberry.wc.integration.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.lcs.wc.util.FileLocation;
import com.lcs.wc.util.LCSProperties;

/**
 * This file is used to generate Logs.
 * 
 * @author "true" ITCINFOTECH
 * @version "true" 1.0
 */
public final class BurberryLogFileGenerator {
	/**
	 * log4jPropertyFile
	 */
	private static String log4jPropertyFile = LCSProperties.get("com.log4j.File");
	
	/**
	 * productAPIlog4jPropertyFile
	 */
	private static String productAPIlog4jPropertyFile=LCSProperties.get("com.log4j.File.productAPI");
	
	
	/**
	 * paletteMaterialAPIlog4jPropertyFile
	 */
	private static String paletteMaterialAPIlog4jPropertyFile=LCSProperties.get("com.log4j.File.paletteMaterialAPI");
	
	

	/**
	 * productBOMAPIlog4jPropertyFile
	 */
	private static String productBOMAPIlog4jPropertyFile = LCSProperties
			.get("com.log4j.File.productBOMAPI");

	
	/**
	 * sampleAPIlog4jPropertyFile.
	 */
	private static String sampleAPIlog4jPropertyFile=LCSProperties.get("com.log4j.File.sampleAPI");

	
	/**
	 * productCostingAPIlog4jPropertyFile.
	 */
	private static String productCostingAPIlog4jPropertyFile=LCSProperties.get("com.log4j.File.productCostingAPI");

	/**
	 * planningAPIlog4jPropertyFile.
	 */
	private static String planningAPIlog4jPropertyFile=LCSProperties.get("com.log4j.File.planningAPI");
	
	/** configuring and instantiating logger.info instance. */
	/**
	 * logger.info logger.info.
	 */
	static final Logger logger = Logger.getLogger(BurberryLogFileGenerator.class);
	
	/**
	 * Constructor
	 */
	private BurberryLogFileGenerator() {
	}

	/**
	 * configureLog Method to Configure logs.
	 * 
	 */
	public static void configureLog() {
		String path = "";
		path = FileLocation.codebase + FileLocation.fileSeperator
				+ log4jPropertyFile;
		PropertyConfigurator.configure(path);
		logger.info("******logger.info Configured !!!!!!!!***");
		logger.info("@@@@@@@check for update@@@@");
	}

	/**
	 * configureProductAPILog method to configure Product API Logs.
	 */
	public static void configureProductAPILog() {
		String path = "";
		path = FileLocation.codebase + FileLocation.fileSeperator
				+ productAPIlog4jPropertyFile;
		PropertyConfigurator.configure(path);
		logger.info("******Product API logger Configured******");
		logger.info("******Check this file for Product API Updates******");

	}

	/**
	 * configurePaletteMaterialAPILog method to configure Material API Logs.
	 */
	public static void configurePaletteMaterialAPILog() {
		String path = "";
		path = FileLocation.codebase + FileLocation.fileSeperator
				+ paletteMaterialAPIlog4jPropertyFile;
		PropertyConfigurator.configure(path);
		logger.info("******Palette Material logger Configured******");
		logger.info("******Check this file for Palette Material API Updates******");

	}

	/**
	 * configureProductBOMAPILog method to configure Product BOM API Logs.
	 */
	public static void configureProductBOMAPILog() {
		String path = "";
		path = FileLocation.codebase + FileLocation.fileSeperator
				+ productBOMAPIlog4jPropertyFile;
		PropertyConfigurator.configure(path);
		logger.info("******Product BOM logger Configured******");
		logger.info("******Check this file for Product BOM API Updates******");

	}
	
	/**
	 * configureSampleAPILog method to configure Sample API Logs.
	 */
	public static void configureSampleAPILog() {
		String path = "";
		path = FileLocation.codebase + FileLocation.fileSeperator
				+ sampleAPIlog4jPropertyFile;
		PropertyConfigurator.configure(path);
		logger.info("******Sample API logger Configured******");
		logger.info("******Check this file for Sample API Updates******");
		
	}
	
	/**
	 * configureProductCostingAPILog method to configure Costing API Logs.
	 */
	public static void configureProductCostingAPILog() {
		String path = "";
		path = FileLocation.codebase + FileLocation.fileSeperator
				+ productCostingAPIlog4jPropertyFile;
		PropertyConfigurator.configure(path);
		logger.info("******Product Costing API logger Configured******");
		logger.info("******Check this file for Product Costing API Updates******");
		
	}

	/**
	 * configurePlanningAPILog method to configure Planning API Logs.
	 */
	public static void configurePlanningAPILog() {
		String path = "";
		path = FileLocation.codebase + FileLocation.fileSeperator
				+ planningAPIlog4jPropertyFile;
		PropertyConfigurator.configure(path);
		logger.info("******Planning API logger Configured******");
		logger.info("******Check this file for Planning API Updates******");
		
	}
}

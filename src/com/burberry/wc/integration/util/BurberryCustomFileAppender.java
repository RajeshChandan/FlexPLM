package com.burberry.wc.integration.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;

import org.apache.log4j.MDC;
import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

import com.lcs.wc.util.*;

public class BurberryCustomFileAppender extends FileAppender {

	/**
	 * REPORTING_API_LOG_FILE.
	 */
	private static final String REPORTING_API_LOG_FILE = "ReportingAPILogFile";

	/**
	 * Default maximum file size is 10MB.
	 */
	private long maxFileSize = 10 * 1024 * 1024;

	/**
	 * Default Constructor.
	 */
	public BurberryCustomFileAppender() {

	}

	/**
	 * Get Maximum File Size Method.
	 * 
	 * @return long
	 */
	public long getMaximumFileSize() {
		return maxFileSize;
	}

	/**
	 * Set Maximum File Size Method.
	 * 
	 * @param maxFileSize
	 */
	public void setMaximumFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	/**
	 * Get Max File Size Method.
	 * 
	 * @param value
	 */
	public void setMaxFileSize(String value) {
		maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.FileAppender#activateOptions()
	 */
	@Override
	public void activateOptions() {
		// Check file Name is not null
		if (fileName != null) {
			// Create a new file
			File file = new File(fileName);
			// Check the file size
			if (file.length() >= maxFileSize) {
				renameFile(file);
			}
			// Set the file name in MDC map
			MDC.put(REPORTING_API_LOG_FILE, fileName);
		}
		super.activateOptions();
	}

	public void renameFile(File file) {
		// Initialisation of Date format
		SimpleDateFormat parseFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss:SSS");
		// Create a new target file
		File target = new File(fileName + '_' + parseFormat.format(new Date()));
		// Close the file
		this.closeFile();
		// File file = new File(fileName);
		// Rename the file name
		boolean renamed = file.renameTo(target);
		LogLog.setQuietMode(false);
		if (!renamed) {
			LogLog.error("Rename of file " + fileName + " Failed");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.log4j.WriterAppender#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	public void append(LoggingEvent event) {
		try {
			// Get the log file name
			String logFileName = (String) MDC.get(REPORTING_API_LOG_FILE);
			// Check file name has content
			if (FormatHelper.hasContent(logFileName)) {
				// Set the file name
				setFile(logFileName, fileAppend, bufferedIO, bufferSize);
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		super.append(event);
	}

}
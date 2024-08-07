package com.sportmaster.wc.document;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import com.lcs.wc.document.FileRenamer;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.ZipGenerator;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.DeleteFileHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.MultiCharDelimStringTokenizer;
import com.lcs.wc.util.RandomStringUtils;
import com.lcs.wc.util.ZipHelper;
import com.sportmaster.wc.bom.SMBOMDocument;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;

/**
 * This class contain the logic for getting image page Document which extension is ".ai" in zip file.
 * This method addImagePageContenttoZip() will called from PDFProductSpecGenerator2.jsp by passing filename
 * and params. it will return document which extension is ".ai".
 * 
 * @author 'true' 
 * @version 'true' 1.0 version number
 */
public class SMImagePageContentGenerator {


	/**
	 * Logger Initialization.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMImagePageContentGenerator.class);
	private static String TEMP_FOLDER;
	public static final String CHARSET_ENCODING = LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset", "UTF-8");


	/**
	 * Default Constructor of class public class SMImagePageContentGenerator.
	 */
	public SMImagePageContentGenerator() {

	}

	/**SportMaster Custom Method to include image page document into zip file.
	 * 	
	 * @return String include document to the zip location
	 */
	public static String addImagePageContenttoZip(String ZipFileName,Map<?,?> params) {

		Collection<String> files = new ArrayList<String>();

		try {

			ZipFile file = new ZipFile(ZipFileName);
			Enumeration<? extends ZipEntry> entries = file.entries();
			String fullPath= null;
			String dir = createUniqueDir();
			while(entries.hasMoreElements()){
				ZipEntry entry = entries.nextElement();
				InputStream stream = file.getInputStream(entry);
				fullPath = URLDecoder.decode(entry.getName(), CHARSET_ENCODING);
				files.add(downloadContent(stream, fullPath, dir));
			}

			file.close();
			DeleteFileHelper.deleteFile(ZipFileName);

			files.addAll(checkandLoadImagePageDoc(params, dir));

			int indx = ZipFileName.indexOf(".zip");
			String zipFile = ZipFileName.substring(0, indx) + ".zip";
			File zFile = new File(zipFile);
			String fileName = zFile.getName();
			fileName = URLDecoder.decode(fileName, CHARSET_ENCODING);
			zipFile = zFile.getParent() + File.separator + fileName;
			zipFile = FileRenamer.rename(zipFile);
			ZipHelper zipHelper = new ZipHelper(zipFile, files);
			zipHelper.zip();

			DeleteFileHelper.deleteDir(new File(dir));

			return zipFile;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} 

		return null;
	}

	/**SportMaster Custom Method to download file to specific directory.
	 * 	
	 * @return String tempfolder location
	 */
	protected static String downloadContent(InputStream inStream, String fileName, String directory) throws IOException, WTException {
		File dir = new File(directory);
		if (!dir.exists()) {
			dir = FileRenamer.rename(dir);
			boolean success = dir.mkdir();
			if (!success) {
				Object[] objB = { directory };
				throw new WTException("com.lcs.wc.resource.MainRB", "missingDirectory_ERR", objB);
			}
			directory = dir.getAbsolutePath();
		}
		if (inStream == null) {
			return "";
		}
		File outFile = new File(directory, fileName);
		outFile = FileRenamer.rename(outFile);

		OutputStream outStream = null;
		int BUF_SIZE = 4096;
		byte[] byteBuffer = new byte[BUF_SIZE];
		try {
			outStream = new FileOutputStream(outFile);
			int count = -1;
			while ((count = inStream.read(byteBuffer, 0, BUF_SIZE)) > 0) {
				outStream.write(byteBuffer, 0, count);
			}
		}
		catch (Exception localException) {}finally
		{
			if (outStream != null)
				outStream.close();
		}
		return outFile.getAbsolutePath();
	}

	/**SportMaster Custom Method to get tempfolder.
	 * 	
	 * @return String tempfolder location
	 */
	protected static String createUniqueDir() throws IOException
	{
		WTProperties wtproperties = WTProperties.getLocalProperties();
		TEMP_FOLDER = wtproperties.getProperty("wt.temp");
		String downloadDir = TEMP_FOLDER + File.separatorChar + "ZipGen" + RandomStringUtils.randomAlphanumeric(6);
		File tempFile = new File(downloadDir);
		while (tempFile.exists()) {
			downloadDir = TEMP_FOLDER + File.separatorChar + "ZipGen" + RandomStringUtils.randomAlphanumeric(6);
			tempFile = new File(downloadDir);
		}
		tempFile.mkdirs();

		return downloadDir;
	}

	/**SportMaster Custom Method to Check image page document whether it is ".ai" extension or something else.
	 * if it is ".ai" extension will include in zip file, if it is not ".ai" extension type it will not include in zip file.
	 * 	
	 * @return List<String> documents
	 */
	public static List<String> checkandLoadImagePageDoc(Map<?,?> params, String outFileDir) {

		List<String> files = new ArrayList<String>();

		String item = null;
		String fullPath = null;
		LCSDocument document = null;
		String componentList = (String) params.get("specPages");
		StringTokenizer token = new MultiCharDelimStringTokenizer(componentList, MOAHelper.DELIM);

		while(token.hasMoreTokens()){

			item = token.nextToken();

			if (item.contains("com.lcs.wc.document.LCSDocument")) {
				try {
					document = (LCSDocument) LCSQuery.findObjectById(item.substring(item.indexOf(":") +2).trim());
					String fileName = FormatHelper.formatRemoveProblemFileNameChars(document.getName());


					document =(LCSDocument) ContentHelper.service.getContents(document);
					ApplicationData primary = (ApplicationData)ContentHelper.getPrimary(document);
					if (primary != null && primary.getFileName().endsWith(".ai")) {

						File documentDirectory = new File(outFileDir + File.separator + fileName + File.separator + "primaryContent");
						if (!documentDirectory.exists()) {
							documentDirectory.mkdirs();
						}

						fullPath = URLDecoder.decode(primary.getFileName(), CHARSET_ENCODING);
						InputStream inStream = ContentServerHelper.service.findContentStream(primary);

						files.add(downloadContent(inStream, fullPath, documentDirectory.toString()));
					}

					Vector<?> contents = ContentHelper.getContentList(document);
					for (int i = 0; i < contents.size(); i++) {

						ApplicationData secondary = (ApplicationData) contents.elementAt(i);
						if(secondary !=null && secondary.getFileName().endsWith(".ai")) {

							File documentDirectory = new File(outFileDir + File.separator + fileName + File.separator + "secondaryContent");

							if (!documentDirectory.exists()) {
								documentDirectory.mkdirs();
							}

							fullPath = URLDecoder.decode(secondary.getFileName(), CHARSET_ENCODING);
							InputStream inStream = ContentServerHelper.service.findContentStream(secondary);

							files.add(downloadContent(inStream, fullPath, documentDirectory.toString()));
						}
					}

				} catch (PropertyVetoException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return files;

	}

}

package com.lowes.massimport.document;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.util.DeleteFileHelper;
import com.lcs.wc.util.FormatHelper;
import com.lowes.massimport.util.MassImport;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

/***
 * DocumentService to download the MassImport document and add the massImport error file
 * 
 * @author Samikkannu Manickam(samikkannu.manickam@lowes.com)
 *
 */
public class DocumentService {

	private static final Logger LOGGER = LogR.getLogger(DocumentService.class.getName());
	private static DocumentService documentService;

	private DocumentService() {
	}

	public static DocumentService getDocumentService() {
		if (documentService == null) {
			documentService = new DocumentService();
		}
		return documentService;
	}

	private String temp_filePath = null;

	public String downloadAndGetPrimaryFilePath(LCSDocument massImportDocument)
			throws WTException, PropertyVetoException, IOException {
		String filePath = "";
		if (massImportDocument == null) {
			throw new WTException("MassImport document does not exist in the system");
		}
		filePath = downloadPrimaryContent(massImportDocument);
		LOGGER.debug("Primary file Path: " + filePath);
		return filePath;
	}

	public void addSecondaryContent(LCSDocument lcsDocument, List<String> errorMessage) {
		Transaction tr = null;
		String error = MassImport.LOG_PREFIX + " Error while associating secondary file.";
		String errorFilePath = "";
		FileInputStream fileStream = null;
		try {
			String errorFileName = "MassImportError_" + lcsDocument.getCreatorName() + System.currentTimeMillis()
					+ ".txt";
			errorFilePath = writeErrorFile(errorFileName, errorMessage);
			tr = new Transaction();
			tr.start();
			deleteSecondaryContent(lcsDocument);
			File file = new File(errorFilePath);
			fileStream = new FileInputStream(file);
			ApplicationData appData = ApplicationData.newApplicationData((FormatContentHolder) lcsDocument);
			appData.setFileName(file.getName());
			appData.setFileSize(file.length());
			appData.setUploadedFromPath(file.getPath());
			appData.setRole(ContentRoleType.SECONDARY);
			appData.setCreatedBy(lcsDocument.getCreator());
			appData.setDescription("Mass Import error");
			appData = ContentServerHelper.service.updateContent((ContentHolder) lcsDocument, (ApplicationData) appData,
					(InputStream) fileStream);
			lcsDocument = (LCSDocument) ContentServerHelper.service.updateHolderFormat(lcsDocument);
			tr.commit();
			tr = null;
		} catch (WTPropertyVetoException e) {
			error += e.getMessage();
			LOGGER.error(error);
		} catch (WTException e) {
			error += e.getMessage();
			LOGGER.error(error);
		} catch (IOException e) {
			error += e.getMessage();
			LOGGER.error(error);
		} catch (PropertyVetoException e) {
			error += e.getMessage();
			LOGGER.error(error);
		} finally {
			if (tr != null) {
				tr.rollback();
			}
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException e) {
					error += e.getMessage();
					LOGGER.error(error);
					e.printStackTrace();
				}
			}
			DeleteFileHelper.deleteFile(errorFilePath);
		}

	}

	public void deleteSecondaryContent(LCSDocument lcsDocument) throws WTException, WTPropertyVetoException {
		QueryResult qr = ContentHelper.service.getContentsByRole(lcsDocument, ContentRoleType.SECONDARY);
		while (qr.hasMoreElements()) {
			ContentItem ci = (ContentItem) qr.nextElement();
			ContentServerHelper.service.deleteContent((ContentHolder) lcsDocument, ci);
		}
	}

	private String downloadPrimaryContent(LCSDocument document) throws WTException, PropertyVetoException, IOException {
		String filePath = "";
		document = (LCSDocument) ContentHelper.service.getContents(document);
		ContentItem contentItem = document.getPrimary();
		if (contentItem == null) {
			throw new WTException("Mass Import does not have primary content (Mass Import Excel)");
		}
		ApplicationData appData = (ApplicationData) contentItem;
		LOGGER.debug("File Name: " + appData.getFileName());
		LOGGER.debug("File Format: " + appData.getFormat().getFormatName());
		String fileName = document.getCreatorName() + System.currentTimeMillis() + appData.getFileName();
		filePath = new File(getUploadPath(), fileName).getCanonicalPath();
		ContentServerHelper.service.writeContentStream(appData, filePath);
		return filePath;

	}

	private String writeErrorFile(String fileName, List<String> errors) throws IOException {
		BufferedWriter writer = null;
		String errorFilePath = "";
		try {
			errorFilePath = getUploadPath() + File.separator + fileName;
			writer = new BufferedWriter(new FileWriter(errorFilePath));
			for (String errorMessage : errors) {
				writer.write(errorMessage);
				writer.newLine();
			}
		} catch (IOException e) {
			String err = MassImport.LOG_PREFIX + " Error while writting the error file. " + e.getMessage();
			LOGGER.error(err);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return errorFilePath;
	}

	private String getUploadPath() throws IOException {
		if (temp_filePath == null) {
			WTProperties wt_properties = WTProperties.getLocalProperties();
			String wt_home = wt_properties.getProperty("wt.home");
			temp_filePath = wt_home + FormatHelper.formatOSFolderLocation("\\temp");
		}
		return temp_filePath;
	}
}

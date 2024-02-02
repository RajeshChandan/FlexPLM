package com.hbi.wc.utility;

import java.util.Collection;

import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentQuery;
import com.lcs.wc.document.LCSDocumentLogic;
import com.lcs.wc.document.LCSDocumentClientModel;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.content.ApplicationData;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HBIImagePageContent implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "administrator");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "QAadmin");
	private static RemoteMethodServer remoteMethodServer;
	
	/*Default executable function of the class HBIImagePageContent */
	public static void main(String[] args) 
	{
		LCSLog.debug("### START HBIImagePageContent.main() ###");
		
		try 
		{
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();

			remoteMethodServer = RemoteMethodServer.getDefault();
	        remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
	        remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
	        
	        GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);
	        
	
	        updateContents();
	        System.exit(0);
		}
		catch (Exception exception) 
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIImagePageContent.main() ###");
	}
	
	public static void updateContents() throws WTException, WTPropertyVetoException
	{
				FlexType docType = FlexTypeCache.getFlexTypeFromPath("Document\\Images Page");
		    	LCSDocumentQuery DOCUMENT_QUERY = new LCSDocumentQuery();
				LCSDocumentLogic docLogic = new LCSDocumentLogic();
				
				String docName = "001 : Front/Back Image - Boys Ctn Knit Rib ExpWB1";
				String[] secondaryContentFiles = new String[4];
				secondaryContentFiles[0] = "B252.2252.7764ft.bk-Cell1.png";
				//secondaryContentFiles[1] = "B252.2252.7764ft.bk-Cell1.svg";
				secondaryContentFiles[1] = "B252.2252.7764ft.bk-Cell2.png";
				//secondaryContentFiles[3] = "B252.2252.7764ft.bk-Cell2.svg";
				
		    	LCSDocument document = DOCUMENT_QUERY.findDocumentByNameType(docName,docType);

				if(document != null){
					String oid = FormatHelper.getVersionId(document);
					LCSDocumentClientModel documentModel = new LCSDocumentClientModel();
					documentModel.load(oid);
					
					documentModel.setContentFile("B252.2252.7764ft.bk.ai");
					documentModel.setSecondaryContentFiles(secondaryContentFiles);
					documentModel.save();
				}
		
	}
}
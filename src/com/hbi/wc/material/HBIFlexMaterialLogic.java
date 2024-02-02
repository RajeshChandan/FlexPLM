package com.hbi.wc.material;


import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.DeleteFileHelper;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.util.*;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.document.ZipGenerator;
import com.lcs.wc.document.*;
import com.lcs.wc.client.ClientContext;
import wt.session.SessionHelper;
import wt.org.WTPrincipal;
import wt.doc.WTDocumentMaster;
import wt.util.WTContext;
//import wt.part.WTPartMaster;
import wt.util.WTException;
import java.util.*;
import java.util.regex.Pattern;




public class HBIFlexMaterialLogic {

// Changed for ticket 141702-15 (added isGenericMatSpec )

	public String generateMaterialTechPackImpl(String timeToLive, String mSupplierId, String isGenericMatSpec) throws WTException {

			LCSMaterialSupplier materialSupp = null;
			String fileURL = "";
			String strMType = "";
			HBIPDFMaterialSpecificationGenerator hmsg = null;
			Object[] arguments;

			DeleteFileHelper dFH = new DeleteFileHelper();
			dFH.deleteOldFiles(FileLocation.PDFDownloadLocationImages, timeToLive);
			dFH.deleteOldFiles(FileLocation.PDFDownloadLocationFiles, timeToLive);

			Locale currentCCLocale = ClientContext.getContext().getLocale();
			Locale currentWCLocale = WTContext.getContext().getLocale();
			ZipGenerator zipGen = new ZipGenerator();
			//System.out.println("inside Logic " + isGenericMatSpec);
			
			if(FormatHelper.hasContent(mSupplierId)) {
				materialSupp = (LCSMaterialSupplier)LCSQuery.findObjectById(mSupplierId);
				//WTPartMaster materialMaster = materialSupp.getMaterialMaster();
				LCSMaterialMaster materialMaster = materialSupp.getMaterialMaster();
				LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);	
				strMType = material.getFlexType().getFullNameDisplay(false);
				arguments = new Object[] { materialSupp, isGenericMatSpec };
				hmsg = HBIPDFMaterialSpecificationGenerator.getOverrideInstance(arguments);
				if(strMType.equalsIgnoreCase("Fabric Make")) {	
					hmsg.setOrientation("LANDSCAPE");
				}else{
					hmsg.setOrientation("PORTRAIT");
				}

				fileURL = hmsg.generateSpec();
			}

			try
			{
				fileURL = zipGen.getURL(fileURL);
				System.out.println("inside Logic fileURL " + fileURL);
				//String documentVault = (String)criteria.get("documentVault");
				//String vaultDocumentTypeId = (String)criteria.get("vaultDocumentTypeId");
				String documentVault = "false";
				//String vaultDocumentTypeId = (String)criteria.get("vaultDocumentTypeId");
				String vaultDocumentTypeId = "";
				
				if((documentVault.equals("true")) && (FormatHelper.hasContent(vaultDocumentTypeId))) {
					LCSDocumentClientModel documentModel = new LCSDocumentClientModel();
					FlexType vaultDocumentType = null;

					if(FormatHelper.hasContent(vaultDocumentTypeId)) {
						vaultDocumentType = FlexTypeCache.getFlexType(vaultDocumentTypeId);
					}

					int indx = hmsg.getFilePath().lastIndexOf(".pdf");
					String zipFile = hmsg.getFilePath().substring(0, indx) + fileURL.substring(fileURL.lastIndexOf("."));
					//System.out.println("ZIP File"+zipFile);
					Collection exisitDocuments = LCSQuery.getObjectsFromResults(new LCSDocumentQuery().findPartDocReferences(materialSupp), "OR:com.lcs.wc.document.LCSDocument:", "LCSDocument.IDA2A2");
					LCSDocument exisitDocument = null;
					String exisitDocumentName = "";
					String exisitDocumentSequenceNumber = "";
					int documentSequenceNumber = 0;
					String documentName = "Material Spec_" + materialSupp.getName() ;
					//System.out.println("Docs Name"+documentName);
					Pattern pattern = Pattern.compile("[0-9]*");
					Iterator exisitDocumentIter = exisitDocuments.iterator();
					while (exisitDocumentIter.hasNext()) {
						exisitDocument = (LCSDocument)exisitDocumentIter.next();
						exisitDocumentName = exisitDocument.getName();
						if((!(exisitDocumentName.startsWith(documentName))) || (exisitDocumentName.length() < exisitDocumentName.lastIndexOf(" - ") + 3)) continue;
						exisitDocumentSequenceNumber = exisitDocumentName.substring(exisitDocumentName.lastIndexOf(" - ") + 3);
						if((!(pattern.matcher(exisitDocumentSequenceNumber).matches())) || (Integer.valueOf(exisitDocumentSequenceNumber).intValue() <= documentSequenceNumber)) continue;
						documentSequenceNumber = Integer.valueOf(exisitDocumentSequenceNumber).intValue();
					}
					documentSequenceNumber += 1;
					documentModel.setFlexType(vaultDocumentType);
					//documentModel.setAtt1(documentName + documentSequenceNumber);
					documentModel.setValue("name", documentName + documentSequenceNumber);
					documentModel.save();
					String otherside = FormatHelper.getObjectId((WTDocumentMaster)(WTDocumentMaster)documentModel.getBusinessObject().getMaster());
					String newDocRefIds = otherside;
					//System.out.println("newDocRefIds"+newDocRefIds);
					Collection ids = MOAHelper.getMOACollection(newDocRefIds);

					//documentModel.associateContent(documentModel.getBusinessObject(), zipFile);
					documentModel.associateDocuments(FormatHelper.getObjectId(materialSupp), ids);

				}
				

			}catch(Exception e){
				e.printStackTrace();
				throw new WTException(e);
			}finally {
				WTContext.getContext().setLocale(currentWCLocale);
				ClientContext.getContext().setLocale(currentCCLocale);
			 }
		//System.out.println("File URL"+fileURL);

        return fileURL;
	}

}// end class
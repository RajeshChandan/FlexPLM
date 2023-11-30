package com.hbi.wc.sample;

import java.io.File;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.RetypeLogic;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.report.ReportQuery;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleClientModel;
import com.lcs.wc.sample.LCSSampleHelper;
import com.lcs.wc.sample.LCSSampleQuery;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class HBIAttributeTransferUtility implements RemoteAccess {

	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;

	public static int SAMPLE_QUERY_LIMIT = LCSProperties.get("com.lcs.wc.sample.LCSSampleQuery.queryLimit", 5000);
	private static Logger log = LogManager.getLogger(HBIAttributeTransferUtility.class);
	public static final String PRODUCT_SAMPLE_ROOT_TYPE = LCSProperties
			.get("com.lcs.wc.sample.LCSSample.Product.Fit.Root");

	/* Default executable function of the class HBIAttributeTransferUtility */
	public static void main(String[] args) {
		log.info("### START HBIAttributeTransferUtility.main() ###");
		long start = System.currentTimeMillis();
		try {
			// These contexts are needed for establishing connection to method
			// server- Do not remove these 2 below lines
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			// These contexts are needed for establishing connection to method
			// server- Do not remove these 2 above lines

			remoteMethodServer = RemoteMethodServer.getDefault();
			remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);
			Class[] argumentClass = {};
			Object[] argumentObject = {};
			remoteMethodServer.invoke("validateAndUpdateSample", "com.hbi.wc.sample.HBIAttributeTransferUtility", null,
					argumentClass, argumentObject);

			long end = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.print("Execution time is :-->" + formatter.format((end - start) / 1000d) + " seconds");

			System.out.println("\n####### Ended Remote method server connection, please check logs in migration #####");
			System.out.println("####### Successfully logged off #####");
			System.exit(0);

		} catch (Exception exception) {
			exception.printStackTrace();
			System.exit(1);
		}

		log.info("### END HBIAttributeTransferUtility.main() ###");
	}

	/**
	 * This function is invoking from the default executable function of the
	 * class to initiate the process of transfer of data from sample request to
	 * sample Object
	 * 
	 *
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	public static void validateAndUpdateSample() throws WTException, WTPropertyVetoException, IOException {

		try {

			HBIAttributeTransferUtility.getSamplesandUpdate();

		} catch (Exception Exp) {
			Exp.printStackTrace();
		} finally {

		}

	}

	/**
	 * 
	 * This function retrieves all the fit samples in the system and calls the
	 * method to update data on the sample Object
	 * 
	 * 
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */

	public static void getSamplesandUpdate()

			throws WTException, WTPropertyVetoException {

		FlexType sampleFlexTypeObj = getSampleFlexType(PRODUCT_SAMPLE_ROOT_TYPE);
		System.out.println("Flex Type is " + sampleFlexTypeObj);

		LCSSampleQuery query = new LCSSampleQuery();
		Map criteria = new HashMap();
		criteria.put("fromIndex", 1);
		criteria.put("toIndex", SAMPLE_QUERY_LIMIT);

		SearchResults results = query.findSamplesByCriteria(criteria, sampleFlexTypeObj, null, null, null, null, false,
				SAMPLE_QUERY_LIMIT);
		if (results != null && results.getResultsFound() > 0) {

			updateSamples(results);
		}

	}

	/**
	 * This function set the requested and needed from the sample request to the
	 * sample Object
	 * 
	 * @param results
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */

	public static void updateSamples(SearchResults results)

			throws WTException, WTPropertyVetoException {

		LCSSample sampleObj = null;

		if (results != null && results.getResultsFound() > 0) {

			Collection<FlexObject> sample_Collection = results.getResults();

			Iterator sampleItr = sample_Collection.iterator();
			while (sampleItr.hasNext()) {
				FlexObject sample_FO = (FlexObject) sampleItr.next();

				sampleObj = (LCSSample) LCSQuery
						.findObjectById("OR:com.lcs.wc.sample.LCSSample:" + sample_FO.getString("LCSSample.IDA2A2"));
				if (sampleObj != null)

				{

					LCSSampleRequest sampleReq = sampleObj.getSampleRequest();

					if (sampleReq != null) {

						Date requested = (Date) sampleReq.getValue("sampleRequestRequestDate");

						Date needed = (Date) sampleReq.getValue("hbiSampleNeeded");

						try {

							if (requested != null)
								sampleObj.setValue("hbiSampleRequestDate", requested);
							if (needed != null)
								sampleObj.setValue("hbiSampleNeededNew", needed);

							sampleObj = LCSSampleHelper.service.saveSample(sampleObj);

						} catch (Exception wtpve) {
							wtpve.printStackTrace();

						}

					}

				}
			}

		}

	}

	/**
	 * This method retrieves the Flex type of the sample
	 * 
	 * @param sampleTypePath
	 * @return
	 * @throws WTException
	 */

	public static FlexType getSampleFlexType(String sampleTypePath) throws WTException {

		FlexType sampleFlexTypeObj = null;
		Collection<FlexType> flexTypesColl = new ArrayList<FlexType>();

		// Initialize all FlexTypes
		FlexType rootFlexType = FlexTypeCache.getFlexTypeFromPath("Sample");
		flexTypesColl.add(rootFlexType);
		if (rootFlexType.getAllChildren() != null) {
			flexTypesColl.addAll(rootFlexType.getAllChildren());
		}

		// Iterate FlexTypes collection to validate with the given FlexTypePath
		// and return the corresponding FlexType Object
		for (FlexType flexTypeObj : flexTypesColl) {

			if (sampleTypePath.equalsIgnoreCase(flexTypeObj.getFullNameDisplay(true))) {
				sampleFlexTypeObj = flexTypeObj;
				break;
			}
		}

		return sampleFlexTypeObj;
	}

}
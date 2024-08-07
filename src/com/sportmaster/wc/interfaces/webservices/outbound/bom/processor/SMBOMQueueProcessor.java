package com.sportmaster.wc.interfaces.webservices.outbound.bom.processor;

import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.foundation.LCSQuery;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMBOMQueueProcessor {

	public static void executeQueue(String requestId) throws WTException, WTPropertyVetoException {

		FlexBOMPart bomPart = (FlexBOMPart) LCSQuery.findObjectById("com.lcs.wc.flexbom.FlexBOMPart:" + requestId);
		// retrive bom part for string id
		new SMBOMClient().processBOMRequest(bomPart);

	}
}

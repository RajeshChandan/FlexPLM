package com.hbi.wc.util;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;

public class QBPlugin {
	public static String formatComposite(String fiberContent) {
		
		if(FormatHelper.hasContent(fiberContent)) {
			fiberContent=MOAHelper.parseOutDelims(fiberContent, ",");
		}
		return fiberContent;
	}
}

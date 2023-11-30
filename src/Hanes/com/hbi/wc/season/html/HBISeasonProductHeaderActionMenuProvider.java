package com.hbi.wc.season.html;

import java.util.List;

import com.google.inject.Inject;
import com.lcs.wc.client.ApplicationContext;
import com.lcs.wc.client.web.html.ActionLink;
import com.lcs.wc.client.web.html.JavascriptFunctionCall;
import com.lcs.wc.client.web.html.header.action.CommonActionsProvider;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.product.html.FlexPlmWebRequestContext;
import com.lcs.wc.season.html.SeasonProductHeaderActionMenuProvider;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

import wt.fc.WTObject;
import wt.util.WTException;

/**
 * @author abwajid
 * 
 *This Class is added by Wipro Upgrade team to add the functionality of Revise Action
 */
public class HBISeasonProductHeaderActionMenuProvider extends SeasonProductHeaderActionMenuProvider{
	
	public static final String SELLING_TYPE = LCSProperties.get("com.hbi.wc.product.html.HBISeasonProductHeaderActionMenuProvider.selling", "SELLING");
	public static final String REVISE_LABEL = LCSProperties.get("com.hbi.wc.product.html.HBISeasonProductHeaderActionMenuProvider.reviseLabel", "Revise");
	

	@Inject
	CommonActionsProvider commonActionsProvider;
	
	public HBISeasonProductHeaderActionMenuProvider() {
		super();
	}

	@Override
	public List<ActionLink> getActionMenu(FlexPlmWebRequestContext pwrc) throws WTException {
		List<ActionLink> actionLinks = super.getActionMenu(pwrc);
		ApplicationContext applicationContext = pwrc.getApplicationContext();
		FlexType productFlexType = applicationContext.getProductARev().getFlexType();
		if (pwrc.isLatestProductIteration() && applicationContext.getProductSeasonRev() != null && productFlexType.getFullName().toUpperCase().contains(SELLING_TYPE)) {
			JavascriptFunctionCall action = JavascriptFunctionCall.create("reviseState").addArguments(JavascriptFunctionCall.Argument.asSafeStringLiterals(new String[]{pwrc.getProdActiveId(),"VIEW_PRODUCT",pwrc.getReturnAction()}));
			String productTypeId = FormatHelper.getObjectId((WTObject)productFlexType);
			ActionLink reviseActionLink = this.commonActionsProvider.getLifecycleManagedLink(productTypeId, "Product", pwrc.getProdActiveId(), "VIEW_PRODUCT", pwrc.getReturnTargetOid());
			if (reviseActionLink!=null) {
				reviseActionLink.setAction(action);
				reviseActionLink.setLabel(REVISE_LABEL);
				actionLinks.add(3, reviseActionLink);
			}
		}	
		
		return actionLinks;
	}
	
}

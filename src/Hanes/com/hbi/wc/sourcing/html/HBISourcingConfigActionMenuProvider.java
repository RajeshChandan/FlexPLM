

package com.hbi.wc.sourcing.html;

import com.lcs.wc.util.LCSProperties;
import wt.util.WTException;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import wt.enterprise.RevisionControlled;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.client.web.html.JavascriptFunctionCall;
import com.lcs.wc.client.ClientContext;
import com.lcs.wc.util.ACLHelper;
import java.util.ArrayList;
import com.lcs.wc.client.web.html.ActionLink;

import java.util.List;
import com.lcs.wc.product.html.FlexPlmWebRequestContext;
import wt.util.WTMessage;
import com.lcs.wc.util.RB;
import com.lcs.wc.client.web.html.action.ActionMenuProvider;

public class HBISourcingConfigActionMenuProvider implements ActionMenuProvider
{
    public static final boolean USE_PRIMARY_BOM;
    String editSTSLButton;
    String removeSTSLButton;
    String deleteButton;
    String setAsPrimaryProductButton;
    String setAsPrimarySeasonButton;
    String skuSourcingButton;
    
    public HBISourcingConfigActionMenuProvider() {
        this.editSTSLButton = WTMessage.getLocalizedMessage("com.lcs.wc.resource.SourcingRB", "editSTSL_Btn", RB.objA);
        this.removeSTSLButton = WTMessage.getLocalizedMessage("com.lcs.wc.resource.SourcingRB", "removeSourceFromSeason_OPT", RB.objA);
        this.deleteButton = WTMessage.getLocalizedMessage("com.lcs.wc.resource.MainRB", "delete_Btn", RB.objA);
        this.setAsPrimaryProductButton = WTMessage.getLocalizedMessage("com.lcs.wc.resource.SourcingRB", "setAsPrimaryProduct_Btn", RB.objA);
        this.setAsPrimarySeasonButton = WTMessage.getLocalizedMessage("com.lcs.wc.resource.SeasonRB", "setAsPrimarySeason_Btn", RB.objA);
        this.skuSourcingButton = WTMessage.getLocalizedMessage("com.lcs.wc.resource.SourcingRB", "skuSourcing_Btn", RB.objA);
    }
    
    public List<ActionLink> getActionMenu(final FlexPlmWebRequestContext flexPlmWebRequestContext) throws WTException {
        final List<ActionLink> linkItems = new ArrayList<ActionLink>();
        final LCSSourcingConfig sourcingConfig = flexPlmWebRequestContext.getSourcingConfig();
        if (ACLHelper.hasModifyAccess((Object)sourcingConfig) && !sourcingConfig.isPrimarySource() && !ClientContext.getContext().isVendor) {
// START - HBI 'Primary Source' Enhancement - Disable 'Set as Primary (Product)' option in the UI as this will done from Customization <-->
 if(!("Sourcing Configuration\\Garment".equalsIgnoreCase(sourcingConfig.getFlexType().getFullName(true)) || "Sourcing Configuration\\Pattern".equalsIgnoreCase(sourcingConfig.getFlexType().getFullName(true))))
													{ 

            linkItems.add(new ActionLink("setAsPrimaryProduct", this.setAsPrimaryProductButton, JavascriptFunctionCall.create("setSourceAsPrimary").addArgument(JavascriptFunctionCall.Argument.asSafeStringLiteral(FormatHelper.getVersionId((RevisionControlled)sourcingConfig)))));
        }
}

// END - HBI 'Primary Source' Enhancement - Disable 'Set as Primary (Product)' option in the UI as this will done from Customization <-->
        final LCSSourceToSeasonLink stsl = flexPlmWebRequestContext.getApplicationContext().getSourceToSeasonLink();
        if (stsl != null && ACLHelper.hasModifyAccess((Object)stsl)) {
            if (!stsl.isPrimarySTSL() && !ClientContext.getContext().isVendor) {

//START - HBI 'Primary Source' Enhancement - Disable 'Set as Primary (Product)' option in the UI as this will done from Customization <-->
											
											 if(!("Sourcing Configuration\\Garment".equalsIgnoreCase(sourcingConfig.getFlexType().getFullName(true)) || "Sourcing Configuration\\Pattern".equalsIgnoreCase(sourcingConfig.getFlexType().getFullName(true))))
												{ 
                linkItems.add(new ActionLink("setAsPrimarySeason", this.setAsPrimarySeasonButton, JavascriptFunctionCall.create("setSTSLAsPrimary").addArgument(JavascriptFunctionCall.Argument.asSafeStringLiteral(FormatHelper.getVersionId((RevisionControlled)stsl)))));
            }}

//END - HBI 'Primary Source' Enhancement - Disable 'Set as Primary (Product)' option in the UI as this will done from Customization <-->
            linkItems.add(new ActionLink("updateSTSL", this.editSTSLButton, JavascriptFunctionCall.create("updateSourceToSeason").addArgument(JavascriptFunctionCall.Argument.asSafeStringLiteral(FormatHelper.getVersionId((RevisionControlled)stsl)))));
            linkItems.add(new ActionLink("skuSourcing", this.skuSourcingButton, JavascriptFunctionCall.create("updateSKUSourcing").addArgument(JavascriptFunctionCall.Argument.asSafeStringLiteral(FormatHelper.getVersionId((RevisionControlled)sourcingConfig)))));
            if (!stsl.isPrimarySTSL() && !ClientContext.getContext().isVendor) {
                linkItems.add(new ActionLink("removeSTSL", this.removeSTSLButton, JavascriptFunctionCall.create("removeSourceToSeason").addArgument(JavascriptFunctionCall.Argument.asSafeStringLiteral(FormatHelper.getVersionId((RevisionControlled)stsl)))));
            }
        }
        if (ACLHelper.hasDeleteAccess((Object)sourcingConfig) && !sourcingConfig.isPrimarySource() && !ClientContext.getContext().isVendor) {
            linkItems.add(new ActionLink("deleteSourcingConfig", this.deleteButton, JavascriptFunctionCall.create("removeSourcingConfig").addArgument(JavascriptFunctionCall.Argument.asSafeStringLiteral(FormatHelper.getVersionId((RevisionControlled)sourcingConfig)))));
        }
        return linkItems;
    }
    
    static {
        USE_PRIMARY_BOM = LCSProperties.getBoolean("com.lcs.wc.specification.usePrimaryBOM");
    }
}
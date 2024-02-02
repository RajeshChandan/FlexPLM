package com.lcs.wc.resource;

import wt.util.resource.*;
import com.lcs.wc.util.LCSProperties;

@RBUUID("com.lcs.wc.resource.CustomHbiRB")
public final class CustomHbiRB extends WTListResourceBundle {
   

	@RBEntry("Product Dashboard")
    public static final String PRIVATE_CONSTANT_0 = "productCalendarStatusNewTab_LBL";
   
   @RBEntry("Error: The Default Reference Calendar 'Product Template' doesn't exist. Please create the default reference calendar else select a new reference calendar from below 'Calendar Template' drop-down and click 'Run' button.")
   public static final String PRIVATE_CONSTANT_1 = "noProductTemplate_Msg";

   @RBEntry("Calendar Template")
   public static final String PRIVATE_CONSTANT_3 = "calendarTemplate_LBL";
    
}

package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductDestination;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import org.apache.log4j.Logger;
import wt.method.MethodContext;
import wt.util.WTException;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SMCostSheetStatus {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetStatus.class);

    public static void updateStatusTotalVariations(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: Start.");
        Map<String,String> colorwaysMap = new HashMap<>();
        Map<String,String> productDestinationsMap = new HashMap<>();

        Map csDimLinks = (Map) MethodContext.getContext().get("COSTSHEET_DIM_LINKS");
        if (csDimLinks != null)	{
            Collection colorwayIDs = (Collection) csDimLinks.get("COLORWAYIDS");
            if (colorwayIDs != null) {
                for (Object obj : colorwayIDs) {
                    String repColorId = (String) obj;
                    if (FormatHelper.hasContent(repColorId)) {
                        LCSSKU sku = (LCSSKU) LCSQuery.findObjectById(repColorId);
                        if (sku != null) {
                            String skuMasterId = FormatHelper.getObjectId( sku.getMaster() );
                            String skuName = (String) sku.getValue("skuName");
                            colorwaysMap.put(skuMasterId, skuName);
                            LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: COSTSHEET_DIM_LINKS: COLORWAYIDS: added (" + skuMasterId + "/" + skuName + ")");
                        }
                    }
                }
            }
            Collection destinationIDs = (Collection) csDimLinks.get("DESTINATIONIDS");
            if (destinationIDs != null) {
                for (Object obj : destinationIDs) {
                    String productDestinationID = (String) obj;
                    if (FormatHelper.hasContent(productDestinationID)) {
                        ProductDestination productDestination = (ProductDestination) LCSQuery.findObjectById(productDestinationID);
                        if (productDestination != null) {
                            productDestinationsMap.put(productDestinationID, productDestination.getName());
                            LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: COSTSHEET_DIM_LINKS: DESTINATIONIDS: added (" + productDestinationID + "/" + productDestination.getName() + ")");
                        }
                    }
                }
            }
        } else  {
            colorwaysMap.putAll( SMCostSheetReader.getColorways(lcsProductCostSheet, false) );
            productDestinationsMap.putAll( SMCostSheetReader.getProductDestinations(lcsProductCostSheet, false) );
        }

        if (colorwaysMap.size() == 0 && productDestinationsMap.size() == 0) {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: Finish: " +
                    "Not representative colorway list size = 0, not representative product destination list size = 0.");
            lcsProductCostSheet.setValue("smNotification", "smGreen");
            lcsProductCostSheet.setValue("smNotificationText", "");
            return;
        }

        String representativeSKUMaster[] = new String[] { "", "" };
        String representativeProductDestination[] = new String[] { "", "" };

        if(csDimLinks != null)	{
            String repColorId = (String) csDimLinks.get("REPCOLOR"); // Colorway
            if(FormatHelper.hasContent(repColorId)) {
                LCSSKU sku = (LCSSKU) LCSQuery.findObjectById(repColorId);
                if (sku != null) {
                    representativeSKUMaster[0] = FormatHelper.getObjectId( sku.getMaster() );
                    representativeSKUMaster[1] = (String) sku.getValue("skuName");
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: COSTSHEET_DIM_LINKS: REPCOLOR: set (" + representativeSKUMaster[0] + "/" + representativeSKUMaster[1] + ")");
                }
            }
            String productDestinationID = (String) csDimLinks.get("REPDESTINATION"); // Destination
            if (FormatHelper.hasContent(productDestinationID)) {
                ProductDestination productDestination = (ProductDestination) LCSQuery.findObjectById(productDestinationID);
                if (productDestination != null) {
                    representativeProductDestination[0] = productDestinationID;
                    representativeProductDestination[1] = productDestination.getName();
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: COSTSHEET_DIM_LINKS: REPDESTINATION: set (" + representativeProductDestination[0] + "/" + representativeProductDestination[1] + ")");
                }
            }
        } else {
            representativeSKUMaster = SMCostSheetReader.getRepresentativeLCSSKUMaster(lcsProductCostSheet);
            representativeProductDestination = SMCostSheetReader.getRepresentativeProductDestination(lcsProductCostSheet);
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: Representative colorway master Ref.: " + representativeSKUMaster[0] +
                ", color: " + representativeSKUMaster[1]);
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: Representative product destination Ref.: " + representativeProductDestination[0] +
                ", product destination name: " + representativeProductDestination[1]);

        if (colorwaysMap.size() == 0) {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: " +
                    "Not representative colorway list size = 0. Added empty colorway.");
            colorwaysMap.put("", "");
        }

        if (productDestinationsMap.size() == 0) {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: " +
                    "Not representative product destination list size = 0. Added empty product destination.");
            productDestinationsMap.put("", "");
        }

        Date dateForPrice = SMCostSheetTypeSelector.getDateForPrice(lcsProductCostSheet);

        // added to fix bug with notification. Just substitute currentTotal with one that is retrieved from BOM
        double currentTotal = SMCostSheetTools.getTotalForBOMTable(lcsProductCostSheet, representativeSKUMaster[0], representativeProductDestination[0], dateForPrice);
        // end of the fix
        String message1 = "Note: Colorway(s)/Destination(s) ";
        String message2 = " listed in this Cost Sheet have different material prices in BOM. Individual Cost Sheets should be created for each price.";
        StringBuilder smNotificationText = new StringBuilder();
        String smNotification = "smGreen";
        for(Map.Entry<String,String> colorEntry : colorwaysMap.entrySet()) {
            String masterColorwayRef = colorEntry.getKey();
            String color = colorEntry.getValue();

            for(Map.Entry<String,String> destinationEntry : productDestinationsMap.entrySet()) {
                String productDestinationRef = destinationEntry.getKey();
                String destination = destinationEntry.getValue();

                if (masterColorwayRef.equals(representativeSKUMaster[0]) && productDestinationRef.equals(representativeProductDestination[0])) {
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: Let's skip the variation of this cost sheet.");
                    continue;
                }

                double total = SMCostSheetTools.getTotalForBOMTable(lcsProductCostSheet, masterColorwayRef, productDestinationRef, dateForPrice);
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: " +
                        "Color master reference: " + masterColorwayRef + ", color name: " + color +
                        ", Product Destination reference: " + productDestinationRef + ", destination name: " + destination +
                        ", total: " + total + "; currentTotal: " + currentTotal + "; date: " + dateForPrice);

                if (total != currentTotal) {
                    smNotification = "smRed";
                    if (smNotificationText.length() == 0)
                        smNotificationText.append(message1);
                    else smNotificationText.append(", ");

                    smNotificationText.append(color);
                    if(!color.isEmpty() && !destination.isEmpty())
                        smNotificationText.append("/");
                    smNotificationText.append(destination);
                }
            }
        }
        if (smNotificationText.length() > 0) {
            smNotificationText.append(message2);
        }

        lcsProductCostSheet.setValue("smNotification", smNotification);
        lcsProductCostSheet.setValue("smNotificationText", smNotificationText.toString());
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetStatus.updateStatusTotalVariations: Finish.");
    }
}

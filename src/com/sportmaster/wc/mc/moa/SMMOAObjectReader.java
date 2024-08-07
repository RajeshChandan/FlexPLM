package com.sportmaster.wc.mc.moa;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.mc.SMNmcRows;
import com.sportmaster.wc.mc.sourcing.SMCostSheetConfig;
import com.sportmaster.wc.mc.sourcing.SMCostSheetTypeSelector;
import org.apache.log4j.Logger;
import wt.util.WTException;

import java.util.HashSet;
import java.util.Set;

public class SMMOAObjectReader {

    private static final Logger LOGGER = Logger.getLogger(SMMOAObjectReader.class);

    public static float getCurrencyExchangeRate(LCSProductCostSheet lcsCostSheet, String currency) throws WTException {
        float exchangeRate = -1;
        if(currency == null || currency.trim().isEmpty())
            return exchangeRate;
        Set<String> cs = new HashSet<String>() ;
        LCSMOATable ratesTable = (LCSMOATable) lcsCostSheet.getValue(SMCostSheetConfig.CURRENCY_RATES_TABLE);
        // Existing rows from currency rates table
        if (ratesTable != null) {
            for (Object o : ratesTable.getRows()) {
                FlexObject row = (FlexObject) o;
                String aCurrency = FormatHelper.format(row.getData(SMCostSheetConfig.MOA_TABLE_EXCHANGE_RATES_CURRENCY_VALUE.toUpperCase()));
                cs.add(aCurrency);
                if (aCurrency.equals(currency)) {
                    exchangeRate = FormatHelper.parseFloat(row.getData(SMCostSheetConfig.MOA_TABLE_EXCHANGE_RATES_CURRENCY_RATE.toUpperCase()));
                    break;
                }
            }
        }
        if ((exchangeRate == -1) && !cs.contains(currency) ) exchangeRate = SMMOAObjectTools.addCurrencyRate(lcsCostSheet, currency);
        return exchangeRate;
    }
    public static SMNmcRows readNmcRows(LCSProductCostSheet lcsCostSheet) throws WTException {
        SMNmcRows result = new SMNmcRows();
        FlexObject row; float price=0; String placement="";

        LCSMOATable bomTable = (LCSMOATable) lcsCostSheet.getValue(SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsCostSheet));

        for (Object o : bomTable.getRows()) {
            row = (FlexObject) o;
            String section = (String) row.get("smSection");
            if (section.equals("NMC"))
                price = FormatHelper.parseFloat((String) row.get("smTotal"));
            placement = (String) row.get("smPlacement");

                 if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_UPPER)) result.setUpperForPulloverPrice(price);
            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_SOLE)) result.setSoleForPulloverPrice(price);
            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_LABOR)) result.setLaborPrice(price);
            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_OVERHEAD)) result.setOverheadPrice(price);
            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_NEW_LAST_COST)) result.setNewLastCostPrice(price);
            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_DIE_CUT_COST)) result.setDieCutCostPrice(price);
            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_PROFIT)) result.setProfitPrice(price);
            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_SOURCING)) result.setSourcingComissionPrice(price);
            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_TRANSPORTATION)) result.setTransportationPrice(price);

        }
        return result;
    }
    public static Set<String> getCurrencyList(LCSProductCostSheet lcscostsheet) throws WTException {

       Set<String> result = new HashSet<String>() ;
       result.add ((String)lcscostsheet.getValue("smCsContractCurrency"));
        FlexObject row;
        LCSMOATable bomTable = (LCSMOATable) lcscostsheet.getValue(SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcscostsheet));
        if (bomTable != null) {
            for (Object o : bomTable.getRows()) {
                row = (FlexObject) o;
                String currency = (String) row.get("smMSCurrency");
                result.add(currency);
            }
        }
       LOGGER.debug("CUSTOM>>>>>> SMMOAObjectReader.getCurrencyList: Result is (" + result.toString() +")");
       return result;
    }
}

package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.mc.config.SMCostSheetMOATableConfig;
import org.apache.log4j.Logger;
import wt.util.WTException;

import java.util.*;

public class SMCostSheetUpdate {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetUpdate.class);

    private LCSProductCostSheet lcsProductCostSheet = null;
    private FlexType moaTableType = null;
    private String costSheetMOATableAttName = null;
    private Boolean isAPD = null;
    private Boolean isSEPD = null;

    private SMCostSheetUpdate() {}

    public static SMCostSheetUpdate newSMCostSheetUpdateForBOMTable(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        SMCostSheetUpdate obj = new SMCostSheetUpdate();
        obj.lcsProductCostSheet = lcsProductCostSheet;
        obj.costSheetMOATableAttName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet);
        obj.moaTableType = SMCostSheetTypeSelector.getSMCostSheetBOMTableType(lcsProductCostSheet);
        obj.isAPD = SMCostSheetTypeSelector.isAPD(lcsProductCostSheet);
        obj.isSEPD = SMCostSheetTypeSelector.isSEPD(lcsProductCostSheet);
        return obj;
    }

    public void copyIDsAndHiddenAttrsForBOMTable(String attrKeySource, String attrKeyTarget, Collection target) throws WTException {
        Set<String> listAttrKeySource = new HashSet<>();
        listAttrKeySource.add(attrKeySource);
        Set<String> listAttrKeyTarget = new HashSet<>();
        listAttrKeyTarget.add(attrKeyTarget);
        List<String> listAttributesForCopy = new ArrayList<>();
        listAttributesForCopy.addAll(SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.MOA_TABLE_ATTRIBUTE_ROW_ID ));
        listAttributesForCopy.addAll(SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.MOA_TABLE_HIDE_ATTRIBUTES_COPY ));
        if (isAPD) {
            listAttributesForCopy.addAll(SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.MOA_TABLE_HIDE_ATTRIBUTES_COPY_ADDITIONAL_APD ));
        }
        if (isSEPD) {
            listAttributesForCopy.addAll(SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.MOA_TABLE_HIDE_ATTRIBUTES_COPY_ADDITIONAL_SEPD ));
        }
        copyCurrentValuesForBOMTable(listAttrKeySource, listAttrKeyTarget, listAttributesForCopy, target);
    }

    public void copyCurrentValuesForBOMTable(String listAttributesForCopy, Collection target) throws WTException {
        Set<String> listAttrKey = new HashSet<>();
        listAttrKey.addAll(SMCostSheetConfig.getValues(SMCostSheetMOATableConfig.MOA_TABLE_ATTRIBUTE_ROW_ID));
        listAttrKey.add("smMaterial");
        listAttrKey.add("smColor");
        listAttrKey.add("smSupplier");
        copyCurrentValuesForBOMTable(listAttrKey, listAttrKey, SMCostSheetConfig.getValues( listAttributesForCopy ), target);
    }

    private void copyCurrentValuesForBOMTable(Set<String> listAttrKeySource, Set<String> listAttrKeyTarget,
                                             List<String> listAttributesForCopy, Collection target) throws WTException {

        if (target == null) {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetUpdate.copyCurrentValuesForBOMTable: target is NULL");
            return;
        }
        if (target.size() == 0) {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetUpdate.copyCurrentValuesForBOMTable: target size = 0");
            return;
        }

        List<String> attributesForRead = new ArrayList<>(listAttributesForCopy);
        attributesForRead.addAll(listAttrKeySource);

        Collection currentValuesSource = SMCostSheetReader.getMOATableRows(lcsProductCostSheet,
                moaTableType, costSheetMOATableAttName, attributesForRead);

        copyCurrentValues(listAttrKeySource, listAttrKeyTarget, listAttributesForCopy, currentValuesSource, target);
    }

    private static void copyCurrentValues(Set<String> listAttrKeySource, Set<String> listAttrKeyTarget,
                                          List<String> listAttributesForCopy, Collection source, Collection target) {

        if (listAttributesForCopy == null || listAttributesForCopy.size() == 0)
            return;
        if (source == null || source.size() == 0)
            return;
        if (target == null)
            return;

        for (Object objectTarget : target) {
            FlexObject flexObjectTarget = (FlexObject) objectTarget;
            String keyTarget = combineValues(listAttrKeyTarget, flexObjectTarget); // FormatHelper.format( flexObjectTarget.getData(attrKeyTarget) );

            for (Object objectSource : source) {
                FlexObject flexObjectSource = (FlexObject) objectSource;
                String keySource = combineValues(listAttrKeySource, flexObjectSource); //FormatHelper.format( flexObjectSource.getData(attrKeySource) );
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetUpdate.copyCurrentValues: keySource= " + keySource + ", keyTarget= " + keyTarget);
                if ( keySource.equals(keyTarget) ) {
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetUpdate.copyCurrentValues: flexObjectSource = " + flexObjectSource);
                    for (String key : listAttributesForCopy) {
                        Object value = flexObjectSource.get(key);
                        if(value != null) {
                            LOGGER.debug("CUSTOM>>>>>> SMCostSheetUpdate.copyCurrentValues: put (key = " + key + ", value = " + value + ")");
                            flexObjectTarget.put(key, value);
                        }
                    }
                    break;
                }
            }
        }
    }

    private static boolean equals(Set<String> listAttrKeySource, FlexObject flexObjectSource,
                                  Set<String> listAttrKeyTarget, FlexObject flexObjectTarget) {

        String valueSource = combineValues(listAttrKeySource, flexObjectSource);
        String valueTarget = combineValues(listAttrKeyTarget, flexObjectTarget);

        return valueSource.equals(valueTarget);
    }

    private static String combineValues(Set<String> listAttrKey, FlexObject flexObject) {

        String result = null;

        for (String key : listAttrKey) {
            String value = FormatHelper.format( flexObject.getData(key) );
            if (result == null) result = value;
            else result += "|~*~|" + value;
        }

        return result;
    }
}

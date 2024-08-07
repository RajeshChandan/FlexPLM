package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.client.web.TableData;
import com.lcs.wc.client.web.TableDataUtil;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.util.SortHelper;
import wt.util.WTException;

import java.util.*;

public class SMCostSheetSorter {

    /**
     * Method sorted rows by section and SORTINGNUMBER (inside section)
     * @param data - Rows collection of BOM Table
     * @param type - FlexBOMLink type
     * @return
     * @throws WTException
     */
    public static Collection sortTopLevel(Collection data, FlexType type) throws WTException {
        if(data == null) return new ArrayList();
        if(data.size() < 1) return data;

        List groupedList = new ArrayList();

        Map table = TableDataUtil.groupIntoCollections(data, type.getAttribute("section").getSearchResultIndex());
        Collection sectionAttrDef = type.getAttribute("section").getAttValueList().getSelectableKeys(null, true);
        for(Object object : sectionAttrDef) {
            String key = (String) object;
            Collection subSet = (Collection) table.get(key);
            if(subSet != null) {
                subSet = SortHelper.sortFlexObjectsByNumber(subSet,"FLEXBOMLINK.SORTINGNUMBER");
                groupedList.addAll(subSet);
            }
        }

        return groupedList;
    }

    public static Collection groupDataToBranchId(Collection data, FlexType type) throws WTException {
        if(data == null) return new ArrayList();
        if(data.size() < 1) return data;

        //Get group by MASTERBRANCHID
        Map table = TableDataUtil.groupIntoCollections(data, "FLEXBOMLINK.MASTERBRANCHID");
        //MASTERBRANCHID = 0
        List current = (List) table.get("0");
        if(current == null) return new ArrayList();

        // List sorted rows by section and SORTINGNUMBER (inside section)
        current = new ArrayList(sortTopLevel(current, type));
        table.remove("0");

        Set keysGroupBy = table.keySet();
        List groupedList = new ArrayList();

        for (int i = 0; i<current.size(); i++) {
            TableData td = (TableData) current.get(i);
            groupedList.add(td);
            String value = td.getData("FLEXBOMLINK.BRANCHID");
            if(keysGroupBy.contains(value)) {

                //LC: Sort the sub bom items by partName before adding to the list
                Collection colSubBranches = (Collection)table.get(value);
                colSubBranches = SortHelper.sortFlexObjectsByNumber(colSubBranches,"FLEXBOMLINK.SORTINGNUMBER");

                groupedList.addAll( colSubBranches );
            }
        }

        return groupedList;
    }
}

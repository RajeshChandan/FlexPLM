package com.limited.exporter.processor;

import com.lcs.wc.db.SearchResults;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.limited.exporter.VSDataExportBean;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.util.WTException;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VSMaterialMOADataProcessorImpl implements VSDataProcessor {
    private static final Logger logger = LogR.getLogger (VSMaterialMOADataProcessorImpl.class.getName ());

    public static final String DEFAULT_ENCODING = LCSProperties.get ("com.lcs.wc.util.CharsetFilter.Charset", "UTF-8");
    private VSDataExportBean bean;

    @Override
    public void processData (SearchResults results, VSDataExportBean exportBean) throws FileNotFoundException, WTException {
        bean = exportBean;
        String dateRange = "";
        if (FormatHelper.hasContent (bean.getStartDate ()) && FormatHelper.hasContent (bean.getEndDate ())) {
            dateRange = bean.getStartDate () + "_" + bean.getEndDate () + "_";
        }

        SearchResults searchResults = getMaterial (exportBean);

    }


    private SearchResults getMaterial (VSDataExportBean exportBean) {

     LCSMaterialQuery qry = new LCSMaterialQuery ();
        FlexType materialType;
        Collection attCols = null;
        Map criteria = new HashMap();
        criteria.put("includeSupplier", "true");
        //criteria.put("filterInactive", "true");
        SearchResults constructionMaterialData = new LCSMaterialQuery().findMaterialsByCriteria(criteria, materialType, attCols, null, oidListCB);
        dataResults = constructionMaterialData.getResults();

        return constructionMaterialData;
    }

}


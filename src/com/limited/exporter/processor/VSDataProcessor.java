package com.limited.exporter.processor;

import com.lcs.wc.db.SearchResults;
import com.limited.exporter.VSDataExportBean;
import wt.util.WTException;

import java.io.FileNotFoundException;

public interface VSDataProcessor {

    void processData (SearchResults results, VSDataExportBean bean) throws FileNotFoundException, WTException;

}

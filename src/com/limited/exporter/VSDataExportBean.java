package com.limited.exporter;

import java.util.List;
import java.util.Map;

public class VSDataExportBean {

    private String name;
    private String startDate;
    private String endDate;
    private String flexType;
    private String flexSubType;
    private String flexTypeClass;
    private String extraction;
    private String thumbnailLoc;
    private List<String> attrs;
    private Map<String, List<String>> objAttrs;

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getStartDate () {
        return startDate;
    }

    public void setStartDate (String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate () {
        return endDate;
    }

    public void setEndDate (String endDate) {
        this.endDate = endDate;
    }

    public String getFlexType () {
        return flexType;
    }

    public String getExtraction () {
        return extraction;
    }

    public void setExtraction (String extraction) {
        this.extraction = extraction;
    }

    public void setFlexType (String flexType) {
        this.flexType = flexType;
        switch (flexType) {
            case "com.lcs.wc.color.LCSColor":
                this.flexTypeClass = "LCSColor";
                break;
            case "com.lcs.wc.flexbom.FlexBOMPart":
                this.flexTypeClass = "FlexBOMPart";
                break;
            case "com.lcs.wc.season.LCSSeason":
                this.flexTypeClass = "LCSSeason";
                break;
            default:
                break;
        }
    }

    public String getFlexSubType () {
        return flexSubType;
    }

    public void setFlexSubType (String flexSubType) {
        this.flexSubType = flexSubType;
    }

    public String getFlexTypeClass () {
        return flexTypeClass;
    }

    public void setFlexTypeClass (String flexTypeClass) {
        this.flexTypeClass = flexTypeClass;
    }

    public String getThumbnailLoc () {
        return thumbnailLoc;
    }

    public void setThumbnailLoc (String thumbnailLoc) {
        this.thumbnailLoc = thumbnailLoc;
    }

    public List<String> getAttrs () {
        return attrs;
    }

    public void setAttrs (List<String> attrs) {
        this.attrs = attrs;
    }

    public Map<String, List<String>> getObjAttrs () {
        return objAttrs;
    }

    public void setObjAttrs (Map<String, List<String>> objAttrs) {
        this.objAttrs = objAttrs;
    }

    public VSDataExportBean (String name, String startDate, String endDate, String flexType, String flexSubType, String flexTypeClass, String thumbnailLoc, List<String> attrs, Map<String, List<String>> objAttrs) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.flexType = flexType;
        this.flexSubType = flexSubType;
        this.flexTypeClass = flexTypeClass;
        this.thumbnailLoc = thumbnailLoc;
        this.attrs = attrs;
        this.objAttrs = objAttrs;
    }

    public VSDataExportBean () {
        super ();
    }

    @Override
    public String toString () {
        return "VSDataExportBean{" +
                "name='" + name + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", flexType='" + flexType + '\'' +
                ", flexSubType='" + flexSubType + '\'' +
                ", flexTypeClass='" + flexTypeClass + '\'' +
                ", extraction='" + extraction + '\'' +
                ", thumbnailLoc='" + thumbnailLoc + '\'' +
                ", attrs=" + attrs +
                ", objAttrs=" + objAttrs +
                '}';
    }
}

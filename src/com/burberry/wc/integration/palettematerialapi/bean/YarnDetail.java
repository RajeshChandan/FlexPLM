
package com.burberry.wc.integration.palettematerialapi.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "warpWeft",
    "yarnType",
    "yarnProcess",
    "yarnSize",
    "yarnUOM",
    "yarnTwist",
    "yarnFibre",
    "yarnPly",
    "yarnUniqId",
    "CRUD"
})
public class YarnDetail implements Serializable
{

    /**
     * Warp/Weft
     * 
     */
    @JsonProperty("warpWeft")
    @JsonPropertyDescription("Warp/Weft")
    private String warpWeft;
    /**
     * Yarn Type
     * 
     */
    @JsonProperty("yarnType")
    @JsonPropertyDescription("Yarn Type")
    private String yarnType;
    /**
     * Yarn Process
     * 
     */
    @JsonProperty("yarnProcess")
    @JsonPropertyDescription("Yarn Process")
    private String yarnProcess;
    /**
     * Yarn Size
     * 
     */
    @JsonProperty("yarnSize")
    @JsonPropertyDescription("Yarn Size")
    private Integer yarnSize;
    /**
     * UOM
     * 
     */
    @JsonProperty("yarnUOM")
    @JsonPropertyDescription("UOM")
    private String yarnUOM;
    /**
     * Number of Twists
     * 
     */
    @JsonProperty("yarnTwist")
    @JsonPropertyDescription("Number of Twists")
    private String yarnTwist;
    /**
     * Fibre Content (%)
     * 
     */
    @JsonProperty("yarnFibre")
    @JsonPropertyDescription("Fibre Content (%)")
    private String yarnFibre;
    /**
     * Ply
     * 
     */
    @JsonProperty("yarnPly")
    @JsonPropertyDescription("Ply")
    private String yarnPly;
    /**
     * Yarn Details Unique ID- internal key
     * 
     */
    @JsonProperty("yarnUniqId")
    @JsonPropertyDescription("Yarn Details Unique ID- internal key")
    private String yarnUniqId;
    /**
     * Return 'DELETE' in case a Yarn row is deleted
     * 
     */
    @JsonProperty("CRUD")
    @JsonPropertyDescription("Return 'DELETE' in case a Yarn row is deleted")
    private String cRUD;
    private final static long serialVersionUID = -1735252448699098117L;

    /**
     * Warp/Weft
     * 
     */
    @JsonProperty("warpWeft")
    public String getWarpWeft() {
        return warpWeft;
    }

    /**
     * Warp/Weft
     * 
     */
    @JsonProperty("warpWeft")
    public void setWarpWeft(String warpWeft) {
        this.warpWeft = warpWeft;
    }

    /**
     * Yarn Type
     * 
     */
    @JsonProperty("yarnType")
    public String getYarnType() {
        return yarnType;
    }

    /**
     * Yarn Type
     * 
     */
    @JsonProperty("yarnType")
    public void setYarnType(String yarnType) {
        this.yarnType = yarnType;
    }

    /**
     * Yarn Process
     * 
     */
    @JsonProperty("yarnProcess")
    public String getYarnProcess() {
        return yarnProcess;
    }

    /**
     * Yarn Process
     * 
     */
    @JsonProperty("yarnProcess")
    public void setYarnProcess(String yarnProcess) {
        this.yarnProcess = yarnProcess;
    }

    /**
     * Yarn Size
     * 
     */
    @JsonProperty("yarnSize")
    public Integer getYarnSize() {
        return yarnSize;
    }

    /**
     * Yarn Size
     * 
     */
    @JsonProperty("yarnSize")
    public void setYarnSize(Integer yarnSize) {
        this.yarnSize = yarnSize;
    }

    /**
     * UOM
     * 
     */
    @JsonProperty("yarnUOM")
    public String getYarnUOM() {
        return yarnUOM;
    }

    /**
     * UOM
     * 
     */
    @JsonProperty("yarnUOM")
    public void setYarnUOM(String yarnUOM) {
        this.yarnUOM = yarnUOM;
    }

    /**
     * Number of Twists
     * 
     */
    @JsonProperty("yarnTwist")
    public String getYarnTwist() {
        return yarnTwist;
    }

    /**
     * Number of Twists
     * 
     */
    @JsonProperty("yarnTwist")
    public void setYarnTwist(String yarnTwist) {
        this.yarnTwist = yarnTwist;
    }

    /**
     * Fibre Content (%)
     * 
     */
    @JsonProperty("yarnFibre")
    public String getYarnFibre() {
        return yarnFibre;
    }

    /**
     * Fibre Content (%)
     * 
     */
    @JsonProperty("yarnFibre")
    public void setYarnFibre(String yarnFibre) {
        this.yarnFibre = yarnFibre;
    }

    /**
     * Ply
     * 
     */
    @JsonProperty("yarnPly")
    public String getYarnPly() {
        return yarnPly;
    }

    /**
     * Ply
     * 
     */
    @JsonProperty("yarnPly")
    public void setYarnPly(String yarnPly) {
        this.yarnPly = yarnPly;
    }

    /**
     * Yarn Details Unique ID- internal key
     * 
     */
    @JsonProperty("yarnUniqId")
    public String getYarnUniqId() {
        return yarnUniqId;
    }

    /**
     * Yarn Details Unique ID- internal key
     * 
     */
    @JsonProperty("yarnUniqId")
    public void setYarnUniqId(String yarnUniqId) {
        this.yarnUniqId = yarnUniqId;
    }

    /**
     * Return 'DELETE' in case a Yarn row is deleted
     * 
     */
    @JsonProperty("CRUD")
    public String getCRUD() {
        return cRUD;
    }

    /**
     * Return 'DELETE' in case a Yarn row is deleted
     * 
     */
    @JsonProperty("CRUD")
    public void setCRUD(String cRUD) {
        this.cRUD = cRUD;
    }

}

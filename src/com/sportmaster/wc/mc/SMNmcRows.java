package com.sportmaster.wc.mc;

public class SMNmcRows {
    // Class to hold complex NMC Row info information

 /*   private String upperForPullover;
    private String soleForPullover;
    private String labor;
    private String overhead;
    private String newLastCost;
    private String dieCutCost;
    private String profit;
    private String sourcingComission;
    private String transportation;

  */

    private double upperForPulloverPrice;
    private double soleForPulloverPrice;
    private double laborPrice;
    private double overheadPrice;
    private double newLastCostPrice;
    private double dieCutCostPrice;
    private double profitPrice;
    private double sourcingComissionPrice;
    private double transportationPrice;


    public SMNmcRows() {
        upperForPulloverPrice = 0;
        soleForPulloverPrice = 0;
        laborPrice = 0;
        overheadPrice = 0;
        newLastCostPrice = 0;
        dieCutCostPrice = 0;
        profitPrice = 0;
        sourcingComissionPrice = 0;
        transportationPrice = 0;
    }


    public double getUpperForPulloverPrice() {
        return upperForPulloverPrice;
    }

    public void setUpperForPulloverPrice(double upperForPulloverPrice) {
        this.upperForPulloverPrice = upperForPulloverPrice;
    }

    public double getSoleForPulloverPrice() {
        return soleForPulloverPrice;
    }

    public void setSoleForPulloverPrice(double soleForPulloverPrice) {
        this.soleForPulloverPrice = soleForPulloverPrice;
    }

    public double getLaborPrice() {
        return laborPrice;
    }

    public void setLaborPrice(double laborPrice) {
        this.laborPrice = laborPrice;
    }

    public double getOverheadPrice() {
        return overheadPrice;
    }

    public void setOverheadPrice(double overheadPrice) {
        this.overheadPrice = overheadPrice;
    }

    public double getNewLastCostPrice() {
        return newLastCostPrice;
    }

    public void setNewLastCostPrice(double newLastCostPrice) {
        this.newLastCostPrice = newLastCostPrice;
    }

    public double getDieCutCostPrice() {
        return dieCutCostPrice;
    }

    public void setDieCutCostPrice(double dieCutCostPrice) {
        this.dieCutCostPrice = dieCutCostPrice;
    }

    public double getProfitPrice() {
        return profitPrice;
    }

    public void setProfitPrice(double profitPrice) {
        this.profitPrice = profitPrice;
    }

    public double getSourcingComissionPrice() {
        return sourcingComissionPrice;
    }

    public void setSourcingComissionPrice(double sourcingComissionPrice) {
        this.sourcingComissionPrice = sourcingComissionPrice;
    }

    public double getTransportationPrice() {
        return transportationPrice;
    }

    public void setTransportationPrice(double transportationPrice) {
        this.transportationPrice = transportationPrice;
    }
}

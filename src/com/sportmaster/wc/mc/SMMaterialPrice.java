package com.sportmaster.wc.mc;

// Class to hold complex Price information
public class SMMaterialPrice {
    private double price = 0.0D;
    private double priceLC = 0.0D;
    private String localCurrency = "";

    public SMMaterialPrice() {
    }

    public void setPrice(double pr) {
        price = pr;
    }

    public void setPriceLC(double prlc) {
        priceLC = prlc;
    }

    public void setLocalCurrency(String curr) {
        localCurrency = curr;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceLC() {
        return priceLC;
    }

    public String getLocalCurrency() {
        return localCurrency;
    }
}

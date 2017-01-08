package com.mola.charts;

public enum Pair {
//    EUR_USD(1);//, GBP_USD(2);//, USD_JPY(3), USD_CHF(4), EUR_GBP(5), EUR_JPY(6),
    // EUR_CHF(7), AUD_USD(8), USD_CAD(9), NZD_USD(
    // 10),
     USD_TRY(11);

    int pair;

    Pair(int pair) {
        this.pair = pair;
    }

    public String getNameFormatted(){
        return this.name().replaceAll("_", "/");
    }
    public int getValue() {
        return this.pair;
    }
}
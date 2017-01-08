package com.mola.charts;

public enum ChartType {
    candles(1), ema(2), sma(3), stoch(4);

    int type;

    ChartType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }
}
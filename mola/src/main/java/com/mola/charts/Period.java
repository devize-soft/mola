package com.mola.charts;

public enum Period {
    SECOND(1000), MINUTE(60000), HOUR(3600000), DAY(86400000), WEEK(604800000), MONTH(
            2.62974e9), YEAR(3.15569e10);
    private final double id;

    Period(double id) {
        this.id = id;
    }

    public double getValue() {
        return id;
    }
}
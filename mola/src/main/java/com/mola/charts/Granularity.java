package com.mola.charts;

import java.util.Calendar;
import java.util.Date;

public enum Granularity {
    // S5, S10, S15, S30, M1, M2, M3, M4, M5, M10, M15, M30, H1, H2, H3, H4, H6,
    // H8, H12, D, W, M
    M1(60 * 1000), M5((60 * 5) * 1000), M30(1800000), H1(3600000), H2(H1.getValue() * 2), D(H1.getValue() * 24), W(D.getValue()*7);
    // D(1), H2(2), H1(3), M30(4);

    int granularity;

    Granularity(int granularity) {
        this.granularity = granularity;
    }

    public int getValue() {
        return this.granularity;
    }

    public double getTimeFromFraction(double fraction){
        switch(this){
            case M1:
                return fraction * 60;
            default:
                return -1;
        }
    }

    public double getMillisecsFromFraction(double value){
        String[] split = String.valueOf(value).split("\\.");
        double integer = Double.parseDouble(split[0]);
        double fraction = Math.abs(integer - value);
        switch (this){
            case M1:
                return (((integer * 60) * 1000) + ((fraction * 60)*1000));
        }
        return -1;
    }

    public int getCalendarValue(){
        switch(this){
            case M1:
                return Calendar.MINUTE;
            default:
                return -1;
        }
    }

    public static long getMaxStartDate(Granularity gran, Date end) {
        return (end.getTime() - Double.valueOf(gran.getValue()).longValue());
    }
}

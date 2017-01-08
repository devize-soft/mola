package com.mola.util;

/**
 * Created by bilgi on 3/22/15.
 */
public class MathUtils {

    public static double stdDev(Double[] a) {
        double[] temp = new double[a.length];
        for(int i=0; i < a.length;++i){
            assert(a[i] != null);
            temp[i] = a[i];
        }
        return Math.sqrt(var(temp));
    }

    //http://introcs.cs.princeton.edu/java/stdlib/StdStats.java.html
    public static double stdDev(double[] a) {
        return Math.sqrt(var(a));
    }

    public static double var(double[] a) {
        if (a.length == 0) return Double.NaN;
        double avg = mean(a);
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - avg) * (a[i] - avg);
        }
        return sum / (a.length - 1);
    }

    public static double mean(Double[] a) {
        if (a.length == 0) return Double.NaN;
        double[] temp = new double[a.length];
        for(int i=0; i < a.length;++i){
            assert(a[i] != null);
            temp[i] = a[i];
        }
        double sum = sum(temp);
        return sum / temp.length;
    }

    public static double mean(double[] a) {
        if (a.length == 0) return Double.NaN;
        double sum = sum(a);
        return sum / a.length;
    }

    public static double sum(double[] a) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

    /**
     * Returns the average value in the subarray a[lo..hi], NaN if no such value.
     */
    public static double mean(double[] a, int lo, int hi) {
        int length = hi - lo + 1;
        if (lo < 0 || hi >= a.length || lo > hi)
            throw new RuntimeException("Subarray indices out of bounds");
        if (length == 0) return Double.NaN;
        double sum = sum(a, lo, hi);
        return sum / length;
    }

    /**
     * Returns the average value in the array a[], NaN if no such value.
     */
    public static double mean(int[] a) {
        if (a.length == 0) return Double.NaN;
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i];
        }
        return sum / a.length;
    }

    public static double sum(double[] a, int lo, int hi) {
        if (lo < 0 || hi >= a.length || lo > hi)
            throw new RuntimeException("Subarray indices out of bounds");
        double sum = 0.0;
        for (int i = lo; i <= hi; i++) {
            sum += a[i];
        }
        return sum;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

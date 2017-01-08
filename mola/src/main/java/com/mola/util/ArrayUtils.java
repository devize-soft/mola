package com.mola.util;

public class ArrayUtils {

    public static int[] arrayToInt(String[] array) {
        int ret[] = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            ret[i] = Integer.valueOf(array[i]).intValue();
        }
        return ret;
    }
}

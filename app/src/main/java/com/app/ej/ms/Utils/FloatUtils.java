package com.app.ej.ms.Utils;

public class FloatUtils {

    public static float getMaxValue(float[] array) {
        float value = Integer.MIN_VALUE;

        if (array.length <= 0) {
            throw new IllegalArgumentException("Array is empty.");
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i] > value) {
                value = array[i];
            }
        }
        return value;
    }

    public static float getMinValue(float[] array) {
        float value = Integer.MAX_VALUE;

        if (array.length <= 0) {
            throw new IllegalArgumentException("Array is empty.");
        }

        for (int i=0; i < array.length; i++) {
            if (array[i] < value) {
                value = array[i];
            }
        }
        return value;
    }
}
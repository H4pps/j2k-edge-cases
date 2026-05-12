package org.example.edgecases;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Exercises varargs, primitive/object arrays, and array covariance edge patterns.
 */
public final class VarargsArraysCase {

    /**
     * Sums int varargs.
     *
     * @param values numbers to sum
     * @return sum of values
     */
    public int sum(int... values) {
        return Arrays.stream(values).sum();
    }

    /**
     * Joins values with a prefix while preserving array input type.
     *
     * @param prefix output prefix
     * @param values values to join
     * @return joined text
     */
    public String joinWithPrefix(String prefix, String[] values) {
        StringJoiner joiner = new StringJoiner(",", prefix + "[", "]");
        for (String value : values) {
            joiner.add(value);
        }
        return joiner.toString();
    }

    /**
     * Flattens a vararg of arrays.
     *
     * @param chunks chunks to flatten
     * @return flattened array
     */
    public String[] flatten(String[]... chunks) {
        int size = 0;
        for (String[] chunk : chunks) {
            size += chunk.length;
        }
        String[] flattened = new String[size];
        int index = 0;
        for (String[] chunk : chunks) {
            for (String item : chunk) {
                flattened[index++] = item;
            }
        }
        return flattened;
    }
}

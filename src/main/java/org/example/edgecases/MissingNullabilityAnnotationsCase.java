package org.example.edgecases;

import java.util.Map;

/**
 * Exercises Java APIs with no nullability annotations where J2K must infer cautiously.
 */
public final class MissingNullabilityAnnotationsCase {
    private final Map<String, String> values;

    /**
     * Creates a lookup wrapper without nullability annotations.
     *
     * @param values backing map
     */
    public MissingNullabilityAnnotationsCase(Map<String, String> values) {
        this.values = values;
    }

    /**
     * Returns a value that may be null, but the method has no annotation.
     *
     * @param key lookup key
     * @return mapped value or null
     */
    public String findOrNull(String key) {
        return values.get(key);
    }

    /**
     * Uses explicit Java null checks around an unannotated lookup.
     *
     * @param key lookup key
     * @param defaultValue fallback length
     * @return looked-up value length or fallback
     */
    public int lengthOrDefault(String key, int defaultValue) {
        String value = findOrNull(key);
        return value == null ? defaultValue : value.length();
    }

    /**
     * Picks the first non-null argument without annotations.
     *
     * @param primary primary value
     * @param fallback fallback value
     * @return selected value
     */
    public String chooseFirst(String primary, String fallback) {
        return primary != null ? primary : fallback;
    }
}

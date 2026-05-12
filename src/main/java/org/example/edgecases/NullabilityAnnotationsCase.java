package org.example.edgecases;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Exercises explicit nullability annotations on fields, parameters, and return values.
 */
public final class NullabilityAnnotationsCase {

    private final Map<String, String> values;

    /**
     * Creates a new nullability demo class.
     *
     * @param values backing map
     */
    public NullabilityAnnotationsCase(@NotNull Map<String, String> values) {
        this.values = values;
    }

    /**
     * Returns mapped value or null.
     *
     * @param key map key
     * @return mapped value if present
     */
    public @Nullable String find(@NotNull String key) {
        return values.get(key);
    }

    /**
     * Requires non-null value and replaces null with fallback.
     *
     * @param key lookup key
     * @param fallback fallback value
     * @return non-null string
     */
    public @NotNull String requireOrFallback(@NotNull String key, @NotNull String fallback) {
        String value = find(key);
        return value == null ? fallback : value;
    }
}

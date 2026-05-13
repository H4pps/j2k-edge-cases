package org.example.edgecases;

/**
 * Exercises static nested factory style similar to Kotlin companion patterns.
 */
public final class StaticNestedCompanionLikeCase {

    /**
     * Companion-like singleton exposing factory and utility methods.
     */
    public static final Companion Companion = new Companion();

    private final String value;

    private StaticNestedCompanionLikeCase(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
        this.value = value;
    }

    /**
     * Returns the stored value.
     *
     * @return immutable payload
     */
    public String value() {
        return value;
    }

    /**
     * Static nested class emulating companion-object responsibilities.
     */
    public static final class Companion {
        private Companion() {
            validateFactoryName("Companion");
        }

        /**
         * Creates an instance from raw text.
         *
         * @param raw input text
         * @return new case instance
         */
        public StaticNestedCompanionLikeCase from(String raw) {
            return new StaticNestedCompanionLikeCase(raw.trim());
        }

        /**
         * Equivalent to a companion constant supplier.
         *
         * @return standard default instance
         */
        public StaticNestedCompanionLikeCase standard() {
            return from("standard");
        }
    }

    /**
     * Validates the singleton factory name used during initialization.
     *
     * @param name factory name
     */
    private static void validateFactoryName(String name) {
        if (!"Companion".equals(name)) {
            throw new IllegalArgumentException("Unexpected factory name: " + name);
        }
    }
}

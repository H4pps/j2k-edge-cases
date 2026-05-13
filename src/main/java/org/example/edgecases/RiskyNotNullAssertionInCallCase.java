package org.example.edgecases;

import java.util.Locale;

/**
 * Exercises chained calls passed directly as function arguments.
 */
public final class RiskyNotNullAssertionInCallCase {

    /**
     * Normalizes two strings and passes the results directly into another call.
     *
     * @param left left input
     * @param right right input
     * @return normalized pair
     */
    public String normalizePair(String left, String right) {
        return join(left.trim().toUpperCase(Locale.ROOT), right.trim().toLowerCase(Locale.ROOT));
    }

    /**
     * Reads nested object state and passes it directly as an argument.
     *
     * @param holder holder with nested state
     * @return decorated nested name
     */
    public String renderHolder(Holder holder) {
        return decorate(holder.normalizedPayloadName());
    }

    private String join(String left, String right) {
        return left + ":" + right;
    }

    private String decorate(String value) {
        return "[" + value + "]";
    }

    /**
     * Simple holder for nested payload access.
     *
     * @param payload nested payload
     */
    public record Holder(Payload payload) {
        /**
         * Returns the normalized nested payload name.
         *
         * @return normalized payload name
         */
        public String normalizedPayloadName() {
            return payload.normalizedName();
        }
    }

    /**
     * Simple payload for nested name access.
     *
     * @param name raw name
     */
    public record Payload(String name) {
        /**
         * Returns the trimmed payload name.
         *
         * @return trimmed payload name
         */
        public String normalizedName() {
            return name.trim();
        }
    }
}

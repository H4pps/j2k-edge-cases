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
        return decorate(holder.payload().name().trim());
    }

    private String join(String left, String right) {
        return left + ":" + right;
    }

    private String decorate(String value) {
        return "[" + value + "]";
    }

    /**
     * Simple holder for nested payload access.
     */
    public record Holder(Payload payload) {
    }

    /**
     * Simple payload for nested name access.
     */
    public record Payload(String name) {
    }
}

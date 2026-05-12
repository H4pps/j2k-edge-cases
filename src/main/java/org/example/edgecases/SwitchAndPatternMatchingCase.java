package org.example.edgecases;

/**
 * Exercises Java switch expressions together with pattern matching for instanceof.
 */
public final class SwitchAndPatternMatchingCase {

    /**
     * Classifies values using Java 17 expression syntax.
     *
     * @param value input value
     * @return compact classification
     */
    public String classify(Object value) {
        if (value instanceof String text && !text.isBlank()) {
            return switch (text.length()) {
                case 1 -> "one-char";
                case 2, 3 -> "short-text";
                default -> "long-text";
            };
        }
        if (value instanceof Number number) {
            return number.intValue() < 0 ? "negative-number" : "number";
        }
        return "other";
    }
}

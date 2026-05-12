package org.example.edgecases;

/**
 * Exercises checked exception declarations, wrapping, and rethrow behavior.
 */
public final class CheckedExceptionsCase {

    /**
     * Parses positive integers and throws checked exceptions for invalid data.
     *
     * @param text input text
     * @return parsed positive integer
     * @throws InvalidConfigurationException for null, non-numeric, or non-positive inputs
     */
    public int parsePositiveInt(String text) throws InvalidConfigurationException {
        if (text == null) {
            throw new InvalidConfigurationException("Input cannot be null");
        }
        try {
            int value = Integer.parseInt(text);
            if (value <= 0) {
                throw new InvalidConfigurationException("Value must be positive: " + text);
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new InvalidConfigurationException("Not an integer: " + text, exception);
        }
    }

    /**
     * Parses positive integers and returns default when parsing fails.
     *
     * @param text input text
     * @param defaultValue fallback
     * @return parsed value or default
     */
    public int parseOrDefault(String text, int defaultValue) {
        try {
            return parsePositiveInt(text);
        } catch (InvalidConfigurationException ignored) {
            return defaultValue;
        }
    }

    /**
     * Checked exception type for configuration parsing failures.
     */
    public static class InvalidConfigurationException extends Exception {
        public InvalidConfigurationException(String message) {
            super(message);
        }

        public InvalidConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

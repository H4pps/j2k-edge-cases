package org.example.edgecases;

import java.util.concurrent.Callable;

/**
 * Exercises nested anonymous classes with captured variables and overridden methods.
 */
public final class NestedAnonymousClassesCase {

    /**
     * Renders the input through three nested anonymous-class levels.
     *
     * @param input value to process
     * @return normalized string with nested wrappers
     */
    public String render(String input) {
        Formatter formatter = new Formatter() {
            @Override
            public String format(String value) {
                Callable<String> callable = new Callable<>() {
                    @Override
                    public String call() {
                        return new Object() {
                            @Override
                            public String toString() {
                                return "wrapped(" + value.trim() + ")";
                            }
                        }.toString();
                    }
                };
                try {
                    return callable.call();
                } catch (Exception exception) {
                    throw new IllegalStateException("Unexpected callable failure", exception);
                }
            }
        };
        return formatter.format(input);
    }

    private interface Formatter {
        String format(String value);
    }
}

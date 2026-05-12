package org.example.edgecases;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Exercises overloaded SAM targets where explicit lambda casts are required.
 */
public final class SamLambdaOverloadsCase {

    /**
     * Invokes both overloads using explicit casts to avoid lambda ambiguity.
     *
     * @param input input value
     * @return joined overload results
     */
    public String evaluate(String input) {
        String supplierValue = run((Supplier<String>) () -> "S:" + input.strip());
        String callableValue = run((Callable<String>) () -> "C:" + input.strip().toUpperCase());
        return supplierValue + ";" + callableValue;
    }

    public String run(Supplier<String> supplier) {
        return supplier.get();
    }

    public String run(Callable<String> callable) {
        try {
            return callable.call();
        } catch (Exception exception) {
            throw new IllegalStateException("Callable execution failed", exception);
        }
    }
}

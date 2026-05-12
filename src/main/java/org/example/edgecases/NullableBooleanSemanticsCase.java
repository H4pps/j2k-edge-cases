package org.example.edgecases;

/**
 * Exercises boxed Boolean checks whose null semantics must survive conversion.
 */
public final class NullableBooleanSemanticsCase {

    /**
     * Classifies a nullable pair of feature flags.
     *
     * @param enabled nullable enabled flag
     * @param archived nullable archived flag
     * @return state label preserving Java's null handling
     */
    public String classify(Boolean enabled, Boolean archived) {
        if (enabled != Boolean.TRUE) {
            return "disabled";
        }
        if (archived == Boolean.TRUE) {
            return "archived";
        }
        return "active";
    }

    /**
     * Returns true only when the nullable flag is explicitly false.
     *
     * @param flag nullable flag value
     * @return whether the flag is explicitly disabled
     */
    public boolean isExplicitlyDisabled(Boolean flag) {
        return Boolean.FALSE.equals(flag);
    }
}

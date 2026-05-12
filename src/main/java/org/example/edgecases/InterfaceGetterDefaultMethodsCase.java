package org.example.edgecases;

/**
 * Exercises interface getter inheritance with default methods and explicit overrides.
 */
public final class InterfaceGetterDefaultMethodsCase {

    /**
     * Creates a sample implementation with custom label and code.
     *
     * @param label label value
     * @param code numeric code
     * @return sample instance
     */
    public Sample sample(String label, int code) {
        return new Sample(label, code);
    }

    /**
     * First interface declaring a getter and default rendering.
     */
    public interface HasLabel {
        String getLabel();

        default String summary() {
            return "label=" + getLabel();
        }
    }

    /**
     * Second interface with its own default summary.
     */
    public interface HasCode {
        int getCode();

        default String summary() {
            return "code=" + getCode();
        }
    }

    /**
     * Implementation overriding conflicting defaults and exposing getter-style methods.
     */
    public static final class Sample implements HasLabel, HasCode {
        private final String label;
        private final int code;

        public Sample(String label, int code) {
            this.label = label;
            this.code = code;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public int getCode() {
            return code;
        }

        @Override
        public String summary() {
            return HasLabel.super.summary() + "," + HasCode.super.summary();
        }
    }
}

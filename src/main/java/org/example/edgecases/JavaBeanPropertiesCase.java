package org.example.edgecases;

/**
 * Exercises JavaBean-style getters/setters including boolean and acronym naming.
 */
public final class JavaBeanPropertiesCase {
    private boolean enabled;
    private String URL;
    private int retryCount;

    /**
     * Bean boolean getter.
     *
     * @return whether feature is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Bean boolean setter.
     *
     * @param enabled new enabled value
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Bean getter with acronym to preserve Java naming semantics.
     *
     * @return URL value
     */
    public String getURL() {
        return URL;
    }

    /**
     * Bean setter with acronym field.
     *
     * @param URL url value
     */
    public void setURL(String URL) {
        this.URL = URL;
    }

    /**
     * Standard integer property getter.
     *
     * @return retry count
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Standard integer property setter.
     *
     * @param retryCount retry count
     */
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}

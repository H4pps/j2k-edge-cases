package org.example.edgecases;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Locale;

/**
 * Exercises annotation-heavy service/controller style code resembling framework wiring.
 */
@FrameworkComponent("order-controller")
@FrameworkRoute(path = "/orders", method = "POST")
@FrameworkValidated(groups = {DefaultChecks.class, StrictChecks.class})
public final class AnnotationHeavyFrameworkStyleCase {

    @FrameworkQualifier("primary")
    private final AuditSink auditSink;

    /**
     * Creates a new controller-like class.
     *
     * @param auditSink audit dependency
     */
    public AnnotationHeavyFrameworkStyleCase(@FrameworkQualifier("primary") AuditSink auditSink) {
        this.auditSink = auditSink;
    }

    /**
     * Simulates a framework endpoint method with parameter-level annotations.
     *
     * @param requestBody incoming data
     * @return result status
     */
    @FrameworkTransactional(readOnly = false)
    public String createOrder(@FrameworkRequestBody @FrameworkNotBlank String requestBody) {
        String normalizedBody = normalize(requestBody);
        if (normalizedBody.isBlank()) {
            auditSink.record(List.of("rejectOrder", "blank-request"));
            return "rejected:blank-request";
        }

        String normalizedSku = normalizedBody.toUpperCase(Locale.ROOT);
        auditSink.record(List.of("createOrder", normalizedSku));
        return "created:" + normalizedSku;
    }

    /**
     * Simulates a read endpoint that combines path and header annotations.
     *
     * @param orderId order id from the request path
     * @param traceId correlation header value
     * @return rendered order status
     */
    @FrameworkRoute(path = "/orders/{orderId}", method = "GET")
    @FrameworkTransactional(readOnly = true)
    public String findOrder(
            @FrameworkPathVariable("orderId") String orderId,
            @FrameworkHeader("X-Trace-Id") String traceId) {
        String normalizedOrderId = normalize(orderId);
        String normalizedTraceId = normalize(traceId);
        auditSink.record(List.of("findOrder", normalizedOrderId, normalizedTraceId));
        return "found:" + normalizedOrderId + ":" + normalizedTraceId;
    }

    /**
     * Normalizes nullable framework input into stable trimmed text.
     *
     * @param text nullable framework input
     * @return trimmed text or an empty string
     */
    private String normalize(String text) {
        return text == null ? "" : text.trim();
    }

    /**
     * Abstraction representing injected infrastructure.
     */
    public interface AuditSink {
        void record(List<String> event);

        default String name() {
            return "audit";
        }
    }
}

interface DefaultChecks {
    default String groupName() {
        return "default";
    }
}

interface StrictChecks {
    default String groupName() {
        return "strict";
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface FrameworkComponent {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@interface FrameworkRoute {
    String path();

    String method();
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@interface FrameworkQualifier {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface FrameworkTransactional {
    boolean readOnly();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface FrameworkValidated {
    Class<?>[] groups();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@interface FrameworkRequestBody {
    String mediaType() default "text/plain";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@interface FrameworkNotBlank {
    int min() default 1;
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@interface FrameworkPathVariable {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@interface FrameworkHeader {
    String value();
}

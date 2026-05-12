package org.example.edgecases;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Exercises annotation-heavy service/controller style code resembling framework wiring.
 */
@FrameworkComponent("order-controller")
@FrameworkRoute(path = "/orders", method = "POST")
@FrameworkValidated(groups = {DefaultChecks.class, StrictChecks.class})
public final class AnnotationHeavyFrameworkStyleCase {

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
        auditSink.record(List.of("createOrder", requestBody));
        return "created:" + requestBody.trim();
    }

    /**
     * Abstraction representing injected infrastructure.
     */
    public interface AuditSink {
        void record(List<String> event);
    }
}

interface DefaultChecks {
}

interface StrictChecks {
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
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@interface FrameworkNotBlank {
}

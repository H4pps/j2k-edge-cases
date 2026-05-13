package org.example.edgecases;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Exercises annotation-heavy service/controller style code resembling framework wiring.
 */
@FrameworkComponent("order-controller")
@FrameworkRoute(path = "/orders", method = "POST")
@FrameworkValidated(groups = {DefaultChecks.class, StrictChecks.class})
public final class AnnotationHeavyFrameworkStyleCase {

    @FrameworkQualifier("primary")
    private final AuditSink auditSink;
    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;

    /**
     * Creates a new controller-like class.
     *
     * @param auditSink audit dependency
     */
    public AnnotationHeavyFrameworkStyleCase(@FrameworkQualifier("primary") AuditSink auditSink) {
        this(auditSink, sku -> !"SKU-MISSING".equals(sku), new InMemoryOrderRepository());
    }

    /**
     * Creates a controller-like class with testable infrastructure dependencies.
     *
     * @param auditSink audit dependency
     * @param inventoryClient inventory lookup dependency
     * @param orderRepository order persistence dependency
     */
    AnnotationHeavyFrameworkStyleCase(
            @FrameworkQualifier("primary") AuditSink auditSink,
            @FrameworkQualifier("inventory") InventoryClient inventoryClient,
            OrderRepository orderRepository) {
        this.auditSink = auditSink;
        this.inventoryClient = inventoryClient;
        this.orderRepository = orderRepository;
    }

    /**
     * Simulates a framework endpoint method with parameter-level annotations,
     * validation, dependency calls, and persistence.
     *
     * @param requestBody incoming data
     * @return result status
     */
    @FrameworkTransactional(readOnly = false)
    public String createOrder(@FrameworkRequestBody @FrameworkNotBlank String requestBody) {
        OrderRequest request = parseRequest(requestBody);
        ValidationResult validation = validate(request);
        if (!validation.valid()) {
            auditSink.record(List.of("rejectOrder", validation.reason()));
            return "rejected:validation:" + validation.reason();
        }

        OrderDraft draft = new OrderDraft(
                normalizeSku(request.sku()),
                request.quantity(),
                request.customer().trim());
        if (!inventoryClient.isAvailable(draft.sku())) {
            auditSink.record(List.of("rejectOrder", draft.sku(), "out-of-stock"));
            return "rejected:out-of-stock:" + draft.sku();
        }

        StoredOrder storedOrder = orderRepository.save(draft);
        auditSink.record(List.of(
                "createOrder",
                storedOrder.normalizedSku(),
                String.valueOf(storedOrder.quantity()),
                storedOrder.customer()));
        return "created:" + storedOrder.id() + ":" + storedOrder.normalizedSku() + ":" + storedOrder.quantity();
    }

    /**
     * Simulates a read endpoint that combines path/header annotations with repository lookup.
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
        auditSink.record(List.of("findOrder", orderId, traceId));
        Optional<StoredOrder> storedOrder = orderRepository.findById(orderId);
        return storedOrder
                .map(order -> "order:" + order.id()
                        + ":" + order.normalizedSku()
                        + ":" + order.quantity()
                        + ":" + order.customer()
                        + ":" + traceId)
                .orElse("missing:" + orderId + ":" + traceId);
    }

    /**
     * Parses a colon-separated request body into an order request.
     *
     * @param requestBody raw body in `sku:quantity:customer` form
     * @return parsed order request
     */
    private OrderRequest parseRequest(String requestBody) {
        String[] parts = requestBody.split(":", -1);
        String sku = parts.length > 0 ? parts[0].trim() : "";
        int quantity = parts.length > 1 ? parseQuantity(parts[1]) : 1;
        String customer = parts.length > 2 ? parts[2].trim() : "anonymous";
        return new OrderRequest(sku, quantity, customer);
    }

    /**
     * Parses positive quantity values and maps invalid text to zero for validation.
     *
     * @param rawQuantity raw quantity text
     * @return parsed quantity or zero when invalid
     */
    private int parseQuantity(String rawQuantity) {
        try {
            return Integer.parseInt(rawQuantity.trim());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    /**
     * Validates parsed order input.
     *
     * @param request parsed request
     * @return validation result
     */
    private ValidationResult validate(OrderRequest request) {
        if (request.sku().isBlank()) {
            return ValidationResult.invalid("blank-sku");
        }
        if (request.quantity() <= 0) {
            return ValidationResult.invalid("invalid-quantity");
        }
        if (request.customer().isBlank()) {
            return ValidationResult.invalid("blank-customer");
        }
        return ValidationResult.passed();
    }

    /**
     * Normalizes SKU input the way framework service layers often normalize request data.
     *
     * @param sku raw SKU
     * @return normalized SKU
     */
    private String normalizeSku(String sku) {
        return sku.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Abstraction representing injected infrastructure.
     */
    public interface AuditSink {
        void record(List<String> event);
    }

    /**
     * Abstraction representing inventory infrastructure.
     */
    public interface InventoryClient {
        boolean isAvailable(String sku);
    }

    /**
     * Abstraction representing order persistence infrastructure.
     */
    public interface OrderRepository {
        StoredOrder save(OrderDraft draft);

        Optional<StoredOrder> findById(String id);
    }

    /**
     * In-memory repository used by the standalone Java baseline.
     */
    private static final class InMemoryOrderRepository implements OrderRepository {
        private final Map<String, StoredOrder> orders = new LinkedHashMap<>();

        /**
         * Stores an order draft under a deterministic id.
         *
         * @param draft order draft
         * @return stored order
         */
        @Override
        public StoredOrder save(OrderDraft draft) {
            String id = "order-" + (orders.size() + 1);
            StoredOrder storedOrder = new StoredOrder(id, draft.sku(), draft.quantity(), draft.customer());
            orders.put(id, storedOrder);
            return storedOrder;
        }

        /**
         * Finds a previously stored order.
         *
         * @param id order id
         * @return optional stored order
         */
        @Override
        public Optional<StoredOrder> findById(String id) {
            return Optional.ofNullable(orders.get(id));
        }
    }

    /**
     * Parsed request body.
     */
    public static final class OrderRequest {
        private final String sku;
        private final int quantity;
        private final String customer;

        /**
         * Creates a parsed request body.
         *
         * @param sku raw SKU
         * @param quantity requested quantity
         * @param customer customer name
         */
        public OrderRequest(
                @FrameworkJsonProperty("sku") String sku,
                @FrameworkJsonProperty("quantity") int quantity,
                @FrameworkJsonProperty("customer") String customer) {
            this.sku = sku;
            this.quantity = quantity;
            this.customer = customer;
        }

        /**
         * Returns the raw SKU.
         *
         * @return raw SKU
         */
        public String sku() {
            return sku;
        }

        /**
         * Returns the requested quantity.
         *
         * @return requested quantity
         */
        public int quantity() {
            return quantity;
        }

        /**
         * Returns the customer name.
         *
         * @return customer name
         */
        public String customer() {
            return customer;
        }
    }

    /**
     * Validated order draft.
     */
    public static final class OrderDraft {
        private final String sku;
        private final int quantity;
        private final String customer;

        /**
         * Creates a validated order draft.
         *
         * @param sku normalized SKU
         * @param quantity requested quantity
         * @param customer customer name
         */
        public OrderDraft(String sku, int quantity, String customer) {
            this.sku = sku;
            this.quantity = quantity;
            this.customer = customer;
        }

        /**
         * Returns the normalized SKU.
         *
         * @return normalized SKU
         */
        public String sku() {
            return sku;
        }

        /**
         * Returns the requested quantity.
         *
         * @return requested quantity
         */
        public int quantity() {
            return quantity;
        }

        /**
         * Returns the customer name.
         *
         * @return customer name
         */
        public String customer() {
            return customer;
        }
    }

    /**
     * Stored order view returned by the repository.
     */
    public static final class StoredOrder {
        private final String id;
        private final String normalizedSku;
        private final int quantity;
        private final String customer;

        /**
         * Creates a stored order view.
         *
         * @param id generated order id
         * @param normalizedSku normalized SKU
         * @param quantity requested quantity
         * @param customer customer name
         */
        public StoredOrder(String id, String normalizedSku, int quantity, String customer) {
            this.id = id;
            this.normalizedSku = normalizedSku;
            this.quantity = quantity;
            this.customer = customer;
        }

        /**
         * Returns the generated order id.
         *
         * @return generated order id
         */
        public String id() {
            return id;
        }

        /**
         * Returns the normalized SKU.
         *
         * @return normalized SKU
         */
        public String normalizedSku() {
            return normalizedSku;
        }

        /**
         * Returns the requested quantity.
         *
         * @return requested quantity
         */
        public int quantity() {
            return quantity;
        }

        /**
         * Returns the customer name.
         *
         * @return customer name
         */
        public String customer() {
            return customer;
        }
    }

    /**
     * Validation result with a stable failure reason.
     */
    private static final class ValidationResult {
        private final boolean valid;
        private final String reason;

        /**
         * Creates a validation result.
         *
         * @param valid whether validation passed
         * @param reason stable failure reason
         */
        private ValidationResult(boolean valid, String reason) {
            this.valid = valid;
            this.reason = reason;
        }

        /**
         * Creates a successful validation result.
         *
         * @return valid result
         */
        private static ValidationResult passed() {
            return new ValidationResult(true, "");
        }

        /**
         * Creates an invalid validation result.
         *
         * @param reason stable failure reason
         * @return invalid result
         */
        private static ValidationResult invalid(String reason) {
            return new ValidationResult(false, reason);
        }

        /**
         * Returns whether validation passed.
         *
         * @return whether validation passed
         */
        private boolean valid() {
            return valid;
        }

        /**
         * Returns the stable failure reason.
         *
         * @return stable failure reason
         */
        private String reason() {
            return reason;
        }
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

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@interface FrameworkJsonProperty {
    String value();
}

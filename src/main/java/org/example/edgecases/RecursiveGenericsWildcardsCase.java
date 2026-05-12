package org.example.edgecases;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercises recursive generic bounds together with wildcard producer/consumer methods.
 */
public final class RecursiveGenericsWildcardsCase {

    /**
     * Base type using a recursive type bound.
     *
     * @param <T> concrete subtype that can copy itself
     */
    public abstract static class Self<T extends Self<T>> {
        private final String id;

        protected Self(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public abstract T withId(String id);

        public T copyIdFrom(T other) {
            return withId(other.id());
        }
    }

    /**
     * Concrete recursive generic implementation.
     */
    public static final class NamedNode extends Self<NamedNode> {
        public NamedNode(String id) {
            super(id);
        }

        @Override
        public NamedNode withId(String id) {
            return new NamedNode(id);
        }
    }

    /**
     * Collects values from wildcard producer into wildcard consumer.
     *
     * @param sink target sink list
     * @param source source list
     * @param <T> recursive generic type
     * @return copied elements count
     */
    public <T extends Self<T>> int copyAll(List<? super T> sink, List<? extends T> source) {
        sink.addAll(source);
        return source.size();
    }

    /**
     * Filters recursive generic values by id length.
     *
     * @param values recursive generic values
     * @param minLength minimum id length
     * @return filtered ids
     */
    public List<String> filterIds(List<? extends Self<?>> values, int minLength) {
        List<String> result = new ArrayList<>();
        for (Self<?> value : values) {
            if (value.id().length() >= minLength) {
                result.add(value.id());
            }
        }
        return result;
    }
}

package org.example.edgecases;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Exercises try-with-resources nesting and close-order semantics.
 */
public final class TryWithResourcesCase {

    /**
     * Reads the first line and uppercases it using nested resources.
     *
     * @param input text input
     * @return uppercased first line
     * @throws IOException when stream processing fails
     */
    public String upperFirstLine(String input) throws IOException {
        try (ByteArrayInputStream bytes =
                     new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
             InputStreamReader reader = new InputStreamReader(bytes, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line = bufferedReader.readLine();
            return line == null ? "" : line.toUpperCase(Locale.ROOT);
        }
    }

    /**
     * Demonstrates deterministic reverse close order for AutoCloseable resources.
     *
     * @return close-order events
     */
    public List<String> closeOrderEvents() {
        List<String> events = new ArrayList<>();
        try (TrackingResource first = new TrackingResource("first", events);
             TrackingResource second = new TrackingResource("second", events)) {
            events.add("work");
        } catch (Exception exception) {
            throw new IllegalStateException("Unexpected close failure", exception);
        }
        return events;
    }

    private static final class TrackingResource implements AutoCloseable {
        private final String name;
        private final List<String> events;

        private TrackingResource(String name, List<String> events) {
            this.name = name;
            this.events = events;
        }

        @Override
        public void close() {
            events.add("close:" + name);
        }
    }
}

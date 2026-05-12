package org.example.edgecases;

/**
 * Exercises Java 17 records and sealed interfaces.
 */
public final class RecordsAndSealedTypesCase {

    /**
     * Calculates a compact description for a sealed shape hierarchy.
     *
     * @param shape sealed shape value
     * @return shape description and area
     */
    public String describe(Shape shape) {
        if (shape instanceof Circle circle) {
            return "circle:" + circle.radius() + ":" + circle.area();
        }
        if (shape instanceof Rectangle rectangle) {
            return "rectangle:" + rectangle.width() + "x" + rectangle.height() + ":" + rectangle.area();
        }
        throw new IllegalArgumentException("Unsupported shape: " + shape);
    }

    /**
     * Sealed shape abstraction.
     */
    public sealed interface Shape permits Circle, Rectangle {
        int area();
    }

    /**
     * Record implementation for circular shapes.
     *
     * @param radius circle radius
     */
    public record Circle(int radius) implements Shape {
        @Override
        public int area() {
            return radius * radius * 3;
        }
    }

    /**
     * Record implementation for rectangular shapes.
     *
     * @param width rectangle width
     * @param height rectangle height
     */
    public record Rectangle(int width, int height) implements Shape {
        @Override
        public int area() {
            return width * height;
        }
    }
}

# Converter Failure Proposal: Java 17 Pattern And Record Conversion

## Failure

The strongest failure in the edge-case dataset is Java 17 syntax conversion.
Static J2K generates invalid Kotlin for pattern matching and records:

- `SwitchAndPatternMatchingCase.kt` contains `value is ???` and leaves a Java
  `switch` expression body in the generated Kotlin.
- `RecordsAndSealedTypesCase.kt` contains `shape is ???` and drops record
  constructor state from generated classes.
- `RiskyNotNullAssertionInCallCase.kt` converts nested records into empty
  classes while converted call sites still call record accessor methods.

## Hypothesis

The converter handles older Java class/method syntax more reliably than Java 17
records, sealed declarations, switch expressions, and `instanceof` pattern
variables. A postprocessor or converter fix should lower these constructs into
plain Kotlin constructs before or during conversion.

## Proposed Kotlin Shape

For `SwitchAndPatternMatchingCase`, a safe target shape is:

```kotlin
class SwitchAndPatternMatchingCase {
    fun classify(value: Any): String {
        if (value is String && value.isNotBlank()) {
            return when (value.length) {
                1 -> "one-char"
                2, 3 -> "short-text"
                else -> "long-text"
            }
        }

        if (value is Number) {
            return if (value.toInt() < 0) "negative-number" else "number"
        }

        return "other"
    }
}
```

For `RecordsAndSealedTypesCase`, a safe target shape is:

```kotlin
class RecordsAndSealedTypesCase {
    fun describe(shape: Shape): String =
        when (shape) {
            is Circle -> "circle:${shape.radius}:${shape.area()}"
            is Rectangle -> "rectangle:${shape.width}x${shape.height}:${shape.area()}"
        }

    sealed interface Shape {
        fun area(): Int
    }

    data class Circle(val radius: Int) : Shape {
        override fun area(): Int = radius * radius * 3
    }

    data class Rectangle(val width: Int, val height: Int) : Shape {
        override fun area(): Int = width * height
    }
}
```

## Implementation Direction

A practical fix does not need to solve all Java 17 syntax at once. The first
target should be:

- map Java records to Kotlin `data class` declarations with constructor
  properties;
- map `instanceof Type name` to Kotlin smart-cast checks;
- map Java switch expressions over primitive/string values to Kotlin `when`;
- avoid emitting placeholder `???` in generated Kotlin.

This is a converter or postprocessor concern, not a dataset workaround. The Java
dataset should remain as-is so it continues to expose the failure.

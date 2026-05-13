# Phase 6 Java Edge Cases for J2K

This repository is a standalone Java/JVM dataset for exercising difficult Java-to-Kotlin (J2K) conversion paths. It is intentionally Java-only and keeps examples focused on source-level semantics that commonly regress under automated conversion.

## Project Profile

- Build tool: Gradle
- Language: Java
- Group: `org.example`
- Toolchain target: JDK 17
- Package root: `org.example.edgecases`

## Edge-Case Catalog

### 1) Nested anonymous classes (`NestedAnonymousClassesCase`)
- Hypothesis: deeply nested anonymous objects can trigger incorrect capture scoping and unexpected synthetic class conversion patterns.
- Expected J2K risk: lambda/anonymous replacement may alter `this` binding or override behavior (`toString` and enclosing references).

### 2) Recursive generics + wildcards (`RecursiveGenericsWildcardsCase`)
- Hypothesis: recursive bounds (`T extends Self<T>`) combined with producer/consumer wildcards stress Kotlin type projections.
- Expected J2K risk: projected type substitutions can become too broad (`*`) or too narrow, producing casts or compile failures.

### 3) SAM/lambda overloads (`SamLambdaOverloadsCase`)
- Hypothesis: overloaded SAM signatures need explicit disambiguation at call sites.
- Expected J2K risk: conversion can lose explicit cast intent and introduce ambiguous Kotlin overload resolution.

### 4) Annotation-heavy framework-style class (`AnnotationHeavyFrameworkStyleCase`)
- Hypothesis: stacked annotations plus controller/service/repository-style logic mimic framework wiring complexity.
- Expected J2K risk: annotation targets/use-site mapping in Kotlin can shift (`@param`, `@field`, etc.), and real request parsing/validation code can expose conversion gaps beyond declaration preservation.

### 5) Try-with-resources (`TryWithResourcesCase`)
- Hypothesis: resource declarations with multiple closables depend on deterministic close order.
- Expected J2K risk: rewritten `use {}` blocks may change close order or exception propagation shape.

### 6) Checked exceptions (`CheckedExceptionsCase`)
- Hypothesis: checked exception contracts and fallback logic are semantically important to callers.
- Expected J2K risk: Kotlin’s unchecked model may hide throws contracts unless `@Throws` metadata is preserved.

### 7) Static nested companion-like pattern (`StaticNestedCompanionLikeCase`)
- Hypothesis: Java static factory holders map naturally to Kotlin companion-like structures.
- Expected J2K risk: static access pattern changes may alter call sites, visibility, or binary compatibility expectations.

### 8) Varargs and arrays (`VarargsArraysCase`)
- Hypothesis: Java array + vararg interactions frequently require spread operators after conversion.
- Expected J2K risk: incorrect spread insertion/removal or array variance mismatches break invocation semantics.

### 9) JavaBean properties (`JavaBeanPropertiesCase`)
- Hypothesis: getter/setter naming (including acronym forms like `getURL`) drives property inference.
- Expected J2K risk: property naming normalization may diverge from expected JavaBean semantics.

### 10) Nullability annotations (`NullabilityAnnotationsCase`)
- Hypothesis: explicit `@NotNull` / `@Nullable` intent should become Kotlin nullability accurately.
- Expected J2K risk: unknown annotation handling or platform-type fallback can erase null-safety intent.

### 11) Interface getter overrides + default methods (`InterfaceGetterDefaultMethodsCase`)
- Hypothesis: conflicts across multiple default methods require explicit override resolution.
- Expected J2K risk: default-method conflict handling can generate incorrect delegation or override bodies.

### 12) Raw types + unchecked casts (`RawTypesUncheckedCastsCase`)
- Hypothesis: legacy raw collections and unchecked casts are common in pre-generics code.
- Expected J2K risk: converter may over-aggressively infer types or emit unsafe casts that alter runtime behavior.

### 13) Nullable boolean semantics (`NullableBooleanSemanticsCase`)
- Hypothesis: boxed `Boolean` comparisons with `Boolean.TRUE` / `Boolean.FALSE` encode null-sensitive behavior.
- Expected J2K risk: nullable boolean rewrites such as `flag != true` can be correct but deserve semantic review.

### 14) Missing nullability annotations (`MissingNullabilityAnnotationsCase`)
- Hypothesis: unannotated APIs force J2K to infer nullability from local usage.
- Expected J2K risk: platform-like types, risky `!!`, or overconfident non-null types can appear.

### 15) Risky not-null assertions inside call arguments (`RiskyNotNullAssertionInCallCase`)
- Hypothesis: chained Java dereferences passed directly as call arguments encourage J2K to emit `!!` in high-risk locations.
- Expected J2K risk: generated Kotlin may contain assertions inside function-call arguments that should be reviewed.

### 16) Records and sealed types (`RecordsAndSealedTypesCase`)
- Hypothesis: Java 17 records and sealed interfaces stress modern syntax and declaration mapping.
- Expected J2K risk: converter may fail, drop sealed semantics, or generate awkward class structures.

### 17) Switch expressions and pattern matching (`SwitchAndPatternMatchingCase`)
- Hypothesis: switch expressions and `instanceof` pattern variables require careful expression lowering.
- Expected J2K risk: branch structure or pattern variable handling may be incorrect or hard to read.

## Documentation

- `docs/EDGE_CASE_TESTS.md` explains the Java baseline tests and dataset audit.
- `docs/EDGE_CASE_REPORT.md` records J2K pass/fail observations after conversion.

## Local Verification

Run from repository root:

```bash
./gradlew test
./gradlew build
```

The test suite (`EdgeCasesSanityTest`) is intentionally lightweight: it validates that each edge-case source compiles and basic runtime behavior remains deterministic.

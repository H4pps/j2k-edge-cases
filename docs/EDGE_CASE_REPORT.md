# Edge-Case J2K Report

This report records the current static J2K behavior for the custom edge-case
dataset. The Java baseline is documented in `docs/EDGE_CASE_TESTS.md`; generated
Kotlin artifacts are produced by the main evaluation pipeline and are not
committed to this repository.

## Run Summary

- Dataset source commit tested: `3783b43e61d6db57093e2b1a6e174893d8c10314`
- Java baseline: `./gradlew test` passed.
- J2K conversion status: completed.
- Java files discovered: 17.
- Generated Kotlin files: 17.
- File coverage: 100%.
- Package preservation: 100%.
- Evaluator status: completed with warnings.
- Evaluator quality warnings: 8.

The converter generated one Kotlin file for every Java input, but this is not a
compile-success guarantee. Manual inspection found invalid generated Kotlin in
the Java 17 syntax cases and several review-worthy quality issues.

## Case Results

| Case | Status | Observation |
| --- | --- | --- |
| Nested anonymous classes | Needs manual review | Generated Kotlin exists with no evaluator warning. Review capture scoping and anonymous-object semantics manually. |
| Recursive generics and wildcards | Needs manual review | Generated Kotlin exists with no evaluator warning. Generic projection shape still deserves manual review. |
| SAM/lambda overloads | Needs manual review | Generated Kotlin exists with no evaluator warning. Check that overload disambiguation remains explicit enough for Kotlin. |
| Annotation-heavy framework style | Needs manual review | Generated Kotlin now contains simple controller-style method bodies, audit calls, default interface methods, and annotation members. The generated annotation declaration still uses `KClass` without an import and should be compile-checked later. |
| Try-with-resources | Needs manual review | Generated Kotlin contains `!!` inside nested `use` conversion. Close order should be reviewed if generated code is compiled later. |
| Checked exceptions | Needs manual review | `@Throws` is preserved, but generated exception messages contain `text!!` inside constructor-call arguments. |
| Static nested companion-like pattern | Needs manual review | Generated Kotlin now contains the private-constructor validation and companion initialization body. Static holder shape should still be reviewed for Kotlin API ergonomics. |
| Varargs and arrays | Failed compile review | Generated `flatten` builds `arrayOfNulls<String>(size)` and returns it as `Array<String>`, which is nullable-array shaped and likely does not compile as written. |
| JavaBean properties | Needs manual review | Most getters/setters map to Kotlin properties, but acronym methods `getURL`/`setURL` become `url`, and `setEnabled` is not reported as a property-backed accessor. |
| Nullability annotations | Needs manual review | Generated Kotlin exists with no evaluator warning. Verify that `@NotNull`/`@Nullable` intent is represented as expected. |
| Interface getter defaults | Needs manual review | Getter accessors are mapped to Kotlin properties, but default-method override semantics should still be reviewed. |
| Raw types and unchecked casts | Needs manual review | Generated Kotlin contains `unchecked!!` while iterating over a cast raw list. |
| Nullable boolean semantics | Needs manual review | Generated Kotlin uses identity checks against `java.lang.Boolean.TRUE`/`FALSE`; this preserves a Java-looking form but is not idiomatic and may be semantically fragile. |
| Missing nullability annotations | Needs manual review | Generated Kotlin contains `value!!` after an explicit null check. This is redundant and a useful nullability postprocessing signal. |
| Risky not-null assertions in call arguments | Failed compile review | Nested Java records now produce method bodies, but generated Kotlin omits the record constructor properties while those methods reference `payload` and `name`, so the generated code appears incomplete. |
| Records and sealed types | Failed compile review | Pattern matching over records produces `shape is ???`, and record constructor properties are missing from generated classes. |
| Switch expressions and pattern matching | Failed compile review | Pattern variables produce `value is ???`, and the Java switch expression body is left in Java syntax. |

## Concrete Failures

- Java 17 record and pattern syntax is the clearest converter weakness in this
  dataset. `RecordsAndSealedTypesCase.kt` and
  `SwitchAndPatternMatchingCase.kt` contain `???` placeholders.
- Record conversion is also weak in `RiskyNotNullAssertionInCallCase.kt`, where
  nested record methods are generated but their constructor-backed state is
  missing, so method bodies reference unavailable `payload` and `name` values.
- Vararg array conversion in `VarargsArraysCase.kt` creates a nullable array but
  returns it as a non-null `Array<String>`.
- Annotation conversion in `AnnotationHeavyFrameworkStyleCase.kt` preserves the
  controller-style method bodies and annotation metadata, but exposes an
  unresolved-looking `KClass` type in generated annotation declarations and
  should be checked by a future generated Kotlin compile step.

## Quality Warnings

- `AnnotationHeavyFrameworkStyleCase.kt`: one `!!` assertion in a null-checked
  normalization helper.
- `CheckedExceptionsCase.kt`: two `!!` assertions, both inside call arguments.
- `MissingNullabilityAnnotationsCase.kt`: one `!!` assertion.
- `RawTypesUncheckedCastsCase.kt`: one `!!` assertion.
- `TryWithResourcesCase.kt`: one `!!` assertion and three `!!` occurrences
  inside call arguments.
- `VarargsArraysCase.kt`: one Java interop leftover warning.

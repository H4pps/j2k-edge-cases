# Edge-Case Test Documentation

This dataset is a standalone Java baseline for stressing static Java-to-Kotlin
conversion. The sources intentionally focus on small, inspectable examples where
raw J2K output is likely to need review or postprocessing.

`EdgeCasesSanityTest` is not a Kotlin correctness test. It verifies that the
Java inputs compile and that each source case has deterministic runtime behavior
before conversion. The J2K evaluator then compares generated Kotlin against this
known Java baseline.

## Test Catalog

| Case | Java source | Hypothesis | Java baseline assertion | J2K risk | Review signal |
| --- | --- | --- | --- | --- | --- |
| Nested anonymous classes | `NestedAnonymousClassesCase` | Nested anonymous objects stress capture scoping and `this` binding. | Rendering trims and wraps a value through nested overrides. | Anonymous classes may be replaced with lambdas incorrectly. | Changed output or suspicious nested object conversion. |
| Recursive generics and wildcards | `RecursiveGenericsWildcardsCase` | Recursive bounds and wildcard producer/consumer APIs stress Kotlin projections. | Copy/filter behavior preserves element count and filtered IDs. | Type projections can become too broad, too narrow, or cast-heavy. | Compile issues, `Any?`, or unsafe casts. |
| SAM/lambda overloads | `SamLambdaOverloadsCase` | Overloaded SAM targets need explicit disambiguation. | Supplier and callable overloads both run with expected labels. | Lambda conversion may create ambiguous overload resolution. | Missing explicit target type or changed overload choice. |
| Annotation-heavy framework style | `AnnotationHeavyFrameworkStyleCase` | Framework-like annotations plus small controller-style methods stress annotation targets and executable body preservation. | Create/read/reject paths return stable values and emit ordered audit events. | Annotation targets may shift and parameter annotations may need Kotlin use-site targets. | Missing annotations, incorrect use-site targets, missing bodies, or changed audit behavior. |
| Try-with-resources | `TryWithResourcesCase` | Multiple resources depend on reverse close order. | First line is uppercased and close events are reverse ordered. | `use {}` nesting may change close order or exception shape. | Different close-event order or nested `use` complexity. |
| Checked exceptions | `CheckedExceptionsCase` | Checked exception contracts are source-significant for callers. | Positive parse succeeds, fallback works, invalid input throws checked type. | Kotlin may drop `@Throws`-relevant information. | Missing throws metadata or changed catch behavior. |
| Static nested companion-like pattern | `StaticNestedCompanionLikeCase` | Static factory holders resemble Kotlin companion objects. | Static holder creates standard and custom instances. | Static access may become awkward or binary-incompatible. | Unexpected object/companion conversion or renamed access. |
| Varargs and arrays | `VarargsArraysCase` | Java varargs and array interactions need Kotlin spread handling. | Sum, join, and flatten methods preserve array behavior. | Spread operators or array variance may be wrong. | Compile issues or suspicious array casts. |
| JavaBean properties | `JavaBeanPropertiesCase` | Getter/setter names drive Kotlin property inference. | Boolean, acronym, and integer properties round-trip. | Acronym names or boolean properties may map incorrectly. | Accessors not mapped to Kotlin properties or wrong names. |
| Nullability annotations | `NullabilityAnnotationsCase` | Explicit annotations should drive Kotlin nullability. | Nullable lookup and non-null fallback behavior are stable. | Unknown annotation handling can erase null-safety intent. | Platform types, `!!`, or nullable mismatch. |
| Interface getter defaults | `InterfaceGetterDefaultMethodsCase` | Getter interfaces with default methods stress override conversion. | Conflicting defaults are combined through explicit override. | Interface getters may become properties while overrides remain methods. | Missing property-backed accessor mapping or invalid override. |
| Raw types and unchecked casts | `RawTypesUncheckedCastsCase` | Legacy raw APIs stress Kotlin casts and type inference. | Raw list values are copied as strings. | J2K may infer unsafe or overly broad types. | `Any?`, unchecked casts, or changed iteration behavior. |
| Nullable boolean semantics | `NullableBooleanSemanticsCase` | Boxed Boolean comparisons must preserve null semantics. | Null and false are disabled, true/null is active, true/true is archived. | `!= true` and `== true` rewrites can be semantically delicate. | Nullable boolean comparison warnings. |
| Missing nullability annotations | `MissingNullabilityAnnotationsCase` | Unannotated APIs should not become overconfident Kotlin types. | Java null checks protect missing lookup values. | J2K may emit platform types or risky assertions. | Unexpected `!!` or nullable type erasure. |
| Risky not-null assertions in call arguments | `RiskyNotNullAssertionInCallCase` | Chained expressions passed into calls can attract risky `!!`. | Normalized strings and nested holder rendering are deterministic. | `!!` can appear inside function-call arguments. | `!!` inside call warning. |
| Records and sealed types | `RecordsAndSealedTypesCase` | Java 17 records and sealed interfaces stress modern syntax support. | Known shape records produce expected descriptions and areas. | Converter may not preserve record/sealed semantics cleanly. | Converter failure, missing declarations, or awkward class output. |
| Switch expressions and pattern matching | `SwitchAndPatternMatchingCase` | Java 17 switch expressions and `instanceof` patterns stress syntax lowering. | Strings, numbers, and fallback values classify deterministically. | Pattern variables or switch expressions may lower poorly. | Converter failure or changed branch structure. |

## Audit

### Keep

- Keep all existing 12 cases. They cover distinct J2K risk areas and compile as
  a compact Java baseline.
- Keep the new five cases because they cover gaps called out by the evaluation
  strategy: nullable booleans, missing nullability annotations, risky `!!`, Java
  17 records/sealed types, and switch/pattern syntax.
- Keep `EdgeCasesSanityTest` as one lightweight Java-baseline test suite instead
  of splitting tests prematurely.
- Keep Gradle wrapper files so CI and reviewers can reproduce the dataset build.

### Remove

- Remove tracked `.idea/*` metadata from Git. IDE settings are local and do not
  define the dataset.
- Do not commit generated build outputs, Gradle caches, `bin/`, or J2K output.

### Add Later

- Add generated-Kotlin compile checks once the main pipeline can construct a
  reliable Kotlin compilation classpath for converted outputs.
- Add more framework-specific annotation cases only if the current annotation
  case does not produce useful converter findings.

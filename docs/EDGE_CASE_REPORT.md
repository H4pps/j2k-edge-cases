# Edge-Case J2K Report

This report records the current static J2K behavior for the custom edge-case
dataset. The Java baseline is documented in `docs/EDGE_CASE_TESTS.md`; generated
Kotlin artifacts are produced by the main evaluation pipeline and are not
committed to this repository.

## Current Status

Conversion observations are pending for the current dataset revision. Run the
main pipeline with `benchmarks/j2k-edge-cases.yml`, then update this table from
`build/j2k/j2k-edge-cases/conversion.json` and
`build/reports/j2k-eval/j2k-edge-cases/evaluation.json`.

| Case | Status | Observation |
| --- | --- | --- |
| Nested anonymous classes | Pending | Awaiting J2K run. |
| Recursive generics and wildcards | Pending | Awaiting J2K run. |
| SAM/lambda overloads | Pending | Awaiting J2K run. |
| Annotation-heavy framework style | Pending | Awaiting J2K run. |
| Try-with-resources | Pending | Awaiting J2K run. |
| Checked exceptions | Pending | Awaiting J2K run. |
| Static nested companion-like pattern | Pending | Awaiting J2K run. |
| Varargs and arrays | Pending | Awaiting J2K run. |
| JavaBean properties | Pending | Awaiting J2K run. |
| Nullability annotations | Pending | Awaiting J2K run. |
| Interface getter defaults | Pending | Awaiting J2K run. |
| Raw types and unchecked casts | Pending | Awaiting J2K run. |
| Nullable boolean semantics | Pending | Awaiting J2K run. |
| Missing nullability annotations | Pending | Awaiting J2K run. |
| Risky not-null assertions in call arguments | Pending | Awaiting J2K run. |
| Records and sealed types | Pending | Awaiting J2K run. |
| Switch expressions and pattern matching | Pending | Awaiting J2K run. |

## Failure Categories To Record

- Missing generated Kotlin file.
- Converter source-level exception.
- Generated Kotlin syntax or compile failure.
- Suspicious `!!`, especially inside function-call arguments.
- Getter/setter conversion that should be represented as Kotlin properties.
- Nullable boolean comparison that may need manual semantic review.
- Java interop leftovers or `Any?` that indicate weak type preservation.

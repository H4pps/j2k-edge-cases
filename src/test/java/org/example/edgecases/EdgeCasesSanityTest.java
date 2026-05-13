package org.example.edgecases;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Sanity checks to ensure all edge-case sources compile and execute deterministically.
 */
class EdgeCasesSanityTest {

    @Test
    void nestedAnonymousClassesCaseRendersWrappedValue() {
        NestedAnonymousClassesCase edgeCase = new NestedAnonymousClassesCase();

        assertEquals("wrapped(value)", edgeCase.render(" value "));
    }

    @Test
    void recursiveGenericsAndWildcardsCopyAndFilter() {
        RecursiveGenericsWildcardsCase edgeCase = new RecursiveGenericsWildcardsCase();
        List<RecursiveGenericsWildcardsCase.NamedNode> source =
                List.of(new RecursiveGenericsWildcardsCase.NamedNode("one"),
                        new RecursiveGenericsWildcardsCase.NamedNode("three"));

        List<RecursiveGenericsWildcardsCase.Self<?>> sink = new java.util.ArrayList<>();
        assertEquals(2, edgeCase.copyAll(sink, source));
        assertEquals(List.of("three"), edgeCase.filterIds(sink, 5));
    }

    @Test
    void samOverloadsAreInvokedViaExplicitCasts() {
        SamLambdaOverloadsCase edgeCase = new SamLambdaOverloadsCase();

        assertEquals("S:text;C:TEXT", edgeCase.evaluate(" text "));
    }

    @Test
    void annotationHeavyClassCanBeConstructedAndCalled() {
        List<List<String>> auditEvents = new ArrayList<>();
        AnnotationHeavyFrameworkStyleCase.AuditSink sink = auditEvents::add;
        AnnotationHeavyFrameworkStyleCase edgeCase = new AnnotationHeavyFrameworkStyleCase(sink);

        assertEquals("created:SKU-123", edgeCase.createOrder(" sku-123 "));
        assertEquals("found:order-1:trace-9", edgeCase.findOrder(" order-1 ", " trace-9 "));
        assertEquals("rejected:blank-request", edgeCase.createOrder("   "));
        assertEquals("audit", sink.name());
        assertEquals(
                List.of(
                        List.of("createOrder", "SKU-123"),
                        List.of("findOrder", "order-1", "trace-9"),
                        List.of("rejectOrder", "blank-request")),
                auditEvents);
    }

    @Test
    void tryWithResourcesProducesExpectedOutputs() throws IOException {
        TryWithResourcesCase edgeCase = new TryWithResourcesCase();

        assertEquals("LINE", edgeCase.upperFirstLine("line\nsecond"));
        assertEquals(List.of("work", "close:second", "close:first"), edgeCase.closeOrderEvents());
    }

    @Test
    void checkedExceptionsSupportFallbackLogic() throws CheckedExceptionsCase.InvalidConfigurationException {
        CheckedExceptionsCase edgeCase = new CheckedExceptionsCase();

        assertEquals(42, edgeCase.parsePositiveInt("42"));
        assertEquals(7, edgeCase.parseOrDefault("nope", 7));
        assertInstanceOf(
                CheckedExceptionsCase.InvalidConfigurationException.class,
                org.junit.jupiter.api.Assertions.assertThrows(
                        CheckedExceptionsCase.InvalidConfigurationException.class,
                        () -> edgeCase.parsePositiveInt("0")));
    }

    @Test
    void staticNestedCompanionLikePatternBuildsInstances() {
        StaticNestedCompanionLikeCase created = StaticNestedCompanionLikeCase.Companion.from(" custom ");

        assertEquals("custom", created.value());
        assertEquals("standard", StaticNestedCompanionLikeCase.Companion.standard().value());
    }

    @Test
    void varargsAndArraysBehavePredictably() {
        VarargsArraysCase edgeCase = new VarargsArraysCase();

        assertEquals(6, edgeCase.sum(1, 2, 3));
        assertEquals("p[a,b]", edgeCase.joinWithPrefix("p", new String[]{"a", "b"}));
        assertEquals(List.of("a", "b", "c"), List.of(edgeCase.flatten(new String[]{"a"}, new String[]{"b", "c"})));
    }

    @Test
    void javaBeanPropertiesExposeExpectedAccessors() {
        JavaBeanPropertiesCase bean = new JavaBeanPropertiesCase();
        bean.setEnabled(true);
        bean.setURL("https://example.test");
        bean.setRetryCount(3);

        assertEquals(true, bean.isEnabled());
        assertEquals("https://example.test", bean.getURL());
        assertEquals(3, bean.getRetryCount());
    }

    @Test
    void nullabilityAnnotationsCaseUsesFallback() {
        NullabilityAnnotationsCase edgeCase =
                new NullabilityAnnotationsCase(Map.of("present", "value"));

        assertEquals("value", edgeCase.find("present"));
        assertEquals("fallback", edgeCase.requireOrFallback("missing", "fallback"));
    }

    @Test
    void interfaceGetterDefaultsResolveThroughOverride() {
        InterfaceGetterDefaultMethodsCase.Sample sample =
                new InterfaceGetterDefaultMethodsCase().sample("alpha", 9);

        assertEquals("alpha", sample.getLabel());
        assertEquals(9, sample.getCode());
        assertEquals("label=alpha,code=9", sample.summary());
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void rawTypesUncheckedCastsProduceTypedList() {
        RawTypesUncheckedCastsCase edgeCase = new RawTypesUncheckedCastsCase();
        Map rawMap = new HashMap();
        rawMap.put("values", List.of("x", 1, true));

        assertEquals(List.of("x", "1", "true"), edgeCase.readStringList(rawMap, "values"));
    }

    @Test
    void nullableBooleanSemanticsPreserveExplicitTrueChecks() {
        NullableBooleanSemanticsCase edgeCase = new NullableBooleanSemanticsCase();

        assertEquals("disabled", edgeCase.classify(null, null));
        assertEquals("disabled", edgeCase.classify(false, false));
        assertEquals("active", edgeCase.classify(true, null));
        assertEquals("archived", edgeCase.classify(true, true));
        assertEquals(true, edgeCase.isExplicitlyDisabled(false));
    }

    @Test
    void missingNullabilityAnnotationsStillBehaveThroughJavaChecks() {
        MissingNullabilityAnnotationsCase edgeCase =
                new MissingNullabilityAnnotationsCase(Map.of("present", "value"));

        assertEquals("value", edgeCase.findOrNull("present"));
        assertEquals(99, edgeCase.lengthOrDefault("missing", 99));
        assertEquals("fallback", edgeCase.chooseFirst(null, "fallback"));
    }

    @Test
    void riskyNotNullAssertionInCallPatternsRenderValues() {
        RiskyNotNullAssertionInCallCase edgeCase = new RiskyNotNullAssertionInCallCase();

        assertEquals("LEFT:right", edgeCase.normalizePair(" left ", " RIGHT "));
        assertEquals(
                "[nested]",
                edgeCase.renderHolder(
                        new RiskyNotNullAssertionInCallCase.Holder(
                                new RiskyNotNullAssertionInCallCase.Payload(" nested "))));
    }

    @Test
    void recordsAndSealedTypesDescribeKnownShapes() {
        RecordsAndSealedTypesCase edgeCase = new RecordsAndSealedTypesCase();

        assertEquals("circle:3:27", edgeCase.describe(new RecordsAndSealedTypesCase.Circle(3)));
        assertEquals("rectangle:4x5:20", edgeCase.describe(new RecordsAndSealedTypesCase.Rectangle(4, 5)));
    }

    @Test
    void switchExpressionsAndInstanceofPatternsClassifyInputs() {
        SwitchAndPatternMatchingCase edgeCase = new SwitchAndPatternMatchingCase();

        assertEquals("one-char", edgeCase.classify("x"));
        assertEquals("short-text", edgeCase.classify("abc"));
        assertEquals("long-text", edgeCase.classify("abcd"));
        assertEquals("negative-number", edgeCase.classify(-1));
        assertEquals("other", edgeCase.classify(""));
    }
}

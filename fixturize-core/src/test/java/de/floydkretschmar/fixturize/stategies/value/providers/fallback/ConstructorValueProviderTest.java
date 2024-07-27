package de.floydkretschmar.fixturize.stategies.value.providers.fallback;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.stategies.metadata.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.value.ValueProviderService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.TestFixtures.createExecutableElementFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixture;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.Modifier.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConstructorValueProviderTest {

    private ConstructorValueProvider valueProvider;

    @Mock
    private ValueProviderService valueProviderService;

    @Mock
    private VariableElement field;
    @BeforeEach
    void setup() {
        valueProvider = new ConstructorValueProvider(valueProviderService);
    }

    public static Stream<Arguments> getParametersForLombokAllArgsConstructorTest() {
        return TestFixtures.getMetadataParameters(
                "new some.test.Class(10, secondFieldValue)",
                "new some.test.Class<>(10, secondFieldValue)");
    }

    @ParameterizedTest
    @MethodSource("getParametersForLombokAllArgsConstructorTest")
    void provideValueAsString_whenFallbackForLombokAllArgsConstructor_returnConstructorValueWithAllFieldsAsParameters(
            TypeMetadata metadata, VariableElement field2, Element expectedElementForValueService, String expectedOutcome) {
        final var field1 = createVariableElementFixture("integerField", false, true, FIELD);
        final var type = TestFixtures.createDeclaredTypeFixture(field1, field2);
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(mock(AllArgsConstructor.class));

        when(field.asType()).thenReturn(type);

        when(valueProviderService.getValueFor(eq(field1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(expectedElementForValueService))).thenReturn("secondFieldValue");

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(expectedOutcome);
        verify(valueProviderService, times(1)).getValueFor(field1);
        verify(valueProviderService, times(1)).getValueFor(expectedElementForValueService);
        verifyNoMoreInteractions(valueProviderService);
    }

    public static Stream<Arguments> getParametersForLombokRequiredArgsConstructorTest() {
        return TestFixtures.getMetadataParameters(
                "new some.test.Class(secondFieldValue)",
                "new some.test.Class<>(secondFieldValue)");
    }

    @ParameterizedTest
    @MethodSource("getParametersForLombokRequiredArgsConstructorTest")
    void provideValueAsString_whenFallbackForLombokRequiredArgsConstructor_returnConstructorValueWithAllFinalFieldsWithoutConstantAsParameters(
            TypeMetadata metadata, VariableElement field2, Element expectedElementForValueService, String expectedOutcome) {
        final var field1 = createVariableElementFixture(null, false, true, FIELD);
        when(field1.getModifiers()).thenReturn(Set.of(PRIVATE));
        when(field2.getModifiers()).thenReturn(Set.of(PRIVATE, FINAL));
        final var field3 = createVariableElementFixture(null, false, true, FIELD);
        when(field3.getModifiers()).thenReturn(Set.of(PRIVATE, FINAL));
        when(field3.getConstantValue()).thenReturn(true);

        final var type = TestFixtures.createDeclaredTypeFixture(field1, field2, field3);
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(mock(RequiredArgsConstructor.class));

        when(field.asType()).thenReturn(type);

        when(valueProviderService.getValueFor(eq(expectedElementForValueService))).thenReturn("secondFieldValue");

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(expectedOutcome);
        verify(valueProviderService, times(1)).getValueFor(expectedElementForValueService);
        verifyNoMoreInteractions(valueProviderService);
    }

    private static Stream<Arguments> getParametersForLombokNoArgsConstructorTest() {
        return Stream.of(
                Arguments.of(TestFixtures.createMetadataFixture(), "new some.test.Class()"),
                Arguments.of(TestFixtures.createMetadataFixtureBuilder("Class", "<String>")
                        .genericTypeMap(Map.of(mock(TypeMirror.class), mock(DeclaredType.class)))
                        .build(), "new some.test.Class<>()")
        );
    }

    @ParameterizedTest
    @MethodSource("getParametersForLombokNoArgsConstructorTest")
    void provideValueAsString_whenFallbackForLombokNoArgsConstructor_returnConstructorValueWithNoParameters(TypeMetadata metadata, String expectedOutcome) {
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(mock(NoArgsConstructor.class));

        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(expectedOutcome);
        verifyNoInteractions(valueProviderService);
    }

    public static Stream<Arguments> getParametersForConstructorTest() {
        return TestFixtures.getMetadataParameters(
                "new some.test.Class(10, secondFieldValue)",
                "new some.test.Class<>(10, secondFieldValue)");
    }

    @ParameterizedTest
    @MethodSource("getParametersForConstructorTest")
    void provideValueAsString_whenFallbackDefinedConstructor_returnConstructorValueWithParameters(
            TypeMetadata metadata, VariableElement parameter2, Element expectedElementForValueService, String expectedOutcome) {
        final var parameter1 = createVariableElementFixture("integerParameter", false, true, null);
        final var constructor1 = createExecutableElementFixture(CONSTRUCTOR, PUBLIC);
        when(constructor1.getParameters()).thenReturn((List) List.of(parameter1, parameter2));
        final var constructor2 = createExecutableElementFixture(CONSTRUCTOR, PUBLIC);
        when(constructor2.getParameters()).thenReturn((List) List.of(parameter1));

        final var type = TestFixtures.createDeclaredTypeFixture(constructor1, constructor2);

        when(field.asType()).thenReturn(type);

        when(valueProviderService.getValueFor(eq(parameter1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(expectedElementForValueService))).thenReturn("secondFieldValue");

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(expectedOutcome);
        verify(valueProviderService, times(1)).getValueFor(parameter1);
        verify(valueProviderService, times(1)).getValueFor(expectedElementForValueService);
        verifyNoMoreInteractions(valueProviderService);
    }
}
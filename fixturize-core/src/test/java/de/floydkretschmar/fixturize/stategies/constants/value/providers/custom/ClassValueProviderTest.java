package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ClassValueProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.TestFixtures.createDeclaredTypeFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createExecutableElementFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureBuilderFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureConstructorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createMetadataFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeMirrorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createVariableElementFixture;
import static de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider.DEFAULT_VALUE;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassValueProviderTest {

    private ClassValueProvider valueProvider;

    @Mock
    private ValueProviderService valueProviderService;

    @Mock
    private VariableElement field;

    @BeforeEach
    void setup() {
        valueProvider = new ClassValueProvider(valueProviderService);
    }

    private static Stream<Arguments> getParametersForLombokTest(String expectedOutcome, String expectedGenericOutcome) {
        final var genericField = createVariableElementFixture("secondField", true, true, FIELD);
        final var genericFieldType = createDeclaredTypeFixture();
        final var genericMetadata = TestFixtures.createMetadataFixtureBuilder("Class", "<String>")
                .genericTypeMap(Map.of(genericField.asType(), genericFieldType)).build();

        final var nonGenericField = createVariableElementFixture("secondField", true, true, FIELD);

        return Stream.of(
                Arguments.of(
                        TestFixtures.createMetadataFixture(),
                        nonGenericField,
                        nonGenericField,
                        expectedOutcome),
                Arguments.of(
                        genericMetadata,
                        genericField,
                        genericFieldType.asElement(),
                        expectedGenericOutcome)
        );
    }

    private static Stream<Arguments> getMetadataParameters(String expectedOutcome, String expectedGenericOutcome) {
        return Stream.of(
                Arguments.of(TestFixtures.createMetadataFixture(), expectedOutcome),
                Arguments.of(TestFixtures.createMetadataFixtureBuilder("Class", "<String>")
                        .genericTypeMap(Map.of(mock(TypeMirror.class), mock(DeclaredType.class)))
                        .build(), expectedGenericOutcome)
        );
    }

    public static Stream<Arguments> getMetadataParameters() {
        return Stream.of(
                Arguments.of(TestFixtures.createMetadataFixture()),
                Arguments.of(TestFixtures.createMetadataFixtureBuilder("Class", "<String>")
                        .genericTypeMap(Map.of(mock(TypeMirror.class), mock(DeclaredType.class)))
                        .build())
        );
    }

    @ParameterizedTest
    @MethodSource("getMetadataParameters")
    void provideValueAsString_whenFallbackForFixtureBuilder_returnValueStringForFixtureBuilderWithMostSetters(TypeMetadata metadata) {
        final var fixtureBuilder = createFixtureBuilderFixture("methodName1", null, "param1", "param2");
        final var fixtureBuilder2 = createFixtureBuilderFixture(null, null, "param1");
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{fixtureBuilder, fixtureBuilder2});
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo("some.test.ClassFixture.methodName1().build()");
        verifyNoInteractions(valueProviderService);
    }

    @ParameterizedTest
    @MethodSource("getMetadataParameters")
    void provideValueAsString_whenFallbackForFixtureConstructor_returnValueStringForFixtureConstructorWithMostParameters(TypeMetadata metadata) {
        final var fixtureConstructor = createFixtureConstructorFixture("methodName1", "param1", "param2");
        final var fixtureConstructor2 = createFixtureConstructorFixture(null, "param1", "param2");
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{fixtureConstructor, fixtureConstructor2});
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo("some.test.ClassFixture.methodName1()");
        verifyNoInteractions(valueProviderService);
    }

    public static Stream<Arguments> getParametersForLombokBuilderTest() {
        return getParametersForLombokTest(
                "some.test.Class.builder().integerField(10).secondField(secondFieldValue).build()",
                "some.test.Class.<String>builder().integerField(10).secondField(secondFieldValue).build()");
    }

    @ParameterizedTest
    @MethodSource("getParametersForLombokBuilderTest")
    void provideValueAsString_whenFallbackForLombokBuilder_returnBuilderValueWithAllFieldsAsSetters(
            TypeMetadata metadata, VariableElement field2, Element expectedElementForValueService, String expectedOutcome) {
        final var field1 = createVariableElementFixture("integerField", true, true, FIELD);
        final var type = TestFixtures.createDeclaredTypeFixture(field1, field2);
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(mock(Builder.class));

        when(field.asType()).thenReturn(type);

        when(valueProviderService.getValueFor(eq(field1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(expectedElementForValueService))).thenReturn("secondFieldValue");

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(expectedOutcome);
        verify(valueProviderService, times(1)).getValueFor(field1);
        verify(valueProviderService, times(1)).getValueFor(expectedElementForValueService);
        verifyNoMoreInteractions(valueProviderService);
    }

    public static Stream<Arguments> getParametersForLombokAllArgsConstructorTest() {
        return getParametersForLombokTest(
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

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
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
        return getParametersForLombokTest(
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

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
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
        return getMetadataParameters("new some.test.Class()", "new some.test.Class<>()");
    }

    @ParameterizedTest
    @MethodSource("getParametersForLombokNoArgsConstructorTest")
    void provideValueAsString_whenFallbackForLombokNoArgsConstructor_returnConstructorValueWithNoParameters(TypeMetadata metadata, String expectedOutcome) {
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
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
        return getParametersForLombokTest(
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
        final var typeAsElement = (TypeElement) type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(null);

        when(field.asType()).thenReturn(type);

        when(valueProviderService.getValueFor(eq(parameter1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(expectedElementForValueService))).thenReturn("secondFieldValue");

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(expectedOutcome);
        verify(valueProviderService, times(1)).getValueFor(parameter1);
        verify(valueProviderService, times(1)).getValueFor(expectedElementForValueService);
        verifyNoMoreInteractions(valueProviderService);
    }


    public static Stream<Arguments> getParametersForBuilderTest() {
        return getParametersForLombokTest(
                "some.test.Class.builder().setIntegerField(10).setSecondField(secondFieldValue).build()",
                "some.test.Class.<String>builder().setIntegerField(10).setSecondField(secondFieldValue).build()");
    }

    @ParameterizedTest
    @MethodSource("getParametersForBuilderTest")
    void provideValueAsString_whenFallbackDefinedBuilderMethod_returnBuilderValueWithAllAvailableSetters(
            TypeMetadata metadata, VariableElement field2, Element expectedElementForValueService, String expectedOutcome) {
        final var classBuilderType = TestFixtures.createDeclaredTypeFixture("%s.%sBuilder".formatted(metadata.getQualifiedClassNameWithoutGeneric(), metadata.getSimpleClassNameWithoutGeneric()));
        final var setter1 = createExecutableElementFixture("setIntegerField", METHOD, classBuilderType, PUBLIC);
        when(setter1.getParameters()).thenReturn((List) List.of(mock(VariableElement.class)));
        final var setter2 = createExecutableElementFixture("setSecondField", METHOD, classBuilderType, PUBLIC);
        when(setter2.getParameters()).thenReturn((List) List.of(mock(VariableElement.class)));

        final var builderMethod = createExecutableElementFixture("builder", METHOD, classBuilderType, PUBLIC, STATIC);
        final var field1 = createVariableElementFixture("integerField", false, true, FIELD);
        when(field1.toString()).thenReturn("integerField");
        when(field2.toString()).thenReturn("secondField");
        final var field3 = createVariableElementFixture(null, false, true, null);
        when(field3.toString()).thenReturn("fieldWithoutSetter");
        when(field3.getKind()).thenReturn(FIELD);
        final var classType = createDeclaredTypeFixture(metadata.getQualifiedClassName(), builderMethod, field1, field2, field3);

        final var buildMethod = createExecutableElementFixture("build", METHOD, classType, PUBLIC);
        when(classBuilderType.asElement().getEnclosedElements()).thenReturn((List) List.of(setter1, setter2, buildMethod));

        final var typeAsElement = (TypeElement) classType.asElement();
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(null);

        when(field.asType()).thenReturn(classType);

        when(valueProviderService.getValueFor(eq(field1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(expectedElementForValueService))).thenReturn("secondFieldValue");

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(expectedOutcome);
        verify(valueProviderService, times(1)).getValueFor(field1);
        verify(valueProviderService, times(1)).getValueFor(expectedElementForValueService);
        verifyNoMoreInteractions(valueProviderService);
    }


    @ParameterizedTest
    @MethodSource("getMetadataParameters")
    void provideValueAsString_whenFallbackDefinedBuilderMethodButNoBuilderFound_returnDefaultValue(TypeMetadata metadata) {
        final var field1 = mock(VariableElement.class);
        final var field2 = mock(VariableElement.class);
        final var classType = TestFixtures.createDeclaredTypeFixture(field1, field2);

        final var typeAsElement = (TypeElement) classType.asElement();
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(null);

        when(field.asType()).thenReturn(classType);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
        verifyNoInteractions(valueProviderService);
    }


    @ParameterizedTest
    @MethodSource("getMetadataParameters")
    void provideValueAsString_whenFallbackDefinedBuilderMethodButNoBuildMethodFound_returnDefaultValue(TypeMetadata metadata) {
        final var classBuilderType = mock(DeclaredType.class);

        final var builderMethod = createExecutableElementFixture(METHOD, PUBLIC, STATIC);
        when(builderMethod.getReturnType()).thenReturn(classBuilderType);
        final var field1 = mock(VariableElement.class);
        final var field2 = mock(VariableElement.class);
        final var classType = TestFixtures.createDeclaredTypeFixture(builderMethod, field1, field2);

        final var typeAsElement = (TypeElement) classType.asElement();
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(Builder.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(AllArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(RequiredArgsConstructor.class))))
                .thenReturn(null);
        when(typeAsElement.getAnnotation(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(NoArgsConstructor.class))))
                .thenReturn(null);

        when(field.asType()).thenReturn(classType);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
        verifyNoInteractions(valueProviderService);
    }

    @Test
    void canProvideFallback_whenCalledForDeclaredTypeClass_returnTrue() {
        final var element = createVariableElementFixture(null, createDeclaredTypeFixture(CLASS));

        final var result = valueProvider.canProvideFallback(element, createMetadataFixture());

        assertThat(result).isTrue();
    }

    @Test
    void canProvideFallback_whenCalledForDeclaredTypeNotClass_returnFalse() {
        final var element = createVariableElementFixture(null, createDeclaredTypeFixture(ENUM));

        final var result = valueProvider.canProvideFallback(element, createMetadataFixture());

        assertThat(result).isFalse();
    }

    @Test
    void canProvideFallback_whenCalledForOtherThanDeclaredType_returnFalse() {
        final var element = createVariableElementFixture(null, createTypeMirrorFixture(TypeKind.INT));

        final var result = valueProvider.canProvideFallback(element, createMetadataFixture());

        assertThat(result).isFalse();
    }
}
package de.floydkretschmar.fixturize.stategies.value.providers.fallback;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.stategies.metadata.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.value.ValueProviderService;
import lombok.Builder;
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
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.TestFixtures.*;
import static de.floydkretschmar.fixturize.stategies.value.providers.ValueProvider.DEFAULT_VALUE;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuilderValueProviderTest {

    private BuilderValueProvider valueProvider;

    @Mock
    private ValueProviderService valueProviderService;

    @Mock
    private VariableElement field;

    @BeforeEach
    void setup() {
        valueProvider = new BuilderValueProvider(valueProviderService);
    }

    static Stream<Arguments> getParametersForLombokBuilderTest() {
        return TestFixtures.getMetadataParameters(
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

    static Stream<Arguments> getParametersForBuilderTest() {
        return TestFixtures.getMetadataParameters(
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

        when(field.asType()).thenReturn(classType);

        when(valueProviderService.getValueFor(eq(field1))).thenReturn("10");
        when(valueProviderService.getValueFor(eq(expectedElementForValueService))).thenReturn("secondFieldValue");

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(expectedOutcome);
        verify(valueProviderService, times(1)).getValueFor(field1);
        verify(valueProviderService, times(1)).getValueFor(expectedElementForValueService);
        verifyNoMoreInteractions(valueProviderService);
    }

    static Stream<Arguments> getMetadataParameters() {
        return Stream.of(
                Arguments.of(TestFixtures.createMetadataFixture()),
                Arguments.of(TestFixtures.createMetadataFixtureBuilder("Class", "<String>")
                        .genericTypeMap(Map.of(mock(TypeMirror.class), mock(DeclaredType.class)))
                        .build())
        );
    }

    @ParameterizedTest
    @MethodSource("getMetadataParameters")
    void provideValueAsString_whenFallbackDefinedBuilderMethodButNoBuilderFound_returnDefaultValue(TypeMetadata metadata) {
        final var field1 = mock(VariableElement.class);
        final var field2 = mock(VariableElement.class);
        final var classType = TestFixtures.createDeclaredTypeFixture(field1, field2);

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

        when(field.asType()).thenReturn(classType);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo(DEFAULT_VALUE);
        verifyNoInteractions(valueProviderService);
    }
}
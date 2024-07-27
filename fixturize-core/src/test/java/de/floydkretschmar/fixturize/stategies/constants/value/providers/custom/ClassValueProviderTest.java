package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilderSetter;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ClassValueProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.TestFixtures.*;
import static de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider.DEFAULT_VALUE;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassValueProviderTest {

    private ClassValueProvider valueProvider;

    @Mock
    private ValueProvider builderValueProvider;
    @Mock
    private ValueProvider constructorValueProvider;

    @Mock
    private VariableElement field;

    @BeforeEach
    void setup() {
        valueProvider = new ClassValueProvider(builderValueProvider, constructorValueProvider);
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
        final var fixtureBuilder = createFixtureBuilderFixture("methodName1", null, mock(FixtureBuilderSetter.class), mock(FixtureBuilderSetter.class));
        final var fixtureBuilder2 = createFixtureBuilderFixture(null, null, mock(FixtureBuilderSetter.class));
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = type.asElement();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{fixtureBuilder, fixtureBuilder2});
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo("some.test.ClassFixture.methodName1().build()");
        verifyNoInteractions(constructorValueProvider, builderValueProvider);
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
        when(builderValueProvider.provideValueAsString(any(), any())).thenReturn(DEFAULT_VALUE);
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo("some.test.ClassFixture.methodName1()");
        verify(builderValueProvider, times(1)).provideValueAsString(field, metadata);
        verifyNoMoreInteractions(builderValueProvider);
        verifyNoInteractions(constructorValueProvider);
    }

    @Test
    void provideValueAsString_whenFallbackForBuilder_returnBuilderValueFromProvider() {
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = (TypeElement) type.asElement();
        final var metadata = TestFixtures.createMetadataFixture();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(builderValueProvider.provideValueAsString(any(), any())).thenReturn("builderValue");
        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo("builderValue");
        verify(builderValueProvider, times(1)).provideValueAsString(field, metadata);
        verifyNoMoreInteractions(builderValueProvider);
        verifyNoInteractions(constructorValueProvider);
    }

    @Test
    void provideValueAsString_whenFallbackForConstructor_returnBuilderValueFromProvider() {
        final var type = TestFixtures.createDeclaredTypeFixture();
        final var typeAsElement = (TypeElement) type.asElement();
        final var metadata = TestFixtures.createMetadataFixture();

        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureBuilder.class))))
                .thenReturn(new FixtureBuilder[]{});
        when(typeAsElement.getAnnotationsByType(ArgumentMatchers.argThat(param -> Objects.nonNull(param) && param.equals(FixtureConstructor.class))))
                .thenReturn(new FixtureConstructor[]{});
        when(builderValueProvider.provideValueAsString(any(), any())).thenReturn(DEFAULT_VALUE);
        when(constructorValueProvider.provideValueAsString(any(), any())).thenReturn("constructorValue");

        when(field.asType()).thenReturn(type);

        final var result = valueProvider.provideValueAsString(field, metadata);

        assertThat(result).isEqualTo("constructorValue");
        verify(builderValueProvider, times(1)).provideValueAsString(field, metadata);
        verify(constructorValueProvider, times(1)).provideValueAsString(field, metadata);
        verifyNoMoreInteractions(constructorValueProvider,builderValueProvider);
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
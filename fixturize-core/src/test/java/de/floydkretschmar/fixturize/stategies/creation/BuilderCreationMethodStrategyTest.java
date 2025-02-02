package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.ConstantMap;
import de.floydkretschmar.fixturize.stategies.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.value.providers.fallback.BuilderValueProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.floydkretschmar.fixturize.TestFixtures.*;
import static de.floydkretschmar.fixturize.stategies.value.providers.ValueProvider.DEFAULT_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuilderCreationMethodStrategyTest {
    private ConstantMap constantMap;
    private BuilderCreationMethodStrategy strategy;

    @Mock
    private ValueProviderService valueProviderService;

    @Mock
    private BuilderValueProvider noUsedSettersValueProvider;

    @BeforeEach
    void setup() {
        strategy = new BuilderCreationMethodStrategy(valueProviderService, noUsedSettersValueProvider);
    }

    @Test
    void createCreationMethods_whenMultipleBuildersDefined_shouldCreateCreationMethodsForDefinedBuilders() {
        constantMap = createConstantDefinitionMapMock();
        final var element = createTypeElementFixture(
                createFixtureBuilderFixture("methodName", "builder", "build", createFixtureBuilderSetterFixture("stringField", "stringField"), createFixtureBuilderSetterFixture("intField", "intField")),
                createFixtureBuilderFixture("methodName2", "builder2", "build", createFixtureBuilderSetterFixture("uuidField", "uuidField")));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(2);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("some.test.TestObject.builder().stringField(stringFieldName).intField(intFieldName).build()")
                        .name("methodName")
                        .build(),
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("some.test.TestObject.builder2().uuidField(uuidFieldName).build()")
                        .name("methodName2")
                        .build());

        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(constantMap, times(1)).getMatchingConstants(List.of("uuidField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(constantMap, element);
        verifyNoInteractions(noUsedSettersValueProvider);
    }

    @Test
    void createCreationMethods_whenSingleBuilderDefined_shouldCreateCreationMethodForDefinedBuilder() {
        constantMap = createConstantDefinitionMapMock();
        final var element = createTypeElementFixture(
                createFixtureBuilderFixture("methodName", "builder", "build", createFixtureBuilderSetterFixture("stringField", "stringField"), createFixtureBuilderSetterFixture("intField", "intField")));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("some.test.TestObject.builder().stringField(stringFieldName).intField(intFieldName).build()")
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(constantMap, element);
        verifyNoInteractions(noUsedSettersValueProvider);
    }

    @Test
    void createCreationMethods_whenAsBuilder_shouldCreateCreationMethodAsBuilder() {
        constantMap = createConstantDefinitionMapMock();
        final var annotation = createFixtureBuilderFixture("methodName", "builder", "build", createFixtureBuilderSetterFixture("stringField", "stringField"), createFixtureBuilderSetterFixture("intField", "intField"));
        when(annotation.asBuilder()).thenReturn(true);
        final var element = createTypeElementFixture(annotation);

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject.TestObjectBuilder")
                        .returnValue("some.test.TestObject.builder().stringField(stringFieldName).intField(intFieldName)")
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(constantMap, element);
        verifyNoInteractions(noUsedSettersValueProvider);
    }

    @Test
    void createCreationMethods_whenBuilderWithNoUsedSetterDefined_shouldCreateCreationMethodForBuilderWithAllSetters() {
        final var element = createTypeElementFixture(
                createFixtureBuilderFixture(
                        "methodName",
                        "builder",
                        "build"));
        final var metadata = TestFixtures.createMetadataFixture("TestObject");
        when(noUsedSettersValueProvider.provideValueAsString(any(), any(), anyString(), anyString())).thenReturn("noUsedSettersValue");

        final var result = strategy.generateCreationMethods(element, constantMap, metadata);

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("noUsedSettersValue")
                        .name("methodName")
                        .build());
        verify(noUsedSettersValueProvider).provideValueAsString(element, metadata, "builder", "build");
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(element, noUsedSettersValueProvider);
    }

    @Test
    void createCreationMethods_whenBuilderWithNoUsedSetterDefinedReturnsDefaultValue_shouldThrowFixtureCreationException() {
        final var element = createTypeElementFixture(
                createFixtureBuilderFixture(
                        null,
                        "builder",
                        "build"));
        final var metadata = TestFixtures.createMetadataFixture("TestObject");
        when(noUsedSettersValueProvider.provideValueAsString(any(), any(), anyString(), anyString())).thenReturn(DEFAULT_VALUE);

        assertThrows(FixtureCreationException.class, () -> strategy.generateCreationMethods(element, constantMap, metadata));

        verify(noUsedSettersValueProvider).provideValueAsString(element, metadata, "builder", "build");
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(element, noUsedSettersValueProvider);
    }

    @Test
    void createCreationMethods_whenGenericDefined_shouldCreateCreationMethodForDefinedBuilder() {
        constantMap = createConstantDefinitionMapMock();
        final var element = createTypeElementFixture(
                createFixtureBuilderFixture("methodName", "builder", "build", createFixtureBuilderSetterFixture("stringField", "stringField"), createFixtureBuilderSetterFixture("intField", "intField")));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixtureBuilder("TestObject", "<String>").build());

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject<String>")
                        .returnValue("some.test.TestObject.<String>builder().stringField(stringFieldName).intField(intFieldName).build()")
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(constantMap, element);
        verifyNoInteractions(noUsedSettersValueProvider);
    }

    @Test
    void createCreationMethods_whenNoBuilderDefined_shouldReturnEmptyList() {
        final var element = TestFixtures.<FixtureBuilder>createTypeElementFixture();

        final Collection<CreationMethod> result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(0);

        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(element);
        verifyNoInteractions(noUsedSettersValueProvider);
    }

    @Test
    void createCreationMethods_whenCalledWithValueThatDoesNotMatchConstant_shouldTryAndResolveValue() {
        final var element = createTypeElementFixture(
                createFixtureBuilderFixture("methodName", "builder", "build", createFixtureBuilderSetterFixture("stringField", "valueThatDoesNotMatchConstant")));

        constantMap = mock(ConstantMap.class);
        when(constantMap.getMatchingConstants(anyCollection())).thenReturn(Map.of("valueThatDoesNotMatchConstant", Optional.empty()));
        when(valueProviderService.resolveValuesForDefaultPlaceholders(anyString())).thenReturn("resolvedValue");

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("some.test.TestObject.builder().stringField(resolvedValue).build()")
                        .name("methodName")
                        .build());

        verify(constantMap, times(1)).getMatchingConstants(List.of("valueThatDoesNotMatchConstant"));
        verify(valueProviderService, times(1)).resolveValuesForDefaultPlaceholders("valueThatDoesNotMatchConstant");
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(constantMap, element);
        verifyNoInteractions(noUsedSettersValueProvider);
    }

    @Test
    void createCreationMethods_whenCalledWithSetterWithoutValue_shouldUseSetterNameAsValue() {
        final var element = createTypeElementFixture(
                createFixtureBuilderFixture("methodName", "builder", "build", createFixtureBuilderSetterFixture("stringField", "")));

        constantMap = mock(ConstantMap.class);
        when(constantMap.getMatchingConstants(anyCollection())).thenReturn(Map.of("stringField", Optional.of(STRING_FIELD_DEFINITION)));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("some.test.TestObject.builder().stringField(stringFieldName).build()")
                        .name("methodName")
                        .build());

        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField"));
        verify(element, times(1)).getAnnotationsByType(FixtureBuilder.class);
        verifyNoMoreInteractions(constantMap, element);
        verifyNoInteractions(noUsedSettersValueProvider);
    }
}
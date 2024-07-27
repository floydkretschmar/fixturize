package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.CreationMethod;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.ConstantMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
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
import static de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider.DEFAULT_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConstructorCreationMethodStrategyTest {
    private ConstantMap constantMap;
    private ConstructorCreationMethodStrategy strategy;

    @Mock
    private ValueProviderService valueProviderService;

    @Mock
    private ValueProvider noConstructorParameterValueProvider;

    @BeforeEach
    void setup() {
        strategy = new ConstructorCreationMethodStrategy(valueProviderService, noConstructorParameterValueProvider);
    }

    @Test
    void createCreationMethods_whenMultipleConstructorsDefined_shouldCreateCreationMethodsForDefinedConstructors() {
        constantMap = createConstantDefinitionMapMock();
        final var element = createTypeElementFixture(
                createFixtureConstructorFixture("methodName", "stringField", "intField"),
                createFixtureConstructorFixture("methodName2", "uuidField"));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(2);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("new some.test.TestObject(stringFieldName, intFieldName)")
                        .name("methodName")
                        .build(),
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("new some.test.TestObject(uuidFieldName)")
                        .name("methodName2")
                        .build());

        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(constantMap, times(1)).getMatchingConstants(List.of("uuidField"));
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(constantMap, element);
    }


    @Test
    void createCreationMethods_whenSingleConstructorDefined_shouldCreateCreationMethodForDefinedConstructor() {
        constantMap = createConstantDefinitionMapMock();
        final var element = createTypeElementFixture(
                createFixtureConstructorFixture("methodName", "stringField", "intField"));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("new some.test.TestObject(stringFieldName, intFieldName)")
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(constantMap, element);
    }

    @Test
    void createCreationMethods_whenConstructorWithNoParametersDefined_shouldCreateCreationMethodForConstructorAccordingToProvider() {
        final var element = createTypeElementFixture(
                createFixtureConstructorFixture("methodName"));
        final var metadata = TestFixtures.createMetadataFixture("TestObject");
        when(noConstructorParameterValueProvider.provideValueAsString(any(), any())).thenReturn("noConstructorParameterValue");

        final var result = strategy.generateCreationMethods(element, constantMap, metadata);

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("noConstructorParameterValue")
                        .name("methodName")
                        .build());
        verify(noConstructorParameterValueProvider).provideValueAsString(element, metadata);
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(element, noConstructorParameterValueProvider);
    }

    @Test
    void createCreationMethods_whenConstructorWithNoParametersDefinedReturnsDefaultValue_shouldThrowFixtureCreationException() {
        final var element = createTypeElementFixture(
                createFixtureConstructorFixture(null));
        final var metadata = TestFixtures.createMetadataFixture("TestObject");
        when(noConstructorParameterValueProvider.provideValueAsString(any(), any())).thenReturn(DEFAULT_VALUE);

        assertThrows(FixtureCreationException.class, () -> strategy.generateCreationMethods(element, constantMap, metadata));

        verify(noConstructorParameterValueProvider).provideValueAsString(element, metadata);
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(element, noConstructorParameterValueProvider);
    }


    @Test
    void createCreationMethods_whenGenericDefined_shouldCreateCreationMethodForDefinedConstructor() {
        constantMap = createConstantDefinitionMapMock();
        final var element = createTypeElementFixture(
                createFixtureConstructorFixture("methodName", "stringField", "intField"));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixtureBuilder("TestObject", "<String>").build());

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject<String>")
                        .returnValue("new some.test.TestObject<>(stringFieldName, intFieldName)")
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(constantMap, element);
    }

    @Test
    void createCreationMethods_whenNoConstructorDefined_shouldReturnEmptyList() {
        final var element = TestFixtures.<FixtureConstructor>createTypeElementFixture();

        final Collection<CreationMethod> result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(0);
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(element);
    }

    @Test
    void createCreationMethods_whenCalledWithParameterThatDoesNotMatchConstant_shouldResolveDefaultValueString() {
        final var element = createTypeElementFixture(
                createFixtureConstructorFixture("methodName", "nonFieldValue"));

        final var constantMap = mock(ConstantMap.class);
        when(constantMap.getMatchingConstants(anyCollection())).thenReturn(Map.of("nonFieldValue", Optional.empty()));
        when(valueProviderService.resolveValuesForDefaultPlaceholders(any())).thenReturn("resolvedValueString");

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("some.test.TestObject")
                        .returnValue("new some.test.TestObject(resolvedValueString)")
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("nonFieldValue"));
        verify(valueProviderService, times(1)).resolveValuesForDefaultPlaceholders("nonFieldValue");
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(constantMap, element);
    }
}
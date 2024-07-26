package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.CreationMethod;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.ConstantMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static de.floydkretschmar.fixturize.TestFixtures.createConstantDefinitionMapMock;
import static de.floydkretschmar.fixturize.TestFixtures.createFixtureConstructorFixture;
import static de.floydkretschmar.fixturize.TestFixtures.createTypeElementFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ConstructorCreationMethodStrategyTest {
    private ConstantMap constantMap;
    private ConstructorCreationMethodStrategy strategy;

    @BeforeEach
    void setup() {
        constantMap = createConstantDefinitionMapMock();
        strategy = new ConstructorCreationMethodStrategy();
    }

    @Test
    void createCreationMethods_whenMultipleConstructorsDefined_shouldCreateCreationMethodsForDefinedConstructors() {
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureConstructorFixture("methodName", "stringField", "intField"),
                createFixtureConstructorFixture("methodName2", "uuidField"));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(2);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(stringFieldName, intFieldName)")
                        .name("methodName")
                        .build(),
                CreationMethod.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(uuidFieldName)")
                        .name("methodName2")
                        .build());

        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(constantMap, times(1)).getMatchingConstants(List.of("uuidField"));
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(constantMap, element);
    }


    @Test
    void createCreationMethods_whenSingleConstructorDefined_shouldCreateCreationMethodForDefinedConstructor() {
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureConstructorFixture("methodName", "stringField", "intField"));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(stringFieldName, intFieldName)")
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(constantMap, element);
    }


    @Test
    void createCreationMethods_whenGenericDefined_shouldCreateCreationMethodForDefinedConstructor() {
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureConstructorFixture("methodName", "stringField", "intField"));

        final var result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixtureBuilder("TestObject", "<String>").build());

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                CreationMethod.builder()
                        .returnType("TestObject<String>")
                        .returnValue("new TestObject<>(stringFieldName, intFieldName)")
                        .name("methodName")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(constantMap, element);
    }

    @Test
    void createCreationMethods_whenNoConstructorDefined_shouldReturnEmptyList() {
        final var element = TestFixtures.<FixtureConstructor>createTypeElementFixture("TestObject");

        final Collection<CreationMethod> result = strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject"));

        assertThat(result).hasSize(0);
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(element);
        verifyNoInteractions(constantMap);
    }

    @Test
    void createCreationMethods_whenCalledWithParameterThatDoesNotMatchConstant_shouldThrowFixtureCreationException() {
        final var element = createTypeElementFixture(
                "TestObject",
                createFixtureConstructorFixture("methodName", "stringField"));
        final var constantMap = mock(ConstantMap.class);
        when(constantMap.getMatchingConstants(anyCollection())).thenThrow(new FixtureCreationException("error"));

        assertThrows(FixtureCreationException.class, () -> strategy.generateCreationMethods(element, constantMap, TestFixtures.createMetadataFixture("TestObject")));

        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField"));
        verify(element, times(1)).getAnnotationsByType(FixtureConstructor.class);
        verifyNoMoreInteractions(constantMap, element);
    }
}
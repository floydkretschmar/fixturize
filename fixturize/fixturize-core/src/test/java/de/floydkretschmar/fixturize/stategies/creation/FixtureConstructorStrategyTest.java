package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethodDefinition;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FixtureConstructorStrategyTest {

    @Test
    void createCreationMethods_whenMultipleConstructorsDefined_shouldCreateCreationMethodsForDefinedConstructors() {
        final var strategy = new FixtureConstructorStrategy(new UpperCamelCaseAndNamingStrategy());
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField"),
                List.of("uuidField")));
        final var constantMap = mockConstantMap();

        final var result = strategy.generateCreationMethods(element, constantMap);

        assertThat(result).hasSize(2);
        assertThat(result.stream()).contains(
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(stringFieldName, intFieldName)")
                        .name("createTestObjectFixtureWithStringFieldAndIntField")
                        .build(),
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(uuidFieldName)")
                        .name("createTestObjectFixtureWithUuidField")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(constantMap, times(1)).getMatchingConstants(List.of("uuidField"));
    }

    @Test
    void createCreationMethods_whenSingleConstructorDefined_shouldCreateCreationMethodForDefinedConstructor() {
        final var strategy = new FixtureConstructorStrategy(new UpperCamelCaseAndNamingStrategy());
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField")));
        final var constantMap = mockConstantMap();

        final var result = strategy.generateCreationMethods(element, constantMap);

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(stringFieldName, intFieldName)")
                        .name("createTestObjectFixtureWithStringFieldAndIntField")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
    }

    @Test
    void createCreationMethods_whenNoConstructorDefined_shouldReturnEmptyList() {
        final var strategy = new FixtureConstructorStrategy(new UpperCamelCaseAndNamingStrategy());
        final var element = mockTypeElement(Stream.of());

        final var constantMap = mockConstantMap();
        final Collection<FixtureCreationMethodDefinition> result = strategy.generateCreationMethods(element, constantMap);

        assertThat(result).hasSize(0);
        verifyNoInteractions(constantMap);
    }

    @Test
    void createCreationMethods_whenCalledWithParameterThatDoesNotMatchConstant_shouldThrowFixtureCreationException() {
        final var strategy = new FixtureConstructorStrategy(new UpperCamelCaseAndNamingStrategy());
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField", "booleanField", "uuidField")));

        final var constantMap = mock(ConstantDefinitionMap.class);
        when(constantMap.getMatchingConstants(anyCollection())).thenThrow(new FixtureCreationException("error"));

        assertThrows(FixtureCreationException.class, () -> strategy.generateCreationMethods(element, constantMap));
    }

    private static ConstantDefinitionMap mockConstantMap() {
        final var constantMap = mock(ConstantDefinitionMap.class);
        when(constantMap.getMatchingConstants(anyCollection())).thenAnswer(call -> {
            final var argument = call.<Collection<String>>getArgument(0);
            return argument.stream().map(name -> FixtureConstantDefinition.builder()
                    .originalFieldName(name)
                    .name("%sName".formatted(name))
                    .type("%sType".formatted(name))
                    .value("%sValue".formatted(name))
                    .build()).toList();
        });
        return constantMap;
    }

    private static TypeElement mockTypeElement(Stream<List<String>> definedFixtureConstructors) {
        final var element = mock(TypeElement.class);
        final var constructors = definedFixtureConstructors.map(parameterNames -> {
            final var fixtureConstructor = mock(FixtureConstructor.class);
            when(fixtureConstructor.constructorParameters()).thenReturn(parameterNames.toArray(String[]::new));
            return fixtureConstructor;
        }).toArray(FixtureConstructor[]::new);

        when(element.getAnnotationsByType(ArgumentMatchers.argThat(param -> param.equals(FixtureConstructor.class)))).thenReturn(constructors);

        final var name = mock(Name.class);
        when(name.toString()).thenReturn("TestObject");
        when(element.getSimpleName()).thenReturn(name);
        return element;
    }
}
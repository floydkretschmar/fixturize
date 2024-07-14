package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilders;
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

import static de.floydkretschmar.fixturize.FormattingUtils.WHITESPACE_16;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FixtureBuilderStrategyTest {

    @Test
    void createCreationMethods_whenMultipleBuildersDefined_shouldCreateCreationMethodsForDefinedBuilders() {
        final var strategy = new FixtureBuilderStrategy(new UpperCamelCaseAndNamingStrategy());
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField"),
                List.of("uuidField")));
        final var constantMap = mockConstantMap();

        final var result = strategy.generateCreationMethods(element, constantMap);

        assertThat(result).hasSize(2);
        assertThat(result.stream()).contains(
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.stringField(stringFieldName)\n%s.intField(intFieldName)"
                                .formatted(WHITESPACE_16, WHITESPACE_16))
                        .name("createTestObjectBuilderFixtureWithStringFieldAndIntField")
                        .build(),
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.uuidField(uuidFieldName)"
                                .formatted(WHITESPACE_16))
                        .name("createTestObjectBuilderFixtureWithUuidField")
                        .build());

        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
        verify(constantMap, times(1)).getMatchingConstants(List.of("uuidField"));
    }

    @Test
    void createCreationMethods_whenSingleBuilderDefined_shouldCreateCreationMethodForDefinedBuilder() {
        final var strategy = new FixtureBuilderStrategy(new UpperCamelCaseAndNamingStrategy());
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField")));
        final var constantMap = mockConstantMap();

        final var result = strategy.generateCreationMethods(element, constantMap);

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.stringField(stringFieldName)\n%s.intField(intFieldName)"
                                .formatted(WHITESPACE_16, WHITESPACE_16))
                        .name("createTestObjectBuilderFixtureWithStringFieldAndIntField")
                        .build());
        verify(constantMap, times(1)).getMatchingConstants(List.of("stringField", "intField"));
    }

    @Test
    void createCreationMethods_whenNoBuilderDefined_shouldReturnEmptyList() {
        final var strategy = new FixtureBuilderStrategy(new UpperCamelCaseAndNamingStrategy());
        final var element = mockTypeElement(Stream.of());

        final var constantMap = mockConstantMap();
        final Collection<FixtureCreationMethodDefinition> result = strategy.generateCreationMethods(element, constantMap);

        assertThat(result).hasSize(0);
        verifyNoInteractions(constantMap);
    }

    @Test
    void createCreationMethods_whenCalledWithParameterThatDoesNotMatchConstant_shouldThrowFixtureCreationException() {
        final var strategy = new FixtureBuilderStrategy(new UpperCamelCaseAndNamingStrategy());
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

    private static TypeElement mockTypeElement(Stream<List<String>> definedFixtureBuilders) {
        final var element = mock(TypeElement.class);
        final var builders = definedFixtureBuilders.map(parameterNames -> {
            final var FixtureBuilder = mock(FixtureBuilder.class);
            when(FixtureBuilder.correspondingFields()).thenReturn(parameterNames.toArray(String[]::new));
            when(FixtureBuilder.buildMethod()).thenReturn("builder");
            return FixtureBuilder;
        }).toArray(FixtureBuilder[]::new);

        if (builders.length > 1) {
            final var FixtureBuilders = mock(FixtureBuilders.class);
            when(FixtureBuilders.value()).thenReturn(builders);
            when(element.getAnnotation(ArgumentMatchers.argThat(param -> param.equals(FixtureBuilders.class)))).thenReturn(FixtureBuilders);
        }
        else if (builders.length == 1) {
            when(element.getAnnotation(ArgumentMatchers.argThat(param -> param.equals(FixtureBuilder.class)))).thenReturn(builders[0]);
        }

        final var name = mock(Name.class);
        when(name.toString()).thenReturn("TestObject");
        when(element.getSimpleName()).thenReturn(name);
        return element;
    }

}
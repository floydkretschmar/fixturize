package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilders;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethodDefinition;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinitionMap;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static de.floydkretschmar.fixturize.FormattingUtils.WHITESPACE_16;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FixtureBuilderStrategyTest {

    public static final FixtureConstantDefinitionMap FIELD_MAP = new FixtureConstantDefinitionMap(Map.of(
            "stringField", FixtureConstantDefinition.builder().originalFieldName("stringField").name("STRING_FIELD").type("String").value("\"STRING_FIELD_VALUE\"").build(),
            "intField", FixtureConstantDefinition.builder().originalFieldName("intField").name("INT_FIELD").type("int").value("0").build(),
            "booleanField", FixtureConstantDefinition.builder().originalFieldName("booleanField").name("BOOLEAN_FIELD").type("boolean").value("false").build(),
            "CUSTOM_FIELD_NAME", FixtureConstantDefinition.builder().originalFieldName("originalFieldName").name("CUSTOM_FIELD_NAME").type("boolean").value("true").build(),
            "uuidField", FixtureConstantDefinition.builder().originalFieldName("uuidField").name("UUID_FIELD").type("UUID").value("UUID.randomUUID()").build()
    ));

    @Test
    void createCreationMethods_whenMultipleBuildersDefined_shouldCreateCreationMethodsForDefinedBuilders() {
        final var strategy = new FixtureBuilderStrategy();
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField", "booleanField", "uuidField"),
                List.of("stringField", "booleanField", "uuidField")));

        final var result = strategy.generateCreationMethods(element, FIELD_MAP);

        assertThat(result).hasSize(2);
        assertThat(result.stream()).contains(
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.stringField(STRING_FIELD)\n%s.intField(INT_FIELD)\n%s.booleanField(BOOLEAN_FIELD)\n%s.uuidField(UUID_FIELD)"
                                .formatted(WHITESPACE_16, WHITESPACE_16, WHITESPACE_16, WHITESPACE_16))
                        .name("createTestObjectFixtureBuilderWithStringFieldAndIntFieldAndBooleanFieldAndUuidField")
                        .build(),
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.stringField(STRING_FIELD)\n%s.booleanField(BOOLEAN_FIELD)\n%s.uuidField(UUID_FIELD)"
                                .formatted(WHITESPACE_16, WHITESPACE_16, WHITESPACE_16))
                        .name("createTestObjectFixtureBuilderWithStringFieldAndBooleanFieldAndUuidField")
                        .build());
    }

    @Test
    void createCreationMethods_whenSingleBuilderDefined_shouldCreateCreationMethodForDefinedBuilder() {
        final var strategy = new FixtureBuilderStrategy();
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField", "booleanField", "uuidField")));

        final var result = strategy.generateCreationMethods(element, FIELD_MAP);

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.stringField(STRING_FIELD)\n%s.intField(INT_FIELD)\n%s.booleanField(BOOLEAN_FIELD)\n%s.uuidField(UUID_FIELD)"
                                .formatted(WHITESPACE_16, WHITESPACE_16, WHITESPACE_16, WHITESPACE_16))
                        .name("createTestObjectFixtureBuilderWithStringFieldAndIntFieldAndBooleanFieldAndUuidField")
                        .build());
    }

    @Test
    void createCreationMethods_whenOriginalFieldNameDifferentFromConstantName_shouldCreateCreationMethodForDefinedBuilder() {
        final var strategy = new FixtureBuilderStrategy();
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField", "CUSTOM_FIELD_NAME", "uuidField")));

        final var result = strategy.generateCreationMethods(element, FIELD_MAP);

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject.TestObjectBuilder")
                        .returnValue("TestObject.builder()\n%s.stringField(STRING_FIELD)\n%s.intField(INT_FIELD)\n%s.originalFieldName(CUSTOM_FIELD_NAME)\n%s.uuidField(UUID_FIELD)"
                                .formatted(WHITESPACE_16, WHITESPACE_16, WHITESPACE_16, WHITESPACE_16))
                        .name("createTestObjectFixtureBuilderWithStringFieldAndIntFieldAndOriginalFieldNameAndUuidField")
                        .build());
    }

    @Test
    void createCreationMethods_whenNoBuilderDefined_shouldReturnEmptyList() {
        final var strategy = new FixtureBuilderStrategy();
        final var element = mockTypeElement(Stream.of());

        final Collection<FixtureCreationMethodDefinition> result = strategy.generateCreationMethods(element, FIELD_MAP);

        assertThat(result).hasSize(0);
    }

    @Test
    void createCreationMethods_whenCalledWithParameterThatDoesNotMatchConstant_shouldThrowFixtureCreationException() {
        final var strategy = new FixtureBuilderStrategy();
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField", "booleanField", "uuidField")));

        final var fixtureConstantMap = new FixtureConstantDefinitionMap(Map.of());

        assertThrows(FixtureCreationException.class, () -> strategy.generateCreationMethods(element, fixtureConstantMap));
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
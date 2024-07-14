package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.annotations.FixtureConstructors;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethodDefinition;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FixtureConstructorStrategyTest {
    @Test
    void createCreationMethods_whenMultipleConstructorsDefined_shouldCreateCreationMethodsForDefinedConstructors() {
        var strategy = new FixtureConstructorStrategy();
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField", "booleanField", "uuidField"),
                List.of("stringField", "booleanField", "uuidField")));

        final Map<String, FixtureConstantDefinition> fixtureConstantMap = Map.of(
                "stringField", FixtureConstantDefinition.builder().name("STRING_FIELD").type("String").value("\"STRING_FIELD_VALUE\"").build(),
                "intField", FixtureConstantDefinition.builder().name("INT_FIELD").type("int").value("0").build(),
                "booleanField", FixtureConstantDefinition.builder().name("BOOLEAN_FIELD").type("boolean").value("false").build(),
                "uuidField", FixtureConstantDefinition.builder().name("UUID_FIELD").type("UUID").value("UUID.randomUUID()").build()
        );
        final Collection<FixtureCreationMethodDefinition> result = strategy.generateCreationMethods(element, fixtureConstantMap);

        assertThat(result).hasSize(2);
        assertThat(result.stream()).contains(
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(STRING_FIELD, INT_FIELD, BOOLEAN_FIELD, UUID_FIELD)")
                        .name("createTestObjectFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField")
                        .build(),
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(STRING_FIELD, BOOLEAN_FIELD, UUID_FIELD)")
                        .name("createTestObjectFixtureWithStringFieldAndBooleanFieldAndUuidField")
                        .build());
    }

    @Test
    void createCreationMethods_whenSingleConstructorDefined_shouldCreateCreationMethodForDefinedConstructor() {
        var strategy = new FixtureConstructorStrategy();
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField", "booleanField", "uuidField")));

        final Map<String, FixtureConstantDefinition> fixtureConstantMap = Map.of(
                "stringField", FixtureConstantDefinition.builder().name("STRING_FIELD").type("String").value("\"STRING_FIELD_VALUE\"").build(),
                "intField", FixtureConstantDefinition.builder().name("INT_FIELD").type("int").value("0").build(),
                "booleanField", FixtureConstantDefinition.builder().name("BOOLEAN_FIELD").type("boolean").value("false").build(),
                "uuidField", FixtureConstantDefinition.builder().name("UUID_FIELD").type("UUID").value("UUID.randomUUID()").build()
        );
        final Collection<FixtureCreationMethodDefinition> result = strategy.generateCreationMethods(element, fixtureConstantMap);

        assertThat(result).hasSize(1);
        assertThat(result.stream()).contains(
                FixtureCreationMethodDefinition.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(STRING_FIELD, INT_FIELD, BOOLEAN_FIELD, UUID_FIELD)")
                        .name("createTestObjectFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField")
                        .build());
    }

    @Test
    void createCreationMethods_whenNoConstructorDefined_shouldReturnEmptyList() {
        var strategy = new FixtureConstructorStrategy();
        final var element = mockTypeElement(Stream.of());

        final Map<String, FixtureConstantDefinition> fixtureConstantMap = Map.of(
                "stringField", FixtureConstantDefinition.builder().name("STRING_FIELD").type("String").value("\"STRING_FIELD_VALUE\"").build(),
                "intField", FixtureConstantDefinition.builder().name("INT_FIELD").type("int").value("0").build(),
                "booleanField", FixtureConstantDefinition.builder().name("BOOLEAN_FIELD").type("boolean").value("false").build(),
                "uuidField", FixtureConstantDefinition.builder().name("UUID_FIELD").type("UUID").value("UUID.randomUUID()").build()
        );
        final Collection<FixtureCreationMethodDefinition> result = strategy.generateCreationMethods(element, fixtureConstantMap);

        assertThat(result).hasSize(0);
    }

    @Test
    void createCreationMethods_whenCalledWithParameterThatDoesNotMatchConstant_shouldThrowFixtureCreationException() {
        var strategy = new FixtureConstructorStrategy();
        final var element = mockTypeElement(Stream.of(
                List.of("stringField", "intField", "booleanField", "uuidField")));

        final Map<String, FixtureConstantDefinition> fixtureConstantMap = Map.of();

        assertThrows(FixtureCreationException.class, () -> strategy.generateCreationMethods(element, fixtureConstantMap));
    }

    private static TypeElement mockTypeElement(Stream<List<String>> definedFixtureConstructors) {
        final var element = mock(TypeElement.class);
        final var constructors = definedFixtureConstructors.map(parameterNames -> {
            final var fixtureConstructor = mock(FixtureConstructor.class);
            when(fixtureConstructor.correspondingFieldNames()).thenReturn(parameterNames.toArray(String[]::new));
            return fixtureConstructor;
        }).toArray(FixtureConstructor[]::new);

        if (constructors.length > 1) {
            final var fixtureConstructors = mock(FixtureConstructors.class);
            when(fixtureConstructors.value()).thenReturn(constructors);
            when(element.getAnnotation(ArgumentMatchers.argThat(param -> param.equals(FixtureConstructors.class)))).thenReturn(fixtureConstructors);
        }
        else if (constructors.length == 1) {
            when(element.getAnnotation(ArgumentMatchers.argThat(param -> param.equals(FixtureConstructor.class)))).thenReturn(constructors[0]);
        }

        final var name = mock(Name.class);
        when(name.toString()).thenReturn("TestObject");
        when(element.getSimpleName()).thenReturn(name);
        return element;
    }
}
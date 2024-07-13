package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.annotations.FixtureConstructors;
import de.floydkretschmar.fixturize.domain.FixtureConstant;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethod;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import org.junit.jupiter.api.Test;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FixtureConstructorCreationMethodGenerationStrategyTest {
    @Test
    void createCreationMethods_whenCalled_shouldCreateCreationMethodsForDefinedConstructors() {
        var strategy = new FixtureConstructorCreationMethodGenerationStrategy();
        final var element = mockTypeElement();

        final Map<String, FixtureConstant> fixtureConstantMap = Map.of(
                "stringField", FixtureConstant.builder().name("STRING_FIELD").type("String").value("\"STRING_FIELD_VALUE\"").build(),
                "intField", FixtureConstant.builder().name("INT_FIELD").type("int").value("0").build(),
                "booleanField", FixtureConstant.builder().name("BOOLEAN_FIELD").type("boolean").value("false").build(),
                "uuidField", FixtureConstant.builder().name("UUID_FIELD").type("UUID").value("UUID.randomUUID()").build()
        );
        final Collection<FixtureCreationMethod> result = strategy.generateCreationMethods(element, fixtureConstantMap);

        assertThat(result).hasSize(2);
        assertThat(result.stream()).contains(
                FixtureCreationMethod.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(STRING_FIELD,INT_FIELD,BOOLEAN_FIELD,UUID_FIELD)")
                        .name("createTestObjectFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField")
                        .build(),
                FixtureCreationMethod.builder()
                        .returnType("TestObject")
                        .returnValue("new TestObject(STRING_FIELD,BOOLEAN_FIELD,UUID_FIELD)")
                        .name("createTestObjectFixtureWithStringFieldAndBooleanFieldAndUuidField")
                        .build());
    }

    @Test
    void createCreationMethods_whenCalledWithParameterThatDoesNotMatchConstant_shouldThrowFixtureCreationException() {
        var strategy = new FixtureConstructorCreationMethodGenerationStrategy();
        final var element = mockTypeElement();

        final Map<String, FixtureConstant> fixtureConstantMap = Map.of();

        assertThrows(FixtureCreationException.class, () -> strategy.generateCreationMethods(element, fixtureConstantMap));
    }

    private static TypeElement mockTypeElement() {
        final var element = mock(TypeElement.class);
        final var fixtureConstructors = mock(FixtureConstructors.class);
        final var fixtureConstructor = mock(FixtureConstructor.class);
        final var fixtureConstructor2 = mock(FixtureConstructor.class);
        when(element.getAnnotation(any())).thenReturn(fixtureConstructors);
        when(fixtureConstructors.value()).thenReturn(new FixtureConstructor[]{fixtureConstructor, fixtureConstructor2});
        when(fixtureConstructor.parameterNames()).thenReturn(new String[]{"stringField", "intField", "booleanField", "uuidField"});
        when(fixtureConstructor2.parameterNames()).thenReturn(new String[]{"stringField", "booleanField", "uuidField"});
        final var name = mock(Name.class);
        when(name.toString()).thenReturn("TestObject");
        when(element.getSimpleName()).thenReturn(name);
        return element;
    }
}
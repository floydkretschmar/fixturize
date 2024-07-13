package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.annotations.FixtureConstructors;
import org.junit.jupiter.api.Test;
import de.floydkretschmar.fixturize.stategies.constants.CamelCaseToScreamingSnakeCaseNamingStrategy;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FixtureConstructorCreationMethodGenerationStrategyTest {
    @Test
    void createCreationMethods_whenCalled_shouldCreateCreationMethodsForDefinedConstructors() {
        var strategy = new FixtureConstructorCreationMethodGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy());

        final var element = mock(Element.class);
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
        final Collection<String> result = strategy.generateCreationMethods(element);

        assertThat(result).hasSize(2);
        assertThat(result).contains("""
                \tpublic TestObject createTestObjectFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField() {
                \t\treturn new TestObject(STRING_FIELD,INT_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                \t}""",
                """
                \tpublic TestObject createTestObjectFixtureWithStringFieldAndBooleanFieldAndUuidField() {
                \t\treturn new TestObject(STRING_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                \t}""");
    }
}
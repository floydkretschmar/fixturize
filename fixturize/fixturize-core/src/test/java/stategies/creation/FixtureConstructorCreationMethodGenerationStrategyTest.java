package stategies.creation;

import mocks.TestObject;
import org.junit.jupiter.api.Test;
import stategies.constants.CamelCaseToScreamingSnakeCaseNamingStrategy;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class FixtureConstructorCreationMethodGenerationStrategyTest {
    @Test
    void createCreationMethods_whenCalled_shouldCreateCreationMethodsForDefinedConstructors() {
        var strategy = new FixtureConstructorCreationMethodGenerationStrategy(new CamelCaseToScreamingSnakeCaseNamingStrategy());

        final Collection<String> result = strategy.generateCreationMethods(TestObject.class);

        assertThat(result).hasSize(1);
        assertThat(result).contains("""
                \tpublic TestObject createTestObjectFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidField() {
                \t\treturn new TestObject(STRING_FIELD,INT_FIELD,BOOLEAN_FIELD,UUID_FIELD);
                \t}""");
    }
}
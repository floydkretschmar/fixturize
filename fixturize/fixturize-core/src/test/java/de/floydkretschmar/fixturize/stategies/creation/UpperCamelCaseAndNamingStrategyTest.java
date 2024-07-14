package de.floydkretschmar.fixturize.stategies.creation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static de.floydkretschmar.fixturize.TestConstants.CUSTOM_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestConstants.INT_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestConstants.STRING_FIELD_DEFINITION;
import static org.assertj.core.api.Assertions.assertThat;

class UpperCamelCaseAndNamingStrategyTest {
    @Test
    void createMethodName_whenCalled_shouldCreateMethodName() {
        final var strategy = new UpperCamelCaseAndNamingStrategy();
        final var constants = List.of(STRING_FIELD_DEFINITION, INT_FIELD_DEFINITION, CUSTOM_FIELD_DEFINITION);

        final var result = strategy.createMethodName("TestObject", constants);

        assertThat(result).isEqualTo("createTestObjectFixtureWithStringFieldAndIntFieldAndOriginalFieldName");
    }
}
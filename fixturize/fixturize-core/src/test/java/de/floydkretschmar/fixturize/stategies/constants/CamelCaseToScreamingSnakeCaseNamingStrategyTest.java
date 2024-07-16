package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CamelCaseToScreamingSnakeCaseNamingStrategyTest {
    @Test
    void createConstantName_whenCalledWithCamelCase_shouldReturnSnakeCase() {
        final var strategy = new CamelCaseToScreamingSnakeCaseNamingStrategy();

        final var result = strategy.createConstantName("camelCaseAttribute");

        assertEquals("CAMEL_CASE_ATTRIBUTE", result);
    }

    @ParameterizedTest
    @CsvSource({"snake_case_attribute", "PascalCase", "wrONgCAmelcase"})
    void createConstantName_whenCalledWithNonCamelCaseAttribute_shouldThrowException(String invalidFieldName) {
        final var strategy = new CamelCaseToScreamingSnakeCaseNamingStrategy();

        assertThrows(FixtureCreationException.class, () -> strategy.createConstantName(invalidFieldName));
    }
}
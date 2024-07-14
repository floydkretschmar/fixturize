package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CamelCaseToScreamingSnakeCaseNamingStrategyTest {
    @Test
    void rename_whenCalledWithCamelCase_shouldReturnSnakeCase() {
        final var strategy = new CamelCaseToScreamingSnakeCaseNamingStrategy();

        final var result = strategy.rename("camelCaseAttribute");

        assertEquals("CAMEL_CASE_ATTRIBUTE", result);
    }

    @ParameterizedTest
    @CsvSource({"snake_case_attribute", "PascalCase", "wrONgCAmelcase"})
    void rename_whenCalledWithNonCamelCaseAttribute_shouldThrowException(String invalidFieldName) {
        final var strategy = new CamelCaseToScreamingSnakeCaseNamingStrategy();

        assertThrows(FixtureCreationException.class, () -> strategy.rename(invalidFieldName));
    }
}
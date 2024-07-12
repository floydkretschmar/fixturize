package stategies.constants;

import exceptions.FixtureCreationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CamelCaseToScreamingSnakeCaseNamingStrategyTest {
    @Test
    void rename_whenCalledWithCamelCase_shouldReturnSnakeCase() {
        var strategy = new CamelCaseToScreamingSnakeCaseNamingStrategy();

        var result = strategy.rename("camelCaseAttribute");

        assertEquals("CAMEL_CASE_ATTRIBUTE", result);
    }

    @ParameterizedTest
    @CsvSource({"snake_case_attribute", "PascalCase", "wrONgCAmelcase"})
    void rename_whenCalledWithNonCamelCaseAttribute_shouldThrowException(String invalidFieldName) {
        var strategy = new CamelCaseToScreamingSnakeCaseNamingStrategy();

        assertThrows(FixtureCreationException.class, () -> strategy.rename(invalidFieldName));
    }
}
package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.floydkretschmar.fixturize.TestConstants.BOOLEAN_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestConstants.CUSTOM_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestConstants.INT_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestConstants.STRING_FIELD_DEFINITION;
import static de.floydkretschmar.fixturize.TestConstants.UUID_FIELD_DEFINITION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixtureConstantDefinitionMapTest {

    private static final FixtureConstantDefinitionMap CONSTANTS_MAP = new FixtureConstantDefinitionMap(Map.of(
            "stringField", STRING_FIELD_DEFINITION,
            "intField", INT_FIELD_DEFINITION,
            "booleanField", BOOLEAN_FIELD_DEFINITION,
            "CUSTOM_FIELD_NAME", CUSTOM_FIELD_DEFINITION,
            "uuidField", UUID_FIELD_DEFINITION
    ));

    @Test
    void getMatchingConstants_whenCalled_returnAllConstantsThatMatch() {
        final var result = CONSTANTS_MAP.getMatchingConstants(List.of("stringField", "intField", "CUSTOM_FIELD_NAME", "uuidField"));

        assertThat(result).hasSize(4);
        assertThat(result).contains(STRING_FIELD_DEFINITION, INT_FIELD_DEFINITION, CUSTOM_FIELD_DEFINITION, UUID_FIELD_DEFINITION);
    }

    @Test
    void getMatchingConstants_whenCalledWithStringThatDoesNotHaveCorrespondingConstant_throwFixtureCreationException() {
        assertThrows(FixtureCreationException.class, () -> CONSTANTS_MAP.getMatchingConstants(List.of("doesNotExist")));
    }
}
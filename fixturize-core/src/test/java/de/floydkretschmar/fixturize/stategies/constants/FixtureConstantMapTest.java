package de.floydkretschmar.fixturize.stategies.constants;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.floydkretschmar.fixturize.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class FixtureConstantMapTest {

    private static final FixtureConstantMap CONSTANTS_MAP = new FixtureConstantMap(Map.of(
            "stringField", STRING_FIELD_DEFINITION,
            "intField", INT_FIELD_DEFINITION,
            "booleanField", BOOLEAN_FIELD_DEFINITION,
            "uuidField", UUID_FIELD_DEFINITION
    ));

    @Test
    void getMatchingConstants_whenCalled_returnAllConstantsThatMatch() {
        final var result = CONSTANTS_MAP.getMatchingConstants(List.of("stringField", "intField", "uuidField", "doesNotExists"));

        assertThat(result).hasSize(4);
        assertThat(result).containsAllEntriesOf(Map.of(
                "stringField", Optional.of(STRING_FIELD_DEFINITION),
                "intField", Optional.of(INT_FIELD_DEFINITION),
                "uuidField", Optional.of(UUID_FIELD_DEFINITION),
                "doesNotExists", Optional.empty()
        ));
    }
}
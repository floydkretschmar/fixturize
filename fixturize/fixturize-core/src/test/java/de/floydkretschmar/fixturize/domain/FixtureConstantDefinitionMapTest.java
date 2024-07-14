package de.floydkretschmar.fixturize.domain;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixtureConstantDefinitionMapTest {

    public static final FixtureConstantDefinition STRING_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("stringField").name("STRING_FIELD").type("String").value("\"STRING_FIELD_VALUE\"").build();
    public static final FixtureConstantDefinition INT_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("intField").name("INT_FIELD").type("int").value("0").build();
    public static final FixtureConstantDefinition BOOLEAN_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("booleanField").name("BOOLEAN_FIELD").type("boolean").value("false").build();
    public static final FixtureConstantDefinition CUSTOM_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("originalFieldName").name("CUSTOM_FIELD_NAME").type("boolean").value("true").build();
    public static final FixtureConstantDefinition UUID_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("uuidField").name("UUID_FIELD").type("UUID").value("UUID.randomUUID()").build();
    public static final FixtureConstantDefinitionMap CONSTANTS_MAP = new FixtureConstantDefinitionMap(Map.of(
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
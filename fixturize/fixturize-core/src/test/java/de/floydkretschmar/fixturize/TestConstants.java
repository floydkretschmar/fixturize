package de.floydkretschmar.fixturize;

import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;

public class TestConstants {
    public static final String RANDOM_UUID = "6b21f215-bf9e-445a-9dd2-5808a3a98d52";
    public static final FixtureConstantDefinition STRING_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("stringField").name("STRING_FIELD").type("String").value("\"STRING_FIELD_VALUE\"").build();
    public static final FixtureConstantDefinition INT_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("intField").name("INT_FIELD").type("int").value("0").build();
    public static final FixtureConstantDefinition BOOLEAN_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("booleanField").name("BOOLEAN_FIELD").type("boolean").value("false").build();
    public static final FixtureConstantDefinition CUSTOM_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("originalFieldName").name("CUSTOM_FIELD_NAME").type("boolean").value("true").build();
    public static final FixtureConstantDefinition UUID_FIELD_DEFINITION = FixtureConstantDefinition.builder().originalFieldName("uuidField").name("UUID_FIELD").type("UUID").value("UUID.randomUUID()").build();

}

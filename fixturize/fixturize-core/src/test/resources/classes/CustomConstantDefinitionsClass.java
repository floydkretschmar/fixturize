package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import java.util.UUID;

@Fixture
@FixtureConstructor(correspondingFields = {"CUSTOM_STRING_FIELD_NAME", "intField", "CUSTOM_BOOLEAN_FIELD_NAME", "uuidField"})
@FixtureConstructor(correspondingFields = {"CUSTOM_STRING_FIELD_NAME", "CUSTOM_BOOLEAN_FIELD_NAME_2", "uuidField"})
public class CustomConstantDefinitionsClass {
    @FixtureConstant(name = "CUSTOM_STRING_FIELD_NAME", value = "\"CUSTOM_CONSTANT_VALUE\"")
    private final String stringField;
    private final int intField;
    @FixtureConstant(name = "CUSTOM_BOOLEAN_FIELD_NAME")
    @FixtureConstant(name = "CUSTOM_BOOLEAN_FIELD_NAME_2", value = "true")
    private final boolean booleanField;
    private final UUID uuidField;

    public CustomConstantDefinitionsClass() {
        this("", 0, false, null);
    }

    public CustomConstantDefinitionsClass(String stringField, int intField, boolean booleanField, UUID uuidField) {
        this.stringField = stringField;
        this.intField = intField;
        this.booleanField = booleanField;
        this.uuidField = uuidField;
    }

    public CustomConstantDefinitionsClass(String stringField, boolean booleanField, UUID uuidField) {
        this(stringField, 0, booleanField, uuidField);
    }
}

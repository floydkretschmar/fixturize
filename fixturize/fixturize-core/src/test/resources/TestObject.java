package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import java.util.UUID;

@Fixture
@FixtureConstructor(correspondingFieldNames = {"stringField", "intField", "booleanField", "uuidField"})
@FixtureConstructor(correspondingFieldNames = {"stringField", "booleanField", "uuidField"})
public class TestObject {
    @FixtureConstant(name = "CUSTOM_STRING_FIELD_NAME", value = "\"CUSTOM_CONSTANT_VALUE\"")
    private final String stringField;
    private final int intField;
    @FixtureConstant(name = "CUSTOM_BOOLEAN_FIELD_NAME")
    private final boolean booleanField;
    private final UUID uuidField;

    public TestObject() {
        this("", 0, false, null);
    }

    public TestObject(String stringField, int intField, boolean booleanField, UUID uuidField) {
        this.stringField = stringField;
        this.intField = intField;
        this.booleanField = booleanField;
        this.uuidField = uuidField;
    }

    public TestObject(String stringField, boolean booleanField, UUID uuidField) {
        this(stringField, 0, booleanField, uuidField);
    }
}

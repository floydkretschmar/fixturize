package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import java.util.UUID;

@Fixture
public class TestObject {
    @FixtureConstant(name = "CUSTOM_STRING_FIELD_NAME")
    private final String stringField;
    private final int intField;
    private final boolean booleanField;
    private final UUID uuidField;

    public TestObject() {
        this("", 0, false, null);
    }

    @FixtureConstructor(correspondingFieldNames = {"stringField", "intField", "booleanField", "uuidField"})
    public TestObject(String stringField, int intField, boolean booleanField, UUID uuidField) {
        this.stringField = stringField;
        this.intField = intField;
        this.booleanField = booleanField;
        this.uuidField = uuidField;
    }

    @FixtureConstructor(correspondingFieldNames = {"stringField", "booleanField", "uuidField"})
    public TestObject(String stringField, boolean booleanField, UUID uuidField) {
        this(stringField, 0, booleanField, uuidField);
    }

    public TestObject(String stringField, UUID uuidField) {
        this(stringField, 0, true, uuidField);
    }
}

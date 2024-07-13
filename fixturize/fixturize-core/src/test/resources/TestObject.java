package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import java.util.UUID;

@Fixture
@FixtureConstructor(parameterNames = {"stringField", "intField", "booleanField", "uuidField"})
@FixtureConstructor(parameterNames = {"stringField", "booleanField", "uuidField"})
public class TestObject {
    private final String stringField;
    private final int intField;
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

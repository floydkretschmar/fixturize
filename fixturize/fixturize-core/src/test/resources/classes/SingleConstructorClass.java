package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import java.util.UUID;

@Fixture
@FixtureConstructor(correspondingFields = {"stringField", "intField", "booleanField", "uuidField"})
public class SingleConstructorClass {
    private final String stringField;
    private final int intField;
    private final boolean booleanField;
    private final UUID uuidField;

    public SingleConstructorClass() {
        this("", 0, false, null);
    }

    public SingleConstructorClass(String stringField, int intField, boolean booleanField, UUID uuidField) {
        this.stringField = stringField;
        this.intField = intField;
        this.booleanField = booleanField;
        this.uuidField = uuidField;
    }

    public SingleConstructorClass(String stringField, boolean booleanField, UUID uuidField) {
        this(stringField, 0, booleanField, uuidField);
    }
}

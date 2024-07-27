package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import java.util.UUID;

@Fixture
@FixtureConstructor(methodName = "createMultiConstructorFixture", constructorParameters = {"stringField", "intField", "booleanField", "uuidField"})
@FixtureConstructor(methodName = "createMultiConstructorFixture2", constructorParameters = {"stringField", "booleanField", "uuidField"})
@FixtureConstructor(methodName = "createMultiConstructorFixtureWithDefaultValue", constructorParameters = {"stringField", "#{java.lang.Boolean}", "uuidField"})
public class MultiConstructorClass {
    private final String stringField;
    private final int intField;
    private final boolean booleanField;
    private final UUID uuidField;

    public MultiConstructorClass() {
        this("", 0, false, null);
    }

    public MultiConstructorClass(String stringField, int intField, boolean booleanField, UUID uuidField) {
        this.stringField = stringField;
        this.intField = intField;
        this.booleanField = booleanField;
        this.uuidField = uuidField;
    }

    public MultiConstructorClass(String stringField, boolean booleanField, UUID uuidField) {
        this(stringField, 0, booleanField, uuidField);
    }
}

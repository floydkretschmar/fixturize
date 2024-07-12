package mocks;

import annotations.FixtureConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@FixtureConstructor(parameterNames = {"stringField", "intField", "booleanField", "uuidField"})
@FixtureConstructor(parameterNames = {"stringField", "booleanField", "uuidField"})
public class TestObject {
    private final String stringField;
    private final int intField;
    private final boolean booleanField;
    private final UUID uuidField;

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

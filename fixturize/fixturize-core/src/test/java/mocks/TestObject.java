package mocks;

import annotations.FixtureConstructor;

import java.util.UUID;

public class TestObject {
    private final String stringField;
    private final int intField;
    private final boolean booleanField;
    private final UUID uuidField;

    @FixtureConstructor(parameterNames = {"stringField", "intField", "booleanField", "uuidField"})
    public TestObject(String stringField, int intField, boolean booleanField, UUID uuidField) {
        this.stringField = stringField;
        this.intField = intField;
        this.booleanField = booleanField;
        this.uuidField = uuidField;
    }

    public String getStringField() {
        return stringField;
    }

    public int getIntField() {
        return intField;
    }

    public boolean isBooleanField() {
        return booleanField;
    }

    public UUID getUuidField() {
        return uuidField;
    }
}

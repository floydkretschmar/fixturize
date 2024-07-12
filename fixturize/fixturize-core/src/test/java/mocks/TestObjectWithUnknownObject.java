package mocks;

import annotations.FixtureConstructor;
import lombok.Value;

import java.util.UUID;

@Value
public class TestObjectWithUnknownObject {
    String stringField;
    int intField;
    boolean booleanField;
    UUID uuidField;

    BuilderTestObject unknownObject;
}

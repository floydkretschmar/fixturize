package mocks;

import lombok.Builder;

import java.util.UUID;

@Builder
public class BuilderTestObject {
    final String stringField;
    final int intField;
    final boolean booleanField;
    final UUID uuidField;
}

package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Fixture
@Builder
@Value
public class CrossReferencedLombokClass {
    String stringField;
    int intField;
    boolean booleanField;
    UUID uuidField;
}

package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Fixture
@Builder
@Value
@FixtureBuilder(correspondingFields = {"stringField", "intField", "booleanField", "uuidField"})
public class LombokClass {
    String stringField;
    int intField;
    boolean booleanField;
    UUID uuidField;
}

package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.ElementKind;
import java.util.UUID;

@Fixture
@Builder
@Value
@FixtureBuilder(methodName = "createLombokFixture")
public class LombokClass {
    String stringField;
    int intField;
    @FixtureConstant(name = "BOOLEAN_FIELD_1")
    @FixtureConstant(name = "BOOLEAN_FIELD_2")
    boolean booleanField;
    UUID uuidField;
    ElementKind[] elementKindsField;
}

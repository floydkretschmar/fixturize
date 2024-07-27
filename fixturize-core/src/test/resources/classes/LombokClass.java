package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.ElementKind;
import java.util.UUID;

@Fixture
@Builder
@Value
@FixtureBuilder(methodName = "createLombokFixture")
@FixtureBuilder(methodName = "createLombokFixtureAsBuilder", asBuilder = true)
@FixtureBuilder(methodName = "createLombokFixture2", usedSetters = {
        @FixtureBuilderSetter(setterName = "stringField"),
        @FixtureBuilderSetter(setterName = "intField", value="10 + #{java.lang.Integer}"),
        @FixtureBuilderSetter(setterName = "booleanField", value = "BOOLEAN_FIELD_1")
})
@FixtureConstructor(methodName = "createLombokConstructorFixture")
@AllArgsConstructor
public class LombokClass {
    String stringField;
    int intField;
    @FixtureConstant(name = "BOOLEAN_FIELD_1")
    @FixtureConstant(name = "BOOLEAN_FIELD_2")
    boolean booleanField;
    UUID uuidField;
    ElementKind[] elementKindsField;
}

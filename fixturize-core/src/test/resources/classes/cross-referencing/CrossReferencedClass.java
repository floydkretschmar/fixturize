package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilderSetter;
import lombok.Builder;
import lombok.Value;

@Fixture
@Builder
@Value
@FixtureBuilder(methodName = "createCrossReferencedFixture", usedSetters = {@FixtureBuilderSetter(setterName = "id")})
public class CrossReferencedClass {
    String id;
}


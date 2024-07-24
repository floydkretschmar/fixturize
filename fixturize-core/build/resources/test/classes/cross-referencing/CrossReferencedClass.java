package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import lombok.Builder;
import lombok.Value;

@Fixture
@Builder
@Value
@FixtureBuilder(methodName = "createCrossReferencedFixture")
public class CrossReferencedClass {
    String id;
}


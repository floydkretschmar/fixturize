package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import lombok.Builder;
import lombok.Value;

@Fixture
@Builder
@Value
public class CrossReferencedClass {
    String id;
}


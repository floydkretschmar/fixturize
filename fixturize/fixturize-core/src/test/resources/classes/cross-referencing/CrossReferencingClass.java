package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import lombok.Builder;
import lombok.Value;

@Fixture
@Builder
@Value
public class CrossReferencingClass {
    @FixtureConstant(name = "REFERENCE_ID", value = "de.floydkretschmar.fixturize.mocks.CrossReferencedClassFixture.ID")
    String referenceId;
    CrossReferencedClass referenceInstance;
    SingleConstructorClass constructorReferenceInstance;
    CrossReferencedConstructorClass constructorClassInstance;
    CrossReferencedBuilderClass builderClassInstance;
    CrossReferencedLombokClass lombokClassInstance;
}

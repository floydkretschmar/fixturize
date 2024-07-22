package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@Fixture(genericImplementations = {"java.lang.String"})
public class CrossReferencedGenericClass<T> {
    T genericField;
}

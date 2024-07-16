package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import lombok.Builder;

@Builder
@Fixture
@FixtureValueProvider(targetType = "java.lang.String", valueProviderCallback = "\"\\\"\" + field.getSimpleName().toString() + \"Value\" + \"\\\"\"")
@FixtureValueProvider(targetType = "INT", valueProviderCallback = "10")
public class CustomValueProviderClass {
    private String stringField;
    private int intField;
}

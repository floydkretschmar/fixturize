package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import lombok.Builder;

import java.util.List;

@Builder
@Fixture
@FixtureValueProvider(targetType = "java.lang.String", valueProviderCallback = "\"\\\"\" + field.getSimpleName().toString() + \"Value\" + \"\\\"\"")
@FixtureValueProvider(targetType = "int", valueProviderCallback = "10")
@FixtureValueProvider(targetType = "java.util.List<java.lang.String>", valueProviderCallback = "\"java.util.List.of(\\\"Value 1\\\", ${java.lang.String}, \\\"Value 3\\\")\"")
public class CustomValueProviderClass {
    private String stringField;
    private int intField;
    private List<String> listField;
}

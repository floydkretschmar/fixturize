package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureValueProvider;
import lombok.Builder;

import java.util.List;

@Builder
@Fixture
@FixtureValueProvider(targetType = "java.lang.String", valueProviderCallback = "function(field, names) `\"${field.getSimpleName().toString()}Value\"`")
@FixtureValueProvider(targetType = "int", valueProviderCallback = "function(field, names) 10")
@FixtureValueProvider(targetType = "java.util.List<java.lang.String>", valueProviderCallback = """
function(field, names) {
  return `java.util.List.of(\"Value 1\", #{java.lang.String}, \"Value 3\")`;
}""")
public class CustomValueProviderClass {
    private String stringField;
    private int intField;
    private List<String> listField;
}

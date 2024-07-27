package de.floydkretschmar.fixturize.mocks;

public class GenericClassFixture {
    public static java.lang.String GENERIC_FIELD = "STRING_VALUE";
    public static int FIELD = 0;

    public static de.floydkretschmar.fixturize.mocks.GenericClass<java.lang.String> createGenericFixtureConstructor() {
        return new de.floydkretschmar.fixturize.mocks.GenericClass<>(GENERIC_FIELD, FIELD);
    }

    public static de.floydkretschmar.fixturize.mocks.GenericClass<java.lang.String> createGenericFixtureBuilder() {
        return de.floydkretschmar.fixturize.mocks.GenericClass.<java.lang.String>builder().genericField(GENERIC_FIELD).field(FIELD).build();
    }
}

package de.floydkretschmar.fixturize.mocks;

public class GenericClassFixture {
    public static java.lang.String GENERIC_FIELD = "STRING_VALUE";
    public static int FIELD = 0;

    public static GenericClass<java.lang.String> createGenericFixtureConstructor() {
        return new GenericClass<>(GENERIC_FIELD, FIELD);
    }

    public static GenericClass.GenericClassBuilder<java.lang.String> createGenericFixtureBuilder() {
        return GenericClass.<java.lang.String>builder()
                .genericField(GENERIC_FIELD)
                .field(FIELD);
    }
}

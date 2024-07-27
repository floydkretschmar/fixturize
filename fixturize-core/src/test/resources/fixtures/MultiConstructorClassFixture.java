package de.floydkretschmar.fixturize.mocks;

public class MultiConstructorClassFixture {
    public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
    public static int INT_FIELD = 0;
    public static boolean BOOLEAN_FIELD = false;
    public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52");

    public static MultiConstructorClass createMultiConstructorFixture() {
        return new MultiConstructorClass(STRING_FIELD, INT_FIELD, BOOLEAN_FIELD, UUID_FIELD);
    }

    public static MultiConstructorClass createMultiConstructorFixture2() {
        return new MultiConstructorClass(STRING_FIELD, BOOLEAN_FIELD, UUID_FIELD);
    }

    public static MultiConstructorClass createMultiConstructorFixtureWithDefaultValue() {
        return new MultiConstructorClass(STRING_FIELD, false, UUID_FIELD);
    }
}

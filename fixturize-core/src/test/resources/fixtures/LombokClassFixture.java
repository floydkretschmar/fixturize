package de.floydkretschmar.fixturize.mocks;

public class LombokClassFixture {
    public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
    public static int INT_FIELD = 0;
    public static boolean BOOLEAN_FIELD_1 = false;
    public static boolean BOOLEAN_FIELD_2 = false;
    public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52");
    public static javax.lang.model.element.ElementKind[] ELEMENT_KINDS_FIELD = new javax.lang.model.element.ElementKind[] {};

    public static de.floydkretschmar.fixturize.mocks.LombokClass createLombokConstructorFixture() {
        return new de.floydkretschmar.fixturize.mocks.LombokClass(STRING_FIELD, INT_FIELD, BOOLEAN_FIELD_1, UUID_FIELD, ELEMENT_KINDS_FIELD);
    }

    public static de.floydkretschmar.fixturize.mocks.LombokClass createLombokFixture() {
        return de.floydkretschmar.fixturize.mocks.LombokClass.builder().stringField(STRING_FIELD).intField(INT_FIELD).booleanField(BOOLEAN_FIELD_1).uuidField(UUID_FIELD).elementKindsField(ELEMENT_KINDS_FIELD).build();
    }

    public static de.floydkretschmar.fixturize.mocks.LombokClass createLombokFixture2() {
        return de.floydkretschmar.fixturize.mocks.LombokClass.builder().stringField(STRING_FIELD).intField(10 + 0).booleanField(BOOLEAN_FIELD_1).build();
    }
}

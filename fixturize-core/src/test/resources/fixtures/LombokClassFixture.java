package de.floydkretschmar.fixturize.mocks;

public class LombokClassFixture {
    public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
    public static int INT_FIELD = 0;
    public static boolean BOOLEAN_FIELD_1 = false;
    public static boolean BOOLEAN_FIELD_2 = false;
    public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52");
    public static javax.lang.model.element.ElementKind[] ELEMENT_KINDS_FIELD = new javax.lang.model.element.ElementKind[] {};

    public static LombokClass.LombokClassBuilder createLombokFixture() {
        return LombokClass.builder().stringField(STRING_FIELD).intField(INT_FIELD).booleanField(BOOLEAN_FIELD_1).uuidField(UUID_FIELD).elementKindsField(ELEMENT_KINDS_FIELD);
    }

    public static LombokClass.LombokClassBuilder createLombokFixture2() {
        return LombokClass.builder().stringField(STRING_FIELD).intField(10 + 0).booleanField(BOOLEAN_FIELD_1);
    }
}

package de.floydkretschmar.fixturize.mocks;

public class LombokClassFixture {
    public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
    public static int INT_FIELD = 0;
    public static boolean BOOLEAN_FIELD = false;
    public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52");
    public static javax.lang.model.element.ElementKind[] ELEMENT_KINDS_FIELD = new javax.lang.model.element.ElementKind[] {};

    public LombokClass.LombokClassBuilder createLombokClassBuilderFixtureWithStringFieldAndIntFieldAndBooleanFieldAndUuidFieldAndElementKindsField() {
        return LombokClass.builder()
                .stringField(STRING_FIELD)
                .intField(INT_FIELD)
                .booleanField(BOOLEAN_FIELD)
                .uuidField(UUID_FIELD)
                .elementKindsField(ELEMENT_KINDS_FIELD);
    }
}

package de.floydkretschmar.fixturize.mocks;

public class SingleConstructorClassFixture {
    public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
    public static int INT_FIELD = 0;
    public static boolean BOOLEAN_FIELD = false;
    public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52");
    public static javax.lang.model.element.ElementKind ELEMENT_KIND_FIELD = javax.lang.model.element.ElementKind.PACKAGE;

    public static de.floydkretschmar.fixturize.mocks.SingleConstructorClass createSingleConstructorFixture() {
        return new de.floydkretschmar.fixturize.mocks.SingleConstructorClass(STRING_FIELD, INT_FIELD, BOOLEAN_FIELD, UUID_FIELD);
    }
}

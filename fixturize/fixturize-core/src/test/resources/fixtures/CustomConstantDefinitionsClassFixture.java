package de.floydkretschmar.fixturize.mocks;

public class CustomConstantDefinitionsClassFixture {
    public static java.lang.String CUSTOM_STRING_FIELD_NAME = "CUSTOM_CONSTANT_VALUE";
    public static int INT_FIELD = 0;
    public static boolean CUSTOM_BOOLEAN_FIELD_NAME = false;
    public static boolean CUSTOM_BOOLEAN_FIELD_NAME_2 = true;
    public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52");
    public static javax.lang.model.element.ElementKind ELEMENT_KIND_FIELD = javax.lang.model.element.ElementKind.PACKAGE;

    public static CustomConstantDefinitionsClass createCustomConstantFixture() {
        return new CustomConstantDefinitionsClass(CUSTOM_STRING_FIELD_NAME, INT_FIELD, CUSTOM_BOOLEAN_FIELD_NAME, UUID_FIELD);
    }

    public static CustomConstantDefinitionsClass createCustomConstantFixture2() {
        return new CustomConstantDefinitionsClass(CUSTOM_STRING_FIELD_NAME, CUSTOM_BOOLEAN_FIELD_NAME_2, UUID_FIELD);
    }
}

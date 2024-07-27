package de.floydkretschmar.fixturize.mocks;

public class BuilderClassFixture {
    public static java.lang.String STRING_FIELD = "STRING_FIELD_VALUE";
    public static int INT_FIELD = 0;
    public static boolean BOOLEAN_FIELD = false;
    public static java.util.UUID UUID_FIELD = java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52");

    public static de.floydkretschmar.fixturize.mocks.BuilderClass createBuilderClass() {
        return de.floydkretschmar.fixturize.mocks.BuilderClass.createBuilder().setStringField(STRING_FIELD).setIntField(INT_FIELD).setBooleanField(BOOLEAN_FIELD).buildCustom();
    }

    public static de.floydkretschmar.fixturize.mocks.BuilderClass createBuilderClassWithParamters() {
        return de.floydkretschmar.fixturize.mocks.BuilderClass.createBuilder().setStringField(STRING_FIELD).setBooleanField(true).build();
    }
}

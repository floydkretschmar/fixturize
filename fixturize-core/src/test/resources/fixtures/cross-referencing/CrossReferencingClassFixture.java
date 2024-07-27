package de.floydkretschmar.fixturize.mocks;

public class CrossReferencingClassFixture {
    public static java.lang.String REFERENCE_ID = de.floydkretschmar.fixturize.mocks.CrossReferencedClassFixture.ID;
    public static de.floydkretschmar.fixturize.mocks.CrossReferencedClass REFERENCE_INSTANCE = de.floydkretschmar.fixturize.mocks.CrossReferencedClassFixture.createCrossReferencedFixture();
    public static de.floydkretschmar.fixturize.mocks.SingleConstructorClass CONSTRUCTOR_REFERENCE_INSTANCE = de.floydkretschmar.fixturize.mocks.SingleConstructorClassFixture.createSingleConstructorFixture();
    public static de.floydkretschmar.fixturize.mocks.CrossReferencedConstructorClass CONSTRUCTOR_CLASS_INSTANCE = new de.floydkretschmar.fixturize.mocks.CrossReferencedConstructorClass("STRING_FIELD_VALUE", false, java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52"));
    public static de.floydkretschmar.fixturize.mocks.CrossReferencedBuilderClass BUILDER_CLASS_INSTANCE = de.floydkretschmar.fixturize.mocks.CrossReferencedBuilderClass.builder().setStringField("STRING_FIELD_VALUE").setIntField(0).setBooleanField(false).setUuidField(java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52")).build();
    public static de.floydkretschmar.fixturize.mocks.CrossReferencedLombokClass LOMBOK_CLASS_INSTANCE = de.floydkretschmar.fixturize.mocks.CrossReferencedLombokClass.builder().stringField("STRING_FIELD_VALUE").intField(0).booleanField(false).uuidField(java.util.UUID.fromString("6b21f215-bf9e-445a-9dd2-5808a3a98d52")).build();
    public static de.floydkretschmar.fixturize.mocks.CrossReferencedGenericClass<java.lang.Integer> GENERIC_CLASS_INSTANCE = de.floydkretschmar.fixturize.mocks.CrossReferencedGenericClass.<java.lang.Integer>builder().genericField(0).build();
}

package de.floydkretschmar.fixturize.mocks;

public class CrossReferencingClassFixture {
    public static java.lang.String REFERENCE_ID = de.floydkretschmar.fixturize.mocks.CrossReferencedClassFixture.ID;
    public static de.floydkretschmar.fixturize.mocks.CrossReferencedClass REFERENCE_INSTANCE = de.floydkretschmar.fixturize.mocks.CrossReferencedClassFixture.createCrossReferencedFixture().build();
    public static de.floydkretschmar.fixturize.mocks.SingleConstructorClass CONSTRUCTOR_REFERENCE_INSTANCE = de.floydkretschmar.fixturize.mocks.SingleConstructorClassFixture.createSingleConstructorFixture();


}

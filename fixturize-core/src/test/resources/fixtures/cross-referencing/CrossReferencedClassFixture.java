package de.floydkretschmar.fixturize.mocks;

public class CrossReferencedClassFixture {
    public static java.lang.String ID = "ID_VALUE";

    public static de.floydkretschmar.fixturize.mocks.CrossReferencedClass.CrossReferencedClassBuilder createCrossReferencedFixture() {
        return de.floydkretschmar.fixturize.mocks.CrossReferencedClass.builder().id(ID).build();
    }
}

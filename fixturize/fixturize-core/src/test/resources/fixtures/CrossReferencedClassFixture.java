package de.floydkretschmar.fixturize.mocks;

public class CrossReferencedClassFixture {
    public static java.lang.String ID = "ID_VALUE";

    public static CrossReferencedClass.CrossReferencedClassBuilder createCrossReferencedFixture() {
        return CrossReferencedClass.builder()
                .id(ID);
    }
}

package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;

import java.util.UUID;

@Fixture
public class CrossReferencedBuilderClass {
    private String stringField;
    private int intField;
    private boolean booleanField;
    private UUID uuidField;

    private CrossReferencedBuilderClass() {

    }

    public static CrossReferencedBuilderClassBuilder builder() {
        return new CrossReferencedBuilderClass.CrossReferencedBuilderClassBuilder();
    }

    static class CrossReferencedBuilderClassBuilder {
        private String stringField;
        private int intField;
        private boolean booleanField;
        private UUID uuidField;

        public CrossReferencedBuilderClassBuilder() {
        }

        public CrossReferencedBuilderClassBuilder setStringField(String stringField) {
            this.stringField = stringField;
            return this;
        }

        public CrossReferencedBuilderClassBuilder setIntField(int intField) {
            this.intField = intField;
            return this;
        }

        public CrossReferencedBuilderClassBuilder setBooleanField(boolean booleanField) {
            this.booleanField = booleanField;
            return this;
        }

        public CrossReferencedBuilderClassBuilder setUuidField(UUID uuidField) {
            this.uuidField = uuidField;
            return this;
        }

        public CrossReferencedBuilderClass build() {
            final var builderClassObject = new CrossReferencedBuilderClass();
            builderClassObject.stringField = this.stringField;
            builderClassObject.intField = this.intField;
            builderClassObject.booleanField = this.booleanField;
            builderClassObject.uuidField = this.uuidField;
            return builderClassObject;
        }
    }
}

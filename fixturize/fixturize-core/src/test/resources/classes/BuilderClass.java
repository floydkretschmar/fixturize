package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;

import java.util.UUID;

@Fixture
@FixtureBuilder(methodName = "createBuilderClassFixture", usedSetters = {"stringField", "intField", "booleanField", "uuidField"})
public class BuilderClass {
    private String stringField;
    private int intField;
    private boolean booleanField;
    private UUID uuidField;

    public static BuilderClassBuilder builder() {
        return new BuilderClass.BuilderClassBuilder();
    }

    static class BuilderClassBuilder {
        private String stringField;
        private int intField;
        private boolean booleanField;
        private UUID uuidField;

        public BuilderClassBuilder() {
        }

        public BuilderClassBuilder stringField(String stringField) {
            this.stringField = stringField;
            return this;
        }

        public BuilderClassBuilder intField(int intField) {
            this.intField = intField;
            return this;
        }

        public BuilderClassBuilder booleanField(boolean booleanField) {
            this.booleanField = booleanField;
            return this;
        }

        public BuilderClassBuilder uuidField(UUID uuidField) {
            this.uuidField = uuidField;
            return this;
        }

        public BuilderClass build() {
            final var builderClassObject = new BuilderClass();
            builderClassObject.stringField = this.stringField;
            builderClassObject.intField = this.intField;
            builderClassObject.booleanField = this.booleanField;
            builderClassObject.uuidField = this.uuidField;
            return builderClassObject;
        }
    }
}

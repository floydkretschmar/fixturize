package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilderSetter;

import java.util.UUID;

@Fixture
@FixtureBuilder(methodName = "createBuilderClass", builderMethod = "createBuilder", buildMethod = "buildCustom")
@FixtureBuilder(methodName = "createBuilderClassWithParamters", builderMethod = "createBuilder", usedSetters = {
        @FixtureBuilderSetter(setterName = "setStringField", value = "stringField"),
        @FixtureBuilderSetter(setterName = "setBooleanField", value = "true")
})
public class BuilderClass {
    private String stringField;
    private int intField;
    private boolean booleanField;
    private UUID uuidField;

    private BuilderClass() {

    }

    public static BuilderClassBuilder builder() {
        return new BuilderClass.BuilderClassBuilder();
    }
    public static BuilderClassBuilder createBuilder() {
        return new BuilderClass.BuilderClassBuilder();
    }
    static class BuilderClassBuilder {
        private String stringField;
        private int intField;
        private boolean booleanField;
        private UUID uuidField;

        public BuilderClassBuilder() {
        }

        public BuilderClassBuilder setStringField(String stringField) {
            this.stringField = stringField;
            return this;
        }

        public BuilderClassBuilder setIntField(int intField) {
            this.intField = intField;
            return this;
        }

        public BuilderClassBuilder setBooleanField(boolean booleanField) {
            this.booleanField = booleanField;
            return this;
        }

        public BuilderClass build() {
            final var builderClassObject = new BuilderClass();
            builderClassObject.stringField = this.stringField;
            builderClassObject.intField = this.intField;
            builderClassObject.booleanField = this.booleanField;
            builderClassObject.uuidField = UUID.randomUUID();
            return builderClassObject;
        }

        public BuilderClass buildCustom() {
            final var builderClassObject = new BuilderClass();
            builderClassObject.stringField = this.stringField;
            builderClassObject.intField = this.intField;
            builderClassObject.booleanField = this.booleanField;
            builderClassObject.uuidField = UUID.randomUUID();
            return builderClassObject;
        }
    }
}

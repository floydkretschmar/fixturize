package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import javax.lang.model.element.ElementKind;
import java.util.List;
import java.util.UUID;

@Fixture
@FixtureConstructor(methodName = "createCustomConstantFixture", constructorParameters = {"CUSTOM_STRING_FIELD_NAME", "intField", "CUSTOM_BOOLEAN_FIELD_NAME", "uuidField"})
@FixtureConstructor(methodName = "createCustomConstantFixture2",constructorParameters = {"CUSTOM_STRING_FIELD_NAME", "CUSTOM_BOOLEAN_FIELD_NAME_2", "uuidField"})
public class CustomConstantDefinitionsClass {
    @FixtureConstant(name = "CUSTOM_STRING_FIELD_NAME", value = "\"CUSTOM_CONSTANT_VALUE\"")
    private final String stringField;
    private final int intField;
    @FixtureConstant(name = "CUSTOM_BOOLEAN_FIELD_NAME")
    @FixtureConstant(name = "CUSTOM_BOOLEAN_FIELD_NAME_2", value = "true")
    private final boolean booleanField;
    private final UUID uuidField;
    private final ElementKind elementKindField;
    @FixtureConstant(name = "OBJECT_LIST_FIELD", value = "java.util.List.of(${java.lang.String}, ${java.lang.Integer})")
    private final List<Object> objectListField;

    public CustomConstantDefinitionsClass() {
        this("", 0, false, null);
    }

    public CustomConstantDefinitionsClass(String stringField, int intField, boolean booleanField, UUID uuidField) {
        this.stringField = stringField;
        this.intField = intField;
        this.booleanField = booleanField;
        this.uuidField = uuidField;
        this.elementKindField = ElementKind.CLASS;
        this.objectListField = List.of();
    }

    public CustomConstantDefinitionsClass(String stringField, boolean booleanField, UUID uuidField) {
        this(stringField, 0, booleanField, uuidField);
    }
}

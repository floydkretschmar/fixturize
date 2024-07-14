package de.floydkretschmar.fixturize.mocks;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import javax.lang.model.element.ElementKind;
import java.util.UUID;

@Fixture
@FixtureConstructor(correspondingFields = {"stringField", "intField", "booleanField", "uuidField"})
public class SingleConstructorClass {
    private final String stringField;
    private final int intField;
    private final boolean booleanField;
    private final UUID uuidField;
    private final ElementKind elementKindField;

    public SingleConstructorClass() {
        this("", 0, false, null);
    }

    public SingleConstructorClass(String stringField, int intField, boolean booleanField, UUID uuidField) {
        this.stringField = stringField;
        this.intField = intField;
        this.booleanField = booleanField;
        this.uuidField = uuidField;
        this.elementKindField = ElementKind.CLASS;
    }

    public SingleConstructorClass(String stringField, boolean booleanField, UUID uuidField) {
        this(stringField, 0, booleanField, uuidField);
    }
}

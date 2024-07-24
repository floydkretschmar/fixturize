package de.floydkretschmar.fixturize.mocks;

import java.util.UUID;

public class CrossReferencedConstructorClass {
    private String stringField;
    private int intField;
    private boolean booleanField;
    private UUID uuidField;

    public CrossReferencedConstructorClass(String stringField, boolean booleanField, UUID uuidField) {
        this.stringField = stringField;
        this.intField = 0;
        this.booleanField = booleanField;
        this.uuidField = uuidField;
    }
}

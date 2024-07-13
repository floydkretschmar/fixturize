package de.floydkretschmar.fixturize.stategies.constants.value;

import javax.lang.model.element.VariableElement;
import java.util.UUID;

public class UUIDValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "java.util.UUID.fromString(\"%s\")".formatted(UUID.randomUUID().toString());
    }
}

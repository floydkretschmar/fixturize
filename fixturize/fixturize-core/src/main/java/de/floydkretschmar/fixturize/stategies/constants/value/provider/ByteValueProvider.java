package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.VariableElement;

public class ByteValueProvider implements ValueProvider<VariableElement> {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "0";
    }
}

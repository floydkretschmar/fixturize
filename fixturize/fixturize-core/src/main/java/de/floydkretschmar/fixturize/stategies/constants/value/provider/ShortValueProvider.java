package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.VariableElement;

public class ShortValueProvider implements ValueProvider<VariableElement> {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "Short.valueOf((short)0)";
    }
}

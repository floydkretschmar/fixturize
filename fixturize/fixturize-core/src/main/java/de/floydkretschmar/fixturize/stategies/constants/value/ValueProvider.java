package de.floydkretschmar.fixturize.stategies.constants.value;

import javax.lang.model.element.VariableElement;

@FunctionalInterface
public interface ValueProvider {
    String provideValueAsString(VariableElement field);
}

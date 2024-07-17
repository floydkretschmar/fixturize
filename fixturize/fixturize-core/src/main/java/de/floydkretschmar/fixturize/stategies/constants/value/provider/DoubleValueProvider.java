package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.VariableElement;

/**
 * Default value provider for {@link Double} and {@link double}.
 *
 * @author Floyd Kretschmar
 */
public class DoubleValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "0.0";
    }
}

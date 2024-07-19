package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import javax.lang.model.element.VariableElement;

/**
 * Default value provider for {@link Float} and {@link float}.
 *
 * @author Floyd Kretschmar
 */
public class FloatValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "0.0F";
    }
}

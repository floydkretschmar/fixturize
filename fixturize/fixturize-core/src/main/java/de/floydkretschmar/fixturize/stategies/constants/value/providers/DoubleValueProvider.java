package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.domain.Names;

import javax.lang.model.element.VariableElement;

/**
 * Default value provider for {@link Double} and {@link double}.
 *
 * @author Floyd Kretschmar
 */
public class DoubleValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field, Names names) {
        return "0.0";
    }
}

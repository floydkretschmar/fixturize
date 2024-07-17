package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.VariableElement;

/**
 * Default value provider for {@link Short} and {@link short}.
 *
 * @author Floyd Kretschmar
 */
public class ShortValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "Short.valueOf((short)0)";
    }
}

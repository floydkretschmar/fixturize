package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.VariableElement;

/***
 * Default value provider for {@link Integer} and {@link int}.
 *
 * @author Floyd Kretschmar
 */
public class IntegerValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "0";
    }
}

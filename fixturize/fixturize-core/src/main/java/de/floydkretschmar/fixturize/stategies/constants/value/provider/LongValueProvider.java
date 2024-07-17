package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.VariableElement;

/**
 * Default value provider for {@link Long} and {@link long}.
 *
 * @author Floyd Kretschmar
 */
public class LongValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "0L";
    }
}

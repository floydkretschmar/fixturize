package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import javax.lang.model.element.VariableElement;

/**
 * Default value provider for {@link Byte} and {@link byte}.
 *
 * @author Floyd Kretschmar
 */
public class ByteValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field) {
        return "0";
    }
}

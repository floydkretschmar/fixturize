package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.domain.Names;

import javax.lang.model.element.VariableElement;

/**
 * Default value provider for {@link Byte} and {@link byte}.
 *
 * @author Floyd Kretschmar
 */
public class ByteValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field, Names names) {
        return "0";
    }
}

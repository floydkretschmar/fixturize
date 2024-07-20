package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.Element;

/**
 * Default value provider for {@link Byte} and {@link byte}.
 *
 * @author Floyd Kretschmar
 */
public class ByteValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(Element field, Names names) {
        return "0";
    }
}

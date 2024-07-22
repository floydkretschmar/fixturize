package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.domain.Metadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.Element;

/**
 * Default value provider for {@link Integer} and {@link int}.
 *
 * @author Floyd Kretschmar
 */
public class IntegerValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(Element field, Metadata metadata) {
        return "0";
    }
}

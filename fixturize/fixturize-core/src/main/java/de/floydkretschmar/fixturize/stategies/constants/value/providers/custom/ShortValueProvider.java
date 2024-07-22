package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.domain.Metadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.Element;

/**
 * Default value provider for {@link Short} and {@link short}.
 *
 * @author Floyd Kretschmar
 */
public class ShortValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(Element field, Metadata metadata) {
        return "Short.valueOf((short)0)";
    }
}

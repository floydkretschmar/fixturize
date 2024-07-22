package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.domain.Metadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.Element;

/**
 * Default value provider for {@link Character} and {@link char}.
 *
 * @author Floyd Kretschmar
 */
public class CharacterValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(Element field, Metadata metadata) {
        return "' '";
    }
}

package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.Element;

/**
 * Default value provider for {@link Double} and {@link double}.
 *
 * @author Floyd Kretschmar
 */
public class DoubleValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(Element field, TypeMetadata metadata) {
        return "0.0";
    }
}

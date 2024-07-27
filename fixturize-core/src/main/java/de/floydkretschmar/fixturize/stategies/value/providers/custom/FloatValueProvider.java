package de.floydkretschmar.fixturize.stategies.value.providers.custom;

import de.floydkretschmar.fixturize.stategies.metadata.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.value.providers.ValueProvider;

import javax.lang.model.element.Element;

/**
 * Default value provider for {@link Float} and {@link float}.
 *
 * @author Floyd Kretschmar
 */
public class FloatValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(Element field, TypeMetadata metadata) {
        return "0.0F";
    }
}

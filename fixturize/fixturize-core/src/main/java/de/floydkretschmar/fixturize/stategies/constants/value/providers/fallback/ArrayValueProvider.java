package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Metadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.Element;

public class ArrayValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(Element field, Metadata metadata) {
        return "new %s {}".formatted(metadata.getQualifiedClassName());
    }
}

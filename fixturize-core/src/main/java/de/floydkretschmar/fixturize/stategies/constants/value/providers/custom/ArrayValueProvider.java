package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.Element;

public class ArrayValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(Element field, TypeMetadata metadata) {
        return "new %s {}".formatted(metadata.getQualifiedClassName());
    }
}

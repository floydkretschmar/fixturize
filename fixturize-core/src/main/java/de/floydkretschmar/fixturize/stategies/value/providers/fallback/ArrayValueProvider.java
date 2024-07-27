package de.floydkretschmar.fixturize.stategies.value.providers.fallback;

import de.floydkretschmar.fixturize.stategies.metadata.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.value.providers.FallbackValueProvider;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;

public class ArrayValueProvider implements FallbackValueProvider {

    @Override
    public boolean canProvideFallback(Element element, TypeMetadata metadata) {
        return element.asType().getKind() == TypeKind.ARRAY;
    }

    @Override
    public String provideValueAsString(Element field, TypeMetadata metadata) {
        return "new %s {}".formatted(metadata.getQualifiedClassName());
    }
}

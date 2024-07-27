package de.floydkretschmar.fixturize.stategies.value.providers;

import de.floydkretschmar.fixturize.stategies.metadata.TypeMetadata;

import javax.lang.model.element.Element;

public interface FallbackValueProvider extends ValueProvider {
    boolean canProvideFallback(Element element, TypeMetadata metadata);
}

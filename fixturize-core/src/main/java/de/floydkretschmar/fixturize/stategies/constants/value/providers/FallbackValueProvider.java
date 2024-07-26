package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.domain.TypeMetadata;

import javax.lang.model.element.Element;

public interface FallbackValueProvider extends ValueProvider {
    boolean canProvideFallback(Element element, TypeMetadata metadata);
}

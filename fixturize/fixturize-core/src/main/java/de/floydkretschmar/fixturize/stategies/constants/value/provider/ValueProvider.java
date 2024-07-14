package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.Element;

@FunctionalInterface
public interface ValueProvider<T extends Element> {
    String provideValueAsString(T field);
}

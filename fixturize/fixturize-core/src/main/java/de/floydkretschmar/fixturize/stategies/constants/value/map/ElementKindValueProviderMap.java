package de.floydkretschmar.fixturize.stategies.constants.value.map;

import de.floydkretschmar.fixturize.stategies.constants.value.provider.DefaultFallbackValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.EnumValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.provider.ValueProvider;

import javax.lang.model.element.ElementKind;
import java.util.HashMap;
import java.util.Map;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;

/**
 * An extension of the {@link HashMap} class that registers a default {@link ValueProvider}s for a number of different
 * {@link ElementKind}s, given no custom provider has been defined for the specified {@link ElementKind}.
 *
 * @author Floyd Kretschmar
 */
public class ElementKindValueProviderMap extends HashMap<ElementKind, ValueProvider> {

    /**
     * Constructs a {@link ElementKindValueProviderMap } registering default {@link ValueProvider}s for a number of different
     * {@link ElementKind}s, given no custom {@link ValueProvider} has been provided for the specified {@link ElementKind}.
     *
     * @param customElementKindValueProviders - the list of custom {@link ValueProvider}s
     */
    public ElementKindValueProviderMap(Map<? extends ElementKind, ? extends ValueProvider> customElementKindValueProviders) {
        super(customElementKindValueProviders);
        this.putIfAbsent(ENUM, new EnumValueProvider());
        this.putIfAbsent(CLASS, new DefaultFallbackValueProvider());
    }
}

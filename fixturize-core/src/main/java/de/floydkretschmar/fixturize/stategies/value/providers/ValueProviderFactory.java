package de.floydkretschmar.fixturize.stategies.value.providers;

import de.floydkretschmar.fixturize.stategies.value.ValueProviderMap;
import de.floydkretschmar.fixturize.stategies.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.value.providers.fallback.BuilderValueProvider;

import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.Map;

/**
 * Defines the methods used to create value providers that are used to generate values for fixture constants.
 */
public interface ValueProviderFactory {

    /**
     * Creates a map of value providers that consists of both default value providers registered for base types and custom value providers.
     *
     * @param customValueProviders - that are registered by using {@link de.floydkretschmar.fixturize.annotations.FixtureValueProvider}
     * @param typeUtils - defining utility methods to work with {@link javax.lang.model.type.TypeMirror}
     * @param valueProviderService - that is used to retrieve the corresponding value representation for a given {@link javax.lang.model.element.Element}.
     * @return the map of value providers
     */
    ValueProviderMap createValueProviders(Map<String, ValueProvider> customValueProviders, Types typeUtils, ValueProviderService valueProviderService);

    /**
     * Creates the collection of {@link FallbackValueProvider}s that will be used for {@link javax.lang.model.element.Element}
     * instances with no specific value provider registered for their {@link javax.lang.model.type.TypeMirror}.
     * @param valueProviderService - that is used to retrieve the corresponding value representation for a given {@link javax.lang.model.element.Element}.
     * @return the collection of fallback value providers
     */
    Collection<FallbackValueProvider> createFallbackValueProviders(ValueProviderService valueProviderService);

    ValueProvider createConstructorValueProvider(ValueProviderService valueProviderService);

    BuilderValueProvider createBuilderValueProvider(ValueProviderService valueProviderService);
}

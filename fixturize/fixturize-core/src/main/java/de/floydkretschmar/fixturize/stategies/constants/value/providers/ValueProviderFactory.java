package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;

/**
 * Defines the methods used to create value providers that are used to generate values for fixture constants.
 */
public interface ValueProviderFactory {

    /**
     * Creates a map of value providers that consists of both default value providers registered for base types and custom value providers.
     *
     * @param customValueProviders - that are registered by using {@link de.floydkretschmar.fixturize.annotations.FixtureValueProvider}
     * @return the map of value providers
     */
    ValueProviderMap createValueProviders(Map<String, ValueProvider> customValueProviders);

    /**
     * Creates the value provider used for {@link javax.lang.model.element.Element} instances with types of type {@link javax.lang.model.type.DeclaredType}.
     * @param valueProviderService - that is used to retrieve the corresponding value representation for a given {@link javax.lang.model.element.Element}.
     * @return the value provider
     */
    ValueProvider createDeclaredTypeValueProvider(ValueProviderService valueProviderService);

    /**
     * Creates the value provider used for {@link javax.lang.model.element.Element} instances with a container type.
     * @param elementUtils - defining utility methods to work with {@link javax.lang.model.element.Element}
     * @param typeUtils - defining utility methods to work with {@link javax.lang.model.type.TypeMirror}
     * @param valueProviderService - that is used to retrieve the corresponding value representation for a given {@link javax.lang.model.element.Element}.
     * @return the value provider
     */
    ValueProvider createContainerValueProvider(Elements elementUtils, Types typeUtils, ValueProviderService valueProviderService);
}

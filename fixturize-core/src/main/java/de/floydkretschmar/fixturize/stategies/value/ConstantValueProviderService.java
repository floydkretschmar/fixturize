package de.floydkretschmar.fixturize.stategies.value;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.metadata.MetadataFactory;
import de.floydkretschmar.fixturize.stategies.value.providers.FallbackValueProvider;
import de.floydkretschmar.fixturize.stategies.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.value.providers.ValueProviderFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Decides which value during constant creation should be used for a given {@link VariableElement}.
 *
 * @author Floyd Kretschmar
 */
public class ConstantValueProviderService implements ValueProviderService {
    /**
     * All default and custom value providers for classes.
     */
    private final ValueProviderMap valueProviders;

    /**
     * The list of fallback value providers that will be tasked with providing values, if no specific value provider
     * has been registered for the type of an element.
     */
    private final Collection<FallbackValueProvider> fallbackValueProviders;

    /**
     * The factory used to create metadata for a given element.
     */
    private final MetadataFactory metadataFactory;

    /**
     * The collection of utility methods used to process {@link Elements}
     */
    private final Elements elementUtils;

    public ConstantValueProviderService(
            Map<String, ValueProvider> customValueProviders,
            ValueProviderFactory valueProviderFactory,
            Elements elementUtils,
            Types typeUtils,
            MetadataFactory metadataFactory) {
        this.valueProviders = valueProviderFactory.createValueProviders(customValueProviders, typeUtils, this);
        this.fallbackValueProviders = valueProviderFactory.createFallbackValueProviders(this);
        this.metadataFactory = metadataFactory;
        this.elementUtils = elementUtils;
    }

    /**
     * Returns the correct value that should be used for constant generation for the specified element.
     *
     * @param element - for which the value is being retrieved
     * @return the value used for constant construction
     */
    @Override
    public String getValueFor(Element element) {
        final var type = element.asType();
        final var metadata = metadataFactory.createMetadataFrom(type);
        var value = ValueProvider.DEFAULT_VALUE;

        if (valueProviders.containsKey(metadata.getQualifiedClassName())) {
            value = valueProviders.get(metadata.getQualifiedClassName()).provideValueAsString(element, metadata);
        }
        else if (valueProviders.containsKey(metadata.getQualifiedClassNameWithoutGeneric())) {
            value = valueProviders.get(metadata.getQualifiedClassNameWithoutGeneric()).provideValueAsString(element, metadata);
        }
        else {
            value = this.fallbackValueProviders.stream()
                    .filter(provider -> provider.canProvideFallback(element, metadata))
                    .findFirst()
                    .map(provider -> provider.provideValueAsString(element, metadata))
                    .orElse(ValueProvider.DEFAULT_VALUE);
        }

        return this.resolveValuesForDefaultPlaceholders(value);
    }

    /**
     * Returns the resolved value representation with the correct values for all default value wildcards that were present in the
     * provided value string.
     * @param valueStringWithPlaceholders - that contains 0 to N default value wildcards
     * @return the fully resolved value
     */
    @Override
    public String resolveValuesForDefaultPlaceholders(String valueStringWithPlaceholders) {
        final var valuePattern = Pattern.compile("(?<defaultValueType>\\#\\{[^\\{\\}\\$]*\\})");
        final var regex = valuePattern.matcher(valueStringWithPlaceholders);
        final var values = new ArrayList<String>();

        var value = valueStringWithPlaceholders;
        while (regex.find()) {
            final var defaultValueTypeNameWildcard = regex.group("defaultValueType");
            final var defaultValueTypeName = defaultValueTypeNameWildcard.substring(2, defaultValueTypeNameWildcard.length() - 1);
            final var element = elementUtils.getTypeElement(defaultValueTypeName);
            if (Objects.isNull(element))
                throw new FixtureCreationException("%s is not a valid type that can be used for default value generation".formatted(defaultValueTypeName));
            values.add(this.getValueFor(element));
            value = value.replace(defaultValueTypeNameWildcard, "%s");
        }

        return value.formatted(values.toArray());
    }
}

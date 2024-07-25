package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;
import de.floydkretschmar.fixturize.stategies.constants.metadata.MetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProviderFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.type.TypeKind.ARRAY;

/**
 * Decides which value during constant creation should be used for a given {@link VariableElement}.
 *
 * @author Floyd Kretschmar
 */
public class ConstantValueProviderService implements ValueProviderService {
    /**
     * The default constant value if all other strategies for generation fail.
     */
    public static final String DEFAULT_VALUE = "null";
    /**
     * All default and custom value providers for classes.
     */
    private final ValueProviderMap valueProviders;

    /**
     * The value provider that provides the fallback value for declared enums if no other value
     * provider has been registered.
     */
    private final ValueProvider enumValueProvider;

    /**
     * The value provider that provides the fallback value for declared classes if no other value
     * provider has been registered.
     */
    private final ValueProvider classValueProvider;

    /**
     * The value provider that provides the fallback value for arrays if no other value provider has been registered.
     */
    private final ValueProvider arrayValueProvider;

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
        this.classValueProvider = valueProviderFactory.createClassValueProvider(this);
        this.arrayValueProvider = valueProviderFactory.createArrayValueProvider();
        this.enumValueProvider = valueProviderFactory.createEnumValueProvider();
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

        if (valueProviders.containsKey(metadata.getQualifiedClassName()))
            return valueProviders.get(metadata.getQualifiedClassName()).provideValueAsString(element, metadata);

        if (valueProviders.containsKey(metadata.getQualifiedClassNameWithoutGeneric()))
            return valueProviders.get(metadata.getQualifiedClassNameWithoutGeneric()).provideValueAsString(element, metadata);

        if (type.getKind() == ARRAY)
            return this.arrayValueProvider.provideValueAsString(element, metadata);

        if (type.getKind() == TypeKind.DECLARED) {
            final var declaredElement = ((DeclaredType)type).asElement();
            final var elementKind = declaredElement.getKind();
            if (elementKind == ENUM) {
                return this.enumValueProvider.provideValueAsString(element, metadata);
            } else if (elementKind == CLASS) {
                return this.classValueProvider.provideValueAsString(element, metadata);
            }
        }

        return DEFAULT_VALUE;
    }

    /**
     * Returns the resolved value representation with the correct values for all default value wildcards that were present in the
     * provided value string.
     * @param valueStringWithPlaceholders - that contains 0 to N default value wildcards
     * @return the fully resolved value
     */
    @Override
    public String resolveValuesForDefaultPlaceholders(String valueStringWithPlaceholders) {
        final var valuePattern = Pattern.compile("(?<defaultValueType>\\$\\{[^\\{\\}\\$]*\\})");
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

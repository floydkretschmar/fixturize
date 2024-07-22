package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.stategies.constants.metadata.MetadataFactory;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProviderFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;

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
     * The value provider that provides the fallback value for declared types (classes and enums) if no other value
     * provider has been registered.
     */
    private final ValueProvider declaredTypeValueProvider;

    /**
     * The value provider that provides the fallback value for container types (arrays, Maps and Collections) if no other value
     * provider has been registered.
     */
    private final ValueProvider containerValueProvider;

    /**
     * The factory used to create metadata for a given element.
     */
    private final MetadataFactory metadataFactory;

    public ConstantValueProviderService(
            Map<String, ValueProvider> customValueProviders,
            ValueProviderFactory valueProviderFactory,
            Elements elementUtils,
            Types typeUtils,
            MetadataFactory metadataFactory) {
        this.valueProviders = valueProviderFactory.createValueProviders(customValueProviders);
        this.declaredTypeValueProvider = valueProviderFactory.createDeclaredTypeValueProvider(this);
        this.containerValueProvider = valueProviderFactory.createContainerValueProvider(elementUtils, typeUtils, this);
        this.metadataFactory = metadataFactory;
    }

    /**
     * Returns the correct value that should be used for constant generation for the specified element.
     *
     * @param element - for which the value is being retrieved
     * @return the value used for constant construction
     */
    @Override
    public String getValueFor(Element element) {
        final var metadata = metadataFactory.createMetadataFrom(element);

        if (valueProviders.containsKey(metadata.getQualifiedClassName()))
            return valueProviders.get(metadata.getQualifiedClassName()).provideValueAsString(element, metadata);

        final var value = this.containerValueProvider.provideValueAsString(element, metadata);

        return value.equals(DEFAULT_VALUE) ? this.declaredTypeValueProvider.provideValueAsString(element, metadata) : value;
    }
}

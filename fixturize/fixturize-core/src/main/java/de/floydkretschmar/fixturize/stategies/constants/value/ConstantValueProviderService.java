package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProviderFactory;

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

    public ConstantValueProviderService(
            Map<String, ValueProvider> customValueProviders,
            ValueProviderFactory valueProviderFactory,
            Elements elementUtils,
            Types typeUtils) {
        this.valueProviders = valueProviderFactory.createValueProviders(customValueProviders);
        this.declaredTypeValueProvider = valueProviderFactory.createDeclaredTypeValueProvider(this);
        this.containerValueProvider = valueProviderFactory.createContainerValueProvider(elementUtils, typeUtils);
    }

    /**
     * Returns the correct value that should be used for constant generation for the specified field.
     *
     * @param field - for which the value is being retrieved
     * @return the value used for constant construction
     */
    @Override
    public String getValueFor(VariableElement field) {
        final var fieldType = field.asType();
        final var names = Names.from(fieldType.toString());

        if (valueProviders.containsKey(names.getQualifiedClassName()))
            return valueProviders.get(names.getQualifiedClassName()).provideValueAsString(field, names);

        final var value = this.containerValueProvider.provideValueAsString(field, names);

        return value.equals(DEFAULT_VALUE) ? this.declaredTypeValueProvider.provideValueAsString(field, names) : value;
    }
}

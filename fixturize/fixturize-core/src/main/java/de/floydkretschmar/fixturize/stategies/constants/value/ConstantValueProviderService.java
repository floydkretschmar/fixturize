package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProviderFactory;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.util.Map;

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
     * The value provider that provides the fallback value for classes if no other value provider has been registered.
     */
    private final ValueProvider classValueProvider;

    /**
     * The value provider that provides the fallback value for enums if no other value provider has been registered.
     */
    private final ValueProvider enumValueProvider;

    /**
     * The value provider that provides the fallback value for containers (arrays, collections) if no other value provider
     * has been registered.
     */
    private final ValueProvider containerValueProvider;

    public ConstantValueProviderService(Map<String, ValueProvider> customValueProviders, ValueProviderFactory valueProviderFactory) {
        this.valueProviders = valueProviderFactory.createValueProviders(customValueProviders);
        this.classValueProvider = valueProviderFactory.createClassValueProvider(this);
        this.enumValueProvider = valueProviderFactory.createEnumValueProvider();
        this.containerValueProvider = valueProviderFactory.createContainerValueProvider();
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
        final var typeKind = fieldType.getKind();

        final var names = Names.from(fieldType.toString());
        if (valueProviders.containsKey(names.getQualifiedClassNameWithoutGeneric()))
            return valueProviders.get(names.getQualifiedClassNameWithoutGeneric()).provideValueAsString(field, names);

        if (typeKind == TypeKind.DECLARED) {
            final var declaredElement = ((DeclaredType) fieldType).asElement();
            final var elementKind = declaredElement.getKind();
            if (elementKind == ENUM) {
                return this.enumValueProvider.provideValueAsString(field, names);
            } else if (elementKind == CLASS) {
                return this.classValueProvider.provideValueAsString(field, names);
            }
        }

        if (typeKind == ARRAY) {
            return this.containerValueProvider.provideValueAsString(field, names);
        }

        return DEFAULT_VALUE;
    }
}

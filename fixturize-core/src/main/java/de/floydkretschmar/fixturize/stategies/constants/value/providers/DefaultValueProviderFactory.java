package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ClassValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ContainerValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.DeclaredTypeValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.EnumValueProvider;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;


public class DefaultValueProviderFactory implements ValueProviderFactory {
    @Override
    public ValueProviderMap createValueProviders(Map<String, ValueProvider> customValueProviders) {
        return new ValueProviderMap(customValueProviders);
    }

    @Override
    public ValueProvider createDeclaredTypeValueProvider(ValueProviderService valueProviderService) {
        return new DeclaredTypeValueProvider(new EnumValueProvider(), new ClassValueProvider(valueProviderService));
    }

    @Override
    public ValueProvider createContainerValueProvider(Elements elementUtils, Types typeUtils, ValueProviderService valueProviderService) {
        return new ContainerValueProvider(elementUtils, typeUtils, valueProviderService);
    }
}

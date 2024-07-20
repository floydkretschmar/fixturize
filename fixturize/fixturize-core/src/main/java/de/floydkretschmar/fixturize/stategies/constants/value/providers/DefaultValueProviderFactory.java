package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ClassValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ContainerValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.EnumValueProvider;

import java.util.Map;

public class DefaultValueProviderFactory implements ValueProviderFactory {
    @Override
    public ValueProviderMap createValueProviders(Map<String, ValueProvider> customValueProviders) {
        return new ValueProviderMap(customValueProviders);
    }

    @Override
    public ValueProvider createClassValueProvider(ValueProviderService valueProviderService) {
        return new ClassValueProvider(valueProviderService);
    }

    public ValueProvider createEnumValueProvider() {
        return new EnumValueProvider();
    }

    @Override
    public ValueProvider createContainerValueProvider() {
        return new ContainerValueProvider();
    }
}

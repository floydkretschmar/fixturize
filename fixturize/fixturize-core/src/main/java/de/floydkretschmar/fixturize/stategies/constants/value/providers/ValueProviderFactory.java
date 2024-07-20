package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;

import java.util.Map;

public interface ValueProviderFactory {
    ValueProviderMap createValueProviders(Map<String, ValueProvider> customValueProviders);

    ValueProvider createClassValueProvider(ValueProviderService valueProviderService);

    ValueProvider createEnumValueProvider();

    ValueProvider createContainerValueProvider();
}

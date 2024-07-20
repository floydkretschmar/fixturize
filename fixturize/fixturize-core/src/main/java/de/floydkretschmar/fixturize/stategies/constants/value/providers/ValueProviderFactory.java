package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;

public interface ValueProviderFactory {
    ValueProviderMap createValueProviders(Map<String, ValueProvider> customValueProviders);

    ValueProvider createDeclaredTypeValueProvider(ValueProviderService valueProviderService);

    ValueProvider createContainerValueProvider(Elements elementUtils, Types typeUtils, ValueProviderService valueProviderService);
}

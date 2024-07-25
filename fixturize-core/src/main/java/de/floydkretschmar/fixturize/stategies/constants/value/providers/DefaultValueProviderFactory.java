package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.custom.ArrayValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.custom.ClassValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.custom.EnumValueProvider;

import javax.lang.model.util.Types;
import java.util.Map;


public class DefaultValueProviderFactory implements ValueProviderFactory {
    @Override
    public ValueProviderMap createValueProviders(Map<String, ValueProvider> customValueProviders, Types typeUtils, ValueProviderService valueProviderService) {
        return new ValueProviderMap(customValueProviders, typeUtils, valueProviderService);
    }

    @Override
    public ValueProvider createClassValueProvider(ValueProviderService valueProviderService) {
        return new ClassValueProvider(valueProviderService);
    }

    @Override
    public ValueProvider createEnumValueProvider() {
        return new EnumValueProvider();
    }

    @Override
    public ValueProvider createArrayValueProvider() {
        return new ArrayValueProvider();
    }
}

package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.*;

import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class ConstantValueProviderFactory implements ValueProviderFactory {
    @Override
    public ValueProviderMap createValueProviders(Map<String, ValueProvider> customValueProviders, Types typeUtils, ValueProviderService valueProviderService) {
        return new ValueProviderMap(customValueProviders, typeUtils, valueProviderService);
    }

    @Override
    public Collection<FallbackValueProvider> createFallbackValueProviders(ValueProviderService valueProviderService) {
        return List.of(
                new ClassValueProvider(new BuilderValueProvider(valueProviderService), new ConstructorValueProvider(valueProviderService)),
                new EnumValueProvider(),
                new ArrayValueProvider()
        );
    }
}

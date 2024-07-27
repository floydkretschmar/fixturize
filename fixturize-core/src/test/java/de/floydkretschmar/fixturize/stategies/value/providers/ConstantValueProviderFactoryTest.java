package de.floydkretschmar.fixturize.stategies.value.providers;

import de.floydkretschmar.fixturize.stategies.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.value.providers.fallback.ArrayValueProvider;
import de.floydkretschmar.fixturize.stategies.value.providers.fallback.ClassValueProvider;
import de.floydkretschmar.fixturize.stategies.value.providers.fallback.EnumValueProvider;
import org.junit.jupiter.api.Test;

import javax.lang.model.util.Types;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConstantValueProviderFactoryTest {
    @Test
    void createValueProviders_whenCalled_shouldReturnValueProviderMapContainingCustomValueProviders() {
        final ValueProvider valueProvider = (f, n) -> "customValueProviderValue";
        final var typeUtils = mock(Types.class);
        final var service = mock(ValueProviderService.class);
        final var factory = new ConstantValueProviderFactory();

        final var map = factory.createValueProviders(Map.of("customValueProviderKey", valueProvider), typeUtils, service);

        assertThat(map).containsEntry("customValueProviderKey", valueProvider);
    }

    @Test
    void createClassValueProvider_whenCalled_shouldReturnClassValueProvider() {
        final var service = mock(ValueProviderService.class);
        final var factory = new ConstantValueProviderFactory();

        final var valueProvider = factory.createFallbackValueProviders(service);

        assertThat(valueProvider)
                .hasSize(3)
                .extracting(FallbackValueProvider::getClass)
                .extracting(Class::getName)
                .contains(ClassValueProvider.class.getName(), EnumValueProvider.class.getName(), ArrayValueProvider.class.getName());
    }
}
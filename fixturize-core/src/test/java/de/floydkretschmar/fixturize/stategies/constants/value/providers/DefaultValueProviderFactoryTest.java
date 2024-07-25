package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.custom.ArrayValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.custom.ClassValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.custom.EnumValueProvider;
import org.junit.jupiter.api.Test;

import javax.lang.model.util.Types;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DefaultValueProviderFactoryTest {
    @Test
    void createValueProviders_whenCalled_shouldReturnValueProviderMapContainingCustomValueProviders() {
        final ValueProvider valueProvider = (f, n) -> "customValueProviderValue";
        final var typeUtils = mock(Types.class);
        final var service = mock(ValueProviderService.class);
        final var factory = new DefaultValueProviderFactory();

        final var map = factory.createValueProviders(Map.of("customValueProviderKey", valueProvider), typeUtils, service);

        assertThat(map).containsEntry("customValueProviderKey", valueProvider);
    }

    @Test
    void createClassValueProvider_whenCalled_shouldReturnClassValueProvider() {
        final var service = mock(ValueProviderService.class);
        final var factory = new DefaultValueProviderFactory();

        final var valueProvider = factory.createClassValueProvider(service);

        assertThat(valueProvider).isInstanceOf(ClassValueProvider.class);
    }

    @Test
    void createEnumValueProvider_whenCalled_shouldReturnEnumValueProvider() {
        final var factory = new DefaultValueProviderFactory();

        final var valueProvider = factory.createEnumValueProvider();

        assertThat(valueProvider).isInstanceOf(EnumValueProvider.class);
    }

    @Test
    void createArrayValueProvider_whenCalled_shouldReturnArrayValueProvider() {
        final var factory = new DefaultValueProviderFactory();

        final var valueProvider = factory.createArrayValueProvider();

        assertThat(valueProvider).isInstanceOf(ArrayValueProvider.class);
    }
}
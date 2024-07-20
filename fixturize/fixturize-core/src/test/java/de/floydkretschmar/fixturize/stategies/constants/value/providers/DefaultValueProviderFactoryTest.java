package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ClassValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ContainerValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.EnumValueProvider;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DefaultValueProviderFactoryTest {
    @Test
    void createValueProviders_whenCalled_shouldReturnValueProviderMapContainingCustomValueProviders() {
        final ValueProvider valueProvider = (f, n) -> "customValueProviderValue";
        final var factory = new DefaultValueProviderFactory();

        final var map = factory.createValueProviders(Map.of("customValueProviderKey", valueProvider));

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
    void createContainerValueProvider_whenCalled_shouldReturnContainerValueProvider() {
        final var factory = new DefaultValueProviderFactory();

        final var valueProvider = factory.createContainerValueProvider();

        assertThat(valueProvider).isInstanceOf(ContainerValueProvider.class);
    }
}
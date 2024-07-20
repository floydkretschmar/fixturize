package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ContainerValueProvider;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.DeclaredTypeValueProvider;
import org.junit.jupiter.api.Test;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
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
    void createDeclaredTypeValueProvider_whenCalled_shouldReturnDeclaredTypeValueProvider() {
        final var service = mock(ValueProviderService.class);
        final var factory = new DefaultValueProviderFactory();

        final var valueProvider = factory.createDeclaredTypeValueProvider(service);

        assertThat(valueProvider).isInstanceOf(DeclaredTypeValueProvider.class);
    }

    @Test
    void createContainerValueProvider_whenCalled_shouldReturnContainerValueProvider() {
        final var elementUtils = mock(Elements.class);
        final var typeUtils = mock(Types.class);
        final var factory = new DefaultValueProviderFactory();

        final var valueProvider = factory.createContainerValueProvider(elementUtils, typeUtils);

        assertThat(valueProvider).isInstanceOf(ContainerValueProvider.class);
    }
}
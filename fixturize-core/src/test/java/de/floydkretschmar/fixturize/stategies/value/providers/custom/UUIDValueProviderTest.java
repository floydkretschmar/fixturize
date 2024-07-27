package de.floydkretschmar.fixturize.stategies.value.providers.custom;

import de.floydkretschmar.fixturize.TestFixtures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.lang.model.element.VariableElement;
import java.util.UUID;

import static de.floydkretschmar.fixturize.TestFixtures.RANDOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UUIDValueProviderTest {

    @Test
    void provideValueAsString_whenCalled_returnsValue() {
        final var provider = new UUIDValueProvider();

        final var field = mock(VariableElement.class);
        final var uuid = UUID.fromString(RANDOM_UUID);

        try (final var uuidStatic = Mockito.mockStatic(UUID.class)) {
            uuidStatic.when(UUID::randomUUID).thenReturn(uuid);

            assertThat(provider.provideValueAsString(field, TestFixtures.createMetadataFixture("java.util.UUID")))
                    .isEqualTo("java.util.UUID.fromString(\"%s\")".formatted(RANDOM_UUID));
        }

    }

}
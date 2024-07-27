package de.floydkretschmar.fixturize.stategies.value.providers.custom;

import de.floydkretschmar.fixturize.TestFixtures;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LongValueProviderTest {

    @ParameterizedTest
    @CsvSource({"java.lang.Long", "long"})
    void provideValueAsString_whenCalled_returnsValue(String className) {
        final var provider = new LongValueProvider();

        assertThat(provider.provideValueAsString(mock(VariableElement.class), TestFixtures.createMetadataFixture(className)))
                .isEqualTo("0L");
    }

}
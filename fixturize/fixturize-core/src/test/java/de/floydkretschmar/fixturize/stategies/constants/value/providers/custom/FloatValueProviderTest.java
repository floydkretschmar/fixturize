package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.domain.Names;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FloatValueProviderTest {

    @ParameterizedTest
    @CsvSource({"java.lang.Float", "float"})
    void provideValueAsString_whenCalled_returnsValue(String className) {
        final var provider = new FloatValueProvider();

        assertThat(provider.provideValueAsString(mock(VariableElement.class), Names.from(className)))
                .isEqualTo("0.0F");
    }
}
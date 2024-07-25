package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArrayValueProviderTest {

    @Mock
    private VariableElement field;

    private ArrayValueProvider valueProvider;

    @BeforeEach
    void setup() {
        valueProvider = new ArrayValueProvider();
    }

    @Test
    void provideValueAsString_whenCalled_returnArrayValue() {
        final var metadata = TestFixtures.createMetadataFixture("Class[]");
        final var result = valueProvider.provideValueAsString(field, metadata);
        assertThat(result).isEqualTo("new some.test.Class[] {}");
    }
}
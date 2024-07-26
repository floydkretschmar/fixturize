package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.TestFixtures;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback.ArrayValueProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

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

    @ParameterizedTest
    @CsvSource({"ARRAY, true", "INT, false"})
    void canProvideFallback_whenCalledForKind_returnExpectedResult(TypeKind kind, boolean expectedResult) {
        final var element = TestFixtures.createVariableElementFixture(null, TestFixtures.createTypeMirrorFixture(kind));

        assertThat(valueProvider.canProvideFallback(element, TestFixtures.createMetadataFixture())).isEqualTo(expectedResult);
    }
}
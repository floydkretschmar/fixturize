package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Names;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.lang.model.element.VariableElement;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArrayValueProviderTest {

    private ArrayValueProvider valueProvider;

    @Mock
    private VariableElement field;

    @BeforeEach
    void setup() {
        valueProvider = new ArrayValueProvider();
    }

    @Test
    void getValueFor_whenCalledForArray_returnCorrespondingValueString() {
        final var names = Names.from("some.test.Class[]");

        final var result = valueProvider.provideValueAsString(field, names);

        assertThat(result).isEqualTo("new some.test.Class[] {}");
    }

}